package mocosose17.wgapp;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity {
    private EditText inputUsername;
    private EditText inputPassword;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Remove title bar
        getSupportActionBar().hide();
        setContentView(R.layout.activity_main);

        inputUsername = (EditText) findViewById(R.id.LoginUsername);
        inputPassword = (EditText) findViewById(R.id.LoginPassword);
    }


    public void executeLogin(View view){
        String username = inputUsername.getText().toString();
        String password = inputPassword.getText().toString();

        new VerifyLogin(MainActivity.this).execute(username, password);


    }

    public void noAccount(View view){
        Intent i = new Intent(this, RegisterActivity.class);
        startActivity(i);
    }
}

class VerifyLogin extends AsyncTask<String, Void, Void> {
    public String response = "";
    private Context context;

    protected VerifyLogin(Context context) {
        this.context = context;
    }

    @Override
    protected Void doInBackground(String... params) {
        URL url;
        try {
            url = new URL("http://mc-wgapp.mybluemix.net/verifyUser");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");



            JSONObject credentials = new JSONObject();
            try {
                credentials.put("username", params[0]);
                credentials.put("password", params[1]);
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


//        try {
//            JSONArray json = new JSONArray(response);
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }


        return null;
    }

    protected void onPostExecute(Void unused){
        if(Boolean.valueOf(response)){
            Toast.makeText(context,"login success",Toast.LENGTH_LONG).show();
//            Intent i = new Intent(context, RegisterActivity.class);
//            context.startActivity(i);
        }else{
            Toast.makeText(context,"login failed",Toast.LENGTH_LONG).show();
        }
//        user1 = (TextView) findViewById(R.id.User1TextView);
//
//
//        try {
//            usernames = new JSONArray(response);
//            for (int i = 0; i <usernames.length(); i++) {
//                JSONObject usernameObject = usernames.getJSONObject(i);
//                System.out.println(usernameObject.getString("username"));
//                users.add(usernameObject.getString("username"));
//
//                user1.setText(usernameObject.getString("username"));
//            }
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }

    }


}