package ims.fhj.at.klettertracker;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ActivitiesActivity extends AppCompatActivity {

    private JSONObject user;
    private JSONArray activities;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_activities);

        String title = (String)getIntent().getExtras().get("title");
        setTitle(title);

        List valueList = new ArrayList<String>();

        try {
            user = new JSONObject(this.getIntent().getStringExtra("user"));

            if (getActivities()) {
                System.out.println("Activities loaded: " + activities.length());
                showToast(activities.length() + " Aktivitäten geladen");
            }


        } catch (JSONException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        for (int i=0; i < activities.length(); i++)
        {
            try {
                JSONObject oneObject = activities.getJSONObject(i);
                /*
                String dateWitWrongFormat = oneObject.getString("tracked");

                SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");

                Date date = sf.parse(dateWitWrongFormat);

                System.out.println(" Date " + date.toString());
                */

                valueList.add(oneObject.get("tracked"));

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        ListAdapter adapter = new ArrayAdapter<String>(getApplicationContext(), R.layout.simple_list_item_dark, valueList);

        ListView listView = (ListView) findViewById(R.id.listView);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                goToDetails();
            }
        });
    }

    public void goToDetails() {
        Intent intent = new Intent(this, DetailsActivity.class);
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_activities, menu);
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

    public boolean getActivities() throws IOException {

        URL url = new URL("http://doktordos.dyndns.org:8080/activities");
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

        try {
            //InputStream in = new BufferedInputStream(urlConnection.getInputStream());
            InputStream response = urlConnection.getInputStream();

            if (urlConnection.getResponseCode() == 200) {

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
                }

                if (reply.contains("_id")) {
                    try {
                        activities = new JSONArray(reply);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                return true;
            }
        }
        finally {
            urlConnection.disconnect();
        }
        return false;
    }

    public void showToast(String message) {
        Toast toast = Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT);
        toast.show();
    }
}
