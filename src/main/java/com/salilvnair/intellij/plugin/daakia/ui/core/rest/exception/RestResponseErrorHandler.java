package com.salilvnair.intellij.plugin.daakia.ui.core.rest.exception;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.DefaultResponseErrorHandler;

import java.io.IOException;

public class RestResponseErrorHandler extends DefaultResponseErrorHandler {
    private boolean hasError = false;
    private HttpStatusCode errorStatusCode;
    private HttpHeaders responseHeaders;


    @Override
    public void handleError(ClientHttpResponse response) throws IOException {
        setHasError(true);
        HttpStatusCode statusCode = response.getStatusCode();
        setResponseHeaders(response.getHeaders());
        setErrorStatusCode(statusCode);
    }

    public HttpStatusCode errorStatusCode() {
        return errorStatusCode;
    }

    public void setErrorStatusCode(HttpStatusCode errorStatusCode) {
        this.errorStatusCode = errorStatusCode;
    }

    public HttpHeaders responseHeaders() {
        return responseHeaders;
    }

    public void setResponseHeaders(HttpHeaders responseHeaders) {
        this.responseHeaders = responseHeaders;
    }

    public boolean hasError() {
        return hasError;
    }

    public void setHasError(boolean hasError) {
        this.hasError = hasError;
    }
}
