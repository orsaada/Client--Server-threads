package bgu.spl181.net.srv.p_commands;

import bgu.spl181.net.api.bidi.Connections;
import bgu.spl181.net.srv.USTBProtocol;
import bgu.spl181.net.srv.bidi.Command;
import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;
import java.util.StringTokenizer;

public class Signout extends DataAccess implements Command {

    private USTBProtocol _prot;

    public Signout(USTBProtocol prot){
        _prot = prot;
    }

    /**
     * message from the client
     * @param arg
     * @return
     */
    @Override
    public void execute(String arg) {

        String[] response = new String[1];
        List<String> list = new LinkedList<>();
        StringTokenizer st = new StringTokenizer(arg);

        while (st.hasMoreElements())
            list.add(st.nextToken());

       synchronized (_prot.get_connections().getKey("users"))
       {
            if(!checkInput(list,response))
            {
                _prot.get_connections().send(_prot.get_id() , response[0]);
                return;
            }

            _prot.get_connections().send(_prot.get_id() , response[0]);
            _prot.get_connections().getLoginList().remove(_prot.get_id());
            _prot.get_connections().disconnect(_prot.get_id());
            _prot.set_terminated(true);

           _prot.get_connections().send(_prot.get_id() , response[0]);
       }



    }


    public boolean checkInput(List<String> input, String[] response) {
        boolean size = input.size() != 1;

        if(size || !_prot.getLog()){
            response[0] = "ERROR signout failed";
            return false;
        } else{
            response[0] = "ACK signout succeeded";
        }
        return true;
    }
}