package mocosose17.wgapp;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
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
 * App entry point, Activity to Log-In
 * @author Tobias Lampprecht
 * @version 1.0
 */
public class MainActivity extends AppCompatActivity {
    private EditText inputUsername;
    private EditText inputPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        inputUsername = (EditText) findViewById(R.id.LoginUsername);
        inputPassword = (EditText) findViewById(R.id.LoginPassword);
    }

    /**
     * onClick listener for Login-button
     * executing AsyncTask to get Data from the API
     * @param view
     */
    public void executeLogin(View view){
        String username = inputUsername.getText().toString();
        String password = inputPassword.getText().toString();


        new VerifyLogin(MainActivity.this).execute(username, password);
    }

    /**
     * onClick listener for no-account text
     * starting RegisterActivity
     * @param view
     */
    public void noAccount(View view){
        Intent i = new Intent(this, RegisterActivity.class);
        startActivity(i);
    }

    /**
     * Class to receive Data from the API
     */
    class VerifyLogin extends AsyncTask<String, Void, Void> {
        public String response = "";
        private Context context;
        private String username;

        protected VerifyLogin(Context context) {
            this.context = context;
        }

        /**
         * Background Task is receiving true or false as response
         * if the login was successful
         * @param params username, password
         * @return null
         */
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
                this.username = params[0];

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


            return null;
        }

        /**
         * Executed when background-task has finished
         * if response(true) setting username as global object and start ShoppinglistActivity
         * if response(false) showing Toast login failed
         * @param unused
         */
        protected void onPostExecute(Void unused){
            if(Boolean.valueOf(response)){
                GlobalObjects globalObjs = GlobalObjects.getInstance();
                globalObjs.setUsername(username);

                Intent i = new Intent(context, ShoppinglistActivity.class);
                context.startActivity(i);
            }else{
                Toast.makeText(context,"login failed",Toast.LENGTH_LONG).show();
            }


        }


    }
}

