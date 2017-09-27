package com.mcndsj.BC_RedisConnector.Utils;

import net.md_5.bungee.api.connection.ProxiedPlayer;
import org.json.simple.JSONObject;

/**
 * Created by Matthew on 2016/4/19.
 */
public class JSONUtils {

    public static String encodePlayer(ProxiedPlayer p, String lobbyName , boolean isVip){
        JSONObject obj = new JSONObject();
        obj.put("name",p.getName());
        obj.put("lobby",lobbyName);
        obj.put("vip", isVip);
        return obj.toJSONString();
    }
}
