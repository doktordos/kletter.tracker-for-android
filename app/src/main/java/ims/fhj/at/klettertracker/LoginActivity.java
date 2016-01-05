package ims.fhj.at.klettertracker;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import cz.msebera.android.httpclient.Header;

public class LoginActivity extends AppCompatActivity {

    private String username, password;
    private JSONObject user;
    boolean testing = false;


    public Activity getActivity() {
        return this;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        setTitle(getString(R.string.action_sign_in));

        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        final EditText usernameInput = (EditText) findViewById(R.id.user_name);
        final EditText passwordInput = (EditText) findViewById(R.id.user_password);

        if (testing == true) {
            usernameInput.setText("hoesed", TextView.BufferType.EDITABLE);
            passwordInput.setText("123456789", TextView.BufferType.EDITABLE);
        }

        if (this.getIntent().getStringExtra("username") != null && this.getIntent().getStringExtra("password") != null) {
            usernameInput.setText(this.getIntent().getStringExtra("username"), TextView.BufferType.EDITABLE);
            passwordInput.setText(this.getIntent().getStringExtra("password"), TextView.BufferType.EDITABLE);
        }

        final Button registerButton = (Button) findViewById(R.id.register_button);
        registerButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                goToRegister();
            }
        });

        final Button button = (Button) findViewById(R.id.login_button);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                if (usernameInput.getText().toString().equals("") || passwordInput.getText().toString().equals("")) {
                    showDialog(getString(R.string.error_title), getString(R.string.error_invalid_credentials));
                } else {
                    username = usernameInput.getText().toString();
                    password = passwordInput.getText().toString();

                    try {
                        postCredentials(username, password, new AsyncHttpResponseHandler() {
                            @Override
                            public void onSuccess(int i, Header[] headers, byte[] bytes) {
                                String reply = new String(bytes, StandardCharsets.UTF_8);

                                if (reply.contains("_id")) {
                                    try {
                                        user = new JSONObject(reply);
                                        System.out.println("Login successfull -> User: " + user.getString("displayName"));
                                        goToHome(user);
                                    } catch (JSONException e) {
                                        showDialog(getString(R.string.error_title), getString(R.string.error_incorrect_user_password));
                                        e.printStackTrace();
                                    }
                                }
                            }

                            @Override
                            public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
                                showDialog(getString(R.string.error_title), getString(R.string.error_incorrect_user_password));
                            }
                        });
                    } catch (Exception e) {
                        showDialog(getString(R.string.error_title), getString(R.string.error_incorrect_user_password));
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    public void postCredentials(String username, String password, AsyncHttpResponseHandler responseHandler) throws IOException {
        showToast(getString(R.string.loading));

        String url = "http://doktordos.dyndns.org:8080/auth/signin";

        RequestParams params = new RequestParams();
        params.add("username", username);
        params.add("password", password);

        AsyncHttpClient client = new AsyncHttpClient();
        client.post(url, params, responseHandler);
    }

    private void showDialog(String title, String message) {
        AlertDialog dialog = createDialog(title, message);
        dialog.show();
    }

    private AlertDialog createDialog(String title, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        builder.setMessage(message)
                .setTitle(title);

        builder.setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
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

    private void goToRegister() {
        Intent intent = new Intent(this, RegisterActivity.class);
        startActivity(intent);
    }

    public void showToast(String message) {
        Toast toast = Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT);
        toast.show();
    }
}
