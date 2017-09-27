package com.mcndsj.BC_RedisConnector.Utils;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

/**
 * Created by Matthew on 2016/4/19.
 */
public class JedisUtils {
    private static JedisPool pool =null;

    public static Jedis get(){
        if(pool == null){
            JedisPoolConfig config = new JedisPoolConfig();
            config.setMaxTotal(2048);
            pool = new JedisPool(config,"192.168.123.2",6379,0,"NO_PUBLIC_INFO");
        }
        return pool.getResource();
    }

    public static void publish(String channel, String msg){
        Jedis jedis = get();
        jedis.publish(channel,msg);
        jedis.close();
    }
}
