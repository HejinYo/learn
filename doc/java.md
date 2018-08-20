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

+ 堆(heap):