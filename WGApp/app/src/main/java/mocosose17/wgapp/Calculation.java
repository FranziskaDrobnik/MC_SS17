package mocosose17.wgapp;


import java.util.ArrayList;

/**
 * Klasse zur Berechnung des Investments der User
 */
public class Calculation {

    //sammelt alle User mit positiven Ausgaben (Haben)
    private ArrayList<UserAdapter> PosUsers= new ArrayList<UserAdapter>();

    //sammelt alle User mit negativen Ausgaben (Schulden)
    private ArrayList <UserAdapter> NegUsers= new ArrayList<UserAdapter>();



    public ArrayList<UserAdapter> getPosUsers() {
        return PosUsers;
    }


    public void setPosUsers(ArrayList<UserAdapter> posUsers) {
        PosUsers = posUsers;
    }


    public ArrayList<UserAdapter> getNegUsers() {
        return NegUsers;
    }


    public void setNegUsers(ArrayList<UserAdapter> negUsers) {
        NegUsers = negUsers;
    }


    /**
     * Bekommt alle User der App in einer ArrayList. Berechnet die Summe aller Ausgaben
     * der User und teilt sie durch die Anzahl der User (sum).
     * Danach wird von jedem einzelnen User die Ausgabe - sum berechnet und zum User hinzugefügt.
     * Ist das Ergebnis negativ wird der User zur Arraylist NegUsers hinzugefügt.
     * Ist es positiv wird er zu PosUsers hinzugefügt
     * @param users
     * @return alle User mit den Berechnetetn Werten des
     *         Investments in user.getUsersSchulden bzw getUsersHaben.
     */
    public ArrayList<UserAdapter> setzeWerte(ArrayList <UserAdapter> users){



        // Ausrechnen der Summe der Ausgaben aller User und teilen durch die Anzahl der User
        double sum = 0;
        for(int i =0; i< users.size(); i++) {
            sum += users.get(i).getFistEdition();
        }
        double output = sum/users.size();

        // setzen der positiven oder negativen Ausgaben der einzelnen User und zuteilen in die Liste.
        for(int i =0; i< users.size(); i++) {
            users.get(i).setInvestment(users.get(i).getFistEdition()- output);
            if (users.get(i).getInvestment() < 0){
                users.get(i).setPositiv(false);
                NegUsers.add(users.get(i));
            } else {
                users.get(i).setPositiv(true);
                PosUsers.add(users.get(i));
            }
        }


        // Aufrufen der eigentlichen Berechnung des Investments
        callUserInvestment(PosUsers.get(0),NegUsers.get(0));

        // alle User mit den richtigen investment werten in eine Arraylist schreiben und zurück geben.
        ArrayList <UserAdapter>  alleUser = new ArrayList<UserAdapter>();
        for (int i = 0; i< PosUsers.size(); i++) {
            alleUser.add(PosUsers.get(i));
        }
        for (int i = 0; i< NegUsers.size(); i++) {
            alleUser.add(NegUsers.get(i));
        }

        return alleUser;

    }

    /**
     *
     * @param pos User mit investment Haben
     * @param neg User mit investment schulden
     */
    public void callUserInvestment(UserAdapter pos,UserAdapter neg){

        int result = calc(pos, neg);

        UserAdapter posUser = null;
        UserAdapter negUser = null;
        boolean weiterPos = false;
        boolean weiterNeg = false;

        switch (result){
            case 1:
                // weiter mit NegUser Zahl. PosUser bleibt
                for(int i =0; i< NegUsers.size(); i++){
                    if(NegUsers.get(i).getName().equals(neg.getName())) {
                        if (i + 1 < NegUsers.size()) {
                            negUser = NegUsers.get(i + 1);
                            // erneuter Aufruf der funktion mit den nächsten Usern
                            callUserInvestment(pos, negUser);
                        }
                    }

                }

                break;

            case 2:
                // weiter mit PosUser, NegUser bleibt.
                for(int i =0; i< PosUsers.size(); i++){
                    if(PosUsers.get(i).getName().equals(pos.getName())){
                        if (i + 1 < PosUsers.size()) {
                            posUser = PosUsers.get(i + 1);
                            // erneuter Aufruf der funktion mit den nächsten Usern
                            callUserInvestment(posUser, neg);
                        }
                    }

                }
                break;

            case 3:
                // weiter mit PosUser und NegUser.
                for(int i =0; i< PosUsers.size(); i++){
                    if(PosUsers.get(i).getName().equals(pos.getName())){
                        if((i+1) < PosUsers.size()){
                            posUser = PosUsers.get(i+1);
                            negUser = neg;
                            weiterPos= true;
                        }
                    }
                }
                for(int i =0; i< NegUsers.size(); i++){
                    if(NegUsers.get(i).getName().equals(neg.getName())){
                        if((i+1) > NegUsers.size()){
                            negUser = NegUsers.get(i+1);
                            posUser = pos;
                            weiterNeg = true;
                        }
                    }
                }
                if(weiterNeg && weiterPos ) {
                    // erneuter Aufruf der funktion mit den nächsten Usern
                    callUserInvestment(negUser,posUser);
                } else
                    return;

                break;

        }

    }


    /**
     * Berechnung des investments der User und Eintragen der Ergebnisse zu den Usern
     * @param pos
     * @param neg
     * @return
     */
    private int calc(UserAdapter pos, UserAdapter neg) {
        double result;
        double postnegativInvestment=0;

        // Das Investment der positven Users wird abgezogen vom Negativen User
        result = neg.getInvestment() + pos.getInvestment();

        if (result > 0){

            System.out.println("if > 0 " + pos.getName()+ pos.getInvestment() + neg.getName() + neg.getInvestment());
            System.out.println("result 1 :" + result);


            // Beim Negativen User wird das investment und der Name des Positiven Users
            // in die Liste der Schulden eingetragen-
            for( int i = 0; i < NegUsers.size(); i++) {
                if (NegUsers.get(i).getName().equals(neg.getName())){
                    NegUsers.get(i).addInvestmentToUser(pos.getName(),neg.getInvestment() *(-1) );
                    postnegativInvestment= neg.getInvestment() *(-1);
                    NegUsers.get(i).setInvestment(0);

                }
            }
            // Beim Positiven User wird der Name und das Investment des
            // Negativen Users in die Liste des Habens eingetragen-
            for( int i =0; i < PosUsers.size(); i++) {
                if (PosUsers.get(i).getName().equals(pos.getName())){
                    PosUsers.get(i).addHabenToUser(neg.getName(),postnegativInvestment);
                    PosUsers.get(i).setInvestment(result);
                }
            }
            return 1; // weiter mit negativen User. Positiver User bleibt.


        } if (result < 0) {


            System.out.println("if < 0 " + pos.getName()+ pos.getInvestment() + neg.getName() + neg.getInvestment());
            System.out.println("result2: " + result);

            // Beim negativen User wird der Name und das Investment
            // des positiven users in die Liste der Schulden eingetragen.
            for( int i =0; i < NegUsers.size(); i++) {
                if (NegUsers.get(i).equals(neg)){
                    NegUsers.get(i).addInvestmentToUser(pos.getName(), pos.getInvestment());

                    NegUsers.get(i).setInvestment(result);

                }
            }
            // Beim positiven User wird der Name des Negativen Users und das
            // eigene Investment in die Liste des Habens eingetragen.
            for( int i = 0; i < PosUsers.size(); i++) {
                if (PosUsers.get(i).equals(pos)){
                    PosUsers.get(i).addHabenToUser(neg.getName(),pos.getInvestment());
                    PosUsers.get(i).setInvestment(0);

                }
            }


            return 2; // weiter mit positiven User. Negativer User bleibt.


        }if (result == 0){

            System.out.println("result3:" + result);
            System.out.println("if==0"+ pos.getName()+ pos.getInvestment() + neg.getName() + neg.getInvestment());

            //Beim negativen User wird der Name des positiven Users und das
            // eigene investment in die Liste der Schulden eingetragen.
            for( int i =0; i < NegUsers.size(); i++) {
                if (NegUsers.get(i).equals(neg)){
                    NegUsers.get(i).addInvestmentToUser(pos.getName(), neg.getInvestment()*(-1));
                    postnegativInvestment = neg.getInvestment()*(-1);
                    NegUsers.get(i).setInvestment(0);

                }
            }
            // Beim positiven User wird der Name des negativen und
            // das investment des negativen Users in die Liste des Habens eingetragen
            for( int i =0; i < PosUsers.size(); i++) {
                if (PosUsers.get(i).equals(pos)){
                    PosUsers.get(i).addHabenToUser(neg.getName(),postnegativInvestment);
                    PosUsers.get(i).setInvestment(0);

                }
            }

            return 3; // weiter mit positiven User und negativen User.


        }	else
            return 0;
    }


}
