package bgu.spl181.net.srv.p_commands;

import bgu.spl181.net.srv.USTBProtocol;
import bgu.spl181.net.srv.bidi.Command;
import bgu.spl181.net.srv.data.User;

import java.io.IOException;
import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;
import java.util.StringTokenizer;

public class Register extends DataAccess implements Command {
    USTBProtocol _prot;

    public Register(USTBProtocol prot){
        _prot = prot;
    }

    /**
     * message from the client
     * @param arg
     * @return
     */
    @Override
    public void execute(String arg)  {
        //array only for pass the value by reference in other function
        String[] response = new String[1];

        List<String> list = new LinkedList<>();
        StringTokenizer st = new StringTokenizer(arg);
        st.nextToken(); //delete REGISTER string

        if(!st.hasMoreElements())
        {
            response[0] = "ERROR registration failed";
            _prot.get_connections().send(_prot.get_id(), response[0]);
            return;
        }

        while (st.hasMoreElements())
            list.add(st.nextToken());

        synchronized (_prot.get_connections().getKey("users")){

            //check the input and if false return the response
            if(!checkInput(list , response)) {
                _prot.get_connections().send(_prot.get_id(), response[0]);
                return;
            }

            User user = null;
            if(list.size() == 3)
            {
                StringTokenizer st2 = new StringTokenizer(list.get(2) , "\"");
                st2.nextToken(); //delete rent string
                list.set(2 , st2.nextToken());
              user = new User(list.get(0) , list.get(1) , list.get(2));
             } else
                   user = new User(list.get(0) , list.get(1) , "");

            //write to the json
            writeDeleteToUsers(user , true);
            _prot.get_connections().send(_prot.get_id(), response[0]);
        }


    }

    //check user input
    private boolean checkInput(List<String> input ,String[] response){

        boolean ans = true;

        if(input.size() < 2 | input.size() > 3)
            ans = false;
        else if(input.size() == 3 && !input.get(2).contains("country="))
            ans = false;
        else if(input.size() == 3 && input.get(2).contains("country="))
        {
            int eq = input.get(2).indexOf('=');
            if(input.get(2).charAt(eq + 1) != '"' | input.get(2).charAt(input.get(2).length() - 1) != '"')
                ans = false;
        }

        if(ans)
            ans = !checkInData(input.get(0) , "username","users");



        if(!ans)
            response[0] = "ERROR registration failed";
        else
            response[0] = "ACK registration succeeded";

        return ans;
    }
}
