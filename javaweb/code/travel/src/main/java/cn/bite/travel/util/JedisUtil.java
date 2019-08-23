package cn.bite.travel.util;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Jedis工具类
 */
public final class JedisUtil {
    //redis连接池
    private static JedisPool jedisPool;

    static {
        //读取配置文件
        InputStream is = JedisPool.class.getClassLoader().
                getResourceAsStream("jedis.properties");
        //创建Properties对象
        Properties pro = new Properties();
        //关联文件
        try {
            pro.load(is);
        } catch (IOException e) {
            e.printStackTrace();
        }
        //获取数据，设置到JedisPoolConfig中
        //JedisConfig:jedis的配置对象(配置最大连接数/最大空闲数/超时时间)
        JedisPoolConfig config = new JedisPoolConfig();
        config.setMaxTotal(Integer.parseInt(pro.getProperty("maxTotal")));
        config.setMaxIdle(Integer.parseInt(pro.getProperty("maxIdle")));

        //初始化JedisPool
        jedisPool = new JedisPool(config, pro.getProperty("host"), Integer.parseInt(pro.getProperty("port")));


    }


    /**
     * 获取连接方法
     * Jedis操作底层redis客户端对象
     */
    public static Jedis getJedis() {
        return jedisPool.getResource();
    }

    /**
     * 关闭Jedis
     */
    public static void close(Jedis jedis) {
        if (jedis != null) {
            jedis.close();
        }
    }
}
