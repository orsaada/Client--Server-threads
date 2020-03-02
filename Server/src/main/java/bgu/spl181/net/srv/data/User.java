package bgu.spl181.net.srv.data;

import java.io.Serializable;

public class User implements Serializable {

     private String username;
     private String type;
     private String password;
     private String country;
     private Movie[] movies;
     private String balance;

     public User(String username, String password, String country){
         this.username = username;
         this.password = password;
         this.country = country;
         this.type = "normal";
         this.movies = new Movie[0];
         this.balance = "0";
     }

    public Movie[] getMovies(){
        return movies;
    }

    public String getField(String field){

         switch (field){
             case "username":
                 return username;
             case "password":
                 return password;
             case "country":
                 return  country;
             case "balance":
                 return  balance;
             case "type":
                 return type;
         }
         return null;
    }

    public void setField(String field , String new_value){

        switch (field){
            case "username":
                username = new_value;
            case "password":
                password = new_value;
            case "country":
                country = new_value;
            case "balance":
                int new_balance = Integer.parseInt(balance) + Integer.parseInt(new_value);
                balance = "" + new_balance;
        }
    }

    public void setMovies(boolean delete , Movie movie){
         if(delete){
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
         }else{
             Movie[] new_movies = new Movie[movies.length + 1];

             int i;
             new_movies[0] = movie;
             for(i = 0; i < movies.length; i++)
                 new_movies[i + 1] = movies[i];

             movies = new_movies;
         }
    }
}
