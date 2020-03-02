package bgu.spl181.net.impl.BBreactor;

import bgu.spl181.net.impl.echo.LineMessageEncoderDecoder;
import bgu.spl181.net.srv.MRSProtocol;
import bgu.spl181.net.srv.Server;

public class ReactorMain {

    public static void main(String[] args){

        Server reactor = Server.reactor(5 , Integer.parseInt(args[0]) , ()->{ return new MRSProtocol(); }, ()->{ return new LineMessageEncoderDecoder(); });
        reactor.serve();
    }
}
