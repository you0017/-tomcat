package com.yc.tomcat2.javax;

import com.yc.tomcat2.javax.servlet.YcServlet;
import com.yc.tomcat2.javax.servlet.YcServletContext;
import com.yc.tomcat2.javax.servlet.YcServletRequest;
import com.yc.tomcat2.javax.servlet.YcServletResponse;
import com.yc.tomcat2.javax.servlet.http.YcHttpServlet;
import com.yc.tomcat2.javax.servlet.http.YcHttpServletRequest;

import java.io.PrintWriter;

public class DynamicProcessor implements Processor {
    @Override
    public void process(YcServletRequest request, YcServletResponse response) {
        //request已经解析好了

        //1.从request中获取requestURI(/hello,到ServletContext的map中去取class
        String uri = ((YcHttpServletRequest)request).getRequestURI().replace(((YcHttpServletRequest) request).getContextPath(),"");
        //2.为了保证单例 先看另一个map中是否已经有这个class的示例  如果有  说明第二次访问   则直接取·再调用service()

        YcServlet servlet = null;
        try {
            if(YcServletContext.servletInstance.containsKey(uri)){
                //    如果没有·则说明此servlet是第一次调用
                //    先用反射创建servlet实例  再调用service()  存到一个map中
                servlet = YcServletContext.servletInstance.get(uri);
            }else{
                Class clz = YcServletContext.servletClass.get(uri);
                Object obj = clz.newInstance();
                if (obj instanceof YcServlet){
                    servlet = (YcServlet)obj;
                    servlet.init();
                    YcServletContext.servletInstance.put(uri,servlet);
                }
            }
            servlet.service(request,response);
        } catch (Exception e){
            // 还要考虑servlet执行失败的情况·则输出500错误，响应给客户端
            String bodyEntity = e.toString();
            String protocol = gen500(bodyEntity);
            PrintWriter out = response.getWriter();
            out.println(protocol);
            out.println(bodyEntity);
            out.flush();
        }




    }

    private String gen500(String bodyEntity) {
        String protocol = "HTTP/1.1 500 Internal Server Error\r\nContext-Type: text/html\r\nContext-length: "+bodyEntity.length()+"\r\n\r\n";
        return protocol;
    }
}
