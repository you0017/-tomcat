版本2： servlet服务器·提供动态资源(class字节码 -> xxxxServlet.class)  访问

1.servlet动态资源实际上是一个java代码，运行在jvm
2.服务器开发商    (接收参数 -> parameterMap
  应用网站开发人员  servlet(parameterMap -> 取参数) html css
  客户端

  -> sun公司制定：j2ee标准(servlet,filter,listener,jdbc,....
3.静态资源和动态资源都支持


要解决的问题:
    1.Servlet的读取
      服务器启动时，扫描类路径: 所有的.class文件，判断哪些类上有 @YcWebServlet，有则保存到
        Map<String,Class>
           <地址,Servlet的class对象>
           <"/hello",HelloServlet的class对象
      技术解决方案:
        (1)针对
            @WebServlet("/hello")
            public class HelloServlet extends HttpServlet
        注解解析(@YcWebServlet + 且继承自HttpServlet
        (2)类扫描: 递归扫描  字节码加载
        (3)<地址,Servlet的class对象>
            -> j2ee作用域对象
                HttpServletRequest -> HttpSession -> ServletContext

2.加入动态资源的请求处理
    1.访问路径的映射:
        客户端:http://localhost:8090/wowotuan/hello
        实际访问的是一个servlet的class:  如 Hello.class
        但浏览器的地址栏是一个url : http://localhost:9090/wowotuan/Hello.do
        在这里简化处理做了一个约定·只要请求资源的后缀名为.do则表示是一个动态资源请求
        回顾一下真实的web项目·我们的请求应该是怎样处理的?
            http://localhost:9090/wowotuan/Hello.do
            http://localhost:9090/wowotuan/Hello.html
            http://localhost:9090/wowotuan/Hello

            注解:@WebServlet
            web.xml
                <servlet>
                    <servlet-class>
                    <servlet-name>
                </servlet>
                <servlet-mapping>
                    <servlet-name>
                    <url-pattern>
                </servlet>

            请求过来 -> tomcat 解析出requestURI -> web.xml看是否有这个映射 -> 有则示例化对应的servlet
                                                                            生命周期：第一次请求  构造->init->service-> doXxx()
                                                                                    第二次             service-> doXxx()
                                                                      ->没有·是否有这个对应的静态资源
                                                                      ->都没有·则显示404.html

    2.动静资源处理的分离
        TaskService.java->doTask() -> 处理完请求·解析出 requestURI(hello.do/hello.html)  后,根据后缀名来判断静态还是动态
        -> 分发处理(静态资源交给静态资源处理·动态资源交给动态资源处理器

        我们这里会做一个资源处理接口Processor -> process()·如果解析出来的是动态资源·则使用DynamicProcessor对象·否则使用StaticProcessor对象

        DynamicProcessor对象功能: