package ims.fhj.at.klettertracker;

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
import java.util.ArrayList;
import java.util.List;

public class RatingsActivity extends AppCompatActivity {

    private JSONObject user;
    private JSONObject route;
    private JSONArray ratings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ratings);

        List valueList = new ArrayList<String>();

        try {
            user = new JSONObject(this.getIntent().getStringExtra("user"));
            route = new JSONObject(this.getIntent().getStringExtra("route"));

            if (getRatings()) {
                System.out.println("Ratings loaded: " + ratings.length());
                //showToast(activities.length() + " Aktivitäten geladen");
            }

            JSONObject sectionObject = route.getJSONObject("section");

            if (route != null) {
                setTitle(route.getString("name"));
            } else {
                setTitle("Nicht gefunden");
            }

        } catch (JSONException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        for (int i=0; i < ratings.length(); i++)
        {
            try {
                JSONObject oneObject = ratings.getJSONObject(i);

                if(oneObject.get("climbingroute").toString().equalsIgnoreCase(route.getString("_id"))) {
                    valueList.add(oneObject.get("comment"));
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        if (valueList.isEmpty()) {
            valueList.add("Keine Bewertungen");
        }

        ListAdapter adapter = new ArrayAdapter<String>(getApplicationContext(), R.layout.simple_list_item_dark, valueList);

        ListView listView = (ListView) findViewById(R.id.listView);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {

            }
        });

    }

    public boolean getRatings() throws IOException {

        URL url = new URL("http://doktordos.dyndns.org:8080/ratings");
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
                        ratings = new JSONArray(reply);
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
