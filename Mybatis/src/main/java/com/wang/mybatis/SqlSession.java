package com.wang.mybatis;

import java.lang.reflect.Proxy;

public class SqlSession {

    private MyExecutor myExecutor = new MyExecutor();

    public <T> T selectOne(String sql,Object param){
       return myExecutor.query(sql,param);
    }

    public <T> T getMapper(Class<T> clazz){
        return (T) Proxy.newProxyInstance(clazz.getClassLoader(),new Class[]{clazz},
                new MapperProxy(this,clazz));
    }
}
