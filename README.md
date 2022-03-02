
# quick-template

#### 介绍
springboot多模块架构，争对企业级开发设计，已集成认证功能，开箱即用，无需多余配置

#### 软件架构
SpringBoot多模块 + shiro实现权限认证 + redis实现单点登录控制（可控制账户同时在线人数）
![<img src>](https://img-blog.csdnimg.cn/0a8003279fed4b919a9cd11a11030069.png?x-oss-process=image/watermark,type_d3F5LXplbmhlaQ,shadow_50,text_Q1NETiBAQXJr5pa56Iif,size_20,color_FFFFFF,t_70,g_se,x_16)

<br>
1. 系统中quick-auth-serve 为认证服务，后续开发新的模块时，只需要新建一个 类似于 quick-base-serve模块工程即可 ，灵活管理项目架构
不同得子模块需要在 config模块中 新建 自己得数据源，即已实现了 各个子模块实现自己得数据源连接不同得数据库操作。

<br>
2. 不同得子模块在swagger中对应不同得模块如下图：

![在这里插入图片描述](https://img-blog.csdnimg.cn/0018a7f48a67431198af5947abb9eb86.png?x-oss-process=image/watermark,type_d3F5LXplbmhlaQ,shadow_50,text_Q1NETiBAQXJr5pa56Iif,size_20,color_FFFFFF,t_70,g_se,x_16)


