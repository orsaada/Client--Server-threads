package bgu.spl181.net.srv;

import bgu.spl181.net.srv.p_commands.Balance;
import bgu.spl181.net.srv.p_commands.MovieInfo;
import bgu.spl181.net.srv.p_commands.*;

public class MRSProtocol extends USTBProtocol {


    @Override
    protected void request(String msg) {
        String command = getCommand(msg);

        if (!get_connections().getLoginList().containsKey(get_id())) {
            _response = "ERROR request " +  command + " failed";
            _connections.send(_id, _response);
            return;
        }

        command = command.toLowerCase();

        switch (command) {
            case "balance":
                new Balance(this).execute(msg);
                break;
            case "info":
                new MovieInfo(this).execute(msg);
                break;
            case "rent":
                new Rent(this).execute(msg);
                break;
            case "return":
                new ReturnMovie(this).execute(msg);
                break;
            case "addmovie":
                new AddMovie(this).execute(msg);
                break;
            case "remmovie":
                new RemMovie(this).execute(msg);
                break;
            case "changeprice":
                new ChangePrice(this).execute(msg);
                break;
            default:
                _connections.send(_id, "ERROR no such command");
                break;

        }
    }
        public int getNewMovieID(){
            new AddMoviesID().setMoviesID(_connections.getMovieIndex());
            int id = _connections.getMovieIndex().pollLast().intValue();
            _connections.getMovieIndex().add(id + 1);
                return id + 1 ;
        }

}
