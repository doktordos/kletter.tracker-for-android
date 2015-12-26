package ims.fhj.at.klettertracker;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

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

            JSONObject sectionObject = route.getJSONObject("section");

            if (route != null) {
                setTitle(route.getString("name"));
                difficultyTextView.setText(route.getString("difficulty"));
                colorTextView.setText(route.getString("colorCode"));
                sectionTextView.setText(sectionObject.getString("code"));
            } else {
                setTitle("Nicht gefunden");
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        final Button trackButton = (Button) findViewById(R.id.track_button);
        trackButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //goToQRCodeReader(user);
                try {
                    if(postTracking(route.getString("_id"), user.getString("_id"), "manual")) {
                        showToast("Erfolgreich getracked!");
                    } else {
                        showToast("Tracking fehlgeschlagen!");
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
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

    private boolean postTracking(String routeID, String userID, String trackingMode) throws IOException {

        showToast("Lade ...");

        String url = "http://doktordos.dyndns.org:8080/activities";
        String charset = "UTF-8";
        String data = "{\"user\":\""+userID+"\",\"climbingroute\":\""+routeID+"\",\"trackingmode\":\""+trackingMode+"\",\"providersShared\":[\"google\"]}";

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
        }
        return false;
    }

    public void showToast(String message) {
        Toast toast = Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT);
        toast.show();
    }
}
