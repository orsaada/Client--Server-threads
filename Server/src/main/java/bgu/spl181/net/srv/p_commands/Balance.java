package bgu.spl181.net.srv.p_commands;

import bgu.spl181.net.srv.BaseServer;
import bgu.spl181.net.srv.USTBProtocol;
import bgu.spl181.net.srv.bidi.Command;
import bgu.spl181.net.srv.data.User;

import java.util.LinkedList;
import java.util.List;
import java.util.StringTokenizer;

public class Balance extends DataAccess implements Command{

    USTBProtocol _prot;

    public Balance(USTBProtocol prot){

        _prot = prot;
    }

    /**
     * message from the client
     * @param arg
     * @return
     */
    @Override
    public void execute(String arg) {

        String response = "";

        //seperate the client message for arguments
        List<String> list = new LinkedList<>();
        StringTokenizer st = new StringTokenizer(arg);
        st.nextToken(); //delete REGISTER string

        while (st.hasMoreElements())
            list.add(st.nextToken());

        //checks if the user ask for info or add balance
        if(list.get(0).equalsIgnoreCase("info"))
       {
           User user = null;
           synchronized (_prot.get_connections().getKey("users"))
           {
               //get user object and return the data as needed
               user = (User) getData(_prot.get_connections().getLoginList().get(_prot.get_id()), "username", "users");
               response = "ACK balance " + user.getField("balance");
               _prot.get_connections().send(_prot.get_id() , "ACK balance " + user.getField("balance"));
           }

       } else if(list.get(0).equalsIgnoreCase("add"))
       {
           synchronized (_prot.get_connections().getKey("users"))
           {
               //get user and add the balance
               User user = (User) getData(_prot.get_connections().getLoginList().get(_prot.get_id()), "username", "users");
               //if the balance is les the 0 return error
               if(Integer.parseInt(list.get(1)) < 0) {
                   _prot.get_connections().send(_prot.get_id() , "ERROR request balance add failed");
                   return;
               }

               user.setField("balance", list.get(1));
               //frist delete the user then add new one with the currect fields
               writeDeleteToUsers(user, false);
               writeDeleteToUsers(user, true);
               //send the message
               _prot.get_connections().send(_prot.get_id() , "ACK balance " + user.getField("balance") + " added " +  list.get(1));
           }

       }

    }


}
