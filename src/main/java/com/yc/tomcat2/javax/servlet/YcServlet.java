package com.yc.tomcat2.javax.servlet;

/**
 * 服务器小程序接口
 */
public interface YcServlet {
    /**
     * 初始化方法:  在生命周期中是在构造方法之后调用一次
     */
    public void init();

    /**
     * 每次请求都会调用service
     * @param request
     * @param response
     */
    public void service(YcServletRequest request, YcServletResponse response);

    /**
     * 销毁方法
     */
    public void destroy();
}
