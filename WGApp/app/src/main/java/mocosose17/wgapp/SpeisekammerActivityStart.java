package mocosose17.wgapp;

import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.os.AsyncTask;
import android.os.Debug;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.SearchView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Arrays;

/**
 * Landing Activity for the pantry
 * @author Sebastian Stumm
 * @version 1.0
 */
public class SpeisekammerActivityStart extends AppCompatActivity{

    private Toolbar toolbar;
    private NavigationView navigationView;
    private DrawerLayout drawerLayout;

    /**
     * Called when the Activity is created.
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_speisekammer_start);


        // Initializing Toolbar and setting it as the actionbar
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Pantry");
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
                        Intent i = new Intent(SpeisekammerActivityStart.this, MainActivity.class);
                        startActivity(i);
                        break;
                    }
                    case R.id.menu_investment: {
                        //do somthing
                        Intent i = new Intent(SpeisekammerActivityStart.this, InvestmentActivity.class);
                        startActivity(i);
                        break;
                    }
                    case R.id.menu_pantry: {
                        //do somthing
                        Intent i = new Intent(SpeisekammerActivityStart.this, SpeisekammerActivityStart.class);
                        startActivity(i);
                        break;
                    }
                    case R.id.menu_shoppinglist: {
                        //do somthing
                        Intent i = new Intent(SpeisekammerActivityStart.this, ShoppinglistActivity.class);
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


        // initialize search view and make so the hint shows
        SearchView sv = (SearchView) findViewById(R.id.speisekammerSearch);
        sv.setIconifiedByDefault(false);

        // Grid view of the categories with images from drawable. Setting Adapter.
        GridView gridview = (GridView) findViewById(R.id.speisekammerGrid);
        TypedArray ta = getResources().obtainTypedArray(R.array.speisekammerImgIds);
        Integer[] imgid = new Integer[ta.length()];
        for(int i = 0; i < ta.length(); i++){
            imgid[i] = ta.getResourceId(i, -1);
        }
        gridview.setAdapter(new SpeisekammerImageAdapter(this, imgid));

        // Listen for click on each element of the grid view and set category according to the icon clicked.
        // If the plus sign is clicked a new Dialog Fragment is created which allows to add a new item to the pantry.
        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {

                String category = null;
                Intent intent = new Intent(getApplicationContext(), SpeisekammerActivityList.class);
                String cate[] = getResources().getStringArray(R.array.categories);
                switch(position){
                    case 0: category = cate[0]; break;
                    case 1: category = cate[1]; break;
                    case 2: category = cate[2]; break;
                    case 3: category = cate[3]; break;
                    case 4: category = cate[4]; break;
                    case 5: category = cate[5]; break;
                    case 6: category = cate[6]; break;
                    case 7: category = cate[7]; break;
                    case 8: SpeisekammerAddDialog dialog = new SpeisekammerAddDialog();
                        FragmentManager fm = getFragmentManager();
                        dialog.show(fm, "Neuer Eintrag"); break;
                }

                if(category != null){
                    intent.putExtra("STRING_CATEGORY", category);
                    startActivity(intent);
                }

            }
        });

        // Set Listener to the search view and create a new Dialog Fragment if the user pressed search.
        SearchView search = (SearchView) findViewById(R.id.speisekammerSearch);
        sv.setOnQueryTextListener(new SearchView.OnQueryTextListener(){

            @Override
            public boolean onQueryTextSubmit(String query) {
                SpeisekammerSearchDialog dialog = new SpeisekammerSearchDialog();
                FragmentManager fm = getFragmentManager();
                dialog.show(fm, "Neuer Eintrag");
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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

}
