package com.mcndsj.BC_RedisConnector.AutoBan;

import com.mcndsj.BC_RedisConnector.BCRedisConnector;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PluginMessageEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;

/**
 * Created by Matthew on 2016/4/22.
 */
public class MessageReceiver implements Listener{

    AutoBanController control = null;
    public MessageReceiver(AutoBanController control){
        BCRedisConnector.getInstance().getProxy().registerChannel("AutoBan");
        BCRedisConnector.getInstance().getProxy().getPluginManager().registerListener(BCRedisConnector.getInstance(),this);
        this.control = control;
    }

    @EventHandler
    public void onBanMessageReceived(PluginMessageEvent evt){
        if(!evt.getTag().equals("AutoBan")){
            return;
        }
        try( ByteArrayInputStream stream = new ByteArrayInputStream(evt.getData()) ){
            try (DataInputStream in = new DataInputStream(stream)){
                String name = in.readUTF();
                ProxiedPlayer p = BCRedisConnector.getInstance().getProxy().getPlayer(name);
                if(p == null){
                    return;
                }else{
                    String ip = p.getAddress().getAddress().getHostAddress();
                    control.autoBan(name,ip);
                }
            }catch(IOException e){
            }
        }catch(IOException e){

        }


    }

}
