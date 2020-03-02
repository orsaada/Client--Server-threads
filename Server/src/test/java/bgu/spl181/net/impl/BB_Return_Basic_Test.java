package bgu.spl181.net.impl;

import org.junit.Test;

import java.io.IOException;

public class BB_Return_Basic_Test extends TPCServerClientTestTemplate{
    @Test(timeout = 50000)
    public void BB_Return_Basic_Test() throws IOException {
        String testPath = "./src/test/resources/Basic_Return_Test/";

        A.prepareDatabase(testPath);

        server = A.initiateServer("TPC",A.serverPort);

        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        Thread[] clients = A.initiateClients(1,A.serverIp,A.serverPort,
                testPath,null,null);

        A.waitForClients(clients);

        A.compareDatabases(testPath);
    }
}
