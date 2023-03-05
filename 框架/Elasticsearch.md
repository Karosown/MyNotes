# Elasticsearch

# 初识Elasticsearch—基于数据库查询的问题

- 搜索服务器

## 数据库查询的问题

- 在做模糊查询下，索引将会失效，会全表扫描
- 功能太弱，无法分词操作

## 倒排索引

- 正向索引

  ![image-20230305191905718](http://gd.7n.cdn.wzl1.top/typora/img/image-20230305191905718.png)

- 词条

  ![image-20230305191959844](http://gd.7n.cdn.wzl1.top/typora/img/image-20230305191959844.png)

  ![image-20230305192056550](http://gd.7n.cdn.wzl1.top/typora/img/image-20230305192056550.png)![image-20230305192156989](http://gd.7n.cdn.wzl1.top/typora/img/image-20230305192157284.png)

  ![image-20230305192248378](http://gd.7n.cdn.wzl1.top/typora/img/image-20230305192248378.png)

倒排索引：将各个文档中的内容进行分词，形成词条，然后记录词条和数据的唯一标识（id）的对应关系，形成的产物

## 存储和搜索的原理

![image-20230305192807132](http://gd.7n.cdn.wzl1.top/typora/img/image-20230305192807132.png)

![image-20230305193246756](http://gd.7n.cdn.wzl1.top/typora/img/image-20230305193246756.png)

## 概念

- 一个基于Lucene的搜索服务器，相当于对Lucene的封装，不需要开发者自我实现分词功能
- 一个分布式、高扩展、高实时的搜索与数据分析引擎
- 基于Restful Web接口
- 用Java开发，并作为Apache许可条款下的开放源码发布，是一种流行的企业级搜索引擎

![image-20230305203125713](http://gd.7n.cdn.wzl1.top/typora/img/image-20230305203125713.png)

应用场景：

- 搜索：海量数据的查询
- 日志数据分析
- 实时数据分析

###  与MySQL的区别

- MySQL——事务性，ES没有，删除后无法恢复
- ES没有物理外键这个特性，如果数据强一致性要求高，那么慎用
- 分工不同，存储与搜索

![image-20230305203547529](http://gd.7n.cdn.wzl1.top/typora/img/image-20230305203547529.png)

# 安装ElasticSearch

