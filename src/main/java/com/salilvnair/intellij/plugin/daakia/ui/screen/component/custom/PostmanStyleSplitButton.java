import com.intellij.icons.AllIcons;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class PostmanStyleSplitButton extends JPanel {
    private final JButton sendButton;
    private final JButton dropButton;
    private final JPopupMenu menu;
    private String selectedLabel = "Send";

    public PostmanStyleSplitButton() {
        setLayout(new GridBagLayout());
        setBorder(BorderFactory.createLineBorder(Color.GRAY));

        sendButton = new JButton(selectedLabel);
        sendButton.setFont(new Font("SansSerif", Font.PLAIN, 12));
        sendButton.setBorder(BorderFactory.createEmptyBorder(6, 10, 6, 10));
        sendButton.setFocusPainted(false);

        dropButton = new JButton("▼"); // Unicode ▼ arrow
        dropButton.setFocusable(false);
        dropButton.setBorder(BorderFactory.createEmptyBorder(3, 1, 0, 0));
        dropButton.setContentAreaFilled(false);
        dropButton.setPreferredSize(new Dimension(20, sendButton.getPreferredSize().height));

        menu = new JPopupMenu();
        JMenuItem sendItem = new JMenuItem("Send");
        JMenuItem sendAndDownloadItem = new JMenuItem("Send and Download");

        sendItem.addActionListener(e -> setSelected("Send"));
        sendAndDownloadItem.addActionListener(e -> setSelected("Send and Download"));

        menu.add(sendItem);
        menu.add(sendAndDownloadItem);

        sendButton.addActionListener(e -> {
            if ("Send".equals(selectedLabel)) {
                JOptionPane.showMessageDialog(this, "Sending...");
            } else {
                JOptionPane.showMessageDialog(this, "Sending and downloading...");
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

    private void setSelected(String label) {
        selectedLabel = label;
        sendButton.setText(label);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame f = new JFrame("Postman Style Split Button");
            f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            f.setSize(320, 100);
            f.setLayout(new FlowLayout());
            f.add(new PostmanStyleSplitButton());
            f.setVisible(true);
        });
    }
}
