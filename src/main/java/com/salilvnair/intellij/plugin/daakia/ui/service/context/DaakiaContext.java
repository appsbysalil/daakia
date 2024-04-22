package com.salilvnair.intellij.plugin.daakia.ui.service.context;

import com.salilvnair.intellij.plugin.daakia.ui.core.model.ResponseMetadata;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

public class DaakiaContext {
    private String errorMessage;
    private MultiValueMap<String, String> requestHeaders;
    private ResponseEntity<String> responseEntity;
    private HttpStatus httpStatus;
    private ResponseMetadata responseMetadata;
    private MultiValueMap<String, String> responseHeaders;

    public MultiValueMap<String, String> requestHeaders() {
        if(requestHeaders == null) {
            requestHeaders = new LinkedMultiValueMap<>();
        }
        return requestHeaders;
    }

    public void setRequestHeaders(MultiValueMap<String, String> requestHeaders) {
        this.requestHeaders = requestHeaders;
    }

    public MultiValueMap<String, String> responseHeaders() {
        if(responseHeaders == null) {
            responseHeaders = new LinkedMultiValueMap<>();
        }
        return responseHeaders;
    }

    public void setResponseHeaders(MultiValueMap<String, String> responseHeaders) {
        this.responseHeaders = responseHeaders;
    }

    public ResponseEntity<String> responseEntity() {
        return responseEntity;
    }

    public void setResponseEntity(ResponseEntity<String> responseEntity) {
        this.responseEntity = responseEntity;
    }

    public ResponseMetadata responseMetadata() {
        return responseMetadata;
    }

    public void setResponseMetadata(ResponseMetadata responseMetadata) {
        this.responseMetadata = responseMetadata;
    }

    public HttpStatus httpStatus() {
        return httpStatus;
    }

    public void setHttpStatus(HttpStatus httpStatus) {
        this.httpStatus = httpStatus;
    }

    public String errorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
}
