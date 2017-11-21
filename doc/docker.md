# docker && jenkins 安装配置

## 一.安装docker
> 1.删除原来docker相关
```$xslt
$ yum remove docker* -y
$ yum remove container-selinux -y
```
> 2.更新yum源
```
$ yum install -y wget
$ cd /etc/yum.repos.d
$ wget http://mirrors.163.com/.help/CentOS7-Base-163.repo
$ yum clean all
$ yum makecache
```

> 3.出错执行
```$xslt
$ yum install -y deltarpm
$ rpm --import https://yum.dockerproject.org/gpg
```

> 4.编辑生成docker的yum源文件
```$xslt
$ tee /etc/yum.repos.d/docker.repo <<-'EOF'
[dockerrepo]
name=Docker Repository
baseurl=https://yum.dockerproject.org/repo/main/centos/7/
enabled=1
gpgcheck=1
gpgkey=https://yum.dockerproject.org/gpg
EOF
```

> 4.安装
> [官方参考](https://docs.docker.com/engine/installation/linux/docker-ce/centos/#install-docker-ce-1 "Markdown")
>> 手动安装Docker
```$xslt
$ yum install docker-engine
```
>> 阿里云脚本
```$xslt
$ curl -sSL http://acs-public-mirror.oss-cn-hangzhou.aliyuncs.com/docker-engine/internet | sh -
```
>> 5.启动docker
```$xslt
$ systemctl start docker
$ docker info
```
# 二、Jenkins 安装
点击链接 [Jenkins 安装](http://m.blog.csdn.net/mmd0308/article/details/77206563 "Markdown")
> 1.下载镜像
```$xslt
docker pull jenkins
```
>> 速度较慢，说明不是阿里云的仓库，重新配置docker吧

> 2、启动镜像
>> 创建运行文件夹,配置权限
```$xslt
mkidr /var/jenkins
chown -R 1000:1000 jenkins/
```
>> 启动
```$xslt
$ docker run -itd -p 8080:8080 -p 50000:50000 --name jenkins --privileged=true  -v /var/jenkins:/var/jenkins_home jenkins
```
+ -d 后台运行，否则就是命令行交互式运行
+ -p 8080:8080 -p 50000:50000 进行端口映射
+ --privileged=true 在CentOS7中的安全模块selinux把权限禁掉了，参数给容器加特权。
+ -v /home/hzq/jenkins:/var/jenkins_home 磁盘挂载
>> 启动出现的问题
会卡在启动页面
```$xslt
vim /var/jenkinds/hudson.model.UpdateCenter.xml
将url替换成 
https://mirrors.tuna.tsinghua.edu.cn/jenkins/updates/current/update-center.json
或 
skye.hejinyo.cn/update-center.json
关闭容器，重新启动
```

# 三、其他命令
>> [docker 命令学习](http://www.runoob.com/docker/docker-pull-command.html)
```$xslt
systemctl start docker.service  //启动docker
docker images   //查看下载的镜像
docker ps   //查看当前运行的容器
docker logs -f CONTAINER_ID/name    //容器日志
docker inspect CONTAINER_ID/name //容器状态
docker stop CONTAINER_ID/name  //停止容器
docker rm CONTAINER_ID/name  //删除容器
docker stop $(docker ps -q) //停用全部运行中的容器
docker rm $(docker ps -aq)  //删除全部容器
```