package com.salilvnair.intellij.plugin.daakia.ui.archive.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DaakiaStoreRecord {
    private Integer id;
    private String uuid;
    private String displayName;
    private String requestType;
    private String url;
    private String headers;
    private String requestBody;
    private String responseBody;
    private String createdDate;


    public String getRequestType() {
        return requestType;
    }

    public void setRequestType(String requestType) {
        this.requestType = requestType;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getHeaders() {
        return headers;
    }

    public void setHeaders(String headers) {
        this.headers = headers;
    }

    public String getRequestBody() {
        return requestBody;
    }

    public void setRequestBody(String requestBody) {
        this.requestBody = requestBody;
    }

    public String getDisplayName() {
        return displayName == null ? url: displayName;
    }

    public String render() {
        String hexCode = "#10b981";
        if("POST".equals(requestType)) {
            hexCode ="#eab208";
        }
        String displayText = displayName == null ? url: displayName;
        return "<html><strong><font color='"+hexCode+"'>"+requestType+"</font></strong>&nbsp; "+displayText+"</html>";
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public Integer getId() {
        return id;
    }
    public void setId(Integer id) {
        this.id = id;
    }

    public String getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(String createdDate) {
        this.createdDate = createdDate;
    }


    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String uuid() {
        return uuid;
    }

    public String getResponseBody() {
        return responseBody;
    }

    public void setResponseBody(String responseBody) {
        this.responseBody = responseBody;
    }

    @Override
    public String toString() {
        return render();
    }
}
