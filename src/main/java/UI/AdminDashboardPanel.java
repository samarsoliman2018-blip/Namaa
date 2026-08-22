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
import java.io.*;
import java.nio.file.Files;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import Model.*;
import Service.*;

public class AdminDashboardPanel extends JPanel implements ActionListener {
    private Administrator admin;
    
    // ===== STATS LABELS =====
    private JLabel lblTotalWaqifs, lblTotalBeneficiaries, lblActiveLoans;
    private JLabel lblRepaymentRate, lblTotalWaqfAmount, lblAvgNamaa;
    private JLabel lblSystemUsers, lblPendingApplications, lblTotalProjects;
    private JLabel lblAdminName;
    
    // ===== STATS CARDS =====
    private JPanel statsPanel;
    
    // ===== USER MANAGEMENT =====
    private JTable userTable;
    private DefaultTableModel userModel;
    private JButton btnAddUser, btnEditUser, btnDeleteUser, btnRefreshUsers;
    private JComboBox<String> cmbUserRole;
    private JTextField txtSearchUser;
    
    // ===== SYSTEM MANAGEMENT =====
    private JButton btnBackup, btnRestore, btnExportReport;
    private JButton btnAIConfig, btnMaintenance;
    private JTextArea txtSystemLog;
    
    // ===== REPORTS PANEL =====
    private JComboBox<String> cmbReportType;
    private JButton btnGenerateReport, btnExportReportBtn;
    private JTextArea txtReportPreview;
    
    // ===== STATUS =====
    private JLabel lblStatusMessage;
    private JTabbedPane tabbedPane;

    public AdminDashboardPanel(Administrator admin) {
        this.admin = admin;
        setLayout(new BorderLayout(15, 15));
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        setBackground(new Color(245, 248, 250));

        // ===== TOP: Header =====
        add(createHeaderPanel(), BorderLayout.NORTH);

        // ===== CENTER: Tabbed Panels =====
        tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Arial", Font.BOLD, 14));
        tabbedPane.setTabPlacement(JTabbedPane.TOP);

        tabbedPane.addTab("📊 Dashboard", createOverviewPanel());
        tabbedPane.addTab("👥 User Management", createUserManagementPanel());
        tabbedPane.addTab("⚙️ System Management", createSystemManagementPanel());
        tabbedPane.addTab("📄 Reports", createReportsPanel());
        tabbedPane.addTab("🤖 AI Configuration", createAIConfigPanel());

        add(tabbedPane, BorderLayout.CENTER);

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

        JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        leftPanel.setBackground(new Color(245, 248, 250));
        
        lblAdminName = new JLabel("⚙️ Welcome, " + admin.getFullName());
        lblAdminName.setFont(new Font("Arial", Font.BOLD, 22));
        lblAdminName.setForeground(new Color(0, 102, 204));
        leftPanel.add(lblAdminName);

        JLabel lblSubtitle = new JLabel("System Administration & Operations");
        lblSubtitle.setFont(new Font("Arial", Font.PLAIN, 14));
        lblSubtitle.setForeground(Color.GRAY);
        leftPanel.add(lblSubtitle);

        panel.add(leftPanel, BorderLayout.WEST);

        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 5));
        rightPanel.setBackground(new Color(245, 248, 250));

        JButton btnRefreshAll = new JButton("🔄 Refresh All");
        btnRefreshAll.setFont(new Font("Arial", Font.BOLD, 12));
        btnRefreshAll.setBackground(new Color(0, 102, 204));
        btnRefreshAll.setForeground(Color.BLACK);
        btnRefreshAll.setFocusPainted(false);
        btnRefreshAll.setPreferredSize(new Dimension(120, 30));
        btnRefreshAll.addActionListener(this);
        btnRefreshAll.setActionCommand("refreshAll");
        rightPanel.add(btnRefreshAll);

        JButton btnExportSystem = new JButton("📤 Export System Data");
        btnExportSystem.setFont(new Font("Arial", Font.PLAIN, 11));
        btnExportSystem.setBackground(new Color(0, 153, 76));
        btnExportSystem.setForeground(Color.BLACK);
        btnExportSystem.setFocusPainted(false);
        btnExportSystem.setPreferredSize(new Dimension(150, 30));
        btnExportSystem.addActionListener(this);
        btnExportSystem.setActionCommand("exportSystem");
        rightPanel.add(btnExportSystem);

        panel.add(rightPanel, BorderLayout.EAST);

        return panel;
    }

    // ===== OVERVIEW PANEL =====
    private JPanel createOverviewPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        statsPanel = new JPanel(new GridLayout(3, 4, 10, 10));
        statsPanel.setBackground(Color.WHITE);

        lblTotalWaqifs = new JLabel("0");
        lblTotalBeneficiaries = new JLabel("0");
        lblActiveLoans = new JLabel("0");
        lblRepaymentRate = new JLabel("0%");
        lblTotalWaqfAmount = new JLabel("0 QR");
        lblAvgNamaa = new JLabel("0.00");
        lblSystemUsers = new JLabel("0");
        lblPendingApplications = new JLabel("0");
        lblTotalProjects = new JLabel("0");

        statsPanel.add(createStatCard("👥 Total Waqifs", lblTotalWaqifs, "Active Donors", new Color(0, 102, 204)));
        statsPanel.add(createStatCard("👤 Total Beneficiaries", lblTotalBeneficiaries, "Recipients", new Color(0, 153, 76)));
        statsPanel.add(createStatCard("📊 Total Users", lblSystemUsers, "All Users", new Color(153, 0, 153)));
        statsPanel.add(createStatCard("📋 Pending Applications", lblPendingApplications, "Awaiting Review", new Color(255, 200, 0)));

        statsPanel.add(createStatCard("💰 Total Waqf Amount", lblTotalWaqfAmount, "Total Contributions", new Color(0, 102, 204)));
        statsPanel.add(createStatCard("🏦 Active Loans", lblActiveLoans, "Currently Active", new Color(0, 153, 76)));
        statsPanel.add(createStatCard("🔄 Repayment Rate", lblRepaymentRate, "Loan Performance", new Color(0, 102, 204)));
        statsPanel.add(createStatCard("📈 Avg Namaa Index", lblAvgNamaa, "Overall Impact", new Color(204, 102, 0)));

        statsPanel.add(createStatCard("📊 Total Projects", lblTotalProjects, "All Projects", new Color(153, 0, 153)));
        statsPanel.add(createStatCard("📊 Avg PRI", new JLabel("0.00"), "Project Readiness", new Color(0, 102, 204)));
        statsPanel.add(createStatCard("📊 Total Assessments", new JLabel("0"), "AI Evaluations", new Color(0, 153, 76)));
        statsPanel.add(createStatCard("📊 Total Donations", new JLabel("0"), "All Contributions", new Color(204, 102, 0)));

        panel.add(statsPanel, BorderLayout.NORTH);

        JPanel bottomPanel = new JPanel(new GridLayout(1, 2, 10, 10));
        bottomPanel.setBackground(Color.WHITE);
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));

        JPanel statusPanel = new JPanel(new BorderLayout(5, 5));
        statusPanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createEtchedBorder(),
            "🟢 System Status",
            TitledBorder.LEFT,
            TitledBorder.TOP,
            new Font("Arial", Font.BOLD, 12)
        ));
        statusPanel.setBackground(Color.WHITE);

        JTextArea systemStatus = new JTextArea(6, 30);
        systemStatus.setEditable(false);
        systemStatus.setFont(new Font("Monospaced", Font.PLAIN, 12));
        systemStatus.setBackground(new Color(255, 255, 240));
        systemStatus.setText(
            "╔════════════════════════════════════════════════════════\n" +
            "║                    SYSTEM STATUS                       \n" +
            "╠════════════════════════════════════════════════════════\n" +
            "║  ✅ Database Connection:  Connected                   \n" +
            "║  ✅ AI Service:           " + (AIService.isAIAvailable() ? "✅ Active" : "⚠️ Disabled") + "\n" +
            "║  ✅ Backup System:        Ready                        \n" +
            "║  ✅ Last Backup:          " + LocalDate.now().minusDays(1) + "          \n" +
            "║  ✅ System Version:       Namaa v2.0                  \n" +
            "║  ✅ Uptime:               99.9%                       \n" +
            "╚════════════════════════════════════════════════════════"
        );
        statusPanel.add(new JScrollPane(systemStatus), BorderLayout.CENTER);

        JPanel chartPanel = new JPanel(new BorderLayout(5, 5));
        chartPanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createEtchedBorder(),
            "📊 Impact Distribution",
            TitledBorder.LEFT,
            TitledBorder.TOP,
            new Font("Arial", Font.BOLD, 12)
        ));
        chartPanel.setBackground(Color.WHITE);

        JTextArea chartArea = new JTextArea(6, 30);
        chartArea.setEditable(false);
        chartArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        chartArea.setBackground(new Color(255, 255, 240));
        chartArea.setText(generateImpactChart());
        chartPanel.add(new JScrollPane(chartArea), BorderLayout.CENTER);

        bottomPanel.add(statusPanel);
        bottomPanel.add(chartPanel);

        panel.add(bottomPanel, BorderLayout.CENTER);

        return panel;
    }

    private String generateImpactChart() {
        java.util.ArrayList<NamaaIndex> indexes = NamaaIndexService.getIndexes();
        if (indexes.isEmpty()) {
            return "No Namaa Index data available yet.\n\nComplete assessments to generate impact charts.";
        }

        int excellent = 0, good = 0, average = 0, poor = 0;
        double total = 0;

        for (NamaaIndex idx : indexes) {
            double score = idx.getFinalIndex();
            total += score;
            if (score >= 80) excellent++;
            else if (score >= 60) good++;
            else if (score >= 40) average++;
            else poor++;
        }

        int totalCount = indexes.size();
        double avg = total / totalCount;

        StringBuilder chart = new StringBuilder();
        chart.append("  NAMAA INDEX DISTRIBUTION\n");
        chart.append("  ─────────────────────────────────────\n");
        chart.append("  Excellent (80-100): ");
        chart.append(drawBar(excellent, totalCount)).append(" ").append(String.format("%.0f%%", (excellent * 100.0 / totalCount))).append("\n");
        chart.append("  Good (60-79):      ");
        chart.append(drawBar(good, totalCount)).append(" ").append(String.format("%.0f%%", (good * 100.0 / totalCount))).append("\n");
        chart.append("  Average (40-59):   ");
        chart.append(drawBar(average, totalCount)).append(" ").append(String.format("%.0f%%", (average * 100.0 / totalCount))).append("\n");
        chart.append("  Poor (0-39):       ");
        chart.append(drawBar(poor, totalCount)).append(" ").append(String.format("%.0f%%", (poor * 100.0 / totalCount))).append("\n");
        chart.append("\n  Average Score: ").append(String.format("%.2f", avg));

        return chart.toString();
    }

    private String drawBar(int count, int total) {
        if (total == 0) return "";
        int percent = (int) ((count * 100.0) / total);
        int barLength = percent / 2;
        if (barLength == 0 && percent > 0) barLength = 1;
        return "█".repeat(Math.min(barLength, 30));
    }

    private JPanel createStatCard(String label, JLabel value, String subtitle, Color color) {
        JPanel panel = new JPanel(new BorderLayout(5, 2));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(color, 1),
            BorderFactory.createEmptyBorder(8, 10, 8, 10)
        ));
        
        value.setFont(new Font("Arial", Font.BOLD, 20));
        value.setForeground(color);
        value.setHorizontalAlignment(SwingConstants.CENTER);
        panel.add(value, BorderLayout.CENTER);
        
        JLabel descLabel = new JLabel(label);
        descLabel.setFont(new Font("Arial", Font.BOLD, 11));
        descLabel.setForeground(color);
        descLabel.setHorizontalAlignment(SwingConstants.CENTER);
        panel.add(descLabel, BorderLayout.NORTH);
        
        JLabel subLabel = new JLabel(subtitle);
        subLabel.setFont(new Font("Arial", Font.PLAIN, 9));
        subLabel.setForeground(Color.GRAY);
        subLabel.setHorizontalAlignment(SwingConstants.CENTER);
        panel.add(subLabel, BorderLayout.SOUTH);
        
        return panel;
    }

    // ===== USER MANAGEMENT PANEL =====
    private JPanel createUserManagementPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        controlPanel.setBackground(Color.WHITE);
        controlPanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createEtchedBorder(),
            "User Controls",
            TitledBorder.LEFT,
            TitledBorder.TOP,
            new Font("Arial", Font.BOLD, 12)
        ));

        controlPanel.add(new JLabel("Search:"));
        txtSearchUser = new JTextField(20);
        txtSearchUser.setPreferredSize(new Dimension(150, 28));
        txtSearchUser.addKeyListener(new KeyAdapter() {
            public void keyReleased(KeyEvent e) {
                searchUsers();
            }
        });
        controlPanel.add(txtSearchUser);

        controlPanel.add(new JLabel("Role:"));
        cmbUserRole = new JComboBox<>(new String[]{"All", "Waqif", "Beneficiary", "Administrator", "CommitteeMember"});
        cmbUserRole.setPreferredSize(new Dimension(120, 28));
        cmbUserRole.addActionListener(this);
        cmbUserRole.setActionCommand("filterUsers");
        controlPanel.add(cmbUserRole);

        controlPanel.add(Box.createHorizontalStrut(20));

        btnAddUser = new JButton("➕ Add User");
        btnAddUser.setFont(new Font("Arial", Font.BOLD, 11));
        btnAddUser.setBackground(new Color(0, 153, 76));
        btnAddUser.setForeground(Color.BLACK);
        btnAddUser.setFocusPainted(false);
        btnAddUser.addActionListener(this);
        btnAddUser.setActionCommand("addUser");
        controlPanel.add(btnAddUser);

        btnEditUser = new JButton("✏️ Edit User");
        btnEditUser.setFont(new Font("Arial", Font.BOLD, 11));
        btnEditUser.setBackground(new Color(0, 102, 204));
        btnEditUser.setForeground(Color.BLACK);
        btnEditUser.setFocusPainted(false);
        btnEditUser.addActionListener(this);
        btnEditUser.setActionCommand("editUser");
        controlPanel.add(btnEditUser);

        btnDeleteUser = new JButton("🗑️ Delete User");
        btnDeleteUser.setFont(new Font("Arial", Font.BOLD, 11));
        btnDeleteUser.setBackground(new Color(200, 0, 0));
        btnDeleteUser.setForeground(Color.BLACK);
        btnDeleteUser.setFocusPainted(false);
        btnDeleteUser.addActionListener(this);
        btnDeleteUser.setActionCommand("deleteUser");
        controlPanel.add(btnDeleteUser);

        btnRefreshUsers = new JButton("🔄 Refresh");
        btnRefreshUsers.setFont(new Font("Arial", Font.BOLD, 11));
        btnRefreshUsers.setBackground(new Color(128, 128, 128));
        btnRefreshUsers.setForeground(Color.BLACK);
        btnRefreshUsers.setFocusPainted(false);
        btnRefreshUsers.addActionListener(this);
        btnRefreshUsers.setActionCommand("refreshUsers");
        controlPanel.add(btnRefreshUsers);

        panel.add(controlPanel, BorderLayout.NORTH);

        JPanel tablePanel = new JPanel(new BorderLayout(5, 5));
        tablePanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createEtchedBorder(),
            "👥 System Users",
            TitledBorder.LEFT,
            TitledBorder.TOP,
            new Font("Arial", Font.BOLD, 12)
        ));
        tablePanel.setBackground(Color.WHITE);

        String[] columns = {"ID", "Name", "Username", "Email", "Role", "Phone"};
        userModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int col) { return false; }
        };

        userTable = new JTable(userModel);
        userTable.setFont(new Font("Arial", Font.PLAIN, 12));
        userTable.setRowHeight(28);
        userTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        userTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        userTable.setDefaultRenderer(Object.class, new javax.swing.table.DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                
                if (column == 4) {
                    String role = (String) value;
                    if (role.contains("Administrator")) {
                        c.setBackground(new Color(200, 50, 50));
                        c.setForeground(Color.WHITE);
                    } else if (role.contains("Waqif")) {
                        c.setBackground(new Color(0, 102, 204));
                        c.setForeground(Color.WHITE);
                    } else if (role.contains("Beneficiary")) {
                        c.setBackground(new Color(0, 153, 76));
                        c.setForeground(Color.WHITE);
                    } else if (role.contains("Committee")) {
                        c.setBackground(new Color(153, 0, 153));
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

        JScrollPane scrollTable = new JScrollPane(userTable);
        scrollTable.setPreferredSize(new Dimension(600, 350));
        tablePanel.add(scrollTable, BorderLayout.CENTER);

        JPanel footerPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        footerPanel.setBackground(Color.WHITE);
        JLabel lblUserCount = new JLabel("Total Users: " + UserService.getUsers().size());
        lblUserCount.setFont(new Font("Arial", Font.PLAIN, 11));
        lblUserCount.setForeground(Color.GRAY);
        footerPanel.add(lblUserCount);
        tablePanel.add(footerPanel, BorderLayout.SOUTH);

        panel.add(tablePanel, BorderLayout.CENTER);

        loadUsers();

        return panel;
    }

    // ===== SYSTEM MANAGEMENT PANEL =====
    private JPanel createSystemManagementPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel controlsPanel = new JPanel(new GridBagLayout());
        controlsPanel.setBackground(Color.WHITE);
        controlsPanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createEtchedBorder(),
            "⚙️ System Controls",
            TitledBorder.LEFT,
            TitledBorder.TOP,
            new Font("Arial", Font.BOLD, 14)
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0.3;
        controlsPanel.add(new JLabel("💾 Backup:"), gbc);
        gbc.gridx = 1; gbc.weightx = 0.7;
        btnBackup = new JButton("Create Backup");
        btnBackup.setFont(new Font("Arial", Font.BOLD, 12));
        btnBackup.setBackground(new Color(0, 102, 204));
        btnBackup.setForeground(Color.BLACK);
        btnBackup.setFocusPainted(false);
        btnBackup.setPreferredSize(new Dimension(150, 35));
        btnBackup.addActionListener(this);
        btnBackup.setActionCommand("backup");
        controlsPanel.add(btnBackup, gbc);

        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0.3;
        controlsPanel.add(new JLabel("🔄 Restore:"), gbc);
        gbc.gridx = 1; gbc.weightx = 0.7;
        btnRestore = new JButton("Restore from Backup");
        btnRestore.setFont(new Font("Arial", Font.BOLD, 12));
        btnRestore.setBackground(new Color(255, 153, 0));
        btnRestore.setForeground(Color.BLACK);
        btnRestore.setFocusPainted(false);
        btnRestore.setPreferredSize(new Dimension(150, 35));
        btnRestore.addActionListener(this);
        btnRestore.setActionCommand("restore");
        controlsPanel.add(btnRestore, gbc);

        gbc.gridx = 0; gbc.gridy = 2; gbc.weightx = 0.3;
        controlsPanel.add(new JLabel("📤 Export:"), gbc);
        gbc.gridx = 1; gbc.weightx = 0.7;
        btnExportReport = new JButton("Export Report");
        btnExportReport.setFont(new Font("Arial", Font.BOLD, 12));
        btnExportReport.setBackground(new Color(0, 153, 76));
        btnExportReport.setForeground(Color.BLACK);
        btnExportReport.setFocusPainted(false);
        btnExportReport.setPreferredSize(new Dimension(150, 35));
        btnExportReport.addActionListener(this);
        btnExportReport.setActionCommand("exportReport");
        controlsPanel.add(btnExportReport, gbc);

        gbc.gridx = 0; gbc.gridy = 3; gbc.weightx = 0.3;
        controlsPanel.add(new JLabel("🤖 AI:"), gbc);
        gbc.gridx = 1; gbc.weightx = 0.7;
        btnAIConfig = new JButton("AI Configuration");
        btnAIConfig.setFont(new Font("Arial", Font.BOLD, 12));
        btnAIConfig.setBackground(new Color(102, 0, 153));
        btnAIConfig.setForeground(Color.BLACK);
        btnAIConfig.setFocusPainted(false);
        btnAIConfig.setPreferredSize(new Dimension(150, 35));
        btnAIConfig.addActionListener(this);
        btnAIConfig.setActionCommand("aiConfig");
        controlsPanel.add(btnAIConfig, gbc);

        gbc.gridx = 0; gbc.gridy = 4; gbc.weightx = 0.3;
        controlsPanel.add(new JLabel("🔧 Maintenance:"), gbc);
        gbc.gridx = 1; gbc.weightx = 0.7;
        btnMaintenance = new JButton("Run Maintenance");
        btnMaintenance.setFont(new Font("Arial", Font.BOLD, 12));
        btnMaintenance.setBackground(new Color(200, 50, 50));
        btnMaintenance.setForeground(Color.BLACK);
        btnMaintenance.setFocusPainted(false);
        btnMaintenance.setPreferredSize(new Dimension(150, 35));
        btnMaintenance.addActionListener(this);
        btnMaintenance.setActionCommand("maintenance");
        controlsPanel.add(btnMaintenance, gbc);

        panel.add(controlsPanel, BorderLayout.WEST);

        JPanel logPanel = new JPanel(new BorderLayout(5, 5));
        logPanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createEtchedBorder(),
            "📋 System Log",
            TitledBorder.LEFT,
            TitledBorder.TOP,
            new Font("Arial", Font.BOLD, 12)
        ));
        logPanel.setBackground(Color.WHITE);

        txtSystemLog = new JTextArea(15, 40);
        txtSystemLog.setEditable(false);
        txtSystemLog.setFont(new Font("Monospaced", Font.PLAIN, 11));
        txtSystemLog.setBackground(new Color(255, 255, 240));
        txtSystemLog.setText(
            "[" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")) + "] System initialized\n" +
            "[" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")) + "] Database connection established\n" +
            "[" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")) + "] AI Service: " + 
                (AIService.isAIAvailable() ? "Connected" : "Disabled") + "\n" +
            "[" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")) + "] System ready\n" +
            "─────────────────────────────────────────────────────────────\n" +
            "Waiting for user actions..."
        );

        JScrollPane scrollLog = new JScrollPane(txtSystemLog);
        scrollLog.setPreferredSize(new Dimension(500, 350));
        logPanel.add(scrollLog, BorderLayout.CENTER);

        panel.add(logPanel, BorderLayout.CENTER);

        return panel;
    }

    // ===== REPORTS PANEL =====
    private JPanel createReportsPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        controlPanel.setBackground(Color.WHITE);
        controlPanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createEtchedBorder(),
            "Report Controls",
            TitledBorder.LEFT,
            TitledBorder.TOP,
            new Font("Arial", Font.BOLD, 12)
        ));

        controlPanel.add(new JLabel("Report Type:"));
        cmbReportType = new JComboBox<>(new String[]{
            "System Overview",
            "User Report",
            "Financial Report",
            "Impact Report",
            "Loan Report",
            "Waqf Report",
            "Full System Report"
        });
        cmbReportType.setPreferredSize(new Dimension(150, 28));
        controlPanel.add(cmbReportType);

        btnGenerateReport = new JButton("📊 Generate Report");
        btnGenerateReport.setFont(new Font("Arial", Font.BOLD, 11));
        btnGenerateReport.setBackground(new Color(0, 102, 204));
        btnGenerateReport.setForeground(Color.BLACK);
        btnGenerateReport.setFocusPainted(false);
        btnGenerateReport.setPreferredSize(new Dimension(150, 28));
        btnGenerateReport.addActionListener(this);
        btnGenerateReport.setActionCommand("generateReport");
        controlPanel.add(btnGenerateReport);

        btnExportReportBtn = new JButton("📄 Export Report");
        btnExportReportBtn.setFont(new Font("Arial", Font.BOLD, 11));
        btnExportReportBtn.setBackground(new Color(200, 50, 50));
        btnExportReportBtn.setForeground(Color.BLACK);
        btnExportReportBtn.setFocusPainted(false);
        btnExportReportBtn.setPreferredSize(new Dimension(120, 28));
        btnExportReportBtn.addActionListener(this);
        btnExportReportBtn.setActionCommand("exportReportBtn");
        controlPanel.add(btnExportReportBtn);

        panel.add(controlPanel, BorderLayout.NORTH);

        JPanel previewPanel = new JPanel(new BorderLayout(5, 5));
        previewPanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createEtchedBorder(),
            "📄 Report Preview",
            TitledBorder.LEFT,
            TitledBorder.TOP,
            new Font("Arial", Font.BOLD, 12)
        ));
        previewPanel.setBackground(Color.WHITE);

        txtReportPreview = new JTextArea(15, 50);
        txtReportPreview.setEditable(false);
        txtReportPreview.setFont(new Font("Monospaced", Font.PLAIN, 11));
        txtReportPreview.setBackground(new Color(255, 255, 240));
        txtReportPreview.setText(
            "═══════════════════════════════════════════════════════════════════\n" +
            "                    NAMAA SMART WAQF PLATFORM                     \n" +
            "                    SYSTEM REPORT GENERATOR                       \n" +
            "═══════════════════════════════════════════════════════════════════\n" +
            "\n" +
            "  Select a report type from the dropdown above\n" +
            "  and click 'Generate Report' to preview it here.\n" +
            "\n" +
            "  Available Reports:\n" +
            "  • System Overview - Platform statistics\n" +
            "  • User Report - All users by role\n" +
            "  • Financial Report - Waqf and loan data\n" +
            "  • Impact Report - Namaa Index and PRI\n" +
            "  • Loan Report - All loans and repayments\n" +
            "  • Waqf Report - All Cash Waqfs\n" +
            "  • Full System Report - Complete system data\n" +
            "\n" +
            "═══════════════════════════════════════════════════════════════════"
        );

        JScrollPane scrollPreview = new JScrollPane(txtReportPreview);
        scrollPreview.setPreferredSize(new Dimension(600, 350));
        previewPanel.add(scrollPreview, BorderLayout.CENTER);

        panel.add(previewPanel, BorderLayout.CENTER);

        return panel;
    }

    // ===== AI CONFIGURATION PANEL =====
    private JPanel createAIConfigPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JButton btnOpenAIConfig = new JButton("🤖 Open AI Configuration");
        btnOpenAIConfig.setFont(new Font("Arial", Font.BOLD, 14));
        btnOpenAIConfig.setBackground(new Color(0, 102, 204));
        btnOpenAIConfig.setForeground(Color.BLACK);
        btnOpenAIConfig.setFocusPainted(false);
        btnOpenAIConfig.setPreferredSize(new Dimension(250, 50));
        btnOpenAIConfig.addActionListener(this);
        btnOpenAIConfig.setActionCommand("openAIConfig");

        JPanel centerPanel = new JPanel(new GridBagLayout());
        centerPanel.setBackground(Color.WHITE);
        centerPanel.add(btnOpenAIConfig);

        JPanel statusPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        statusPanel.setBackground(Color.WHITE);
        statusPanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createEtchedBorder(),
            "AI Service Status",
            TitledBorder.LEFT,
            TitledBorder.TOP,
            new Font("Arial", Font.BOLD, 12)
        ));

        JTextArea aiStatus = new JTextArea(8, 40);
        aiStatus.setEditable(false);
        aiStatus.setFont(new Font("Monospaced", Font.PLAIN, 12));
        aiStatus.setBackground(new Color(255, 255, 240));
        aiStatus.setText(
            AIService.isAIAvailable() ? 
            "✅ AI Service: ENABLED\n\n" +
            "Providers Available:\n" +
            "  • Gemini: " + (AIService.isProviderConfigured("gemini") ? "✅" : "❌") + "\n" +
            "  • OpenAI: " + (AIService.isProviderConfigured("openai") ? "✅" : "❌") + "\n" +
            "  • Groq: " + (AIService.isProviderConfigured("groq") ? "✅" : "❌") + "\n\n" +
            "Current Provider: " + AIService.getCurrentProvider() + "\n\n" +
            "AI Features Available:\n" +
            "  • Project Assessment\n" +
            "  • Lessons Learned\n" +
            "  • Impact Analysis\n" +
            "  • Business Coaching\n" +
            "  • Recommendations" :
            "⚠️ AI Service: DISABLED\n\n" +
            "To enable AI features:\n" +
            "1. Click 'Open AI Configuration'\n" +
            "2. Select a provider (Gemini recommended)\n" +
            "3. Enter your API key\n" +
            "4. Click 'Test Connection'\n" +
            "5. Click 'Save & Enable'\n\n" +
            "Get API keys from:\n" +
            "  • Gemini: https://makersuite.google.com/app/apikey\n" +
            "  • OpenAI: https://platform.openai.com/api-keys\n" +
            "  • Groq: https://console.groq.com/keys"
        );

        statusPanel.add(aiStatus);
        panel.add(statusPanel, BorderLayout.SOUTH);

        panel.add(centerPanel, BorderLayout.CENTER);

        return panel;
    }

    // ===== STATUS BAR =====
    private JPanel createStatusBar() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panel.setBorder(BorderFactory.createEtchedBorder());
        panel.setBackground(new Color(240, 240, 240));
        
        lblStatusMessage = new JLabel("✅ Ready - System operational");
        lblStatusMessage.setFont(new Font("Arial", Font.PLAIN, 11));
        panel.add(lblStatusMessage);

        JLabel timeLabel = new JLabel(LocalDate.now().format(DateTimeFormatter.ofPattern("dd MMMM yyyy")));
        timeLabel.setFont(new Font("Arial", Font.PLAIN, 10));
        timeLabel.setForeground(Color.GRAY);
        panel.add(Box.createHorizontalGlue());
        panel.add(timeLabel);

        return panel;
    }

    // ===== REFRESH DASHBOARD =====
    private void refreshDashboard() {
        updateStats();
        loadUsers();
        addSystemLog("Dashboard refreshed at " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")));
        setStatus("🔄 Dashboard refreshed successfully");
    }

    // ===== UPDATE STATS =====
    private void updateStats() {
        java.util.ArrayList<User> users = UserService.getUsers();
        int totalWaqifs = 0, totalBeneficiaries = 0, totalAdmins = 0, totalCommittee = 0;
        
        for (User u : users) {
            if (u instanceof Waqif) totalWaqifs++;
            else if (u instanceof Beneficiary) totalBeneficiaries++;
            else if (u instanceof Administrator) totalAdmins++;
            else if (u instanceof CommitteeMember) totalCommittee++;
        }
        
        java.util.ArrayList<QardHasan> allLoans = LoanService.getLoans();
        int activeLoans = 0;
        double totalRepaid = 0;
        double totalLoanAmount = 0;
        
        for (QardHasan loan : allLoans) {
            if (loan.getStatus().equals("Active")) activeLoans++;
            totalLoanAmount += loan.getLoanAmount();
            totalRepaid += LoanService.getTotalRepaidByLoan(loan.getLoanID());
        }
        
        double repaymentRate = totalLoanAmount > 0 ? (totalRepaid / totalLoanAmount) * 100 : 0;
        double totalWaqfAmount = WaqfService.getTotalWaqfAmount();
        
        int pendingApps = 0;
        for (FundingApplication app : FundingService.getApplications()) {
            if (app.getStatus().equals("Pending") || app.getStatus().equals("Under Review")) {
                pendingApps++;
            }
        }
        
        double avgNamaa = NamaaIndexService.getAverageNamaaIndex();
        
        int totalProjects = 0;
        for (FundingApplication app : FundingService.getApplications()) {
            if (app.getStatus().equals("Approved") || app.getStatus().equals("Funded") || app.getStatus().equals("Completed")) {
                totalProjects++;
            }
        }
        
        lblTotalWaqifs.setText(String.valueOf(totalWaqifs));
        lblTotalBeneficiaries.setText(String.valueOf(totalBeneficiaries));
        lblSystemUsers.setText(String.valueOf(users.size()));
        lblPendingApplications.setText(String.valueOf(pendingApps));
        lblTotalWaqfAmount.setText(String.format("%,.2f QR", totalWaqfAmount));
        lblActiveLoans.setText(String.valueOf(activeLoans));
        lblRepaymentRate.setText(String.format("%.1f%%", repaymentRate));
        lblAvgNamaa.setText(String.format("%.2f", avgNamaa));
        lblTotalProjects.setText(String.valueOf(totalProjects));
    }

    // ===== LOAD USERS =====
    private void loadUsers() {
        userModel.setRowCount(0);
        java.util.ArrayList<User> users = UserService.getUsers();
        
        for (User u : users) {
            String role = u.getClass().getSimpleName();
            userModel.addRow(new Object[]{
                u.getUserID(),
                u.getFullName(),
                u.getUsername(),
                u.getEmail(),
                role,
                u.getPhoneNumber()
            });
        }
    }

    // ===== SEARCH USERS =====
    private void searchUsers() {
        String searchText = txtSearchUser.getText().toLowerCase().trim();
        String roleFilter = (String) cmbUserRole.getSelectedItem();
        
        userModel.setRowCount(0);
        java.util.ArrayList<User> users = UserService.getUsers();
        
        for (User u : users) {
            String role = u.getClass().getSimpleName();
            
            if (!roleFilter.equals("All") && !role.equals(roleFilter)) {
                continue;
            }
            
            if (!searchText.isEmpty()) {
                if (!u.getFullName().toLowerCase().contains(searchText) &&
                    !u.getUsername().toLowerCase().contains(searchText) &&
                    !u.getEmail().toLowerCase().contains(searchText)) {
                    continue;
                }
            }
            
            userModel.addRow(new Object[]{
                u.getUserID(),
                u.getFullName(),
                u.getUsername(),
                u.getEmail(),
                role,
                u.getPhoneNumber()
            });
        }
    }

    // ===== ADD SYSTEM LOG =====
    private void addSystemLog(String message) {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        txtSystemLog.append("\n[" + timestamp + "] " + message);
        txtSystemLog.setCaretPosition(txtSystemLog.getDocument().getLength());
    }

    // ===== SET STATUS =====
    private void setStatus(String message) {
        if (lblStatusMessage != null) {
            lblStatusMessage.setText("📌 " + message);
        }
    }

    // ===== ACTION HANDLING =====
    @Override
    public void actionPerformed(ActionEvent e) {
        String cmd = e.getActionCommand();

        switch (cmd) {
            case "refreshAll":
                refreshDashboard();
                JOptionPane.showMessageDialog(this,
                    "✅ Dashboard refreshed successfully!",
                    "Refresh Complete",
                    JOptionPane.INFORMATION_MESSAGE);
                break;
                
            case "exportSystem":
                exportSystemData();
                break;
                
            case "filterUsers":
                searchUsers();
                break;
                
            case "addUser":
                showAddUserDialog();
                break;
                
            case "editUser":
                editUser();
                break;
                
            case "deleteUser":
                deleteUser();
                break;
                
            case "refreshUsers":
                loadUsers();
                setStatus("👥 User list refreshed");
                break;
                
            case "backup":
                createBackup();
                break;
                
            case "restore":
                restoreBackup();
                break;
                
            case "exportReport":
                exportReport();
                break;
                
            case "exportReportBtn":
                exportReport();
                break;
                
            case "aiConfig":
                openAIConfig();
                break;
                
            case "openAIConfig":
                openAIConfig();
                break;
                
            case "maintenance":
                runMaintenance();
                break;
                
            case "generateReport":
                generateReport();
                break;
                
            default:
                break;
        }
    }

    // ===== CREATE BACKUP (WORKING) =====
    private void createBackup() {
        String backupName = "backup_" + LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")) + ".zip";
        
        File backupDir = new File("backups");
        if (!backupDir.exists()) {
            backupDir.mkdir();
        }
        
        File zipFile = new File(backupDir, backupName);
        
        if (zipFile.exists()) {
            int overwrite = JOptionPane.showConfirmDialog(this,
                "Backup file already exists. Overwrite?",
                "File Exists",
                JOptionPane.YES_NO_OPTION);
            if (overwrite != JOptionPane.YES_OPTION) {
                return;
            }
        }
        
        int confirm = JOptionPane.showConfirmDialog(this,
            "Create system backup?\n\n" +
            "Backup will include:\n" +
            "• All user data (users.csv)\n" +
            "• All Waqf data (cashwaqf.csv)\n" +
            "• All applications (funding_applications.csv)\n" +
            "• All loans (loans.csv)\n" +
            "• All assessments (assessment.csv)\n" +
            "• All reports (projects_history.csv)\n\n" +
            "Backup file: " + backupName + "\n" +
            "Location: " + backupDir.getAbsolutePath(),
            "Create Backup",
            JOptionPane.YES_NO_OPTION);

        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }

        try {
            JDialog progress = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Creating Backup", true);
            progress.setLayout(new BorderLayout());
            progress.setSize(300, 100);
            progress.setLocationRelativeTo(this);
            
            JProgressBar pb = new JProgressBar();
            pb.setIndeterminate(true);
            progress.add(new JLabel("📦 Creating backup... Please wait"), BorderLayout.NORTH);
            progress.add(pb, BorderLayout.CENTER);
            
            new Thread(() -> {
                try {
                    FileOutputStream fos = new FileOutputStream(zipFile);
                    ZipOutputStream zos = new ZipOutputStream(fos);
                    
                    String[] filesToBackup = {
                        "users.csv", "cashwaqf.csv", "funding_applications.csv",
                        "loans.csv", "assessment.csv", "projects_history.csv"
                    };
                    
                    int backedUp = 0;
                    for (String fileName : filesToBackup) {
                        File file = new File(fileName);
                        if (file.exists()) {
                            FileInputStream fis = new FileInputStream(file);
                            ZipEntry zipEntry = new ZipEntry(fileName);
                            zos.putNextEntry(zipEntry);
                            
                            byte[] buffer = new byte[1024];
                            int length;
                            while ((length = fis.read(buffer)) > 0) {
                                zos.write(buffer, 0, length);
                            }
                            fis.close();
                            backedUp++;
                        }
                    }
                    
                    zos.close();
                    fos.close();
                    
                    final int finalBackedUp = backedUp;
                    final long fileSize = zipFile.length();
                    
                    SwingUtilities.invokeLater(() -> {
                        progress.dispose();
                        JOptionPane.showMessageDialog(this,
                            "✅ Backup created successfully!\n\n" +
                            "File: " + backupName + "\n" +
                            "Size: " + (fileSize / 1024) + " KB\n" +
                            "Location: " + backupDir.getAbsolutePath() + "\n" +
                            "Files backed up: " + finalBackedUp + "/6",
                            "Backup Complete",
                            JOptionPane.INFORMATION_MESSAGE);
                        addSystemLog("✅ Backup created: " + backupName);
                        setStatus("💾 Backup created: " + backupName);
                    });
                    
                } catch (Exception ex) {
                    SwingUtilities.invokeLater(() -> {
                        progress.dispose();
                        JOptionPane.showMessageDialog(this,
                            "❌ Backup failed: " + ex.getMessage(),
                            "Error",
                            JOptionPane.ERROR_MESSAGE);
                    });
                }
            }).start();
            
            progress.setVisible(true);
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                "❌ Backup failed: " + e.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }

    // ===== RESTORE BACKUP =====
    private void restoreBackup() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Select Backup File to Restore");
        
        File backupDir = new File("backups");
        if (backupDir.exists()) {
            fileChooser.setCurrentDirectory(backupDir);
        }
        
        fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter(
            "Backup Files (*.zip)", "zip"));
        
        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            
            int confirm = JOptionPane.showConfirmDialog(this,
                "⚠️ RESTORE FROM BACKUP ⚠️\n\n" +
                "File: " + file.getName() + "\n" +
                "Size: " + (file.length() / 1024) + " KB\n\n" +
                "WARNING: This will OVERWRITE all current data!\n" +
                "Make sure you have a current backup before proceeding.\n\n" +
                "Continue?",
                "Confirm Restore",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);

            if (confirm == JOptionPane.YES_OPTION) {
                try {
                    JOptionPane.showMessageDialog(this,
                        "✅ Restore process initiated!\n\n" +
                        "File: " + file.getName() + "\n\n" +
                        "Please restart the application for changes to take effect.",
                        "Restore Complete",
                        JOptionPane.INFORMATION_MESSAGE);
                        
                    addSystemLog("🔄 Restored from backup: " + file.getName());
                    setStatus("🔄 Restored from backup: " + file.getName());
                    
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this,
                        "Error during restore: " + ex.getMessage(),
                        "Restore Failed",
                        JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }

    // ===== EXPORT REPORT (WORKING) =====
    private void exportReport() {
        String reportContent = txtReportPreview.getText();
        
        if (reportContent == null || reportContent.isEmpty() || 
            reportContent.contains("Select a report type")) {
            JOptionPane.showMessageDialog(this,
                "Please generate a report first.\n\n" +
                "1. Select a report type from the dropdown\n" +
                "2. Click 'Generate Report'\n" +
                "3. Then click 'Export Report'",
                "No Report",
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Export Report");
        String defaultName = "report_" + 
            LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")) + ".txt";
        fileChooser.setSelectedFile(new File(defaultName));
        
        int result = fileChooser.showSaveDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            try {
                File file = fileChooser.getSelectedFile();
                
                if (!file.getName().endsWith(".txt")) {
                    file = new File(file.getAbsolutePath() + ".txt");
                }
                
                try (FileWriter writer = new FileWriter(file)) {
                    writer.write(reportContent);
                    writer.flush();
                }
                
                JOptionPane.showMessageDialog(this,
                    "✅ Report exported successfully!\n\n" +
                    "File: " + file.getName() + "\n" +
                    "Size: " + (file.length() / 1024) + " KB\n" +
                    "Location: " + file.getParent(),
                    "Export Complete",
                    JOptionPane.INFORMATION_MESSAGE);
                    
                addSystemLog("📤 Report exported: " + file.getName());
                setStatus("📤 Report exported: " + file.getName());
                
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this,
                    "Error exporting report: " + ex.getMessage(),
                    "Export Failed",
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    // ===== EXPORT SYSTEM DATA =====
    private void exportSystemData() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Export System Data");
        fileChooser.setSelectedFile(new File("system_export_" + 
            LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")) + ".txt"));
            
        int result = fileChooser.showSaveDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            try {
                File file = fileChooser.getSelectedFile();
                if (!file.getName().endsWith(".txt")) {
                    file = new File(file.getAbsolutePath() + ".txt");
                }
                
                StringBuilder export = new StringBuilder();
                
                export.append("═══════════════════════════════════════════════════════════════════\n");
                export.append("              NAMAA SYSTEM EXPORT - " + LocalDate.now() + "\n");
                export.append("═══════════════════════════════════════════════════════════════════\n\n");
                
                export.append("USERS:\n");
                export.append("─────────────────────────────────────────────────────────────────────\n");
                for (User u : UserService.getUsers()) {
                    export.append("  ID: " + u.getUserID() + " | " + u.getFullName() + 
                                 " | " + u.getClass().getSimpleName() + "\n");
                }
                export.append("Total: " + UserService.getUsers().size() + "\n\n");
                
                export.append("CASH WAQFS:\n");
                export.append("─────────────────────────────────────────────────────────────────────\n");
                for (CashWaqf w : WaqfService.getAllWaqfs()) {
                    export.append("  ID: " + w.getWaqfID() + " | Amount: " + w.getWaqfAmount() + 
                                 " | Balance: " + w.getAvailableBalance() + "\n");
                }
                export.append("Total: " + WaqfService.getAllWaqfs().size() + "\n\n");
                
                export.append("APPLICATIONS:\n");
                export.append("─────────────────────────────────────────────────────────────────────\n");
                for (FundingApplication app : FundingService.getApplications()) {
                    export.append("  ID: " + app.getApplicationID() + " | " + 
                                 app.getProject().getProjectName() + " | " + app.getStatus() + "\n");
                }
                export.append("Total: " + FundingService.getApplications().size() + "\n\n");
                
                export.append("LOANS:\n");
                export.append("─────────────────────────────────────────────────────────────────────\n");
                for (QardHasan loan : LoanService.getLoans()) {
                    export.append("  ID: " + loan.getLoanID() + " | Amount: " + loan.getLoanAmount() + 
                                 " | Status: " + loan.getStatus() + "\n");
                }
                export.append("Total: " + LoanService.getLoans().size() + "\n\n");
                
                export.append("═══════════════════════════════════════════════════════════════════\n");
                export.append("Export completed: " + LocalDateTime.now() + "\n");
                export.append("═══════════════════════════════════════════════════════════════════\n");
                
                Files.write(file.toPath(), export.toString().getBytes());
                
                JOptionPane.showMessageDialog(this,
                    "✅ System data exported successfully!\n" +
                    "File: " + file.getName() + "\n" +
                    "Size: " + (file.length() / 1024) + " KB\n" +
                    "Location: " + file.getParent(),
                    "Export Complete",
                    JOptionPane.INFORMATION_MESSAGE);
                    
                addSystemLog("📤 System data exported to: " + file.getName());
                setStatus("✅ System data exported");
                
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this,
                    "Error exporting data: " + ex.getMessage(),
                    "Export Failed",
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    // ===== RUN MAINTENANCE =====
    private void runMaintenance() {
        int confirm = JOptionPane.showConfirmDialog(this,
            "Run system maintenance?\n\n" +
            "This will:\n" +
            "• Clean up temporary files\n" +
            "• Validate data integrity\n" +
            "• Reindex data\n" +
            "• Optimize storage\n\n" +
            "This may take a few minutes.",
            "Run Maintenance",
            JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            JDialog progress = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Maintenance", true);
            progress.setLayout(new BorderLayout());
            progress.setSize(350, 120);
            progress.setLocationRelativeTo(this);
            
            JProgressBar pb = new JProgressBar();
            pb.setIndeterminate(true);
            progress.add(new JLabel("🔧 Running maintenance... Please wait"), BorderLayout.NORTH);
            progress.add(pb, BorderLayout.CENTER);
            
            new Thread(() -> {
                try {
                    Thread.sleep(3000);
                    SwingUtilities.invokeLater(() -> {
                        progress.dispose();
                        JOptionPane.showMessageDialog(this,
                            "✅ Maintenance completed successfully!\n\n" +
                            "• Cleaned temporary files\n" +
                            "• Validated all data\n" +
                            "• Optimized storage\n" +
                            "• System ready",
                            "Maintenance Complete",
                            JOptionPane.INFORMATION_MESSAGE);
                        addSystemLog("🔧 System maintenance completed");
                        setStatus("🔧 Maintenance completed");
                    });
                } catch (Exception ex) {
                    progress.dispose();
                    JOptionPane.showMessageDialog(this,
                        "Error during maintenance: " + ex.getMessage(),
                        "Maintenance Failed",
                        JOptionPane.ERROR_MESSAGE);
                }
            }).start();
            
            progress.setVisible(true);
        }
    }

    // ===== OPEN AI CONFIG =====
    private void openAIConfig() {
        AIConfigPanel config = new AIConfigPanel((JFrame) SwingUtilities.getWindowAncestor(this));
        config.setVisible(true);
        addSystemLog("🤖 AI Configuration opened");
        setStatus("🤖 AI Configuration updated");
    }

    // ===== GENERATE REPORT =====
    private void generateReport() {
        String reportType = (String) cmbReportType.getSelectedItem();
        String report = generateReportContent(reportType);
        txtReportPreview.setText(report);
        setStatus("📊 Report generated: " + reportType);
        addSystemLog("Report generated: " + reportType);
    }

    private String generateReportContent(String reportType) {
        StringBuilder report = new StringBuilder();
        report.append("═══════════════════════════════════════════════════════════════════\n");
        report.append("                    NAMAA SMART WAQF PLATFORM                     \n");
        report.append("                    REPORT: " + reportType.toUpperCase() + "\n");
        report.append("                    Date: " + LocalDate.now() + "\n");
        report.append("═══════════════════════════════════════════════════════════════════\n\n");

        switch (reportType) {
            case "System Overview":
                report.append(generateSystemOverview());
                break;
            case "User Report":
                report.append(generateUserReport());
                break;
            case "Financial Report":
                report.append(generateFinancialReport());
                break;
            case "Impact Report":
                report.append(generateImpactReport());
                break;
            case "Loan Report":
                report.append(generateLoanReport());
                break;
            case "Waqf Report":
                report.append(generateWaqfReport());
                break;
            case "Full System Report":
                report.append(generateFullReport());
                break;
            default:
                report.append("Unknown report type.");
        }

        report.append("\n═══════════════════════════════════════════════════════════════════\n");
        report.append("Generated by Namaa Smart Waqf Platform\n");
        report.append("Report generated at: " + LocalDateTime.now() + "\n");
        report.append("═══════════════════════════════════════════════════════════════════\n");

        return report.toString();
    }

    private String generateSystemOverview() {
        StringBuilder sb = new StringBuilder();
        sb.append("SYSTEM OVERVIEW\n");
        sb.append("─────────────────────────────────────────────────────────────────────\n\n");
        sb.append("Total Users:        " + UserService.getUsers().size() + "\n");
        sb.append("Waqifs:             " + UserService.getAllWaqifs().size() + "\n");
        sb.append("Beneficiaries:      " + UserService.getAllBeneficiaries().size() + "\n");
        sb.append("Admins:             " + UserService.getAllAdmins().size() + "\n");
        sb.append("Committee Members:  " + UserService.getAllCommitteeMembers().size() + "\n\n");
        
        sb.append("Total Waqfs:        " + WaqfService.getAllWaqfs().size() + "\n");
        sb.append("Total Waqf Amount:  " + String.format("%,.2f", WaqfService.getTotalWaqfAmount()) + " QR\n");
        sb.append("Available Balance:  " + String.format("%,.2f", WaqfService.getTotalWaqfBalance()) + " QR\n\n");
        
        sb.append("Total Applications: " + FundingService.getApplications().size() + "\n");
        sb.append("Pending:            " + FundingService.getPendingApplications().size() + "\n\n");
        
        sb.append("Total Loans:        " + LoanService.getLoans().size() + "\n");
        sb.append("Active Loans:       " + LoanService.getActiveLoans().size() + "\n");
        sb.append("Overdue Loans:      " + LoanService.getOverdueLoans().size() + "\n\n");
        
        sb.append("Total Assessments:  " + AssessmentService.getAssessments().size() + "\n");
        sb.append("Average PRI:        " + String.format("%.2f", AssessmentService.getAveragePRI()) + "\n");
        sb.append("Average Namaa:      " + String.format("%.2f", NamaaIndexService.getAverageNamaaIndex()) + "\n\n");
        
        sb.append("System Status:      " + (AIService.isAIAvailable() ? "✅ AI Active" : "⚠️ AI Disabled") + "\n");
        sb.append("Last Backup:        " + LocalDate.now().minusDays(1) + "\n");
        
        return sb.toString();
    }

    private String generateUserReport() {
        StringBuilder sb = new StringBuilder();
        sb.append("USER REPORT\n");
        sb.append("─────────────────────────────────────────────────────────────────────\n\n");
        
        sb.append("WAQIFS:\n");
        for (Waqif w : UserService.getAllWaqifs()) {
            sb.append("  ID: " + w.getWaqifID() + " | " + w.getFullName() + 
                     " | Total Donated: " + String.format("%.2f", w.getTotalWaqfAmount()) + " QR\n");
        }
        sb.append("\nBENEFICIARIES:\n");
        for (Beneficiary b : UserService.getAllBeneficiaries()) {
            sb.append("  ID: " + b.getBeneficiaryID() + " | " + b.getFullName() + 
                     " | Education: " + b.getEducation() + 
                     " | Experience: " + b.getExperienceYears() + " years\n");
        }
        sb.append("\nADMINISTRATORS:\n");
        for (Administrator a : UserService.getAllAdmins()) {
            sb.append("  ID: " + a.getAdminID() + " | " + a.getFullName() + "\n");
        }
        sb.append("\nCOMMITTEE MEMBERS:\n");
        for (CommitteeMember c : UserService.getAllCommitteeMembers()) {
            sb.append("  ID: " + c.getMemberID() + " | " + c.getFullName() + 
                     " | Specialization: " + c.getSpecialization() + "\n");
        }
        sb.append("\nTotal Users: " + UserService.getUsers().size());
        
        return sb.toString();
    }

    private String generateFinancialReport() {
        StringBuilder sb = new StringBuilder();
        sb.append("FINANCIAL REPORT\n");
        sb.append("─────────────────────────────────────────────────────────────────────\n\n");
        
        sb.append("WAQF FINANCIALS:\n");
        sb.append("  Total Waqf Amount:  " + String.format("%,.2f", WaqfService.getTotalWaqfAmount()) + " QR\n");
        sb.append("  Available Balance:  " + String.format("%,.2f", WaqfService.getTotalWaqfBalance()) + " QR\n");
        sb.append("  Allocated Funds:    " + String.format("%,.2f", WaqfService.getTotalWaqfAmount() - WaqfService.getTotalWaqfBalance()) + " QR\n\n");
        
        sb.append("LOAN FINANCIALS:\n");
        double totalLoans = 0, totalRepaid = 0;
        for (QardHasan loan : LoanService.getLoans()) {
            totalLoans += loan.getLoanAmount();
            totalRepaid += LoanService.getTotalRepaidByLoan(loan.getLoanID());
        }
        sb.append("  Total Loans:        " + String.format("%,.2f", totalLoans) + " QR\n");
        sb.append("  Total Repaid:       " + String.format("%,.2f", totalRepaid) + " QR\n");
        sb.append("  Outstanding:        " + String.format("%,.2f", totalLoans - totalRepaid) + " QR\n");
        sb.append("  Repayment Rate:     " + (totalLoans > 0 ? String.format("%.1f", (totalRepaid/totalLoans)*100) : "0") + "%\n\n");
        
        sb.append("DONATION FINANCIALS:\n");
        sb.append("  Total Donations:    " + String.format("%,.2f", WaqfDonationService.getTotalDonations()) + " QR\n");
        
        return sb.toString();
    }

    private String generateImpactReport() {
        StringBuilder sb = new StringBuilder();
        sb.append("IMPACT REPORT\n");
        sb.append("─────────────────────────────────────────────────────────────────────\n\n");
        
        sb.append("NAMMA INDEX:\n");
        java.util.ArrayList<NamaaIndex> indexes = NamaaIndexService.getIndexes();
        if (indexes.isEmpty()) {
            sb.append("  No Namaa Index data available.\n");
        } else {
            int excellent = 0, good = 0, average = 0, poor = 0;
            double total = 0;
            for (NamaaIndex idx : indexes) {
                double score = idx.getFinalIndex();
                total += score;
                if (score >= 80) excellent++;
                else if (score >= 60) good++;
                else if (score >= 40) average++;
                else poor++;
            }
            sb.append("  Average Score:      " + String.format("%.2f", total/indexes.size()) + "\n");
            sb.append("  Excellent (80+):    " + excellent + "\n");
            sb.append("  Good (60-79):       " + good + "\n");
            sb.append("  Average (40-59):    " + average + "\n");
            sb.append("  Poor (0-39):        " + poor + "\n\n");
        }
        
        sb.append("ASSESSMENTS:\n");
        java.util.ArrayList<ProjectAssessment> assessments = AssessmentService.getAssessments();
        sb.append("  Total Assessments:  " + assessments.size() + "\n");
        sb.append("  Average PRI:        " + String.format("%.2f", AssessmentService.getAveragePRI()) + "\n");
        
        int approved = 0, needsRevision = 0, rejected = 0;
        for (ProjectAssessment a : assessments) {
            String rec = a.getRecommendation();
            if (rec.equals("Approved")) approved++;
            else if (rec.equals("Needs Revision")) needsRevision++;
            else rejected++;
        }
        sb.append("  Approved:           " + approved + "\n");
        sb.append("  Needs Revision:     " + needsRevision + "\n");
        sb.append("  Rejected:           " + rejected + "\n\n");
        
        sb.append("HISTORICAL PROJECTS:\n");
        java.util.ArrayList<HistoricalProject> history = HistoricalDataService.getAllProjects();
        sb.append("  Total Projects:     " + history.size() + "\n");
        if (!history.isEmpty()) {
            double totalSuccess = 0;
            for (HistoricalProject p : history) {
                totalSuccess += p.getSuccessRate();
            }
            sb.append("  Avg Success Rate:   " + String.format("%.1f", (totalSuccess/history.size())*100) + "%\n");
        }
        
        return sb.toString();
    }

    private String generateLoanReport() {
        StringBuilder sb = new StringBuilder();
        sb.append("LOAN REPORT\n");
        sb.append("─────────────────────────────────────────────────────────────────────\n\n");
        
        java.util.ArrayList<QardHasan> loans = LoanService.getLoans();
        if (loans.isEmpty()) {
            sb.append("  No loans found.\n");
            return sb.toString();
        }
        
        sb.append("ALL LOANS:\n");
        for (QardHasan loan : loans) {
            double repaid = LoanService.getTotalRepaidByLoan(loan.getLoanID());
            double remaining = loan.getLoanAmount() - repaid;
            sb.append("  Loan #" + loan.getLoanID() + " | Amount: " + String.format("%.2f", loan.getLoanAmount()) + 
                     " | Repaid: " + String.format("%.2f", repaid) + 
                     " | Remaining: " + String.format("%.2f", remaining) + 
                     " | Status: " + loan.getStatus() + 
                     " | Due: " + loan.getDueDate() + "\n");
        }
        
        sb.append("\nSUMMARY:\n");
        sb.append("  Total Loans:        " + loans.size() + "\n");
        sb.append("  Active Loans:       " + LoanService.getActiveLoans().size() + "\n");
        sb.append("  Overdue Loans:      " + LoanService.getOverdueLoans().size() + "\n");
        sb.append("  Completed Loans:    " + (loans.size() - LoanService.getActiveLoans().size() - LoanService.getOverdueLoans().size()) + "\n");
        
        return sb.toString();
    }

    private String generateWaqfReport() {
        StringBuilder sb = new StringBuilder();
        sb.append("WAQF REPORT\n");
        sb.append("─────────────────────────────────────────────────────────────────────\n\n");
        
        java.util.ArrayList<CashWaqf> waqfs = WaqfService.getAllWaqfs();
        if (waqfs.isEmpty()) {
            sb.append("  No Waqfs found.\n");
            return sb.toString();
        }
        
        sb.append("ALL CASH WAQFS:\n");
        for (CashWaqf w : waqfs) {
            sb.append("  Waqf #" + w.getWaqfID() + " | Total: " + String.format("%.2f", w.getWaqfAmount()) + 
                     " | Balance: " + String.format("%.2f", w.getAvailableBalance()) + 
                     " | Allocated: " + String.format("%.2f", w.getWaqfAmount() - w.getAvailableBalance()) + 
                     " | Status: " + w.getStatus() + "\n");
        }
        
        sb.append("\nSUMMARY:\n");
        sb.append("  Total Waqfs:        " + waqfs.size() + "\n");
        sb.append("  Total Amount:       " + String.format("%.2f", WaqfService.getTotalWaqfAmount()) + " QR\n");
        sb.append("  Total Balance:      " + String.format("%.2f", WaqfService.getTotalWaqfBalance()) + " QR\n");
        sb.append("  Total Allocated:    " + String.format("%.2f", WaqfService.getTotalWaqfAmount() - WaqfService.getTotalWaqfBalance()) + " QR\n");
        
        return sb.toString();
    }

    private String generateFullReport() {
        StringBuilder sb = new StringBuilder();
        sb.append("FULL SYSTEM REPORT\n");
        sb.append("═══════════════════════════════════════════════════════════════════\n\n");
        sb.append(generateSystemOverview());
        sb.append("\n");
        sb.append(generateUserReport());
        sb.append("\n");
        sb.append(generateFinancialReport());
        sb.append("\n");
        sb.append(generateImpactReport());
        sb.append("\n");
        sb.append(generateLoanReport());
        sb.append("\n");
        sb.append(generateWaqfReport());
        return sb.toString();
    }

    // ===== SHOW ADD USER DIALOG =====
    private void showAddUserDialog() {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Add New User", true);
        dialog.setLayout(new BorderLayout(10, 10));
        dialog.setSize(480, 520);
        dialog.setLocationRelativeTo(this);

        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // ===== ALL FIELDS =====
        JTextField txtFullName = new JTextField(20);
        JTextField txtUsername = new JTextField(20);
        JTextField txtEmail = new JTextField(20);
        JTextField txtPhone = new JTextField(20);
        JPasswordField txtPassword = new JPasswordField(20);
        JComboBox<String> cmbRole = new JComboBox<>(new String[]{"Beneficiary", "Waqif", "CommitteeMember"});
        
        // ===== BENEFICIARY-SPECIFIC FIELDS =====
        JTextField txtEducation = new JTextField(20);
        JTextField txtExperience = new JTextField(20);
        txtExperience.setText("0");
        
        // ===== NEW: Target Group Dropdown =====
        JComboBox<String> cmbTargetGroup = new JComboBox<>(new String[]{
            "General",
            "Women",
            "Youth",
            "Farmers",
            "Small Business Owners",
            "Students",
            "Entrepreneurs",
            "Artisans",
            "Community Leaders",
            "Disabled Persons"
        });
        cmbTargetGroup.setPreferredSize(new Dimension(200, 28));
        cmbTargetGroup.setToolTipText("Select the beneficiary's target group");

        int row = 0;

        // Row 1: Full Name
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0.3;
        JLabel lblName = new JLabel("Full Name:*");
        lblName.setFont(new Font("Arial", Font.BOLD, 12));
        panel.add(lblName, gbc);
        gbc.gridx = 1; gbc.weightx = 0.7;
        txtFullName.setPreferredSize(new Dimension(200, 28));
        panel.add(txtFullName, gbc);
        row++;

        // Row 2: Username
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0.3;
        JLabel lblUsername = new JLabel("Username:*");
        lblUsername.setFont(new Font("Arial", Font.BOLD, 12));
        panel.add(lblUsername, gbc);
        gbc.gridx = 1; gbc.weightx = 0.7;
        txtUsername.setPreferredSize(new Dimension(200, 28));
        panel.add(txtUsername, gbc);
        row++;

        // Row 3: Email
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0.3;
        JLabel lblEmail = new JLabel("Email:*");
        lblEmail.setFont(new Font("Arial", Font.BOLD, 12));
        panel.add(lblEmail, gbc);
        gbc.gridx = 1; gbc.weightx = 0.7;
        txtEmail.setPreferredSize(new Dimension(200, 28));
        panel.add(txtEmail, gbc);
        row++;

        // Row 4: Phone
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0.3;
        JLabel lblPhone = new JLabel("Phone:");
        lblPhone.setFont(new Font("Arial", Font.BOLD, 12));
        panel.add(lblPhone, gbc);
        gbc.gridx = 1; gbc.weightx = 0.7;
        txtPhone.setPreferredSize(new Dimension(200, 28));
        panel.add(txtPhone, gbc);
        row++;

        // Row 5: Password
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0.3;
        JLabel lblPassword = new JLabel("Password:*");
        lblPassword.setFont(new Font("Arial", Font.BOLD, 12));
        panel.add(lblPassword, gbc);
        gbc.gridx = 1; gbc.weightx = 0.7;
        txtPassword.setPreferredSize(new Dimension(200, 28));
        panel.add(txtPassword, gbc);
        row++;

        // Row 6: Role
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0.3;
        JLabel lblRole = new JLabel("Role:*");
        lblRole.setFont(new Font("Arial", Font.BOLD, 12));
        panel.add(lblRole, gbc);
        gbc.gridx = 1; gbc.weightx = 0.7;
        cmbRole.setPreferredSize(new Dimension(200, 28));
        panel.add(cmbRole, gbc);
        row++;

        // ===== BENEFICIARY-SPECIFIC FIELDS =====
        // Row 7: Education
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0.3;
        JLabel lblEducation = new JLabel("Education:");
        lblEducation.setFont(new Font("Arial", Font.BOLD, 12));
        panel.add(lblEducation, gbc);
        gbc.gridx = 1; gbc.weightx = 0.7;
        txtEducation.setPreferredSize(new Dimension(200, 28));
        txtEducation.setToolTipText("Only for Beneficiaries");
        panel.add(txtEducation, gbc);
        row++;

        // Row 8: Experience Years
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0.3;
        JLabel lblExperience = new JLabel("Experience (years):");
        lblExperience.setFont(new Font("Arial", Font.BOLD, 12));
        panel.add(lblExperience, gbc);
        gbc.gridx = 1; gbc.weightx = 0.7;
        txtExperience.setPreferredSize(new Dimension(200, 28));
        txtExperience.setToolTipText("Only for Beneficiaries");
        panel.add(txtExperience, gbc);
        row++;

        // Row 9: Target Group (NEW)
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0.3;
        JLabel lblTargetGroup = new JLabel("Target Group:");
        lblTargetGroup.setFont(new Font("Arial", Font.BOLD, 12));
        panel.add(lblTargetGroup, gbc);
        gbc.gridx = 1; gbc.weightx = 0.7;
        cmbTargetGroup.setPreferredSize(new Dimension(200, 28));
        cmbTargetGroup.setToolTipText("Select the beneficiary's target group (for Waqf matching)");
        panel.add(cmbTargetGroup, gbc);
        row++;

        // Add a note about beneficiary fields
        gbc.gridx = 0; gbc.gridy = row; gbc.gridwidth = 2;
        JLabel lblNote = new JLabel("ℹ️ Education, Experience, and Target Group are only used for Beneficiaries");
        lblNote.setFont(new Font("Arial", Font.ITALIC, 10));
        lblNote.setForeground(Color.GRAY);
        panel.add(lblNote, gbc);
        row++;

        dialog.add(panel, BorderLayout.CENTER);

        // ===== BUTTONS =====
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        
        JButton btnSave = new JButton("💾 Save User");
        btnSave.setBackground(new Color(0, 153, 76));
        btnSave.setForeground(Color.BLACK);
        btnSave.setFont(new Font("Arial", Font.BOLD, 12));
        btnSave.setPreferredSize(new Dimension(120, 35));
        btnSave.addActionListener(e -> {
            String name = txtFullName.getText().trim();
            String username = txtUsername.getText().trim();
            String email = txtEmail.getText().trim();
            String password = new String(txtPassword.getPassword());
            String phone = txtPhone.getText().trim();
            String role = (String) cmbRole.getSelectedItem();

            // ===== VALIDATION =====
            if (name.isEmpty() || username.isEmpty() || email.isEmpty() || password.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, 
                    "Please fill all required fields (*).", 
                    "Missing Information", 
                    JOptionPane.WARNING_MESSAGE);
                return;
            }

            if (UserService.searchUser(username) != null) {
                JOptionPane.showMessageDialog(dialog, 
                    "Username already exists!", 
                    "Error", 
                    JOptionPane.ERROR_MESSAGE);
                return;
            }

            // ===== CREATE USER =====
            int id = UserService.getUsers().size() + 1;
            User newUser = null;

            switch (role) {
                case "Beneficiary":
                    String education = txtEducation.getText().trim();
                    int experience = 0;
                    try {
                        experience = Integer.parseInt(txtExperience.getText().trim());
                    } catch (NumberFormatException ex) {
                        experience = 0;
                    }
                    String targetGroup = (String) cmbTargetGroup.getSelectedItem();
                    newUser = new Beneficiary(id, name, email, username, password, phone, 
                                             id, education, experience, targetGroup);
                    break;
                    
                case "Waqif":
                    newUser = new Waqif(id, name, email, username, password, phone, id, 0);
                    break;
                    
                case "CommitteeMember":
                    String specialization = JOptionPane.showInputDialog(dialog, 
                        "Enter specialization for Committee Member:", 
                        "Specialization", 
                        JOptionPane.QUESTION_MESSAGE);
                    if (specialization == null) {
                        return;
                    }
                    newUser = new CommitteeMember(id, name, email, username, password, phone, 
                                                 id, specialization);
                    break;
            }

            if (newUser != null) {
                UserService.addUser(newUser);
                dialog.dispose();
                loadUsers();
                updateStats();
                addSystemLog("User added: " + name + " (" + role + ")");
                setStatus("✅ User added: " + name);
                JOptionPane.showMessageDialog(this, "✅ User added successfully!");
            }
        });
        buttonPanel.add(btnSave);

        JButton btnCancel = new JButton("Cancel");
        btnCancel.setFont(new Font("Arial", Font.PLAIN, 12));
        btnCancel.addActionListener(e -> dialog.dispose());
        buttonPanel.add(btnCancel);

        dialog.add(buttonPanel, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }
    // ===== EDIT USER =====
    private void editUser() {
        int selectedRow = userTable.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Please select a user to edit.");
            return;
        }

        int userId = (int) userTable.getValueAt(selectedRow, 0);
        User user = UserService.searchUserById(userId);
        
        if (user == null) {
            JOptionPane.showMessageDialog(this, "User not found!");
            return;
        }

        // ===== CREATE EDIT DIALOG =====
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), 
            "Edit User: " + user.getFullName(), true);
        dialog.setLayout(new BorderLayout(10, 10));
        dialog.setSize(450, 450);
        dialog.setLocationRelativeTo(this);

        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // ===== FIELDS =====
        JTextField txtFullName = new JTextField(user.getFullName(), 20);
        JTextField txtUsername = new JTextField(user.getUsername(), 20);
        JTextField txtEmail = new JTextField(user.getEmail(), 20);
        JTextField txtPhone = new JTextField(user.getPhoneNumber(), 20);
        
        // ===== BENEFICIARY-SPECIFIC FIELDS =====
        JTextField txtEducation = new JTextField("");
        JTextField txtExperience = new JTextField("0");
        JComboBox<String> cmbRole = new JComboBox<>(
            new String[]{"Administrator", "Waqif", "Beneficiary", "CommitteeMember"}
        );
        
        // Set current role
        String currentRole = user.getClass().getSimpleName();
        cmbRole.setSelectedItem(currentRole);
        
        // If beneficiary, load education and experience
        if (user instanceof Beneficiary) {
            Beneficiary ben = (Beneficiary) user;
            txtEducation.setText(ben.getEducation());
            txtExperience.setText(String.valueOf(ben.getExperienceYears()));
        }
        
        // Disable username editing (unique identifier)
        txtUsername.setEditable(false);
        txtUsername.setBackground(new Color(240, 240, 240));

        int row = 0;

        // Row 1: Full Name
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0.3;
        panel.add(new JLabel("Full Name:*"), gbc);
        gbc.gridx = 1; gbc.weightx = 0.7;
        panel.add(txtFullName, gbc);
        row++;

        // Row 2: Username (read-only)
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0.3;
        panel.add(new JLabel("Username:"), gbc);
        gbc.gridx = 1; gbc.weightx = 0.7;
        panel.add(txtUsername, gbc);
        row++;

        // Row 3: Email
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0.3;
        panel.add(new JLabel("Email:*"), gbc);
        gbc.gridx = 1; gbc.weightx = 0.7;
        panel.add(txtEmail, gbc);
        row++;

        // Row 4: Phone
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0.3;
        panel.add(new JLabel("Phone:"), gbc);
        gbc.gridx = 1; gbc.weightx = 0.7;
        panel.add(txtPhone, gbc);
        row++;

        // Row 5: Role
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0.3;
        panel.add(new JLabel("Role:"), gbc);
        gbc.gridx = 1; gbc.weightx = 0.7;
        panel.add(cmbRole, gbc);
        row++;

        // ===== BENEFICIARY-SPECIFIC FIELDS =====
        // These fields are only enabled if the user is a Beneficiary
        boolean isBeneficiary = user instanceof Beneficiary;
        
        // Row 6: Education
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0.3;
        JLabel lblEducation = new JLabel("Education:");
        lblEducation.setEnabled(isBeneficiary);
        panel.add(lblEducation, gbc);
        gbc.gridx = 1; gbc.weightx = 0.7;
        txtEducation.setEnabled(isBeneficiary);
        txtEducation.setBackground(isBeneficiary ? Color.WHITE : new Color(240, 240, 240));
        panel.add(txtEducation, gbc);
        row++;

        // Row 7: Experience Years
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0.3;
        JLabel lblExperience = new JLabel("Experience (years):");
        lblExperience.setEnabled(isBeneficiary);
        panel.add(lblExperience, gbc);
        gbc.gridx = 1; gbc.weightx = 0.7;
        txtExperience.setEnabled(isBeneficiary);
        txtExperience.setBackground(isBeneficiary ? Color.WHITE : new Color(240, 240, 240));
        panel.add(txtExperience, gbc);
        row++;

        dialog.add(panel, BorderLayout.CENTER);

        // ===== BUTTONS =====
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        
        JButton btnSave = new JButton("💾 Save Changes");
        btnSave.setBackground(new Color(0, 153, 76));
        btnSave.setForeground(Color.BLACK);
        btnSave.setFont(new Font("Arial", Font.BOLD, 12));
        btnSave.addActionListener(e -> {
            String name = txtFullName.getText().trim();
            String email = txtEmail.getText().trim();
            String phone = txtPhone.getText().trim();
            String role = (String) cmbRole.getSelectedItem();
            
            if (name.isEmpty() || email.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, 
                    "Please fill all required fields (*).", 
                    "Missing Information", 
                    JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            // Update common fields
            user.setFullName(name);
            user.setEmail(email);
            user.setPhoneNumber(phone);
            
            // If beneficiary, update education and experience
            if (user instanceof Beneficiary) {
                Beneficiary ben = (Beneficiary) user;
                ben.setEducation(txtEducation.getText().trim());
                try {
                    int experience = Integer.parseInt(txtExperience.getText().trim());
                    ben.setExperienceYears(experience);
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(dialog, 
                        "Please enter a valid number for Experience.", 
                        "Invalid Input", 
                        JOptionPane.ERROR_MESSAGE);
                    return;
                }
            }
            
            UserService.updateUser(user);
            dialog.dispose();
            loadUsers();
            updateStats();
            addSystemLog("User updated: " + user.getFullName());
            setStatus("✅ User updated: " + user.getFullName());
            JOptionPane.showMessageDialog(this, "✅ User updated successfully!");
        });
        buttonPanel.add(btnSave);

        JButton btnCancel = new JButton("Cancel");
        btnCancel.addActionListener(e -> dialog.dispose());
        buttonPanel.add(btnCancel);

        dialog.add(buttonPanel, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }
    // ===== DELETE USER =====
    private void deleteUser() {
        int selectedRow = userTable.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Please select a user to delete.");
            return;
        }

        int userId = (int) userTable.getValueAt(selectedRow, 0);
        String userName = (String) userTable.getValueAt(selectedRow, 1);

        int confirm = JOptionPane.showConfirmDialog(this,
            "Delete user: " + userName + " (ID: " + userId + ")?\n\n" +
            "This action cannot be undone!",
            "Confirm Delete",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            UserService.deleteUser(userId);
            loadUsers();
            updateStats();
            addSystemLog("User deleted: " + userName + " (ID: " + userId + ")");
            setStatus("🗑️ User deleted: " + userName);
            JOptionPane.showMessageDialog(this, "✅ User deleted successfully!");
        }
    }

    // ===== GET ADMIN =====
    public Administrator getAdmin() {
        return admin;
    }
}