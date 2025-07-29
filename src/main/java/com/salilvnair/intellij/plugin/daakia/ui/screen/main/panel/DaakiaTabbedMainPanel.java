package com.salilvnair.intellij.plugin.daakia.ui.screen.main.panel;

import com.intellij.icons.AllIcons;
import com.intellij.ui.components.JBTabbedPane;
import com.salilvnair.intellij.plugin.daakia.ui.core.event.type.DaakiaEvent;
import com.salilvnair.intellij.plugin.daakia.ui.core.event.type.DaakiaEventType;
import com.salilvnair.intellij.plugin.daakia.ui.core.icon.DaakiaIcons;
import com.salilvnair.intellij.plugin.daakia.ui.core.model.DaakiaBaseStoreData;
import com.salilvnair.intellij.plugin.daakia.ui.screen.component.custom.IconButton;
import com.salilvnair.intellij.plugin.daakia.ui.screen.component.panel.EnvironmentPanel;
import com.salilvnair.intellij.plugin.daakia.ui.service.context.DataContext;
import com.salilvnair.intellij.plugin.daakia.ui.service.context.GlobalContext;
import com.salilvnair.intellij.plugin.daakia.ui.service.type.AppDaakiaType;
import com.salilvnair.intellij.plugin.daakia.ui.service.type.DaakiaType;
import com.salilvnair.intellij.plugin.daakia.ui.service.type.RequestType;
import com.salilvnair.intellij.plugin.daakia.ui.utils.LabelUtils;
import com.intellij.openapi.application.ApplicationManager;
import com.salilvnair.intellij.plugin.daakia.ui.utils.ColorUtils;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.EventObject;

public class DaakiaTabbedMainPanel extends BaseDaakiaPanel<DaakiaTabbedMainPanel> {
    private JTabbedPane dynamicDaakiaTabbedPane;

    public DaakiaTabbedMainPanel(JRootPane rootPane, DataContext dataContext) {
        super(rootPane, dataContext);
        init(this);
    }

    @Override
    public void initLayout() {
        setLayout(new BorderLayout());
    }

    @Override
    public void initComponents() {
        dynamicDaakiaTabbedPane = new JBTabbedPane();
        initAddNewTabButtonOnTab();
        initDefaultTab();
    }


    @Override
    public void initStyle() {
        super.initStyle();
    }


    @Override
    public void initChildrenLayout() {
        add(dynamicDaakiaTabbedPane, BorderLayout.CENTER);
    }

    @Override
    public void initListeners() {
        dynamicDaakiaTabbedPane.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int tabIndex = dynamicDaakiaTabbedPane.indexAtLocation(e.getX(), e.getY());
                if (tabIndex != -1) {
                    String tabTitle = dynamicDaakiaTabbedPane.getTitleAt(tabIndex);
                    if (tabTitle.equals("+")) {
                        addNewTab(dataContext, "GET", null, false);
                    }
                }
            }
            @Override
            public void mousePressed(MouseEvent e) {
                int tabIndex = dynamicDaakiaTabbedPane.indexAtLocation(e.getX(), e.getY());
                if (tabIndex != -1) {
                    String tabTitle = dynamicDaakiaTabbedPane.getTitleAt(tabIndex);
                    if (tabTitle.equals("+")) {
                        addNewTab(dataContext, "GET", null, false);
                    }
                }
            }
        });

        listenGlobal(e -> {
            if(DaakiaEvent.ofAnyType(e, DaakiaEventType.ON_LOAD_SELECTED_HISTORY_DATA, DaakiaEventType.ON_LOAD_SELECTED_STORE_COLLECTION_DATA)) {
                initNewTabBySelectedNode(e);
            }
            else if(DaakiaEvent.ofType(e, DaakiaEventType.ON_OPEN_ENVIRONMENT_MANAGER)) {
                initNewEnvironmentTab(e);
            }
        });
    }

    private void initNewEnvironmentTab(EventObject e) {
        EnvironmentPanel panel = new EnvironmentPanel(getRootPane(), new DataContext(dataContext.project(), dataContext.globalContext()));
        ApplicationManager.getApplication().invokeLater(
                () -> addPanelTab("Environment", DaakiaIcons.EnvironmentIcon, panel)
        );
    }

    private void initNewTabBySelectedNode(EventObject e) {
        DaakiaEvent daakiaEvent = DaakiaEvent.extract(e);
        DataContext selectedNodeDataContext = daakiaEvent.dataContext();
        DataContext newTabDataContext = new DataContext(dataContext.project(), selectedNodeDataContext.globalContext(), dataContext.uiContext());
        DaakiaBaseStoreData storeData = DaakiaEvent.ofType(e, DaakiaEventType.ON_LOAD_SELECTED_HISTORY_DATA) ? daakiaEvent.selectedDaakiaHistory() : daakiaEvent.selectedDaakiaStoreRecord();
        String requestType = storeData.getRequestType();
        String displayName = storeData.getDisplayName();
        displayName = displayName == null ? "Untitled" : displayName;
        String finalDisplayName = displayName;
        ApplicationManager.getApplication().invokeLater(() -> {
            addNewTab(newTabDataContext, requestType, finalDisplayName, true);
            daakiaService(DaakiaType.APP).execute(
                    DaakiaEvent.ofType(e, DaakiaEventType.ON_LOAD_SELECTED_HISTORY_DATA)
                            ? AppDaakiaType.ON_CLICK_HISTORY_NODE
                            : AppDaakiaType.ON_CLICK_STORE_COLLECTION_NODE,
                    newTabDataContext);
        });
    }

    private DaakiaRightVerticalSplitPanel tabContent(GlobalContext globalContext) {
        return new DaakiaRightVerticalSplitPanel(getRootPane(), new DataContext(dataContext.project(), globalContext, dataContext.uiContext()));
    }

    private DaakiaRightVerticalSplitPanel tabContent(DataContext dataContext) {
        return new DaakiaRightVerticalSplitPanel(getRootPane(), dataContext);
    }

    private Object[] tabPanel(String requestType, String tabTitle, JPanel contentPanel) {
        JPanel pnlTab = new JPanel();
        pnlTab.setLayout(new BoxLayout(pnlTab, BoxLayout.X_AXIS));
        pnlTab.setOpaque(false);
        String hexCode = ColorUtils.hexCodeByRequestType(RequestType.findByType(requestType));
        JLabel lblTitle = new JLabel(LabelUtils.coloredText(null, requestType, tabTitle, hexCode));
        dataContext.uiContext().setLabelTitle(lblTitle);
        IconButton btnClose = initTabCloseButton();
        pnlTab.add(Box.createHorizontalGlue());
        pnlTab.add(dataContext.uiContext().labelTitle());
        pnlTab.setToolTipText(tabTitle);
        pnlTab.add(Box.createHorizontalStrut(15));
        pnlTab.add(btnClose);
        pnlTab.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                selectTabByPanel(contentPanel);
                System.out.println("Label ref dataContext.uiContext().labelTitle(): " + System.identityHashCode(dataContext.uiContext().labelTitle()));
                System.out.println("Label ref lblTitle: " + System.identityHashCode(lblTitle));
                System.out.println("Label ref pnlTab.getComponent(1): " + System.identityHashCode(pnlTab.getComponent(1)));
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                if (SwingUtilities.isRightMouseButton(e)) {
                    showPopupMenu(e.getComponent(), e.getX(), e.getY(), contentPanel);
                }
            }
        });
        return new Object[] { pnlTab, lblTitle, tabTitle };
    }

    private void selectTabByPanel(JPanel panel) {
        for (int i = 0; i < dynamicDaakiaTabbedPane.getTabCount(); i++) {
            if (dynamicDaakiaTabbedPane.getComponentAt(i) == panel) {
                dynamicDaakiaTabbedPane.setSelectedIndex(i);
                break;
            }
        }
    }

    private @NotNull IconButton initTabCloseButton() {
        IconButton btnClose = new IconButton(AllIcons.Actions.Close);
        btnClose.addActionListener(e -> {
            JButton button = (JButton)e.getSource();
            closeCurrentTabbedPaneAndRearrange(button);
        });
        return btnClose;
    }

    private void closeCurrentTabbedPaneAndRearrange(JButton button) {
        for(int i = 0; i < dynamicDaakiaTabbedPane.getTabCount(); i++) {
            if(SwingUtilities.isDescendingFrom(button, dynamicDaakiaTabbedPane.getTabComponentAt(i))) {
                dynamicDaakiaTabbedPane.remove(i);
                int newTabIndex = dynamicDaakiaTabbedPane.getSelectedIndex() == i ? i == 0 ? 0 : i - 1 : dynamicDaakiaTabbedPane.getSelectedIndex();
                dynamicDaakiaTabbedPane.setSelectedIndex(newTabIndex);
                break;
            }
        }
    }

    private void showPopupMenu(Component component, int x, int y, JPanel contentPanel) {
        JPopupMenu popupMenu = new JPopupMenu();
        JMenuItem menuItem1 = new JMenuItem("Close All Tabs");
        JMenuItem menuItem2 = new JMenuItem("Close Others");
        menuItem1.addActionListener(e -> {
            while (dynamicDaakiaTabbedPane.getTabCount() > 1) {
                dynamicDaakiaTabbedPane.removeTabAt(0);
            }
        });
        menuItem2.addActionListener(e ->  {
            int clickedTabIndex = -1;
            for (int i = 0; i < dynamicDaakiaTabbedPane.getTabCount(); i++) {
                if (dynamicDaakiaTabbedPane.getComponentAt(i) == contentPanel) {
                    clickedTabIndex = i;
                    break;
                }
            }
            if (clickedTabIndex != -1) {
                for (int i = dynamicDaakiaTabbedPane.getTabCount() - 2; i >= 0; i--) {
                    if (i != clickedTabIndex) {
                        dynamicDaakiaTabbedPane.removeTabAt(i);
                    }
                }
                dynamicDaakiaTabbedPane.setSelectedIndex(0);
            }
        });
        popupMenu.add(menuItem1);
        popupMenu.add(menuItem2);
        popupMenu.show(component, x, y);
    }

    public void addNewTab(DataContext dataContext, String requestType, String tabTitle, boolean onSideNavAction) {
        DaakiaRightVerticalSplitPanel tabC;
        if(onSideNavAction) {
            tabC = tabContent(dataContext);
        }
        else {
            tabC = tabContent(dataContext.globalContext());
        }
        int index = dynamicDaakiaTabbedPane.getTabCount() - 1;
        tabTitle = tabTitle == null ? "Untitled" : tabTitle;
        JPanel pnlTab = (JPanel) tabPanel(requestType, tabTitle, tabC)[0];
        dataContext.uiContext().setTabTitle(tabTitle);
        dataContext.uiContext().setSelectedPnlTab(pnlTab);
        dynamicDaakiaTabbedPane.insertTab(tabTitle, null, tabC, null, index);
        dynamicDaakiaTabbedPane.setSelectedIndex(index);
        dynamicDaakiaTabbedPane.setTabComponentAt(index, pnlTab);
        dataContext.uiContext().setDynamicDaakiaTabbedPane(dynamicDaakiaTabbedPane);
    }

    public void addPanelTab(String tabTitle, Icon tabIcon, JPanel panel) {
        int index = dynamicDaakiaTabbedPane.getTabCount() - 1;
        JPanel pnlTab = (JPanel) tabPanel("", tabTitle, panel)[0];
        dynamicDaakiaTabbedPane.insertTab(tabTitle, tabIcon, panel, null, index);
        dynamicDaakiaTabbedPane.setSelectedIndex(index);
        dynamicDaakiaTabbedPane.setTabComponentAt(index, pnlTab);
    }
    public void addPanelTab(String tabTitle, JPanel panel) {
        addPanelTab(tabTitle, null, panel);
    }

    private void initDefaultTab() {
        addNewTab(dataContext, "GET", null, false);
    }

    private void initAddNewTabButtonOnTab() {
        dynamicDaakiaTabbedPane.addTab("+", new JPanel());
        JPanel pnlTab = addNewTabButtonPanel();
        int index = dynamicDaakiaTabbedPane.getTabCount() - 1;
        dynamicDaakiaTabbedPane.setTabComponentAt(index, pnlTab);
    }

    private @NotNull JPanel addNewTabButtonPanel() {
        JPanel pnlTab = new JPanel(new BorderLayout());
        pnlTab.setOpaque(false);
        JLabel lblTitle = new JLabel("+");
        pnlTab.add(lblTitle);
        lblTitle.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                addNewTab(dataContext, "GET", null, false);
            }
        });
        return pnlTab;
    }
}
