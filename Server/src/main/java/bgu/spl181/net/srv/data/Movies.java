package bgu.spl181.net.srv.data;

import java.util.LinkedList;
import java.util.List;

public class Movies {
    private Movie[] movies;

    public Movie[] getMovies() {
        return movies;
    }

    @Override
    public String toString() {
        String ans = "";
        for(int i = 0; i < movies.length; i++)
            ans += '"' + movies[i].getField("name") + '"' + " ";
        ans = ans.substring(0 , ans.length() - 1);
        return ans;
    }

    public void addMovies(Movie movie){
        Movie[] new_movies = new Movie[movies.length + 1];

        int i;
        new_movies[0] = movie;
        for(i = 0; i < movies.length; i++)
            new_movies[i + 1] = movies[i];

        movies = new_movies;
    }

    public void removeMovies(Movie movie){

        for (int i = 0; i < movies.length; i++)
        {
            if (movies[i].getField("name").equalsIgnoreCase(movie.getField("name")))
            {
                Movie[] copy = new Movie[movies.length - 1];
                System.arraycopy(movies, 0, copy, 0, i);
                System.arraycopy(movies, i+1, copy, i, movies.length - i - 1);
                movies = copy;
                return;
            }
        }

    }
}
