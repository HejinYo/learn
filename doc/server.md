# MYSQL
##一、mysql 安装

[Centos7安装并配置mysql5.6完美教程](http://blog.csdn.net/qq_17776287/article/details/53536761)

### 删除自带的mariadb
```text
#查询出来已安装的mariadb 
rpm -qa|grep mariadb

#卸载mariadb，文件名为上述命令查询出来的文件
rpm -e --nodeps mariadb-libs-5.5.41-2.el7_0.x86_64
```
### 准备工作
```text
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
```text
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
#防火墙
firewall-cmd --add-port=3306/tcp --permanent
systemctl restat firewalld
```
### mysql 命令添加环境变量
```text
#个人
vim ~/.bash_profile
#全局 
vim /etc/profile
export PATH=$PATH:/usr/local/mysql/bin
source ~/.bash_profile 
```

### mysql配置
```text
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
```text
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
```text
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

# 服务自启
> CentOS 7的服务systemctl脚本存放在：/usr/lib/systemd/，有系统（system）和用户（user）之分，需要开机不登陆就能运行的程序，存在系统服务里，即：/usr/lib/systemd/system目录下
  每一个服务以.service结尾，一般会分为3部分：[Unit]、[Service]和[Install]，我写的这个服务用于开机运行tomcat项目:

```text
vim /usr/lib/systemd/system/tomcat.service  
[Unit]  
Description=tomcatapi  
After=network.target  
   
[Service]  
Type=forking  
PIDFile=/usr/local/tomcat/tomcat.pid  
ExecStart=/usr/local/tomcat/bin/startup.sh  
ExecReload=  
ExecStop=/usr/local/tomcat/bin/shutdown.sh  
PrivateTmp=true  
   
[Install]  
WantedBy=multi-user.target  
```
[Unit]部分主要是对这个服务的说明，内容包括Description和After，Description用于描述服务，After用于描述服务类别;
[Service]部分是服务的关键，是服务的一些具体运行参数的设置，这里Type=forking是后台运行的形式，PIDFile为存放PID的文件路径，ExecStart为服务的运行命令，ExecReload为重启命令，ExecStop为停止命令，PrivateTmp=True表示给服务分配独立的临时空间，注意：[Service]部分的启动、重启、停止命令全部要求使用绝对路径，使用相对路径则会报错;
[Install]部分是服务安装的相关设置，可设置为多用户的
服务脚本按照上面编写完成后，以754的权限保存在/usr/lib/systemd/system目录下，这时就可以利用systemctl进行测试了
最后用以下命令将服务加入开机启动即可：
```text
systemctl enable tomcat  
```

# 三、Redis 安装[参考](http://blog.csdn.net/gxw19874/article/details/51992125)
## 下载文件
```text
wget http://download.redis.io/releases/redis-4.0.6.tar.gz
tar -xvf redis-4.0.6.tar.gz
cd redis-4.0.6.tar.gz
yum -y install gcc
make 
#如果编译报错，使用以下命令
#jemalloc重载了Linux下的ANSI C的malloc和free函数。解决办法：make时添加参数。
make MALLOC=libc

mkdir /usr/local/redis
chmod 755 /usr/local/redis
cp src/redis-server /usr/local/redis
cp src/redis-cli /usr/local/redis
cp redis.conf /etc/init.d/redis

vim /etc/local/redis/redis.conf
/daemonize yes //后台运行


```

##
systemctl daemon-reload

# 2个redis哨兵，1个master，1个slave
```text
wget http://download.redis.io/releases/redis-4.0.6.tar.gz
tar -xvf redis-4.0.6.tar.gz
cd redis-4.0.6.tar.gz
yum -y install gcc
make 
#如果编译报错，使用以下命令
#jemalloc重载了Linux下的ANSI C的malloc和free函数。解决办法：make时添加参数。
make MALLOC=libc

mkdir /var/log/redis
mkdir /usr/local/redis
# 主
mkdir -p /usr/local/redis/master/config
# 从
mkdir -p /usr/local/redis/slave/config
chmod -R 755 /usr/local/redis


# 主
cp src/redis-server /usr/local/redis/master/
cp src/redis-cli /usr/local/redis/master/
cp redis.conf /usr/local/redis/master/config/
cp src/redis-sentinel /usr/local/redis/master/
cp sentinel.conf /usr/local/redis/master/config/
# 从
cp src/redis-server /usr/local/redis/slave/
cp src/redis-cli /usr/local/redis/slave/
cp redis.conf /usr/local/redis/slave/config/
cp src/redis-sentinel /usr/local/redis/slave/
cp sentinel.conf /usr/local/redis/slave/config/

#主配置
vim /usr/local/redis/master/config/redis.conf
    daemonize yes //后台运行
    port 6380 //端口
    pidfile /var/run/redis_6380.pid //进程文件
    logfile "/var/log/redis/redis_6380.log"  //日志文件
    requirepass crm_redis   //redis密码
    masterauth crm_redis    //主备密码，切换需要保持一致
    bind 0.0.0.0 //远程开启
    

#从配置
vim /usr/local/redis/slave/config/redis.conf
    daemonize yes //后台运行
    port 6381 //端口
    pidfile /var/run/redis_6381.pid //进程文件
    logfile "/var/log/redis/redis_6381.log"  //日志文件
    requirepass crm_redis   //redis密码
    slaveof 172.16.2.251 6380  //主节点
    masterauth crm_redis    //主备密码，切换需要保持一致
    bind 0.0.0.0 //远程开启


# 启动查看状态
./redis-server redis.config
> info
#主
# Replication
role:master
connected_slaves:1
slave0:ip=127.0.0.1,port=6381,state=online,offset=17860,lag=0
#从
# Replication
role:slave
master_host:127.0.0.1
master_port:6382

#哨兵配置
#主
vim /usr/local/redis/master/sentinel.conf
    #后台运行 
    daemonize yes
    #日志
    logfile "/var/log/redis/sentinel_26380.log"
    #端口
    port 26380
    #监听主 1代表1个哨兵及以上检测主挂了就切换
    sentinel monitor mymaster 127.0.0.1 6380 2
    #如果3s内mymaster无响应，则认为mymaster宕机了 ，默认30秒 
    sentinel down-after-milliseconds mymaster 3000  
    #如果10秒后,mysater仍没活过来，则启动failover  ，默认3分钟
    sentinel failover-timeout mymaster 10000 
    #redis主节点密码  
    sentinel auth-pass mymaster 123456
#从
vim /usr/local/redis/slave/sentinel.conf
    #后台运行 
    daemonize yes
    #日志
    logfile "/var/log/redis/sentinel_26381.log"
    #端口
    port 26381
    #监听主 1代表1个哨兵及以上检测主挂了就切换
    sentinel monitor mymaster 172.16.2.251 6380 2
    #如果3s内mymaster无响应，则认为mymaster宕机了 ，默认30秒 
    sentinel down-after-milliseconds mymaster 3000  
    #如果10秒后,mysater仍没活过来，则启动failover  ，默认3分钟
    sentinel failover-timeout mymaster 10000 
    #redis主节点密码  
    sentinel auth-pass mymaster 123456
 
#守护进程
cp
vim /etc/init.d/redis
    # chkconfig:   2345 90 10
    # description:  Redis is a persistent key-value database
    REDISPORT=6379
    EXEC=/usr/local/redis/redis-server
    CLIEXEC=/usr/local/redis/redis-cli
    PIDFILE=/var/run/redis_${REDISPORT}.pid
    CONF="/usr/local/redis/redis.conf"

#守护进程
chkconfig --add redis
# 重载 systemctl 单元
systemctl daemon-reload
systemctl start redis
    

    










```


































