package com.lqh.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

//加载配置文件
public class Commutils {
    private static final Gson GSON = new GsonBuilder().create();


    public static Properties loadProperties(String fileName){
     Properties properties =new Properties();
     //将文件配置为输入流
        InputStream inputStream = Commutils.class.getClassLoader()
                .getResourceAsStream(fileName);
        //加载配置信息
        try {
            properties.load(inputStream);
        } catch (IOException e) {
            System.out.println("资源文件加载失败");
            e.printStackTrace();
            return null;
        }
        return properties;
    }

    /**
     * 把对象变为json字符串
     * @param object
     * @return
     */
    public static String objToJson(Object object){
        return GSON.toJson(object);
    }


    /**
     * 把json字符串变为对象
     * @param str
     * @param obj
     * @return
     */
    public static Object jsonToObject(String str,Class obj){
        return GSON.fromJson(str,obj);
    }
}
