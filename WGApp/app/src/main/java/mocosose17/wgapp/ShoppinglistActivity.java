package mocosose17.wgapp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CheckedTextView;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

//import static com.vogella.android.devogellaandroidsqlitefirst.R.layout.item_comment;

public class ShoppinglistActivity extends Activity {
    ArrayList<Item> items = new ArrayList<Item>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.shoppinglist);


        /*datasource = new ItemsDataSource(this);
        datasource.open();*/

        //ArrayList<Item> values = new ArrayList<Item>(datasource.getAllItems());

        // use the SimpleCursorAdapter to show the
        // elements in a ListView
        //ItemAdapter adapter = new ItemAdapter(this);
        new RegisterItems().execute();
        //setListAdapter(adapter);
    }

    // Will be called via the onClick attribute
    // of the buttons in main.xml
    public void onClick(View view) {
        @SuppressWarnings("unchecked")
        //ArrayAdapter<Item> adapter = (ArrayAdapter<Item>) getListAdapter();
                Item item = null;
        switch (view.getId()) {
            case R.id.add:
                item = new Item();
                item.setNewlyCreated(true);
                addItem(item);
                break;
            case R.id.delete:
                //if (getListAdapter().getCount() > 0) {
                // item = (Item) getListAdapter().getItem(0);
                // datasource.deleteComment(item);
                // adapter.remove(item);
                //  }
                Log.d("", items.toString());
                for (int i = 0; i < items.size(); i++) {
                    Log.d("","delete");
                    item=items.get(i);
                    if(item.getBought()){
                        //items.remove(item); steht schon in removeItem
                        removeItem(item);
                        i--;
                        if (!item.getNewlyCreated()) {
                            new DeleteArticleFromShoppinglist(item).execute();
                        }
                    }
                }

                break;
            case R.id.save:
                StringBuilder sb = new StringBuilder("");

                int boughtItems = 0;

                String text = "";
                if (((EditText) findViewById(R.id.etPrice)).getText() != null)
                    text = ((EditText) findViewById(R.id.etPrice)).getText().toString().toString().replace(',', '.');
                Boolean canParse = true;

                try {
                    double d = Double.parseDouble(text);
                    if (d==0) {
                        ((EditText) findViewById(R.id.etPrice)).setHint("please enter price");//it gives user to hint
                        ((EditText) findViewById(R.id.etPrice)).setError("please enter price");//it gives user to info message //use any one //
                        return;
                    }
                } catch (Exception e) {
                    canParse = false;
                }


                if (!canParse) {
                    Toast.makeText(this, "\"" + text + "\" ist kein gültiger Betrag.", Toast.LENGTH_LONG).show();
                } else {

                    for (int i = 0; i < items.size(); i++) {
                        item = items.get(i);
                        if (item.getNewlyCreated() && item.getBought()) {
                            new AddArticleToPantry(item).execute();
                            boughtItems++;
                            sb.append(item.getName());
                            i--;//sonst werdenb items übersprungen
                            removeItem(item);
                        } else if (item.getNewlyCreated()) {
                            new AddArticleToShoppinglist(item).execute();
                            item.setNewlyCreated(false);
                            item.etAmount.setEnabled(false);
                            item.etName.setEnabled(false);
                            item.spUnitKind.setEnabled(false);
                        } else if (item.getBought()) {
                            Log.d("","add to pantry");
                            new AddArticleToPantry(item).execute();
                            new DeleteArticleFromShoppinglist(item).execute();
                            boughtItems++;
                            sb.append(item.getName());
                            i--;//sonst werden items übersprungen
                            removeItem(item);

                        }
                    }
                    //Woher weiß die RestAPI wer (welcher User) die Anfrage macht??
                    if (boughtItems > 0) {
                        //  if(((EditText) findViewById(R.id.etPrice)).getText().toString()=="0.00"){
                        //   Toast.makeText(this, "Bitte geben Sie einen Betrag für Ihren Einkauf ein!",
                        //   Toast.LENGTH_LONG).show();
                        //}
                        new AddInvestment(((EditText) findViewById(R.id.etPrice)).getText().toString(), sb.toString()).execute();
                        ((EditText) findViewById(R.id.etPrice)).setText("0,00");
                    }
                }
        }
    }

    public void addItem(final Item item) {
        LinearLayout listView = (LinearLayout) findViewById(R.id.mainLayoutShoppinglist);
        items.add(item);
        item.etAmount = new EditText(ShoppinglistActivity.this);//(EditText) convertView.findViewById(R.id.etAmount);
        item.spUnitKind = new Spinner(ShoppinglistActivity.this, Spinner.MODE_DROPDOWN);
        final String[] unitkinds = new String[]{"Stk", "g", "kg", "ml", "l"};
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(ShoppinglistActivity.this, android.R.layout.simple_spinner_dropdown_item, unitkinds);
        item.spUnitKind.setAdapter(adapter);
        item.etName = new EditText(ShoppinglistActivity.this);//(EditText) convertView.findViewById(R.id.etName);
        //neu
        item.spCategory = new Spinner(ShoppinglistActivity.this, Spinner.MODE_DROPDOWN);
        final String[] categories = new String[]{"Sonstiges", "Getränke", "Backwaren", "Tiefkühl", "Kühlschrank", "Früchte", "Teigwaren", "Gemüse"};
        ArrayAdapter<String> categoryAdapter = new ArrayAdapter<String>(ShoppinglistActivity.this, android.R.layout.simple_spinner_dropdown_item, categories);
        item.spCategory.setAdapter(categoryAdapter);
        item.cbBought = new CheckBox(ShoppinglistActivity.this);//(CheckBox) convertView.findViewById(R.id.cbBought);

        //LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.MarginLayoutParams., LinearLayout.LayoutParams.WRAP_CONTENT);
        item.layout = new LinearLayout(ShoppinglistActivity.this);
        item.layout.setOrientation(LinearLayout.VERTICAL);
        LinearLayout i = new LinearLayout(ShoppinglistActivity.this);
        i.addView(item.cbBought);
        i.addView(item.etAmount);
        i.addView(item.spUnitKind);
        i.addView(item.spCategory);
        item.layout.addView(item.etName);
        item.layout.addView(i);


        listView.addView(item.layout);

        item.etName.setText(item.getName());
        item.cbBought.setChecked(item.getBought());
        item.etAmount.setText(item.getAmount());
        item.spUnitKind.setSelection(Arrays.asList(unitkinds).indexOf(item.getUnitkind()));
        item.spCategory.setSelection(Arrays.asList(categories).indexOf(item.getCategory()));

        item.cbBought.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                //item.setBought(isChecked);
                //buttonView=item.cbBought;
                if (buttonView.isChecked()) {
                    item.setBought(true);
                } else{
                    item.setBought(false);
                }
            }
        });
        item.spCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                item.setCategory(((CheckedTextView) selectedItemView).getText().toString());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }

        });

        if (!item.getNewlyCreated()) {
            item.spUnitKind.setEnabled(false);
            item.etName.setEnabled(false);
            item.etAmount.setEnabled(false);
        } else {
            item.etAmount.addTextChangedListener(new TextWatcher() {

                public void onTextChanged(CharSequence s, int start, int before,
                                          int count) {
                    item.setAmount(s.toString());
                }


                public void beforeTextChanged(CharSequence s, int start, int count,
                                              int after) {

                }

                public void afterTextChanged(Editable s) {

                }
            });
            item.etName.addTextChangedListener(new TextWatcher() {

                public void onTextChanged(CharSequence s, int start, int before,
                                          int count) {
                    item.setName(s.toString());
                }


                public void beforeTextChanged(CharSequence s, int start, int count,
                                              int after) {

                }

                public void afterTextChanged(Editable s) {

                }
            });
            item.spUnitKind.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                    item.setUnitkind(((CheckedTextView) selectedItemView).getText().toString());
                }

                @Override
                public void onNothingSelected(AdapterView<?> parentView) {
                    // your code here
                }

            });

        }
    }

    public void removeItem(Item item) {
        LinearLayout listView = (LinearLayout) findViewById(R.id.mainLayoutShoppinglist);

        listView.removeView(item.layout);
        item.layout = null;
        item.etAmount = null;
        item.etName = null;
        item.spUnitKind = null;

        items.remove(item);
    }

    /* @Override
     protected void onResume() {
         datasource.open();
         super.onResume();
     }

     @Override
     protected void onPause() {
         datasource.close();
         super.onPause();
     }
 */
    class RegisterItems extends AsyncTask<String, Void, Void> {
        private String response = "";

        protected RegisterItems() {

        }


        @Override
        protected Void doInBackground(String... params) {
            URL url;
            try {
                url = new URL("http://mc-wgapp.mybluemix.net/shoppinglist");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();

                conn.setRequestMethod("GET");
                conn.setRequestProperty("Content-Type", "application/json");



            /*JSONObject credentials = new JSONObject();
            try {
                credentials.put("username", params[0]);
                credentials.put("password", params[1]);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            String str = credentials.toString();
            byte[] outputBytes = str.getBytes("UTF-8");
            OutputStream os = conn.getOutputStream();
            os.write(outputBytes);*/

                if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    String line;
                    BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    while ((line = br.readLine()) != null) {
                        response += line;
                    }

                } else {
                    response = "";
                }

            } catch (IOException e) {
                e.printStackTrace();
            }

            Log.d("RESPONSE", response);


            return null;
        }

        //Anzeige UI
        protected void onPostExecute(Void unused) {
            JSONArray json = null;
            try {
                json = new JSONArray(response);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            try {
                for (int i = 0; i < json.length(); i++) {
                    JSONObject itemObject = json.getJSONObject(i);
                    final Item item = new Item(itemObject);
                    addItem(item);
                }
                //adapter.notifyDataSetChanged();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    class AddArticleToShoppinglist extends AsyncTask<String, Void, Void> {
        private String response = "";
        private Item item;

        protected AddArticleToShoppinglist(Item item) {
            this.item = item;
        }

        @Override
        protected Void doInBackground(String... params) {
            URL url;
            try {
                url = new URL("http://mc-wgapp.mybluemix.net/addArticleToShoppinglist");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();

                conn.setRequestMethod("PUT");
                conn.setRequestProperty("Content-Type", "application/json");


                JSONObject credentials = new JSONObject();
                try {
                    credentials.put("articleName", item.getName());
                    credentials.put("quantity", item.getAmount());
                    credentials.put("type", item.getUnitkind());
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                String str = credentials.toString();
                byte[] outputBytes = str.getBytes("UTF-8");
                OutputStream os = conn.getOutputStream();
                os.write(outputBytes);

                if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    String line;
                    BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    while ((line = br.readLine()) != null) {
                        response += line;
                    }
                } else {
                    response = "";
                }

            } catch (IOException e) {
                e.printStackTrace();
            }

            Log.d("RESPONSE", response);


            return null;
        }

        //Anzeige UI
        protected void onPostExecute(Void unused) {
            //Brauche ich die hier??
        }
    }

    class DeleteArticleFromShoppinglist extends AsyncTask<String, Void, Void> {
        private String response = "";
        private Context context;
        private Item item;

        protected DeleteArticleFromShoppinglist(Item item) {
            this.item = item;
        }

        @Override
        protected Void doInBackground(String... params) {
            URL url;
            try {
                url = new URL("http://mc-wgapp.mybluemix.net/deleteArticleFromShoppinglist/" + URLEncoder.encode(item.getName(), "utf-8"));
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();

                conn.setRequestMethod("DELETE");
                conn.setRequestProperty("Content-Type", "application/json");

                if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    String line;
                    BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    while ((line = br.readLine()) != null) {
                        response += line;
                    }
                } else {
                    response = "";
                }

            } catch (IOException e) {
                e.printStackTrace();
            }

            Log.d("RESPONSE", response);


            return null;
        }

        //Anzeige UI
        protected void onPostExecute(Void unused) {
            //???
        }
    }

    class AddArticleToPantry extends AsyncTask<String, Void, Void> {
        private String response = "";
        private Context context;
        private Item item;


        protected AddArticleToPantry(Item item) {
            this.item = item;
        }

        @Override
        protected Void doInBackground(String... params) {
            URL url;

            try {
                url = new URL("http://mc-wgapp.mybluemix.net/addArticleToPantry");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();

                conn.setRequestMethod("PUT");
                conn.setRequestProperty("Content-Type", "application/json");

                JSONObject credentials = new JSONObject();
                try {

                    credentials.put("articleName", item.getName());
                    credentials.put("quantity", item.getAmount());
                    credentials.put("type", item.getUnitkind());
                    credentials.put("category", item.getCategory());
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                String str = credentials.toString();
                byte[] outputBytes = str.getBytes("UTF-8");
                OutputStream os = conn.getOutputStream();
                os.write(outputBytes);

                if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    String line;
                    BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    while ((line = br.readLine()) != null) {
                        response += line;
                    }
                } else {
                    response = "";
                }

            } catch (IOException e) {
                e.printStackTrace();
            }

            Log.d("RESPONSE", response);


            return null;
        }

        //Anzeige UI
        protected void onPostExecute(Void unused) {
            //TODO ???
        }
    }

    class AddInvestment extends AsyncTask<String, Void, Void> {
        private String response = "";
        private Context context;
        private String price;
        private String reason;

        protected AddInvestment(String price, String reason) {
            this.price = price;
            this.reason = reason;
        }

        @Override
        protected Void doInBackground(String... params) {
            URL url;
            try {
                url = new URL("http://mc-wgapp.mybluemix.net/addInvestment");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();

                conn.setRequestMethod("PUT");
                conn.setRequestProperty("Content-Type", "application/json");

                // TODO bzw Liste an Artikeln???
                JSONObject credentials = new JSONObject();
                try {
                    credentials.put("amount", price.replace('.',','));
                    credentials.put("reason", reason);
                    credentials.put("user", "bla"); //TODO User einfügen
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                String str = credentials.toString();
                byte[] outputBytes = str.getBytes("UTF-8");
                OutputStream os = conn.getOutputStream();
                os.write(outputBytes);

                if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    String line;
                    BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    while ((line = br.readLine()) != null) {
                        response += line;
                    }
                } else {
                    response = "";
                }

            } catch (IOException e) {
                e.printStackTrace();
            }

            Log.d("RESPONSE", response);


            return null;
        }

        //Anzeige UI
        protected void onPostExecute(Void unused) {
            //TODO
        }
    }
}
