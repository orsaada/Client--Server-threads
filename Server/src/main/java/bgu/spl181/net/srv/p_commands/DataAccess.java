package bgu.spl181.net.srv.p_commands;

import bgu.spl181.net.srv.data.Movie;
import bgu.spl181.net.srv.data.Movies;
import bgu.spl181.net.srv.data.User;
import bgu.spl181.net.srv.data.Users;
import com.google.gson.*;
import com.google.gson.stream.JsonWriter;

import java.io.*;
import java.util.Arrays;

public abstract class DataAccess {

    /**
     *
     * @param data Object to delete User or Movie
     * @param write true for write to file and false for delete from file
     */
    public void writeDeleteToUsers(Object data , boolean write)  {
            try {
            Gson gson = new Gson();
            File file = null;
            Users users = null;
            Movies movies = null;
                try {
                    FileReader fr = null;
                    if(data instanceof User) //checks if the object is typeof User
                    {
                        fr = new FileReader("Database/Users.json");
                        file = new File("Database/Users.json");
                        users = gson.fromJson(fr, Users.class);
                        //if true write else delete
                        if(write)
                            users.addUser((User) data);
                        else
                            users.removeUser((User) data);
                        FileWriter fileWriter = new FileWriter(file);
                        fileWriter.write(gson.toJson(users));
                        fileWriter.flush();
                        fileWriter.close();
                    }
                    else
                    {
                        fr = new FileReader("Database/Movies.json");
                        file = new File("Database/Movies.json");
                        movies = gson.fromJson(fr, Movies.class);
                        if(write)
                            movies.addMovies((Movie) data);
                        else
                            movies.removeMovies((Movie) data);
                        FileWriter fileWriter = new FileWriter(file);
                        fileWriter.write(gson.toJson(movies));
                        fileWriter.flush();
                        fileWriter.close();
                    }
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
    }

    /**
     *
     * @param key the value that need to chek for example "ronaldo" (username)
     * @param field field that we want to compare example username
     * @param table Moives or Users
     * @return true if find false other wise
     */
    public boolean checkInData(String key , String field, String table){
        
        Gson gson = new Gson();
        if(table.equals("users"))
        {
            Users users =  new Users();
            try {
                users = gson.fromJson(new FileReader("Database/Users.json") , Users.class);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }

            for(User user : users.getUsers())
                if(user.getField(field).equalsIgnoreCase(key))
                    return true;
        }
        else if(table.equalsIgnoreCase("movies"))
        {
            Movies movies =  new Movies();
            try {
                movies = gson.fromJson(new FileReader("Database/Movies.json") , Movies.class);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }

            for(Movie movie : movies.getMovies()){
                if(movie.getField(field).equalsIgnoreCase(key))
                    return true;
            }

        }
        return false;
    }

    /**
     *
     * @param username username to check if login
     * @param pass password of the username
     * @return true if equals other wise false
     */
    public boolean checkLogInData(String username , String pass){

        Gson gson = new Gson();
        Users users =  new Users();
        try {
            users = gson.fromJson(new FileReader("Database/Users.json") , Users.class);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        for(User user : users.getUsers())
            if(user.getField("username").equalsIgnoreCase(username) && user.getField("password").equals(pass))
                return true;

        return false;
    }

    /**
     *
     * @param user_movie the value of the user/movie field example "rick and morty" name of the movie
     * @param field field that we want to compare example username
     * @param table choose between Movie or User data
     * @return true if find other wise false
     */
    public Object getData(String user_movie , String field , String table){
        Gson gson = new Gson();
        if(table.equalsIgnoreCase("users"))
        {
            Users users =  new Users();
            try {
                users = gson.fromJson(new FileReader("Database/Users.json") , Users.class);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            for(User user_name : users.getUsers())
                if(user_name.getField(field).equalsIgnoreCase(user_movie))
                    return user_name;
        }
        else if(table.equalsIgnoreCase("movies"))
        {
            Movies movies =  new Movies();
            try {
                movies = gson.fromJson(new FileReader("Database/Movies.json") , Movies.class);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }

            for(Movie movie : movies.getMovies())
                if(movie.getField("name").equalsIgnoreCase(user_movie))
                    return movie;
        }
        return null;
    }

    /**
     *
     * @param table Movies or Users
     * @return the all table of the data
     */
    public Object getTable(String table){
        Gson gson = new Gson();
        if(table.equalsIgnoreCase("users"))
        {
            Users users =  new Users();
            try {
                users = gson.fromJson(new FileReader("Database/Users.json") , Users.class);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
                    return users;
        }
        else if(table.equalsIgnoreCase("movies"))
        {
            Movies movies =  new Movies();
            try {
                movies = gson.fromJson(new FileReader("Database/Movies.json") , Movies.class);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
                    return movies;
        }
        return null;
    }


}
