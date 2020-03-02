package bgu.spl181.net.impl;

import org.junit.Test;

import java.io.IOException;

public class UTBP_AdvancedTwo_Test extends TPCServerClientTestTemplate{
    @Test(timeout = 50000)
    public void UTBP_AdvancedTwo_Test() throws IOException {
        String testPath = "./src/test/resources/AdvancedTwo_UTBP_Test/";

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

        A.compareDatabases(testPath);
    }
}
