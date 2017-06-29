package mocosose17.wgapp;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Dialog for adding a new item
 * @author Sebastian Stumm
 * @version 1.0
 */

public class SpeisekammerAddDialog extends DialogFragment {

    public TextView bez;
    public TextView men;
    public Spinner typ;
    public TextView mindm;
    public Spinner cate;

    /**
     * Called when the Dialog is created
     * @param savedInstanceState
     * @return
     */
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();

        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        builder.setView(inflater.inflate(R.layout.speisekammeradddialog, null));
        return builder.create();
    }

    /**
     * Called when the Dialog has been created. Handles the listener and values of the elements in the dialog
     */
    @Override
    public void onStart(){
        super.onStart();

        Button addbtn = (Button)this.getDialog().findViewById(R.id.skAddButton);
        bez = (TextView)this.getDialog().findViewById(R.id.skBezeichnungTv);
        men = (TextView)this.getDialog().findViewById(R.id.skMengeTv);
        mindm = (TextView)this.getDialog().findViewById(R.id.skMindestmengeTv);
        cate = (Spinner) this.getDialog().findViewById(R.id.skSpinnerCatV);
        typ = (Spinner)this.getDialog().findViewById(R.id.skSpinnerTypV);

        try{
            String s = ((SpeisekammerActivityList)getActivity()).cat;
            int pos = 0;
            for(int i = 0; i < getResources().getStringArray(R.array.categories).length; i++){
                if(getResources().getStringArray(R.array.categories)[i].equals(s)){
                    pos = i;
                    break;
                }
            }

            cate.setSelection(pos);
        }catch (Exception e){
        }
        addbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String pa0 = bez.getText().toString();
                String pa1 = men.getText().toString();
                String pa2 = typ.getSelectedItem().toString();
                String pa3 = mindm.getText().toString();
                String pa4 = cate.getSelectedItem().toString();
                new AddItems(getActivity()).execute(pa0, pa1, pa2, pa3, pa4);
                try{
                    ((SpeisekammerActivityList)getActivity()).reload();
                }catch (Exception e){

                }
                Toast t = Toast.makeText(getActivity(), pa1 +"x "+pa0+" has been added to pantry.", Toast.LENGTH_SHORT);
                t.show();
                getDialog().dismiss();
            }
        });

    }

    /**
     * Backend class of the dialog which communicates with the database
     * @author Sebastian Stumm
     * @version 1.0
     */
    class AddItems extends AsyncTask<String, Void, Void> {
        private String response = "";
        private Context context;

        protected AddItems(Context context) {
            this.context = context;
        }

        @Override
        protected Void doInBackground(String... params) {
            URL url;
            try {
                // Add an article to the database
                url = new URL("http://mc-wgapp.mybluemix.net/addArticleToPantry");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();

                conn.setRequestMethod("PUT");
                conn.setRequestProperty("Content-Type", "application/json");

                JSONObject item = new JSONObject();
                try {
                    item.put("articleName", params[0]);
                    item.put("quantity", params[1]);
                    item.put("type", params[2]);
                    item.put("minQuantity", params[3]);
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
                Log.d("Fehler", "aha");
                e.printStackTrace();
            }

            Log.d("RESPONSE", response);


            return null;
        }

        protected void onPostExecute(Void unused) {

        }


    }

}
