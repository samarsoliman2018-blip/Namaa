package UI;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.time.format.DateTimeFormatter;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import Model.*;
import Service.*;

public class CommitteeDashboardPanel extends JPanel implements ActionListener {
    private CommitteeMember committeeMember;
    
    // ===== STATS LABELS =====
    private JLabel lblPendingReviews, lblAvgPRI, lblHighRiskProjects;
    private JLabel lblRecommendedProjects, lblBudgetRemaining, lblTotalApplications;
    private JLabel lblCommitteeName;
    
    // ===== APPLICATION REVIEW PANEL =====
    private JTable appTable;
    private DefaultTableModel appModel;
    private JTextArea txtApplicationDetails;
    private JTextArea txtAIAssessment;
    private JButton btnApprove, btnReject, btnRequestRevision;
    private JButton btnViewDetails, btnRefresh;
    private JComboBox<String> cmbFilterStatus;
    private JLabel lblSelectedApp;
    private FundingApplication selectedApplication;
    private ProjectAssessment selectedAssessment;
    private NamaaIndex selectedIndex;
    
    // ===== AI SUMMARY PANEL =====
    private JPanel aiSummaryPanel;
    private JTextArea txtAIRecommendations;
    private JLabel lblAIAvailability;
    
    // ===== STATUS =====
    private JLabel lblStatusMessage;

    public CommitteeDashboardPanel(CommitteeMember committeeMember) {
        this.committeeMember = committeeMember;
        setLayout(new BorderLayout(15, 15));
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        setBackground(new Color(245, 248, 250));

        // ===== TOP: Header =====
        add(createHeaderPanel(), BorderLayout.NORTH);

        // ===== CENTER: Main Split =====
        JSplitPane mainSplit = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        mainSplit.setDividerLocation(500);
        mainSplit.setResizeWeight(0.45);

        // LEFT: Applications List + Stats
        mainSplit.setLeftComponent(createLeftPanel());

        // RIGHT: Application Details + AI Assessment
        mainSplit.setRightComponent(createRightPanel());

        add(mainSplit, BorderLayout.CENTER);

        // ===== BOTTOM: Status Bar =====
        add(createStatusBar(), BorderLayout.SOUTH);

        // Load initial data
        refreshDashboard();
    }

    // ===== HEADER PANEL =====
    private JPanel createHeaderPanel() {
        JPanel panel = new JPanel(new BorderLayout(15, 5));
        panel.setBackground(new Color(245, 248, 250));
        panel.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));

        // Left: Welcome
        JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        leftPanel.setBackground(new Color(245, 248, 250));
        
        lblCommitteeName = new JLabel("👥 Welcome, " + committeeMember.getFullName());
        lblCommitteeName.setFont(new Font("Arial", Font.BOLD, 22));
        lblCommitteeName.setForeground(new Color(0, 102, 204));
        leftPanel.add(lblCommitteeName);

        JLabel lblSubtitle = new JLabel("Funding Decision Command Center");
        lblSubtitle.setFont(new Font("Arial", Font.PLAIN, 14));
        lblSubtitle.setForeground(Color.GRAY);
        leftPanel.add(lblSubtitle);

        panel.add(leftPanel, BorderLayout.WEST);

        // Right: Stats
        JPanel rightPanel = new JPanel(new GridLayout(2, 3, 10, 5));
        rightPanel.setBackground(new Color(245, 248, 250));
        rightPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 10));

        // Initialize stats labels
        lblPendingReviews = new JLabel("0");
        lblAvgPRI = new JLabel("0.00");
        lblHighRiskProjects = new JLabel("0");
        lblRecommendedProjects = new JLabel("0");
        lblBudgetRemaining = new JLabel("0 QR");
        lblTotalApplications = new JLabel("0");

        rightPanel.add(createStatCard("⏳ Pending Reviews", lblPendingReviews, new Color(255, 200, 0)));
        rightPanel.add(createStatCard("📊 Avg PRI", lblAvgPRI, new Color(0, 102, 204)));
        rightPanel.add(createStatCard("⚠️ High Risk", lblHighRiskProjects, new Color(200, 0, 0)));
        rightPanel.add(createStatCard("✅ Recommended", lblRecommendedProjects, new Color(0, 153, 76)));
        rightPanel.add(createStatCard("💰 Budget Remaining", lblBudgetRemaining, new Color(0, 102, 204)));
        rightPanel.add(createStatCard("📋 Total Applications", lblTotalApplications, new Color(153, 0, 153)));

        panel.add(rightPanel, BorderLayout.EAST);

        return panel;
    }

    private JPanel createStatCard(String label, JLabel value, Color color) {
        JPanel panel = new JPanel(new BorderLayout(5, 2));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(color, 1),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        
        value.setFont(new Font("Arial", Font.BOLD, 16));
        value.setForeground(color);
        value.setHorizontalAlignment(SwingConstants.CENTER);
        panel.add(value, BorderLayout.CENTER);
        
        JLabel descLabel = new JLabel(label);
        descLabel.setFont(new Font("Arial", Font.PLAIN, 9));
        descLabel.setForeground(Color.GRAY);
        descLabel.setHorizontalAlignment(SwingConstants.CENTER);
        panel.add(descLabel, BorderLayout.SOUTH);
        
        return panel;
    }

    // ===== LEFT PANEL =====
    private JPanel createLeftPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 5));

        // Filter Panel
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        filterPanel.setBackground(Color.WHITE);
        filterPanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createEtchedBorder(),
            "Filter Applications",
            TitledBorder.LEFT,
            TitledBorder.TOP,
            new Font("Arial", Font.BOLD, 11)
        ));

        filterPanel.add(new JLabel("Status:"));
        cmbFilterStatus = new JComboBox<>(new String[]{
            "All", "PENDING", "UNDER_REVIEW", "APPROVED", "REJECTED", "FUNDED", "COMPLETED"
        });
        cmbFilterStatus.setFont(new Font("Arial", Font.PLAIN, 11));
        cmbFilterStatus.setPreferredSize(new Dimension(120, 25));
        cmbFilterStatus.addActionListener(this);
        cmbFilterStatus.setActionCommand("filter");
        filterPanel.add(cmbFilterStatus);

        btnRefresh = new JButton("🔄 Refresh");
        btnRefresh.setFont(new Font("Arial", Font.BOLD, 11));
        btnRefresh.setBackground(new Color(0, 102, 204));
        btnRefresh.setForeground(Color.WHITE);
        btnRefresh.setFocusPainted(false);
        btnRefresh.setPreferredSize(new Dimension(100, 25));
        btnRefresh.addActionListener(this);
        btnRefresh.setActionCommand("refresh");
        filterPanel.add(btnRefresh);

        panel.add(filterPanel, BorderLayout.NORTH);

        // Applications Table
        JPanel tablePanel = new JPanel(new BorderLayout(5, 5));
        tablePanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createEtchedBorder(),
            "📋 Applications",
            TitledBorder.LEFT,
            TitledBorder.TOP,
            new Font("Arial", Font.BOLD, 12)
        ));
        tablePanel.setBackground(Color.WHITE);

        String[] columns = {"ID", "Project", "Amount (QR)", "Status", "PRI"};
        appModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int col) { return false; }
        };
        
        appTable = new JTable(appModel);
        appTable.setFont(new Font("Arial", Font.PLAIN, 12));
        appTable.setRowHeight(28);
        appTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        appTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        appTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int row = appTable.getSelectedRow();
                if (row >= 0) {
                    int id = (int) appModel.getValueAt(row, 0);
                    loadApplicationDetails(id);
                }
            }
        });

        // Color-code rows
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
                } else if (column == 4) {
                    try {
                        double pri = Double.parseDouble(value.toString());
                        if (pri >= 80) {
                            c.setBackground(new Color(0, 200, 0));
                            c.setForeground(Color.WHITE);
                        } else if (pri >= 60) {
                            c.setBackground(new Color(255, 200, 0));
                            c.setForeground(Color.BLACK);
                        } else {
                            c.setBackground(new Color(200, 0, 0));
                            c.setForeground(Color.WHITE);
                        }
                    } catch (Exception ex) {
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

        JScrollPane scrollTable = new JScrollPane(appTable);
        scrollTable.setPreferredSize(new Dimension(450, 350));
        tablePanel.add(scrollTable, BorderLayout.CENTER);

        panel.add(tablePanel, BorderLayout.CENTER);

        return panel;
    }

    // ===== RIGHT PANEL =====
    private JPanel createRightPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 0));

        // Selected Application Info
        JPanel selectedPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        selectedPanel.setBackground(Color.WHITE);
        selectedPanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createEtchedBorder(),
            "📌 Selected Application",
            TitledBorder.LEFT,
            TitledBorder.TOP,
            new Font("Arial", Font.BOLD, 11)
        ));
        lblSelectedApp = new JLabel("No application selected");
        lblSelectedApp.setFont(new Font("Arial", Font.BOLD, 12));
        lblSelectedApp.setForeground(Color.GRAY);
        selectedPanel.add(lblSelectedApp);
        panel.add(selectedPanel, BorderLayout.NORTH);

        // Split: Details (Top) and AI Assessment (Bottom)
        JSplitPane detailSplit = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        detailSplit.setDividerLocation(300);
        detailSplit.setResizeWeight(0.5);

        // Application Details
        JPanel detailsPanel = new JPanel(new BorderLayout(5, 5));
        detailsPanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createEtchedBorder(),
            "📄 Application Details",
            TitledBorder.LEFT,
            TitledBorder.TOP,
            new Font("Arial", Font.BOLD, 12)
        ));
        detailsPanel.setBackground(Color.WHITE);

        txtApplicationDetails = new JTextArea();
        txtApplicationDetails.setEditable(false);
        txtApplicationDetails.setFont(new Font("Monospaced", Font.PLAIN, 12));
        txtApplicationDetails.setBackground(new Color(255, 255, 240));
        txtApplicationDetails.setText(
            "Select an application from the list to view details.\n\n" +
            "📋 Information will appear here including:\n" +
            "  • Project description\n" +
            "  • Funding amount requested\n" +
            "  • Beneficiary information\n" +
            "  • Assessment scores\n" +
            "  • PRI calculation\n" +
            "  • Risk assessment"
        );

        JScrollPane scrollDetails = new JScrollPane(txtApplicationDetails);
        scrollDetails.setPreferredSize(new Dimension(400, 250));
        detailsPanel.add(scrollDetails, BorderLayout.CENTER);

        // Action Buttons
        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        actionPanel.setBackground(Color.WHITE);

        btnApprove = new JButton("✅ Approve");
        btnApprove.setFont(new Font("Arial", Font.BOLD, 12));
        btnApprove.setBackground(new Color(0, 153, 76));
        btnApprove.setForeground(Color.WHITE);
        btnApprove.setFocusPainted(false);
        btnApprove.setPreferredSize(new Dimension(120, 35));
        btnApprove.addActionListener(this);
        btnApprove.setActionCommand("approve");
        actionPanel.add(btnApprove);

        btnReject = new JButton("❌ Reject");
        btnReject.setFont(new Font("Arial", Font.BOLD, 12));
        btnReject.setBackground(new Color(200, 0, 0));
        btnReject.setForeground(Color.WHITE);
        btnReject.setFocusPainted(false);
        btnReject.setPreferredSize(new Dimension(120, 35));
        btnReject.addActionListener(this);
        btnReject.setActionCommand("reject");
        actionPanel.add(btnReject);

        btnRequestRevision = new JButton("🔄 Request Revision");
        btnRequestRevision.setFont(new Font("Arial", Font.BOLD, 12));
        btnRequestRevision.setBackground(new Color(255, 153, 0));
        btnRequestRevision.setForeground(Color.WHITE);
        btnRequestRevision.setFocusPainted(false);
        btnRequestRevision.setPreferredSize(new Dimension(150, 35));
        btnRequestRevision.addActionListener(this);
        btnRequestRevision.setActionCommand("requestRevision");
        actionPanel.add(btnRequestRevision);

        btnViewDetails = new JButton("📊 Full Details");
        btnViewDetails.setFont(new Font("Arial", Font.PLAIN, 11));
        btnViewDetails.setBackground(new Color(0, 102, 204));
        btnViewDetails.setForeground(Color.WHITE);
        btnViewDetails.setFocusPainted(false);
        btnViewDetails.setPreferredSize(new Dimension(120, 35));
        btnViewDetails.addActionListener(this);
        btnViewDetails.setActionCommand("fullDetails");
        actionPanel.add(btnViewDetails);

        detailsPanel.add(actionPanel, BorderLayout.SOUTH);

        detailSplit.setTopComponent(detailsPanel);

        // AI Assessment Panel
        JPanel aiPanel = new JPanel(new BorderLayout(5, 5));
        aiPanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(0, 102, 204), 2),
            "🤖 AI Assessment & Recommendation",
            TitledBorder.LEFT,
            TitledBorder.TOP,
            new Font("Arial", Font.BOLD, 12),
            new Color(0, 102, 204)
        ));
        aiPanel.setBackground(Color.WHITE);

        txtAIAssessment = new JTextArea();
        txtAIAssessment.setEditable(false);
        txtAIAssessment.setFont(new Font("Monospaced", Font.PLAIN, 12));
        txtAIAssessment.setBackground(new Color(255, 255, 240));
        txtAIAssessment.setText(
            "🤖 AI Assessment will appear here.\n\n" +
            "Select an application to see:\n" +
            "  • PRI Score & Analysis\n" +
            "  • Success Probability\n" +
            "  • Similar Projects Comparison\n" +
            "  • Expected Repayment Rate\n" +
            "  • Risk Assessment\n" +
            "  • AI Recommendation"
        );

        JScrollPane scrollAI = new JScrollPane(txtAIAssessment);
        scrollAI.setPreferredSize(new Dimension(400, 200));
        aiPanel.add(scrollAI, BorderLayout.CENTER);

        // AI Status
        JPanel aiStatusPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        aiStatusPanel.setBackground(Color.WHITE);
        lblAIAvailability = new JLabel(AIService.isAIAvailable() ? "✅ AI Active" : "⚠️ AI Disabled");
        lblAIAvailability.setFont(new Font("Arial", Font.PLAIN, 10));
        lblAIAvailability.setForeground(AIService.isAIAvailable() ? new Color(0, 153, 76) : Color.RED);
        aiStatusPanel.add(lblAIAvailability);
        aiPanel.add(aiStatusPanel, BorderLayout.SOUTH);

        detailSplit.setBottomComponent(aiPanel);

        panel.add(detailSplit, BorderLayout.CENTER);

        return panel;
    }

    // ===== STATUS BAR =====
    private JPanel createStatusBar() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panel.setBorder(BorderFactory.createEtchedBorder());
        panel.setBackground(new Color(240, 240, 240));
        
        lblStatusMessage = new JLabel("✅ Ready - Select an application to review");
        lblStatusMessage.setFont(new Font("Arial", Font.PLAIN, 11));
        panel.add(lblStatusMessage);

        JLabel timeLabel = new JLabel(LocalDate.now().format(DateTimeFormatter.ofPattern("dd MMMM yyyy")));
        timeLabel.setFont(new Font("Arial", Font.PLAIN, 10));
        timeLabel.setForeground(Color.GRAY);
        panel.add(Box.createHorizontalGlue());
        panel.add(timeLabel);

        return panel;
    }

    // ===== LOAD APPLICATION DETAILS =====
    private void loadApplicationDetails(int applicationId) {
        selectedApplication = FundingService.searchApplication(applicationId);
        
        if (selectedApplication == null) {
            setStatus("❌ Application not found: " + applicationId);
            return;
        }

        // Get assessment
        selectedAssessment = AssessmentService.searchAssessmentByApplication(applicationId);
        
        // Get Namaa Index
        if (selectedApplication.getProject() != null) {
            selectedIndex = NamaaIndexService.getIndexByProject(
                selectedApplication.getProject().getProjectID()
            );
        }

        updateApplicationDetails();
        updateAIAssessment();
        
        lblSelectedApp.setText("ID: " + applicationId + " | " + selectedApplication.getProject().getProjectName());
        lblSelectedApp.setForeground(new Color(0, 102, 204));
        
        setStatus("📋 Loaded Application #" + applicationId);
    }

    // ===== UPDATE APPLICATION DETAILS =====
    private void updateApplicationDetails() {
        if (selectedApplication == null) return;

        StringBuilder details = new StringBuilder();
        details.append("╔═══════════════════════════════════════════════════════════════╗\n");
        details.append("║              APPLICATION DETAILS                             ║\n");
        details.append("╠═══════════════════════════════════════════════════════════════╣\n");
        details.append("║  Application ID:  " + String.format("%-42d", selectedApplication.getApplicationID()) + "║\n");
        details.append("║  Project Name:    " + String.format("%-42s", selectedApplication.getProject().getProjectName()) + "║\n");
        details.append("║  Sector:          " + String.format("%-42s", selectedApplication.getProject().getSector()) + "║\n");
        details.append("║  Amount:          " + String.format("%-42s", String.format("%,.2f QR", selectedApplication.getRequestedAmount())) + "║\n");
        details.append("║  Status:          " + String.format("%-42s", selectedApplication.getStatus()) + "║\n");
        details.append("║  Date:            " + String.format("%-42s", selectedApplication.getApplicationDate().toLocalDate()) + "║\n");
        
        if (selectedApplication.getBeneficiary() != null) {
            details.append("╠═══════════════════════════════════════════════════════════════╣\n");
            details.append("║              BENEFICIARY INFORMATION                         ║\n");
            details.append("╠═══════════════════════════════════════════════════════════════╣\n");
            details.append("║  Name:            " + String.format("%-42s", selectedApplication.getBeneficiary().getFullName()) + "║\n");
            details.append("║  Education:       " + String.format("%-42s", selectedApplication.getBeneficiary().getEducation()) + "║\n");
            details.append("║  Experience:      " + String.format("%-42s", selectedApplication.getBeneficiary().getExperienceYears() + " years") + "║\n");
        }

        if (selectedAssessment != null) {
            details.append("╠═══════════════════════════════════════════════════════════════╣\n");
            details.append("║              ASSESSMENT SCORES                               ║\n");
            details.append("╠═══════════════════════════════════════════════════════════════╣\n");
            details.append("║  Economic:        " + String.format("%-42.2f", selectedAssessment.getEconomicScore()) + "║\n");
            details.append("║  Technical:       " + String.format("%-42.2f", selectedAssessment.getTechnicalScore()) + "║\n");
            details.append("║  Social:          " + String.format("%-42.2f", selectedAssessment.getSocialScore()) + "║\n");
            details.append("║  Environmental:   " + String.format("%-42.2f", selectedAssessment.getEnvironmentalScore()) + "║\n");
            details.append("║  Innovation:      " + String.format("%-42.2f", selectedAssessment.getInnovationScore()) + "║\n");
            details.append("╠═══════════════════════════════════════════════════════════════╣\n");
            details.append("║  ★ PRI SCORE:     " + String.format("%-42.2f", selectedAssessment.getPriScore()) + "║\n");
            details.append("║  ★ Recommendation:" + String.format("%-42s", selectedAssessment.getRecommendation()) + "║\n");
            details.append("║  Assessment Date: " + String.format("%-42s", selectedAssessment.getAssessmentDate()) + "║\n");
        }

        if (selectedIndex != null) {
            details.append("╠═══════════════════════════════════════════════════════════════╣\n");
            details.append("║              NAMAA INDEX                                    ║\n");
            details.append("╠═══════════════════════════════════════════════════════════════╣\n");
            details.append("║  Economic Impact: " + String.format("%-42.2f", selectedIndex.getEconomicImpact()) + "║\n");
            details.append("║  Social Impact:   " + String.format("%-42.2f", selectedIndex.getSocialImpact()) + "║\n");
            details.append("║  Sustainability:  " + String.format("%-42.2f", selectedIndex.getSustainability()) + "║\n");
            details.append("║  Innovation:      " + String.format("%-42.2f", selectedIndex.getInnovation()) + "║\n");
            details.append("╠═══════════════════════════════════════════════════════════════╣\n");
            details.append("║  ★ FINAL INDEX:   " + String.format("%-42.2f", selectedIndex.getFinalIndex()) + "║\n");
        }

        details.append("╚═══════════════════════════════════════════════════════════════╝\n");

        txtApplicationDetails.setText(details.toString());
    }

    // ===== UPDATE AI ASSESSMENT =====
    private void updateAIAssessment() {
        if (selectedApplication == null) {
            txtAIAssessment.setText("Select an application to see AI assessment.");
            return;
        }

        StringBuilder assessment = new StringBuilder();
        assessment.append("╔═══════════════════════════════════════════════════════════════╗\n");
        assessment.append("║              AI ASSESSMENT & RECOMMENDATION                  ║\n");
        assessment.append("╠═══════════════════════════════════════════════════════════════╣\n");

        // PRI Analysis
        if (selectedAssessment != null) {
            double pri = selectedAssessment.getPriScore();
            assessment.append("║  📊 PRI SCORE:       " + String.format("%-42.2f", pri) + "║\n");
            
            String priGrade;
            String priColor;
            if (pri >= 80) {
                priGrade = "EXCELLENT";
                priColor = "✅";
            } else if (pri >= 60) {
                priGrade = "GOOD";
                priColor = "⚠️";
            } else {
                priGrade = "NEEDS IMPROVEMENT";
                priColor = "❌";
            }
            assessment.append("║  ★ Grade:           " + String.format("%-42s", priColor + " " + priGrade) + "║\n");
            
            // Success probability based on historical data
            double successProb = calculateSuccessProbability(selectedApplication);
            assessment.append("║  📈 Success Prob:    " + String.format("%-42.1f%%", successProb * 100) + "║\n");
            
            // Similar projects comparison
            int similarProjects = countSimilarProjects(selectedApplication);
            assessment.append("║  📋 Similar Projects:" + String.format("%-42d", similarProjects) + "║\n");
            
            // Average repayment rate for similar projects
            double avgRepayment = getAverageRepaymentForSector(selectedApplication.getProject().getSector());
            assessment.append("║  💰 Avg Repayment:   " + String.format("%-42.1f%%", avgRepayment * 100) + "║\n");
            
            // Risk assessment
            String riskLevel = assessRisk(selectedApplication, selectedAssessment);
            assessment.append("║  ⚠️ Risk Level:      " + String.format("%-42s", riskLevel) + "║\n");
        } else {
            assessment.append("║  ⚠️ No assessment found for this application.              ║\n");
            assessment.append("║  Please complete the assessment first.                     ║\n");
        }

        // AI Recommendation
        assessment.append("╠═══════════════════════════════════════════════════════════════╣\n");
        assessment.append("║              🤖 AI RECOMMENDATION                             ║\n");
        assessment.append("╠═══════════════════════════════════════════════════════════════╣\n");

        String recommendation = generateAIRecommendation(selectedApplication, selectedAssessment);
        assessment.append(recommendation);

        assessment.append("╚═══════════════════════════════════════════════════════════════╝\n");

        txtAIAssessment.setText(assessment.toString());
    }

    // ===== CALCULATE SUCCESS PROBABILITY =====
    private double calculateSuccessProbability(FundingApplication app) {
        if (app == null) return 0.5;
        
        double baseRate = 0.7; // Default
        
        // Adjust based on PRI if available
        if (selectedAssessment != null) {
            double pri = selectedAssessment.getPriScore();
            if (pri >= 80) baseRate += 0.15;
            else if (pri >= 60) baseRate += 0.05;
            else baseRate -= 0.15;
        }
        
        // Adjust based on sector performance
        String sector = app.getProject().getSector();
        double sectorRate = HistoricalDataService.predictSuccessRate(sector, app.getRequestedAmount());
        baseRate = (baseRate + sectorRate) / 2;
        
        // Adjust based on beneficiary experience
        if (app.getBeneficiary() != null) {
            int experience = app.getBeneficiary().getExperienceYears();
            if (experience >= 5) baseRate += 0.1;
            else if (experience >= 3) baseRate += 0.05;
            else baseRate -= 0.05;
        }
        
        return Math.max(0, Math.min(1, baseRate));
    }

    // ===== COUNT SIMILAR PROJECTS =====
    private int countSimilarProjects(FundingApplication app) {
        int count = 0;
        String sector = app.getProject().getSector();
        double amount = app.getRequestedAmount();
        
        for (HistoricalProject p : HistoricalDataService.getAllProjects()) {
            if (p.getCategory().equalsIgnoreCase(sector)) {
                // Check if similar budget range (+/- 30%)
                if (p.getActualCost() >= amount * 0.7 && p.getActualCost() <= amount * 1.3) {
                    count++;
                }
            }
        }
        return count;
    }

    // ===== GET AVERAGE REPAYMENT FOR SECTOR =====
    private double getAverageRepaymentForSector(String sector) {
        double totalRepayment = 0;
        int count = 0;
        
        // Get all loans in this sector
        for (QardHasan loan : LoanService.getLoans()) {
            if (loan.getApplication() != null && 
                loan.getApplication().getProject() != null &&
                loan.getApplication().getProject().getSector().equalsIgnoreCase(sector)) {
                double repaid = LoanService.getTotalRepaidByLoan(loan.getLoanID());
                double rate = loan.getLoanAmount() > 0 ? repaid / loan.getLoanAmount() : 0;
                totalRepayment += rate;
                count++;
            }
        }
        
        return count > 0 ? totalRepayment / count : 0.85; // Default 85%
    }

    // ===== ASSESS RISK =====
    private String assessRisk(FundingApplication app, ProjectAssessment assessment) {
        int riskScore = 0;
        
        // PRI based risk
        if (assessment != null) {
            if (assessment.getPriScore() < 50) riskScore += 3;
            else if (assessment.getPriScore() < 70) riskScore += 1;
        }
        
        // Amount based risk
        if (app.getRequestedAmount() > 200000) riskScore += 2;
        else if (app.getRequestedAmount() > 100000) riskScore += 1;
        
        // Experience based risk
        if (app.getBeneficiary() != null) {
            if (app.getBeneficiary().getExperienceYears() < 2) riskScore += 2;
            else if (app.getBeneficiary().getExperienceYears() < 4) riskScore += 1;
        }
        
        // Sector based risk (from historical data)
        String sector = app.getProject().getSector();
        double sectorSuccess = HistoricalDataService.predictSuccessRate(sector, app.getRequestedAmount());
        if (sectorSuccess < 0.6) riskScore += 2;
        else if (sectorSuccess < 0.75) riskScore += 1;
        
        if (riskScore >= 6) return "🔴 HIGH RISK";
        if (riskScore >= 4) return "🟡 MEDIUM RISK";
        return "🟢 LOW RISK";
    }

    // ===== GENERATE AI RECOMMENDATION =====
    private String generateAIRecommendation(FundingApplication app, ProjectAssessment assessment) {
        if (app == null || assessment == null) {
            return "║  ⚠️ Insufficient data for recommendation.                      ║\n" +
                   "║  Please complete the assessment first.                         ║\n";
        }

        double pri = assessment.getPriScore();
        double successProb = calculateSuccessProbability(app);
        double avgRepayment = getAverageRepaymentForSector(app.getProject().getSector());
        String risk = assessRisk(app, assessment);

        StringBuilder rec = new StringBuilder();
        
        // Decision based on multiple factors
        if (pri >= 70 && successProb >= 0.75 && avgRepayment >= 0.80) {
            rec.append("║  ✅ RECOMMENDATION: APPROVE                                     ║\n");
            rec.append("║  ─────────────────────────────────────────────────────────────  ║\n");
            rec.append("║  Strong application with excellent metrics:                    ║\n");
            rec.append("║  • High PRI score (" + String.format("%.1f", pri) + ")                                        ║\n");
            rec.append("║  • Strong success probability (" + String.format("%.0f", successProb * 100) + "%)                      ║\n");
            rec.append("║  • Good repayment track record (" + String.format("%.0f", avgRepayment * 100) + "%)                 ║\n");
            rec.append("║  • Risk level: " + risk + "                                     ║\n");
        } else if (pri >= 55 && successProb >= 0.55 && avgRepayment >= 0.70) {
            rec.append("║  ⚠️ RECOMMENDATION: APPROVE WITH CONDITIONS                     ║\n");
            rec.append("║  ─────────────────────────────────────────────────────────────  ║\n");
            rec.append("║  Application shows promise but needs monitoring:               ║\n");
            rec.append("║  • Moderate PRI score (" + String.format("%.1f", pri) + ")                                      ║\n");
            rec.append("║  • Success probability (" + String.format("%.0f", successProb * 100) + "%)                      ║\n");
            rec.append("║  • Consider additional support/mentoring                      ║\n");
            rec.append("║  • Risk level: " + risk + "                                     ║\n");
        } else {
            rec.append("║  ❌ RECOMMENDATION: REJECT OR REQUEST REVISION                  ║\n");
            rec.append("║  ─────────────────────────────────────────────────────────────  ║\n");
            rec.append("║  Application needs significant improvement:                   ║\n");
            rec.append("║  • Low PRI score (" + String.format("%.1f", pri) + ")                                        ║\n");
            rec.append("║  • Low success probability (" + String.format("%.0f", successProb * 100) + "%)                 ║\n");
            rec.append("║  • Risk level: " + risk + "                                     ║\n");
            rec.append("║  • Recommend: Address weaknesses and resubmit                 ║\n");
        }

        // Add evidence/justification
        rec.append("║  ─────────────────────────────────────────────────────────────  ║\n");
        rec.append("║  📊 EVIDENCE SUMMARY:                                          ║\n");
        rec.append("║  • Similar projects: " + String.format("%-30d", countSimilarProjects(app)) + "║\n");
        rec.append("║  • Sector average: " + String.format("%-30.1f%%", getSectorAverageSuccess(app.getProject().getSector()) * 100) + "║\n");
        rec.append("║  • Beneficiary experience: " + String.format("%-25d", app.getBeneficiary().getExperienceYears()) + " years║\n");

        return rec.toString();
    }

    private double getSectorAverageSuccess(String sector) {
        return HistoricalDataService.predictSuccessRate(sector, 100000);
    }

    // ===== REFRESH DASHBOARD =====
    private void refreshDashboard() {
        updateStats();
        updateTable();
        setStatus("🔄 Dashboard refreshed at " + LocalDate.now());
    }

    // ===== UPDATE STATS =====
    private void updateStats() {
        java.util.ArrayList<FundingApplication> allApps = FundingService.getApplications();
        int total = allApps.size();
        int pending = 0;
        int highRisk = 0;
        int recommended = 0;
        double totalPRI = 0;
        int priCount = 0;
        double totalBudget = 0;
        
        for (FundingApplication app : allApps) {
            ApplicationStatus status = app.getStatus();
            if (status == ApplicationStatus.PENDING || status == ApplicationStatus.UNDER_REVIEW) {
                pending++;
            }
            
            totalBudget += app.getRequestedAmount();
            
            // Get assessment for PRI
            ProjectAssessment assessment = AssessmentService.searchAssessmentByApplication(app.getApplicationID());
            if (assessment != null) {
                totalPRI += assessment.getPriScore();
                priCount++;
                
                // Check if high risk
                String risk = assessRisk(app, assessment);
                if (risk.contains("HIGH")) highRisk++;
                
                // Check if recommended
                if (assessment.getPriScore() >= 70) recommended++;
            }
        }

        // Calculate budget remaining (assuming total Waqf balance)
        double totalWaqfBalance = WaqfService.getTotalWaqfBalance();
        double budgetRemaining = totalWaqfBalance - totalBudget;

        // Update labels
        lblPendingReviews.setText(String.valueOf(pending));
        lblAvgPRI.setText(String.format("%.2f", priCount > 0 ? totalPRI / priCount : 0));
        lblHighRiskProjects.setText(String.valueOf(highRisk));
        lblRecommendedProjects.setText(String.valueOf(recommended));
        lblBudgetRemaining.setText(String.format("%.2f QR", Math.max(0, budgetRemaining)));
        lblTotalApplications.setText(String.valueOf(total));
    }

    // ===== GET STATUS EMOJI (FIXED - Takes ApplicationStatus enum) =====
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

    // ===== GET STATUS DISPLAY NAME =====
    private String getStatusDisplayName(ApplicationStatus status) {
        if (status == null) return "UNKNOWN";
        return status.name();
    }

    // ===== UPDATE TABLE (FIXED - Uses enum properly) =====
    private void updateTable() {
        appModel.setRowCount(0);
        
        String filter = (String) cmbFilterStatus.getSelectedItem();
        
        for (FundingApplication app : FundingService.getApplications()) {
            ApplicationStatus status = app.getStatus();
            String statusName = status != null ? status.name() : "";
            
            // Apply filter
            if (!filter.equals("All") && !statusName.equals(filter)) {
                continue;
            }
            
            // Get PRI
            double pri = 0;
            ProjectAssessment assessment = AssessmentService.searchAssessmentByApplication(app.getApplicationID());
            if (assessment != null) {
                pri = assessment.getPriScore();
            }
            
            String statusEmoji = getStatusEmoji(status);
            String statusDisplay = statusEmoji + " " + getStatusDisplayName(status);
            
            appModel.addRow(new Object[]{
                app.getApplicationID(),
                app.getProject().getProjectName(),
                String.format("%.2f", app.getRequestedAmount()),
                statusDisplay,
                String.format("%.2f", pri)
            });
        }
    }

    // ===== ACTION HANDLING =====
    @Override
    public void actionPerformed(ActionEvent e) {
        String cmd = e.getActionCommand();

        switch (cmd) {
            case "filter":
                updateTable();
                setStatus("🔍 Filtered: " + cmbFilterStatus.getSelectedItem());
                break;
            case "refresh":
                refreshDashboard();
                JOptionPane.showMessageDialog(this,
                    "✅ Dashboard refreshed successfully!",
                    "Refreshed",
                    JOptionPane.INFORMATION_MESSAGE);
                break;
            case "approve":
                approveApplication();
                break;
            case "reject":
                rejectApplication();
                break;
            case "requestRevision":
                requestRevision();
                break;
            case "fullDetails":
                showFullDetails();
                break;
            default:
                break;
        }
    }

    // ===== APPROVE APPLICATION =====
    private void approveApplication() {
        if (selectedApplication == null) {
            JOptionPane.showMessageDialog(this,
                "Please select an application first.",
                "No Selection",
                JOptionPane.WARNING_MESSAGE);
            return;
        }

        ApplicationStatus currentStatus = selectedApplication.getStatus();
        if (currentStatus != ApplicationStatus.PENDING && 
            currentStatus != ApplicationStatus.UNDER_REVIEW) {
            JOptionPane.showMessageDialog(this,
                "This application cannot be approved.\n" +
                "Current status: " + currentStatus,
                "Cannot Approve",
                JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Get approval notes (optional)
        String notes = JOptionPane.showInputDialog(this,
            "Optional: Add approval notes for the beneficiary:",
            "Approval Notes",
            JOptionPane.QUESTION_MESSAGE);

        int confirm = JOptionPane.showConfirmDialog(this,
            "Approve Application #" + selectedApplication.getApplicationID() + "?\n\n" +
            "Project: " + selectedApplication.getProject().getProjectName() + "\n" +
            "Amount: " + String.format("%.2f", selectedApplication.getRequestedAmount()) + " QR\n" +
            "PRI Score: " + (selectedAssessment != null ? 
                String.format("%.2f", selectedAssessment.getPriScore()) : "N/A") + "\n\n" +
            (notes != null && !notes.isEmpty() ? "Notes: " + notes + "\n\n" : "") +
            "This will mark the application as APPROVED\n" +
            "and allow loan processing.",
            "Approve Application",
            JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            selectedApplication.setStatus(ApplicationStatus.APPROVED);
            FundingService.updateApplication(selectedApplication);
            
            JOptionPane.showMessageDialog(this,
                "✅ Application #" + selectedApplication.getApplicationID() + " approved!\n\n" +
                "The beneficiary can now proceed with loan processing.\n" +
                (notes != null && !notes.isEmpty() ? "\nNotes: " + notes : ""),
                "Approved",
                JOptionPane.INFORMATION_MESSAGE);
            
            refreshDashboard();
            loadApplicationDetails(selectedApplication.getApplicationID());
            setStatus("✅ Application #" + selectedApplication.getApplicationID() + " approved");
        }
    }

    // ===== REJECT APPLICATION =====
    private void rejectApplication() {
        if (selectedApplication == null) {
            JOptionPane.showMessageDialog(this,
                "Please select an application first.",
                "No Selection",
                JOptionPane.WARNING_MESSAGE);
            return;
        }

        ApplicationStatus currentStatus = selectedApplication.getStatus();
        if (currentStatus != ApplicationStatus.PENDING && 
            currentStatus != ApplicationStatus.UNDER_REVIEW) {
            JOptionPane.showMessageDialog(this,
                "This application cannot be rejected.\n" +
                "Current status: " + currentStatus,
                "Cannot Reject",
                JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Get rejection reason
        String reason = JOptionPane.showInputDialog(this,
            "Please provide a reason for rejection:",
            "Rejection Reason",
            JOptionPane.QUESTION_MESSAGE);

        if (reason == null) return; // Cancelled

        int confirm = JOptionPane.showConfirmDialog(this,
            "Reject Application #" + selectedApplication.getApplicationID() + "?\n\n" +
            "Project: " + selectedApplication.getProject().getProjectName() + "\n" +
            "Reason: " + reason + "\n\n" +
            "This will mark the application as REJECTED.",
            "Reject Application",
            JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            selectedApplication.setStatus(ApplicationStatus.REJECTED);
            FundingService.updateApplication(selectedApplication);
            
            JOptionPane.showMessageDialog(this,
                "❌ Application #" + selectedApplication.getApplicationID() + " rejected.\n\n" +
                "Reason: " + reason,
                "Rejected",
                JOptionPane.INFORMATION_MESSAGE);
            
            refreshDashboard();
            loadApplicationDetails(selectedApplication.getApplicationID());
            setStatus("❌ Application #" + selectedApplication.getApplicationID() + " rejected");
        }
    }

    // ===== REQUEST REVISION =====
    private void requestRevision() {
        if (selectedApplication == null) {
            JOptionPane.showMessageDialog(this,
                "Please select an application first.",
                "No Selection",
                JOptionPane.WARNING_MESSAGE);
            return;
        }

        ApplicationStatus currentStatus = selectedApplication.getStatus();
        if (currentStatus != ApplicationStatus.PENDING && 
            currentStatus != ApplicationStatus.UNDER_REVIEW) {
            JOptionPane.showMessageDialog(this,
                "This application cannot be sent for revision.\n" +
                "Current status: " + currentStatus,
                "Cannot Request Revision",
                JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Get revision notes
        String notes = JOptionPane.showInputDialog(this,
            "Please provide revision notes for the beneficiary:",
            "Revision Request",
            JOptionPane.QUESTION_MESSAGE);

        if (notes == null) return; // Cancelled

        int confirm = JOptionPane.showConfirmDialog(this,
            "Request Revision for Application #" + selectedApplication.getApplicationID() + "?\n\n" +
            "Project: " + selectedApplication.getProject().getProjectName() + "\n" +
            "Notes: " + notes + "\n\n" +
            "The beneficiary will be notified to revise and resubmit.",
            "Request Revision",
            JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            // Set status back to PENDING for revision
            selectedApplication.setStatus(ApplicationStatus.PENDING);
            FundingService.updateApplication(selectedApplication);
            
            JOptionPane.showMessageDialog(this,
                "📝 Revision requested for Application #" + selectedApplication.getApplicationID() + "\n\n" +
                "Notes sent to beneficiary: " + notes,
                "Revision Requested",
                JOptionPane.INFORMATION_MESSAGE);
            
            refreshDashboard();
            loadApplicationDetails(selectedApplication.getApplicationID());
            setStatus("📝 Revision requested for Application #" + selectedApplication.getApplicationID());
        }
    }

    // ===== SHOW FULL DETAILS =====
    private void showFullDetails() {
        if (selectedApplication == null) {
            JOptionPane.showMessageDialog(this,
                "Please select an application first.",
                "No Selection",
                JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Create full details dialog
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), 
            "Full Application Details - #" + selectedApplication.getApplicationID(), true);
        dialog.setLayout(new BorderLayout(10, 10));
        dialog.setSize(800, 600);
        dialog.setLocationRelativeTo(this);

        JTextArea fullDetails = new JTextArea();
        fullDetails.setEditable(false);
        fullDetails.setFont(new Font("Monospaced", Font.PLAIN, 12));
        fullDetails.setBackground(new Color(255, 255, 240));

        StringBuilder details = new StringBuilder();
        details.append("╔═══════════════════════════════════════════════════════════════════════════════╗\n");
        details.append("║                         FULL APPLICATION DETAILS                             ║\n");
        details.append("╚═══════════════════════════════════════════════════════════════════════════════╝\n\n");

        // Project Information
        details.append("📋 PROJECT INFORMATION\n");
        details.append("─────────────────────────────────────────────────────────────────────────────────\n");
        details.append("  Application ID:  ").append(selectedApplication.getApplicationID()).append("\n");
        details.append("  Project Name:    ").append(selectedApplication.getProject().getProjectName()).append("\n");
        details.append("  Sector:          ").append(selectedApplication.getProject().getSector()).append("\n");
        details.append("  Description:     ").append(selectedApplication.getProject().getDescription()).append("\n");
        details.append("  Location:        ").append(selectedApplication.getProject().getLocation()).append("\n");
        details.append("  Requested Amount:").append(String.format("%,.2f QR", selectedApplication.getRequestedAmount())).append("\n");
        details.append("  Duration:        ").append(selectedApplication.getProject().getDurationMonths()).append(" months\n");
        details.append("  Status:          ").append(selectedApplication.getStatus()).append("\n");
        details.append("  Application Date:").append(selectedApplication.getApplicationDate()).append("\n\n");

        // Beneficiary Information
        if (selectedApplication.getBeneficiary() != null) {
            details.append("👤 BENEFICIARY INFORMATION\n");
            details.append("─────────────────────────────────────────────────────────────────────────────────\n");
            details.append("  Name:            ").append(selectedApplication.getBeneficiary().getFullName()).append("\n");
            details.append("  Email:           ").append(selectedApplication.getBeneficiary().getEmail()).append("\n");
            details.append("  Phone:           ").append(selectedApplication.getBeneficiary().getPhoneNumber()).append("\n");
            details.append("  Education:       ").append(selectedApplication.getBeneficiary().getEducation()).append("\n");
            details.append("  Experience:      ").append(selectedApplication.getBeneficiary().getExperienceYears()).append(" years\n\n");
        }

        // Assessment Scores
        if (selectedAssessment != null) {
            details.append("📊 ASSESSMENT SCORES\n");
            details.append("─────────────────────────────────────────────────────────────────────────────────\n");
            details.append("  Economic:        ").append(String.format("%.2f", selectedAssessment.getEconomicScore())).append("\n");
            details.append("  Technical:       ").append(String.format("%.2f", selectedAssessment.getTechnicalScore())).append("\n");
            details.append("  Social:          ").append(String.format("%.2f", selectedAssessment.getSocialScore())).append("\n");
            details.append("  Environmental:   ").append(String.format("%.2f", selectedAssessment.getEnvironmentalScore())).append("\n");
            details.append("  Innovation:      ").append(String.format("%.2f", selectedAssessment.getInnovationScore())).append("\n");
            details.append("  ─────────────────────────────────────────────────────────────────────────────\n");
            details.append("  ★ PRI SCORE:     ").append(String.format("%.2f", selectedAssessment.getPriScore())).append("\n");
            details.append("  ★ Recommendation:").append(selectedAssessment.getRecommendation()).append("\n");
            details.append("  Assessment Date: ").append(selectedAssessment.getAssessmentDate()).append("\n\n");
        }

        // Namaa Index
        if (selectedIndex != null) {
            details.append("📈 NAMAA INDEX\n");
            details.append("─────────────────────────────────────────────────────────────────────────────────\n");
            details.append("  Economic Impact: ").append(String.format("%.2f", selectedIndex.getEconomicImpact())).append("\n");
            details.append("  Social Impact:   ").append(String.format("%.2f", selectedIndex.getSocialImpact())).append("\n");
            details.append("  Sustainability:  ").append(String.format("%.2f", selectedIndex.getSustainability())).append("\n");
            details.append("  Innovation:      ").append(String.format("%.2f", selectedIndex.getInnovation())).append("\n");
            details.append("  ─────────────────────────────────────────────────────────────────────────────\n");
            details.append("  ★ FINAL INDEX:   ").append(String.format("%.2f", selectedIndex.getFinalIndex())).append("\n\n");
        }

        // AI Analysis
        details.append("🤖 AI ANALYSIS\n");
        details.append("─────────────────────────────────────────────────────────────────────────────────\n");
        details.append("  Success Probability: ").append(String.format("%.1f%%", calculateSuccessProbability(selectedApplication) * 100)).append("\n");
        details.append("  Similar Projects:   ").append(countSimilarProjects(selectedApplication)).append("\n");
        details.append("  Avg Repayment Rate: ").append(String.format("%.1f%%", getAverageRepaymentForSector(selectedApplication.getProject().getSector()) * 100)).append("\n");
        details.append("  Risk Level:         ").append(assessRisk(selectedApplication, selectedAssessment)).append("\n\n");

        details.append(txtAIAssessment.getText());

        fullDetails.setText(details.toString());
        JScrollPane scroll = new JScrollPane(fullDetails);
        scroll.setPreferredSize(new Dimension(750, 550));
        dialog.add(scroll, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel();
        JButton btnClose = new JButton("Close");
        btnClose.addActionListener(e -> dialog.dispose());
        buttonPanel.add(btnClose);
        dialog.add(buttonPanel, BorderLayout.SOUTH);

        dialog.setVisible(true);
    }

    // ===== SET STATUS =====
    private void setStatus(String message) {
        if (lblStatusMessage != null) {
            lblStatusMessage.setText("📌 " + message);
        }
    }

    // ===== GET COMMITTEE MEMBER =====
    public CommitteeMember getCommitteeMember() {
        return committeeMember;
    }
}