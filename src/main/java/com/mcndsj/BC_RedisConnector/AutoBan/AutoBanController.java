package com.mcndsj.BC_RedisConnector.AutoBan;

import com.mcndsj.BC_RedisConnector.BCRedisConnector;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.event.PreLoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

/**
 * Created by Matthew on 2016/4/22.
 */
public class AutoBanController implements Listener{

    JedisController backend ;
    MessageReceiver message;

    public AutoBanController(){
        this.backend = new JedisController();
        this.message = new MessageReceiver(this);
    }

    @EventHandler
    public void onLogin(PreLoginEvent evt){
        evt.registerIntent(BCRedisConnector.getInstance());
        ProxyServer.getInstance().getScheduler().runAsync(BCRedisConnector.getInstance(), () -> {
            try {
                String str = getBannedStr(evt.getConnection().getAddress().getAddress().getHostAddress());
                if (str != null) {
                    evt.setCancelled(true);
                    evt.setCancelReason(ChatColor.RED + "YourCraft反作弊 >> 您因被怀疑使用第三方程序而被制裁,剩余 " + str + " 在制裁期间内您将无法登录游戏，请勿使用第三方工具，否则将会被永久封停！ 如果您相信本次制裁是误报，请至论坛置顶帖回复反馈！");
                }
            }catch (Exception e){
                e.printStackTrace();
            }
            evt.completeIntent(BCRedisConnector.getInstance()); // Allow the event to process again
        });

    }

    public String getBannedStr(String ip){
        long l = backend.getBannedTime(ip);
        if(l <= 0){
            return null;
        }
        StringBuilder sb = new StringBuilder();
        if(l > 3600){
            sb.append( l/3600 ).append(" 小时 ");
        }
        if(l > 60){
            sb.append(l%3600 / 60).append(" 分钟 ");
        }

        sb.append(l%3600%60).append(" 秒");
        return sb.toString();
    }


    public void autoBan(String name, String ip){
        backend.setBanned(ip,1);
    }
}
