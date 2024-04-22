package com.salilvnair.intellij.plugin.daakia.ui.service.provider;

import com.intellij.openapi.application.ApplicationManager;
import com.salilvnair.intellij.plugin.daakia.ui.core.model.ResponseMetadata;
import com.salilvnair.intellij.plugin.daakia.ui.core.rest.exception.RestResponseErrorHandler;
import com.salilvnair.intellij.plugin.daakia.ui.service.base.BaseDaakiaService;
import com.salilvnair.intellij.plugin.daakia.ui.service.context.DaakiaContext;
import com.salilvnair.intellij.plugin.daakia.ui.service.context.DataContext;
import com.salilvnair.intellij.plugin.daakia.ui.service.type.DaakiaTypeBase;
import com.salilvnair.intellij.plugin.daakia.ui.service.type.RestDaakiaType;
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
        String url = dataContext.uiContext().urlTextField().getText();
        String requestType = (String) dataContext.uiContext().requestTypes().getSelectedItem();

        RestTemplate restTemplate = new RestTemplate();
        HttpEntity<?> entity = new HttpEntity<>(dataContext.uiContext().requestTextArea().getText(), prepareRequestHeaders(dataContext));
        long startTime = System.currentTimeMillis();
        RestResponseErrorHandler errorHandler = new RestResponseErrorHandler();
        restTemplate.setErrorHandler(errorHandler);
        restTemplate.setRequestFactory(new JdkClientHttpRequestFactory());
        ResponseEntity<String> response = null;
        String errorMessage = null;
        try {
            response = restTemplate.exchange(url, HttpMethod.valueOf(requestType != null ? requestType : "GET"), entity, String.class);
        }
        catch (Exception e) {
            errorMessage = e.getLocalizedMessage();
        }
        ResponseEntity<String> finalResponse = response;
        String finalErrorMessage = errorMessage;
        ApplicationManager.getApplication().invokeLater(() -> {
            updateDaakiaContext(startTime, dataContext, finalResponse, finalErrorMessage);
        });
    }

    private void updateDaakiaContext(long startTime, DataContext dataContext, ResponseEntity<String> response, String finalErrorMessage) {
        if (response != null) {
            String body = response.getBody();
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
    }

    private MultiValueMap<String, String> prepareRequestHeaders(DataContext dataContext) {
        MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
        dataContext.uiContext().headerTextFields().forEach((k, v) -> {
            headers.add(v.get(0).getText(), v.get(1).getText());
        });
        dataContext.daakiaContext().setRequestHeaders(headers);
        return headers;
    }
}
