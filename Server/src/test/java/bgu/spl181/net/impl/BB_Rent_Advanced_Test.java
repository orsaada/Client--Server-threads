package bgu.spl181.net.impl;

import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;

public class BB_Rent_Advanced_Test extends TPCServerClientTestTemplate{
    @Test(timeout = 50000)
    public void BB_Rent_Advanced_Test() throws IOException {
        String testPath = "./src/test/resources/Advanced_Rent_Test/";
        AtomicInteger failLoginCounter = new AtomicInteger(0);
        Runnable testLambda = () -> failLoginCounter.incrementAndGet();

        A.prepareDatabase(testPath);

        server = A.initiateServer("TPC",A.serverPort);

        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        Thread[] clients = A.initiateClients(100,A.serverIp,A.serverPort,
                testPath,"*",testLambda);

        A.waitForClients(clients);

        if(failLoginCounter.get() != 90){
            Assert.fail();
        }

        if(!A.compareMoviesInDatabase(testPath)){
            Assert.fail();
        }
    }
}
