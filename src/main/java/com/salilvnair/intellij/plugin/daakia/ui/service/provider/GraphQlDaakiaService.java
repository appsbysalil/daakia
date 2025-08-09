package com.salilvnair.intellij.plugin.daakia.ui.service.provider;

import com.intellij.openapi.application.ApplicationManager;
import com.salilvnair.intellij.plugin.daakia.ui.core.model.ResponseMetadata;
import com.salilvnair.intellij.plugin.daakia.ui.core.model.AuthInfo;
import com.salilvnair.intellij.plugin.daakia.ui.core.rest.exception.RestResponseErrorHandler;
import com.salilvnair.intellij.plugin.daakia.ui.service.base.BaseDaakiaService;
import com.salilvnair.intellij.plugin.daakia.ui.service.context.DataContext;
import com.salilvnair.intellij.plugin.daakia.ui.core.model.Environment;
import com.salilvnair.intellij.plugin.daakia.ui.service.context.DaakiaContext;
import com.salilvnair.intellij.plugin.daakia.ui.service.type.DaakiaTypeBase;
import com.salilvnair.intellij.plugin.daakia.ui.service.type.GraphQlDaakiaType;
import com.salilvnair.intellij.plugin.daakia.ui.service.type.AuthorizationType;
import com.salilvnair.intellij.plugin.daakia.ui.utils.PostmanEnvironmentUtils;
import com.salilvnair.intellij.plugin.daakia.ui.utils.DaakiaScriptExecutor;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.*;
import org.springframework.http.client.JdkClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

public class GraphQlDaakiaService extends BaseDaakiaService {
    @Override
    public DaakiaContext execute(DaakiaTypeBase type, DataContext dataContext, Object... objects) {
        if(GraphQlDaakiaType.EXECUTE.equals(type)) {
            ApplicationManager.getApplication().executeOnPooledThread(() -> invokeGraphQlApi(dataContext));
        }
        return dataContext.daakiaContext();
    }

    private void invokeGraphQlApi(DataContext dataContext) {
        executePreRequestScript(dataContext);
        Environment env = dataContext.globalContext().selectedEnvironment();
        String url = PostmanEnvironmentUtils.resolveVariables(dataContext.uiContext().urlTextField().getText(), dataContext);
        String originalBody = dataContext.uiContext().requestTextArea().getText();
        String resolvedBody = PostmanEnvironmentUtils.resolveVariables(originalBody, dataContext);

        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = prepareRequestHeaders(dataContext);
        headers.setContentType(MediaType.APPLICATION_JSON);
        Map<String, Object> payload = new HashMap<>();
        payload.put("query", resolvedBody);
        HttpEntity<?> entity = new HttpEntity<>(payload, headers);
        long startTime = System.currentTimeMillis();
        RestResponseErrorHandler errorHandler = new RestResponseErrorHandler();
        restTemplate.setErrorHandler(errorHandler);
        restTemplate.setRequestFactory(new JdkClientHttpRequestFactory());
        ResponseEntity<?> response = null;
        String errorMessage = null;
        try {
            if(dataContext.uiContext().downloadResponse()) {
                response = restTemplate.postForEntity(url, entity, byte[].class);
            }
            else {
                response = restTemplate.postForEntity(url, entity, String.class);
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
        headers.setContentType(MediaType.APPLICATION_JSON);
        Map<String, Object> payload = new HashMap<>();
        payload.put("query", bodyText);
        return new HttpEntity<>(payload, headers);
    }

    private HttpHeaders prepareRequestHeaders(DataContext dataContext) {
        HttpHeaders headers = new HttpHeaders();
        dataContext.uiContext().headerTextFields().forEach((k, v) -> {
            String headerName = PostmanEnvironmentUtils.resolveVariables(v.get(0).getText(), dataContext);
            String headerVal = PostmanEnvironmentUtils.resolveVariables(v.get(1).getText(), dataContext);
            headers.add(headerName, headerVal);
        });
        dataContext.daakiaContext().setRequestHeaders(headers);
        HttpHeaders headersForRequest = new HttpHeaders(headers);
        addAuthorizationHeaderIfPresent(dataContext, headersForRequest);
        return headersForRequest;
    }

    private void addAuthorizationHeaderIfPresent(DataContext dataContext, HttpHeaders headers) {
        AuthInfo authInfo = buildAuthInfoFromUi(dataContext);
        dataContext.uiContext().setAuthInfo(authInfo);
        if (authInfo == null || authInfo.getAuthType() == null) {
            return;
        }
        if (AuthorizationType.BEARER_TOKEN.type().equals(authInfo.getAuthType())) {
            String bearerToken = PostmanEnvironmentUtils.resolveVariables(authInfo.getToken(), dataContext);
            headers.setBearerAuth(bearerToken);
        }
        else if (AuthorizationType.BASIC_AUTH.type().equals(authInfo.getAuthType())) {
            String userName = PostmanEnvironmentUtils.resolveVariables(authInfo.getUsername(), dataContext);
            String password = PostmanEnvironmentUtils.resolveVariables(authInfo.getPassword(), dataContext);
            headers.setBasicAuth(userName, password);
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

    private void updateDaakiaContext(long startTime, DataContext dataContext, ResponseEntity<?> response, String finalErrorMessage) {
        if (response != null) {
            String body = response.getBody()!=null ? response.getBody()+"" : null;
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
            dataContext.daakiaContext().setResponseHeaders(new HttpHeaders());
        }
        dataContext.daakiaContext().setErrorMessage(finalErrorMessage);
        dataContext.eventPublisher().afterRestApiExchange(dataContext.daakiaContext());
        executePostRequestScript(dataContext);
    }

    private void executePreRequestScript(DataContext dataContext) {
        Environment env = dataContext.globalContext().selectedEnvironment();
        String script = dataContext.uiContext().preRequestScriptArea() != null ? dataContext.uiContext().preRequestScriptArea().getText() : null;
        DaakiaScriptExecutor.execute(script, env);
    }

    private void executePostRequestScript(DataContext dataContext) {
        Environment env = dataContext.globalContext().selectedEnvironment();
        String script = dataContext.uiContext().postRequestScriptArea() != null ? dataContext.uiContext().postRequestScriptArea().getText() : null;
        DaakiaScriptExecutor.execute(script, env);
    }
}
