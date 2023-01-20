# MyBatis

![image-20220710184234987](C:\Users\30398\AppData\Roaming\Typora\typora-user-images\image-20220710184234987.png)

## 持久层：

- 负责将数据到保存到数据库的那一层代码
- JavaEE三层架构：表现层、业务层、**持久层**

## JDBC缺点：

1. 硬编码 ->Mybatis配置文件
   - 注册驱动、获取链接
   - SQL语句
2. 操作繁琐 ->Mybatis自动完成
   - 手动设置参数
   - 手动封装结果集

MyBatis免除了几乎所有的Jdbc代码，以及色湖之参数和获取结果集的工作

# 快速入门

1. 创建User表，添加数据
   <img src="G:\TY\MyBatis\image-20220710185324288.png" alt="image-20220710185324288" style="zoom: 80%;" />
2. 创建模块，导入坐标
   ![image-20220710185432419](G:\TY\MyBatis\image-20220710185432419.png)
   MySQL驱动
3. 编写MyBatis核心配置文件 --> 替换连接信息 解决硬编码问题  ![image-20220710185646022](G:\TY\MyBatis\image-20220710185646022.png)
4. 编写SQL映射文件 --> 同一个管理SQL语句，解决硬编码问题
   ![image-20220710185809328](G:\TY\MyBatis\image-20220710185809328.png)
5. 编码
   1. 定义PO JO类
   2. 加载核心配置文件 获取SqlSesionFactory对象
   3. 获取SqlSession对象，执行SQL语句
   4. 释放资源

# XML

# User类

# Mybatis的映射文件

![image-20220716042023090](G:/TY/MyBatis/image-20220716042023090.png)

![image-20220711165716968](G:\TY\MyBatis\image-20220711165716968.png)

![image-20220711165835393](G:\TY\MyBatis\image-20220711165835393.png)

#  启动类

```java
        //加载核心配置文件
       InputStream is = Resources.getResourceAsStream("mybatis-config.xml");
            //获取SqlSessionFactoryBuilder对象
            SqlSessionFactoryBuilder sqlSessionFactoryBuilder = new SqlSessionFactoryBuilder();
            //获取SqlSessionFactory对象
        SqlSessionFactory  sqlSessionFactory = sqlSessionFactoryBuilder.build(is);
        //获取Slsession
        SqlSession sqlSession = sqlSessionFactory.openSession();
        //获取mapper接口对象
        UserMapper mapper = sqlSession.getMapper(UserMapper.class);
        int result = mapper.insertUser();
        sqlSession.commit();
        System.out.println("result:"+result);
```

# 起别名

<properties>设置其他文件的变量

![image-20220720041136663](G:/TY/MyBatis/image-20220720041136663.png)

# 获取参数的两种方式以及各种情况

- ${} 本质字符串拼接
- #{}本质占位符赋值

在jdbc中，字符串拼接，占位符?赋值

MyBatis获取参数值的各种情况：

- mapper接口方法的参数为单个的字面量类型

  - #直接用
  - $加单引号
  - 可以通过${}和#{}鲁以任意的名称获取参数值，但是需要注意的单引号问题

- mapper接口方法参数为多个时

  此时MyBatis会将这些参数放在一个map集合中，以两种方式进行存储a>以arg0, arg1...为键，以参数为值
  b>以param1 , param2...为键，以参数为值
  因此只需要通过#{}和\${}以键的方式访问值即可，但是需要注意\${}的单引号问题![image-20220721003010562](G:/TY/MyBatis/image-20220721003010562.png)![image-20220721003050032](G:/TY/MyBatis/image-20220721003050032.png)![image-20220721003319626](G:/TY/MyBatis/image-20220721003319626.png)

- 也可以直接传入一个map
  只需要通过#{}和鲁以键的方式访问值即可，但是需要注意$的单引号问题

- 传入实例对象

  只需要通过#秘鲁以属性的方式访问属性值即可，但是需要注意$的单引号问题![image-20220721041047023](G:/TY/MyBatis/image-20220721041047023.png)

- 使用@Param
             此时MyBatis会将这些参数放在一个map集合中，以两种方式进行存储

  - a>以@Param注解的值为键，以参数为值
  - b>以param1 , param2...为键，以参数为值

# MyBatis的各种查询功能

![image-20220723205006416](G:/TY/MyBatis/image-20220723205006416.png)

## 默认类型别名

![image-20220723223536105](G:/TY/MyBatis/image-20220723223536105.png)

![image-20220723222733694](G:/TY/MyBatis/image-20220723222733694.png)

## Map

- 以字段为键，以值为值![image-20220723223717440](G:/TY/MyBatis/image-20220723223717440.png)
- 可以在Mapper接口的方法上添加@MapKey注解

# MyBatis处理模糊查询



```java
/**
 * 根据用户名进行模糊查询
 * @param username 
 * @return java.util.List<com.atguigu.mybatis.pojo.User>
 * @date 2022/2/26 21:56
 */
List<User> getUserByLike(@Param("username") String username);
```

```xml
<!--List<User> getUserByLike(@Param("username") String username);-->
<select id="getUserByLike" resultType="User">
	<!--select * from t_user where username like '%${mohu}%'-->  
	<!--select * from t_user where username like concat('%',#{mohu},'%')-->  
	select * from t_user where username like "%"#{mohu}"%"
</select>
```

- 其中`select * from t_user where username like "%"#{mohu}"%"`是最常用的

# 批量删除

- 只能使用\${}，如果使用#{}，则解析后的sql语句为`delete from t_user where id in ('1,2,3')`，这样是将`1,2,3`看做是一个整体，只有id为`1,2,3`的数据会被删除。正确的语句应该是`delete from t_user where id in (1,2,3)`，或者`delete from t_user where id in ('1','2','3')`

```java
/**
 * 根据id批量删除
 * @param ids 
 * @return int
 * @date 2022/2/26 22:06
 */
int deleteMore(@Param("ids") String ids);
```

```xml
<delete id="deleteMore">
	delete from t_user where id in (${ids})
</delete>
```

```java
//测试类
@Test
public void deleteMore() {
	SqlSession sqlSession = SqlSessionUtils.getSqlSession();
	SQLMapper mapper = sqlSession.getMapper(SQLMapper.class);
	int result = mapper.deleteMore("1,2,3,8");
	System.out.println(result);
}
```

# 动态设置表名

- 只能使用${}，因为表名不能加单引号

```java
/**
 * 查询指定表中的数据
 * @param tableName 
 * @return java.util.List<com.atguigu.mybatis.pojo.User>
 * @date 2022/2/27 14:41
 */
List<User> getUserByTable(@Param("tableName") String tableName);
```

```xml
<!--List<User> getUserByTable(@Param("tableName") String tableName);-->
<select id="getUserByTable" resultType="User">
	select * from ${tableName}
</select>
```

# 添加功能获取自增的主键

- 使用场景

 - t_clazz(clazz_id,clazz_name)  

   - t_student(student_id,student_name,clazz_id)  

   1. 添加班级信息  
   2. 获取新添加的班级的id  
   3. 为班级分配学生，即将某学的班级id修改为新添加的班级的id

- 在mapper.xml中设置两个属性

 - useGeneratedKeys：设置使用自增的主键  

   * keyProperty：因为增删改有统一的返回值是受影响的行数，因此只能将获取的自增的主键放在传输的参数user对象的某个属性中

```java
/**
 * 添加用户信息
 * @param user 
 * @date 2022/2/27 15:04
 */
void insertUser(User user);
```

```xml
<!--void insertUser(User user);-->
<insert id="insertUser" useGeneratedKeys="true" keyProperty="id">
	insert into t_user values (null,#{username},#{password},#{age},#{sex},#{email})
</insert>
```

```java
//测试类
@Test
public void insertUser() {
	SqlSession sqlSession = SqlSessionUtils.getSqlSession();
	SQLMapper mapper = sqlSession.getMapper(SQLMapper.class);
	User user = new User(null, "ton", "123", 23, "男", "123@321.com");
	mapper.insertUser(user);
	System.out.println(user);
	//输出：user{id=10, username='ton', password='123', age=23, sex='男', email='123@321.com'}，自增主键存放到了user的id属性中
}
```

