package com.salilvnair.intellij.plugin.daakia.ui.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.intellij.icons.AllIcons;
import com.intellij.ide.highlighter.HtmlFileType;
import com.intellij.ide.highlighter.XmlFileType;
import com.intellij.json.JsonFileType;
import com.intellij.openapi.fileTypes.FileType;
import com.salilvnair.intellij.plugin.daakia.ui.screen.component.custom.editor.type.DaakiaJavaScriptFileType;
import javax.swing.*;
import java.util.Map;
import java.util.function.Function;

public class FormatUtils {

    public record FormatterMeta(Function<String, String> formatter, Icon icon) {}

    public static final Map<Class<? extends FileType>, FormatterMeta> formatterMap = Map.of(
            JsonFileType.class, new FormatterMeta(FormatUtils::formatJson, AllIcons.FileTypes.Json),
            XmlFileType.class, new FormatterMeta(FormatUtils::formatXml, AllIcons.FileTypes.Xml),
            HtmlFileType.class, new FormatterMeta(FormatUtils::formatHtml, AllIcons.FileTypes.Html),
            DaakiaJavaScriptFileType.INSTANCE.getClass(), new FormatterMeta(FormatUtils::formatJs, AllIcons.FileTypes.JavaScript)
    );

    public static String formatJson(String text) {
        Object json = null;
        try {
            json = new ObjectMapper().readValue(text, Object.class);
            return new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(json);
        }
        catch (Exception e) {
            return null;
        }
    }

    public static String formatXml(String text) {
        // Use javax.xml.transform.Transformer for XML pretty print
        var factory = javax.xml.transform.TransformerFactory.newInstance();
        try {
            var transformer = factory.newTransformer();
            transformer.setOutputProperty(javax.xml.transform.OutputKeys.INDENT, "yes");
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");

            var source = new javax.xml.transform.stream.StreamSource(new java.io.StringReader(text));
            var result = new java.io.StringWriter();
            transformer.transform(source, new javax.xml.transform.stream.StreamResult(result));
            return result.toString();
        }
        catch (Exception e) {
            return null;
        }
    }

    public static String formatHtml(String text) {
        // Simple indent trick using JSoup
        return org.jsoup.Jsoup.parse(text).outerHtml();
    }

    public static String formatJs(String text) {
        // Dummy for now: you can wire Prettier or UglifyJS via GraalVM
        return text; // Replace with real JS formatter logic
    }


}
