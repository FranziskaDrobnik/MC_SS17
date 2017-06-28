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
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CheckedTextView;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

//import static com.vogella.android.devogellaandroidsqlitefirst.R.layout.item_comment;

public class ShoppinglistActivity extends AppCompatActivity {
    ArrayList<Item> items = new ArrayList<Item>();
    private Toolbar toolbar;
    private NavigationView navigationView;
    private DrawerLayout drawerLayout;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.shoppinglist);




        // Initializing Toolbar and setting it as the actionbar
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Shopping List");
        setSupportActionBar(toolbar);

        //Initializing NavigationView
        navigationView = (NavigationView) findViewById(R.id.navigation_view);

        //Setting Navigation View Item Selected Listener to handle the item click of the navigation menu
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {

            // This method will trigger on item Click of navigation menu
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {


                switch (item.getItemId()) {

                    case R.id.menu_logout: {
                        //do somthing
                        GlobalObjects go = GlobalObjects.getInstance();
                        go.setUsername(null);
                        Intent i = new Intent(ShoppinglistActivity.this, MainActivity.class);
                        startActivity(i);
                        break;
                    }
                    case R.id.menu_investment: {
                        //do somthing
                        Intent i = new Intent(ShoppinglistActivity.this, InvestmentActivity.class);
                        startActivity(i);
                        break;
                    }
                    case R.id.menu_pantry: {
                        //do somthing
                        Intent i = new Intent(ShoppinglistActivity.this, SpeisekammerActivityStart.class);
                        startActivity(i);
                        break;
                    }
                    case R.id.menu_shoppinglist: {
                        //do somthing
                        Intent i = new Intent(ShoppinglistActivity.this, ShoppinglistActivity.class);
                        startActivity(i);
                        break;
                    }
                }
                //close navigation drawer
                drawerLayout.closeDrawer(GravityCompat.START);
                return true;
            }
        });

        // Initializing Drawer Layout and ActionBarToggle
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer);
        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this,drawerLayout,toolbar,R.string.openDrawer, R.string.closeDrawer){

            @Override
            public void onDrawerClosed(View drawerView) {
                // Code here will be triggered once the drawer closes as we dont want anything to happen so we leave this blank
                super.onDrawerClosed(drawerView);
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                // Code here will be triggered once the drawer open as we dont want anything to happen so we leave this blank

                super.onDrawerOpened(drawerView);
            }
        };

        //Setting the actionbarToggle to drawer layout
        drawerLayout.setDrawerListener(actionBarDrawerToggle);

        //calling sync state is necessay or else your hamburger icon wont show up
        actionBarDrawerToggle.syncState();



        /*datasource = new ItemsDataSource(this);
        datasource.open();*/

        //ArrayList<Item> values = new ArrayList<Item>(datasource.getAllItems());

        // use the SimpleCursorAdapter to show the
        // elements in a ListView
        //ItemAdapter adapter = new ItemAdapter(this);
        new RegisterItems().execute();
        //setListAdapter(adapter);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);

        GlobalObjects go = GlobalObjects.getInstance();
        TextView txtUser = (TextView) findViewById(R.id.txtUsername);
        txtUser.setText(go.getUsername());
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//            return true;
//        }

        return super.onOptionsItemSelected(item);
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
                    Boolean itemChecked=false;
                    for(int i=0;i<items.size();i++) {
                        if (items.get(i).getBought()) {
                            itemChecked = true;
                            break;
                        }
                    }
                    if (d==0 && itemChecked) {
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
                            //new AddArticleToPantry(item).execute();
                            Log.d("addpantry", item.getName()+"\t"+item.getAmount()+"\t"+item.getUnitkind()+"\t"+item.getCategory());
                            new CheckAmount(this).execute("LOOKUP", item.getName(), item.getAmount(), item.getUnitkind(), item.getCategory());
                            boughtItems++;
                            sb.append(item.getName());
                            i--;//sonst werdenb items übersprungen
                            removeItem(item);
                        } else if (item.getNewlyCreated()) {
                            //prüft, ob item schon shoppinglist ist und wenn ja erhöht nur menge
                            new SpeisekammerShoppingDialog().accessShoppingItem("LOOKUP",item.getName(),item.getAmount(),item.getUnitkind(),item.getCategory());
                            new AddArticleToShoppinglist(item).execute();
                            item.setNewlyCreated(false);
                            item.etAmount.setEnabled(false);
                            item.etName.setEnabled(false);
                            item.spUnitKind.setEnabled(false);
                            item.spCategory.setEnabled(false);
                            finish();
                            startActivity(getIntent());

                        } else if (item.getBought()) {
                            Log.d("","add to pantry");
                            //prüft, ob item schon shoppinglist ist und wenn ja erhöht nur menge
                            new SpeisekammerShoppingDialog().accessShoppingItem("LOOKUP",item.getName(),item.getAmount(),item.getUnitkind(),item.getCategory());
                            //new AddArticleToPantry(item).execute();
                            new CheckAmount(this).execute("LOOKUP", item.getName(), item.getAmount().toString(), item.getUnitkind().toString(), item.getCategory().toString());
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
        final String[] categories = getResources().getStringArray(R.array.categories);
        String tmp= categories[0];
        categories[0]=categories[5];
        categories[5]=tmp;
        ArrayAdapter<String> categoryAdapter = new ArrayAdapter<String>(ShoppinglistActivity.this, android.R.layout.simple_spinner_dropdown_item, categories);
        item.spCategory.setAdapter(categoryAdapter);
        item.cbBought = new CheckBox(ShoppinglistActivity.this);//(CheckBox) convertView.findViewById(R.id.cbBought);

        //LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.MarginLayoutParams., LinearLayout.LayoutParams.WRAP_CONTENT);
        item.layout = new LinearLayout(ShoppinglistActivity.this);
        item.layout.setOrientation(LinearLayout.VERTICAL);
        item.layout.setBackground(ContextCompat.getDrawable(this, R.drawable.shoppinglistcustomborder));
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
            item.spCategory.setEnabled(false);
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
        item.spCategory = null;

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
    public void accessAddToPantry(Item item){
        new AddArticleToPantry(item).execute();
    }

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
                //url = new URL("http://mc-wgapp.mybluemix.net/deleteArticleFromShoppinglist/" + URLEncoder.encode(item.getName(), "utf-8"));
                url = new URL("http://mc-wgapp.mybluemix.net/deleteArticleFromShoppinglist");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();

                conn.setRequestMethod("DELETE");
                conn.setRequestProperty("Content-Type", "application/json");

                JSONObject credentials = new JSONObject();
                try {

                    credentials.put("articleName", item.getName());
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
            Log.d("test",item.getName()+"\t"+item.getAmount()+"\t"+item.getUnitkind()+"\t"+item.getCategory());
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

                //User
                GlobalObjects go = GlobalObjects.getInstance();

                JSONObject credentials = new JSONObject();
                try {
                    credentials.put("amount", price.replace('.',','));
                    credentials.put("reason", reason);
                    credentials.put("user", go.getUsername());
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
    class CheckAmount extends AsyncTask<String, Void, Void> {
        private String response = "";
        private Context context;
        private String[] param;

        protected CheckAmount(Context context) {
            this.context = context;
        }

        @Override
        protected Void doInBackground(String... params) {
            URL url;
            this.param=params;
            Log.d("param", ""+param.length);
            if(this.param[0].equals("LOOKUP")){
                try {
                    url = new URL("http://mc-wgapp.mybluemix.net/pantry");
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();

                    conn.setRequestMethod("GET");
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
            }else if (this.param[0].equals("true")){
                //wenn item existiert
                try {
                    url = new URL("http://mc-wgapp.mybluemix.net/changeQuantityInPantry");
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();

                    conn.setRequestMethod("POST");
                    conn.setRequestProperty("Content-Type", "application/json");

                    JSONObject item = new JSONObject();
                    //menge die ich ändere+amount aus db
                    Integer newAmount = Integer.parseInt(param[2]+param[3]);
                    try {
                        item.put("articleName", params[0]);
                        item.put("quantity", newAmount);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    String str = item.toString();
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

            }else if (this.param[0].equals("false")){

                Item item= new Item();
                item.setName(param[1]);
                item.setAmount(param[2]);
                item.setUnitkind(param[3]);
                item.setCategory(param[4]);
                accessAddToPantry(item);
            }


            return null;
        }

        protected void onPostExecute(Void unused) {
            if (param[0].equals("LOOKUP") && response.length() > 5) {
                try {
                    JSONArray objects = new JSONArray(response);
                    JSONObject o = objects.getJSONObject(0);
                    new CheckAmount(getApplicationContext()).execute("true", o.getString("articleName"), param[2],o.getString("quantity"));

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }else{
                new CheckAmount(getApplicationContext()).execute("false", param[1], param[2], param[3], param[4]);
            }
        }

    }

}
