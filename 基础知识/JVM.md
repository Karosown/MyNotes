> 本文原创，内容结合视频 [黑马程序员JVM完整教程，Java虚拟机快速入门，全程干货不拖沓*哔哩哔哩*bilibili](https://www.bilibili.com/video/BV1yE411Z7AP/) 和 周志明 - 《深入理解Java虚拟机》而作，同步发于个人博客：[JVM-内存结构篇 - Karos (wzl1.top)](https://www.wzl1.top/2023/02/jvm-内存结构篇/) 与 腾讯云开发者社区：[JVM-内存结构篇笔记 - 腾讯云开发者社区-腾讯云 (tencent.com)](https://cloud.tencent.com/developer/article/2217584)，部分图片来源已添加链接

# JVM

其他文章：

[jvm参数优化 - 知乎 (zhihu.com)](https://zhuanlan.zhihu.com/p/78699741#:~:text=1 对于布尔类型的参数，我们有”%2B”或”-“，然后才设置JVM选项的实际名称。 例如，-XX%3A%2B用于激活选项，而-XX%3A-用于注销选项。,2 对于需要非布尔值的参数，如string或者integer，我们先写参数的名称，后面加上”%3D”，最后赋值。 例如， -XX%3A%3D给赋值。)

# Java 内存区域与内存溢出异常

## 运行时数据区域

![image-20230122220140540](http://gd.7n.cdn.wzl1.top/typora/img/image-20230122220140540.png)

## 程序计数器（PCR）

==记录下一条指令的地址==

PCR是一个较小的内存空间，可以看作是当前线程所执行的字节码的行号指示器（==不会存在内存溢出==）。

在JVM的概念模型中，字节码解释器工作时就是通过改变PCR的值来选取下一条需要执行的字节码指令，是程序控制流的指示器，分支、循环、跳转、异常处理、线程恢复等基本功能都依赖这个计数器来完成。

> 程序控制流：
>
> 控制流是指**按一定的顺序排列程序元素来决定程序执行的顺序**。

由于JVM的多线程是通过线程轮流替换、分配处理器执行时间的方式（**抢占式调度方式**）来实现，因此在任意一个确定时刻，一个处理器或一个内核都只会执行一条线程中的指令。

> [Java的JVM虚拟机线程调度和进程调度方式 - 简书 (jianshu.com)](https://www.jianshu.com/p/6b3c206bfab0)

为了保证线程切换后能回到正确的执行位置，所以每条线程都要有一条独立的程序计数器，互不影响、独立存储，称这块区域为线程的私有内存。

如果线程正在执行的是一个Java方法，那么PCR记录的是正在执行的虚拟机字节码指令的字节地址

如果是个本地方法，则为空

> [(78条消息) Java本地方法调用_外星喵的博客-CSDN博客_本地方法的调用](https://blog.csdn.net/c15158032319/article/details/117703519)

## Java虚拟机栈

==线程运行时需要的内存空间==

生命周期与线程相同，描述的是Java方法执行的线程内存模型：每个方法被执行的时候，Java虚拟机都会同步创建一个栈帧用于存储局部变量表、操作数栈、动态连接、方法出口等信息。

<img src="http://gd.7n.cdn.wzl1.top/typora/img/image-20230214061143920.png" style="zoom:33%;" /><img src="http://gd.7n.cdn.wzl1.top/typora/img/image-20230214061329499.png" alt="image-20230214061329499" style="zoom: 33%;" />

>- 垃圾回收是否设计栈内存？
>  不涉及，只涉及堆内存
>
>- 占内存分配越大越好吗？
>
>```bash
>-Xss1m #通过-Xss来设置栈内存，Linux/x64、macOs、OracleSolaris/x64 默认为1024KB=1m，Windows会根据虚拟内存影响栈的大小
>```
>
>唯一的好处就是增大方法的递归调用，以及间接调用，明显的坏处就是减少线程数。
>
>- 方法内的局部变量是否线程安全？
>  由于Java虚拟机栈线程隔离，而一个方法的局部变量存放于栈帧中，所以线程安全，如果是共享变量（静态变量），那么线程不安全
>   <img src="http://gd.7n.cdn.wzl1.top/typora/img/image-20230214062826308.png" alt="image-20230214062826308" style="zoom: 33%;" /><img src="http://gd.7n.cdn.wzl1.top/typora/img/image-20230214063026191.png" alt="image-20230214063026191" style="zoom:33%;" />
>
>特殊情况：
>
><img src="http://gd.7n.cdn.wzl1.top/typora/img/image-20230214063346572.png" alt="image-20230214063346572" style="zoom:33%;" />
>
>**如果方法内局部变量没有逃离方法的作用范围，它是线程安全的**
>
>**如果局部变量引用了对象，并逃离方法的作用范围，需要考虑线程安全问题**

### 局部变量表

存放编译器可知的各种JVM基本数类型、对象引用和returnAddress类型(指向了一条字节码指令的地址)，存储空间以局部变量槽(Slot)表示，long和double占2个slot。

表所需的内存空间在编译期间完成分配，当进入一个方法时，这个方法需要在栈帧中分配的局部空间大小完全确定，**在方法运行期间不会改变局部变量表的大小（slot的数量）**

### 栈内存溢出

- 栈帧过多导致栈内存溢出

  - 递归调用

  <img src="http://gd.7n.cdn.wzl1.top/typora/img/image-20230214065751022.png" alt="image-20230214065751022" style="zoom: 50%;" /><img src="http://gd.7n.cdn.wzl1.top/typora/img/image-20230214065809203.png" alt="image-20230214065809203" style="zoom:50%;" />

  - 循环依赖

  <img src="http://gd.7n.cdn.wzl1.top/typora/img/image-20230214070242836.png" alt="image-20230214070242836" style="zoom: 80%;" />

  > 解决方法：@JsonIgnore
  >
  > Spring 通过三级缓存解决循环依赖

- 栈帧过大导致栈内存溢出

### 线程运行诊断

*案例1: cpu占用过多*

定位：

- 用top命令定位那个进程对cpu的占用过高

- ps H -eo pid，%cpu | grep 进程id 

  #用ps命令进一步定位是哪一个线程引起的

- jstack 进程id

![img](http://gd.7n.cdn.wzl1.top/typora/img/image-20230214163118421.png)

>jstack中：
>
>tid: java内的线程id
>
>nid: 操作系统级别线程的线程id
>
>prio: java内定义的线程的优先级
>
>os_prio:操作系统级别的优先级
>
>
>操作系统中：
>
>- **pid**: 进程ID。
>- **lwp**: 线程ID。在用户态的命令(比如ps)中常用的显示方式。
>- **tid**: 线程ID，等于lwp。tid在系统提供的接口函数中更常用，比如syscall(SYS_gettid)和syscall(__NR_gettid)。
>- **tgid**: 线程组ID，也就是线程组leader的进程ID，等于pid。
>- ------分割线------
>- **pgid**: 进程组ID，也就是进程组leader的进程ID。
>- **pthread id**: pthread库提供的ID，生效范围不在系统级别，可以忽略。
>- **sid**: session ID for the session leader。
>- **tpgid**: tty process group ID for the process group leader。

- windows: tasklist

- 无结果（线程死锁）

  - jstack 进程id

    ![image-20230214172749657](http://gd.7n.cdn.wzl1.top/typora/img/image-20230214172749657.png)

## 本地方法栈

本地方法：由C/C++等与操作系统打交道的语言编写

与VM Stack类似，区别在于服务对象不同，NMS为JVM用到的本地方法服务

## Java堆

Java Heap是虚拟机所管理的内存中最大的一块，是被所有线程共享的一块内存区域，在虚拟机启动时创建。

**唯一目的：存放对象实例**

从分配内存的角度看，所有线程共享的Java堆中可以划分出多个线程私有的分配缓冲区（TLAB），提升对象分配时的效率。

**Java Heap在物理上可以不连续，但在逻辑上应该连续**

可以通过设置参数-Xmx和-Xms设定Java Heap是固定大小还是可扩展

>* 通过new关键字，创建对象都会使用堆内存
>* 线程共享，堆中的 对象都需要考虑线程安全问题
>* 有垃圾回收机制

### 堆内存溢出

<img src="http://gd.7n.cdn.wzl1.top/typora/img/image-20230214183001646.png" alt="image-20230214183001646" style="zoom: 50%;" /><img src="http://gd.7n.cdn.wzl1.top/typora/img/image-20230214183050934.png" alt="image-20230214183050934" style="zoom:50%;" />

**成次幂**

### 堆内存诊断

工具>>

![image-20230214184717834](http://gd.7n.cdn.wzl1.top/typora/img/image-20230214184717834.png)

示例：<img src="http://gd.7n.cdn.wzl1.top/typora/img/image-20230214194854017.png" alt="image-20230214194854017" style="zoom: 33%;" />

#### JMAP

<img src="http://gd.7n.cdn.wzl1.top/typora/img/image-20230214195449267.png" alt="image-20230214195449267" style="zoom: 33%;" />

```bash
jmap -heap PID
```

<img src="http://gd.7n.cdn.wzl1.top/typora/img/image-20230214195729993.png" alt="image-20230214195729993" style="zoom: 33%;" /><img src="http://gd.7n.cdn.wzl1.top/typora/img/image-20230214195847722.png" alt="image-20230214195847722" style="zoom: 33%;" />

左：堆配置				右：堆内存占用情况

#### JConsole

![image-20230214205736847](http://gd.7n.cdn.wzl1.top/typora/img/image-20230214205736847.png)

#### JvirsualVm

![image-20230215031704078](http://gd.7n.cdn.wzl1.top/typora/img/image-20230215031704078.png)

具有堆转储功能(堆Dump)，截取快照

## 方法区

==所有Java虚拟机线程共享的区域==

存储和类相关的信息、成员方法、构造器方法、特殊方法

创建时期：虚拟机被启动时，逻辑上是堆的一部分，但事实不一定。

方法区的实现：永久代（**hotspot** JDK1.8以前）、元空间（操作系统内存）

![image-20230215045133801](http://gd.7n.cdn.wzl1.top/typora/img/image-20230215045133801.png)

如果申请内存时发现内存不住，也会发出outofMemoryError异常

**线程共享**的内存区域，用于存储已被虚拟机加载的类型模型、final、static、即时编译器编译后的代码缓存

### 内存溢出

![image-20230215050458867](http://gd.7n.cdn.wzl1.top/typora/img/image-20230215050458867.png)

元空间默认情况下使用系统内存，无上限 

```bash
#设定原空间内存溢出
-XX:MaxMetaspaceSize=XXX
#设置永久代内存溢出
-XX:MaxPermSize=XXX
```

![image-20230215052109506](http://gd.7n.cdn.wzl1.top/typora/img/image-20230215052109506.png)

![image-20230215052551991](http://gd.7n.cdn.wzl1.top/typora/img/image-20230215052551991.png)

*场景：*

Cglib

- Spring
- MyBatis

### 常量池

Java堆类进行编译时，会产生二进制字节码，包含类基本信息、常量池、类方法定义（包含了虚拟机指令）

![image-20230215055456838](http://gd.7n.cdn.wzl1.top/typora/img/image-20230215055456838.png)

- 通过javap指令对.class文件进行反编译

  类的基本信息

  ![image-20230215055435608](http://gd.7n.cdn.wzl1.top/typora/img/image-20230215055435608.png)

类的方法定义

- 无参构造

  <img src="http://gd.7n.cdn.wzl1.top/typora/img/image-20230215055625190.png" alt="image-20230215055625190" style="zoom: 50%;" />

- main方法

  ![image-20230215055823167](http://gd.7n.cdn.wzl1.top/typora/img/image-20230215055823167.png)

- 常量池

  ![image-20230215063424268](http://gd.7n.cdn.wzl1.top/typora/img/image-20230215063424268.png)

### 运行时常量池（Runtime Constant Pool）

从上面可以看出，常量池其实就是一张表，虚拟机指令根据这张常量表找到要执行的类名、方法铭、参数类型、字面量等信息

RCP是方法区的一部分，常量池是*.class文件中的，当该类被加载，它的常量池信息就会背放入运行是常量池，并把里面的符号变为真实地址。

Class文件中除了有类的版本、字段、方法、接口等描述信息外，还有一项信息时常量池表（Constant Pool Table），CPT用于存放你编译器生成的各种字面量与符号引用，这部分内容将在类加载后存放到方法区的运行时常量池中。

RCP相对于Class文件常量池的另外一个重要特征：**动态性**

Java并不要求常量一定要在编译器才能产生，也就是说并非预置入Class文件中的常量池的内容才能进入方法区运行常量池，运行期间也可以将新的常量放入池中，比如String::inter

> :tada:String::inter()
>
> ![image-20230122235442146](http://gd.7n.cdn.wzl1.top/typora/img/image-20230122235442146.png)
>
> 会在常量池中寻找字符串

#### StringTable

![image-20230215161421366](http://gd.7n.cdn.wzl1.top/typora/img/image-20230215161421366.png)

使用变量相加，由于是变量，所以不会在编译期间优化

#### 特性

- 常量池中的字符串仅是符号，第一次使用到时才变为对象

- 利用串池机制，避免重复创建字符串对象

- 字符串变量拼接的原理是StringBuilder(1.8).append

- 字符串常量拼接的原理是**编译器优化**

- 可以使用intern方法，主动将串池中还没有的字符串对象放入串池

  - 1.8 将这个字符串对象尝试放入串池，如果有则不会放入，如果没有则会放入串池，并返回![image-20230215163719209](http://gd.7n.cdn.wzl1.top/typora/img/image-20230215163719209.png)

    >true
    >
    >true

     ![image-20230215163710427](http://gd.7n.cdn.wzl1.top/typora/img/image-20230215163710427.png)

  - 1.6 将这个字符串对象尝试放入串池，如果有则不会放入，如果没有则会把这个对象赋值一份，放入串池，会把串池中的对象返回![image-20230215180145104](http://gd.7n.cdn.wzl1.top/typora/img/image-20230215180145104.png)

**面试题**

![image-20230215071801534](http://gd.7n.cdn.wzl1.top/typora/img/image-20230215071801534.png)

#### 位置

1.8之前，放在方法去永久代中，而1.7 1.8之后放在堆里面

![image-20230215181821229](http://gd.7n.cdn.wzl1.top/typora/img/image-20230215181821229.png)

#### 垃圾回收

![image-20230215184329180](http://gd.7n.cdn.wzl1.top/typora/img/image-20230215184329180.png)允许使用限制JVM在gc上的时间比例的策略。
异常抛出，默认启用此选项，如果超过98%的时间用于垃圾收集，则并行GC将抛出一个outofMemoryError
并且不到2%的堆被恢复。当堆很小时，这个特性可以用来防止应用程序长时间运行，而不会有什么进展。若要禁用此选项，
指定选项-xx：-UseGCOverheadLimit

```bash
-XX:+PrintStringTavleStatistics #打印字符串表的统计信息
#打印垃圾回收的详细信息
-XX:+PrintGcDetails
-vaerbase:gc
```

![image-20230215201809922](http://gd.7n.cdn.wzl1.top/typora/img/image-20230215201809922.png)

![image-20230215201904326](http://gd.7n.cdn.wzl1.top/typora/img/image-20230215201904326.png)

类名、方法名也是以字符串常量的形式存储在JVM中，当内存空间不足，内存分配失败时会进行垃圾回收

![image-20230216024912772](http://gd.7n.cdn.wzl1.top/typora/img/image-20230216024912772.png)

#### 调优

**修改哈希桶个数，改变存储、查询时间**

```bash
-XX:StringTableSize=xxxx #设置StringTable哈希表 桶的个数
```

**考虑将字符串对象是否入池**

## 直接内存

直接内存（Direct Memory）并不是虚拟机运行时数据区的一部分，也不是《JVM》规范中定义的内存区域。

在JDK 1.4中新加入了**NIO类**，引入了一种**基于通道（Channel）与缓冲区（Buffer）**的I/O方式，可以使用Native函数库直接分配**堆外内存**，然后通过一个存储在Java堆里面的**DirectByteBuffer对象**作为这块内存的引用操作，避免了在Java Heap和Native Heap中**来回复制数据**。

**直接内存属于系统内存**

- 常见于NIO操作、用于数据缓冲区
- 分配回收成本高，但读写性能强
- 不受JVM内存回收管理

**传统IO**

<img src="http://gd.7n.cdn.wzl1.top/typora/img/image-20230216040231966.png" alt="image-20230216040231966" style="zoom: 50%;" />

**直接内存**

ByteBuffer.allocateDirect(_1Mb); //分配直接内存

![image-20230216040351823](http://gd.7n.cdn.wzl1.top/typora/img/image-20230216040351823.png)

### 直接内存溢出

![image-20230216040900894](http://gd.7n.cdn.wzl1.top/typora/img/image-20230216040900894.png)

### 直接内存释放

```bash
-XX:MaxDirectMemorySize=XXX #设置直接内存大小
```

![image-20230216043702897](http://gd.7n.cdn.wzl1.top/typora/img/image-20230216043702897.png)

![image-20230216043657291](http://gd.7n.cdn.wzl1.top/typora/img/image-20230216043657291.png)

Cleaner （虚引用类型）当前对象被回收时会执行一个回调方法

![image-20230216052204620](http://gd.7n.cdn.wzl1.top/typora/img/image-20230216052204620.png)

> [阿里面试： 说说强引用、软引用、弱引用、虚引用吧 - 腾讯云开发者社区-腾讯云 (tencent.com)](https://cloud.tencent.com/developer/article/1632413#:~:text=虚引用，顾名思义，得一个对象实例。)
>
> 强：打死都不删除
>
> 软：容量不够了再删，前提是所引用的对象没有被强应用
>
> 弱：有gc就删，前提是所引用的对象没有被强应用或者软引用
>
> 虚：只有需引用时随时都可以被删（**设置虚引用的唯一目的，就是在这个对象被回收器回收的时候收到一个系统通知或者后续添加进一步的处理**）
>
> ![img](https://ask.qcloudimg.com/http-save/yehe-2520554/9yvnwbzri2.jpeg?imageView2/2/w/1620)

![image-20230216052441677](http://gd.7n.cdn.wzl1.top/typora/img/image-20230216052441677.png)

![image-20230216052423752](http://gd.7n.cdn.wzl1.top/typora/img/image-20230216052423752.png)

clean()方法在后台的RefenceHandler线程中检测虚引用对象，一旦虚引用对象关联的实际对象被回收掉后，就会执行clean方法

**分配和回收原理：![image-20230216053629422](http://gd.7n.cdn.wzl1.top/typora/img/image-20230216053629422.png)**

### 禁用显示回收对直接内存的影响

```bash
-XX:+DisableExplicitGC #显示的，禁用System.gc();
```

被禁用后对直接内存使用回调影响：无法手动进行垃圾回收，导致长时间占用直接内存

解决：

- 手动使用unsafe.fereeMemory

## HotSpot虚拟机对象揭秘

### 对象的创建

当虚拟机遇见一条字节码new指令，首先会检查这个指令的参数是否能在常量池中定位到一个类的符号引用，并且检查这个符号引用的类是否已被加载、解析、初始化过。如果没有，那必须先执行相应的类加载过程。

![HotSpotCreateObject](http://gd.7n.cdn.wzl1.top/typora/img/HotSpotCreateObject.png?dNow)

### HotSpot解释器代码片段

```c++
// 确保常量池中存放的是已解释的类  
if (!constants->tag_at(index).is_unresolved_klass()) {  
  // 断言确保是klassOop和instanceKlassOop（这部分下一节介绍）  
  oop entry = (klassOop) *constants->obj_at_addr(index);  
  assert(entry->is_klass()， "Should be resolved klass");  
  klassOop k_entry = (klassOop) entry;  
  assert(k_entry->klass_part()->oop_is_instance()， "Should be instanceKlass");  
  instanceKlass* ik = (instanceKlass*) k_entry->klass_part();  
  // 确保对象所属类型已经经过初始化阶段  
  if ( ik->is_initialized() && ik->can_be_fastpath_allocated() ) {  
    // 取对象长度  
    size_t obj_size = ik->size_helper();  
    oop result = NULL;  
    // 记录是否需要将对象所有字段置零值  
    bool need_zero = !ZeroTLAB;  
    // 是否在TLAB中分配对象  
    if (UseTLAB) {  
      result = (oop) THREAD->tlab().allocate(obj_size);  
    }  
    if (result == NULL) {  
      need_zero = true;  
      // 直接在eden中分配对象  
retry:  
      HeapWord* compare_to = *Universe::heap()->top_addr();  
      HeapWord* new_top = compare_to + obj_size;  
      // cmpxchg是x86中的CAS指令，这里是一个C++方法，通过CAS方式分配空间，并发失败的话，转到retry中重试直至成功分配为止  
      if (new_top <= *Universe::heap()->end_addr()) {  
        if (Atomic::cmpxchg_ptr(new_top， Universe::heap()->top_addr()， compare_to) != compare_to) {  
          goto retry;  
        }  
        result = (oop) compare_to;  
      }  
    }  
    if (result != NULL) {  
      // 如果需要，为对象初始化零值  
      if (need_zero ) {  
        HeapWord* to_zero = (HeapWord*) result + sizeof(oopDesc) / oopSize;  
        obj_size -= sizeof(oopDesc) / oopSize;  
        if (obj_size > 0 ) {  
          memset(to_zero， 0， obj_size * HeapWordSize);  
        }  
      }  
      // 根据是否启用偏向锁，设置对象头信息  
      if (UseBiasedLocking) {  
        result->set_mark(ik->prototype_header());  
      } else {  
        result->set_mark(markOopDesc::prototype());  
      }  
      result->set_klass_gap(0);  
      result->set_klass(k_entry);  
      // 将对象引用入栈，继续执行下一条指令  
      SET_STACK_OBJECT(result， 0);  
      UPDATE_PC_AND_TOS_AND_CONTINUE(3， 1);  
    }  
  }  
}  
```

# 垃圾回收

## 如何判断对象已死

### 引用计数法

早期Python虚拟机采用

当对象被引用时，引用计数器++，取消引用时--

无法解决问题：相互循环引用

![image-20230216184716527](http://gd.7n.cdn.wzl1.top/typora/img/image-20230216184716527.png)

### 可达性分析算法

通过一系列成为GC Roots的根对象作为起始节点集，从GCRoots开始向下搜索，搜索的路径为”引用链“，如果没有引用链，也就是不可达，说明对象不可能再被使用

在Java技术体系中，固定可作为GCRoots的对象包括：

- 在JVMStack中引用的对象
- 在方法区中类静态属性引用的变量
- 在方法区中常量引用的对象
- 在本地方法栈中JNI引用的对象
- JVM内部引用，基本数据类型对应的Class对象、常驻异常对象、系统类加载器
- 所有被同步锁所持有的对象
- 反应JVM内部情况的JMXBean、JVMTI中注册的回调、本地代码缓存等
- 根据所选择的垃圾收集器、当前回收区域，采用其他对象临时性的加入

#### MAT

```bash
jmap -dump:format=b，live，file=11.bin
```

![image-20230216202205427](http://gd.7n.cdn.wzl1.top/typora/img/image-20230216202205427.png)

![image-20230216202215970](http://gd.7n.cdn.wzl1.top/typora/img/image-20230216202215970.png)

![image-20230216202258353](http://gd.7n.cdn.wzl1.top/typora/img/image-20230216202258353.png)

### 引用

强、软（SoftReference）、弱（WeakReference）

软引用：

![image-20230217033930873](http://gd.7n.cdn.wzl1.top/typora/img/image-20230217033930873.png)

![image-20230217034307493](http://gd.7n.cdn.wzl1.top/typora/img/image-20230217034307493.png)

必须配合引用队列：虚（PPhantomRefernce）、终结（FinalReference）

终结：使用finallize方法

![image-20230217030503981](http://gd.7n.cdn.wzl1.top/typora/img/image-20230217030503981.png)

#### 引用队列关联

![image-20230217040646855](http://gd.7n.cdn.wzl1.top/typora/img/image-20230217040646855.png)

### 回收对象

利用可达性算法，不可达进行第一次标记。

标记后进行筛选：是否有必要执行finalize()方法，如果没有重写或者已经被调用过，那么就视为没有必要执行

如果有必要执行，那么会放入一个F-Queued的队列（引用队列）中，并在稍后由一条由虚拟机自动建立的，低调度优先级的Finalizer线程去执行他们的finalize()方法。

为了防止出现阻塞，F-Queue不会等待方法执行结束，否则会让其他对象永久处于等待，造成内存回收子系统 崩溃。

如果finalize()成功执行，重新于引用链上任一对象建立关系即可：比如把this赋值给某个类变量或者对象的成员变量，那么在第二次标记时，他将会被移出”即将回收“的集合。如果此时对象仍然没有逃脱，那么就要被回收了。

>Systen.gc()还是系统自动回收，都只会调用一次finalize()方法

### 回收方法区

主要回收两个内容：**废弃常量**和**不再使用类型**。

判断一个常量是否废弃相对简单：该常量曾经进入常量池中，但现在又没有一个对象的值和该常量相同，也就是说没有对象引用该常量，且虚拟机中也没有其他地方引用该字面量，则该常量可以被垃圾回收，清理出常量池。

类、方法、字段的符号引用也类似。

但是判断一个类是否废弃需要满足下面三个条件：

1. 该类的所有实例都已被回收（包括派生类）
2. 加载该类的类加载器已经被回收，这个条件除非是经过精心设计的可替换类加载器的场景，如OSGi、JSP的重加载等，否则通常很难达成
3. 该类对应的java.lang.Class对象没有在任何地方被引用，无法在任何地方通过u哦反射访问该类的方法。

对于是否被零星回收，HotSpot提供了 -Xnoclassgc 参数进行控制，还可以使用 -XX:+TraceClassLoading、-XX:TraceClassUnLoading查看类加载和写在信息（前两个可以在Product版JVM中使用，最后一种需要FastDebug版的JVM支持）。

## 垃圾回收算法

### 标记清除

![image-20230217043904028](http://gd.7n.cdn.wzl1.top/typora/img/image-20230217043904028.png)

- 优点：记录垃圾碎片的起止地址，快速，不用清零

- 缺点：碎片较多

  ![image-20230217044056312](http://gd.7n.cdn.wzl1.top/typora/img/image-20230217044056312.png)

### 标记整理

![image-20230217045512040](http://gd.7n.cdn.wzl1.top/typora/img/image-20230217045512040.png)

- 优点：没有内存碎片
- 缺点：速度慢

### 复制算法

![image-20230217045707833](http://gd.7n.cdn.wzl1.top/typora/img/image-20230217045707833.png)

- 优点：不会有内存碎片
- 需要占用双倍内存空间

## 分代回收

根据对象生命周期的不同特点分为新生代和老年代

新生代：处理不常用的对象

老年代：处理常用的对象

> 在新生代中，每次垃圾收集时都发现有大批对象死去，而每次回收后存活的少量对象将会逐步晋升到老年代释放

### 新生代垃圾回收

#### 第一次垃圾回收

![image-20230217062039111](http://gd.7n.cdn.wzl1.top/typora/img/image-20230217062039111.png)

新创建的对象选择伊甸园空间

![image-20230217065534758](http://gd.7n.cdn.wzl1.top/typora/img/image-20230217065534758.png)

当伊甸园空间满了以后会进行垃圾护手，新生代的垃圾回收叫做Minor GC，回收后将存活对象赋值到幸存区，并将寿命+1

![image-20230217074310165](http://gd.7n.cdn.wzl1.top/typora/img/image-20230217074310165.png)

再将to和form交换指向位置

![image-20230217074458806](http://gd.7n.cdn.wzl1.top/typora/img/image-20230217074458806.png)

#### 第二次垃圾回收

在第一次的回收基础上，对幸存区的某一个进行取消引用

![image-20230217075326558](http://gd.7n.cdn.wzl1.top/typora/img/image-20230217075326558.png)

![image-20230217075440697](http://gd.7n.cdn.wzl1.top/typora/img/image-20230217075440697.png)

此时to中的1已经被取消引用，所以直接删除

![image-20230217075630003](C:/Users/30398/AppData/Roaming/Typora/typora-user-images/image-20230217075630003.png)

最后from和to交换位置、放入新对象

![image-20230217075721298](http://gd.7n.cdn.wzl1.top/typora/img/image-20230217075721298.png)

当年龄达到一定阙值后放入老年代

### 老年代垃圾回收

![image-20230217171031347](http://gd.7n.cdn.wzl1.top/typora/img/image-20230217171031347.png)

当新生代内存不够时，会尝试促发FullGC进行老年代的垃圾回收，准确来说是整个堆空间

![1](https://images0.cnblogs.com/blog/587773/201409/061921034534396.png)

### 总结

- 对象首先非陪在伊甸园区域
- 新生代空间不足时，出发MinorGC，伊甸园和from存活的对象使用copy复制到to中，存活的对象年龄+1并且交换fromto
- Minor GC 会引发 stop the world （暂停 其他用户正在执行的线程 wait，等垃圾回收结束后，用户线程才恢复运行 notifyAll）   
- 当对象寿命超过阙值时，会晋升至老年代，最大寿命是15（4bit，但是不同的垃圾回收器不同）
- 当老年代空间不足，会先尝试促发MinorGC，如果空间仍不足，那么触发Full GC，STW暂停时间（老年代回收时间）更长

## GC相关参数

![image-20230217180038687](http://gd.7n.cdn.wzl1.top/typora/img/image-20230217180038687.png)

## GC分析

```bash
-Xms20M -Xmx20M -Xmn10M -XX:+UseSerialGC -XX:+PrintGCDetails -verbose:gc
```

代码：

```java
package org.example;

import java.io.IOException;

public class Main {
    public static int a=5;
    public static void main(String[] args) throws InterruptedException {
        Thread.sleep(10000000000l);
        return ;
    }
}
```

E:F:T=8:1:1，其中T默认是已使用内存

![此图没有使用Thread.sleep()方法仍然有Eden占用情况](http://gd.7n.cdn.wzl1.top/typora/img/image-20230217183029649.png)

![image-20230218055700061](http://gd.7n.cdn.wzl1.top/typora/img/image-20230218055700061.png)

既然刚启动，那为什么EdenSpace会有占用呢？

- 类加载过程

  ![image-20230218060431033](http://gd.7n.cdn.wzl1.top/typora/img/image-20230218060431033.png)

> 思考：当new的对象占用内存如果超过了幸存区内存会发生什么？内存溢出？动态占比分配？
>
> 试验代码:
>
> ```java
> package org.example;
> 
> import java.io.IOException;
> import java.lang.reflect.Array;
> import java.util.ArrayList;
> 
> public class Main {
>     public static int a=5;
>     public static void main(String[] args) throws InterruptedException， IOException {
>         System.in.read();
>         ArrayList<byte[]> bytes=new ArrayList<>();
>         System.out.println("添加对象中");
>         bytes.add(new byte[1024*1024*5]);
>         bytes.forEach(System.out::println);
> 
>         System.in.read();
>         bytes.add(new byte[1024*1024*5]);
> 
>         bytes.forEach(System.out::println);
>         System.in.read();
>         bytes.add(new byte[1024*1024*5]);
>         bytes.forEach(System.out::println);
>         bytes.add(new byte[1024*1024*5]);
> 
>         bytes.forEach(System.out::println);
>         return ;
>     }
> }
> ```
>
> 结果:
>
> ```
> D:\Program\jdk\bin\java.exe -Xms20M -Xmx20M -Xmn10M -XX:+UseSerialGC -XX:+PrintGCDetails -verbose:gc "-javaagent:D:\Program\IntelliJ IDEA 2022.3.1\lib\idea_rt.jar=61656:D:\Program\IntelliJ IDEA 2022.3.1\bin" -Dfile.encoding=UTF-8 -classpath H:\JVM\gc\target\classes org.example.Main
> [0.003s][warning][gc] -XX:+PrintGCDetails is deprecated. Will use -Xlog:gc* instead.
> [0.010s][info   ][gc] Using Serial
> [0.010s][info   ][gc，init] Version: 17.0.1+12-39 (release)
> [0.010s][info   ][gc，init] CPUs: 8 total， 8 available
> [0.010s][info   ][gc，init] Memory: 13810M
> [0.010s][info   ][gc，init] Large Page Support: Disabled
> [0.010s][info   ][gc，init] NUMA Support: Disabled
> [0.010s][info   ][gc，init] Compressed Oops: Enabled (32-bit)
> [0.010s][info   ][gc，init] Heap Min Capacity: 20M
> [0.010s][info   ][gc，init] Heap Initial Capacity: 20M
> [0.010s][info   ][gc，init] Heap Max Capacity: 20M
> [0.010s][info   ][gc，init] Pre-touch: Disabled
> [0.010s][info   ][gc，metaspace] CDS archive(s) mapped at: [0x0000000800000000-0x0000000800bc0000-0x0000000800bc0000)， size 12320768， SharedBaseAddress: 0x0000000800000000， ArchiveRelocationMode: 0.
> [0.011s][info   ][gc，metaspace] Compressed class space mapped at: 0x0000000800c00000-0x0000000840c00000， reserved size: 1073741824
> [0.011s][info   ][gc，metaspace] Narrow klass base: 0x0000000800000000， Narrow klass shift: 0， Narrow klass range: 0x100000000
> [10.160s][info   ][gc，start    ] GC(0) Pause Young (Allocation Failure)
> [10.165s][info   ][gc，heap     ] GC(0) DefNew: 8192K(9216K)->1023K(9216K) Eden: 8192K(8192K)->0K(8192K) From: 0K(1024K)->1023K(1024K)
> [10.165s][info   ][gc，heap     ] GC(0) Tenured: 0K(10240K)->1443K(10240K)
> [10.165s][info   ][gc，metaspace] GC(0) Metaspace: 2679K(2880K)->2679K(2880K) NonClass: 2400K(2496K)->2400K(2496K) Class: 279K(384K)->279K(384K)
> [10.165s][info   ][gc          ] GC(0) Pause Young (Allocation Failure) 8M->2M(19M) 4.780ms
> [10.165s][info   ][gc，cpu      ] GC(0) User=0.00s Sys=0.02s Real=0.01s
> [10.384s][info   ][gc，start    ] GC(1) Pause Young (Allocation Failure)
> [10.390s][info   ][gc，heap     ] GC(1) DefNew: 9215K(9216K)->482K(9216K) Eden: 8192K(8192K)->0K(8192K) From: 1023K(1024K)->482K(1024K)
> [10.390s][info   ][gc，heap     ] GC(1) Tenured: 1443K(10240K)->2463K(10240K)
> [10.390s][info   ][gc，metaspace] GC(1) Metaspace: 4881K(5120K)->4881K(5120K) NonClass: 4357K(4480K)->4357K(4480K) Class: 523K(640K)->523K(640K)
> [10.390s][info   ][gc          ] GC(1) Pause Young (Allocation Failure) 10M->2M(19M) 5.666ms
> [10.390s][info   ][gc，cpu      ] GC(1) User=0.00s Sys=0.00s Real=0.01s
> [26.252s][info   ][gc，start    ] GC(2) Pause Full (System.gc())
> [26.252s][info   ][gc，phases，start] GC(2) Phase 1: Mark live objects
> [26.257s][info   ][gc，phases      ] GC(2) Phase 1: Mark live objects 5.380ms
> [26.257s][info   ][gc，phases，start] GC(2) Phase 2: Compute new object addresses
> [26.259s][info   ][gc，phases      ] GC(2) Phase 2: Compute new object addresses 2.149ms
> [26.259s][info   ][gc，phases，start] GC(2) Phase 3: Adjust pointers
> [26.262s][info   ][gc，phases      ] GC(2) Phase 3: Adjust pointers 3.118ms
> [26.263s][info   ][gc，phases，start] GC(2) Phase 4: Move objects
> [26.263s][info   ][gc，phases      ] GC(2) Phase 4: Move objects 0.855ms
> [26.264s][info   ][gc，heap        ] GC(2) DefNew: 8083K(9216K)->0K(9216K) Eden: 7600K(8192K)->0K(8192K) From: 482K(1024K)->0K(1024K)
> [26.264s][info   ][gc，heap        ] GC(2) Tenured: 2463K(10240K)->3900K(10240K)
> [26.264s][info   ][gc，metaspace   ] GC(2) Metaspace: 7956K(8256K)->7956K(8256K) NonClass: 7083K(7232K)->7083K(7232K) Class: 872K(1024K)->872K(1024K)
> [26.264s][info   ][gc             ] GC(2) Pause Full (System.gc()) 10M->3M(19M) 12.256ms
> [26.264s][info   ][gc，cpu         ] GC(2) User=0.02s Sys=0.00s Real=0.01s
> 
> 添加对象中
> [B@3b07d329
> [49.675s][info   ][gc，start       ] GC(3) Pause Young (Allocation Failure)
> [49.678s][info   ][gc，heap        ] GC(3) DefNew: 8192K(9216K)->35K(9216K) Eden: 8192K(8192K)->0K(8192K) From: 0K(1024K)->35K(1024K)
> [49.678s][info   ][gc，heap        ] GC(3) Tenured: 3900K(10240K)->9020K(10240K)
> [49.678s][info   ][gc，metaspace   ] GC(3) Metaspace: 8137K(8384K)->8137K(8384K) NonClass: 7263K(7360K)->7263K(7360K) Class: 874K(1024K)->874K(1024K)
> [49.678s][info   ][gc             ] GC(3) Pause Young (Allocation Failure) 11M->8M(19M) 2.957ms
> [49.678s][info   ][gc，cpu         ] GC(3) User=0.00s Sys=0.02s Real=0.00s
> 
> 
> [96.458s][info   ][gc，start       ] GC(4) Pause Young (Allocation Failure)
> [96.458s][info   ][gc             ] GC(4) Pause Young (Allocation Failure) 14M->14M(19M) 0.078ms
> [96.458s][info   ][gc，cpu         ] GC(4) User=0.00s Sys=0.00s Real=0.00s
> [96.458s][info   ][gc，start       ] GC(5) Pause Full (Allocation Failure)
> [96.458s][info   ][gc，phases，start] GC(5) Phase 1: Mark live objects
> [96.467s][info   ][gc，phases      ] GC(5) Phase 1: Mark live objects 8.235ms
> [96.467s][info   ][gc，phases，start] GC(5) Phase 2: Compute new object addresses
> [96.469s][info   ][gc，phases      ] GC(5) Phase 2: Compute new object addresses 2.466ms
> [96.469s][info   ][gc，phases，start] GC(5) Phase 3: Adjust pointers
> [96.473s][info   ][gc，phases      ] GC(5) Phase 3: Adjust pointers 4.087ms
> [96.473s][info   ][gc，phases，start] GC(5) Phase 4: Move objects
> [96.474s][info   ][gc，phases      ] GC(5) Phase 4: Move objects 0.946ms
> [96.475s][info   ][gc，heap        ] GC(5) DefNew: 5571K(9216K)->0K(9216K) Eden: 5535K(8192K)->0K(8192K) From: 35K(1024K)->0K(1024K)
> [96.475s][info   ][gc，heap        ] GC(5) Tenured: 9020K(10240K)->8999K(10240K)
> [96.475s][info   ][gc，metaspace   ] GC(5) Metaspace: 8213K(8448K)->8092K(8448K) NonClass: 7339K(7424K)->7244K(7424K) Class: 874K(1024K)->848K(1024K)
> [96.475s][info   ][gc             ] GC(5) Pause Full (Allocation Failure) 14M->8M(19M) 16.586ms
> [96.475s][info   ][gc，cpu         ] GC(5) User=0.02s Sys=0.00s Real=0.02s
> [B@3b07d329
> [B@682a0b20
> [96.477s][info   ][gc，start       ] GC(6) Pause Young (Allocation Failure)
> [96.477s][info   ][gc             ] GC(6) Pause Young (Allocation Failure) 13M->13M(19M) 0.086ms
> [96.477s][info   ][gc，cpu         ] GC(6) User=0.00s Sys=0.00s Real=0.00s
> [96.477s][info   ][gc，start       ] GC(7) Pause Full (Allocation Failure)
> [96.477s][info   ][gc，phases，start] GC(7) Phase 1: Mark live objects
> [96.485s][info   ][gc，phases      ] GC(7) Phase 1: Mark live objects 8.022ms
> [96.485s][info   ][gc，phases，start] GC(7) Phase 2: Compute new object addresses
> [96.487s][info   ][gc，phases      ] GC(7) Phase 2: Compute new object addresses 1.451ms
> [96.487s][info   ][gc，phases，start] GC(7) Phase 3: Adjust pointers
> [96.491s][info   ][gc，phases      ] GC(7) Phase 3: Adjust pointers 4.088ms
> [96.491s][info   ][gc，phases，start] GC(7) Phase 4: Move objects
> [96.492s][info   ][gc，phases      ] GC(7) Phase 4: Move objects 0.856ms
> [96.492s][info   ][gc，heap        ] GC(7) DefNew: 5217K(9216K)->5121K(9216K) Eden: 5217K(8192K)->5121K(8192K) From: 0K(1024K)->0K(1024K)
> [96.492s][info   ][gc，heap        ] GC(7) Tenured: 8999K(10240K)->8962K(10240K)
> [96.492s][info   ][gc，metaspace   ] GC(7) Metaspace: 8097K(8448K)->8028K(8448K) NonClass: 7247K(7424K)->7189K(7424K) Class: 849K(1024K)->838K(1024K)
> [96.492s][info   ][gc             ] GC(7) Pause Full (Allocation Failure) 13M->13M(19M) 15.333ms
> [96.492s][info   ][gc，cpu         ] GC(7) User=0.02s Sys=0.00s Real=0.02s
> [96.492s][info   ][gc，start       ] GC(8) Pause Full (Allocation Failure)
> [96.492s][info   ][gc，phases，start] GC(8) Phase 1: Mark live objects
> [96.498s][info   ][gc，phases      ] GC(8) Phase 1: Mark live objects 6.004ms
> [96.498s][info   ][gc，phases，start] GC(8) Phase 2: Compute new object addresses
> [96.500s][info   ][gc，phases      ] GC(8) Phase 2: Compute new object addresses 1.182ms
> [96.500s][info   ][gc，phases，start] GC(8) Phase 3: Adjust pointers
> [96.504s][info   ][gc，phases      ] GC(8) Phase 3: Adjust pointers 4.837ms
> [96.505s][info   ][gc，phases，start] GC(8) Phase 4: Move objects
> [96.507s][info   ][gc，phases      ] GC(8) Phase 4: Move objects 2.185ms
> [96.507s][info   ][gc，heap        ] GC(8) DefNew: 5121K(9216K)->5121K(9216K) Eden: 5121K(8192K)->5121K(8192K) From: 0K(1024K)->0K(1024K)
> [96.507s][info   ][gc，heap        ] GC(8) Tenured: 8962K(10240K)->8450K(10240K)
> [96.507s][info   ][gc，metaspace   ] GC(8) Metaspace: 8028K(8448K)->8028K(8448K) NonClass: 7189K(7424K)->7189K(7424K) Class: 838K(1024K)->838K(1024K)
> [96.507s][info   ][gc             ] GC(8) Pause Full (Allocation Failure) 13M->13M(19M) 14.864ms
> [96.507s][info   ][gc，cpu         ] GC(8) User=0.02s Sys=0.00s Real=0.02s
> [96.509s][info   ][gc，heap，exit   ] Heap
> [96.509s][info   ][gc，heap，exit   ]  def new generation   total 9216K， used 5405K [0x00000000fec00000， 0x00000000ff600000， 0x00000000ff600000)
> [96.509s][info   ][gc，heap，exit   ]   eden space 8192K，  65% used [0x00000000fec00000， 0x00000000ff1475c8， 0x00000000ff400000)
> [96.509s][info   ][gc，heap，exit   ]   from space 1024K，   0% used [0x00000000ff500000， 0x00000000ff500000， 0x00000000ff600000)
> [96.509s][info   ][gc，heap，exit   ]   to   space 1024K，   0% used [0x00000000ff400000， 0x00000000ff400000， 0x00000000ff500000)
> [96.509s][info   ][gc，heap，exit   ]  tenured generation   total 10240K， used 8450K [0x00000000ff600000， 0x0000000100000000， 0x0000000100000000)
> [96.509s][info   ][gc，heap，exit   ]    the space 10240K，  82% used [0x00000000ff600000， 0x00000000ffe40b90， 0x00000000ffe40c00， 0x0000000100000000)
> [96.509s][info   ][gc，heap，exit   ]  Metaspace       used 8034K， committed 8448K， reserved 1056768K
> [96.509s][info   ][gc，heap，exit   ]   class space    used 839K， committed 1024K， reserved 1048576K
> Exception in thread "main" java.lang.OutOfMemoryError: Java heap space
> 	at org.example.Main.main(Main.java:21)
> 
> 进程已结束，退出代码1
> ```
>
> ![image-20230218075230272](http://gd.7n.cdn.wzl1.top/typora/img/image-20230218075230272.png)
>
> 最开始放到新生代,但是当继续new的时候,直接放入了老年代.
>
> 
>
> 重复实验,发现如果幸存区空间充足,对象会放入幸存区并且延寿,否则直接将待延寿对象晋升到老年代,当老年代空间不足时,抛出oom异常.
>
> ```java
> package org.example;
> 
> import java.io.IOException;
> import java.lang.reflect.Array;
> import java.util.ArrayList;
> 
> public class Main {
>     public static int a=5;
>     public static void main(String[] args) throws InterruptedException， IOException {
>         System.in.read();
>         ArrayList<byte[]> bytes=new ArrayList<>();
>         System.out.println("添加对象中");
>         bytes.add(new byte[1024*1024*5]);
>         bytes.forEach(System.out::println);
> 
>         System.in.read();
>         bytes.add(new byte[1024*1024*8]);
>     }
> }
> ```
>
> ```
> 
> 添加对象中
> [B@3b07d329
> [34.804s][info   ][gc，start       ] GC(4) Pause Young (Allocation Failure)
> [34.806s][info   ][gc，heap        ] GC(4) DefNew: 8192K(9216K)->14K(9216K) Eden: 8192K(8192K)->0K(8192K) From: 0K(1024K)->14K(1024K)
> [34.806s][info   ][gc，heap        ] GC(4) Tenured: 3897K(10240K)->9017K(10240K)
> [34.806s][info   ][gc，metaspace   ] GC(4) Metaspace: 8072K(8320K)->8072K(8320K) NonClass: 7196K(7296K)->7196K(7296K) Class: 875K(1024K)->875K(1024K)
> [34.806s][info   ][gc             ] GC(4) Pause Young (Allocation Failure) 11M->8M(19M) 2.195ms
> [34.806s][info   ][gc，cpu         ] GC(4) User=0.00s Sys=0.00s Real=0.00s
> 
> [59.066s][info   ][gc，start       ] GC(5) Pause Young (Allocation Failure)
> [59.066s][info   ][gc             ] GC(5) Pause Young (Allocation Failure) 11M->11M(19M) 0.075ms
> [59.066s][info   ][gc，cpu         ] GC(5) User=0.00s Sys=0.00s Real=0.00s
> [59.066s][info   ][gc，start       ] GC(6) Pause Full (Allocation Failure)
> [59.066s][info   ][gc，phases，start] GC(6) Phase 1: Mark live objects
> [59.074s][info   ][gc，phases      ] GC(6) Phase 1: Mark live objects 7.218ms
> [59.074s][info   ][gc，phases，start] GC(6) Phase 2: Compute new object addresses
> [59.076s][info   ][gc，phases      ] GC(6) Phase 2: Compute new object addresses 1.923ms
> [59.076s][info   ][gc，phases，start] GC(6) Phase 3: Adjust pointers
> [59.080s][info   ][gc，phases      ] GC(6) Phase 3: Adjust pointers 3.998ms
> [59.080s][info   ][gc，phases，start] GC(6) Phase 4: Move objects
> [59.080s][info   ][gc，phases      ] GC(6) Phase 4: Move objects 0.713ms
> [59.082s][info   ][gc，heap        ] GC(6) DefNew: 3000K(9216K)->0K(9216K) Eden: 2985K(8192K)->0K(8192K) From: 14K(1024K)->0K(1024K)
> [59.082s][info   ][gc，heap        ] GC(6) Tenured: 9017K(10240K)->9000K(10240K)
> [59.082s][info   ][gc，metaspace   ] GC(6) Metaspace: 8199K(8448K)->8078K(8448K) NonClass: 7323K(7424K)->7229K(7424K) Class: 875K(1024K)->849K(1024K)
> [59.082s][info   ][gc             ] GC(6) Pause Full (Allocation Failure) 11M->8M(19M) 15.316ms
> [59.082s][info   ][gc，cpu         ] GC(6) User=0.02s Sys=0.00s Real=0.01s
> [59.082s][info   ][gc，start       ] GC(7) Pause Full (Allocation Failure)
> [59.082s][info   ][gc，phases，start] GC(7) Phase 1: Mark live objects
> [59.090s][info   ][gc，phases      ] GC(7) Phase 1: Mark live objects 7.740ms
> [59.090s][info   ][gc，phases，start] GC(7) Phase 2: Compute new object addresses
> [59.091s][info   ][gc，phases      ] GC(7) Phase 2: Compute new object addresses 1.085ms
> [59.091s][info   ][gc，phases，start] GC(7) Phase 3: Adjust pointers
> [59.094s][info   ][gc，phases      ] GC(7) Phase 3: Adjust pointers 3.000ms
> [59.094s][info   ][gc，phases，start] GC(7) Phase 4: Move objects
> [59.095s][info   ][gc，phases      ] GC(7) Phase 4: Move objects 1.331ms
> [59.095s][info   ][gc，heap        ] GC(7) DefNew: 0K(9216K)->0K(9216K) Eden: 0K(8192K)->0K(8192K) From: 0K(1024K)->0K(1024K)
> [59.095s][info   ][gc，heap        ] GC(7) Tenured: 9000K(10240K)->8452K(10240K)
> [59.095s][info   ][gc，metaspace   ] GC(7) Metaspace: 8078K(8448K)->8009K(8448K) NonClass: 7229K(7424K)->7171K(7424K) Class: 849K(1024K)->838K(1024K)
> [59.095s][info   ][gc             ] GC(7) Pause Full (Allocation Failure) 8M->8M(19M) 13.684ms
> [59.096s][info   ][gc，cpu         ] GC(7) User=0.02s Sys=0.00s Real=0.01s
> [59.097s][info   ][gc，heap，exit   ] Heap
> [59.097s][info   ][gc，heap，exit   ]  def new generation   total 9216K， used 220K [0x00000000fec00000， 0x00000000ff600000， 0x00000000ff600000)
> [59.097s][info   ][gc，heap，exit   ]   eden space 8192K，   2% used [0x00000000fec00000， 0x00000000fec37390， 0x00000000ff400000)
> [59.097s][info   ][gc，heap，exit   ]   from space 1024K，   0% used [0x00000000ff500000， 0x00000000ff500000， 0x00000000ff600000)
> [59.097s][info   ][gc，heap，exit   ]   to   space 1024K，   0% used [0x00000000ff400000， 0x00000000ff400000， 0x00000000ff500000)
> [59.097s][info   ][gc，heap，exit   ]  tenured generation   total 10240K， used 8452K [0x00000000ff600000， 0x0000000100000000， 0x0000000100000000)
> [59.097s][info   ][gc，heap，exit   ]    the space 10240K，  82% used [0x00000000ff600000， 0x00000000ffe41268， 0x00000000ffe41400， 0x0000000100000000)
> [59.097s][info   ][gc，heap，exit   ]  Metaspace       used 8013K， committed 8448K， reserved 1056768K
> [59.097s][info   ][gc，heap，exit   ]   class space    used 839K， committed 1024K， reserved 1048576K
> Exception in thread "main" java.lang.OutOfMemoryError: Java heap space
> 	at org.example.Main.main(Main.java:17)
> 
> 进程已结束，退出代码1
> 
> ```
>
> ![image-20230218075825787](http://gd.7n.cdn.wzl1.top/typora/img/image-20230218075825787.png)

### 大对象_oom

刚刚想到问题并实现了,下面就是这个类似的.:laughing:

没错,这种情况如果老年代空间足够(前提是新生代不够用)直接晋升

![image-20230218080813951](http://gd.7n.cdn.wzl1.top/typora/img/image-20230218080813951.png)

如果超过老年代的话,很明显就是OOM,不过会提前做个自救工作,GC

![image-20230218081024873](http://gd.7n.cdn.wzl1.top/typora/img/image-20230218081024873.png)

### 线程中OOM

当某个线程发生OOM时,会影响主线程吗?

![image-20230218081429236](http://gd.7n.cdn.wzl1.top/typora/img/image-20230218081429236.png)

- 无论时哪个线程,只要是一个进程,那么堆内存是共享的
- 但是当一个线程抛出异常后,JVM会释放的该线程所占用的内存资源

综上,不会影响

![image-20230218081821798](http://gd.7n.cdn.wzl1.top/typora/img/image-20230218081821798.png)

## 垃圾回收器

### 串行

- 单线程
- 堆内存较小，适合个人电脑

```bash
-XX:+uSEsERIALgc=Serial+SerialOld
```

Serial：作用于新生代，标记复制

SerialOld：作用于老年代，标记整理

![image-20230218181333908](http://gd.7n.cdn.wzl1.top/typora/img/image-20230218181333908.png)

### 吞吐量优先

吞吐量：运行用户代码时间/(运行用户代码时间+垃圾回收总时间)

- 多线程
- 堆内存较大，多核cpu
- 让单位时间内，stw的时间最短

```bash
#JDK8下默认开启
-XX:+UseParallelGC ~ -XX:+UseParallelOldGC
#需要手动开启
#自适应新生代大小
-XX:+UseAdaptiveSizePolicy
#根据目标调整吞吐量目标，如果达不到则调整堆的大小 1/(1+ratio) => 垃圾回收时间/总的运行时间，结果表示每100分钟执行n分钟
-XX:GCTimeRatio=ratio
#GC暂停时间
-XX:MaxGCPauseMillis=ms
-XX:ParallelGCThreads=n
```

默认线程数为cpu核数

![image-20230218184152738](http://gd.7n.cdn.wzl1.top/typora/img/image-20230218184152738.png)

### 响应优先 —— CMS 并发标记清除收集器

- 多线程
- 堆内存较大，多核cpu
- 尽可能让单次stop onworld（stw）的时间的最短

#### 并行和并发

这里本来是OS的内容，在这里再提一下

- 并行
  ![img](http://c.biancheng.net/uploads/allimg/211207/15153644a-1.gif)
- 并发（Concurrent）
  ![img](http://c.biancheng.net/uploads/allimg/211207/1515363219-0.gif)
- 并行+并发
  ![img](http://c.biancheng.net/uploads/allimg/211207/15153613F-2.gif)

#### 响应优先

```bash
-XX:+UseConcMarkSweepGc
-XX:+UserParNewGC
#老年代补救
-XX:SerialOld
#并行线程数，CPU核数
-XX:ParallelGCThreads=n
#并发线程数，一般设置为并行线程数的1/4
-XX:ConGCThreads=threads
-XX:CMSInitiatingOccupancyFraction=parent
-XX:+CMSScavengeBeforeRemark
```

![image-20230219060040703](http://gd.7n.cdn.wzl1.top/typora/img/image-20230219060040703.png)

- 初始标记：只标记与GCRoots能直接关联到的对象
- 并发标记：从GCRoots关联对象开始遍历整个图
- 重新标记：修复并发标记期间，用户程序运作而导致标记产生变动的那部分对象的标记记录
- 清楚阶段：清楚标记阶段判断的已死亡对象

#### 缺点

- 虽然不会导致用户线程停顿，但是导致应用程序变慢，降低总吞吐量

- CMS默认启动的回收线程数是
  $$
  （处理器核心数量+3）/4
  $$
  ，也就是说当核心数≥4时，并发回收垃圾手机线程只占用不超过25%的处理器运算资源，并且伴随着核心数增多而下降

- 当核心数＜4时，对用户线程的影响变大
  扩展：增量式并发收集器

- 无法处理 浮动垃圾 ，有可能出现CMF（并发mode失败）从而导致另一次STW的Full GC产生

### HotSpot算法细节实现

#### 根节点枚举

GC Roots主要在全局性的引用和执行上下文中，且目前Java应用越做越庞大，类、常量很多，逐个检查耗费时间较长。

- 所有收集器根节点枚举必须STW
- 可达性分析算法耗时最长的查找引用链过程已经可以做到与用户线程一起并发
- 根节点枚举必须在保证一致性的快照中才得以进行——枚举期间，根节点对象的引用关系不发生变化

目前主流的虚拟机均采用**准确式垃圾收集**（虚拟机可以知道内存中某个位置的数据具体是什么类型），当用户线程停顿下来以后，并不需要一个不漏地检查完所有执行上下文和全局的应用位置，虚拟机应当时又办法直接得到哪些地方存放着用户引用的。

在HotSpot中，使用一组称为OopMap的数据结构来解决准确式垃圾回收的问题，在类加载动作完成的时候，虚拟机会把对象内什么偏移量上是什么类型的数据计算出来，在即使编译过程中，也会在特定位置记录栈、寄存器中哪些位置是引用。

#### 安全点

OopMap协助虚拟机快速准确完成GC Roots的枚举，但是可能导致引用关系变化，换句话说

​		**导致OopMap内容发生变化的指令非常多，如果每个指令都生成对应的OopMap，那么会需要大量的额外存储空间，这样垃圾收集伴随的空间成本将无比高昂**

实际上HotSpot的确没有为每条指令生成OopMap，只是在**特定的位置**记录了这些信息你，这些位置被称为**安全点**。

安全点的设定，决定了用户程序执行时并非在代码指定流的任意位置都能停下来进行垃圾收集，必须到达安全点才能暂停。

**安全点的选定**：

- 不能太少，也不能让收集器的等待时间过长
- 不能太频繁增大运行时的内存负荷
- 以”是否具有让程序长时间执行的特征“为标准进行选定
  - 指令序列复用
    - 方法调用
    - 循环跳转
    - 异常跳转

#### 安全区域

### G1

