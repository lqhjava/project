package com.lqh.utils;

import com.lqh.client.po.User;
import org.junit.Assert;
import org.junit.Test;

import java.util.Properties;

public class CommutilsTest {

    @Test
    public void loadProperties() {
        Properties properties = new Properties();
       properties= Commutils.loadProperties("db.properties");
        String url = properties.getProperty("url");
        Assert.assertNotNull(url);
    }

    @Test
    public void toJson(){
        User user = new User();
        user.setId(1);
        user.setUserName("hua");
        user.setPassword("123");
        user.setBrief("haha");
        String str = Commutils.objToJson(user);
        System.out.println(str);

    }

    @Test
    public void testToObjetc(){
        String str = "{\"id\":1,\"userName\":\"hua\",\"password\":\"123\",\"brief\":\"haha\"}";
        User user = (User) Commutils.jsonToObject(str,User.class);
        System.out.println(user);
    }
}