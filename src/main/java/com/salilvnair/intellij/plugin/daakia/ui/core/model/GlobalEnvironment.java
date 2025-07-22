package com.salilvnair.intellij.plugin.daakia.ui.core.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class GlobalEnvironment implements EnvironmentTemplate {
    private List<Variable> variables;


    public List<Variable> variables() {
        if(variables == null) {
            variables = new ArrayList<>();
        }
        return variables;
    }

}
