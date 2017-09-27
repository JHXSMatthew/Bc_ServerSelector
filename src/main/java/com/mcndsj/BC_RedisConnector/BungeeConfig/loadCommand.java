package com.mcndsj.BC_RedisConnector.BungeeConfig;

import com.mcndsj.BC_RedisConnector.BCRedisConnector;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.command.ConsoleCommandSender;

import java.sql.SQLException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class loadCommand extends Command {
	
	  public loadCommand(){
	      super("serverload");
	      
	  }

	@Override
	public void execute(CommandSender arg0, String[] arg1) {
			if(arg0.hasPermission("whatthefuck.fuck")){
				BCRedisConnector.getInstance().loadServers();
				arg0.sendMessage(new TextComponent("Load request sent!"));
			}
	}
	

}
