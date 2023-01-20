# RabbitMQ

AMQP协议（Advanced Message Queuing Protrocol） -- 高级消息队列协议、应用层

# 基础架构

![image-20230120121647888](http://gd.7n.cdn.wzl1.top/typora/img/image-20230120121647888.png)

# RabbitMQ安装

>安装过程基于Docker，Dokcer用法详见另外一篇

从RabbitMQ官网查看对应的Erlang版本

[RabbitMQ Erlang Version Requirements — RabbitMQ](https://www.rabbitmq.com/which-erlang.html)

![image-20230120083613195](http://gd.7n.cdn.wzl1.top/typora/img/image-20230120083613195.png)

```bash
docker pull erlang:25.2
docker pull rabbitmq:3.11-manager
docker run -id erlang
docker run -it --rm --name rabbitmq -p 5672:5672 -p 15672:15672 rabbitmq:3.11-management
```

> 查询IP：
>
> ```
> ifconfig
> ```
>
> 登录管控面板: IP:15672
>
> 账号密码均为guest

# 生产者&消费者

![Producer -> Queue -> Consuming: send and receive messages from a named queue.](https://www.rabbitmq.com/img/tutorials/python-one-overall.png)

![image-20230120121051847](http://gd.7n.cdn.wzl1.top/typora/img/image-20230120121051847.png)

# Demo

```java
package org.example;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeoutException;

public class Main {
    public static void main(String[] args) throws IOException, TimeoutException {

        // 创建连接工厂
        ConnectionFactory connectionFactory=new ConnectionFactory();

        // 设置参数
        connectionFactory.setHost("192.168.131.129");
        connectionFactory.setPort(5672);
        connectionFactory.setUsername("guest");     //默认值为guest
        connectionFactory.setPassword("guest");
        connectionFactory.setVirtualHost("/hello");
        // 创建连接 Connection
        Connection connection=connectionFactory.newConnection();
        // 创建Channel
        Channel channel = connection.createChannel();
        /**
         queueDeclare(String queue, boolean durable, boolean exclusive, boolean autoDelete, Map<String, Object> arguments)
         queue 队列名称
         durable 是否持久化
         exclusive{
         是否独占，只能又一个消费者监听这个队列
         当connection close时，是否删除队列
         }
         autoDelete 当没有Consumer时是否自动删除
         */
        // 创建队列
        channel.queueDeclare("Hello",true,false,false,null);
        // send
        /**
         * String exchange      交换机名称，简单模式下使用默认的""
         * String routingKey    路由名称，如果使用默认交换机，路由名称就为channel名称
         * AMQP.BasicProperties props 配置信息
         * byte[] body          //发送消息数据
         */
        String p="Hello!";
        channel.basicPublish("","Hello",null,p.getBytes());
        //释放资源
        channel.close();
        connection.close();
        System.out.println("OK");
    }
}
```

```java
package org.example;

import com.rabbitmq.client.*;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeoutException;

public class Main {
    public static void main(String[] args) throws IOException, TimeoutException {

        // 创建连接工厂
        ConnectionFactory connectionFactory=new ConnectionFactory();

        // 设置参数
        connectionFactory.setHost("192.168.131.129");
        connectionFactory.setPort(5672);
        connectionFactory.setUsername("guest");     //默认值为guest
        connectionFactory.setPassword("guest");
        connectionFactory.setVirtualHost("/hello");
        // 创建连接 Connection
        Connection connection=connectionFactory.newConnection();
        // 创建Channel
        Channel channel = connection.createChannel();
        /**
         queueDeclare(String queue, boolean durable, boolean exclusive, boolean autoDelete, Map<String, Object> arguments)
         queue 队列名称
         durable 是否持久化
         exclusive{
         是否独占，只能又一个消费者监听这个队列
         当connection close时，是否删除队列
         }
         autoDelete 当没有Consumer时是否自动删除
         */
        // 创建队列
        channel.queueDeclare("Hello",true,false,false,null);
        //String queue  队列名称
        // boolean autoAck  是否自动确认
        // Consumer callback 回调对象
        Consumer consumer=new DefaultConsumer(channel){
            //回调方法  ，当收到消息后自动执行
            // cisynerTag 标识
            // proerties 配置信息
            // body 数据
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                System.out.println(new String(body));
            }
        };
        channel.basicConsume("Hello",true,consumer);


    }
}
```

# 工作模式

## WorkQueues

![image-20230120182743025](http://gd.7n.cdn.wzl1.top/typora/img/image-20230120182743025.png)