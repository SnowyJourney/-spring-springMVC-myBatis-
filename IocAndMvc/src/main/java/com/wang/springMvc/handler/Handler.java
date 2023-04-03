package com.wang.springMvc.handler;

import java.lang.reflect.Method;

public class Handler {
    private String url;
    private Object object;
    private Method method;

    public Handler(String url, Object object, Method method) {
        this.url = url;
        this.object = object;
        this.method = method;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Object getObject() {
        return object;
    }

    public void setObject(Object object) {
        this.object = object;
    }

    public Method getMethod() {
        return method;
    }

    public void setMethod(Method method) {
        this.method = method;
    }
}
