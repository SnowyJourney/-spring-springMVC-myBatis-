package com.wang.mybatis;

public class SqlSessionFactory {

    public SqlSession openSession(){
        return new SqlSession();
    }
}
