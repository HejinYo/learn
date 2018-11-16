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

# Hello Web Security Java配置
## 第一步是创建Spring Security Java配置。配置创建一个Servlet过滤器，称为springSecurityFilterChain负责应用程序内的所有安全性（保护应用程序URL，验证提交的用户名和密码，重定向到登录表单等）。您可以在下面找到Spring Security Java配置的最基本示例：
 ```java
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.context.annotation.*;
import org.springframework.security.config.annotation.authentication.builders.*;
import org.springframework.security.config.annotation.web.configuration.*;

@EnableWebSecurity
public class WebSecurityConfig implements WebMvcConfigurer {

	@Bean
	public UserDetailsService userDetailsService() throws Exception {
		InMemoryUserDetailsManager manager = new InMemoryUserDetailsManager();
		manager.createUser(User.withDefaultPasswordEncoder().username("user").password("password").roles("USER").build());
		return manager;
	}
}
```
这种配置确实没什么用，但它做了很多。您可以在下面找到以下功能的摘要：
>要求对应用程序中的每个URL进行身份验证

>为您生成登录表单

>允许具有Username 用户和密码 密码的用户使用基于表单的身份验证进行身份验证

>允许用户注销

>CSRF攻击预防

>会话固定保护

>安全标头集成

>>  用于安全请求的 HTTP严格传输安全性

>>  X-Content-Type-Options集成

>>  缓存控制（稍后可由应用程序覆盖以允许缓存静态资源）

>>  X-XSS-Protection集成

>>  X-Frame-Options集成有助于防止Clickjacking

>与以下Servlet API方法集成

>>  HttpServletRequest的＃getRemoteUser（）
>>  HttpServletRequest.html＃getUserPrincipal（）
>>  HttpServletRequest.html＃的isUserInRole（java.lang.String中）
>>  HttpServletRequest.html＃login（java.lang.String，java.lang.String）
>>  HttpServletRequest.html＃注销（）

## HttpSecurity
到目前为止，我们的WebSecurityConfig仅包含有关如何验证用户身份的信息。Spring Security如何知道我们要求所有用户进行身份验证？Spring Security如何知道我们想要支持基于表单的身份验证？原因是WebSecurityConfigurerAdapter在configure(HttpSecurity http)方法中提供了一个默认配置，如下所示：
### 5.2基本默认配置
```java
@EnableWebSecurity
public class WebSecurityConfig implements WebMvcConfigurer {

    protected void configure(HttpSecurity http) throws Exception {
	    http
		    .authorizeRequests()
			    .anyRequest().authenticated()
			    .and()
		    .formLogin()
			    .and()
		    .httpBasic();
    }
}
```
上面的默认配置：

+ 确保对我们的应用程序的任何请求都要求用户进行身份验证
+ 允许用户使用基于表单的登录进行身份验证
+ 允许用户使用HTTP基本身份验证进行身份验证
### 5.3表单登录

```java

@EnableWebSecurity
public class WebSecurityConfig implements WebMvcConfigurer {

    protected void configure(HttpSecurity http) throws Exception {
    	http
    		.authorizeRequests()
    			.anyRequest().authenticated()
    			.and()
    		.formLogin()
    			.loginPage("/login") // 指定登录页面的位置
    			.permitAll();        // formLogin().permitAll()方法允许为与基于表单的登录相关联的所有URL授予对所有用户的访问权限。
    }
}
```
```html
<c:url value="/login" var="loginUrl"/>
<form action="${loginUrl}" method="post">       <!--对/loginURL 的POST 将尝试对用户进行身份验证-->
	<c:if test="${param.error != null}">        <!-- 如果查询参数error存在，则尝试进行身份验证并失败 -->
		<p>
			Invalid username and password.
		</p>
	</c:if>
	<c:if test="${param.logout != null}">       <!-- 如果查询参数logout存在，则表示用户已成功注销 -->
		<p>
			You have been logged out.
		</p>
	</c:if>
	<p>
		<label for="username">Username</label>
		<input type="text" id="username" name="username"/>	<!-- 用户名必须作为名为username的HTTP参数出现 -->
	</p>
	<p>
		<label for="password">Password</label>
		<input type="password" id="password" name="password"/>	<!-- 密码必须作为名为password的HTTP参数出现 -->
	</p>
	<input type="hidden"                        <!-- -->
		name="${_csrf.parameterName}"
		value="${_csrf.token}"/>
	<button type="submit" class="btn">Log in</button>
</form>
```

### 5.4授权请求
我们可以通过向http.authorizeRequests()方法添加多个子项来指定URL的自定义要求
```java
@EnableWebSecurity
public class WebSecurityConfig implements WebMvcConfigurer {
    protected void configure(HttpSecurity http) throws Exception {
    	http
    	    // 该方法有多个子项，http.authorizeRequests()每个匹配器按其声明的顺序进行考虑。
    		.authorizeRequests()                          
    		    // 我们指定了任何用户都可以访问的多种URL模式。具体来说，如果URL以“/ resources /”开头，等于“/ signup”或等于“/ about”，则任何用户都可以访问请求                                      
    			.antMatchers("/resources/**", "/signup", "/about").permitAll()
    			
    			// 任何以“/ admin /”开头的URL都将仅限于具有“ROLE_ADMIN”角色的用户。您会注意到，由于我们正在调用该hasRole方法，因此我们不需要指定“ROLE_”前缀                  
    			.antMatchers("/admin/**").hasRole("ADMIN")
    			
    			// 任何以“/ db /”开头的URL都要求用户同时拥有“ROLE_ADMIN”和“ROLE_DBA”。您会注意到，由于我们使用的是hasRole表达式，因此我们不需要指定“ROLE_”前缀                                      
    			.antMatchers("/db/**").access("hasRole('ADMIN') and hasRole('DBA')")
    			
    			// 任何尚未匹配的URL仅要求对用户进行身份验证            
    			.anyRequest().authenticated()                                                   
    			.and()
    		// ...
    		.formLogin();
    }
}

```

### 5.5处理注销
使用时WebSecurityConfigurerAdapter，会自动应用注销功能。默认情况下，访问URL /logout将通过以下方式记录用户：

+ 使HTTP会话无效
+ 清理已配置的任何RememberMe身份验证
+ 清除 SecurityContextHolder
+ 重定向到 /login?logout

```java

@EnableWebSecurity
public class WebSecurityConfig implements WebMvcConfigurer {
    protected void configure(HttpSecurity http) throws Exception {
    	http
    	     // 提供注销支持。使用时会自动应用WebSecurityConfigurerAdapter。
    		.logout()
    		    
    		     // 触发注销的URL（默认为/logout）。如果启用了CSRF保护（默认），则该请求也必须是POST
    			.logoutUrl("/my/logout")
    			
    			 // 注销后重定向到的URL。默认是/login?logout
    			.logoutSuccessUrl("/my/index") 
    			
    			// 我们指定一个自定义LogoutSuccessHandler。如果指定了，logoutSuccessUrl()则忽略
    			.logoutSuccessHandler(logoutSuccessHandler)
    			
    			// 指定HttpSession在注销时是否使其无效。默认情况下这是真的。配置SecurityContextLogoutHandler封面
    			.invalidateHttpSession(true)
    			
    			// 添加一个LogoutHandler。默认情况下SecurityContextLogoutHandler添加为最后一个LogoutHandler
    			.addLogoutHandler(logoutHandler)
    			
    			//允许指定在注销成功时删除的cookie的名称。这是CookieClearingLogoutHandler显式添加的快捷方式
    			.deleteCookies(cookieNamesToClear) 
    			.and();
    }

}
```
#### 5.5.1 LogoutHandler
通常，LogoutHandler 实现指示能够参与注销处理的类。预计将调用它们以进行必要的清理。因此，他们不应该抛出异常。提供了各种实现：

+ PersistentTokenBasedRememberMeServices
+ TokenBasedRememberMeServices
+ CookieClearingLogoutHandler
+ CsrfLogoutHandler
+ SecurityContextLogoutHandler

LogoutHandler流畅的API 不是直接提供实现，而是提供了快捷方式，提供了各自的LogoutHandler实现。例如，deleteCookies()允许指定在注销成功时要删除的一个或多个cookie的名称。与添加a相比，这是一种快捷方式 CookieClearingLogoutHandler

#### 5.5.2 LogoutSuccessHandler
该LogoutSuccessHandler被成功注销后调用LogoutFilter，来处理如重定向或转发到相应的目的地。
请注意，界面几乎与之相同，LogoutHandler但可能引发异常。

提供以下实现：
+ SimpleUrlLogoutSuccessHandler
+ HttpStatusReturningLogoutSuccessHandler

如上所述，您无需SimpleUrlLogoutSuccessHandler直接指定。相
反，流畅的API通过设置提供快捷方式logoutSuccessUrl()。
这将设置SimpleUrlLogoutSuccessHandler封底。发生注销后，提供的URL将重定向到。默认是/login?logout。

本HttpStatusReturningLogoutSuccessHandler可以在REST API类型场景有趣。成功注销后，
LogoutSuccessHandler 您可以提供要返回的纯HTTP状态代码，而不是重定向到URL 。如果未配置，则默认返回状态代码200。








