# Maven入门

## 概述

Maven时专门用于管理和构建Java项目的工具，他的主要功能又：

- 提供了一套标准化的项目结构
  解决不同IDE项目结构不同不通用的问题

  ![image-20220702190855697](http://7n.cdn.wzl1.top/typora/img/image-20220702192651183.png)

- 提供了一套标注年华的构建流程（编译、测试、打包、发布）

![image-20220702192651183](http://7n.cdn.wzl1.top/typora/img/image-20220702192937863.png)

- 提供了一套以来管理机制

![image-20220702192937863](http://7n.cdn.wzl1.top/typora/img/image-20220703040454717.png)

## 本质

项目管理工具，将项目开发和管理过程抽象成一个项目对象模型 POM

POM Project Object Model:项目对象模型

![image-20220703040454717](http://7n.cdn.wzl1.top/typora/img/image-20220708004148634.png)

## 作用

- 项目构建：提供标准的、跨平台的自动化项目构建方式
- 依赖管理：方便快捷的管理项目以来的资源（Jar包），避免资源间的版本冲突问题
- 统一开发结构：提供标准的、统一的项目结构

## 下载和安装：

[Maven – Download Apache Maven](https://maven.apache.org/download.cgi)

bin文件，下载解压

![image-20220703041432157](http://7n.cdn.wzl1.top/typora/img/image-20220702190855697.png)

配置环境变量

# Maven基础概念

## 坐标

- 坐标：用于描述仓库中资源的位置
  - 仓库源[repo1.maven.org/maven2](http://repo1.maven.org/maven2)
  - [Maven Repository: Search/Browse/Explore (mvnrepository.com)](https://mvnrepository.com/)
- 坐标的主要组成：
  - groupID: 定义当前Maven项目隶属组织名称（通常是域名反写，例如：org.mybatis）
  - artifactID:定义当前Maven项目名称（通常是模块名称）
  - version:定义当前项目版本号
  - packaging:定义当前项目的打包方式
- Maven坐标的作用
- 使用唯一标识，唯一性定位资源位置，通过该表示可以将资源的识别与下载工作交由机器完成

## 仓库

- 仓库：用于存储村粗资源，包含各种jar包
- 仓库分类：
  - 本地仓库：自己电脑上存储资源的仓库，连接远程仓库获取资源
  - 远程仓库：非本机电脑上的仓库，为本地仓库提供资源
    - 中央仓库：Maven团队维护，存储所有资源的仓库
    - 私服：部门/公司范围内存储资源的仓库，从中央仓库获取资源
- 私服的作用：
  - 保存具有版权的资源，包含购买或自主研发的jar
    - 中央仓库中的jar都是开源的，不能存储具有版权的资源
  - 一定范围内共享资源，仅对内部开放，不对外共享

### 本地仓库配置

本地仓库默认位置：

![image-20220703075932391](http://7n.cdn.wzl1.top/typora/img/image-20220703075839929.png)

1. 修改本地仓库位置
   根目录/config/settings.xml
   ![image-20220703075839929](http://7n.cdn.wzl1.top/typora/img/image-20220703075932391.png)

在下方插入一句话：

```xml
<localRepository>F:/maven/repository</localRepository>
```

### 远程仓库配置

Maven莫尔尼连接的仓库位置

找到lib/maven-model-builder-3.8.6.jar/org/apache/maven/model/pom-4.0.0.xml

```xml
  <repositories>
    <repository>
      <id>central</id>
      <name>Central Repository</name>
      <url>https://repo.maven.apache.org/maven2</url>
      <layout>default</layout>
      <snapshots>
        <enabled>false</enabled>
      </snapshots>
    </repository>
  </repositories>
```

### 镜像仓库配置

- 在setting文件中配置阿里云镜像仓库

使用Ctrl+F搜索mirrors

在里面配置

```xml
   <mirror>
       <!--给镜像起名-->
      <id>nexus-aliyun</id>
       <!--镜像源-->
      <mirrorOf>central</mirrorOf>
      <name>Nexus aliyun</name>
       <!--http://maven.aliyun.com/nexus/content/groups/public-->
      <url>https://maven.aliyun.com/repository/public</url>
      <!-- <blocked>true</blocked>-->
    </mirror>
```

## 全局setting与用户setting的区别

- 全局setting定义了当前计算器中Maven的公共配置
- 用户setting定义了当前用户的配置

# 第一个Maven项目（手工制作）

## Maven工程目录结构

```bash
#Java工程结构
C:.
└─project-java
    └─src
        ├─main
        │  ├─java
        │  │  └─com
        │  │      └─Karos
        │  │          └─Maven
        │  └─resourses
        └─test
            ├─java
            │  └─com
            │      └─Karos
            │          └─Maven
            └─resourses

#Web工程结构
C:.
└─project-java
    └─src
        ├─main
        │  ├─java
        │  │  └─com
        │  │      └─Karos
        │  │          └─Maven
        │  ├─resourses
        │  └─webapp
        │      └─WEB-INF
        └─test
            ├─java
            │  └─com
            │      └─Karos
            │          └─Maven
            └─resourses
```

pom.xml配置

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
     <modelVersion>4.0.0</modelVersion>
        <artifactId>project-java</artifactId>
        <groupId>com.karos.Maven</groupId>
        <version>1.0.0</version>
        <packaging>jar</packaging>
    <dependencies>
        <dependency>
            <groupId>junit</groupId>
            <artifactId>junit</artifactId>
            <version>4.12</version>
        </dependency>
    </dependencies>
</project>
```

## 构建命令

```bash
mvn compile #编译 生成target目录
mvn clean #清理  清除target
mvn test #测试  生成surefire-reports和test-classes以及classes
mvn package #打包 打包
mvn install #安装到本地仓库
```

- surefire-reports 报告文件夹

## 插件创建工程

- 创建工程

  ```bash
  mvn archetype:generate         						#使用模板生成
  	-DgroupId={project-packaging}
  	-DartifactId={project-name}
  	-DarchetypeArtifactId=maven-archetype-quickstart  #模板名称
  	-Dversion=0.0.1-snapshot
  	-DinteractiveMode=false
  ```

  创建的时候要求不是Maven工程结构

- 创建Java工程

  ```bash
  mvn archetype:generate         						#使用模板生成
  	-DgroupId=com.Karos
  	-DartifactId=java-project
  	-DarchetypeArtifactId=maven-archetype-quickstart  #模板名称
  	-DinteractiveMode=false
  ```

- 创建Web工程

  ```bash
  mvn archetype:generate         						#使用模板生成
  	-DgroupId=com.Karos
  	-DartifactId=web-project
  	-DarchetypeArtifactId=maven-archetype-webapp  #模板名称
  	-Dversion=0.0.1-snapshot
  	-DinteractiveMode=false
  ```


# IDEA

每次都要自行配置

# 插件安装

## Tomcat

# 依赖管理

## 依赖配置

```xml
<!--设置具体的依赖-->
<dependency>
    <!--依赖所属群组ID-->
      <groupId>org.springframework</groupId>
    <!--依赖所属项目ID-->
      <artifactId>spring</artifactId>
    <!--依赖版本号-->
      <version>5.3.21</version>
</dependency>
```

## 依赖传递

依赖的依赖，模块的依赖都称为依赖，都可以用

- 依赖具有传递性
  - 直接依赖：在当前项目中通过依赖配置建立的依赖关系
  - 间接依赖：被资源的资源如果依赖其他资源，当前项目建业依赖其他资源

![image-20220708004148634](http://7n.cdn.wzl1.top/typora/img/]Q%2NV%}PPSOH0HYWYVC]K.png)

### 依赖传递冲突问题

- 路径优先
  当依赖中出现相同的资源时，层级越深，优先级越低，层级越浅，优先级越高
- 声明优先
  当资源在相同层级被依赖时，配置顺序考前的覆盖配置靠后的
- 特殊优先：
  当统计配置了相同资源的不同版本，后配置的覆盖先配置的

![image-20220708004610521](http://7n.cdn.wzl1.top/typora/img/image-20220703041432157.png)

### 可选依赖

- 可选以来指对外隐藏当前所依赖的资源（在上图中，3度对2度隐藏，不让自己的资源让别人看到）

  ```xml
  <optional>true</optional>
  ```

### 排除依赖

- 主动断开依赖的资源，被排除的资源无需指定版本（如，3中我们不想用4的某个依赖，并且排除的是所有的，所以就不用写版本号，主动断开间接依赖的资源）

  ```xml
  <exclusions>
  	<exclusion>
      	<groupId></groupId>
          <artofactId></artofactId>
      </exclusion>
  </exclusions>
  ```

## 依赖范围

![image-20220708010342603](http://7n.cdn.wzl1.top/typora/img/image-20220708010342603.png)

# 生命周期和插件

## 构建生命周期

- Maven构建生命周期描述的是一次构建过程经历经历了多少事件
  ![img](http://7n.cdn.wzl1.top/typora/img/image-20220708004610521.png)

- Maven对项目构建的生命周期划分为3套

  - clean：清理工作

    - pre-clean：执行一些需要在clean之前完成的工作
    - clean：移除所有上一次构建生成的文件
    - post-clean：执行一些需要在clean之后立刻完成的工作

  - default：核心工作 - 编译 测试 打包 部署

    ![image-20220708011835161](http://7n.cdn.wzl1.top/typora/img/image-20220708012910899.png)

  - site：产生报告、发布站点

    - pre-site：执行一些需要在生成站点文档之前完成的工作
    - site：生成项目的站点文档
    - post-site：执行一些需要在生成站点文档之后完成的工作，并且为部署做准备
    - site-deploy：将生成的站点文档部署到特定的服务器上

从头到底全部执行

## 插件

- 插件与生命周期内的阶段绑定，在执行到对应生命周期时执行对应的插件功能
- 默认maven在各个生命周期上绑定有预设的功能
- 通过插件可以自定义其他功能

对于jar插件

![image-20220708012910899](http://7n.cdn.wzl1.top/typora/img/image-20220708012930670.png)

![image-20220708012930670](http://7n.cdn.wzl1.top/typora/img/image-20220713030020403.png)

```xml
<executions>
    <execution>
        <!--执行位置-->
    	<goals>
        	<goal></goal>
        </goals>
        <!--在什么时候执行-->
        <phase></phase>
    </execution>
</executions>
```

# Maven高级

## 分模块开发与设计（重点）

### 工程模块与模块划分

![image-20220708020039767](http://7n.cdn.wzl1.top/typora/img/image-20220708020039767.png)

#### ssm_pojo拆分

新建模块 复制类

#### dao模块拆分

mybatis的配置文件也要复制

导入资源文件pojo（记得install）<img src="http://7n.cdn.wzl1.top/typora/img/image-20220713030832761.png" alt="image-20220713030020403" style="zoom:50%;" />

#### service拆分

Copy Src

删

改Context名

test修改  ![image-20220713030832761](http://7n.cdn.wzl1.top/typora/img/image-20220713031714919.png)

#### controller拆分

 ![image-20220713031340394](http://7n.cdn.wzl1.top/typora/img/image-20220713031340394.png)

## 聚合（重点）

### 多模块维护

![image-20220713031457309](http://7n.cdn.wzl1.top/typora/img/image-20220713031702794.png)![image-20220713031702794](http://7n.cdn.wzl1.top/typora/img/image-20220713031457309.png)![image-20220713031714919](http://7n.cdn.wzl1.top/typora/img/image-20220713031730981.png)![image-20220713031730981](http://7n.cdn.wzl1.top/typora/img/image-20220713032321604.png)![image-20220713031745392](http://7n.cdn.wzl1.top/typora/img/image-20220713031745392.png)

编译顺序按照依赖传递顺序编译，工程没有写打包方式默认为jar包

![image-20220713032007654](http://7n.cdn.wzl1.top/typora/img/image-20220713032538962.png)

## 继承（重点）

### 模块依赖关系维护

![image-20220713032321604](http://7n.cdn.wzl1.top/typora/img/image-20220713032007654.png)

![image-20220713032538962](http://7n.cdn.wzl1.top/typora/img/image-20220713033544758.png)![image-20220713033013594](http://7n.cdn.wzl1.top/typora/img/image-20220713033013594.png)

父工程统一管理，子工程引用（插件、拆分后的依赖关系也可以）

- 继承
  ![image-20220713033544758](http://7n.cdn.wzl1.top/typora/img/image-20220713033703151.png)
- 继承依赖定义
  ![image-20220713033703151](http://7n.cdn.wzl1.top/typora/img/image-20220713033734485.png)
- 继承依赖使用
  ![image-20220713033734485](http://7n.cdn.wzl1.top/typora/img/image-20220713033905915.png)
- 继承的资源
  ![image-20220713033815147](http://gd.7n.cdn.wzl1.top/typora/img/image-20220708011835161.png)
- 继承与聚合
  ![image-20220713033905915](http://gd.7n.cdn.wzl1.top/typora/img/image-20220713033815147.png)



## 属性（重点)

## 版本管理

## 资源配置

## 多环境开发配置

## 跳过测试

## 私服（重点）