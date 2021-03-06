# JVM
## 一、随笔

+ 虚拟机内存管理和垃圾回收，避免了绝大部分的内存泄露，但也正是因为如此，一旦有内存泄露，问题将很难排查

+ GC会对内存空间进行优化，将对象进行紧凑排列，类似OS中常用的碎片整理工具

+ 句柄是一种特殊的只能指针，当一个应用程序需要引用其他进程(数据库，操作系统)所管理的内存块或对象时，需要用到句柄

+ Online Stack Relacement (OSR 栈上替换)：虚拟机中，将**正在执行**的**低优化级**编译或者解释的代码，用更**高优化级**的代码替换，以提高程序执行效率
```java
class c{
    static int sum (int c){
        int y = 0;
        for (int i = 0; i < c; i++){
            y += 1;
        }
        return y;
    }
}
// 刚开始，java虚拟机解释执行，到循环达到一定程度后，java虚拟机会编译此方法，用编译后的代码代替解释执行
```

+ JDK7的HotSpot中，将原本在永久代的字符串常量池移除，转移到heap中;Symbols 符号引用放到了native memory
+ JDK8永久代被移除，被元数据区（元空间）取代
```text
类的元数据放入 natice memory;
字符串常量池和类的静态变量放入 heap 中;

元空间的本质和永久代类型，都是对JVM规范中方法区的实现，
最大的区别在于，元空间并不在虚拟机中，而是使用本地内存，
加载多少类的元数据不再由 MaxPermSize 控制，而由系统实际可用空间来控制
```
 + intern ：扩充常量池的一个方法；检测字符串常量池是否有当前值，有则返回字符串常量池中的对象的引用，没有则在常量池中增加unicode等于str的字符串并返回它的引用
 ```text
注意：只有使用字面量的时候，才会执行字符串常量池的相关操作；
使用 new 或者 + 连接符连接字符串常量池存在的变量，都不会执行字符串常量池操作；
```
```java
/**
* java中==和equals的区别
  java中的数据类型，可分为两类： 
  1.基本数据类型，也称原始数据类型。byte,short,char,int,long,float,double,boolean 
    他们之间的比较，应用双等号（==）,比较的是他们的值。 
  2.复合数据类型(类) 
    当他们用（==）进行比较的时候，比较的是他们在内存中的存放地址，
    所以，除非是同一个new出来的对象，他们的比较后的结果为true，否则比较后结果为false。
     JAVA当中所有的类都是继承于Object这个基类的，在Object中的基类中定义了一个equals的方法，
     这个方法的初始行为是比较对象的内存地 址，但在一些类库当中这个方法被覆盖掉了，
     如String,Integer,Date在这些类当中equals有其自身的实现，而不再是比较类在堆内存中的存放地址了。
*/
class c {
    public static void main(String[] args){
        String s0 = "kvill";
        String s1 = new String("kvill");
        String s2  = new  String("kvill");
        System.out.println(s1==s1.intern()); // false 如果池中没有，创建一个新的字符串，而不是引用heap中的地址
        System.out.println(s0==s1); // false s0 在池中， s1 在heap
        s1.intern();
        s2 = s2.intern();
        System.out.println(s0 == s1); // false s1 只执行了返回池中地址，但并为将这个地址的引用赋值 给 s1
        System.out.println(s0 == s1.intern()); // true
        System.out.println(s0 == s2); // true
    }
}
```

+ Xms - 初始化堆大小,jvm启动时分配的内存
+ Xmx - java heap最大值,运行过程中分配的最大内存
+ Xmn - 年轻代堆大小
+ Xss - 每个线程的栈大小

查询JDK默认JVM配置
```java
class test{
    public static void main(String[] args) {
        java.lang.management.MemoryUsage usage = java.lang.management.ManagementFactory.getMemoryMXBean().getHeapMemoryUsage();
        System.out.println("Max: " + usage.getMax());
        System.out.println("Init: " + usage.getInit());
        System.out.println("Committed: " + usage.getCommitted());
        System.out.println("Used: " + usage.getUsed());
    }
}
```

## 二、重点
![image](https://raw.githubusercontent.com/HejinYo/learn/master/assets/img/jvm.png)

+ 程序计数器(Program Counter Register):
```text
行号显示器，字节码指令的分支、循环、跳转、异常处理、线程恢复，
每条线程都需要一个独立的计数器，线程私有内存互不影响，该区域不会发生内存溢出异常
```

+ 本地方法栈(native method stack):
```text
为虚拟机使用到的Native方法服务，在虚拟机规范中对本地方法栈使用的语言、
使用方式与数据结构没有强制规定，可以自由实现，HotSport直接把本地方法栈和虚拟机栈合二为一；
此区域会抛出StackOverflowError和OutOfMemmorError异常
```


+ 虚拟机栈（vm stack）:
```text
每一个线程都有一个虚拟机栈，随着线程的创建而创建；描述的是JAVA方法在执行期间的内存模型；
每一个方法在执行的同时都会创建一个“栈帧（stack frame）”，
用于存储局部变量表、操作数栈、动态链接、方法出口等信息；
每一个方法从调用到执行完毕，就对应一个栈帧在虚拟机中从入栈到出栈的过程；
局部变量表所需要的内存空间在编译期间完成分配，当进入一个方法时，需要在栈中分配多大的局部变量是可以完全确定的，
在方法运行期间，不会改变局部变量表的大小

局部变量表：存放基本数据类型和对象的引用
实例变量：随类的实例化而创建
类变量（静态变量）：被static所修饰，随程序的创建而创建

线程请求的栈的深度大于虚拟机所允许的深度，将抛出StackOverfllowError异常
如果栈可以动态扩展，无法申请到足够的内存，将抛出OutOfMemoryError异常

```

+ 方法区（method area）：
```text
用于存储虚拟机加载的类信息（元数据）、常量、静态变量、即时编译器编译后的代码（动态加载OSGI）等数据；
有永久代(Permanent Generation)之称，但是本质并不等价，HotSpot用永久代实现了方法区，GC分代收集可以管理这部分内存；
对于其他虚拟机而言，不存在永久代的概念，这个区域可以不进行垃圾回收，主要回收目标针对常量池的回收和对类型class的卸载；
回收的成绩不是很理想，尤其是对类型的卸载，条件苛刻，但是回收确实是有必要的；

内存分配不够,将抛出OutOfMemoryError异常

```
+ 运行常量池（Runtime Constant Pool）:
```text
方法区的一部分，class 文件有类的版本、字段、方法、接口等描述信息，还有一项信息是
常量池（Constant Pool Table），用于存放编译期生成的各种字面量和符号引用，这部分内容将在类加载后进入方法区的运行时常量池
运行时常量池相对于class常量池的重要特性是具备动态性，常量不一定只有编译期才能产生，运行期间可以，比如String类的intern()方法
```

+ 堆(heap)
```text
    Java 堆（Java Heap）是Java虚拟机所管理的内存中最大的一块，所有线程共享，虚拟机启动时创建；
此区域唯一的目的就是存放对象实例，几乎所有的对象实例都在这里分配内存，JVM规范：所有的对象实例以及数组都要在堆上分配
但是随着JIT编译器与逃逸技术逐渐成熟，栈上分配、标量替换，所有对象都在堆上分配渐渐变得不是那么绝对

   垃圾收集器管理的主要区域，所以也被叫做GC堆（Garbage Collected Heap）;
内存回收角度看：基本都采用分代收集算法，细分为新生代和老年代；再细分为 Eden 空间， From Survivor空间， To Survivor 空间等
内存分配角度看：线程共享Java堆可能划分出多个线程私有的分配缓冲区（Thread Local Allocation Buffer, TLAB）

无论如何划分，都与存放内容无关，存储的任然都是对象实例，进一步划分的目的是为了更好的回收内存，或者更快的分配内存

内存不够分配，将抛出OutOfMemoryError异常
      

```

## 对象的创建

Java 是面向对象的编程语言，在程序运行过程中，无时无刻都有对象被创建出来
虚拟机遇到一条 new 指令时
+ 1、首先去检查这个指令的参数是否能在 **常量池** 中定位到一个类的符号引用，
而且检查这个符号引用代表的类是否已经被 加载、解析和初始化过，
如果没有，必须先执行相应的 类加载过程

+ 2、类加载检查通过后，接下来为新生对象分配内存，对象需要的内存大小在类加载完成后可以完全确定；
如果内存是绝对规整的，用过的内存放一边，空闲的放另外一边，中间指针为分界指示器，分配内存把指针挪动与对象大小相等的距离，被称为“指针碰撞（Bump The Pointer）”；
如果空闲和使用相互交错，虚拟机维护一个列表，记录那些是可用的，分配的时候，找到足够大的空间划分给对象实例，更新列表，被称为“空闲列表（Free List）”；
```text

选择哪种分配方式由Java堆是否规整决定，Java堆是否规整由垃圾收集器是否带有压缩整理功能决定；
在使用 Serial、ParNew 等带有 Compact过程的收集器，系统采用指针碰撞；
使用CMS基于Mark-Sweep算法收集器，采用空闲列表


分配内存线程安全问题：
1、对分配内存空间进行同步处理，实际采用CAS配上失败重试（乐观锁思想）的方式保证更新操作的原子性；
2、把内存分配动作按照线程划分在不同的空间之中进行，每个线程在Java堆中预先分配一小块内存，
称为**本地线程分配缓冲**（Thread Local Allocation Buffer,TLAB）那个线哪程要分配内存，就在哪个线程的TLAB上分配，
只有TLAB用完并分配新的TLAB时，才需要同步锁，虚拟机是否使用TLAB，可用通过-XX:+/-UseTLAB参数来设定
```

+ 3、内存分配完成后，虚拟机需要将分配到的内存空间都初始化为零值（不包含对象头），如果使用TLAB，这一工作提前至TLAB分配时进行，
这一步操作保证了**对象的实例字段**在Java代码中可以**不赋初始化值**就可以直接使用，程序访问到这些字段的数据类型所对应的零值

+ 4、虚拟机对对象进行必要的设置，
如：**对象是哪个类的实例、如何才能找到类的元数据信息、对象的哈希码、对象的GC分代年龄等信息**
这些信息存放在对象的对象头（Object Header）之中。

完成上面的工作，从虚拟机的角度来看，一个新的对象已经产生，但从Java程序的角度来看，对象创建才开始：init()方法还没执行，
所有的字段都还为零，执行 new 指令后会接着执行<init>方法，按照程序员的意愿进行初始化，一个真正可用的对象才算完全生产出来。