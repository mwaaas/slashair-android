package com.mwaside.android.slashair;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.mwaside.android.slashair.MainActivity.END_POINT_URL;


/**
 * Created by mwas on 4/12/15.
 */
public class ServerEndpoint {

    String token_key, username, base_url = END_POINT_URL, Tag="SeverEndpoint";
    List<NameValuePair> header_params;

    public ServerEndpoint(Context context){
        header_params = new ArrayList<>();
        SharedPreferences settings;
        settings = PreferenceManager.
                getDefaultSharedPreferences(context.getApplicationContext());
        this.token_key = settings.getString(SlashAirPreferenceActivity.AUTH_TOKEN, "");
        this.username = settings.getString(SlashAirPreferenceActivity.KEY_USERNAME, "");
    }

    private HttpGet add_header(HttpGet httpGet){
        httpGet.addHeader("Authorization", "Token " + token_key);
        return httpGet;
    }
    public String [] get_recent_transaction()  {
        String recent_transaction_url = base_url + "api_v1/recent_transaction/?transactions__user__username="+username;
        HttpGet httpGet = new HttpGet(recent_transaction_url);

        // add headers
        httpGet = add_header(httpGet);
        HttpClient httpClient = new DefaultHttpClient();
        try {
            Log.i(Tag, "execute url: "+recent_transaction_url);
            HttpResponse response = httpClient.execute(httpGet);
            // get response content
            HttpEntity response_entity = response.getEntity();
            String response_content = EntityUtils.toString(response_entity);
            Log.i(Tag, "response: " + response_content);

            JSONObject jsonObject = new JSONObject(response_content);
            //JSONArray array = new JSONParser()
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // return mock data
        String [] forecast = {
                "Today          - sunny     - 88/63",
                "Tomorrow       - foggy      -70/40",
                "Weds           - cloudy     - 7263",
                "Thus           - Asteroid   - 75/65",
                "Friday         - Heavy rain  - 65/56",
                "Sat            - help         -65/51",
                "sun            -sunny          -80/68"
        };
        return forecast;
    }


}
