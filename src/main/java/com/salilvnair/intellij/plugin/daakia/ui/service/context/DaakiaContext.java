package com.salilvnair.intellij.plugin.daakia.ui.service.context;

import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

public class DaakiaContext {
    private MultiValueMap<String, String> requestHeaders;
    private ResponseEntity<String> responseEntity;

    public MultiValueMap<String, String> requestHeaders() {
        if(requestHeaders == null) {
            requestHeaders = new LinkedMultiValueMap<>();
        }
        return requestHeaders;
    }

    public void setRequestHeaders(MultiValueMap<String, String> requestHeaders) {
        this.requestHeaders = requestHeaders;
    }

    public ResponseEntity<String> responseEntity() {
        return responseEntity;
    }

    public void setResponseEntity(ResponseEntity<String> responseEntity) {
        this.responseEntity = responseEntity;
    }
}
