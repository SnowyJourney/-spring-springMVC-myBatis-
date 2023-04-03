package com.wang.mybatis;

import com.wang.mapperConfig.Function;
import com.wang.mapperConfig.MapperBean;
import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.security.spec.RSAOtherPrimeInfo;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class Configuration {

//    获取类加载器
    private static ClassLoader classLoader = ClassLoader.getSystemClassLoader();

//    通过配置文件获取xml信息
    public static Connection buildByXml(String configXML){
//        获取文件输入流
        InputStream resourceAsStream = classLoader.getResourceAsStream(configXML);
//        dom4j的xml文件解析器
        SAXReader saxReader = new SAXReader();
        try {
            Document read = saxReader.read(resourceAsStream);
//            获取到根元素
            Element rootElement = read.getRootElement();
            //获取根元素下的子元素
            List<Element> elements = rootElement.elements();
            //取出property的name和value
            String driverClassName=null;
            String url=null;
            String userName=null;
            String password=null;
            for (Element element : elements) {
                String name = element.attribute("name").getText();
                String value = element.attribute("value").getText();
                switch (name){
                    case "driverClassName":
                        driverClassName=value;
                        break;
                    case "url":
                        url=value;
                        break;
                    case "userName":
                        userName=value;
                        break;
                    case "password":
                        password=value;
                        break;
                    default:
                        throw new RuntimeException("没有对应"+name);
                }
            }
//            注册驱动
            Class.forName(driverClassName);
//            获取链接
            Connection connection = DriverManager.getConnection(url, userName, password);
            return connection;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

//    获取mapper.xml文件的信息
    public static MapperBean readMapperXML(String mapperXMl){
//        封装信息到MapperBean
        MapperBean mapperBean = new MapperBean();
//        存储一个mapperXML文件下的所有方法信息
        List<Function> functions=new ArrayList<>();
//        通过类加载器去加载文件流
        InputStream resourceAsStream = classLoader.getResourceAsStream(mapperXMl);
        SAXReader saxReader = new SAXReader();
        Document readXml = null;
        try {
//            读取文件
            readXml = saxReader.read(resourceAsStream);
        } catch (DocumentException e) {
            e.printStackTrace();
        }
//        获取根元素
        Element rootElement = readXml.getRootElement();
//        封装接口信息
        String namespace = rootElement.attribute("namespace").getText();
        mapperBean.setInterfaceName(namespace);
//        获取根元素下的子元素
        List<Element> elements = rootElement.elements();
//        封装到function列表中
        for (Element element : elements) {
            String sqlType = element.getName();
            String methodId = element.attribute("id").getText();
            String parameterType = element.attribute("parameterType").getText();
            Attribute resultType = element.attribute("resultType");
            String result="";
            if(resultType!=null){
                result = resultType.getText();
            }
            String sql = element.getText();

//            反射创建resultType实例对象
            Object instance = null;
            if(!result.equals("")) {
                try {
                    Class<?> clazz = Class.forName(result);
                    instance = clazz.getDeclaredConstructor().newInstance();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
//            封装接口方法在XMl文件中信息
            Function function = new Function(sqlType, methodId, parameterType, instance, sql);
            functions.add(function);
        }
        mapperBean.setFunctions(functions);
        return mapperBean;
    }
}
