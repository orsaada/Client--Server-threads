package bgu.spl181.net.srv.p_commands;

import bgu.spl181.net.srv.USTBProtocol;
import bgu.spl181.net.srv.bidi.Command;
import bgu.spl181.net.srv.data.Movie;
import bgu.spl181.net.srv.data.Movies;

import java.util.LinkedList;
import java.util.List;
import java.util.StringTokenizer;

public class MovieInfo extends DataAccess implements Command{

    USTBProtocol _prot;

    public MovieInfo(USTBProtocol prot){
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

        List<String> list = new LinkedList<>();

        int first = 0;
        for(int i = 0; i < arg.length(); i++)
        {
          if((arg.charAt(i) == ' '))
          {
              first = i;
              break;
          }
        }

        if(first == 0)
            list.add(arg);
        else
        {
            list.add(arg.substring(0 , first));
            list.add(arg.substring(first + 2 , arg.length() - 1));
        }

        synchronized (_prot.get_connections().getKey("common"))
        {
            //check the movie info
            checkInput(list , response);
            _prot.get_connections().send(_prot.get_id(), response[0]);
        }


    }

    private boolean checkInput(List<String> input , String[] response){

       if(input.size() != 1)
       {
           //get movie data from json
           Movie movie = (Movie)getData( input.get(1),"name", "movies");
           //null if the movie dosnot exist
           if(movie == null)
           {
               response[0] = "ERROR The movie does not exist";
               return false;
           }
           response[0] = "ACK info " + movie;
       } else if(input.size() == 1)
       {
           Movies movies = (Movies)getTable("movies");
           response[0] = "ACK info " + movies;
       }

        return true;
    }
}
