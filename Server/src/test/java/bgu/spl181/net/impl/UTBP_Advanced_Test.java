package bgu.spl181.net.impl;

import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;

public class UTBP_Advanced_Test extends TPCServerClientTestTemplate{
    @Test(timeout = 50000)
    public void UTBP_Advanced_Test() throws IOException {
        String testPath = "./src/test/resources/Advanced_UTBP_Test/";
        AtomicInteger failLoginCounter = new AtomicInteger(0);

        Runnable testLambda = () -> failLoginCounter.incrementAndGet();

        A.prepareDatabase(testPath);

        server = A.initiateServer("TPC", A.serverPort);

        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        Thread[] clients = A.initiateClients(1, A.serverIp, A.serverPort,
                testPath,null,null);

        A.waitForClients(clients);

        A.initiateClients(200,A.serverIp, A.serverPort,
                testPath,"*",testLambda);

        // Waiting for clients to finish there respective commands since they will not terminate by themself as
        // this test need it be be that way, not you can increment the sleep
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        if(failLoginCounter.get() != 199){
            Assert.fail();
        }

        A.compareDatabases(testPath);
    }
}
