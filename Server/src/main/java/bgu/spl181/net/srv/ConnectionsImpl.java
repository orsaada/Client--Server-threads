package bgu.spl181.net.srv;

import bgu.spl181.net.api.bidi.Connections;
import bgu.spl181.net.srv.bidi.ConnectionHandler;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

public class ConnectionsImpl<T> implements Connections<T> {

    private ConcurrentHashMap<Integer , ConnectionHandler> _active_users = new ConcurrentHashMap();
    private ConcurrentHashMap<Integer , String> _login_users = new ConcurrentHashMap();
    private TreeSet<Integer> moviesIndex = new TreeSet<Integer>();
    protected Object users_key = new Object();
    protected Object comm_key = new Object();

    @Override
    public boolean send(int connectionId, T msg) {

        if(!_active_users.containsKey(connectionId))
            return false;
        _active_users.get(connectionId).send(msg);
        return true;
    }

    @Override
    public void broadcast(T msg) {
        for(Map.Entry<Integer , ConnectionHandler> key : _active_users.entrySet())
        {
            key.getValue().send(msg);
        }

    }

    @Override
    public void disconnect(int connectionId) {

        _active_users.remove(connectionId);
    }

    @Override
    public void addActiveUser(int id ,ConnectionHandler ch) {
        _active_users.put(id , ch);
    }

    public ConcurrentHashMap<Integer , String> getLoginList(){ return _login_users ; }

    public void closeSock(int id){
        try {
            _active_users.get(id).close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public TreeSet<Integer> getMovieIndex(){
            return moviesIndex;
    }

    public Object getKey(String key){
        if(key.equalsIgnoreCase("users"))
            return users_key;
        else if(key.equalsIgnoreCase("common"))
            return comm_key;
        else
            return null;
    }

}
