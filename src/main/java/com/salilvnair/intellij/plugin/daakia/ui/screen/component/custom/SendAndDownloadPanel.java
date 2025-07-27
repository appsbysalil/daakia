package com.salilvnair.intellij.plugin.daakia.ui.screen.component.custom;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

public class SendAndDownloadPanel extends JPanel {
    private final JButton sendButton;
    private final JButton dropButton;
    private final JPopupMenu menu;
    private String selectedLabel = "Send";
    private enum SelectedType {
        SEND("Send"),
        SEND_AND_DOWNLOAD("Send and Download");

        private final String label;

        SelectedType(String label) {
            this.label = label;
        }

        public String label() {
            return label;
        }
    }
    private SelectedType selectedType;

    private ActionListener onSendActionListener;
    private ActionListener onSendAndDownloadActionListener;

    public SendAndDownloadPanel() {
        setLayout(new GridBagLayout());
        setBorder(BorderFactory.createLineBorder(Color.GRAY));

        sendButton = new JButton(selectedLabel);
        sendButton.setFont(new Font("SansSerif", Font.PLAIN, 12));
        sendButton.setBorder(BorderFactory.createEmptyBorder(6, 10, 6, 10));
        sendButton.setFocusPainted(false);

        dropButton = new JButton("▼");
        dropButton.setFocusable(false);
        dropButton.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 1, 0, 0, Color.GRAY),
                BorderFactory.createEmptyBorder(4, 0, 0, 0)
        ));
        dropButton.setContentAreaFilled(false);
        dropButton.setPreferredSize(new Dimension(20, sendButton.getPreferredSize().height));
        dropButton.setFont(new Font("SansSerif", Font.PLAIN, 10));

        menu = new JPopupMenu();
        JMenuItem sendItem = new JMenuItem(SelectedType.SEND.label());
        JMenuItem sendAndDownloadItem = new JMenuItem(SelectedType.SEND_AND_DOWNLOAD.label());

        sendItem.addActionListener(e -> setSelected(SelectedType.SEND));
        sendAndDownloadItem.addActionListener(e -> setSelected(SelectedType.SEND_AND_DOWNLOAD));

        menu.add(sendItem);
        menu.add(sendAndDownloadItem);

        sendButton.addActionListener(e -> {
            if (onSendActionListener != null) {
                onSendActionListener.actionPerformed(e);
            }
            else if (onSendAndDownloadActionListener != null) {
                onSendAndDownloadActionListener.actionPerformed(e);
            }
        });

        dropButton.addActionListener(e -> menu.show(dropButton, 0, dropButton.getHeight()));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(0, 0, 0, 0);

        gbc.gridx = 0;
        gbc.weightx = 0.95;
        add(sendButton, gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.05;
        add(dropButton, gbc);
    }

    public void addSendActionListener(ActionListener listener) {
        this.onSendActionListener = listener;
    }

    public void addSendAndDownloadActionListener(ActionListener listener) {
        this.onSendAndDownloadActionListener = listener;
    }

    public String selectedLabel() {
        return selectedLabel;
    }

    public void setButtonsEnabled(boolean enabled) {
        sendButton.setEnabled(enabled);
        dropButton.setEnabled(enabled);
    }

    public JButton sendButton() {
        return sendButton;
    }

    public JButton dropButton() {
        return dropButton;
    }

    private void setSelected(SelectedType selectedType) {
        selectedLabel = selectedType.label();
        sendButton.setText(selectedType.label());
    }
}
