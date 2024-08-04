package com.yc.tomcat1;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class TomcatServer {
    private static Logger log = Logger.getLogger(TomcatServer.class);
    public static void main(String[] args) {

        log.debug("程序启动了");
        TomcatServer ts = new TomcatServer();
        int port = ts.parsePartFromXml();
        log.debug("程序端口为:"+port);
        ts.startServer(port);
    }

    private void startServer(int port){
        boolean flag = true;
        try (ServerSocket ss = new ServerSocket(port)){
            log.debug("服务器启动成功,配置端口为:"+port);
            //TODO: 可以读取Server.xml中是否开启线程池的配置，决定是否使用线程池
            while (flag){
                try {
                    Socket s = ss.accept();
                    log.debug("客户端"+s.getRemoteSocketAddress()+"连接上了服务器");

                    TaskService task = new TaskService(s);
                    Thread t = new Thread(task);
                    t.start();
                } catch (IOException e) {
                    e.printStackTrace();
                    log.error("客户端连接失败");
                }
            }
        }catch (IOException e){
            e.printStackTrace();
            log.error("服务器套接字创建失败");
        }
    }

    private int parsePartFromXml(){
        int port = 8080;
        //方案一：根据字节码构建  (Target/classes/
        //TomcatServer.class.getClassLoader().getResourceAsStream();
        //方案二：
        String serverxmlPath = System.getProperty("user.dir") + File.separator+"conf"+File.separator+"server.xml";
        //log.info("serverxmlPath:"+serverxmlPath);
        try (
                InputStream iis = new FileInputStream(serverxmlPath);
                ){
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder documentBuilder = factory.newDocumentBuilder();
            Document doc = documentBuilder.parse(iis);

            NodeList nl = doc.getElementsByTagName("Connector");
            for (int i=0;i<nl.getLength();i++){
                Element node = (Element) nl.item(i);
                port = Integer.parseInt(node.getAttribute("port"));

            }
        }catch (Exception e){

        }
        return port;
    }
}
