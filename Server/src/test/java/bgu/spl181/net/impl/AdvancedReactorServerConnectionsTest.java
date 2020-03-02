package bgu.spl181.net.impl;

import bgu.spl181.net.impl.BBreactor.ReactorMain;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;

public class AdvancedReactorServerConnectionsTest extends ReactorServerClientTestTemplate{
    @Test(timeout = 50000)
    public void AdvancedReactorServerConnectionsTest() throws IOException {
        String testPath = "./src/test/resources/AdvancedServerConnectionsTest/";

        AtomicInteger failLoginCounter = new AtomicInteger(0);

        Runnable testLambda = () -> failLoginCounter.incrementAndGet();

        server = A.initiateServer("DemoReactorConnections",A.serverPort);

        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        Thread[] clients = A.initiateClients(40,A.serverIp,A.serverPort,
                testPath,"*",testLambda);

        A.waitForClients(clients);

        if(failLoginCounter.get() > 20){
            Assert.fail();
        }
    }
}
