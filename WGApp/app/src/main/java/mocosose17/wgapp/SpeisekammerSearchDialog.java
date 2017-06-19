package mocosose17.wgapp;

import android.app.Dialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
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
 * Created by Sebastian on 19.06.2017.
 */

public class SpeisekammerSearchDialog extends DialogFragment {

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();

        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        builder.setView(inflater.inflate(R.layout.speisekammersearchdialog, null));
        String query = ((SearchView)((SpeisekammerActivityStart)getActivity()).findViewById(R.id.speisekammerSearch)).getQuery().toString();
        new SearchItems(getActivity()).execute(query);
        return builder.create();
    }

    @Override
    public void onStart() {
        super.onStart();
        Button del = (Button)this.getDialog().findViewById(R.id.speisekammerRemoveButton);
        del.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String s = ((TextView)getDialog().findViewById(R.id.speisekammerItem)).getText().toString();
                getDialog().dismiss();
            }
        });
    }

    class SearchItems extends AsyncTask<String, Void, Void> {
        private String response = "";
        private Context context;

        protected SearchItems(Context context) {
            this.context = context;
        }

        @Override
        protected Void doInBackground(String... params) {
            URL url;
            if(!params[0].equals("DELETE")){
                try {
                    url = new URL("http://mc-wgapp.mybluemix.net/pantry/"+params[0]);
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
                    Log.d("Fehler", "aha");
                    e.printStackTrace();
                }

                Log.d("RESPONSE", response);
            }else{

            }



            return null;
        }

        protected void onPostExecute(Void unused) {
            try {
                JSONObject dbitems = new JSONObject(response);
                ((TextView)getDialog().findViewById(R.id.speisekammerItem)).setText(dbitems.getString("articleName"));
                ((TextView)getDialog().findViewById(R.id.speisekammerAmount)).setText(dbitems.getString("articleName"));
                }catch (Exception e) {
            }


        }


    }

}
