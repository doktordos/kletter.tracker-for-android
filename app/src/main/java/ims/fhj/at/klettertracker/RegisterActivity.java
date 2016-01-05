package ims.fhj.at.klettertracker;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
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

public class RegisterActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        final EditText usernameEditText = (EditText) findViewById(R.id.userEditText);
        final EditText passwordEditText = (EditText) findViewById(R.id.passwordEditText);
        final EditText prenameEditText = (EditText) findViewById(R.id.prenameEditText);
        final EditText surnameEditText = (EditText) findViewById(R.id.surnameEditText);
        final EditText mailEditText = (EditText) findViewById(R.id.mailEditText);

        final Button registerButton = (Button) findViewById(R.id.register_button2);
        registerButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                try {
                    if (postRegistration(usernameEditText.getText().toString(), passwordEditText.getText().toString(), prenameEditText.getText().toString(), surnameEditText.getText().toString(), mailEditText.getText().toString())) {
                        showToast(getString(R.string.success_registration));
                        goToLogin(usernameEditText.getText().toString(), passwordEditText.getText().toString());
                    } else {
                        showToast(getString(R.string.error_registration));
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

    }

    private boolean postRegistration(String username, String password, String prename, String surname, String email) throws IOException {

        showToast(getString(R.string.loading));

        String url = "http://doktordos.dyndns.org:8080/auth/signup";
        String charset = "UTF-8";
        String data = "{\"username\":\""+username+"\",\"password\":\""+password+"\",\"firstName\":\""+prename+"\",\"lastName\":\""+surname+"\",\"email\":\""+email+"\"}";

        System.out.println(data);

        byte[] outputBytes = data.getBytes(charset);

        HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
        connection.setDoOutput(true); // Triggers POST.
        connection.setRequestProperty("Accept-Charset", charset);
        connection.setRequestProperty("Content-Type", "application/json; charset=" + charset);

        try (OutputStream output = connection.getOutputStream()) {
            output.write(outputBytes);
            output.close();
        }

        System.err.println("ResponseCode: " + connection.getResponseCode());

        if (connection.getResponseCode() == 200) {
            InputStream response = connection.getInputStream();

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

            System.out.println(reply.toString());

            return true;
        } else {
            System.err.println("ResponseCode: " + connection.getResponseCode());

            InputStream response = connection.getErrorStream();

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

            if (reply.contains("message")) {
                try {
                    JSONObject object = new JSONObject(reply);
                    showToast(object.getString("message"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            System.err.println(reply.toString());
        }
        return false;
    }

    public void goToLogin(String username, String password) {
        Intent intent = new Intent(this, LoginActivity.class);

        if (!username.equals("") && !password.equals("")) {
            intent.putExtra("username", username);
            intent.putExtra("password", password);
        }
        startActivity(intent);
    }

    public void showToast(String message) {
        Toast toast = Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT);
        toast.show();
    }
}
