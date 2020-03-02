package bgu.spl181.net.api.bidi;

import bgu.spl181.net.srv.bidi.ConnectionHandler;

import java.io.IOException;
import java.util.List;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;

public interface Connections<T> {

    boolean send(int connectionId, T msg);

    void broadcast(T msg);

    void disconnect(int connectionId);

    void addActiveUser(int id , ConnectionHandler handler);

    ConcurrentHashMap<Integer , String> getLoginList();

    void closeSock(int id);

    public TreeSet<Integer> getMovieIndex();

    public Object getKey(String key);
}