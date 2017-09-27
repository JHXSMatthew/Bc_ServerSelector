package com.mcndsj.BC_RedisConnector.LobbySend;

/**
 * Created by Matthew on 21/06/2016.
 */
public class AuthRandomize {

    private static int max = 6;
    private int current;

    public String get(){
        if(current == max){
            current = 1;
        }else{
            current++;
        }
        return "auth"+current;
    }
}
