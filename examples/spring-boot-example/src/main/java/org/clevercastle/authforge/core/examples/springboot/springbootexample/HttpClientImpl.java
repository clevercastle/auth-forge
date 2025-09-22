package org.clevercastle.authforge.core.examples.springboot.springbootexample;

import org.apache.hc.client5.http.classic.methods.HttpUriRequestBase;
import org.apache.hc.client5.http.config.RequestConfig;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClientBuilder;
import org.apache.hc.core5.http.Header;
import org.apache.hc.core5.util.Timeout;
import org.clevercastle.authforge.core.exception.CastleException;
import org.clevercastle.authforge.core.http.HttpRequest;
import org.clevercastle.authforge.core.http.HttpResponse;
import org.clevercastle.authforge.core.http.IHttpClient;

import java.io.IOException;
import java.net.URI;
import java.nio.charset.Charset;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class HttpClientImpl implements IHttpClient {
    private final CloseableHttpClient httpClient = HttpClientBuilder.create().build();

    public HttpClientImpl() {
    }

    @Override
    public HttpResponse execute(HttpRequest request) throws CastleException {
        RequestConfig requestConfig = RequestConfig.custom()
                .setResponseTimeout(Timeout.of(15, TimeUnit.SECONDS))  // Response timeout - 3 seconds
                .build();
        HttpUriRequestBase httpRequest = new HttpUriRequestBase(request.getMethod(), URI.create(request.getUrl()));
        request.getHeaders().forEach(httpRequest::setHeader);
        httpRequest.setConfig(requestConfig);
        try {
            CloseableHttpResponse response = httpClient.execute(httpRequest);
            Map<String, String> headers = new LinkedHashMap<>();
            for (Header it : response.getHeaders()) {
                headers.put(it.getName(), it.getValue());
            }
            return new HttpResponse(response.getCode(),
                    new String(response.getEntity().getContent().readAllBytes(), Charset.defaultCharset()),
                    headers);
        } catch (IOException e) {
            throw new CastleException(e);
        }
    }
}
