package ims.fhj.at.klettertracker;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ActivitiesActivity extends AppCompatActivity {

    private JSONObject user;
    private JSONArray activities;
    private JSONArray climbingroutes;

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
            }

            if (getClimbingRoutes()) {
                System.out.println("Routes loaded: " + climbingroutes.length());
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
                JSONObject routeObject = oneObject.getJSONObject("climbingroute");

                String dateWithWrongFormat = oneObject.getString("tracked");

                SimpleDateFormat wf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
                SimpleDateFormat rf = new SimpleDateFormat("dd.MM.yyyy HH:mm");

                Date date = wf.parse(dateWithWrongFormat);

                //System.out.println(" Date " + rf.format(date).toString());

                valueList.add(rf.format(date).toString() + " / " + routeObject.getString("name"));

            } catch (JSONException e) {
                e.printStackTrace();
            } catch (ParseException e) {
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

                String routeID;

                try {
                    JSONObject activityObject = activities.getJSONObject((int) id);
                    JSONObject routeObject = activityObject.getJSONObject("climbingroute");

                    routeID = routeObject.getString("_id");

                    if (routeID != null && climbingroutes.toString().contains(routeID)) {

                        for (int i = 0; i < climbingroutes.length(); i++) {
                            JSONObject oneObject = climbingroutes.getJSONObject(i);
                            if (oneObject.getString("_id").equals(routeID)) {
                                JSONObject route = climbingroutes.getJSONObject(i);
                                goToDetails(route);
                            }
                        }
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void goToDetails(JSONObject route) {
        Intent intent = new Intent(this, DetailsActivity.class);

        if (user != null) {
            intent.putExtra("user", user.toString());
        }

        if (route != null) {
            intent.putExtra("route", route.toString());
        }

        startActivity(intent);
    }

    public boolean getActivities() throws IOException {

        URL url = new URL("http://doktordos.dyndns.org:8080/activities");
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

        try {
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

    public boolean getClimbingRoutes() throws IOException {

        URL url = new URL("http://doktordos.dyndns.org:8080/climbingroutes");
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

        try {
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
}