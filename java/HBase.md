# HBase
> 在HBase中，表被分割成区域，并由区域服务器提供服务。区域被列族垂直分为“Stores”。Stores被保存在HDFS文件。下面显示的是HBase的结构。
+ 注意：术语“store”是用于区域来解释存储结构。
+ HBase Architecture HBase有三个主要组成部分：客户端库，主服务器和区域服务器。
区域服务器可以按要求添加或删除。
![image](https://raw.githubusercontent.com/HejinYo/learn/master/assets/img/Hbase-001.jpg)

## 1、主服务器 
+ 分配区域给区域服务器并在Apache ZooKeeper的帮助下完成这个任务。
+ 处理跨区域的服务器区域的负载均衡。它卸载繁忙的服务器和转移区域较少占用的服务器。
+ 通过判定负载均衡以维护集群的状态。
+ 负责模式变化和其他元数据操作，如创建表和列。 

## 2、区域 
+ 只不过是表被拆分，并分布在区域服务器。 

## 3、区域服务器 区域服务器拥有区域如下 
+ 与客户端进行通信并处理数据相关的操作。
+ 句柄读写的所有地区的请求。
+ 由以下的区域大小的阈值决定的区域的大小。
> 需要深入探讨区域服务器：包含区域和存储，
 如下图所示：Regional Server 存储包含内存存储和HFiles。memstore就像一个高速缓存。在这里开始进入了HBase存储。数据被传送并保存在Hfiles作为块并且memstore刷新。 
 
## 4、Zookeeper
+ Zookeeper管理是一个开源项目，提供服务，如维护配置信息，命名，提供分布式同步等
+ Zookeeper代表不同区域的服务器短暂节点。主服务器使用这些节点来发现可用的服务器。
+ 除了可用性，该节点也用于追踪服务器故障或网络分区。
+ 客户端通过与zookeeper区域服务器进行通信。
+ 在模拟和独立模式，HBase由zookeeper来管理。


# HBase Shell 
HBase包含可以与HBase进行通信的Shell。 HBase使用Hadoop文件系统来存储数据。它拥有一个主服务器和区域服务器。数据存储将在区域(表)的形式。这些区域被分割并存储在区域服务器。 主服务器管理这些区域服务器，所有这些任务发生在HDFS。下面给出的是一些由HBase Shell支持的命令。
## 通用命令
+ status: 提供HBase的状态，例如，服务器的数量。
+ version: 提供正在使用HBase版本。
+ table_help: 表引用命令提供帮助。
+ whoami: 提供有关用户的信息。 

## 数据定义语言 这些是关于HBase在表中操作的命令。
+ create: 创建一个表。
+ list: 列出HBase的所有表。
+ disable: 禁用表。
+ is_disabled: 验证表是否被禁用。
+ enable: 启用一个表。
+ is_enabled: 验证表是否已启用。
+ describe: 提供了一个表的描述。
+ alter: 改变一个表。
+ exists: 验证表是否存在。
+ drop: 从HBase中删除表。
+ drop_all: 丢弃在命令中给出匹配“regex”的表。
+ Java Admin API: 在此之前所有的上述命令，Java提供了一个通过API编程来管理实现DDL功能。在这个org.apache.hadoop.hbase.client包中有HBaseAdmin和HTableDescriptor 这两个重要的类提供DDL功能。 

## 数据操纵语言put: 把指定列在指定的行中单元格的值在一个特定的表。
+ get: 取行或单元格的内容。
+ delete: 删除表中的单元格值。
+ deleteall: 删除给定行的所有单元格。
+ scan: 扫描并返回表数据。
+ count: 计数并返回表中的行的数目。
+ truncate: 禁用，删除和重新创建一个指定的表。
+ Java client API: 在此之前所有上述命令，Java提供了一个客户端API来实现DML功能，CRUD（创建检索更新删除）操作更多的是通过编程，在org.apache.hadoop.hbase.client包下。 在此包HTable 的 Put和Get是重要的类。

### 创建表
+ create 'test','info'
### 列出HBase的所有表
+ list
### 写入
+ put 'test','0001','info:username','henry'
+ pub 'test','0001','info:age','20'

### 返回表数据
+ scan 'test'

### 查看表信息
+ describe 'test'
### 禁用表
+ disable 'test'
### 是否启用
+ is_enabled 'test'
### 删除表
+ drop 'test'