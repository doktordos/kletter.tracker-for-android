package ims.fhj.at.klettertracker;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

public class DetailsActivity extends AppCompatActivity {

    private JSONObject user;
    private JSONObject route;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        final TextView difficultyTextView = (TextView) findViewById(R.id.textViewDifficulty);
        final TextView colorTextView = (TextView) findViewById(R.id.textViewColor);
        final TextView sectionTextView = (TextView) findViewById(R.id.textViewSection);

        try {
            user = new JSONObject(this.getIntent().getStringExtra("user"));
            route = new JSONObject(this.getIntent().getStringExtra("route"));

            if (route != null) {
                setTitle(route.getString("name"));
                difficultyTextView.setText(route.getString("difficulty"));
                colorTextView.setText(route.getString("colorCode"));
                sectionTextView.setText("TODO"); // TODO
            } else {
                setTitle("Fehler");
            }


        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_details, menu);
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

    public void showToast(String message) {
        Toast toast = Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT);
        toast.show();
    }
}
