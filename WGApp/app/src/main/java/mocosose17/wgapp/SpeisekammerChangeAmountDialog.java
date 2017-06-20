package mocosose17.wgapp;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.SearchView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

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

public class SpeisekammerChangeAmountDialog extends DialogFragment {

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();

        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        builder.setView(inflater.inflate(R.layout.speisekammerchangeamount, null));

        return builder.create();
    }

    @Override
    public void onStart() {
        super.onStart();
        if(((SpeisekammerActivityList)getActivity()).scla.toIncrease){
            ((TextView)getDialog().findViewById(R.id.speisekammerCATitle)).setText("Bestand von " + ((SpeisekammerActivityList)getActivity()).scla.toChangeName + " erhöhen");
        }else{
            ((TextView)getDialog().findViewById(R.id.speisekammerCATitle)).setText("Bestand von " + ((SpeisekammerActivityList)getActivity()).scla.toChangeName + " veringern");
        }
        ((TextView)getDialog().findViewById(R.id.speisekammerCAAmount)).setHint(""+((SpeisekammerActivityList)getActivity()).scla.toChangeAmount);
        ((TextView)getDialog().findViewById(R.id.speisekammerCAType)).setText(((SpeisekammerActivityList)getActivity()).scla.toChangeType);

        SeekBar seeker = (SeekBar)getDialog().findViewById(R.id.speisekammerCASeek);
        int max = 100;
        if(((SpeisekammerActivityList)getActivity()).scla.toChangeAmount < 10){
            if(((SpeisekammerActivityList)getActivity()).scla.toIncrease){
                max = 10;
            }else{
                max = ((SpeisekammerActivityList)getActivity()).scla.toChangeAmount;
            }

        }else{
            max = ((SpeisekammerActivityList)getActivity()).scla.toChangeAmount;
        }
        seeker.setMax(max);

        seeker.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if(((SpeisekammerActivityList)getActivity()).scla.toIncrease){
                    ((TextView)getDialog().findViewById(R.id.speisekammerCAAmount)).setHint(""+(((SpeisekammerActivityList)getActivity()).scla.toChangeAmount + progress));
                }else{
                    ((TextView)getDialog().findViewById(R.id.speisekammerCAAmount)).setHint(""+(((SpeisekammerActivityList)getActivity()).scla.toChangeAmount - progress));
                }

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        Button done = (Button)getDialog().findViewById(R.id.speisekammerCADone);
        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((TextView)getDialog().findViewById(R.id.speisekammerCAAmount)).setText(((TextView)getDialog().findViewById(R.id.speisekammerCAAmount)).getHint());
                if(Integer.parseInt(((TextView)getDialog().findViewById(R.id.speisekammerCAAmount)).getText().toString()) > 0){
                    new ChangeAmount(getActivity()).execute(((SpeisekammerActivityList)getActivity()).scla.toChangeName,((TextView)getDialog().findViewById(R.id.speisekammerCAAmount)).getText().toString());
                    Toast t = Toast.makeText(getActivity(), "Menge von "+ ((SpeisekammerActivityList)getActivity()).scla.toChangeName+" wurde angepasst", Toast.LENGTH_SHORT);
                    t.show();
                    ((SpeisekammerActivityList)getActivity()).reload();
                    getDialog().dismiss();
                }
            }
        });

    }

    class ChangeAmount extends AsyncTask<String, Void, Void> {
        private String response = "";
        private Context context;

        protected ChangeAmount(Context context) {
            this.context = context;
        }

        @Override
        protected Void doInBackground(String... params) {
            URL url;
            try {
                url = new URL("http://mc-wgapp.mybluemix.net/changeQuantityInPantry");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();

                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/json");

                JSONObject item = new JSONObject();
                try {
                    item.put("articleName", params[0]);
                    item.put("quantity", params[1]);
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


            return null;
        }

        protected void onPostExecute(Void unused) {

        }


    }

}
