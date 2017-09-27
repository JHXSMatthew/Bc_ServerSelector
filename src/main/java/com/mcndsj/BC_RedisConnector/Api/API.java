package com.mcndsj.BC_RedisConnector.Api;

import com.mcndsj.BC_RedisConnector.BCRedisConnector;

/**
 * Created by Matthew on 2016/4/23.
 */
public class API {

    public String getLastLobby(String name){
        return BCRedisConnector.getInstance().getLobby().getLobby(name);
    }
}
