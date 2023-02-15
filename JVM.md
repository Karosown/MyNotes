# JVM

# Java 内存区域与内存溢出异常

## 运行时数据区域

![image-20230122220140540](http://gd.7n.cdn.wzl1.top/typora/img/image-20230122220140540.png)

### 程序计数器（PCR）

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

### Java虚拟机栈

==线程运行时需要的内存空间==

生命周期与线程相同，描述的是Java方法执行的线程内存模型：每个方法被执行的时候，Java虚拟机都会同步创建一个栈帧用于存储局部变量表、操作数栈、动态连接、方法出口等信息。

<img src="http://gd.7n.cdn.wzl1.top/typora/img/image-20230214061143920.png" alt="image-20230214061143920" style="zoom: 33%;" /><img src="http://gd.7n.cdn.wzl1.top/typora/img/image-20230214061329499.png" alt="image-20230214061329499" style="zoom: 33%;" />

>- 垃圾回收是否设计栈内存？
>   不涉及，只涉及堆内存
>
>- 占内存分配越大越好吗？
>
>  ```bash
>  -Xss1m #通过-Xss来设置栈内存，Linux/x64、macOs、OracleSolaris/x64 默认为1024KB=1m，Windows会根据虚拟内存影响栈的大小
>  ```
>
>  唯一的好处就是增大方法的递归调用，以及间接调用，明显的坏处就是减少线程数。
>
>- 方法内的局部变量是否线程安全？
>  由于Java虚拟机栈线程隔离，而一个方法的局部变量存放于栈帧中，所以线程安全，如果是共享变量（静态变量），那么线程不安全
>   <img src="http://gd.7n.cdn.wzl1.top/typora/img/image-20230214062826308.png" alt="image-20230214062826308" style="zoom: 33%;" /><img src="http://gd.7n.cdn.wzl1.top/typora/img/image-20230214063026191.png" alt="image-20230214063026191" style="zoom:33%;" />
>
>  特殊情况：
>
>  <img src="http://gd.7n.cdn.wzl1.top/typora/img/image-20230214063346572.png" alt="image-20230214063346572" style="zoom:33%;" />
>
>  **如果方法内局部变量没有逃离方法的作用范围，它是线程安全的**
>
>  **如果局部变量引用了对象，并逃离方法的作用范围，需要考虑线程安全问题**

#### 局部变量表

存放编译器可知的各种JVM基本数类型、对象引用和returnAddress类型(指向了一条字节码指令的地址)，存储空间以局部变量槽(Slot)表示，long和double占2个slot。

表所需的内存空间在编译期间完成分配，当进入一个方法时，这个方法需要在栈帧中分配的局部空间大小完全确定，**在方法运行期间不会改变局部变量表的大小（slot的数量）**

#### 栈内存溢出

- 栈帧过多导致栈内存溢出

  - 递归调用

  <img src="http://gd.7n.cdn.wzl1.top/typora/img/image-20230214065751022.png" alt="image-20230214065751022" style="zoom: 50%;" /><img src="http://gd.7n.cdn.wzl1.top/typora/img/image-20230214065809203.png" alt="image-20230214065809203" style="zoom:50%;" />

  - 循环依赖

  <img src="http://gd.7n.cdn.wzl1.top/typora/img/image-20230214070242836.png" alt="image-20230214070242836" style="zoom: 80%;" />

  > 解决方法：@JsonIgnore
  >
  > Spring 通过三级缓存解决循环依赖

- 栈帧过大导致栈内存溢出

#### 线程运行诊断

- *案例1: cpu占用过多*

  定位：

  - 用top命令定位那个进程对cpu的占用过高

  - ps H -eo pid,%cpu | grep 进程id

    ​	用ps命令进一步定位是哪一个线程引起的

  - jstack 进程id

    <img src="http://gd.7n.cdn.wzl1.top/typora/img/image-20230214163118421.png" alt="image-20230214163118421" style="zoom: 33%;" />

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

### 本地方法栈

本地方法：由C/C++等与操作系统打交道的语言编写

与VM Stack类似，区别在于服务对象不同，NMS为JVM用到的本地方法服务

### Java堆

Java Heap是虚拟机所管理的内存中最大的一块，是被所有线程共享的一块内存区域，在虚拟机启动时创建。

**唯一目的：存放对象实例**

从分配内存的角度看，所有线程共享的Java堆中可以划分出多个线程私有的分配缓冲区（TLAB），提升对象分配时的效率。

**Java Heap在物理上可以不连续，但在逻辑上应该连续**

可以通过设置参数-Xmx和-Xms设定Java Heap是固定大小还是可扩展

>* 通过new关键字，创建对象都会使用堆内存
>* 线程共享，堆中的 对象都需要考虑线程安全问题
>* 有垃圾回收机制

#### 堆内存溢出

<img src="http://gd.7n.cdn.wzl1.top/typora/img/image-20230214183001646.png" alt="image-20230214183001646" style="zoom: 50%;" /><img src="http://gd.7n.cdn.wzl1.top/typora/img/image-20230214183050934.png" alt="image-20230214183050934" style="zoom:50%;" />

**成次幂**

#### 堆内存诊断

工具>>

![image-20230214184717834](http://gd.7n.cdn.wzl1.top/typora/img/image-20230214184717834.png)

示例：<img src="http://gd.7n.cdn.wzl1.top/typora/img/image-20230214194854017.png" alt="image-20230214194854017" style="zoom: 33%;" />

##### JMAP

<img src="http://gd.7n.cdn.wzl1.top/typora/img/image-20230214195449267.png" alt="image-20230214195449267" style="zoom: 33%;" />

```bash
jmap -heap PID
```

<img src="C:/Users/30398/AppData/Roaming/Typora/typora-user-images/image-20230214195729993.png" alt="image-20230214195729993" style="zoom: 50%;" /><img src="C:/Users/30398/AppData/Roaming/Typora/typora-user-images/image-20230214195847722.png" alt="image-20230214195847722" style="zoom: 50%;" />

左：堆配置				右：堆内存占用情况

##### JConsole

![image-20230214205736847](http://gd.7n.cdn.wzl1.top/typora/img/image-20230214205736847.png)

##### JvirsualVm

![image-20230215031704078](http://gd.7n.cdn.wzl1.top/typora/img/image-20230215031704078.png)

具有堆转储功能(堆Dump)，截取快照

### 方法区

==所有Java虚拟机线程共享的区域==

存储和类相关的信息、成员方法、构造器方法、特殊方法

创建时期：虚拟机被启动时，逻辑上是堆的一部分，但事实不一定。

方法区的实现：永久代（**hotspot** JDK1.8以前）、元空间（操作系统内存）

![image-20230215045133801](http://gd.7n.cdn.wzl1.top/typora/img/image-20230215045133801.png)

如果申请内存时发现内存不住，也会发出outofMemoryError异常

**线程共享**的内存区域，用于存储已被虚拟机加载的类型模型、final、static、即时编译器编译后的代码缓存

#### 内存溢出

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

#### 常量池

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

#### 运行时常量池（Runtime Constant Pool）

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

##### StringTable

![image-20230215161421366](http://gd.7n.cdn.wzl1.top/typora/img/image-20230215161421366.png)

使用变量相加，由于是变量，所以不会在编译期间优化

###### 特性

- 常量池中的字符串仅是符号，第一次使用到时才变为对象

- 利用串池机制，避免重复创建字符串对象

- 字符串变量拼接的原理是StringBuilder(1.8).append

- 字符串常量拼接的原理是编译器优化

- 可以使用intern方法，主动将串池中还没有的字符串对象放入串池

  - 1.8 将这个字符串对象尝试放入串池，如果有则不会放入，如果没有则会放入串池，并返回![image-20230215163719209](http://gd.7n.cdn.wzl1.top/typora/img/image-20230215163719209.png)

    >true
    >
    >true

     ![image-20230215163710427](http://gd.7n.cdn.wzl1.top/typora/img/image-20230215163710427.png)

  - 1.6 将这个字符串对象尝试放入串池，如果有则不会放入，如果没有则会把这个对象赋值一份，放入串池，会把串池中的对象返回![image-20230215180145104](http://gd.7n.cdn.wzl1.top/typora/img/image-20230215180145104.png)

**面试题**

![image-20230215071801534](http://gd.7n.cdn.wzl1.top/typora/img/image-20230215071801534.png)

###### 位置

1.8之前，放在方法去永久代中，而1.7 1.8之后放在堆里面

![image-20230215181821229](http://gd.7n.cdn.wzl1.top/typora/img/image-20230215181821229.png)

###### 垃圾回收

![image-20230215184329180](http://gd.7n.cdn.wzl1.top/typora/img/image-20230215184329180.png)允许使用限制JVM在gc上的时间比例的策略。
异常抛出，默认启用此选项，如果超过98%的时间用于垃圾收集，则并行GC将抛出一个outofMemoryError
并且不到2%的堆被恢复。当堆很小时，这个特性可以用来防止应用程序长时间运行，而不会有什么进展。若要禁用此选项，
指定选项-xx：-UseGCOverheadLimit



### 直接内存

直接内存（Direct Memory）并不是虚拟机运行时数据区的一部分，也不是《JVM》规范中定义的内存区域。

在JDK 1.4中新加入了NIO类，引入了一种基于通道（Channel）与缓冲区（Buffer）的I/O方式，可以使用Native函数库直接分配堆外内存，然后通过一个存储在Java堆里面的DirectByteBuffer对象作为这块内存的引用操作，避免了在Java Heap和Native Heap中来回复制数据。

## HotSpot虚拟机对象揭秘

### 对象的创建

当虚拟机遇见一条字节码new指令，首先会检查这个指令的参数是否能在常量池中定位到一个类的符号引用，并且检查这个符号引用的类是否已被加载、解析、初始化过。如果没有，那必须先执行相应的类加载过程。

![HotSpotCreateObject](http://gd.7n.cdn.wzl1.top/typora/img/HotSpotCreateObject.png?dNow)

#### HotSpot解释器代码片段

```c++
// 确保常量池中存放的是已解释的类  
if (!constants->tag_at(index).is_unresolved_klass()) {  
  // 断言确保是klassOop和instanceKlassOop（这部分下一节介绍）  
  oop entry = (klassOop) *constants->obj_at_addr(index);  
  assert(entry->is_klass(), "Should be resolved klass");  
  klassOop k_entry = (klassOop) entry;  
  assert(k_entry->klass_part()->oop_is_instance(), "Should be instanceKlass");  
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
        if (Atomic::cmpxchg_ptr(new_top, Universe::heap()->top_addr(), compare_to) != compare_to) {  
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
          memset(to_zero, 0, obj_size * HeapWordSize);  
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
      SET_STACK_OBJECT(result, 0);  
      UPDATE_PC_AND_TOS_AND_CONTINUE(3, 1);  
    }  
  }  
}  
```

