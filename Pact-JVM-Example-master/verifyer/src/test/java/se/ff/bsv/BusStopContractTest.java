package se.ff.bsv;


import au.com.dius.pact.provider.PactVerifyProvider;
import au.com.dius.pact.provider.junit.*;
import au.com.dius.pact.provider.junit.loader.PactFolder;
import au.com.dius.pact.provider.junit.target.HttpTarget;
import au.com.dius.pact.provider.junit.target.Target;
import au.com.dius.pact.provider.junit.target.TestTarget;
import org.apache.http.HttpRequest;
import org.junit.runner.RunWith;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

@RunWith(PactRunner.class) // Say JUnit to run tests with custom Runner
@Provider("BusService") // Set up name of tested provider
@PactFolder("../pacts") // Point where to find pacts (See also section Pacts source in documentation)
@VerificationReports(value = {"console", "markdown", "json"}, reportDir = ".\\target\\Results")
public class BusStopContractTest {
    public BusStopContractTest() throws MalformedURLException {
    }

    @State("There is a bus with number 613 arriving to Hammersmith bus station") // Method will be run before testing interactions that require "with-data" state
    public void hammerSmith() {
        System.out.println("There is a bus with number 613 arriving to Hammersmith bus station" );
    }

    @PactVerifyProvider("BusService")
    public void verify()
    {
    }

    @TargetRequestFilter
    public void exampleRequestFilter(HttpRequest request){
        System.out.println("exampleRequestFilter called: " + request.getRequestLine().getUri());
    }

    @TestTarget // Annotation denotes Target that will be used for tests    //public final Target target = new HttpTarget(8111); // Out-of-the-box implementation of Target (for more information take a look at Test Target section)
    public final Target target = new CustomHttpTarget(new URL("https://pact.web:8443"),
            false, new File("C:/Users/anuraga/Desktop/Pact-JVM-Example-master/Pact-JVM-Example-master/pact-jvm-512.jks"), "password");
}
