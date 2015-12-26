package ims.fhj.at.klettertracker;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.json.JSONException;
import org.json.JSONObject;

public class HomeActivity extends AppCompatActivity {

    private JSONObject user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        try {
            user = new JSONObject(this.getIntent().getStringExtra("user"));
            //showToast("Willkommen " + user.getString("displayName") + "!" );
        } catch (JSONException e) {
            e.printStackTrace();
        }

        final ImageButton qrCodeButton = (ImageButton) findViewById(R.id.qr_button);
        qrCodeButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                goToQRCodeReader(user);
            }
        });

        final ImageButton manuelButton = (ImageButton) findViewById(R.id.man_button);
        manuelButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                goToManualTracking(user);
            }
        });

        final ImageButton activityButton = (ImageButton) findViewById(R.id.act_button);
        activityButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                goToActivities(user);
            }
        });

        final ImageButton logoutButton = (ImageButton) findViewById(R.id.logout_button);
        logoutButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                logout();
            }
        });
    }

    public void goToQRCodeReader(JSONObject user) {
        IntentIntegrator scanIntegrator = new IntentIntegrator(this);
        scanIntegrator.initiateScan();
    }

    public void goToManualTracking(JSONObject user) {
        Intent intent = new Intent(this, ManualTrackingActivity.class);
        intent.putExtra("title", "Verfügbare Routen");

        if (user != null) {
            intent.putExtra("user", user.toString());
        }

        startActivity(intent);
    }

    public void goToActivities(JSONObject user) {
        Intent intent = new Intent(this, ActivitiesActivity.class);
        intent.putExtra("title", "Aktivitäten");

        if (user != null) {
            intent.putExtra("user", user.toString());
        }

        startActivity(intent);
    }

    public void logout() {
        Intent intent = new Intent(this, LoginActivity.class);

        // TODO: Logout /auth/signout über GET

        startActivity(intent);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        IntentResult scanningResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);

        if (scanningResult != null) {
            //we have a result
            String scanContent = scanningResult.getContents();
            String scanFormat = scanningResult.getFormatName();

            //formatTxt.setText("FORMAT: " + scanFormat);
            //showToast("Scan: " + scanContent);

            Intent nextIntent = new Intent(this, ManualTrackingActivity.class);
            nextIntent.putExtra("title", "Verfügbare Routen");

            if (user != null) {
                nextIntent.putExtra("user", user.toString());
            }

            if (scanContent != null) {
                nextIntent.putExtra("routeID", scanContent);
            }
            startActivity(nextIntent);
        } else {
            showToast("Scan nicht erfolgreich!");
        }
    }


    public void showToast(String message) {
        Toast toast = Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT);
        toast.show();
    }
}
