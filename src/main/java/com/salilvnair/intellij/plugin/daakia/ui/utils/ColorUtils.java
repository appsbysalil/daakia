package com.salilvnair.intellij.plugin.daakia.ui.utils;

import com.salilvnair.intellij.plugin.daakia.ui.service.type.RequestType;

public class ColorUtils {
    private ColorUtils() {}

    public enum HexCode {
        GREEN("Green", "#027f31", "#6bdd99"),
        YELLOW("Yellow", "#ac7a04", "#ffe47e"),
        BLUE("Blue", "#0053b8", "#74adf6"),
        RED("Red", "#8e1b11", "#f79a8e"),
        PINK("Pink", "#a61468", "#f15eb0"),
        PURPLE("Purple", "#7f3cff", "#b89cff"),
        GRAY("Gray", "#444444", "#aaaaaa"),
        CYAN("Cyan", "#005757", "#00cccc"),
        ORANGE("Orange", "#b84300", "#ffb380"),
        BROWN("Brown", "#5c3317", "#d2a679"),
        TEAL("Teal", "#008080", "#20b2aa"),
        INDIGO("Indigo", "#3f51b5", "#9fa8da"),
        EMERALD("Emerald", "#10b97e", "#0a7a56"),
        CRIMSON("Crimson", "#ef4444", "#b91c1c"),

        ;

        private final String colorName;
        private final String lightHex;
        private final String darkHex;

        HexCode(String colorName, String lightHex, String darkHex) {
            this.colorName = colorName;
            this.lightHex = lightHex;
            this.darkHex = darkHex;
        }

        public String colorName() {
            return colorName;
        }

        public String hex() {
            boolean hasDarkTheme = IntellijUtils.newUiDarkTheme();
            return hasDarkTheme ? darkHex : lightHex;
        }
    }



    public static String hexCodeByRequestType(RequestType requestType) {
        HexCode hexCode = switch (requestType) {
            case GET -> HexCode.GREEN;
            case POST -> HexCode.YELLOW;
            case PUT -> HexCode.BLUE;
            case DELETE -> HexCode.RED;
            case GRAPHQL -> HexCode.PURPLE;
        };

        return hexCode.hex();
    }


}
