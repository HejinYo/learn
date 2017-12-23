# MYSQL
##一、mysql 安装

[Centos7安装并配置mysql5.6完美教程](http://blog.csdn.net/qq_17776287/article/details/53536761)

### 删除自带的mariadb
```angular2html
#查询出来已安装的mariadb 
rpm -qa|grep mariadb

#卸载mariadb，文件名为上述命令查询出来的文件
rpm -e --nodeps mariadb-libs-5.5.41-2.el7_0.x86_64
```
### 准备工作
```angular2html
#本地上传
rz  mysql-5.6.38-linux-glibc2.12-x86_64.tar.gz
#网上下载
wget https://cdn.mysql.com//Downloads/MySQL-5.6/mysql-5.6.38-linux-glibc2.12-x86_64.tar.gz
#解压
tar -xvf  mysql-5.6.38-linux-glibc2.12-x86_64.tar.gz
#复制
mv mysql-5.6.38-linux-glibc2.12-x86_64 /usr/local/mysql
```

### 开始配置
```angular2html
#创建用户组
groupadd mysql

#创建用户
useradd -g mysql mysql

cd /usr/local/mysql

#复制配置文件，mysql默认扫描到 /etc/my.cnf 则忽略安装目录下的配置文件
cp support-files/my-default.cnf /etc/my.cnf

#编辑配置文件
vim /etc/my.cnf
    [mysqld]
    #禁用授权，不需要密码登录
    #skip-grant-tables
    #设置安装目录
    basedir = /usr/local/mysql
    #设置数据库目录
    datadir = /usr/local/mysql/data
    #设置端口
    port = 3306
    # pid路径
    pid-file=/usr/local/mysql/mysql.pid
    #日志路径
    log-error=/var/log/mysql/error.log



#修改权限
chown -R mysql:mysql /usr/local/mysql/
mkdir /var/log/mysql
chown -R mysql:mysql /var/log/mysql/

#安装依赖
yum -y install autoconf
yum install -y libaio 

#安装数据库
./scripts/mysql_install_db --user=mysql --basedir=/usr/local/mysql/ --datadir=/usr/local/mysql/data/

#添加守护进程
cp ./support-files/mysql.server /etc/rc.d/init.d/mysqld
chmod +x /etc/rc.d/init.d/mysqld 

#添加到系统服务
chkconfig --add mysqld
chkconfig --list mysqld

#开启服务
systemctl start mysqld / service mysqld start
#检查
systemctl status mysqld / service mysqld status
```
### mysql 命令添加环境变量
```angular2html
#个人
vim ~/.bash_profile
#全局 
vim /etc/profile
export PATH=$PATH:/usr/local/mysql/bin
source ~/.bash_profile 
```

### mysql配置
```angular2html
mysql -u root -p
use mysql;
#修改密码
update user set password=password('redhat') where user='root';
#开启远程
GRANT ALL PRIVILEGES ON *.* TO 'root'@'%' IDENTIFIED BY 'redhat' WITH GRANT OPTION;
#刷新配置
flush privileges;

```

## 二、主从配置
> 主配置
```angular2html
每个从数据库会使用一个MySQL账号来连接主数据库，所以我们要在主数据库里创建一个账号，并且该账号要授予 REPLICATION SLAVE 权限
#添加用户
CREATE USER 'crm_slave_1'@'192.168.10.%' IDENTIFIED BY 'crm_slave_1';

#授权
GRANT REPLICATION SLAVE ON *.* TO 'crm_slave_1'@'192.168.10.%';

#修改配置文件
vim /etc/my.cnf
    [mysqld]
    server-id=1 //不配置server-id或者配置值为0，那么主服务器将拒绝所有从服务器的连接
    log-bin=mysql-bin
    binlog-do-db=yoyo //要同步的mstest数据库,要同步多个数据库，就多加几个replicate-db-db=数据库名 
    #binlog-ignore-db=mysql //要忽略的数据库
    #在使用InnoDB的事务复制，为了尽可能持久和数据一致，
    #你应该在my.cnf里配置innodb_flush_log_at_trx_commit=1 和 sync_binlog=1；

#重启服务
systemctl restart mysqld

#查看状态，并记录 相关参数
show master status;
| File             | Position | Binlog_Do_DB | Binlog_Ignore_DB | Executed_Gtid_Set |
+------------------+----------+--------------+------------------+-------------------+
| mysql-bin.000001 |      433 | yoyo         |                  |                   |
+------------------+----------+--------------+------------------+-------------------+

```

> 从配置
[参考1](http://blog.csdn.net/envon123/article/details/76615059)
[参考2](https://www.cnblogs.com/fxmemory/p/7198663.html)
```angular2html
#修改配置文件
vim /etc/my.cnf
    [mysqld]
    server-id=2
    replicate-do-db = yoyo //可以指定要复制的库 ,在master端不指定binlog-do-db，在slave端用replication-do-db来过滤
    #replicate-ignore-db = mysql //忽略的库
    #relay-log=mysqld-relay-bin //二进制日志，用于恢复数据

#mysql 命令执行
mysql> stop slave;
mysql> change master to master_host='192.168.10.231',master_user='crm_slave_1',master_password='crm_slave_1', master_log_file='mysql-bin.000001',master_log_pos=443;
mysql> start slave;

#查看状态
mysql> show slave status\G;
*************************** 1. row ***************************
               Slave_IO_State: Waiting for master to send event
                  Master_Host: 192.168.10.231
                  Master_User: repl
                  Master_Port: 3306
                Connect_Retry: 60
              Master_Log_File: mysql-bin.000001
          Read_Master_Log_Pos: 433
               Relay_Log_File: localhost-relay-bin.000002
                Relay_Log_Pos: 283
        Relay_Master_Log_File: mysql-bin.000001
             Slave_IO_Running: Yes  # 两个Yes才算成功
            Slave_SQL_Running: Yes  # 两个Yes才算成功
              Replicate_Do_DB: yoyo
              
              
# slave 服务重置
使用reset slave all清空所有的复制信息，然后重置gtid_purged和master.infor
start slave 后复制正常

```