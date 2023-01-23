# SpringBoot2

# SpringBoot2入门程序开发

SpringBoot室友PRivotal团队提供的全新框架，其设计目的是用来简化Spring应用的初始搭建以及开发过程

# IDEA版本

必须联网

# 官网创建版本

[Spring Initializr](https://start.spring.io/)

# aliyun

[阿里云知行动手实验室-在浏览器沉浸式学习最新云原生技术 (aliyun.com)](https://start.aliyun.com/)

# 手工

# 入门案例解析

- Spring程序缺点
  - 依赖设置繁琐
  - 配置繁琐
- SpringBoot程序有点
  - 起步依赖（简化依赖配置）
  - 自动配置（简化常用工程配置）
  - 辅助功能（内置服务器，......）

## partent

两次继承，Maven高级  

## starter

包含springmvc springweb等等

 ![image-20220713043518853](http://gd.7n.cdn.wzl1.top/typora/img/image-20220713043737872.png)

## 引导类

![image-20220713043737872](http://gd.7n.cdn.wzl1.top/typora/img/image-20220713191949189.png)

![image-20220713043925501](http://gd.7n.cdn.wzl1.top/typora/img/image-20220713043925501.png)

## 内嵌Tomcat

 ![image-20220713044403031](http://gd.7n.cdn.wzl1.top/typora/img/image-20220713044643425.png)

- Jetty比Tomacat更轻量级，可扩展性更强（相较于Tomcat），谷歌应用引起（GAE）以及全面切换为Jetty

### 内嵌服务器

![image-20220713044643425](http://gd.7n.cdn.wzl1.top/typora/img/image-20220713043518853.png)

# REST风格

笔记在SpringMVC中

# Spring注解

![image-20220714054823020](http://gd.7n.cdn.wzl1.top/typora/img/image-20220713193039614.png)

# 基础配置

## 属性配置

### application.properties

- banner logo设置
- logging 日志

https://docs.spring.io/spring-boot/docs/current/reference/html/application-properties.htm

所有配置是和所用技术有关的

### application.yml

### application.yaml

![image-20220713191942029](http://gd.7n.cdn.wzl1.top/typora/img/image-20220713191942029.png)

![image-20220713191949189](http://gd.7n.cdn.wzl1.top/typora/img/image-20220713044403031.png)

prop>yml>yaml

### 共存叠加

先沟通按照优先级，不同的都加载

### 配置文件识别 - Facket

在IDEA中配置，可以改名，也可以定义默认的配置文件

![image-20220713193039614](http://gd.7n.cdn.wzl1.top/typora/img/image-20220714032349341.png)

## yaml数据格式

- YAML（YAML Ain't Markup Language），一种数据序列化格式
- 优点：
  - 容易阅读
  - 容易与脚本语言交互
  - 以数据为核心，重数据 轻格式
- YAML文件扩展名
  - .yml（主流）
  - .yaml

### yaml语法规则

- 大小写敏感
- 属性层级关系使用多行描述，每行结尾使用冒号结束
- 使用缩进表示层级关系，同层级左侧对齐，只允许使用空格（不允许使用Tab键）
- 多个数据使用 - 分割 表示数组，也可以用中括号[]表示（此时用{}表示对象）
- 属性值前面添加空格（属性名与属性值之间使用冒号+空格作为分割）
- \# 表示注释

![image-20220713195021907](http://gd.7n.cdn.wzl1.top/typora/img/image-20220713195021907.png)

![image-20220713195111715](http://gd.7n.cdn.wzl1.top/typora/img/image-20220714032601698.png)

### 读取yaml单一属性数据

@value+spel表达式，对象访问同理用.，数组用[]

![image-20220714032209336](http://gd.7n.cdn.wzl1.top/typora/img/image-20220714032209336.png)

### yaml文件中的变量引用

![image-20220714032349341](http://gd.7n.cdn.wzl1.top/typora/img/image-20220713195111715.png)

在yaml中不会自动转义，需要加引号，而properties会自动转义且不能用引号

![image-20220714032601698](http://gd.7n.cdn.wzl1.top/typora/img/image-20220714041847815.png)

### 读取全部属性数据

Environment 自动装配

![image-20220714033951619](http://gd.7n.cdn.wzl1.top/typora/img/image-20220714041905799.png)

### 读取yaml引用类型属性数据

![image-20220714041117826](http://gd.7n.cdn.wzl1.top/typora/img/image-20220714033951619.png)

# 整合第三方技术

## SpringBoot整合Junit

![image-20220714041847815](http://gd.7n.cdn.wzl1.top/typora/img/image-20220714041117826.png)

![image-20220714041905799](http://gd.7n.cdn.wzl1.top/typora/img/image-20220714043535879.png)

### 整合Junit-classes属性

当引导类和test文件不在一个包中的时候会报错，因为没有Bean管理对象，找对象是在东一个包中找，否则自己给个类

![image-20220714042135811](http://gd.7n.cdn.wzl1.top/typora/img/image-20220714042619369.png)

![image-20220714042619369](http://gd.7n.cdn.wzl1.top/typora/img/image-20220714042135811.png)

没有在同一个包或者子包中，加上@SpringBootTest(classes = SpringJunitApplication.class)

### 整合mybatis

1. 创建新模块，选择Spring初始化
2. 导坐标 包括starter
3. 设置数据源参数
4. 定义数据层接口与映射配置
5. 注入Dao接口，测试功能



![image-20220714043535879](http://gd.7n.cdn.wzl1.top/typora/img/image-20220804035156689.png)

# 热部署

pom dev-tools

![image-20220804020846221](http://gd.7n.cdn.wzl1.top/typora/img/image-20220804020827462.png)![image-20220804020827462](http://gd.7n.cdn.wzl1.top/typora/img/image-20220804020846221.png)

![](http://gd.7n.cdn.wzl1.top/typora/img/image-20220804042235572.png)

## 关闭热部署

可以在配置文件中关闭，也可以

![image-20220804035156689](http://gd.7n.cdn.wzl1.top/typora/img/image-20220804040747082.png)

# 第三Bean属性绑定

![image-20220804040747082](http://gd.7n.cdn.wzl1.top/typora/img/image-20220804042820100.png)

![image-20220804041234279](http://gd.7n.cdn.wzl1.top/typora/img/image-20220804041234279.png) 

## 宽松绑定

@ConfigurationProperties注解

![image-20220804042218840](http://gd.7n.cdn.wzl1.top/typora/img/image-20220804042831632.png)

![image-20220804042235572](http://gd.7n.cdn.wzl1.top/typora/img/image-20220804054015610.png)

![image-20220804042820100](http://gd.7n.cdn.wzl1.top/typora/img/image-20220804042218840.png)

![image-20220804042831632](http://gd.7n.cdn.wzl1.top/typora/img/image-20220804050210068.png)

# 计量单位

![image-20220804050210068](http://gd.7n.cdn.wzl1.top/typora/img/image-20220805040135002.png)

# Bean属性校验

pom:validation-api  规范（jdbc和mysqldriver的关系）

## 开启注入校验

@Validated 放到类上

## 设置具体规则

@Max

@Min

## 使用Hibernate校验框架

# 加载测试专用属性

![image-20220804053956128](http://gd.7n.cdn.wzl1.top/typora/img/image-20220804054823195.png)

![image-20220804054015610](http://gd.7n.cdn.wzl1.top/typora/img/image-20220804053956128.png)

![image-20220804054031606](http://gd.7n.cdn.wzl1.top/typora/img/image-20220804054031606.png)

2.7以前arg>properties

2.7以后properties>arg

# 加载测试专用配置

![image-20220804054641550](http://gd.7n.cdn.wzl1.top/typora/img/image-20220805042806400.png)

![image-20220804054823195](http://gd.7n.cdn.wzl1.top/typora/img/image-20220805043249793.png)

2.7以后不需要@Import

# 测试类中启动Web环境

@SpringBootTest（webEnvironment

=SpringBootTest/WebEmvironment.RANDOM_PORT)

![image-20220805040135002](http://gd.7n.cdn.wzl1.top/typora/img/image-20220805051412307.png)

# 发送虚拟请求

@AutoConfigureMockMvc

@AutoWired MockMVC mvn

  ![image-20220805042806400](http://gd.7n.cdn.wzl1.top/typora/img/image-20220804054641550.png)

# 匹配响应执行状态

![image-20220805043249793](http://gd.7n.cdn.wzl1.top/typora/img/image-20220805055051737.png)

# 匹配相应体

## String

![image-20220805051412307](http://gd.7n.cdn.wzl1.top/typora/img/image-20220805060619778.png)

## Json

![image-20220805051714056](http://gd.7n.cdn.wzl1.top/typora/img/image-20220805051714056.png)

Json匹配的是内容，顺序可以不同

![image-20220805052924834](http://gd.7n.cdn.wzl1.top/typora/img/image-20220805060001825.png)

# 事务回滚

和Spring一样

@RollBack注解

# 测试数据使用随机数据

![image-20220805055051737](http://gd.7n.cdn.wzl1.top/typora/img/image-20220805060631975.png)

![image-20220805055508564](http://gd.7n.cdn.wzl1.top/typora/img/image-20220805061205710.png)

# 内置数据源 -- 数据层解决方案

![image-20220805060001825](http://gd.7n.cdn.wzl1.top/typora/img/image-20220805052924834.png)

![image-20220805060619778](http://gd.7n.cdn.wzl1.top/typora/img/image-20220714054823020.png)

![image-20220805060631975](http://gd.7n.cdn.wzl1.top/typora/img/image-20220805055508564.png)

![image-20220805061205710](http://gd.7n.cdn.wzl1.top/typora/img/image-20220804034320452.png)
