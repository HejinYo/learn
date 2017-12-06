# Linux 学习
## 一、系统基本命令
### 1、系统目录结构
一切皆文件，不区分文件和文件夹，不区分文件后缀格式
```
/bin        存放二进制可执行文件，常用命令（ll,cd,rm）
/etc        配置文件
/home       普通用户的家目录，工作空间
/usr        安装的程序，依赖库，文档等；根目录下的/bin,/lib,/sbin都是连接到此目录下的文件 
/opt        一般存放免安装的程序，解压后可直接命令执行，如：tomcat,jdk
/proc       内存文件的映射，可以查询系统信息
/root       root用户工作空间
/dev        接入到系统设备，硬盘，内存，U盘等
/mnt        用于临时挂载其他文件系统，如访问U盘的内容
/var        存放大文件，如：日志文件，默认存放到此目录
/           根目录
├── bin     存放用户二进制文件
├── boot    存放内核引导配置文件
├── dev     存放设备文件
├── etc     存放系统配置文件
├── home    用户主目录
├── lib     动态共享库
├── lost+found  文件系统恢复时的恢复文件
├── media   可卸载存储介质挂载点
├── mnt     文件系统临时挂载点
├── opt     附加的应用程序包
├── proc    系统内存的映射目录，提供内核与进程信息
├── root    root 用户主目录
├── sbin    存放系统二进制文件
├── srv     存放服务相关数据
├── sys     sys 虚拟文件系统挂载点
├── tmp     存放临时文件
├── usr     存放用户应用程序
└── var     存放邮件、系统日志等变化文件

```
### 2、系统操作
> 1、文件和目录
```
cd  /home   -- 进入目录
cd ..   -- 上级目录
cd ../..    -- 上两级目录
cd ~    -- 进入用户主目录 等同直接 cd
cd -    -- 返回上次所在目录
pwd     -- 显示当前工作路径
ls      -- 查看目录中的文件
ls -l   -- 查看目录中文件/文件夹的详细资料，别名：ll
ll -h   -- 友好显示列表中文件大小
tree    -- 树形结构显示目录下的所有文件/文件夹
tree -L 1   -- 树形结构需要显示的树的级别

touch filename    -- 创建一个文件
mkdir /crm   -- 新建文件夹
mkdir -p crm/test/pro      -- 递归创建文件夹

rmdir /crm   -- 删除文件夹
rm test.sh   -- 删除文件
rm -r /crm   -- 递归删除
rm -f /crm   -- 直接删除，不进行确认  

mv filename1 filename2    -- 移动/重命名 文件/文件夹

cp test.sh pro.sh   -- 复制文件
cp /crm/* .     -- 复制目录下所有文件到当前目录，不能复制目录
cp -a /crm/* /test  -- 复制整个目录到新目录

ln -s file lnk  -- 创建一个软链接（类似快捷方式，源文件删除，链接失效）
ln file lnk     -- 创建一个硬链接（相当于对文件的实时拷贝，源文件删除，不影响链接）

```
> 2、文件搜索
```
find /root -name filename      -- 从/root目录中搜索指定名称的文件/文件夹
find /root -type f -name filename   -- 指定搜索文件（文件夹type为d）
which mkdir     -- 搜索命令的二进制文件位置

```
> 3、权限管理（敲黑板：重点！）
>> 需要理解：
```
权限分类：r (可读)，w (可写)，x（可执行）
权限组分类：u （归属人），g (归属组)，o (其他人)

例子：ll
  权限      硬连接数    归属用户    归属组
-rw-r--r--.    1        root       root   31 Dec  7 00:09 crm.sh
drwxr-xr-x.    2        root       root    6 Aug 20  2016 Videos

1、权限 -rw-r--r--  
10个字符分为4个部分：
第一个 "-" 代表这是一个文件（目录则是d）
rw-  代表归属人权限 可读可写
r--  代表归属组权限 可读
r--  代表其他人权限 可读

权限可用十进制来表示：
rw- 对应二进制为 110  转十进制为 6
r-- 对应二进制为 100  转十进制为 4
所以 rw-r--r-- 可表示为 644

```
>> 修改权限("+" 设置权限，使用 "-" 用于取消)
```
chmod ugo+rwx crm.sh    -- 设置文件的归属人，归属组，其他人的权限为可读可写可执行
chmod a+rwx crm.sh      -- 同上命令
chmod +rwx crm.sh       -- 同上命令
chmod a-x crm.sh        -- 取消所有人的可执行权限
chmod 755 crm.sh        -- 设置权限为 rwxr-xr-x，归属人可读可写可执行，归属组和其他人可读可执行
chmod -R a+x /test      -- 递归设置目录及目录下所有文件的可执行权限

chown root crm.sh       -- 设置文件的归属人为root
chown -R root /test     -- 递归设置目录下所有文件的归属人为root
chown root:root crm.sh  -- 设置文件的归属人为root,设置文件的归属组为root
chown :root crm.sh      -- 设置文件的归属组为root

chgrp root crm.sh       -- 设置文件的归属组为root

```
> 4、vim的使用
```
一、普通模式下的操作
vim +2 file -- 进入file的第二行首，没数字默认最后一行。
vim +/hss file -- 进入file 中字符串hss第一次出来的行首。
1、进入插入模式
  i     光标前插入 
  I      光标行首插入 
  a     光标后插入 
  A    光标行尾插入 
  o     光标所在行下插入一行，行首插入 
  O    光标所在行上插入一行，行首插入
  R -- 替换模式，替换光标的字符
2、光标定位
  h,j,k,l    左移，下移，上移，右移
  gg  移至首行
  G    移至最后一行行首 
  nG    移至第 n 行行首 
  ^    移至行首
  $     移至行尾
  w    光标移动到下一个单词首部
  b    光标移动到上一个光标首部
3、替换和删除
  x    删除光标处的字符。
  nx    删除光标到右边N个字符。
  dd    删除光标所在的整行。
  dG    删除当前行到最后一行。
  dw   删除光标后的一个单词

4、复制和粘贴
  yy    当前行复制到缓冲区 
  nyy     当前开始的 n 行复制到缓冲区 
  yG    光标所在行至最后一行复制到缓冲区 
  y1G    光标所在行至第一行复制到缓冲区 
  y$    光标所在位置到行尾复制到缓冲区 
  y^    光标所在位置到行首复制到缓冲区 
  y0    光标所在位置的前一个字符到行首复制到缓冲区 
  p    将缓冲区的内容写到光标的前面或者下一行  
  P    将缓冲区的内容写到光标的前面或者上一行



二、命令行模式下的操作
  :n1,n2 co n3    将 n1 行至 n2行复制到 n3 后面 
  :n1,n2 m n3    将 n1行至 n2 行移动到 n3 后面
  :set number    在编辑文件时显示行号 
  :set nonumber     不在编辑文件时显示行号
  :w file    当前编辑的内容写到 file 中
  :%s/x/y/gi -- 将X替换成Y，g--一行中多个位置，i--不区分大小写

vim底行模式下" ：r ! blkid  " 插入命令结果进去。
shift + v --可视模式，用于选择段落
u -- 撤销操作
Ctrl r -- 恢复操作
vim +3 file -- 进入文件的第三行
vim +/xxx file -- 进入文档并搜索xxx
底行模式 ：/t -- 搜索xxx
  n-- 下一个
  N-- 上一个
  *  立即向前搜索光标处的单词
```
> 5、压缩解压
```
tar --create --file f.tar.gz --gzip a b c --将a b c 三个文件压缩成f.tar.gz
tar --list --file f.tar.gz -- 查看f.tar.gz 中的文件
tar --extract --file f.tar.gz -- 解压 f.tar.gz 文件

bunzip2 f.bz2 -- 解压'f.bz2'的文件
bzip2 f -- 压缩 'f' 的文件
gunzip f.gz -- 解压 'f.gz'的文件
gzip f -- 压缩 'f'的文件
gzip -9 file1 最大程度压缩

rar a f.rar test_file -- 创建 'f.rar' 的包
rar a f1.rar f1 f2 dir1 -- 同时压缩 'f1', 'f2' 以及目录 'dir1'
rar x file1.rar -- 解压rar包
unrar x file1.rar -- 解压rar包

zip f.zip file -- 创建zip格式的压缩包
zip -r f.zip f1 f2 dir1 -- 将几个文件和目录同时压缩成zip格式的压缩包
unzip f.zip -- 解压一个zip格式压缩包

tar
  -c -- 打包
  -v -- 显示过程
  -f -- 指定打包后的文件名
  -z -- 压缩格式 .tar.gz
  -j -- 压缩格式 .bz2
  -x -- 解打包
  -t -- 查看打包内容
tar -cvf a.tar file1 -- 创建一个非压缩的 tarball
tar -cvf a.tar f1 f2 dir1 -- 创建一个包含了 'f1', 'f2' 以及 'dir1'的档案文件
tar -tf a.tar -- 显示一个包中的内容
tar -xvf a.tar -- 释放一个包
tar -xvf a.tar -C /tmp --               将压缩包释放到 /tmp目录下
先打包后压缩，先解压后解包
  tar -cvjf archive.tar.bz2 dir1         创建一个bzip2格式的压缩包
  tar -xvjf archive.tar.bz2                 解压一个bzip2格式的压缩包
  tar -cvzf archive.tar.gz  dir1        创建一个gzip格式的压缩包
  tar -xvzf archive.tar.gz                 解压一个gzip格式的压缩包
file archive.tar.gz -- 检查文件属于什么类型
```

### 3、服务管理
> 软件管理

> 定时任务

> sudo

> 后台执行

> 服务管理

> 防火墙（安全方面）

> sshd 密钥登录

> 查看进程top ps

> 搭建运行环境(jdk,nginx,redis,mysql)

> redis攻击

> docker


### 4、其他
> 翻墙



































