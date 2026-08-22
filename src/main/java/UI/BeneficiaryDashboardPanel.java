package UI;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.time.format.DateTimeFormatter;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import Model.*;
import Service.*;

public class BeneficiaryDashboardPanel extends JPanel implements ActionListener {
    private Beneficiary beneficiary;
    
    // ===== STATS LABELS =====
    private JLabel lblTotalLoans, lblActiveLoans, lblTotalRepaid;
    private JLabel lblRemainingBalance, lblNextPayment, lblApplicationStatus;
    private JLabel lblBeneficiaryName;
    
    // ===== APPLICATION PANEL =====
    private JTextField txtProjectName, txtAmount, txtDuration, txtExpectedBeneficiaries;
    private JComboBox<String> cmbSector, cmbLocation;
    private JTextArea txtDescription;
    private JButton btnSubmitApplication, btnUploadBusinessPlan, btnUploadFinancials, btnUploadDocuments;
    private JLabel lblBusinessPlan, lblFinancials, lblDocuments;
    private JTable appTable;
    private DefaultTableModel appModel;
    private JPanel summaryPanel;
    private FundingApplication currentApp;
    
    // ===== LOAN & REPAYMENT PANEL =====
    private JTable loanTable, repaymentTable;
    private DefaultTableModel loanModel, repaymentModel;
    private JButton btnAddRepayment;
    
    // ===== REPORTS PANEL =====
    private JTextArea txtReport;
    private JButton btnUploadReport, btnUploadPhotos, btnUploadInvoices, btnSubmitReport;
    private JLabel lblReportStatus;
    private JComboBox<String> cmbReportPeriod;
    
    // ===== AI COACH PANEL =====
    private JTextArea txtAICoach;
    private JButton btnGetAIAdvice, btnRefreshAI;
    
    // ===== STATUS =====
    private JLabel lblStatusMessage;
    private JTabbedPane tabbedPane;
    
    // ===== TEMPLATE FIELDS =====
    private JSlider progressSlider;
    private JLabel lblProgressValue;
    private JTextField txtAmountSpent, txtRevenue, txtActualBeneficiaries;
    private JTextArea txtAchievements, txtChallenges, txtFuturePlans;
    private JButton btnSubmitTemplate;

    public BeneficiaryDashboardPanel(Beneficiary beneficiary) {
        this.beneficiary = beneficiary;
        setLayout(new BorderLayout(15, 15));
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        setBackground(new Color(245, 248, 250));

        add(createHeaderPanel(), BorderLayout.NORTH);

        tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Arial", Font.BOLD, 14));
        tabbedPane.setTabPlacement(JTabbedPane.TOP);

        tabbedPane.addTab("📋 My Applications", createApplicationPanel());
        tabbedPane.addTab("💰 My Loans", createLoanPanel());
        tabbedPane.addTab("📊 Reports", createReportPanel());
        tabbedPane.addTab("📋 Template Report", createTemplatePanel());
        tabbedPane.addTab("🤖 AI Coach", createAICoachPanel());

        add(tabbedPane, BorderLayout.CENTER);
        add(createStatusBar(), BorderLayout.SOUTH);

        System.out.println("=== BENEFICIARY DASHBOARD INIT ===");
        System.out.println("Beneficiary ID: " + beneficiary.getBeneficiaryID());
        System.out.println("Total loans in system: " + LoanService.getLoans().size());
        for (QardHasan loan : LoanService.getLoans()) {
            System.out.println("  Loan: " + loan.getLoanID() + 
                               " | App: " + loan.getApplication().getApplicationID() +
                               " | Ben: " + loan.getApplication().getBeneficiary().getBeneficiaryID());
        }

        refreshDashboard();
    }

    // ============================================================
    // HEADER PANEL
    // ============================================================
    private JPanel createHeaderPanel() {
        JPanel panel = new JPanel(new BorderLayout(15, 5));
        panel.setBackground(new Color(245, 248, 250));
        panel.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));

        JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        leftPanel.setBackground(new Color(245, 248, 250));
        
        lblBeneficiaryName = new JLabel("👤 Welcome, " + beneficiary.getFullName());
        lblBeneficiaryName.setFont(new Font("Arial", Font.BOLD, 22));
        lblBeneficiaryName.setForeground(new Color(0, 102, 204));
        leftPanel.add(lblBeneficiaryName);

        JLabel lblSubtitle = new JLabel("Your Entrepreneur Workspace");
        lblSubtitle.setFont(new Font("Arial", Font.PLAIN, 14));
        lblSubtitle.setForeground(Color.GRAY);
        leftPanel.add(lblSubtitle);

        panel.add(leftPanel, BorderLayout.WEST);

        JPanel rightPanel = new JPanel(new GridLayout(2, 2, 10, 5));
        rightPanel.setBackground(new Color(245, 248, 250));
        rightPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 10));

        lblTotalLoans = new JLabel("0");
        lblActiveLoans = new JLabel("0");
        lblTotalRepaid = new JLabel("0 QR");
        lblRemainingBalance = new JLabel("0 QR");
        lblNextPayment = new JLabel("N/A");
        lblApplicationStatus = new JLabel("No Applications");

        rightPanel.add(createStatLabel("💰 Total Loans:", lblTotalLoans));
        rightPanel.add(createStatLabel("📊 Active Loans:", lblActiveLoans));
        rightPanel.add(createStatLabel("💳 Total Repaid:", lblTotalRepaid));
        rightPanel.add(createStatLabel("📉 Remaining:", lblRemainingBalance));

        panel.add(rightPanel, BorderLayout.EAST);

        return panel;
    }

    private JPanel createStatLabel(String label, JLabel value) {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 0));
        panel.setBackground(new Color(245, 248, 250));
        JLabel lbl = new JLabel(label);
        lbl.setFont(new Font("Arial", Font.PLAIN, 11));
        lbl.setForeground(Color.GRAY);
        value.setFont(new Font("Arial", Font.BOLD, 12));
        value.setForeground(new Color(0, 102, 204));
        panel.add(lbl);
        panel.add(value);
        return panel;
    }

    // ============================================================
    // APPLICATION PANEL
    // ============================================================
    private JPanel createApplicationPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Left: Application Form
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createEtchedBorder(),
            "📝 Submit New Application",
            TitledBorder.LEFT,
            TitledBorder.TOP,
            new Font("Arial", Font.BOLD, 14)
        ));
        formPanel.setBackground(Color.WHITE);
        formPanel.setPreferredSize(new Dimension(420, 520));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(3, 5, 3, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Project Name
        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0.3;
        JLabel lblProjectName = new JLabel("Project Name:*");
        lblProjectName.setFont(new Font("Arial", Font.BOLD, 12));
        formPanel.add(lblProjectName, gbc);
        gbc.gridx = 1; gbc.weightx = 0.7;
        txtProjectName = new JTextField(20);
        txtProjectName.setPreferredSize(new Dimension(200, 28));
        formPanel.add(txtProjectName, gbc);

        // Sector
        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0.3;
        JLabel lblSector = new JLabel("Sector:*");
        lblSector.setFont(new Font("Arial", Font.BOLD, 12));
        formPanel.add(lblSector, gbc);
        gbc.gridx = 1; gbc.weightx = 0.7;
        cmbSector = new JComboBox<>(new String[]{
            "Agriculture", "Education", "Healthcare", 
            "Technology", "Small Business", "Infrastructure"
        });
        cmbSector.setPreferredSize(new Dimension(200, 28));
        formPanel.add(cmbSector, gbc);

        // Amount
        gbc.gridx = 0; gbc.gridy = 2; gbc.weightx = 0.3;
        JLabel lblAmount = new JLabel("Amount (QR):*");
        lblAmount.setFont(new Font("Arial", Font.BOLD, 12));
        formPanel.add(lblAmount, gbc);
        gbc.gridx = 1; gbc.weightx = 0.7;
        txtAmount = new JTextField(20);
        txtAmount.setPreferredSize(new Dimension(200, 28));
        formPanel.add(txtAmount, gbc);

        // Duration
        gbc.gridx = 0; gbc.gridy = 3; gbc.weightx = 0.3;
        JLabel lblDuration = new JLabel("Duration (months):");
        lblDuration.setFont(new Font("Arial", Font.BOLD, 12));
        formPanel.add(lblDuration, gbc);
        gbc.gridx = 1; gbc.weightx = 0.7;
        txtDuration = new JTextField(20);
        txtDuration.setText("12");
        txtDuration.setPreferredSize(new Dimension(200, 28));
        formPanel.add(txtDuration, gbc);

        // Expected Beneficiaries
        gbc.gridx = 0; gbc.gridy = 4; gbc.weightx = 0.3;
        JLabel lblBeneficiaries = new JLabel("Expected Beneficiaries:*");
        lblBeneficiaries.setFont(new Font("Arial", Font.BOLD, 12));
        formPanel.add(lblBeneficiaries, gbc);
        gbc.gridx = 1; gbc.weightx = 0.7;
        txtExpectedBeneficiaries = new JTextField(20);
        txtExpectedBeneficiaries.setText("50");
        txtExpectedBeneficiaries.setPreferredSize(new Dimension(200, 28));
        formPanel.add(txtExpectedBeneficiaries, gbc);

        // Location
        gbc.gridx = 0; gbc.gridy = 5; gbc.weightx = 0.3;
        JLabel lblLocation = new JLabel("Location:*");
        lblLocation.setFont(new Font("Arial", Font.BOLD, 12));
        formPanel.add(lblLocation, gbc);
        gbc.gridx = 1; gbc.weightx = 0.7;
        cmbLocation = new JComboBox<>(new String[]{
            "Al Rayyan", "Umm Salal", "Al Khor", "Al Wakrah", "Al Shamal", "Al-Shahaniya", "Duhail", "Al Hilal"
        });
        cmbLocation.setPreferredSize(new Dimension(200, 28));
        formPanel.add(cmbLocation, gbc);

        // Description
        gbc.gridx = 0; gbc.gridy = 6; gbc.weightx = 0.3;
        JLabel lblDesc = new JLabel("Description:*");
        lblDesc.setFont(new Font("Arial", Font.BOLD, 12));
        formPanel.add(lblDesc, gbc);
        gbc.gridx = 1; gbc.weightx = 0.7;
        txtDescription = new JTextArea(4, 20);
        txtDescription.setLineWrap(true);
        txtDescription.setWrapStyleWord(true);
        txtDescription.setFont(new Font("Arial", Font.PLAIN, 12));
        JScrollPane scrollDesc = new JScrollPane(txtDescription);
        scrollDesc.setPreferredSize(new Dimension(200, 70));
        formPanel.add(scrollDesc, gbc);

        // Document Uploads
        gbc.gridx = 0; gbc.gridy = 7; gbc.weightx = 0.3;
        gbc.gridheight = 3;
        JLabel lblDocs = new JLabel("Documents:");
        lblDocs.setFont(new Font("Arial", Font.BOLD, 12));
        formPanel.add(lblDocs, gbc);
        gbc.gridx = 1; gbc.weightx = 0.7;
        gbc.gridheight = 1;
        
        JPanel docPanel = new JPanel(new GridLayout(3, 2, 5, 3));
        docPanel.setBackground(Color.WHITE);
        docPanel.setPreferredSize(new Dimension(200, 90));
        
        btnUploadBusinessPlan = new JButton("📄 Upload Business Plan");
        btnUploadBusinessPlan.setFont(new Font("Arial", Font.PLAIN, 10));
        btnUploadBusinessPlan.setPreferredSize(new Dimension(120, 25));
        btnUploadBusinessPlan.addActionListener(this);
        btnUploadBusinessPlan.setActionCommand("uploadBusinessPlan");
        docPanel.add(btnUploadBusinessPlan);
        
        lblBusinessPlan = new JLabel("Not uploaded");
        lblBusinessPlan.setFont(new Font("Arial", Font.PLAIN, 9));
        lblBusinessPlan.setForeground(Color.GRAY);
        docPanel.add(lblBusinessPlan);
        
        btnUploadFinancials = new JButton("📊 Upload Financial Statements");
        btnUploadFinancials.setFont(new Font("Arial", Font.PLAIN, 10));
        btnUploadFinancials.setPreferredSize(new Dimension(120, 25));
        btnUploadFinancials.addActionListener(this);
        btnUploadFinancials.setActionCommand("uploadFinancials");
        docPanel.add(btnUploadFinancials);
        
        lblFinancials = new JLabel("Not uploaded");
        lblFinancials.setFont(new Font("Arial", Font.PLAIN, 9));
        lblFinancials.setForeground(Color.GRAY);
        docPanel.add(lblFinancials);
        
        btnUploadDocuments = new JButton("📎 Upload Supporting Docs");
        btnUploadDocuments.setFont(new Font("Arial", Font.PLAIN, 10));
        btnUploadDocuments.setPreferredSize(new Dimension(120, 25));
        btnUploadDocuments.addActionListener(this);
        btnUploadDocuments.setActionCommand("uploadDocuments");
        docPanel.add(btnUploadDocuments);
        
        lblDocuments = new JLabel("Not uploaded");
        lblDocuments.setFont(new Font("Arial", Font.PLAIN, 9));
        lblDocuments.setForeground(Color.GRAY);
        docPanel.add(lblDocuments);
        
        formPanel.add(docPanel, gbc);
        gbc.gridheight = 1;

        // Submit Button
        gbc.gridx = 0; gbc.gridy = 10; gbc.gridwidth = 2;
        gbc.insets = new Insets(8, 5, 5, 5);
        btnSubmitApplication = new JButton("🚀 Submit Application");
        btnSubmitApplication.setFont(new Font("Arial", Font.BOLD, 14));
        btnSubmitApplication.setBackground(new Color(0, 153, 76));
        btnSubmitApplication.setForeground(Color.WHITE);
        btnSubmitApplication.setFocusPainted(false);
        btnSubmitApplication.setPreferredSize(new Dimension(200, 40));
        btnSubmitApplication.addActionListener(this);
        btnSubmitApplication.setActionCommand("submitApplication");
        formPanel.add(btnSubmitApplication, gbc);

        panel.add(formPanel, BorderLayout.WEST);

        // Right: Application Status
        JPanel statusPanel = new JPanel(new BorderLayout(5, 5));
        statusPanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createEtchedBorder(),
            "📋 Application Status",
            TitledBorder.LEFT,
            TitledBorder.TOP,
            new Font("Arial", Font.BOLD, 14)
        ));
        statusPanel.setBackground(Color.WHITE);
        statusPanel.setPreferredSize(new Dimension(400, 520));

        JPanel tableControlPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        tableControlPanel.setBackground(Color.WHITE);
        tableControlPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 5, 0));

        JButton btnRefreshApps = new JButton("🔄 Refresh Status");
        btnRefreshApps.setFont(new Font("Arial", Font.BOLD, 11));
        btnRefreshApps.setBackground(new Color(0, 102, 204));
        btnRefreshApps.setForeground(Color.WHITE);
        btnRefreshApps.setFocusPainted(false);
        btnRefreshApps.setPreferredSize(new Dimension(130, 28));
        btnRefreshApps.addActionListener(e -> {
            loadApplications(appModel);
            updateStatusCounts(summaryPanel);
            setStatus("🔄 Applications refreshed");
            JOptionPane.showMessageDialog(this, "✅ Application status refreshed!");
        });
        tableControlPanel.add(btnRefreshApps);
        statusPanel.add(tableControlPanel, BorderLayout.NORTH);

        // Application Table
        String[] appColumns = {"ID", "Project", "Amount (QR)", "Status", "Date"};
        appModel = new DefaultTableModel(appColumns, 0) {
            @Override
            public boolean isCellEditable(int row, int col) { return false; }
        };
        
        appTable = new JTable(appModel);
        appTable.setFont(new Font("Arial", Font.PLAIN, 11));
        appTable.setRowHeight(25);
        appTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 11));
        
        appTable.setDefaultRenderer(Object.class, new javax.swing.table.DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                if (column == 3) {
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

        loadApplications(appModel);

        JScrollPane scrollApps = new JScrollPane(appTable);
        scrollApps.setPreferredSize(new Dimension(380, 250));
        statusPanel.add(scrollApps, BorderLayout.CENTER);

        // Status Summary Cards
        summaryPanel = new JPanel(new GridLayout(1, 4, 8, 5));
        summaryPanel.setBackground(Color.WHITE);
        summaryPanel.setBorder(BorderFactory.createEmptyBorder(10, 5, 5, 5));
        summaryPanel.setPreferredSize(new Dimension(380, 70));
        
        updateStatusCounts(summaryPanel);
        statusPanel.add(summaryPanel, BorderLayout.SOUTH);

        panel.add(statusPanel, BorderLayout.CENTER);

        return panel;
    }

    // ============================================================
    // LOAD APPLICATIONS
    // ============================================================
    private void loadApplications(DefaultTableModel model) {
        model.setRowCount(0);
        
        System.out.println("=== LOADING APPLICATIONS FOR BENEFICIARY ===");
        System.out.println("Beneficiary ID: " + beneficiary.getBeneficiaryID());
        System.out.println("Total applications in system: " + FundingService.getApplications().size());
        
        int count = 0;
        for (FundingApplication app : FundingService.getApplications()) {
            System.out.println("  App: " + app.getApplicationID() + 
                               " | Beneficiary: " + app.getBeneficiary().getBeneficiaryID() +
                               " | Status: " + app.getStatus());
            
            if (app.getBeneficiary().getBeneficiaryID() == beneficiary.getBeneficiaryID()) {
                String statusEmoji = getStatusEmoji(app.getStatus());
                model.addRow(new Object[]{
                    app.getApplicationID(),
                    app.getProject().getProjectName(),
                    String.format("%.2f", app.getRequestedAmount()),
                    statusEmoji + " " + app.getStatus().name(),
                    app.getApplicationDate().toLocalDate()
                });
                count++;
            }
        }
        
        System.out.println("Loaded " + count + " applications for this beneficiary");
        
        if (count == 0) {
            model.addRow(new Object[]{"-", "No applications found", "-", "-", "-"});
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

    // ============================================================
    // UPDATE STATUS COUNTS
    // ============================================================
    private void updateStatusCounts(JPanel summaryPanel) {
        summaryPanel.removeAll();
        
        int pending = getPendingCount();
        int approved = getApprovedCount();
        int funded = getFundedCount();
        int rejected = getRejectedCount();
        
        summaryPanel.add(createStatusCard("⏳ Pending", pending, new Color(255, 200, 0)));
        summaryPanel.add(createStatusCard("✅ Approved", approved, new Color(0, 200, 0)));
        summaryPanel.add(createStatusCard("💰 Funded", funded, new Color(0, 100, 200)));
        summaryPanel.add(createStatusCard("❌ Rejected", rejected, new Color(200, 0, 0)));
        
        summaryPanel.revalidate();
        summaryPanel.repaint();
    }

    private int getPendingCount() {
        int count = 0;
        for (FundingApplication app : FundingService.getApplications()) {
            if (app.getBeneficiary().getBeneficiaryID() == beneficiary.getBeneficiaryID()) {
                if (app.getStatus() == ApplicationStatus.PENDING || 
                    app.getStatus() == ApplicationStatus.UNDER_REVIEW) {
                    count++;
                }
            }
        }
        return count;
    }

    private int getApprovedCount() {
        int count = 0;
        for (FundingApplication app : FundingService.getApplications()) {
            if (app.getBeneficiary().getBeneficiaryID() == beneficiary.getBeneficiaryID()) {
                if (app.getStatus() == ApplicationStatus.APPROVED) {
                    count++;
                }
            }
        }
        return count;
    }

    private int getFundedCount() {
        int count = 0;
        for (FundingApplication app : FundingService.getApplications()) {
            if (app.getBeneficiary().getBeneficiaryID() == beneficiary.getBeneficiaryID()) {
                if (app.getStatus() == ApplicationStatus.FUNDED || 
                    app.getStatus() == ApplicationStatus.COMPLETED) {
                    count++;
                }
            }
        }
        return count;
    }

    private int getRejectedCount() {
        int count = 0;
        for (FundingApplication app : FundingService.getApplications()) {
            if (app.getBeneficiary().getBeneficiaryID() == beneficiary.getBeneficiaryID()) {
                if (app.getStatus() == ApplicationStatus.REJECTED) {
                    count++;
                }
            }
        }
        return count;
    }

    private JPanel createStatusCard(String label, int count, Color color) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createLineBorder(color, 2));
        
        JLabel countLabel = new JLabel(String.valueOf(count));
        countLabel.setFont(new Font("Arial", Font.BOLD, 18));
        countLabel.setForeground(color);
        countLabel.setHorizontalAlignment(SwingConstants.CENTER);
        panel.add(countLabel, BorderLayout.CENTER);
        
        JLabel descLabel = new JLabel(label);
        descLabel.setFont(new Font("Arial", Font.PLAIN, 10));
        descLabel.setForeground(Color.GRAY);
        descLabel.setHorizontalAlignment(SwingConstants.CENTER);
        panel.add(descLabel, BorderLayout.SOUTH);
        
        return panel;
    }

    // ============================================================
    // SUBMIT APPLICATION
    // ============================================================
    private void submitApplication() {
        String name = txtProjectName.getText().trim();
        String amountText = txtAmount.getText().trim();
        String desc = txtDescription.getText().trim();

        if (name.isEmpty() || amountText.isEmpty() || desc.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "Please fill all required fields (*).",
                "Missing Information",
                JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            double amount = Double.parseDouble(amountText);
            String sector = (String) cmbSector.getSelectedItem();
            int duration = Integer.parseInt(txtDuration.getText().trim());
            int expectedBeneficiaries = Integer.parseInt(txtExpectedBeneficiaries.getText().trim());
            String location = (String) cmbLocation.getSelectedItem();

            int id = getNextApplicationId();

            Project project = new Project(
                id,
                name,
                sector,
                desc,
                location,
                amount,
                expectedBeneficiaries,
                duration
            );

            currentApp = new FundingApplication(
                id,
                beneficiary,
                project,
                LocalDateTime.now(),
                amount,
                ApplicationStatus.PENDING
            );

            FundingService.submitApplication(currentApp);

            loadApplications(appModel);
            updateStatusCounts(summaryPanel);
            updateLoanStats();

            JOptionPane.showMessageDialog(this,
                "✅ Application submitted successfully!\n\n" +
                "ID: " + id + "\n" +
                "Project: " + name + "\n" +
                "Amount: " + String.format("%.2f", amount) + " QR",
                "Application Submitted",
                JOptionPane.INFORMATION_MESSAGE);

            txtProjectName.setText("");
            txtAmount.setText("");
            txtDescription.setText("");
            txtDuration.setText("12");
            txtExpectedBeneficiaries.setText("50");
            lblBusinessPlan.setText("Not uploaded");
            lblFinancials.setText("Not uploaded");
            lblDocuments.setText("Not uploaded");

            setStatus("✅ Application #" + id + " submitted successfully");

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this,
                "Please enter valid numbers for Amount, Duration, and Beneficiaries.",
                "Invalid Input",
                JOptionPane.ERROR_MESSAGE);
        }
    }

    private int getNextApplicationId() {
        int maxId = 0;
        for (FundingApplication app : FundingService.getApplications()) {
            if (app.getApplicationID() > maxId) {
                maxId = app.getApplicationID();
            }
        }
        return maxId + 1;
    }

    // ============================================================
    // UPLOAD DOCUMENT
    // ============================================================
    private void uploadDocument(String docType, JLabel statusLabel) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Upload " + docType);
        
        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            java.io.File file = fileChooser.getSelectedFile();
            String fileName = file.getName();
            
            if (currentApp != null) {
                System.out.println("📎 Uploading file to application #" + currentApp.getApplicationID());
                System.out.println("   File: " + fileName);
                
                if (statusLabel != null) {
                    statusLabel.setText("✅ " + fileName);
                    statusLabel.setForeground(new Color(0, 153, 76));
                }
                setStatus("📎 Uploaded: " + fileName);
                
                JOptionPane.showMessageDialog(this,
                    "✅ " + docType + " uploaded successfully!\n" +
                    "File: " + fileName + "\n" +
                    "Size: " + file.length() / 1024 + " KB",
                    "Upload Complete",
                    JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this,
                    "⚠️ Please submit an application first before uploading files.",
                    "No Application",
                    JOptionPane.WARNING_MESSAGE);
            }
        }
    }

    // ============================================================
    // LOAN PANEL
    // ============================================================
    private JPanel createLoanPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setDividerLocation(400);
        splitPane.setResizeWeight(0.4);

        JPanel loanDetailsPanel = new JPanel(new BorderLayout(10, 10));
        loanDetailsPanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createEtchedBorder(),
            "💰 My Loans",
            TitledBorder.LEFT,
            TitledBorder.TOP,
            new Font("Arial", Font.BOLD, 14)
        ));
        loanDetailsPanel.setBackground(Color.WHITE);
        loanDetailsPanel.setPreferredSize(new Dimension(380, 300));

        String[] loanColumns = {"Loan ID", "Amount (QR)", "Status", "Due Date", "Remaining"};
        loanModel = new DefaultTableModel(loanColumns, 0) {
            @Override
            public boolean isCellEditable(int row, int col) { return false; }
        };
        
        loanTable = new JTable(loanModel);
        loanTable.setFont(new Font("Arial", Font.PLAIN, 11));
        loanTable.setRowHeight(25);
        loanTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 11));
        loanTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        loanTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int row = loanTable.getSelectedRow();
                if (row >= 0) {
                    loadRepaymentSchedule((int) loanModel.getValueAt(row, 0));
                }
            }
        });

        loadLoans();

        JScrollPane scrollLoans = new JScrollPane(loanTable);
        scrollLoans.setPreferredSize(new Dimension(350, 180));
        loanDetailsPanel.add(scrollLoans, BorderLayout.CENTER);

        JPanel paymentPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        paymentPanel.setBackground(Color.WHITE);
        btnAddRepayment = new JButton("💳 Make Repayment");
        btnAddRepayment.setFont(new Font("Arial", Font.BOLD, 12));
        btnAddRepayment.setBackground(new Color(0, 102, 204));
        btnAddRepayment.setForeground(Color.WHITE);
        btnAddRepayment.setFocusPainted(false);
        btnAddRepayment.setPreferredSize(new Dimension(180, 35));
        btnAddRepayment.addActionListener(this);
        btnAddRepayment.setActionCommand("makeRepayment");
        paymentPanel.add(btnAddRepayment);
        
        loanDetailsPanel.add(paymentPanel, BorderLayout.SOUTH);

        // Right: Repayment Schedule
        JPanel schedulePanel = new JPanel(new BorderLayout(10, 10));
        schedulePanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createEtchedBorder(),
            "📅 Repayment Schedule",
            TitledBorder.LEFT,
            TitledBorder.TOP,
            new Font("Arial", Font.BOLD, 14)
        ));
        schedulePanel.setBackground(Color.WHITE);
        schedulePanel.setPreferredSize(new Dimension(380, 300));

        String[] repaymentColumns = {"#", "Amount (QR)", "Date", "Status"};
        repaymentModel = new DefaultTableModel(repaymentColumns, 0) {
            @Override
            public boolean isCellEditable(int row, int col) { return false; }
        };
        
        repaymentTable = new JTable(repaymentModel);
        repaymentTable.setFont(new Font("Arial", Font.PLAIN, 11));
        repaymentTable.setRowHeight(25);
        repaymentTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 11));

        repaymentTable.setDefaultRenderer(Object.class, new javax.swing.table.DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                if (column == 3) {
                    String status = (String) value;
                    if (status.contains("Paid")) {
                        c.setBackground(new Color(0, 200, 0));
                        c.setForeground(Color.WHITE);
                    } else if (status.contains("Overdue")) {
                        c.setBackground(new Color(200, 0, 0));
                        c.setForeground(Color.WHITE);
                    } else if (status.contains("Pending")) {
                        c.setBackground(new Color(255, 200, 0));
                        c.setForeground(Color.BLACK);
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

        JScrollPane scrollRepayments = new JScrollPane(repaymentTable);
        scrollRepayments.setPreferredSize(new Dimension(350, 180));
        schedulePanel.add(scrollRepayments, BorderLayout.CENTER);

        splitPane.setLeftComponent(loanDetailsPanel);
        splitPane.setRightComponent(schedulePanel);
        panel.add(splitPane, BorderLayout.CENTER);

        return panel;
    }

    // ============================================================
    // LOAD LOANS
    // ============================================================
    private void loadLoans() {
        loanModel.setRowCount(0);
        
        System.out.println("=== LOADING LOANS FOR BENEFICIARY ===");
        System.out.println("Beneficiary ID: " + beneficiary.getBeneficiaryID());
        System.out.println("Total loans in system: " + LoanService.getLoans().size());
        
        java.util.ArrayList<QardHasan> allLoans = LoanService.getLoans();
        for (QardHasan loan : allLoans) {
            System.out.println("  Loan: " + loan.getLoanID() + 
                               " | App: " + loan.getApplication().getApplicationID() +
                               " | Ben: " + loan.getApplication().getBeneficiary().getBeneficiaryID());
        }
        
        java.util.ArrayList<QardHasan> loans = LoanService.getLoansByBeneficiary(beneficiary.getBeneficiaryID());
        
        System.out.println("Loans found for this beneficiary: " + loans.size());
        
        for (QardHasan loan : loans) {
            double remaining = LoanService.getRemainingBalance(loan.getLoanID());
            String statusEmoji = loan.getStatus().equals("Active") ? "✅ " :
                                loan.getStatus().equals("Completed") ? "📌 " : "❌ ";
            loanModel.addRow(new Object[]{
                loan.getLoanID(),
                String.format("%.2f", loan.getLoanAmount()),
                statusEmoji + loan.getStatus(),
                loan.getDueDate(),
                String.format("%.2f", remaining)
            });
            System.out.println("  ✅ Added loan: " + loan.getLoanID());
        }
        
        if (loans.isEmpty()) {
            loanModel.addRow(new Object[]{"-", "-", "No loans found", "-", "-"});
        }
    }

    private void loadRepaymentSchedule(int loanId) {
        repaymentModel.setRowCount(0);
        java.util.ArrayList<Repayment> repayments = LoanService.getRepaymentsByLoan(loanId);
        
        if (repayments.isEmpty()) {
            repaymentModel.addRow(new Object[]{"-", "-", "-", "No repayments recorded"});
            return;
        }

        int count = 1;
        for (Repayment r : repayments) {
            repaymentModel.addRow(new Object[]{
                count++,
                String.format("%.2f", r.getAmount()),
                r.getPaymentDate(),
                "✅ Paid"
            });
        }
    }

  

    // ============================================================
    // SUBMIT TEMPLATE REPORT (Action Handler)
    // ============================================================
    private void submitTemplateReport() {
        // Find a funded or approved application
        FundingApplication selectedApp = null;
        for (FundingApplication app : FundingService.getApplications()) {
            if (app.getBeneficiary().getBeneficiaryID() == beneficiary.getBeneficiaryID()) {
                if (app.getStatus() == ApplicationStatus.FUNDED || 
                    app.getStatus() == ApplicationStatus.APPROVED) {
                    selectedApp = app;
                    break;
                }
            }
        }

        if (selectedApp == null) {
            JOptionPane.showMessageDialog(this,
                "Please select a project first.\n\n" +
                "You need at least one FUNDED or APPROVED application.",
                "No Project Selected",
                JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            // ===== READ VALUES FROM FIELDS =====
            double amountSpent = Double.parseDouble(txtAmountSpent.getText().trim());
            double revenue = Double.parseDouble(txtRevenue.getText().trim());
            int beneficiaries = Integer.parseInt(txtActualBeneficiaries.getText().trim());

            String achievements = txtAchievements.getText().trim();
            String challenges = txtChallenges.getText().trim();
            String futurePlans = txtFuturePlans.getText().trim();

            if (achievements.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                    "Please enter at least one achievement.",
                    "Missing Information",
                    JOptionPane.WARNING_MESSAGE);
                return;
            }

            // ===== CREATE REPORT =====
            PeriodicReport report = new PeriodicReport();
            report.setApplication(selectedApp);
            report.setProgressPercentage(progressSlider.getValue());
            report.setAmountSpent(amountSpent);
            report.setAmountRemaining(selectedApp.getRequestedAmount() - amountSpent);
            report.setRevenueGenerated(revenue);
            report.setActualBeneficiaries(beneficiaries);
            report.setAchievements(achievements);
            report.setChallenges(challenges);
            report.setFuturePlans(futurePlans);
            
            // ===== FIXED: Use setStatus(String) method =====
            report.setStatus("SUBMITTED");

            // ===== SAVE REPORT =====
            PeriodicReportService.submitReport(report);

            // ===== GENERATE AI ANALYSIS =====
            String aiAnalysis = report.generateAIAnalysis();

            // ===== SHOW SUCCESS =====
            JOptionPane.showMessageDialog(this,
                "✅ Template report submitted successfully!\n\n" +
                "📊 AI Analysis:\n" + aiAnalysis,
                "Report Submitted",
                JOptionPane.INFORMATION_MESSAGE);

            // ===== CLEAR FIELDS =====
            txtAmountSpent.setText("");
            txtRevenue.setText("");
            txtActualBeneficiaries.setText("");
            txtAchievements.setText("");
            txtChallenges.setText("");
            txtFuturePlans.setText("");
            progressSlider.setValue(50);
            lblProgressValue.setText("50%");

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this,
                "Please enter valid numbers for Amount, Revenue, and Beneficiaries.",
                "Input Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }

    // ============================================================
    // GENERATE AI ANALYSIS FOR TEMPLATE
    // ============================================================
    private String generateTemplateAIAnalysis(PeriodicReport report) {
        StringBuilder sb = new StringBuilder();
        sb.append("─────────────────────────────────────────────\n");
        sb.append("📊 AI ANALYSIS - TEMPLATE REPORT\n");
        sb.append("─────────────────────────────────────────────\n");
        sb.append("Project: ").append(report.getApplication().getProject().getProjectName()).append("\n");
        sb.append("Sector: ").append(report.getApplication().getProject().getSector()).append("\n");
        sb.append("Progress: ").append(String.format("%.1f%%", report.getProgressPercentage())).append("\n");
        sb.append("Spent: ").append(String.format("%.2f QR", report.getAmountSpent())).append("\n");
        sb.append("Remaining: ").append(String.format("%.2f QR", report.getAmountRemaining())).append("\n");
        sb.append("Revenue: ").append(String.format("%.2f QR", report.getRevenueGenerated())).append("\n");
        sb.append("Beneficiaries: ").append(report.getActualBeneficiaries()).append("\n\n");

        // Performance assessment
        sb.append("📈 PERFORMANCE ASSESSMENT:\n");
        double progress = report.getProgressPercentage();
        double totalBudget = report.getAmountSpent() + report.getAmountRemaining();
        double spentRatio = totalBudget > 0 ? (report.getAmountSpent() / totalBudget) * 100 : 0;

        if (progress >= 80 && spentRatio <= 80) {
            sb.append("✅ On track and within budget.\n");
        } else if (progress < 50 && spentRatio > 60) {
            sb.append("⚠️ Budget may be insufficient for remaining work.\n");
        } else if (progress >= 80 && spentRatio > 90) {
            sb.append("⚠️ Project nearly complete, budget almost fully utilized.\n");
        } else {
            sb.append("ℹ️ Progressing as expected.\n");
        }

        // Revenue analysis
        if (report.getRevenueGenerated() > 0) {
            double roi = report.getAmountSpent() > 0 ? 
                (report.getRevenueGenerated() / report.getAmountSpent()) * 100 : 0;
            sb.append("\n💰 REVENUE ANALYSIS:\n");
            sb.append("Revenue Generated: ").append(String.format("%.2f QR", report.getRevenueGenerated())).append("\n");
            sb.append("ROI: ").append(String.format("%.1f%%", roi)).append("\n");
            if (roi > 100) {
                sb.append("✅ Excellent return on investment.\n");
            } else if (roi > 50) {
                sb.append("✅ Good return on investment.\n");
            } else {
                sb.append("ℹ️ Revenue generation needs improvement.\n");
            }
        }

        // Achievements
        if (report.getAchievements() != null && !report.getAchievements().isEmpty()) {
            sb.append("\n✅ ACHIEVEMENTS:\n").append(report.getAchievements()).append("\n");
        }

        // Challenges
        if (report.getChallenges() != null && !report.getChallenges().isEmpty()) {
            sb.append("\n⚠️ CHALLENGES:\n").append(report.getChallenges()).append("\n");
        }

        // Future Plans
        if (report.getFuturePlans() != null && !report.getFuturePlans().isEmpty()) {
            sb.append("\n📋 FUTURE PLANS:\n").append(report.getFuturePlans()).append("\n");
        }

        // Recommendations
        sb.append("\n💡 RECOMMENDATIONS:\n");
        if (progress < 50 && report.getAmountSpent() > 0) {
            sb.append("• Review project timeline and resource allocation.\n");
        }
        if (report.getChallenges() != null && !report.getChallenges().isEmpty()) {
            sb.append("• Address challenges proactively with committee support.\n");
        }
        if (progress >= 80) {
            sb.append("• Plan for project completion and documentation.\n");
        }
        sb.append("• Continue regular reporting for accurate tracking.\n");

        sb.append("─────────────────────────────────────────────");
        return sb.toString();
    }

    // ============================================================
    // REPORT PANEL (Existing)
    // ============================================================
    private JPanel createReportPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel uploadPanel = new JPanel(new GridBagLayout());
        uploadPanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createEtchedBorder(),
            "📤 Upload Reports",
            TitledBorder.LEFT,
            TitledBorder.TOP,
            new Font("Arial", Font.BOLD, 14)
        ));
        uploadPanel.setBackground(Color.WHITE);
        uploadPanel.setPreferredSize(new Dimension(400, 250));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0.3;
        uploadPanel.add(new JLabel("Report Period:"), gbc);
        gbc.gridx = 1; gbc.weightx = 0.7;
        cmbReportPeriod = new JComboBox<>(new String[]{
            "January 2026", "February 2026", "March 2026", "April 2026",
            "May 2026", "June 2026", "July 2026", "August 2026",
            "September 2026", "October 2026", "November 2026", "December 2026"
        });
        cmbReportPeriod.setPreferredSize(new Dimension(180, 28));
        uploadPanel.add(cmbReportPeriod, gbc);

        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0.3;
        uploadPanel.add(new JLabel("Reports:"), gbc);
        gbc.gridx = 1; gbc.weightx = 0.7;
        JPanel uploadButtons = new JPanel(new GridLayout(3, 2, 5, 5));
        uploadButtons.setBackground(Color.WHITE);
        uploadButtons.setPreferredSize(new Dimension(200, 100));

        btnUploadReport = new JButton("📄 Upload Progress Report");
        btnUploadReport.setFont(new Font("Arial", Font.PLAIN, 10));
        btnUploadReport.addActionListener(this);
        btnUploadReport.setActionCommand("uploadReport");
        uploadButtons.add(btnUploadReport);

        JLabel lblReport = new JLabel("Not uploaded");
        lblReport.setFont(new Font("Arial", Font.PLAIN, 9));
        lblReport.setForeground(Color.GRAY);
        uploadButtons.add(lblReport);

        btnUploadPhotos = new JButton("📷 Upload Photos");
        btnUploadPhotos.setFont(new Font("Arial", Font.PLAIN, 10));
        btnUploadPhotos.addActionListener(this);
        btnUploadPhotos.setActionCommand("uploadPhotos");
        uploadButtons.add(btnUploadPhotos);

        JLabel lblPhotos = new JLabel("Not uploaded");
        lblPhotos.setFont(new Font("Arial", Font.PLAIN, 9));
        lblPhotos.setForeground(Color.GRAY);
        uploadButtons.add(lblPhotos);

        btnUploadInvoices = new JButton("🧾 Upload Invoices");
        btnUploadInvoices.setFont(new Font("Arial", Font.PLAIN, 10));
        btnUploadInvoices.addActionListener(this);
        btnUploadInvoices.setActionCommand("uploadInvoices");
        uploadButtons.add(btnUploadInvoices);

        JLabel lblInvoices = new JLabel("Not uploaded");
        lblInvoices.setFont(new Font("Arial", Font.PLAIN, 9));
        lblInvoices.setForeground(Color.GRAY);
        uploadButtons.add(lblInvoices);

        uploadPanel.add(uploadButtons, gbc);

        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 2;
        btnSubmitReport = new JButton("📤 Submit Report");
        btnSubmitReport.setFont(new Font("Arial", Font.BOLD, 12));
        btnSubmitReport.setBackground(new Color(0, 102, 204));
        btnSubmitReport.setForeground(Color.WHITE);
        btnSubmitReport.setFocusPainted(false);
        btnSubmitReport.setPreferredSize(new Dimension(200, 35));
        btnSubmitReport.addActionListener(this);
        btnSubmitReport.setActionCommand("submitReport");
        uploadPanel.add(btnSubmitReport, gbc);

        panel.add(uploadPanel, BorderLayout.WEST);

        JPanel historyPanel = new JPanel(new BorderLayout(5, 5));
        historyPanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createEtchedBorder(),
            "📋 Report History",
            TitledBorder.LEFT,
            TitledBorder.TOP,
            new Font("Arial", Font.BOLD, 14)
        ));
        historyPanel.setBackground(Color.WHITE);
        historyPanel.setPreferredSize(new Dimension(400, 250));

        txtReport = new JTextArea(8, 30);
        txtReport.setEditable(false);
        txtReport.setFont(new Font("Monospaced", Font.PLAIN, 12));
        txtReport.setBackground(new Color(255, 255, 240));
        txtReport.setText("Your submitted reports will appear here.");

        JScrollPane scrollReport = new JScrollPane(txtReport);
        historyPanel.add(scrollReport, BorderLayout.CENTER);

        JPanel statusPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        statusPanel.setBackground(Color.WHITE);
        lblReportStatus = new JLabel("📌 Status: No reports submitted");
        lblReportStatus.setFont(new Font("Arial", Font.PLAIN, 11));
        lblReportStatus.setForeground(Color.GRAY);
        statusPanel.add(lblReportStatus);
        historyPanel.add(statusPanel, BorderLayout.SOUTH);

        panel.add(historyPanel, BorderLayout.CENTER);

        return panel;
    }

    // ============================================================
    // AI COACH PANEL
    // ============================================================
    private JPanel createAICoachPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel coachPanel = new JPanel(new BorderLayout(10, 10));
        coachPanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(0, 102, 204), 2),
            "🤖 Your AI Business Coach",
            TitledBorder.LEFT,
            TitledBorder.TOP,
            new Font("Arial", Font.BOLD, 16),
            new Color(0, 102, 204)
        ));
        coachPanel.setBackground(Color.WHITE);

        txtAICoach = new JTextArea(8, 50);
        txtAICoach.setEditable(false);
        txtAICoach.setFont(new Font("Monospaced", Font.PLAIN, 13));
        txtAICoach.setBackground(new Color(255, 255, 240));
        txtAICoach.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        txtAICoach.setText("🤖 Welcome to your AI Coach!\n\nClick 'Get AI Advice' to receive insights.");

        JScrollPane scrollCoach = new JScrollPane(txtAICoach);
        scrollCoach.setPreferredSize(new Dimension(500, 250));
        coachPanel.add(scrollCoach, BorderLayout.CENTER);

        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        controlPanel.setBackground(Color.WHITE);

        btnGetAIAdvice = new JButton("💡 Get AI Advice");
        btnGetAIAdvice.setFont(new Font("Arial", Font.BOLD, 14));
        btnGetAIAdvice.setBackground(new Color(0, 102, 204));
        btnGetAIAdvice.setForeground(Color.WHITE);
        btnGetAIAdvice.setFocusPainted(false);
        btnGetAIAdvice.setPreferredSize(new Dimension(180, 40));
        btnGetAIAdvice.addActionListener(this);
        btnGetAIAdvice.setActionCommand("getAIAdvice");
        controlPanel.add(btnGetAIAdvice);

        btnRefreshAI = new JButton("🔄 Refresh Analysis");
        btnRefreshAI.setFont(new Font("Arial", Font.PLAIN, 12));
        btnRefreshAI.setBackground(new Color(200, 200, 200));
        btnRefreshAI.setFocusPainted(false);
        btnRefreshAI.setPreferredSize(new Dimension(150, 35));
        btnRefreshAI.addActionListener(this);
        btnRefreshAI.setActionCommand("refreshAI");
        controlPanel.add(btnRefreshAI);

        coachPanel.add(controlPanel, BorderLayout.SOUTH);

        panel.add(coachPanel, BorderLayout.CENTER);

        return panel;
    }

    // ============================================================
    // STATUS BAR
    // ============================================================
    private JPanel createStatusBar() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panel.setBorder(BorderFactory.createEtchedBorder());
        panel.setBackground(new Color(240, 240, 240));
        
        lblStatusMessage = new JLabel("✅ Ready - Welcome " + beneficiary.getFullName());
        lblStatusMessage.setFont(new Font("Arial", Font.PLAIN, 11));
        panel.add(lblStatusMessage);

        JLabel timeLabel = new JLabel(LocalDate.now().format(DateTimeFormatter.ofPattern("dd MMMM yyyy")));
        timeLabel.setFont(new Font("Arial", Font.PLAIN, 10));
        timeLabel.setForeground(Color.GRAY);
        panel.add(Box.createHorizontalGlue());
        panel.add(timeLabel);

        return panel;
    }

    // ============================================================
    // REFRESH DASHBOARD
    // ============================================================
    private void refreshDashboard() {
        System.out.println("🔄 Refreshing Beneficiary Dashboard...");
        loadLoans();
        updateLoanStats();
        if (appModel != null) {
            loadApplications(appModel);
        }
        if (summaryPanel != null) {
            updateStatusCounts(summaryPanel);
        }
        revalidate();
        repaint();
        setStatus("🔄 Dashboard refreshed at " + LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")));
    }

    // ============================================================
    // UPDATE LOAN STATS
    // ============================================================
    private void updateLoanStats() {
        java.util.ArrayList<QardHasan> loans = LoanService.getLoansByBeneficiary(beneficiary.getBeneficiaryID());
        int totalLoans = loans.size();
        int activeLoans = 0;
        double totalRepaid = 0;
        double totalRemaining = 0;
        LocalDate nextPayment = null;

        System.out.println("📊 Updating loan stats for beneficiary: " + beneficiary.getBeneficiaryID());
        System.out.println("   Loans found: " + totalLoans);

        for (QardHasan loan : loans) {
            if (loan.getStatus().equals("Active")) activeLoans++;
            double repaid = LoanService.getTotalRepaidByLoan(loan.getLoanID());
            totalRepaid += repaid;
            double remaining = loan.getLoanAmount() - repaid;
            totalRemaining += remaining;
            
            if (loan.getDueDate() != null && remaining > 0) {
                if (nextPayment == null || loan.getDueDate().isBefore(nextPayment)) {
                    nextPayment = loan.getDueDate();
                }
            }
        }

        lblTotalLoans.setText(String.valueOf(totalLoans));
        lblActiveLoans.setText(String.valueOf(activeLoans));
        lblTotalRepaid.setText(String.format("%.2f QR", totalRepaid));
        lblRemainingBalance.setText(String.format("%.2f QR", totalRemaining));
        lblNextPayment.setText(nextPayment != null ? nextPayment.toString() : "N/A");
        
        System.out.println("   Stats updated: Total=" + totalLoans + ", Active=" + activeLoans);
    }

    // ============================================================
    // SET STATUS
    // ============================================================
    private void setStatus(String message) {
        if (lblStatusMessage != null) {
            lblStatusMessage.setText("📌 " + message);
        }
    }

    // ============================================================
    // ACTION HANDLING
    // ============================================================
    @Override
    public void actionPerformed(ActionEvent e) {
        String cmd = e.getActionCommand();

        switch (cmd) {
            case "submitApplication":
                submitApplication();
                break;
            case "uploadBusinessPlan":
                uploadDocument("Business Plan", lblBusinessPlan);
                break;
            case "uploadFinancials":
                uploadDocument("Financial Statements", lblFinancials);
                break;
            case "uploadDocuments":
                uploadDocument("Supporting Documents", lblDocuments);
                break;
            case "uploadReport":
                uploadDocument("Progress Report", null);
                break;
            case "uploadPhotos":
                uploadDocument("Photos", null);
                break;
            case "uploadInvoices":
                uploadDocument("Invoices", null);
                break;
            case "submitReport":
                JOptionPane.showMessageDialog(this,
                    "📤 Report submitted successfully!\n" +
                    "Period: " + cmbReportPeriod.getSelectedItem(),
                    "Report Submitted",
                    JOptionPane.INFORMATION_MESSAGE);
                lblReportStatus.setText("✅ Report submitted for " + cmbReportPeriod.getSelectedItem());
                lblReportStatus.setForeground(new Color(0, 153, 76));
                break;
            case "makeRepayment":
                makeRepayment();
                break;
            case "getAIAdvice":
                getAIAdvice();
                break;
            case "refreshAI":
                getAIAdvice();
                break;
            case "submitTemplate":
                submitTemplateReport();
                break;
            case "uploadTemplatePhotos":
                uploadDocument("Template Photos", null);
                break;
            case "uploadTemplateInvoices":
                uploadDocument("Template Invoices", null);
                break;
            default:
                break;
        }
    }

    // ============================================================
    // MAKE REPAYMENT
    // ============================================================
    private void makeRepayment() {
        int selectedRow = loanTable.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this,
                "Please select a loan from the table.",
                "No Loan Selected",
                JOptionPane.WARNING_MESSAGE);
            return;
        }

        int loanId = (int) loanModel.getValueAt(selectedRow, 0);
        QardHasan loan = LoanService.searchLoan(loanId);
        
        if (loan == null) {
            JOptionPane.showMessageDialog(this, "Loan not found!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        double remaining = LoanService.getRemainingBalance(loanId);
        if (remaining <= 0) {
            JOptionPane.showMessageDialog(this,
                "This loan is already fully paid!",
                "Loan Complete",
                JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        String amountStr = JOptionPane.showInputDialog(this,
            "Enter repayment amount:\n" +
            "Loan ID: " + loanId + "\n" +
            "Remaining Balance: " + String.format("%.2f", remaining) + " QR",
            "Make Repayment",
            JOptionPane.QUESTION_MESSAGE);

        if (amountStr != null && !amountStr.trim().isEmpty()) {
            try {
                double amount = Double.parseDouble(amountStr.trim());
                if (amount <= 0 || amount > remaining) {
                    JOptionPane.showMessageDialog(this,
                        "Please enter a valid amount (1 - " + String.format("%.2f", remaining) + " QR)",
                        "Invalid Amount",
                        JOptionPane.ERROR_MESSAGE);
                    return;
                }

                Repayment repayment = new Repayment();
                repayment.setRepaymentID((int) (System.currentTimeMillis() % 10000));
                repayment.setLoan(loan);
                repayment.setAmount(amount);
                repayment.setPaymentDate(LocalDate.now());

                LoanService.addRepayment(repayment);
                refreshDashboard();
                setStatus("✅ Repayment of " + String.format("%.2f", amount) + " QR recorded");

                JOptionPane.showMessageDialog(this,
                    "✅ Repayment successful!\n" +
                    "Amount: " + String.format("%.2f", amount) + " QR\n" +
                    "Remaining: " + String.format("%.2f", LoanService.getRemainingBalance(loanId)) + " QR",
                    "Payment Complete",
                    JOptionPane.INFORMATION_MESSAGE);

            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this,
                    "Please enter a valid number.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    // ============================================================
    // GET AI ADVICE
    // ============================================================
    private void getAIAdvice() {
        txtAICoach.setText("🤖 AI Coach is analyzing your data...\n\nPlease wait...");

        SwingWorker<String, Void> worker = new SwingWorker<>() {
            @Override
            protected String doInBackground() {
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
                return generateAIAnalysisText();
            }

            @Override
            protected void done() {
                try {
                    txtAICoach.setText(get());
                } catch (Exception e) {
                    txtAICoach.setText("⚠️ Error: " + e.getMessage());
                }
            }
        };
        worker.execute();
    }

    private String generateAIAnalysisText() {
        StringBuilder sb = new StringBuilder();
        sb.append("🤖 AI BUSINESS COACH - ANALYSIS\n");
        sb.append("═══════════════════════════════════════════════════\n\n");
        
        java.util.ArrayList<QardHasan> loans = LoanService.getLoansByBeneficiary(beneficiary.getBeneficiaryID());
        
        if (loans.isEmpty()) {
            sb.append("📌 No active loans found.\n\n");
            sb.append("💡 GETTING STARTED:\n");
            sb.append("   • Submit a funding application\n");
            sb.append("   • Complete your project proposal\n");
            sb.append("   • Wait for committee approval\n");
        } else {
            sb.append("📊 PERFORMANCE ANALYSIS:\n");
            sb.append("─────────────────────────────────────────────\n");
            
            for (QardHasan loan : loans) {
                double repaid = LoanService.getTotalRepaidByLoan(loan.getLoanID());
                double remaining = loan.getLoanAmount() - repaid;
                double progress = loan.getLoanAmount() > 0 ? (repaid / loan.getLoanAmount()) * 100 : 0;
                
                sb.append("\nLoan #" + loan.getLoanID() + ":\n");
                sb.append("  Amount: " + String.format("%.2f QR", loan.getLoanAmount()) + "\n");
                sb.append("  Progress: " + String.format("%.1f%%", progress) + "\n");
                sb.append("  Status: " + loan.getStatus() + "\n");
                
                if (loan.getDueDate() != null) {
                    long daysToDue = java.time.temporal.ChronoUnit.DAYS.between(LocalDate.now(), loan.getDueDate());
                    if (daysToDue < 0) {
                        sb.append("  ⚠️ OVERDUE by " + Math.abs(daysToDue) + " days!\n");
                    } else if (daysToDue < 30) {
                        sb.append("  ⏳ Due in " + daysToDue + " days\n");
                    }
                }
            }
            
            sb.append("\n💡 RECOMMENDATIONS:\n");
            sb.append("─────────────────────────────────────────────\n");
            sb.append("1. Maintain regular progress reporting\n");
            sb.append("2. Upload photos and invoices monthly\n");
            sb.append("3. Stay on track with repayment schedule\n");
            sb.append("4. Reach out to committee if you need support\n");
            
            sb.append("\n📈 GROWTH OPPORTUNITIES:\n");
            sb.append("─────────────────────────────────────────────\n");
            sb.append("• Explore new markets for your products\n");
            sb.append("• Build partnerships with other businesses\n");
            sb.append("• Document your success story\n");
        }
        
        return sb.toString();
    }

    // ============================================================
    // GET BENEFICIARY
    // ============================================================
    public Beneficiary getBeneficiary() {
        return beneficiary;
    }
    
    private JPanel createTemplatePanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel templatePanel = new JPanel(new GridBagLayout());
        templatePanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createEtchedBorder(),
            "📋 Standardized Progress Report Template",
            TitledBorder.RIGHT,
            TitledBorder.TOP,
            new Font("Arial", Font.BOLD, 14)
        ));
        templatePanel.setBackground(Color.WHITE);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;

        int row = 0;

        // ===== SECTION: PROJECT SELECTION =====
        gbc.gridx = 0; gbc.gridy = row; gbc.gridwidth = 2;
        JLabel lblSection1 = new JLabel("📌 Project Selection");
        lblSection1.setFont(new Font("Arial", Font.BOLD, 14));
        lblSection1.setForeground(new Color(0, 102, 204));
        templatePanel.add(lblSection1, gbc);
        row++;

        // Project Selector
        gbc.gridx = 0; gbc.gridy = row; gbc.gridwidth = 1;
        gbc.weightx = 0.3;
        templatePanel.add(new JLabel("Project:"), gbc);
        gbc.gridx = 1; gbc.weightx = 0.7;
        JComboBox<FundingApplication> cmbTemplateProject = new JComboBox<>();
        for (FundingApplication app : FundingService.getApplications()) {
            if (app.getBeneficiary().getBeneficiaryID() == beneficiary.getBeneficiaryID()) {
                if (app.getStatus() == ApplicationStatus.FUNDED || 
                    app.getStatus() == ApplicationStatus.APPROVED) {
                    cmbTemplateProject.addItem(app);
                }
            }
        }
        if (cmbTemplateProject.getItemCount() == 0) {
            JLabel lblNoProject = new JLabel("No funded projects available");
            lblNoProject.setForeground(Color.RED);
            gbc.gridx = 1;
            templatePanel.add(lblNoProject, gbc);
        } else {
            cmbTemplateProject.setRenderer(new DefaultListCellRenderer() {
                @Override
                public Component getListCellRendererComponent(JList<?> list, Object value, 
                        int index, boolean isSelected, boolean cellHasFocus) {
                    if (value instanceof FundingApplication) {
                        FundingApplication app = (FundingApplication) value;
                        return super.getListCellRendererComponent(list, 
                            app.getProject().getProjectName() + " (ID: " + app.getApplicationID() + ")", 
                            index, isSelected, cellHasFocus);
                    }
                    return super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                }
            });
            templatePanel.add(cmbTemplateProject, gbc);
        }
        row++;

        // ===== SECTION: PROGRESS =====
        gbc.gridx = 0; gbc.gridy = row; gbc.gridwidth = 2;
        JLabel lblSection2 = new JLabel("📊 Progress & Achievement");
        lblSection2.setFont(new Font("Arial", Font.BOLD, 14));
        lblSection2.setForeground(new Color(0, 102, 204));
        templatePanel.add(lblSection2, gbc);
        row++;
        gbc.gridwidth = 1;

        // Progress Slider
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0.3;
        JLabel lblProgress = new JLabel("Progress (%):");
        lblProgress.setToolTipText("Percentage of project completion based on timeline and budget");
        templatePanel.add(lblProgress, gbc);
        gbc.gridx = 1; gbc.weightx = 0.7;
        JPanel sliderContainer = new JPanel(new BorderLayout(5, 0));
        progressSlider = new JSlider(0, 100, 50);
        progressSlider.setMajorTickSpacing(25);
        progressSlider.setPaintTicks(true);
        progressSlider.setPaintLabels(true);
        lblProgressValue = new JLabel("50%");
        progressSlider.addChangeListener(e -> {
            lblProgressValue.setText(progressSlider.getValue() + "%");
        });
        sliderContainer.add(progressSlider, BorderLayout.CENTER);
        sliderContainer.add(lblProgressValue, BorderLayout.EAST);
        templatePanel.add(sliderContainer, gbc);
        row++;

        // ===== SECTION: FINANCIAL METRICS =====
        gbc.gridx = 0; gbc.gridy = row; gbc.gridwidth = 2;
        JLabel lblSection3 = new JLabel("💰 Financial Metrics");
        lblSection3.setFont(new Font("Arial", Font.BOLD, 14));
        lblSection3.setForeground(new Color(0, 102, 204));
        templatePanel.add(lblSection3, gbc);
        row++;
        gbc.gridwidth = 1;

        // Amount Spent
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0.3;
        JLabel lblSpent = new JLabel("Amount Spent (QR):");
        lblSpent.setToolTipText("Total amount spent so far from the project budget");
        templatePanel.add(lblSpent, gbc);
        gbc.gridx = 1; gbc.weightx = 0.7;
        JPanel spentPanel = new JPanel(new BorderLayout(5, 0));
        txtAmountSpent = new JTextField(15);
        txtAmountSpent.setToolTipText(
            "<html>" +
            "<b>Includes:</b> All project-related expenses (materials, wages, services, etc.)<br>" +
            "<b>Example:</b> For a farm project: seeds, fertilizer, labor costs, etc." +
            "</html>"
        );
        spentPanel.add(txtAmountSpent, BorderLayout.CENTER);
        spentPanel.add(createHelpButton("Amount Spent", 
            "This field includes all project-related expenses:\n\n" +
            "• Materials and equipment\n" +
            "• Labor wages\n" +
            "• Services (electricity, water, etc.)\n" +
            "• Rent\n" +
            "• Marketing costs\n\n" +
            "⚠️ Do NOT include personal expenses or costs unrelated to the project."), 
            BorderLayout.EAST);
        templatePanel.add(spentPanel, gbc);
        row++;

        // Revenue Generated
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0.3;
        JLabel lblRevenue = new JLabel("Revenue Generated (QR):");
        lblRevenue.setToolTipText("Total revenue or sales generated by the project");
        templatePanel.add(lblRevenue, gbc);
        gbc.gridx = 1; gbc.weightx = 0.7;
        JPanel revenuePanel = new JPanel(new BorderLayout(5, 0));
        txtRevenue = new JTextField(15);
        txtRevenue.setToolTipText(
            "<html>" +
            "<b>Includes:</b> All revenue from the project<br>" +
            "<b>Example:</b> For a farm project: value of crops sold." +
            "</html>"
        );
        revenuePanel.add(txtRevenue, BorderLayout.CENTER);
        revenuePanel.add(createHelpButton("Revenue Generated",
            "This field includes all revenue from the project:\n\n" +
            "• Sales\n" +
            "• Service fees\n" +
            "• Any other income related to the project\n\n" +
            "📊 This metric helps measure Return on Investment (ROI)."), 
            BorderLayout.EAST);
        templatePanel.add(revenuePanel, gbc);
        row++;

        // ===== SECTION: SOCIAL IMPACT =====
        gbc.gridx = 0; gbc.gridy = row; gbc.gridwidth = 2;
        JLabel lblSection4 = new JLabel("👥 Social Impact Metrics");
        lblSection4.setFont(new Font("Arial", Font.BOLD, 14));
        lblSection4.setForeground(new Color(0, 102, 204));
        templatePanel.add(lblSection4, gbc);
        row++;
        gbc.gridwidth = 1;

        // Actual Beneficiaries
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0.3;
        JLabel lblBeneficiaries = new JLabel("Actual Beneficiaries:");
        lblBeneficiaries.setToolTipText("Number of people who directly benefited from the project");
        templatePanel.add(lblBeneficiaries, gbc);
        gbc.gridx = 1; gbc.weightx = 0.7;
        JPanel beneficiariesPanel = new JPanel(new BorderLayout(5, 0));
        txtActualBeneficiaries = new JTextField(15);
        txtActualBeneficiaries.setToolTipText(
            "<html>" +
            "<b>Definition:</b> People who received direct service or support from the project<br>" +
            "<b>Includes:</b> Trainees, service recipients, customers<br>" +
            "<b>Does NOT include:</b> Workers or employees<br>" +
            "<b>Example:</b> 50 farmers who received irrigation systems, or 100 students in a training program" +
            "</html>"
        );
        beneficiariesPanel.add(txtActualBeneficiaries, BorderLayout.CENTER);
        beneficiariesPanel.add(createHelpButton("Beneficiaries",
            "Definition of a beneficiary:\n\n" +
            "✅ COUNT as a beneficiary:\n" +
            "• Person who received direct service from the project\n" +
            "• Person who used the project's product or service\n" +
            "• Person who received training or education\n\n" +
            "❌ Do NOT count:\n" +
            "• Workers or employees (count these as 'Jobs')\n" +
            "• Passersby or visitors\n\n" +
            "📊 Example:\n" +
            "If project is vocational training: Beneficiaries = number of trainees\n" +
            "If project is a farm: Beneficiaries = number of farmers who received the service"),
            BorderLayout.EAST);
        templatePanel.add(beneficiariesPanel, gbc);
        row++;

        // Jobs Created
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0.3;
        JLabel lblJobs = new JLabel("Jobs Created:");
        lblJobs.setToolTipText("Number of direct jobs created by the project");
        templatePanel.add(lblJobs, gbc);
        gbc.gridx = 1; gbc.weightx = 0.7;
        JPanel jobsPanel = new JPanel(new BorderLayout(5, 0));
        JTextField txtJobsCreated = new JTextField(15);
        txtJobsCreated.setToolTipText(
            "<html>" +
            "<b>Definition:</b> Direct jobs created as a result of the project<br>" +
            "<b>Includes:</b> Workers, employees, full-time or part-time<br>" +
            "<b>Does NOT include:</b> Volunteers (count separately)<br>" +
            "<b>Example:</b> 5 farm workers, 2 administrative staff" +
            "</html>"
        );
        jobsPanel.add(txtJobsCreated, BorderLayout.CENTER);
        jobsPanel.add(createHelpButton("Jobs Created",
            "Definition of a job:\n\n" +
            "✅ COUNT as a job:\n" +
            "• Full-time or part-time workers\n" +
            "• Administrative staff\n" +
            "• Ongoing service contracts\n\n" +
            "❌ Do NOT count:\n" +
            "• Volunteers\n" +
            "• Short-term contracts (less than 1 month)\n" +
            "• Temporary workers only\n\n" +
            "📊 Example:\n" +
            "Farm project with 5 permanent workers and 2 seasonal staff = 7 jobs"),
            BorderLayout.EAST);
        jobsPanel.add(txtJobsCreated, BorderLayout.CENTER);
        templatePanel.add(jobsPanel, gbc);
        row++;

        // ===== SECTION: QUALITATIVE METRICS =====
        gbc.gridx = 0; gbc.gridy = row; gbc.gridwidth = 2;
        JLabel lblSection5 = new JLabel("📝 Achievements & Challenges");
        lblSection5.setFont(new Font("Arial", Font.BOLD, 14));
        lblSection5.setForeground(new Color(0, 102, 204));
        templatePanel.add(lblSection5, gbc);
        row++;
        gbc.gridwidth = 1;

        // Achievements
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0.3;
        JLabel lblAchievements = new JLabel("Achievements:");
        lblAchievements.setToolTipText("What has been achieved during this reporting period?");
        templatePanel.add(lblAchievements, gbc);
        gbc.gridx = 1; gbc.weightx = 0.7;
        JPanel achievementsPanel = new JPanel(new BorderLayout(5, 0));
        txtAchievements = new JTextArea(3, 20);
        txtAchievements.setLineWrap(true);
        txtAchievements.setWrapStyleWord(true);
        txtAchievements.setToolTipText(
            "Write the achievements during this period\n" +
            "Example: Trained 30 women, product launched, new branch opened"
        );
        JScrollPane scrollAchievements = new JScrollPane(txtAchievements);
        scrollAchievements.setPreferredSize(new Dimension(300, 60));
        achievementsPanel.add(scrollAchievements, BorderLayout.CENTER);
        achievementsPanel.add(createHelpButton("Achievements",
            "What achievements were made?\n\n" +
            "Write the key achievements during this period:\n" +
            "• Goals achieved\n" +
            "• Milestones reached\n" +
            "• Any tangible progress\n\n" +
            "📌 Example:\n" +
            "• Trained 30 women in sewing\n" +
            "• Marketed 500 kg of products\n" +
            "• Opened new branch in area X"),
            BorderLayout.EAST);
        templatePanel.add(achievementsPanel, gbc);
        row++;

        // Challenges
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0.3;
        JLabel lblChallenges = new JLabel("Challenges:");
        lblChallenges.setToolTipText("What difficulties did you face during this period?");
        templatePanel.add(lblChallenges, gbc);
        gbc.gridx = 1; gbc.weightx = 0.7;
        JPanel challengesPanel = new JPanel(new BorderLayout(5, 0));
        txtChallenges = new JTextArea(3, 20);
        txtChallenges.setLineWrap(true);
        txtChallenges.setWrapStyleWord(true);
        txtChallenges.setToolTipText(
            "Write the challenges and difficulties you faced\n" +
            "Example: Supply delays, low sales, technical issues"
        );
        JScrollPane scrollChallenges = new JScrollPane(txtChallenges);
        scrollChallenges.setPreferredSize(new Dimension(300, 60));
        challengesPanel.add(scrollChallenges, BorderLayout.CENTER);
        challengesPanel.add(createHelpButton("Challenges",
            "What challenges did you face?\n\n" +
            "Write the difficulties and problems that affected execution:\n" +
            "• Delays\n" +
            "• Financial issues\n" +
            "• Technical difficulties\n" +
            "• Market challenges\n\n" +
            "📌 Example:\n" +
            "• Delay in receiving raw materials\n" +
            "• Low sales due to season\n" +
            "• Shortage of skilled labor"),
            BorderLayout.EAST);
        templatePanel.add(challengesPanel, gbc);
        row++;

        // Future Plans
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0.3;
        JLabel lblFuture = new JLabel("Future Plans:");
        lblFuture.setToolTipText("What are your plans for the coming months?");
        templatePanel.add(lblFuture, gbc);
        gbc.gridx = 1; gbc.weightx = 0.7;
        JPanel futurePanel = new JPanel(new BorderLayout(5, 0));
        txtFuturePlans = new JTextArea(3, 20);
        txtFuturePlans.setLineWrap(true);
        txtFuturePlans.setWrapStyleWord(true);
        txtFuturePlans.setToolTipText(
            "Write your plans for the coming months\n" +
            "Example: Expand to new area, increase production, improve quality"
        );
        JScrollPane scrollFuture = new JScrollPane(txtFuturePlans);
        scrollFuture.setPreferredSize(new Dimension(300, 60));
        futurePanel.add(scrollFuture, BorderLayout.CENTER);
        futurePanel.add(createHelpButton("Future Plans",
            "What are your future plans?\n\n" +
            "Write the upcoming goals and plans:\n" +
            "• Geographic expansion\n" +
            "• Production increase\n" +
            "• Quality improvement\n" +
            "• Marketing\n\n" +
            "📌 Example:\n" +
            "• Expand to 2 new areas\n" +
            "• Increase production by 20%\n" +
            "• Improve product quality"),
            BorderLayout.EAST);
        templatePanel.add(futurePanel, gbc);
        row++;

        // ===== UPLOAD SECTION =====
        gbc.gridx = 0; gbc.gridy = row; gbc.gridwidth = 2;
        JLabel lblSection6 = new JLabel("📎 Attachments");
        lblSection6.setFont(new Font("Arial", Font.BOLD, 14));
        lblSection6.setForeground(new Color(0, 102, 204));
        templatePanel.add(lblSection6, gbc);
        row++;
        gbc.gridwidth = 1;

        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0.3;
        templatePanel.add(new JLabel("Files:"), gbc);
        gbc.gridx = 1; gbc.weightx = 0.7;
        JPanel uploadPanel = new JPanel(new GridLayout(1, 2, 10, 5));
        btnUploadPhotos = new JButton("📷 Upload Photos");
        btnUploadInvoices = new JButton("🧾 Upload Invoices");
        btnUploadPhotos.addActionListener(this);
        btnUploadPhotos.setActionCommand("uploadTemplatePhotos");
        btnUploadInvoices.addActionListener(this);
        btnUploadInvoices.setActionCommand("uploadTemplateInvoices");
        uploadPanel.add(btnUploadPhotos);
        uploadPanel.add(btnUploadInvoices);
        templatePanel.add(uploadPanel, gbc);
        row++;

        // ===== SUBMIT BUTTON =====
        gbc.gridx = 0; gbc.gridy = row; gbc.gridwidth = 2;
        gbc.insets = new Insets(10, 5, 5, 5);
        btnSubmitTemplate = new JButton("📤 Submit Template Report");
        btnSubmitTemplate.setFont(new Font("Arial", Font.BOLD, 14));
        btnSubmitTemplate.setBackground(new Color(0, 153, 76));
        btnSubmitTemplate.setForeground(Color.WHITE);
        btnSubmitTemplate.setFocusPainted(false);
        btnSubmitTemplate.setPreferredSize(new Dimension(250, 40));
        btnSubmitTemplate.addActionListener(this);
        btnSubmitTemplate.setActionCommand("submitTemplate");
        templatePanel.add(btnSubmitTemplate, gbc);

        panel.add(templatePanel, BorderLayout.CENTER);

        // ===== HISTORY PANEL =====
        JPanel historyPanel = new JPanel(new BorderLayout(5, 5));
        historyPanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createEtchedBorder(),
            "📋 Template Reports History",
            TitledBorder.LEFT,
            TitledBorder.TOP,
            new Font("Arial", Font.BOLD, 12)
        ));
        historyPanel.setBackground(Color.WHITE);
        historyPanel.setPreferredSize(new Dimension(400, 150));

        JTextArea txtTemplateHistory = new JTextArea(5, 30);
        txtTemplateHistory.setEditable(false);
        txtTemplateHistory.setFont(new Font("Monospaced", Font.PLAIN, 12));
        txtTemplateHistory.setBackground(new Color(255, 255, 240));
        txtTemplateHistory.setText("Your submitted template reports will appear here.");

        JScrollPane scrollHistory = new JScrollPane(txtTemplateHistory);
        historyPanel.add(scrollHistory, BorderLayout.CENTER);

        panel.add(historyPanel, BorderLayout.SOUTH);

        return panel;
    }
    
    private JButton createHelpButton(String title, String content) {
        JButton btnHelp = new JButton("❓");
        btnHelp.setFont(new Font("Arial", Font.BOLD, 10));
        btnHelp.setPreferredSize(new Dimension(25, 25));
        btnHelp.setFocusPainted(false);
        btnHelp.setToolTipText("Help");
        btnHelp.addActionListener(e -> {
            JOptionPane.showMessageDialog(this,
                content,
                "📖 " + title,
                JOptionPane.INFORMATION_MESSAGE);
        });
        return btnHelp;
    }
}