package bgu.spl181.net.impl;

import org.junit.AfterClass;

public class ReactorServerClientTestTemplate extends ServerClientTestTemplate{

    static Thread server;

    @AfterClass
    public static void finish(){
        server.interrupt();
        try {
            server.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        assertTestSuccessful();
    }
}
