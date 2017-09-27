package com.mcndsj.BC_RedisConnector;

import com.mcndsj.BC_RedisConnector.Api.API;
import com.mcndsj.BC_RedisConnector.AutoBan.AutoBanController;
import com.mcndsj.BC_RedisConnector.BungeeConfig.ServerHelper;
import com.mcndsj.BC_RedisConnector.BungeeConfig.loadCommand;
import com.mcndsj.BC_RedisConnector.LobbySend.LobbyController;
import com.mcndsj.BC_RedisConnector.SeverSelector.ServerSelector;
import com.mcndsj.BC_RedisConnector.Utils.SQLUtils;
import com.mcndsj.BC_RedisConnector.resource.ResourceListener;
import com.mcndsj.BC_RedisConnector.scoreboard.BoardListener;
import net.md_5.bungee.BungeeServerInfo;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.plugin.Plugin;

import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Matthew on 2016/4/18.
 */
public class BCRedisConnector extends Plugin{
    //<ip,name>
    private Map<String,String> servers = null;

    private ServerSelector serverSelector;
    private LobbyController lobby= null;
    private static BCRedisConnector instance;
    private API api ;


    public void onEnable(){
        instance = this;
        servers = new HashMap<String,String>();
        loadServers();
        serverSelector = new ServerSelector();
        lobby = new LobbyController();
        getProxy().getPluginManager().registerListener(this,new AutoBanController());
        getProxy().getPluginManager().registerListener(this,new BoardListener());
        getProxy().getPluginManager().registerListener(this,new ResourceListener());
        getProxy().getPluginManager().registerCommand(this,new loadCommand());
        api = new API();

        System.out.println("Finished loading BC Redis_control!");

    }

    public API getAPI(){
        return this.api;
    }


    public LobbyController getLobby(){
        return lobby;
    }


    public String getServerName(String ip){
        if(servers.containsKey(ip)) {
            return servers.get(ip);
        }else{
            //System.out.println("Server name not found " + ip);
            return null;
        }
    }

    public static BCRedisConnector getInstance(){
        return instance;
    }


    public void loadServers(){
        HashMap<String,String> dbMap = SQLUtils.getServerFromDB();

        for(Map.Entry<String,String> entry : dbMap.entrySet()){
            if(getProxy().getServers().containsKey(entry.getKey())){
                String ip = getProxy().getServers().get(entry.getKey()).getAddress().getAddress().getHostAddress()+ ":" + getProxy().getServers().get(entry.getKey()).getAddress().getPort() ;
                if(ip.equals(entry.getValue())){
                    continue;
                }
            }
            InetSocketAddress address = getIp(entry.getValue());

            if(address == null){
                System.err.println("No ip found for " + entry.getKey() + " " + entry.getValue());
                continue;
            }
            ServerInfo info = new BungeeServerInfo(entry.getKey(),address, "", false);
            ServerHelper.addServer(info);
           // System.err.println("Add new server to config "  + entry.getKey() + " " + entry.getValue());
        }

        getProxy().getScheduler().runAsync(this, new Runnable() {
            @Override
            public void run() {
                Map<String,ServerInfo> map = getProxy().getConfig().getServers();
                for(String serverName : map.keySet()){
                    InetSocketAddress address = map.get(serverName).getAddress();
                    String ip = address.getHostName() + ":"+address.getPort();
                    servers.put(ip,serverName);
                    System.out.println("add key - " + serverName);
                    // System.out.print("Register: " + entry.getKey() +" " +  ip );
            }
        }});

        System.out.println("load finished!");

    }

    private static InetSocketAddress getIp(String input) {
        if ((!input.contains(":")) || (!input.contains("."))) {
            return null;
        }

        String[] parts = input.split(":");

        if (input.split(":").length != 2) {
            return null;
        }

        if (input.split("\\.").length != 4) {
            return null;
        }

        for (char c : parts[0].replace(".", "").toCharArray()) {
            if (!Character.isDigit(c)) {
                return null;
            }
        }

        for (char c : parts[1].toCharArray()) {
            if (!Character.isDigit(c)) {
                return null;
            }
        }

        return new InetSocketAddress(parts[0],Integer.valueOf(parts[1]).intValue());
    }

}
