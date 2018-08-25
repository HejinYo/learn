## 一、OAuth2 根据使用场景不同，分成了4种模式

授权码模式（authorization code）
简化模式（implicit）
密码模式（resource owner password credentials）
客户端模式（client credentials）

###  二、OAuth2 角色
+ Authorization Server：认证服务器，进行认证和授权
+ Resource Server：资源服务器，保护受保护的资源
+ client：请求资源服务器资源的第三方程序,客户端同时也可能是一个资源服务器
+ resource owner：资源所有者（指用户）


### 三、Oauth 2.0 Provider
> Authorization Server:
  + AuthorizationEndpoint:进行授权的服务，Default URL: /oauth/authorize
  + TokenEndpoint：获取token的服务，Default URL: /oauth/token
> Resource Server:
  + OAuth2AuthenticationProcessingFilter：给带有访问令牌的请求加载认

### 四、Authorization Server
#### 1、@EnableAuthorizationServer：
> 声明一个认证服务器，当用此注解后，应用启动后将自动生成几个Endpoint：
（注：其实实现一个认证服务器就是这么简单，加一个注解就搞定，当然真正用到生产环境还是要进行一些配置和复写工作的。）

+ /oauth/authorize：验证
+ /oauth/token：获取token
+ /oauth/confirm_access：用户授权
+ /oauth/error：认证失败
+ /oauth/check_token：资源服务器用来校验token
+ /oauth/token_key：如果jwt模式则可以用此来从认证服务器获取公钥

#### 2、AuthorizationServerConfigurer
1、包含三种配置
+ ClientDetailsServiceConfigurer：client客户端的信息配置，client信息包括：clientId、secret、scope、authorizedGrantTypes、authorities
>>（1）scope：表示权限范围，可选项，用户授权页面时进行选择

>>（2）authorizedGrantTypes：有四种授权方式

>>> Authorization Code：用验证获取code，再用code去获取token（用的最多的方式，也是最安全的方式）

>>> Implicit: 隐式授权模式

>>> Client Credentials (用來取得 App Access Token)

>>> Resource Owner Password Credentials

>>（3）authorities：授予client的权限：这里的具体实现有多种，in-memory、JdbcClientDetailsService、jwt等

+ AuthorizationServerSecurityConfigurer：声明安全约束，哪些允许访问，哪些不允许访问

+ AuthorizationServerEndpointsConfigurer：声明授权和token的端点以及token的服务的一些配置信息，比如采用什么存储方式、token的有效期等


client的信息的读取：在ClientDetailsServiceConfigurer类里面进行配置，可以有in-memory、jdbc等多种读取方式。

jdbc需要调用JdbcClientDetailsService类，此类需要传入相应的DataSource.

2、下面再介绍一下如何管理token:
+ AuthorizationServerTokenServices接口:声明必要的关于token的操作
> （1）当token创建后，保存起来，以便之后的接受访问令牌的资源可以引用它。

> （2）访问令牌用来加载认证

+ token存储方式共有三种分别是：
>（1）InMemoryTokenStore：存放内存中，不会持久化

>（2）JdbcTokenStore：存放数据库中

>（3）Jwt: json web token,详见 http://blog.leapoahead.com/2015/09/06/understanding-jwt/

+ 授权类型：
可以通过AuthorizationServerEndpointsConfigurer来进行配置，默认情况下，支持除了密码外的所有授权类型。相关授权类型的一些类
>（1）authenticationManager：直接注入一个AuthenticationManager，自动开启密码授权类型

>（2）userDetailsService：如果注入UserDetailsService，那么将会启动刷新token授权类型，会判断用户是否还是存活的

>（3）authorizationCodeServices：AuthorizationCodeServices的实例，auth code 授权类型的服务

>（4）implicitGrantService：imlpicit grant

>（5）tokenGranter：

+ endpoint的URL的配置：

>（1）AuthorizationServerEndpointsConfigurer的pathMapping()方法，有两个参数，第一个是默认的URL路径，第二个是自定义的路径

>（2）WebSecurityConfigurer的实例，可以配置哪些路径不需要保护，哪些需要保护。默认全都保护。

#### 3、自定义UI:

（1）实际项目开发中，我们可能需要自定义的登录页面和认证页面。只需要创建一个login.html,
利用thymeleaf结合springsecurity的配置进行自定义登录页面，因为默认的oauth2的登录点是/login，
当然这个是可以根据自己的需求进行更改的 。

（2）另外一个是授权页。此页面可以参考源码里的实现，自己生成一个controller的类，
再创建一个对应的web页面即可实现自定义的功能，后续文章会为大家介绍如何正确的生成授权页面，
目前我只有2种方式 或许还有更多方法 需要等大家一起探讨探讨。

####  4、Resource Server：保护资源，需要令牌才能访问
在配置类上加上注解@EnableResourceServer即启动。使用ResourceServerConfigurer进行配置：

+ （1）tokenServices：ResourceServerTokenServices的实例，声明了token的服务

+ （2）resourceId：资源Id，由auth Server验证。

+ （3）其它一些扩展点，比如可以从请求中提取token的tokenExtractor

+ （4）一些自定义的资源保护配置，通过HttpSecurity来设置

使用token的方式也有两种：

+ （1）Bearer Token（https传输方式保证传输过程的安全）:主流

+ （2）Mac（http+sign）

#### 5、如何访问资源服务器中的API？
如果资源服务器和授权服务器在同一个应用程序中，并且您使用DefaultTokenServices，那么您不必太考虑这一点，因为它实现所有必要的接口，因此它是自动一致的。如果您的资源服务器是一个单独的应用程序，那么您必须确保您匹配授权服务器的功能，并提供知道如何正确解码令牌的ResourceServerTokenServices。与授权服务器一样，您可以经常使用DefaultTokenServices，并且选项大多通过TokenStore（后端存储或本地编码）表示。

（1）在校验request中的token时，使用RemoteTokenServices去调用AuthServer中的/auth/check_token。

（2）共享数据库，使用Jdbc存储和校验token，避免再去访问AuthServer。

（3）使用JWT签名的方式，资源服务器自己直接进行校验，不借助任何中间媒介

### 五、oauth client
在客户端获取到token之后，想去调用下游服务API时，为了能将token进行传递，可以使用RestTemplate.然后使用restTemplate进行调用Api。

注：scopes和authorities的区别：

scopes是client权限，至少授予一个scope的权限，否则报错。

authorities是用户权限。