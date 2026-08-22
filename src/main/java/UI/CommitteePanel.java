package UI;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.time.LocalDate;
import java.time.LocalTime;
import Model.*;
import Service.*;

public class CommitteePanel extends JPanel implements ActionListener {
    private JTextField txtAppID;
    private JTextArea txtDecision;
    private JTable table;
    private DefaultTableModel tableModel;
    private JButton btnApprove, btnReject, btnViewAll, btnRefresh, btnComplete;
    private JButton btnViewDocuments, btnAIAnalyze;
    private JButton btnShowCompleted, btnShowActive;
    private JLabel lblStatus;
    private JTextArea txtDocumentPreview;
    private JLabel lblToggleInfo;
    private boolean showCompleted = false;
    private FundingApplication currentApplication;

    public CommitteePanel() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel lblTitle = new JLabel("NAMAA SMART WAQF PLATFORM (Committee Decisions)", JLabel.CENTER);
        lblTitle.setFont(new Font("Arial", Font.BOLD, 20));
        lblTitle.setForeground(new Color(0, 102, 204));
        add(lblTitle, BorderLayout.NORTH);

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setDividerLocation(520);
        splitPane.setResizeWeight(0.45);

        JPanel leftPanel = new JPanel(new BorderLayout(5, 5));

        JPanel inputPanel = new JPanel(new GridBagLayout());
        inputPanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createEtchedBorder(),
            "Decision",
            TitledBorder.LEFT,
            TitledBorder.TOP,
            new Font("Arial", Font.BOLD, 12)
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0.3;
        JLabel lblAppID = new JLabel("Application ID:");
        lblAppID.setFont(new Font("Arial", Font.BOLD, 12));
        inputPanel.add(lblAppID, gbc);
        
        gbc.gridx = 1; gbc.weightx = 0.7;
        txtAppID = new JTextField(10);
        txtAppID.setPreferredSize(new Dimension(120, 28));
        inputPanel.add(txtAppID, gbc);

        leftPanel.add(inputPanel, BorderLayout.NORTH);

        JPanel docPanel = new JPanel(new BorderLayout(5, 5));
        docPanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createEtchedBorder(),
            "📎 Documents & Files",
            TitledBorder.LEFT,
            TitledBorder.TOP,
            new Font("Arial", Font.BOLD, 11)
        ));
        docPanel.setPreferredSize(new Dimension(400, 120));
        
        txtDocumentPreview = new JTextArea(5, 20);
        txtDocumentPreview.setEditable(false);
        txtDocumentPreview.setFont(new Font("Monospaced", Font.PLAIN, 11));
        txtDocumentPreview.setBackground(new Color(255, 255, 240));
        txtDocumentPreview.setText("Select an application to view its documents.");
        
        JScrollPane scrollDoc = new JScrollPane(txtDocumentPreview);
        scrollDoc.setPreferredSize(new Dimension(380, 90));
        docPanel.add(scrollDoc, BorderLayout.CENTER);
        
        JPanel docButtonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 2));
        
        btnViewDocuments = new JButton("📄 View All Documents");
        btnViewDocuments.setFont(new Font("Arial", Font.PLAIN, 11));
        btnViewDocuments.setBackground(new Color(0, 102, 204));
        btnViewDocuments.setForeground(Color.BLACK);
        btnViewDocuments.setFocusPainted(false);
        btnViewDocuments.setPreferredSize(new Dimension(160, 30));
        btnViewDocuments.addActionListener(this);
        btnViewDocuments.setActionCommand("viewDocuments");
        docButtonPanel.add(btnViewDocuments);
        
        btnAIAnalyze = new JButton("🤖 AI Analyze Documents");
        btnAIAnalyze.setFont(new Font("Arial", Font.PLAIN, 11));
        btnAIAnalyze.setBackground(new Color(153, 0, 153));
        btnAIAnalyze.setForeground(Color.BLACK);
        btnAIAnalyze.setFocusPainted(false);
        btnAIAnalyze.setPreferredSize(new Dimension(170, 30));
        btnAIAnalyze.addActionListener(this);
        btnAIAnalyze.setActionCommand("aiAnalyze");
        docButtonPanel.add(btnAIAnalyze);
        
        docPanel.add(docButtonPanel, BorderLayout.SOUTH);
        leftPanel.add(docPanel, BorderLayout.CENTER);

        JPanel decisionPanel = new JPanel(new BorderLayout());
        decisionPanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createEtchedBorder(),
            "📋 Decision Log",
            TitledBorder.LEFT,
            TitledBorder.TOP,
            new Font("Arial", Font.BOLD, 11)
        ));
        decisionPanel.setPreferredSize(new Dimension(400, 100));
        
        txtDecision = new JTextArea(4, 20);
        txtDecision.setEditable(false);
        txtDecision.setFont(new Font("Monospaced", Font.PLAIN, 11));
        txtDecision.setBackground(new Color(255, 255, 240));
        txtDecision.setText("📌 Decision Log\n");
        txtDecision.append("─────────────────────────────────────────────\n");
        txtDecision.append("Ready - Select an application and make a decision\n");
        
        JScrollPane scrollDecision = new JScrollPane(txtDecision);
        scrollDecision.setPreferredSize(new Dimension(380, 90));
        scrollDecision.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        decisionPanel.add(scrollDecision, BorderLayout.CENTER);
        
        leftPanel.add(decisionPanel, BorderLayout.SOUTH);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 5));
        buttonPanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createEtchedBorder(),
            "Actions",
            TitledBorder.LEFT,
            TitledBorder.TOP,
            new Font("Arial", Font.BOLD, 12)
        ));
        buttonPanel.setPreferredSize(new Dimension(400, 90));

        btnApprove = new JButton("✅ Approve");
        btnApprove.setFont(new Font("Arial", Font.BOLD, 12));
        btnApprove.setBackground(new Color(0, 153, 76));
        btnApprove.setForeground(Color.BLACK);
        btnApprove.setFocusPainted(false);
        btnApprove.setPreferredSize(new Dimension(120, 35));
        btnApprove.addActionListener(this);
        buttonPanel.add(btnApprove);

        btnReject = new JButton("❌ Reject");
        btnReject.setFont(new Font("Arial", Font.BOLD, 12));
        btnReject.setBackground(new Color(200, 0, 0));
        btnReject.setForeground(Color.BLACK);
        btnReject.setFocusPainted(false);
        btnReject.setPreferredSize(new Dimension(120, 35));
        btnReject.addActionListener(this);
        buttonPanel.add(btnReject);

        btnComplete = new JButton("📌 Complete Project");
        btnComplete.setFont(new Font("Arial", Font.BOLD, 12));
        btnComplete.setBackground(new Color(255, 153, 0));
        btnComplete.setForeground(Color.BLACK);
        btnComplete.setFocusPainted(false);
        btnComplete.setPreferredSize(new Dimension(140, 35));
        btnComplete.addActionListener(this);
        buttonPanel.add(btnComplete);

        buttonPanel.add(new JLabel("  |  "));

        btnRefresh = new JButton("🔄 Refresh");
        btnRefresh.setFont(new Font("Arial", Font.BOLD, 12));
        btnRefresh.setBackground(new Color(0, 102, 204));
        btnRefresh.setForeground(Color.BLACK);
        btnRefresh.setFocusPainted(false);
        btnRefresh.setPreferredSize(new Dimension(110, 35));
        btnRefresh.addActionListener(this);
        buttonPanel.add(btnRefresh);

        btnViewAll = new JButton("📋 View All");
        btnViewAll.setFont(new Font("Arial", Font.BOLD, 12));
        btnViewAll.setBackground(new Color(153, 0, 153));
        btnViewAll.setForeground(Color.BLACK);
        btnViewAll.setFocusPainted(false);
        btnViewAll.setPreferredSize(new Dimension(110, 35));
        btnViewAll.addActionListener(this);
        buttonPanel.add(btnViewAll);

        buttonPanel.add(new JLabel("  |  "));

        btnShowCompleted = new JButton("📊 Completed");
        btnShowCompleted.setFont(new Font("Arial", Font.BOLD, 11));
        btnShowCompleted.setBackground(new Color(0, 0, 153));
        btnShowCompleted.setForeground(Color.BLACK);
        btnShowCompleted.setFocusPainted(false);
        btnShowCompleted.setPreferredSize(new Dimension(110, 30));
        btnShowCompleted.addActionListener(this);
        buttonPanel.add(btnShowCompleted);

        btnShowActive = new JButton("📋 Active");
        btnShowActive.setFont(new Font("Arial", Font.BOLD, 11));
        btnShowActive.setBackground(new Color(0, 102, 102));
        btnShowActive.setForeground(Color.BLACK);
        btnShowActive.setFocusPainted(false);
        btnShowActive.setPreferredSize(new Dimension(110, 30));
        btnShowActive.addActionListener(this);
        buttonPanel.add(btnShowActive);

        leftPanel.add(buttonPanel, BorderLayout.SOUTH);

        JPanel rightPanel = new JPanel(new BorderLayout(5, 5));

        JPanel tableHeaderPanel = new JPanel(new BorderLayout());
        JLabel lblTableTitle = new JLabel("Applications", JLabel.CENTER);
        lblTableTitle.setFont(new Font("Arial", Font.BOLD, 14));
        tableHeaderPanel.add(lblTableTitle, BorderLayout.CENTER);
        
        lblToggleInfo = new JLabel("Showing: Active Applications", JLabel.CENTER);
        lblToggleInfo.setFont(new Font("Arial", Font.PLAIN, 10));
        lblToggleInfo.setForeground(Color.GRAY);
        tableHeaderPanel.add(lblToggleInfo, BorderLayout.SOUTH);
        
        rightPanel.add(tableHeaderPanel, BorderLayout.NORTH);

        String[] columns = {"ID", "Project", "Amount (QR)", "Status", "Type"};
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
                    txtAppID.setText(String.valueOf(id));
                    setStatus("Selected: " + tableModel.getValueAt(row, 1) + " (ID: " + id + ")");
                    loadApplicationData(id);
                }
            }
        });

        table.setDefaultRenderer(Object.class, new javax.swing.table.DefaultTableCellRenderer() {
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
                    } else if (status.contains("COMPLETED")) {
                        c.setBackground(new Color(0, 102, 204));
                        c.setForeground(Color.WHITE);
                    } else if (status.contains("FUNDED")) {
                        c.setBackground(new Color(0, 153, 76));
                        c.setForeground(Color.WHITE);
                    } else {
                        c.setBackground(table.getBackground());
                        c.setForeground(table.getForeground());
                    }
                } else if (column == 4) {
                    String type = (String) value;
                    if (type.contains("Completed") || type.contains("Historical")) {
                        c.setForeground(new Color(0, 102, 204));
                        c.setFont(c.getFont().deriveFont(Font.ITALIC));
                    } else {
                        c.setForeground(Color.BLACK);
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
        table.getColumnModel().getColumn(3).setPreferredWidth(100);
        table.getColumnModel().getColumn(4).setPreferredWidth(80);

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createEtchedBorder());
        rightPanel.add(scrollPane, BorderLayout.CENTER);

        lblStatus = new JLabel("Click a row to load application details", JLabel.CENTER);
        lblStatus.setFont(new Font("Arial", Font.PLAIN, 11));
        lblStatus.setForeground(Color.GRAY);
        rightPanel.add(lblStatus, BorderLayout.SOUTH);

        splitPane.setLeftComponent(leftPanel);
        splitPane.setRightComponent(rightPanel);
        add(splitPane, BorderLayout.CENTER);

        updateTable();
    }

    // ===== LOAD APPLICATION DATA =====
    private void loadApplicationData(int applicationId) {
        FundingApplication app = FundingService.searchApplication(applicationId);
        
        if (app != null) {
            currentApplication = app;
            updateDocumentPreview(app);
            return;
        }
        
        HistoricalProject hist = HistoricalDataService.getProjectById(applicationId);
        if (hist != null) {
            txtDocumentPreview.setText(
                "📋 HISTORICAL PROJECT #" + applicationId + "\n" +
                "─────────────────────────────────────────────\n" +
                "Project: " + hist.getProjectName() + "\n" +
                "Category: " + hist.getCategory() + "\n" +
                "Status: ✅ COMPLETED\n" +
                "Success Rate: " + String.format("%.0f", hist.getSuccessRate() * 100) + "%\n" +
                "Namaa Index: " + String.format("%.2f", hist.getFinalIndex()) + "\n\n" +
                "💡 This project is already completed.\n" +
                "View the Analytics panel for more details."
            );
            currentApplication = null;
            return;
        }
        
        txtDocumentPreview.setText("⚠️ Application not found.");
        currentApplication = null;
    }

    // ===== UPDATE DOCUMENT PREVIEW (
    private void updateDocumentPreview(FundingApplication app) {
        if (app == null) {
            txtDocumentPreview.setText("No application selected");
            return;
        }
        
        int appId = app.getApplicationID();
        StringBuilder docs = new StringBuilder();
        
        docs.append("📋 DOCUMENTS FOR APPLICATION #").append(appId).append("\n");
        docs.append("─────────────────────────────────────────────\n");
        docs.append("Beneficiary: ").append(app.getBeneficiary().getFullName()).append("\n");
        docs.append("Project: ").append(app.getProject().getProjectName()).append("\n");
        docs.append("Status: ").append(app.getStatus()).append("\n");
        docs.append("─────────────────────────────────────────────\n\n");
        
        // ===== BUSINESS PLAN =====
        docs.append("📄 Business Plan:\n");
        if (app.hasBusinessPlan()) {
            String fileName = app.getBusinessPlanFile();
            String filePath = DocumentStorageService.getDocumentPath(appId, fileName);
            docs.append("   ✅ File: ").append(fileName).append("\n");
            if (filePath != null) {
                docs.append("   📁 Path: ").append(filePath).append("\n");
                File file = new File(filePath);
                if (file.exists()) {
                    docs.append("   📊 Size: ").append(file.length() / 1024).append(" KB\n");
                } else {
                    docs.append("   ⚠️ File not found in storage!\n");
                }
            } else {
                docs.append("   ⚠️ No file path found\n");
            }
        } else {
            docs.append("   ⏳ Not Uploaded\n");
        }
        docs.append("\n");
        
        // ===== FINANCIAL STATEMENTS =====
        docs.append("📊 Financial Statements:\n");
        if (app.hasFinancialStatements()) {
            String fileName = app.getFinancialStatementsFile();
            String filePath = DocumentStorageService.getDocumentPath(appId, fileName);
            docs.append("   ✅ File: ").append(fileName).append("\n");
            if (filePath != null) {
                docs.append("   📁 Path: ").append(filePath).append("\n");
                File file = new File(filePath);
                if (file.exists()) {
                    docs.append("   📊 Size: ").append(file.length() / 1024).append(" KB\n");
                } else {
                    docs.append("   ⚠️ File not found in storage!\n");
                }
            } else {
                docs.append("   ⚠️ No file path found\n");
            }
        } else {
            docs.append("   ⏳ Not Uploaded\n");
        }
        docs.append("\n");
        
        // ===== SUPPORTING DOCUMENTS =====
        docs.append("📎 Supporting Documents:\n");
        if (app.hasSupportingDocuments()) {
            String fileName = app.getSupportingDocumentsFile();
            String filePath = DocumentStorageService.getDocumentPath(appId, fileName);
            docs.append("   ✅ File: ").append(fileName).append("\n");
            if (filePath != null) {
                docs.append("   📁 Path: ").append(filePath).append("\n");
                File file = new File(filePath);
                if (file.exists()) {
                    docs.append("   📊 Size: ").append(file.length() / 1024).append(" KB\n");
                } else {
                    docs.append("   ⚠️ File not found in storage!\n");
                }
            } else {
                docs.append("   ⚠️ No file path found\n");
            }
        } else {
            docs.append("   ⏳ Not Uploaded\n");
        }
        docs.append("\n");
        
        // ===== ADDITIONAL FILES =====
        if (app.getUploadedFileNames() != null && !app.getUploadedFileNames().isEmpty()) {
            docs.append("📁 Additional Files:\n");
            for (int i = 0; i < app.getUploadedFileNames().size(); i++) {
                String fileName = app.getUploadedFileNames().get(i);
                String filePath = null;
                if (app.getUploadedFilePaths() != null && i < app.getUploadedFilePaths().size()) {
                    filePath = app.getUploadedFilePaths().get(i);
                }
                docs.append("   📎 ").append(fileName).append("\n");
                if (filePath != null && !filePath.isEmpty()) {
                    File file = new File(filePath);
                    if (file.exists()) {
                        docs.append("      📁 ").append(filePath).append("\n");
                        docs.append("      📊 Size: ").append(file.length() / 1024).append(" KB\n");
                    } else {
                        docs.append("      ⚠️ File not found\n");
                    }
                }
            }
        }
        docs.append("\n");
        
        // ===== ASSESSMENT INFO =====
        ProjectAssessment assessment = AssessmentService.searchAssessmentByApplication(appId);
        if (assessment != null) {
            docs.append("📊 Assessment:\n");
            docs.append("   PRI Score: ").append(String.format("%.2f", assessment.getPriScore())).append("\n");
            docs.append("   Recommendation: ").append(assessment.getRecommendation()).append("\n");
            docs.append("   Date: ").append(assessment.getAssessmentDate()).append("\n");
        }
        
        txtDocumentPreview.setText(docs.toString());
        txtDocumentPreview.setCaretPosition(0);
    }
    
    // ===== VIEW DOCUMENTS =====
    private void viewDocuments() {
        String idText = txtAppID.getText().trim();
        if (idText.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "Please select an application first.",
                "No Application Selected",
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int id = Integer.parseInt(idText);
        FundingApplication app = null;
        
        if (currentApplication != null && currentApplication.getApplicationID() == id) {
            app = currentApplication;
        } else {
            app = FundingService.searchApplication(id);
        }
        
        if (app != null) {
            showApplicationDocuments(app);
            return;
        }
        
        HistoricalProject hist = HistoricalDataService.getProjectById(id);
        if (hist != null) {
            showHistoricalProject(hist);
            return;
        }
        
        JOptionPane.showMessageDialog(this,
            "Application not found!",
            "Error",
            JOptionPane.ERROR_MESSAGE);
    }

    // ===== SHOW APPLICATION DOCUMENTS =====
    private void showApplicationDocuments(FundingApplication app) {
        // Get latest version from service
        FundingApplication latestApp = FundingService.searchApplication(app.getApplicationID());
        if (latestApp != null) {
            app = latestApp;
            currentApplication = latestApp;
        }
        
        int appId = app.getApplicationID();
        
        JDialog docDialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), 
            "Documents - " + app.getProject().getProjectName(), true);
        docDialog.setLayout(new BorderLayout(10, 10));
        docDialog.setSize(750, 550);
        docDialog.setLocationRelativeTo(this);
        
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // ===== Document List =====
        JPanel docListPanel = new JPanel();
        docListPanel.setLayout(new BoxLayout(docListPanel, BoxLayout.Y_AXIS));
        docListPanel.setBackground(new Color(245, 248, 250));
        docListPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        
        JScrollPane scrollList = new JScrollPane(docListPanel);
        scrollList.setPreferredSize(new Dimension(320, 400));
        scrollList.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
            "📄 Uploaded Documents",
            TitledBorder.LEFT,
            TitledBorder.TOP,
            new Font("Arial", Font.BOLD, 12)
        ));
        
        boolean hasFiles = false;
        
        // ===== Business Plan =====
        if (app.hasBusinessPlan()) {
            String fileName = app.getBusinessPlanFile();
            String filePath = DocumentStorageService.getDocumentPath(appId, fileName);
            if (filePath != null) {
                hasFiles = true;
                JButton bpButton = createDocButton("📄 " + fileName, filePath);
                docListPanel.add(bpButton);
            } else {
                JLabel bpLabel = new JLabel("📄 Business Plan: ⚠️ File Missing");
                bpLabel.setFont(new Font("Arial", Font.PLAIN, 11));
                bpLabel.setForeground(Color.RED);
                bpLabel.setBorder(BorderFactory.createEmptyBorder(8, 10, 8, 10));
                bpLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
                docListPanel.add(bpLabel);
            }
        } else {
            JLabel bpLabel = new JLabel("📄 Business Plan: ⏳ Not Uploaded");
            bpLabel.setFont(new Font("Arial", Font.PLAIN, 11));
            bpLabel.setForeground(Color.GRAY);
            bpLabel.setBorder(BorderFactory.createEmptyBorder(8, 10, 8, 10));
            bpLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
            docListPanel.add(bpLabel);
        }
        
        // ===== Financial Statements =====
        if (app.hasFinancialStatements()) {
            String fileName = app.getFinancialStatementsFile();
            String filePath = DocumentStorageService.getDocumentPath(appId, fileName);
            if (filePath != null) {
                hasFiles = true;
                JButton fsButton = createDocButton("📊 " + fileName, filePath);
                docListPanel.add(fsButton);
            } else {
                JLabel fsLabel = new JLabel("📊 Financial Statements: ⚠️ File Missing");
                fsLabel.setFont(new Font("Arial", Font.PLAIN, 11));
                fsLabel.setForeground(Color.RED);
                fsLabel.setBorder(BorderFactory.createEmptyBorder(8, 10, 8, 10));
                fsLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
                docListPanel.add(fsLabel);
            }
        } else {
            JLabel fsLabel = new JLabel("📊 Financial Statements: ⏳ Not Uploaded");
            fsLabel.setFont(new Font("Arial", Font.PLAIN, 11));
            fsLabel.setForeground(Color.GRAY);
            fsLabel.setBorder(BorderFactory.createEmptyBorder(8, 10, 8, 10));
            fsLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
            docListPanel.add(fsLabel);
        }
        
        // ===== Supporting Documents =====
        if (app.hasSupportingDocuments()) {
            String fileName = app.getSupportingDocumentsFile();
            String filePath = DocumentStorageService.getDocumentPath(appId, fileName);
            if (filePath != null) {
                hasFiles = true;
                JButton sdButton = createDocButton("📎 " + fileName, filePath);
                docListPanel.add(sdButton);
            } else {
                JLabel sdLabel = new JLabel("📎 Supporting Documents: ⚠️ File Missing");
                sdLabel.setFont(new Font("Arial", Font.PLAIN, 11));
                sdLabel.setForeground(Color.RED);
                sdLabel.setBorder(BorderFactory.createEmptyBorder(8, 10, 8, 10));
                sdLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
                docListPanel.add(sdLabel);
            }
        } else {
            JLabel sdLabel = new JLabel("📎 Supporting Documents: ⏳ Not Uploaded");
            sdLabel.setFont(new Font("Arial", Font.PLAIN, 11));
            sdLabel.setForeground(Color.GRAY);
            sdLabel.setBorder(BorderFactory.createEmptyBorder(8, 10, 8, 10));
            sdLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
            docListPanel.add(sdLabel);
        }
        
        // ===== Additional files =====
        if (app.getUploadedFileNames() != null && !app.getUploadedFileNames().isEmpty()) {
            JLabel sepLabel = new JLabel("─────────────────────────────────────");
            sepLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
            docListPanel.add(sepLabel);
            
            JLabel extraLabel = new JLabel("📁 Additional Files:");
            extraLabel.setFont(new Font("Arial", Font.BOLD, 12));
            extraLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
            docListPanel.add(extraLabel);
            
            for (int i = 0; i < app.getUploadedFileNames().size(); i++) {
                String fileName = app.getUploadedFileNames().get(i);
                String filePath = (app.getUploadedFilePaths() != null && i < app.getUploadedFilePaths().size()) 
                                  ? app.getUploadedFilePaths().get(i) : "";
                if (filePath != null && !filePath.isEmpty()) {
                    hasFiles = true;
                    File file = new File(filePath);
                    if (file.exists()) {
                        JButton fileButton = createDocButton("📎 " + fileName, filePath);
                        docListPanel.add(fileButton);
                    }
                }
            }
        }
        
        // ===== If no files =====
        if (!hasFiles) {
            JLabel noFilesLabel = new JLabel("No documents uploaded for this application.");
            noFilesLabel.setFont(new Font("Arial", Font.ITALIC, 12));
            noFilesLabel.setForeground(Color.GRAY);
            noFilesLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
            noFilesLabel.setBorder(BorderFactory.createEmptyBorder(20, 10, 20, 10));
            docListPanel.add(noFilesLabel);
        }
        
        mainPanel.add(scrollList, BorderLayout.WEST);
        
        // ===== Preview Panel =====
        JPanel previewPanel = new JPanel(new BorderLayout(10, 10));
        previewPanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
            "📄 File Preview",
            TitledBorder.LEFT,
            TitledBorder.TOP,
            new Font("Arial", Font.BOLD, 12)
        ));
        
        JTextArea previewArea = new JTextArea(15, 40);
        previewArea.setEditable(false);
        previewArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        previewArea.setBackground(new Color(255, 255, 240));
        previewArea.setText("Click a document from the list to view its contents...");
        
        JScrollPane scrollPreview = new JScrollPane(previewArea);
        previewPanel.add(scrollPreview, BorderLayout.CENTER);
        
        mainPanel.add(previewPanel, BorderLayout.CENTER);
        docDialog.add(mainPanel, BorderLayout.CENTER);
        
        // ===== Close Button =====
        JPanel closePanel = new JPanel();
        JButton btnClose = new JButton("Close");
        btnClose.setFont(new Font("Arial", Font.BOLD, 12));
        btnClose.setBackground(new Color(200, 50, 50));
        btnClose.setForeground(Color.BLACK);
        btnClose.setFocusPainted(false);
        btnClose.setPreferredSize(new Dimension(100, 35));
        btnClose.addActionListener(e -> docDialog.dispose());
        closePanel.add(btnClose);
        docDialog.add(closePanel, BorderLayout.SOUTH);
        
        docDialog.setVisible(true);
    }

    // ===== CREATE DOCUMENT BUTTON =====
    private JButton createDocButton(String text, String filePath) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.PLAIN, 11));
        button.setBackground(Color.WHITE);
        button.setForeground(new Color(0, 102, 204));
        button.setHorizontalAlignment(SwingConstants.LEFT);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(200, 200, 200)),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        button.setAlignmentX(Component.LEFT_ALIGNMENT);
        button.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));
        button.addActionListener(e -> viewFileContent(filePath, text));
        return button;
    }

    // ===== VIEW FILE CONTENT =====
    private void viewFileContent(String filePath, String fileName) {
        try {
            File file = new File(filePath);
            if (!file.exists()) {
                JOptionPane.showMessageDialog(this,
                    "File not found: " + filePath + "\n\n" +
                    "The file may have been moved or deleted.",
                    "File Not Found",
                    JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            String ext = "";
            int dotIndex = fileName.lastIndexOf(".");
            if (dotIndex > 0) {
                ext = fileName.substring(dotIndex + 1).toLowerCase();
            }
            
            if (ext.equals("txt") || ext.equals("csv") || ext.equals("json") || ext.equals("xml") || 
                ext.equals("html") || ext.equals("css") || ext.equals("js") || ext.equals("java") ||
                ext.equals("py") || ext.equals("sql") || ext.equals("log") || ext.equals("md")) {
                
                StringBuilder content = new StringBuilder();
                try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                    String line;
                    int lineCount = 0;
                    while ((line = reader.readLine()) != null) {
                        content.append(line).append("\n");
                        lineCount++;
                        if (lineCount > 500) {
                            content.append("\n... (File truncated, only showing first 500 lines)");
                            break;
                        }
                    }
                }
                
                JDialog contentDialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), 
                    "File Content: " + new File(filePath).getName(), true);
                contentDialog.setLayout(new BorderLayout(10, 10));
                contentDialog.setSize(600, 450);
                contentDialog.setLocationRelativeTo(this);
                
                JTextArea textArea = new JTextArea(content.toString());
                textArea.setEditable(false);
                textArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
                textArea.setBackground(new Color(255, 255, 240));
                JScrollPane scroll = new JScrollPane(textArea);
                contentDialog.add(scroll, BorderLayout.CENTER);
                
                JPanel infoPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
                infoPanel.setBackground(Color.WHITE);
                JLabel infoLabel = new JLabel("File: " + file.getName() + " | Size: " + (file.length() / 1024) + " KB");
                infoLabel.setFont(new Font("Arial", Font.PLAIN, 10));
                infoLabel.setForeground(Color.GRAY);
                infoPanel.add(infoLabel);
                contentDialog.add(infoPanel, BorderLayout.SOUTH);
                
                JPanel closePanel = new JPanel();
                JButton btnClose = new JButton("Close");
                btnClose.addActionListener(e -> contentDialog.dispose());
                closePanel.add(btnClose);
                contentDialog.add(closePanel, BorderLayout.SOUTH);
                
                contentDialog.setVisible(true);
                
            } else if (ext.equals("jpg") || ext.equals("jpeg") || ext.equals("png") || 
                       ext.equals("gif") || ext.equals("bmp") || ext.equals("tiff")) {
                
                JOptionPane.showMessageDialog(this,
                    "🖼️ Image File\n\n" +
                    "File: " + file.getName() + "\n" +
                    "Size: " + (file.length() / 1024) + " KB\n" +
                    "Location: " + filePath + "\n\n" +
                    "💡 To view this image, open it directly using your file explorer.",
                    "Image File",
                    JOptionPane.INFORMATION_MESSAGE);
                
            } else if (ext.equals("pdf")) {
                JOptionPane.showMessageDialog(this,
                    "📄 PDF File\n\n" +
                    "File: " + file.getName() + "\n" +
                    "Size: " + (file.length() / 1024) + " KB\n" +
                    "Location: " + filePath + "\n\n" +
                    "💡 To view this PDF, open it with a PDF reader.",
                    "PDF File",
                    JOptionPane.INFORMATION_MESSAGE);
                
            } else {
                JOptionPane.showMessageDialog(this,
                    "📄 File Information\n\n" +
                    "File: " + file.getName() + "\n" +
                    "Type: " + (ext.isEmpty() ? "Unknown" : ext.toUpperCase()) + "\n" +
                    "Size: " + (file.length() / 1024) + " KB\n" +
                    "Location: " + filePath + "\n\n" +
                    "💡 To view this file, open it directly using your file explorer.",
                    "File Information",
                    JOptionPane.INFORMATION_MESSAGE);
            }
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                "Error reading file: " + e.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }

    // ===== SHOW HISTORICAL PROJECT =====
    private void showHistoricalProject(HistoricalProject hist) {
        JDialog histDialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), 
            "Historical Project Details", true);
        histDialog.setLayout(new BorderLayout(10, 10));
        histDialog.setSize(500, 350);
        histDialog.setLocationRelativeTo(this);
        
        JTextArea histArea = new JTextArea();
        histArea.setEditable(false);
        histArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        histArea.setBackground(new Color(255, 255, 240));
        histArea.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        
        StringBuilder content = new StringBuilder();
        content.append("╔═══════════════════════════════════════════════════════════════╗\n");
        content.append("║              HISTORICAL PROJECT DETAILS                       ║\n");
        content.append("╚═══════════════════════════════════════════════════════════════╝\n\n");
        content.append("Project ID:     ").append(hist.getProjectId()).append("\n");
        content.append("Project Name:   ").append(hist.getProjectName()).append("\n");
        content.append("Category:       ").append(hist.getCategory()).append("\n");
        content.append("Status:         ✅ COMPLETED\n");
        content.append("Success Rate:   ").append(String.format("%.0f", hist.getSuccessRate() * 100)).append("%\n");
        content.append("Namaa Index:    ").append(String.format("%.2f", hist.getFinalIndex())).append("\n");
        content.append("Cost:           ").append(String.format("%,.2f", hist.getActualCost())).append(" QR\n");
        content.append("Beneficiaries:  ").append(hist.getBeneficiariesReached()).append("\n");
        content.append("Duration:       ").append(hist.getActualDuration()).append(" months\n\n");
        content.append("─────────────────────────────────────────────────────────────────────\n");
        content.append("💡 LESSONS LEARNED:\n");
        content.append(hist.getLessonsLearned()).append("\n");
        
        histArea.setText(content.toString());
        histDialog.add(new JScrollPane(histArea), BorderLayout.CENTER);
        
        JPanel closePanel = new JPanel();
        JButton btnClose = new JButton("Close");
        btnClose.setFont(new Font("Arial", Font.BOLD, 12));
        btnClose.setBackground(new Color(0, 102, 204));
        btnClose.setForeground(Color.BLACK);
        btnClose.setFocusPainted(false);
        btnClose.setPreferredSize(new Dimension(100, 35));
        btnClose.addActionListener(e -> histDialog.dispose());
        closePanel.add(btnClose);
        histDialog.add(closePanel, BorderLayout.SOUTH);
        
        histDialog.setVisible(true);
    }

    // ===== AI ANALYZE DOCUMENTS =====
    private void aiAnalyzeDocuments() {
        String idText = txtAppID.getText().trim();
        if (idText.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "Please select an application first.",
                "No Application Selected",
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int id = Integer.parseInt(idText);
        FundingApplication app = null;
        
        if (currentApplication != null && currentApplication.getApplicationID() == id) {
            app = currentApplication;
        } else {
            app = FundingService.searchApplication(id);
        }
        
        if (app == null) {
            HistoricalProject hist = HistoricalDataService.getProjectById(id);
            if (hist != null) {
                JOptionPane.showMessageDialog(this,
                    "📌 This is a historical project (already completed).\n\n" +
                    "Success Rate: " + String.format("%.0f", hist.getSuccessRate() * 100) + "%\n" +
                    "Namaa Index: " + String.format("%.2f", hist.getFinalIndex()) + "\n\n" +
                    "🤖 AI analysis is only available for active applications.",
                    "Historical Project",
                    JOptionPane.INFORMATION_MESSAGE);
                return;
            }
            
            JOptionPane.showMessageDialog(this,
                "Application not found!",
                "Error",
                JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        final FundingApplication finalApp = app;
        int appId = app.getApplicationID();
        final int finalAppId = appId;
        
        JDialog aiDialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), 
            "🤖 AI Analysis", true);
        aiDialog.setLayout(new BorderLayout(10, 10));
        aiDialog.setSize(650, 500);
        aiDialog.setLocationRelativeTo(this);
        
        JTextArea aiResult = new JTextArea();
        aiResult.setEditable(false);
        aiResult.setFont(new Font("Monospaced", Font.PLAIN, 12));
        aiResult.setBackground(new Color(255, 255, 240));
        aiResult.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        aiResult.setText("🤖 AI is analyzing the application...\n\nPlease wait...");
        
        JScrollPane scroll = new JScrollPane(aiResult);
        aiDialog.add(scroll, BorderLayout.CENTER);
        
        JPanel progressPanel = new JPanel();
        progressPanel.setBackground(Color.WHITE);
        JLabel progressLabel = new JLabel("⏳ Analyzing...");
        progressLabel.setFont(new Font("Arial", Font.ITALIC, 11));
        progressLabel.setForeground(Color.GRAY);
        progressPanel.add(progressLabel);
        aiDialog.add(progressPanel, BorderLayout.SOUTH);
        
        SwingWorker<String, Void> worker = new SwingWorker<>() {
            @Override
            protected String doInBackground() {
                if (AIService.isAIAvailable()) {
                    Project project = finalApp.getProject();
                    Beneficiary beneficiary = finalApp.getBeneficiary();
                    ProjectAssessment assessment = AssessmentService.searchAssessmentByApplication(finalAppId);
                    
                    StringBuilder prompt = new StringBuilder();
                    prompt.append("Analyze this funding application and provide:\n\n");
                    prompt.append("1. PROJECT SUMMARY\n");
                    
                    if (project != null) {
                        prompt.append("Project: ").append(project.getProjectName() != null ? project.getProjectName() : "N/A").append("\n");
                        prompt.append("Sector: ").append(project.getSector() != null ? project.getSector() : "N/A").append("\n");
                        prompt.append("Description: ").append(project.getDescription() != null ? project.getDescription() : "N/A").append("\n");
                    } else {
                        prompt.append("Project: N/A\n");
                        prompt.append("Sector: N/A\n");
                        prompt.append("Description: N/A\n");
                    }
                    prompt.append("Amount: ").append(finalApp.getRequestedAmount()).append(" QR\n\n");
                    
                    prompt.append("2. BENEFICIARY INFORMATION\n");
                    if (beneficiary != null) {
                        prompt.append("Name: ").append(beneficiary.getFullName() != null ? beneficiary.getFullName() : "N/A").append("\n");
                        prompt.append("Education: ").append(beneficiary.getEducation() != null ? beneficiary.getEducation() : "N/A").append("\n");
                        prompt.append("Experience: ").append(beneficiary.getExperienceYears()).append(" years\n\n");
                    } else {
                        prompt.append("Name: N/A\n");
                        prompt.append("Education: N/A\n");
                        prompt.append("Experience: 0 years\n\n");
                    }
                    
                    if (assessment != null) {
                        prompt.append("3. ASSESSMENT SCORES\n");
                        prompt.append("PRI Score: ").append(assessment.getPriScore()).append("\n");
                        prompt.append("Economic: ").append(assessment.getEconomicScore()).append("\n");
                        prompt.append("Social: ").append(assessment.getSocialScore()).append("\n");
                        prompt.append("Technical: ").append(assessment.getTechnicalScore()).append("\n");
                        prompt.append("Innovation: ").append(assessment.getInnovationScore()).append("\n\n");
                        prompt.append("Recommendation: ").append(assessment.getRecommendation() != null ? assessment.getRecommendation() : "N/A").append("\n\n");
                    }
                    
                    prompt.append("4. Provide a brief recommendation on whether to approve or reject this application.\n");
                    prompt.append("5. List 2-3 key reasons for your recommendation.");
                    
                    return AIService.sendChatMessage(
                        "You are an expert committee evaluator. Analyze this application and provide a recommendation.",
                        prompt.toString()
                    );
                } else {
                    return generateFallbackAnalysis(finalApp);
                }
            }
            
            @Override
            protected void done() {
                try {
                    String result = get();
                    aiResult.setText(result);
                    progressPanel.setVisible(false);
                    aiDialog.revalidate();
                    aiDialog.repaint();
                } catch (Exception e) {
                    aiResult.setText("⚠️ Error: " + e.getMessage());
                    progressPanel.setVisible(false);
                }
            }
        };
        worker.execute();
        
        JPanel closePanel = new JPanel();
        JButton btnClose = new JButton("Close");
        btnClose.setFont(new Font("Arial", Font.BOLD, 12));
        btnClose.setBackground(new Color(0, 102, 204));
        btnClose.setForeground(Color.BLACK);
        btnClose.setFocusPainted(false);
        btnClose.setPreferredSize(new Dimension(100, 35));
        btnClose.addActionListener(e -> {
            worker.cancel(true);
            aiDialog.dispose();
        });
        closePanel.add(btnClose);
        aiDialog.add(closePanel, BorderLayout.SOUTH);
        
        aiDialog.setVisible(true);
    }

    private String generateFallbackAnalysis(FundingApplication app) {
        StringBuilder sb = new StringBuilder();
        sb.append("╔═══════════════════════════════════════════════════════════════╗\n");
        sb.append("║                 AI DOCUMENT ANALYSIS                         ║\n");
        sb.append("╚═══════════════════════════════════════════════════════════════╝\n\n");
        sb.append("1. PROJECT SUMMARY\n");
        sb.append("─────────────────────────────────────────────────────────────\n");
        sb.append("Project: ").append(app.getProject().getProjectName()).append("\n");
        sb.append("Sector: ").append(app.getProject().getSector()).append("\n");
        sb.append("Amount: ").append(String.format("%,.2f", app.getRequestedAmount())).append(" QR\n");
        sb.append("Description: ").append(app.getProject().getDescription()).append("\n\n");
        
        sb.append("2. BENEFICIARY INFORMATION\n");
        sb.append("─────────────────────────────────────────────────────────────\n");
        sb.append("Name: ").append(app.getBeneficiary().getFullName()).append("\n");
        sb.append("Education: ").append(app.getBeneficiary().getEducation()).append("\n");
        sb.append("Experience: ").append(app.getBeneficiary().getExperienceYears()).append(" years\n\n");
        
        ProjectAssessment assessment = AssessmentService.searchAssessmentByApplication(app.getApplicationID());
        if (assessment != null) {
            sb.append("3. ASSESSMENT SCORES\n");
            sb.append("─────────────────────────────────────────────────────────────\n");
            sb.append("PRI Score: ").append(String.format("%.2f", assessment.getPriScore())).append("\n");
            sb.append("Recommendation: ").append(assessment.getRecommendation()).append("\n\n");
        }
        
        sb.append("4. RECOMMENDATION\n");
        sb.append("─────────────────────────────────────────────────────────────\n");
        sb.append("✅ Based on the available information, this application appears to be well-prepared.\n");
        sb.append("   The beneficiary has relevant experience and the project is in a promising sector.\n\n");
        
        sb.append("5. KEY REASONS\n");
        sb.append("─────────────────────────────────────────────────────────────\n");
        sb.append("  • Complete and clear business plan\n");
        sb.append("  • Beneficiary has relevant experience\n");
        sb.append("  • Realistic funding request\n\n");
        
        sb.append("📌 To enable full AI analysis, set your API key in config.properties");
        return sb.toString();
    }

    // ===== UPDATE TABLE =====
    private void updateTable() {
        tableModel.setRowCount(0);
        
        if (showCompleted) {
            for (HistoricalProject p : HistoricalDataService.getAllProjects()) {
                Object[] row = {
                    p.getProjectId(),
                    p.getProjectName(),
                    String.format("%.2f", p.getActualCost()),
                    "✅ COMPLETED",
                    "Historical"
                };
                tableModel.addRow(row);
            }
            updateToggleLabel("Showing: Completed Projects");
        } else {
            for (FundingApplication app : FundingService.getApplications()) {
                ApplicationStatus status = app.getStatus();
                String statusDisplay = getStatusDisplay(status);
                Object[] row = {
                    app.getApplicationID(),
                    app.getProject().getProjectName(),
                    String.format("%.2f", app.getRequestedAmount()),
                    statusDisplay,
                    "Active"
                };
                tableModel.addRow(row);
            }
            updateToggleLabel("Showing: Active Applications");
        }
        
        lblStatus.setText("Showing " + tableModel.getRowCount() + " items");
    }

    private String getStatusDisplay(ApplicationStatus status) {
        if (status == null) return "⏳ UNKNOWN";
        
        switch (status) {
            case APPROVED:    return "✅ APPROVED";
            case REJECTED:    return "❌ REJECTED";
            case PENDING:     return "⏳ PENDING";
            case UNDER_REVIEW: return "🔍 UNDER_REVIEW";
            case FUNDED:      return "💰 FUNDED";
            case COMPLETED:   return "📌 COMPLETED";
            default:          return "⏳ " + status.name();
        }
    }

    private void updateToggleLabel(String text) {
        if (lblToggleInfo != null) {
            lblToggleInfo.setText(text);
        }
    }

    // ===== COMPLETE PROJECT =====
    private void completeProject(int applicationId) {
        try {
            FundingApplication app = FundingService.searchApplication(applicationId);
            
            if (app == null) {
                JOptionPane.showMessageDialog(this, "Application not found!");
                return;
            }
            
            ApplicationStatus status = app.getStatus();
            if (status != ApplicationStatus.APPROVED && status != ApplicationStatus.FUNDED) {
                JOptionPane.showMessageDialog(this, 
                    "❌ Only Approved or Funded projects can be completed!\n" +
                    "Current status: " + status,
                    "Invalid Status",
                    JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            HistoricalProject existing = HistoricalDataService.getProjectById(applicationId);
            if (existing != null) {
                JOptionPane.showMessageDialog(this,
                    "⚠️ This project is already in the history!\n" +
                    "Project: " + app.getProject().getProjectName(),
                    "Already Completed",
                    JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            ProjectAssessment assessment = AssessmentService.searchAssessmentByApplication(applicationId);
            NamaaIndex index = NamaaIndexService.getIndexByProject(app.getProject().getProjectID());
            
            JFrame parentFrame = (JFrame) SwingUtilities.getWindowAncestor(this);
            CompleteProjectDialog dialog = new CompleteProjectDialog(parentFrame, app, assessment, index);
            dialog.setVisible(true);
            
            updateTable();
            
            txtDecision.append("\n📌 Application " + applicationId + " COMPLETED at " + LocalTime.now());
            
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                "Error completing project: " + ex.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        try {
            if (e.getSource() == btnShowCompleted) {
                showCompleted = true;
                updateTable();
                setStatus("Showing completed projects");
                return;
            }
            
            if (e.getSource() == btnShowActive) {
                showCompleted = false;
                updateTable();
                setStatus("Showing active applications");
                return;
            }
            
            String idText = txtAppID.getText().trim();
            if (idText.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                    "Please enter or select an Application ID.",
                    "Missing ID",
                    JOptionPane.WARNING_MESSAGE);
                return;
            }

            int id = Integer.parseInt(idText);

            if (e.getSource() == btnApprove) {
                FundingApplication app = FundingService.searchApplication(id);
                if (app != null) {
                    app.setStatus(ApplicationStatus.APPROVED);
                    FundingService.updateApplication(app);
                    String timestamp = LocalTime.now().toString();
                    txtDecision.append("\n✅ Application " + id + " APPROVED at " + timestamp);
                    JOptionPane.showMessageDialog(this, "✅ Application approved successfully!");
                    updateTable();
                    setStatus("Application " + id + " approved");
                }
                
            } else if (e.getSource() == btnReject) {
                String reason = JOptionPane.showInputDialog(this,
                    "Please provide a reason for rejection:",
                    "Rejection Reason",
                    JOptionPane.QUESTION_MESSAGE);
                if (reason == null) return;
                
                FundingApplication app = FundingService.searchApplication(id);
                if (app != null) {
                    app.setStatus(ApplicationStatus.REJECTED);
                    FundingService.updateApplication(app);
                    String timestamp = LocalTime.now().toString();
                    txtDecision.append("\n❌ Application " + id + " REJECTED at " + timestamp);
                    txtDecision.append("\n   Reason: " + reason);
                    JOptionPane.showMessageDialog(this, "❌ Application rejected!");
                    updateTable();
                    setStatus("Application " + id + " rejected");
                }
                
            } else if (e.getSource() == btnRefresh) {
                updateTable();
                txtDecision.append("\n🔄 Table refreshed at " + LocalTime.now());
                setStatus("Table refreshed");
                
            } else if (e.getSource() == btnViewAll) {
                StringBuilder sb = new StringBuilder("\n=== All Applications ===\n");
                for (FundingApplication app : FundingService.getApplications()) {
                    sb.append("  ID: ").append(app.getApplicationID())
                      .append(" | ").append(app.getProject().getProjectName())
                      .append(" | ").append(app.getStatus()).append("\n");
                }
                sb.append("\n=== Completed Projects ===\n");
                for (HistoricalProject p : HistoricalDataService.getAllProjects()) {
                    sb.append("  ID: ").append(p.getProjectId())
                      .append(" | ").append(p.getProjectName())
                      .append(" | ✅ COMPLETED\n");
                }
                txtDecision.setText(sb.toString());
                setStatus("Showing all applications and completed projects");
                
            } else if (e.getSource() == btnComplete) {
                completeProject(id);
                
            } else if (e.getSource() == btnViewDocuments) {
                viewDocuments();
                
            } else if (e.getSource() == btnAIAnalyze) {
                aiAnalyzeDocuments();
            }
            
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                "Error: " + ex.getMessage(),
                "Operation Failed",
                JOptionPane.ERROR_MESSAGE);
            setStatus("Error: " + ex.getMessage());
        }
    }

    private void setStatus(String message) {
        lblStatus.setText(message);
    }
}