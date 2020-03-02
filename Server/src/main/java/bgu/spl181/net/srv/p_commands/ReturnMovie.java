package bgu.spl181.net.srv.p_commands;

import bgu.spl181.net.srv.USTBProtocol;
import bgu.spl181.net.srv.bidi.Command;
import bgu.spl181.net.srv.data.Movie;
import bgu.spl181.net.srv.data.Movies;
import bgu.spl181.net.srv.data.User;
import bgu.spl181.net.srv.p_commands.DataAccess;
import java.util.List;
import java.util.Map;

public class ReturnMovie extends DataAccess implements Command {

    public USTBProtocol _protocol;
    public ReturnMovie(USTBProtocol protocol) {
        _protocol = protocol;
    }

    /**
     * message from the client
     * @param arg
     * @return
     */
    @Override
    public void execute(String arg) {

        String[] strings = arg.split("[“”\"]");
        String movieName = strings[1];
        Map<Integer , String> loginList = _protocol.get_connections().getLoginList();
        String userName = loginList.get(_protocol.get_id());


        Movie movieRead = null;

        synchronized (_protocol.get_connections().getKey("users"))
        {
            synchronized (_protocol.get_connections().getKey("common"))
            {
                User user = (User) getData(userName, "username", "users");
                Object movie = getData(movieName, "name", "movies");

                if (movie == null) {
                    _protocol.get_connections().send(_protocol.get_id() ,"ERROR request return failed");
                    return;
                }
                Movie found =null ;

                Movie[] userMovies = user.getMovies();
                for (Movie searchMovie : userMovies ) {
                    if (searchMovie.getField("name").equalsIgnoreCase(movieName)) {
                        found = searchMovie;
                        break;
                    }
                }
                if(found == null) {
                    _protocol.get_connections().send(_protocol.get_id() ,"ERROR request return failed");
                    return;
                }
                writeDeleteToUsers(user,false);
                user.setMovies(true,found);
                writeDeleteToUsers(user,true);


                movieRead = (Movie) getData(movieName,"name","movies");
                writeDeleteToUsers(movieRead,false);

                movieRead.setField("availableAmount", ((Integer.parseInt(movieRead.getField("availableAmount")) + 1) + "")); //P
                writeDeleteToUsers(movieRead,true);

                _protocol.get_connections().send(_protocol.get_id() , "ACK return "+'"'+movieName+'"'+" success");

                for (Integer id :_protocol.get_connections().getLoginList().keySet()) {
                    _protocol.get_connections().send(id,"BROADCAST movie "+ "\"" +movieName + "\"" + " "+
                            movieRead.getField("availableAmount")+" "+movieRead.getField("price"));
                }

            }
        }



    }

}