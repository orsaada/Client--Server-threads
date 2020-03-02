package bgu.spl181.net.srv.p_commands;

import bgu.spl181.net.api.bidi.Connections;
import bgu.spl181.net.srv.ConnectionsImpl;
import bgu.spl181.net.srv.USTBProtocol;
import bgu.spl181.net.srv.bidi.Command;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.StringTokenizer;

public class Login extends DataAccess implements Command {

    private USTBProtocol _prot;

    public Login( USTBProtocol prot){
        _prot = prot;
    }

    /**
     * message from the client
     * @param arg
     * @return
     */
    @Override
    public void execute(String arg) {

        //array only for pass the value by reference in other function
        String[] response = new String[1];

        //tokenaizer for seperate the user message
        List<String> list = new ArrayList<>();
        StringTokenizer st = new StringTokenizer(arg);
        st.nextToken(); //delete REGISTER string

        while (st.hasMoreElements())
            list.add(st.nextToken());

        synchronized (_prot.get_connections().getKey("users"))
        {
            //checking the currect input
            if(!checkInput(list , response)) {
                _prot.get_connections().send(_prot.get_id(), response[0]);
                return;
            }


            //set the user in the login list
            _prot.get_connections().getLoginList().put(_prot.get_id() , list.get(0));
            _prot.set_log(true);
            _prot.get_connections().send(_prot.get_id(), response[0]);
        }

    }


    //check the user input and the data
    public boolean checkInput(List<String> input, String[] response) {
        boolean ans = true;

            if(input.size() != 2 )
                ans = false;
            else if(!checkLogInData(input.get(0) , input.get(1)) )
                ans = false;
            else if(!checkInData(input.get(0) , "username" ,"users"))
                ans = false;
            else if(_prot.get_connections().getLoginList().contains(input.get(0)))
                ans = false;

        if(!ans)
            response[0] = "ERROR login failed";
        else
            response[0] = "ACK login succeeded";

        return ans;
    }
}
