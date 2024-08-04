package com.yc.tomcat2;

import com.yc.tomcat2.javax.servlet.YcServletContext;
import com.yc.tomcat2.javax.servlet.YcWebServlet;
import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.*;
import java.net.*;
import java.util.Enumeration;

public class TomcatServer {
    private static Logger log = Logger.getLogger(TomcatServer.class);

    public static void main(String[] args) {

        log.debug("程序启动了");
        TomcatServer ts = new TomcatServer();
        int port = ts.parsePartFromXml();
        log.debug("程序端口为:" + port);
        ts.startServer(port);
    }

    private void startServer(int port) {
        boolean flag = true;

        String packageName = "com.yc";
        String packagePath = packageName.replaceAll("\\.", "/");

        //服务器启动时·扫描它所有的 classes,查找带有@YcWebServlet的class存到map中
        //jvm类加载器
        try {
            Enumeration<URL> files = Thread.currentThread().getContextClassLoader().getResources(packagePath);
            while (files.hasMoreElements()) {
                URL url = files.nextElement();
                log.info("正在扫描的包路径为:" + url.getFile());
                //查找此包下的文件
                findPackageClasses(url.getFile(), packageName);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        try (ServerSocket ss = new ServerSocket(port)) {
            log.debug("服务器启动成功,配置端口为:" + port);
            //TODO: 可以读取Server.xml中是否开启线程池的配置，决定是否使用线程池
            while (flag) {
                try {
                    Socket s = ss.accept();
                    log.debug("客户端" + s.getRemoteSocketAddress() + "连接上了服务器");

                    TaskService task = new TaskService(s);
                    Thread t = new Thread(task);
                    t.start();
                } catch (IOException e) {
                    e.printStackTrace();
                    log.error("客户端连接失败");
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            log.error("服务器套接字创建失败");
        }
    }

    /**
     * @param packagePath com/yc
     * @param packageName com.yc
     */
    private void findPackageClasses(String packagePath, String packageName) throws UnsupportedEncodingException {
        if (packagePath.startsWith("/")) {
            packagePath = packagePath.substring(1);
        }
        packagePath = URLDecoder.decode(packagePath, "utf-8");
        //取这个路径下所有的文件
        File file = new File(packagePath);
        File[] classFiles = file.listFiles(new FileFilter() {
            @Override
            public boolean accept(File pathname) {
                if (pathname.getName().endsWith(".class") || pathname.isDirectory()) {
                    return true;
                }else {
                    return false;
                }
            }
        });
        //System.out.println(classFiles);
        if (classFiles != null && classFiles.length > 0){
            for (File classFile : classFiles) {
                if (classFile.isDirectory()){
                    findPackageClasses(classFile.getAbsolutePath(), packageName + "." + classFile.getName());
                }else {
                    //是字节码文件·则利用类加载器加载class文件
                    URLClassLoader uc = new URLClassLoader(new URL[]{});
                    try {
                        Class cls = uc.loadClass(packageName + "." + classFile.getName().replaceAll(".class", ""));

                        if (cls.isAnnotationPresent(YcWebServlet.class)){
                            log.info("加载了一个类:"+cls.getName());
                            //通过注解的value()方法取出url地址，存到YcServletContext
                            String url = ((YcWebServlet) cls.getAnnotation(YcWebServlet.class)).value();
                            YcServletContext.servletClass.put(url,cls);
                        }
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    private int parsePartFromXml() {
        int port = 8080;
        //方案一：根据字节码构建  (Target/classes/
        //TomcatServer.class.getClassLoader().getResourceAsStream();
        //方案二：
        String serverxmlPath = System.getProperty("user.dir") + File.separator + "conf" + File.separator + "server.xml";
        //log.info("serverxmlPath:"+serverxmlPath);
        try (
                InputStream iis = new FileInputStream(serverxmlPath);
        ) {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder documentBuilder = factory.newDocumentBuilder();
            Document doc = documentBuilder.parse(iis);

            NodeList nl = doc.getElementsByTagName("Connector");
            for (int i = 0; i < nl.getLength(); i++) {
                Element node = (Element) nl.item(i);
                port = Integer.parseInt(node.getAttribute("port"));

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return port;
    }
}
