package com.yc.tomcat2.javax;

import com.yc.tomcat2.javax.servlet.YcServletRequest;
import com.yc.tomcat2.javax.servlet.YcServletResponse;

public class StaticProcessor implements Processor {
    @Override
    public void process(YcServletRequest request, YcServletResponse response) {
        //((YcHttpServletResponse)response).send();
        response.send();
    }
}
