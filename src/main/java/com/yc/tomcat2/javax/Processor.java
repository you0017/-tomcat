package com.yc.tomcat2.javax;

import com.yc.tomcat2.javax.servlet.YcServletRequest;
import com.yc.tomcat2.javax.servlet.YcServletResponse;

/**
 * 资源处理接口
 */
public interface Processor {
    public void process(YcServletRequest request, YcServletResponse response);
}
