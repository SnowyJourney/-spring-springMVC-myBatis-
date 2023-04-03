package com.wang.ioc;

public class BeanDefine {
    private String scope;
    private Class clazz;

    public BeanDefine() {
    }

    public BeanDefine(String scope, Class clazz) {
        this.scope = scope;
        this.clazz = clazz;
    }

    public String getScope() {
        return scope;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }

    public Class getClazz() {
        return clazz;
    }

    public void setClazz(Class clazz) {
        this.clazz = clazz;
    }
}
