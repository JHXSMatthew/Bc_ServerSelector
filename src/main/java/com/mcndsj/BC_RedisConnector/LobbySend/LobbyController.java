package com.mcndsj.BC_RedisConnector.LobbySend;

import com.github.JHXSMatthew.Core;
import com.imaginarycode.minecraft.redisbungee.RedisBungee;
import com.mcndsj.BC_RedisConnector.BCRedisConnector;
import com.mcndsj.BC_RedisConnector.Utils.JSONUtils;
import com.mcndsj.BC_RedisConnector.Utils.JedisUtils;
import com.mcndsj.BC_RedisConnector.Utils.SQLUtils;
import com.mcndsj.BC_RedisConnector.Utils.WordUtils;
import io.netty.util.internal.ConcurrentSet;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.event.PluginMessageEvent;
import net.md_5.bungee.api.event.ServerConnectEvent;
import net.md_5.bungee.api.event.ServerConnectedEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.md_5.bungee.event.EventPriority;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Created by Matthew on 2016/4/23.
 */
public class LobbyController implements Listener{


    private HashMap<String,String> lastLobbyType;
    private ConcurrentSet<String> nameSet = null;
    private AuthRandomize random = null;
    private ExecutorService threadPool = Executors.newCachedThreadPool();
    private ConcurrentHashMap<String,String> queueCache;

    public LobbyController(){
        lastLobbyType = new HashMap<String,String>();
        queueCache = new ConcurrentHashMap<>();
        nameSet = new ConcurrentSet<>();
        random = new AuthRandomize();
        BCRedisConnector.getInstance().getProxy().registerChannel("LobbyConnect");
        BCRedisConnector.getInstance().getProxy().getPluginManager().registerListener(BCRedisConnector.getInstance(),this);

    }

    public String getLobby(String name){
        if(!lastLobbyType.containsKey(name)){
            lastLobbyType.put(name,SQLUtils.getLastLobby(name));

        }
        return lastLobbyType.get(name);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPostConnect(final ServerConnectedEvent evt){
        if(evt.getServer().getInfo().getName().contains("lobby")){
            int id = WordUtils.getIntFromString(evt.getServer().getInfo().getName());
            final String type = evt.getServer().getInfo().getName().replace(String.valueOf(id),"");

            if(!lastLobbyType.containsKey(evt.getPlayer().getName()) || !lastLobbyType.get(evt.getPlayer().getName()).equals(type)){
                lastLobbyType.put(evt.getPlayer().getName(), type);
                ProxyServer.getInstance().getScheduler().runAsync(BCRedisConnector.getInstance(), () -> {
                    SQLUtils.setLastServerType(evt.getPlayer().getName(), type);
                });
            }

        }
    }



    @EventHandler
    public void onBackLobbyMessage(PluginMessageEvent evt){

        if(!evt.getTag().equals("LobbyConnect")){
            return;
        }
        try( ByteArrayInputStream stream = new ByteArrayInputStream(evt.getData()) ){
            try (DataInputStream in = new DataInputStream(stream)){
                String name = in.readUTF();
                ProxiedPlayer p = BCRedisConnector.getInstance().getProxy().getPlayer(name);
                if(p == null){
                    return;
                }else{
                    if(!lastLobbyType.containsKey(name)){
                        ProxyServer.getInstance().getScheduler().runAsync(BCRedisConnector.getInstance(), () -> {
                            lastLobbyType.put(name, SQLUtils.getLastLobby(name));
                            sendLobbyRequest(p,lastLobbyType.get(name) + "1");
                        });
                    }else{
                        sendLobbyRequest(p,lastLobbyType.get(name) + "1");
                    }
                }
            }catch(IOException e){
            }
        }catch(IOException e){

        }
    }

    public void setPlayerConnectAllow(String player){
        this.nameSet.add(player);
    }

    @EventHandler
    public void onQuit(PlayerDisconnectEvent evt){
        lastLobbyType.remove(evt.getPlayer().getName());
        nameSet.remove(evt.getPlayer().getName());
        String lastQueue = queueCache.remove(evt.getPlayer().getName());
        if(lastQueue != null){
            sendLobbyQueueQuitRequest(evt.getPlayer(),lastQueue);
        }
    }

    @EventHandler
    public void onSend(ServerConnectEvent evt){
        if(evt.getTarget().getName().contains("lobby") ){
            if(!nameSet.contains(evt.getPlayer().getName())) {
                evt.setCancelled(true);
                sendLobbyRequest(evt.getPlayer(),evt.getTarget().getName());
                return;
            }
        }else if(evt.getTarget().getName().contains("auth")){
            try {
                evt.setTarget(ProxyServer.getInstance().getServerInfo(random.get()));
            }catch(Exception e){
                e.printStackTrace();
            }
            return;
        }

        nameSet.remove(evt.getPlayer().getName());
        queueCache.remove(evt.getPlayer().getName());
    }


    /**
     * @precondition must be a lobby server
     * @param player
     * @param fullServerName
     */

    public void sendLobbyRequest(ProxiedPlayer player,String fullServerName){
        final String msg = JSONUtils.encodePlayer(player, RedisBungee.getApi().getServerId() , Core.getVip().isVip(player.getName()));
        int id = WordUtils.getIntFromString(fullServerName);
        final String type = fullServerName.replace(String.valueOf(id),"");

        String lastFullName = queueCache.put(player.getName(),fullServerName);
        if(lastFullName != null)
            sendLobbyQueueQuitRequest(player,lastFullName);

        //System.out.print("lobbyJoin." + type + "msg:" + msg);
        threadPool.execute(new Runnable() {
            @Override
            public void run() {
                JedisUtils.publish("lobbyJoin." + type ,msg);
            }
        });
    }

    public void sendLobbyQueueQuitRequest(ProxiedPlayer player,String fullServerName){
        final String msg = JSONUtils.encodePlayer(player, RedisBungee.getApi().getServerId() , Core.getVip().isVip(player.getName()));
        int id = WordUtils.getIntFromString(fullServerName);
        final String type = fullServerName.replace(String.valueOf(id),"");
        //System.out.print("lobbyJoin." + type + "msg:" + msg);
        threadPool.execute(new Runnable() {
            @Override
            public void run() {
                JedisUtils.publish("lobbyQuit." + type ,msg);
            }
        });
    }


}
