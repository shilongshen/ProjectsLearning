package com.example.miaosha.validator;


import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import java.util.Set;


@Component
public class ValidatorImpl implements InitializingBean {
    //当springBean初始化完成后会回调ValidatorImpl对应的afterPropertiesSet
    private Validator validator;//validator工具

    @Override
    public void afterPropertiesSet() throws Exception {
//        将hibernate validator通过工厂的初始化方式使其实例化
        this.validator = Validation.buildDefaultValidatorFactory().getValidator();

    }

//    实现校验方法并返回校验结果
    public ValidationResult validate(Object bean) {
        ValidationResult result = new ValidationResult();
//        如果对应的bean中一些参数的规则有违背validator定义的annotation，constraintViolationSet中就会有这个值
        Set<ConstraintViolation<Object>> constraintViolationSet = validator.validate(bean);
        if (constraintViolationSet.size() > 0) {
//            size>0说明有错误,将ValidationResult中的hasErrors设置为true
            result.setHasErrors(true);

//            遍历constraintViolationSet，这个set中的每一个元素objectConstraintViolation
//            的errMsg存放了所违背的信息；
//            propertyName:哪一个字段发生错误；errMsg-->发生了什么错误,(错误信息)
            constraintViolationSet.forEach(objectConstraintViolation -> {
                String errMsg = objectConstraintViolation.getMessage();
                String propertyName = objectConstraintViolation.getPropertyPath().toString();
                result.getErrMsgMap().put(propertyName,errMsg);
            });
        }
        return result;
    }
}
