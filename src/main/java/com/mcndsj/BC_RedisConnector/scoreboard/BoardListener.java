package com.mcndsj.BC_RedisConnector.scoreboard;

import net.md_5.bungee.UserConnection;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.event.ServerSwitchEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

import java.lang.reflect.Method;

/**
 * Created by Matthew on 6/07/2016.
 */
public class BoardListener implements Listener {


    @EventHandler
    public void onServerSwitch(ServerSwitchEvent evt){
        UserConnection uc = (UserConnection) evt.getPlayer();
        uc.getServerSentScoreboard().clear();
        System.out.println("Scoreboard Clear " + evt.getPlayer().getName());
    }

    @EventHandler
    public void onDisconnect(PlayerDisconnectEvent evt){
        UserConnection uc = (UserConnection) evt.getPlayer();
        uc.getServerSentScoreboard().clear();
        System.out.println("Scoreboard Clear " + evt.getPlayer().getName());
    }

}
