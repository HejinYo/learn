# 一、mysql 8.0 安装（CentOS 7 及以上）
> rpm 安装包

## 1、系统准备工作
```text
# 替换163源（非必要）
# 备份默认源
mv /etc/yum.repos.d/CentOS-Base.repo /etc/yum.repos.d/CentOS-Base.repo.backup

# 下载163源到仓库目录
cd /etc/yum.repos.d
wget http://mirrors.163.com/.help/CentOS7-Base-163.repo

# 清除本地缓存
yum clean all

# 创建本地缓存
yum makecache

----------------------------------------------------------------------------------------------------

# 关闭SELINUX（必要，可以选择关闭，也可以选择放行某些操作限制，一般都是关闭）
selinux是对系统所有文件进行安全保护的，每一个文件都会打上标签，文件被修改，谁在修改，selinux都会进行拦截验证，
可能导致服务不能正常对文件进行访问，对安全性要求很高，除非你对selinux很熟悉

查看状态：getenforce 
Enforcing -- 运行

# 关掉，暂时的
setenforce 0 -- 禁用
setenforce 1 -- 启用

# 永久关掉
vim /etc/selinux/config    
SELINUX=permissive -- 运行但不拦截
SELINUX=disabled -- 关闭服务

----------------------------------------------------------------------------------------------------
# 关闭防火墙(想要开启远程访问的话，必要)，centos7 默认安装了firewalld防火墙（iptables的一种封装，底层命令还是iptables），
云服务器有也有外网的安全组，具体需要如何配置，根据需要的安全策略进行制定，但是这里先关闭
公司阿里云有些服务器貌似卸载了firewalld(>_<)，iptables也没有用，全部放行，所以全依赖安全组

# 暂时关闭，系统重启后，依然穷
systemctl stop firewalld

# 永久关闭
systemctl disable firewalld
```


## 2、需要的文件
> 必须安装如下清单，可能系统中已经默认安装其中某些，但是最好卸载，因为默认安装的版本比较低，建议下载和主程序版本对应的依赖

|包名称                              |下载地址                               |
|:------------------------------------|------------------------------------|
|mysql-community-server |[mysql-community-server](https://cdn.mysql.com/Downloads/MySQL-8.0/mysql-community-server-8.0.12-1.el7.x86_64.rpm "mysql-community-server")|
|mysql-community-client |[mysql-community-client](https://cdn.mysql.com//Downloads/MySQL-8.0/mysql-community-client-8.0.12-1.el7.x86_64.rpm "mysql-community-client")|
|mysql-community-common	|[mysql-community-common](https://cdn.mysql.com/Downloads/MySQL-8.0/mysql-community-common-8.0.12-1.el7.x86_64.rpm "mysql-community-common")|
|mysql-community-libs	|[mysql-community-libs](https://cdn.mysql.com//Downloads/MySQL-8.0/mysql-community-libs-8.0.12-1.el7.x86_64.rpm "mysql-community-libs")|
|mysql-community-libs-compat	|[mysql-community-libs-compat](https://cdn.mysql.com//Downloads/MySQL-8.0/mysql-community-libs-compat-8.0.12-1.el7.x86_64.rpm "Markdown")|

## 3、开始安装
```text
## 安装公共
rpm -ivh mysql-community-common-8.0.12-1.el7.x86_64.rpm

## 查看默认安装依赖
yum list installed | grep mariadb-libs

## 删除掉默认安装的mysql依赖库
yum remove mariadb-libs -y

## 安装依赖库
rpm -ivh mysql-community-libs-8.0.12-1.el7.x86_64.rpm

## 安装XtraBackup需要的依赖
rpm -ivh mysql-community-libs-compat-8.0.12-1.el7.x86_64.rpm

## 安装客户端
rpm -ivh mysql-community-client-8.0.12-1.el7.x86_64.rpm 

# 安装服务端
rpm -ivh mysql-community-server-8.0.12-1.el7.x86_64.rpm


```

## 4、安装布局
|文件或资源                            |位置                               |
|:------------------------------------|------------------------------------|
|Client programs and scripts	|/usr/bin|
|mysqld server	|/usr/sbin|
|Configuration file	|/etc/my.cnf|
|Data directory	|/var/lib/mysql|
|Error log file	|For RHEL, Oracle Linux, CentOS or Fedora platforms: /var/log/mysqld.log <br> For SLES: /var/log/mysql/mysqld.log|
|Value of secure_file_priv	|/var/lib/mysql-files|
|System V init script	|For RHEL, Oracle Linux, CentOS or Fedora platforms: /etc/init.d/mysqld <br>For SLES: /etc/init.d/mysql|
|Systemd service |For RHEL, Oracle Linux, CentOS or Fedora platforms: mysqld ;For SLES: mysql|
|Pid file	|/var/run/mysql/mysqld.pid|
|Socket	|/var/lib/mysql/mysql.sock|
|Keyring directory	|/var/lib/mysql-keyring|
|Unix manual pages	|/usr/share/man|
|Include (header) files	|/usr/include/mysql|
|Libraries	|/usr/lib/mysql|
|Miscellaneous support files (for example, error messages, and character set files)	|/usr/share/mysql|

### 注意：
> 安装还会在系统上创建名为mysql的用户和名为mysql的组<br>使用较旧的软件包安装以前版本的MySQL可能会创建一个名为/usr/my.cnf的配置文件<br> 强烈建议您检查文件的内容并将所需的设置迁移到文件/etc/my.cnf文件中，然后删除/usr/my.cnf。


### 4、修改默认配置（先不着急启动服务）
```text
# 这里是基本配置，主从配置详情看最后部分

vim /etc/my.cnf

---------------------------------------------------------
# 客户端配置
[client]
port=3306
socket=/data/mysqldata/data/mysql.sock
default-character-set = utf8mb4

# 服务器端配置 
[mysqld]

#忘记密码时使用
#skip-grant-tables

#设置协议认证方式(重点)
default_authentication_plugin=mysql_native_password

# 端口
port=3306

# 修改数据存储路径
datadir=/data/mysqldata/data

# 通信文件路径
socket=/data/mysqldata/data/mysql.sock

# 错误日志路径
log-error=/var/log/mysqld.log 

# 进程文件路径
pid-file=/data/mysqldata/data/mysql.pid
---------------------------------------------------------
# 创建数据目录
mkdir /data/mysqldata/data/ -p

mkdir /data/mysqldata/innodb/data -p
mkdir /data/mysqldata/innodb/group -p
mkdir /data/mysqldata/innodb/undo -p
mkdir /var/log/mysql

# 目录授权给mysql
chown mysql:mysql /data/mysqldata/* -R
chown mysql:mysql /var/log/mysql -R

# 启动服务
systemctl start mysqld

# 开机自启
systemctl enable mysqld

# 查看状态
systemctl status myslqd

# 查看启动日志(启动失败，排错有用)
journalctl --unit mysqld
```

## 5、启动成功，
默认在data目录中创建SSL证书和密钥文件等信息，mysql会创建一个超级用户帐户 'root'@'localhost，设置超级用户的密码并将其存储在错误日志文件中，
validate_password 默认安装。实现的默认密码策略validate_password要求密码包含至少一个大写字母，一个小写字母，一个数字和一个特殊字符，并且密码总长度至少为8个字符。

```text
# 获取随机root密码：
sudo grep 'temporary password' /var/log/mysql/mysqld.log

# 登录
shell> mysql -u root -p

# 修改密码 
mysql> ALTER USER 'root'@'localhost' IDENTIFIED BY 'Mysql@2018';

# 刷新配置
mysql>FLUSH PRIVILEGES;
```

## 6、开启远程&修改加密规则（如果启动之前忘记配置了）
```text
# 查询当前加密规则，默认caching_sha2_password，但是当前Navicat不支持，所有需要修改为mysql_native_password
mysql> use mysql;
mysql> select host, user, authentication_string, plugin from user; 

# 修改规则 这里需要指定localhost
mysql> ALTER USER 'root'@'localhost' IDENTIFIED WITH mysql_native_password BY 'Mysql@2018';

#刷新配置
mysql> FLUSH PRIVILEGES; 

# 开启远程 方法一
# 选择 mysql库
mysql> use mysql;

# 主机更新为所有
mysql> update user set host='%' where user ='root';

#刷新配置
mysql> FLUSH PRIVILEGES; 

# 开启远程 方法二
# 创建一个用户
mysql> CREATE USER 'root'@'%' IDENTIFIED WITH mysql_native_password BY 'Mysql@2018';

# 给这个用户授权
mysql> grant all privileges on *.* to 'root'@'%' identified by 'Mysql@2018';

```

# 二、主从配置
## 1、主从配置
```text
--------------------------主库配置----------------------
# 添加用户
mysql> CREATE USER 'crm_slave' IDENTIFIED BY 'Slave@2018';

# 授权
mysql> GRANT REPLICATION SLAVE ON *.* TO 'crm_slave';

# 刷新配置
mysql> FLUSH PRIVILEGES; 

#查看状态，并记录 相关参数 MASTER_LOG_FILE master_log_pos
show master status;

--------------------------从库配置----------------------
# 停止正在复制的工作
mysql> stop slave;

# 改变/添加主库信息
mysql> CHANGE MASTER TO MASTER_HOST='192.168.231.131', MASTER_USER='crm_slave', MASTER_PASSWORD='Slave@2018',MASTER_LOG_FILE='mysql-bin.000002',master_log_pos=888;

# 查看配置状态
mysql> show slave status\G;

# 开始主从复制
mysql> start slave;

# 查看配置状态
mysql> show slave status\G;
*************************** 1. row ***************************
               Slave_IO_State: Waiting for master to send event
                  Master_Host: 192.168.231.131
                  Master_User: crm_slave
                  Master_Port: 3306
                Connect_Retry: 60
              Master_Log_File: mysql-bin.000006
          Read_Master_Log_Pos: 993
               Relay_Log_File: hejinyo-relay-bin.000005
                Relay_Log_Pos: 525
        Relay_Master_Log_File: mysql-bin.000006
             Slave_IO_Running: Yes # 两个Yes才算成功
            Slave_SQL_Running: Yes # 两个Yes才算成功


# 如果配置错了，进行重置从库配置
mysql> stop slave;
mysql> reset slave all;
```
## 2、主主配置
原理：从添加主配置，主添加从配置



## 3、问题排查
```text
Navicat 执行sql报错：
 1055 - Expression #1 of ORDER BY clause is not in GROUP BY clause and contains nonaggregated column 'information_schema.PROFILING.SEQ' which is not functionally dependent on columns in GROUP BY clause; this is incompatible with sql_mode=only_full_group_by

# 查询sql_mode
mysql> select version(), @@sql_mode;

# 去掉 ONLY_FULL_GROUP_BY
mysql> SET sql_mode=(SELECT REPLACE(@@sql_mode,'ONLY_FULL_GROUP_BY','')); 

# 解决navicat执行sql后报警
vim /etc/my.cnf
    sql_mode='STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION'

```

```text
MySQL主从同步报错,将binglog指针下移一个位置。
mysql> slave stop; 
Query OK, 0 rows affected (0.01 sec)

mysql> set GLOBAL SQL_SLAVE_SKIP_COUNTER=1;
Query OK, 0 rows affected (0.00 sec)

mysql> slave start;
Query OK, 0 rows affected (0.00 sec)
```
## 4、跳过主从复制出现的错误
https://blog.csdn.net/jesseyoung/article/details/40585809

## 5、其他配置详情
```text
# 记录二进制日志文件名称，默认为binlog，每次重新启动mysql都会新建一个binlog
log-bin = binlog
二进制日志文件的默认位置是basedir数据目录，您可以使用--log-bin选项指定位置，支持相对路和绝对路径（不建议绝对路径，可能导致路径不同步，具体看https://dev.mysql.com/doc/refman/8.0/en/replication-options-binary-log.html）
默认情况下启用二进制日志记录（log_bin系统变量设置为ON）。例外情况是，如果使用mysqld通过使用--initialize或--initialize-insecure选项手动初始化数据目录，则默认情况下禁用二进制日志记录。在这种情况下，可以通过指定--log-bin选项来启用二进制日志记录。
要禁用二进制日志记录，可以在启动时指定--skip-log-bin或--disable-log-bin选项。如果指定了其中任何一个选项并且还指定了--log-bin，则稍后指定的选项优先。
--log-slave-updates和--slave-preserve-commit-order选项需要二进制日志记录。如果禁用二进制日志记录，请省略这些选项，或指定--skip-log-slave-updates和--skip-slave-preserve-commit-order。默认情况下，当指定了--skip-log-bin或--disable-log-bin时，MySQL会禁用这些选项。如果将--log-slave-updates或--slave-preserve-commit-order与--skip-log-bin或--disable-log-bin一起指定，则会发出警告或错误消息。
在MySQL 5.7中，必须在启用二进制日志记录时指定服务器ID，否则服务器将无法启动。
在MySQL 8.0中，server_id系统变量默认设置为1。启用二进制日志记录后，现在可以使用此默认服务器ID启动服务器，
但如果未使用--server-id选项明确指定服务器标识，则会发出信息性消息。对于复制拓扑中使用的服务器，必须为每个服务器指定唯一的非零服务器ID。

# 在使用InnoDB的事务复制，为了尽可能持久和数据一致，你应该在my.cnf里配置innodb_flush_log_at_trx_commit=1 和 sync_binlog=1；
0：log buffer将每秒一次地写入log file中，并且log file的flush(刷到磁盘)操作同时进行。该模式下在事务提交的时候，不会主动触发写入磁盘的操作。
1：每次事务提交时MySQL都会把log buffer的数据写入log file，并且flush(刷到磁盘)中去，该模式为系统默认。
2：每次事务提交时MySQL都会把log buffer的数据写入log file，但是flush(刷到磁盘)操作并不会同时进行。该模式下，MySQL会每秒执行一次 flush(刷到磁盘)操作。
当设置为0，该模式速度最快，但不太安全，mysqld进程的崩溃会导致上一秒钟所有事务数据的丢失。

当设置为1，该模式是最安全的，但也是最慢的一种方式。在mysqld 服务崩溃或者服务器主机crash的情况下，binary log 只有可能丢失最多一个语句或者一个事务。。
```

## 6、增加从库
开始只有一个MySQL实例在运行，后期因为安全性，压力，备份等原因需要在此实例的基础上面新增一个从库

MySQL主库已经运行一段时间了，里面已经有相当多数据，我们需要将这些数据备份出来，
然后从库再从备份的节点同步数据，这样来保持主从的数据一致性，并且在操作过程中最好不要影响我们的业务正常运行。
最终决定使用xtrabackup来备份数据，因为用xtrabackup备份数据的时候不需要琐表，
但只限于InnoDB引擎的数据库和XtraDB引擎的数据库，对于MyISAM引擎的数据库还是会琐表

### 第一步：部署从数据库服务器，最好数据库版本一致，部署过程省略

### 第二步：安装XtraBackup 
```text
xtrabackup 工具目前不支持 mysql 8.0，https://jira.percona.com/browse/PXB-1517
支持计划 2018-03-27 3:24 PM开始，截至2018-08-27还未支持

yum install http://www.percona.com/downloads/percona-release/redhat/0.1-6/percona-release-0.1-6.noarch.rpm
yum list | grep percona
yum install percona-xtrabackup-24

```

### 第三步：备份数据

注意：以需要使用root用户执行，或者使用sudo权限执行

### 备份整个库 
```text
xtrabackup --user=root --password=Mysql@2018 --target-dir=/root/mysql_bak -S /data/mysqldata/data/mysql.sock --backup

--user          指定数据库访问用户名
--password      指定数据库访问密码，如果密码有特殊字符需要使用单引号引起来
--target-dir    指定备份路径，最好写绝对路径
--backup        与--target-dir选项一起使用
-S              指定mysql.sock文件位置
```

### 备份单个库
```text
xtrabackup --user=root --password=Mysql@2018 --databases=test1 --target-dir=/root/mysql_bak -S /data/mysqldata/data/mysql.sock --backup
--databases    指定需要备份的库名
```

### 备份多个库
```text
xtrabackup --user=root --password=Mysql@2018 --databases="test1 test2 test3" --target-dir=/root/mysql_bak -S /data/mysqldata/data/mysql.sock --backup
--databases    多库用双引号引起来，使用空格进行分隔
```

###备份某个库的指定表

```text
xtrabackup --user=root --password=Mysql@2018 --databases="test1.tables1 test2.tables2 test3.tables3" --target-dir=/root/mysql_bak -S /data/mysqldata/data/mysql.sock --backup
--databases    备份表使用库名.表名的方式，如果是多个表就使用双引号引起来，不同表使用空格分隔
```

## 第六步：将数据还原到从服务器

注意：还原之前需要停止从库数据库服务并清空从服务器数据目录，如果有需要的数据就先备份到其实地方
```text
# 停止服务
service mysqld restart || systemctl restart mysqld
# 备份原数据
mkdir /data/mysql_bak
mv /data/mysql/* /data/mysql_bak
还原操作

xtrabackup --prepare --target-dir=/data/mysql_slave
xtrabackup --copy-back --target-dir=/data/mysql_slave
```

## 第七步：启动数据库并启用主从
```text

# 将原备份数据复制回原目录，注意：ib_buffer_pool ibdata1 ib_logfile0 ib_logfile1四个文件不要覆盖
# 如果从库没有数据需要往回复制的话就不需要cp这步操作
cp -r /data/mysql_bak/* /data/mysql
chown -R mysql:mysql /data/mysql
service mysqld restart || systemctl restart mysqld
执行以下命令

mysql> change master to master_host='172.16.10.10',master_port=3306,master_user='slave',master_password='123qweASD',master_log_file='mysql-bin.000011',master_log_pos=154;
master_host：             master服务器的IP地址

master_port：             master服务器的端口

master_user：             master服务器授权从服务器主从同步的用户名

master_password：    master服务器授权从服务器主从同步的密码

master_log_file：        从主服务器备份出来的文件：xtrabackup_info中获取

grep "binlog_pos" xtrabackup_info | awk -F "'" '{print $2}'
master_log_pos：       从主服务器备份出来的文件：xtrabackup_info中获取

grep "binlog_pos" xtrabackup_info | awk -F "'" '{print $4}'
执行以下命令启用主从同步

mysql> start slave;
小知识：停止主从同步命令为

# 以下为扩展知识命令，如果执行后需要再执行上条命令start slave;
mysql> stop slave;

```

## 第八步：测试并检查主从状态
```text
mysql> show slave status \G;
如果看以以下两个值为Yes说明主从同步正常

Slave_IO_Running: Yes
Slave_SQL_Running: Yes
```


## 第九步：测试

可以修改下需要同步的库，或者表的数据看上是否能正常同步去，或者在备份完的时候就去修改下数据，这时候备份里同是没有此修改记录，当启用主从同步的时候看下数据是否会同步过来



## mysqldump备份主库
恢复到从库，mysqldump是逻辑备份，数据量大时，备份速度会很慢，锁表的时间也会很长

### 1、备份主库

```text
mysqldump -uroot -pMysql@2018 --routines --single-transaction --databases crm mustang_crm mustang_selfexam mustang_v2 quartz --master-data=2 > mustang.sql


参数说明：https://www.cnblogs.com/qq78292959/p/3637135.html
--routines：导出存储过程和函数
--single_transaction：导出开始时设置事务隔离状态，并使用一致性快照开始事务，然后unlock tables; 而lock-tables是锁住一张表不能写操作，直到dump完毕。
--master-data：默认等于1，将dump起始（change master to）binlog点和pos值写到结果中，等于2是将change master to写到结果中并注释。

```

### 2. 把备份库拷贝到从库
```text
#scp mustang.2018-08-27.sql root@192.168.231.132:/root/
```

### 3. 从库导入备份库
```text
mysql -uroot -pMysql@2018 < ~/mustang.2018-08-28.sql

```
### 4. 在备份文件weibo.sql查看binlog和pos值
```text

head mustang.2018-08-27.sql -n80 | grep "MASTER_LOG_POS"

-- CHANGE MASTER TO MASTER_LOG_FILE='mysql-bin.000007', MASTER_LOG_POS=5728;

```

### 6. 从库设置从这个日志点同步，并启动
```text
mysql> start slave;
mysql> CHANGE MASTER TO MASTER_HOST='192.168.231.131', MASTER_USER='crm_slave', MASTER_PASSWORD='Slave@2018',MASTER_LOG_FILE='mysql-bin.000007',master_log_pos=5728;
mysql> stop slave;
mysql> show slave status\G;

可以看到IO和SQL线程均为YES，说明主从配置成功。
```


# master.cfn 主库配置文件 （mysql 8.0）
```text

[client]

# 客户端连接端口
port=3306

# 客户端直接连接服务器端的socket文件地址
socket = /data/mysqldata/data/mysql.sock

# 默认字符集
default-character-set = utf8mb4


[mysqld] 

#忘记密码时使用
#skip-grant-tables

#设置协议认证方式(重点)
default_authentication_plugin=mysql_native_password

#----------------------------------------------主从和基本配置 START--------------------------------------------------#

# 主库的主机名，建议为IP地址，容易辨别
report-host=192.168.231.131

# 服务ID（8.0默认为1），主要用于主从复制，不配置server-id或者配置值为0，那么主服务器将拒绝所有从服务器的连接
server_id = 131

# 服务监听端口
port = 3306

# 进程持有用户
user = mysql

# 数据目录
datadir = /data/mysqldata/data

# 进程文件路径
pid-file = /data/mysqldata/data/mysql.pid

# 套接字文件路径
socket = /data/mysqldata/data/mysql.sock

# 数据库名和表名的大小写的。1表示不区分大小写（8.0默认1）
lower_case_table_names = 1

# 默认事物引擎
default_storage_engine = innodb

# 记录二进制日志文件名称，默认为binlog，每次重新启动mysql都会新建一个binlog
log-bin = mysql-bin

# 要同步的mstest数据库,不设置复制所有库，要同步多个数据库，就多加几个replicate-db-db=数据库名 
#binlog-do-db=jelly

# 要忽略的数据库
#binlog-ignore-db=mysql

# 自增ID配置，主主配置需要注意，复制的时候避免主键冲突，可考虑单双主键
auto-increment-increment = 2
auto-increment-offset = 1

# 接收的更新记录到自己的二进制日志中,默认值（> = 8.0.3）TRUE （<= 8.0.2）FALSE
log_slave_updates = 1 

# 二进制日志有效期，>= 8.0.11 默认30天
binlog_expire_logs_seconds = 86400

# 二进制日志文件最大，默认1GB
max_binlog_size = 1024MB 

# 复制的时候同意所有操作，如创建function
log_bin_trust_function_creators = true

# 字符集和排序规则默认配置
character-set-client-handshake = FALSE 
character-set-server = utf8mb4 
collation-server = utf8mb4_unicode_ci 
init_connect='SET NAMES utf8mb4'

# 解决navicat执行sql后报警
sql_mode='STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION'


#----------------------------------------------主从和基本配置 END--------------------------------------------------#

#----------------------------------------------日志配置 START--------------------------------------------------#

# 主要是控制显示时间 error log、slow_log、genera log,不会影响 general log 和 slow log 写到表 (mysql.general_log, mysql.slow_log)
log_timestamps = SYSTEM

# 日志记录路径
log-error = /var/log/mysql/mysqld.log

# 启用slow sql
slow_query_log = 1

# slow sql 设置 1s
long_query_time = 1 

# slow sql 日志路径
slow_query_log_file =/var/log/mysql/mysql-slow.log

# SQL语句没有使用索引记录到慢查询日志文件中(默认不记录)
log_queries_not_using_indexes = 1

# 日志记录使用混合格式
binlog_format = mixed

# binlog缓存大小,默认32k
binlog_cache_size = 1M

#----------------------------------------------日志配置 END--------------------------------------------------#

#=========================================MYSQL Commmon Specific options=====================================#
# 多个连续连接请求在没有成功连接的情况下中断，服务器会阻止该主机进一步连接(默认100)
max_connect_errors = 1000

# 设置MySQL服务器和客户端（包括复制从属服务器）之间任何单个消息大小的上限（默认64MB）
max_allowed_packet = 64M

# 允许的最大同时客户端连接数（默认151）
max_connections = 3072

# 操作系统允许mysqld打开的文件数（默认5000，最大操作系统限制）
open_files_limit = 10000

# 所有线程的打开表的数量（默认4000）
table_open_cache = 2048

# MySQL可以拥有的未完成连接请求的数量
back_log = 512

# 允许用户创建的MEMORY表增长的最大大小（默认16M）
max_heap_table_size = 32M

# 对MyISAM表执行顺序扫描的每个线程为其扫描的每个表分配一个此大小的缓冲区
read_buffer_size = 2M
read_rnd_buffer_size = 8M
sort_buffer_size = 4M
join_buffer_size = 4M
thread_cache_size = 32

# 最小索引长度（默认4）
ft_min_word_len = 1

#=========================================INNODB Specific options===========================================#


# 限制mysql的使用内存（默认最小128）
#innodb_buffer_pool_size = 3G

innodb_data_home_dir = /data/mysqldata/innodb/data
innodb_data_file_path = ibdata1:1G:autoextend
innodb_write_io_threads = 16
innodb_read_io_threads = 16
innodb_thread_concurrency = 32
innodb_flush_log_at_trx_commit = 2

## innodb_fast_shutdown
innodb_log_buffer_size = 8M
innodb_log_file_size = 256M
innodb_log_files_in_group = 3
innodb_log_group_home_dir=/data/mysqldata/innodb/group
innodb_max_dirty_pages_pct = 90

## innodb_flush_method=O_DSYNC
innodb_lock_wait_timeout = 120
innodb_undo_directory=/data/mysqldata/innodb/undo


[mysqld_safe]
log-error = /var/log/mysql/mysqld.log
pid-file = /data/mysqldata/data/mysql.pid

!includedir /etc/my.cnf.d

[mysqldump]
quick
max_allowed_packet = 16M

[mysql] 
no-auto-rehash
# 默认字符集
default-character-set = utf8mb4 

[myisamchk]
key_buffer_size = 512M
sort_buffer_size = 512M
read_buffer = 8M
write_buffer = 8M

[mysqlhotcopy]
interactive-timeout
```

# slave.cfn 从库配置文件（mysql 8.0）
```text
[client]

# 客户端连接端口
port=3306

# 客户端直接连接服务器端的socket文件地址
socket = /data/mysqldata/data/mysql.sock

# 默认字符集
default-character-set = utf8mb4


[mysqld] 

#忘记密码时使用
#skip-grant-tables

#设置协议认证方式(重点)
default_authentication_plugin=mysql_native_password

#----------------------------------------------主从和基本配置 START--------------------------------------------------#

# 主库的主机名，建议为IP地址，容易辨别
report-host=192.168.231.128

# 从库只读 但root用户还是可以写操作
# read-only = 1

# 服务ID（8.0默认为1），主要用于主从复制，不配置server-id或者配置值为0，那么主服务器将拒绝所有从服务器的连接
server_id = 128

# 服务监听端口
port = 3306

# 进程持有用户
user = mysql

# 数据目录
datadir = /data/mysqldata/data

# 进程文件路径
pid-file = /data/mysqldata/data/mysql.pid

# 套接字文件路径
socket = /data/mysqldata/data/mysql.sock

# 数据库名和表名的大小写的。1表示不区分大小写（8.0默认1）
lower_case_table_names = 1

# 默认事物引擎
default_storage_engine = innodb

# 记录二进制日志文件名称，默认为binlog，每次重新启动mysql都会新建一个binlog
log-bin = mysql-bin

# 要同步的mstest数据库,不设置复制所有库，要同步多个数据库，就多加几个replicate-db-db=数据库名 
#binlog-do-db=jelly

# 要忽略的数据库
#binlog-ignore-db=mysql

# 自增ID配置，主主配置需要注意，复制的时候避免主键冲突，可考虑单双主键
auto-increment-increment = 2
auto-increment-offset = 2

# 接收的更新记录到自己的二进制日志中,默认值（> = 8.0.3）TRUE （<= 8.0.2）FALSE
log_slave_updates = 1 

# 二进制日志有效期，>= 8.0.11 默认30天
binlog_expire_logs_seconds = 86400

# 二进制日志文件最大，默认1GB
max_binlog_size = 1024MB 

# 复制的时候同意所有操作，如创建function
log_bin_trust_function_creators = true

# 字符集和排序规则默认配置
character-set-client-handshake = FALSE 
character-set-server = utf8mb4 
collation-server = utf8mb4_unicode_ci 
init_connect='SET NAMES utf8mb4'

# 解决navicat执行sql后报警
sql_mode='STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION'


#----------------------------------------------主从和基本配置 END--------------------------------------------------#

#----------------------------------------------日志配置 START--------------------------------------------------#

# 主要是控制显示时间 error log、slow_log、genera log,不会影响 general log 和 slow log 写到表 (mysql.general_log, mysql.slow_log)
log_timestamps = SYSTEM

# 日志记录路径
log-error = /var/log/mysql/mysqld.log

# 启用slow sql
slow_query_log = 1

# slow sql 设置 1s
long_query_time = 1 

# slow sql 日志路径
slow_query_log_file =/var/log/mysql/mysql-slow.log

# SQL语句没有使用索引记录到慢查询日志文件中(默认不记录)
log_queries_not_using_indexes = 1

# 日志记录使用混合格式
binlog_format = mixed

# binlog缓存大小,默认32k
binlog_cache_size = 1M

#----------------------------------------------日志配置 END--------------------------------------------------#

#=========================================MYSQL Commmon Specific options=====================================#
# 多个连续连接请求在没有成功连接的情况下中断，服务器会阻止该主机进一步连接(默认100)
max_connect_errors = 1000

# 设置MySQL服务器和客户端（包括复制从属服务器）之间任何单个消息大小的上限（默认64MB）
max_allowed_packet = 64M

# 允许的最大同时客户端连接数（默认151）
max_connections = 3072

# 操作系统允许mysqld打开的文件数（默认5000，最大操作系统限制）
open_files_limit = 10000

# 所有线程的打开表的数量（默认4000）
table_open_cache = 2048

# MySQL可以拥有的未完成连接请求的数量
back_log = 512

# 允许用户创建的MEMORY表增长的最大大小（默认16M）
max_heap_table_size = 32M

# 对MyISAM表执行顺序扫描的每个线程为其扫描的每个表分配一个此大小的缓冲区
read_buffer_size = 2M
read_rnd_buffer_size = 8M
sort_buffer_size = 4M
join_buffer_size = 4M
thread_cache_size = 32

# 最小索引长度（默认4）
ft_min_word_len = 1

#=========================================INNODB Specific options===========================================#


# 限制mysql的使用内存（默认最小128）
#innodb_buffer_pool_size = 3G

innodb_data_home_dir = /data/mysqldata/innodb/data
innodb_data_file_path = ibdata1:1G:autoextend
innodb_write_io_threads = 16
innodb_read_io_threads = 16
innodb_thread_concurrency = 32
innodb_flush_log_at_trx_commit = 2

## innodb_fast_shutdown
innodb_log_buffer_size = 8M
innodb_log_file_size = 256M
innodb_log_files_in_group = 3
innodb_log_group_home_dir=/data/mysqldata/innodb/group
innodb_max_dirty_pages_pct = 90

## innodb_flush_method=O_DSYNC
innodb_lock_wait_timeout = 120
innodb_undo_directory=/data/mysqldata/innodb/undo


[mysqld_safe]
log-error = /var/log/mysql/mysqld.log
pid-file = /data/mysqldata/data/mysql.pid

!includedir /etc/my.cnf.d

[mysqldump]
quick
max_allowed_packet = 16M

[mysql] 
no-auto-rehash
# 默认字符集
default-character-set = utf8mb4 

[myisamchk]
key_buffer_size = 512M
sort_buffer_size = 512M
read_buffer = 8M
write_buffer = 8M

[mysqlhotcopy]
interactive-timeout
```

# my.cfn (mysql 5.7.23)
```text
[client]
port=3306
socket = /data/mysqldata/data/mysql.sock
default-character-set = utf8mb4

[mysqld]
server_id = 45
report-host=172.16.3.45
port = 3306
user = mysql
#basedir = /usr/local/mysql
datadir = /data/mysqldata/data
pid-file = /data/mysqldata/data/mysql.pid
socket = /data/mysqldata/data/mysql.sock
disable-partition-engine-check = 1
lower_case_table_names = 1
character-set-client-handshake = FALSE
character-set-server = utf8mb4
collation-server = utf8mb4_unicode_ci
init_connect = 'SET NAMES utf8mb4'
default_storage_engine = innodb

sql_mode='STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_AUTO_CREATE_USER,NO_ENGINE_SUBSTITUTION'

#==================================================LOG=======================================================#
log_timestamps = SYSTEM
log-error = /var/log/mysql/mysqld.log
long_query_time = 1 
slow_query_log_file =/var/log/mysql/mysql-slow.log
slow_query_log = 1
log_queries_not_using_indexes = 1
log-bin = mysql-bin
log_bin_trust_function_creators = true
binlog_format = mixed
binlog_cache_size = 1M
#binlog-do-db=all
log_slave_updates = 1 

auto-increment-increment = 2
auto-increment-offset = 2

## symbolic-links = 0
expire_logs_days = 1
max_binlog_size = 1024MB 
tmpdir = /data/mysqldata/tmp
tmp_table_size = 64M

#=========================================MYSQL Commmon Specific options=====================================#
max_connect_errors = 1000
max_allowed_packet = 32M
max_connections = 3072
open_files_limit = 327680
table_open_cache = 2048
back_log = 512

#==== not shared in every connections ====#
max_heap_table_size = 32M
read_buffer_size = 2M
read_rnd_buffer_size = 8M
sort_buffer_size = 4M
join_buffer_size = 4M
thread_cache_size = 32
# 1 ON,2 OFF,3 DEMAND
query_cache_type = DEMAND
query_cache_size = 64M
query_cache_limit = 2M
#==== not shared in every connections ====#
ft_min_word_len = 1
thread_stack = 256K
# READ-UNCOMMITTED, READ-COMMITTED, REPEATABLE-READ, SERIALIZABLE.  
transaction_isolation = REPEATABLE-READ
explicit_defaults_for_timestamp = true

#=========================================INNODB Specific options===========================================#

## skip-innodb
## innodb_additional_mem_pool_size = 16M
#innodb_buffer_pool_size = 6G
## innodb_buffer_pool_instances = 8
innodb_data_file_path = ibdata1:1G:autoextend
innodb_data_home_dir = /data/mysqldata/innodb/data
innodb_write_io_threads = 16
innodb_read_io_threads = 16
## innodb_force_recovery = 1
innodb_thread_concurrency = 32
# 1 most safe mode
# 2 good speed
innodb_flush_log_at_trx_commit = 2
## innodb_fast_shutdown
innodb_log_buffer_size = 8M
innodb_log_file_size = 256M
innodb_log_files_in_group = 3
innodb_log_group_home_dir=/data/mysqldata/innodb/group
innodb_max_dirty_pages_pct = 90
## innodb_flush_method=O_DSYNC
innodb_lock_wait_timeout = 120
innodb_undo_directory=/data/mysqldata/innodb/undo
innodb_undo_tablespaces = 3
innodb_undo_logs = 128
# InnoDB every table has it's file
innodb_file_per_table = 1
innodb_buffer_pool_dump_at_shutdown = 1
innodb_buffer_pool_load_at_startup = 1
# 1 ON,0 OFF
innodb_flush_neighbors = 0
## innodb_lru_scan_depth = 2000
## innodb_io_capacity = 4000
## innodb_io_capacity_max = 8000

## innodb_file_format = Barracuda
## innodb_file_format_max = Barracuda
## innodb_strict_mode = 1
## innodb_print_all_deadlocks = 1

[mysqld_safe]
log-error=/var/log/mysql/mysqld.log
pid-file=/data/mysqldata/data/mysql.pid
open-files-limit = 65535

!includedir /etc/my.cnf.d

[mysqldump]
quick
max_allowed_packet = 16M

[mysql]
no-auto-rehash
default-character-set = utf8mb4

[myisamchk]
key_buffer_size = 512M
sort_buffer_size = 512M
read_buffer = 8M
write_buffer = 8M

[mysqlhotcopy]
interactive-timeout

```