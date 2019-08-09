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

import static org.junit.Assert.assertTrue;

public class Tests {
    @Rule
    /*public PactProviderRuleMk2 provider = new PactProviderRuleMk2("DummyService",this);*/
    public PactProviderRuleMk2 provider = new PactProviderRuleMk2("DummyService", "localhost",8114, this);

    @Pact(provider="DummyService", consumer = "DummyServiceClient")
    public RequestResponsePact createPactForDummyServiceProvider(PactDslWithProvider builder) {
        Map<String, String> headers = new HashMap();
        headers.put("Content-Type", "application/json");

        DslPart etaResults = new PactDslJsonBody()
                .stringType("employee_name","Dinesh")
                .stringType("id","465")
                .integerType("employee_salary",46)
                .integerType("employee_age",28)
                .stringType("profile_image","")
                .guid("")
                .asBody();

        return builder
                .given("There is an employee with employee id 465")
                .uponReceiving("A request for employee name for the employee id 465")
                .path("/v1/employee/465")
                .headers(headers)
                .method("GET")
                .willRespondWith()
                .status(200)
                .headers(headers)
                .body(etaResults).toPact();

    }

    @Test
    @PactVerification()
    public void DummyAPIShouldReturnEmployeeName() {
        System.setProperty("pact.rootDir","../pacts");  // Change output dir for generated pact-files
        String employee_name = new DummyAPIClient().checkName("465");
        System.out.println("According to test employee name="+employee_name);
        assertTrue(!employee_name.isEmpty());
    }
}
