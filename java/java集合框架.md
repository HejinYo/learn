# ArrayList 与 LinkedList 区别
+ 1、都是不同步的，不保证线程安全
+ 2、ArrayList底层使用Object数组，LinkedList使用的是双向链表
+ 3、ArrayList使用数组存储，插入和删除操作的时间复杂度受插入位置的影响，
默认追加末尾为O(1)，指定位置i插入或删除为O(n-i)，
LinkedList采用链表存储，插入和删除都是O(1)
+ 4、ArrayList支持高效随机访问，LinkedList不支持
+ 5、ArrayList在结尾会预留一定的容量空间，而LinkedList每一个元素都需要消耗比ArrayList更多的空间

> 实现了RandomAccess接口的List，有限选择普通for循环，其次foreach;
未实现的List,优先选择iterator遍历（foreach底层也是通过iterator实现的）,大size千万不要用普通for循环


# ArrayList 与 Vector 区别
+ 1、vector的所有方法都是同步的，是线程安全的，同步操作会消耗大量时间
+ 2、ArrayList是不同步的，但效率比vector高


# Iterator 迭代器
```text

Iterator<Object> listIterator = list.iterator();
while(listIterator.hasNext()){
    listIterator.next()
}

Iterator<Map.Entry<String,Object>> mapIterator = map.entrySet().iterator();
while(mapIterator.hasNext()){
    mapIterator.next().getKey();
    mapIterator.next().getValue();
}

```

# ListIterator 迭代器
+ ListIterator与Iterator接口不同，它不仅可以向后迭代，它还可以向前迭代。
+ ListIterator相对Iterator增加了如下3个方法：
+ boolean hasPrevious()：返回该迭代器关联的集合是否还有上一个元素。
+ Object previous()：返回该迭代器的上一个元素。
+ void add()：在指定位置插入一个元素


# HashMap
+ 根据键的hashCode值存储数据，有很快的访问速度，遍历顺序不确定
+ 允许null的key和value
+ 非线程安全，可以使用Conllections的Synchornized方法是HashMap具有线程安全能力，或者使用ConcurrentHashMap

# Hashtable
+ 继承Dictionay类，基本和HashMap类似，但是线程安全，性能不如ConcurrentHashMap;
+ 不允许key或value为null

# LinkedHashMap
+ HashMap的子类，保存了记录的插入顺序，Iterator遍历时得到的记录是插入顺序
+ 允许一个key为null，多个value为null
+ 可以在构造函数时带入参数，按照访问次序排序

# TreeMap
+ 能够把它保存的记录根据键排序，默认升序，可以指定排序的比较器，当用Iterator遍历TreeMap时，得到的记录是排过序的。
+ 如果使用排序的映射，建议使用TreeMap。在使用TreeMap时，key必须实现Comparable接口或者在构造TreeMap传入自定义的Comparator，否则会在运行时抛出java.lang.ClassCastException类型的异常。

#HashMap 实现原理
从结构实现来讲，HashMap是数组+链表+红黑树（JDK1.8增加了红黑树部分）实现的

> Node[] table的初始化长度length(默认值是16)，Load factor为负载因子(默认值是0.75)，threshold是HashMap所能容纳的最大数据量的Node(键值对)个数。threshold = length * Load factor

> 当链表长度太长（默认超过8）时，链表就转换为红黑树，利用红黑树快速增删改查的特点提高HashMap的性能，其中会用到红黑树的插入、删除、查找等算法

## 1、确定哈希桶数组索引位置
(h = key.hashCode()) ^ (h >>> 16) & (length-1) = h % 16

## 2、扩容机制
Java里的数组是无法自动扩容的，方法是使用一个新的数组代替已有的容量小的数组
+ jdk1.7先放在一个索引上的元素终会被放到Entry链的尾部(如果发生了hash冲突的话）
+ Jdk1.8保持原来的链表顺序(解决了死循环的问题)，通过重新计算索引位置后，有可能被放到了新数组的不同位置上

# HashMap 和 Hashtable 的区别

1. **线程是否安全：** HashMap 是非线程安全的，HashTable 是线程安全的；HashTable 内部的方法基本都经过  `synchronized`  修饰。（如果你要保证线程安全的话就使用 ConcurrentHashMap 吧！）；
2. **效率：** 因为线程安全的问题，HashMap 要比 HashTable 效率高一点。另外，HashTable 基本被淘汰，不要在代码中使用它；
3. **对Null key 和Null value的支持：** HashMap 中，null 可以作为键，这样的键只有一个，可以有一个或多个键所对应的值为 null。。但是在 HashTable 中 put 进的键值只要有一个 null，直接抛出 NullPointerException。
4. **初始容量大小和每次扩充容量大小的不同 ：**   ①创建时如果不指定容量初始值，Hashtable 默认的初始大小为11，之后每次扩充，容量变为原来的2n+1。HashMap 默认的初始化大小为16。之后每次扩充，容量变为原来的2倍。②创建时如果给定了容量初始值，那么 Hashtable 会直接使用你给定的大小，而 HashMap 会将其扩充为2的幂次方大小（HashMap 中的`tableSizeFor()`方法保证，下面给出了源代码）。也就是说 HashMap 总是使用2的幂作为哈希表的大小,后面会介绍到为什么是2的幂次方。
5. **底层数据结构：** JDK1.8 以后的 HashMap 在解决哈希冲突时有了较大的变化，当链表长度大于阈值（默认为8）时，将链表转化为红黑树，以减少搜索时间。Hashtable 没有这样的机制。

# ConcurrentHashMap 和 Hashtable 的区别

ConcurrentHashMap 和 Hashtable 的区别主要体现在实现线程安全的方式上不同。

- **底层数据结构：** JDK1.7的 ConcurrentHashMap 底层采用 **分段的数组+链表** 实现，JDK1.8 采用的数据结构跟HashMap1.8的结构一样，数组+链表/红黑二叉树。Hashtable 和 JDK1.8 之前的 HashMap 的底层数据结构类似都是采用 **数组+链表** 的形式，数组是 HashMap 的主体，链表则是主要为了解决哈希冲突而存在的；
- **实现线程安全的方式（重要）：** ① **在JDK1.7的时候，ConcurrentHashMap（分段锁）** 对整个桶数组进行了分割分段(Segment)，每一把锁只锁容器其中一部分数据，多线程访问容器里不同数据段的数据，就不会存在锁竞争，提高并发访问率。（默认分配16个Segment，比Hashtable效率提高16倍。） **到了 JDK1.8 的时候已经摒弃了Segment的概念，而是直接用 Node 数组+链表+红黑树的数据结构来实现，并发控制使用 synchronized 和 CAS 来操作。（JDK1.6以后 对 synchronized锁做了很多优化）**  整个看起来就像是优化过且线程安全的 HashMap，虽然在JDK1.8中还能看到 Segment 的数据结构，但是已经简化了属性，只是为了兼容旧版本；② **Hashtable(同一把锁)** :使用 synchronized 来保证线程安全，效率非常低下。当一个线程访问同步方法时，其他线程也访问同步方法，可能会进入阻塞或轮询状态，如使用 put 添加元素，另一个线程不能使用 put 添加元素，也不能使用 get，竞争会越来越激烈效率越低。


#### JDK1.8与JDK1.7的性能对比
+ HashMap中，如果key经过hash算法得出的数组索引位置全部不相同，即Hash算法非常好，那样的话，getKey方法的时间复杂度就是O(1),
+ 如果Hash算法技术的结果碰撞非常多，假如Hash算极其差，所有的Hash算法结果得出的索引位置一样，那样所有的键值对都集中到一个桶中，或者在一个链表中，或者在一个红黑树中，时间复杂度分别为O(n)和O(lgn)。
+ 鉴于JDK1.8做了多方面的优化，总体性能优于JDK1.7

