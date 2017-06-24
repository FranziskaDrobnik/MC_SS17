package mocosose17.wgapp;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.support.annotation.IdRes;
import android.support.v7.app.AlertDialog;
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


    private String users ="";
    private String investments ="";
    private TableLayout layout;
    private DecimalFormat format = new DecimalFormat("##00.00");
    private GlobalObjects globalObjs = GlobalObjects.getInstance();
    final int CHECK_BUTTON_ID = 982301;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_investment);
        // Holen der Usernamen.
        new ExecuteGetMethod().execute();
        Button insert;
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

            return null;
        }

        // Holen der Gesamtausgaben der User, Berechnen der Ausgaben, wer wem etwas schuldet.
        @Override
        protected void onPostExecute(Void unused){

            JSONArray usernames = new JSONArray();
            JSONArray investment = new JSONArray();
            ArrayList<UserAdapter> endUsers = new ArrayList();

            // Eingeben der User mit den ersten gesammelten Augaben und Namen
            layout = (TableLayout) findViewById(R.id.tableLayout);


            try {
                usernames = new JSONArray(users);
                investment = new JSONArray(investments);
                // Prüfen, ob es überhaupt Ausgaben gibt.
                // Alle Usernamen holen.
                if (investment.length() > 0) {
                    for (int i = 0; i < usernames.length(); i++) {
                        JSONObject usernameObject = usernames.getJSONObject(i);

                        //User in eine Liste schreiben mit dem Betrag 0, sodass alle User existieren
                        endUsers.add(new UserAdapter(usernameObject.getString("username"), 0.00));


                        //Die ausgegeben Summen der User i holen, UserAdapter als FirstEdition speichern
                        for (int j = 0; j < investment.length(); j++) {
                            JSONObject investmentObject = investment.getJSONObject(j);
                            if (investmentObject.getString("user").equals(usernameObject.getString("username"))) {
                                for (int index = 0; index < endUsers.size(); index++) {
                                    if (endUsers.get(index).getName().equals(investmentObject.getString("user"))) {
                                        Double amountDoubleFormat = Double.parseDouble(investmentObject.getString("sum(amount)"));
                                        String ergAsObj = format.format(amountDoubleFormat);
                                        endUsers.get(index).setFistEdition(Double.parseDouble(ergAsObj));
                                    }
                                }

                            }

                        }
                    }
                    if (endUsers.size() > 0) {
                        // Berechnen der Summen des Users
                        Calculation calculation = new Calculation();
                        ArrayList<UserAdapter> allUser = calculation.setzeWerte(endUsers);

                        for (int index = 0; index < allUser.size(); index++) {

                            if (allUser.get(index).getName().equals(globalObjs.getUsername())) {

                                // Schreiben des bekommens in das TextView
                                for (Map.Entry e : allUser.get(index).getUsersHaben().entrySet()) {
                                    TextView amount = new TextView(InvestmentActivity.this);
                                    TextView usernameHaben = new TextView(InvestmentActivity.this);
                                    String formatAmount = format.format(e.getValue());
                                    System.out.println(endUsers);
                                    System.out.println(allUser);

                                    if (Double.parseDouble(formatAmount) > 0) {
                                        usernameHaben.setText(e.getKey().toString());
                                        usernameHaben.setTextSize(20);
                                        amount.setText(formatAmount);


                                        TableRow tr = new TableRow(InvestmentActivity.this);
                                        amount.setTextColor(Color.GREEN);
                                        amount.setTextSize(20);
                                        usernameHaben.setMinHeight(50);
                                        tr.setBottom(200);
                                        tr.addView(usernameHaben);
                                        tr.addView(amount);
                                        layout.addView(tr);
                                    }


                                }

                                // Schreiben der Schulden in das textView
                                for (Map.Entry e : allUser.get(index).getUsersSchulden().entrySet()) {
                                    TextView amountSchulden = new TextView(InvestmentActivity.this);
                                    TextView usernameSchulden = new TextView(InvestmentActivity.this);
                                    String formatAmount = format.format(e.getValue());

                                    if (Double.parseDouble(formatAmount) > 0) {

                                        usernameSchulden.setText(e.getKey().toString());
                                        amountSchulden.setText(formatAmount);
                                        usernameSchulden.setTextSize(20);


                                        TableRow row = new TableRow(InvestmentActivity.this);
                                        amountSchulden.setTextColor(Color.RED);
                                        amountSchulden.setTextSize(20);

                                        row.setBottom(200);
                                        row.addView(usernameSchulden);
                                        row.addView(amountSchulden);
                                        layout.addView(row);


                                    }
                                }
                                createButtonResetInvestment();
                            }
                        }
                    } else {
                        Toast.makeText(InvestmentActivity.this, " No Investment", Toast.LENGTH_LONG).show();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
        // erstellen des Buttons Kassensturz mit Löschen funktion und Dialogfenster
        private void  createButtonResetInvestment(){


            TableRow table_row = new TableRow(InvestmentActivity.this);
            TableRow table_row1 = new TableRow(InvestmentActivity.this);
            Button resetInvestment = new Button(InvestmentActivity.this);
            table_row.addView(resetInvestment);
            View spacerColumn = new View(InvestmentActivity.this);
            // Neue Reihe für die Optik
            table_row1.addView(spacerColumn, new TableRow.LayoutParams(1, 100));


            final int id = CHECK_BUTTON_ID;
            resetInvestment.setId(id);
            resetInvestment.setText("reset Investment");
            layout.addView(table_row1);
            layout.addView(table_row);

            resetInvestment.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    // TODO Auto-generated method stub

                    if(v.getId() == id){

                        AlertDialog.Builder builder;
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            builder = new AlertDialog.Builder(InvestmentActivity.this, android.R.style.Theme_Material_Dialog_Alert);
                        } else {
                            builder = new AlertDialog.Builder(InvestmentActivity.this);
                        }
                        builder.setTitle("Delete investment")
                                .setMessage("Are you sure you want to delete investment?")
                                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        System.out.println("Delete");
                                        // Löschen der Investmenteinträge
                                        new ExecuteDeleteMethod().execute();
                                        startActivity(getIntent());
                                    }
                                })
                                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        // do nothing
                                    }
                                })
                                .setIcon(android.R.drawable.ic_dialog_alert)
                                .show();
                    }
                }
            });
        }

    }

    // Eintragen der Beträge und des Grundes in die Datenbank
    private class ExecutePostMethod extends AsyncTask<String,Void, Void> {

        @Override
        protected Void doInBackground(String... params) {
            URL url;
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
    /*
Kassensturz

Löschen aller Einträge von Investment
*/
    private class ExecuteDeleteMethod extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            URL url;


            try {
                url = new URL("http://mc-wgapp.mybluemix.net/clearInvestments");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();

                conn.setRequestMethod("DELETE");
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

            Log.d("RESPONSE", investments);


            //user1 = (TextView) findViewById(R.id.User1TextView);

            return null;
        }



    }



}






