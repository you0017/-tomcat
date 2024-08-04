package com.yc.tomcat2.javax.servlet;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 应用程序上下文类
 */
public class YcServletContext {
    /**
     * url地址,servlet字节码路径
     */
    public static Map<String,Class> servletClass = new ConcurrentHashMap<>();

    /**
     * 每个servlet都是单例·当第一次访问这个servlet时·创建后保存到这个map中
     */
    public static Map<String,YcServlet> servletInstance = new ConcurrentHashMap<>();
}
