package com.salilvnair.intellij.plugin.daakia.ui.screen.main.panel;

import com.intellij.icons.AllIcons;
import com.intellij.ui.JBColor;
import com.intellij.util.ui.JBUI;
import com.salilvnair.intellij.plugin.daakia.ui.core.event.type.DaakiaEvent;
import com.salilvnair.intellij.plugin.daakia.ui.core.event.type.DaakiaEventType;
import com.salilvnair.intellij.plugin.daakia.ui.screen.component.custom.IconButton;
import com.salilvnair.intellij.plugin.daakia.ui.service.context.DataContext;
import com.salilvnair.intellij.plugin.daakia.ui.utils.DaakiaUtils;
import com.salilvnair.intellij.plugin.daakia.ui.utils.IconUtils;

import javax.swing.*;
import javax.swing.plaf.basic.BasicSplitPaneUI;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class DaakiaRightVerticalSplitPanel extends BaseDaakiaPanel<DaakiaRightVerticalSplitPanel> {

    private DaakiaRightVerticalSplitLeftPanel leftPanel;
    private DaakiaRightVerticalSplitRightPanel rightPanel;
    private JSplitPane leftRightSplitPane;
    private JPanel rightPanelContainer;

    // Loader overlay that replaces right panel content while a request runs
    private JPanel loaderPanel;
    private JProgressBar progressBar;
    private IconButton stopButton;

    public DaakiaRightVerticalSplitPanel(JRootPane rootPane, DataContext dataContext) {
        super(rootPane, dataContext);
        init(this);
    }

    @Override
    public void initLayout() {
        setLayout(new BorderLayout());
    }

    @Override
    public void initComponents() {
        leftPanel = new DaakiaRightVerticalSplitLeftPanel(rootPane, dataContext);
        rightPanel = new DaakiaRightVerticalSplitRightPanel(rootPane, dataContext);

        // --- LOADER OVERLAY (full-size, centered stop button) ---
        loaderPanel = new JPanel(new GridBagLayout());
        loaderPanel.setOpaque(false);
        loaderPanel.setVisible(false);

        progressBar = new JProgressBar();
        progressBar.setIndeterminate(true);

        Icon closeIcon = IconUtils.scaleIcon(AllIcons.Process.StopHovered, 15, 15); // or AllIcons.Actions.Close
        stopButton = new IconButton(closeIcon);

        // Make the stop button hitbox exactly the icon bounds
        int iw = closeIcon.getIconWidth();
        int ih = closeIcon.getIconHeight();
        Dimension tight = new Dimension(iw, ih);
        stopButton.setPreferredSize(tight);
        stopButton.setMinimumSize(tight);
        stopButton.setMaximumSize(tight);
        stopButton.setBorder(BorderFactory.createEmptyBorder());
        stopButton.setMargin(JBUI.emptyInsets());
        stopButton.setContentAreaFilled(false);
        stopButton.setFocusPainted(false);
        stopButton.setOpaque(false);
        stopButton.setForeground(JBColor.RED);

        // Layout: keep progress bar near top, button exactly in center
        // Progress bar at top, full width
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0; gbc.gridy = 0;
        gbc.weightx = 1.0; gbc.weighty = 0;
        gbc.anchor = GridBagConstraints.NORTH;
        gbc.fill = GridBagConstraints.HORIZONTAL; // stretch horizontally
        gbc.insets = JBUI.insets(16, 16, 0, 16); // optional side padding
        loaderPanel.add(progressBar, gbc);

        // Filler to push center area
        gbc = new GridBagConstraints();
        gbc.gridx = 0; gbc.gridy = 1;
        gbc.weightx = 1; gbc.weighty = 1;
        gbc.fill = GridBagConstraints.BOTH;
        loaderPanel.add(Box.createGlue(), gbc);

        // Centered stop button
        gbc = new GridBagConstraints();
        gbc.gridx = 0; gbc.gridy = 2;
        gbc.weightx = 1; gbc.weighty = 0;
        gbc.anchor = GridBagConstraints.CENTER;
        loaderPanel.add(stopButton, gbc);

        // Filler to keep button centered vertically
        gbc = new GridBagConstraints();
        gbc.gridx = 0; gbc.gridy = 3;
        gbc.weightx = 1; gbc.weighty = 1;
        gbc.fill = GridBagConstraints.BOTH;
        loaderPanel.add(Box.createGlue(), gbc);

        // Swallow clicks on the loader background so only the button acts
        loaderPanel.addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) { /* no-op */ }
        });

        // --- RIGHT SIDE uses CardLayout to swap between CONTENT and LOADER ---
        rightPanelContainer = new JPanel(new CardLayout());
        rightPanelContainer.add(rightPanel, "CONTENT");
        rightPanelContainer.add(loaderPanel, "LOADER");

        // --- SPLIT PANE ---
        leftRightSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, leftPanel, rightPanelContainer);
        leftRightSplitPane.setDividerSize(2);

        // Expose to UI context
        uiContext().setProgressBar(progressBar);
        uiContext().setStopButton(stopButton);
    }

    @Override
    public void initStyle() {
        leftRightSplitPane.setUI(DaakiaUtils.thinDivider());
        leftRightSplitPane.setBorder(null);
    }

    @Override
    public void initChildrenLayout() {
        add(leftRightSplitPane, BorderLayout.CENTER);
    }

    @Override
    public void initListeners() {
        initDividerDoubleClickListener();

        // Swap to loader when send is clicked; swap back after request completes or stop is clicked
        listen(event -> {
            if (DaakiaEvent.ofType(event, DaakiaEventType.ON_CLICK_SEND)) {
                com.intellij.openapi.application.ApplicationManager.getApplication().invokeLater(() -> ((CardLayout) rightPanelContainer.getLayout()).show(rightPanelContainer, "LOADER"));
            }
            else if (DaakiaEvent.ofAnyType(event, DaakiaEventType.AFTER_REST_EXCHANGE, DaakiaEventType.ON_CLICK_STOP)) {
                com.intellij.openapi.application.ApplicationManager.getApplication().invokeLater(() -> ((CardLayout) rightPanelContainer.getLayout()).show(rightPanelContainer, "CONTENT"));
            }
        });

        stopButton.addActionListener(e -> {
            java.util.concurrent.Future<?> future = uiContext().activeRequest();
            if (future != null) {
                future.cancel(true);
                uiContext().setActiveRequest(null);
            }
            eventPublisher().onClickStop();
            globalEventPublisher().onClickStop();
        });

        listenGlobal(event -> {
            if (DaakiaEvent.ofType(event, DaakiaEventType.ON_CLICK_REQUEST_PANEL_VISIBILITY_TOGGLER)) {
                int width = leftRightSplitPane.getWidth();
                if (leftRightSplitPane.getDividerLocation() <= 30) {
                    leftRightSplitPane.setDividerLocation(width / 2);
                }
                else {
                    leftRightSplitPane.setDividerLocation(30);
                }
            }
            else if (DaakiaEvent.ofType(event, DaakiaEventType.ON_CLICK_RESPONSE_PANEL_VISIBILITY_TOGGLER)) {
                int width = leftRightSplitPane.getWidth();
                if (width - leftRightSplitPane.getDividerLocation() <= 30) {
                    leftRightSplitPane.setDividerLocation(width / 2);
                }
                else {
                    leftRightSplitPane.setDividerLocation(width - 30);
                }
            }
        });
    }

    private void initDividerDoubleClickListener() {
        Component divider = ((BasicSplitPaneUI) leftRightSplitPane.getUI()).getDivider();
        divider.addMouseListener(new MouseAdapter() {
            int toggleState = 0;
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    SwingUtilities.invokeLater(() -> {
                        int width = leftRightSplitPane.getWidth();
                        switch (toggleState) {
                            case 0 -> { leftRightSplitPane.setDividerLocation(0);        toggleState = 1; }
                            case 1 -> { leftRightSplitPane.setDividerLocation(width / 2); toggleState = 2; }
                            case 2 -> { leftRightSplitPane.setDividerLocation(width);     toggleState = 0; }
                        }
                    });
                }
            }
        });
    }
}
