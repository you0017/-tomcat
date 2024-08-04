package com.yc.wowotuan;


import com.yc.tomcat2.javax.servlet.YcWebServlet;
import com.yc.tomcat2.javax.servlet.http.YcHttpServlet;
import com.yc.tomcat2.javax.servlet.http.YcHttpServletRequest;
import com.yc.tomcat2.javax.servlet.http.YcHttpServletResponse;

import java.io.PrintWriter;

@YcWebServlet("/hello")
public class HelloServlet extends YcHttpServlet {
    public HelloServlet(){
        System.out.println("构造方法");
    }

    public void init()
    {
        System.out.println("init方法");
    }

    protected void doGet(YcHttpServletRequest request, YcHttpServletResponse response){
        //System.out.println("qwe");
        PrintWriter writer = response.getWriter();
        String result = "hello world.你好";
        //TODO 标准的tomcat是由tomcat服务器来完成这个响应的构建
        writer.print("HTTP/1.1 200 OK\r\nContent-Type: text/html;charset=utf-8\r\nContent-length: "+result.getBytes().length+"\r\n\r\n");
        System.out.println(result);
        writer.println(result);
        writer.flush();
    }
}
