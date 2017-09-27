package com.mcndsj.BC_RedisConnector.AutoBan;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

/**
 * Created by Matthew on 2016/4/22.
 */
public class JedisController {
    JedisPool pool = null;

    public JedisController(){
        JedisPoolConfig config = new JedisPoolConfig();
        pool = new JedisPool(config,"192.168.123.2",6379,2000,"NO_PUBLIC_INFO");
    }

    public Long getBannedTime(String ip){
        try(Jedis j = pool.getResource()){
            return j.ttl("AutoBan_" + ip);

        }catch(Exception e){
        }
        return Long.valueOf(-1);
    }

    public void setBanned(String ip, int hours){
        try(Jedis j = pool.getResource()){
            j.set("AutoBan_" + ip, "banned");
            j.expire("AutoBan_" + ip , 60 * 60 * hours);
        }catch(Exception e){

        }
    }


}
