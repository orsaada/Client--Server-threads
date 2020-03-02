package bgu.spl181.net.impl;

import org.junit.Test;

import java.io.IOException;

public class BB_Addmovie_Advanced_Test extends TPCServerClientTestTemplate{
    @Test(timeout = 50000)
    public void BB_Addmovie_Advanced_Test() throws IOException {
        String testPath = "./src/test/resources/Advanced_Addmovie_Test/";

        A.prepareDatabase(testPath);

        server = A.initiateServer("TPC",A.serverPort);

        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        Thread[] clients = A.initiateClients(100,A.serverIp,A.serverPort,
                testPath,"*",null);

        A.waitForClients(clients);

        A.compareDatabaseslistsSizes(testPath);
    }
}
