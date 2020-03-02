package bgu.spl181.net.srv.p_commands;

import bgu.spl181.net.srv.USTBProtocol;
import bgu.spl181.net.srv.bidi.Command;
import bgu.spl181.net.srv.data.Movie;
import bgu.spl181.net.srv.data.User;
import bgu.spl181.net.srv.data.Users;

import java.util.LinkedList;
import java.util.List;
import java.util.StringTokenizer;

import static java.lang.Thread.sleep;

public class Rent extends DataAccess implements Command {

    USTBProtocol _prot;

    public Rent(USTBProtocol prot){
        _prot = prot;
    }

    /**
     * message from the client
     * @param arg
     * @return
     */
    @Override
    public void execute(String arg) {

        List<String> list = new LinkedList<>();
        StringTokenizer st = new StringTokenizer(arg , "\"");
        st.nextToken(); //delete rent string
        String movie_name = st.nextToken();

        Movie movie = null;

        synchronized (_prot.get_connections().getKey("users"))
        {
            synchronized (_prot.get_connections().getKey("common"))
            {
                User user = (User) getData( _prot.get_connections().getLoginList().get(_prot.get_id()),"username", "users");
                movie = (Movie)getData( movie_name,"name", "movies");

                if(movie == null || Integer.parseInt(movie.getField("price")) > Integer.parseInt(user.getField("balance")) ||
                        Integer.parseInt(movie.getField("availableAmount")) == 0) {
                    _prot.get_connections().send(_prot.get_id() ,"ERROR request rent failed");
                    return;
                }

                for(int i = 0; i < user.getMovies().length; i++)
                {
                    if(user.getMovies()[i].getField("name").equalsIgnoreCase(movie.getField("name"))) {
                        _prot.get_connections().send(_prot.get_id() ,"ERROR request rent failed");
                        return;
                    }
                }


                for(int i = 0; i < movie.getBannedCountries().length; i++) {
                    if (movie.getBannedCountries()[i].equalsIgnoreCase(user.getField("country"))) {
                        _prot.get_connections().send(_prot.get_id(), "ERROR request rent failed");
                        return;
                    }
                }

                user.setMovies(false , movie);
                user.setField("balance" , -Integer.parseInt(movie.getField("price")) + "");
                movie.setField("availableAmount" , Integer.parseInt(movie.getField("availableAmount")) - 1 + "");


                writeDeleteToUsers(user , false);
                writeDeleteToUsers(user , true);


                writeDeleteToUsers(movie , false);
                writeDeleteToUsers(movie , true);

                _prot.get_connections().send(_prot.get_id() ,"ACK rent " + "\""+ movie.getField("name") + "\"" + " success");

                for (Integer id :_prot.get_connections().getLoginList().keySet()) {
                    _prot.get_connections().send(id,"BROADCAST movie " + movie.ToStringNoCountries());
                }

            }
        }


    }

}
