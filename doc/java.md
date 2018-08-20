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
加载多少类的元数据不再游 MaxPermSize 控制，而由系统实际可用空间来控制
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
