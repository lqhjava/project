package com.lqh;

import java.sql.*;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args )
    {
        //1加载驱动
        try {
            Class.forName("com.mysql.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        //2 获取连接
        String url = "jdbc:mysql://localhost:3306/jdbc_test?useSSL=false";
        String username = "root";
        String password = "19970306";

        Connection connection = null;
        Statement statement = null;
        ResultSet resultSet = null;

        try {
            connection = DriverManager.getConnection(url,username,password);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        //3 执行sql语句
        try {
            statement =connection.createStatement();
            String sqlStr = "SELECT * FROM user;";
            resultSet = statement.executeQuery(sqlStr);
            while (resultSet.next()){
                int id = resultSet.getInt("id");
                String userName = resultSet.getString("username");
                String passWord = resultSet.getString("password");
                System.out.println("id: "+id+", username: "+ userName+",password" +passWord);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }finally {
            //4关闭连接
            try {
                connection.close();
                resultSet.close();
                statement.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }

        }
    }
}
