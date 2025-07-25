import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class PostmanStyleSendButton extends JPanel {
    private final JButton sendButton;
    private final JButton dropButton;
    private final JPopupMenu menu;
    private String selectedLabel = "Send";

    public PostmanStyleSendButton() {
        setLayout(new GridLayout(1, 2));
        setBorder(BorderFactory.createLineBorder(Color.GRAY));

        sendButton = new JButton("Send");
        dropButton = new JButton("▼");

        // Remove individual borders, set font/padding to match
        sendButton.setBorder(BorderFactory.createEmptyBorder(6, 12, 6, 12));
        dropButton.setBorder(BorderFactory.createMatteBorder(0, 1, 0, 0, Color.GRAY));

        sendButton.setFocusPainted(false);
        dropButton.setFocusPainted(false);

        // Unified font
        Font btnFont = new Font("SansSerif", Font.PLAIN, 13);
        sendButton.setFont(btnFont);
        dropButton.setFont(btnFont);

        // Menu
        menu = new JPopupMenu();
        JMenuItem sendItem = new JMenuItem("Send");
        JMenuItem sendAndDownloadItem = new JMenuItem("Send and Download");

        sendItem.addActionListener(e -> setSelected("Send"));
        sendAndDownloadItem.addActionListener(e -> setSelected("Send and Download"));

        menu.add(sendItem);
        menu.add(sendAndDownloadItem);

        // Button actions
        sendButton.addActionListener(e -> {
            if ("Send".equals(selectedLabel)) {
                JOptionPane.showMessageDialog(this, "Sending...");
            } else {
                JOptionPane.showMessageDialog(this, "Sending and downloading...");
            }
        });

        dropButton.addActionListener(e -> {
            menu.show(dropButton, 0, dropButton.getHeight());
        });

        add(sendButton);
        add(dropButton);
    }

    private void setSelected(String label) {
        selectedLabel = label;
        sendButton.setText(label);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame f = new JFrame("Postman Split Button");
            f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            f.setSize(300, 100);
            f.setLayout(new FlowLayout());
            f.add(new PostmanStyleSendButton());
            f.setVisible(true);
        });
    }
}
