package mocosose17.wgapp;


import java.util.HashMap;
import java.util.Map;

public class UserAdapter {
    private String name;
    private double fistEdition;


    private double investment;
    private boolean isPositiv;
    // Betrag den man an den User (Key) Bezahlen muss
    private Map<String, Double> usersSchulden = new HashMap<String, Double>();
    //Betrag, den man vom User (Key) bekommt
    private Map <String, Double> usersHaben = new HashMap<String, Double>();


    public UserAdapter(String name, double fistEdition) {
        super();
        this.name = name;
        this.fistEdition = fistEdition;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public double getInvestment() {
        return investment;
    }
    public void setInvestment(double investment) {
        this.investment = investment;
    }
    public boolean isPositiv() {
        return isPositiv;
    }
    public Map<String, Double> getUsersSchulden() {

        return usersSchulden;
    }

    public Map<String, Double> getUsersHaben() {
        return usersHaben;
    }

    public void setPositiv(boolean isPositiv) {
        this.isPositiv = isPositiv;
    }


    public double getFistEdition() {
        return fistEdition;
    }

    public void setFistEdition(double fistEdition) {
        this.fistEdition = fistEdition;
    }
    //Hinzufügen der Schulden Mit Name des zu bezahlenden und zu Bezahlenden Betrages
    public void addInvestmentToUser(String user, double investment){
        double i = 0;
        if(usersSchulden.containsKey(user)){
            System.out.println("schulden");

            double inv = usersSchulden.get(user);
            System.out.println("userbisherige schulden:" + usersSchulden.get(user));

            i = inv + investment;

            usersSchulden.replace(user, i);
        }else {
            usersSchulden.put(user, investment);
        }

    }

    //Hinzufügen des bekommens. Mit Namen, des zu bekommendes und des bekommenden Betrages
    public void addHabenToUser(String user, double investment){
        System.out.println("investment"+ investment);
        if(usersHaben.containsKey(user)){
            double inv = usersHaben.get(user);

            double i = inv + investment;
            usersHaben.replace(user, i);
        }else {
            usersHaben.put(user, investment);
        }

    }
}
