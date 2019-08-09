package se.ff.bsc;

import org.apache.http.HttpResponse;
import org.apache.http.client.fluent.Request;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

public class DummyAPIClient {
    public String checkName(String id) {
        try {
            String url=String.format("http://localhost:8114/v1/employee/%s",id);
            System.out.println("using url: "+url);
            HttpResponse r = Request
                                .Get(url)
                                .addHeader("Content-Type", "application/json")
                                .execute()
                                .returnResponse();

            String json = EntityUtils.toString(r.getEntity());
            System.out.println("json="+json);
            JSONObject jsonObject = new JSONObject(json);
            String employee_name = jsonObject.get("employee_name").toString();
            return new String(employee_name);
        }
        catch (Exception e) {
            System.out.println("Unable to get employee name"+e);
            return null;
        }
    }

}
