package bgu.spl181.net.srv.p_commands;

import bgu.spl181.net.srv.USTBProtocol;
import bgu.spl181.net.srv.data.Movie;
import bgu.spl181.net.srv.data.Movies;

import java.util.TreeMap;
import java.util.TreeSet;

public class AddMoviesID extends DataAccess {

    /**
     * addint new id for the new movie
     * @param movies_id
     */
    public void setMoviesID(TreeSet<Integer> movies_id){

        while(!movies_id.isEmpty())
            movies_id.pollLast();
        Movie[] movies = ((Movies)getTable("movies")).getMovies();
        for(Movie mov : movies) {
            movies_id.add(Integer.parseInt(mov.getField("id")));
        }
    }
}
