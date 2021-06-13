参考：

REDEME在[该博客](https://blog.csdn.net/m0_37657841/article/details/90524410)基础上进行更改

[课程]()

## 项目环境

- IDEA，maven，MySQL5.x

* 项目运行方式：从IDEA导入项目，更新maven依赖，然后在MySQL数据库中运行miaosha.sql文件生成数据库。
* 项目入口为：com.miaoshaproject.App，使用IDEA启动后，若端口被占用，修改application.properties中的端口配置。
* 项目采用前后端分离，直接在浏览器打开resources目录下的getotp.html即可。



##	第1章 课程介绍

**电商秒杀应用简介**

> * 商品列表页获取秒杀商品列表
> * 进入商品详情页获取秒杀商品详情
> * 秒杀开始后进入下单确认页下单并支付成功

##	第2章 应用SpringBoot完成基础项目搭建

###	2.1 使用IDEA创建maven项目

1.new->project->maven项目->选择maven-archetype-quickstart

以jar包方式对外输出

​	稍等一会，可能会有点慢

2.新建一个resources目录，作为资源文件目录，指定为Resource root

###	2.2 引入SpringBoot依赖包实现简单的Web项目

进入官方文档https://spring.io/guides/gs/rest-service/

**Building a RESTful Web Service**

1.引入父pom

```xml
<parent>
  <groupId>org.springframework.boot</groupId>
  <artifactId>spring-boot-starter-parent</artifactId>
  <version>2.1.4.RELEASE</version>
</parent>
```

2.引入依赖

```xml
<dependency>
  <groupId>org.springframework.boot</groupId>
  <artifactId>spring-boot-starter-web</artifactId>
</dependency>
```

3.maven Reimport刷新一下，会自动下载相应jar包（注：可以把idea设定为自动导入maven依赖）

4.SpringBoot的Web项目

```java
@EnableAutoConfiguration
@RestController
public class App 
{

    @RequestMapping("/")
    public String home() {
        return "hello World!";
    }
    public static void main( String[] args )
    {
        System.out.println("Hello World!");
        SpringApplication.run(App.class,args);
    }
}
```

再次启动App，访问localhost:8080

###	2.3 Mybatis接入SpringBoot项目

1.SpringBoot的默认配置

在resources目录下新建SpringBoot的默认配置文件application.properties

通过一行简单的属性就能更改tomcat的端口

```xml
server.port=8090
```

2.配置pom文件

```xml
<!--数据库-->
<dependency>
  <groupId>mysql</groupId>
  <artifactId>mysql-connector-java</artifactId>
    <version>5.1.47</version>
</dependency>
<!--数据库连接池-->
<dependency>
  <groupId>com.alibaba</groupId>
  <artifactId>druid</artifactId>
  <version>1.1.3</version>
</dependency>
<!--Mybatis依赖-->
<dependency>
  <groupId>org.mybatis.spring.boot</groupId>
  <artifactId>mybatis-spring-boot-starter</artifactId>
  <version>1.3.1</version>
</dependency>
```

3.配置文件application.properties，设置

`mybatis.mapper-locations=classpath:mapping/*.xml`

然后在resources目录下新建mapping目录

4.**自动生成工具，生成数据库文件的映射**

引入插件

```xml
<!--自动生成工具，生成数据库文件的映射-->
<plugin>
  <groupId>org.mybatis.generator</groupId>
  <artifactId>mybatis-generator-maven-plugin</artifactId>
  <version>1.3.5</version>
  <dependencies>
    <dependency>
      <groupId>org.mybatis.generator</groupId>
      <artifactId>mybatis-generator-core</artifactId>
      <version>1.3.5</version>
    </dependency>
    <dependency>
      <groupId>mysql</groupId>
      <artifactId>mysql-connector-java</artifactId>
      <version>5.1.41</version>
    </dependency>
  </dependencies>
  <executions>
    <execution>
      <id>mybatis generator</id>
      <phase>package</phase>
      <goals>
        <goal>generate</goal>
      </goals>
    </execution>
  </executions>
  <configuration>
    <!--允许移动生成的文件-->
    <verbose>true</verbose>
    <!--允许自动覆盖文件（生产环境中千万不要这样做）-->
    <overwrite>true</overwrite>
    <configurationFile>
      src/main/resources/mybatis-generator.xml
    </configurationFile>
  </configuration>
</plugin>
```

###	2.4 Mybatis自动生成器的使用方式

- 可自动生成封装数据的bean类
- 可以自动生成dao接口
- 可以自动生成mapping文件

1.新建文件src/main/resources/mybatis-generator.xml，从官网下载xml配置文件

http://www.mybatis.org/generator/configreference/xmlconfig.html

2.新建数据库

新建一个miaosha的数据库，并建立两张表，分别是user_info和user_password

表中的列分别为：

user_info

![](https://gitee.com/shilongshen/xiaoxingimagebad/raw/master/img/20210506141304.png)

user_password

![](https://gitee.com/shilongshen/xiaoxingimagebad/raw/master/img/20210506141348.png)

3.修改配置文件`mybatis-generator.xml`

```xml
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE generatorConfiguration
        PUBLIC "-//mybatis.org//DTD MyBatis Generator Configuration 1.0//EN"
        "http://mybatis.org/dtd/mybatis-generator-config_1_0.dtd">

<generatorConfiguration>

    <context id="DB2Tables" targetRuntime="MyBatis3">
        <!--数据库链接地址账号密码-->
        <jdbcConnection driverClass="com.mysql.jdbc.Driver"
                        connectionURL="jdbc:mysql://localhost:3306/miaosha"
                        userId="root"
                        password="123456">
        </jdbcConnection>

        <!--生成DataObject类存放位置-->
        <javaModelGenerator targetPackage="com.miaoshaproject.dataobject" targetProject="src/main/java">
            <property name="enableSubPackages" value="true" />
            <property name="trimStrings" value="true" />
        </javaModelGenerator>

        <!--生成映射文件存放位置-->
        <sqlMapGenerator targetPackage="mapping"  targetProject="src/main/resources">
            <property name="enableSubPackages" value="true" />
        </sqlMapGenerator>

        <!--生成Dao类存放位置-->
        <javaClientGenerator type="XMLMAPPER" targetPackage="com.miaoshaproject.dao"  targetProject="src/main/java">
            <property name="enableSubPackages" value="true" />
        </javaClientGenerator>

        <!--生成对应表及类名-->
        <!--  enableCountByExample="false"
               enableUpdateByExample="false"
               enableDeleteByExample="false"
               enableSelectByExample="false"
               selectByExampleQueryId="false"
               这些属性是为了使得只生成简单查询的对应文件，去掉复杂查询的生成文件，因为一般开发中不太用的到-->
        <table tableName="user_info" domainObjectName="UserDO"
               enableCountByExample="false"
               enableUpdateByExample="false"
               enableDeleteByExample="false"
               enableSelectByExample="false"
               selectByExampleQueryId="false"></table>
        <table tableName="user_password" domainObjectName="userPasswordDO"
               enableCountByExample="false"
               enableUpdateByExample="false"
               enableDeleteByExample="false"
               enableSelectByExample="false"
               selectByExampleQueryId="false" ></table>

    </context>
</generatorConfiguration>
```

4.生成文件

在项目中添加`mybatis-generator:generate`并运行，然后会生成对应的dataObject类、dao类、映射文件

![](https://gitee.com/shilongshen/xiaoxingimagebad/raw/master/img/20210506141021.png)

以user_password表为例，此时会生成`userPasswordDO.java`

```java
/*
会生成对应的字段和相应的get、set方法
*/
package com.example.miaosha.dataobject;

public class userPasswordDO {
    private Integer id
    private String encrptPassword;
    private Integer userId;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getEncrptPassword() {
        return encrptPassword;
    }

    public void setEncrptPassword(String encrptPassword) {
        this.encrptPassword = encrptPassword == null ? null : encrptPassword.trim();
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }
}
```

会生成`userPasswordDOMapper.java`

```java
package com.example.miaosha.dao;import com.example.miaosha.dataobject.userPasswordDO;public interface userPasswordDOMapper {    //通过id进行删除    int deleteByPrimaryKey(Integer id);    //在表中增加一行    int insert(userPasswordDO record);   //增加一行    int insertSelective(userPasswordDO record);    //通过id进行查找    userPasswordDO selectByPrimaryKey(Integer id);   //通过id更新其中的一行    int updateByPrimaryKeySelective(userPasswordDO record);    //更新一行    int updateByPrimaryKey(userPasswordDO record);}
```

会生成`userPasswordDOMapper.xml`

```xml
<?xml version="1.0" encoding="UTF-8"?><!DOCTYPE mapper PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN" "http://mybatis.org/dtd/mybatis-3-mapper.dtd"><mapper namespace="com.example.miaosha.dao.userPasswordDOMapper">  <resultMap id="BaseResultMap" type="com.example.miaosha.dataobject.userPasswordDO">    <!--      WARNING - @mbg.generated      This element is automatically generated by MyBatis Generator, do not modify.      This element was generated on Thu May 06 14:06:08 CST 2021.    -->    <id column="id" jdbcType="INTEGER" property="id" />    <result column="encrpt_password" jdbcType="VARCHAR" property="encrptPassword" />    <result column="user_id" jdbcType="INTEGER" property="userId" />  </resultMap>      <sql id="Base_Column_List">    <!--      WARNING - @mbg.generated      This element is automatically generated by MyBatis Generator, do not modify.      This element was generated on Thu May 06 14:06:08 CST 2021.    -->    id, encrpt_password, user_id  </sql>          <select id="selectByPrimaryKey" parameterType="java.lang.Integer" resultMap="BaseResultMap">    <!--      WARNING - @mbg.generated      This element is automatically generated by MyBatis Generator, do not modify.      This element was generated on Thu May 06 14:06:08 CST 2021.    -->    select     <include refid="Base_Column_List" />    from user_password    where id = #{id,jdbcType=INTEGER}  </select>          <delete id="deleteByPrimaryKey" parameterType="java.lang.Integer">    <!--      WARNING - @mbg.generated      This element is automatically generated by MyBatis Generator, do not modify.      This element was generated on Thu May 06 14:06:08 CST 2021.    -->    delete from user_password    where id = #{id,jdbcType=INTEGER}  </delete>      <insert id="insert" parameterType="com.example.miaosha.dataobject.userPasswordDO">    <!--      WARNING - @mbg.generated      This element is automatically generated by MyBatis Generator, do not modify.      This element was generated on Thu May 06 14:06:08 CST 2021.    -->    insert into user_password (id, encrpt_password, user_id      )    values (#{id,jdbcType=INTEGER}, #{encrptPassword,jdbcType=VARCHAR}, #{userId,jdbcType=INTEGER}      )  </insert>      <insert id="insertSelective" parameterType="com.example.miaosha.dataobject.userPasswordDO">    <!--      WARNING - @mbg.generated      This element is automatically generated by MyBatis Generator, do not modify.      This element was generated on Thu May 06 14:06:08 CST 2021.    -->    insert into user_password    <trim prefix="(" suffix=")" suffixOverrides=",">      <if test="id != null">        id,      </if>      <if test="encrptPassword != null">        encrpt_password,      </if>      <if test="userId != null">        user_id,      </if>    </trim>    <trim prefix="values (" suffix=")" suffixOverrides=",">      <if test="id != null">        #{id,jdbcType=INTEGER},      </if>      <if test="encrptPassword != null">        #{encrptPassword,jdbcType=VARCHAR},      </if>      <if test="userId != null">        #{userId,jdbcType=INTEGER},      </if>    </trim>  </insert>      <update id="updateByPrimaryKeySelective" parameterType="com.example.miaosha.dataobject.userPasswordDO">    <!--      WARNING - @mbg.generated      This element is automatically generated by MyBatis Generator, do not modify.      This element was generated on Thu May 06 14:06:08 CST 2021.    -->    update user_password    <set>      <if test="encrptPassword != null">        encrpt_password = #{encrptPassword,jdbcType=VARCHAR},      </if>      <if test="userId != null">        user_id = #{userId,jdbcType=INTEGER},      </if>    </set>    where id = #{id,jdbcType=INTEGER}  </update>      <update id="updateByPrimaryKey" parameterType="com.example.miaosha.dataobject.userPasswordDO">    <!--      WARNING - @mbg.generated      This element is automatically generated by MyBatis Generator, do not modify.      This element was generated on Thu May 06 14:06:08 CST 2021.    -->    update user_password    set encrpt_password = #{encrptPassword,jdbcType=VARCHAR},      user_id = #{userId,jdbcType=INTEGER}    where id = #{id,jdbcType=INTEGER}  </update></mapper>
```



5.接入mysql数据源

```properties
spring.datasource.name=miaosha
spring.datasource.url=jdbc:mysql://localhost:3306/miaosha
spring.datasource.username=root
spring.datasource.password=123456

#使用druid数据源
spring.datasource.type=com.alibaba.druid.pool.DruidDataSource
spring.datasource.driver-class-name=com.mysql.jdbc.Driver
```

6.测试数据库

修改App类

```java
@SpringBootApplication(scanBasePackages = {"com.miaoshaproject"})
@RestController
@MapperScan("com.miaoshaproject.dao")
public class App {

    @Autowired
    private UserDOMapper userDOMapper;

    @RequestMapping("/")
    public String home() {
        UserDO userDO = userDOMapper.selectByPrimaryKey(1);
        if (userDO == null) {
            return "用户对象不存在";
        } else {
            return userDO.getName();
        }
    }
}
```

启动测试

##	第3章 用户模块开发

###	3.1 使用SpringMVC方式开发用户信息

1.增加controller层、dao层

创建UserController

```java
@Controller("user")
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    @RequestMapping("/get")
    @ResponseBody
    public UserModel getUser(@RequestParam(name = "id") Integer id) {
        //调用service服务获取对应id的用户对象并返回给前端
        UserModel userModel = userService.getUserById(id);
        return userModel;
    }
}
```

userController需要UserModel

> dataobject与数据库一一对应，数据库有什么字段，dataobject就有什么属性
>
> 在service中不能够直接将dataobject返回给想要dataobject的服务（为了安全考虑），所以在service层必须有一个叫model的概念，这个model才是业务逻辑交互的概念。
>
> 
>
> 为了安全考虑是将user_info和user_password放在两张表中的。UserModel相当于将user_info和user_password封装在一起
>
> 因此UserModel需要增加用户的密码，其通过userPasswordDOMapper从userPasswordDO得到
>
> <img src="https://gitee.com/shilongshen/xiaoxingimagebad/raw/master/img/20210509142659.png" style="zoom:80%;" />
>
> 

2.在service层增加UserModel

```java
package com.miaoshaproject.service.model;

/**
 * @author KiroScarlet
 * @date 2019-05-15  -16:50
 */
public class UserModel {
    private Integer id;
    private String name;
    private Byte gender;
    private Integer age;
    private String telphone;
    private String regisitMode;
    private Integer thirdPartyId;
    private String encrptPassword;//账户密码字段也是属于用户中的一个重要信息，为了安全考虑是将它放在另外一个表中
}
```

UserModel需要增加 用户的密码，其通过userPasswordDOMapper从userPasswordDO得到

3.修改userPasswordDOMapper.xml和.java文件

增加方法

```xml
<select id="selectByUserId" parameterType="java.lang.Integer" resultMap="BaseResultMap">
  select
  <include refid="Base_Column_List" />
  from user_password
  where user_id = #{userId,jdbcType=INTEGER}
</select>
```

```java
userPasswordDO selectByUserId(Integer UserId);
```

4.编写UserService

```java
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserDOMapper userDOMapper;

    @Autowired
    private userPasswordDOMapper userPasswordDOMapper;

    @Override
    public UserModel getUserById(Integer id) {
        //调用UserDOMapper获取到对应的用户dataobject
        UserDO userDO = userDOMapper.selectByPrimaryKey(id);
        if (userDO == null) {
            return null;
        }

        //通过用户id获取对应的用户加密密码信息
        userPasswordDO userPasswordDO = userPasswordDOMapper.selectByUserId(userDO.getId());

        return convertFromDataObject(userDO, userPasswordDO);
    }

    private UserModel convertFromDataObject(UserDO userDO,userPasswordDO userPasswordDO) {
        if (userDO == null) {
            return null;
        }

        UserModel userModel = new UserModel();
        BeanUtils.copyProperties(userDO, userModel);

        if (userPasswordDO != null) {
            userModel.setEncrptPassword(userPasswordDO.getEncrptPassword());
        }
        return userModel;

    }
}
```

5.这种方式存在的问题

> 直接给前端用户返回了UserModel，使得攻击者可以直接看到密码
>
> 需要在controller层增加一个viewobject模型对象,使得传给前端必要的信息而不是全部信息

只需要这些信息：

```java
private Integer id;
    private String name;
    private Byte gender;
    private  Integer age;
    private String telphone;
    /**
     * 对于前端展示仅仅只需要以上信息，而不需要以下3个信息
     * */
//    private String regisitMode;
//    private Integer thirdPartyId;
//    private String encrptPassword;
```

6.改造controller

```java
public UserVO getUser(@RequestParam(name = "id") Integer id) {
    //调用service服务获取对应id的用户对象并返回给前端
    UserModel userModel = userService.getUserById(id);

    //将核心领域模型用户对象转化为可供UI使用的viewobject
    return convertFromModel(userModel);
}

private UserVO convertFromModel(UserModel userModel) {
    if (userModel == null) {
        return null;
    }
    UserVO userVO = new UserVO();
    BeanUtils.copyProperties(userModel, userVO);
    return userVO;

}
```

### 3.2 定义通用的返回对象——返回正确信息

之前的程序一旦出错，只会返回一个白页，并没有错误信息，需要返回一个有意义的错误信息。

1.增加一个response包。创建CommonReturnType类

```java
public class CommonReturnType {
//    表明对应请求的返回处理结果，status为“success”或“fail”
    private String status;

//  如果status为“success”，则data内返回前端需要的json数据
//  如果status为“fail”，则data内使用通用的错误码格式
    private Object data;

//    定义一个通用的创建方法
//    当controller完成了处理，调用对应的create方法 ，
//    如果说create方法不带有status，那么对应的status就是“success”
//    然后创建对应的CommonReturnType，并且将status和data进行设置，然后将CommonReturnType返回
    public  static CommonReturnType create(Object result){
        return CommonReturnType.create(result,"success");
    }
    public  static CommonReturnType create(Object result,String status){
        CommonReturnType type=new CommonReturnType();
        type.setStatus(status);
        type.setData(result);
        return type;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }
}
```

2.改造返回值

返回值为固定形式，为`status+data`

```java
public CommonReturnType getUser(@RequestParam(name = "id") Integer id) {
//        调用service服务获取对应id的用户对象并返回给前端
        UserModel userModel = userService.getUserById(id);

//将核心领域模型用户对象转换为可供前端使用的viewobject
       UserVO userVO=convertFromModel(userModel);

//       返回通用对象
       return CommonReturnType.create(userVO);
    }

/**
返回的信息：
{"status":"success","data":{"id":1,"name":"zhangsan","gender":0,"age":99,"telphone":"13525489654"}}
*/
```

### 3.3 定义通用的返回对象——返回错误信息

统一管理错误信息

1.创建error包

2.创建commonError接口

```java
public interface CommonError {
    public int getErrCode();

    public String getErrMsg();

    public CommonError setErrMsg(String errMs);
}
```

3.创建实现类(枚举类)

```java
public enum EmBusinessError implements CommonError {
    //通用错误类型00001
    PARAMETER_VALIDATION_ERROR(00001, "参数不合法"),


    //10000开头为用户信息相关错误定义
    USER_NOT_EXIST(10001, "用户不存在")
    ;

    private EmBusinessError(int errCode, String errMsg) {
        this.errCode = errCode;
        this.errMsg = errMsg;
    }

    private int errCode;
    private String errMsg;

    @Override
    public int getErrCode() {
        return this.errCode;
    }

    @Override
    public String getErrMsg() {
        return this.errMsg;
    }

    @Override
    public CommonError setErrMsg(String errMsg) {
        this.errMsg = errMsg;
        return this;
    }
}
```

4.包装器模式实现BusinessException类

```java
/**
 * 包装器业务异常类实现
 * BusinessException和EmBusinessError都共同继承了CommonError
 * 所以外部不仅可以通过new BusinessException而且可以通过new EmBusinessError
 * 都可以获得code和errMsg的组装定义
 * 并且需要实现setErrorMsg，用于将errMsg覆盖掉
 * */
public class BusinessException extends Exception implements CommonError{
    private CommonError commonError;

//    直接接收EmBusinessError的传参用于构造业务异常
    public BusinessException(CommonError commonError) {
        super();
        this.commonError = commonError;
    }
    
//接收自定义errMsg的方式构造业务异常
    public BusinessException(CommonError commonError, String errMsg) {
        super();
        this.commonError = commonError;
        this.commonError.setErrorMsg(errMsg);//将ErrMsg重新设置
    }

    @Override
    public int getErrorCode() {
        return this.commonError.getErrorCode();
    }

    @Override
    public String getErrorMsg() {
        return this.commonError.getErrorMsg();
    }

    @Override
    public CommonError setErrorMsg(String errMsg) {
         this.commonError.setErrorMsg(errMsg);
        return this;
    }
}
```

5.抛出异常类

```java
public class UserController extends BaseController{//继承BaseController

    @Autowired
    private UserService userService;

    @RequestMapping("/get")
    @ResponseBody
    public CommonReturnType getUser(@RequestParam(name = "id") Integer id) throws BusinessException {
//        调用service服务获取对应id的用户对象并返回给前端
        UserModel userModel = userService.getUserById(id);

//        如果获取的对应用户信息不存在,直接抛出一个异常，交由BusinessException处理
        if (userModel==null){
//            userModel.setEncrptPassword("123");
            throw new BusinessException(EmBusinessError.USER_NOT_EXIST);
        }

//将核心领域模型用户对象转换为可供前端使用的viewobject
       UserVO userVO=convertFromModel(userModel);

//       返回通用对象
       return CommonReturnType.create(userVO);
    }

    private UserVO convertFromModel(UserModel userModel){
        if (userModel==null){
            return null;
        }
        UserVO userVO=new UserVO();
        BeanUtils.copyProperties(userModel,userVO);
        return  userVO;
    }
}
```

### 3.4 定义通用的返回对象——异常处理

1.定义exceptionHandler解决未被controller层吸收的exception

```java
public class BaseController {

//    异常处理模块
    //    定义exceptionhandler解决未被controller层吸收的exception异常
//    就是之前的抛出的BusinessException可以交给这里来处理，或者是其他异常
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody//返回json
    public Object handlerException(HttpServletRequest request, Exception exception){
        
        Map<String,Object> responseData=new HashMap<>();
        if (exception instanceof BusinessException){//如果抛出的为BusinessException，
            BusinessException businessException= (BusinessException) exception;
            responseData.put("errCode",businessException.getErrorCode());
            responseData.put("errMsg",businessException.getErrorMsg());
        }else {//如果不是抛出的BusinessException就提示未知错误
            responseData.put("errCode", EmBusinessError.UNKNOWN_ERROR.getErrorCode());
            responseData.put("errMsg",EmBusinessError.UNKNOWN_ERROR.getErrorMsg());
        }
        
        return CommonReturnType.create(responseData,"fail");
    }
}
```

### 3.5 用户模型管理——otp验证码获取

通过手机号注册的流程：

- otp短信获取
- otp注册用户
- 用户手机登录

```java
public class UserController extends BaseController{

    @Autowired
    private UserService userService;

    @Autowired
    private HttpServletRequest httpServletRequest;

    //用户获取otp短信接口
    @RequestMapping("/getotp")
    @ResponseBody
    public CommonReturnType getOtp(@RequestParam(name = "telphone") String telphone) {
        //需要按照一定的规则生成OTP验证码
        Random random = new Random();
        int randomInt = random.nextInt(99999);
        randomInt += 10000;
        String otpCode = String.valueOf(randomInt);

        //将OTP验证码同对应用户的手机号关联，使用httpsession的方式绑定手机号与OTPCDOE
        httpServletRequest.getSession().setAttribute(telphone, otpCode);

        //将OTP验证码通过短信通道发送给用户，省略
        System.out.println("telphone=" + telphone + "&otpCode=" + otpCode);

        return CommonReturnType.create(null);
    }
```

测试，在控制台打印数据

### 3.6 用户模型管理——Metronic模板简介

- 利用已有的模板来快速构建页面

- Metronic：基于bootstrap的付费ui模版

采用前后端分离的思想，建立一个html文件夹，引入static文件夹

前端文件保存在本地的哪个盘下都可以，因为是通过ajax来异步获取接口

###	3.7 用户模型管理——getotp页面实现

1.getotp.html：

```html
<html>
<head>
    <meta charset="UTF-8">
    <script src="static/assets/global/plugins/jquery-1.11.0.min.js" type="text/javascript"></script>
    <title>Title</title>
</head>
<body>
    <div>
        <h3>获取otp信息</h3>
        <div>
            <label>手机号</label>
            <div>
                <input type="text" placeholder="手机号" name="telphone" id="telphone"/>
            </div>
        </div>
        <div>
            <button id="getotp" type="submit">
                获取otp短信
            </button>
        </div>
    </div>

</body>

<script>
    jQuery(document).ready(function () {

        //绑定otp的click事件用于向后端发送获取手机验证码的请求
        $("#getotp").on("click",function () {

            var telphone=$("#telphone").val();
            if (telphone==null || telphone=="") {
                alert("手机号不能为空");
                return false;
            }


            //映射到后端@RequestMapping(value = "/getotp", method = {RequestMethod.POST}, consumes = {CONTENT_TYPE_FORMED})
            $.ajax({
                type:"POST",
                //发送信息至服务器时内容编码类型
                contentType:"application/x-www-form-urlencoded",
                //当前页地址。发送请求的地址。
                url:"http://localhost:8080/user/getotp",
                //发送到服务器的数据。将自动转换为请求字符串格式。
                data:{
                    "telphone":$("#telphone").val(),
                },
                //请求成功后的回调函数。
                success:function (data) {
                    if (data.status=="success") {
                        alert("otp已经发送到了您的手机，请注意查收");
                    }else {
                        alert("otp发送失败，原因为" + data.data.errMsg);
                    }
                },
                //在请求出错时调用
                error:function (data) {
                    alert("otp发送失败，原因为"+data.responseText);
                }
            });
        });
    });
</script>
</html>
```

2.指定controller的method

```java
@RequestMapping(value = "/getotp", method = {RequestMethod.POST}, consumes = {CONTENT_TYPE_FORMED})
```

3.提示发送失败，使用chrome调试，发现报错为

```
getotp.html?_ijt=cqdae6hmhq9069c9s4muooakju:1 Access to XMLHttpRequest at 'http://localhost:8080/user/getotp' from origin 'http://localhost:63342' has been blocked by CORS policy: No 'Access-Control-Allow-Origin' header is present on the requested resource.
```

跨域请求错误，只需要在UserController类上加一个注解`@CrossOrigin`即可

###	3.8 用户模型管理——getotp页面美化

1.引入样式表

```html
<link href="static/assets/global/plugins/bootstrap/css/bootstrap.min.css" rel="stylesheet" type="text/css"/>
<link href="static/assets/global/plugins/css/component.css" rel="stylesheet" type="text/css"/>
<link href="static/assets/admin/pages/css/login.css" rel="stylesheet" type="text/css"/>
```

2.使用样式

```html
<body class="login">
    <div class="content">
        <h3 class="form-title">获取otp信息</h3>
        <div class="form-group">
            <label class="control-label">手机号</label>
            <div>
                <input class="form-control" type="text" placeholder="手机号" name="telphone" id="telphone"/>
            </div>
        </div>
        <div class="form-actions">
            <button class="btn blue" id="getotp" type="submit">
                获取otp短信
            </button>
        </div>
    </div>

</body>
```

###	3.9 用户模型管理——用户注册功能实现

1.实现方法：用户注册接口

```java
     //用户注册接口
    @RequestMapping(value = "/register", method = {RequestMethod.POST}, consumes = {CONTENT_TYPE_FORMED})
    @ResponseBody
    public CommonReturnType register(@RequestParam(name = "telphone") String telphone,
                                     @RequestParam(name = "otpCode") String otpCode,
                                     @RequestParam(name = "name") String name,
                                     @RequestParam(name = "gender") String gender,
                                     @RequestParam(name = "age") String age,
                                     @RequestParam(name = "password") String password) throws BusinessException, UnsupportedEncodingException, NoSuchAlgorithmException {

        //验证手机号和对应的otpCode相符合
        String inSessionOtpCode = (String) this.httpServletRequest.getSession().getAttribute(telphone);
        if (!com.alibaba.druid.util.StringUtils.equals(otpCode, inSessionOtpCode)) {
            throw new BusinessException(EmBusinessError.PARAMETER_VALIDATION_ERROR, "短信验证码不符合");
        }
        //用户的注册流程
        UserModel userModel = new UserModel();
        userModel.setName(name);
        userModel.setAge(Integer.valueOf(age));
        userModel.setGender(Byte.valueOf(gender));
        userModel.setTelphone(telphone);
        userModel.setRegisitMode("byphone");

        //密码加密
        userModel.setEncrptPassword(this.EncodeByMd5(password));

        userService.register(userModel);
        return CommonReturnType.create(null);

    }

    //使用MD5方式进行密码加密
    public String EncodeByMd5(String str) throws NoSuchAlgorithmException, UnsupportedEncodingException {
        //确定计算方法
        MessageDigest md5 = MessageDigest.getInstance("MD5");
        BASE64Encoder base64en = new BASE64Encoder();
        //加密字符串
        String newstr = base64en.encode(md5.digest(str.getBytes("utf-8")));
        return newstr;
    }
```

2.引入做输入校验的依赖

```xml
<!-- https://mvnrepository.com/artifact/org.apache.commons/commons-lang3 -->
<dependency>
  <groupId>org.apache.commons</groupId>
  <artifactId>commons-lang3</artifactId>
  <version>3.7</version>
</dependency>
```

3.UserServiceImpl的register方法

```java
//    完成用户注册
    @Override
    @Transactional//事务
    public void register(UserModel userModel) throws BusinessException {
//控制判断，如果为空直接抛异常
        if (userModel == null) {
            throw new BusinessException(EmBusinessError.PARAMETER_VALIDATION_ERROR, "未创建用户");
        }
//        后端校验
        if (StringUtils.isEmpty(userModel.getName())
                || userModel.getGender() == null
                || userModel.getAge() == null
                || StringUtils.isEmpty(userModel.getTelphone())) {
            throw new BusinessException(EmBusinessError.PARAMETER_VALIDATION_ERROR, "用户信息填写格式有误");
        }
        //如果用户信息填写无误，就创建用户，写入数据库中

//        实现UserModel->UserDao
        UserDO userDO = convertFromModel(userModel);
//        将userDO写入数据库
        try {
            userDOMapper.insertSelective(userDO);
        } catch (DuplicateKeyException exception) {
            throw new BusinessException(EmBusinessError.PARAMETER_VALIDATION_ERROR,"手机号已被注册");
        }

//user_password表中的user_id就等于user_info表中的id，取出user_info中的自增主键，并赋值给userModel，
        userModel.setId(userDO.getId());

//        实现UserModel->userPasswordDO
        userPasswordDO userPasswordDO = convertuserPasswordDOFromModel(userModel);
//        将userPasswordDO写数数据库中
        userPasswordDOMapper.insertSelective(userPasswordDO);

/**
 * 为什么要使用insertSelective而不是insert?
 *insertSelective相对于insert方法，不会覆盖掉数据库的默认值
 * */

    }

    //实现UserModel->UserDao
    private UserDO convertFromModel(UserModel userModel) {
        if (userModel == null) {
            return null;
        }
        UserDO userDO = new UserDO();
//        将userModel的属性复制到userDO
        BeanUtils.copyProperties(userModel, userDO);
        return userDO;
    }

    //实现UserModel->userPasswordDO
    private userPasswordDO convertuserPasswordDOFromModel(UserModel userModel) {
        if (userModel == null) {
            return null;
        }
        userPasswordDO userPasswordDO = new userPasswordDO();
        userPasswordDO.setEncrptPassword(userModel.getEncrptPassword());
        userPasswordDO.setUserId(userModel.getId());
        return userPasswordDO;
    }
```

**为什么要使用insertSelective而不是insert?**

 先来看一看两者的语句

```xml
<insert id="insert" parameterType="com.example.miaosha.dataobject.UserDO">
    insert into user_info (id, name, gender, 
      age, telphone, regisit_mode, 
      third_party_id)
    values (#{id,jdbcType=INTEGER}, #{name,jdbcType=VARCHAR}, #{gender,jdbcType=TINYINT}, 
      #{age,jdbcType=INTEGER}, #{telphone,jdbcType=VARCHAR}, #{regisitMode,jdbcType=VARCHAR}, 
      #{thirdPartyId,jdbcType=INTEGER})
  </insert>
```

注意要使用`keyProperty="id" useGeneratedKeys="true"`，这样才能够保证进行主键自增

```xml
<insert id="insertSelective" parameterType="com.example.miaosha.dataobject.UserDO" keyProperty="id" useGeneratedKeys="true">
    insert into user_info
    <trim prefix="(" suffix=")" suffixOverrides=",">
      <if test="id != null">
        id,
      </if>
      <if test="name != null">
        name,
      </if>
      <if test="gender != null">
        gender,
      </if>
      <if test="age != null">
        age,
      </if>
      <if test="telphone != null">
        telphone,
      </if>
      <if test="regisitMode != null">
        regisit_mode,
      </if>
      <if test="thirdPartyId != null">
        third_party_id,
      </if>
    </trim>
    <trim prefix="values (" suffix=")" suffixOverrides=",">
      <if test="id != null">
        #{id,jdbcType=INTEGER},
      </if>
      <if test="name != null">
        #{name,jdbcType=VARCHAR},
      </if>
      <if test="gender != null">
        #{gender,jdbcType=TINYINT},
      </if>
      <if test="age != null">
        #{age,jdbcType=INTEGER},
      </if>
      <if test="telphone != null">
        #{telphone,jdbcType=VARCHAR},
      </if>
      <if test="regisitMode != null">
        #{regisitMode,jdbcType=VARCHAR},
      </if>
      <if test="thirdPartyId != null">
        #{thirdPartyId,jdbcType=INTEGER},
      </if>
    </trim>
  </insert>
```

`insertSelective`会首先判断对应的字段在dataobject中是否为null，如果不为null的话就执行insert操作，如果为null的话就跳过，不insert这个字段，不insert的意思是完全依赖于数据库，数据库提供什么默认值，我就提供什么默认值。<u>`insertSelective`操作中dataobject中某个字段为null表示未定义，且不将数据库中的默认值进行覆盖</u>，(未定义，不覆盖)这种方式在`update`中尤其有用，如果某个字段为null，就表示不对这个值进行更新，保持默认值。

如果采用的是`insert`这种方式，如果dataobject中对应的某个字段为null的话，就会用null覆盖掉数据库中的值。

经验，在一般的数据库设计中，尽量避免使用null字段，并且使用默认值，好处：

- java在处理空指针时是非常脆弱的

- null对于前端的展示是没有任何意义的，

  null表示未定义，这个未定义只在程序级别有效，对于用户级别他在界面看到的就是一个空字符串（或其他）

为什么是尽量避免，而不是所用情况下都不要使用呢（就是说有的情况下可以使用null），理由是，

假设手机号（telphone）在表中只能够有一个（telphone不能重复），为了达到这个目的一般情况下会给telphone加上唯一索引。但是如果通过第三方注册（third_party_id）是没有手机号的（或者做一个强绑定，third_party_id必须有一个手机号），此时会遇到一个情况，因为将telphone设置为了not null，必须在其他情况是一个非空字符串

但是null是不受唯一索引约束的，就是说有两条手机号都是null是不会影响唯一索引的。

所以说当用户必须要有手机号的时候，就可以将手机号设置为not null，并指定字段是唯一的key（唯一索引），

当用户不一定有手机号的，如果还将手机号设置为not null，那唯一索引就加不上去了，只能通过在应用程序呢通过其他方式去解决。

<img src="https://gitee.com/shilongshen/xiaoxingimagebad/raw/master/img/20210512153128.png" style="zoom:50%;" />



4.前端界面

首先在getotp界面添加注册成功的跳转界面

```javascript
success:function (data) {
    if (data.status=="success") {
        alert("otp已经发送到了您的手机，请注意查收");
        window.location.href="register.html";
    }else {
        alert("otp发送失败，原因为" + data.data.errMsg);
    }
},
```

模仿之前写的界面，新建一个register.html

```html
<body class="login">
    <div class="content">
        <h3 class="form-title">用户注册</h3>
        
        <div class="form-group">
            <label class="control-label">手机号</label>
            <div>
                <input class="form-control" type="text" placeholder="手机号" name="telphone" id="telphone"/>
            </div>
        </div>
        
        <div class="form-group">
            <label class="control-label">验证码</label>
            <div>
                <input class="form-control" type="text" placeholder="验证码" name="otpCode" id="otpCode"/>
            </div>
        </div>
        
        <div class="form-group">
            <label class="control-label">用户昵称</label>
            <div>
                <input class="form-control" type="text" placeholder="用户昵称" name="name" id="name"/>
            </div>
        </div>
        
        <div class="form-group">
            <label class="control-label">性别</label>
            <div>
                <input class="form-control" type="text" placeholder="性别" name="gender" id="gender"/>
            </div>
        </div>
        
        <div class="form-group">
            <label class="control-label">年龄</label>
            <div>
                <input class="form-control" type="text" placeholder="年龄" name="age" id="age"/>
            </div>
        </div>
        
        <div class="form-group">
            <label class="control-label">密码</label>
            <div>
                <input class="form-control" type="password" placeholder="密码" name="password" id="password"/>
            </div>
        </div>
        
        <div class="form-actions">
            <button class="btn blue" id="register" type="submit">
                提交注册
            </button>
        </div>
        
    </div>

</body>

<script>
    jQuery(document).ready(function () {

        //绑定otp的click事件用于向后端发送获取手机验证码的请求
        $("#register").on("click",function () {
//$("#telphone").val()：选择id为telphone的元素，并命名变量telphone
            var telphone=$("#telphone").val();
            var otpCode=$("#otpCode").val();
            var password=$("#password").val();
            var age=$("#age").val();
            var gender=$("#gender").val();
            var name=$("#name").val();
            
            /**
            在前端进行校验开始
            为什么后端进行了校验，前端还要做校验？
            
            */
            if (telphone==null || telphone=="") {
                alert("手机号不能为空");
                return false;
            }
            if (otpCode==null || otpCode=="") {
                alert("验证码不能为空");
                return false;
            }
            if (name==null || name=="") {
                alert("用户名不能为空");
                return false;
            }
            if (gender==null || gender=="") {
                alert("性别不能为空");
                return false;
            }
            if (age==null || age=="") {
                alert("年龄不能为空");
                return false;
            }
            if (password==null || password=="") {
                alert("密码不能为空");
                return false;
            }
            
            /**
            在前端进行校验结束
            */

            //映射到后端@RequestMapping(value = "/register", method = {RequestMethod.POST}, consumes = {CONTENT_TYPE_FORMED})
            $.ajax({
                type:"POST",
                contentType:"application/x-www-form-urlencoded",
                url:"http://localhost:8080/user/register",
                data:{
                    "telphone":telphone,
                    "otpCode":otpCode,
                    "password":password,
                    "age":age,
                    "gender":gender,
                    "name":name
                },
                //允许跨域请求
                xhrFields:{withCredentials:true},
                success:function (data) {
                    if (data.status=="success") {
                        alert("注册成功");
                    }else {
                        alert("注册失败，原因为" + data.data.errMsg);
                    }
                },
                error:function (data) {
                    alert("注册失败，原因为"+data.responseText);
                }
            });
            return false;
        });
    });
</script>
```

> 为什么后端进行了校验，前端还要做校验？
>
> 因为校验必须要发生在离用户最近的地方，如果校验能够在界面尽早的被用户感知，可以增强用户的体验，而不用在后端服务器绕一圈。
>
> 当然后端的校验是必不可少的，因为这是进入数据库的唯一一道阀门

5.调试

发现报错，获取不到验证码

跨域请求问题

在UserController上添加如下注解：

```java
//DEFAULT_ALLOWED_HEADERS：允许跨域传输所有header参数，将用于使用token放入header域，做session共享的跨域请求
//DEFAULT_ALLOW_CREDENTIALS=true;需配合前端设置xhrFields授信后使得跨域session共享
//跨域请求中，不能做到session共享
@CrossOrigin(allowCredentials = "true",allowedHeaders = "*")
```

6.注册成功，但是查看数据库，发现password表中并没有user_id

原因是`insertSelective`并没有指定数据库的`keyProperty`，id是user_info的自增主键

> MyBatis中sql映射文件的参数
>
> | `useGeneratedKeys` | （仅适用于 insert 和 update）这会令 MyBatis 使用 JDBC 的 getGeneratedKeys 方法来取出由数据库内部生成的主键（比如：像 MySQL 和 SQL Server 这样的关系型数据库管理系统的自动递增字段），默认值：false。 |
> | ------------------ | ------------------------------------------------------------ |
> | `keyProperty`      | （仅适用于 insert 和 update）指定能够唯一识别对象的属性（主键），MyBatis 会使用 getGeneratedKeys 的返回值或 insert 语句的 selectKey 子元素设置它的值，默认值：未设置（`unset`）。如果生成列不止一个，可以用逗号分隔多个属性名称。 |

在UserDOMapper的insertSelective方法中添加如下代码：

```xml
 <insert id="insertSelective" parameterType="com.miaoshaproject.dataobject.UserDO" keyProperty="id" useGeneratedKeys="true">
 <!--
keyProperty="id"：设置主键为id，
useGeneratedKeys="true"：取出自动生成的主键
-->
```

通过这样的方式将自增id取出之后复制给对应的userModel，以便于将其转换为userPasswordDO

7.修改UserServiceImpl

```java
UserDO userDO = convertFromModel(userModel);
//insertSelective相对于insert方法，不会覆盖掉数据库的默认值
userDOMapper.insertSelective(userDO);

userModel.setId(userDO.getId());//将自增id取出之后复制给对应的userModel

userPasswordDO userPasswordDO = convertPasswordFromModel(userModel);
userPasswordDOMapper.insertSelective(userPasswordDO);

return;
```

重新测试成功

8.上面并没有做手机号的唯一性验证

<u>使用相同的手机号可以进行多次的注册</u>；解决方式：将user_info表中将`telphone`字段设置为唯一索引

首先，在数据库中添加索引：

索引名称为：telphone_unique_index，索引字段选择telphone，索引类型为UNIQUE，索引方法为BTREE

![](https://gitee.com/shilongshen/xiaoxingimagebad/raw/master/img/20210512145505.png)

此时尝试使用相同的手机号进行注册，控制台会出现以下错误提示：

```shell
Error updating database.  Cause: java.sql.SQLIntegrityConstraintViolationException: Duplicate entry '13632562145' for key 'user_info.telphone_unique_index'
The error may exist in file [D:\Projects\IdeaProjects\ProjectsLearning\miaosha\target\classes\mapping\UserDOMapper.xml]
The error may involve com.example.miaosha.dao.UserDOMapper.insertSelective-Inline
The error occurred while setting parameters
SQL: insert into user_info( name,gender,age,telphone,regisit_mode ) values ( ?,?,?,?,? )
Cause: java.sql.SQLIntegrityConstraintViolationException: Duplicate entry '13632562145' for key 'user_info.telphone_unique_index'; Duplicate entry '13632562145' for key 'user_info.telphone_unique_index'; nested exception is java.sql.SQLIntegrityConstraintViolationException: Duplicate entry '13632562145' for key 'user_info.telphone_unique_index']
```

为了提供更好的用户体验，修改以下代码：

```java
try {
    userDOMapper.insertSelective(userDO);
} catch (DuplicateKeyException ex) {
    throw new BusinessException(EmBusinessError.PARAMETER_VALIDATION_ERROR, "手机号已注册");
}
```

###	3.9 用户模型管理——用户登录功能实现

1.UserController中的用户登录接口

```java
    //用户登录接口
    @RequestMapping(value = "/login", method = {RequestMethod.POST}, consumes = {CONTENT_TYPE_FORMED})
    @ResponseBody
    public CommonReturnType login(@RequestParam(name = "telphone") String telphone,
                                  @RequestParam(name = "password") String password) throws BusinessException, UnsupportedEncodingException, NoSuchAlgorithmException {
        //入参校验
        if (StringUtils.isEmpty(telphone) || StringUtils.isEmpty(password)) {
            throw new BusinessException(EmBusinessError.PARAMETER_VALIDATION_ERROR);
        }

        //用户登录服务，用来校验用户登录是否合法
        //用户加密后的密码
        UserModel userModel = userService.validateLogin(telphone, this.EncodeByMd5(password));

        //将登陆凭证加入到用户登录成功的session内
        this.httpServletRequest.getSession().setAttribute("IS_LOGIN", true);
        this.httpServletRequest.getSession().setAttribute("LOGIN_USER", userModel);

        return CommonReturnType.create(null);

    }
```

2.UserService中的校验登录方法

```java
    /*
    telphone:用户注册手机
    encrptPassowrd:用户加密后的密码
     */
    UserModel validateLogin(String telphone, String encrptPassword) throws BusinessException;
```

3.UserServiceImpl的登录方法实现

```java
    @Override
    public UserModel validateLogin(String telphone, String encrptPassword) throws BusinessException {
        //通过用户手机获取用户信息
        UserDO userDO = userDOMapper.selectByTelphone(telphone);
        if (userDO == null) {
            throw new BusinessException(EmBusinessError.USER_LOOGIN_FAIL);
        }
        userPasswordDO userPasswordDO = userPasswordDOMapper.selectByUserId(userDO.getId());
        UserModel userModel = convertFromDataObject(userDO, userPasswordDO);

        //比对用户信息内加密的密码是否和传输进来的密码相匹配
        if (StringUtils.equals(encrptPassword, userModel.getEncrptPassword())) {
            throw new BusinessException(EmBusinessError.USER_LOOGIN_FAIL);
        }

        return userModel;
    }
```

4.UserDOMapper.xml中的新建方法

```xml
<select id="selectByTelphone" resultMap="BaseResultMap">
    select
    <include refid="Base_Column_List"/>
    from user_info
    where telphone = #{telphone,jdbcType=VARCHAR}
</select>
```

5.UserDOMapper中建立映射

```java
//根据电话号码取得用户对象
UserDO selectByTelphone(String telphone);
```

6.新建前端界面：login.html

```html
<body class="login">
    <div class="content">
        <h3 class="form-title">用户登录</h3>
        <div class="form-group">
            <label class="control-label">手机号</label>
            <div>
                <input class="form-control" type="text" placeholder="手机号" name="telphone" id="telphone"/>
            </div>
        </div>
        <div class="form-group">
            <label class="control-label">密码</label>
            <div>
                <input class="form-control" type="password" placeholder="密码" name="password" id="password"/>
            </div>
        </div>
        <div class="form-actions">
            <button class="btn blue" id="login" type="submit">
                登录
            </button>
            <button class="btn green" id="register" type="submit">
                注册
            </button>
        </div>
    </div>

</body>

<script>
    jQuery(document).ready(function () {

        //绑定注册按钮的click事件用于跳转到注册页面
        $("#register").on("click",function () {
            window.location.href = "getotp.html";
        });

        //绑定登录按钮的click事件用于登录
        $("#login").on("click",function () {

            var telphone=$("#telphone").val();
            var password=$("#password").val();
            if (telphone==null || telphone=="") {
                alert("手机号不能为空");
                return false;
            }
            if (password==null || password=="") {
                alert("密码不能为空");
                return false;
            }

            //映射到后端@RequestMapping(value = "/login", method = {RequestMethod.POST}, consumes = {CONTENT_TYPE_FORMED})
            $.ajax({
                type:"POST",
                contentType:"application/x-www-form-urlencoded",
                url:"http://localhost:8080/user/login",
                data:{
                    "telphone":telphone,
                    "password":password
                },
                //允许跨域请求
                xhrFields:{withCredentials:true},
                success:function (data) {
                    if (data.status=="success") {
                        alert("登录成功");
                    }else {
                        alert("登录失败，原因为" + data.data.errMsg);
                    }
                },
                error:function (data) {
                    alert("登录失败，原因为"+data.responseText);
                }
            });
            return false;
        });
    });
```

###	3.10 优化校验规则

校验规则

1.查询maven仓库中是否由可用类库

```xml
<!--校验-->
<dependency>
  <groupId>org.hibernate</groupId>
  <artifactId>hibernate-validator</artifactId>
  <version>5.2.4.Final</version>
</dependency>
```

2.对validator进行一个简单的封装

新建validator的目录

新建一个ValidationResult的类

```java
public class ValidationResult {
    //校验结果是否有错
    private boolean hasErrors = false;

    //存放错误信息的map
    private Map<String, String> errorMsgMap = new HashMap<>();

    public boolean isHasErrors() {
        return hasErrors;
    }

    public void setHasErrors(boolean hasErrors) {
        this.hasErrors = hasErrors;
    }

    public Map<String, String> getErrorMsgMap() {
        return errorMsgMap;
    }

    public void setErrorMsgMap(Map<String, String> errorMsgMap) {
        this.errorMsgMap = errorMsgMap;
    }

    //实现通用的通过格式化字符串信息获取错误结果的msg方法
    public String getErrMsg() {
        return StringUtils.join(errorMsgMap.values().toArray(), ",");
    }
}
```

新建一个ValidatiorImpl的类

```java
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

```

3.修改UserModel，基于注解的校验方式

```java
@NotBlank(message = "用户名不能为空")
private String name;

@NotNull(message = "性别不能填写")
private Byte gender;

@NotNull(message = "年龄不能不填写")
@Min(value = 0, message = "年龄必须大于0岁")
@Max(value = 150, message = "年龄必须小于150岁")
private Integer age;

@NotBlank(message = "手机号不能为空")
private String telphone;
private String regisitMode;
private Integer thirdPartyId;

@NotBlank(message = "密码不能为空")
private String encrptPassword;
```

4.在UserServiceImpl中使用validator

引入bean

```java
@Autowired
private ValidatorImpl validator;
```



```java
        //校验
//        if (userModel == null) {
//            throw new BusinessException(EmBusinessError.PARAMETER_VALIDATION_ERROR);
//        }
//        if (StringUtils.isEmpty(userModel.getName())
//                || userModel.getGender() == null
//                || userModel.getAge() == null
//                || StringUtils.isEmpty(userModel.getTelphone())) {
//            throw new BusinessException(EmBusinessError.PARAMETER_VALIDATION_ERROR);
//        }

        ValidationResult result = validator.validate(userModel);
        if (result.isHasErrors()) {
            throw new BusinessException(EmBusinessError.PARAMETER_VALIDATION_ERROR, result.getErrMsg());
        }
```

以后做校验时只需要在model的属性上做注解即可

1.运行时报错

```shell
...... No validator could be found for constraint 'javax.validation.constraints.Not  ......
```

```java
import org.hibernate.validator.constraints.NotBlank;
//import javax.validation.constraints.NotBlank;   将导入的包进行替换


public class UserModel {
    private Integer id;

    @NotBlank(message = "用户名不能为空")//说明name不能为空字符串并且不能为null，否则报错message
    private String name;

......
}
```



##	第4章 商品模块开发

包括了商品创建和商品的展示

###	4.1 商品模型管理——商品创建

经验：在做任何一个业务之前，首先应该想好对应的商品模型是什么样的

1.首先设计商品领域模型

```java
public class ItemModel {
    private Integer id;

    //商品名称
    private String title;

    //商品价格
    private BigDecimal price;

    //商品的库存
    private Integer stock;

    //商品的描述
    private String description;

    //商品的销量
    private Integer sales;

    //商品描述图片的url
    private String imgUrl;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public Integer getStock() {
        return stock;
    }

    public void setStock(Integer stock) {
        this.stock = stock;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getSales() {
        return sales;
    }

    public void setSales(Integer sales) {
        this.sales = sales;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }
}
```

2.设计数据库

思考：是否应该按照商品模型一模一样的设计数据库表？

两张表：商品表`item`和库存表`item_stock`

分表的原因：每次对商品表的操作就是对库存表的操作，库存进行分表操作以便于后续做一些性能的优化

商品表`item`：

![](https://gitee.com/shilongshen/xiaoxingimagebad/raw/master/img/20210512222702.png)

库存表`item_stock`：

![](https://gitee.com/shilongshen/xiaoxingimagebad/raw/master/img/20210512222822.png)

3.修改pom文件

```xml
<!--允许移动生成的文件-->
<verbose>true</verbose>
<!--允许自动覆盖文件（生产环境中千万不要这样做）-->
<overwrite>false</overwrite>
```

4.修改mybatis-generator配置文件

添加两张表

运行`	mvn mybatis-generator:generate`

5.修改mapper的xml文件

把insert和insertSelective方法后添加属性 `	keyProperty="id" useGeneratedKeys="true"`，使其保持自增

6.创建ItemService接口

```java
public interface ItemService {

    //创建商品
    ItemModel createItem(ItemModel itemModel);

    //商品列表浏览
    List<ItemModel> listItem();


    //商品详情浏览
    ItemModel getItemById(Integer id);
}
```

7.ItemServiceImpl实现类

入参校验

```java
//商品名称
@NotBlank(message = "商品名称不能为空")
private String title;

//商品价格
@NotNull(message = "商品价格不能为空")
@Min(value = 0,message = "商品价格必须大于0")
private BigDecimal price;

//商品的库存
@NotNull(message = "库存不能不填")
private Integer stock;

//商品的描述
@NotBlank(message = "商品描述信息不能为空")
private String description;

//商品的销量
@NotBlank(message = "商品图片信息不能为空")
private Integer sales;

//商品描述图片的url
private String imgUrl;
```

实现方法

```xml
<!--ItemStockDOMapper.xml中添加-->

<select id="selectByItemId" parameterType="java.lang.Integer" resultMap="BaseResultMap">
    <!--
      WARNING - @mbg.generated
      This element is automatically generated by MyBatis Generator, do not modify.
      This element was generated on Thu May 13 19:56:33 CST 2021.
    -->
    select
    <include refid="Base_Column_List" />
    from item_stock
    where item_id = #{itemId,jdbcType=INTEGER}
  </select>
```

```java
//ItemStockDOMapper添加
//<!--通过itemId获得用户库存  -->
    ItemStockDO selectByItemId(Integer itemId);
```



```java
@Service
public class ItemServiceImpl implements ItemService {

    @Autowired
    private ValidatorImpl validator;

    @Autowired
    private ItemDOMapper itemDOMapper;

    @Autowired
    private ItemStockDOMapper itemStockDOMapper;

    @Override
    @Transactional
    public ItemModel createItem(ItemModel itemModel) throws BusinessException {

        //校验入参
        ValidationResult result = validator.validate(itemModel);
        if (result.isHasErrors()) {
            throw new BusinessException(EmBusinessError.PARAMETER_VALIDATION_ERROR, result.getErrMsg());
        }
        //转化itemmodel->dataobject
        ItemDO itemDO = this.convertItemDOFromItemModel(itemModel);

        //写入数据库
        itemDOMapper.insertSelective(itemDO);
        itemModel.setId(itemDO.getId());

        ItemStockDO itemStockDO = this.convertItemStockDOFromItemModel(itemModel);
        itemStockDOMapper.insertSelective(itemStockDO);

        //返回创建完成的对象
        return this.getItemById(itemModel.getId());

    }

    private ItemDO convertItemDOFromItemModel(ItemModel itemModel) {
        if (itemModel == null) {
            return null;
        }
        ItemDO itemDO = new ItemDO();
        BeanUtils.copyProperties(itemModel, itemDO);
        return itemDO;
    }

    private ItemStockDO convertItemStockDOFromItemModel(ItemModel itemModel) {
        if (itemModel == null) {
            return null;
        }
        ItemStockDO itemStockDO = new ItemStockDO();
        itemStockDO.setItemId(itemModel.getId());
        itemStockDO.setStock(itemModel.getStock());

        return itemStockDO;
    }

    @Override
    public List<ItemModel> listItem() {
        return null;
    }

    @Override
    public ItemModel getItemById(Integer id) {
        ItemDO itemDO = itemDOMapper.selectByPrimaryKey(id);
        if (itemDO == null) {
            return null;
        }
        //操作获得库存数量
        ItemStockDO itemStockDO = itemStockDOMapper.selectByItemId(itemDO.getId());

        //将dataobject-> Model
        ItemModel itemModel = convertModelFromDataObject(itemDO, itemStockDO);
        return itemModel;
    }

    private ItemModel convertModelFromDataObject(ItemDO itemDO, ItemStockDO itemStockDO) {
        ItemModel itemModel = new ItemModel();
        BeanUtils.copyProperties(itemDO, itemModel);
        itemModel.setStock(itemStockDO.getStock());
        return itemModel;
    }
}
```

8.ItemController

```java
@Controller("/item")
@RequestMapping("/item")
//跨域请求中，不能做到session共享
@CrossOrigin(origins = {"*"}, allowCredentials = "true")
public class ItemController extends BaseController {

    @Autowired
    private ItemService itemService;

    //创建商品的controller
    @RequestMapping(value = "/create", method = {RequestMethod.POST}, consumes = {CONTENT_TYPE_FORMED})
    @ResponseBody
    public CommonReturnType createItem(@RequestParam(name = "title") String title,
                                       @RequestParam(name = "description") String description,
                                       @RequestParam(name = "price") BigDecimal price,
                                       @RequestParam(name = "stock") Integer stock,
                                       @RequestParam(name = "imgUrl") String imgUrl) throws BusinessException {
        //封装service请求用来创建商品
        ItemModel itemModel = new ItemModel();
        itemModel.setTitle(title);
        itemModel.setDescription(description);
        itemModel.setPrice(price);
        itemModel.setStock(stock);
        itemModel.setImgUrl(imgUrl);

        ItemModel itemModelForReturn = itemService.createItem(itemModel);
        ItemVO itemVO = convertVOFromModel(itemModelForReturn);
        return CommonReturnType.create(itemVO);

    }

    private ItemVO convertVOFromModel(ItemModel itemModel) {
        if (itemModel == null) {
            return null;
        }
        ItemVO itemVO = new ItemVO();
        BeanUtils.copyProperties(itemModel, itemVO);
        return itemVO;
    }

}
```

创建createitem.html

```html
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>创建商品</title>
    <!--    引入css-->
    <link href="static/assets/global/plugins/bootstrap/css/bootstrap.min.css" rel="stylesheet" type="text/css"/>
    <link href="/static/assets/global/css/components.css" rel="stylesheet" type="text/css"/>
    <link href="static/assets/admin/pages/css/login.css" rel="stylesheet" type="text/css"/>
    <!--    引入js-->
    <script src="static/assets/global/plugins/jquery-1.11.0.min.js" type="text/javascript"></script>
</head>

<body class="login">
<!--创建商品-->
<div class="content">
    <h3 class="form-title">创建商品</h3>

    <div class="form-group">
        <label class="control-label">商品名</label>
        <div>
            <input class="form-control" type="text" placeholder="商品名" name="title" id="title">
        </div>
    </div>

    <div class="form-group">
        <label class="control-label">商品价格</label>
        <div>
            <input class="form-control" type="text" placeholder="商品价格" name="price" id="price">
        </div>
    </div>

    <div class="form-group">
        <label class="control-label">商品库存</label>
        <div>
            <input class="form-control" type="text" placeholder="商品库存" name="stock" id="stock">
        </div>
    </div>

    <div class="form-group">
        <label class="control-label">商品描述</label>
        <div>
            <input class="form-control" type="text" placeholder="商品描述" name="description" id="description">
        </div>
    </div>

<!--    <div class="form-group">-->
<!--        <label class="control-label">商品销量</label>-->
<!--        <div>-->
<!--            <input class="form-control" type="text" placeholder="商品销量" name="sales" id="sales">-->
<!--        </div>-->
<!--    </div>-->

    <div class="form-group">
        <label class="control-label">商品描述图片的url</label>
        <div>
            <input class="form-control" type="text" placeholder="商品描述图片的url" name="imgUrl" id="imgUrl">
        </div>
    </div>

    <div class="form-actions">
        <button class="btn blue" id="create" type="submit">
            创建商品
        </button>
    </div>
</div>
</body>

<script>
    jQuery(document).ready(function () {
        //绑定register按钮的click事件
        //ajax框架
        $("#create").on("click", function () {

            var title=$("#title").val();
            var price=$("#price").val();
            var stock=$("#stock").val();
            var description=$("#description").val();
            // var sales=$("#sales").val();
            var imgUrl=$("#imgUrl").val();


            if (title==null || title=="") {
                alert("商品名不能为空");
                return false;
            }
            if (price==null || price=="") {
                alert("商品价格不能为空");
                return false;
            }
            if (stock==null || stock=="") {
                alert("商品库存不能为空");
                return false;
            }
            if (description==null || description=="") {
                alert("商品描述不能为空");
                return false;
            }
            // if (sales==null || sales=="") {
            //     alert("商品销量不能为空");
            //     return false;
            // }
            if (imgUrl==null || imgUrl=="") {
                alert("商品描述图片的url不能为空");
                return false;
            }


            $.ajax({
                type: "POST",
                //contextType->consumes,对应的一个后端需要消费一个contextType名字
                contextType: "application/x-www-form-urlencoded",
                url: "http://localhost:8080/item/create",
                //传递参数
                data: {
                    "title":title,
                    "price":price,
                    "stock":stock,
                    "description":description,
                    // "sales":sales,
                    "imgUrl":imgUrl
                },
                //允许跨域请求
                xhrFields:{withCredentials:true},

                /**
                 * 只要被服务端正确处理，会进入success
                 * 如果比如由于网络原因，会进入error
                 * */
                success: function (data) {
                    if (data.status == "success") {
                        alert("创建商品成功");
                    } else {
                        alert("创建商品失败，原因为" + data.data.errMsg);
                    }
                },
                error: function (data) {
                    alert("创建商品失败，原因为" + data.responseText);
                },
            });
            return false;
        });
    });
</script>

</html>
```



运行报错

```shell
SQL: insert into item_stock      ( stock,                       item_id )       values ( ?,                       ? )
### Cause: java.sql.SQLException: Field 'id' doesn't have a default value
; Field 'id' doesn't have a default value; nested exception is java.sql.SQLException: Field 'id' doesn't have a default value]
```

将主键自增选项勾选上

🎂**使用BeanUtils.copyProperties**

复制对象属性的时候，需要将对象的get，set方法写出来，否则无法复制

```java
public static void copyProperties(Object source, Object target) throws BeansException {
        copyProperties(source, target, (Class)null, (String[])null);
}
```



9.商品详情页浏览

```java
@RequestMapping(value = "/get", method = {RequestMethod.GET})
@ResponseBody
public CommonReturnType getItem(@RequestParam(name = "id") Integer id) {
    ItemModel itemModel = itemService.getItemById(id);

    ItemVO itemVO = convertVOFromModel(itemModel);

    return CommonReturnType.create(itemVO);
}
```

运行报错





###	4.2 商品模型管理——商品列表

*假设我们的需求是按照销量从高到低显示所有商品*

1.创建sql语句

在ItemDOMapper.xml中新建方法

```xml
<select id="listItem"  resultMap="BaseResultMap">

  select
  <include refid="Base_Column_List" />
  /*通过销量倒序排序*/
  from item ORDER BY sales DESC;
</select>
```

2.在ItemDOMapper中创建方法

```java
List<ItemDO> listItem();
```

3.在ItemServiceImpl中实现方法

```java
@Override
public List<ItemModel> listItem() {
    List<ItemDO> itemDOList = itemDOMapper.listItem();

    //使用Java8的stream API
    List<ItemModel> itemModelList = itemDOList.stream().map(itemDO -> {
        ItemStockDO itemStockDO = itemStockDOMapper.selectByItemId(itemDO.getId());
        ItemModel itemModel = this.convertModelFromDataObject(itemDO, itemStockDO);
        return itemModel;
    }).collect(Collectors.toList());

    return itemModelList;
}
```

4.controller层

```java
//商品列表页面浏览
@RequestMapping(value = "/list", method = {RequestMethod.GET})
@ResponseBody
public CommonReturnType listItem() {
    List<ItemModel> itemModelList = itemService.listItem();
    List<ItemVO> itemVOList = itemModelList.stream().map(itemModel -> {
        ItemVO itemVO = this.convertVOFromModel(itemModel);
        return itemVO;
    }).collect(Collectors.toList());

    return CommonReturnType.create(itemVOList);
}
```

###	4.3 商品模型管理——商品列表页面

后端得到的json数据，前端负责页面的开发

listitem.html

```html
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>商品列表浏览</title>
    <!--    引入css-->
    <link href="static/assets/global/plugins/bootstrap/css/bootstrap.min.css" rel="stylesheet" type="text/css"/>
    <link href="/static/assets/global/css/components.css" rel="stylesheet" type="text/css"/>
    <link href="static/assets/admin/pages/css/login.css" rel="stylesheet" type="text/css"/>
    <!--    引入js-->
    <script src="static/assets/global/plugins/jquery-1.11.0.min.js" type="text/javascript"></script>
</head>

<body>
<!--创建商品-->
<div class="content">
    <h3 class="form-title">商品列表浏览</h3>
    <div class="table-responsive">
        <table class="table">
            <thead>
            <tr>
                <th>商品名</th>
                <th>商品图片</th>
                <th>商品描述</th>
                <th>商品价格</th>
                <th>商品库存</th>
                <th>商品销量</th>
            </tr>
            </thead>
            <tbody id="container">

            </tbody>
        </table>

    </div>
</div>
</body>

<script>

    var g_itemList = [];
    jQuery(document).ready(function () {
        /**
         * 通过ajax请求获取后端的json数据，并且将对应dom的信息进行填充
         * */
        //定义全局商品数组信息

        $.ajax({
            type: "GET",
            url: "http://localhost:8080/item/list",

            //允许跨域请求
            xhrFields: {withCredentials: true},

            /**
             * 只要被服务端正确处理，会进入success
             * 如果比如由于网络原因，会进入error
             * */
            success: function (data) {
                if (data.status == "success") {
                    g_itemList = data.data;
                    reloadDom();

                    // alert("获取商品信息成功");

                } else {
                    alert("获取商品信息失败，原因为" + data.data.errMsg);
                }
            },
            error: function (data) {
                alert("获取商品信息失败，原因为" + data.responseText);
            },
        });
    });

    function reloadDom() {
        for (var i = 0; i < g_itemList.length; i++) {
            var itemVO = g_itemList[i];
            var dom =
                "<tr data-id='" + itemVO.id + "' id='itemDetail" + itemVO.id + "'>\
			<td>" + itemVO.title + "</td>\
			<td><img style='width:100px;heigth:auto;' src='" + itemVO.imgUrl + "'/></td>\
			<td>" + itemVO.description + "</td>\
			<td>" + itemVO.price + "</td>\
			<td>" + itemVO.stock + "</td>\
			<td>" + itemVO.sales + "</td>\
			</tr>";

            $("#container").append($(dom));

            //点击一行任意的位置 跳转到商品详情页
            $("#itemDetail" + itemVO.id).on("click", function (e) {
                window.location.href = "getitem.html?id=" + $(this).data("id");
            });
        }
    }


</script>


</html>
```

###	4.4 商品模型管理——商品详情页面

getitem.html

```html
<html>
<head>
    <meta charset="UTF-8">
    <link href="static/assets/global/plugins/bootstrap/css/bootstrap.min.css" rel="stylesheet" type="text/css"/>
    <link href="static/assets/global/css/components.css" rel="stylesheet" type="text/css"/>
    <link href="static/assets/admin/pages/css/login.css" rel="stylesheet" type="text/css"/>
    <script src="static/assets/global/plugins/jquery-1.11.0.min.js" type="text/javascript"></script>
    <title>商品详情</title>
</head>
<body class="login">
<div class="content">
    <h3 class="form-title">商品详情</h3>
    <div id="promoStartDateContainer" class="form-group">
        <label style="color:blue" id="promoStatus" class="control-label"></label>
        <div>
            <label style="color:red" class="control-label" id="promoStartDate" />
        </div>
    </div>
    <div class="form-group">
        <div>
            <label class="control-label" id="title" />
        </div>
    </div>
    <div class="form-group">
        <div>
            <img style="width:200px;height:auto;" id="imgUrl">
        </div>
    </div>
    <div class="form-group">
        <label class="control-label">商品描述</label>
        <div>
            <label class="control-label" id="description" />
        </div>
    </div>
    <div id="normalPriceContainer" class="form-group">
        <label class="control-label">商品价格</label>
        <div>
            <label class="control-label" id="price" />
        </div>
    </div>
    <div id="promoPriceContainer" class="form-group">
        <label style="color:red" class="control-label">秒杀价格</label>
        <div>
            <label style="color:red" class="control-label" id="promoPrice" />
        </div>
    </div>
    <div class="form-group">
        <label class="control-label">商品库存</label>
        <div>
            <label class="control-label" id="stock" />
        </div>
    </div>
    <div class="form-group">
        <label class="control-label">商品销量</label>
        <div>
            <label class="control-label" id="sales" />
        </div>
    </div>
    <div class="form-actions">
        <button class="btn blue" id="createOrder" type="submit">
            立即购买
        </button>
    </div>
</div>
</body>

<script>
    var g_itemVO = {};
    $(document).ready(function() {
        // 获取商品详情
        $.ajax({
            type: "GET",
            url: "http://localhost:8080/item/get",
            data: {
                "id": getParam("id"),
            },
            xhrFields:{
                withCredentials:true
            },
            success: function(data) {
                if (data.status == "success") {
                    g_itemVO = data.data;
                    reloadDom();
                    setInterval(reloadDom, 1000);
                } else {
                    alert("获取信息失败，原因为" + data.data.errMsg);
                }
            },
            error: function(data) {
                alert("获取信息失败，原因为" + data.responseText);
            }
        });
        $("#createOrder").on("click", function() {
            $.ajax({
                type: "POST",
                url: "http://localhost:8080/order/createorder",
                contentType: "application/x-www-form-urlencoded",
                data: {
                    "itemId": g_itemVO.id,
                    "promoId": g_itemVO.promoId,
                    "amount": 1,//暂时写死为一件
                },
                xhrFields:{
                    withCredentials:true
                },
                success: function(data) {
                    if (data.status == "success") {
                        alert("下单成功");
                        window.location.reload();
                    } else {
                        alert("下单失败，原因为" + data.data.errMsg);
                        if (data.data.errCode == 20003) {
                            window.location.href="login.html";
                        }
                    }
                },
                error: function(data) {
                    alert("下单失败，原因为" + data.responseText);
                }
            });
        });
    });
    function reloadDom() {
        $("#title").text(g_itemVO.title);
        $("#imgUrl").attr("src", g_itemVO.imgUrl);
        $("#description").text(g_itemVO.description);
        $("#price").text(g_itemVO.price);
        $("#stock").text(g_itemVO.stock);
        $("#sales").text(g_itemVO.sales);
        if (g_itemVO.promoStatus == 1) {
            // 秒杀活动还未开始
            console.log(g_itemVO.startDate);
            var startTime = g_itemVO.startDate.replace(new RegExp("-", "gm"), "/");
            startTime = (new Date(startTime)).getTime();
            var nowTime = Date.parse(new Date());
            var delta = (startTime - nowTime) / 1000;
            if (delta <= 0) {
                // 活动开始了
                g_itemVO.promoStatus = 2;
                reloadDom();
            }
            $("#promoStartDate").text("秒杀活动将于："+g_itemVO.startDate+" 开始售卖 倒计时："+delta+"  秒");
            $("#promoPrice").text(g_itemVO.promoPrice);
            $("#createOrder").attr("disabled", true);
        } else if (g_itemVO.promoStatus == 2) {
            // 秒杀活动进行中
            $("#promoStartDate").text("秒杀正在进行中");
            $("#promoPrice").text(g_itemVO.promoPrice);
            $("#createOrder").attr("disabled", false);
            $("#normalPriceContainer").hide();
        }
    }
    function getParam(paramName) {
        paramValue = "", isFound = !1;
        if (this.location.search.indexOf("?") == 0 && this.location.search.indexOf("=") > 1) {
            arrSource = unescape(this.location.search).substring(1, this.location.search.length).split("&"), i = 0;
            while (i < arrSource.length && !isFound)
                arrSource[i].indexOf("=") > 0 && arrSource[i].split("=")[0].toLowerCase() == paramName.toLowerCase() && (paramValue = arrSource[i].split("=")[1], isFound = !0), i++
        }
        return paramValue == "" && (paramValue = null), paramValue
    }
</script>

</html>
```

##	第5章 交易模块开发

###	5.1 交易模型管理——交易模型创建

1.先设计用户下单的交易模型

```java
//用户下单的交易模型
public class OrderModel {
    //交易单号，例如2019052100001212，使用string类型
    //交易订单号，是有对应的生成规则的，设置为主键，但是不设计为自增
    private String id;

    //购买的用户id
    private Integer userId;

    //购买的商品id
    private Integer itemId;

    //购买时商品的单价
    private BigDecimal itemPrice;

    //购买数量
    private Integer amount;

    //购买金额
    private BigDecimal orderPrice;
    
    ...
}
```

2.设计数据库

```sql
CREATE TABLE `order_info`  (
  `id` varchar(32) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
  `user_id` int(11) NOT NULL DEFAULT 0,
  `item_id` int(11) NOT NULL DEFAULT 0,
  `item_price` decimal(10, 2) NOT NULL DEFAULT 0.00,
  `amount` int(11) NOT NULL DEFAULT 0,
  `order_price` decimal(40, 2) NOT NULL DEFAULT 0.00,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_bin ROW_FORMAT = Compact;
```

3.修改配置

```xml
<table tableName="order_info" domainObjectName="OrderDO"
       enableCountByExample="false"
       enableUpdateByExample="false"
       enableDeleteByExample="false"
       enableSelectByExample="false"
       selectByExampleQueryId="false" ></table>
```

4.生成文件

在终端运行``` mvn mybatis-generator:generate```命令

###	5.2 交易模型管理——交易下单

1.OrderService

```java
public interface OrderService {

    OrderModel createOrder(Integer userId, Integer itemId, Integer amount) throws BusinessException;
}
```

2.OrderServiceImpl

```java
@Override
@Transactional
public OrderModel createOrder(Integer userId, Integer itemId, Integer amount) throws BusinessException {
    //1.校验下单状态，下单的商品是否存在，用户是否合法，购买数量是否正确
    ItemModel itemModel = itemService.getItemById(itemId);
    if (itemModel == null) {
        throw new BusinessException(EmBusinessError.PARAMETER_VALIDATION_ERROR, "商品信息不存在");
    }

    UserModel userModel = userService.getUserById(userId);
    if (userModel == null) {
        throw new BusinessException(EmBusinessError.PARAMETER_VALIDATION_ERROR, "用户信息不存在");
    }

    if (amount <= 0 || amount > 99) {
        throw new BusinessException(EmBusinessError.PARAMETER_VALIDATION_ERROR, "数量信息不存在");
    }

    //2.落单减库存
    boolean result = itemService.decreaseStock(itemId, amount);
    if (!result) {
        throw new BusinessException(EmBusinessError.STOCK_NOT_ENOUGH);
    }

    //3.订单入库

//        设置订单（orderModel）的各个属性
        OrderModel orderModel = new OrderModel();
        orderModel.setUserId(userId);
        orderModel.setItemId(itemId);
        orderModel.setAmount(amount);
        orderModel.setItemPrice(itemModel.getPrice());
        orderModel.setOrderPrice(itemModel.getPrice().multiply(new BigDecimal(amount)));

//生成交易订单号


        OrderDO orderDO = convertFromOrderModel(orderModel);//将orderModel转换为orderDO
        orderDOMapper.insertSelective(orderDO);//将orderDO存入数据库表中

    //4.返回前端
}


//将OrderModel转换为orderDO
 private OrderDO convertFromOrderModel(OrderModel orderModel) {
        if (orderModel == null) {
            return null;
        }
        OrderDO orderDO = new OrderDO();
        BeanUtils.copyProperties(orderModel, orderDO);
        return orderDO;
    }
```

3.落单减库存

需要对item_stock表进行操作

* ItemService

  ```java
  //库存扣减
  boolean decreaseStock(Integer itemId, Integer amount) throws BusinessException;
  ```

* ItemServiceImpl

  ```java
     @Override
      @Transactional
  //    库存扣减,根据商品id和商品购买数量扣减商品库存
      public boolean decreaseStock(Integer itemId, Integer amount) throws BusinessException {
          //        返回值为影响的条目数，如果sql语句执行失败，返回值为0
          int affectedRow = itemStockDOMapper.decreaseStock(itemId, amount);
          if (affectedRow > 0) {
              //更新库存成功
              return true;
          } else {
              //更新库存失败
              return false;
          }
      }
  
  ```

* ItemStockMapper

  ```java
      int decreaseStock(@Param("itemId") Integer itemId, @Param("amount") Integer amount);
  ```

* ItemStockMapper.xml中进行修改

  ```xml
    <update id="decreaseStock">
      update item_stock
      set stock = stock-#{amount}
      where item_id = #{item_id} and stock>=#{amount}
    </update>
  
  ```

4.生成交易流水号

新建一个数据库`sequence_info`，这张表创建的意义为初始化一些序列，并且这些序列的初始值为0，每当我从这个序列中获取一个数据的时候，就加上对应的一个步长。

```sql
CREATE TABLE `sequence_info`  (
  `name` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
  `current_value` int(11) NOT NULL DEFAULT 0,
  `step` int(11) NOT NULL DEFAULT 0,
  PRIMARY KEY (`name`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_bin ROW_FORMAT = Compact;
```

插入一条语句，用来生成当前流水号

```sql
INSERT INTO `sequence_info` VALUES ('order_info', 0, 1);
```

修改mybatis-generator

```xml
<table tableName="sequence_info" domainObjectName="SequenceDO"
       enableCountByExample="false"
       enableUpdateByExample="false"
       enableDeleteByExample="false"
       enableSelectByExample="false"
       selectByExampleQueryId="false" ></table>
```

在终端运行``` mvn mybatis-generator:generate```命令

修改SequenceDOMapper.xml

```xml
<select id="getSequenceByName" parameterType="java.lang.String" resultMap="BaseResultMap">
  select
  <include refid="Base_Column_List" />
  from sequence_info
  where name = #{name,jdbcType=VARCHAR} for update
</select>
```

添加方法

```java
//SequenceDOMapper.java中添加
SequenceDO getSequenceByName(String name);
```



```java
//生成交易订单号，假设订单号为16位，由时间+自增序列+分库分表位
private String generateOrderNo() {
        StringBuilder stringBuilder = new StringBuilder();
        //1.假设订单号为16位，前8位为年月日，
        LocalDateTime now = LocalDateTime.now();
        String nowDate = now.format(DateTimeFormatter.ISO_DATE).replace("-", "");
        stringBuilder.append(nowDate);

        // 2.中间6位为自增序列，
//        获取当前sequence
        int sequence = 0;
        SequenceDO sequenceDO = sequenceDOMapper.getSequenceByName("order_info");
        sequence = sequenceDO.getCurrentValue();
//        按照step增加CurrentValue
        sequenceDO.setCurrentValue(sequenceDO.getCurrentValue() + sequenceDO.getStep());
//        写入数据库
        sequenceDOMapper.updateByPrimaryKeySelective(sequenceDO);
//        将当前sequence转换为string
        String sequenceStr = String.valueOf(sequence);
        for (int i = 0; i < 6 - sequenceStr.length(); i++) {
            stringBuilder.append(0);//不足6位用0拼接
        }
        stringBuilder.append(sequenceStr);
    //存在的问题：自增序列没有设置最大值，很有可能超过6位，所以在对应的数据库表中应该设置最大值

        // 3.最后两位为分库分表位,00-99,,暂时为00
        stringBuilder.append("00");
    
        return stringBuilder.toString();
    }
```

存在的问题：

①自增序列没有设置最大值，很有可能超过6位，所以在对应的数据库表sequence_info中应该设置最大值。

②因为对应的`service`是标注了`@Transactional`，如果对应的sql操作`generateOrderNo`之后的操作失败了(例如`orderDOMapper.insertSelective(orderDO);`)会进行整个事务的回滚，因为将`generateOrderNo()`中的sql也包含在对应的`@Transactional`标注内，因此`generateOrderNo()`中的sql也会被回滚。此时下一个事务拿到的还是这个序列值，但是针对序列的定义，就算是事务失败回滚了，这个序列也不应该再被重复的使用，这是为了保证全局唯一性的策略（就算当前交易失败了，这个交易失败的订单号也不能够被重新使用）。

解决方式：在`generateOrderNo`加上标注`@Transactional(propagation = Propagation.REQUIRES_NEW)`，这样就能保证`generateOrderNo`开启了一个新的事务，只要这段代码执行完毕就会提交，对应的序列都被使用掉了，而不会由于`createOrder`的`@Transactional`进行回滚。

```java
 @Transactional//表示事务操作
 public OrderModel createOrder(Integer userId, Integer itemId, Integer amount){
     //...
         //生成交易订单号
        orderModel.setId(generateOrderNo());

        OrderDO orderDO = convertFromOrderModel(orderModel);//将orderModel转换为orderDO
        orderDOMapper.insertSelective(orderDO);//将orderDO存入数据库表中
    // ...
 }
```

```java
Propagation propagation() default Propagation.REQUIRED;
//...
public enum Propagation {
    REQUIRED(0),//必须要开启一个事务并且在这个事务当中，如果一段代码已经在一个事务中了就不必重新开启新的事务
    SUPPORTS(1),
    MANDATORY(2),
    REQUIRES_NEW(3),//无论我的代码是否在一个事务中，都会重新开启一个事务
    NOT_SUPPORTED(4),
    NEVER(5),
    NESTED(6);
//...
}
```

```java
@Transactional(propagation = Propagation.REQUIRES_NEW)
    private String generateOrderNo() {
        //...
    }
```

商品详情页面：

getitem.html

```html
<html>
<head>
    <meta charset="UTF-8">
    <link href="static/assets/global/plugins/bootstrap/css/bootstrap.min.css" rel="stylesheet" type="text/css"/>
    <link href="static/assets/global/css/components.css" rel="stylesheet" type="text/css"/>
    <link href="static/assets/admin/pages/css/login.css" rel="stylesheet" type="text/css"/>
    <script src="static/assets/global/plugins/jquery-1.11.0.min.js" type="text/javascript"></script>
    <title>商品详情</title>
</head>
<body class="login">
<div class="content">
    <h3 class="form-title">商品详情</h3>
    <div id="promoStartDateContainer" class="form-group">
        <label style="color:blue" id="promoStatus" class="control-label"></label>
        <div>
            <label style="color:red" class="control-label" id="promoStartDate" />
        </div>
    </div>
    <div class="form-group">
        <div>
            <label class="control-label" id="title" />
        </div>
    </div>
    <div class="form-group">
        <div>
            <img style="width:200px;height:auto;" id="imgUrl">
        </div>
    </div>
    <div class="form-group">
        <label class="control-label">商品描述</label>
        <div>
            <label class="control-label" id="description" />
        </div>
    </div>
    <div id="normalPriceContainer" class="form-group">
        <label class="control-label">商品价格</label>
        <div>
            <label class="control-label" id="price" />
        </div>
    </div>
    <div id="promoPriceContainer" class="form-group">
        <label style="color:red" class="control-label">秒杀价格</label>
        <div>
            <label style="color:red" class="control-label" id="promoPrice" />
        </div>
    </div>
    <div class="form-group">
        <label class="control-label">商品库存</label>
        <div>
            <label class="control-label" id="stock" />
        </div>
    </div>
    <div class="form-group">
        <label class="control-label">商品销量</label>
        <div>
            <label class="control-label" id="sales" />
        </div>
    </div>
    <div class="form-actions">
        <button class="btn blue" id="createOrder" type="submit">
            立即购买
        </button>
    </div>
</div>
</body>

<script>
    var g_itemVO = {};
    $(document).ready(function() {
        // 获取商品详情
        $.ajax({
            type: "GET",
            url: "http://localhost:8080/item/get",
            data: {
                "id": getParam("id"),
            },
            xhrFields:{
                withCredentials:true
            },
            success: function(data) {
                if (data.status == "success") {
                    g_itemVO = data.data;
                    reloadDom();
                    setInterval(reloadDom, 1000);
                } else {
                    alert("获取信息失败，原因为" + data.data.errMsg);
                }
            },
            error: function(data) {
                alert("获取信息失败，原因为" + data.responseText);
            }
        });

        $("#createOrder").on("click", function() {
            $.ajax({
                type: "POST",
                url: "http://localhost:8080/order/createorder",
                contentType: "application/x-www-form-urlencoded",
                data: {
                    "itemId": g_itemVO.id,
                    "promoId": g_itemVO.promoId,
                    "amount": 1,//暂时写死为一件
                },
                xhrFields:{
                    withCredentials:true
                },
                success: function(data) {
                    if (data.status == "success") {
                        alert("下单成功");
                        window.location.reload();<!--刷新页面-->
                    } else {
                        alert("下单失败，原因为" + data.data.errMsg);
                        //如果下单失败的原因是'200003',说明用户还未登录，跳转到用户登录页面
                        if (data.data.errCode == 200003) {
                            window.location.href="login.html";
                        }
                    }
                },
                error: function(data) {
                    alert("下单失败，原因为" + data.responseText);
                }
            });
        });
    });

    function reloadDom() {
        $("#title").text(g_itemVO.title);
        $("#imgUrl").attr("src", g_itemVO.imgUrl);
        $("#description").text(g_itemVO.description);
        $("#price").text(g_itemVO.price);
        $("#stock").text(g_itemVO.stock);
        $("#sales").text(g_itemVO.sales);
        if (g_itemVO.promoStatus == 1) {
            // 秒杀活动还未开始
            console.log(g_itemVO.startDate);
            var startTime = g_itemVO.startDate.replace(new RegExp("-", "gm"), "/");
            startTime = (new Date(startTime)).getTime();
            var nowTime = Date.parse(new Date());
            var delta = (startTime - nowTime) / 1000;
            if (delta <= 0) {
                // 活动开始了
                g_itemVO.promoStatus = 2;
                reloadDom();
            }
            $("#promoStartDate").text("秒杀活动将于："+g_itemVO.startDate+" 开始售卖 倒计时："+delta+"  秒");
            $("#promoPrice").text(g_itemVO.promoPrice);
            $("#createOrder").attr("disabled", true);
        } else if (g_itemVO.promoStatus == 2) {
            // 秒杀活动进行中
            $("#promoStartDate").text("秒杀正在进行中");
            $("#promoPrice").text(g_itemVO.promoPrice);
            $("#createOrder").attr("disabled", false);
            $("#normalPriceContainer").hide();
        }
    }
    function getParam(paramName) {
        paramValue = "", isFound = !1;
        if (this.location.search.indexOf("?") == 0 && this.location.search.indexOf("=") > 1) {
            arrSource = unescape(this.location.search).substring(1, this.location.search.length).split("&"), i = 0;
            while (i < arrSource.length && !isFound)
                arrSource[i].indexOf("=") > 0 && arrSource[i].split("=")[0].toLowerCase() == paramName.toLowerCase() && (paramValue = arrSource[i].split("=")[1], isFound = !0), i++
        }
        return paramValue == "" && (paramValue = null), paramValue
    }
</script>

</html>
```

5.销量增加

itemDOMapper.xml

```xml
<update id="increaseSales">
  update item
  set sales = sales+ #{amount}
  where id = #{id,jdbcType=INTEGER}
</update>
```

itemDOMapper

```java
int increaseSales(@Param("id") Integer id, @Param("amount") Integer amount);
```

ItemServiceImpl

```java
@Override
@Transactional
public void increaseSales(Integer itemId, Integer amount) throws BusinessException {
    itemDOMapper.increaseSales(itemId,amount);
}
```

6.最终的OrderServiceImpl

```java
@Override
@Transactional
public OrderModel createOrder(Integer userId, Integer itemId, Integer amount) throws BusinessException {
    //1.校验下单状态，下单的商品是否存在，用户是否合法，购买数量是否正确
    ItemModel itemModel = itemService.getItemById(itemId);
    if (itemModel == null) {
        throw new BusinessException(EmBusinessError.PARAMETER_VALIDATION_ERROR, "商品信息不存在");
    }

    UserModel userModel = userService.getUserById(userId);
    if (userModel == null) {
        throw new BusinessException(EmBusinessError.PARAMETER_VALIDATION_ERROR, "用户信息不存在");
    }

    if (amount <= 0 || amount > 99) {
        throw new BusinessException(EmBusinessError.PARAMETER_VALIDATION_ERROR, "数量信息不存在");
    }

    //2.落单减库存
    boolean result = itemService.decreaseStock(itemId, amount);
    if (!result) {
        throw new BusinessException(EmBusinessError.STOCK_NOT_ENOUGH);
    }

    //3.订单入库
    OrderModel orderModel = new OrderModel();
    orderModel.setUserId(userId);
    orderModel.setItemId(itemId);
    orderModel.setAmount(amount);
    orderModel.setItemPrice(itemModel.getPrice());
    orderModel.setOrderPrice(itemModel.getPrice().multiply(BigDecimal.valueOf(amount)));

    //生成交易流水号
    orderModel.setId(generateOrderNo());
    OrderDO orderDO = this.convertFromOrderModel(orderModel);
    orderDOMapper.insertSelective(orderDO);
    //加上商品的销量
    itemService.increaseSales(itemId, amount);

    //4.返回前端
    return orderModel;
}
```

7.OrderController层

```java
public class OrderController extends BaseController {
    @Autowired
    private OrderService orderService;
    @Autowired
    private HttpServletRequest httpServletRequest;


    //封装下单请求
    @RequestMapping(value = "/createorder", method = {RequestMethod.POST}, consumes = {CONTENT_TYPE_FORMED})
    @ResponseBody
    public CommonReturnType createOrder(@RequestParam(name = "itemId") Integer itemId,
                                        @RequestParam(name = "amount") Integer amount) throws BusinessException {

//        在UserController将登录凭证加入到用户登录成功的session内，在Session中设置IS_LOGIN，LOGIN_USER
//        因此只需要从用户的Session中获取道对应的用户信息即可,

        //根据IS_LOGIN判断用户是否登录
        Boolean isLogin = (Boolean) httpServletRequest.getSession().getAttribute("IS_LOGIN");
        if (isLogin == null || !isLogin) {
            throw new BusinessException(EmBusinessError.USER_NOT_LOGIN);
        }
        //获取用户的登录信息userModel  LOGIN_USER
        UserModel userModel = (UserModel) httpServletRequest.getSession().getAttribute("LOGIN_USER");

        if (userModel == null) {
            throw new BusinessException(EmBusinessError.PARAMETER_VALIDATION_ERROR, "userModel等于null");
        }


//创建订单,只有用户登录了才能够进行下单，用户的登录信息是在当前Session中获取的
        OrderModel orderModel = orderService.createOrder(userModel.getId(), itemId, amount);


        return CommonReturnType.create(null);
    }
//
}
```

##	第6章 秒杀模块开发

###	6.1 秒杀模型管理——活动模型创建

1.使用joda-time

```xml
<dependency>
  <groupId>joda-time</groupId>
  <artifactId>joda-time</artifactId>
  <version>2.9.1</version>
</dependency>
```

2.创建活动模型

```java
public class PromoModel {
    private Integer id;

    //秒杀活动状态：1表示还未开始，2表示正在进行，3表示已结束
    private Integer status;

    
    //秒杀活动名称
    private String promoName;

    //秒杀活动的开始时间
    private DateTime startDate;

    //秒杀活动的结束时间
    private DateTime endDate;

    //秒杀活动的适用商品
    private Integer itemId;

    //秒杀活动的商品价格
    private BigDecimal promoItemPrice;
```

3.设计数据库

```sql
CREATE TABLE `promo`  (
  `id` int(100) NOT NULL AUTO_INCREMENT,
  `promo_name` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL DEFAULT '',
  `start_date` datetime NOT NULL DEFAULT '0000-00-00 00:00:00',
  `end_date` datetime NOT NULL DEFAULT '0000-00-00 00:00:00',
  `item_id` int(11) NOT NULL DEFAULT 0,
  `promo_item_price` decimal(10, 2) NOT NULL DEFAULT 0.00,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8 COLLATE = utf8_bin ROW_FORMAT = Compact;
```

创建表中报错：`Invalid default value for start_date`，这是MySQL中默认DATE 不能全部为0的原因

[解决方法](https://blog.csdn.net/u011499484/article/details/80415417)

4.mybatis逆向工程

```xml
<table tableName="promo" domainObjectName="PromoDO"
       enableCountByExample="false"
       enableUpdateByExample="false"
       enableDeleteByExample="false"
       enableSelectByExample="false"
       selectByExampleQueryId="false" ></table>
```

###	6.2 秒杀模型管理——活动模型与商品模型结合

1.service

秒杀服务根据商品id，查询得到当前的活动以及其价格

PromoService

```java
PromoModel getPromoByItemId(Integer itemId);
```

PromoServiceImpl

```java
@Service
public class PromoServiceImpl implements PromoService {

    @Autowired
    private PromoDOMapper promoDOMapper;



    //根据itemId获取即将开始的或者正在进行的活动
    @Override
    public PromoModel getPromoByItemId(Integer itemId) {

        //获取商品对应的秒杀信息
        PromoDO promoDO = promoDOMapper.selectByItemId(itemId);

        //dataobject->model
        PromoModel promoModel = convertFromDataObject(promoDO);
        if (promoModel == null) {
            return null;
        }

        //判断当前时间是否秒杀活动即将开始或正在进行
        DateTime now = new DateTime();
        if (promoModel.getStartDate().isAfterNow()) {
            promoModel.setStatus(1);
        } else if (promoModel.getEndDate().isBeforeNow()) {
            promoModel.setStatus(3);
        } else {
            promoModel.setStatus(2);
        }

        return promoModel;
    }

    private PromoModel convertFromDataObject(PromoDO promoDO) {
        if (promoDO == null) {
            return null;
        }
        PromoModel promoModel = new PromoModel();
        BeanUtils.copyProperties(promoDO, promoModel);
        promoModel.setStartDate(new DateTime(promoDO.getStartDate()));
        promoModel.setEndDate(new DateTime(promoDO.getEndDate()));

        return promoModel;
    }
}
```

2.使用聚合模型，在ItemModel上添加属性

```java
//使用聚合模型，如果promoModel不为空，则表示其拥有还未结束的秒杀活动
private PromoModel promoModel;
```

更改ItemServiceImpl

```java
@Override
public ItemModel getItemById(Integer id) {
    ItemDO itemDO = itemDOMapper.selectByPrimaryKey(id);
    if (itemDO == null) {
        return null;
    }
    //操作获得库存数量
    ItemStockDO itemStockDO = itemStockDOMapper.selectByItemId(itemDO.getId());

    //将dataobject-> Model
    ItemModel itemModel = convertModelFromDataObject(itemDO, itemStockDO);

    //获取活动商品信息
    PromoModel promoModel = promoService.getPromoByItemId(itemModel.getId());
    if (promoModel != null && promoModel.getStatus().intValue() != 3) {
        itemModel.setPromoModel(promoModel);
    }
    return itemModel;
}
```

同时修改ItemVO

```java
//商品是否在秒杀活动中，以及对应的状态：0表示没有秒杀活动，1表示秒杀活动等待开始，2表示进行中
private Integer promoStatus;

//秒杀活动价格
private BigDecimal promoPrice;

//秒杀活动id
private Integer promoId;

//秒杀活动开始时间
private String startDate;
```

修改ItemController

```java
private ItemVO convertVOFromModel(ItemModel itemModel) {
    if (itemModel == null) {
        return null;
    }
    ItemVO itemVO = new ItemVO();
    BeanUtils.copyProperties(itemModel, itemVO);
    if (itemModel.getPromoModel() != null) {
        itemVO.setPromoStatus(itemModel.getPromoModel().getStatus());
        itemVO.setPromoId(itemModel.getPromoModel().getId());
                   itemVO.setStartDate(itemModel.getPromoModel().getStartDate().
                    toString(DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss")));
        itemVO.setPromoPrice(itemModel.getPromoModel().getPromoItemPrice());
    } else {
        itemVO.setPromoStatus(0);
    }
    return itemVO;
}
```

3.修改前端界面

规定：当商品存在秒杀活动的时候，则规定该商品只有在秒杀开始后才可以进行下单，之前不可以进行下单

getitem.html

```html
<html>
<head>
    <meta charset="UTF-8">
    <link href="static/assets/global/plugins/bootstrap/css/bootstrap.min.css" rel="stylesheet" type="text/css"/>
    <link href="static/assets/global/css/components.css" rel="stylesheet" type="text/css"/>
    <link href="static/assets/admin/pages/css/login.css" rel="stylesheet" type="text/css"/>
    <script src="static/assets/global/plugins/jquery-1.11.0.min.js" type="text/javascript"></script>
    <title>商品详情</title>
</head>
<body class="login">
<div class="content">
    <h3 class="form-title">商品详情</h3>

    <div id="promoStartDateContainer" class="form-group">
        <label style="color:blue" id="promoStatus" class="control-label"></label>
        <div>
            <label style="color:red" class="control-label" id="promoStartDate" />
        </div>
    </div>

    <div class="form-group">
        <div>
            <label class="control-label" id="title" />
        </div>
    </div>

    <div class="form-group">
        <div>
            <img style="width:200px;height:auto;" id="imgUrl">
        </div>
    </div>

    <div class="form-group">
        <label class="control-label">商品描述</label>
        <div>
            <label class="control-label" id="description" />
        </div>
    </div>

    <div id="normalPriceContainer" class="form-group">
        <label class="control-label">商品价格</label>
        <div>
            <label class="control-label" id="price" />
        </div>
    </div>

    <div id="promoPriceContainer" class="form-group">
        <label style="color:red" class="control-label">秒杀价格</label>
        <div>
            <label style="color:red" class="control-label" id="promoPrice" />
        </div>
    </div>

    <div class="form-group">
        <label class="control-label">商品库存</label>
        <div>
            <label class="control-label" id="stock" />
        </div>
    </div>

    <div class="form-group">
        <label class="control-label">商品销量</label>
        <div>
            <label class="control-label" id="sales" />
        </div>
    </div>

    <div class="form-actions">
        <button class="btn blue" id="createOrder" type="submit">
            立即购买
        </button>
    </div>

</div>
</body>

<script>
    var g_itemVO = {};
    $(document).ready(function() {
        // 获取商品详情
        $.ajax({
            type: "GET",
            url: "http://localhost:8080/item/get",
            data: {
                "id": getParam("id"),
            },
            xhrFields:{
                withCredentials:true
            },
            success: function(data) {
                if (data.status == "success") {
                    g_itemVO = data.data;
                    reloadDom();
                    setInterval(reloadDom, 1000);//定时器
                } else {
                    alert("获取信息失败，原因为" + data.data.errMsg);
                }
            },
            error: function(data) {
                alert("获取信息失败，原因为" + data.responseText);
            }
        });

        $("#createOrder").on("click", function() {
            $.ajax({
                type: "POST",
                url: "http://localhost:8080/order/createorder",
                contentType: "application/x-www-form-urlencoded",
                data: {
                    "itemId": g_itemVO.id,
                    "promoId": g_itemVO.promoId,
                    "amount": 1,//暂时写死为一件
                },
                xhrFields:{
                    withCredentials:true
                },
                success: function(data) {
                    if (data.status == "success") {
                        alert("下单成功");
                        window.location.reload();<!--刷新页面-->
                    } else {
                        alert("下单失败，原因为" + data.data.errMsg);
                        //如果下单失败的原因是'200003',说明用户还未登录，跳转到用户登录页面
                        if (data.data.errCode == 200003) {
                            window.location.href="login.html";
                        }
                    }
                },
                error: function(data) {
                    alert("下单失败，原因为" + data.responseText);
                }
            });
        });
    });

    function reloadDom() {
        $("#title").text(g_itemVO.title);
        $("#imgUrl").attr("src", g_itemVO.imgUrl);
        $("#description").text(g_itemVO.description);
        $("#price").text(g_itemVO.price);
        $("#stock").text(g_itemVO.stock);
        $("#sales").text(g_itemVO.sales);
        if (g_itemVO.promoStatus == 1) {
            // 秒杀活动还未开始
            console.log(g_itemVO.startDate);
            var startTime = g_itemVO.startDate.replace(new RegExp("-", "gm"), "/");
            startTime = (new Date(startTime)).getTime();
            var nowTime = Date.parse(new Date());
            var delta = (startTime - nowTime) / 1000;
            if (delta <= 0) {
                // 活动开始了
                g_itemVO.promoStatus = 2;
                reloadDom();
            }
            $("#promoStartDate").text("秒杀活动将于："+g_itemVO.startDate+" 开始售卖 倒计时："+delta+"  秒");
            $("#promoPrice").text(g_itemVO.promoPrice);
            //规定：当商品存在秒杀活动的时候，则规定该商品只有在秒杀开始后才可以进行下单，之前不可以进行下单
            $("#createOrder").attr("disabled", true);
        } else if (g_itemVO.promoStatus == 2) {
            // 秒杀活动进行中
            $("#promoStartDate").text("秒杀正在进行中");
            $("#promoPrice").text(g_itemVO.promoPrice);
            $("#createOrder").attr("disabled", false);
            $("#normalPriceContainer").hide();//在秒杀状态下将普通价格隐藏
        }else if (g_itemVO.promoStatus == 0) {
        //    说明该商品没有秒杀活动
        //    将秒杀信息隐藏
            $("#promoStartDateContainer").hide();
            $("#promoPriceContainer").hide();
        }
    }
    function getParam(paramName) {
        paramValue = "", isFound = !1;
        if (this.location.search.indexOf("?") == 0 && this.location.search.indexOf("=") > 1) {
            arrSource = unescape(this.location.search).substring(1, this.location.search.length).split("&"), i = 0;
            while (i < arrSource.length && !isFound)
                arrSource[i].indexOf("=") > 0 && arrSource[i].split("=")[0].toLowerCase() == paramName.toLowerCase() && (paramValue = arrSource[i].split("=")[1], isFound = !0), i++
        }
        return paramValue == "" && (paramValue = null), paramValue
    }
</script>

</html>
```



4.修改OrderModel

增加秒杀价格字段

```java
//若非空，则表示是以秒杀商品方式下单
private Integer promoId;

//购买时商品的单价,若promoId非空，则表示是以秒杀商品方式下单
private BigDecimal itemPrice;
```

然后在数据库中，DO中，DOMapper中增加此字段

5.改造下单接口

```java
//1.通过url上传过来秒杀活动id，然后下单接口内校验对应id是否属于对应商品且活动已开始
//2.直接在下单接口内判断对应的商品是否存在秒杀活动，若存在进行中的则以秒杀价格下单
//倾向于使用第一种形式，因为对同一个商品可能存在不同的秒杀活动，而且第二种方案普通销售的商品也需要校验秒杀
OrderModel createOrder(Integer userId, Integer itemId, Integer promoId, Integer amount) throws BusinessException;
```

实现

```java
//校验活动信息
        if (promoId != null) {
            //(1)校验对应活动是否存在这个适用商品
            if (promoId.intValue() != itemModel.getPromoModel().getId()) {
                throw new BusinessException(EmBusinessError.PARAMETER_VALIDATION_ERROR, "活动信息不正确");
                //(2)校验活动是否正在进行中
            } else if (itemModel.getPromoModel().getStatus() != 2) {
                throw new BusinessException(EmBusinessError.PARAMETER_VALIDATION_ERROR, "活动信息不正确");
            }
        }

        //2.落单减库存
        boolean result = itemService.decreaseStock(itemId, amount);
        if (!result) {
            throw new BusinessException(EmBusinessError.STOCK_NOT_ENOUGH);
        }

        //3.订单入库
        OrderModel orderModel = new OrderModel();
        orderModel.setUserId(userId);
        orderModel.setItemId(itemId);
        orderModel.setPromoId(promoId);
        orderModel.setAmount(amount);

        if (promoId != null) {
            orderModel.setItemPrice(itemModel.getPromoModel().getPromoItemPrice());
        } else {
            orderModel.setItemPrice(itemModel.getPrice());
        }

        orderModel.setOrderPrice(orderModel.getItemPrice().multiply(BigDecimal.valueOf(amount)));
```

在controller层添加参数

```java
@RequestParam(name = "promoId",required = false) Integer promoId,
```

进行测试

# 课程总结1

- 学习使用SpringBoot+MyBatis完成JavaWeb项目的搭建
- 学习一个电商秒杀系统的基本流程以及代码实现

使用了前后端分离的一个设计方式，在前端使用了html,css,jquery以及Metronic模板来完成用户注册、登录以及商品展示，下单交易，秒杀倒计时的基本前端功能。

在接入层使用了springMvc的controller定义对应的view object和返回了通用的对象，并且在controller层通过了通用的异常处理方式，结合通用的返回对象，返回了对应的前后端分离的json的data模型。

在业务层中，使用了对应的MyBatis接入以及model层（领域模型的概念）完成了对应的用户服务、商品服务、交易服务以及活动服务。

并且在数据层使用了@Transactional标签来完成事务的切面，使用dao来完成数据库的相关操作。

使用MySQL数据库来完成数据源的操作。

<img src="https://gitee.com/shilongshen/xiaoxingimagebad/raw/master/img/20210520111601.png" style="zoom:67%;" />

出错调试：
- 先确认问题点：环境问题、ui展示问题、接口问题、服务问题、配置问题
- 断点调试，日志调试
- 互联网寻找答案

拓展思维：

- 目前的项目是针对单商品、单库存、单活动的，那多商品、多库存、多活动模型怎么实现？

遗留问题：
- 如何支撑亿级别秒杀流量？
- 如何发现容量问题
- 如何使得系统水平拓展
- 查询效率低下
- 活动开始前页面被疯狂刷新
- 库存行锁问题
- 下单操作多，缓慢
- 浪涌流量如何解决



---

---

---

更新说明：之前项目的延申，[聚焦Java性能优化 打造亿级流量秒杀系统](https://coding.imooc.com/class/338.html)

![](https://gitee.com/shilongshen/xiaoxingimagebad/raw/master/img/20210522085653.png)



# 第7章 云端部署，性能压测

从本地调试到云端上线的必经之路

目标：

- 项目云端/私有部署
- jmeter性能压测
- 如何发现系统瓶颈问题 

## 本地部署

**说明**：在本地进行部署，程序是在window上编写的，将其上传到一个本地Linux服务器上

常用命令

```shell
ip:10.249.159.48
文件传输：
scp 本地文件 ssl@10.249.159.48:远程路径
```

1. 本地Linux服务器需要安装jdk1.8，MySQL

```shell
版本
(base) ssl@ssl-H310M-S2:~$ mysql -V
mysql  Ver 14.14 Distrib 5.7.33, for Linux (x86_64) using  EditLine wrapper
(base) ssl@ssl-H310M-S2:~$ java -version
java version "1.8.0_291"
Java(TM) SE Runtime Environment (build 1.8.0_291-b10)
Java HotSpot(TM) 64-Bit Server VM (build 25.291-b10, mixed mode)
```

2.将本地数据库进行备份、上传以及恢复
```shell
#切换到MySQL的bin目录下，使用mysqldump备份本地数据库
mysqldump -u root -p --databases miaosha > D:\Projects\IdeaProjects\ProjectsLearning\miaosha.sql
#将本地备份的miaosha.sql上传到Linux服务器
D:\Projects\IdeaProjects\ProjectsLearning>scp miaosha.sql ssl@10.249.159.48:/home/ssl/IdeaProjects/ProjectLearning
#将Linuxx服务器上的miaosha.sql进行恢复
mysql -u root -p
source miaosha.sql;
```

3.将程序进行打包（jar）

在pom.xml中添加

```xml
<!--添加这一段依赖，否则在运行jar的时候会报错-->
        <dependency>
            <groupId>javax.xml.bind</groupId>
            <artifactId>jaxb-api</artifactId>
            <version>2.3.0</version>
        </dependency>

        <dependency>
            <groupId>javax.activation</groupId>
            <artifactId>activation</artifactId>
            <version>1.1.1</version>
        </dependency>
```

```shel
报错Caused by: java.lang.NoClassDefFoundError: javax/xml/bind/ValidationException
参考https://blog.csdn.net/sihai12345/article/details/80744012
```



```shell
#进入项目根目录
mvn clean package
cd target 
#测试
java -jar miaosha-0.0.1-SNAPSHOT.jar
```

4.将jar上传到Linux服务器

```shell
scp *** ***
#在服务器测试
java -jar ***
#本地运行
http://10.249.159.48:8080/
```

5.设置外挂配置文件

在同名的情况下，外挂配置文件的优先级更高

application.properties

6.启动脚本

新建deploy.sh

```shell
nohup java -jar miaosha-0.0.1-SNAPSHOT.jar --spring.config.addition-location=application.properties
```

nohup  #启动应用程序，即便控制台退出，应用程序也不会退出

```shell
#启动
>./deploy.sh &
>nohup: 忽略输入并把输出追加到'nohup.out'
#原本打印在控制台上的信息打印到nohup.out
#进程终止
kill -9  进程号PID
```

## jmeter性能压测

- 线程组
- Http请求
- 查看结果树
- 聚合报告

下载[地址](https://jmeter.apache.org/download_jmeter.cgi)

  ```markdown
  ## 运行
  ## Running JMeter
  1. Change to the `bin` directory
  2. Run the `jmeter` (Un\*x) or `jmeter.bat` (Windows) file.
  ```

注意：测试时要保证服务器上的程序是跑起来的

1.新建线程组

<img src="https://gitee.com/shilongshen/xiaoxingimagebad/raw/master/img/20210523094206.png" style="zoom:50%;" />

参数：100个线程，在10秒内启动，每个线程循环调用10次

2.在线程组下添加Http请求

<img src="https://gitee.com/shilongshen/xiaoxingimagebad/raw/master/img/20210523101819.png" style="zoom:80%;" />

<img src="https://gitee.com/shilongshen/xiaoxingimagebad/raw/master/img/20210523101919.png" style="zoom:80%;" />

3.在线程组下添加查看结果树



4.在线程组下添加聚合报告

![](https://gitee.com/shilongshen/xiaoxingimagebad/raw/master/img/20210523102653.png)

总体：

![](https://gitee.com/shilongshen/xiaoxingimagebad/raw/master/img/20210523094613.png)

- 当能够承受的并发数越高并且返回的速度越快，tps就越大，性能就越好

```shell
> ps -ef |grep java  #查看进程
> ssl      22772 22771  1 10:13 pts/40   00:00:13 java -jar miaosha-0.0.1-SNAPSHOT.jar --spring.config.addition-location=application.properties

> pstree -p 22772  #查看进程中的线程数
> 
            
> pstree -p 22772 | wc -l  #查看进程下的线程数量
> 32

> top -H  #查看机器性能，如下图所示

```

![](https://gitee.com/shilongshen/xiaoxingimagebad/raw/master/img/20210523200849.png)

当线程组中的线程数以及循环调用次数增加时会发现服务端的性能无法承受，Http请求报错。

### 优化Tomcat配置

查看spring boot配置

- spring-configuration-metadata.json文件下查看各个结点的配置

<img src="https://gitee.com/shilongshen/xiaoxingimagebad/raw/master/img/20210523201618.png" style="zoom: 33%;" />

在服务端自定义的application.properties中编写,**调整服务器支持的进程数**

```properties
server.tomcat.accept-count=1000
server.tomcat.threads.max=800
server.tomcat.threads.min-spare=100
```

- 定制化内嵌tomcat开发

<img src="https://gitee.com/shilongshen/xiaoxingimagebad/raw/master/img/20210523204626.png" style="zoom:33%;" />

新建`config`包，新建WebServerConfiguration.java

```java
/**
 * 当Spring容器中没有TomcatEmbeddedServletContainerFactory这个bean时，会把此bean加载进来
 */
@Component
public class WebServerConfiguration implements WebServerFactoryCustomizer<ConfigurableWebServerFactory> {
    @Override
    public void customize(ConfigurableWebServerFactory factory) {
//使用对应工厂类提供给我们的接口定制化我们的tomcat connector
        ((TomcatServletWebServerFactory) factory).addConnectorCustomizers(
                new TomcatConnectorCustomizer() {
                    @Override
                    public void customize(Connector connector) {
                        Http11NioProtocol protocol = (Http11NioProtocol) connector.getProtocolHandler();
//                        定制化KeepAliveTimeout,设置30S内没有请求则服务端自动断开keepalive
                        protocol.setKeepAliveTimeout(300000);
//                        当客户端发送超过10000个请求则自动断开keep alive链接
                        protocol.setMaxKeepAliveRequests(10000);

                    }
                }

        );

    }
}
```

### 优化方向

响应时间变长，tps上不去

- 线程数不是越多越好，太多的话花费在线程调度上的时间会太长
- 等待队列也不是越长越好

<img src="https://gitee.com/shilongshen/xiaoxingimagebad/raw/master/img/20210523211331.png" style="zoom:33%;" />

- 主键索引为聚簇索引，查询效率最高

<img src="https://gitee.com/shilongshen/xiaoxingimagebad/raw/master/img/20210523211519.png" style="zoom:33%;" />

- 数据插入操作的效率相对其他操作效率较低

<img src="https://gitee.com/shilongshen/xiaoxingimagebad/raw/master/img/20210523211904.png" style="zoom:33%;" />

# 第8章 分布式拓展

目标：

- nginx反向代理负载均衡
- 分布式会话管理
- 使用redis实现分布式会话存储

## nginx反向代理负载均衡

使用`top -H`可以查看

单机容量存在问题：cpu使用率增高，内存占用增加，网络带宽使用增加

<img src="https://gitee.com/shilongshen/xiaoxingimagebad/raw/master/img/20210524204748.png" style="zoom:33%;" />

解决方案：

- MySQL数据库开放远端连接
- 服务端水平对称部署
- 验证访问

改进之前的部署：

<img src="https://gitee.com/shilongshen/xiaoxingimagebad/raw/master/img/20210524205235.png" style="zoom:40%;" />

改进后的部署结构：

需要4台服务器来做水平拓展，一台用于nginx，一台用于MySQL，另外两台用于Java应用服务

<img src="https://gitee.com/shilongshen/xiaoxingimagebad/raw/master/img/20210524205431.png" style="zoom:44%;" />

注意

1.因为此时数据库存放的服务器和jar包存放的服务器已经不是同一台了，所以需要将存放jar的服务器上的`application.properties`进行相应的更改

```properties
spring.datasource.url=jdbc:mysql://“存放数据库服务器的ip地址”:3306/miaosha?serverTimezone=GMT%2B8
```

2.MySQL默认只允许本地程序使用密码进行访问，可以使用以下命令使得所有服务器都可以使用密码进行数据库的访问

```sql
grant all privileges on *.* to root@'%' identified by 'root';
flush privileges;
```

此时部署在两台不同服务器上的Java应用都可以访问部署在另外一台服务器上的数据库，完成了MySQL数据库开放远端连接以及服务器水平拓展。

### nginx

nginx的主要功能：

- 使用nginx作为web服务器
- 使用nginx作为dong动静分离服务器，可以把动态页面和静态页面交由不同的服务器来解析，减少服务器压力。
- 使用nginx作为反向代理服务器，由反向代理服务器去选择目标服务器,对外就一个服务器，暴露的是反向代理服务器地址，隐藏了真实服务器IP地址.

<img src="https://gitee.com/shilongshen/xiaoxingimagebad/raw/master/img/20210524213055.png" style="zoom:50%;" />



H5(访问静态资源)访问resources目录下的时候会在nginx中进行寻找

创建`gethost.js`

```js
//在所有文件内引入该js文件，替换掉发送ajax请求路径
var g_host="localhost:8080";
```

将所有的localhost:8080替换为"+g_host+"

将远端服务器地址配置化，一旦将项目部署到服务器上，可以调整g_host

```html
$.ajax({
                type: "POST",
                //contextType->consumes,对应的一个后端需要消费一个contextType名字
                contextType: "application/x-www-form-urlencoded",
                //url: "http://localhost:8080/item/create",
				url: "http://"+g_host+"/item/create",
                //传递参数
            //...   
});
```

实现动静分离，将静态资源部署nginx上

> OpenResty(又称：ngx_openresty) 是一个基于 NGINX 的可伸缩的 Web 平台

使用[OpenResty](https://www.runoob.com/w3cnote/openresty-intro.html)

默认安装到`/usr/local/openresty`

<img src="https://gitee.com/shilongshen/xiaoxingimagebad/raw/master/img/20210525161626.png" style="zoom:50%;" />

#### 将nginx指定为web服务器

- location 结点path：指定url映射key
- location结点内容：root指定location path 后对应的根路径，index执行默认的访问页
- sbin/nginx -c conf/nginx.conf命令启动nginx服务器
  - 修改配置后直接使用命令sbin/nginx -s reload 进行nginx服务器的重启

使用sbin/nginx -c conf/nginx.conf，启动访问`ip:80`

<img src="https://gitee.com/shilongshen/xiaoxingimagebad/raw/master/img/20210525163320.png" style="zoom:50%;" />



完成前端资源的部署，以使得nginx容器能够作为一个web服务器使用。

- 将前端的代码上传到/usr/local/openresty/nginx/html，使用nginx作为静态资源服务器

- nginx.conf内容解析以及修改

```properties
#user  nobody;
worker_processes  1;  #工作进程

#error_log  logs/error.log;
#error_log  logs/error.log  notice;
#error_log  logs/error.log  info;

#pid        logs/nginx.pid;


events {
    worker_connections  1024;  #可接收的工作链接
}


http {
    include       mime.types;
    default_type  application/octet-stream;

    #log_format  main  '$remote_addr - $remote_user [$time_local] "$request" '
    #                  '$status $body_bytes_sent "$http_referer" '
    #                  '"$http_user_agent" "$http_x_forwarded_for"';

    #access_log  logs/access.log  main;

    sendfile        on;
    #tcp_nopush     on;

    #keepalive_timeout  0;
    keepalive_timeout  65;

    #gzip  on;

    server {
        listen       80;
        server_name  localhost;

        #charset koi8-r;
        #charset koi8-r;

        #access_log  logs/host.access.log  main;

#不需要所有location访问都进入html路径，只需要将/resources/目录下的结构进入resources下的HTML静态资源路径
        location /resources/ {
            #root   html;
            #alias表示当location的规则命中了/resources/，将其替换为/usr/local/openresty/nginx/html/resources/
            alias   /usr/local/openresty/nginx/html/resources/; 
            index  index.html index.htm;
        }

        #error_page  404              /404.html;

        # redirect server error pages to the static page /50x.html
        #
        error_page   500 502 503 504  /50x.html;
        location = /50x.html {
            root   html;
        }

        # proxy the PHP scripts to Apache listening on 127.0.0.1:80
        #
        #location ~ \.php$ {
        #    proxy_pass   http://127.0.0.1;
        #}

        # pass the PHP scripts to FastCGI server listening on 127.0.0.1:9000
        #
        #location ~ \.php$ {
        #    root           html;
        #    fastcgi_pass   127.0.0.1:9000;
        #    fastcgi_index  index.php;
        #    fastcgi_param  SCRIPT_FILENAME  /scripts$fastcgi_script_name;
        #    include        fastcgi_params;
        #}

        # deny access to .htaccess files, if Apache's document root
        # concurs with nginx's one
        #
        #location ~ /\.ht {
             #
        #location ~ /\.ht {
        #    deny  all;
        #}
    }


    # another virtual host using mix of IP-, name-, and port-based configuration
    #
    #server {
    #    listen       8000;
    #    listen       somename:8080;
    #    server_name  somename  alias  another.alias;

    #    location / {
    #        root   html;
    #        index  index.html index.htm;
    #    }
    #}


    # HTTPS server
    #
    #server {
    #    listen       443 ssl;
    #    server_name  localhost;

    #    ssl_certificate      cert.pem;
    #    ssl_certificate_key  cert.key;

    #    ssl_session_cache    shared:SSL:1m;
    #    ssl_session_timeout  5m;

    #    ssl_ciphers  HIGH:!aNULL:!MD5;
    #    ssl_prefer_server_ciphers  on;

    #    location / {
    #        root   html;
    #        index  index.html index.htm;
    #        root   html;
    #        index  index.html index.htm;
    #    }
    #}

}
```

新建resources目录，将文件进行移动

![](https://gitee.com/shilongshen/xiaoxingimagebad/raw/master/img/20210525171727.png)

重启nginx服务器sbin/nginx -s reload，访问http://10.250.191.96/resources/register.html

#### nginx动静分离服务器

- location节点path特定resources：静态资源路径
- location节点其他路径：动态资源用
- 开启tomcat access log验证

nginx.conf内容解析以及修改

`cd /usr/local/openresty`

```properties
#添加,指定需要进行反向代理服务器的IP地址-》存放Havana程序的服务器
#添加,
location / {
#当路径访问到除了resources外的路径时，nginx不处理请求，而是反向代理到backend_server
#backend_server的IP地址由上面的upstream指出，即Java程序服务器的地址
	proxy_pass http://backend_server;
	proxy_set_header Host $http_host:$proxy_port;
	proxy_set_header X-Real-IP $remote_addr;
	proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
}
```

访问<http://10.250.191.96/item/get?id=10>

开启tomcat access log验证

修改配置文件

```properties
server.tomcat.accesslog.enabled=true
server.tomcat.accesslog.directory=/...
server.tomcat.accesslog.pattern=%h %l %u %t "%r" %s %b %D 
```

默认nginx与后端服务器是短连接（使用的是HTTP1.0，默认不支持keepalive），修改为长连接，减少网络建联的消耗。

#### nginx 高性能原因

- epoll多路复用，解决了IO阻塞回调的问题
- master worker 进程模型，可以完成平滑的重启
- 协程机制，将每个用户的请求对应到线程中的协程种，然后再协程中使用epoll多路复用机制来完成同步调用开发。

##### epoll多路复用

- Java bio模型，阻塞进程式

<img src="https://gitee.com/shilongshen/xiaoxingimagebad/raw/master/img/20210601201701.png" style="zoom:50%;" />

- Linux select模型，变更触发轮询查找，有1024数量上限

<img src="https://gitee.com/shilongshen/xiaoxingimagebad/raw/master/img/20210601201758.png" style="zoom:50%;" />

- epoll模型，变更触发回调直接读取，理论上无上限

<img src="https://gitee.com/shilongshen/xiaoxingimagebad/raw/master/img/20210601202044.png" style="zoom:50%;" />



##### master worker 进程模型

<img src="https://gitee.com/shilongshen/xiaoxingimagebad/raw/master/img/20210601202146.png" style="zoom: 50%;" />

master进程是父进程，worker进程是子进程，master可以管理worker进程，worker进程次才是处理客户端连接的进程。 

每一个worker进程全都是单线程的

##### 协程机制

- 依附线程的内存模型，切换开销小
- 遇阻塞及归还执行权，代码同步
- 无需加锁

## 会话管理

- 基于cookie传输sessionId：Java tomcat容器session实现

存在的问题是，用户认证之后，服务端做认证记录，如果认证的记录被保存在内存中的话，这意味着用户下次请求还必须要请求在**这台**服务器上,这样才能拿到授权的资源，这样在分布式的应用上，相应的限制了负载均衡器的能力。这也意味着限制了应用的扩展能力。

- 基于token传输类似sessionId，Java代码session实现

基于token（令牌）的鉴权机制类似于http协议也是无状态的，**它不需要在服务端去保留用户的认证信息或者会话信息。这就意味着基于token认证机制的应用不需要去考虑用户在哪一台服务器登录了**，这就为应用的扩展提供了便利。



###  解决方式：分布式会话

1.基于cookie传输sessionId：Java tomcat容器session实现迁移到redis

  将session存放到redis中，而不是存放在某一台特定的Java程序服务器中。这样就可以解决用户的请求必须在某一台特定的服务器上才能够进行认证。
  具体来说，在用户认证的时候是将user model放在session中进行认证。通过将session放在redis中就可以完成单点登录。

引入依赖pom.xml

```xml
<dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-data-redis</artifactId>
<!--            <version>1.2.6</version>-->
        </dependency>
        <dependency>
            <groupId>org.springframework.session</groupId>
            <artifactId>spring-session-data-redis</artifactId>
<!--            <version>2.0.4</version>-->
        </dependency>
```

[安装redis](https://redis.io/download)

application.properties中添加

```properties
#配置springboot对redis的依赖
spring.redis.host=127.0.0.1
spring.redis.port=6379
spring.redis.database=10
#设置jedis连接池
spring.redis.jedis.pool.max-active=50
spring.redis.jedis.pool.min-idle=20
```

修改UserModel实现序列化，因为存入redis中数据需要进行序列化

```java
public class UserModel implements Serializable{
 //...   
}
```

运行程序，并查询redis,可以看到已经将session放入到redis中

<img src="https://gitee.com/shilongshen/xiaoxingimagebad/raw/master/img/20210601220112.png" style="zoom: 67%;" />

接下来验证这套方案是否可以满足分布式环境下的要求。

本地项目打包并上传到云端服务器。

在另外一台服务器上启动redis

在Java程序服务器上修改`application.properties`，

```properties
#添加
spring.redis.host="redis服务器的ip地址"
```



2.基于token传输类似sessionId，Java代码session实现迁移到redis

修改UserController

```java
 @Autowired
    private RedisTemplate redisTemplate;

//    用户登录接口
    @RequestMapping(value = "/login", method = {RequestMethod.POST}, consumes = {CONTENT_TYPE_FORMED})
    @ResponseBody
public CommonReturnType login(@RequestParam(name = "telphone") String telphone
            , @RequestParam(name = "password") String password) throws BusinessException, UnsupportedEncodingException, NoSuchAlgorithmException {

//...修改以下代码
    

//        修改为如果用户登录验证成功将对应的登录信息和登录凭证一起存入redis中
//        生成登录凭证token，UUID
        String uuidToken = UUID.randomUUID().toString();
        uuidToken = uuidToken.replace("-", "");
//        建立token和用户登录态之间的联系
    /**
        * 注意新建token后token会存储在redis以及本地浏览器中，注意清除本地浏览器中的token,
        * */
    
        redisTemplate.opsForValue().set(uuidToken, userModel);//redis中uuidToken就是key，userModel就是value这样一来只要redis中存在uuidToken这个key，就惹味userModel存在
        redisTemplate.expire(uuidToken, 1, TimeUnit.HOURS);//设置超时时间为1小时

//        现在不需要IS_LOGIN了
//        this.httpServletRequest.getSession().setAttribute("IS_LOGIN", true);
//        //如果用户登录成功，就将userModel放到对应用户的session
//        this.httpServletRequest.getSession().setAttribute("LOGIN_USER", userModel);
        // 并且返回给前端一个正确的信息


//        下发了token
        return CommonReturnType.create(uuidToken);
    }
```

修改login.html

```html
//...
$.ajax({
                type: "POST",
                //contextType->consumes,对应的一个后端需要消费一个contextType名字
                contextType: "application/x-www-form-urlencoded",
                url: "http://"+g_host+"/user/login",
                //传递参数
                data: {
                    "telphone": $("#telphone").val(),
                    "password": password,
                },
                //允许跨域请求
                xhrFields: {withCredentials: true},
                /**
                 * 只要被服务端正确处理，会进入success
                 * 如果比如由于网络原因，会进入error
                 * */
                success: function (data) {
                    if (data.status == "success") {
                        alert("登录成功");
                        //如果登录成功，直接跳转到商品列表页

					//如果登录成功，在data中拿到token，并将其存储
                        var token=data.data;
                        window.localStorage["token"]=token;//将token存储，本质上是一个key-value，key是token，value是token


                        window.location.href="listitem.html";
                    } else {
                        alert("登录失败，原因为" + data.data.errMsg);
                    }
                },
                error: function (data) {
                    alert("登录失败，原因为" + data.responseText);
                },
            });
```

修改getitem.html

```html
//...
$("#createOrder").on("click", function() {

           //当点击下单按钮的时，从window.localStorage拿到token，如果token为null，直接跳转到登录页
            var token=window.localStorage["token"];
            if(token==null){
                alert("没有登录，不能下单");
                window.location.href="login.html";//如果没有登录就直接跳到登录页
                return false;
            }
            $.ajax({
                type: "POST",
                //约定：如果用户登录或者需要登录态的请求，就将token拼接在一个url在中
                url: "http://"+g_host+"/order/createorder?token="+token,
                contentType: "application/x-www-form-urlencoded",
                data: {
                    "itemId": g_itemVO.id,
                    "promoId": g_itemVO.promoId,
                    "amount": 1,//暂时写死为一件
                },
//...
```

修改orderController



```java
@Autowired
    private RedisTemplate redisTemplate;

//封装下单请求
    @RequestMapping(value = "/createorder", method = {RequestMethod.POST}, consumes = {CONTENT_TYPE_FORMED})
    @ResponseBody
    public CommonReturnType createOrder(@RequestParam(name = "itemId") Integer itemId,
                                        @RequestParam(name = "promoId", required = false) Integer promoId,
                                        @RequestParam(name = "amount") Integer amount) throws BusinessException {

//        在UserController将登录凭证加入到用户登录成功的session内，在Session中设置IS_LOGIN，LOGIN_USER
//        因此只需要从用户的Session中获取道对应的用户信息即可,

        //根据IS_LOGIN判断用户是否登录
//        现在不用IS_LOGIN进行判断了
//        Boolean isLogin = (Boolean) httpServletRequest.getSession().getAttribute("IS_LOGIN");
//        获取token
        String token = httpServletRequest.getParameterMap().get("token")[0];
        if (token == null) {
            throw new BusinessException(EmBusinessError.USER_NOT_LOGIN);
        }
//通过token获取userModel
        UserModel userModel = (UserModel) redisTemplate.opsForValue().get(token);


//        if (isLogin == null || !isLogin) {
//            throw new BusinessException(EmBusinessError.USER_NOT_LOGIN);
//        }
        //获取用户的登录信息userModel  LOGIN_USER
//        UserModel userModel = (UserModel) httpServletRequest.getSession().getAttribute("LOGIN_USER");

        if (userModel == null) {
            throw new BusinessException(EmBusinessError.PARAMETER_VALIDATION_ERROR, "用户登录过期，userModel等于null");
        }


//创建订单,只有用户登录了才能够进行下单，用户的登录信息是在当前Session中获取的
        OrderModel orderModel = orderService.createOrder(userModel.getId(), itemId, promoId, amount);


        return CommonReturnType.create(null);
    }

```

测试

讨论：

在后序的操作中会尽量的避免使用cookie传输session的方式，并不是说cookie传输session的方式不安全或不好，。而是对于一个企业级别的应用来说，不仅仅要支持HTML页面，也要支持移动设备，例如小程序，Android等，并不是所有的都支持cookie这种方式传输session；网络的情况下对应的cookie规则是否会被改变，这些都是后端开发人员无法控制的。因此大型企业应用中一般都是使用基于token传输类似sessionId这种方式来完成分布式会话。



## 本章小结

- 使用nginx反向代理来完成分布式web应用的扩展
- 使用分布式session来完成分布式会话管理
- 使用redist解决分布式会话管理问题

# 第9章 查询优化技术之多级缓存

本章目标

- 掌握多级缓存的定义
- 掌握redis缓存，本地缓存
- 掌握热点nginx lua缓存

缓存设计的原则

- 用快速存取设备，用内存
- 将缓存推到离用户最近的地方
- 脏缓存清理

多级缓存

- redis缓存
- 热点内存本地缓存
- nginx proxy cache 缓存
- nginx lua缓存

<img src="https://gitee.com/shilongshen/xiaoxingimagebad/raw/master/img/20210605204114.png" style="zoom:33%;" />

> 性能越高的缓存就是离用户越近的地方。
>
> 但是离用户越近的缓存更占用系统分布式的资源、且更不容易被更新，因此应用需要忍受脏读

redis缓存

redis缓存是具备将数据存储到磁盘的能力的

常将其作为集中式缓存中间件，并将其作为key-value内存级别数据库存储，且是易失性存储

两种模式：

- 单机版
- sentinal哨兵模式

有多台redis服务器，它们之间是主从（master，slave）关系，有一台redis哨兵。哨兵会同时监听多台redis服务；应用服务器会向redis哨兵发送ask信号，redis哨兵返回一个信号告诉哪一台是redis master，这样应用服务器就会访问redis master。如果redis master出现了故障，redis哨兵redis master进行更改，同时会将返回一个信号告诉应用服务器redis master已经被更改了，这样应用服务器就会访问新的redis master。（应用服务器只和redis master进行连接，并且只需要感知redis哨兵即可）

<img src="https://gitee.com/shilongshen/xiaoxingimagebad/raw/master/img/20210602222405.png" style="zoom:33%;" />

- 集群cluster模式

多台redis

redis集群，网状连接，会自动竞选出哪一个为master 哪一个为slave。应用服务器只需要连接任何一台redis，既可以得到redis集群中任何一台redis信息，维护在内存中。

<img src="https://gitee.com/shilongshen/xiaoxingimagebad/raw/master/img/20210602224350.png" style="zoom:33%;" />

## 使用redis进行商品详情页动态内容请求的实现

使用redis来做商品详情页动态获取接口缓存内容实现，在springMVC的controller层将对应的redis引入，将从下游server层获取到的一些数据在controller层缓存起来，以便于下次再有任何的请求进来时，可以直接判断缓存中是否有对应商品详情页的数据 ，如果有的话直接返回 ，不走下游的service层调用，减少对数据库的依赖。

**修改ItemControlller**

之前获取商品的逻辑是根据传入的商品Id到数据库中取查找是否有相应的商品，这样会花费大量的时间在数据库的查找中。

改进的思路就是在ItemControlller层中，在访问对应的item之前完成缓存的操作。如果redis缓存中没有就到数据库中查询并将其存在redis中，如果有就直接在redis缓存中进行访问，以减少数据库访问的耗时。

```java

@Autowired
    private RedisTemplate redisTemplate;


/**
     * 商品详情页浏览
     */
    @RequestMapping(value = "/get", method = {RequestMethod.GET})
    @ResponseBody
    public CommonReturnType getItem(@RequestParam(name = "id") Integer id) {

//      根据商品的id到redis内获取itemModel--->在redis中key为item_id,value为itemModel
        ItemModel itemModel = (ItemModel) redisTemplate.opsForValue().get("item_" + id);
//      如果redis中不存在对应的itemModel，则访问下游service
        if (itemModel == null) {
            itemModel = itemService.getItemById(id);
//            设置itemModel到redis内
            redisTemplate.opsForValue().set("item_" + id, itemModel);
//            设置redis缓存的失效时间
            redisTemplate.expire("item_" + id, 10, TimeUnit.MINUTES);
        }
    
    //......
    }
```

云端部署，并进行测试

## 本地热点缓存

在redis缓存之上还需要引入一个本地热点缓存。

`多级缓存的概念：先去本地缓存、如果本地缓存不存在，到redis缓存中取，如果redis缓存中也不存在，才到数据库中取。`

- 用于存放热点数据

  可以减少服务端到redis中取数据的网络开销，也可以减少redis服务器的压力。

- 脏读非常不敏感

  因为本地热点缓存在一个分布式的环境下，是每一台服务器都有热门商品的一个备份数据，如果说对应的商品在数据库中发生变化的时候，对于redis的缓存只需要清除对应的key即可，但是对于本地的热点缓存来说，很少有方法清除JVM中的数据的，因为要清除JVM中的数据必须要每台服务器都清除对应的数据，要通知每台应用服务器都清除本地热点缓存信息，本身就是很难的（可以使用MQ，异步消息队列，来广播通知，应用服务器作为监听）

- 内存可控

  热点数据可以认为是经常被访问并且变化非常少的数据。本地热点缓存的生命周期不是特别长（比redis的短）

> 本地热点缓存存在的问题：当数据更新的时候，没有很好的办法更新本地热点缓存，而且存在JVM容量大小的限制。

本地热点缓存的设计时需要考虑淘汰的机制（例如先进先出、最长时间未被访问等），以保证不常被使用的key被淘汰掉，也可以做到key自动失效

### Guava cache

本质上是一个可并发的hashmap

- 可以控制key-value的大小和key的超时时间
- 可以配置LRU策略（最近最少未被使用的key会被淘汰）
- 线程安全

1.引入依赖

```xml
 <dependency>
   <groupId>com.google.guava</groupId>
   <artifactId>guava</artifactId>
   <version>19.0</version>
 </dependency>
```

2.CacheService

```java
package com.example.miaosha.service;

//封装本地缓存
public interface CacheService {
    //    存方法
    void setCommonCache(String key, Object value);

    //    取方法
    Object getCommonCache(String key);
}
```

3.CacheServiceImpl

```java
package com.example.miaosha.service.impl;

import com.example.miaosha.service.CacheService;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import org.springframework.stereotype.Service;


import javax.annotation.PostConstruct;
import java.util.concurrent.TimeUnit;

@Service
public class CacheServiceImpl implements CacheService {

    private Cache<String, Object> commonCache = null;

    @PostConstruct
    public void init() {
        commonCache = CacheBuilder.newBuilder()
//设置缓存容器的初始化容量为10
                .initialCapacity(10)
//设置缓存中最大可以存储100个KEY，超过100个以后会按照LRU的策略移除缓存项
                .maximumSize(100)
//设置写缓存后多少秒过期
                .expireAfterWrite(60, TimeUnit.SECONDS).build();
    }

    @Override
    public void setCommonCache(String key, Object value) {
        commonCache.put(key, value);
    }

    @Override
    public Object getCommonCache(String key) {
        return commonCache.getIfPresent(key);
    }
}
```

4.修改ItemController

```java
@Autowired
    private CacheService cacheService;

//...

/**
     * 商品详情页浏览
     */
    @RequestMapping(value = "/get", method = {RequestMethod.GET})
    @ResponseBody
    public CommonReturnType getItem(@RequestParam(name = "id") Integer id) {
        ItemModel itemModel = null;

//1.先到本地缓存中找对应的商品
        itemModel = (ItemModel) cacheService.getCommonCache("item_" + id);

//2.如果本地缓存中不存在，到redis缓存中找
        if (itemModel == null) {
//      根据商品的id到redis内获取itemModel--->在redis中key为item_id,value为itemModel
            itemModel = (ItemModel) redisTemplate.opsForValue().get("item_" + id);
//3.如果redis中不存在对应的itemModel，则访问下游service，到数据库中找
            if (itemModel == null) {
                itemModel = itemService.getItemById(id);
//            并设置itemModel到redis内
                redisTemplate.opsForValue().set("item_" + id, itemModel);
//            设置redis缓存的失效时间
                redisTemplate.expire("item_" + id, 10, TimeUnit.MINUTES);
            }
//            填充本地缓存
            cacheService.setCommonCache("item_" + id, itemModel);
        }

        ItemVO itemVO = converVOFromModel(itemModel);
        return CommonReturnType.create(itemVO);
    }
```

## nginx缓存策略

之前的本地热点缓存时部署在应用服务器上的，可以考虑将其部署到nginx上。因为nginx是离用户H5最近的一个结点。

<img src="https://gitee.com/shilongshen/xiaoxingimagebad/raw/master/img/20210604150232.png" style="zoom:33%;" />

###  nginx proxy cache缓存

- nginx反向代理前置
- 依靠文件系统存索引级文件
- 依靠内存缓存文件地址

修改nginx.conf

```properties
#声明一个cache缓存结点的内容
proxy_cache_path /usr/local/openresty/nginx/tmp_cache levels=1:2 keys_zone=tmp_cache:100m inactive=7d max_size=10g;
#keys_zone的大小为100m，存储所用的key,时间为7天，存储容量操作10刚开始采取LRU算法

location / {
	proxy_cache tmp_cache;
	proxy_cache_key $uri;
	proxy_cache_valid 200 206 304 302 7d;
	#只用状态码为200 206 304 302才进行cache
}
```

重启：sbin/nginx -s reload，访问：http://10.250.191.96/item/get?id=11

但是这种方法并没有比直接使用将本地热点缓存时部署在应用服务器上好！原因为本地缓存是直接在磁盘上进行存取，并没在nginx的内存中。

### nginx lua

- lua协程机制

协程机制的好处就是在编写对应代码的时候就无需考虑异步的方式，完全以同步的方式。一旦协程在运行的时候遇到了任何的阻塞，比如果说发生了IO调用，会主动到nginx的epoll模型上注册异步回调的句柄，放弃自己的执行权限，然后当对应的epoll模型接收到IO模型阻塞调用返回之后会再将对应的协程唤醒。

- 协程机制的好处

  依附于线程内存模型，切换开销小
  遇到阻塞就归还执行权，代码同步
  无需加锁

<img src="https://gitee.com/shilongshen/xiaoxingimagebad/raw/master/img/20210604161640.png" style="zoom:50%;" />

- nginx协程

  - nginx的每一个worker进程都是在epoll或kqueue这种事件模型之上，封装成协程
  - 每一个请求都哦于一个协程进行处理
  - 即使ngx_lua需要运行lua，相对C有一定的开销，但是仍然能保证高并发能力。
  - nginx每个工作进程创建一个lua虚拟机
  - 工作进程内的所有协程共享同一个JVM
  - 每个外部请求由一个lua协程处理，之间数据隔离
  - lua代码调用io等异步接口时，协程被挂起，上下文自动保存，不阻塞工作进程，io异步操作完成后还原协程上下文，代码继续执行。

<img src="https://gitee.com/shilongshen/xiaoxingimagebad/raw/master/img/20210604165154.png" style="zoom:50%;" />



- nginx lua插载点
  - init_by_lua:系统启动时调用
  - init_worker_by_lua：worker进程启动时调用
  - set_by_lua：nginx变量用复杂lua return
  - rewrite_by_lua:重写url规则
  - access_by_lua：权限验证阶段
  - content_by_lua：内容输出结点

<img src="https://gitee.com/shilongshen/xiaoxingimagebad/raw/master/img/20210605174614.png" style="zoom:50%;" />

修改nginx.conf

```conf
 #lua，即在系统启动时调用，这个只是用来做演示的！
    init_by_lua_file ../lua/init.lua;
```

添加init.lua

```shell
root@wag-SYS-7049GP-TRT:/usr/local/openresty# mkdir lua
root@wag-SYS-7049GP-TRT:/usr/local/openresty# cd lua
root@wag-SYS-7049GP-TRT:/usr/local/openresty/lua# vi init.lua
```

```lua
#添加
ngx.log(ngx.ERR,"init lua success");
```

再次修改nginx.conf

```conf
location /staticitem/get{
    default_type "text/html";#指定输出格式
   content_by_lua_file ../lua/staticitem.lua;
}
```

lua文件夹下添加staticitem.lua

```lua
hello static item lua
```

重启：sbin/nginx -s reload，访问：http://10.250.191.96/staticitem/get?id=11， 会输出

```
hello static item lua
```

可以通过lua挂载点来修改某一个url对应的输出



<img src="https://gitee.com/shilongshen/xiaoxingimagebad/raw/master/img/20210605184454.png" style="zoom: 33%;" />



### openResty实践

- shared dic：共享内存字典，所有worker进程可见，lru淘汰    --将热点商品的缓存放在nginx中

修改nginx.conf

```conf
lua_shared_dict my_cache 128m;

location /luaitem/get{
	default_type "application/json";
	content_by_lua_file ../lua/itemshareddic.lua;
}
```

进入lua目录新建itemshareddic.lua

```lua
function get_from_cache(key)
    local cache_ngx=ngx.shared.my_cache
    local value=cache_ngx:get(key)
    return value
end

function set_to_cache(key,value,exptime)
    if not exptime then
        exptime=0;
    end
    local cache_ngx=ngx.shared.my_cache
    local succ,err,forcible=cache_ngx:set(key,value,exptime)
    return succ
end

local args=ngx.req.get_uri_args() --拿到nginx get 请求上的参数
local id=args["id"]
local item_model=get_from_cache("item_"..id) -- 到nginx缓存中查找是否有对应的商品
if item_model==nil then
    local resp=ngx.location.capture("/item/get?id="..id)--如果说nginx缓存中没有对应的商品，就将对应的请求转发给后端的应用服务器上
    item_model=resp.body -- json返回请求
    set_to_cache("item_"..id,item_model,1*60)
end
ngx.say(item_model)--如果说nginx缓存中有对应的商品，直接返回
```

重启：sbin/nginx -s reload，访问：http://10.250.191.96/luaitem/get?id=11，

---



shared dic：共享内存字典支持lru淘汰，但是更新机制并不好

如何解决这一问题：

nginx的shared dic只将热点数据存在缓存中，非热点但是高流量数据存在redis的slave中，只读redis的slave，同时redis的slave通过redis的master做一个主从之间的同步，更新对应的一个脏数据。

<img src="https://gitee.com/shilongshen/xiaoxingimagebad/raw/master/img/20210605193719.png" style="zoom:33%;" />

在lua文件夹下新建itemredis.lua

```lua
local args=ngx.req.get_uri_args()
local id=args["id"]
local redis=require "resty.redis"
local cache = redis:new()
local ok,err=cache:connect("redis服务器的ip地址","6379")
local item_model=cache:get("item_"..id)
if item_model == ngx.null or item_model==nil then
    local resp = ngx.location.capture("/item/get?id="..id)
    item_model=repo.body
end

ngx.say(item_model)
```

修改ngnix.conf

```conf
location /luaitem/get{
	default_type "application/json";
	content_by_lua_file ../lua/itemredis.lua;
}
```

## 小结

思考：

- 如何解决缓存脏读和失效问题
- 在大型的应用集群中如果对redis访问过度依赖，是否会产生应用服务器到redis之间的网络带宽产生瓶颈？如果产生瓶颈如何解决
- nginx作为一个反向代理的中间件节点节点，若感知到业务，例如商品详情页的查询，是否会引入太多的定制化业务的能力，是否可以考虑隔离分层？是否可以使用两层nginx的策略取解决？多引入一层后又如何确保性能？

# 第10章 查询优化技术之页面静态化

之前的多级缓存都是H5(ajax)进来的请求，如何对H5(static)文件路径进行优化？

<img src="https://gitee.com/shilongshen/xiaoxingimagebad/raw/master/img/20210605193719.png" style="zoom:33%;" />

## 静态请求CDN

- DNS用CNAME解析到源站
- 回源缓存设置
- 强推失效

<img src="https://gitee.com/shilongshen/xiaoxingimagebad/raw/master/img/20210605211742.png" style="zoom:53%;" />

 

<img src="https://gitee.com/shilongshen/xiaoxingimagebad/raw/master/img/20210605213301.png" style="zoom:33%;" />





<img src="https://gitee.com/shilongshen/xiaoxingimagebad/raw/master/img/20210605215517.png" style="zoom:33%;" />

<img src="https://gitee.com/shilongshen/xiaoxingimagebad/raw/master/img/20210605220238.png" style="zoom: 50%;" />

有效性判断

- ETag:资源唯一标识
- if-none-match：客户端发送的匹配etag标识符
- last-modified：资源最后被修改的时间
- if-modified-since：客户端发送的匹配资源最后修改时间的标识符

<img src="https://gitee.com/shilongshen/xiaoxingimagebad/raw/master/img/20210605221738.png" style="zoom:33%;" />

<img src="https://gitee.com/shilongshen/xiaoxingimagebad/raw/master/img/20210605222311.png" style="zoom:33%;" />

**协商机制**：比较last-modified和etag到服务端，若服务端判断没变化则304不返回数据，否则200返回数据。

<img src="https://gitee.com/shilongshen/xiaoxingimagebad/raw/master/img/20210606145737.png" style="zoom:63%;" />



## CDN自定义缓存策略

对于CDN，它是介于客户端浏览器和服务端nginx之间的一个代理层，既充当了客户端的一个服务端的角色，也充当了服务端nginx的一个客户端的角色。

- 可自定义目录过期时间
- 可自定义后缀名过期时间
- 可自定义对应权重
- 可通过界面或api强制cdn对应目录刷新（非保成功）

<img src="https://gitee.com/shilongshen/xiaoxingimagebad/raw/master/img/20210606150724.png" style="zoom:50%;" />

## 静态资源部署策略

方法1：css,js,img等元素使用带版本号部署，例如a.js?v=1.0，这种方法不便利，且维护困难

方法2：css,js,img等元素使用带摘要部署，例如a.js?v=45dw，这种方法存在先部署HTML还是先部署资源 的覆盖问题

方法3（**推荐**）：css,js,img等元素使用摘要做文件名部署，例如a45dw.js，这种方法存新老版本可以并存并且可以回滚，资源部署完后再部署HTML。

---

<img src="https://gitee.com/shilongshen/xiaoxingimagebad/raw/master/img/20210606152058.png" style="zoom:40%;" />

<img src="https://gitee.com/shilongshen/xiaoxingimagebad/raw/master/img/20210606152213.png" style="zoom:40%;" />

## 全页面静态化

对应的部署结构：（获取商品详情）用户先访问秒杀nginx服务器下resources目录下的getitem.html，然后通过CDN拿到对应的静态资源文件，然后通过Ajax请求发送具体的根据商品id获取商品内容的请求，然后通过多级缓存返回对应的json数据给H5(Ajax),然后渲染H5(static)，最后生成一个给用户的页面。

<img src="https://gitee.com/shilongshen/xiaoxingimagebad/raw/master/img/20210605213301.png" style="zoom:33%;" />

定义：在服务端完成HTML，css,甚至js的load渲染成纯html文件后直接以静态资源的方式部署到cdn上。

（已经填充数据的商品详情页直接部署到CDN上）

### [phantomjs](https://github.com/ariya/phantomjs) 

> [参考](http://www.jsphp.net/python/show-24-270-1.html)
>
> PhantomJS是一个基于webkit的JavaScript API。它使用QtWebKit作为它核心浏览器的功能，使用webkit来编译解释执行JavaScript代码。任何你可以在基于webkit浏览器做的事情，它都能做到。它不仅是个隐形的浏览器，提供了诸如CSS选择器、支持Web标准、DOM操作、JSON、HTML5、Canvas、SVG等，同时也提供了处理文件I/O的操作，从而使你可以向操作系统读写文件等。PhantomJS的用处可谓非常广泛，诸如网络监测、网页截屏、无需浏览器的 Web 测试、页面访问自动化等。

<img src="https://gitee.com/shilongshen/xiaoxingimagebad/raw/master/img/20210606154835.png" style="zoom: 33%;" />

将对应的jQuery这边的内部的ajax获取商品详情的操作，在服务端或者在爬虫端执行掉，执行完成之后，依赖爬虫生成一个已经reloadDom完成的静态资源文件，然后将它部署到CDN上，完成全页面静态化操作。

在PhantomJS下新建getitem.js

```javascript
var page = require("webpage").create();
var fs = require("fs");
page.open("http://10.250.191.96/luaitem/get?id=11", function (status) {
    
    console.log("status=" + status);
    var isInit = "0";

    setInterval(function () {
        if (isInit !== "1") {
            page.evaluate(function () {
                initView();
            });
            isInit = page.evaluate(function () {
                return hasInit();
            });
        } else {
            fs.write("getitem.html", page.content, "w");
            phantom.exit();
        }
    }, 1000);

});
```

修改getitem.html

```html
<input type="hidden" id="isInit" value="0"/>

function hasInit() {
        var isInit = $("#isInit").val();
        return isInit;
    }

    function setHasInit() {
        $("#isInit").val("1");
    }

    function initView() {
        var isInit = hasInit();
        if (isInit == "1") {
            return;
        }
		...
    }
```

# 第11章 交易优化技术之缓存库存

本章目标

- 掌握高效交易验证模式   ------->创建订单(交易时)的时候先从redis缓存中查找是否有对应的item model和user model，如果有则直接从缓存中取，从而减少数据库查询消耗。
- 掌握缓存库存模型

交易性能瓶颈

- 交易验证完全依赖数据库
- 库存行锁，所有减库存操作都是串行进行的
- 后置处理逻辑

<img src="https://gitee.com/shilongshen/xiaoxingimagebad/raw/master/img/20210607193238.png" style="zoom:50%;" />

## 交易验证优化

- 用户风控策略优化：策略缓存模型化

- 活动校验策略优化：引入活动发布流程，模型缓存化，紧急下线能力

修改ItemServiceImpl

```java

//    item以及promo model缓存模型,在缓存中通过id获取商品
    @Override
    public ItemModel getItemByIdInCache(Integer id) {
//        在redis缓存中查询是否有对应的商品
        ItemModel itemModel = (ItemModel) redisTemplate.opsForValue().get("item_validate_" + id);
        if (itemModel == null) {
//            如果没有在数据库中查找，并将其设置到redis缓存中
            itemModel = this.getItemById(id);
            redisTemplate.opsForValue().set("item_validate_" + id, itemModel);
            redisTemplate.expire("item_validate_" + id,20, TimeUnit.MINUTES);
        }
        return itemModel;
    }
```

修改UserServiceImpl

```java
public UserModel getUserByIdInCache(Integer id) {
        UserModel userModel = (UserModel) redisTemplate.opsForValue().get("user_validate_" + id);
        if (userModel == null) {
            userModel = this.getUserById(id);
            redisTemplate.opsForValue().set("user_validate_" + id, userModel);
            redisTemplate.expire("user_validate_" + id, 20, TimeUnit.MINUTES);
        }
        return userModel;
}
```

修改OrderServiceImpl，**创建订单的时候先从redis缓存中查找是否有对应的item model和user model，如果有则直接从缓存中取，从而减少数据库查询消耗。**

```java
public OrderModel createOrder(Integer userId, Integer itemId, Integer promoId, Integer amount) throws BusinessException {
        //1.校验下单状态，下单的商品是否存在，用户是否合法，购买数量是否正确,以及校验活动信息
//        ItemModel itemModel = itemService.getItemById(itemId);//通过itemId获取itemModel
        ItemModel itemModel = itemService.getItemByIdInCache(itemId);//在redis缓存中通过itemId获取itemModel

        if (itemModel == null) {
            throw new BusinessException(EmBusinessError.PARAMETER_VALIDATION_ERROR, "商品不存在");
        }

        UserModel userModel = userService.getUserByIdInCache(userId);//查询用户信息
 //...   
}
```

## 库存行锁优化

首先分析：在库存扣减的时候会执行decreaseStock的sql语句上，其实数据库会在减库存的时候在itemId字段加上一个行锁（如果该字段有索引），如果该字段没有索引就会对整张表进行上锁。

```xml
<!--更新库存，根据商品id和商品购买数量扣减商品库存-->
    <update id="decreaseStock">
        update item_stock
        set stock = stock - #{amount}<!--将商品库存扣减-->
        where item_id = #{itemId} and stock >= #{amount}
    </update>
```

为item_stock表的item_id字段加上唯一索引。

  ```mysql
  alter table item_stock add unique index item_id_index(item_id)
  ```

<img src="https://gitee.com/shilongshen/xiaoxingimagebad/raw/master/img/20210608105837.png" style="zoom:80%;" />

库存行锁优化方案：

### 扣减库存缓存化

- 活动发布同步库存进缓存

修改PromoServiceImpl
```java
public void publishPromo(Integer promoId) {
//        通过活动id获取活动
        PromoDO promoDO = promoDOMapper.selectByPrimaryKey(promoId);
        if (promoDO==null||promoDO.getItemId() == null || promoDO.getItemId().intValue() == 0) {
            return;
        }
//        获取有活动的商品
        ItemModel itemModel = itemService.getItemById(promoDO.getItemId());
//        将库存同步到redis内
        redisTemplate.opsForValue().set("promo_item_stock_" + itemModel.getId(), itemModel.getStock());

    }
```

修改itemController

```java
/**
     * 发布活动商品，将库存存入缓存中
     */
    @RequestMapping(value = "/publishPromo", method = {RequestMethod.GET})
    @ResponseBody
    public CommonReturnType publishPromo(@RequestParam(name = "promoId") Integer promoId) {
        promoService.publishPromo(promoId);
        return CommonReturnType.create(null);
    }
```



- (活动商品)下单交易减缓存中库存

```java
//    库存扣减,根据商品id和商品购买数量扣减商品库存
    @Override
    @Transactional
    public Boolean decreaseStock(Integer itemId, Integer amount) {
//        返回值为影响的条目数，如果sql语句执行失败，返回值为0
//        int affectRow = itemStockDOMapper.decreaseStock(itemId, amount);
//        更新活动商品（减少）redis中的库存
        long result = redisTemplate.opsForValue().increment("promo_item_stock_" + itemId, amount.intValue()*-1);
//        result表示完成减库存操作后的数字
        if (result >= 0) {
//            更新库存成功
            return true;
        } else {
//            更新库存失败
            return false;
        }

    }
```

存在的问题

- 数据库和缓存中的库存记录不一致

**使用消息队列进行解决**:arrow_down:

### 异步同步数据库

解决方案：

- 活动发布同步库存进缓存
- 下单交易减缓存中的库存
- 异步消息扣减数据库中的库存

#### 异步消息队列rocketmq

- 高性能，高并发，分布式消息中间件
- 典型应用场景：分布式事务，异步解耦

<img src="https://gitee.com/shilongshen/xiaoxingimagebad/raw/master/img/20210608174133.png" style="zoom:40%;" />

<img src="https://gitee.com/shilongshen/xiaoxingimagebad/raw/master/img/20210608174213.png" style="zoom:40%;" />



<img src="https://gitee.com/shilongshen/xiaoxingimagebad/raw/master/img/20210608194345.png" style="zoom: 50%;" />



---

具体的步骤如下：

引入依赖

```xml
<dependency>
   <groupId>org.apache.rocketmq</groupId>
   <artifactId>rocketmq-client</artifactId>
   <version>4.8.0</version>
</dependency>
```

修改applacation.properties

```properties
#指定MQ nameserver的地址
mq.nameserver.addr=10.249.159.48:9876
#指定MQ的top
mq.topicname=stock
```

声明两个bean，一个是消息发送方，另一个是消息接收方；一旦redis缓存中更新库存成功，就发送一条消息出去，让异步消息队列感知到，然后减数据库的库存。

```java
@Component
public class MqProducer {
    private DefaultMQProducer producer;
    @Value("${mq.nameserver.addr}")
    private String nameAddr;
    @Value("${mq.topicname}")
    private String topicName;

    @PostConstruct
    public void init() throws MQClientException {
//    做mq producer的初始化
        producer = new DefaultMQProducer("producer_group");
        producer.setNamesrvAddr(nameAddr);
        producer.start();
    }

    //    同步库存扣减消息
    public boolean asyncReduceStock(Integer itemId, Integer amount) {
        Map<String, Object> bodyMap = new HashMap<>();
        bodyMap.put("itemId", itemId);
        bodyMap.put("amount", amount);
        Message message = new Message(topicName, "increase", JSON.toJSON(bodyMap).toString().getBytes(StandardCharsets.UTF_8));
        try {
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
```



```java
@Component
public class MqConsumer {
    private DefaultMQPushConsumer consumer;
    @Value("${mq.nameserver.addr}")
    private String nameAddr;
    @Value("${mq.topicname}")
    private String topicName;

    @Autowired
    private ItemStockDOMapper itemStockDOMapper;

    @PostConstruct
    public void init() throws MQClientException {
        //    做mq consumer的初始化
        consumer = new DefaultMQPushConsumer("stock_consumer_group");
        consumer.setNamespace(nameAddr);
        consumer.subscribe(topicName, "*");

        consumer.registerMessageListener(new MessageListenerConcurrently() {
            @Override
            public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> msgs, ConsumeConcurrentlyContext consumeConcurrentlyContext) {
//                实现库存到数据库扣减的逻辑,消费者订阅消息，一旦有消息就实现数据库库存扣减
                MessageExt message = msgs.get(0);
                String jsonString = new String(message.getBody());
                Map<String, Object> map = JSON.parseObject(jsonString, Map.class);
                Integer itemId = (Integer) map.get("itemId");
                Integer amount = (Integer) map.get("amount");

                //异步扣减数据库
                itemStockDOMapper.decreaseStock(itemId, amount);
//                一旦返回成功，MQ就会认定这条消息被消费，下次不会再做对应的投放了
                return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
            }
        });
        consumer.start();
    }
}
```

修改ItemServiceImpl

```java
//    库存扣减,根据商品id和商品购买数量扣减商品库存
    @Override
    @Transactional
    public Boolean decreaseStock(Integer itemId, Integer amount) {
//        返回值为影响的条目数，如果sql语句执行失败，返回值为0
//        int affectRow = itemStockDOMapper.decreaseStock(itemId, amount);
//        更新活动商品（减少）redis中的库存
        long result = redisTemplate.opsForValue().increment("promo_item_stock_" + itemId, amount.intValue() * -1);
//        result表示完成减库存操作后的数字
        if (result >= 0) {
//            更新库存成功

//          producer发送消息

            boolean mqResult = mqProducer.asyncReduceStock(itemId, amount);
//            如果消息发送失败，首先将库存进行回滚，返回false
            if (!mqResult) {
                redisTemplate.opsForValue().increment("promo_item_stock_" + itemId, amount.intValue());
                return false;
            }
//            如果消息发送成功，返回true
            return true;

        } else {
//            更新库存失败
//            有可能amount数量太多了，将库存回滚
            redisTemplate.opsForValue().increment("promo_item_stock_" + itemId, amount.intValue());
            return false;
        }

    }
```

存在问题:

异步消息发送失败，扣减操作执行失败,，下单失败无法正确这些情况下无法正确回补库存。

如果用户取消掉支付了，因为没有对应的库存操作记录，所以没有办法回滚对应的库存。

无法知道当前数据库的数据是否是正确的，是因为异步消息没有到，没有正确的减1，还是因为数据库没有减成功，都是没有办法知道的。就是说目前缺少一条记录用来确定当前缓存库存的操作是什么状态，如何解决这一问题呢？:point_down:

### 库存数据库最终一致性保证

请见下章:small_red_triangle_down:

# 第12章 交易优化技术之事务型消息

本章目标：

- 掌握异步化事务型消息模型
- 掌握库存售罄模型

## 引入事务型消息机制

存在问题：

```java
 @Transactional   //事务，一但有一个出错就会就不会执行
    public OrderModel createOrder(...){
        //1.校验下单状态，下单的商品是否存在，用户是否合法，购买数量是否正确,以及校验活动信息
        //2.落单减库存
        Boolean result = itemService.decreaseStock(itemId, amount);
        //3.订单入库
        //......
    }
```

```java
//    库存扣减,根据商品id和商品购买数量扣减商品库存
    @Override
    @Transactional
    public Boolean decreaseStock(Integer itemId, Integer amount) {
//        返回值为影响的条目数，如果sql语句执行失败，返回值为0
//        int affectRow = itemStockDOMapper.decreaseStock(itemId, amount);
//        更新活动商品（减少）redis中的库存
        long result = redisTemplate.opsForValue().increment("promo_item_stock_" + itemId, amount.intValue() * -1);
//        result表示完成减库存操作后的数字
        if (result >= 0) {
//            更新库存成功

//          producer发送消息
            boolean mqResult = mqProducer.asyncReduceStock(itemId, amount);
//            如果消息发送失败，首先将库存进行回滚，返回false
            if (!mqResult) {
                redisTemplate.opsForValue().increment("promo_item_stock_" + itemId, amount.intValue());
                return false;
            }
//            如果消息发送成功，返回true
            return true;

        } else {
//            更新库存失败
//            有可能amount数量太多了，将库存回滚
            redisTemplate.opsForValue().increment("promo_item_stock_" + itemId, amount.intValue());
            return false;
        }
    }
```

将`decreaseStock`操作搞到了redis内存中，如果`long result = redisTemplate.opsForValue().increment("promo_item_stock_" + itemId, amount.intValue() * -1);`操作成功（redis扣减成功），异步消息也成功发送出去了，但是此时订单入库产生异常，返回给用户的结果是落单失败，这会使得库存白白损失掉了（异步消息不进行回滚？异步消息一旦发送出去后，会将数据库中的库存扣除），虽然不会造成超卖的现象，但是会发生少卖，库存减少了，但是没有那么多的订单。

本质的问题在于**分布式事务**问题。`decreaseStock`操作在发送对应的异步消息之前，没有办法确定对应的其他操作，例如同一个事务中的订单入库操作，是否成功。

**是否可以在事务中的逻辑执行成功后再发送异步消息呢？**

修改ItemServiceImpl

```java
//    库存扣减,根据商品id和商品购买数量扣减商品库存
    @Override
    @Transactional
    public boolean decreaseStock(Integer itemId, Integer amount) {
//        返回值为影响的条目数，如果sql语句执行失败，返回值为0
//        int affectRow = itemStockDOMapper.decreaseStock(itemId, amount);
//        更新活动商品（减少）redis中的库存
        long result = redisTemplate.opsForValue().increment("promo_item_stock_" + itemId, amount.intValue() * -1);
//        result表示完成减库存操作后的数字
        if (result >= 0) {
//            更新库存成功,返回true
            return true;

        } else {
//            更新库存失败
//            有可能amount数量太多了，将库存回滚
            increaseStock(itemId, amount);
            return false;
        }
    }

//    库存回补
    @Override
    public boolean increaseStock(Integer itemId, Integer amount) {
        redisTemplate.opsForValue().increment("promo_item_stock_" + itemId, amount.intValue());
        return true;
    }

//异步更新库存
    @Override
    public boolean asyncDecreaseStock(Integer itemId, Integer amount) {
        return mqProducer.asyncReduceStock(itemId, amount);
    }
```

修改OrderServiceImpl

```java
@Override
    @Transactional
    public OrderModel createOrder(Integer userId, Integer itemId, Integer promoId, Integer amount) throws BusinessException {
        //1.校验下单状态，下单的商品是否存在，用户是否合法，购买数量是否正确,以及校验活动信息
		//...
        //2.落单减库存
		//...
        //3.订单入库
		//...
        
        
        //当该事务中的前面操作全部成功后才发送异步消息更新数据库中的库存
        //异步更新库存
        boolean mqResult = itemService.asyncDecreaseStock(itemId, amount);
//        如果消息发送失败，将库存进行回滚,抛出异常
        if (!mqResult) {
            itemService.increaseStock(itemId,amount);
            throw new BusinessException(EmBusinessError.MQ_SEND_FAIL);
        }
        
        //4.返回前端
        return orderModel;
    }
```

还是存在问题：

即使异步消息发送成功，还是可能在最后commit的时候因为网络原因或者是磁盘满了等原因产生了失败，这样库存还是被扣掉了。

解决：当事务提交（commit）成功后再去执行某一个方法，这样就可以在事务commit之后再发送异步消息！修改OrderServiceImpl

```java
TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronizationAdapter() {
            @SneakyThrows
            @Override
//            在最近一个Transactional成功commit后再执行方法
            public void afterCommit() {
                //当该事务中的前面操作全部成功后（commit后）才发送异步消息更新数据库中的库存
                //异步更新库存
                boolean mqResult = itemService.asyncDecreaseStock(itemId, amount);
//                但是一旦异步消息发送失败就没有办法回滚库存了
//        如果消息发送失败，将库存进行回滚,抛出异常
//                if (!mqResult) {
//                    itemService.increaseStock(itemId,amount);
//                    throw new BusinessException(EmBusinessError.MQ_SEND_FAIL);
//                }
            }
        });

```

但是还是存在问题：一旦异步消息发送失败就没有办法回滚库存了。

### 事务性消息机制

解决：使用RocketMQ Transactional，**保证数据库提交了，消息必定发送成功，数据库事务回滚了，消息必定不发送**。数据库状态未知，消息处理中，等待commit或者rollback。

修改MqProducer

```java
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
             *     ROLLBACK_MESSAGE：表示将prepare状态的消息撤回，等于没法
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

                try {
                    //只用订单创建成功了（commit后），才发送异步消息，否则不发送异步消息
                    orderService.createOrder(userId, itemId, promoId, amount);
                } catch (BusinessException e) {
                    e.printStackTrace();
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
                return null;
            }
        });
    }

    //  事务型同步库存扣减消息
    public boolean transactionAsyncReduceStock(Integer userId, Integer promoId, Integer itemId, Integer amount) {
        Map<String, Object> bodyMap = new HashMap<>();
        bodyMap.put("itemId", itemId);
        bodyMap.put("amount", amount);

        Map<String, Object> argsMap = new HashMap<>();
        argsMap.put("itemId", itemId);
        argsMap.put("amount", amount);
        argsMap.put("userId", userId);
        argsMap.put("promoId", promoId);

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
```

修改ordercontroller

```java
//封装下单请求
    @RequestMapping(value = "/createorder", method = {RequestMethod.POST}, consumes = {CONTENT_TYPE_FORMED})
    @ResponseBody
    public CommonReturnType createOrder(...){
//...

//创建订单
//OrderModel orderModel = orderService.createOrder(userModel.getId(), itemId, promoId, amount);
        //调用事务型同步库存创建订单
        boolean transactionAsyncReduceStock = mqProducer.transactionAsyncReduceStock(userModel.getId(), itemId, promoId, amount);
        if (!transactionAsyncReduceStock) {
            throw new BusinessException(EmBusinessError.UNKNOWN_ERROR, "下单失败");
        }

        return CommonReturnType.create(null);
    }
//
}
```

思考`checkLocalTransaction`函数对应的意义：当`createOrder`**操作耗时很长时**，例如在数据库压力比较大的时候耗时很长，这种情况下消息中间件broker中prepare的消息的状态一直没有改变（ROLLBACK_MESSAGE或COMMIT_MESSAGE）就会发起`checkLocalTransaction`函数的回调来判断库存扣减和下单是否是成功的，但是仅仅凭借`itemId`和`amount`是无法知道是哪一个订单(发送的异步消息是itemId和amont)，因此需要引入库存流水。



## 操作流水

数据类型：1.主业务类型：master data 2.操作型数据：log data

新建表

```mysql
CREATE TABLE `stock_log`  (
  `stock_log_id` varchar(64) CHARACTER SET utf8 COLLATE utf8_unicode_ci NOT NULL,
  `item_id` int(0) NOT NULL DEFAULT 0,
  `amount` int(0) NOT NULL DEFAULT 0,
  PRIMARY KEY (`stock_log_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_unicode_ci ROW_FORMAT = Dynamic;
```

MyBatis逆向工程

```xml
<table tableName="stock_log" domainObjectName="StockLogDO"
               enableCountByExample="false"
               enableUpdateByExample="false"
               enableDeleteByExample="false"
               enableSelectByExample="false"
               selectByExampleQueryId="false"></table>
```

修改ItemService，创建stockLog

```java
@Override
    @Transactional
//    初始化对应的库存流水
    //1表示初始状态，2表示下单成功，3表示下单回滚
    public String initStockLog(Integer itemId, Integer amount) {
        StockLogDO stockLogDO = new StockLogDO();
        stockLogDO.setItemId(itemId);
        stockLogDO.setAmount(amount);
        stockLogDO.setStockLogId(UUID.randomUUID().toString().replace("-", ""));
        stockLogDO.setStatus(1);

        stockLogDOMapper.insertSelective(stockLogDO);
        return stockLogDO.getStockLogId();
    }
```

修改MqProducer，**将stockLogId添加到消息中发送出去**

```java
//  事务型同步库存扣减消息
    public boolean transactionAsyncReduceStock(Integer userId, Integer promoId, Integer itemId, Integer amount, String stockLogId) {
        Map<String, Object> bodyMap = new HashMap<>();
        bodyMap.put("itemId", itemId);
        bodyMap.put("amount", amount);
        bodyMap.put("stockLogId", stockLogId);
    //...
    }
```

修改OrderController

```java
//封装下单请求
@RequestMapping(value = "/createorder", method = {RequestMethod.POST}, consumes = {CONTENT_TYPE_FORMED})
@ResponseBody
public CommonReturnType createOrder(@RequestParam(name = "itemId") Integer itemId,
                                        @RequestParam(name = "promoId", required = false) Integer promoId,@RequestParam(name = "amount") Integer amount) throws BusinessException {
       
    //...
    //加入库存流水init状态  -->下单之前初始化一条库存流水,然后库存流水就可以用于追踪异步扣减库存的消息
        String stockLogId = itemService.initStockLog(itemId, amount);

//        再去完成对应的下单事务型消息机制
//创建订单,只有用户登录了才能够进行下单，用户的登录信息是在当前Session中获取的
        //OrderModel orderModel = orderService.createOrder(userModel.getId(), itemId, promoId, amount);
        boolean transactionAsyncReduceStock = mqProducer.transactionAsyncReduceStock(userModel.getId(), itemId, promoId, amount, stockLogId);
        if (!transactionAsyncReduceStock) {
            throw new BusinessException(EmBusinessError.UNKNOWN_ERROR, "下单失败");
        }

        return CommonReturnType.create(null);
    
    //...
    }


```

修改OrderServiceImpl，

```java

@Override
    @Transactional
    public OrderModel createOrder(Integer userId, Integer itemId, Integer promoId, Integer amount, String stockLogId) throws BusinessException {   
     //...
        
		//设置库存流水状态为成功
        StockLogDO stockLogDO = stockLogDOMapper.selectByPrimaryKey(stockLogId);
        if (stockLogDO == null) {
            throw new BusinessException(EmBusinessError.UNKNOWN_ERROR);
        }
        stockLogDO.setStatus(2);
        stockLogDOMapper.updateByPrimaryKeySelective(stockLogDO);


        //4.返回前端
        return orderModel;
    }

```

这确保了如果`MqProduce`中`orderService.createOrder(userId, itemId, promoId, amount,stockLogId);` 更新成功，库存流水状态为成功，无论`return`是否被发送，

```java
try {
     //创建订单
     orderService.createOrder(userId, itemId, promoId, amount,stockLogId);
    } catch (BusinessException e) {
    	 e.printStackTrace();
         return LocalTransactionState.ROLLBACK_MESSAGE;
    }
return LocalTransactionState.COMMIT_MESSAGE;
```

修改MqProducer

```java
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
```

问题：多加了库存流水查询和插入操作，好不容易将库存的状态移动到redis缓存中，又加了一个库存流水的操作，这不是性能等于没有任何提升？实际不然！原因是之前扣减库存的操作是在`itemId`，但是`stockLogId`是跟着``orderController`的`creatrOrder`，也就是说只有当用户下单之后才会插入下单记录，每一个交易都是一个单独的行锁，单独的行锁对数据库的状态的压力是非常小的，因为不需要有一个锁并发竞争的状态。

## 业务场景决定高可用技术实现

<img src="https://gitee.com/shilongshen/xiaoxingimagebad/raw/master/img/20210612223854.png" style="zoom:30%;" />

<img src="https://gitee.com/shilongshen/xiaoxingimagebad/raw/master/img/20210612224130.png" style="zoom:33%;" />

## 库存售罄

- 库存售罄标识
- 售罄后不去操作后序流程
- 售罄后通知各系统售罄
- 回补上新

修改itemServiceImpl

```java
public boolean decreaseStock(Integer itemId, Integer amount) {
    //...
    long result = redisTemplate.opsForValue().increment("promo_item_stock_" + itemId, amount.intValue() * -1);
//        result表示完成减库存操作后的数字
        if (result > 0) {
//            更新库存成功
//            如果消息发送成功，返回true
            return true;
        } else if (result == 0) {
//打上库存已经售罄的标识
            redisTemplate.opsForValue().set("promo_item_stock_" + itemId,"true");
            return  true;
        } else {
//            更新库存失败
//            有可能amount数量太多了，将库存回滚
            increaseStock(itemId, amount);
            return false;
        }
    //...
}
```



修改orderController

```java
public CommonReturnType createOrder(...){
    
    //...
     if (userModel == null) {
            throw new BusinessException(EmBusinessError.PARAMETER_VALIDATION_ERROR, "用户登录过期，userModel等于null");
        }

//判断是否库存已售罄，若对应的售罄key存在，则直接返回下单失败
        if (redisTemplate.hasKey("promo_item_stock_" + itemId)) {
            throw new BusinessException(EmBusinessError.STOCK_NOT_ENOUGH);
        }
//加入库存流水init状态  -->下单之前初始化一条库存流水,然后库存流水就可以用于追踪异步扣减库存的消息
        String stockLogId = itemService.initStockLog(itemId, amount);
    
    //...
    
}
```

## 后置流程

- 销量逻辑异步化
- 交易逻辑异步化

交易单逻辑异步化

1.生成交易单sequence后直接返回

2.前端轮询异步单状态





















