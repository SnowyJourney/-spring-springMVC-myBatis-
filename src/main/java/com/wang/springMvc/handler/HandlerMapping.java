package com.wang.springMvc.handler;

import com.wang.ioc.ApplicationContext;

import javax.servlet.http.HttpServletRequest;
import java.util.concurrent.ConcurrentHashMap;

public class HandlerMapping {
    private ApplicationContext ioc;
    private ConcurrentHashMap<String,Handler> handlerMap;

    public HandlerMapping(ApplicationContext ioc) {
        this.ioc = ioc;
        handlerMap=ioc.getHandlerMap();
    }

    //根据请求，解析，获取到映射表中的handler
    public Handler getHandler(HttpServletRequest request){
        String contextPath = request.getContextPath();
        String requestURI = request.getRequestURI();
        requestURI = requestURI.replace(contextPath, "");
        Handler handler = handlerMap.get(requestURI);
        return handler;
    }
}
