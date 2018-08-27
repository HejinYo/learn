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
|mysql-community-libs-compat	|[mysql-community-libs-compat](https://github.com/younghz/Markdown "Markdown")|

## 3、开始安装
```text
## 安装公共
rpm -ivh mysql-community-common-8.0.12-1.el7.x86_64.rpm

## 查看默认安装依赖
yum list installed | grep mariadb-libs

## 删除掉默认安装的mysql依赖库
yum remove mariadb-libs

## 安装依赖库
rpm -ivh mysql-community-libs-8.0.12-1.el7.x86_64.rpm

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

#datadir=/var/lib/mysql 修改数据存储路径
datadir=/data/mysqldata/data

#socket=/var/lib/mysql/mysql.sock 通信文件路径
socket=/data/mysqldata/data/mysql.sock

# 错误日志路径
log-error=/var/log/mysqld.log 

#pid-file=/var/run/mysqld/mysqld.pid 进程文件路径
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

# 修改规则 这里需要指定localhost，因为8.0这里好像不支持通配符
mysql> ALTER USER 'root'@'localhost' IDENTIFIED WITH mysql_native_password BY 'Mysql@2018';
mysql> FLUSH PRIVILEGES; 

# 开启远程
mysql> use mysql;
mysql> update user set host='%' where user ='root';
mysql> FLUSH PRIVILEGES; 

```

# 二、主从配置

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
# 三、主从配置
原理：从添加主配置，主添加从配置




# 问题排查
```text
Navicat 执行sql报错：
 1055 - Expression #1 of ORDER BY clause is not in GROUP BY clause and contains nonaggregated column 'information_schema.PROFILING.SEQ' which is not functionally dependent on columns in GROUP BY clause; this is incompatible with sql_mode=only_full_group_by

# 查询sql_mode
select version(), @@sql_mode;

# 去掉 ONLY_FULL_GROUP_BY
SET sql_mode=(SELECT REPLACE(@@sql_mode,'ONLY_FULL_GROUP_BY','')); 
```

```text
MySQL主从同步报错排错结果及修复过程之：Slave_SQL_Running: No
从库报错内容为：创建库失败
mysql> show slave status\G;
Slave_IO_Running: Yes
Slave_SQL_Running: No
Last_Error: Error 'Can't drop database 'lcp'; database doesn't exist' on query. Default database: 'lcp'. Query: 'drop database lcp'

查看日志，发现错误报告，不能与主库同步，上一条报告lcp库已存在，
原因：想创建“lcp”库却发现此库已存在，想删掉“lcp”库，却发现系统报错此库不存在

解决方案：将binglog指针下移一个位置。
mysql> slave stop; 
Query OK, 0 rows affected (0.01 sec)

mysql> set GLOBAL SQL_SLAVE_SKIP_COUNTER=1;
Query OK, 0 rows affected (0.00 sec)

mysql> slave start;
Query OK, 0 rows affected (0.00 sec)

mysql> show slave status\G;

Slave_IO_Running: Yes
Slave_SQL_Running: Yes
```


## 其他配置详情
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