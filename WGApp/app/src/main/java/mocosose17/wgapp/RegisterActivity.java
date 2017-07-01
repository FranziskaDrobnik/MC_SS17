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

/**
 * Activity to create an account
 * @author Tobias Lampprecht
 * @version 1.0
 */
public class RegisterActivity extends AppCompatActivity {
    private EditText inputUsername;
    private EditText inputPassword;
    private EditText inputPasswordRepeat;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);


        inputUsername = (EditText) findViewById(R.id.RegisterUsername);
        inputPassword = (EditText) findViewById(R.id.RegisterPassword);
        inputPasswordRepeat = (EditText) findViewById(R.id.RegisterPasswordRepeat);
    }

    /**
     * onClick listener for signup-button
     * executing AsyncTask to get Data from the API
     * @param view
     */
    public void signUp(View view){
        String username = inputUsername.getText().toString();
        String password = inputPassword.getText().toString();
        String passwordRepeat = inputPasswordRepeat.getText().toString();

        if(password.equals(passwordRepeat)) {
            new RegisterUser(RegisterActivity.this).execute(username, password);
        }else{
            Toast.makeText(this, "Passwords are not equal", Toast.LENGTH_LONG).show();
        }
    }
}

/**
 * Class to receive Data from the API
 */
class RegisterUser extends AsyncTask<String, Void, Void> {
    private String response = "";
    private Context context;

    protected RegisterUser(Context context) {
        this.context = context;
    }
    private String username;

    /**
     * Background Task inserts the new user in database via API
     * @param params username, password
     * @return null
     */
    @Override
    protected Void doInBackground(String... params) {
        URL url;
        try {
            url = new URL("http://mc-wgapp.mybluemix.net/addUser");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            conn.setRequestMethod("PUT");
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
     * if response(false) showing Toast signup failed
     * @param unused
     */
    protected void onPostExecute(Void unused) {
        if (Boolean.valueOf(response)) {
            GlobalObjects globalObjs = GlobalObjects.getInstance();
            globalObjs.setUsername(username);

            Intent i = new Intent(context, ShoppinglistActivity.class);
            context.startActivity(i);
        } else {
            Toast.makeText(context, "SignUp failed", Toast.LENGTH_LONG).show();
        }

    }


}