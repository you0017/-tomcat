package com.yc.tomcat1;

import java.io.*;


/**
 * request请求信息，处理响应
 * 1xx:  请求未完成
 * 2xx:  有这个资源，正常响应
 * 3xx:   有这个缓存·重定向
 * 4xx:  没有这个资源·即没有request指定的文件
 * 5xx:  服务器内部错误
 */
public class YcHttpServletResponse {
    private YcHttpServletRequest request;
    private OutputStream oos;

    public YcHttpServletResponse(YcHttpServletRequest request, OutputStream oos) {
        this.request = request;
        this.oos = oos;
    }

    public void send() {
        String uri = request.getRequestURI();//  /wowotuan/index.html
        String realPath = request.getRealPath();    //服务器路径 webapps路径
        File f = new File(realPath, uri);
        byte[] fileContent = null;
        String responseProtocol = null;
        if (!f.exists()) {
            //文件不存在  则4xx响应
            fileContent = readFile(new File(realPath, "/404.html"));
            responseProtocol = gen404(fileContent);
        } else {
            //文件存在  则读取  回2xx
            fileContent = readFile(new File(realPath, uri));
            responseProtocol = gen200(fileContent);
        }

        try {
            //响应头
            oos.write(responseProtocol.getBytes());
            oos.flush();
            //响应内容
            oos.write(fileContent);
            oos.flush();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (oos != null) {
                try {
                    oos.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private String gen200(byte[] fileContent) {
        String protocol200 = "";
        //先取出请求的资源类型
        String uri = request.getRequestURI();
        //取出uri的后缀
        int index = uri.lastIndexOf(".");
        if (index >= 0) {
            index += 1;
        }
        //TODO: 策略模式读取server.xml中的配置
        String fileExtension = uri.substring(index);
        if ("JPG".equalsIgnoreCase(fileExtension)) {
            protocol200 = "HTTP/1.1 200 OK\r\n" +
                    "Content-Type: image/jpeg;\r\n" +
                    "Content-Length: " + fileContent.length + "\r\n\r\n";
        } else if ("CSS".equalsIgnoreCase(fileExtension)) {
            protocol200 = "HTTP/1.1 200 OK\r\n" +
                    "Content-Type: text/css;\r\n" +
                    "Content-Length: " + fileContent.length + "\r\n\r\n";
        } else if ("js".equalsIgnoreCase(fileExtension)) {
            protocol200 = "HTTP/1.1 200 OK\r\n" +
                    "Content-Type: application/javascript;\r\n" +
                    "Content-Length: " + fileContent.length + "\r\n\r\n";
        } else if ("gif".equalsIgnoreCase(fileExtension)) {
            protocol200 = "HTTP/1.1 200 OK\r\n" +
                    "Content-Type: image/gif;\r\n" +
                    "Content-Length: " + fileContent.length + "\r\n\r\n";
        } else if ("png".equalsIgnoreCase(fileExtension)) {
            protocol200 = "HTTP/1.1 200 OK\r\n" +
                    "Content-Type: image/png;\r\n" +
                    "Content-Length: " + fileContent.length + "\r\n\r\n";
        } else {
            protocol200 = "HTTP/1.1 200 OK\r\n" +
                    "Content-Type: text/html; charset=utf-8\r\n" +
                    "Content-Length: " + fileContent.length + "\r\n\r\n";
        }

        return protocol200;
    }

    /**
     * @param fileContent
     * @return
     */
    private String gen404(byte[] fileContent) {
        String protocol404 = "HTTP/1.1 404 Not Found\r\n" +
                "Content-Type: text/html\r\n" +
                "Content-Length: " + fileContent.length + "\r\n";
        protocol404 += "Server: kitty server\r\n\r\n";
        return protocol404;
    }

    /**
     * 读取本地文件
     *
     * @param file
     * @return
     */
    private byte[] readFile(File file) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        InputStream iis = null;
        try {
            iis = new FileInputStream(file);
            byte[] buffer = new byte[1024 * 100];
            int length = -1;
            while ((length = iis.read(buffer, 0, buffer.length)) != -1) {
                baos.write(buffer, 0, length);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            try {
                baos.close();
                iis.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return baos.toByteArray();
    }
}
