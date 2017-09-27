package com.mcndsj.BC_RedisConnector.Utils;

import com.mcndsj.JHXSMatthew.Shared.GameManager;
import com.mcndsj.JHXSMatthew.Shared.LobbyManager;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;

/**
 * Created by Matthew on 2016/4/22.
 */
public class SQLUtils {

    // name, port
    public static HashMap<String,String> getServerFromDB(){
        Connection connection = null;
        Statement statement = null;
        ResultSet resultSet = null;
        HashMap<String,String> map = new HashMap<>();
        int count = 0;
        try {
            connection = GameManager.getInstance().getConnection();
            if(connection == null || connection.isClosed()){
                return map;
            }
            statement = connection.createStatement();
            resultSet = statement.executeQuery("SELECT * FROM `BungeeConfiguration`;");
            while(resultSet.next()){
                map.put(resultSet.getString("name"),resultSet.getString("address") + ":" + resultSet.getInt("port"));
                count ++;
            }
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        finally{
            if (resultSet != null) try { resultSet.close(); } catch (SQLException e) {e.printStackTrace();}
            if (statement != null) try { statement.close(); } catch (SQLException e) {e.printStackTrace();}
            if (connection != null) try { connection.close(); } catch (SQLException e) {e.printStackTrace();}
        }
        System.out.println("Load " + count + "Server from Configuration DB!");
        return map;
    }


    public static String getLastLobby(String name){
        Connection connection = null;
        Statement statement = null;
        ResultSet resultSet = null;
        String returnValue = "lobby";
        try {
            connection = LobbyManager.getInstance().getConnection();
            if(connection == null || connection.isClosed()){
                return "lobby";
            }
            statement = connection.createStatement();
            resultSet = statement.executeQuery("SELECT LastLobby FROM `LobbyPlayers` Where `Name`='"+name+"';");
            if(resultSet.next()){
                returnValue = resultSet.getString("LastLobby");
            }

        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        finally{
            if (resultSet != null) try { resultSet.close(); } catch (SQLException e) {e.printStackTrace();}
            if (statement != null) try { statement.close(); } catch (SQLException e) {e.printStackTrace();}
            if (connection != null) try { connection.close(); } catch (SQLException e) {e.printStackTrace();}
        }
        return returnValue;
    }

    public static int isPlayerInLobbySheet(String name){
        Connection connection = null;
        Statement statement = null;
        ResultSet resultSet = null;
        int returnValue = -1;
        try {
            connection = LobbyManager.getInstance().getConnection();
            if(connection == null || connection.isClosed()){
                returnValue =  -1;
            }else {
                statement = connection.createStatement();
                resultSet = statement.executeQuery("SELECT id FROM `LobbyPlayers` Where `Name`='" + name + "';");
                if (resultSet.next()) {
                    returnValue = 1;
                }else{
                    returnValue = 0;
                }
            }
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        finally {
            if (resultSet != null) try {
                resultSet.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            if (statement != null) try {
                statement.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            if (connection != null) try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return returnValue;
    }

    public static void setLastServerType(String player,String lobby){
        int value = isPlayerInLobbySheet(player);
        if( value == -1){
            return;
        }

        Connection connection = null;
        Statement statement = null;
        try {
            connection = LobbyManager.getInstance().getConnection();
            if(connection == null || connection.isClosed()){
                return;
            }
            statement = connection.createStatement();
            if(value == 0){
                statement.executeUpdate("INSERT INTO `LobbyPlayers` (`Name`,`LastLobby`) VALUES ('"+player+"','"+ lobby + "');");
            }else{
                statement.executeUpdate("UPDATE `LobbyPlayers` SET `LastLobby`='"+ lobby +"' Where `Name`='"+player+"';");
            }
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        finally {
            }
            if (statement != null) try {
                statement.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            if (connection != null) try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

}
