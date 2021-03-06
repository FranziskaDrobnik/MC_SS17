package mocosose17.wgapp;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.design.widget.NavigationView;
import android.support.v4.app.DialogFragment;
import android.app.FragmentManager;
import android.content.Context;
import android.support.v4.util.DebugUtils;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Spinner;
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
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
/**
 * Class for the list for a category.
 * @author Sebastian Stumm
 * @version 1.0
 */
public class SpeisekammerActivityList extends AppCompatActivity {

    public ArrayList<String> itemname = new ArrayList<String>();
    public String[]in;
    public ArrayList<String> amount = new ArrayList<String>();
    public String[] am;
    public ArrayList<String> mamount = new ArrayList<String>();
    public String[] mam;
    public ArrayList<String> type = new ArrayList<String>();
    public String[] ty;
    public ListView lv;

    public String cat = "Default";
    public String highlight = null;

    public SpeisekammerCustomListAdapter scla;

    private Toolbar toolbar;
    private NavigationView navigationView;
    private DrawerLayout drawerLayout;

    /**
     * Can be called to reload the list
     */
    public void reload(){
        new SpecifiedItems(this).execute();
    }

    /**
     * Can be called to delete an item from the list
     * @param item item to delete
     */
    public void delete(String item){
        new SpecifiedItems(this).execute(item);
        Toast t = Toast.makeText(getApplicationContext(), item+" has been deleted from the pantry.", Toast.LENGTH_SHORT);
        t.show();
    }

    /**
     * On creation of the activity this method is called. It Checks the Intent for extras and sets the category to the value of the STRING_CATEGORY extra.
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_speisekammer_list);

        String category = "Default";

        Bundle extras = getIntent().getExtras();
        if(extras != null){
            category = extras.getString("STRING_CATEGORY");
            if(extras.size() > 1){
                highlight = extras.getString("SEARCHRESULT");
            }
        }

        cat = category;

        // Initializing Toolbar and setting it as the actionbar
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(cat);
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
                        GlobalObjects go = GlobalObjects.getInstance();
                        go.setUsername(null);
                        Intent i = new Intent(SpeisekammerActivityList.this, MainActivity.class);
                        startActivity(i);
                        break;
                    }
                    case R.id.menu_investment: {
                        Intent i = new Intent(SpeisekammerActivityList.this, InvestmentActivity.class);
                        startActivity(i);
                        break;
                    }
                    case R.id.menu_pantry: {
                        Intent i = new Intent(SpeisekammerActivityList.this, SpeisekammerActivityStart.class);
                        startActivity(i);
                        break;
                    }
                    case R.id.menu_shoppinglist: {
                        Intent i = new Intent(SpeisekammerActivityList.this, ShoppinglistActivity.class);
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





        // Set title to category name
        TextView catHeader = (TextView)findViewById(R.id.speisekammerListTv);
        catHeader.setText(category);

        // prepare Adapter
        in = itemname.toArray(new String[itemname.size()]);
        am = amount.toArray(new String[amount.size()]);
        mam = mamount.toArray(new String[mamount.size()]);
        ty = type.toArray(new String[type.size()]);
        scla = new SpeisekammerCustomListAdapter(this, in, am, mam, ty);
        lv = (ListView)findViewById(R.id.speisekammerList);
        lv.setAdapter(scla);

        // Button makes a new DialogFragment to add a new item
        Button add = (Button)findViewById(R.id.speisekammerListAddButton);
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SpeisekammerAddDialog dialog = new SpeisekammerAddDialog();
                FragmentManager fm = getFragmentManager();
                dialog.show(fm, "Neuer Eintrag");
            }

        });

        // get pantry items
        new SpecifiedItems(SpeisekammerActivityList.this).execute();

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


    /**
     * @author Sebastian Stummm
     * @version 1.0
     * Backend of the Activity working with the database.
     */
    class SpecifiedItems extends AsyncTask<String, Void, Void> {
        private String response = "";
        private Context context;
        private boolean bdelete = false;

        protected SpecifiedItems(Context context) {
            this.context = context;
        }

        @Override
        protected Void doInBackground(String... params) {
            URL url;
            if(params.length <= 0){
                bdelete = false;
                try {
                    // Get the every item in pantry
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
            }else{
                bdelete = true;
                try {
                    // Delete single item from pantry
                    url = new URL("http://mc-wgapp.mybluemix.net/deleteArticleFromPantry");
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();

                    conn.setRequestMethod("DELETE");
                    conn.setRequestProperty("Content-Type", "application/json");

                    JSONObject item = new JSONObject();
                    try {
                        item.put("articleName", params[0]);
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
            }



            return null;
        }

        protected void onPostExecute(Void unused) {

            try {
                if(bdelete == false){
                    JSONArray dbitems = new JSONArray(response);
                    itemname.clear();
                    amount.clear();
                    mamount.clear();
                    type.clear();
                    // fill the lists with database items
                    for(int i = 0; i < dbitems.length(); i++){
                        JSONObject obji = (JSONObject)dbitems.get(i);
                        if(obji.getString("category").equals(cat)){
                            itemname.add(obji.getString("articleName"));
                            amount.add(obji.getString("quantity"));
                            type.add(obji.getString("type"));
                            mamount.add(obji.getString("minQuantity"));
                        }
                    }
                }else{
                    reload();
                }

                // renew the adapter with the items from the database
                if(itemname.size() >= 0){
                    in = itemname.toArray(new String[itemname.size()]);
                    am = amount.toArray(new String[amount.size()]);
                    mam = mamount.toArray(new String[mamount.size()]);
                    ty = type.toArray(new String[type.size()]);
                    scla = new SpeisekammerCustomListAdapter(SpeisekammerActivityList.this, in, am, mam, ty);
                    lv.setAdapter(scla);
                }


            } catch (Throwable t) {
                Log.e("My App", "Could not parse malformed JSON: \"" + response + "\"");
            }



        }


    }

}


