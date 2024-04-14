package com.salilvnair.intellij.plugin.daakia.ui.core.event.type;

import java.util.EventObject;

/**
 * @author Salil V Nair
 */
public class EchoEvent extends EventObject {
    private String echo;
    public EchoEvent(Object source, String echo) {
        super(source);
        this.echo = echo;
    }

    public String echo() {
        return echo;
    }

    public void setEcho(String echo) {
        this.echo = echo;
    }
}
