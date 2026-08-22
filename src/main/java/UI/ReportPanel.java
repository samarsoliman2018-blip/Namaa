package UI;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashSet;
import Model.*;
import Service.*;

public class ReportPanel extends JPanel implements ActionListener {
    private JTextField txtReportID, txtProjectID;
    private JTable table;
    private DefaultTableModel tableModel;
    private JButton btnGenerate, btnClear, btnViewAll, btnLoadProject;
    private ImpactReport currentReport;
    private JLabel lblStatusMessage, lblProjectName;
    private JComboBox<String> cmbProject;
    private JTextArea txtFullReport;

    public ReportPanel() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Title
        JLabel lblTitle = new JLabel("Impact Reports", JLabel.CENTER);
        lblTitle.setFont(new Font("Arial", Font.BOLD, 20));
        lblTitle.setForeground(new Color(0, 102, 204));
        add(lblTitle, BorderLayout.NORTH);

        // Split Panel
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setDividerLocation(500);
        splitPane.setResizeWeight(0.45);

        // ===== LEFT PANEL =====
        JPanel leftPanel = new JPanel(new BorderLayout(5, 5));

        // Form Panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createEtchedBorder(),
            "Report Generation",
            TitledBorder.LEFT,
            TitledBorder.TOP,
            new Font("Arial", Font.BOLD, 12)
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 4, 4, 4);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Row 1: Report ID (Auto)
        txtReportID = new JTextField(10);
        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0.3;
        JLabel lblReportID = new JLabel("Report ID:");
        lblReportID.setFont(new Font("Arial", Font.BOLD, 12));
        formPanel.add(lblReportID, gbc);

        gbc.gridx = 1; gbc.weightx = 0.7;
        txtReportID.setEditable(false);
        txtReportID.setBackground(new Color(240, 240, 240));
        txtReportID.setPreferredSize(new Dimension(120, 28));
        txtReportID.setText(String.valueOf(getNextReportId()));
        formPanel.add(txtReportID, gbc);

        // Row 2: Select Project
        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0.3;
        JLabel lblProjectSelect = new JLabel("Select Project:");
        lblProjectSelect.setFont(new Font("Arial", Font.BOLD, 12));
        formPanel.add(lblProjectSelect, gbc);

        gbc.gridx = 1; gbc.weightx = 0.7;
        JPanel projectPanel = new JPanel(new BorderLayout(5, 0));
        cmbProject = new JComboBox<>();
        cmbProject.setFont(new Font("Arial", Font.PLAIN, 12));
        cmbProject.setPreferredSize(new Dimension(120, 28));
        cmbProject.addActionListener(this);
        projectPanel.add(cmbProject, BorderLayout.CENTER);
        
        JButton btnRefreshProjects = new JButton("↻");
        btnRefreshProjects.setFont(new Font("Arial", Font.BOLD, 14));
        btnRefreshProjects.setPreferredSize(new Dimension(30, 28));
        btnRefreshProjects.setToolTipText("Refresh project list");
        btnRefreshProjects.addActionListener(this);
        btnRefreshProjects.setActionCommand("refreshProjects");
        projectPanel.add(btnRefreshProjects, BorderLayout.EAST);
        
        formPanel.add(projectPanel, gbc);

        // Row 3: Project ID (read-only)
        gbc.gridx = 0; gbc.gridy = 2; gbc.weightx = 0.3;
        JLabel lblProjectID = new JLabel("Project ID:");
        lblProjectID.setFont(new Font("Arial", Font.BOLD, 12));
        formPanel.add(lblProjectID, gbc);

        gbc.gridx = 1; gbc.weightx = 0.7;
        txtProjectID = new JTextField(10);
        txtProjectID.setEditable(false);
        txtProjectID.setBackground(new Color(240, 240, 240));
        txtProjectID.setPreferredSize(new Dimension(120, 28));
        formPanel.add(txtProjectID, gbc);

        // Row 4: Project Name (read-only)
        gbc.gridx = 0; gbc.gridy = 3; gbc.weightx = 0.3;
        JLabel lblProjectNameLabel = new JLabel("Project Name:");
        lblProjectNameLabel.setFont(new Font("Arial", Font.BOLD, 12));
        formPanel.add(lblProjectNameLabel, gbc);

        gbc.gridx = 1; gbc.weightx = 0.7;
        this.lblProjectName = new JLabel("Not selected");
        this.lblProjectName.setFont(new Font("Arial", Font.PLAIN, 12));
        this.lblProjectName.setForeground(new Color(0, 102, 204));
        this.lblProjectName.setBackground(new Color(240, 240, 240));
        this.lblProjectName.setOpaque(true);
        this.lblProjectName.setPreferredSize(new Dimension(120, 28));
        this.lblProjectName.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        formPanel.add(this.lblProjectName, gbc);

        leftPanel.add(formPanel, BorderLayout.NORTH);

        // ===== FULL REPORT DISPLAY =====
        JPanel reportPanel = new JPanel(new BorderLayout());
        reportPanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createEtchedBorder(),
            "📄 Complete Impact Report",
            TitledBorder.LEFT,
            TitledBorder.TOP,
            new Font("Arial", Font.BOLD, 12)
        ));
        
        txtFullReport = new JTextArea(15, 30);
        txtFullReport.setEditable(false);
        txtFullReport.setFont(new Font("Monospaced", Font.PLAIN, 12));
        txtFullReport.setBackground(new Color(255, 255, 240));
        txtFullReport.setText(
            "═══════════════════════════════════════════════════════════════\n" +
            "                    IMPACT REPORT GENERATOR                   \n" +
            "═══════════════════════════════════════════════════════════════\n" +
            "\n" +
            "  Select a project from the dropdown above and click\n" +
            "  'Generate Report' to create a comprehensive impact report.\n" +
            "\n" +
            "═══════════════════════════════════════════════════════════════"
        );
        
        JScrollPane scrollReport = new JScrollPane(txtFullReport);
        scrollReport.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        reportPanel.add(scrollReport, BorderLayout.CENTER);
        leftPanel.add(reportPanel, BorderLayout.CENTER);

        // Buttons
        JPanel buttonPanel = new JPanel(new GridLayout(1, 3, 5, 5));
        btnGenerate = new JButton("Generate Report");
        btnClear = new JButton("Clear");
        btnViewAll = new JButton("View All");

        JButton[] buttons = {btnGenerate, btnClear, btnViewAll};
        Color[] colors = {
            new Color(0, 153, 76),   // Generate - Green
            new Color(128, 128, 128), // Clear - Gray
            new Color(0, 0, 153)     // View All - Dark Blue
        };
        
        for (int i = 0; i < buttons.length; i++) {
            buttons[i].setFont(new Font("Arial", Font.BOLD, 12));
            buttons[i].setBackground(colors[i]);
            buttons[i].setForeground(Color.RED);
            buttons[i].setFocusPainted(false);
            buttons[i].setPreferredSize(new Dimension(120, 40));
            buttons[i].addActionListener(this);
            buttonPanel.add(buttons[i]);
        }

        leftPanel.add(buttonPanel, BorderLayout.SOUTH);

        // ===== RIGHT PANEL: Table =====
        JPanel rightPanel = new JPanel(new BorderLayout(5, 5));

        JLabel lblTableTitle = new JLabel("All Reports", JLabel.CENTER);
        lblTableTitle.setFont(new Font("Arial", Font.BOLD, 14));
        rightPanel.add(lblTableTitle, BorderLayout.NORTH);

        String[] columns = {"ID", "Project", "Date", "Summary"};
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
                    loadReportById(id);
                }
            }
        });

        table.getColumnModel().getColumn(0).setPreferredWidth(50);
        table.getColumnModel().getColumn(1).setPreferredWidth(120);
        table.getColumnModel().getColumn(2).setPreferredWidth(100);
        table.getColumnModel().getColumn(3).setPreferredWidth(150);

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createEtchedBorder());
        rightPanel.add(scrollPane, BorderLayout.CENTER);

        // Status label
        JLabel lblStatus = new JLabel("Click a report to view details", JLabel.CENTER);
        lblStatus.setFont(new Font("Arial", Font.PLAIN, 11));
        lblStatus.setForeground(Color.GRAY);
        rightPanel.add(lblStatus, BorderLayout.SOUTH);

        splitPane.setLeftComponent(leftPanel);
        splitPane.setRightComponent(rightPanel);
        add(splitPane, BorderLayout.CENTER);

        // Status Bar
        JPanel statusBar = new JPanel(new FlowLayout(FlowLayout.LEFT));
        statusBar.setBorder(BorderFactory.createEtchedBorder());
        lblStatusMessage = new JLabel("Ready - Select a project and generate a report");
        lblStatusMessage.setFont(new Font("Arial", Font.PLAIN, 11));
        statusBar.add(lblStatusMessage);
        add(statusBar, BorderLayout.SOUTH);

        // Initialize
        currentReport = new ImpactReport();
        loadProjects();
        updateTable();
    }

    // ===== GET NEXT REPORT ID =====
    private int getNextReportId() {
        int maxId = 0;
        for (ImpactReport report : ReportService.getReports()) {
            if (report.getReportID() > maxId) {
                maxId = report.getReportID();
            }
        }
        return maxId + 1;
    }

    // ===== LOAD PROJECTS INTO COMBOBOX =====
    private void loadProjects() {
        cmbProject.removeAllItems();
        
        java.util.ArrayList<FundingApplication> apps = FundingService.getApplications();
        HashSet<Integer> projectIds = new HashSet<>();
        
        if (apps.isEmpty()) {
            cmbProject.addItem("No projects found - Submit an application first");
            setStatus("⚠️ No projects available. Submit a funding application first.");
            return;
        }
        
        for (FundingApplication app : apps) {
            Project project = app.getProject();
            if (project != null && !projectIds.contains(project.getProjectID())) {
                projectIds.add(project.getProjectID());
                String status = app.getStatus().name();
                String statusEmoji = getStatusEmoji(app.getStatus());
                String displayText = "ID: " + project.getProjectID() + 
                                    " | " + project.getProjectName() + 
                                    " (" + statusEmoji + " " + status + ")";
                cmbProject.addItem(displayText);
            }
        }
        
        if (cmbProject.getItemCount() == 0) {
            cmbProject.addItem("No projects found");
        } else {
            setStatus("Loaded " + cmbProject.getItemCount() + " projects");
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

    // ===== GET SELECTED PROJECT =====
    private Project getSelectedProject() {
        String selected = (String) cmbProject.getSelectedItem();
        if (selected == null || selected.startsWith("No projects")) {
            return null;
        }
        
        try {
            int start = selected.indexOf("ID: ") + 4;
            int end = selected.indexOf(" |");
            if (start > 0 && end > start) {
                int projectId = Integer.parseInt(selected.substring(start, end));
                for (FundingApplication app : FundingService.getApplications()) {
                    if (app.getProject().getProjectID() == projectId) {
                        return app.getProject();
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    // ===== GET APPLICATION FOR PROJECT =====
    private FundingApplication getApplicationForProject(int projectId) {
        for (FundingApplication app : FundingService.getApplications()) {
            if (app.getProject().getProjectID() == projectId) {
                return app;
            }
        }
        return null;
    }

    // ===== GET ASSESSMENT FOR APPLICATION =====
    private ProjectAssessment getAssessmentForApplication(int applicationId) {
        for (ProjectAssessment assessment : AssessmentService.getAssessments()) {
            if (assessment.getApplication().getApplicationID() == applicationId) {
                return assessment;
            }
        }
        return null;
    }

    // ===== GET NAMAA INDEX FOR PROJECT =====
    private NamaaIndex getNamaaIndexForProject(int projectId) {
        for (NamaaIndex index : NamaaIndexService.getIndexes()) {
            if (index.getProject().getProjectID() == projectId) {
                return index;
            }
        }
        return null;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        try {
            // Handle project selection
            if (e.getSource() == cmbProject) {
                Project selectedProject = getSelectedProject();
                if (selectedProject != null) {
                    txtProjectID.setText(String.valueOf(selectedProject.getProjectID()));
                    lblProjectName.setText(selectedProject.getProjectName());
                    setStatus("Selected Project: " + selectedProject.getProjectName());
                } else {
                    txtProjectID.setText("");
                    lblProjectName.setText("Not selected");
                }
                return;
            }
            
            String command = e.getActionCommand();
            if ("refreshProjects".equals(command)) {
                loadProjects();
                JOptionPane.showMessageDialog(this,
                    "Project list refreshed!",
                    "Refreshed",
                    JOptionPane.INFORMATION_MESSAGE);
                return;
            }
            
            if (e.getSource() == btnGenerate) {
                generateReport();
            } else if (e.getSource() == btnClear) {
                clearForm();
            } else if (e.getSource() == btnViewAll) {
                updateTable();
                setStatus("Displaying all reports");
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                "Error: " + ex.getMessage(),
                "Operation Failed",
                JOptionPane.ERROR_MESSAGE);
            setStatus("Error: " + ex.getMessage());
        }
    }

    // ===== LOAD REPORT BY ID =====
    private void loadReportById(int id) {
        ImpactReport report = ReportService.searchReport(id);
        if (report != null) {
            txtFullReport.setText(report.getReportSummary());
            txtReportID.setText(String.valueOf(report.getReportID()));
            if (report.getProject() != null) {
                txtProjectID.setText(String.valueOf(report.getProject().getProjectID()));
                lblProjectName.setText(report.getProject().getProjectName());
            }
            setStatus("Loaded Report ID: " + id);
        }
    }

    // ===== GENERATE COMPREHENSIVE REPORT =====
    private void generateReport() {
        Project project = getSelectedProject();
        if (project == null) {
            JOptionPane.showMessageDialog(this,
                "Please select a project from the dropdown.",
                "No Project Selected",
                JOptionPane.WARNING_MESSAGE);
            return;
        }

        int reportId = getNextReportId();
        
        // ===== GATHER ALL DATA =====
        FundingApplication application = getApplicationForProject(project.getProjectID());
        if (application == null) {
            JOptionPane.showMessageDialog(this,
                "No application found for this project.",
                "Error",
                JOptionPane.ERROR_MESSAGE);
            return;
        }

        ProjectAssessment assessment = getAssessmentForApplication(application.getApplicationID());
        NamaaIndex index = getNamaaIndexForProject(project.getProjectID());

        // ===== BUILD COMPREHENSIVE REPORT =====
        String reportSummary = buildReportSummary(project, application, assessment, index);

        // ===== FIXED: Create ImpactReport using setter approach =====
        currentReport = new ImpactReport();
        currentReport.setReportID(reportId);
        currentReport.setProject(project);
        currentReport.setAssessment(assessment);
        currentReport.setNamaaIndex(index);
        currentReport.setReportDate(LocalDate.now());
        currentReport.setReportSummary(reportSummary);

        ReportService.addReport(currentReport);
        txtReportID.setText(String.valueOf(getNextReportId()));
        updateTable();

        // Display the report
        txtFullReport.setText(reportSummary);

        setStatus("Report ID " + reportId + " generated for Project: " + project.getProjectName());

        JOptionPane.showMessageDialog(this,
            "✅ Report generated successfully!\n" +
            "Report ID: " + reportId + "\n" +
            "Project: " + project.getProjectName(),
            "Success",
            JOptionPane.INFORMATION_MESSAGE);
    }

    // ===== BUILD REPORT SUMMARY =====
    private String buildReportSummary(Project project, FundingApplication application, 
                                      ProjectAssessment assessment, NamaaIndex index) {
        StringBuilder report = new StringBuilder();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMMM yyyy");
        String currentDate = LocalDate.now().format(formatter);

        // Header
        report.append("╔═══════════════════════════════════════════════════════════════════╗\n");
        report.append("║                    IMPACT REPORT                                ║\n");
        report.append("║                    Namaa Smart Waqf Platform                    ║\n");
        report.append("╠═══════════════════════════════════════════════════════════════════╣\n");
        report.append("║  Date Generated: ").append(String.format("%-52s", currentDate)).append("║\n");
        report.append("╚═══════════════════════════════════════════════════════════════════╝\n\n");

        // ===== 1. PROJECT INFORMATION =====
        report.append("📋 1. PROJECT INFORMATION\n");
        report.append("─────────────────────────────────────────────────────────────────────\n");
        report.append("  Project ID:       ").append(project.getProjectID()).append("\n");
        report.append("  Project Name:     ").append(project.getProjectName()).append("\n");
        report.append("  Sector:           ").append(project.getSector()).append("\n");
        report.append("  Description:      ").append(project.getDescription() != null ? project.getDescription() : "N/A").append("\n");
        report.append("  Location:         ").append(project.getLocation()).append("\n");
        report.append("  Total Cost:       ").append(String.format("%,.2f QR", project.getProjectCost())).append("\n");
        report.append("  Expected Beneficiaries: ").append(project.getExpectedBeneficiaries()).append("\n");
        report.append("  Duration:         ").append(project.getDurationMonths()).append(" months\n");
        report.append("  Application Status: ").append(application.getStatus()).append("\n\n");

        // ===== 2. APPLICATION DETAILS =====
        report.append("📝 2. APPLICATION DETAILS\n");
        report.append("─────────────────────────────────────────────────────────────────────\n");
        report.append("  Application ID:   ").append(application.getApplicationID()).append("\n");
        report.append("  Requested Amount: ").append(String.format("%,.2f SAR", application.getRequestedAmount())).append("\n");
        report.append("  Application Date: ").append(application.getApplicationDate().toLocalDate()).append("\n");
        if (application.getBeneficiary() != null) {
            report.append("  Beneficiary:      ").append(application.getBeneficiary().getFullName()).append("\n");
            report.append("  Education:        ").append(application.getBeneficiary().getEducation()).append("\n");
            report.append("  Experience:       ").append(application.getBeneficiary().getExperienceYears()).append(" years\n");
        }
        report.append("\n");

        // ===== 3. ASSESSMENT RESULTS =====
        report.append("📊 3. ASSESSMENT RESULTS\n");
        report.append("─────────────────────────────────────────────────────────────────────\n");
        if (assessment != null) {
            report.append("  Assessment ID:    ").append(assessment.getAssessmentID()).append("\n");
            report.append("  Economic Score:   ").append(String.format("%.2f", assessment.getEconomicScore())).append("\n");
            report.append("  Technical Score:  ").append(String.format("%.2f", assessment.getTechnicalScore())).append("\n");
            report.append("  Social Score:     ").append(String.format("%.2f", assessment.getSocialScore())).append("\n");
            report.append("  Environmental:    ").append(String.format("%.2f", assessment.getEnvironmentalScore())).append("\n");
            report.append("  Innovation Score: ").append(String.format("%.2f", assessment.getInnovationScore())).append("\n");
            report.append("  ─────────────────────────────────────────────────\n");
            report.append("  ★ PRI SCORE:      ").append(String.format("%.2f", assessment.getPriScore())).append("\n");
            
            String recommendation = assessment.getRecommendation();
            String recEmoji = recommendation.equals("Approved") ? "✅" : 
                             recommendation.equals("Needs Revision") ? "⚠️" : "❌";
            report.append("  ★ Recommendation: ").append(recEmoji).append(" ").append(recommendation).append("\n");
            report.append("  Assessment Date:  ").append(assessment.getAssessmentDate()).append("\n");
        } else {
            report.append("  ⚠️ No assessment found for this project.\n");
        }
        report.append("\n");

        // ===== 4. NAMAA INDEX =====
        report.append("📈 4. NAMAA INDEX - IMPACT METRICS\n");
        report.append("─────────────────────────────────────────────────────────────────────\n");
        if (index != null) {
            report.append("  Index ID:         ").append(index.getIndexID()).append("\n");
            report.append("  Economic Impact:  ").append(String.format("%.2f", index.getEconomicImpact())).append("\n");
            report.append("  Social Impact:    ").append(String.format("%.2f", index.getSocialImpact())).append("\n");
            report.append("  Sustainability:   ").append(String.format("%.2f", index.getSustainability())).append("\n");
            report.append("  Innovation:       ").append(String.format("%.2f", index.getInnovation())).append("\n");
            report.append("  ─────────────────────────────────────────────────\n");
            
            double finalIndex = index.getFinalIndex();
            String grade;
            String gradeEmoji;
            if (finalIndex >= 80) {
                grade = "Excellent";
                gradeEmoji = "🌟";
            } else if (finalIndex >= 60) {
                grade = "Good";
                gradeEmoji = "✅";
            } else if (finalIndex >= 40) {
                grade = "Average";
                gradeEmoji = "⚠️";
            } else {
                grade = "Needs Improvement";
                gradeEmoji = "❌";
            }
            report.append("  ★ FINAL INDEX:    ").append(String.format("%.2f", finalIndex)).append("\n");
            report.append("  ★ GRADE:          ").append(gradeEmoji).append(" ").append(grade).append("\n");
        } else {
            report.append("  ⚠️ No Namaa Index calculated for this project.\n");
        }
        report.append("\n");

        // ===== 5. RECOMMENDATIONS =====
        report.append("💡 5. RECOMMENDATIONS\n");
        report.append("─────────────────────────────────────────────────────────────────────\n");
        if (assessment != null) {
            String rec = assessment.getRecommendation();
            if (rec.equals("Approved")) {
                report.append("  ✅ Project is recommended for funding.\n");
                report.append("  Next Steps:\n");
                report.append("    • Proceed with funding allocation\n");
                report.append("    • Monitor project implementation\n");
            } else if (rec.equals("Needs Revision")) {
                report.append("  ⚠️ Project needs revision before approval.\n");
                report.append("  Areas for Improvement:\n");
                if (assessment.getEconomicScore() < 60) report.append("    • Improve economic viability\n");
                if (assessment.getTechnicalScore() < 60) report.append("    • Enhance technical feasibility\n");
                if (assessment.getSocialScore() < 60) report.append("    • Increase social impact\n");
            } else {
                report.append("  ❌ Project is rejected for funding.\n");
                report.append("  Alternative Actions:\n");
                report.append("    • Consider a revised project approach\n");
                report.append("    • Seek partnership or different funding source\n");
            }
        }
        report.append("\n");

        // Footer
        report.append("═══════════════════════════════════════════════════════════════════\n");
        report.append("  Report generated by Namaa Smart Waqf Platform v2.0\n");
        report.append("═══════════════════════════════════════════════════════════════════\n");

        return report.toString();
    }

    // ===== CLEAR FORM =====
    private void clearForm() {
        txtReportID.setText(String.valueOf(getNextReportId()));
        txtProjectID.setText("");
        lblProjectName.setText("Not selected");
        if (cmbProject.getItemCount() > 0) {
            cmbProject.setSelectedIndex(0);
        }
        txtFullReport.setText(
            "═══════════════════════════════════════════════════════════════\n" +
            "                    IMPACT REPORT GENERATOR                   \n" +
            "═══════════════════════════════════════════════════════════════\n" +
            "\n" +
            "  Select a project from the dropdown above and click\n" +
            "  'Generate Report' to create a comprehensive impact report.\n" +
            "\n" +
            "═══════════════════════════════════════════════════════════════"
        );
        currentReport = new ImpactReport();
        setStatus("Form cleared. Ready to generate a new report.");
    }

    // ===== UPDATE TABLE =====
    private void updateTable() {
        tableModel.setRowCount(0);
        for (ImpactReport report : ReportService.getReports()) {
            String summary = report.getReportSummary();
            if (summary != null && summary.length() > 50) {
                summary = summary.substring(0, 50) + "...";
            }
            Object[] row = {
                report.getReportID(),
                report.getProject() != null ? report.getProject().getProjectName() : "N/A",
                report.getReportDate(),
                summary != null ? summary : "No summary"
            };
            tableModel.addRow(row);
        }
        setStatus("Displaying " + ReportService.getReports().size() + " reports");
    }

    private void setStatus(String message) {
        if (lblStatusMessage != null) {
            lblStatusMessage.setText("Status: " + message);
        }
    }
}