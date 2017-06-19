package mocosose17.wgapp;

import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.os.AsyncTask;
import android.os.Debug;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
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

public class SpeisekammerActivityStart extends AppCompatActivity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_speisekammer_start);

        SearchView sv = (SearchView) findViewById(R.id.speisekammerSearch);
        sv.setIconifiedByDefault(false);

        GridView gridview = (GridView) findViewById(R.id.speisekammerGrid);
        TypedArray ta = getResources().obtainTypedArray(R.array.speisekammerImgIds);
        Integer[] imgid = new Integer[ta.length()];
        for(int i = 0; i < ta.length(); i++){
            imgid[i] = ta.getResourceId(i, -1);
        }
        gridview.setAdapter(new SpeisekammerImageAdapter(this, imgid));

        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {

                String category = null;
                Intent intent = new Intent(getApplicationContext(), SpeisekammerActivityList.class);
                switch(position){
                    case 0: category = "Getränke"; break;
                    case 1: category = "Backwaren"; break;
                    case 2: category = "Tiefkühl"; break;
                    case 3: category = "Kühlschrank"; break;
                    case 4: category = "Früchte"; break;
                    case 5: category = "Sonstiges"; break;
                    case 6: category = "Teigwaren"; break;
                    case 7: category = "Gemüse"; break;
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
}
