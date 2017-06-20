package mocosose17.wgapp;

import android.os.AsyncTask;
import android.support.v4.app.DialogFragment;
import android.app.FragmentManager;
import android.content.Context;
import android.support.v4.util.DebugUtils;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
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

    public void reload(){
        new SpecifiedItems(this).execute();
    }

    public void delete(String item){
        new SpecifiedItems(this).execute(item);
        Toast t = Toast.makeText(getApplicationContext(), item+" wurde aus der Kammer entfernt.", Toast.LENGTH_SHORT);
        t.show();
    }

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

        TextView catHeader = (TextView)findViewById(R.id.speisekammerListTv);
        catHeader.setText(category);

        in = itemname.toArray(new String[itemname.size()]);
        am = amount.toArray(new String[amount.size()]);
        mam = mamount.toArray(new String[mamount.size()]);
        ty = type.toArray(new String[type.size()]);
        scla = new SpeisekammerCustomListAdapter(this, in, am, mam, ty);
        lv = (ListView)findViewById(R.id.speisekammerList);
        lv.setAdapter(scla);

        Button add = (Button)findViewById(R.id.speisekammerListAddButton);
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SpeisekammerAddDialog dialog = new SpeisekammerAddDialog();
                FragmentManager fm = getFragmentManager();
                dialog.show(fm, "Neuer Eintrag");
            }

        });


        new SpecifiedItems(SpeisekammerActivityList.this).execute();

    }


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
                    url = new URL("http://mc-wgapp.mybluemix.net/deleteArticleFromPantry/"+params[0]);
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


