参考：

REDEME在[该博客](https://blog.csdn.net/m0_37657841/article/details/90524410)基础上进行更改

[课程]()

## 项目环境

- IDEA，maven，MySQL5.x

* 项目运行方式：从IDEA导入项目，更新maven依赖，然后在MySQL数据库中运行miaosha.sql文件生成数据库。
* 项目入口为：com.miaoshaproject.App，使用IDEA启动后，若端口被占用，修改application.properties中的端口配置。
* 项目采用前后端分离，直接在浏览器打开resources目录下的getotp.html即可。



##	第一章 课程介绍

**电商秒杀应用简介**

> * 商品列表页获取秒杀商品列表
> * 进入商品详情页获取秒杀商品详情
> * 秒杀开始后进入下单确认页下单并支付成功

##	第二章 应用SpringBoot完成基础项目搭建

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

##	第三章 用户模块开发

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



##	第四章 商品模块开发

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

##	第五章 交易模块开发

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

##	第六章 秒杀模块开发

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



    //根据iremId获取即将开始的或者正在进行的活动
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





