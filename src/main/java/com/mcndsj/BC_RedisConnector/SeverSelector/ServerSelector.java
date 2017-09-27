package com.mcndsj.BC_RedisConnector.SeverSelector;

import com.imaginarycode.minecraft.redisbungee.RedisBungee;
import com.mcndsj.BC_RedisConnector.BCRedisConnector;
import com.mcndsj.BC_RedisConnector.Utils.JedisUtils;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.JedisPubSub;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by Matthew on 2016/4/18.
 */
public class ServerSelector extends JedisPubSub{

    ExecutorService thread = Executors.newCachedThreadPool();

    private SendSubPubHandler handler;

    public ServerSelector(){
        handler = new SendSubPubHandler();

        thread.execute(new Runnable() {
            public void run() {
                JedisUtils.get().subscribe(get(),"ServerManage.ServerNameQuery");
            }
        });

        thread.execute(new Runnable() {
            public void run() {
                JedisUtils.get().subscribe(handler,"ServerSend."+ RedisBungee.getApi().getServerId());
            }
        });
    }

    @Override
    public void onMessage(String channel, String message) {
        if(channel.equals("ServerManage.ServerNameQuery")){
            String name = BCRedisConnector.getInstance().getServerName(message);
            if(name != null) {
                JedisUtils.publish("ServerManage.ServerNameQuery." + message, name);

            }else{
                System.err.println("ip is null, no name found! " + message);
            }
        }
    }

    public ServerSelector get(){
        return this;
    }
}
