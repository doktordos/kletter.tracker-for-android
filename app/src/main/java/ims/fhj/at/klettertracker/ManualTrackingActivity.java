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
import java.util.ArrayList;
import java.util.List;

public class ManualTrackingActivity extends AppCompatActivity {

    private JSONObject user;
    private JSONArray climbingroutes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manual_tracking);

        String title = (String)getIntent().getExtras().get("title");
        setTitle(title);

        List valueList = new ArrayList<String>();

        try {
            user = new JSONObject(this.getIntent().getStringExtra("user"));

            if (getClimbingRoutes()) {
                System.out.println("Routes loaded: " + climbingroutes.length());
                showToast(climbingroutes.length() + " Routen geladen");
            }

        } catch (JSONException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        for (int i=0; i < climbingroutes.length(); i++)
        {
            try {
                JSONObject oneObject = climbingroutes.getJSONObject(i);

                valueList.add(oneObject.getString("name"));

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

                try {
                    JSONObject route = climbingroutes.getJSONObject((int)id);

                    goToDetails(route);
                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }
        });
    }

    public void goToDetails(JSONObject route) {
        Intent intent = new Intent(this, DetailsActivity.class);

        if (route != null) {
            intent.putExtra("route", route.toString());
        }

        if (user != null) {
            intent.putExtra("user", user.toString());
        }

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

    public boolean getClimbingRoutes() throws IOException {

        URL url = new URL("http://doktordos.dyndns.org:8080/climbingroutes");
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
                        climbingroutes = new JSONArray(reply);
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

