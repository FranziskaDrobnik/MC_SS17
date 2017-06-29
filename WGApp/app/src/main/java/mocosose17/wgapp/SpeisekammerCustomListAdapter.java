package mocosose17.wgapp;

import android.app.Activity;
import android.app.FragmentManager;
import android.content.Context;
import android.graphics.Color;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
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
 * Custom adapter class for the list view in the pantry
 * @author Sebastian Stumm
 * @version 1.0
 */
public class SpeisekammerCustomListAdapter extends ArrayAdapter<String> {

    private final Activity context;
    private final String[] itemname;
    private final String[] amount;
    private final String[] mamount;
    private final String[] type;

    public int toChangeAmount = 0;
    public String toChangeName = "";
    public String toChangeType = "";
    public boolean toIncrease = true;

    public SpeisekammerCustomListAdapter(Activity context, String[] itemname, String[] amount, String[] mamount, String[] type){

        super(context, R.layout.speisekammerlistelement, itemname);

        this.context = context;
        this.itemname = itemname;
        this.amount = amount;
        this.mamount = mamount;
        this.type = type;
    }

    public View getView(int position, View view, ViewGroup parent) {

        final LayoutInflater inflater = context.getLayoutInflater();
        View rowView = inflater.inflate(R.layout.speisekammerlistelement, parent,false);

        TextView itemView = (TextView) rowView.findViewById(R.id.speisekammerItem);
        TextView amountView = (TextView) rowView.findViewById(R.id.speisekammerAmount);

        itemView.setText(itemname[position]);
        amountView.setText(amount[position]+" "+type[position]);

        if(((SpeisekammerActivityList)context).highlight != null){
            if(itemView.getText().toString().equals(((SpeisekammerActivityList)context).highlight)){
                rowView.setBackgroundColor(Color.parseColor("#fffeaa0c"));
            }
        }

        // Check for minQuantity
        if(Integer.parseInt(amount[position]) < Integer.parseInt(mamount[position])){
            amountView.setTextColor(Color.RED);
            new ShoppingAdd(context).execute(itemname[position]);
        }

        // Remove button interaction
        Button remove = (Button)rowView.findViewById(R.id.speisekammerRemoveButton);
        remove.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                View parentRow = (View) v.getParent();
                ListView listView = (ListView) parentRow.getParent();
                final int position = listView.getPositionForView(parentRow);
                String s = listView.getItemAtPosition(position).toString();
                ((SpeisekammerActivityList)context).delete(s);
            }
        });

        // Increase Button interaction
        Button increase = (Button)rowView.findViewById(R.id.speisekammerAddAmount);
        increase.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                View parentRow = (View) v.getParent();
                ListView listView = (ListView) parentRow.getParent();
                final int position = listView.getPositionForView(parentRow);

                toChangeName = listView.getItemAtPosition(position).toString();
                String temps = ((TextView)listView.getChildAt(position).findViewById(R.id.speisekammerAmount)).getText().toString();
                String[] split = temps.split(" ");
                toChangeAmount = Integer.parseInt(split[0]);
                toChangeType = split[1];
                toIncrease = true;

                SpeisekammerChangeAmountDialog dialog = new SpeisekammerChangeAmountDialog();
                FragmentManager fm =  context.getFragmentManager();
                dialog.show(fm, "Neuer Wert");
            }
        });

        // Decrease Button interaction
        Button decrease = (Button)rowView.findViewById(R.id.speisekammerReduceAmount);
        decrease.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                View parentRow = (View) v.getParent();
                ListView listView = (ListView) parentRow.getParent();
                final int position = listView.getPositionForView(parentRow);

                toChangeName = listView.getItemAtPosition(position).toString();
                String temps = ((TextView)listView.getChildAt(position).findViewById(R.id.speisekammerAmount)).getText().toString();
                String[] split = temps.split(" ");
                toChangeAmount = Integer.parseInt(split[0]);
                toChangeType = split[1];
                toIncrease = false;

                SpeisekammerChangeAmountDialog dialog = new SpeisekammerChangeAmountDialog();
                FragmentManager fm =  context.getFragmentManager();
                dialog.show(fm, "Neuer Wert");
            }
        });

        // Shopping cart Button interaction
        Button shopping = (Button)rowView.findViewById(R.id.speisekammerShoppingCart);
        shopping.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                View parentRow = (View) v.getParent();
                ListView listView = (ListView) parentRow.getParent();
                final int position = listView.getPositionForView(parentRow);

                toChangeName = listView.getItemAtPosition(position).toString();
                String temps = ((TextView)listView.getChildAt(position).findViewById(R.id.speisekammerAmount)).getText().toString();
                String[] split = temps.split(" ");
                toChangeAmount = Integer.parseInt(split[0]);
                toChangeType = split[1];

                SpeisekammerShoppingDialog dialog = new SpeisekammerShoppingDialog();
                FragmentManager fm = context.getFragmentManager();
                dialog.show(fm, "Einkaufsliste-Eintrag");
            }
        });

        return rowView;

    }

    class ShoppingAdd extends AsyncTask<String, Void, Void> {
        private String response = "";
        private Context context;
        private String[] params;

        protected ShoppingAdd(Context context) {
            this.context = context;
        }

        @Override
        protected Void doInBackground(String... params) {
            this.params = params;
            URL url;

            if(params.length == 1){
                try {
                    url = new URL("http://mc-wgapp.mybluemix.net/shoppinglist/"+params[0]);
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

                Log.d("RESPONSE", response);
            }else  if(params.length == 2 && params[1].equals("pantry") ){
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
                    e.printStackTrace();
                }

                Log.d("RESPONSE", response);
            }else{
                try {
                    url = new URL("http://mc-wgapp.mybluemix.net/addArticleToShoppinglist");
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();

                    conn.setRequestMethod("PUT");
                    conn.setRequestProperty("Content-Type", "application/json");

                    JSONObject item = new JSONObject();
                    try {
                        item.put("articleName", params[0]);
                        item.put("quantity", params[1]);
                        item.put("type", params[2]);
                        item.put("category", params[3]);
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
            }



            return null;
        }

        protected void onPostExecute(Void unused) {
            if(params.length == 1){
                if(response.length() < 5){
                    new ShoppingAdd(getContext()).execute(params[0], "pantry");
                }
            }else if(params.length == 2 && params[1].equals("pantry")){
                if(response.length() > 0){
                    Log.d("RESPONSE INSIDE", response);
                    try{
                        JSONArray objia = new JSONArray(response);
                        JSONObject obji = objia.getJSONObject(0);
                        Integer currentAmount = Integer.parseInt(obji.getString("quantity"));
                        Integer desiredAmount = Integer.parseInt(obji.getString("minQuantity"));
                        Integer dif = desiredAmount - currentAmount;
                        String type = obji.getString("type");
                        String cate = obji.getString("category");
                        new ShoppingAdd(getContext()).execute(params[0], dif.toString(), type, cate);
                    }catch (Exception e){
                        Log.d("Error adding to SL", e.toString());
                    }
                }
            }else{
                Toast t = Toast.makeText(getContext(), params[0]+" has been added to the shoppinglist", Toast.LENGTH_SHORT);
                t.show();
            }


        }


    }

}
