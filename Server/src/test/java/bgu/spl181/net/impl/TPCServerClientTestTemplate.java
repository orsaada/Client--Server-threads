package bgu.spl181.net.impl;

import org.junit.AfterClass;

import java.io.IOException;
import java.net.Socket;

public class TPCServerClientTestTemplate extends ServerClientTestTemplate{
    static Thread server;

    @AfterClass
    public static void finish(){
        try {
            server.interrupt();
            new Socket("localhost", Integer.parseInt(A.serverPort)).close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            server.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        assertTestSuccessful();
    }
}
