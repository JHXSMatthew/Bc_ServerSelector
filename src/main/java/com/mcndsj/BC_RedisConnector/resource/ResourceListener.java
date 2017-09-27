package com.mcndsj.BC_RedisConnector.resource;

import com.mcndsj.BC_RedisConnector.BCRedisConnector;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.event.ServerConnectEvent;
import net.md_5.bungee.api.event.ServerConnectedEvent;
import net.md_5.bungee.api.event.ServerSwitchEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.md_5.bungee.protocol.Protocol;
import net.md_5.bungee.protocol.ProtocolConstants;

import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Created by Matthew on 2/07/2016.
 */
public class ResourceListener implements Listener{

    //server type to clean resource pack from
    private List<ResourceWrapper> serverType = new ArrayList<ResourceWrapper>();
    private HashMap<String,ResourceWrapper> pendingConnections = new HashMap<>();



    public ResourceListener(){
        serverType.add(new ResourceWrapper("csgolobby","csgo"));
        try {
            Method map = Protocol.class.getDeclaredMethod("map", int.class, int.class);
            map.setAccessible(true);
            Object mapping18 = map.invoke(null, ProtocolConstants.MINECRAFT_1_8, 0x48);
            Object mapping19 = map.invoke(null, ProtocolConstants.MINECRAFT_1_9, 0x32);
            Object mappingsObject = Array.newInstance(mapping18.getClass(), 2);
            Array.set(mappingsObject, 0, mapping18);
            Array.set(mappingsObject, 1, mapping19);
            Object[] mappings = (Object[]) mappingsObject;
            Method reg = Protocol.DirectionData.class.getDeclaredMethod("registerPacket", Class.class, mappings.getClass());
            reg.setAccessible(true);
            reg.invoke(Protocol.GAME.TO_CLIENT, ResourcePacket.class, mappings);
        }catch(Exception e){

        }
    }

    @EventHandler
    public void onServerSwitch(ServerSwitchEvent evt){
        if(evt.getPlayer().getServer() == null)
            return;
        final String playerName = evt.getPlayer().getName();
        if(!pendingConnections.containsKey(playerName)){
            return;
        }


        BCRedisConnector.getInstance().getProxy().getScheduler().schedule(BCRedisConnector.getInstance(), new Runnable() {
            @Override
            public void run() {
                ProxiedPlayer p = BCRedisConnector.getInstance().getProxy().getPlayer(playerName);

                if(p == null || p.getServer() == null){
                    System.err.println("st none!!");
                    return;
                }
                if(pendingConnections.get(playerName).shouldSetEmpty(p.getServer().getInfo().getName())) {
                    //System.err.println("Sending Packet!!");
                    p.unsafe().sendPacket(new ResourcePacket(ResourceType.empty.getURL(), ResourceType.empty.getHash()));

                }
            }
        }, 100, TimeUnit.MILLISECONDS);
        return;

    }

    @EventHandler
    public void onConnect(ServerConnectEvent evt){
        if(evt.isCancelled()){
            return;
        }
        if(evt.getPlayer().getServer() == null ){
            return;
        }

        String name = evt.getPlayer().getServer().getInfo().getName();

        for(final ResourceWrapper rp : serverType ) {
            if (rp.isThisTheServer(name)) {
                pendingConnections.put(evt.getPlayer().getName(),rp);
                return;
            }
        }

    }

    @EventHandler
    public void onDisconnected(PlayerDisconnectEvent evt){

        pendingConnections.remove(evt.getPlayer().getName());
    }

}
