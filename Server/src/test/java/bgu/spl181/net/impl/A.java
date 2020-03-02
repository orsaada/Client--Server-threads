package bgu.spl181.net.impl;

import bgu.spl181.net.api.MessageEncoderDecoder;
import bgu.spl181.net.api.bidi.BidiMessagingProtocol;
import bgu.spl181.net.api.bidi.Connections;
import bgu.spl181.net.impl.BBreactor.ReactorMain;
import bgu.spl181.net.impl.BBtpc.TPCMain;
import bgu.spl181.net.srv.Server;
import com.google.gson.Gson;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Vector;

import static org.junit.Assert.*;

public class A {
    static String serverPort = "2222";
    static String serverIp = "127.0.0.1";
    static volatile AssertionError exc;
    static volatile boolean startTest = false;
    static Object startTestSyncObject = new Object();

    @Test
    public void clientCodePreparation(){
        /*
        System.out.println("Preparing client code...\n");
        System.out.println("---------------------------------------------------------------------------------------");
        ClientTest.makeClientCode();
        System.out.println("---------------------------------------------------------------------------------------" +
                "\n");
        System.out.println("Test starting...\n\n");
        */
    }

    public static Thread[] initiateClients(int number,String ip,String port,String testResourcesPath,
                                           String generalCLient,Runnable testLambda){
        Thread[] clients = new Thread[number];
        exc = null;
        String clientName;
        startTest = false;

        for(int i = 0; i < clients.length;i++){
            if(generalCLient != null && generalCLient.equals("*")){
                clientName = "Client" + generalCLient;
            }
            else{
                clientName = "Client" + i;
            }

            clients[i] = new Thread(new ClientTest(ip,port,clientName,"" + i,
                    testResourcesPath + clientName,testLambda));
        }

        for(int i = 0; i < clients.length;i++){
            clients[i].start();
        }

        synchronized (startTestSyncObject){
            startTest = true;
            startTestSyncObject.notifyAll();
        }

        return clients;
    }

    public static Thread initiateServer(String type,String port){
        Thread server = null;
        String streamInput = null;
        int i;

        if(type.equals("Reactor")){
            server = new Thread(new ReactorServerTest(port));
            server.start();
        }
        else if(type.equals("TPC")){
            server = new Thread(new TPCServerTest(port));
            server.start();
        }
        else if(type.equals("DemoReactor")){
            server = new Thread(new DemoReactorServerTest(port));
            server.start();
        }
        else if(type.equals("DemoTPC")){
            server = new Thread(new DemoTPCServerTest(port));
            server.start();
        }
        else if(type.equals("DemoTPCConnections")){
            server = new Thread(new DemoTPCConnectionsTest(port));
            server.start();
        }
        else if(type.equals("DemoReactorConnections")){
            server = new Thread(new DemoReactorConnectionsTest(port));
            server.start();
        }
        else{
            System.out.println("Server type unidentified");
            Assert.fail();
        }

        // Creating a stream to capture server start message
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrintStream ps = new PrintStream(baos);

        // Saving old stream to put it back when finished
        PrintStream old = System.out;

        // Setting the capture stream
        System.setOut(ps);

        // Waiting for server started message
        for(i = 0;i < 10;i++){
            try {
                Thread.sleep(300);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.flush();

            // Retrieving stream input
            streamInput = baos.toString();

            // Asserting server start message in input
            if(streamInput.contains("Server started")){
                break;
            }
        }

        if(i == 10){
            Assert.fail();
        }

        // Put things back
        System.setOut(old);

        // Show what happened
        System.out.println(streamInput);

        return server;
    }

    public static void waitForClients(Thread[] clients){
        for(int i = 0; i < clients.length;i++){
            try {
                clients[i].join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        if (exc != null){
            throw exc;
        }
    }


    public static Vector<String> readTestDataFromFile(String commandsFilePath){

        Vector<String> testDataLines = new Vector<>();

        try(BufferedReader reader = new BufferedReader(new FileReader(commandsFilePath), 1024)){
            String line;

            while((line = reader.readLine()) != null) {
                testDataLines.add(line);
            }
        }
        catch (IOException exp){
            exp.printStackTrace();
            System.out.println("Error during read of file !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
            Assert.fail();
        }

        return testDataLines;
    }

    public static <T> T retrieveDataFromJSON(String path, Class<T> classOfT) {
        // Initializing
        String json = null;
        Gson gson = new Gson();

        // Reading json file to string
        try {
            json = new String(Files.readAllBytes(Paths.get(path)));
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Parsing json file to java object
        return gson.fromJson(json, classOfT);
    }

    public static void prepareDatabase(String theDatabasePath) throws IOException {
        String[] fileTypes = new String[]{"Users.json","Movies.json"};

        for(String type : fileTypes){
            prepareDatabaseFile(theDatabasePath,type);
        }
    }

    public static void prepareDatabaseFile(String theDatabaseFilePath,String theFileType) throws IOException {
        // Initializing
        String json = null;

        // Reading json file from string
        json = new String(Files.readAllBytes(Paths.get(theDatabaseFilePath + "Database/" + theFileType)));

        // Writing json string to file
        try (PrintWriter out = new PrintWriter("./Database/" + theFileType)) {
            // Writing
            out.println(json);
        }
    }

    public static void compareDatabaseslistsSizes(String testDatabasePath){
        BlockBusterUsersHardData usersTest = retrieveDataFromJSON(testDatabasePath + "Result_Database/Users.json",
                BlockBusterUsersHardData.class);
        BlockBusterUsersHardData usersActual = retrieveDataFromJSON(  "Database/Users.json",
                BlockBusterUsersHardData.class);

        BlockBusterMoviesHardData moviesTest = retrieveDataFromJSON(testDatabasePath + "Result_Database/Movies.json",
                BlockBusterMoviesHardData.class);
        BlockBusterMoviesHardData moviesActual = retrieveDataFromJSON(  "Database/Movies.json",
                BlockBusterMoviesHardData.class);

        if(usersTest.users.length != usersActual.users.length || moviesTest.movies.length != moviesActual.movies.length){
            Assert.fail();
        }
    }

    public static void compareDatabases(String testDatabasePath){
        if(!(compareUsersInDatabase(testDatabasePath) && compareMoviesInDatabase(testDatabasePath))){
            Assert.fail();
        }
    }

    public static boolean compareUsersInDatabase(String testDatabasePath){
        BlockBusterUsersHardData usersTest = retrieveDataFromJSON(testDatabasePath + "Result_Database/Users.json",
                BlockBusterUsersHardData.class);
        BlockBusterUsersHardData usersActual = retrieveDataFromJSON(  "Database/Users.json",
                BlockBusterUsersHardData.class);

        if(usersTest.users.length != usersActual.users.length){
            return false;
        }

        for(BlockBusterHardDataOfUser testUser: usersTest.users){
            boolean foundEqual = false;

            for(BlockBusterHardDataOfUser actualUser : usersActual.users){
                if(testUser.username.equals(actualUser.username) &&
                        testUser.password.equals(actualUser.password) &&
                        testUser.type.equals(actualUser.type) &&
                        testUser.country.equals(actualUser.country) &&
                        testUser.balance == actualUser.balance){
                    if(compareUsersMovies(testUser,actualUser)){
                        foundEqual = true;
                        actualUser.username = Math.random() + "";
                        actualUser.password = Math.random() + "";
                    }
                    else{
                        return false;
                    }
                }
            }

            if(!foundEqual){
                return false;
            }
        }

        return true;

    }

    public static boolean compareMoviesInDatabase(String testDatabasePath){
        BlockBusterMoviesHardData moviesTest = retrieveDataFromJSON(testDatabasePath + "Result_Database/Movies.json",
                BlockBusterMoviesHardData.class);
        BlockBusterMoviesHardData moviesActual = retrieveDataFromJSON(  "Database/Movies.json",
                BlockBusterMoviesHardData.class);

        if(moviesTest.movies.length != moviesActual.movies.length){
            return false;
        }

        for(BlockBusterHardDataOfMovie testMovie: moviesTest.movies){
            boolean foundEqual = false;

            for(BlockBusterHardDataOfMovie actualMovie : moviesActual.movies){
                if(testMovie.id == actualMovie.id &&
                        testMovie.availableAmount == actualMovie.availableAmount &&
                        testMovie.totalAmount == actualMovie.totalAmount &&
                        testMovie.name.equals(actualMovie.name) &&
                        testMovie.price == actualMovie.price){
                    if(comapreBannedCountries(testMovie,actualMovie)){
                        foundEqual = true;
                        actualMovie.id = (int)(Math.random()*1000);
                    }
                    else{
                        return false;
                    }
                }
            }

            if(!foundEqual){
                return false;
            }

        }

        return true;
    }

    public static boolean compareUsersMovies(BlockBusterHardDataOfUser testUser,BlockBusterHardDataOfUser actualUser){
        if(testUser.movies.length != actualUser.movies.length){
            return false;
        }

        for(UserMovie testUserMovie: testUser.movies){
            boolean foundEqual = false;

            for(UserMovie actualUserMovie: actualUser.movies){
                if(testUserMovie.id == actualUserMovie.id &&
                        testUserMovie.name.equals(actualUserMovie.name)){
                    foundEqual = true;
                    actualUserMovie.id = (int)(Math.random()*1000);
                }
            }

            if(!foundEqual){
                return false;
            }
        }

        return true;
    }

    public static boolean comapreBannedCountries(BlockBusterHardDataOfMovie testMovie,BlockBusterHardDataOfMovie actualMovie){
        if(testMovie.bannedCountries.length != actualMovie.bannedCountries.length){
            return false;
        }

        for(String testCountry: testMovie.bannedCountries){
            boolean foundEqual = false;

            for(String actualCountry: actualMovie.bannedCountries){
                if(testCountry.equals(actualCountry)){
                    foundEqual = true;
                    testCountry = (int)(Math.random()*1000) + "";
                }
            }

            if(!foundEqual){
                return false;
            }
        }

        return true;
    }

    public static boolean contains(Vector<String> vector,String str){
        boolean contain = false;

        for(int i = 0;i < vector.size();i++){
            if(vector.get(i).equals(str)){
                contain = true;
                vector.set(i,Math.random()*1000 + "");
                break;
            }
        }

        return contain;

    }

    public static class ReactorServerTest implements Runnable{

        String port;

        public ReactorServerTest(String port){
            this.port = port;
        }

        @Override
        public void run(){
            ReactorMain.main(new String[]{this.port});
        }
    }

    public static class TPCServerTest implements Runnable{

        String port;

        public TPCServerTest(String port){
            this.port = port;
        }

        @Override
        public void run(){
            TPCMain.main(new String[]{this.port});
        }
    }

    public static class DemoReactorServerTest implements Runnable{

        String port;

        public DemoReactorServerTest(String port){
            this.port = port;
        }

        @Override
        public void run(){
            DemoReactorServerMain.main(new String[]{this.port});
        }
    }

    public static class DemoTPCServerTest implements Runnable{

        String port;

        public DemoTPCServerTest(String port){
            this.port = port;
        }

        @Override
        public void run(){
            DemoTPCServerMain.main(new String[]{this.port});
        }
    }

    public static class DemoTPCConnectionsTest implements Runnable{

        String port;

        public DemoTPCConnectionsTest(String port){
            this.port = port;
        }

        @Override
        public void run(){
            DemoTPCConnectionsMain.main(new String[]{this.port});
        }
    }

    public static class DemoReactorConnectionsTest implements Runnable{

        String port;

        public DemoReactorConnectionsTest(String port){
            this.port = port;
        }

        @Override
        public void run(){
            DemoReactorConnectionsMain.main(new String[]{this.port});
        }
    }

    public static class ClientTest implements Runnable{

        String ipAndPort;
        String clientFilePath;
        String clientName;
        OutputStreamWriter clientTestResultsStream;
        Runnable testRunnable;
        String clientNumber;

        public ClientTest(String ip,String port,String clientName,String clientNumber,String commandsFilePath,Runnable testRunnable){

            this.ipAndPort = ip + " " + port;
            this.clientName = clientName;
            this.clientFilePath = commandsFilePath + "/" + clientName;
            this.testRunnable = testRunnable;
            this.clientNumber = clientNumber;
        }

        private void print(String message) throws IOException {
            String toPrint = clientName + ": " + message;
            System.out.println(toPrint);
            this.clientTestResultsStream.write(toPrint);
            this.clientTestResultsStream.write("\n");
            this.clientTestResultsStream.flush();
        }

        @Override
        public void run(){
            synchronized (startTestSyncObject){
                while(startTest == false){
                    try {
                        startTestSyncObject.wait();
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        return;
                    }
                }
            }

            int index = 0;
            int broadcastIndex = 0;
            Vector<String> commands= null;
            Vector<String> serverResponse = null;
            Vector<String> serverBroadcast= null;

            try {

                Process p;
                p = Runtime.getRuntime().exec("./BBclient " + this.ipAndPort,
                        null, new File("./../Client/bin"));

                try(OutputStreamWriter theClientTestResultsStream = new OutputStreamWriter(new FileOutputStream(
                        clientFilePath + "_testResults"));
                    BufferedReader input = new BufferedReader(new InputStreamReader(p.getInputStream()));
                    BufferedWriter output = new BufferedWriter(new OutputStreamWriter(p.getOutputStream()))) {

                    this.clientTestResultsStream = theClientTestResultsStream;

                    String line;

                    commands = readTestDataFromFile(clientFilePath + "_commands");

                    serverResponse = readTestDataFromFile(clientFilePath + "_responses");

                    serverBroadcast = readTestDataFromFile(clientFilePath + "_broadcasts");

                    String wildCard = Math.random()*1000000 + "";
                    for(int i = 0;i < commands.size();i++){
                        commands.set(i,commands.get(i).replace("*",wildCard));
                    }

                    for(int i = 0;i < commands.size();i++){
                        commands.set(i,commands.get(i).replace("+",this.clientNumber));
                    }

                    for(int i = 0;i < serverResponse.size();i++){
                        serverResponse.set(i,serverResponse.get(i).replace("+",this.clientNumber));
                    }

                    line = input.readLine();
                    print(line);

                    print("Client Message ===> " + commands.get(index));
                    output.write(commands.get(index) + "\n");
                    output.flush();
                    line = input.readLine();
                    index++;

                    do {
                        print(line);

                        if (line.contains("BROADCAST")) {
                            if (!serverBroadcast.get(broadcastIndex).equals("*")) {
                                if(!contains(serverBroadcast,line)){
                                    try{
                                        Assert.fail();
                                    }
                                    catch (AssertionError exp){
                                        exc = exp;
                                        Assert.fail();
                                    }
                                }
                                broadcastIndex++;
                            }
                            else if(serverBroadcast.get(broadcastIndex).equals("NO BROADCAST SHOULD BE RECEIVED")){
                                Assert.fail();
                            }
                        }

                        if(index ==  (commands.size() - 1)
                                && serverBroadcast.size() > 1 && broadcastIndex != serverBroadcast.size()){
                            try {
                                Thread.sleep(5000);
                            } catch (InterruptedException e) {
                                Thread.currentThread().interrupt();
                            }
                        }

                        if (index <= commands.size()) {


                            try {
                                if (!line.contains("BROADCAST")) {
                                    if(testRunnable == null){
                                        assertEquals(serverResponse.get(index - 1),line);
                                    }
                                    else{
                                        if(!serverResponse.get(index - 1).equals(line)){
                                            testRunnable.run();
                                        }
                                    }

                                    if(index < commands.size()){
                                        print("Client Message ===> " + commands.get(index));
                                        output.write(commands.get(index) + "\n");
                                        output.flush();
                                    }

                                    index++;
                                }
                            } catch (AssertionError e) {
                                exc = e;
                                Assert.fail();
                            }
                        } else if(index <= commands.size() + 1){
                            print("Test Message ===> Test Ended !!! Waiting for client termination...");
                            index++;
                        }
                    } while ((line = input.readLine()) != null);

                    if(!(index >= commands.size() && (
                            (serverBroadcast.size() == 1 && serverBroadcast.get(0).equals("*")) ||
                                    (broadcastIndex == serverBroadcast.size()) ||
                                    (serverBroadcast.size() == 1 && serverBroadcast.get(0).equals(
                                            "NO BROADCAST SHOULD BE RECEIVED")))
                            )){
                        try{
                            Assert.fail();
                        }
                        catch (AssertionError exp){
                            exc = exp;
                            Assert.fail();
                        }
                    }
                }
            } catch (IOException e) {
                try{
                    Assert.fail();
                }
                catch (AssertionError exp){
                    exc = exp;
                    Assert.fail();
                }
            }
            catch (Exception e) {
                e.printStackTrace();
                try{
                    Assert.fail();
                }
                catch (AssertionError exp){
                    exc = exp;
                    Assert.fail();
                }
            }
        }

        public static void makeClientCode(){
            executeCommand("make -C ../Client clean");
            executeCommand("make -C ../Client");
        }

        public static void executeCommand(String command){
            Process p = null;
            try {
                p = Runtime.getRuntime().exec(command);
            } catch (IOException e) {
                e.printStackTrace();
            }

            BufferedReader input = new BufferedReader(new InputStreamReader(p.getInputStream()));

            String line;

            try {
                while ((line = input.readLine()) != null){
                    System.out.println("Client: " + line);
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static class DemoReactorServerMain {
        public static int main(String[] args) {
            // Initializing
            Integer port = null;

            // Retrieving port number from user
            if (args.length >= 1) {
                // Attempting to pars port to int
                try {
                    port = Integer.parseInt(args[0]);
                } catch (NumberFormatException ignore) {
                }
            }

            // Asserting port has been parsed successfully
            if (port == null) {
                // Declaring bad input and exiting
                System.out.println("Bad or no port was given !!!");
                return 1;
            }

            // Initializing reactor server and starting it
            Server.reactor(
                    Runtime.getRuntime().availableProcessors(),
                    port, //port
                    () -> new TestProtocol(), //protocol factory
                    TestMessageEncoderDecoder::new //message encoder decoder factory
            ).serve();

            return 0;
        }
    }

    public static class DemoTPCServerMain {
        public static int main(String[] args) {
            // Initializing
            Integer port = null;

            // Retrieving port number from user
            if (args.length >= 1) {
                // Attempting to pars port to int
                try {
                    port = Integer.parseInt(args[0]);
                } catch (NumberFormatException ignore) {
                }
            }

            // Asserting port has been parsed successfully
            if (port == null) {
                // Declaring bad input and exiting
                System.out.println("Bad or no port was given !!!");
                return 1;
            }

            // Initializing reactor server and starting it
            Server.threadPerClient(
                    port, //port
                    () -> new TestProtocol(), //protocol factory
                    TestMessageEncoderDecoder::new //message encoder decoder factory
            ).serve();

            return 0;
        }
    }

    public static class DemoTPCConnectionsMain {
        public static int main(String[] args) {
            // Initializing
            Integer port = null;

            // Retrieving port number from user
            if (args.length >= 1) {
                // Attempting to pars port to int
                try {
                    port = Integer.parseInt(args[0]);
                } catch (NumberFormatException ignore) {
                }
            }

            // Asserting port has been parsed successfully
            if (port == null) {
                // Declaring bad input and exiting
                System.out.println("Bad or no port was given !!!");
                return 1;
            }

            // Initializing reactor server and starting it
            Server.threadPerClient(
                    port, //port
                    () -> new TestConnectionsProtocol(), //protocol factory
                    TestMessageEncoderDecoder::new //message encoder decoder factory
            ).serve();

            return 0;
        }
    }

    public static class DemoReactorConnectionsMain {
        public static int main(String[] args) {
            // Initializing
            Integer port = null;

            // Retrieving port number from user
            if (args.length >= 1) {
                // Attempting to pars port to int
                try {
                    port = Integer.parseInt(args[0]);
                } catch (NumberFormatException ignore) {
                }
            }

            // Asserting port has been parsed successfully
            if (port == null) {
                // Declaring bad input and exiting
                System.out.println("Bad or no port was given !!!");
                return 1;
            }

            // Initializing reactor server and starting it
            Server.reactor(
                    Runtime.getRuntime().availableProcessors(),
                    port, //port
                    () -> new TestConnectionsProtocol(), //protocol factory
                    TestMessageEncoderDecoder::new //message encoder decoder factory
            ).serve();

            return 0;
        }
    }

    public static class TestProtocol implements BidiMessagingProtocol<String> {

        // Hold the connection handler id which this protocol instance belongs to
        protected Integer connectionHandlerId;

        // Hold all server connections the connection handler belongs to
        protected Connections<String> connections;

        // Indicate if the client should terminate
        protected boolean shouldTerminate = false;

        int count = 0;

        /**
         * Setting up protocol
         *
         * @param connectionId -
         *                     the connection id of the handler that uses of this protocol
         * @param connections -
         *                    the connections of the server the handler belongs to
         */
        public void start(int connectionId, Connections<String> connections) {
            // Initializing
            this.connectionHandlerId = connectionId;
            this.connections = connections;
        }

        /**
         * Processing message by protocol
         *
         * @param message -
         *                the message to process
         */
        public void process(String message) {
            count++;

            if(count == 10){
                connections.send(connectionHandlerId,"ACK signout succeeded");
                shouldTerminate = true;
            }
            else{
                connections.send(connectionHandlerId,"Response from server");
            }
        }

        /**
         * @return true if the connection should be terminated and false otherwise
         */
        public boolean shouldTerminate() {
            return shouldTerminate;
        }

    }

    public static class TestConnectionsProtocol implements BidiMessagingProtocol<String> {

        // Hold the connection handler id which this protocol instance belongs to
        protected Integer connectionHandlerId;

        // Hold all server connections the connection handler belongs to
        protected Connections<String> connections;

        // Indicate if the client should terminate
        protected boolean shouldTerminate = false;

        int count = 0;

        /**
         * Setting up protocol
         *
         * @param connectionId -
         *                     the connection id of the handler that uses of this protocol
         * @param connections -
         *                    the connections of the server the handler belongs to
         */
        public void start(int connectionId, Connections<String> connections) {
            // Initializing
            this.connectionHandlerId = connectionId;
            this.connections = connections;
        }

        /**
         * Processing message by protocol
         *
         * @param message -
         *                the message to process
         */
        public void process(String message) {
            count++;

            if(count == 40){
                shouldTerminate = true;
                connections.send(connectionHandlerId,"ACK signout succeeded");
            }
            else{
                connections.send(connectionHandlerId,"Response from server");
                connections.broadcast("BROADCAST THREAD SAFE TEST !!!");
            }
        }

        /**
         * @return true if the connection should be terminated and false otherwise
         */
        public boolean shouldTerminate() {
            return shouldTerminate;
        }

    }

    public static class TestMessageEncoderDecoder implements MessageEncoderDecoder<String> {

        // Hold the current message in bytes
        private byte[] bytes = new byte[1 << 10]; //start with 1k

        // Hold the current message length
        private int len = 0;

        /**
         * Decoding next byte of message
         *
         * @param nextByte the next byte to consider for the currently decoded
         *                 message
         * @return next message or null if message was not decoded completely yet
         */
        @Override
        public String decodeNextByte(byte nextByte) {
            // Asserting current byte is end of message
            if (nextByte == "\n".getBytes(StandardCharsets.UTF_8)[0]) {
                // Returning decoded message
                return popString();
            }

            // Adding byte to current byte data of message
            pushByte(nextByte);

            // Declaring current message has not yet been completely retrieved
            return null;
        }

        /**
         * @param message -
         *                the message to encode
         * @return the encoded message in a byte array
         */
        @Override
        public byte[] encode(String message) {
            // Encoding message
            return (message + "\n").getBytes(StandardCharsets.UTF_8);
        }

        /**
         * Adding next byte to current byte data of message
         *
         * @param nextByte -
         *                 next byte to add to current byte data of message
         */
        private void pushByte(byte nextByte) {
            // Asserting byte array is not full yet
            if (len >= bytes.length) {
                // Increasing size of byte array
                bytes = Arrays.copyOf(bytes, len * 2);
            }

            // Adding byte
            bytes[len++] = nextByte;
        }


        /**
         * @return current decoded message
         */
        private String popString() {
            // Decoding byte data of message
            String result = new String(bytes, 0, len, StandardCharsets.UTF_8);

            // Resetting byte data length to receive new messages
            len = 0;

            // Returning decoded message
            return result;
        }
    }

    class BlockBusterUsersHardData{

        BlockBusterHardDataOfUser[] users;

        BlockBusterUsersHardData(){
            users = new BlockBusterHardDataOfUser[0];
        }
    }

    // A user hard data class of block buster
    class BlockBusterHardDataOfUser{

        String username;

        String type;

        String password;

        String country;

        UserMovie[] movies;

        int balance;

        BlockBusterHardDataOfUser(String username,String password,String type,String country,UserMovie[] movies,int balance){
            this.username = username;
            this.password = password;
            this.type = type;
            this.country = country;
            this.movies = movies;
            this.balance = balance;
        }
    }

    // A user rented movie object
    class UserMovie{
        int id;

        String name;

        UserMovie(int id, String name){
            this.id = id;
            this.name = name;
        }
    }

    class BlockBusterMoviesHardData {

        BlockBusterHardDataOfMovie[] movies;

        BlockBusterMoviesHardData(){
            movies = new BlockBusterHardDataOfMovie[0];
        }
    }

    // A movie hard data class of block buster
    class BlockBusterHardDataOfMovie {

        int id;

        String name;

        int totalAmount;

        int price;

        String[] bannedCountries;

        int availableAmount;

        BlockBusterHardDataOfMovie(int id, String name, int amount, int price, String[] bannedCountries) {
            this.id = id;
            this.name = name;
            this.totalAmount = amount;
            this.availableAmount = this.totalAmount;
            this.price = price;
            this.bannedCountries = bannedCountries;
        }
    }
}