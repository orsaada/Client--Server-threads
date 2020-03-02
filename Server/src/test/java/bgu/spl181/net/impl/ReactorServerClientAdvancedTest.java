package bgu.spl181.net.impl;

import org.junit.Test;

public class ReactorServerClientAdvancedTest extends ReactorServerClientTestTemplate{
    @Test(timeout = 50000)
    public void ReactorServerClientAdvancedTest(){
        server = A.initiateServer("DemoReactor",A.serverPort);

        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        Thread[] clients = A.initiateClients(100,A.serverIp,A.serverPort,
                "./src/test/resources/Advanced_Server_Client_Test/","*",null);

        A.waitForClients(clients);
    }
}
