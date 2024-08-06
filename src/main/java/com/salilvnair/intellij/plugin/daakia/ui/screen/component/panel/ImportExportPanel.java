package com.salilvnair.intellij.plugin.daakia.ui.screen.component.panel;

import com.intellij.icons.AllIcons;
import com.salilvnair.intellij.plugin.daakia.ui.core.model.DaakiaStoreRecord;
import com.salilvnair.intellij.plugin.daakia.ui.screen.component.custom.BasicButton;
import com.salilvnair.intellij.plugin.daakia.ui.screen.main.panel.BaseDaakiaPanel;
import com.salilvnair.intellij.plugin.daakia.ui.service.context.DataContext;

import javax.swing.*;
import java.awt.*;

public class ImportExportPanel extends BaseDaakiaPanel<ImportExportPanel> {
    private JPanel buttonPanel;

    public ImportExportPanel(JRootPane rootPane, DataContext dataContext) {
        super(rootPane, dataContext);
        init();
    }

    @Override
    public void initLayout() {
       setLayout(new BorderLayout());
    }

    @Override
    public void initComponents() {
        buttonPanel = new JPanel();
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.Y_AXIS));
        BasicButton importButton = new BasicButton("Import");
        importButton.setIcon(AllIcons.ToolbarDecorator.Import);
        buttonPanel.add(importButton);

        BasicButton exportButton = new BasicButton("Export");
        exportButton.setIcon(AllIcons.ToolbarDecorator.Export);
        buttonPanel.add(exportButton);
    }

    @Override
    public void initStyle() {
        super.initStyle();
    }

    @Override
    public void initChildrenLayout() {
        add(buttonPanel, BorderLayout.CENTER);
    }

    @Override
    public void initListeners() {

    }

    private void showPopupMenu(Component component, int x, int y, DaakiaStoreRecord daakiaStoreRecord) {
        JPopupMenu popupMenu = new JPopupMenu();
        JMenuItem renameMenuItem = new JMenuItem("Rename");
        renameMenuItem.addActionListener(e -> renameSelectedTreeItem(daakiaStoreRecord));
        popupMenu.add(renameMenuItem);
        popupMenu.show(component, x, y);
    }

    private void renameSelectedTreeItem(DaakiaStoreRecord daakiaStoreRecord) {
        globalEventPublisher().onRightClickRenameStoreCollectionNode(daakiaStoreRecord);
    }
}
