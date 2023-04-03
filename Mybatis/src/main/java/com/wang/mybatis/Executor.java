package com.wang.mybatis;

/**
 * 执行器接口
 */
public interface Executor {
    public <T> T query(String sql,Object param);
}
