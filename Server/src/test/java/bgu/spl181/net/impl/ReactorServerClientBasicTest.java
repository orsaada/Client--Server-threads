package bgu.spl181.net.impl;

import bgu.spl181.net.impl.A;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.*;

public class ReactorServerClientBasicTest extends ReactorServerClientTestTemplate{
    @Test(timeout = 50000)
    public void ReactorServerClientBasicTest(){
        server = A.initiateServer("DemoReactor",A.serverPort);

        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        Thread[] clients = A.initiateClients(1,A.serverIp,A.serverPort,
                "./src/test/resources/Basic_Server_Client_Test/",null,null);

        A.waitForClients(clients);

    }
}