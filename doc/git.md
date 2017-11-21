# git常用命令
> 设置Git
+  git config --global user.name "hejinyo"  
+  git config --global user.email hejinyo@gmail.com

> 添加文件
+  git add . 
+  git add my_file,other_file
+  
> 提交文件
+  git commit -m "initial commit"

> 推送
+  初始化配置：
+  git remote add origin https://your_username@bitbucket.org/your_username/name_of_remote_repository.git 
+  推送：
+  git push origin master

+  
> 创建本地Git仓库
+  $ mkdir document
+  $ git init
+  
> 编写readme.text
+  touch readme.text
+  
> 添加文件到仓库
+  git add readme.txt
+  
> 提交文件
+  git commit -m "initial commit"

> 修改文件
+  vim readme.text
+  
> 查看仓库文件修改状态
+  git status
+  git diff readme.txt //查看修改内容
+  
> 提交修改（和提交文件一样）

```$xslt
git log //查看版本库状态
git rm test.txt //删除仓库版本中的文件，提交生效
git checkout --test.txt //撤销删除
git remote add origin https://github.com/HejinYo/document.git // 关联远程库
git remote remove origin // 删除关联
git push -u origin master  //第一次推送master分支的所有内容；
git push origin master  //以后的提交
git glone git@github.com:HejinYo/document.git //克隆
```