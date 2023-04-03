package com.wang.mybatis;

import com.wang.mapperConfig.Function;
import com.wang.mapperConfig.MapperBean;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.List;

/**
 * 简单来说就是这类就是invokeHandler执行器
 */
public class MapperProxy implements InvocationHandler {

    private SqlSession sqlSession;
    private String mapperFile;

    public MapperProxy(SqlSession sqlSession, Class clazz){
        this.sqlSession=sqlSession;
        this.mapperFile="mapper/"+clazz.getSimpleName()+".xml";
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
//        获取到需要调用mapper的对应xml文件信息
        MapperBean mapperBean = Configuration.readMapperXML(mapperFile);
//        判断method执行的类型是否是这个mapperXML对应的接口,如果不是就直接返回
        if(!method.getDeclaringClass().getName().equals(mapperBean.getInterfaceName()))
            return null;
//        过滤方法mapperXML文件中没有方法mapper
        List<Function> functions = mapperBean.getFunctions();
        if(functions==null||functions.size()==0)
            return null;
        for (Function function : functions) {
//            找到对应的mapperXML文件中的方法
            if(function.getMethodId().equals(method.getName())){
//                以前是在这里直接反射待用method.invoke(bean, args),但是这里是调用执行器中的对应方法,通过执行器去操作数据库
                //这里只是演示反射调用,真正的myBatis很复杂
                if(function.getSqlType().equalsIgnoreCase("select")){
//                    这里就直接返回执行后封装的结果
                    return sqlSession.selectOne(function.getSql(), String.valueOf(args[0]));
                }
            }
        }
        return null;
    }
}
