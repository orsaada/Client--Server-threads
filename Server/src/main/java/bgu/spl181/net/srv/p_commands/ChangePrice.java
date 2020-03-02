package bgu.spl181.net.srv.p_commands;


import bgu.spl181.net.srv.USTBProtocol;
import bgu.spl181.net.srv.bidi.Command;
import bgu.spl181.net.srv.data.Movie;
import bgu.spl181.net.srv.data.User;
import bgu.spl181.net.srv.p_commands.DataAccess;

import java.util.*;

public class ChangePrice extends DataAccess implements Command {

    private USTBProtocol _protocol;

    public ChangePrice(USTBProtocol protocol) {
        _protocol = protocol;
    }

    /**
     * message from the client
     * @param arg
     * @return
     */
    @Override
    public void execute(String arg) {


        //check if the user is logged in
        String[] strings = arg.split("[“”\"]");
        String movieName = strings[1];
        String price = strings[2].substring(1, strings[2].length());
        Map<Integer , String> loginList = _protocol.get_connections().getLoginList();
        String userName = loginList.get(_protocol.get_id());

        if(userName == null)
            return;


        synchronized (_protocol.get_connections().getKey("users"))
        {

            //get user data
            User user = (User) getData(userName , "username" , "users");
            //if not admin return error
            if(user == null || !user.getField("type").equalsIgnoreCase("admin")) {
                _protocol.get_connections().send(_protocol.get_id(),"ERROR request changeprice failed");
                return;
            }
        }

        //check if the movie not exist and return error
        Object movie = null;
        synchronized (_protocol.get_connections().getKey("common"))
        {
            movie = getData(movieName , "name" , "movies");
            if(movie == null) {
                _protocol.get_connections().send(_protocol.get_id(),"ERROR request changeprice failed");
                return;
            }
            //cehck if the price more then 0
            if(Integer.parseInt(price)<= 0) {
                _protocol.get_connections().send(_protocol.get_id(),"ERROR request changeprice failed");
                return;
            }

            ((Movie)movie).setField("price",price);

            //delete the old movie and add the new one
            writeDeleteToUsers(movie , false);
            writeDeleteToUsers(movie , true);

            //send boroadcast message
            _protocol.get_connections().send(_protocol.get_id(),"ACK changeprice \""+movieName+"\" success");


            for (Integer id :_protocol.get_connections().getLoginList().keySet()) {
                _protocol.get_connections().send(id,"BROADCAST movie "+'"'+movieName+'"' + " " + ((Movie) movie).getField("availableAmount") + " " + price);
            }

        }


    }
}