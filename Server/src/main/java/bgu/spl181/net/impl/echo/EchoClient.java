package bgu.spl181.net.impl.echo;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Scanner;

import bgu.spl181.net.srv.data.User;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonWriter;

public class EchoClient {

    public static void main(String[] args) throws IOException {

        LineMessageEncoderDecoder enden = new LineMessageEncoderDecoder();

        if (args.length == 0) {
            args = new String[]{"localhost", "hello"};
        }

        if (args.length < 2) {
            System.out.println("you must supply two arguments: host, message");
            System.exit(1);
        }

        //BufferedReader and BufferedWriter automatically using UTF-8 encoding



        try (Socket sock = new Socket(/*args[0]*/"localhost", 7777);
                BufferedReader in = new BufferedReader(new InputStreamReader(sock.getInputStream()));
                BufferedWriter out = new BufferedWriter(new OutputStreamWriter(sock.getOutputStream()))) {

            PrintWriter writerOut = new PrintWriter(sock.getOutputStream() , true);

            Thread t = new Thread(()->{
                while (true){
                    Scanner myScanner = new Scanner(System.in);
                    String s = myScanner.nextLine();
                    writerOut.println(s);
                    System.out.println("awaiting response");
                }
            });

            t.start();
            while(1 == 1){

//                System.out.println("sending message to server");
//                out.write(args[1]);
//                out.newLine();
//                out.flush();

                String line = "";
                line = in.readLine();
                System.out.println("message from server: " + line);

            }

        }
    }
}
