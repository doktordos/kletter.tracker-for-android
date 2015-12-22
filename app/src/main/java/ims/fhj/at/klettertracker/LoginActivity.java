package ims.fhj.at.klettertracker;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;


public class LoginActivity extends AppCompatActivity {

    private String username, password;
    private JSONObject user;


    public Activity getActivity() {
        return this;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        final EditText usernameInput = (EditText) findViewById(R.id.user_name);
        final EditText passwordInput = (EditText) findViewById(R.id.user_password);

        final Button button = (Button) findViewById(R.id.login_button);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                if (usernameInput.getText().toString().equals("") || passwordInput.getText().toString().equals("")) {
                    showDialog("Fehler", "Bitte Benutzernamen und Passwort eingeben!");
                } else {
                    username = usernameInput.getText().toString();
                    password = passwordInput.getText().toString();

                    try {
                        if (postCredentials(username, password)) {
                            System.out.println("Login successfull -> User: " + user.getString("displayName"));
                            goToHome(user);
                        } else {
                            showDialog("Fehler", "Falscher Benutzername oder Passwort!");
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_login, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private boolean postCredentials(String username, String password) throws IOException {

        showToast("Lade ...");

        String url = "http://doktordos.dyndns.org:8080/auth/signin";
        String charset = "UTF-8";
        String data = "{\"username\": \"" + username + "\", \"password\": \"" + password + "\"}";

        byte[] outputBytes = data.getBytes(charset);

        HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
        connection.setDoOutput(true); // Triggers POST.
        connection.setRequestProperty("Accept-Charset", charset);
        connection.setRequestProperty("Content-Type", "application/json; charset=" + charset);

        try (OutputStream output = connection.getOutputStream()) {
            output.write(outputBytes);
            output.close();
        }

        InputStream response = connection.getInputStream();

        if (connection.getResponseCode() == 200) {

            String reply;
            StringBuffer sb = new StringBuffer();
            try {
                int chr;
                while ((chr = response.read()) != -1) {
                    sb.append((char) chr);
                }
                reply = sb.toString();
            } finally {
                response.close();
                connection.disconnect();
            }

            if (reply.contains("_id")) {
                try {
                    user = new JSONObject(reply);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            return true;
        } else {
            System.err.println("ResponseCode: " + connection.getResponseCode());
        }

        return false;
    }

    private void showDialog(String title, String message) {
        AlertDialog dialog = createDialog(title, message);
        dialog.show();
    }

    private AlertDialog createDialog(String title, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        builder.setMessage(message)
                .setTitle(title);

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked OK button
            }
        });
        AlertDialog dialog = builder.create();

        return dialog;
    }

    private void goToHome(JSONObject user) {
        Intent intent = new Intent(this, HomeActivity.class);

        if (user != null) {
            intent.putExtra("user", user.toString());
        }

        startActivity(intent);
    }

    public void showToast(String message) {
        Toast toast = Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT);
        toast.show();
    }
}
