package com.salilvnair.intellij.plugin.daakia.ui.screen.component.dialog;

import com.salilvnair.intellij.plugin.daakia.ui.core.icon.DaakiaIcons;

import javax.swing.*;
import java.awt.*;

/**
 * Simple dialog with options to import Postman collections or environments.
 */
public class PostmanImportDialog extends JDialog {
    public PostmanImportDialog(Window parent, Runnable importCollectionAction, Runnable importEnvironmentAction) {
        super(parent, "Import Postman", ModalityType.APPLICATION_MODAL);
        setLayout(new FlowLayout(FlowLayout.CENTER, 20, 20));

        JButton collectionButton = new JButton("Import Collection", DaakiaIcons.CollectionIcon);
        JButton environmentButton = new JButton("Import Environments", DaakiaIcons.EnvironmentIcon);

        collectionButton.addActionListener(e -> {
            if (importCollectionAction != null) {
                importCollectionAction.run();
            }
            dispose();
        });

        environmentButton.addActionListener(e -> {
            if (importEnvironmentAction != null) {
                importEnvironmentAction.run();
            }
            dispose();
        });

        add(collectionButton);
        add(environmentButton);
        pack();
        setLocationRelativeTo(parent);
    }
}
