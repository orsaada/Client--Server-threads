package bgu.spl181.net.srv;

import bgu.spl181.net.api.bidi.BidiMessagingProtocol;
import bgu.spl181.net.api.bidi.Connections;
import bgu.spl181.net.srv.bidi.ConnectionHandler;
import bgu.spl181.net.srv.data.User;
import bgu.spl181.net.srv.p_commands.Login;
import bgu.spl181.net.srv.p_commands.Register;
import bgu.spl181.net.srv.p_commands.Signout;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonWriter;
import org.omg.CORBA.Request;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.Serializable;
import java.util.HashMap;
import java.util.StringTokenizer;

public abstract class USTBProtocol implements BidiMessagingProtocol<String> {

    protected String _response;
    protected Connections<String> _connections;
    protected int _id;
    protected boolean _log;
    protected boolean _terminated;

    public USTBProtocol(){
        _log = false;
        _terminated = false;
    }
    @Override
    public void start(int connectionId, Connections<String> connections) {
        _id = connectionId;
        _connections = connections;
    }

    @Override
    public void process(String message) {
        
        String command = getCommand(message);

        command = command.toUpperCase();

        switch (command){

            case "REGISTER":
                new Register(this).execute(message);
                break;
            case "LOGIN":
                new Login(this ).execute(message);
                break;
            case "SIGNOUT":
                new Signout(this ).execute(message);
                break;
            case"REQUEST":
                request(message.substring(8 , message.length()));
                break;
            default:
                _response = "ERROR no such command";
                _connections.send(_id , _response);
                break;
        }
    }

    @Override
    public boolean shouldTerminate() {

        return _terminated;
    }

    protected String getCommand(String msg){
        StringTokenizer st = new StringTokenizer(msg);
        try{
            return st.nextToken();
        }catch (Exception e){
            return "error";
        }

    }

    public void set_log(Boolean _log) {
        this._log = _log;
    }

    public Connections<String> get_connections() {
        return _connections;
    }

    public int get_id() {
        return _id;
    }

    public void set_terminated(boolean _terminated) {
        this._terminated = _terminated;
    }

    public boolean getLog(){
        return  this._log;
    }

    abstract protected void request(String msg);

}
