package bgu.spl181.net.impl.BBtpc;

import bgu.spl181.net.impl.echo.LineMessageEncoderDecoder;
import bgu.spl181.net.srv.MRSProtocol;
import bgu.spl181.net.srv.Server;


public class TPCMain {

    public static void main(String[] args){
        Server tcp = Server.threadPerClient(Integer.parseInt(args[0]) , ()->{ return new MRSProtocol(); }, ()->{ return new LineMessageEncoderDecoder(); });
        tcp.serve();
    }
}
