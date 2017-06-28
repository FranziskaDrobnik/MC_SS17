package mocosose17.wgapp;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Debug;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
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

/**
 * Created by Sebastian on 20.06.2017.
 */

public class SpeisekammerShoppingDialog extends DialogFragment {

    private boolean sliderMoved = false;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();

        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        builder.setView(inflater.inflate(R.layout.speisekammershoppingdialog, null));

        return builder.create();
    }

    @Override
    public void onStart() {
        super.onStart();

        final String name = ((SpeisekammerActivityList)getActivity()).scla.toChangeName;
        final Integer amount = ((SpeisekammerActivityList)getActivity()).scla.toChangeAmount;
        final String type = ((SpeisekammerActivityList)getActivity()).scla.toChangeType;
        final String category = ((SpeisekammerActivityList)getActivity()).cat;

        ((TextView)getDialog().findViewById(R.id.speisekammerSDTitle)).setText(name + " add to shoppinglist");
        ((TextView)getDialog().findViewById(R.id.speisekammerSDAmount)).setHint("0");
        ((TextView)getDialog().findViewById(R.id.speisekammerSDType)).setText(type);

        SeekBar seeker = (SeekBar)getDialog().findViewById(R.id.speisekammerSDSeek);
        int max = 100;
        if(amount < 10){
            max = 10;
        }else{
            max = ((SpeisekammerActivityList)getActivity()).scla.toChangeAmount;
        }
        seeker.setMax(max);

        seeker.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                ((TextView)getDialog().findViewById(R.id.speisekammerSDAmount)).setHint(""+ progress);
                sliderMoved = true;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        Button done = (Button)getDialog().findViewById(R.id.speisekammerSDDone);
        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(sliderMoved){
                    ((TextView)getDialog().findViewById(R.id.speisekammerSDAmount)).setText(((TextView)getDialog().findViewById(R.id.speisekammerSDAmount)).getHint());
                }

                if(Integer.parseInt(((TextView)getDialog().findViewById(R.id.speisekammerSDAmount)).getText().toString()) > 0){
                    new ShoppingItem(getActivity()).execute("LOOKUP", name, ((TextView)getDialog().findViewById(R.id.speisekammerSDAmount)).getText().toString(), type, category);
                    Toast t = Toast.makeText(getActivity(), name+" has "+((TextView)getDialog().findViewById(R.id.speisekammerSDAmount)).getText().toString()+" times been added to the shoppinglist", Toast.LENGTH_SHORT);
                    t.show();
                }else{
                    Toast t = Toast.makeText(getActivity(),"Shoppinglist did not change", Toast.LENGTH_SHORT);
                    t.show();
                }

                getDialog().dismiss();
            }
        });

    }

    class ShoppingItem extends AsyncTask<String, Void, Void> {
        private String response = "";
        private Context context;
        private String[] params;

        protected ShoppingItem(Context context) {
            this.context = context;
        }

        @Override
        protected Void doInBackground(String... params) {
            this.params = params;
            URL url;
            for(int i = 0; i < this.params.length; i++){
                Log.d("Param",this.params[i]);
            }
            if(params.length > 0 && params[0].equals("LOOKUP")){
                try {
                    url = new URL("http://mc-wgapp.mybluemix.net/shoppinglist/"+params[1]);
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
            }

            if(params.length > 0 && params[0].equals("EXIST_FALSE")){
                try {
                    url = new URL("http://mc-wgapp.mybluemix.net/addArticleToShoppinglist");
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();

                    conn.setRequestMethod("PUT");
                    conn.setRequestProperty("Content-Type", "application/json");

                    JSONObject item = new JSONObject();
                    try {
                        item.put("articleName", params[1]);
                        item.put("quantity", params[2]);
                        item.put("type", params[3]);
                        item.put("category", params[4]);
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

            if(params.length > 0 && params[0].equals("EXIST_TRUE")){
                try {
                    url = new URL("http://mc-wgapp.mybluemix.net/changeQuantityInShoppinglist");
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();

                    conn.setRequestMethod("POST");
                    conn.setRequestProperty("Content-Type", "application/json");

                    Integer newQuantity = Integer.parseInt(params[4]) + Integer.parseInt(params[2]);

                    JSONObject item = new JSONObject();
                    try {
                        item.put("articleName", params[1]);
                        item.put("quantity", newQuantity);
                        item.put("type", params[3]);
                        item.put("category", params[4]);
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


            Log.d("RESPONSE", response);


            return null;
        }

        protected void onPostExecute(Void unused) {
            if(params.length > 0 && params[0].equals("LOOKUP")){
                if(response.length() > 5){
                    try{
                        JSONArray dbitems = new JSONArray(response);
                        JSONObject obji = dbitems.getJSONObject(0);
                        new ShoppingItem(getActivity()).execute("EXIST_TRUE", params[1], params[2], params[3], obji.getString("quantity"), obji.getString("category"));
                    }catch (Exception e){

                    }
                }else{
                    new ShoppingItem(getActivity()).execute("EXIST_FALSE", params[1], params[2], params[3], params[4]);
                }
            }
        }


    }

}
