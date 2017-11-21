# node.js 安装
## 下载
 [https://nodejs.org/en/download/](https://nodejs.org/en/download/)
## 配置
> 解压到C:\java\tools\Node

> 创建两个文件夹，node_cache,node_global

> 配置环境变量:
+ NODE_HOME C:\java\tools\Node
+ Path 添加 %NODE_HOME%;
> 配置参数：
+ npm config set prefix "C:\java\tools\Node\node_global
+ npm config set cache "C:\java\tools\Node\node_cache"
> 安装cnpm:
+ npm install -g cnpm --registry=https://registry.npm.taobao.org
+ cnpm -v

> 运行
+ cnpm install
+ npm run dev

> 编译
+ npm run build