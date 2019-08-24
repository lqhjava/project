package com.lqh.client.dao;


import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.pool.DruidDataSourceFactory;
import com.lqh.utils.Commutils;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collection;
import java.util.Collections;
import java.util.Properties;

/**
 * dao层的顶层父类，封装数据源，获取连接，关闭资源等共有操作
 */
public class BasedDao {
    //Druid能够提供强大的监控和扩展功能。
    private static DruidDataSource DATA_SOURCERCE;

    static {
        //1. 加载
        Properties properties = Commutils.loadProperties("db.properties");
        try{
            //加载配置文件 加载数据源
            DATA_SOURCERCE = (DruidDataSource) DruidDataSourceFactory.
                    createDataSource(properties);
        }catch (Exception e){
            System.out.println("数据源加载失败");
            e.printStackTrace();
        }
    }

    //2. 获取连接
    protected Connection getConnection(){
        try{
            return (Connection) DATA_SOURCERCE.getPooledConnection();
        }catch (SQLException e){

        }
        return null;
    }

    //关闭连接
    protected void closeConnection(Connection connection, Statement statement){
        if(connection != null){
            try{
                connection.close();
            }catch (SQLException e){
                e.printStackTrace();
            }
        }
        if(statement != null){
            try{
                statement.close();
            }catch(SQLException e){
                e.printStackTrace();
            }
        }
    }
    protected void closeConnection(Connection connection,
                                   Statement statement,
                                   ResultSet resultSet){
        this.closeConnection(connection,statement);
        try{
            resultSet.close();
        }catch (SQLException e){
            e.printStackTrace();
        }
    }

}
