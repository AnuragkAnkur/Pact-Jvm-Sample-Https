package se.ff.bsv;

import au.com.dius.pact.model.Interaction;
import au.com.dius.pact.model.PactSource;
import au.com.dius.pact.model.ProviderState;
import au.com.dius.pact.model.RequestResponseInteraction;
import au.com.dius.pact.provider.*;
import java.util.function.Consumer;
import au.com.dius.pact.provider.junit.TargetRequestFilter;
import au.com.dius.pact.provider.junit.target.BaseTarget;

import java.io.File;
import java.lang.annotation.Annotation;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jetbrains.annotations.NotNull;
import org.junit.runners.model.FrameworkMethod;

public class CustomHttpTarget extends BaseTarget {

    private final String host;
    private final int port;
    private final String protocol;
    private final String path;
    private final boolean insecure;
    private File trustStore;
    private String password;

    public CustomHttpTarget(String protocol, String host, int port, String path, boolean insecure) {
        super();
        this.host = host;
        this.port = port;
        this.protocol = protocol;
        this.path = path;
        this.insecure = insecure;
    }

    public CustomHttpTarget(String protocol, String host, int port, String path, boolean insecure, File trustStore, String password) {
        super();
        this.host = host;
        this.port = port;
        this.protocol = protocol;
        this.path = path;
        this.insecure = insecure;
        this.trustStore = trustStore;
        this.password = password;
    }

    public CustomHttpTarget(URL url, boolean insecure) {
        this(url.getProtocol() == null ? "http" : url.getProtocol(), url.getHost(), url.getPort() == -1 && url.getProtocol().equalsIgnoreCase("http") ? 8080 : (url.getPort() == -1 && url.getProtocol().equalsIgnoreCase("https") ? 443 : url.getPort()), url.getPath() == null ? "/" : url.getPath(), insecure);
    }

    public CustomHttpTarget(URL url, boolean insecure, File trustStore, String password) {
        this(url.getProtocol() == null ? "http" : url.getProtocol(), url.getHost(), url.getPort() == -1 && url.getProtocol().equalsIgnoreCase("http") ? 8080 : (url.getPort() == -1 && url.getProtocol().equalsIgnoreCase("https") ? 443 : url.getPort()), url.getPath() == null ? "/" : url.getPath(), insecure, trustStore, password);
    }

    @Override
    protected ProviderInfo getProviderInfo(PactSource pactSource) {
        Annotation provider = testClass.getAnnotation(au.com.dius.pact.provider.junit.Provider.class);
        ProviderInfo providerInfo = new ProviderInfo(provider.annotationType().getName());
        providerInfo.setPort(port);
        providerInfo.setHost(host);
        providerInfo.setProtocol(protocol);
        providerInfo.setPath(path);
        providerInfo.setInsecure(insecure);

        if (trustStore != null)
        {
            providerInfo.setTrustStore(trustStore);
            providerInfo.setTrustStorePassword(password);
        }

        List<FrameworkMethod> methods = testClass.getAnnotatedMethods(TargetRequestFilter.class);
        providerInfo.setRequestFilter((Consumer)httpRequest -> {
            for (FrameworkMethod method : methods) {
                try {
                    method.invokeExplosively(testTarget,  httpRequest);
                } catch (Throwable t) {
                    throw new AssertionError("Request filter method ${method.name} failed with an exception", t);
                }
            }
        });
        return providerInfo;
    }

    @Override
    protected ProviderVerifier setupVerifier(Interaction interaction, ProviderInfo providerInfo, ConsumerInfo consumerInfo) {
        ProviderVerifier verifier = new ProviderVerifier();
        setupReporters(verifier, providerInfo.getName(), interaction.getDescription());

        verifier.initialiseReporters(providerInfo);
        verifier.reportVerificationForConsumer(consumerInfo, providerInfo);

        if (!interaction.getProviderStates().isEmpty()) {
            for (ProviderState state : interaction.getProviderStates())
                verifier.reportStateForInteraction(state.getName(), providerInfo, consumerInfo, true);
        }

        verifier.reportInteractionDescription(interaction);

        return verifier;
    }

    @Override
    public void testInteraction(@NotNull String consumerName, @NotNull Interaction interaction, @NotNull PactSource pactSource, @NotNull Map<String, ?> context) {
        ProviderInfo provider = getProviderInfo(pactSource);
        ConsumerInfo consumer = new ConsumerInfo(consumerName);
        ProviderVerifier verifier = setupVerifier(interaction, provider, consumer);

        Map<String, Object> failures = new HashMap<String, Object>();
        ProviderClient client = new ProviderClient(provider, new ClientFactory());
        verifier.verifyResponseFromProvider(provider, (RequestResponseInteraction) interaction, interaction.getDescription(),
                failures, client, context);
        reportTestResult(failures.isEmpty(), verifier);

        try {
            if (!failures.isEmpty()) {
                verifier.displayFailures(failures);
                throw getAssertionError(failures);
            }
        } finally {
            verifier.finaliseReports();
        }
    }
}