package UI;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.time.LocalDate;
import Model.*;
import Service.*;

public class LoanPanel extends JPanel implements ActionListener {
    // ===== FORM FIELDS =====
    private JTextField txtLoanID, txtAppID, txtAmount;
    private JComboBox<String> cmbWaqf;
    
    // ===== TABLES =====
    private JTable table, appTable;
    private DefaultTableModel tableModel, appTableModel;
    
    // ===== BUTTONS =====
    private JButton btnCreateLoan, btnRepayment, btnViewAll, btnLoad, btnRefreshWaqfs;
    private JButton btnRefreshApps;
    private JButton btnFindMatchingWaqfs;
    
    // ===== CURRENT LOAN =====
    private QardHasan currentLoan;
    
    // ===== LABELS & TEXT AREAS =====
    private JLabel lblCurrentLoan, lblStatusMessage;
    private JTextArea txtLoanDetails;
    private JTextArea txtWaqfMatchInfo;
    private JLabel appCountLabel;

    public LoanPanel() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Title
        JLabel lblTitle = new JLabel("NAMAA SMART WAQF PLATFORM (Qard Hasan Loans)", JLabel.CENTER);
        lblTitle.setFont(new Font("Arial", Font.BOLD, 20));
        lblTitle.setForeground(new Color(0, 102, 204));
        add(lblTitle, BorderLayout.NORTH);

        // Split Panel
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setDividerLocation(500);
        splitPane.setResizeWeight(0.45);

        // ===== LEFT PANEL =====
        JPanel leftPanel = new JPanel(new BorderLayout(5, 5));

        // ---- Form Panel ----
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createEtchedBorder(),
            "Loan Details",
            TitledBorder.LEFT,
            TitledBorder.TOP,
            new Font("Arial", Font.BOLD, 12)
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 4, 4, 4);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Row 1: Loan ID (Auto)
        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0.3;
        JLabel lblLoanID = new JLabel("Loan ID:");
        lblLoanID.setFont(new Font("Arial", Font.BOLD, 12));
        formPanel.add(lblLoanID, gbc);

        gbc.gridx = 1; gbc.weightx = 0.7;
        txtLoanID = new JTextField(10);
        txtLoanID.setEditable(false);
        txtLoanID.setBackground(new Color(240, 240, 240));
        txtLoanID.setPreferredSize(new Dimension(120, 28));
        txtLoanID.setText(String.valueOf(getNextLoanId()));
        formPanel.add(txtLoanID, gbc);

        // Row 2: Application ID
        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0.3;
        JLabel lblAppID = new JLabel("Application ID:");
        lblAppID.setFont(new Font("Arial", Font.BOLD, 12));
        formPanel.add(lblAppID, gbc);

        gbc.gridx = 1; gbc.weightx = 0.7;
        txtAppID = new JTextField(10);
        txtAppID.setPreferredSize(new Dimension(120, 28));
        formPanel.add(txtAppID, gbc);

        // Row 3: Amount
        gbc.gridx = 0; gbc.gridy = 2; gbc.weightx = 0.3;
        JLabel lblAmount = new JLabel("Amount (QR):");
        lblAmount.setFont(new Font("Arial", Font.BOLD, 12));
        formPanel.add(lblAmount, gbc);

        gbc.gridx = 1; gbc.weightx = 0.7;
        txtAmount = new JTextField(10);
        txtAmount.setPreferredSize(new Dimension(120, 28));
        formPanel.add(txtAmount, gbc);

        // Row 4: Source Waqf (Admin only - hidden in beneficiary view)
        gbc.gridx = 0; gbc.gridy = 3; gbc.weightx = 0.3;
        JLabel lblWaqf = new JLabel("Source Waqf:");
        lblWaqf.setFont(new Font("Arial", Font.BOLD, 12));
        formPanel.add(lblWaqf, gbc);

        gbc.gridx = 1; gbc.weightx = 0.7;
        JPanel waqfPanel = new JPanel(new BorderLayout(5, 0));
        cmbWaqf = new JComboBox<>();
        cmbWaqf.setFont(new Font("Arial", Font.PLAIN, 12));
        cmbWaqf.setPreferredSize(new Dimension(100, 28));
        waqfPanel.add(cmbWaqf, BorderLayout.CENTER);
        
        btnRefreshWaqfs = new JButton("↻");
        btnRefreshWaqfs.setFont(new Font("Arial", Font.BOLD, 14));
        btnRefreshWaqfs.setPreferredSize(new Dimension(30, 28));
        btnRefreshWaqfs.setToolTipText("Refresh Waqf list");
        btnRefreshWaqfs.addActionListener(this);
        waqfPanel.add(btnRefreshWaqfs, BorderLayout.EAST);
        
        formPanel.add(waqfPanel, gbc);

        leftPanel.add(formPanel, BorderLayout.NORTH);

        // ---- Waqf Matching Info Panel (Admin only) ----
        JPanel matchPanel = new JPanel(new BorderLayout(5, 5));
        matchPanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createEtchedBorder(),
            "🔍 Waqf Matching Information",
            TitledBorder.LEFT,
            TitledBorder.TOP,
            new Font("Arial", Font.BOLD, 11)
        ));
        matchPanel.setPreferredSize(new Dimension(400, 120));
        
        txtWaqfMatchInfo = new JTextArea(4, 20);
        txtWaqfMatchInfo.setEditable(false);
        txtWaqfMatchInfo.setFont(new Font("Monospaced", Font.PLAIN, 11));
        txtWaqfMatchInfo.setBackground(new Color(255, 255, 240));
        txtWaqfMatchInfo.setText("Select an approved application to see matching Waqfs");
        
        JScrollPane scrollMatch = new JScrollPane(txtWaqfMatchInfo);
        scrollMatch.setPreferredSize(new Dimension(380, 70));
        matchPanel.add(scrollMatch, BorderLayout.CENTER);
        
        btnFindMatchingWaqfs = new JButton("🔍 Find Matching Waqfs");
        btnFindMatchingWaqfs.setFont(new Font("Arial", Font.BOLD, 11));
        btnFindMatchingWaqfs.setBackground(new Color(153, 0, 153));
        btnFindMatchingWaqfs.setForeground(Color.BLACK);
        btnFindMatchingWaqfs.setFocusPainted(false);
        btnFindMatchingWaqfs.setPreferredSize(new Dimension(160, 28));
        btnFindMatchingWaqfs.addActionListener(this);
        btnFindMatchingWaqfs.setActionCommand("findMatching");
        matchPanel.add(btnFindMatchingWaqfs, BorderLayout.SOUTH);
        
        leftPanel.add(matchPanel, BorderLayout.CENTER);

        // ---- Loan Details Display ----
        JPanel detailsPanel = new JPanel(new BorderLayout());
        detailsPanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createEtchedBorder(),
            "Loan Information",
            TitledBorder.LEFT,
            TitledBorder.TOP,
            new Font("Arial", Font.BOLD, 11)
        ));
        detailsPanel.setPreferredSize(new Dimension(400, 100));
        
        txtLoanDetails = new JTextArea(4, 20);
        txtLoanDetails.setEditable(false);
        txtLoanDetails.setFont(new Font("Monospaced", Font.PLAIN, 11));
        txtLoanDetails.setBackground(new Color(255, 255, 240));
        txtLoanDetails.setText("No loan loaded\nEnter Application ID and click Create");
        
        JScrollPane scrollDetails = new JScrollPane(txtLoanDetails);
        scrollDetails.setPreferredSize(new Dimension(380, 80));
        detailsPanel.add(scrollDetails, BorderLayout.CENTER);
        leftPanel.add(detailsPanel, BorderLayout.SOUTH);

        // ---- Current Loan Display ----
        JPanel currentPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        currentPanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createEtchedBorder(),
            "Current Loan",
            TitledBorder.LEFT,
            TitledBorder.TOP,
            new Font("Arial", Font.BOLD, 11)
        ));
        lblCurrentLoan = new JLabel("None selected");
        lblCurrentLoan.setFont(new Font("Arial", Font.BOLD, 12));
        lblCurrentLoan.setForeground(Color.RED);
        currentPanel.add(lblCurrentLoan);
        leftPanel.add(currentPanel, BorderLayout.SOUTH);

        // ===== RIGHT PANEL =====
        JPanel rightPanel = new JPanel(new BorderLayout(5, 5));

        // Tabbed pane
        JTabbedPane tabbedPane = new JTabbedPane();

        // === Applications Tab ===
        JPanel appPanel = new JPanel(new BorderLayout(5, 5));
        
        JPanel appHeaderPanel = new JPanel(new BorderLayout());
        JLabel appTableTitle = new JLabel("✅ Approved Applications (Eligible for Loans)", JLabel.CENTER);
        appTableTitle.setFont(new Font("Arial", Font.BOLD, 12));
        appTableTitle.setForeground(new Color(0, 153, 76));
        appHeaderPanel.add(appTableTitle, BorderLayout.CENTER);
        
        btnRefreshApps = new JButton("🔄 Refresh Approved Apps");
        btnRefreshApps.setFont(new Font("Arial", Font.PLAIN, 10));
        btnRefreshApps.setBackground(new Color(0, 102, 204));
        btnRefreshApps.setForeground(Color.BLACK);
        btnRefreshApps.setFocusPainted(false);
        btnRefreshApps.addActionListener(this);
        btnRefreshApps.setActionCommand("refreshApps");
        appHeaderPanel.add(btnRefreshApps, BorderLayout.EAST);
        
        appPanel.add(appHeaderPanel, BorderLayout.NORTH);

        String[] appColumns = {"ID", "Project", "Sector", "Amount (QR)", "Beneficiary"};
        appTableModel = new DefaultTableModel(appColumns, 0) {
            @Override
            public boolean isCellEditable(int row, int col) {
                return false;
            }
        };

        appTable = new JTable(appTableModel);
        appTable.setFont(new Font("Arial", Font.PLAIN, 12));
        appTable.setRowHeight(28);
        appTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        appTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        appTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int row = appTable.getSelectedRow();
                if (row >= 0) {
                    int id = (int) appTableModel.getValueAt(row, 0);
                    txtAppID.setText(String.valueOf(id));
                    for (FundingApplication app : FundingService.getApplications()) {
                        if (app.getApplicationID() == id) {
                            txtAmount.setText(String.valueOf(app.getRequestedAmount()));
                            showMatchingWaqfs(app);
                            break;
                        }
                    }
                    setStatus("Selected Application ID: " + id);
                }
            }
        });

        JScrollPane appScroll = new JScrollPane(appTable);
        appScroll.setBorder(BorderFactory.createEtchedBorder());
        appPanel.add(appScroll, BorderLayout.CENTER);

        // Status summary for applications
        JPanel appSummary = new JPanel(new FlowLayout(FlowLayout.LEFT));
        appSummary.setBackground(Color.WHITE);
        appCountLabel = new JLabel("Approved applications ready for loans: 0");
        appCountLabel.setFont(new Font("Arial", Font.PLAIN, 11));
        appCountLabel.setForeground(Color.GRAY);
        appSummary.add(appCountLabel);
        appPanel.add(appSummary, BorderLayout.SOUTH);

        tabbedPane.addTab("📋 Approved Apps", appPanel);

        // === Loans Tab ===
        JPanel loanPanel = new JPanel(new BorderLayout(5, 5));
        
        JLabel loanTableTitle = new JLabel("All Loans", JLabel.CENTER);
        loanTableTitle.setFont(new Font("Arial", Font.BOLD, 12));
        loanPanel.add(loanTableTitle, BorderLayout.NORTH);

        // ===== FIXED: Removed "Waqf Source" from beneficiary view =====
        String[] loanColumns = {"ID", "Amount (QR)", "Status", "Due Date"};
        tableModel = new DefaultTableModel(loanColumns, 0) {
            @Override
            public boolean isCellEditable(int row, int col) {
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
                    loadLoanById(id);
                }
            }
        });

        table.setDefaultRenderer(Object.class, new javax.swing.table.DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                
                if (column == 2) {
                    String status = (String) value;
                    if ("Active".equals(status)) {
                        c.setBackground(new Color(0, 200, 0));
                        c.setForeground(Color.WHITE);
                    } else if ("Completed".equals(status)) {
                        c.setBackground(new Color(0, 100, 200));
                        c.setForeground(Color.WHITE);
                    } else if ("Defaulted".equals(status)) {
                        c.setBackground(new Color(200, 0, 0));
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

        JScrollPane loanScroll = new JScrollPane(table);
        loanScroll.setBorder(BorderFactory.createEtchedBorder());
        loanPanel.add(loanScroll, BorderLayout.CENTER);

        tabbedPane.addTab("💰 All Loans", loanPanel);

        rightPanel.add(tabbedPane, BorderLayout.CENTER);

        // ===== BUTTONS =====
        JPanel buttonPanel = new JPanel(new GridLayout(2, 2, 5, 5));
        buttonPanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createEtchedBorder(),
            "Actions",
            TitledBorder.LEFT,
            TitledBorder.TOP,
            new Font("Arial", Font.BOLD, 12)
        ));

        btnCreateLoan = new JButton("💰 Create Loan");
        btnRepayment = new JButton("💳 Add Repayment");
        btnViewAll = new JButton("🔄 Refresh");
        btnLoad = new JButton("📋 Load Loan");

        JButton[] buttons = {btnCreateLoan, btnRepayment, btnViewAll, btnLoad};
        Color[] colors = {
            new Color(0, 153, 76),   // Create Loan - Green
            new Color(0, 102, 204),  // Repayment - Blue
            new Color(255, 153, 0),  // Refresh - Orange
            new Color(153, 0, 153)   // Load - Purple
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

        rightPanel.add(buttonPanel, BorderLayout.SOUTH);

        splitPane.setLeftComponent(leftPanel);
        splitPane.setRightComponent(rightPanel);
        add(splitPane, BorderLayout.CENTER);

        // Status Bar
        JPanel statusBar = new JPanel(new FlowLayout(FlowLayout.LEFT));
        statusBar.setBorder(BorderFactory.createEtchedBorder());
        lblStatusMessage = new JLabel("Ready - Select an approved application and create a loan");
        lblStatusMessage.setFont(new Font("Arial", Font.PLAIN, 11));
        statusBar.add(lblStatusMessage);
        add(statusBar, BorderLayout.SOUTH);

        // Initialize
        currentLoan = new QardHasan();
        loadWaqfs();
        updateTables();
        updateCurrentDisplay();
    }

    // ===== GET NEXT LOAN ID =====
    private int getNextLoanId() {
        int maxId = 0;
        for (QardHasan loan : LoanService.getLoans()) {
            if (loan.getLoanID() > maxId) {
                maxId = loan.getLoanID();
            }
        }
        return maxId + 1;
    }

    // ===== LOAD WAQFS INTO COMBOBOX =====
    private void loadWaqfs() {
        cmbWaqf.removeAllItems();
        java.util.ArrayList<CashWaqf> waqfs = WaqfService.getAllWaqfs();
        
        if (waqfs.isEmpty()) {
            cmbWaqf.addItem("No Waqfs available - Create one first");
            setStatus("⚠️ No Cash Waqf found. Please create a Waqf first.");
        } else {
            boolean hasActiveWaqf = false;
            for (CashWaqf w : waqfs) {
                if (w.getStatus().equals("Active") && w.getAvailableBalance() > 0) {
                    WaqfCondition condition = WaqfConditionService.getConditionByWaqfId(w.getWaqfID());
                    String sectorInfo = "";
                    if (condition != null) {
                        sectorInfo = " | Sector: " + condition.getAllowedSector() + 
                                    " | Min PRI: " + condition.getMinimumPRI();
                    }
                    String displayText = "ID: " + w.getWaqfID() + 
                                        " | Balance: " + String.format("%.2f", w.getAvailableBalance()) + 
                                        " QR" + sectorInfo;
                    cmbWaqf.addItem(displayText);
                    hasActiveWaqf = true;
                }
            }
            
            if (!hasActiveWaqf) {
                cmbWaqf.addItem("No active waqfs with balance");
                setStatus("⚠️ No active Waqfs with available balance");
            } else {
                setStatus("Loaded " + cmbWaqf.getItemCount() + " active Waqfs");
            }
        }
    }

    // ===== GET SELECTED WAQF =====
    private CashWaqf getSelectedWaqf() {
        String selected = (String) cmbWaqf.getSelectedItem();
        
        if (selected == null || selected.equals("No Waqfs available - Create one first") ||
            selected.equals("No active waqfs with balance")) {
            return null;
        }
        
        try {
            int start = selected.indexOf("ID: ") + 4;
            int end = selected.indexOf(" |");
            if (start > 0 && end > start) {
                int waqfId = Integer.parseInt(selected.substring(start, end));
                return WaqfService.searchWaqf(waqfId);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    // ===== SHOW MATCHING WAQFS =====
    private void showMatchingWaqfs(FundingApplication app) {
        if (app == null) {
            txtWaqfMatchInfo.setText("No application selected");
            return;
        }
        
        StringBuilder info = new StringBuilder();
        info.append("🔍 MATCHING WAQFS FOR APPLICATION #" + app.getApplicationID() + "\n");
        info.append("─────────────────────────────────────────────────────\n");
        info.append("Project: " + app.getProject().getProjectName() + "\n");
        info.append("Sector: " + app.getProject().getSector() + "\n");
        info.append("Amount: " + String.format("%.2f", app.getRequestedAmount()) + " QR\n");
        
        ProjectAssessment assessment = AssessmentService.searchAssessmentByApplication(app.getApplicationID());
        double pri = assessment != null ? assessment.getPriScore() : 0;
        info.append("PRI Score: " + String.format("%.2f", pri) + "\n\n");
        
        info.append("AVAILABLE WAQFS:\n");
        info.append("─────────────────────────────────────────────────────\n");
        
        java.util.ArrayList<CashWaqf> waqfs = WaqfService.getAllWaqfs();
        boolean foundMatch = false;
        int matchCount = 0;
        
        for (CashWaqf w : waqfs) {
            if (!w.getStatus().equals("Active") || w.getAvailableBalance() <= 0) continue;
            
            WaqfCondition condition = WaqfConditionService.getConditionByWaqfId(w.getWaqfID());
            
            if (condition != null) {
                boolean sectorMatches = condition.getAllowedSector().equals("All") || 
                                       condition.getAllowedSector().equalsIgnoreCase(app.getProject().getSector());
                boolean priMatches = pri >= condition.getMinimumPRI();
                boolean amountMatches = app.getRequestedAmount() <= condition.getMaximumFunding();
                
                String targetGroup = condition.getTargetBeneficiaries();
                boolean targetMatches = targetGroup.equals("All Beneficiaries") || 
                                       (app.getBeneficiary() != null && 
                                        app.getBeneficiary().getTargetGroup() != null &&
                                        targetGroup.equalsIgnoreCase(app.getBeneficiary().getTargetGroup()));
                
                if (sectorMatches && priMatches && amountMatches && targetMatches) {
                    foundMatch = true;
                    matchCount++;
                    info.append("✅ WAQF #" + w.getWaqfID() + " - MATCHES!\n");
                    info.append("   Balance: " + String.format("%.2f", w.getAvailableBalance()) + " QR\n");
                    info.append("   Sector: " + condition.getAllowedSector() + "\n");
                    info.append("   Min PRI: " + condition.getMinimumPRI() + "\n");
                    info.append("   Max Funding: " + String.format("%.2f", condition.getMaximumFunding()) + " QR\n");
                    info.append("   Target: " + condition.getTargetBeneficiaries() + "\n\n");
                } else {
                    info.append("❌ WAQF #" + w.getWaqfID() + " - Does NOT match\n");
                    if (!sectorMatches) info.append("   ✗ Sector mismatch (requires: " + condition.getAllowedSector() + ")\n");
                    if (!priMatches) info.append("   ✗ PRI too low (requires: " + condition.getMinimumPRI() + ")\n");
                    if (!amountMatches) info.append("   ✗ Amount exceeds max (max: " + String.format("%.2f", condition.getMaximumFunding()) + " QR)\n");
                    if (!targetMatches) info.append("   ✗ Target group mismatch (requires: " + condition.getTargetBeneficiaries() + ")\n");
                    info.append("\n");
                }
            } else {
                info.append("⚠️ WAQF #" + w.getWaqfID() + " - No conditions set (can fund any project)\n");
                info.append("   Balance: " + String.format("%.2f", w.getAvailableBalance()) + " QR\n\n");
                foundMatch = true;
                matchCount++;
            }
        }
        
        if (!foundMatch) {
            info.append("❌ No matching Waqfs found for this application.\n");
            info.append("💡 Consider creating a Waqf with appropriate conditions.\n");
        } else {
            info.append("✅ Found " + matchCount + " matching Waqf(s)\n");
            info.append("💡 Select a Waqf from the dropdown above and create the loan.\n");
        }
        
        txtWaqfMatchInfo.setText(info.toString());
        txtWaqfMatchInfo.setCaretPosition(0);
    }

    // ===== UPDATE TABLES =====
    private void updateTables() {
        updateApprovedApplicationsTable();
        updateLoansTable();
    }

    // ===== UPDATE APPROVED APPLICATIONS TABLE =====
    private void updateApprovedApplicationsTable() {
        appTableModel.setRowCount(0);
        int approvedCount = 0;
        
        for (FundingApplication app : FundingService.getApplications()) {
            if (app.getStatus() == ApplicationStatus.APPROVED) {
                String beneficiaryName = app.getBeneficiary() != null ? 
                    app.getBeneficiary().getFullName() : "Unknown";
                Object[] row = {
                    app.getApplicationID(),
                    app.getProject().getProjectName(),
                    app.getProject().getSector(),
                    String.format("%.2f", app.getRequestedAmount()),
                    beneficiaryName
                };
                appTableModel.addRow(row);
                approvedCount++;
            }
        }
        
        if (appCountLabel != null) {
            appCountLabel.setText("Approved applications ready for loans: " + approvedCount);
        }
        
        if (approvedCount == 0) {
            setStatus("⚠️ No approved applications found. Approve applications in Committee panel first.");
        } else {
            setStatus("✅ " + approvedCount + " approved applications ready for loans");
        }
    }

    // ===== UPDATE LOANS TABLE =====
    private void updateLoansTable() {
        tableModel.setRowCount(0);
        
        // ===== DEBUG: Print all loans =====
        System.out.println("=== UPDATING LOANS TABLE ===");
        System.out.println("Total loans in system: " + LoanService.getLoans().size());
        
        for (QardHasan loan : LoanService.getLoans()) {
            System.out.println("  Loan ID: " + loan.getLoanID() + 
                               " | Amount: " + loan.getLoanAmount() +
                               " | Status: " + loan.getStatus());
            
            String statusEmoji = loan.getStatus().equals("Active") ? "✅" :
                                loan.getStatus().equals("Completed") ? "📌" : "❌";
            
            String waqfInfo = "";
            if (loan.getCashWaqf() != null) {
                waqfInfo = "Waqf #" + loan.getCashWaqf().getWaqfID();
            }
            
            Object[] row = {
                loan.getLoanID(),
                String.format("%.2f", loan.getLoanAmount()),
                statusEmoji + " " + loan.getStatus(),
                loan.getDueDate(),
                waqfInfo
            };
            tableModel.addRow(row);
        }
        
        if (LoanService.getLoans().isEmpty()) {
            tableModel.addRow(new Object[]{"-", "-", "No loans found", "-", "-"});
        }
    }

    // ===== LOAD LOAN BY ID =====
    private void loadLoanById(int id) {
        System.out.println("🔍 Loading loan ID: " + id);
        
        for (QardHasan loan : LoanService.getLoans()) {
            if (loan.getLoanID() == id) {
                currentLoan = loan;
                txtLoanID.setText(String.valueOf(currentLoan.getLoanID()));
                txtAppID.setText(String.valueOf(currentLoan.getApplication().getApplicationID()));
                txtAmount.setText(String.valueOf(currentLoan.getLoanAmount()));
                
                // ===== FIXED: Update the display immediately =====
                updateCurrentDisplay();
                setStatus("✅ Loaded Loan ID: " + id);
                
                System.out.println("✅ Loan loaded: " + currentLoan.getLoanID() + 
                                   " | Amount: " + currentLoan.getLoanAmount() +
                                   " | Status: " + currentLoan.getStatus());
                
                // ===== FIXED: Show success with details =====
                JOptionPane.showMessageDialog(this,
                    "✅ Loan loaded successfully!\n\n" +
                    "Loan ID: " + currentLoan.getLoanID() + "\n" +
                    "Application: " + currentLoan.getApplication().getApplicationID() + "\n" +
                    "Amount: " + String.format("%.2f", currentLoan.getLoanAmount()) + " QR\n" +
                    "Status: " + currentLoan.getStatus() + "\n" +
                    "Due Date: " + currentLoan.getDueDate() + "\n" +
                    "Source Waqf: " + (currentLoan.getCashWaqf() != null ? 
                                       "Waqf #" + currentLoan.getCashWaqf().getWaqfID() : "N/A"),
                    "Loan Loaded",
                    JOptionPane.INFORMATION_MESSAGE);
                return;
            }
        }
        
        JOptionPane.showMessageDialog(this,
            "❌ Loan ID " + id + " not found!",
            "Error",
            JOptionPane.ERROR_MESSAGE);
    }

    // ===== UPDATE CURRENT DISPLAY =====
 // In LoanPanel.java

    private void updateCurrentDisplay() {
        System.out.println("🔄 Updating current display...");
        
        if (currentLoan != null && currentLoan.getLoanID() > 0) {
            String statusEmoji = currentLoan.getStatus().equals("Active") ? "✅" :
                                currentLoan.getStatus().equals("Completed") ? "📌" : "❌";
            
            // Update the current loan label
            lblCurrentLoan.setText("ID: " + currentLoan.getLoanID() + 
                                  " | Amount: " + currentLoan.getLoanAmount() +
                                  " QR | Status: " + statusEmoji + " " + currentLoan.getStatus());
            lblCurrentLoan.setForeground(new Color(0, 102, 204));
            
            // ===== FIXED: Update the loan details text area =====
            String waqfInfo = currentLoan.getCashWaqf() != null ? 
                "Waqf #" + currentLoan.getCashWaqf().getWaqfID() : "N/A";
            
            // Calculate remaining balance
            double repaid = LoanService.getTotalRepaidByLoan(currentLoan.getLoanID());
            double remaining = currentLoan.getLoanAmount() - repaid;
            
            txtLoanDetails.setText(
                "╔═══════════════════════════════════════════════════════╗\n" +
                "║                  LOAN DETAILS                        ║\n" +
                "╠═══════════════════════════════════════════════════════╣\n" +
                "║  Loan ID:        " + String.format("%-28d", currentLoan.getLoanID()) + "║\n" +
                "║  Application:    " + String.format("%-28d", currentLoan.getApplication().getApplicationID()) + "║\n" +
                "║  Amount:         " + String.format("%-28.2f", currentLoan.getLoanAmount()) + "║\n" +
                "║  Repaid:         " + String.format("%-28.2f", repaid) + "║\n" +
                "║  Remaining:      " + String.format("%-28.2f", remaining) + "║\n" +
                "║  Status:         " + String.format("%-28s", currentLoan.getStatus()) + "║\n" +
                "║  Issue Date:     " + String.format("%-28s", currentLoan.getIssueDate().toString()) + "║\n" +
                "║  Due Date:       " + String.format("%-28s", currentLoan.getDueDate().toString()) + "║\n" +
                "║  Source Waqf:    " + String.format("%-28s", waqfInfo) + "║\n" +
                "╚═══════════════════════════════════════════════════════╝\n" +
                "\n" +
                "💡 To add a repayment, enter the amount and click 'Add Repayment'"
            );
            
            System.out.println("✅ Display updated for loan: " + currentLoan.getLoanID());
            
        } else {
            lblCurrentLoan.setText("None selected");
            lblCurrentLoan.setForeground(Color.RED);
            txtLoanDetails.setText(
                "╔═══════════════════════════════════════════════════════╗\n" +
                "║                  NO LOAN LOADED                      ║\n" +
                "╠═══════════════════════════════════════════════════════╣\n" +
                "║                                                       ║\n" +
                "║  📋 To load a loan:                                   ║\n" +
                "║  1. Go to 'All Loans' tab                            ║\n" +
                "║  2. Click on a loan row                              ║\n" +
                "║  3. Click 'Load Loan' button                         ║\n" +
                "║                                                       ║\n" +
                "║  OR                                                   ║\n" +
                "║                                                       ║\n" +
                "║  Enter Loan ID and click 'Load Loan'                 ║\n" +
                "╚═══════════════════════════════════════════════════════╝"
            );
        }
    }

    // ===== ACTION PERFORMED =====
    @Override
    public void actionPerformed(ActionEvent e) {
        try {
            if (e.getSource() == btnCreateLoan) {
                createLoan();
            } else if (e.getSource() == btnRepayment) {
                addRepayment();
            } else if (e.getSource() == btnViewAll) {
                loadWaqfs();
                updateTables();
                setStatus("Refreshed all data");
            } else if (e.getSource() == btnLoad) {
                loadSelectedLoan();
            } else if (e.getSource() == btnRefreshWaqfs) {
                loadWaqfs();
                JOptionPane.showMessageDialog(this,
                    "Waqf list refreshed!\nFound " + cmbWaqf.getItemCount() + " active waqfs.",
                    "Refreshed",
                    JOptionPane.INFORMATION_MESSAGE);
            } else if (e.getSource() == btnRefreshApps) {
                updateApprovedApplicationsTable();
                setStatus("✅ Approved applications refreshed");
            } else if (e.getSource() == btnFindMatchingWaqfs) {
                String appIdText = txtAppID.getText().trim();
                if (appIdText.isEmpty()) {
                    JOptionPane.showMessageDialog(this,
                        "Please select an application first.",
                        "No Application",
                        JOptionPane.WARNING_MESSAGE);
                    return;
                }
                int appId = Integer.parseInt(appIdText);
                FundingApplication app = FundingService.searchApplication(appId);
                if (app != null) {
                    showMatchingWaqfs(app);
                    setStatus("🔍 Found matching Waqfs for Application #" + appId);
                } else {
                    JOptionPane.showMessageDialog(this,
                        "Application not found!",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                }
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                "Error: " + ex.getMessage(),
                "Operation Failed",
                JOptionPane.ERROR_MESSAGE);
            setStatus("Error: " + ex.getMessage());
        }
    }

    // ===== CREATE LOAN =====
    private void createLoan() {
        String appIdText = txtAppID.getText().trim();
        String amountText = txtAmount.getText().trim();

        if (appIdText.isEmpty() || amountText.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "Please fill all fields.",
                "Missing Information",
                JOptionPane.WARNING_MESSAGE);
            return;
        }

        int appId = Integer.parseInt(appIdText);
        double amount = Double.parseDouble(amountText);

        FundingApplication app = FundingService.searchApplication(appId);
        if (app == null) {
            JOptionPane.showMessageDialog(this,
                "❌ Application ID " + appId + " not found!",
                "Error",
                JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (app.getStatus() != ApplicationStatus.APPROVED) {
            JOptionPane.showMessageDialog(this,
                "❌ Cannot create loan for this application!\n\n" +
                "Current Status: " + app.getStatus() + "\n\n" +
                "📌 Only applications with status 'APPROVED' can get loans.",
                "Application Not Approved",
                JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Check if loan already exists
        for (QardHasan existingLoan : LoanService.getLoans()) {
            if (existingLoan.getApplication().getApplicationID() == appId) {
                JOptionPane.showMessageDialog(this,
                    "❌ A loan already exists for Application ID " + appId + "!\n" +
                    "Loan ID: " + existingLoan.getLoanID(),
                    "Duplicate Loan",
                    JOptionPane.ERROR_MESSAGE);
                return;
            }
        }

        CashWaqf waqf = getSelectedWaqf();
        if (waqf == null) {
            JOptionPane.showMessageDialog(this,
                "❌ Please select a valid Source Waqf.",
                "No Waqf Selected",
                JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (amount > waqf.getAvailableBalance()) {
            JOptionPane.showMessageDialog(this,
                "❌ Insufficient balance in Waqf!\n" +
                "Available: " + waqf.getAvailableBalance() + " QR\n" +
                "Requested: " + amount + " QR",
                "Insufficient Funds",
                JOptionPane.ERROR_MESSAGE);
            return;
        }

        // ===== CREATE LOAN =====
        int loanId = getNextLoanId();
        currentLoan = new QardHasan(
            loanId,
            waqf,
            app,
            amount,
            LocalDate.now(),
            LocalDate.now().plusMonths(12),
            "Active"
        );

        // Allocate funds from Waqf
        boolean allocated = waqf.allocateFunding(amount);
        if (!allocated) {
            JOptionPane.showMessageDialog(this,
                "❌ Failed to allocate funds from Waqf!",
                "Allocation Failed",
                JOptionPane.ERROR_MESSAGE);
            return;
        }

        // ===== FIXED: Save loan and force refresh =====
        LoanService.createLoan(currentLoan);
        
        // ===== DEBUG: Print loan count =====
        System.out.println("=== LOAN CREATED ===");
        System.out.println("Loan ID: " + loanId);
        System.out.println("Total loans in system: " + LoanService.getLoans().size());
        System.out.println("Loans saved to loans.csv");
        
        // ===== FORCE REFRESH TABLES =====
        updateTables();
        
        txtLoanID.setText(String.valueOf(getNextLoanId()));
        updateCurrentDisplay();
        txtAmount.setText("");
        loadWaqfs();

        setStatus("✅ Loan ID " + loanId + " created successfully for Application " + appId);

        JOptionPane.showMessageDialog(this,
            "✅ Loan created successfully!\n" +
            "Loan ID: " + loanId + "\n" +
            "Amount: " + amount + " QR\n" +
            "Source Waqf: " + waqf.getWaqfID() + "\n" +
            "Waqf Balance: " + waqf.getAvailableBalance() + " QR\n\n" +
            "📌 The loan has been saved to loans.csv",
            "Success",
            JOptionPane.INFORMATION_MESSAGE);
    }

    // ===== ADD REPAYMENT =====
    private void addRepayment() {
        if (currentLoan == null || currentLoan.getLoanID() == 0) {
            JOptionPane.showMessageDialog(this,
                "Please create a new loan or load an existing one.",
                "No Loan Selected",
                JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (!currentLoan.getStatus().equals("Active")) {
            JOptionPane.showMessageDialog(this,
                "❌ Cannot add repayment to a loan with status: " + currentLoan.getStatus(),
                "Invalid Status",
                JOptionPane.ERROR_MESSAGE);
            return;
        }

        String amountText = txtAmount.getText().trim();
        if (amountText.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "Please enter repayment amount.",
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

        Repayment repayment = new Repayment(
            (int) (System.currentTimeMillis() % 10000),
            currentLoan,
            amount,
            LocalDate.now()
        );

        LoanService.addRepayment(repayment);
        
        updateTables();
        updateCurrentDisplay();
        txtAmount.setText("");
        setStatus("💳 Repayment of " + amount + " QR added to Loan " + currentLoan.getLoanID());
        
        JOptionPane.showMessageDialog(this,
            "✅ Repayment added successfully!\n" +
            "Amount: " + amount + " QR",
            "Success",
            JOptionPane.INFORMATION_MESSAGE);
    }

    // ===== LOAD SELECTED LOAN =====
 // In LoanPanel.java

    private void loadSelectedLoan() {
        int selectedRow = table.getSelectedRow();
        
        // ===== DEBUG: Print selected row =====
        System.out.println("Selected row: " + selectedRow);
        
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this,
                "Please select a loan from the table.\n\n" +
                "1. Go to the 'All Loans' tab\n" +
                "2. Click on a loan row to select it\n" +
                "3. Then click 'Load Loan'",
                "No Selection",
                JOptionPane.WARNING_MESSAGE);
            return;
        }

        int modelRow = table.convertRowIndexToModel(selectedRow);
        int id = (int) tableModel.getValueAt(modelRow, 0);
        
        System.out.println("🔍 Loading loan from table: ID=" + id);
        loadLoanById(id);
    }
    private void setStatus(String message) {
        if (lblStatusMessage != null) {
            lblStatusMessage.setText("Status: " + message);
        }
    }
 // In LoanPanel.java

    
}