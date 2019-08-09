package se.ff.bsc;

import au.com.dius.pact.consumer.Pact;
import au.com.dius.pact.consumer.PactProviderRuleMk2;
import au.com.dius.pact.consumer.PactVerification;
import au.com.dius.pact.consumer.dsl.DslPart;
import au.com.dius.pact.consumer.dsl.PactDslJsonBody;
import au.com.dius.pact.consumer.dsl.PactDslWithProvider;
import au.com.dius.pact.model.RequestResponsePact;
import org.junit.Rule;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

public class UrlEncodedTests {
    @Rule
    public PactProviderRuleMk2 provider = new PactProviderRuleMk2("BusService", "localhost", 8112, this);

    @Pact(consumer = "UrlEncodedClient")
    public RequestResponsePact createPactForBusServiceProvider(PactDslWithProvider builder) {
        Map<String, String> headers = new HashMap();
        headers.put("Content-Type", "application/json");


        Map<String, String> requestHeader = new HashMap();
        requestHeader.put("Content-Type", "application/x-www-form-urlencoded");
        requestHeader.put("Accept", "application/json");


        DslPart etaResults = new PactDslJsonBody()
                .stringType("station","Hammersmith")
                .stringType("nr","613")
                .integerType("eta",4)
                .asBody();

        DslPart responseBody =  new PactDslJsonBody()
                .stringMatcher("access_token","[0-9a-zA-Z]*", "VF92vEWLjBbZavBkw2bVhde68AXu")
                .stringType("token_type","Bearer")
                .stringType("expires_in","3599")
                .asBody();


        return builder
                .given("There is a bus with number 613 arriving to Hammersmith bus station")
                .uponReceiving("A request for eta for bus number 613 to Hammersmith bus station")
                .path("/v2/auth/token")
                .headers(requestHeader)
                .method("POST")
                .query("client_id=cVyyUM3sAx9u1yN8g8zCw9oYdYi2XFQK&client_secret=pkFpx45QcAya27tY&grant_type=client_credentials&scope=RESETPASSWORD")
                .willRespondWith()
                .status(200)
                .headers(headers)
                .body(responseBody).toPact();

    }

    @Test
    @PactVerification()
    public void BusServiceShouldReturnEta() {
        System.setProperty("pact.rootDir", "../pacts");  // Change output dir for generated pact-files
        Integer eta = new WhenComesTheBus(provider.getPort()).checkEta();
        System.out.println("According to test eta="+eta);

    }
}
