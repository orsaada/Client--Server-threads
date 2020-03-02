package bgu.spl181.net.srv.p_commands;

import bgu.spl181.net.srv.MRSProtocol;
import bgu.spl181.net.srv.USTBProtocol;
import bgu.spl181.net.srv.bidi.Command;
import bgu.spl181.net.srv.data.Movie;
import bgu.spl181.net.srv.data.User;
import bgu.spl181.net.srv.p_commands.DataAccess;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

public class AddMovie extends DataAccess implements Command {

    private MRSProtocol _protocol;

    public AddMovie(MRSProtocol protocol) {
        _protocol = protocol;
    }

    /*
    @param String arg client massage
     */
    @Override
    public void execute(String arg) {

        //tokenaizer for split the client massage
        StringTokenizer st = new StringTokenizer(arg, "\"");
        List<String> str = new ArrayList<String>();
        List<String> countrys = new ArrayList<String>();

        st.nextToken().trim(); // remove addmovie word

        while (st.hasMoreTokens())
            str.add(st.nextToken());   //add to the list all the words seperated

        //init all the words to string ver
            String movieName = str.get(0);
        String price_amount = str.get(1);

        //splitin the two numbers price and amount
        st = new StringTokenizer(price_amount , " ");
        String amount = st.nextToken();
        String price = st.nextToken();

        //adding all the countries to the list and from list to array
        for(int i = 2; i < str.size(); i++)
            if(!str.get(i).equals(" "))
                countrys.add(str.get(i));
        String[] countries = countrys.toArray(new String[countrys.size()]);

        Map<Integer , String> loginList = _protocol.get_connections().getLoginList();
        String userName = loginList.get(_protocol.get_id());

        Movie toAdd = null;
        synchronized (_protocol.get_connections().getKey("users"))
        {
            synchronized (_protocol.get_connections().getKey("common"))
            {

                //get user data and check if he is admin
                User user = (User) getData(userName , "username" , "users");
                if(!user.getField("type").equalsIgnoreCase("admin")) {
                    _protocol.get_connections().send(_protocol.get_id(), "ERROR request addmovie failed");
                    return;
                }
                //check if this movie not exist alredy
                Object movie = getData(movieName , "name" , "movies");
                if(movie != null) {
                    _protocol.get_connections().send(_protocol.get_id(), "ERROR request addmovie failed");
                    return;
                }
                if(Integer.parseInt(price) <= 0 || Integer.parseInt(amount) <= 0) {
                    _protocol.get_connections().send(_protocol.get_id(), "ERROR request addmovie failed");
                    return;
                }

                //create new movie
                toAdd = new Movie(String.valueOf(_protocol.getNewMovieID()), movieName ,
                        price, countries, amount , amount); // problem in the index

                //add new movie
                writeDeleteToUsers(toAdd , true);
                _protocol.get_connections().send(_protocol.get_id(),"ACK addmovie \""+ movieName +"\" success");

                //send a broadcast message
                for (Integer id :_protocol.get_connections().getLoginList().keySet()) {
                    _protocol.get_connections().send(id,"BROADCAST movie "+ toAdd.ToStringNoCountries());
                }

            }
        }


        return;
    }
}