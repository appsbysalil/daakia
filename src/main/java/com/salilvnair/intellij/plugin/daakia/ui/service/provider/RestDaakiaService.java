package com.salilvnair.intellij.plugin.daakia.ui.service.provider;

import com.salilvnair.intellij.plugin.daakia.ui.service.base.BaseDaakiaService;
import com.salilvnair.intellij.plugin.daakia.ui.service.context.DaakiaContext;
import com.salilvnair.intellij.plugin.daakia.ui.service.context.DataContext;
import com.salilvnair.intellij.plugin.daakia.ui.service.type.DaakiaTypeBase;
import com.salilvnair.intellij.plugin.daakia.ui.service.type.RestDaakiaType;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

public class RestDaakiaService extends BaseDaakiaService {
    @Override
    public DaakiaContext execute(DaakiaTypeBase type, DataContext dataContext, Object... objects) {
        if(RestDaakiaType.EXCHANGE.equals(type)) {
            invokeRestApi(dataContext);
        }
        return dataContext.daakiaContext();
    }

    private void invokeRestApi(DataContext dataContext) {
        String url = dataContext.uiContext().urlTextField().getText();
        String requestType = (String) dataContext.uiContext().requestTypes().getSelectedItem();

        RestTemplate restTemplate = new RestTemplate();
        HttpEntity<?> entity = new HttpEntity<>(dataContext.uiContext().requestTextArea().getText(), prepareRequestHeaders(dataContext));
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.valueOf(requestType != null ? requestType : "GET"), entity, String.class);
        dataContext.daakiaContext().setResponseEntity(response);
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
