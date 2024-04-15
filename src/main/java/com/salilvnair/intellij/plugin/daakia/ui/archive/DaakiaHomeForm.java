package com.salilvnair.intellij.plugin.daakia.ui.archive;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.intellij.icons.AllIcons;
import com.intellij.icons.ExpUiIcons;
import com.intellij.ide.ui.laf.darcula.ui.DarculaButtonUI;
import com.intellij.openapi.application.PathManager;
import com.intellij.openapi.ui.ComboBox;
import com.intellij.ui.JBColor;
import com.intellij.ui.components.JBList;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.ui.components.JBTabbedPane;
import com.intellij.ui.treeStructure.Tree;
import com.salilvnair.intellij.plugin.daakia.ui.archive.model.DaakiaHistory;
import com.salilvnair.intellij.plugin.daakia.ui.archive.util.DaakiaIcons;
import com.salilvnair.intellij.plugin.daakia.ui.archive.util.TextInputField;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rsyntaxtextarea.SyntaxConstants;
import org.fife.ui.rtextarea.RTextScrollPane;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import javax.swing.*;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.*;
import java.util.stream.Collectors;


public class DaakiaHomeForm extends JFrame {
    private JPanel mainPanel;
    private MultiValueMap<String, String> requestHeaders = new LinkedMultiValueMap<>();
    private DefaultListModel<DaakiaHistory> historyListModel;
    private DefaultTreeModel treeModel;
    String dbCollection;
    ComboBox<String> requestTypeComboBox;
    JTextField urlTextField;
    RSyntaxTextArea requestTextArea;
    JBList<DaakiaHistory> historyList;
    Map<String, List<DaakiaHistory>> historyData = new TreeMap<>();
    Map<String, List<String>> data = new TreeMap<>();
    Tree historyTree;
    Map<String, List<TextInputField>> headerTextFields = new LinkedHashMap<>();
    JPanel headersPanel;
    JBTabbedPane tabbedPane;
    JPanel headerScrollPanel;

    public DaakiaHomeForm() {
        init();
        setContentPane(mainPanel);
    }

    public JPanel getMainPanel() {
        return mainPanel;
    }


    public void init() {
        String currentDirectory = PathManager.getPluginsPath();
        System.out.println("Current location: " + currentDirectory);

        // Load the SQLite JDBC driver
        try {
            Class.forName("org.sqlite.JDBC");
        }
        catch (ClassNotFoundException e) {
            e.printStackTrace();
            System.out.println("Something went wrong");
        }

        dbCollection = currentDirectory + "/daakia_history.db";


        setTitle("Daakia");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 600);

        JPanel rightPanel = new JPanel(new BorderLayout());

        // Side panel for history using JSplitPane
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setDividerLocation(300); // Initial divider location
        mainPanel = new JPanel(new BorderLayout());
        mainPanel.add(splitPane, BorderLayout.WEST);

        // Side panel for history
        JPanel leftPanel = new JPanel(new BorderLayout());
        leftPanel.setPreferredSize(new Dimension(250, 0));
        leftPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

//        JLabel historyLabel = new JLabel("History");
//        historyLabel.setFont(new Font("Arial", Font.BOLD, 16));
//        historyLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
//        leftPanel.add(historyLabel, BorderLayout.NORTH);

        historyListModel = new DefaultListModel<>();
        historyList = new JBList<>(historyListModel);

        JPanel historyTreePanel = initHistoryJTree();
        int leftPanelDividerSize = splitPane.getDividerSize();
        if(historyData.isEmpty()) {
            leftPanel.setVisible(false);
            splitPane.setDividerSize(0);
        }
        JBTabbedPane sideNavPane = new JBTabbedPane(JBTabbedPane.LEFT);
        sideNavPane.addTab(null, ExpUiIcons.General.History, historyTreePanel);
        sideNavPane.addTab(null, DaakiaIcons.CollectionIcon, dynamicTree(this));
        sideNavPane.setToolTipTextAt(0, "History");
        sideNavPane.setToolTipTextAt(1, "Collections");

        leftPanel.add(sideNavPane, BorderLayout.CENTER);

//        mainPanel.add(leftPanel, BorderLayout.WEST);

        // Top panel for request type, URL, and headers
        JPanel topPanel = new JPanel();
        topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.PAGE_AXIS));

        JPanel requestPanel = new JPanel(new FlowLayout(FlowLayout.LEADING, 10, 10));
        requestTypeComboBox = new ComboBox<>(new String[]{"GET", "POST", "PUT", "DELETE"});
        urlTextField = new JTextField();
        urlTextField.setPreferredSize(new Dimension(600, 25));
        JButton sendButton = new JButton("Send");
        sendButton.setIcon(AllIcons.Actions.Execute);

        JButton saveButton = new JButton("Save");
        saveButton.setIcon(DaakiaIcons.SaveIcon);

        saveButton.addActionListener(actionEvent -> {
            showDialog(this);
        });

        requestPanel.add(new JLabel("Request Type:"));
        requestPanel.add(requestTypeComboBox);
        requestPanel.add(new JLabel("URL:"));
        requestPanel.add(urlTextField);
        requestPanel.add(sendButton);
        requestPanel.add(saveButton);
        requestPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        topPanel.add(requestPanel);

        JPanel bodyPanel = new JPanel(new BorderLayout());

        // Panel for headers
        headersPanel = new JPanel();
        headersPanel.setLayout(new BoxLayout(headersPanel, BoxLayout.Y_AXIS));
        headersPanel.setBorder(BorderFactory.createTitledBorder("Header List"));

        JPanel addButtonContainerPanel = new JPanel(new FlowLayout(FlowLayout.TRAILING, 0, 0));
        JButton addHeaderButton = new JButton("Add Header");
        addHeaderButton.setIcon(AllIcons.Actions.AddList);
        addButtonContainerPanel.add(addHeaderButton);
        addButtonContainerPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        topPanel.add(addButtonContainerPanel);

        headerScrollPanel = new JPanel();
        headerScrollPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));
//        headerScrollPanel.add(addButtonContainerPanel);


//        headerScrollPane.setVisible(false);

//        headerScrollPane.setLayout();

        tabbedPane = new JBTabbedPane();

        // Create panels for each tab

        // Add tabs to the tabbed pane

//        topPanel.add(tabbedPane);

        rightPanel.add(topPanel, BorderLayout.NORTH);

        // Center panel for request parameters and response
        requestTextArea = new RSyntaxTextArea();
        requestTextArea.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_JSON);
        requestTextArea.setCodeFoldingEnabled(true);
        RTextScrollPane requestScrollPane = new RTextScrollPane(requestTextArea);
        requestScrollPane.setIconRowHeaderEnabled(true); // Enable icon row header for folding icons
        requestScrollPane.setFoldIndicatorEnabled(true); // Enable fold indicators
        requestScrollPane.setViewportView(requestTextArea);

        RSyntaxTextArea responseTextArea = new RSyntaxTextArea();
        responseTextArea.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_JSON);
        RTextScrollPane responseScrollPane = new RTextScrollPane(responseTextArea);
        responseScrollPane.setIconRowHeaderEnabled(true);
        responseScrollPane.setFoldIndicatorEnabled(true);
        responseScrollPane.setViewportView(responseTextArea);

        JSplitPane centerSplitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT,
                requestScrollPane, responseScrollPane);
        centerSplitPane.setResizeWeight(0.5);

        bodyPanel.add(centerSplitPane, BorderLayout.CENTER);

        // Bottom panel for status
        JPanel bottomPanel = new JPanel(new BorderLayout());
        JLabel statusLabel = new JLabel("Status:");
        bottomPanel.add(statusLabel, BorderLayout.WEST);

        bodyPanel.add(bottomPanel, BorderLayout.SOUTH);

//        rightPanel.add(bodyPanel);
        headerScrollPanel.add(headersPanel);
        JBScrollPane headerScrollPane = new JBScrollPane(headerScrollPanel);
//
//        // Set the preferred size of the scroll pane
        headerScrollPane.setPreferredSize(new Dimension(220, 300));

//        headerButtonsPanel.add(addButtonContainerPanel);

//        headerButtonsPanel.add(headerScrollPanel);
        if(headerTextFields.isEmpty()) {
            headerScrollPanel.setVisible(false);
        }
        tabbedPane.addTab("Request Headers", AllIcons.Actions.Minimap, headerScrollPane);
        tabbedPane.addTab("Body", AllIcons.Json.Object, bodyPanel);
        tabbedPane.addTab("dynamicTree", dynamicTree(this));


        rightPanel.add(tabbedPane);

        rightPanel.setMinimumSize(new Dimension(1100, 0));
        splitPane.setRightComponent(rightPanel);
        splitPane.setLeftComponent(leftPanel);
        mainPanel.add(splitPane);

        // ActionListener for adding header
        addHeaderButton.addActionListener(e -> {
            tabbedPane.setSelectedIndex(0);
            createHeader(null, null);
        });

        sendButton.addActionListener(e -> {
            System.out.println(requestTypeComboBox.getSelectedItem());
            System.out.println(urlTextField.getText());
            System.out.println(requestTextArea.getText());
            String requestType = (String) requestTypeComboBox.getSelectedItem();
            String url = urlTextField.getText();
            String requestBody = requestTextArea.getText();
            leftPanel.setVisible(true);
            splitPane.setDividerSize(leftPanelDividerSize);
            splitPane.setDividerLocation(300);
            prepareRequestHeaders();
            System.out.println(requestHeaders);

            RestTemplate restTemplate = new RestTemplate();
            HttpEntity<?> entity = new HttpEntity<>(requestBody, requestHeaders);
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.valueOf(requestType), entity, String.class);

            System.out.println(response.getBody());
            responseTextArea.setCodeFoldingEnabled(true);
            responseTextArea.setText(response.getBody());

            // Add request to history

            DaakiaHistory daakiaHistory = new DaakiaHistory();
            daakiaHistory.setRequestType(requestType);
            daakiaHistory.setUrl(url);
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                String headers = objectMapper.writeValueAsString(requestHeaders);
                daakiaHistory.setHeaders(headers);
            }
            catch (JsonProcessingException ex) {

            }
            daakiaHistory.setRequestBody(requestBody);
            daakiaHistory.setCreatedDate(todayAsString());

            boolean saved = saveHistoryToDatabase(daakiaHistory);
            if (saved) {
//                historyListModel.addElement(daakiaHistory);
                addHistoryData(todayAsString(), daakiaHistory);
                expandAllNodes(historyTree);
            }
        });

        historyList.addMouseListener(new MouseAdapter() {

            @Override
            public void mouseReleased(MouseEvent e) {
                if (SwingUtilities.isRightMouseButton(e)) {
                    int index = historyList.locationToIndex(e.getPoint());
                    if (index != -1) {
                        historyList.setSelectedIndex(index);
                        showPopupMenu(e.getComponent(), e.getX(), e.getY());
                    }
                }
            }

            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 1) { // Single click
                    int index = historyList.locationToIndex(e.getPoint());
                    if (index != -1) {
                        DaakiaHistory daakiaHistory = historyList.getModel().getElementAt(index);
                        // Call your method with the selected item
                        loadHistoryDataIntoUI(daakiaHistory);
                    }
                }
                else if (e.getClickCount() == 2) {
                    renameSelectedItem();
                }
            }
        });

        // Create SQLite database table for history
        createHistoryTable();

        // Load history from database
//        loadHistoryFromDatabase();
    }

    private void loadHeadersFromDb(MultiValueMap<String, String> requestHeaderMap) {
        if(requestHeaderMap!=null && !requestHeaderMap.isEmpty()) {
            headersPanel.removeAll();
            requestHeaderMap.forEach((key, value) -> {
                createHeader(key, value.get(0));
            });
        }
    }

    private void createHeader(String headerKey, String headerValue) {
        String rowId = UUID.randomUUID().toString();
        TextInputField headerKeyField = new TextInputField("Header "+(headerTextFields.size() + 1));
        headerKeyField.setPreferredSize(new Dimension(350, 25));
        headerKeyField.setText(headerKey);
        TextInputField headerValueField = new TextInputField("Value "+(headerTextFields.size() + 1));
        headerValueField.setPreferredSize(new Dimension(600, 25));
        headerValueField.setText(headerValue);
        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        headerPanel.add(headerKeyField);
        headerPanel.add(headerValueField);
        JButton deleteHeaderButton = new JButton("Delete");
        headerPanel.add(deleteHeaderButton);
        headersPanel.add(headerPanel);
        headersPanel.revalidate();
        headersPanel.repaint();
        List<TextInputField> headerKeyValFields = List.of(headerKeyField, headerValueField);
        headerTextFields.put(rowId, headerKeyValFields);
        headerScrollPanel.setVisible(true);
//            deleteHeaderButton.setEnabled(!(headerKeyField.getText() != null && headerKeyField.getText().isEmpty()));
        // ActionListener for deleting header
        deleteHeaderButton.addActionListener(e1 -> {
            headersPanel.remove(headerPanel);
            requestHeaders.remove(headerKeyField.getText());
            headerTextFields.remove(rowId);
            headersPanel.revalidate();
            headersPanel.repaint();
            headerScrollPanel.revalidate();
            headerScrollPanel.repaint();
            if(headerTextFields.isEmpty()) {
                headerScrollPanel.setVisible(false);
            }
        });
    }

    private void prepareRequestHeaders() {
        headerTextFields.forEach((k, v) -> {
            requestHeaders.add(v.get(0).getText(), v.get(1).getText());
        });
    }

    private void loadHistoryDataIntoUI(DaakiaHistory daakiaHistory) {
//        System.out.println(daakiaHistory);
        requestTypeComboBox.setSelectedItem(daakiaHistory.getRequestType());
        urlTextField.setText(daakiaHistory.getUrl());
        requestTextArea.setText(daakiaHistory.getRequestBody());
        MultiValueMap<String, String> requestHeaderMap = jsonStringToMultivaluedMap(daakiaHistory.getHeaders());
        loadHeadersFromDb(requestHeaderMap);
    }

    private void createHistoryTable() {
        try (Connection conn = DriverManager.getConnection("jdbc:sqlite:"+dbCollection);
             Statement stmt = conn.createStatement()) {
            stmt.execute("CREATE TABLE IF NOT EXISTS history (id INTEGER PRIMARY KEY AUTOINCREMENT, createdDate TEXT, displayName TEXT, requestType TEXT, url TEXT, headers TEXT, request TEXT)");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void loadHistoryFromDatabase() {
//        historyListModel.clear();
//        historyData.clear();
        List<DaakiaHistory> dbData = new ArrayList<>();
        try (Connection conn = DriverManager.getConnection("jdbc:sqlite:"+dbCollection);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT id, createdDate, displayName, requestType, url, headers, request FROM history ORDER BY id DESC")) {
            while (rs.next()) {
                DaakiaHistory daakiaHistory = new DaakiaHistory();
                int id = rs.getInt("id");
                String createdDate = rs.getString("createdDate");
                String displayName = rs.getString("displayName");
                String requestType = rs.getString("requestType");
                String url = rs.getString("url");
                String headers = rs.getString("headers");
                String requestBody = rs.getString("request");
                daakiaHistory.setId(id);
                daakiaHistory.setCreatedDate(createdDate);
                daakiaHistory.setDisplayName(displayName);
                daakiaHistory.setRequestType(requestType);
                daakiaHistory.setUrl(url);
                daakiaHistory.setHeaders(headers);
                daakiaHistory.setRequestBody(requestBody);
//                historyListModel.addElement(daakiaHistory);
                dbData.add(daakiaHistory);
            }
            historyData = dbData.stream().collect(Collectors.toMap(DaakiaHistory::getCreatedDate, e ->  {
                List<DaakiaHistory> daakiaHistories = new ArrayList<>();
                daakiaHistories.add(e);
                return daakiaHistories;
            }, (a,b) -> {
                b.addAll(a);
                return b;
            },TreeMap::new));

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private boolean saveHistoryToDatabase(DaakiaHistory daakiaHistory) {
        try (Connection conn = DriverManager.getConnection("jdbc:sqlite:"+dbCollection);
             PreparedStatement pstmt = conn.prepareStatement("INSERT INTO history( createdDate, displayName, requestType, url, headers, request) VALUES(?,?,?,?,?,?)")) {
            pstmt.setString(1, daakiaHistory.getCreatedDate());
            pstmt.setString(2, daakiaHistory.getDisplayName());
            pstmt.setString(3, daakiaHistory.getRequestType());
            pstmt.setString(4, daakiaHistory.getUrl());
            pstmt.setString(5, daakiaHistory.getHeaders());
            pstmt.setString(6, daakiaHistory.getRequestBody());
            int affectedRows = pstmt.executeUpdate();
            // Check if the insert was successful and get the last inserted ID
            if (affectedRows > 0) {
                try (ResultSet rs = pstmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        int lastInsertedId = rs.getInt(1);
                        System.out.println("Last inserted ID: " + lastInsertedId);
                        daakiaHistory.setId(lastInsertedId);
                    }
                }
            }
            else {
                System.out.println("Insert failed.");
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        return daakiaHistory.getId() != null;
    }

    private boolean updateHistoryTable(DaakiaHistory daakiaHistory) {
        String updateSql = "UPDATE history SET displayName = ?, requestType = ?, url = ?, headers = ?, request = ? WHERE id = ?";

        try (Connection conn = DriverManager.getConnection("jdbc:sqlite:"+dbCollection);
             PreparedStatement pstmt = conn.prepareStatement(updateSql)) {
            pstmt.setString(1, daakiaHistory.getDisplayName());
            pstmt.setString(2, daakiaHistory.getRequestType());
            pstmt.setString(3, daakiaHistory.getUrl());
            pstmt.setString(4, daakiaHistory.getHeaders());
            pstmt.setString(5, daakiaHistory.getRequestBody());
            pstmt.setInt(6, daakiaHistory.getId());
            int affectedRows = pstmt.executeUpdate();
            // Check if the insert was successful and get the last inserted ID
            if (affectedRows > 0) {
                System.out.println("Update successful.");
                return true;
            }
            else {
                System.out.println("Insert failed.");
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Method to show the popup menu for renaming
    private void showPopupMenu(Component component, int x, int y) {
        JPopupMenu popupMenu = new JPopupMenu();
        JMenuItem renameMenuItem = new JMenuItem("Rename");
        renameMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                renameSelectedItem();
            }
        });
        popupMenu.add(renameMenuItem);
        popupMenu.show(component, x, y);
    }

    private void showPopupMenu(Component component, int x, int y, DaakiaHistory daakiaHistory) {
        JPopupMenu popupMenu = new JPopupMenu();
        JMenuItem renameMenuItem = new JMenuItem("Rename");
        renameMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                renameSelectedTreeItem(daakiaHistory);
            }
        });
        popupMenu.add(renameMenuItem);
        popupMenu.show(component, x, y);
    }

    // Method to rename the selected item in the history list
    private void renameSelectedItem() {
        int selectedIndex = historyList.getSelectedIndex();
        if (selectedIndex != -1) {
            DaakiaHistory selectedItem = historyListModel.getElementAt(selectedIndex);
            String newName = JOptionPane.showInputDialog("Enter a new name:", selectedItem);
            if (newName != null && !newName.isEmpty()) {
                selectedItem.setDisplayName(newName);
                boolean updated = updateHistoryTable(selectedItem);
                if (updated) {
                    historyListModel.set(selectedIndex, selectedItem);
                }
            }
        }
    }

    private void renameSelectedTreeItem(DaakiaHistory selectedItem) {

        Icon icon = DaakiaIcons.RenameHistory48;

        String newName = (String) JOptionPane
                                .showInputDialog(
                                        null,
                                        "Enter a new name:",
                                        "",
                                        JOptionPane.QUESTION_MESSAGE,
                                        icon,
                                        null,
                                        selectedItem.getDisplayName());
        if (newName != null && !newName.isEmpty()) {
            selectedItem.setDisplayName(newName);
            boolean updated = updateHistoryTable(selectedItem);
        }
    }

    private String todayAsString() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        return sdf.format(new java.util.Date());
    }

    private String yearAsString() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy");
        return sdf.format(new java.util.Date());
    }

    private String yearFromDateString(String dateString) {
        return dateString.substring(0, 4);
    }

    private JPanel initJTreeDemo() {


        // Sample data with string dates
        data.put("2022-01-01", new ArrayList<>(List.of("Row Entry 1", "Row Entry 2")));
        data.put("2022-01-02", new ArrayList<>(List.of("Row Entry 3", "Row Entry 4")));
        data.put("2022-01-03", new ArrayList<>(List.of("Row Entry 5", "Row Entry 6")));

        // Create the root node of the tree
        DefaultMutableTreeNode root = new DefaultMutableTreeNode("");

        // Add date nodes as parent nodes
//        for (Map.Entry<String, List<String>> entry : data.entrySet()) {
//            try {
//                Date date = new SimpleDateFormat("yyyy-MM-dd").parse(entry.getKey());
//                DefaultMutableTreeNode dateNode = new DefaultMutableTreeNode(date);
//                for (String rowEntry : entry.getValue()) {
//                    dateNode.add(new DefaultMutableTreeNode(rowEntry));
//                }
//                root.add(dateNode);
//            } catch (ParseException e) {
//                e.printStackTrace();
//            }
//        }

        for (Map.Entry<String, List<String>> entry : data.entrySet()) {
            DefaultMutableTreeNode dateNode = new DefaultMutableTreeNode(entry.getKey());
            for (String rowEntry : entry.getValue()) {
                dateNode.add(new DefaultMutableTreeNode(rowEntry));
            }
            root.add(dateNode);
        }

        // Create the tree with the root node
        historyTree = new Tree(new DefaultTreeModel(root));
        historyTree.setRootVisible(false);
        // Add tree selection listener
        historyTree.addTreeSelectionListener(new TreeSelectionListener() {
            @Override
            public void valueChanged(TreeSelectionEvent e) {
                DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) historyTree.getLastSelectedPathComponent();
                if (selectedNode != null) {
                    Object userObject = selectedNode.getUserObject();
                    System.out.println(userObject);
                }
            }
        });

        // Set custom cell renderer to display dates
//        tree.setCellRenderer(new DateTreeCellRenderer());

        // Expand all nodes by default
        for (int i = 0; i < historyTree.getRowCount(); i++) {
            historyTree.expandRow(i);
        }

        JPanel panel = new JPanel(new BorderLayout());
        panel.setSize(800, 300);
        panel.add(new JScrollPane(historyTree), BorderLayout.CENTER);
        return panel;
    }

    private JPanel initHistoryJTree() {

          loadHistoryFromDatabase();
//        dummyHistoryDataForTesting();

        // Create the root node of the tree
        DefaultMutableTreeNode root = new DefaultMutableTreeNode("");

        List<String> historyYears = initHistoryYears(historyData);
        Map<String, DefaultMutableTreeNode> yearNodes = new LinkedHashMap<>();
        for (String historyYear : historyYears) {
            DefaultMutableTreeNode yearNode = new DefaultMutableTreeNode(historyYear);
            yearNodes.put(historyYear, yearNode);
        }
        for (Map.Entry<String, List<DaakiaHistory>> entry : historyData.entrySet()) {
            String date = entry.getKey();
            DefaultMutableTreeNode yearNode = yearNodes.get(yearFromDateString(date));
            DefaultMutableTreeNode dateNode = new DefaultMutableTreeNode(date);
            for (DaakiaHistory rowEntry : entry.getValue()) {
                dateNode.add(new DefaultMutableTreeNode(rowEntry));
            }
            yearNode.add(dateNode);
        }

        for (String year : yearNodes.keySet()) {
            root.add(yearNodes.get(year));
        }

        // Create the tree with the root node
        treeModel = new DefaultTreeModel(root);
        historyTree = new Tree(treeModel);
        historyTree.setRootVisible(false);

        // Add tree selection listener
        historyTree.addTreeSelectionListener(new TreeSelectionListener() {
            @Override
            public void valueChanged(TreeSelectionEvent e) {
                DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) historyTree.getLastSelectedPathComponent();
                if (selectedNode != null) {
                    Object userObject = selectedNode.getUserObject();
                    if(userObject instanceof DaakiaHistory) {
                        loadHistoryDataIntoUI((DaakiaHistory) userObject);
                    }
                }
            }
        });

        historyTree.addMouseListener(new MouseAdapter() {

            @Override
            public void mouseReleased(MouseEvent e) {
                if (SwingUtilities.isRightMouseButton(e)) {
                    renameHistoryItem(e, true);
                }
            }

            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 1) { // Single click
                    TreePath path = historyTree.getPathForLocation(e.getX(), e.getY());
                    if (path != null) {
                        // Get the node associated with the clicked path
                        DefaultMutableTreeNode node = (DefaultMutableTreeNode) path.getLastPathComponent();
                        if (node != null) {
                            Object userObject = node.getUserObject();
                            if(userObject instanceof DaakiaHistory) {
                                loadHistoryDataIntoUI((DaakiaHistory) userObject);
                            }
                        }
                    }
                }
                else if (e.getClickCount() == 2) {
                    renameHistoryItem(e, false);
                }
            }
        });

        // Set custom cell renderer to display dates
        historyTree.setCellRenderer(new HistoryTreeCellRenderer());

        expandAllNodes(historyTree);

        JPanel panel = new JPanel(new BorderLayout());
        panel.setSize(800, 300);
        panel.add(new JScrollPane(historyTree), BorderLayout.CENTER);
        return panel;
    }

    private void renameHistoryItem(MouseEvent e, boolean showPopupMenu) {
        TreePath path = historyTree.getPathForLocation(e.getX(), e.getY());
        if (path != null) {
            // Get the node associated with the clicked path
            DefaultMutableTreeNode node = (DefaultMutableTreeNode) path.getLastPathComponent();
            if (node != null) {
                // Perform action based on the clicked node
                Object userObject = node.getUserObject();
                if(userObject instanceof DaakiaHistory) {
                    if(showPopupMenu) {
                        showPopupMenu(e.getComponent(), e.getX(), e.getY(), (DaakiaHistory) userObject);
                    }
                    else {
                        renameSelectedTreeItem((DaakiaHistory) userObject);
                    }
                    treeModel.nodeChanged(node);
                }
            }
        }
    }

    private void dummyHistoryDataForTesting() {
        DaakiaHistory daakiaHistory = new DaakiaHistory();
        daakiaHistory.setCreatedDate("2022-01-01");
        daakiaHistory.setUrl("abc.com");
        daakiaHistory.setRequestType("POST");
        daakiaHistory.setRequestBody("{}");
        historyData.put("2022-01-01", new ArrayList<>(List.of(daakiaHistory)));


        daakiaHistory = new DaakiaHistory();
        daakiaHistory.setCreatedDate("2023-01-01");
        daakiaHistory.setUrl("xyz.com");
        daakiaHistory.setRequestType("GET");
        daakiaHistory.setRequestBody("{}");
        historyData.put("2023-01-01", new ArrayList<>(List.of(daakiaHistory)));
    }

    private List<String> initHistoryYears(Map<String, List<DaakiaHistory>> historyData) {
        if(historyData.isEmpty()) {
            return new ArrayList<>();
        }
        return historyData.keySet().stream().map(this::yearFromDateString).collect(Collectors.toList());
    }

    private Map<String, List<DaakiaHistory>> initYearHistoryData(Map<String, List<DaakiaHistory>> historyData) {
        if(historyData.isEmpty()) {
            return new HashMap<>();
        }
        return historyData.keySet().stream().collect(Collectors.toMap(this::yearFromDateString, k -> new ArrayList<>(historyData.get(k)), (o, n) -> {
            n.addAll(o);
            return n;
        }));
    }

//    void addData(String date, String entry) {
//        if (data.containsKey(date)) {
//            // If the date exists, add the new entry to the corresponding list of entries
//            List<String> entries = data.get(date);
//            entries.add(entry);
//        }
//        else {
//            // If the date doesn't exist, create a new list and add the new entry to it
//            List<String> entries = new ArrayList<>();
//            entries.add(entry);
//            data.put(date, entries);
//        }
//
//        // Update the tree model with new data
//        DefaultTreeModel treeModel = (DefaultTreeModel) historyTree.getModel();
//        DefaultMutableTreeNode root = (DefaultMutableTreeNode) treeModel.getRoot();
//
//        DefaultMutableTreeNode dateNode = findValueNode(root, date);
//        if (dateNode != null) {
//            dateNode.add(new DefaultMutableTreeNode(entry));
//            treeModel.nodesWereInserted(dateNode, new int[]{dateNode.getChildCount() - 1});
//        }
//        else {
//            dateNode = new DefaultMutableTreeNode(date);
//            dateNode.add(new DefaultMutableTreeNode(entry));
//            root.add(dateNode);
//            treeModel.nodesWereInserted(root, new int[]{root.getIndex(dateNode)});
//        }
//        treeModel.reload();
//    }

    void addHistoryData(String date, DaakiaHistory entry) {
        if (historyData.containsKey(date)) {
            // If the date exists, add the new entry to the corresponding list of entries
            List<DaakiaHistory> entries = historyData.get(date);
            entries.add(entry);
        }
        else {
            // If the date doesn't exist, create a new list and add the new entry to it
            List<DaakiaHistory> entries = new ArrayList<>();
            entries.add(entry);
            historyData.put(date, entries);
        }

        // Update the tree model with new data
        DefaultTreeModel treeModel = (DefaultTreeModel) historyTree.getModel();
        DefaultMutableTreeNode root = (DefaultMutableTreeNode) treeModel.getRoot();

        DefaultMutableTreeNode yearNode = findValueNode(root, yearFromDateString(date));
        if (yearNode != null) {
            DefaultMutableTreeNode dateNode = findValueNode(yearNode, date);
            if (dateNode != null) {
                dateNode.add(new DefaultMutableTreeNode(entry));
                treeModel.nodesWereInserted(dateNode, new int[]{dateNode.getChildCount() - 1});
            }
            else {
                dateNode = new DefaultMutableTreeNode(date);
                dateNode.add(new DefaultMutableTreeNode(entry));
                yearNode.add(dateNode);
                treeModel.nodesWereInserted(yearNode, new int[]{yearNode.getIndex(dateNode)});
            }
        }
        else {
            yearNode = new DefaultMutableTreeNode(yearFromDateString(date));
            DefaultMutableTreeNode dateNode = new DefaultMutableTreeNode(date);
            dateNode.add(new DefaultMutableTreeNode(entry));
            yearNode.add(dateNode);
            root.add(yearNode);
            treeModel.nodesWereInserted(root, new int[]{root.getIndex(yearNode)});
        }


    }

    public static MultiValueMap<String, String> jsonStringToMultivaluedMap(String jsonString) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            // Deserialize JSON string into Map<String, List<String>>
            Map<String, List<String>> map = objectMapper.readValue(jsonString, new TypeReference<>() {
            });

            // Convert Map<String, List<String>> to MultiValuedMap<String, String>
            MultiValueMap<String, String> multivaluedMap = new LinkedMultiValueMap<>();
            for (Map.Entry<String, List<String>> entry : map.entrySet()) {
                multivaluedMap.addAll(entry.getKey(), entry.getValue());
            }
            return multivaluedMap;
        }
        catch (IOException e) {
            return null;
        }
    }


    static DefaultMutableTreeNode findValueNode(DefaultMutableTreeNode treeNode, String date) {
        Enumeration<?> enumeration = treeNode.breadthFirstEnumeration();
        while (enumeration.hasMoreElements()) {
            DefaultMutableTreeNode node = (DefaultMutableTreeNode) enumeration.nextElement();
            if (node.getUserObject().equals(date)) {
                return node;
            }
        }
        return null;
    }


    static class HistoryTreeCellRenderer extends DefaultTreeCellRenderer {

        private SimpleDateFormat dateFormat;

        public HistoryTreeCellRenderer() {
            dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        }

        @Override
        public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded, boolean leaf, int row, boolean hasFocus) {
            JLabel label = (JLabel) super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);
            if (value instanceof DefaultMutableTreeNode node) {
                Object userObject = node.getUserObject();
                if (userObject instanceof DaakiaHistory) {
                    label.setText(((DaakiaHistory) userObject).render());
                }
            }
            setIcon(null);
            return label;
        }
    }

    public JPanel dynamicTree(Component parentComponent) {
        DefaultMutableTreeNode rootNode = new DefaultMutableTreeNode("");
        Tree tree = new Tree(rootNode);
        tree.setCellRenderer(new MyTreeCellRenderer(tree));
        JPanel panel = new JPanel(new BorderLayout());
        JScrollPane scrollPane = new JBScrollPane(tree);
        JButton addButton = new JButton("Add");
        panel.add(addButton, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        scrollPane.setVisible(false);
        // Hide the root node
        tree.setRootVisible(false);

        // Expand all nodes in the tree
        expandAllNodes(tree);

        addButton.addActionListener(actionEvent -> {
            scrollPane.setVisible(true);
            DefaultMutableTreeNode node = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
            if(node == null) {
                node = rootNode;
            }
            String newName = (String) JOptionPane
                    .showInputDialog(
                            parentComponent,
                            "Enter a name",
                            "",
                            JOptionPane.QUESTION_MESSAGE,
                            DaakiaIcons.WebServiceClient48,
                            null,
                            null);
            if(newName!=null && !newName.isEmpty()) {
                DefaultMutableTreeNode newNode = new DefaultMutableTreeNode(newName);
                node.add(newNode);
                ((DefaultTreeModel) tree.getModel()).reload(node);
                parentComponent.revalidate();
                parentComponent.repaint();
            }
            expandAllNodes(tree);
        });


        return panel;
    }

    // Method to expand all nodes in the tree
    private void expandAllNodes(Tree tree) {
        TreeNode root = (TreeNode) tree.getModel().getRoot();
        expandAll(tree, new TreePath(root));
    }

    private void expandAll(Tree tree, TreePath parent) {
        TreeNode node = (TreeNode) parent.getLastPathComponent();
        if (node.getChildCount() >= 0) {
            for (Enumeration<?> e = node.children(); e.hasMoreElements(); ) {
                TreeNode n = (TreeNode) e.nextElement();
                TreePath path = parent.pathByAddingChild(n);
                expandAll(tree, path);
            }
        }
        tree.expandPath(parent);
    }


    static class MyTreeCellRenderer extends DefaultTreeCellRenderer {
        private JButton addButton;

        public MyTreeCellRenderer(Tree tree) {

        }

        @Override
        public Component getTreeCellRendererComponent(JTree tree, Object value, boolean selected,
                                                      boolean expanded, boolean leaf, int row, boolean hasFocus) {
            Component renderer = super.getTreeCellRendererComponent(tree, value, selected, expanded, leaf, row, hasFocus);
            if (value instanceof DefaultMutableTreeNode) {
                DefaultMutableTreeNode node = (DefaultMutableTreeNode) value;
                if (node.getParent() == null) {
                    // Root node
                    setIcon(DaakiaIcons.CollectionFolder);
                }
                else if (!node.isLeaf()) {
                    // Leaf node (file)
                    setIcon(DaakiaIcons.CollectionFolder);
                }
                else {
                    Object userObject = node.getUserObject();
                    if(userObject instanceof DaakiaHistory) {
                        setIcon(DaakiaIcons.HttpRequestsFiletype);
                    }
                    else {
                        setIcon(DaakiaIcons.CollectionFolder);
                    }
                }
            }
            return renderer;
        }
    }

    private void showDialog(JFrame parentFrame) {
        // Create a JPanel to be shown in the dialog
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        panel.add(dynamicTree(panel), BorderLayout.CENTER);

        // Create a JDialog with the parent frame as the owner
        JDialog dialog = new JDialog(parentFrame, "Save", true);
        dialog.setSize(700, 500);
        dialog.setLocationRelativeTo(parentFrame);

        JPanel actionPanel = new JPanel();
        actionPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));
        JButton saveButton = new JButton("Save") {
            @Override
            public void updateUI() {
                setUI(new DarculaButtonUI() {
                    @Override
                    public void paint(Graphics g, JComponent c) {
                        super.paint(g, c);

                    }
                });
            }

            @Override
            public void setBackground(Color bg) {
                super.setBackground(JBColor.RED);
            }

            @Override
            public void setContentAreaFilled(boolean b) {
                super.setContentAreaFilled(true);
            }
        };
        // Set button background color to blue
//        saveButton.setBackground(new JBColor(new Color(0, 122, 255), new Color(0, 122, 255))); // RGB value for blue
        // Set button foreground (text) color to white
//        saveButton.setForeground(JBColor.RED);
        JButton cancelButton = new JButton("Cancel");
        actionPanel.add(saveButton);
        actionPanel.add(cancelButton);
        panel.add(actionPanel, BorderLayout.SOUTH);


        // Add the panel to the dialog
        dialog.getContentPane().add(panel);

        // Set the dialog visible
        dialog.setVisible(true);
    }

}
