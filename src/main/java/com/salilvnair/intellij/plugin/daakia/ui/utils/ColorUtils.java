package com.salilvnair.intellij.plugin.daakia.ui.utils;

import com.salilvnair.intellij.plugin.daakia.ui.service.type.RequestType;

public class ColorUtils {
    private ColorUtils() {}


    public static String hexCodeByRequestType(RequestType requestType) {
        boolean hasDarkTheme = IntellijUtils.hasDarkTheme();
        String hexCode = hasDarkTheme ? "#6bdd99" : "#027f31";
        if(RequestType.GET.equals(requestType)) {
            hexCode = hasDarkTheme ? "#6bdd99" : "#027f31";
        }
        else if(RequestType.POST.equals(requestType)) {
            hexCode = hasDarkTheme ? "#ffe47e" : "#ac7a04";
        }
        else if(RequestType.PUT.equals(requestType)) {
            hexCode = hasDarkTheme ? "#74adf6" : "#0053b8";
        }
        else if(RequestType.DELETE.equals(requestType)) {
            hexCode = hasDarkTheme ? "#f79a8e" : "#8e1b11";
        }
        else if(RequestType.GRAPHQL.equals(requestType)) {
            hexCode = hasDarkTheme ? "#f15eb0" : "#a61468";
        }
        return hexCode;
    }

}
