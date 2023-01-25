# Docker学习笔记

# 安装

```bash
# 1、yum 包更新到最新 
yum update
# 2、安装需要的软件包， yum-util 提供yum-config-manager功能，另外两个是devicemapper驱动依赖的 
yum install -y yum-utils device-mapper-persistent-data lvm2
# 3、 设置yum源
yum-config-manager --add-repo https://download.docker.com/linux/centos/docker-ce.repo
# 4、 安装docker，出现输入的界面都输入 y 
yum install -y docker-ce
# 5、 查看docker版本，验证是否验证成功
docker -v
```

# Docker架构

![image-20230119150730726](http://gd.7n.cdn.wzl1.top/typora/img/image-20230119150730726.png)

# 配置Docker镜像加速

我用的阿里云的

如果是宝塔可以直接配置

## 宝塔

![image-20230119143834671](http://gd.7n.cdn.wzl1.top/typora/img/image-20230119143834671.png)

## bash

```bash
sudo mkdir -p /etc/docker
sudo tee /etc/docker/daemon.json <<-'EOF'
{
  "registry-mirrors": ["加速地址"]
}
EOF
sudo systemctl daemon-reload
sudo systemctl restart docker
```

> 这里对tee命令
>
> ![image-20230119144527374](http://gd.7n.cdn.wzl1.top/typora/img/image-20230119144527374.png)

# Docker进程相关命令

```bash
systemctl start docker
systemctl stop docker
systemctl restart docker
systemctl status docker #查看Docker服务状态
systemctl enable docker #设置开机自启
```

# Docker镜像相关命令

```bash
docker images
docker images –q				# 查看所有镜像的id
docker search 镜像名称
docker pull 镜像名称
docker rmi 镜像id				  # 删除指定本地镜像
docker rmi `docker images -q`  # 删除所有本地镜像
```

> 镜像版本查看：https://hub.docker.com
>
> 如果对于版本有限制，则为 镜像名称:版本号，否则默认为latest

# Docker容器相关命令

```bash
docker ps								# 查看正在运行的容器 
docker ps –a 							# 查看所有容器
docker run 参数
docker exec 参数 						   # 进入容器
docker stop 容器名称
docker start 容器名称 镜像名 /bin/bash
docker rm 容器名称						  #运行状态删除失败
docker inspect 容器名称					  # 查看容器信息
```

## run 参数说明

- `-i`:保持容器运行。通常与 -t 同时使用。加入it这两个参数后，容器创建后自动进入容器中，退出容器后，容器自动关闭。
- `-t`:为容器重新分配一个伪输入终端，通常与 -i 同时使用。
- `-d`:以守护(后台)模式运行容器。创建一个容器在后台运行，需要使用docker exec 进入容器。退出后，容器不会关闭。
- `-it `创建的容器一般称为交互式容器，-id 创建的容器一般称为守护式容器
- `--name`:为创建的容器命名。

# Docker数据卷

![image-20230119150749902](http://gd.7n.cdn.wzl1.top/typora/img/image-20230119150749902.png)

> **挂载**
>
> 复习一下，这个和Linux的挂载做下区分，不要弄混
>
> <img src="http://gd.7n.cdn.wzl1.top/typora/img/image-20230119150946773.png" alt="image-20230119150946773"  />
>
> 参考链接：
>
> - [什么是挂载，Linux挂载详解 (biancheng.net)](http://c.biancheng.net/view/2859.html)
> - [(77条消息) Linux中挂载详解以及mount命令用法_daydayup654的博客-CSDN博客_mount](https://blog.csdn.net/daydayup654/article/details/78788310)
>
> 

```bash
docker run ... –v 宿主机目录(文件):容器内目录(文件) ...
docker run -it --name='name' -v 宿主机目录(文件):容器内目录(文件) -v 宿主机目录(文件):容器内目录(文件)
```

.e.g:

启动一个Centos容器

```bash
docker run -it --name=c3 -v /root/data:/root/data_container centos /bin/bash
```

## 数据卷容器

![image-20230119152544956](http://gd.7n.cdn.wzl1.top/typora/img/image-20230119152544956.png)

# Docker应用部署

## MySQL

![image-20230119153815035](http://gd.7n.cdn.wzl1.top/typora/img/image-20230119153815035.png)

```bash
docker search mysql
docker pull mysql:5.6
# 在/root目录下创建mysql目录用于存储mysql数据信息
mkdir ~/mysql
cd ~/mysql
docker run -id \
-p 3306:3306 \
--name=c_mysql \
-v $PWD/conf:/etc/mysql/conf.d \
-v $PWD/logs:/logs \
-v $PWD/data:/var/lib/mysql \
-e MYSQL_ROOT_PASSWORD=123456 \
mysql:5.6
```

>$PWD是啥？和pwd有什么区别？
>
>前者是变量，后者是命令，用于获得当前目录

## TomCat

```bash
docker search tomcat
docker pull tomcat
# 在/root目录下创建tomcat目录用于存储tomcat数据信息
mkdir ~/tomcat
cd ~/tomcat
docker run -id --name=c_tomcat \
-p 8080:8080 \
-v $PWD:/usr/local/tomcat/webapps \
tomcat 
```

## Nginx

```bash
docker search nginx
docker pull nginx
# 在/root目录下创建nginx目录用于存储nginx数据信息
mkdir ~/nginx
cd ~/nginx
mkdir conf
cd conf
# 在~/nginx/conf/下创建nginx.conf文件,粘贴下面内容
vim nginx.conf	#用vi也行
```

```nginx
user  nginx;
worker_processes  1;

error_log  /var/log/nginx/error.log warn;
pid        /var/run/nginx.pid;


events {
    worker_connections  1024;
}


http {
    include       /etc/nginx/mime.types;
    default_type  application/octet-stream;

    log_format  main  '$remote_addr - $remote_user [$time_local] "$request" '
                      '$status $body_bytes_sent "$http_referer" '
                      '"$http_user_agent" "$http_x_forwarded_for"';

    access_log  /var/log/nginx/access.log  main;

    sendfile        on;
    #tcp_nopush     on;

    keepalive_timeout  65;

    #gzip  on;

    include /etc/nginx/conf.d/*.conf;
}
```

```bash
docker run -id --name=c_nginx \
-p 80:80 \
-v $PWD/conf/nginx.conf:/etc/nginx/nginx.conf \
-v $PWD/logs:/var/log/nginx \
-v $PWD/html:/usr/share/nginx/html \
nginx
```

## Redis

```bash
docker search redis
docker pull redis:5.0
docker run -id --name=c_redis -p 6379:6379 redis:5.0
```

外部连接Redis

```bash
redis-cli.exe -h 服务器地址 -p 6379
```

# Docker镜像

## Docker镜像原理

![image-20230119153921383](http://gd.7n.cdn.wzl1.top/typora/img/image-20230119153921383.png)

## Docker镜像制作

![image-20230119170711484](http://gd.7n.cdn.wzl1.top/typora/img/image-20230119170711484.png)

```bash
docker commit 容器id 镜像名称:版本号			#创建镜像
docker save -o 压缩文件名称 镜像名称:版本号	 #镜像压缩
docker load -i 压缩文件名称 				  #还原镜像
```

# DockerFile

## 概念

![image-20230119171458545](http://gd.7n.cdn.wzl1.top/typora/img/image-20230119171458545.png)

## DockerFile关键字

| 关键字      | 作用                     | 备注                                                         |
| ----------- | ------------------------ | ------------------------------------------------------------ |
| FROM        | 指定父镜像               | 指定dockerfile基于那个image构建                              |
| MAINTAINER  | 作者信息                 | 用来标明这个dockerfile谁写的                                 |
| LABEL       | 标签                     | 用来标明dockerfile的标签 可以使用Label代替Maintainer 最终都是在docker image基本信息中可以查看 |
| RUN         | 执行命令                 | 执行一段命令 默认是/bin/sh 格式: RUN command 或者 RUN [“command” , “param1”,”param2”] |
| CMD         | 容器启动命令             | 提供启动容器时候的默认命令 和ENTRYPOINT配合使用.格式 CMD command param1 param2 或者 CMD [“command” , “param1”,”param2”] |
| ENTRYPOINT  | 入口                     | 一般在制作一些执行就关闭的容器中会使用                       |
| COPY        | 复制文件                 | build的时候复制文件到image中                                 |
| ADD         | 添加文件                 | build的时候添加文件到image中 不仅仅局限于当前build上下文 可以来源于远程服务 |
| ENV         | 环境变量                 | 指定build时候的环境变量 可以在启动的容器的时候 通过-e覆盖 格式ENV name=value |
| ARG         | 构建参数                 | 构建参数 只在构建的时候使用的参数 如果有ENV 那么ENV的相同名字的值始终覆盖arg的参数 |
| VOLUME      | 定义外部可以挂载的数据卷 | 指定build的image那些目录可以启动的时候挂载到文件系统中 启动容器的时候使用 -v 绑定 格式 VOLUME [“目录”] |
| EXPOSE      | 暴露端口                 | 定义容器运行的时候监听的端口 启动容器的使用-p来绑定暴露端口 格式: EXPOSE 8080 或者 EXPOSE 8080/udp |
| WORKDIR     | 工作目录                 | 指定容器内部的工作目录 如果没有创建则自动创建 如果指定/ 使用的是绝对地址 如果不是/开头那么是在上一条workdir的路径的相对路径 |
| USER        | 指定执行用户             | 指定build或者启动的时候 用户 在RUN CMD ENTRYPONT执行的时候的用户 |
| HEALTHCHECK | 健康检查                 | 指定监测当前容器的健康监测的命令 基本上没用 因为很多时候 应用本身有健康监测机制 |
| ONBUILD     | 触发器                   | 当存在ONBUILD关键字的镜像作为基础镜像的时候 当执行FROM完成之后 会执行 ONBUILD的命令 但是不影响当前镜像 用处也不怎么大 |
| STOPSIGNAL  | 发送信号量到宿主机       | 该STOPSIGNAL指令设置将发送到容器的系统调用信号以退出。       |
| SHELL       | 指定执行脚本的shell      | 指定RUN CMD ENTRYPOINT 执行命令的时候 使用的shell            |

## 根据DockerFile制作镜像

```bash
docker build -f DockerFile文件 -t 镜像名称:镜像版本号 .
```



## Demo

### SpringBoot

![image-20230119171759110](http://gd.7n.cdn.wzl1.top/typora/img/image-20230119171759110.png)

```bash
vim springboot_dockerfile

FROM java:8
MAINTAINER itheima <itheima@itcast.cn>
ADD HelloDocker-0.0.1-SNAPSHOT.jar app.jar
CMD java -jar app.jar

docker build -f springboot_dockerfile -t app .
docker run -id -p 9000:8080 app	#端口映射
```

### Centos7

![image-20230119172148853](http://gd.7n.cdn.wzl1.top/typora/img/image-20230119172148853.png)

```bash
vim centos_dockerfile

FROM centos:7
MAINTAINER itheima<itheima@itcast.cn>
RUN yum install -y vim
WORKDIR /usr
CMD /bin/bash

docker build -f centos_dockerfile -t itheima_centos:1 .
docker run -it --name=c5 itheima_centos:1
```

# Docker服务编排

![image-20230119173054097](http://gd.7n.cdn.wzl1.top/typora/img/image-20230119173054097.png)

为了降低工作量，我们引入了Docker Compose

## Docker Compose

![image-20230119173123724](http://gd.7n.cdn.wzl1.top/typora/img/image-20230119173123724.png)

### 安装Docker Compose

```bash
# Compose目前已经完全支持Linux、Mac OS和Windows
#在我们安装Compose之前，需要先安装Docker。下面我 们以编译好的二进制包方式安装在Linux系统中。 
curl -L https://github.com/docker/compose/releases/download/1.22.0/docker-compose-`uname -s`-`uname -m` -o usr/local/bin/docker-compose
# 设置文件可执行权限 
chmod +x /usr/local/bin/docker-compose
# 查看版本信息 
docker-compose -version
```

### 卸载Docker Compose

```bash
# 二进制包方式安装的，删除二进制文件即可
rm /usr/local/bin/docker-compose
```

###  使用docker compose编排nginx+springboot项目

```bash
mkdir ~/docker-compose
cd ~/docker-compose

vim docker-compose.yml

###docker-compose.yml-begin
version: '3'
services:
  nginx:
   image: nginx
   ports:
    - 80:80
   links:
    - app
   volumes:
    - ./nginx/conf.d:/etc/nginx/conf.d
  app:					##容器名称
    image: app			##绑定镜像
    expose:
      - "8080"
###docker-compose.yml-end

mkdir -p ./nginx/conf.d
vim ./nginx/conf.d/itheima.conf

###./nginx/conf.d/itheima.conf-begin
server {
    listen 80;
    access_log off;

    location / {
        proxy_pass http://app:8080;
    }
}
###./nginx/conf.d/itheima.conf-end

docker-compose up
```

# Docker私有仓库（私服）

![image-20230119173901991](http://gd.7n.cdn.wzl1.top/typora/img/image-20230119173901991.png)

## 私服搭建

```bash
# 1、拉取私有仓库镜像 
docker pull registry
# 2、启动私有仓库容器 
docker run -id --name=registry -p 5000:5000 registry
# 3、打开浏览器 输入地址http://私有仓库服务器ip:5000/v2/_catalog，看到{"repositories":[]} 表示私有仓库 搭建成功
# 4、修改daemon.json   
vim /etc/docker/daemon.json    
# 在上述文件中添加一个key，保存退出。此步用于让 docker 信任私有仓库地址；注意将私有仓库服务器ip修改为自己私有仓库服务器真实ip 
{"insecure-registries":["私有仓库服务器ip:5000"]} 
# 5、重启docker 服务 
systemctl restart docker
docker start registry
```

## 镜像上传

```bash
# 1、标记镜像为私有仓库的镜像     
docker tag centos:7 私有仓库服务器IP:5000/centos:7
 
# 2、上传标记的镜像     
docker push 私有仓库服务器IP:5000/centos:7
```

## 拉取镜像

```bash
#拉取镜像 
docker pull 私有仓库服务器ip:5000/centos:7
```

# 容器虚拟化与传统虚拟机的比较

![image-20230119174230926](http://gd.7n.cdn.wzl1.top/typora/img/image-20230119174230926.png)

![image-20230119174422057](http://gd.7n.cdn.wzl1.top/typora/img/image-20230119174422057.png)

# 结束语

>本笔记完全适用于[黑马程序员Docker容器化技术，从零学会Docker教程_哔哩哔哩_bilibili](https://www.bilibili.com/video/BV1CJ411T7BK/?spm_id_from=333.337.search-card.all.click&vd_source=39ab311d30f9c989c01184bc337556cf)
>
>部分笔记采纳于[黑马程序员-Docker - WeiBlog (weishao-996.github.io)](https://weishao-996.github.io/2022/11/17/黑马程序员-Docker/#Docker容器化虚拟化与传统虚拟机比较)

