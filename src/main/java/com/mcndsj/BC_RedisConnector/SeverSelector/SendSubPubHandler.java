package com.mcndsj.BC_RedisConnector.SeverSelector;

import com.mcndsj.BC_RedisConnector.BCRedisConnector;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import redis.clients.jedis.JedisPubSub;

/**
 * Created by Matthew on 20/06/2016.
 */
public class SendSubPubHandler extends JedisPubSub {

    @Override
    public void onMessage(String channel, String message) {
        JSONParser parser = new JSONParser();
        try {
            Object ojb = parser.parse(message);
            JSONObject obj = (JSONObject) ojb;
            try {
                ProxiedPlayer p = ProxyServer.getInstance().getPlayer((String) obj.get("name"));
                ServerInfo server = ProxyServer.getInstance().getServerInfo((String)obj.get("server"));
                if(p == null && server != null){
                    return;
                }
                BCRedisConnector.getInstance().getLobby().setPlayerConnectAllow(p.getName());
                p.connect(server);
                try {
                    p.sendMessage(new TextComponent(ChatColor.RED + "匹配 >> 正在将您从" + ChatColor.YELLOW + p.getServer().getInfo().getName() + ChatColor.RED + "传送至大厅" + ChatColor.YELLOW + server.getName()));
                }catch(Exception ee){

                }
            }catch(Exception e){
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }

    }
}