package bgu.spl181.net.srv.data;

public class Movie {


    private String id;
    private String name;
    private String price;
    private String[] bannedCountries;
    private String availableAmount;
    private String totalAmount;


    public Movie(String id, String name, String price, String[] bannedCountries, String availableAmount, String totalAmount) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.bannedCountries = bannedCountries;
        this.availableAmount = availableAmount;
        this.totalAmount = totalAmount;
    }


    public String getField(String field) {
        switch (field){
            case "id":
                return id;
            case "name":
                return name;
            case "price":
                return price;
            case "availableAmount":
                return availableAmount;
            case "totalAmount":
                return totalAmount;
        }

        return null;
    }

    public void setField(String field , String value) {
        switch (field){
            case "id":
                id = value;
                break;
            case "name":
                name = value;
                break;
            case "price":
                 price = value;
                 break;
            case "availableAmount":
                 availableAmount = value;
                 break;
            case "totalAmount":
                 totalAmount = value;
                 break;
        }
    }

    public String[] getBannedCountries(){
        return bannedCountries;
    }

    @Override
    public String toString(){
        String ans = "\"" + name + "\"" + " " + availableAmount + " " + price;

        for(int i = 0; i < bannedCountries.length; i++){
            ans += " " + '"' + bannedCountries[i] + '"';
        }

        return ans;
    }

    public String ToStringNoCountries(){
        String ans = "\"" + name + "\"" + " " + availableAmount + " " + price;

        return ans;
    }
}

