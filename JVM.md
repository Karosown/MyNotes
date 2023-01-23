# JVM

# Java 内存区域与内存溢出异常

## 运行时数据区域

![image-20230122220140540](http://gd.7n.cdn.wzl1.top/typora/img/image-20230122220140540.png)

### 程序计数器（PCR）

PCR是一个较小的内存空间，可以看作是当前线程所执行的字节码的行号指示器。

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

生命周期与线程相同，描述的是Java方法执行的线程内存模型：每个方法被执行的时候，Java虚拟机都会同步创建一个栈帧用于存储局部变量表、操作数栈、动态连接、方法出口等信息。

#### 局部变量表

存放编译器可知的各种JVM基本数类型、对象引用和returnAddress类型(指向了一条字节码指令的地址)，存储空间以局部变量槽(Slot)表示，long和double占2个slot。

表所需的内存空间在编译期间完成分配，当进入一个方法时，这个方法需要在栈帧中分配的局部空间大小完全确定，**在方法运行期间不会改变局部变量表的大小（slot的数量）**

### 本地方法栈

与VM Stack类似，区别在于服务对象不同，NMS为JVM用到的本地方法服务

### Java堆

Java Heap是虚拟机所管理的内存中最大的一块，是被所有线程共享的一块内存区域，在虚拟机启动时创建。

**唯一目的：存放对象实例**

从分配内存的角度看，所有线程共享的Java堆中可以划分出多个线程私有的分配缓冲区（TLAB），提升对象分配时的效率。

**Java Heap在物理上可以不连续，但在逻辑上应该连续**

可以通过设置参数-Xmx和-Xms设定Java Heap是固定大小还是可扩展

### 方法区

线程共享的内存区域，用于存储已被虚拟机加载的类型模型、final、static、即时编译器编译后的代码缓存

#### 运行时常量池（Runtime Constant Pool）

RCP时方法区的一部分。

Class文件中除了有类的版本、字段、方法、接口等描述信息外，还有一项信息时常量池表（Constant Pool Table），CPT用于存放你编译器生成的各种字面量与符号引用，这部分内容将在类加载后存放到方法区的运行时常量池中。

RCP相对于Class文件常量池的另外一个重要特征：**动态性**

Java并不要求常量一定要在编译器才能产生，也就是说并非预置入Class文件中的常量池的内容才能进入方法区运行常量池，运行期间也可以将新的常量放入池中，比如String::inter

> :tada:String::inter()
>
> ![image-20230122235442146](http://gd.7n.cdn.wzl1.top/typora/img/image-20230122235442146.png)
>
> 会在常量池中寻找字符串

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

