版本一：http服务器·提供静态资源访问

浏览器：http://localhost:8090/wowotuan/index.html
显示wowotuan页面

分析协议：
    请求部分：（浏览器自动实现
        3 图片
          GET /xxx/xxx.jpg HTTP/1.1
          Referer:http://localhost:8080/wowotuanStatic/index.html
          Sec-Fetch-Dest: image
          .....
          /r/n

    服务器响应部分：
        3 图片：
          HTTP/1.1 200 OK
          Accept-Ranges: bytes
          Content-Length:92174    **
          Content-Type: text/html
          ...
          /r/n

          响应实体(index.html的文本内容


服务器功能：
1.接收客户端请求并借其他请求的文件名(/wowotuan/index.html) 及相对路径(F:\java\java\。，。\三期\gitee3_thread\webapps + wowotuan/index.html
                                                                System.getProperty("user.dir")
2.查找这个文件是否存在在·不存在->404页面
    存在->
       1 读取这个资源  文件输入流
       2 构建响应协议
            HTTP/1.1 200 OK
            Content-Type:   浏览器根据响应中的   Content-Type来决定用什么引擎来解析数据
                            text/html: html -> html渲染
                            text/css:   css引擎
                            image/png   图片:图片引擎
                            text/javascript 引擎
            Content-Length:

用到的技术：
    1.ServerSocket -> Socket
    2.多线程
    3.slf4j
    4.属性内处理     因为字符字节码无法修改  -》  属性文件
                    1.properties        Properties类  .load()
                    2.xml文件             -> server.xml
                    3.json文件          ->以流的方式获取  -> String ->Gson.toJson()  转为json
        don解析：xml
            a.DOM方式  javascript:document.getElementByTagName()
                        一次性将整个xml加载到内存，再以dom的方式解析
            b.SAX方式  事件解析方式
        以上两种解析方式：j2ee自带
            xml解析框架
              c  dom4j
              d  jdom

KittyServer:
    xml解析窗口
    ServerSocket ss = new ServerSocket(端口);

    Socket s = ss.accept();
    Thread t = new Thread();
    t.start();