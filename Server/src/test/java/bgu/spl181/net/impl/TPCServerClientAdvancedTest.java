package bgu.spl181.net.impl;

import org.junit.Test;

public class TPCServerClientAdvancedTest extends TPCServerClientTestTemplate{
    @Test(timeout = 50000)
    public void TPCServerClientAdvancedTest() {
        server = A.initiateServer("DemoTPC", A.serverPort);

        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        Thread[] clients = A.initiateClients(100, A.serverIp, A.serverPort,
                "./src/test/resources/Intermediate_Server_Client_Test/","*",null);

        A.waitForClients(clients);
    }
}
