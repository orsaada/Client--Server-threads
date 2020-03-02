package bgu.spl181.net.impl;

import bgu.spl181.net.impl.A;
import org.junit.AfterClass;
import org.junit.Test;

import java.io.IOException;
import java.net.Socket;

import static org.junit.Assert.*;

public class TPCServerClientIntermediateTest extends TPCServerClientTestTemplate{
    @Test(timeout = 50000)
    public void TPCServerClientIntermediateTest() {
        server = A.initiateServer("DemoTPC", A.serverPort);

        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        Thread[] clients = A.initiateClients(30, A.serverIp, A.serverPort,
                "./src/test/resources/Intermediate_Server_Client_Test/","*",null);

        A.waitForClients(clients);
    }
}
