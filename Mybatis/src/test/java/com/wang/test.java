package com.wang;

import com.wang.entity.Monster;
import com.wang.mapper.MonsterMapper;
import com.wang.mapperConfig.MapperBean;
import com.wang.mybatis.Configuration;
import com.wang.mybatis.MyExecutor;
import com.wang.mybatis.SqlSession;
import com.wang.mybatis.SqlSessionFactory;
import org.junit.Test;

import java.sql.Connection;

public class test {

    @Test
    public void test01(){
        Connection connection = Configuration.buildByXml("myBatis.xml");
        System.out.println(connection);
    }

    @Test
    public void test02(){
        MyExecutor myExecutor = new MyExecutor();
        Monster query = myExecutor.query("select * from monster where id=?", 1);
        System.out.println(query);
    }

    @Test
    public void selectOne(){
        SqlSession sqlSession = new SqlSession();
        Monster o = sqlSession.selectOne("select * from monster where id=?", 1);
        System.out.println(o);
    }

    @Test
    public void readXMl(){
        MapperBean mapperBean = Configuration.readMapperXML("mapper/monsterMapper.xml");
        System.out.println(mapperBean);
    }

    @Test
    public void test03(){
        SqlSessionFactory sqlSessionFactory = new SqlSessionFactory();
        SqlSession sqlSession = sqlSessionFactory.openSession();
        MonsterMapper mapper = sqlSession.getMapper(MonsterMapper.class);
        System.out.println(mapper.getClass());
        Monster monster = mapper.queryOne(1);
        System.out.println(monster);
    }
}
