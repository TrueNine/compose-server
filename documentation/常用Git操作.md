# 常用 Git 操作

## 添加多个远程仓库推送地址
```sh
# 添加一个名为 all 的远程仓库，使用其中一个仓库的 URL 作为基础
git remote add all https://github.com/TrueNine/compose-server.git

# 为 all 添加多个推送地址
git remote set-url --add --push all https://github.com/TrueNine/compose-server.git
git remote set-url --add --push all https://codeup.aliyun.com/63fc0978360d441ff22c91e5/TrueNine/compose-server.git
```

## 解决 git 不安全的文件归属问题
```shell
git config --global --add safe.directory <你的项目路径>
``` 
