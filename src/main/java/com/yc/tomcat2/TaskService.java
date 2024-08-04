package com.yc.tomcat2;



import com.yc.tomcat2.javax.DynamicProcessor;
import com.yc.tomcat2.javax.Processor;
import com.yc.tomcat2.javax.StaticProcessor;
import com.yc.tomcat2.javax.servlet.YcServletContext;
import com.yc.tomcat2.javax.servlet.http.YcHttpServletRequest;
import com.yc.tomcat2.javax.servlet.http.YcHttpServletResponse;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

/**
 * Connection close
 *            keep-alive
 */
public class TaskService implements Runnable {
    private Logger log = Logger.getLogger(TaskService.class);
    private Socket s;
    private InputStream iis;
    private OutputStream oos;
    private boolean flag = true;


    public TaskService(Socket s) {
        this.s = s;
        try {
            this.iis = s.getInputStream();
            this.oos = s.getOutputStream();
        }catch (Exception e){
            log.error("Socket获取流异常");
            flag = false;
        }
    }

    @Override
    public void run() {
        //TODO:  Connection keep-alive
        //长连接
        /*do {
            //通过socket的InputStream读取客户端的请求·解析
            //处理请求的资源
            //返回响应
        }while ();*/
        //结束(socket.close() )

        //短连接
        if (this.flag){
            //解析出所有的请求信息(method,资源地址uri,http版本,头域(referer,user-agent,host,connection,content-length,content-type),参数parameter)
            YcHttpServletRequest request = new YcHttpServletRequest(s,this.iis);

            //响应,本地地址+资源地址uri，读取文件，拼接http响应  以流的形式回传给客户端
            YcHttpServletResponse response = new YcHttpServletResponse(request,this.oos);

            //根据request中的URI来判断什么资源
            Processor processor = null;
            if (YcServletContext.servletClass.containsKey(request.getRequestURI().replace(request.getContextPath(),""))){
                processor = new DynamicProcessor();
            }else {
                processor = new StaticProcessor();
            }
            processor.process(request,response);

            //response.send();
        }
        try {
            this.iis.close();
            this.oos.close();
            this.s.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
