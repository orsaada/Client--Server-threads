package bgu.spl181.net.srv.data;

import java.util.LinkedList;
import java.util.List;

public class Users {
    private User[] users;

    public User[] getUsers() {
        return users;
    }

    public void addUser(User user){
        User[] new_users = new User[users.length + 1];

        int i;
        for(i = 0; i < users.length; i++)
            new_users[i] = users[i];
        new_users[i] = user;
        users = new_users;
    }

    public void removeUser(User user){

        for (int i = 0; i < users.length; i++)
        {
            if (users[i].getField("username").equalsIgnoreCase(user.getField("username")))
            {
                User[] copy = new User[users.length - 1];
                System.arraycopy(users, 0, copy, 0, i);
                System.arraycopy(users, i+1, copy, i, users.length - i - 1);
                users = copy;
                return;
            }
        }
    }

    public String toString(){

        String ans = "";

        for(int i = 0; i < users.length; i++)
            ans = ans + " " + users[i].getField("username");
        return ans;
    }

}
