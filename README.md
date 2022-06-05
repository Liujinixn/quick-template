
<br>

**请前往发行版下载（稳定版）： [https://gitee.com/liujinxin_ark/quick-template/releases](https://gitee.com/liujinxin_ark/quick-template/releases)**

`master分支处于开发阶段，请colne对应分支的稳定版本。`

 **注意后续更新版本仓库移直至 [https://gitee.com/schema-template/quick-template](http://https://gitee.com/schema-template/quick-template)**   :point_left: 


## 介绍
SpringBoot多模块架构，争对企业级开发设计，已集成认证功能、文档功能，开箱即用。
技术栈： springboot + redis + shiro + swagger 

<br>
<hr>

## 软件架构
SpringBoot多模块 + shiro实现权限认证 + redis实现单点登录控制（可控制账户同时在线人数）+ Swagger文档

![架构](https://img-blog.csdnimg.cn/144c196ab27c4eba9e5fcddcfb1d0a73.png?x-oss-process=image/watermark,type_d3F5LXplbmhlaQ,shadow_50,text_Q1NETiBAQXJr5pa56Iif,size_20,color_FFFFFF,t_70,g_se,x_16)


![在这里插入图片描述](https://img-blog.csdnimg.cn/93fc026f8a484786b18c30069d05a656.png)

<br>
<hr>

## quick-auth-serve 工程
quick-auth-serve 为认证服务，包含系统认证、鉴权，以及系统得用户、角色、权限管理。

shiro相关信息配置可通过quick-config模块下得application-shiro.yml配置，如：令牌前缀、同一个帐号最大会话数、认证授权信息缓存等配置。

该对其他子模块提供了 ShiroUtil 类，通过该类获取当前登录用户信息。

> **注意：**
> 如果需要关闭项目的认证鉴权功能，在 quick-config 工程下的 application-shiro.yml 关闭认证鉴权功能：

![在这里插入图片描述](https://img-blog.csdnimg.cn/e3021e8292624a5fb7f522430f7827b2.png?x-oss-process=image/watermark,type_d3F5LXplbmhlaQ,shadow_50,text_Q1NETiBAQXJr5pa56Iif,size_20,color_FFFFFF,t_70,g_se,x_16)
 

<br>
<hr>

## quick-log-serve 工程
quick-log-serve 为日志服务，包含系统日志访问接口。

该对其他子模块提供了 LogbackController 接口，通过该接口访问系统 html 和 txt 的Logback日志文件。
如：http://localhost:8082/log-server-api/logback/html/2022-03-24?accessKey=F9F09728BBC81DA9&level=info 可访问html日志信息记录

接口： /log-server-api/logback/{type}/{dateTime}?accessKey=F9F09728BBC81DA9&level=info
type参数：日志类型可选值html、txt
dataTime参数： 某一天的日志信息
accessKey参数：令牌（该接口不会参与到  quick-auth-serve 服务的认证鉴权，所以单独开发了这个接口）
level参数： 如果type参数为txt的话，该值生效。即访问的日志文件级别。

> **注意：**
如果不要对外开发该接口，可在 application-log.yml 中关闭该接口。


<br>
<hr>

## quick-common 工程
quick-auth-serve 为常用的工具服务，包含提供redisClient、File操作、Word转PDF、手动Validator验证工具（系统同时还配置全局异常处理方式）等工具类。

<br>
<hr>

## quick-config 工程
quick-condig 系统统一配置服务。

（1）每个子模块都有自己数据源配置，实现各个子模块连接自己的数据源，参考com.quick.config.datasource 数据源配置类 和 application-db.yml 配置文件；**可访问 IP:端口/druid 访问数据源监控**。

（2）各个server模块，需要在 quick-config 工程中下的 application-system.yml 配置 request 前缀：

```
# 设置子模块请求前缀
request:
  prefix:
    auth_server: /auth-server-api
    log_server: /log-server-api
    base_server: /base-server-api
```

（3）系统配置了 sentinel 实现接口限流配置，项目启动后会自动注册到 sentinel 服务中，（自动注册配置类 com.quick.config.sentinel.SentinelConfig），即系统扫描所有接口，注册到 sentinel 服务（sentinel 程序在 control 目录下）。

> **注意：**
如果需要关闭接口限流功能，可在 application-sentinel.yml 中关闭该功能。

（4）swagger配置，各个对外暴露的接口工程，都实现了swagger分组功能，配置类 com.quick.config.swagger.SwaggerConfig，项目启动后访问 **IP:端口/doc.html** 。
![在这里插入图片描述](https://img-blog.csdnimg.cn/0018a7f48a67431198af5947abb9eb86.png?x-oss-process=image/watermark,type_d3F5LXplbmhlaQ,shadow_50,text_Q1NETiBAQXJr5pa56Iif,size_20,color_FFFFFF,t_70,g_se,x_16)

<br>
<hr>

## quick-base-serve 工程
该工程为一个案例工程，如项目中需要开发一个订单模块，可按照该模块创建。

> **注意：** 
不同的子模块需要在config-server工程中配置各自的数据源；
不同的子模块提供controller接口需要在config-server工程中配置各自的swagger配置分组
不同的子模块可自行配置 @RequestMapping 前缀，在config-server工程中的 application-system.yml配置。

<br>
<hr>

## quick-web-serve 工程
该工程为启动工程，通过QuickWebApplication启动器启动项目。

<br>
<hr>

## control 目录
项目涉及到的其他服务，如：sentinel-dashboard-1.8.3.jar



