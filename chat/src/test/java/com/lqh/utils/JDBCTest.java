package com.lqh.utils;

import org.junit.Test;

import java.sql.*;
import java.util.Properties;

public class JDBCTest {
    public static String url;
    public static String userName;
    public static String password;



    static{
        try{
            Class.forName("com.mysql.jdbc.Driver");
        }catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        try {
            Properties properties = new Properties();
            properties = Commutils.loadProperties("db.properties");
            url = properties.getProperty("url");
            userName = properties.getProperty("username");
            password = properties.getProperty("password");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void selectTest(){
        Statement statement = null;
        ResultSet resultSet = null;
        Connection connection = null;
        try {
            String sql = "SELECT * FROM user";
            connection = DriverManager.getConnection(url,userName,password);
            statement = connection.createStatement();
            resultSet = statement.executeQuery(sql);
            while (resultSet.next()){
                int id = resultSet.getInt("id");
                String userName = resultSet.getString("username");
                String password = resultSet.getString("password");
                System.out.println("id: "+id+", username: "+ userName+",password" +password);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }finally {
            try{
                resultSet.close();
                statement.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

    }

    @Test
    public void testZero(){
        int[] arr = new int[0];
    }
}
