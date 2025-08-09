package com.salilvnair.intellij.plugin.daakia.ui.service.provider;

import com.intellij.openapi.application.ApplicationManager;
import com.salilvnair.intellij.plugin.daakia.script.main.DaakiaScriptExecutor;
import com.salilvnair.intellij.plugin.daakia.ui.core.model.AuthInfo;
import com.salilvnair.intellij.plugin.daakia.ui.core.model.ResponseMetadata;
import com.salilvnair.intellij.plugin.daakia.ui.core.rest.exception.RestResponseErrorHandler;
import com.salilvnair.intellij.plugin.daakia.ui.service.base.BaseDaakiaService;
import com.salilvnair.intellij.plugin.daakia.ui.service.context.DaakiaContext;
import com.salilvnair.intellij.plugin.daakia.ui.service.context.DataContext;
import com.salilvnair.intellij.plugin.daakia.ui.service.type.AuthorizationType;
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
            java.util.concurrent.Future<?> future = ApplicationManager.getApplication().executeOnPooledThread(() -> {
                invokeRestApi(dataContext);
            });
            dataContext.uiContext().setActiveRequest(future);

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
            if (Thread.currentThread().isInterrupted()) {
                dataContext.uiContext().setActiveRequest(null);
                return;
            }
            errorMessage = e.getLocalizedMessage();
        }
        if (Thread.currentThread().isInterrupted()) {
            dataContext.uiContext().setActiveRequest(null);
            return;
        }
        ResponseEntity<?> finalResponse = response;
        String finalErrorMessage = errorMessage;
        dataContext.uiContext().setActiveRequest(null);
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
        addApplicableContentType(dataContext, headers);
        dataContext.daakiaContext().setRequestHeaders(headers);
        HttpHeaders headersForRequest = new HttpHeaders(headers);
        addAuthorizationHeaderIfPresent(dataContext, headersForRequest);
        return headersForRequest;
    }

    private void addApplicableContentType(DataContext dataContext, HttpHeaders headers) {
        if("2".equals(dataContext.uiContext().requestContentType())) {
            headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        }
    }

    private void addAuthorizationHeaderIfPresent(DataContext dataContext, HttpHeaders headers) {
        AuthInfo authInfo = buildAuthInfoFromUi(dataContext);
        dataContext.uiContext().setAuthInfo(authInfo);
        if (authInfo == null || authInfo.getAuthType() == null) {
            return;
        }
        if (AuthorizationType.BEARER_TOKEN.type().equals(authInfo.getAuthType())) {
            String resolvedBearerToken = PostmanEnvironmentUtils.resolveVariables(authInfo.getToken(), dataContext);
            headers.setBearerAuth(resolvedBearerToken);
        }
        else if (AuthorizationType.BASIC_AUTH.type().equals(authInfo.getAuthType())) {
            String resolvedUserName = PostmanEnvironmentUtils.resolveVariables(authInfo.getUsername(), dataContext);
            String resolvedPassword = PostmanEnvironmentUtils.resolveVariables(authInfo.getPassword(), dataContext);
            headers.setBasicAuth(resolvedUserName, resolvedPassword);
        }
    }

    private AuthInfo buildAuthInfoFromUi(DataContext dataContext) {
        if(dataContext.uiContext().authTypes() == null) {
            return null;
        }
        String selectedAuthType = (String) dataContext.uiContext().authTypes().getSelectedItem();
        if(selectedAuthType == null || AuthorizationType.NONE.type().equals(selectedAuthType)) {
            return null;
        }
        AuthInfo authInfo = new AuthInfo();
        authInfo.setAuthType(selectedAuthType);
        if(AuthorizationType.BEARER_TOKEN.type().equals(selectedAuthType)) {
            authInfo.setToken(new String(dataContext.uiContext().bearerTokenTextField().getPassword()));
        }
        else if(AuthorizationType.BASIC_AUTH.type().equals(selectedAuthType)) {
            authInfo.setUsername(dataContext.uiContext().userNameTextField().getText());
            authInfo.setPassword(new String(dataContext.uiContext().passwordTextField().getPassword()));
        }
        return authInfo;
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
