package com.yc.tomcat1;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.InputStream;
import java.net.Socket;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 从输入流中取出http请求·解析出相应的信息，存好
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class YcHttpServletRequest {
    private Socket s;
    private InputStream iis;
    //GET,POST,PUT,DELETE,HEAD,OPTIONS,TRACE,CONNECT
    private String method;
    //定位符 http://localhost:81/108109res/doUpload.action
    private String requestURL;
    //标识符 /108109res/doUpload.action
    private String requestURI;
    //上下文 /108109res
    private String contextPath;
    //请求字符串  请求的地址栏参数   age=20&sex=male
    private String queryString;

    //参数    地址栏参数  age=20&sex=male  表单中的参数-请求实体
    private Map<String,String[]> parameterMap = new ConcurrentHashMap<>();

    //协议类型  http://
    private String scheme;
    //协议版本
    private String protocol;
    //真实路径   比如C://xxx这种
    private String realPath;

    private Logger log = Logger.getLogger(YcHttpServletRequest.class);

    public YcHttpServletRequest(Socket s,InputStream iis) {
        this.iis = iis;
        this.s = s;
        this.parseRequest();
    }

    /**
     * 解析方法
     */
    private void parseRequest(){
        String requestInfoString = readFromInputStream();  //从输入流中读取http请求信息
        if (requestInfoString == null|| requestInfoString.trim().equals("")){
            throw new RuntimeException("读取输入流异常");  //
        }
        //2.解析http请求头(存各种信息
        parseRequestInfoString(requestInfoString);
    }

    /**
     * 解析http请求头(存各种信息)
     * http请求协议
     *  method 资源地址 资源版本
     *  请求头域键 : 值*
     *  空行
     *  请求实体
     *  资源地址 /108109res/doUpload.action?age=20&sex=male
     */
    private void parseRequestInfoString(String requestInfoString) {
        StringTokenizer st = new StringTokenizer(requestInfoString);
        this.method = st.nextToken();
        this.requestURI = st.nextToken();
        //requestURI要考虑地址栏参数
        int questionIndex = this.requestURI.lastIndexOf("?");
        if (questionIndex >= 0){
            //有?  即有地址栏参数 -> 参数存queryString
            this.queryString = this.requestURI.substring(questionIndex+1);
            this.requestURL = this.requestURI.substring(0,questionIndex);
        }
        //第三部分：协议版本
        this.protocol = st.nextToken();
        // HTTP
        this.scheme = this.protocol.substring(0,this.protocol.indexOf("/"));

        //requestURI:  /108109res/index.html
        //  www.baidu.com ->  GET /
        //contextPath: /108109res
        //             /
        int slash2Index = this.requestURI.indexOf("/",1);
        if (slash2Index >= 0){
            this.contextPath = this.requestURI.substring(0,slash2Index);
        }else{
            this.contextPath = this.requestURI;
        }

        //requestURL: URL统一资源定位符    http://ip:端口/requestURI
        this.requestURL = this.scheme+"://" + this.s.getLocalSocketAddress() + this.requestURI;

        //参数的处理  :  /108109res/doUpload.action?age=20&sex=male
        //从queryString中取出参数
        if (this.queryString != null && this.queryString.length()>0){
            String[] ps = this.queryString.split("&");
            for (String s : ps) {
                String[] params = s.split("=");
                //  情况一  uname = abc
                //  情况而  ins = a,b,c
                this.parameterMap.put(params[0],params[1].split(","));
            }
            //TODO:  还有post的实体中也有可能有参数
        }


        //realPath  webapps静态资源路径
        this.realPath = System.getProperty("user.dir")+ File.separator+"webapps";
    }

    /**
     * 从输入流中读取http请求信息
     */
    private String readFromInputStream() {
        int length = -1;
        StringBuffer sb = null;
        byte[] bs = new byte[1024*300];  //TODO 300k足够存普通请求  文件上传之外的请求
        try {
            length = this.iis.read(bs,0,bs.length);
            //将byte[] -> String
            sb = new StringBuffer();
            for (int i = 0; i < length; i++) {
                sb.append((char)bs[i]);
            }
            /*while ((length = this.iis.read(bs)) != -1){
                sb.append(new String(bs,0,length));
            }*/
        }catch (Exception e){
            log.error("读取请求信息异常");
            e.printStackTrace();
        }
        return sb.toString();
    }

    public String[] getParameterValues(String name){
        if (parameterMap==null||parameterMap.size()<=0){
            return null;
        }
        return parameterMap.get(name);
    }

    public String getParameter(String name){
        if (parameterMap==null||parameterMap.size()<=0){
            return null;
        }
        String[] strings = parameterMap.get(name);
        if (strings==null||strings.length<=0){
            return null;
        }
        return strings[0];
    }

    private void parseRequest(InputStream iis){

    }

    public String getMethod() {
        return this.method;
    }
}
