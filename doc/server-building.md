# mysql 8.0 安装（CentOS 7 及以上）
> rpm 安装包

## 1、替换163源（非必要）
```text
# 备份默认源
mv /etc/yum.repos.d/CentOS-Base.repo /etc/yum.repos.d/CentOS-Base.repo.backup

# 下载163源到仓库目录
cd /etc/yum.repos.d
wget http://mirrors.163.com/.help/CentOS7-Base-163.repo

# 清除本地缓存
yum clean all

# 创建本地缓存
yum makecache
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


### 修改默认配置
```text
vim /etc/my.cnf

# 客户端配置
[client]
port=3306
socket=/data/mysqldata/data/mysql.sock
default-character-set = utf8mb4


# 服务器端配置
[mysqld]
---------------------------------------------------------
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

# 目录授权给mysql
chown mysql:mysql /data/mysqldata/data/ -R

# 关闭SELINUX
查看状态：getforce
Enforcing -- 运行
# 关掉
setenforce 0 -- 禁用
setenforce 1 -- 启用
vim /etc/selinux/config    //永久修改
SELINUX=permissive -- 运行但不拦截
SELINUX=disabled -- 关闭服务


# 启动服务
systemctl start mysqld

# 开机自启
systemctl enable mysqld

# 查看状态
systemctl status myslqd

# 查看启动日志(启动失败，排错有用)
journalctl --unit mysqld

```

## 启动成功，
默认在data目录中创建SSL证书和密钥文件等信息，
```text
-rw-r-----. 1 mysql mysql       56 Aug 23 05:36 auto.cnf
-rw-r-----. 1 mysql mysql      155 Aug 23 05:37 binlog.000001
-rw-r-----. 1 mysql mysql       16 Aug 23 05:37 binlog.index
-rw-------. 1 mysql mysql     1680 Aug 23 05:36 ca-key.pem
-rw-r--r--. 1 mysql mysql     1112 Aug 23 05:36 ca.pem
-rw-r--r--. 1 mysql mysql     1112 Aug 23 05:36 client-cert.pem
-rw-------. 1 mysql mysql     1676 Aug 23 05:36 client-key.pem
-rw-r-----. 1 mysql mysql     5961 Aug 23 05:36 ib_buffer_pool
-rw-r-----. 1 mysql mysql 12582912 Aug 23 05:37 ibdata1
-rw-r-----. 1 mysql mysql 50331648 Aug 23 05:37 ib_logfile0
-rw-r-----. 1 mysql mysql 50331648 Aug 23 05:36 ib_logfile1
-rw-r-----. 1 mysql mysql 12582912 Aug 23 05:37 ibtmp1
drwxr-x---. 2 mysql mysql      143 Aug 23 05:36 mysql
-rw-r-----. 1 mysql mysql 25165824 Aug 23 05:37 mysql.ibd
-rw-r-----. 1 mysql mysql        5 Aug 23 05:37 mysql.pid
srwxrwxrwx. 1 mysql mysql        0 Aug 23 05:37 mysql.sock
-rw-------. 1 mysql mysql        5 Aug 23 05:37 mysql.sock.lock
drwxr-x---. 2 mysql mysql     4096 Aug 23 05:36 performance_schema
-rw-------. 1 mysql mysql     1680 Aug 23 05:36 private_key.pem
-rw-r--r--. 1 mysql mysql      452 Aug 23 05:36 public_key.pem
-rw-r--r--. 1 mysql mysql     1112 Aug 23 05:36 server-cert.pem
-rw-------. 1 mysql mysql     1676 Aug 23 05:36 server-key.pem
drwxr-x---. 2 mysql mysql       28 Aug 23 05:36 sys
-rw-r-----. 1 mysql mysql 10485760 Aug 23 05:37 undo_001
-rw-r-----. 1 mysql mysql 10485760 Aug 23 05:37 undo_002

```

```text
# 获取随机root密码：
sudo grep 'temporary password' /var/log/mysqld.log

# 登录
shell> mysql -u root -p

# 修改密码
mysql> ALTER USER 'root'@'localhost' IDENTIFIED BY 'Mysql@2018';

# 刷新配置
mysql>FLUSH PRIVILEGES;

# 下面是其他注意事项，可忽略

在服务器初始启动时，如果服务器的数据目录为空，则会发生以下情况：
服务器已初始化。
SSL证书和密钥文件在数据目录中生成。
validate_password 已安装并已启用。
将'root'@'localhost创建一个超级用户帐户。设置超级用户的密码并将其存储在错误日志文件中。要显示它，请使用以下命令：
shell> sudo grep 'temporary password' /var/log/mysqld.log
通过使用生成的临时密码登录并为超级用户帐户设置自定义密码，尽快更改root密码：
shell> mysql -uroot -p
mysql> ALTER USER 'root'@'localhost' IDENTIFIED BY 'MyNewPass4!';
FLUSH PRIVILEGES;
注意
validate_password 默认安装。实现的默认密码策略validate_password要求密码包含至少一个大写字母，一个小写字母，一个数字和一个特殊字符，并且密码总长度至少为8个字符。
https://dev.mysql.com/doc/refman/8.0/en/linux-installation-yum-repo.html


初始root帐户可能有也可能没有密码。选择以下适用的程序：
如果root帐户存在初始随机密码已过期，请root使用该密码连接到服务器，然后选择新密码。如果数据目录是使用mysqld --initialize初始化的，则可以手动或使用安装程序在安装操作期间无法指定密码。由于密码存在，您必须使用它来连接到服务器。但由于密码已过期，除了选择新密码之外，您不能将该帐户用于任何其他目的，直到您选择一个密码。
如果您不知道初始随机密码，请查看服务器错误日志。
root使用密码 连接到服务器：
shell> mysql -u root -p
Enter password: (enter the random root password here)
选择一个新密码来替换随机密码：
mysql> ALTER USER 'root'@'localhost' IDENTIFIED BY 'new_password';
如果root帐户存在但没有密码，请root 使用无密码连接到服务器，然后分配密码。如果使用mysqld初始化数据目录，则会出现这种情况--initialize-insecure。
root使用无密码 连接到服务器：
shell> mysql -u root --skip-password
分配密码：
mysql> ALTER USER 'root'@'localhost' IDENTIFIED BY 'new_password';
为root帐户分配密码后，只要您使用该帐户连接到服务器，就必须提供该密码。例如，要使用mysql客户端连接到服务器 ，请使用以下命令：
shell> mysql -u root -p
Enter password: (enter root password here)
要使用mysqladmin关闭服务器，请使用以下命令：
shell> mysqladmin -u root -p shutdown
Enter password: (enter root password here)
https://dev.mysql.com/doc/refman/8.0/en/default-privileges.html

```

## 开启远程&修改加密规则
```text

ALTER USER 'root'@'localhost' IDENTIFIED BY 'Redhat@2018' PASSWORD EXPIRE NEVER; 
一般是utf8mb4_general_ci和utf8mb4_bin,前者不区分大小写
修改加密规则

ALTER USER 'root'@'localhost' IDENTIFIED BY 'password' PASSWORD EXPIRE NEVER; 
1
password 为你当前密码。

9.更新 root 用户密码

ALTER USER 'root'@'%' IDENTIFIED WITH mysql_native_password BY 'password'; 
1
password 为你新设置的密码。

10.刷新权限

FLUSH PRIVILEGES; 

方法1

CREATE USER ‘root‘@‘%‘ IDENTIFIED WITH mysql_native_password  BY ‘123123‘;      //修改密码认证方式为mysql_native_password

GRANT ALL ON *.* TO ‘root‘@‘%‘;

方法2

CREATE USER ‘root‘@‘%‘ IDENTIFIED BY ‘123123‘;     //默认的密码认证插件caching_sha2_password

GRANT ALL ON *.* TO ‘root‘@‘%‘; 

ALTER USER ‘root‘@‘%‘ IDENTIFIED WITH mysql_native_password BY ‘123123‘;
```
