## 使用方法  
运行controller包内的CloudDiskServer    
即可等待客户端的连接  
同时还可以在命令行输入命令来  
1.显示所用的用户信息 2.退出服务端  

## 包内结构
该包共包含三部分：
1. controller  
用于服务端的启动和终止
2. service  
包含所有业务代码  
为客户端提供服务  
注册登录、上传下载文件  
3. repository  
存储用户信息  
存储用户的云盘文件  
