# Spring WEB 学习

## 1 MVC框架的整合

环境搭建请参照sweb项目

### 1.1 Spring MVC 知识概要

#### 1.1.1 为什么要整合MVC框架？

```markdown

1. MVC框架提供了控制器(Controller)用于调用业务(Service) DAO ---> SERVICE
2. MVC框架可以做出请求响应的处理
3. MVC框架可以接收请求参数
4. MVC框架可以控制程序的运行流程
5. MVC框架可以帮助我们做视图解析(JSP JSON Freemarker Thyemeleaf)
```

#### 1.1.2 Spring框架可以整合哪些MVC框架？

```markdown
1. struts1
2. webwork
3. jsf
4. struts2
5. springMVC
```

#### 1.1.3 Spring整合MVC框架的核心思路

**准备工厂**

思路分析

```markdown
Spring工厂是Spring最为核心的内容，有了Spring工厂我们才能完成依赖注入、AOP、整合DAO、事务等相关的功能
回顾之前，在我们的开发过程中，我们的测试方法是写在我们的测试类中的，我们的第一步，永远是创建这个ApplicationContext的，因为只有创建了ApplicationContext这个对象，我们才拥有了Spring的工厂，才能完成我们的后续操作。
ApplicationContext ctx = new ClassPathXmlApplicationContext("application-config.xml");

1.但是现在对于我们来讲，我们要整合MVC框架了，要进行WEB业务的开发了，那么我们就要理解，如何在Web开发的过程中，创建我们的Spring工厂了。
ApplicationContext ctx = new WebXmlApplicationContext();

2.我们之前讲过这个Spring工厂是一个重量级的对象，十分吃内存的，也不宜创建多个，那么我们在web开发的过程中，工厂创建完成后，如何保证工厂唯一，同时被共用呢？
答：MVC涉及到web开发，那web开发涉及到了几个典型的作用域分别是，Request、Session、ServletContext(Application)，这几个作用域专门用于存储对象的，那我们将一个对象存储在ServletContext中，会有什么样的效果啊？因为这个作用域是整个Web应用唯一的，那我们把ApplicationContext存放到咱们的这个ServletContext的作用域中，这不就全局唯一了嘛
ServletContext.setAttribute("applicationContext",applicationContext);

3.如何保证唯一呢？确保这个对象只被创建一次呢？
答：这个ServletContext是不是全局唯一的啊，那我们在其创建的同时，将我们的ApplicationContext也进行创建，那么是不是就也是全局唯一的啦！

4.那我怎么知道ServletContext什么时候创建的啊？我不确定这个，我怎么知道在哪个时机去创建我们这个Spring工厂啊？
答：这个就要借助到Web开发过程中的另一个对象来帮助我们完成了，ServletContextListener，这个监听器有什么特点啊？他会监听这个ServletContext是否被创建，一旦ServletContext被创建，那么就会被这个监听器监听到，而且这个监听只会被执行一次，因为这个ServletContext只会创建一次，那么我们就在这个ServletContextListener中创建我们的这个Spring工厂，就能确保这个对象只被创建一次，且全局唯一。
```

小小的总结

```mark
1.首先在ServletContext中创建我们的Spring工厂，这样就能保证我们的工厂是唯一的
 
2.那创建完成我们的Spring工厂之后，怎么保证这个工厂可以被大家共用啊？
我们在创建完Spring工厂之后就把这个工厂对象存入到这个ServletContext中，供后续大家的使用

3.那你能想到这样子去操作，来解决Spring工厂的创建还有唯一及共用的问题，那么人家Spring也早就想到了
所以不用上面两步，根本就不需要我们进行操作，Spring的工程师们已经帮我们完成了，呐，Spring封装了一个ContextLoaderListener供我们使用

4.ContextLoaderListener
核心：1.创建工厂、2.将工厂存入ServletContext中
```

ContextLoaderListener

```markdown
1.那么ContextLoaderListener怎么使用？
在web.xml进行配置
<listener>
	<listener-class>org.springframework.web.context.ContextLoaderListener</listener-class>
</listener>
小细节：
作为Spring提供了的ContextLoaderListener它是帮我们封装
ApplicationContext context = new WebXmlApplicationContext("applicationContext.xml");这个代码的，但是Spring工厂的配置文件，还是需要我们进行自己创建的，而这个配置文件作为Spring的配置文件，文件名称还有放置位置他这个ContextLoaderListener它是不知道的，
那么在这个listener后面我们就要进行这个配置文件的说明
<context-param>
	<param-name>contextConfigLocation</param-name>
	<param-value>classpath:applicationContext.xml</param-value>
</context-param>
```



**代码整合**

先来看看控制器，这个控制器可以是SpringMVC的Controller或者是Struts2的Action，因为这个控制器的功能是比较单一的，假如我们有一个类：

```java
public class XXXController|XXXAction{
  private XxxService xxxService;
}
/**
	那么这个类主要的功能有：
		1.接收client端的请求参数
		2.调用service对象
		3.进行流程的跳转(如果是Ajax的话，那么他就是响应JSON)
	其中有一个非常重要的操作，那么就是调用Service层，假定没有这步，那么他也没必要去接收参数，进行流程跳转了，
	所以他依赖Service，有依赖就要用到我们的Spring工厂了，我们就需要将Service进行依赖注入
*/
```

那上面的描述就是我们使用Spring进行框架整合的核心功能，将service对象注入控制器对象

## 2 Spring与Struts2框架的整合

### 2.1 思路分析

```markdown
1.Struts2中的Action需要通过Spring的依赖注入，获得Service对象。
```

### 2.2 整合编码

#### 2.2.1 搭建开发环境

引入相关依赖

```markdown
需要Spring和Struts2的相关的依赖，Spring我们刚开始已经引入了，那就来整Struts2的依赖吧
```

```xml
<!-- spring与struts2 -->
<!-- 这个jar包就是为了完成struts2与spring的整合的-->
<dependency>
  <groupId>org.apache.struts</groupId>
  <artifactId>struts2-spring-plugin</artifactId>
  <version>2.3.8</version>
</dependency>
```

引入相应配置文件

- applicationContext.xml

- struts.xml

- log4j.properties

初始化配置

- Spring(ContextLoaderListener ---> web.xml)
- Struts2(Filter ---> web.xml)

```xml
<!DOCTYPE web-app PUBLIC
 "-//Sun Microsystems, Inc.//DTD Web Application 2.3//EN"
 "http://java.sun.com/dtd/web-app_2_3.dtd" >

<web-app xmlns="http://xmlns.jcp.org/xml/ns/javaee"
    xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
    xsi:schemaLocation="http://xmlns.jcp.org/xml/ns/javaee http://xmlns.jcp.org/xml/ns/javaee/web-app_3_1.xsd"
    version="3.1" metadata-complete="true">
  <display-name>Archetype Created Web Application</display-name>

  <listener>
    <listener-class>org.springframework.web.context.ContextLoaderListener</listener-class>
  </listener>
  <context-param>
    <param-name>contextConfigLocation</param-name>
    <param-value>classpath:applicationContext.xml</param-value>
  </context-param>

  <filter>
    <filter-name>struts2</filter-name>
    <filter-class>org.apache.struts2.dispatcher.ng.filter.StrutsPrepareAndExecuteFilter</filter-class>
  </filter>

  <filter-mapping>
    <filter-name>struts2</filter-name>
    <url-pattern>/*</url-pattern>
  </filter-mapping>
  
</web-app>
```

#### 2.2.2 编码环节

第一步，开发Service对象

```markdown
最终在Spring配置文件中创建Service对象
<bean id="userService" class="club.musician.struts2.service.impl.UserServiceImpl"></bean>
```

第二步，开发Action对象

```java
package club.musician.struts2.action;

import club.musician.struts2.service.UserService;
import com.opensymphony.xwork2.Action;

public class RegAction implements Action {

    private UserService userService;

    public UserService getUserService() {
        return userService;
    }

    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    @Override
    public String execute() throws Exception {
        userService.register();
        return Action.SUCCESS;
    }
}
```

第三步，配置文件

Spring配置(applicationContext.xml)

```xml
<bean id="regAction" class="club.musician.struts2.action.RegAction" scope="prototype">
  <property name="userService" ref="userService"></property>
</bean>
```

Struts2配置(struts.xml)

```xml
<package name="basePackage" extends="struts-default">
  <!--
            name：如果这里配置了name为reg的话，那么以后用户写访问路径的时候就要加上reg
            class：会通过这个class创建对象来为用户服务

            那么这个RegAction这个对象是不是已经有了啊？
            答：是的，已经在Spring工厂已经被创建好了，所以这里就不要这样写了，
            我们直接写在Spring工厂中创建好的对象的ID就行了！
         -->
  <!--        <action name="reg" class="club.musician.struts2.action.RegAction">-->
  <action name="reg" class="regAction">
    <result name="success">/index.jsp</result>
  </action>
</package>
```

mac环境

```markdown
apache-tomcat-8.5.53 % cd bin 
bin % chmod u+x *.sh
```

## 3 Spring+Struts2+Mybatis

```markdown
1. Controller
2. Service
3. DAO
4. DB
不同的层次有不同的框架，struts2解决的是Controller层的问题，Spring可以出现在Controller，也可以出现在Service层，也可以出现在DAO层，Mybatis是用在DAO层
```

### 3.1 SSM整合编码

1. 引入相关jar包

   Spring(core、beans、context、expression、web、aop、aspectj、tx...)

   Struts2(struts-spring-plugin...)

   Mybatis(mybatis、mybatis-spring...)

   DB(mysql-jdbc...)

2. 引入对应配置文件

   applicationContext.xml

   struts.xml

   log4j

3. 初始化配置文件

   ```xml
   web.xml
   
   监听用以创建Spring工厂：ContextLoaderLisntener
   指定Spring工厂配置文件：contextConfigLocation
   
   配置struts2过滤器：strutsPreapareAndExecuteFilter
   过滤器拦截路径：filter-mapping-url-pattern="/*"
   ```

4. DAO(Spring+Mybatis)

   ```markdown
   一. 配文件的配置
   	1. DataSource
   	2. SqlSessionFactory ------> SqlSessionFactoryBean
   			1). dataSource
   			2). typeAliases
   			3). mapperLocation
   	3. MapperScannerConfigure 创建DAO接口实现类
   			1). SqlSessionFactory
   			2). daoLocation
   二. 编码			
   	1. entity
   	2. table
   	3. DAO接口
   	4. Mapper.xml
   ```

5. Service

   ```markdown
   最重要的是通过AOP的方式为Service添加事务的额外功能
   1. 原始对象
   2. 额外功能 ----> DataSourceTransactionManner ---> druid.dataSource
   3. 切入点+事务属性		@Transactional
   4. 开启Spring注解事务		<tx:annotation-driven>
   ```

6. Controller

   ```markdown
   Spring+Struts2的整合
   1. 开发控制器 implements Action 注入Service
   2. Spring的配置文件
   		1). 注入Service
   		2). scope=prototype
   3. struts.xml
   		<action class="Spring配置文件中action对应的id">
   ```

## 4 Spring多配置文件

咱么这个applicationContext.xml内容已经狠多了，在后续的开发中，还会增加更多的配置，后面对我们的维护来说，难度特别大，多配置文件的需求就特别明显。

```markdown
Spring会根据需要，把配置文件分门别类的防止在多个配置文件中，便于后续的管理及维护

DAO				--->			applicationContext-dao.xml
Service		--->			applicationContext-service.xml
Action		--->			applicationContext-action.xml

注意：虽然提供了多个配置文件，但是后续应用的过程中，还是需要进行整合的
```

整合的方式

```markdown
一、通配符的方式
1. 非web环境
		ApplicationContext ctx = new ClassPathXmlApplicationContext("/applicationContext-*.xml")
2. web环境中
		在web.xml中
		<context-param>
				<param-name>contextConfigLocation</param-name>
				<param-value>classpath:applicationContext-*.xml</param-value>
		</context-param>

二、import标签的方式
1. 首先需要我们提供一个主配置文件applicationContext.xml 目的不进行任何配置，整合其他配置内容
2.	<import resource="applicationContext-dao.xml"></import>
		<import resource="applicationContext-service.xml"></import>
		<import resource="applicationContext-action.xml"></import>
3. 在web.xml中
		<context-param>
				<param-name>contextConfigLocation</param-name>
				<param-value>classpath:applicationContext.xml</param-value>
		</context-param>
```























