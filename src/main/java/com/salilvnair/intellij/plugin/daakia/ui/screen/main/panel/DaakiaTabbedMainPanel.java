package com.salilvnair.intellij.plugin.daakia.ui.screen.main.panel;

import com.intellij.icons.AllIcons;
import com.intellij.ui.components.JBTabbedPane;
import com.salilvnair.intellij.plugin.daakia.ui.core.event.type.DaakiaEvent;
import com.salilvnair.intellij.plugin.daakia.ui.core.event.type.DaakiaEventType;
import com.salilvnair.intellij.plugin.daakia.ui.core.model.DaakiaBaseStoreData;
import com.salilvnair.intellij.plugin.daakia.ui.screen.component.custom.IconButton;
import com.salilvnair.intellij.plugin.daakia.ui.service.context.DataContext;
import com.salilvnair.intellij.plugin.daakia.ui.service.context.GlobalContext;
import com.salilvnair.intellij.plugin.daakia.ui.service.type.AppDaakiaType;
import com.salilvnair.intellij.plugin.daakia.ui.service.type.DaakiaType;
import com.salilvnair.intellij.plugin.daakia.ui.utils.LabelUtils;
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
        init();
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

        globalSubscriber().subscribe(e -> {
            if(DaakiaEvent.ofAnyType(e, DaakiaEventType.ON_LOAD_SELECTED_HISTORY_DATA, DaakiaEventType.ON_LOAD_SELECTED_STORE_COLLECTION_DATA)) {
                initNewTabBySelectedNode(e);
            }
        });
    }

    private void initNewTabBySelectedNode(EventObject e) {
        DaakiaEvent daakiaEvent = DaakiaEvent.extract(e);
        DataContext selectedNodeDataContext = daakiaEvent.dataContext();
        DataContext newTabDataContext = new DataContext(selectedNodeDataContext.globalContext());
        DaakiaBaseStoreData storeData = DaakiaEvent.ofType(e, DaakiaEventType.ON_LOAD_SELECTED_HISTORY_DATA) ? daakiaEvent.selectedDaakiaHistory() : daakiaEvent.selectedDaakiaStoreRecord();
        String requestType = storeData.getRequestType();
        String displayName = storeData.getDisplayName();
        displayName = displayName == null ? "Untitled" : displayName;
        addNewTab(newTabDataContext, requestType, displayName, true);
        daakiaService(DaakiaType.APP).execute(DaakiaEvent.ofType(e, DaakiaEventType.ON_LOAD_SELECTED_HISTORY_DATA) ? AppDaakiaType.ON_CLICK_HISTORY_NODE : AppDaakiaType.ON_CLICK_STORE_COLLECTION_NODE, newTabDataContext);
    }

    private DaakiaRightVerticalSplitPanel tabContent(GlobalContext globalContext) {
        return new DaakiaRightVerticalSplitPanel(getRootPane(), new DataContext(globalContext));
    }

    private DaakiaRightVerticalSplitPanel tabContent(DataContext dataContext) {
        return new DaakiaRightVerticalSplitPanel(getRootPane(), dataContext);
    }

    private JPanel tabPanel(String requestType, String tabTitle, DaakiaRightVerticalSplitPanel contentPanel) {
        JPanel pnlTab = new JPanel();
        pnlTab.setLayout(new BoxLayout(pnlTab, BoxLayout.X_AXIS));
        pnlTab.setOpaque(false);
        String hexCode = "#10b981";
        if("POST".equals(requestType)) {
            hexCode ="#eab208";
        }
        JLabel lblTitle = new JLabel(LabelUtils.colorText(null, requestType, tabTitle, hexCode));
        IconButton btnClose = initTabCloseButton();
        pnlTab.add(Box.createHorizontalGlue());
        pnlTab.add(lblTitle);
        pnlTab.add(Box.createHorizontalStrut(15));
        pnlTab.add(btnClose);
        pnlTab.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                selectTabByPanel(contentPanel);
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                if (SwingUtilities.isRightMouseButton(e)) {
                    showPopupMenu(e.getComponent(), e.getX(), e.getY(), contentPanel);
                }
            }
        });
        return pnlTab;
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

    private void showPopupMenu(Component component, int x, int y, DaakiaRightVerticalSplitPanel contentPanel) {
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
        JPanel pnlTab = tabPanel(requestType, tabTitle, tabC);
        dynamicDaakiaTabbedPane.insertTab(tabTitle, null, tabC, null, index);
        dynamicDaakiaTabbedPane.setSelectedIndex(index);
        dynamicDaakiaTabbedPane.setTabComponentAt(index, pnlTab);
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
