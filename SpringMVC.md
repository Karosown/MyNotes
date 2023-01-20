#  SpringMVC

# SpringMVC概述

- SpringMVC技术与Severlet技术功能等同，均属于Web层开发技术

![image-20220711001301802](C:\Users\30398\AppData\Roaming\Typora\typora-user-images\image-20220711001301802.png)

![image-20220711001312582](C:\Users\30398\AppData\Roaming\Typora\typora-user-images\image-20220711001312582.png)

- SpringMVC是一种基于Java实现MVC模型的轻量级Web框架
- 优点
  - 使用简单，开发便捷（相比于Servlet）
  - 灵活性强
- SpringMVC是一种表现层框架技术
- SpringMVC用于进行表现层功能开发

# SpringMVC入门案例

```xml
<properties>
  <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
  <maven.compiler.encoding>UTF-8</maven.compiler.encoding>
  <java.version>1.8</java.version>
  <maven.compiler.source>1.8</maven.compiler.source>
  <maven.compiler.target>1.8</maven.compiler.target>
</properties>

 <plugin>
        <groupId>org.apache.tomcat.maven</groupId>
        <artifactId>tomcat7-maven-plugin</artifactId>
        <version>2.1</version>
        <configuration>
          <port>8080</port>
          <path>/</path>
        </configuration>
      </plugin>
```

1. 加入依赖

   - SpringMVC
   - servlet

2. 创建SpringMVC控制器类（等同于Servlet功能）

   ```java
   @Controller
   public class UserController{
       @RequestMapping("/save")
       @ResponseBody
       public String save(){
           System.out.println("user save...");
           return "{'info':'springmvc'}";
       }
   }
   ```

3. 初始化SpringMVC环境（同Spring环境），设定SpringMVC加载对应的Bean

4. 初始化Servlet容器，加载SpringMVC环境，并设置SpringMVC技术处理请求

   ```java
   public class SevletContainersInitConfig extends AbstractDispatcherServletInitializer {
       //加载SpringMVC容器配置
       protected WebApplicationContext createServletApplicationContext() {
           AnnotationConfigWebApplicationContext ctx= new AnnotationConfigWebApplicationContext();
           ctx.register(SpringMvcConfig.class);
           return ctx;
       }
       //设置哪些请求归属SpringMVC处理
       protected String[] getServletMappings() {
           //设置所有请求
           return new String[]{"/"};
       }
       //加载Spring容器配置
       protected WebApplicationContext createRootApplicationContext() {
           return null;
       }
   }
   ```

![image-20220711013407209](G:\TY\SpringMVC\image-20220711013407209.png)

![image-20220711013423598](G:\TY\SpringMVC\image-20220711013423598.png)

![image-20220711014000612](G:\TY\SpringMVC\image-20220711014000612.png)

## SpringMVC入门程序开发总结 1+N

![image-20220711013515315](G:\TY\SpringMVC\image-20220711013515315.png)

## 入门案例工作流程分析

- 启动服务器初始化过程
  ![](G:\TY\SpringMVC\image-20220711014851292.png)<img src="G:\TY\SpringMVC\image-20220711015112651.png" alt="image-20220711015112651" style="zoom:50%;" />
  1. 服务启动，执行ServletContainersInitConfig类，初始化Web容器
  2. 执行createServletApplicationContext方法，创建了WebApplicationContext对象
  3. 加载SpringＭvcConfig
  4. 执行@ComponentScan加载对应的bean
  5. 加载UserController，每个@RequestMapping的名称对应一个具体的方法（调用的话为请求过程）
  6. 执行getServletMapps方法，定义所有请求都通过MVC
- 单次请求过程
  1. 发送请求localhost/save
  2. web容器发现所有请求都经过SpringMVC，将请求交给SpringMVC处理
  3. 解析请求路径/save
  4. 由/save匹配执行对应的方法save()
  5. 执行save()
  6. 检测到有@ResponseBody直接将save()方法的返回值作为相应求体返回给请求方

# Bean加载控制	

**Controller加载控制与业务bean加载控制**

- SpringMVC相关bean（表现层bean）
- Spring控制的bean
  - 业务bean serivce
  - 功能bean datasource等



- SpringMVC相关Bean加载控制
  - SpringMVC加载的Bean对应的包均在com.iteheima.controller包内 
- Spring相关bean加载控制
  - 方式一：Spring加载的bean设定扫描范围为com.itheima，排除掉controller包内的bean
    在@ComponentScan中设置 excludeFilters = @ComponentScan.Filter(type=FilterType.ANNOTATION,calsses=Controller.class))
  - 方式二：Spring加载的bean设定扫描范围为精准范围，例如service包、dao包等
  - 方式三：不区分Spring和SpringMVC的环境，加载到同一个环境中

![image-20220711024022425](G:\TY\SpringMVC\image-20220711024022425.png)

![image-20220711024042635](G:\TY\SpringMVC\image-20220711024042635.png)

简化开发

![image-20220711024343231](G:\TY\SpringMVC\image-20220711024343231.png)

# PostMan

- Postman是一款功能强大的网页调试与发送网页HTTP请求的Chrome插件
- 作用：常用于进行接口测试
- 特征
  - 简单
  - 实用
  - 美观
  - 大方

> 不过我用的是APIFOX

# 请求

##  设置请求映射路径

思考：如果团队多人开发，没人设置不同的请求路径，冲突问题如何解决？

- 设置模块名作为请求路径前缀 /user/save /book/save
- 为了降低耦合度，可以把@RequestMapping放到类上，设置一个前缀（直接设置）

## 请求方式

- GET请求
  -  写参数，传参自动带入 
- POST请求
  - 测试时使用body ×-www-form-urlencoded，如果使用form-data支持文件
  - 字符集过滤器
    重写getServletFilters
    CharacterEncodingFIlter
    setEncoding

## 5种类型参数传递

### 普通参数 @RequestParam

函数参数与传入参数的名字必须相同，否则需要使用注解@RequestParam进行绑定，其实都要写上，只不过默认框架给你省略了

### POJO参数

#### 简单数据

自动装配

#### 嵌套POJO数据

传参：内部pojo.属性

### 数组

传参使用同样的名称

### 集合

由于引用类型会造对象，所以接口无法直接传参进去，加个注解RequestParam，那最后使用的就只是传进来的数据

![image-20220714134442890](G:\TY\SpringMVC\image-20220714134442890.png)

## 请求参数（传递Json数据）

### 导入坐标

![image-20220711051057407](G:\TY\SpringMVC\image-20220711051057407.png)

### 开启Json转对象功能

在MvcConfig上面添加注解@EnableWebMvc

### @RequestParam

![image-20220714140151742](G:\TY\SpringMVC\image-20220714140151742.png)

### 使用@RequestBody

因为Json数据是在请求体中，而Param属于表单

![image-20220711052015427](G:\TY\SpringMVC\image-20220711052015427.png)

### @RequestHeader

![image-20220714135948245](G:\TY\SpringMVC\image-20220714135948245.png)

### @ Value

![image-20220714140058909](G:\TY\SpringMVC\image-20220714140058909.png)

## 日期类型传递

- 日期类型数据基于系统不同格式也不尽相同

可以直接传递，但是格式有要求

### 自定义格式

@DateTimeFormat(pattern = "yyy-MM-dd") 形参

![image-20220711052625590](G:\TY\SpringMVC\image-20220711052625590.png)

### 类型转换器

Converter接口

# 响应

RequestMapping可以返回文本 文件 字符串

如果想要自定义返回体，则添加注解@ResponseBody

## @ResponseBody

![image-20220711053438250](G:\TY\SpringMVC\image-20220711053438250.png)

## HttpMessageConverter接口（类型转换器）

![image-20220711053502854](G:\TY\SpringMVC\image-20220711053502854.png)

![image-20220711053608185](G:\TY\SpringMVC\image-20220711053608185.png)

# Ant风格路径

![image-20220714135727013](G:\TY\SpringMVC\image-20220714135727013.png)

# 页面转发与@RequestAttribute

![image-20220714140823962](G:\TY\SpringMVC\image-20220714140823962.png)

# 页面重定向

![image-20220813174007948](G:/TY/SpringMVC/image-20220813174007948.png)

# @MatreixVariable

SpringBoot默认禁用了该功能，需手动开启

![image-20220714141437186](G:\TY\SpringMVC\image-20220714141437186.png)

![image-20220714141544648](G:\TY\SpringMVC\image-20220714141544648.png)

# REST风格

## REST风格简介

- REST（Representation State Transfer），表现形式状态转换
  - 传统风格资源描述形式
    	http://localhost/user/getById?id=1
    	http://locaohost/user/saveUser
  - REST风格描述形式
    http://localhost/user/1
    http://localhost/user
  - 优点：
    - 隐藏资源的访问行为，无法通过地址得知资源是何种操作
    - 书写简化
- 按照REST风格访问资源时使用行为动作区分对资源进行了何种操作
  - http://localhost/users 查询全部用户信息 GET
  - http://localhost/users/1 查询指定用户信息 GET
  - http://localhost/users 添加用户信息 POST
  - http://localhost/users 修改用户信息 PUT
  - http://localhost/users/1 删除用户信息 DELETE![image-20220711131214877](G:\TY\SpringMVC\image-20220711131214877.png)
- 根据REST风格对资源进行访问称为RESTful

## RESTful入门案例

给Request设置一个method = RequestMethod.XXX

![image-20220711134657831](G:\TY\SpringMVC\image-20220711134657831.png)

![image-20220711134937339](G:\TY\SpringMVC\image-20220711134937339.png)  

![image-20220714134857905](G:\TY\SpringMVC\image-20220714134857905.png)![image-20220714135531950](G:\TY\SpringMVC\image-20220714135531950.png)

### 步骤：

1. 设定HTTP请求动作（动词）
   ![  ](G:\TY\SpringMVC\image-20220711135149502.png)
2. 设定请求参数（路径变量）
   ![image-20220711135230028](G:\TY\SpringMVC\image-20220711135230028.png)

![image-20220711135323775](G:\TY\SpringMVC\image-20220711135323775.png)

### 形参注解的区别

![image-20220711135416017](G:\TY\SpringMVC\image-20220711135416017.png)

## REST快速开发

### RestController = Controller + ResponseBody

### @RequestMapping设置POST请求使用PostMapping替代，如果设置ID加值即可

![image-20220711140041207](G:\TY\SpringMVC\image-20220711140041207.png)

## 案例：基于RESTFUL页面数据交互

# 异常管理器

@RestControllerAdvice=ConTrolllerAdvice+ResultBody

@ExcceptionHandler

要被扫描

# 拦截器

![image-20220714130814181](G:\TY\SpringMVC\image-20220714130814181.png)

![image-20220714130904490](G:\TY\SpringMVC\image-20220714130904490.png)

![image-20220714130753395](G:\TY\SpringMVC\image-20220714130753395.png)

![image-20220714132015698](G:\TY\SpringMVC\image-20220714132015698.png)

![image-20220714132024163](G:\TY\SpringMVC\image-20220714132024163.png)

![image-20220714132042768](G:\TY\SpringMVC\image-20220714132042768.png)![image-20220714132244383](G:\TY\SpringMVC\image-20220714132244383.png)

![image-20220714132321527](G:\TY\SpringMVC\image-20220714132321527.png)

![image-20220714133213843](G:\TY\SpringMVC\image-20220714133213843.png)

![image-20220714133222490](G:\TY\SpringMVC\image-20220714133222490.png  )

![  ](G:\TY\SpringMVC\image-20220714133249378.png)

![image-20220714133317964](G:\TY\SpringMVC\image-20220714133317964.png)

![image-20220714134155016](G:\TY\SpringMVC\image-20220714134155016.png)