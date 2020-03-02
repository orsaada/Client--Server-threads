package bgu.spl181.net.srv.p_commands;

import bgu.spl181.net.srv.ConnectionsImpl;
import bgu.spl181.net.srv.USTBProtocol;
import bgu.spl181.net.srv.bidi.Command;
import bgu.spl181.net.srv.data.Movie;
import bgu.spl181.net.srv.data.Movies;
import bgu.spl181.net.srv.data.User;
import bgu.spl181.net.srv.data.Users;
import bgu.spl181.net.srv.p_commands.DataAccess;

import java.util.LinkedList;
import java.util.List;
import java.util.StringTokenizer;

public class RemMovie extends DataAccess implements Command {

    USTBProtocol _prot;

    public RemMovie(USTBProtocol prot) {
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

        StringTokenizer st = new StringTokenizer(arg , "\"");
        st.nextToken(); //delete removie string
        String movieName = st.nextToken();


        User user = null;
        synchronized (_prot.get_connections().getKey("users"))
        {
            //get user info and chek if he admin if not return error message
            user = (User) getData(_prot.get_connections().getLoginList().get(_prot.get_id()),"username" , "users");
            if(user == null || !user.getField("type").equalsIgnoreCase("admin")) {
                _prot.get_connections().send(_prot.get_id(), "ERROR request remmovie failed");
                return;
            }
        }

        Movie  mov = null;

        synchronized (_prot.get_connections().getKey("common"))
        {
            //get movie data
            Movie[] movies = ((Movies) getTable("movies")).getMovies();

            boolean llegal = false;

            //search the movie in the list
            for(Movie movie: movies){
                if(movie.getField("name").equalsIgnoreCase(movieName)) {
                    mov = movie;
                    if(Integer.parseInt(movie.getField("availableAmount")) < Integer.parseInt(movie.getField("totalAmount")))
                        llegal = false;
                    else
                        llegal = true;
                    break;
                }
            }

            if(llegal == false) {
                _prot.get_connections().send(_prot.get_id(), "ERROR request remmovie failed");
                return;
            }

            //remove to json
            writeDeleteToUsers(mov , false);

            //send to broadcast
            _prot.get_connections().send(_prot.get_id() ,"ACK remmovie \""+movieName+"\" success"); //need to be only log in .. need to fix

            for (Integer id :_prot.get_connections().getLoginList().keySet()) {
                _prot.get_connections().send(id,"BROADCAST movie " +'"' + mov.getField("name") + '"'+ " removed");
            }

        }


    }

}