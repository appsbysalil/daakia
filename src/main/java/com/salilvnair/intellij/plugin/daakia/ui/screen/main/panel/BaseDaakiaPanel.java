package com.salilvnair.intellij.plugin.daakia.ui.screen.main.panel;

import com.intellij.ui.JBColor;
import com.intellij.ui.components.JBPanel;
import com.salilvnair.intellij.plugin.daakia.ui.core.awt.SwingComponent;
import com.salilvnair.intellij.plugin.daakia.ui.core.event.core.Publisher;
import com.salilvnair.intellij.plugin.daakia.ui.core.event.provider.DaakiaEventPublisher;
import com.salilvnair.intellij.plugin.daakia.ui.core.event.provider.DaakiaGlobalEventPublisher;
import com.salilvnair.intellij.plugin.daakia.ui.service.context.DaakiaContext;
import com.salilvnair.intellij.plugin.daakia.ui.service.context.DataContext;
import com.salilvnair.intellij.plugin.daakia.ui.service.context.SideNavContext;
import com.salilvnair.intellij.plugin.daakia.ui.service.context.UIContext;
import com.salilvnair.intellij.plugin.daakia.ui.service.core.DaakiaService;
import com.salilvnair.intellij.plugin.daakia.ui.service.type.DaakiaType;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.util.EventObject;
import java.util.Random;

public abstract class BaseDaakiaPanel<T extends JBPanel<T>> extends JBPanel<T> implements SwingComponent {
    protected final JRootPane rootPane;
    protected final DataContext dataContext;

    protected boolean debugEnabled = false;

    public BaseDaakiaPanel(JRootPane rootPane, DataContext dataContext) {
        this.rootPane = rootPane;
        this.dataContext = dataContext;
    }


    public void setDebugEnabled(boolean debugEnabled) {
        this.debugEnabled = debugEnabled;
    }

    protected void debugIfApplicable(Object instance) {
        if(debugEnabled) {
            JBColor jbColor = generateRandomJBColor();
            TitledBorder titledBorder = BorderFactory.createTitledBorder(
                    BorderFactory.createLineBorder(jbColor, 2),
                    instance.getClass().getSimpleName(),
                    TitledBorder.CENTER,
                    TitledBorder.DEFAULT_POSITION,
                    new Font("Arial", Font.BOLD, 14),
                    jbColor
            );
            setBorder(titledBorder);
        }
    }

    protected void debugIfApplicable(JPanel panel, String name) {
        if(debugEnabled) {
            JBColor jbColor = generateRandomJBColor();
            TitledBorder titledBorder = BorderFactory.createTitledBorder(
                    BorderFactory.createLineBorder(jbColor, 2),
                    name,
                    TitledBorder.CENTER,
                    TitledBorder.DEFAULT_POSITION,
                    new Font("Arial", Font.BOLD, 14),
                    jbColor
            );
            panel.setBorder(titledBorder);
        }
    }

    protected  JBColor generateRandomJBColor() {
        Random random = new Random();
        // Generate random values for red, green, and blue components
        int red = random.nextInt(256);
        int green = random.nextInt(256);
        int blue = random.nextInt(256);
        // Create and return a new Color object with the random values
        return new JBColor(new Color(red, green, blue), new Color(red, green, blue));
    }


    protected UIContext uiContext() {
        return dataContext.uiContext();
    }

    protected SideNavContext sideNavContext() {
        return dataContext.sideNavContext();
    }

    protected DaakiaContext daakiaContext() {
        return dataContext.daakiaContext();
    }

    protected DaakiaService daakiaService(DaakiaType daakiaType) {
        return dataContext.daakiaService(daakiaType);
    }


    public DaakiaEventPublisher eventPublisher() {
        return dataContext.eventPublisher();
    }

    public Publisher<EventObject> subscriber() {
        return dataContext.subscriber();
    }


    public DaakiaGlobalEventPublisher globalEventPublisher() {
        return dataContext.globalEventPublisher();
    }

    public Publisher<EventObject> globalSubscriber() {
        return dataContext.globalSubscriber();
    }


}
