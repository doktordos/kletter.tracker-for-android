package ims.fhj.at.klettertracker;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class LoginActivity extends AppCompatActivity {

    public String username, password;

    public Activity getActivity() {
        return this;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        final EditText usernameInput = (EditText) findViewById(R.id.user_name);
        final EditText passwordInput = (EditText) findViewById(R.id.user_password);

        final Button button = (Button) findViewById(R.id.login_button);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                if ( usernameInput.getText().toString().equals("") || passwordInput.getText().toString().equals("")) {
                    showDialog("Fehler", "Bitte Benutzernamen und Passwort eingeben!");
                } else {
                    username = usernameInput.getText().toString();
                    password = passwordInput.getText().toString();

                    if (isValidUser(username, password)) {
                        System.out.println("Valid User!");
                        goToHome();
                    } else {
                        showDialog("Fehler", "Falscher Benutzername oder Passwort!");
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



    public boolean isValidUser(String username, String password) {
        if (username.equals("root") && password.equals("root66")) {
            return true;

        } else {
            return false;
        }
    }

    public void showDialog(String title, String message) {
        AlertDialog dialog = createDialog(title, message);
        dialog.show();
    }

    public AlertDialog createDialog(String title, String message) {
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

    public void goToHome() {
        Intent intent = new Intent(this, HomeActivity.class);
        startActivity(intent);
    }
}
