package com.yc.tomcat2.javax.servlet.http;


import com.yc.tomcat2.javax.servlet.YcServlet;
import com.yc.tomcat2.javax.servlet.YcServletRequest;
import com.yc.tomcat2.javax.servlet.YcServletResponse;

public abstract class YcHttpServlet implements YcServlet {
    @Override
    public void init() {

    }

    @Override
    public void destroy() {

    }

    protected void doGet(YcHttpServletRequest request, YcHttpServletResponse response) {}
    protected void doPost(YcHttpServletRequest request, YcHttpServletResponse response) {}
    protected void doHead(YcHttpServletRequest request, YcHttpServletResponse response) {}
    protected void doDelete(YcHttpServletRequest request, YcHttpServletResponse response) {}
    protected void doTrace(YcHttpServletRequest request, YcHttpServletResponse response) {}
    protected void doOption(YcHttpServletRequest request, YcHttpServletResponse response) {}

    /**
     * 模板设计模式  规范httpServlet中各方法的调用顺序
     * @param request
     * @param response
     */
    @Override  //判断method是什么·再调用对应的doXxx方法
    public void service(YcServletRequest request, YcServletResponse response) {
        //从request中取出method(http协议特有
        String method = ((YcHttpServletRequest)request).getMethod();
        if ("GET".equalsIgnoreCase(method)){
            doGet((YcHttpServletRequest)request, (YcHttpServletResponse)response);
        } else if ("POST".equalsIgnoreCase(method)) {
            doPost((YcHttpServletRequest)request, (YcHttpServletResponse)response);
        }else if ("HEAD".equalsIgnoreCase(method)) {
            doHead((YcHttpServletRequest)request, (YcHttpServletResponse)response);
        }else if ("DELETE".equalsIgnoreCase(method)) {
            doDelete((YcHttpServletRequest)request, (YcHttpServletResponse)response);
        }else if ("TRACE".equalsIgnoreCase(method)) {
            doTrace((YcHttpServletRequest)request, (YcHttpServletResponse)response);
        }else if ("OPTIONS".equalsIgnoreCase(method)) {
            doOption((YcHttpServletRequest)request, (YcHttpServletResponse)response);
        }else {
            //TODO: 错误的响应协议
        }
    }


    public void service(YcHttpServletRequest request, YcHttpServletRequest response) {
        service(request, response);
    }
}
