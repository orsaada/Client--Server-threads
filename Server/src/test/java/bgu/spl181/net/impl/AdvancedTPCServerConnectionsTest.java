package bgu.spl181.net.impl;

import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;

public class AdvancedTPCServerConnectionsTest extends TPCServerClientTestTemplate{
    @Test(timeout = 50000)
    public void AdvancedTPCServerConnectionsTest() throws IOException {
        String testPath = "./src/test/resources/AdvancedServerConnectionsTest/";

        AtomicInteger failLoginCounter = new AtomicInteger(0);

        Runnable testLambda = () -> failLoginCounter.incrementAndGet();

        server = A.initiateServer("DemoTPCConnections",A.serverPort);

        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        Thread[] clients = A.initiateClients(20,A.serverIp,A.serverPort,
                testPath,"*",testLambda);

        A.waitForClients(clients);

        if(failLoginCounter.get() > 20){
            Assert.fail();
        }
    }
}
