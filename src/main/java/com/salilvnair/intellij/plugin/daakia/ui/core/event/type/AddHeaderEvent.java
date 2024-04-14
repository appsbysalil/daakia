package com.salilvnair.intellij.plugin.daakia.ui.core.event.type;

import java.util.EventObject;

/**
 * @author Salil V Nair
 */
public class AddHeaderEvent extends EventObject {
    private boolean addHeaderPressed;
    public AddHeaderEvent(Object source, boolean addHeaderPressed) {
        super(source);
        this.addHeaderPressed = addHeaderPressed;
    }

    public boolean addHeaderPressed() {
        return addHeaderPressed;
    }

    public void setAddHeaderPressed(boolean addHeaderPressed) {
        this.addHeaderPressed = addHeaderPressed;
    }
}
