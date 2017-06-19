package mocosose17.wgapp;

import android.content.Context;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.annotation.IdRes;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


    public class InvestmentActivity extends AppCompatActivity {

        private JSONArray usernames = new JSONArray();
        private JSONArray investment = new JSONArray();
        private ArrayList<UserAdapter> endUsers = new ArrayList();
        private String users ="";
        private String investments ="";
        private TableLayout layout;
        private Button insert;
        private DecimalFormat format = new DecimalFormat("##00.00");
        private GlobalObjects globalObjs = GlobalObjects.getInstance();
        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_investment);
            // Holen der Usernamen.
            new ExecuteGetMethod().execute();
            insert = (Button) findViewById(R.id.ButtonInsert);

            // Eingeben eines neuen ausgegebenen Betrages
            insert.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v){


                    EditText reason = (EditText) findViewById(R.id.reason);
                    EditText amount = (EditText) findViewById(R.id.amount);
                    String amountText = amount.getText().toString();
                    String reasonText = reason.getText().toString();

                    if(TextUtils.isEmpty(reasonText)) {
                        reason.setError("missing Reason");
                        return;
                    }
                    if(TextUtils.isEmpty(amountText)) {
                        reason.setError("missing amount");
                        return;
                    }

                    reason.setText("");
                    amount.setText("");

                    new ExecutePostMethod().execute(reasonText,amountText);
                   // Kassenstand neu anzeigen (activity neu laden)
                    startActivity(getIntent());
                }
            });
        }



        @Override
        public View findViewById(@IdRes int id) {
            return super.findViewById(id);
        }

        /*
        Klasse  holt sich  die User und deren Ausgaben, und schreibt nach der Berechnung
        die Ausgaben in die Textviews
         */
        private class ExecuteGetMethod extends AsyncTask<Void, Void, Void> {

            @Override
            protected Void doInBackground(Void... params) {
                URL url;
                try {
                    url = new URL("http://mc-wgapp.mybluemix.net/users");
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();

                    conn.setRequestMethod("GET");
                    conn.setRequestProperty("Content-Type", "application/json");

                    if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
                        //Log.e(TAG, "14 - HTTP_OK");


                        String line;
                        BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                        while ((line = br.readLine()) != null) {
                            users += line;
                        }
                    } else {
                        //Log.e(TAG, "14 - False - HTTP_OK");
                        users = "";
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }

                Log.d("USERS", users);
                try {
                    url = new URL("http://mc-wgapp.mybluemix.net/investmentsForUsers");
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();

                    conn.setRequestMethod("GET");
                    conn.setRequestProperty("Content-Type", "application/json");

                    if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
                        //Log.e(TAG, "14 - HTTP_OK");


                        String line;
                        BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                        while ((line = br.readLine()) != null) {
                            investments += line;
                        }
                    } else {
                        //Log.e(TAG, "14 - False - HTTP_OK");
                        investments = "";
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }

                Log.d("INVESTMENT", investments);

                //user1 = (TextView) findViewById(R.id.User1TextView);

                return null;
            }

            // Holen der Gesamtausgaben der User, Berechnen der Ausgaben, wer wem etwas schuldet.
            @Override
            protected void onPostExecute(Void unused){



                // Eingeben der User mit den ersten gesammelten Augaben und Namen
                layout = (TableLayout) findViewById(R.id.tableLayout);


                try {
                    usernames = new JSONArray(users);
                    investment = new JSONArray(investments);
                    // Alle Usernamen holen
                    for (int i = 0; i <usernames.length(); i++) {
                        JSONObject usernameObject = usernames.getJSONObject(i);
                        System.out.println(usernameObject.getString("username"));

                        //User in eine Liste schreiben mit dem Betrag 0, sodass alle User existieren
                        endUsers.add(new UserAdapter(usernameObject.getString("username"), 0.00));



                        //Die ausgegeben Summen der User i holen, UserAdapter als FirstEdition speichern
                        for (int j =0; j < investment.length(); j++){
                            JSONObject investmentObject = investment.getJSONObject(j);
                            System.out.println(investmentObject.getString("sum(amount)"));
                            if (investmentObject.getString("user").equals(usernameObject.getString("username"))){
                                for(int index=0; index< endUsers.size(); index ++) {
                                    if (endUsers.get(index).getName().equals(investmentObject.getString("user"))) {
                                        Double test = Double.parseDouble(investmentObject.getString("sum(amount)"));
                                        String ergAsObj =format.format(test);
                                        System.out.println("format"+ format.format(test));
                                        System.out.println("Double"+Double.parseDouble(ergAsObj));

                                        endUsers.get(index).setFistEdition(Double.parseDouble(ergAsObj));
                                        System.out.println("test"+ endUsers.get(index).getFistEdition());


                                    }
                                }


                            }

                        }
                    }
                    // Berechnen der Summen des Users
                    Calculation calculation = new Calculation();
                    ArrayList <UserAdapter> allUser= calculation.setzeWerte(endUsers);
                    for (int index = 0; index < allUser.size(); index++) {

                        if(allUser.get(index).getName().equals(globalObjs.getUsername())) {
                            TableRow trName = new TableRow(InvestmentActivity.this);
                            // Schreiben des bekommens in das TextView
                            for(Map.Entry e : allUser.get(index).getUsersHaben().entrySet()) {
                                TextView amount = new TextView(InvestmentActivity.this);
                                TextView username = new TextView(InvestmentActivity.this);
                                String formatAmount = format.format(e.getValue());

                                if(Double.parseDouble(formatAmount) > 0) {
                                    username.setText(e.getKey().toString());
                                    username.setTextSize(20);
                                    amount.setText(formatAmount);


                                    TableRow tr = new TableRow(InvestmentActivity.this);
                                    CheckBox cb = new CheckBox(InvestmentActivity.this);
                                    amount.setTextColor(Color.GREEN);
                                    amount.setTextSize(20);
                                    username.setMinHeight(50);
                                    tr.addView(username);
                                    tr.addView(amount);
                                    tr.addView(cb);
                                    layout.addView(tr);
                                }

                            }
                            // Schreiben der Schulden in das textView
                            for(Map.Entry e : allUser.get(index).getUsersSchulden().entrySet()) {
                                TextView amountSchulden = new TextView(InvestmentActivity.this);
                                TextView usernameSchulden = new TextView(InvestmentActivity.this);
                                String formatAmount =format.format(e.getValue());

                                if(Double.parseDouble(formatAmount) > 0) {

                                    usernameSchulden.setText(e.getKey().toString());
                                    amountSchulden.setText(formatAmount);
                                    usernameSchulden.setTextSize(20);


                                    TableRow row = new TableRow(InvestmentActivity.this);
                                    CheckBox checkBox = new CheckBox(InvestmentActivity.this);
                                    amountSchulden.setTextColor(Color.RED);
                                    amountSchulden.setTextSize(20);

                                    row.addView(usernameSchulden);
                                    row.addView(amountSchulden);
                                    row.addView(checkBox);
                                    layout.addView(row);
                                }
                            }
                        }
                    }



                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

        }

        // Eintragen der Beträge und des Grundes in die Datenbank
        private class ExecutePostMethod extends AsyncTask<String,Void, Void> {

            @Override
            protected Void doInBackground(String... params) {
                URL url;
                System.out.println(params[0]);
                System.out.println(params[1]);
                JSONObject object = new JSONObject();
                try {
                    object.put("reason", params[0]);
                    object.put("amount", params[1]);
                    object.put("user", (globalObjs.getUsername()));
                } catch (JSONException e) {
                    e.printStackTrace();
                }


                try {
                    url = new URL("http://mc-wgapp.mybluemix.net/addInvestment");
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();

                    conn.setRequestMethod("PUT");
                    conn.setRequestProperty("Content-Type", "application/json");



                    String str = object.toString();
                    byte[] outputBytes = str.getBytes("UTF-8");
                    OutputStream os = conn.getOutputStream();
                    os.write(outputBytes);


                    if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
                        //Log.e(TAG, "14 - HTTP_OK");


                        String line;
                        BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                        while ((line = br.readLine()) != null) {
                            investments += line;
                        }
                    } else {
                        //Log.e(TAG, "14 - False - HTTP_OK");
                        investments = "";
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }

                Log.d("RESPONSE", investments);


                //user1 = (TextView) findViewById(R.id.User1TextView);

                return null;
            }

        }


    }






