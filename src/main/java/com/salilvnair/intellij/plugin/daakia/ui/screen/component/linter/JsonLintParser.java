package com.salilvnair.intellij.plugin.daakia.ui.screen.component.linter;

import org.fife.ui.rsyntaxtextarea.RSyntaxDocument;
import org.fife.ui.rsyntaxtextarea.parser.AbstractParser;
import org.fife.ui.rsyntaxtextarea.parser.DefaultParseResult;
import org.fife.ui.rsyntaxtextarea.parser.DefaultParserNotice;
import org.fife.ui.rsyntaxtextarea.parser.ParseResult;
import org.json.JSONException;
import org.json.JSONTokener;

import javax.swing.text.BadLocationException;

public class JsonLintParser extends AbstractParser {
    @Override
    public ParseResult parse(RSyntaxDocument doc, String style) {
        DefaultParseResult result = new DefaultParseResult(this);
        String text;
        try {
            text = doc.getText(0, doc.getLength());
            // Parse the text with jsonlint
            new JSONTokener(text).nextValue();
        }
        catch (BadLocationException | JSONException e) {
            int line = Integer.parseInt(e.getMessage().split("line ")[1].split("]")[0]);
            result.addNotice(new DefaultParserNotice(this, e.getMessage(), (line -1)));
        }
        return result;
    }
}
