package com.mcndsj.BC_RedisConnector.resource;

import java.util.List;

/**
 * Created by Matthew on 2/07/2016.
 */
public class ResourceWrapper {

    private  String serverType;
    private  String[] notReplaceWithEpty;

    public ResourceWrapper(String serverType, String... others){
        this.notReplaceWithEpty = others;
        this.serverType = serverType;
    }

    public boolean shouldSetEmpty(String server){
        for(String s : notReplaceWithEpty){
            if(server.contains(s)){
                return false;
            }
        }
        return true;
    }

    public boolean isThisTheServer(String s){
        return s.contains(serverType);
    }
}
