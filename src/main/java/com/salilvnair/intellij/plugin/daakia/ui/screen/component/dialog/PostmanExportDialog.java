package com.salilvnair.intellij.plugin.daakia.ui.screen.component.dialog;

import com.salilvnair.intellij.plugin.daakia.ui.core.icon.DaakiaIcons;

import javax.swing.*;
import java.awt.*;

/**
 * Simple dialog with options to export Postman collections or environments.
 */
public class PostmanExportDialog extends JDialog {
    public PostmanExportDialog(Window parent, Runnable exportCollectionAction, Runnable exportEnvironmentAction) {
        super(parent, "Export Postman", ModalityType.APPLICATION_MODAL);
        setLayout(new FlowLayout(FlowLayout.CENTER, 20, 20));

        JButton collectionButton = new JButton("Export Collection", DaakiaIcons.CollectionIcon);
        JButton environmentButton = new JButton("Export Environment", DaakiaIcons.EnvironmentIcon);

        collectionButton.addActionListener(e -> {
            if (exportCollectionAction != null) {
                exportCollectionAction.run();
            }
            dispose();
        });

        environmentButton.addActionListener(e -> {
            if (exportEnvironmentAction != null) {
                exportEnvironmentAction.run();
            }
            dispose();
        });

        add(collectionButton);
        add(environmentButton);
        pack();
        setLocationRelativeTo(parent);
    }
}