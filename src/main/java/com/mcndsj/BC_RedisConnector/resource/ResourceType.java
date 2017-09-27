package com.mcndsj.BC_RedisConnector.resource;

/**
 * Created by Matthew on 2/07/2016.
 */
public enum ResourceType {

    empty("http://rank.mcndsj.com/texts/Empty.zip","3934d29cc6f7c271afdc477f6dd6b2ea90493825");

    private String url;
    private String hash;

    ResourceType(String url, String hash){
        this.url = url;
        this.hash = hash;
    }

    public String getURL(){
        return url;
    }

    public String getHash(){
        return hash;
    }
}
