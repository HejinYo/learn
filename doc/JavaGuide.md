# java
## 一、基础

### 1、面向对象
+ 类: 具有相同的属性和功能的对象的抽象的几集合
+ 字段: private 私有变量
+ 属性: public 共有变量

#### 封装:
> 将抽象性函式接口的实现细节部份包装、隐藏起来的方法

+ public：可以被所有其他类所访问
+ private：只能被自己访问和修改
+ protected：自身、子类及同一个包中类可以访问
+ default：同一包中的类可以访问，声明时没有加修饰符，认为是friendly。

#### 继承: 
> 是一种类与类之间强耦合的关系，一个类是另一个类的特殊种类，适用继承。

#### 多态: 
> 表示不同对象可以执行相同的动作，但要通过它们自己的实现代码来执行

#### 抽象类:
> 不能实例化 ，抽象方法是必须被子类重写，抽象类尽可能总有多的共同代码，尽可能少的数据，具体类不是用来继承的

#### 接口: 
> 把隐式公共方法和属性组合起来，以封装特定功能的一个集合。一旦类实现了接口，类就支持接口所指定的所有属性和成员。
不能实例化，不能有构造方法和字段，不能有修饰符，实现接口必须实现接口中的所有方法和属性

类是对对象的抽象，抽象类是对类的抽象，接口是对行为的抽象
+ 一、接口对类的局部（行为）进行抽象，抽象类对类的整体（字段，属性，方法）的抽象
+ 二、行为跨越不同类的对象，可使用接口；对于一些相似的类对象用继承抽象类
+ 三、从设计角度讲，抽象类是从子类中发现了公共的东西，泛化出父类，然后子类继承父类，而接口根本不知子类的存在，方法如何实现还不确认，预先定义。

#### final 关键字
final关键字主要用在三个地方：变量、方法、类。

+ 对于一个final变量，如果是基本数据类型的变量，则其数值一旦在初始化之后便不能更改；如果是引用类型的变量，则在对其初始化之后便不能再让其指向另一个对象。

+ 当用final修饰一个类时，表明这个类不能被继承。final类中的所有成员方法都会被隐式地指定为final方法。

+ 使用final方法的原因有两个。第一个原因是把方法锁定，以防任何继承类修改它的含义；第二个原因是效率。在早期的Java实现版本中，会将final方法转为内嵌调用。但是如果方法过于庞大，可能看不到内嵌调用带来的任何性能提升（现在的Java版本已经不需要使用final方法进行这些优化了）。类中所有的private方法都隐式地指定为final。

#### static 关键字
static 关键字主要有以下四种使用场景：

+ 修饰成员变量和成员方法: 被 static 修饰的成员属于类，不属于单个这个类的某个对象，被类中所有对象共享，可以并且建议通过类名调用。被static 声明的成员变量属于静态成员变量，静态变量 存放在 Java 内存区域的方法区。调用格式：类名.静态变量名 类名.静态方法名()
+ 静态代码块: 静态代码块定义在类中方法外, 静态代码块在非静态代码块之前执行(静态代码块—>非静态代码块—>构造方法)。 该类不管创建多少对象，静态代码块只执行一次.
+ 静态内部类（static修饰类的话只能修饰内部类）： 静态内部类与非静态内部类之间存在一个最大的区别: 非静态内部类在编译完成之后会隐含地保存着一个引用，该引用是指向创建它的外围类，但是静态内部类却没有。没有这个引用就意味着：1. 它的创建是不需要依赖外围类的创建。2. 它不能使用任何外围类的非static成员变量和方法。
+ 静态导包(用来导入类中的静态资源，1.5之后的新特性): 格式为：import static 这两个关键字连用可以指定导入某个类中的指定静态资源，并且不需要使用类名调用类中静态成员，可以直接使用类中静态成员变量和成员方法。
## 、J2EE
### JSP有9个内置对象：
+ request：封装客户端的请求，其中包含来自GET或POST请求的参数；
+ response：封装服务器对客户端的响应；
+ pageContext：通过该对象可以获取其他对象；
+ session：封装用户会话的对象；
+ application：封装服务器运行环境的对象；
+ out：输出服务器响应的输出流对象；
+ config：Web应用的配置对象；
+ page：JSP页面本身（相当于Java程序中的this）；
+ exception：封装页面抛出异常的对象。

### JSP中的四种作用域：
+ page代表与一个页面相关的对象和属性。
+ request代表与Web客户机发出的一个请求相关的对象和属性。一个请求可能跨越多个页面，涉及多个Web组件；需要在页面显示的临时数据可以置于此作用域。
+ session代表与某个用户与服务器建立的一次会话相关的对象和属性。跟某个用户相关的数据应该放在用户自己的session中。
+ application代表与整个Web应用程序相关的对象和属性，它实质上是跨越整个Web应用程序，包括多个页面、请求和会话的一个全局作用域。

### ListIterator迭代器的概述

+ ListIterator与Iterator接口不同，它不仅可以向后迭代，它还可以向前迭代。
+ ListIterator相对Iterator增加了如下3个方法：
+ boolean hasPrevious()：返回该迭代器关联的集合是否还有上一个元素。
+ Object previous()：返回该迭代器的上一个元素。
+ void add()：在指定位置插入一个元素

### Arraylist 与 LinkedList 异同

- **1. 是否保证线程安全：** ArrayList 和 LinkedList 都是不同步的，也就是不保证线程安全；
- **2. 底层数据结构：** Arraylist 底层使用的是Object数组；LinkedList 底层使用的是双向链表数据结构（注意双向链表和双向循环链表的区别：）；
-  **3. 插入和删除是否受元素位置的影响：** ① **ArrayList 采用数组存储，所以插入和删除元素的时间复杂度受元素位置的影响。** 比如：执行`add(E e)  `方法的时候， ArrayList 会默认在将指定的元素追加到此列表的末尾，这种情况时间复杂度就是O(1)。但是如果要在指定位置 i 插入和删除元素的话（`add(int index, E element) `）时间复杂度就为 O(n-i)。因为在进行上述操作的时候集合中第 i 和第 i 个元素之后的(n-i)个元素都要执行向后位/向前移一位的操作。 ② **LinkedList 采用链表存储，所以插入，删除元素时间复杂度不受元素位置的影响，都是近似 O（1）而数组为近似 O（n）。**
- **4. 是否支持快速随机访问：** LinkedList 不支持高效的随机元素访问，而 ArrayList 支持。快速随机访问就是通过元素的序号快速获取元素对象(对应于`get(int index) `方法)。
- **5. 内存空间占用：** ArrayList的空 间浪费主要体现在在list列表的结尾会预留一定的容量空间，而LinkedList的空间花费则体现在它的每一个元素都需要消耗比ArrayList更多的空间（因为要存放直接后继和直接前驱以及数据）。 



### map
#### HashMap
+ 根据键的hashCode的值存储数据，有很快的访问速度，但是遍历顺序是不确定的
+ 允许一个key为null，多个value为null
+ 非线程安全，可以使用Conllections的SynchonizedMap方法使HashMap具有线程安全能力，或者使用ConcurrentHashMap;
#### Hashtable
+ 继承Dictionay类，基本和HashMap类似，但是线程安全，性能不如ConcurrentHashMap;
+ 不允许key或value为null
#### LinkedHashMap
+ HashMap的子类，保存了记录的插入顺序，Iterator遍历时得到的记录是插入顺序
+ 允许一个key为null，多个value为null
+ 可以在构造函数时带入参数，按照访问次序排序
#### TreeMap
+ 能够把它保存的记录根据键排序，默认是按键值的升序排序，也可以指定排序的比较器，当用Iterator遍历TreeMap时，得到的记录是排过序的。
+ 如果使用排序的映射，建议使用TreeMap。在使用TreeMap时，key必须实现Comparable接口或者在构造TreeMap传入自定义的Comparator，否则会在运行时抛出java.lang.ClassCastException类型的异常。


### HashMap 实现原理
从结构实现来讲，HashMap是数组+链表+红黑树（JDK1.8增加了红黑树部分）实现的

> Node[] table的初始化长度length(默认值是16)，Load factor为负载因子(默认值是0.75)，threshold是HashMap所能容纳的最大数据量的Node(键值对)个数。threshold = length * Load factor

> 当链表长度太长（默认超过8）时，链表就转换为红黑树，利用红黑树快速增删改查的特点提高HashMap的性能，其中会用到红黑树的插入、删除、查找等算法

#### 实现 
##### 1、确定哈希桶数组索引位置
(h = key.hashCode()) ^ (h >>> 16) & (length-1) = h % 16

##### 2、扩容机制
Java里的数组是无法自动扩容的，方法是使用一个新的数组代替已有的容量小的数组
+ jdk1.7先放在一个索引上的元素终会被放到Entry链的尾部(如果发生了hash冲突的话）
+ Jdk1.8保持原来的链表顺序(解决了死循环的问题)，通过重新计算索引位置后，有可能被放到了新数组的不同位置上

#### HashMap 和 Hashtable 的区别

1. **线程是否安全：** HashMap 是非线程安全的，HashTable 是线程安全的；HashTable 内部的方法基本都经过  `synchronized`  修饰。（如果你要保证线程安全的话就使用 ConcurrentHashMap 吧！）；
2. **效率：** 因为线程安全的问题，HashMap 要比 HashTable 效率高一点。另外，HashTable 基本被淘汰，不要在代码中使用它；
3. **对Null key 和Null value的支持：** HashMap 中，null 可以作为键，这样的键只有一个，可以有一个或多个键所对应的值为 null。。但是在 HashTable 中 put 进的键值只要有一个 null，直接抛出 NullPointerException。
4. **初始容量大小和每次扩充容量大小的不同 ：**   ①创建时如果不指定容量初始值，Hashtable 默认的初始大小为11，之后每次扩充，容量变为原来的2n+1。HashMap 默认的初始化大小为16。之后每次扩充，容量变为原来的2倍。②创建时如果给定了容量初始值，那么 Hashtable 会直接使用你给定的大小，而 HashMap 会将其扩充为2的幂次方大小（HashMap 中的`tableSizeFor()`方法保证，下面给出了源代码）。也就是说 HashMap 总是使用2的幂作为哈希表的大小,后面会介绍到为什么是2的幂次方。
5. **底层数据结构：** JDK1.8 以后的 HashMap 在解决哈希冲突时有了较大的变化，当链表长度大于阈值（默认为8）时，将链表转化为红黑树，以减少搜索时间。Hashtable 没有这样的机制。

#### ConcurrentHashMap 和 Hashtable 的区别

ConcurrentHashMap 和 Hashtable 的区别主要体现在实现线程安全的方式上不同。

- **底层数据结构：** JDK1.7的 ConcurrentHashMap 底层采用 **分段的数组+链表** 实现，JDK1.8 采用的数据结构跟HashMap1.8的结构一样，数组+链表/红黑二叉树。Hashtable 和 JDK1.8 之前的 HashMap 的底层数据结构类似都是采用 **数组+链表** 的形式，数组是 HashMap 的主体，链表则是主要为了解决哈希冲突而存在的；
- **实现线程安全的方式（重要）：** ① **在JDK1.7的时候，ConcurrentHashMap（分段锁）** 对整个桶数组进行了分割分段(Segment)，每一把锁只锁容器其中一部分数据，多线程访问容器里不同数据段的数据，就不会存在锁竞争，提高并发访问率。（默认分配16个Segment，比Hashtable效率提高16倍。） **到了 JDK1.8 的时候已经摒弃了Segment的概念，而是直接用 Node 数组+链表+红黑树的数据结构来实现，并发控制使用 synchronized 和 CAS 来操作。（JDK1.6以后 对 synchronized锁做了很多优化）**  整个看起来就像是优化过且线程安全的 HashMap，虽然在JDK1.8中还能看到 Segment 的数据结构，但是已经简化了属性，只是为了兼容旧版本；② **Hashtable(同一把锁)** :使用 synchronized 来保证线程安全，效率非常低下。当一个线程访问同步方法时，其他线程也访问同步方法，可能会进入阻塞或轮询状态，如使用 put 添加元素，另一个线程不能使用 put 添加元素，也不能使用 get，竞争会越来越激烈效率越低。


#### JDK1.8与JDK1.7的性能对比
+ HashMap中，如果key经过hash算法得出的数组索引位置全部不相同，即Hash算法非常好，那样的话，getKey方法的时间复杂度就是O(1),
+ 如果Hash算法技术的结果碰撞非常多，假如Hash算极其差，所有的Hash算法结果得出的索引位置一样，那样所有的键值对都集中到一个桶中，或者在一个链表中，或者在一个红黑树中，时间复杂度分别为O(n)和O(lgn)。
+ 鉴于JDK1.8做了多方面的优化，总体性能优于JDK1.7



### synchronized
> 广义上的可重入锁指的是可重复可递归调用的锁，在外层使用锁之后，在内层仍然可以使用，并且不发生死锁（前提得是同一个对象或者class），这样的锁就叫做可重入锁。ReentrantLock和synchronized都是可重入锁

synchronized取得的锁都是对象锁，而不是把一段代码或方法当做锁。 如果多个线程访问的是同一个对象，哪个线程先执行带synchronized关键字的方法，则哪个线程就持有该方法，那么其他线程只能呈等待状态。如果多个线程访问的是多个对象则不一定，因为多个对象会产生多个锁。

+ Runnable接口不会返回结果但是Callable接口可以返回结果。

 ### Executor 框架的使用示意图
#### 1、主线程首先要创建实现Runnable或者Callable接口的任务对象。
> 备注： 工具类Executors可以实现Runnable对象和Callable对象之间的相互转换。（Executors.callable（Runnable task）或Executors.callable（Runnable task，Object resule））。

#### 2、然后可以把创建完成的Runnable对象直接交给ExecutorService执行（ExecutorService.execute（Runnable command））；或者也可以把Runnable对象或Callable对象提交给ExecutorService执行（ExecutorService.submit（Runnable task）或ExecutorService.submit（Callable task））。

#### 执行execute()方法和submit()方法的区别是什么呢？
+ 1)execute()方法用于提交不需要返回值的任务，所以无法判断任务是否被线程池执行成功与否；
+ 2)submit()方法用于提交需要返回值的任务。线程池会返回一个future类型的对象，通过这个future对象可以判断任务是否执行成功，并且可以通过future的get()方法来获取返回值，get()方法会阻塞当前线程直到任务完成，而使用get（long timeout，TimeUnit unit）方法则会阻塞当前线程一段时间后立即返回，这时候有可能任务没有执行完。

#### 3、如果执行ExecutorService.submit（…），ExecutorService将返回一个实现Future接口的对象（我们刚刚也提到过了执行execute()方法和submit()方法的区别，到目前为止的JDK中，返回的是FutureTask对象）。由于FutureTask实现了Runnable，程序员也可以创建FutureTask，然后直接交给ExecutorService执行。

#### 4、最后，主线程可以执行FutureTask.get()方法来等待任务执行完成。主线程也可以执行FutureTask.cancel（boolean mayInterruptIfRunning）来取消此任务的执行。




