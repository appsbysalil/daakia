package com.salilvnair.intellij.plugin.daakia.ui.service.provider;

import com.intellij.openapi.application.ApplicationManager;
import com.salilvnair.intellij.plugin.daakia.script.main.DaakiaScriptExecutor;
import com.salilvnair.intellij.plugin.daakia.ui.core.model.ResponseMetadata;
import com.salilvnair.intellij.plugin.daakia.ui.core.rest.exception.RestResponseErrorHandler;
import com.salilvnair.intellij.plugin.daakia.ui.service.base.BaseDaakiaService;
import com.salilvnair.intellij.plugin.daakia.ui.service.context.DaakiaContext;
import com.salilvnair.intellij.plugin.daakia.ui.service.context.DataContext;
import com.salilvnair.intellij.plugin.daakia.ui.service.type.DaakiaTypeBase;
import com.salilvnair.intellij.plugin.daakia.ui.service.type.RestDaakiaType;
import com.salilvnair.intellij.plugin.daakia.ui.utils.PostmanEnvironmentUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.*;
import org.springframework.http.client.JdkClientHttpRequestFactory;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

public class RestDaakiaService extends BaseDaakiaService {
    @Override
    public DaakiaContext execute(DaakiaTypeBase type, DataContext dataContext, Object... objects) {
        if(RestDaakiaType.EXCHANGE.equals(type)) {
            ApplicationManager.getApplication().executeOnPooledThread(() -> {
                invokeRestApi(dataContext);
            });

        }
        return dataContext.daakiaContext();
    }

    private void invokeRestApi(DataContext dataContext) {
        executePreRequestScript(dataContext);
        String url = PostmanEnvironmentUtils.resolveVariables(dataContext.uiContext().urlTextField().getText(), dataContext);
        String requestType = (String) dataContext.uiContext().requestTypes().getSelectedItem();

        String originalBody = dataContext.uiContext().requestTextArea().getText();
        String resolvedBody = PostmanEnvironmentUtils.resolveVariables(originalBody, dataContext);

        RestTemplate restTemplate = new RestTemplate();
        HttpEntity<?> entity = prepareRequestEntity(dataContext, resolvedBody);
        long startTime = System.currentTimeMillis();
        RestResponseErrorHandler errorHandler = new RestResponseErrorHandler();
        restTemplate.setErrorHandler(errorHandler);
        restTemplate.setRequestFactory(new JdkClientHttpRequestFactory());
        ResponseEntity<?> response = null;
        String errorMessage = null;
        try {
            if(dataContext.uiContext().downloadResponse()) {
                response = restTemplate.exchange(url, HttpMethod.valueOf(requestType != null ? requestType : "GET"), entity, byte[].class);
            }
            else {
                response = restTemplate.exchange(url, HttpMethod.valueOf(requestType != null ? requestType : "GET"), entity, String.class);
            }
        }
        catch (Exception e) {
            errorMessage = e.getLocalizedMessage();
        }
        ResponseEntity<?> finalResponse = response;
        String finalErrorMessage = errorMessage;
        ApplicationManager.getApplication().invokeLater(() -> {
            updateDaakiaContext(startTime, dataContext, finalResponse, finalErrorMessage);
        });
    }

    private @NotNull HttpEntity<?> prepareRequestEntity(DataContext dataContext, String bodyText) {
        HttpHeaders headers = prepareRequestHeaders(dataContext);
        if("2".equals(dataContext.uiContext().requestContentType())) {
            return formDataRequestEntity(dataContext, headers);
        }
        return new HttpEntity<>(bodyText, headers);
    }

    private HttpEntity<?> formDataRequestEntity(DataContext dataContext, HttpHeaders headers) {
        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        for (String key : dataContext.uiContext().formDataFileFields().keySet()) {
            body.add(key, new FileSystemResource(dataContext.uiContext().formDataFileFields().get(key)));
        }
        dataContext.uiContext().formDataTextFields().forEach((k, v) -> {
            String val = PostmanEnvironmentUtils.resolveVariables(v.get(1).getText(), dataContext);
            body.add(v.get(0).getText(), val);
        });
        return new HttpEntity<>(body, headers);
    }

    private void updateDaakiaContext(long startTime, DataContext dataContext, ResponseEntity<?> response, String finalErrorMessage) {
        if (response != null) {
            String body = response.getBody()!=null ? response.getBody()+"": null;
            HttpHeaders headers = response.getHeaders();
            HttpStatusCode statusCode = response.getStatusCode();
            HttpStatus status = HttpStatus.valueOf(statusCode.value());
            long endTime = System.currentTimeMillis();
            long timeTaken = endTime - startTime;
            ResponseMetadata responseMetadata = new ResponseMetadata();
            responseMetadata.setSizeText((headers.getContentLength() != -1 ? headers.getContentLength() : body!=null ? body.getBytes().length : 0) + " bytes");
            responseMetadata.setTimeTaken(timeTaken + " ms");
            responseMetadata.setStatusCode(statusCode.value());
            dataContext.daakiaContext().setResponseEntity(response);
            dataContext.daakiaContext().setResponseMetadata(responseMetadata);
            dataContext.daakiaContext().setResponseHeaders(headers);
            dataContext.daakiaContext().setHttpStatus(status);
        }
        else {
            HttpStatus status = HttpStatus.valueOf(503);
            dataContext.daakiaContext().setHttpStatus(status);
            long endTime = System.currentTimeMillis();
            long timeTaken = endTime - startTime;
            ResponseMetadata responseMetadata = new ResponseMetadata();
            responseMetadata.setSizeText("0 bytes");
            responseMetadata.setTimeTaken(timeTaken + " ms");
            responseMetadata.setStatusCode(503);
            dataContext.daakiaContext().setResponseMetadata(responseMetadata);
            dataContext.daakiaContext().setResponseHeaders(new LinkedMultiValueMap<>());
        }
        dataContext.daakiaContext().setErrorMessage(finalErrorMessage);
        dataContext.eventPublisher().afterRestApiExchange(dataContext.daakiaContext());
        executePostRequestScript(dataContext);
    }

    private HttpHeaders prepareRequestHeaders(DataContext dataContext) {
        HttpHeaders headers = new HttpHeaders();
        dataContext.uiContext().headerTextFields().forEach((k, v) -> {
            String headerName = PostmanEnvironmentUtils.resolveVariables(v.get(0).getText(), dataContext);
            String headerVal = PostmanEnvironmentUtils.resolveVariables(v.get(1).getText(), dataContext);
            headers.add(headerName, headerVal);
        });
        HttpHeaders authHeaders = addAuthorizationHeaderIfPresent(dataContext);
        if(!authHeaders.isEmpty()) {
            headers.addAll(authHeaders);
        }
        addApplicableContentType(dataContext, headers);
        dataContext.daakiaContext().setRequestHeaders(headers);
        return headers;
    }

    private void addApplicableContentType(DataContext dataContext, HttpHeaders headers) {
        if("2".equals(dataContext.uiContext().requestContentType())) {
            headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        }
    }

    private HttpHeaders addAuthorizationHeaderIfPresent(DataContext dataContext) {
        HttpHeaders authHeaders = new HttpHeaders();
        if(dataContext.uiContext().authTypes() != null) {
            String selectedAuthType = (String) dataContext.uiContext().authTypes().getSelectedItem();
            if("Bearer Token".equals(selectedAuthType)) {
                String bearerToken = new String(dataContext.uiContext().bearerTokenTextField().getPassword());
                String resolvedBearerToken = PostmanEnvironmentUtils.resolveVariables(bearerToken, dataContext);
                authHeaders.setBearerAuth(resolvedBearerToken);
            }
            else if("Basic Auth".equals(selectedAuthType)) {
                String userName = dataContext.uiContext().userNameTextField().getText();
                String password = new String(dataContext.uiContext().passwordTextField().getPassword());
                String resolvedUserName = PostmanEnvironmentUtils.resolveVariables(userName, dataContext);
                String resolvedPassword = PostmanEnvironmentUtils.resolveVariables(password, dataContext);
                authHeaders.setBasicAuth(resolvedUserName, resolvedPassword);
            }
        }
        return authHeaders;
    }

    private void executePreRequestScript(DataContext dataContext) {
        String script = dataContext.uiContext().preRequestScriptArea() != null ? dataContext.uiContext().preRequestScriptArea().getText() : null;
        try (DaakiaScriptExecutor executor = DaakiaScriptExecutor.init(dataContext)) {
            executor.executeScript(script);
        }
    }

    private void executePostRequestScript(DataContext dataContext) {
        String script = dataContext.uiContext().postRequestScriptArea() != null ? dataContext.uiContext().postRequestScriptArea().getText() : null;
        try (DaakiaScriptExecutor executor = DaakiaScriptExecutor.init(dataContext)) {
            executor.executeScript(script);
        }
    }
}
