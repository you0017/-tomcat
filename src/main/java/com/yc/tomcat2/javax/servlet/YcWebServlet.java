package com.yc.tomcat2.javax.servlet;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 注解
 */
@Target({ElementType.TYPE}) //这个注解只能放在类/接口/枚举上面   不能放在方法实体上面
@Retention(RetentionPolicy.RUNTIME)//保持策略   运行时有效
public @interface YcWebServlet {
    String value() default "";
}

//@YcWebServlet(value="/hello")
//使用二 @YcWebServlet("/hello")
// public class HelloServlet extends YcHttpServlet{}