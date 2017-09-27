package com.mcndsj.BC_RedisConnector.BungeeConfig;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.concurrent.TimeUnit;

import com.mcndsj.BC_RedisConnector.BCRedisConnector;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.scheduler.TaskScheduler;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;

public class ConfigHelper
{
    private static File file;
    private static Configuration bungeeConfig;
    private static boolean locked;

    public static void addToConfig(ServerInfo serverInfo)
    {
        if (locked) {
            return;
        }

        bungeeConfig.set("servers." + serverInfo.getName() + ".motd", serverInfo.getMotd().replace('§', '&'));
        bungeeConfig.set("servers." + serverInfo.getName() + ".address", serverInfo.getAddress().getHostName() + ":" + serverInfo.getAddress().getPort());
        bungeeConfig.set("servers." + serverInfo.getName() + ".restricted", Boolean.valueOf(false));
        saveConfig();
    }

    public static void removeFromConfig(ServerInfo serverInfo) {
        removeFromConfig(serverInfo.getName());
    }

    public static void removeFromConfig(String name) {
        if (locked) {
            return;
        }

        bungeeConfig.set("servers." + name, null);
        saveConfig();
    }

    private static void saveConfig() {
        if (locked) {
            return;
        }
        try
        {
            YamlConfiguration.getProvider(YamlConfiguration.class).save(bungeeConfig, file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void setupConfig() {
        try {
            file = new File(ProxyServer.getInstance().getPluginsFolder().getParentFile(), "config.yml");
            bungeeConfig = YamlConfiguration.getProvider(YamlConfiguration.class).load(file);
        } catch (IOException e) {
            e.printStackTrace();
        }

        locked = bungeeConfig == null;
    }

    static
    {
        setupConfig();

        if (locked)
            ProxyServer.getInstance().getScheduler().schedule(BCRedisConnector.getInstance(), new Runnable()
                    {
                        public void run() {
                            ConfigHelper.saveConfig();
                        }
                    }
                    , 5L, TimeUnit.SECONDS);
    }
}