package se.ff.bsc;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.fluent.Request;
import org.apache.http.entity.ContentType;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class WhenComesTheBus
{
    int port=8111;

    WhenComesTheBus() {
        // Will use default port.
        System.out.println("Default port "+port);
    }

    WhenComesTheBus(int port) {
        this.port=port;
        System.out.println("Custom port "+port);
    }

    public static void main( String[] args ) {
        Integer eta = new WhenComesTheBus().checkEta("Hammersmith","613");
        System.out.println("eta="+eta);
    }

    public Integer checkEta(String station, String nr) {
        try {
            String url=String.format("Http://localhost:%d/bus/%s/%s", port, station, nr);
            System.out.println("using url: "+url);
            HttpResponse r = Request.Get(url).execute().returnResponse();
            String json = EntityUtils.toString(r.getEntity());
            System.out.println("json="+json);
            JSONObject jsonObject = new JSONObject(json);
            String eta = jsonObject.get("eta").toString();
            return new Integer(eta);

        }
        catch (Exception e) {
            System.out.println("Unable to get eta, e="+e);
            return null;
        }
    }

    public Integer checkEta() {
        try {
            String url=String.format("Http://localhost:%d/v2/auth/token?client_id=cVyyUM3sAx9u1yN8g8zCw9oYdYi2XFQK&client_secret=pkFpx45QcAya27tY&grant_type=client_credentials&scope=RESETPASSWORD", port);
            System.out.println("using url: "+url);

            Request request = Request.Post(url)
                    .addHeader("Content-Type", "application/x-www-form-urlencoded")
                    .addHeader("Accept", "application/json");

            HttpResponse r = request
                    .execute().returnResponse();
            String json = EntityUtils.toString(r.getEntity());
            System.out.println("json="+json);
            JSONObject jsonObject = new JSONObject(json);
            String eta = jsonObject.get("eta").toString();
            return new Integer(eta);
        }
        catch (Exception e) {
            System.out.println("Unable to get eta, e="+e);
            return null;
        }
    }
}
