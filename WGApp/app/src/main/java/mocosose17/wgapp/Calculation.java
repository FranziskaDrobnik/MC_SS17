package mocosose17.wgapp;


import java.util.ArrayList;

public class Calculation {
    private ArrayList<UserAdapter> PosUsers= new ArrayList<UserAdapter>();
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


    public ArrayList<UserAdapter> setzeWerte(ArrayList <UserAdapter> users){



        // Ausrechnen der Gesamtsumme
        double sum = 0;
        for(int i =0; i< users.size(); i++) {
            sum += users.get(i).getFistEdition();
        }
        double output = sum/users.size();

        for(int i =0; i< users.size(); i++) {
            users.get(i).setInvestment(users.get(i).getFistEdition()-output);
            if (users.get(i).getInvestment() < 0){
                users.get(i).setPositiv(false);
                NegUsers.add(users.get(i));
            } else {
                users.get(i).setPositiv(true);
                PosUsers.add(users.get(i));
            }
        }




        callUserInvestment(PosUsers.get(0),NegUsers.get(0));
        ArrayList <UserAdapter>  alleUser = new ArrayList<UserAdapter>();
        for (int i = 0; i< PosUsers.size(); i++) {
            alleUser.add(PosUsers.get(i));
        }
        for (int i = 0; i< NegUsers.size(); i++) {
            alleUser.add(NegUsers.get(i));
        }

        return alleUser;

    }


    public void callUserInvestment(UserAdapter pos,UserAdapter neg){

        int result = calc(pos, neg);

        UserAdapter posUser = null;
        UserAdapter negUser = null;
        boolean weiterPos = false;
        boolean weiterNeg = false;

        switch (result){
            case 1: result = 1;

                for(int i =0; i< NegUsers.size(); i++){
                    if(NegUsers.get(i).getName().equals(neg.getName())) {
                        if (i + 1 < NegUsers.size()) {
                            negUser = NegUsers.get(i + 1);
                            callUserInvestment(pos, negUser);
                        }
                    }

                }

                break;

            case 2: result = 2;
                for(int i =0; i< PosUsers.size(); i++){
                    if(PosUsers.get(i).getName().equals(pos.getName())){
                        if (i + 1 < PosUsers.size()) {
                            posUser = PosUsers.get(i + 1);
                            callUserInvestment(posUser, neg);
                        }
                    }

                }
                break;

            case 3: result = 3;

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
                    callUserInvestment(negUser,posUser);
                } else
                    return;

                break;

        }

    }




    public int calc(UserAdapter pos, UserAdapter neg) {
        double result;
        double postnegativInvestment=0;
        result = neg.getInvestment() + pos.getInvestment();

        if (result > 0){
            // weiter mit negativen Zahl.Positive Zahl bleibt
            System.out.println("if > 0 " + pos.getName()+ pos.getInvestment() + neg.getName() + neg.getInvestment());
            System.out.println("result 1 :" + result);



            for( int i = 0; i < NegUsers.size(); i++) {
                if (NegUsers.get(i).getName().equals(neg.getName())){
                    NegUsers.get(i).addInvestmentToUser(pos.getName(),neg.getInvestment() *(-1) );
                    postnegativInvestment= neg.getInvestment() *(-1);
                    NegUsers.get(i).setInvestment(0);

                }
            }
            for( int i =0; i < PosUsers.size(); i++) {
                if (PosUsers.get(i).getName().equals(pos.getName())){
                    PosUsers.get(i).addHabenToUser(neg.getName(),postnegativInvestment);
                    PosUsers.get(i).setInvestment(result);
                }
            }
            return 1;


        } if (result < 0) {
            // weiter mit Positiven Zahl negative Zahl bleibt.

            System.out.println("if < 0 " + pos.getName()+ pos.getInvestment() + neg.getName() + neg.getInvestment());
            System.out.println("result2: " + result);

            for( int i =0; i < NegUsers.size(); i++) {
                if (NegUsers.get(i).equals(neg)){
                    NegUsers.get(i).addInvestmentToUser(pos.getName(), pos.getInvestment());

                    NegUsers.get(i).setInvestment(result);

                }
            }
            for( int i = 0; i < PosUsers.size(); i++) {
                if (PosUsers.get(i).equals(pos)){
                    PosUsers.get(i).addHabenToUser(neg.getName(),pos.getInvestment());
                    PosUsers.get(i).setInvestment(0);

                }
            }


            return 2;


        }if (result == 0){
            // weiter mit positiver und negativer Zahl.
            System.out.println("result3:" + result);
            System.out.println("if==0"+ pos.getName()+ pos.getInvestment() + neg.getName() + neg.getInvestment());


            for( int i =0; i < NegUsers.size(); i++) {
                if (NegUsers.get(i).equals(neg)){
                    NegUsers.get(i).addInvestmentToUser(pos.getName(), neg.getInvestment()*(-1));
                    NegUsers.get(i).setInvestment(0);

                }
            }
            for( int i =0; i < PosUsers.size(); i++) {
                if (PosUsers.get(i).equals(pos)){
                    PosUsers.get(i).addHabenToUser(neg.getName(),neg.getInvestment() *(-1));
                    PosUsers.get(i).setInvestment(0);

                }
            }

            return 3;


        }	else
            return 0;
    }


}
