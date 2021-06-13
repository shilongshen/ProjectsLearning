package com.example.miaosha.mq;

import com.alibaba.fastjson.JSON;
import com.example.miaosha.dao.StockLogDOMapper;
import com.example.miaosha.dataobject.StockLogDO;
import com.example.miaosha.error.BusinessException;
import com.example.miaosha.service.OrderService;
import org.apache.rocketmq.client.exception.MQBrokerException;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.*;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.remoting.exception.RemotingException;
import org.apache.tomcat.jni.Local;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Component
public class MqProducer {
    private DefaultMQProducer producer;
    private TransactionMQProducer transactionMQProducer;
    @Value("${mq.nameserver.addr}")
    private String nameAddr;
    @Value("${mq.topicname}")
    private String topicName;
    @Autowired
    private OrderService orderService;
    @Autowired
    private StockLogDOMapper stockLogDOMapper;

    @PostConstruct
    public void init() throws MQClientException {
//    做mq producer的初始化
        producer = new DefaultMQProducer("producer_group");
        producer.setNamesrvAddr(nameAddr);
        producer.start();

        transactionMQProducer = new TransactionMQProducer("transaction_producer_group");
        transactionMQProducer.setNamesrvAddr(nameAddr);
        transactionMQProducer.start();

        transactionMQProducer.setTransactionListener(new TransactionListener() {
            /**
             * public enum LocalTransactionState {
             *     COMMIT_MESSAGE:表示将prepare状态的消息转换为commit消息给消费方消费
             *     ROLLBACK_MESSAGE：表示将prepare状态的消息撤回，等于没发
             *     UNKNOW：未知现在是什么状态
             *
             *     private LocalTransactionState() {
             *     }
             * }
             * */
            @Override
            public LocalTransactionState executeLocalTransaction(Message message, Object arg) {
//                真正要做的事，创建订单
                Integer userId = (Integer) ((Map) arg).get("userId");
                Integer itemId = (Integer) ((Map) arg).get("itemId");
                Integer promoId = (Integer) ((Map) arg).get("promoId");
                Integer amount = (Integer) ((Map) arg).get("amount");
                String stockLogId = (String) ((Map) arg).get("stockLogId");

                try {
                    //创建订单
                    orderService.createOrder(userId, itemId, promoId, amount, stockLogId);
                    //根据checkLocalTransaction确定返回什么状态
                } catch (BusinessException e) {
                    e.printStackTrace();
                    //设置对应的stockLogId为回滚状态
                    StockLogDO stockLogDO = stockLogDOMapper.selectByPrimaryKey(stockLogId);
                    stockLogDO.setStatus(3);
                    stockLogDOMapper.updateByPrimaryKeySelective(stockLogDO);
                    return LocalTransactionState.ROLLBACK_MESSAGE;
                }
                return LocalTransactionState.COMMIT_MESSAGE;
            }

            @Override
            public LocalTransactionState checkLocalTransaction(MessageExt messageExt) {
                //根据是否扣减库存成功，来判断要返回COMMIT_MESSAGE，ROLLBACK_MESSAGE，UNKNOW
                String jsonString = new String(messageExt.getBody());
                Map<String, Integer> map = JSON.parseObject(jsonString, Map.class);
                Integer itemId = map.get("itemId");
                Integer amount = map.get("amount");
                //在异步消息中拿到stockLogId，并获取对应的状态，
                String stockLogId = String.valueOf(map.get("stockLogId"));
                StockLogDO stockLogDO = stockLogDOMapper.selectByPrimaryKey(stockLogId);
                if (stockLogDO == null) {
                    return LocalTransactionState.UNKNOW;
                }
                //1表示初始状态，2表示下单成功，3表示下单回滚
                if (stockLogDO.getStatus() == 2) {
                    return LocalTransactionState.COMMIT_MESSAGE;
                } else if (stockLogDO.getStatus() == 1) {
                    return LocalTransactionState.UNKNOW;
                }
                //其他的状态设置为ROLLBACK_MESSAGE
                return LocalTransactionState.ROLLBACK_MESSAGE;
            }


        });
    }

    //  事务型同步库存扣减消息
    public boolean transactionAsyncReduceStock(Integer userId, Integer promoId, Integer itemId, Integer amount, String stockLogId) {
        Map<String, Object> bodyMap = new HashMap<>();
        bodyMap.put("itemId", itemId);
        bodyMap.put("amount", amount);
        bodyMap.put("stockLogId", stockLogId);

        Map<String, Object> argsMap = new HashMap<>();
        argsMap.put("itemId", itemId);
        argsMap.put("amount", amount);
        argsMap.put("userId", userId);
        argsMap.put("promoId", promoId);
        argsMap.put("stockLogId", stockLogId);

        Message message = new Message(topicName, "increase", JSON.toJSON(bodyMap).toString().getBytes(StandardCharsets.UTF_8));
        TransactionSendResult sendResult = null;
        try {
            //transactionMQProducer发送事务型消息，消息发送出去后broker可以收到，
            // 但是消息不是可被消费状态，而是一个prepare状态，这一状态下消息是不会被消费者看到的
            //在prepare状态下会执行executeLocalTransaction方法
            //就是说sendMessageInTransaction做两件事：
            //向消息队列中投递prepare消息，维护在broker上面，然后在本地会执行executeLocalTransaction方法（真正要做的事，创建订单）
            sendResult = transactionMQProducer.sendMessageInTransaction(message, argsMap);
        } catch (MQClientException e) {
            e.printStackTrace();
            return false;
        }

        if (sendResult.getLocalTransactionState() == LocalTransactionState.ROLLBACK_MESSAGE) {
            return false;
        } else if (sendResult.getLocalTransactionState() == LocalTransactionState.COMMIT_MESSAGE) {
            return true;
        } else {
            return false;
        }
    }

    //    同步库存扣减消息,
    public boolean asyncReduceStock(Integer itemId, Integer amount) {
        Map<String, Object> bodyMap = new HashMap<>();
        bodyMap.put("itemId", itemId);
        bodyMap.put("amount", amount);
        Message message = new Message(topicName, "increase", JSON.toJSON(bodyMap).toString().getBytes(StandardCharsets.UTF_8));
        try {
            //producer只管把消息发送出去，consumer可以消费消息
            producer.send(message);
        } catch (MQClientException e) {
            e.printStackTrace();
            return false;
        } catch (RemotingException e) {
            e.printStackTrace();
            return false;
        } catch (MQBrokerException e) {
            e.printStackTrace();
            return false;
        } catch (InterruptedException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }
}
