package ims.fhj.at.klettertracker;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageButton;

public class HomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        /*Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "BETA APP ;)", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);*/

        final ImageButton qrCodeButton = (ImageButton) findViewById(R.id.qr_button);
        qrCodeButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                goToQRCodeReader();
            }
        });

        final ImageButton manuelButton = (ImageButton) findViewById(R.id.man_button);
        manuelButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                goToAvailableTracks();
            }
        });

        final ImageButton activityButton = (ImageButton) findViewById(R.id.act_button);
        activityButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                goToActivities();
            }
        });

        final ImageButton logoutButton = (ImageButton) findViewById(R.id.logout_button);
        logoutButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                logout();
            }
        });
    }

    public void goToQRCodeReader() {
        Intent intent = new Intent(this, QRCodeActivity.class);
        startActivity(intent);
    }

    public void goToAvailableTracks() {
        // TODO: Go to ActivitiesActivity with all available tracks

        Intent intent = new Intent(this, ActivitiesActivity.class);
        intent.putExtra("title", "Verfügbare Routen");
        startActivity(intent);
    }

    public void goToActivities() {
        // TODO: Go to ActivitiesActivity with currently tracked tracks only

        Intent intent = new Intent(this, ActivitiesActivity.class);
        intent.putExtra("title", "Aktivitäten");
        startActivity(intent);
    }

    public void logout() {
        // TODO: Logout
    }
}