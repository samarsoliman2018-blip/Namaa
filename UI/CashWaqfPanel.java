package UI;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.*;
import java.time.LocalDate;
import Model.*;
import Service.*;

public class CashWaqfPanel extends JPanel implements ActionListener {
    // Form Fields
    private JTextField txtWaqfID, txtAmount, txtBalance;
    private JComboBox<String> cmbStatus;
    private JComboBox<String> cmbSector;
    private JTextField txtMaxFunding;
    private JTextField txtMinPRI;
    private JComboBox<String> cmbTargetBeneficiaries;
    private JButton btnCreate, btnDeposit, btnAllocate, btnRepayment, btnClear, btnViewAll, btnLoad;
    private JButton btnForceRefresh;  // ← ADDED
    
    // Table for Display
    private JTable table;
    private DefaultTableModel tableModel;
    private TableRowSorter<DefaultTableModel> sorter;
    
    // Current Waqf
    private CashWaqf currentWaqf;
    
    // Status label
    private JLabel lblStatusMessage;
    private JLabel lblCurrentWaqf;

    public CashWaqfPanel() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // ===== TOP: Title =====
        JLabel lblTitle = new JLabel("NAMAA SMART WAQF PLATFORM (Smart Cash Waqf Ecosystem)", JLabel.CENTER);
        lblTitle.setFont(new Font("Arial", Font.BOLD, 20));
        lblTitle.setForeground(new Color(0, 102, 204));
        add(lblTitle, BorderLayout.NORTH);
        
        // ===== CENTER: Split Panel =====
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setDividerLocation(480);
        splitPane.setResizeWeight(0.45);
        
        // ---- LEFT PANEL: Input Form ----
        JPanel leftPanel = new JPanel(new BorderLayout(5, 5));
        
        // Form Panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createEtchedBorder(),
            "Waqf Details",
            TitledBorder.LEFT,
            TitledBorder.TOP,
            new Font("Arial", Font.BOLD, 12)
        ));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 4, 4, 4);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        // Row 1: Waqf ID
        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0.35;
        JLabel lblID = new JLabel("Waqf ID:");
        lblID.setFont(new Font("Arial", Font.BOLD, 12));
        formPanel.add(lblID, gbc);
        gbc.gridx = 1; gbc.weightx = 0.65;
        txtWaqfID = new JTextField(10);
        txtWaqfID.setEditable(false);
        txtWaqfID.setBackground(new Color(240, 240, 240));
        txtWaqfID.setPreferredSize(new Dimension(120, 28));
        txtWaqfID.setText(String.valueOf(getNextWaqfId()));
        formPanel.add(txtWaqfID, gbc);
        
        // Row 2: Amount
        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0.35;
        JLabel lblAmount = new JLabel("Amount (QR):");
        lblAmount.setFont(new Font("Arial", Font.BOLD, 12));
        formPanel.add(lblAmount, gbc);
        gbc.gridx = 1; gbc.weightx = 0.65;
        txtAmount = new JTextField(10);
        txtAmount.setPreferredSize(new Dimension(120, 28));
        formPanel.add(txtAmount, gbc);
        
        // Row 3: Balance
        gbc.gridx = 0; gbc.gridy = 2; gbc.weightx = 0.35;
        JLabel lblBalance = new JLabel("Balance (QR):");
        lblBalance.setFont(new Font("Arial", Font.BOLD, 12));
        formPanel.add(lblBalance, gbc);
        gbc.gridx = 1; gbc.weightx = 0.65;
        txtBalance = new JTextField(10);
        txtBalance.setEditable(false);
        txtBalance.setBackground(new Color(240, 240, 240));
        txtBalance.setPreferredSize(new Dimension(120, 28));
        formPanel.add(txtBalance, gbc);
        
        // Row 4: Status
        gbc.gridx = 0; gbc.gridy = 3; gbc.weightx = 0.35;
        JLabel lblStatus = new JLabel("Status:");
        lblStatus.setFont(new Font("Arial", Font.BOLD, 12));
        formPanel.add(lblStatus, gbc);
        gbc.gridx = 1; gbc.weightx = 0.65;
        cmbStatus = new JComboBox<>(new String[]{"Active", "Inactive"});
        cmbStatus.setPreferredSize(new Dimension(120, 28));
        formPanel.add(cmbStatus, gbc);
        
        // Row 5: Allowed Sector
        gbc.gridx = 0; gbc.gridy = 4; gbc.weightx = 0.35;
        JLabel lblSector = new JLabel("Allowed Sector:");
        lblSector.setFont(new Font("Arial", Font.BOLD, 12));
        formPanel.add(lblSector, gbc);
        gbc.gridx = 1; gbc.weightx = 0.65;
        cmbSector = new JComboBox<>(new String[]{
            "All Sectors", "Agriculture", "Education", "Healthcare", 
            "Technology", "Small Business", "Infrastructure"
        });
        cmbSector.setPreferredSize(new Dimension(120, 28));
        formPanel.add(cmbSector, gbc);
        
        // Row 6: Max Funding
        gbc.gridx = 0; gbc.gridy = 5; gbc.weightx = 0.35;
        JLabel lblMaxFunding = new JLabel("Max Funding per Project:");
        lblMaxFunding.setFont(new Font("Arial", Font.BOLD, 12));
        formPanel.add(lblMaxFunding, gbc);
        gbc.gridx = 1; gbc.weightx = 0.65;
        txtMaxFunding = new JTextField(10);
        txtMaxFunding.setPreferredSize(new Dimension(120, 28));
        txtMaxFunding.setText("100000");
        formPanel.add(txtMaxFunding, gbc);
        
        // Row 7: Min PRI
        gbc.gridx = 0; gbc.gridy = 6; gbc.weightx = 0.35;
        JLabel lblMinPRI = new JLabel("Minimum PRI:");
        lblMinPRI.setFont(new Font("Arial", Font.BOLD, 12));
        formPanel.add(lblMinPRI, gbc);
        gbc.gridx = 1; gbc.weightx = 0.65;
        txtMinPRI = new JTextField(10);
        txtMinPRI.setPreferredSize(new Dimension(120, 28));
        txtMinPRI.setText("60");
        formPanel.add(txtMinPRI, gbc);
        
        // Row 8: Target Beneficiaries
        gbc.gridx = 0; gbc.gridy = 7; gbc.weightx = 0.35;
        JLabel lblTarget = new JLabel("Target Beneficiaries:");
        lblTarget.setFont(new Font("Arial", Font.BOLD, 12));
        formPanel.add(lblTarget, gbc);
        gbc.gridx = 1; gbc.weightx = 0.65;
        cmbTargetBeneficiaries = new JComboBox<>(new String[]{
            "All Beneficiaries", "Women", "Youth", "Farmers", 
            "Small Business Owners", "Students", "Entrepreneurs"
        });
        cmbTargetBeneficiaries.setPreferredSize(new Dimension(120, 28));
        formPanel.add(cmbTargetBeneficiaries, gbc);
        
        leftPanel.add(formPanel, BorderLayout.NORTH);
        
        // ---- Current Waqf Display ----
        JPanel currentPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        currentPanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createEtchedBorder(),
            "Currently Selected",
            TitledBorder.LEFT,
            TitledBorder.TOP,
            new Font("Arial", Font.BOLD, 11)
        ));
        lblCurrentWaqf = new JLabel("None selected");
        lblCurrentWaqf.setFont(new Font("Arial", Font.BOLD, 12));
        lblCurrentWaqf.setForeground(new Color(0, 102, 204));
        currentPanel.add(lblCurrentWaqf);
        leftPanel.add(currentPanel, BorderLayout.CENTER);
        
        // ---- BUTTONS ----
        JPanel buttonPanel = new JPanel(new GridLayout(2, 4, 5, 5));
        buttonPanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createEtchedBorder(),
            "Actions",
            TitledBorder.LEFT,
            TitledBorder.TOP,
            new Font("Arial", Font.BOLD, 12)
        ));
        
        btnCreate = new JButton("Create");
        btnDeposit = new JButton("Deposit");
        btnAllocate = new JButton("Allocate");
        btnRepayment = new JButton("Repayment");
        btnClear = new JButton("Clear");
        btnViewAll = new JButton("View All");
        btnLoad = new JButton("Load Selected");
        btnForceRefresh = new JButton("🔄 Force Refresh");  // ← ADDED
        
        JButton[] buttons = {btnCreate, btnDeposit, btnAllocate, btnRepayment, 
                             btnClear, btnViewAll, btnLoad, btnForceRefresh};
        Color[] colors = {new Color(0, 153, 76), new Color(0, 102, 204), new Color(255, 153, 0), 
                          new Color(204, 0, 102), new Color(128, 128, 128), new Color(0, 0, 153), 
                          new Color(204, 0, 0), new Color(0, 102, 204)};
        
        for (int i = 0; i < buttons.length; i++) {
            buttons[i].setFont(new Font("Arial", Font.BOLD, 11));
            buttons[i].setBackground(colors[i]);
            buttons[i].setForeground(Color.BLACK);
            buttons[i].setFocusPainted(false);
            buttons[i].setPreferredSize(new Dimension(95, 40));
            buttons[i].addActionListener(this);
            buttonPanel.add(buttons[i]);
        }
        
        leftPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        // ---- RIGHT PANEL: Table ----
        JPanel rightPanel = new JPanel(new BorderLayout(5, 5));
        
        JPanel tableHeaderPanel = new JPanel(new BorderLayout());
        JLabel lblTableTitle = new JLabel("All Cash Waqfs", JLabel.CENTER);
        lblTableTitle.setFont(new Font("Arial", Font.BOLD, 14));
        tableHeaderPanel.add(lblTableTitle, BorderLayout.CENTER);
        
        JLabel lblSearchHint = new JLabel("Click a row to select, then click 'Load Selected'");
        lblSearchHint.setFont(new Font("Arial", Font.PLAIN, 10));
        lblSearchHint.setForeground(Color.GRAY);
        tableHeaderPanel.add(lblSearchHint, BorderLayout.SOUTH);
        
        rightPanel.add(tableHeaderPanel, BorderLayout.NORTH);
        
        // Table setup
        String[] columns = {"ID", "Total (QR)", "Balance (QR)", "Sector", "Status"};
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
                    setStatus("Selected Waqf ID: " + id + " - Click 'Load Selected' to work with it");
                }
            }
        });
        
        sorter = new TableRowSorter<>(tableModel);
        table.setRowSorter(sorter);
        
        table.getColumnModel().getColumn(0).setPreferredWidth(50);
        table.getColumnModel().getColumn(1).setPreferredWidth(80);
        table.getColumnModel().getColumn(2).setPreferredWidth(80);
        table.getColumnModel().getColumn(3).setPreferredWidth(100);
        table.getColumnModel().getColumn(4).setPreferredWidth(80);
        
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createEtchedBorder());
        rightPanel.add(scrollPane, BorderLayout.CENTER);
        
        splitPane.setLeftComponent(leftPanel);
        splitPane.setRightComponent(rightPanel);
        
        add(splitPane, BorderLayout.CENTER);
        
        // ---- BOTTOM: Status Bar ----
        JPanel statusBar = new JPanel(new FlowLayout(FlowLayout.LEFT));
        statusBar.setBorder(BorderFactory.createEtchedBorder());
        lblStatusMessage = new JLabel("Ready - Create a new Waqf with sector preferences");
        lblStatusMessage.setFont(new Font("Arial", Font.PLAIN, 11));
        statusBar.add(lblStatusMessage);
        add(statusBar, BorderLayout.SOUTH);
        
        // Initialize
     // Initialize
        currentWaqf = new CashWaqf();

        // ===== FIXED: Call refreshWaqfs() correctly =====
        WaqfService.refreshWaqfs();  // ← This now exists!
        updateTable();
        updateCurrentWaqfDisplay();

        System.out.println("✅ CashWaqfPanel initialized. Total waqfs: " + WaqfService.getAllWaqfs().size());
    }
    // ===== GET NEXT WAQF ID =====
    private int getNextWaqfId() {
        int maxId = 0;
        for (CashWaqf w : WaqfService.getAllWaqfs()) {
            if (w.getWaqfID() > maxId) {
                maxId = w.getWaqfID();
            }
        }
        return maxId + 1;
    }
    
    // ===== UPDATE CURRENT WAQF DISPLAY =====
    private void updateCurrentWaqfDisplay() {
        if (currentWaqf != null && currentWaqf.getWaqfID() > 0) {
            lblCurrentWaqf.setText("ID: " + currentWaqf.getWaqfID() + 
                                  " | Balance: " + String.format("%.2f", currentWaqf.getAvailableBalance()) + 
                                  " QR | Status: " + currentWaqf.getStatus());
            lblCurrentWaqf.setForeground(new Color(0, 102, 204));
        } else {
            lblCurrentWaqf.setText("None selected");
            lblCurrentWaqf.setForeground(Color.RED);
        }
    }
    
    @Override
    public void actionPerformed(ActionEvent e) {
        try {
            if (e.getSource() == btnCreate) {
                createWaqf();
            } else if (e.getSource() == btnDeposit) {
                depositMoney();
            } else if (e.getSource() == btnAllocate) {
                allocateMoney();
            } else if (e.getSource() == btnRepayment) {
                receiveRepayment();
            } else if (e.getSource() == btnClear) {
                clearForm();
            } else if (e.getSource() == btnViewAll) {
                updateTable();
                setStatus("Displaying all waqfs");
            } else if (e.getSource() == btnLoad) {
                loadSelectedWaqf();
            } else if (e.getSource() == btnForceRefresh) {
                WaqfService.refreshWaqfs();
                updateTable();
                setStatus("🔄 Force refreshed! Found " + WaqfService.getAllWaqfs().size() + " waqfs");
                JOptionPane.showMessageDialog(this,
                    "✅ Waqf list refreshed!\n" +
                    "Total waqfs found: " + WaqfService.getAllWaqfs().size(),
                    "Refresh Complete",
                    JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this,
                "Please enter a valid number in the Amount field.",
                "Input Error",
                JOptionPane.ERROR_MESSAGE);
            setStatus("Error: Invalid number format");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                "Error: " + ex.getMessage(),
                "Operation Failed",
                JOptionPane.ERROR_MESSAGE);
            setStatus("Error: " + ex.getMessage());
        }
    }
    
    // ===== LOAD SELECTED WAQF =====
    private void loadSelectedWaqf() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this,
                "Please select a row from the table first.",
                "No Selection",
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int modelRow = table.convertRowIndexToModel(selectedRow);
        int id = (int) tableModel.getValueAt(modelRow, 0);
        
        CashWaqf loaded = WaqfService.searchWaqf(id);
        if (loaded != null) {
            currentWaqf = loaded;
            
            txtWaqfID.setText(String.valueOf(currentWaqf.getWaqfID()));
            txtAmount.setText("");
            txtBalance.setText(String.format("%.2f", currentWaqf.getAvailableBalance()));
            cmbStatus.setSelectedItem(currentWaqf.getStatus());
            
            updateCurrentWaqfDisplay();
            setStatus("Loaded Waqf ID: " + id + " - Ready for transactions");
            
            JOptionPane.showMessageDialog(this,
                "Waqf ID " + id + " loaded successfully!\n" +
                "Balance: " + currentWaqf.getAvailableBalance() + " QR\n" +
                "Status: " + currentWaqf.getStatus(),
                "Waqf Loaded",
                JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this,
                "Waqf ID " + id + " not found!",
                "Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    // ===== CREATE WAQF =====
    private void createWaqf() {
        String amountText = txtAmount.getText().trim();
        
        if (amountText.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "Please enter the amount.",
                "Missing Information",
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int id = getNextWaqfId();
        double amount = Double.parseDouble(amountText);
        
        Waqif waqif = new Waqif();
        waqif.setWaqifID(1);
        waqif.setFullName("Default Waqif");
        
        String status = (String) cmbStatus.getSelectedItem();
        
        currentWaqf = new CashWaqf(
            id,
            waqif,
            amount,
            amount,
            LocalDate.now(),
            status
        );
        
        // Create conditions
        String sector = (String) cmbSector.getSelectedItem();
        double maxFunding = Double.parseDouble(txtMaxFunding.getText().trim());
        int minPRI = Integer.parseInt(txtMinPRI.getText().trim());
        String targetBeneficiaries = (String) cmbTargetBeneficiaries.getSelectedItem();
        
        WaqfCondition condition = new WaqfCondition(
            id,
            currentWaqf,
            sector.equals("All Sectors") ? "All" : sector,
            maxFunding,
            minPRI,
            targetBeneficiaries
        );
        
        WaqfService.createCashWaqf(currentWaqf);
        WaqfConditionService.addCondition(condition);
        
        txtWaqfID.setText(String.valueOf(getNextWaqfId()));
        txtBalance.setText(String.format("%.2f", currentWaqf.getAvailableBalance()));
        updateTable();
        updateCurrentWaqfDisplay();
        txtAmount.setText("");
        
        StringBuilder msg = new StringBuilder();
        msg.append("Cash Waqf created successfully!\n\n");
        msg.append("ID: " + id + "\n");
        msg.append("Amount: " + amount + " QR\n");
        msg.append("Status: " + status + "\n\n");
        msg.append("📋 WAQF CONDITIONS:\n");
        msg.append("  • Allowed Sector: " + sector + "\n");
        msg.append("  • Max Funding per Project: " + maxFunding + " QR\n");
        msg.append("  • Minimum PRI: " + minPRI + "\n");
        msg.append("  • Target Beneficiaries: " + targetBeneficiaries + "\n");
        
        setStatus("Waqf ID " + id + " created successfully with conditions");
        
        JOptionPane.showMessageDialog(this,
            msg.toString(),
            "Success",
            JOptionPane.INFORMATION_MESSAGE);
    }
    
    // ===== DEPOSIT =====
    private void depositMoney() {
        if (currentWaqf == null || currentWaqf.getWaqfID() == 0) {
            JOptionPane.showMessageDialog(this,
                "Please create a new Waqf or load an existing one.",
                "No Waqf Selected",
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        String amountText = txtAmount.getText().trim();
        if (amountText.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "Please enter the deposit amount.",
                "Missing Amount",
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        double amount = Double.parseDouble(amountText);
        
        if (amount <= 0) {
            JOptionPane.showMessageDialog(this,
                "Amount must be greater than zero.",
                "Invalid Amount",
                JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        currentWaqf.addDonation(amount);
        txtBalance.setText(String.format("%.2f", currentWaqf.getAvailableBalance()));
        updateTable();
        updateCurrentWaqfDisplay();
        txtAmount.setText("");
        
        setStatus("Deposited " + amount + " QR to Waqf " + currentWaqf.getWaqfID());
        JOptionPane.showMessageDialog(this,
            "Deposit successful!\n" +
            "Amount: " + amount + " QR\n" +
            "New Balance: " + currentWaqf.getAvailableBalance() + " QR",
            "Success",
            JOptionPane.INFORMATION_MESSAGE);
    }
    
    // ===== ALLOCATE =====
    private void allocateMoney() {
        if (currentWaqf == null || currentWaqf.getWaqfID() == 0) {
            JOptionPane.showMessageDialog(this,
                "Please create a new Waqf or load an existing one.",
                "No Waqf Selected",
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        String amountText = txtAmount.getText().trim();
        if (amountText.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "Please enter the allocation amount.",
                "Missing Amount",
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        double amount = Double.parseDouble(amountText);
        
        if (amount <= 0) {
            JOptionPane.showMessageDialog(this,
                "Amount must be greater than zero.",
                "Invalid Amount",
                JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        if (amount > currentWaqf.getAvailableBalance()) {
            JOptionPane.showMessageDialog(this,
                "Insufficient balance!\n" +
                "Available: " + currentWaqf.getAvailableBalance() + " QR\n" +
                "Requested: " + amount + " QR",
                "Allocation Failed",
                JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        currentWaqf.allocateFunding(amount);
        txtBalance.setText(String.format("%.2f", currentWaqf.getAvailableBalance()));
        updateTable();
        updateCurrentWaqfDisplay();
        txtAmount.setText("");
        
        setStatus("Allocated " + amount + " QR from Waqf " + currentWaqf.getWaqfID());
        JOptionPane.showMessageDialog(this,
            "Allocation successful!\n" +
            "Amount: " + amount + " QR\n" +
            "Remaining Balance: " + currentWaqf.getAvailableBalance() + " QR",
            "Success",
            JOptionPane.INFORMATION_MESSAGE);
    }
    
    // ===== REPAYMENT =====
    private void receiveRepayment() {
        if (currentWaqf == null || currentWaqf.getWaqfID() == 0) {
            JOptionPane.showMessageDialog(this,
                "Please create a new Waqf or load an existing one.",
                "No Waqf Selected",
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        String amountText = txtAmount.getText().trim();
        if (amountText.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "Please enter the repayment amount.",
                "Missing Amount",
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        double amount = Double.parseDouble(amountText);
        
        if (amount <= 0) {
            JOptionPane.showMessageDialog(this,
                "Amount must be greater than zero.",
                "Invalid Amount",
                JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        currentWaqf.receiveRepayment(amount);
        txtBalance.setText(String.format("%.2f", currentWaqf.getAvailableBalance()));
        updateTable();
        updateCurrentWaqfDisplay();
        txtAmount.setText("");
        
        setStatus("Received repayment of " + amount + " QR for Waqf " + currentWaqf.getWaqfID());
        JOptionPane.showMessageDialog(this,
            "Repayment received!\n" +
            "Amount: " + amount + " QR\n" +
            "New Balance: " + currentWaqf.getAvailableBalance() + " QR",
            "Success",
            JOptionPane.INFORMATION_MESSAGE);
    }
    
    // ===== CLEAR FORM =====
    private void clearForm() {
        txtWaqfID.setText(String.valueOf(getNextWaqfId()));
        txtAmount.setText("");
        txtBalance.setText("");
        cmbStatus.setSelectedIndex(0);
        cmbSector.setSelectedIndex(0);
        txtMaxFunding.setText("100000");
        txtMinPRI.setText("60");
        cmbTargetBeneficiaries.setSelectedIndex(0);
        currentWaqf = new CashWaqf();
        updateCurrentWaqfDisplay();
        
        setStatus("Form cleared. Ready to create a new Waqf.");
        JOptionPane.showMessageDialog(this,
            "Form has been cleared.\n" +
            "All data in the table remains intact.",
            "Form Cleared",
            JOptionPane.INFORMATION_MESSAGE);
    }
    
    // ===== UPDATE TABLE =====
 // In CashWaqfPanel.java - Update the updateTable() method

    private void updateTable() {
        tableModel.setRowCount(0);
        
        // ===== DEBUG: Print all waqfs =====
        System.out.println("=== UPDATE TABLE ===");
        System.out.println("WaqfService.getAllWaqfs() size: " + WaqfService.getAllWaqfs().size());
        
        java.util.ArrayList<CashWaqf> waqfs = WaqfService.getAllWaqfs();
        
        // ===== DEBUG: Print each waqf =====
        for (CashWaqf w : waqfs) {
            System.out.println("  Waqf ID: " + w.getWaqfID() + 
                               " | Amount: " + w.getWaqfAmount() + 
                               " | Balance: " + w.getAvailableBalance() +
                               " | Status: " + w.getStatus());
        }
        
        if (waqfs.isEmpty()) {
            tableModel.addRow(new Object[]{"-", "-", "-", "-", "-"});
            setStatus("⚠️ No waqfs found. Create a new Waqf.");
            System.out.println("⚠️ No waqfs to display");
            return;
        }
        
        for (CashWaqf w : waqfs) {
            String sector = "N/A";
            WaqfCondition condition = WaqfConditionService.getConditionByWaqfId(w.getWaqfID());
            if (condition != null) {
                sector = condition.getAllowedSector();
            }
            
            Object[] row = {
                w.getWaqfID(),
                String.format("%.2f", w.getWaqfAmount()),
                String.format("%.2f", w.getAvailableBalance()),
                sector,
                w.getStatus()
            };
            tableModel.addRow(row);
            System.out.println("  ✅ Added row: " + w.getWaqfID());
        }
        
        setStatus("Displaying " + waqfs.size() + " waqfs");
    }
    
    private void setStatus(String message) {
        lblStatusMessage.setText("Status: " + message);
    }
    
    private ImageIcon loadImage(String path) {
        try {
            java.net.URL imgURL = getClass().getResource(path);
            if (imgURL != null) {
                return new ImageIcon(imgURL);
            }
        } catch (Exception e) {
            System.out.println("Could not load image: " + path);
        }
        return null;
    }
    private void setupRefreshButton() {
        btnForceRefresh = new JButton("🔄 Force Refresh");
        btnForceRefresh.setFont(new Font("Arial", Font.BOLD, 11));
        btnForceRefresh.setBackground(new Color(0, 102, 204));
        btnForceRefresh.setForeground(Color.BLACK);
        btnForceRefresh.setFocusPainted(false);
        btnForceRefresh.addActionListener(e -> {
            // ===== FIXED: Safe refresh with confirmation =====
            int confirm = JOptionPane.showConfirmDialog(this,
                "⚠️ Force Refresh will reload all Waqf data from CSV.\n\n" +
                "This will discard any unsaved changes in memory.\n" +
                "All current data in the table will be reloaded.\n\n" +
                "Continue?",
                "Confirm Refresh",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);
            
            if (confirm == JOptionPane.YES_OPTION) {
                // First save current data to ensure persistence
                WaqfService.saveWaqfsToFile();
                
                // Then refresh
                WaqfService.refreshWaqfs();
                updateTable();
                
                // Update current Waqf display
                if (currentWaqf != null && currentWaqf.getWaqfID() > 0) {
                    CashWaqf refreshed = WaqfService.searchWaqf(currentWaqf.getWaqfID());
                    if (refreshed != null) {
                        currentWaqf = refreshed;
                        txtBalance.setText(String.format("%.2f", currentWaqf.getAvailableBalance()));
                        updateCurrentWaqfDisplay();
                    }
                }
                
                setStatus("🔄 Force refreshed! Found " + WaqfService.getAllWaqfs().size() + " waqfs");
                JOptionPane.showMessageDialog(this,
                    "✅ Waqf data refreshed successfully!\n" +
                    "Total waqfs found: " + WaqfService.getAllWaqfs().size(),
                    "Refresh Complete",
                    JOptionPane.INFORMATION_MESSAGE);
            }
        });
    }
}