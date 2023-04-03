package com.wang.mybatis;

import com.wang.entity.Monster;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class MyExecutor implements Executor{

    @Override
    public <T> T query(String sql, Object param) {
        Connection connection = Configuration.buildByXml("myBatis.xml");

        PreparedStatement preparedStatement=null;
        ResultSet resultSet=null;
        try {
//            预编译sql语句
            preparedStatement = connection.prepareStatement(sql);
//            填入参数
            preparedStatement.setString(1,param.toString());
//            执行sql获取结果
            resultSet = preparedStatement.executeQuery();
//            封装结果
            Monster monster = new Monster();
            while (resultSet.next()){
                monster.setId(resultSet.getInt("id"));
                monster.setName(resultSet.getString("name"));
                monster.setEmail(resultSet.getString("email"));
                monster.setAge(resultSet.getInt("age"));
                monster.setGender(resultSet.getInt("gender"));
                monster.setBirthday(resultSet.getDate("birthday"));
                monster.setSalary(resultSet.getDouble("salary"));
            }
            //返回封装结果
            return (T)monster;
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            try {
                if(resultSet!=null)
                    resultSet.close();
                if(preparedStatement!=null)
                    preparedStatement.close();
                if(connection!=null)
                    connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return null;
    }
}
