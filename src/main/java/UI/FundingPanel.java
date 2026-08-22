package UI;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

import Model.*;
import Service.*;

public class FundingPanel extends JPanel implements ActionListener {
    // Form Fields
    private JTextField txtAppID, txtProjectName, txtAmount;
    private JComboBox<String> cmbSector, cmbBeneficiary;
    private JTextArea txtDescription;
    
    // Table
    private JTable table;
    private DefaultTableModel tableModel;
    
    // Buttons
    private JButton btnSubmit, btnClear, btnViewAll, btnLoad;
    
    // Current Application
    private FundingApplication currentApp;
    private JLabel lblCurrentApp;
    private JLabel lblStatusMessage;

    public FundingPanel() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Title
        JLabel lblTitle = new JLabel("Funding Applications", JLabel.CENTER);
        lblTitle.setFont(new Font("Arial", Font.BOLD, 20));
        lblTitle.setForeground(new Color(0, 102, 204));
        add(lblTitle, BorderLayout.NORTH);

        // Split Panel
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setDividerLocation(450);
        splitPane.setResizeWeight(0.4);

        // LEFT PANEL
        JPanel leftPanel = new JPanel(new BorderLayout(5, 5));

        // Form Panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createEtchedBorder(),
            "Application Details",
            TitledBorder.LEFT,
            TitledBorder.TOP,
            new Font("Arial", Font.BOLD, 12)
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 4, 4, 4);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Row 1: Application ID (Auto)
        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0.3;
        JLabel lblID = new JLabel("App ID:");
        lblID.setFont(new Font("Arial", Font.BOLD, 12));
        formPanel.add(lblID, gbc);

        gbc.gridx = 1; gbc.weightx = 0.7;
        txtAppID = new JTextField(10);
        txtAppID.setFont(new Font("Arial", Font.PLAIN, 12));
        txtAppID.setEditable(false);
        txtAppID.setBackground(new Color(240, 240, 240));
        txtAppID.setPreferredSize(new Dimension(120, 28));
        txtAppID.setText(String.valueOf(getNextAppId()));
        formPanel.add(txtAppID, gbc);

        // Row 2: Project Name
        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0.3;
        JLabel lblProject = new JLabel("Project Name:");
        lblProject.setFont(new Font("Arial", Font.BOLD, 12));
        formPanel.add(lblProject, gbc);

        gbc.gridx = 1; gbc.weightx = 0.7;
        txtProjectName = new JTextField(10);
        txtProjectName.setFont(new Font("Arial", Font.PLAIN, 12));
        txtProjectName.setPreferredSize(new Dimension(120, 28));
        formPanel.add(txtProjectName, gbc);

        // Row 3: Sector
        gbc.gridx = 0; gbc.gridy = 2; gbc.weightx = 0.3;
        JLabel lblSector = new JLabel("Sector:");
        lblSector.setFont(new Font("Arial", Font.BOLD, 12));
        formPanel.add(lblSector, gbc);

        gbc.gridx = 1; gbc.weightx = 0.7;
        cmbSector = new JComboBox<>(new String[]{
            "Agriculture", "Education", "Healthcare", 
            "Technology", "Small Business", "Infrastructure"
        });
        cmbSector.setFont(new Font("Arial", Font.PLAIN, 12));
        cmbSector.setPreferredSize(new Dimension(120, 28));
        formPanel.add(cmbSector, gbc);

        // Row 4: Amount
        gbc.gridx = 0; gbc.gridy = 3; gbc.weightx = 0.3;
        JLabel lblAmount = new JLabel("Amount (QR):");
        lblAmount.setFont(new Font("Arial", Font.BOLD, 12));
        formPanel.add(lblAmount, gbc);

        gbc.gridx = 1; gbc.weightx = 0.7;
        txtAmount = new JTextField(10);
        txtAmount.setFont(new Font("Arial", Font.PLAIN, 12));
        txtAmount.setPreferredSize(new Dimension(120, 28));
        formPanel.add(txtAmount, gbc);

        // Row 5: Beneficiary
        gbc.gridx = 0; gbc.gridy = 4; gbc.weightx = 0.3;
        JLabel lblBeneficiary = new JLabel("Beneficiary:");
        lblBeneficiary.setFont(new Font("Arial", Font.BOLD, 12));
        formPanel.add(lblBeneficiary, gbc);

        gbc.gridx = 1; gbc.weightx = 0.7;
        cmbBeneficiary = new JComboBox<>();
        cmbBeneficiary.setFont(new Font("Arial", Font.PLAIN, 12));
        cmbBeneficiary.setPreferredSize(new Dimension(120, 28));
        loadBeneficiaries();
        formPanel.add(cmbBeneficiary, gbc);

        // Row 6: Description
        gbc.gridx = 0; gbc.gridy = 5; gbc.weightx = 0.3;
        JLabel lblDesc = new JLabel("Description:");
        lblDesc.setFont(new Font("Arial", Font.BOLD, 12));
        formPanel.add(lblDesc, gbc);

        gbc.gridx = 1; gbc.weightx = 0.7;
        txtDescription = new JTextArea(15, 10);
        txtDescription.setFont(new Font("Arial", Font.PLAIN, 12));
        txtDescription.setLineWrap(true);
        JScrollPane scrollDesc = new JScrollPane(txtDescription);
        scrollDesc.setPreferredSize(new Dimension(120, 60));
        formPanel.add(scrollDesc, gbc);

        leftPanel.add(formPanel, BorderLayout.NORTH);

        // Current Application Display
        JPanel currentPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        currentPanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createEtchedBorder(),
            "Currently Selected",
            TitledBorder.LEFT,
            TitledBorder.TOP,
            new Font("Arial", Font.BOLD, 11)
        ));
        lblCurrentApp = new JLabel("None selected");
        lblCurrentApp.setFont(new Font("Arial", Font.BOLD, 12));
        lblCurrentApp.setForeground(new Color(0, 102, 204));
        currentPanel.add(lblCurrentApp);
        leftPanel.add(currentPanel, BorderLayout.CENTER);

        // Buttons
        JPanel buttonPanel = new JPanel(new GridLayout(2, 2, 5, 5));
        buttonPanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createEtchedBorder(),
            "Actions",
            TitledBorder.LEFT,
            TitledBorder.TOP,
            new Font("Arial", Font.BOLD, 12)
        ));

        btnSubmit = new JButton("Submit");
        btnClear = new JButton("Clear");
        btnViewAll = new JButton("View All");
        btnLoad = new JButton("Load Selected");

        JButton[] buttons = {btnSubmit, btnClear, btnViewAll, btnLoad};
        Color[] colors = {
            new Color(0, 153, 76),   // Submit - Green
            new Color(128, 128, 128), // Clear - Gray
            new Color(0, 0, 153),    // View All - Dark Blue
            new Color(204, 0, 0)     // Load - Red
        };
        
        for (int i = 0; i < buttons.length; i++) {
            buttons[i].setFont(new Font("Arial", Font.BOLD, 12));
            buttons[i].setBackground(colors[i]);
            buttons[i].setForeground(Color.BLACK);
            buttons[i].setFocusPainted(false);
            buttons[i].setPreferredSize(new Dimension(100, 40));
            buttons[i].addActionListener(this);
            buttonPanel.add(buttons[i]);
        }

        leftPanel.add(buttonPanel, BorderLayout.SOUTH);

        // RIGHT PANEL: Table
        JPanel rightPanel = new JPanel(new BorderLayout(5, 5));

        JLabel lblTableTitle = new JLabel("All Applications", JLabel.CENTER);
        lblTableTitle.setFont(new Font("Arial", Font.BOLD, 14));
        rightPanel.add(lblTableTitle, BorderLayout.NORTH);

        String[] columns = {"ID", "Project", "Sector", "Amount (QR)", "Status"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        table = new JTable(tableModel);
        table.setFont(new Font("Arial", Font.PLAIN, 12));
        table.setRowHeight(25);
        table.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int row = table.getSelectedRow();
                if (row >= 0) {
                    int id = (int) tableModel.getValueAt(row, 0);
                    setStatus("Selected Application ID: " + id + " - Click 'Load Selected'");
                }
            }
        });

        // Color-code status in table
        table.setDefaultRenderer(Object.class, new javax.swing.table.DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                
                if (column == 4) {
                    String status = (String) value;
                    if (status.contains("APPROVED")) {
                        c.setBackground(new Color(0, 200, 0));
                        c.setForeground(Color.WHITE);
                    } else if (status.contains("REJECTED")) {
                        c.setBackground(new Color(200, 0, 0));
                        c.setForeground(Color.WHITE);
                    } else if (status.contains("PENDING") || status.contains("UNDER_REVIEW")) {
                        c.setBackground(new Color(255, 200, 0));
                        c.setForeground(Color.BLACK);
                    } else if (status.contains("FUNDED") || status.contains("COMPLETED")) {
                        c.setBackground(new Color(0, 100, 200));
                        c.setForeground(Color.WHITE);
                    } else {
                        c.setBackground(table.getBackground());
                        c.setForeground(table.getForeground());
                    }
                } else {
                    c.setBackground(isSelected ? table.getSelectionBackground() : table.getBackground());
                    c.setForeground(isSelected ? table.getSelectionForeground() : table.getForeground());
                }
                return c;
            }
        });

        table.getColumnModel().getColumn(0).setPreferredWidth(50);
        table.getColumnModel().getColumn(1).setPreferredWidth(120);
        table.getColumnModel().getColumn(2).setPreferredWidth(80);
        table.getColumnModel().getColumn(3).setPreferredWidth(80);
        table.getColumnModel().getColumn(4).setPreferredWidth(80);

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createEtchedBorder());
        rightPanel.add(scrollPane, BorderLayout.CENTER);

        splitPane.setLeftComponent(leftPanel);
        splitPane.setRightComponent(rightPanel);

        add(splitPane, BorderLayout.CENTER);

        // Status Bar
        JPanel statusBar = new JPanel(new FlowLayout(FlowLayout.LEFT));
        statusBar.setBorder(BorderFactory.createEtchedBorder());
        lblStatusMessage = new JLabel("Ready - Submit new application or select from table");
        lblStatusMessage.setFont(new Font("Arial", Font.PLAIN, 11));
        statusBar.add(lblStatusMessage);
        add(statusBar, BorderLayout.SOUTH);

        // Initialize
        currentApp = new FundingApplication();
        updateTable();
        updateCurrentDisplay();
    }

    private int getNextAppId() {
        int maxId = 0;
        for (FundingApplication app : FundingService.getApplications()) {
            if (app.getApplicationID() > maxId) {
                maxId = app.getApplicationID();
            }
        }
        return maxId + 1;
    }

    private void loadBeneficiaries() {
        cmbBeneficiary.removeAllItems();
        
        // Get ALL users from UserService
        ArrayList<User> allUsers = UserService.getUsers();
        int beneficiaryCount = 0;
        
        for (User user : allUsers) {
            if (user instanceof Beneficiary) {
                String displayText = user.getFullName() + " (ID: " + user.getUserID() + ")";
                cmbBeneficiary.addItem(displayText);
                beneficiaryCount++;
            }
        }
        
        if (beneficiaryCount == 0) {
            cmbBeneficiary.addItem("No beneficiaries found - Please add one");
            setStatus("No beneficiaries found. Please add beneficiaries to the system.");
        } else {
            setStatus("Loaded " + beneficiaryCount + " beneficiaries");
        }
    }

    // ===== HELPER METHOD: Get Beneficiary from ComboBox Selection =====
    private Beneficiary getSelectedBeneficiary() {
        String selected = (String) cmbBeneficiary.getSelectedItem();
        
        if (selected == null || selected.equals("No beneficiaries found - Please add one")) {
            return null;
        }
        
        try {
            // Extract ID from the display text: "Name (ID: 123)"
            int start = selected.indexOf("ID: ") + 4;
            int end = selected.indexOf(")");
            if (start > 0 && end > start) {
                int beneficiaryId = Integer.parseInt(selected.substring(start, end));
                
                // Search for the beneficiary in UserService
                for (User user : UserService.getUsers()) {
                    if (user.getUserID() == beneficiaryId && user instanceof Beneficiary) {
                        return (Beneficiary) user;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private void updateCurrentDisplay() {
        if (currentApp != null && currentApp.getApplicationID() > 0) {
            String statusEmoji = getStatusEmoji(currentApp.getStatus());
            lblCurrentApp.setText("ID: " + currentApp.getApplicationID() + 
                                 " | Project: " + currentApp.getProject().getProjectName() +
                                 " | Status: " + statusEmoji + " " + currentApp.getStatus());
            lblCurrentApp.setForeground(new Color(0, 102, 204));
        } else {
            lblCurrentApp.setText("None selected");
            lblCurrentApp.setForeground(Color.RED);
        }
    }

    private String getStatusEmoji(ApplicationStatus status) {
        if (status == null) return "⏳";
        switch (status) {
            case APPROVED: return "✅";
            case REJECTED: return "❌";
            case FUNDED: return "💰";
            case COMPLETED: return "📌";
            case UNDER_REVIEW: return "🔍";
            case PENDING: return "⏳";
            default: return "📝";
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        try {
            if (e.getSource() == btnSubmit) {
                submitApplication();
            } else if (e.getSource() == btnClear) {
                clearForm();
            } else if (e.getSource() == btnViewAll) {
                updateTable();
                setStatus("Displaying all applications");
            } else if (e.getSource() == btnLoad) {
                loadSelectedApplication();
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                "Error: " + ex.getMessage(),
                "Operation Failed",
                JOptionPane.ERROR_MESSAGE);
            setStatus("Error: " + ex.getMessage());
        }
    }

    private void submitApplication() {
        String name = txtProjectName.getText().trim();
        String amountText = txtAmount.getText().trim();

        if (name.isEmpty() || amountText.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "Please fill all required fields.",
                "Missing Information",
                JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            int id = getNextAppId();
            double amount = Double.parseDouble(amountText);
            String sector = (String) cmbSector.getSelectedItem();

            // Get beneficiary from combo box
            Beneficiary beneficiary = getSelectedBeneficiary();
            if (beneficiary == null) {
                JOptionPane.showMessageDialog(this,
                    "Please select a valid beneficiary.\n" +
                    "Make sure you have added beneficiaries in the system.",
                    "No Beneficiary Selected",
                    JOptionPane.WARNING_MESSAGE);
                return;
            }

            // Create project
            Project project = new Project(
                id,
                name,
                sector,
                txtDescription.getText(),
                "Location",
                amount,
                10,
                12
            );

            // Create application with enum status
            currentApp = new FundingApplication(
                id,
                beneficiary,
                project,
                LocalDateTime.now(),
                amount,
                ApplicationStatus.PENDING
            );

            FundingService.submitApplication(currentApp);
            updateTable();
            updateCurrentDisplay();
            txtAppID.setText(String.valueOf(getNextAppId()));
            txtAmount.setText("");
            txtProjectName.setText("");
            txtDescription.setText("");

            setStatus("Application ID " + id + " submitted successfully");

            JOptionPane.showMessageDialog(this,
                "Application submitted successfully!\n" +
                "ID: " + id + "\n" +
                "Project: " + name + "\n" +
                "Beneficiary: " + beneficiary.getFullName() + "\n" +
                "Amount: " + amount + " QR",
                "Success",
                JOptionPane.INFORMATION_MESSAGE);

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this,
                "Please enter a valid number for the amount.",
                "Invalid Amount",
                JOptionPane.ERROR_MESSAGE);
        }
    }

    private void loadSelectedApplication() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this,
                "Please select an application from the table.",
                "No Selection",
                JOptionPane.WARNING_MESSAGE);
            return;
        }

        int modelRow = table.convertRowIndexToModel(selectedRow);
        int id = (int) tableModel.getValueAt(modelRow, 0);
        
        FundingApplication loaded = FundingService.searchApplication(id);
        if (loaded != null) {
            currentApp = loaded;
            txtAppID.setText(String.valueOf(currentApp.getApplicationID()));
            txtProjectName.setText(currentApp.getProject().getProjectName());
            txtAmount.setText(String.valueOf(currentApp.getRequestedAmount()));
            cmbSector.setSelectedItem(currentApp.getProject().getSector());
            txtDescription.setText(currentApp.getProject().getDescription());
            
            // Find and select the beneficiary in combo box
            String beneficiaryName = currentApp.getBeneficiary().getFullName();
            int beneficiaryId = currentApp.getBeneficiary().getUserID();
            String searchText = beneficiaryName + " (ID: " + beneficiaryId + ")";
            for (int i = 0; i < cmbBeneficiary.getItemCount(); i++) {
                if (cmbBeneficiary.getItemAt(i).equals(searchText)) {
                    cmbBeneficiary.setSelectedIndex(i);
                    break;
                }
            }
            
            updateCurrentDisplay();
            setStatus("Loaded Application ID: " + id);
            
            JOptionPane.showMessageDialog(this,
                "Application loaded successfully!\n" +
                "Project: " + currentApp.getProject().getProjectName() + "\n" +
                "Beneficiary: " + currentApp.getBeneficiary().getFullName(),
                "Loaded",
                JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this,
                "Application not found!",
                "Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }

    private void clearForm() {
        txtAppID.setText(String.valueOf(getNextAppId()));
        txtProjectName.setText("");
        txtAmount.setText("");
        txtDescription.setText("");
        cmbSector.setSelectedIndex(0);
        if (cmbBeneficiary.getItemCount() > 0) {
            cmbBeneficiary.setSelectedIndex(0);
        }
        currentApp = new FundingApplication();
        updateCurrentDisplay();
        setStatus("Form cleared. Ready to submit new application.");
    }

    // ===== UPDATE TABLE (FIXED - Handles Enum Status) =====
    private void updateTable() {
        tableModel.setRowCount(0);
        for (FundingApplication app : FundingService.getApplications()) {
            String statusEmoji = getStatusEmoji(app.getStatus());
            Object[] row = {
                app.getApplicationID(),
                app.getProject().getProjectName(),
                app.getProject().getSector(),
                String.format("%.2f", app.getRequestedAmount()),
                statusEmoji + " " + app.getStatus().name()
            };
            tableModel.addRow(row);
        }
        setStatus("Displaying " + FundingService.getApplications().size() + " applications");
    }

    private void setStatus(String message) {
        if (lblStatusMessage != null) {
            lblStatusMessage.setText("Status: " + message);
        }
    }

    // ===== REFRESH TABLE (Called from Dashboard) =====
    public void refreshTable() {
        updateTable();
        setStatus("Table refreshed - showing latest statuses");
    }
 // In FundingPanel.java - uploadDocument() method


    private void uploadDocument(String docType, JLabel statusLabel) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Upload " + docType);
        
        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            File sourceFile = fileChooser.getSelectedFile();
            String originalName = sourceFile.getName();
            
            if (currentApp != null) {
                int appId = currentApp.getApplicationID();
                String documentType = "";
                String savedFileName = null;
                
                switch (docType) {
                    case "Business Plan":
                        documentType = "business_plan";
                        savedFileName = DocumentStorageService.saveDocument(appId, sourceFile, documentType);
                        if (savedFileName != null) {
                            currentApp.setBusinessPlanFile(savedFileName);
                        }
                        break;
                    case "Financial Statements":
                        documentType = "financials";
                        savedFileName = DocumentStorageService.saveDocument(appId, sourceFile, documentType);
                        if (savedFileName != null) {
                            currentApp.setFinancialStatementsFile(savedFileName);
                        }
                        break;
                    default:
                        documentType = "supporting";
                        savedFileName = DocumentStorageService.saveDocument(appId, sourceFile, documentType);
                        if (savedFileName != null) {
                            currentApp.setSupportingDocumentsFile(savedFileName);
                        }
                        break;
                }
                
                if (savedFileName != null) {
                    FundingService.updateApplication(currentApp);
                    if (statusLabel != null) {
                        statusLabel.setText("✅ " + originalName);
                        statusLabel.setForeground(new Color(0, 153, 76));
                    }
                    setStatus("📎 Uploaded: " + originalName);
                }
            }
        }
    }
}