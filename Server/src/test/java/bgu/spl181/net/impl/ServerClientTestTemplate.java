package bgu.spl181.net.impl;

import org.junit.BeforeClass;

public class ServerClientTestTemplate {
    @BeforeClass
    public static void initialization(){
        A.exc = null;

    }

    public static void assertTestSuccessful(){
        if(A.exc != null){
            throw A.exc;
        }
    }
}
