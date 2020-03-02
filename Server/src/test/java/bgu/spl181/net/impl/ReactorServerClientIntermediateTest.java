package bgu.spl181.net.impl;

import org.junit.Test;

public class ReactorServerClientIntermediateTest extends ReactorServerClientTestTemplate{
    @Test(timeout = 50000)
    public void ReactorServerClientIntermediateTest(){
        server = A.initiateServer("DemoReactor",A.serverPort);

        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        Thread[] clients = A.initiateClients(30,A.serverIp,A.serverPort,
                "./src/test/resources/Intermediate_Server_Client_Test/","*",null);

        A.waitForClients(clients);
    }
}
