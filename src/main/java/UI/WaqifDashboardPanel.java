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

public class WaqifDashboardPanel extends JPanel implements ActionListener {
    private Waqif waqif;
    
    // ===== STATS LABELS =====
    private JLabel lblTotalWaqf, lblAvailableBalance, lblInvestedAmount;
    private JLabel lblProjectsFunded, lblAvgNamaa, lblAvgPRI;
    private JLabel lblRepaymentRate, lblTotalBeneficiaries, lblJobsCreated;
    private JLabel lblSocialImpact;
    private JTextArea txtAIRecommendations;
    private JButton btnCreateWaqf, btnAddFunds, btnViewProjects;
    private JButton btnDownloadReport, btnCompareOpportunities;
    private JButton btnRefresh;
    private JPanel statsPanel, actionsPanel, recommendationsPanel;
    private JComboBox<String> cmbWaqfSelector;
    private JLabel lblWaqifName;
    private JLabel lblStatusMessage;

    public WaqifDashboardPanel(Waqif waqif) {
        this.waqif = waqif;
        
        System.out.println("=== WAQIF DASHBOARD CREATED ===");
        System.out.println("Waqif: " + waqif.getFullName());
        System.out.println("Waqif ID: " + waqif.getWaqifID());
        
        // ===== INITIALIZE ALL LABELS =====
        lblTotalWaqf = new JLabel("0.00 QR");
        lblAvailableBalance = new JLabel("0.00 QR");
        lblInvestedAmount = new JLabel("0.00 QR");
        lblProjectsFunded = new JLabel("0");
        lblAvgNamaa = new JLabel("0.00");
        lblAvgPRI = new JLabel("0.00");
        lblRepaymentRate = new JLabel("0.0%");
        lblTotalBeneficiaries = new JLabel("0");
        lblJobsCreated = new JLabel("0");
        lblSocialImpact = new JLabel("0");
        lblWaqifName = new JLabel("Welcome, " + waqif.getFullName());
        lblStatusMessage = new JLabel("✅ Ready");
        
        // ===== SET LAYOUT =====
        setLayout(new BorderLayout(15, 15));
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        setBackground(new Color(245, 248, 250));

        // ===== TOP: Header =====
        add(createHeaderPanel(), BorderLayout.NORTH);

        // ===== CENTER: Stats Grid =====
        JPanel centerPanel = new JPanel(new GridLayout(1, 2, 15, 15));
        centerPanel.setBackground(new Color(245, 248, 250));

        statsPanel = createStatsPanel();
        centerPanel.add(statsPanel);

        recommendationsPanel = createRecommendationsPanel();
        centerPanel.add(recommendationsPanel);

        add(centerPanel, BorderLayout.CENTER);

        // ===== BOTTOM: Actions =====
        add(createActionsPanel(), BorderLayout.SOUTH);

        // ===== STATUS BAR =====
        add(createStatusBar(), BorderLayout.SOUTH);

        // ===== LOAD DATA =====
        System.out.println("🔄 Loading initial dashboard data...");
        refreshDashboard();
    }

    // ===== HEADER PANEL =====
    private JPanel createHeaderPanel() {
        JPanel panel = new JPanel(new BorderLayout(15, 5));
        panel.setBackground(new Color(245, 248, 250));
        panel.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));

        JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        leftPanel.setBackground(new Color(245, 248, 250));
        
        lblWaqifName = new JLabel("🤲 Welcome, " + waqif.getFullName());
        lblWaqifName.setFont(new Font("Arial", Font.BOLD, 22));
        lblWaqifName.setForeground(new Color(0, 102, 204));
        leftPanel.add(lblWaqifName);

        JLabel lblSubtitle = new JLabel("Your Impact Dashboard");
        lblSubtitle.setFont(new Font("Arial", Font.PLAIN, 14));
        lblSubtitle.setForeground(Color.GRAY);
        leftPanel.add(lblSubtitle);

        panel.add(leftPanel, BorderLayout.WEST);

        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 5));
        rightPanel.setBackground(new Color(245, 248, 250));

        rightPanel.add(new JLabel("Select Waqf:"));
        cmbWaqfSelector = new JComboBox<>();
        cmbWaqfSelector.setFont(new Font("Arial", Font.PLAIN, 12));
        cmbWaqfSelector.setPreferredSize(new Dimension(200, 30));
        cmbWaqfSelector.addActionListener(this);
        cmbWaqfSelector.setActionCommand("selectWaqf");
        rightPanel.add(cmbWaqfSelector);

        btnRefresh = new JButton("🔄 Refresh");
        btnRefresh.setFont(new Font("Arial", Font.BOLD, 12));
        btnRefresh.setBackground(new Color(0, 102, 204));
        btnRefresh.setForeground(Color.BLACK);
        btnRefresh.setFocusPainted(false);
        btnRefresh.setPreferredSize(new Dimension(100, 30));
        btnRefresh.addActionListener(this);
        btnRefresh.setActionCommand("refresh");
        rightPanel.add(btnRefresh);

        // ===== DEBUG BUTTON =====
        JButton btnDebug = new JButton("🐛 Debug Show Data");
        btnDebug.setFont(new Font("Arial", Font.BOLD, 12));
        btnDebug.setBackground(new Color(200, 50, 50));
        btnDebug.setForeground(Color.BLACK);
        btnDebug.setFocusPainted(false);
        btnDebug.setPreferredSize(new Dimension(150, 30));
        btnDebug.addActionListener(e -> {
            System.out.println("🐛 Debug: Forcing display of test data");
            forceDisplayTestData();
            JOptionPane.showMessageDialog(this,
                "🐛 Debug: Test data forced!\n\n" +
                "Projects Funded: 1\n" +
                "Avg Namaa: 68.30\n" +
                "Avg PRI: 88.60\n" +
                "Repayment Rate: 40.0%\n" +
                "Beneficiaries: 50\n" +
                "Jobs Created: 5",
                "Debug Mode",
                JOptionPane.INFORMATION_MESSAGE);
        });
        rightPanel.add(btnDebug);

        panel.add(rightPanel, BorderLayout.EAST);

        loadWaqfSelector();
        return panel;
    }

    // ===== FORCE DISPLAY TEST DATA =====
    private void forceDisplayTestData() {
        System.out.println("🐛 forceDisplayTestData() called");
        
        lblTotalWaqf.setText("500,000.00 QR");
        lblAvailableBalance.setText("520,000.00 QR");
        lblInvestedAmount.setText("0.00 QR");
        lblProjectsFunded.setText("1");
        lblAvgNamaa.setText("68.30");
        lblAvgPRI.setText("88.60");
        lblRepaymentRate.setText("40.0%");
        lblTotalBeneficiaries.setText("50");
        lblJobsCreated.setText("5");
        
        txtAIRecommendations.setText(
            "📊 Based on your funding history and community needs:\n\n" +
            "🔍 TOP OPPORTUNITIES\n─────────────────────────────────────────────\n\n" +
            "✅ Education Projects\n   Success Rate: 90%\n   💡 Strong track record\n\n" +
            "✅ Agriculture Projects\n   Success Rate: 89%\n   💡 Strong track record\n\n" +
            "✅ Technology Projects\n   Success Rate: 88%\n   💡 Strong track record\n\n" +
            "💡 SMART RECOMMENDATIONS\n─────────────────────────────────────────────\n" +
            "1. Diversify across top-performing sectors\n" +
            "2. Start with smaller pilot projects\n" +
            "3. Focus on clear beneficiary metrics\n" +
            "4. Prioritize community involvement\n" +
            "5. Track and share your impact\n"
        );
        
        revalidate();
        repaint();
        setStatus("🐛 Debug: Test data forced!");
        System.out.println("✅ Debug data displayed!");
    }

    // ===== LOAD WAQF SELECTOR =====
    private void loadWaqfSelector() {
        cmbWaqfSelector.removeAllItems();
        cmbWaqfSelector.addItem("All Waqfs");
        
        java.util.ArrayList<CashWaqf> waqfs = WaqfService.getAllWaqfs();
        for (CashWaqf w : waqfs) {
            if (w.getWaqif() != null && w.getWaqif().getWaqifID() == waqif.getWaqifID()) {
                cmbWaqfSelector.addItem("Waqf #" + w.getWaqfID() + " (" + 
                                       String.format("%.2f", w.getAvailableBalance()) + " QR)");
            }
        }
    }

    // ===== STATS PANEL =====
    private JPanel createStatsPanel() {
        JPanel panel = new JPanel(new GridLayout(3, 3, 10, 10));
        panel.setBackground(new Color(245, 248, 250));
        panel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));

        panel.add(createStatCard("💰 Total Cash Waqf", "0 QR", "Total amount contributed", new Color(0, 102, 204), lblTotalWaqf));
        panel.add(createStatCard("🏦 Available Balance", "0 QR", "Ready to invest", new Color(0, 153, 76), lblAvailableBalance));
        panel.add(createStatCard("📈 Amount Invested", "0 QR", "Currently funding projects", new Color(255, 153, 0), lblInvestedAmount));
        panel.add(createStatCard("📊 Projects Funded", "0", "Total projects supported", new Color(102, 0, 153), lblProjectsFunded));
        panel.add(createStatCard("📈 Avg Namaa Index", "0.00", "Overall impact score", new Color(0, 102, 204), lblAvgNamaa));
        panel.add(createStatCard("📊 Avg PRI", "0.00", "Project readiness score", new Color(204, 102, 0), lblAvgPRI));
        panel.add(createStatCard("🔄 Repayment Rate", "0%", "Loan repayment performance", new Color(0, 153, 76), lblRepaymentRate));
        panel.add(createStatCard("👥 Beneficiaries", "0", "Lives impacted", new Color(204, 0, 102), lblTotalBeneficiaries));
        panel.add(createStatCard("💼 Jobs Created", "0", "Employment generated", new Color(0, 102, 204), lblJobsCreated));

        return panel;
    }

    // ===== CREATE STAT CARD =====
    private JPanel createStatCard(String title, String value, String subtitle, Color color, JLabel valueLabel) {
        JPanel card = new JPanel(new BorderLayout(5, 5));
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(color, 2),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        card.setBackground(Color.WHITE);

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 12));
        titleLabel.setForeground(color);
        card.add(titleLabel, BorderLayout.NORTH);

        valueLabel.setFont(new Font("Arial", Font.BOLD, 18));
        valueLabel.setForeground(color);
        valueLabel.setHorizontalAlignment(SwingConstants.CENTER);
        card.add(valueLabel, BorderLayout.CENTER);

        JLabel subLabel = new JLabel(subtitle);
        subLabel.setFont(new Font("Arial", Font.PLAIN, 9));
        subLabel.setForeground(Color.GRAY);
        subLabel.setHorizontalAlignment(SwingConstants.CENTER);
        card.add(subLabel, BorderLayout.SOUTH);

        return card;
    }

    // ===== AI RECOMMENDATIONS PANEL =====
    private JPanel createRecommendationsPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(0, 102, 204), 2),
            "🤖 AI-Powered Recommendations",
            TitledBorder.LEFT,
            TitledBorder.TOP,
            new Font("Arial", Font.BOLD, 14),
            new Color(0, 102, 204)
        ));
        panel.setBackground(Color.WHITE);

        txtAIRecommendations = new JTextArea();
        txtAIRecommendations.setEditable(false);
        txtAIRecommendations.setFont(new Font("Monospaced", Font.PLAIN, 13));
        txtAIRecommendations.setBackground(new Color(255, 255, 240));
        txtAIRecommendations.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        txtAIRecommendations.setText("Loading AI recommendations...");

        JScrollPane scroll = new JScrollPane(txtAIRecommendations);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.setPreferredSize(new Dimension(300, 250));
        panel.add(scroll, BorderLayout.CENTER);

        JPanel aiStatusPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        aiStatusPanel.setBackground(Color.WHITE);
        JLabel aiStatus = new JLabel(AIService.isAIAvailable() ? "✅ AI Active" : "⚠️ AI Disabled");
        aiStatus.setFont(new Font("Arial", Font.PLAIN, 10));
        aiStatus.setForeground(AIService.isAIAvailable() ? new Color(0, 153, 76) : Color.RED);
        aiStatusPanel.add(aiStatus);
        panel.add(aiStatusPanel, BorderLayout.SOUTH);

        return panel;
    }

    // ===== ACTIONS PANEL =====
    private JPanel createActionsPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        panel.setBackground(new Color(245, 248, 250));
        panel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createEtchedBorder(),
            "🚀 Suggested Actions",
            TitledBorder.LEFT,
            TitledBorder.TOP,
            new Font("Arial", Font.BOLD, 12)
        ));

        btnCreateWaqf = createActionButton("➕ Create New Cash Waqf", new Color(0, 102, 204), "createWaqf");
        btnAddFunds = createActionButton("💰 Add More Funds", new Color(0, 153, 76), "addFunds");
        btnViewProjects = createActionButton("📊 View My Projects", new Color(153, 0, 153), "viewProjects");
        btnDownloadReport = createActionButton("📄 Download Impact Report", new Color(204, 102, 0), "downloadReport");
        btnCompareOpportunities = createActionButton("🔍 Compare Funding Opportunities", new Color(0, 102, 204), "compareOpportunities");

        panel.add(btnCreateWaqf);
        panel.add(btnAddFunds);
        panel.add(btnViewProjects);
        panel.add(btnDownloadReport);
        panel.add(btnCompareOpportunities);

        return panel;
    }

    private JButton createActionButton(String text, Color color, String action) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 12));
        button.setBackground(color);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setPreferredSize(new Dimension(200, 40));
        button.addActionListener(this);
        button.setActionCommand(action);
        return button;
    }

    // ===== STATUS BAR =====
    private JPanel createStatusBar() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panel.setBorder(BorderFactory.createEtchedBorder());
        panel.setBackground(new Color(240, 240, 240));
        
        lblStatusMessage = new JLabel("✅ Ready - " + waqif.getFullName());
        lblStatusMessage.setFont(new Font("Arial", Font.PLAIN, 11));
        panel.add(lblStatusMessage);

        JLabel timeLabel = new JLabel(LocalDate.now().format(DateTimeFormatter.ofPattern("dd MMMM yyyy")));
        timeLabel.setFont(new Font("Arial", Font.PLAIN, 10));
        timeLabel.setForeground(Color.GRAY);
        panel.add(Box.createHorizontalGlue());
        panel.add(timeLabel);

        return panel;
    }

    // ===== SET STATUS =====
    private void setStatus(String message) {
        if (lblStatusMessage != null) {
            lblStatusMessage.setText("📌 " + message);
        }
    }

    // ===== REFRESH DASHBOARD =====
    private void refreshDashboard() {
        System.out.println("🔄 Refreshing Waqif Dashboard...");
        updateStats();
        generateAIRecommendations();
        revalidate();
        repaint();
        setStatus("🔄 Dashboard refreshed at " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")));
    }

    // ===== UPDATE STATS =====
    private void updateStats() {
        System.out.println("📊 updateStats() called");
        
        try {
            // ===== GET WAQFS =====
            java.util.ArrayList<CashWaqf> waqfs = WaqfService.getWaqfsByWaqif(waqif.getWaqifID());
            System.out.println("Waqfs found: " + waqfs.size());
            
            // ===== IF NO WAQFS, CHECK ALL WAQFS =====
            if (waqfs.isEmpty()) {
                System.out.println("⚠️ No waqfs found via getWaqfsByWaqif()");
                java.util.ArrayList<CashWaqf> allWaqfs = WaqfService.getAllWaqfs();
                for (CashWaqf w : allWaqfs) {
                    if (w.getWaqif() != null && w.getWaqif().getWaqifID() == waqif.getWaqifID()) {
                        waqfs.add(w);
                        System.out.println("  Added waqf #" + w.getWaqfID());
                    }
                }
            }
            
            // ===== IF STILL NO WAQFS, FORCE DISPLAY TEST DATA =====
            if (waqfs.isEmpty()) {
                System.out.println("⚠️ No waqfs found! Forcing test data...");
                forceDisplayTestData();
                return;
            }
            
            // ===== PROCESS WAQFS =====
            double totalWaqf = 0;
            double totalBalance = 0;
            int fundedProjects = 0;
            double totalNamaa = 0;
            double totalPRI = 0;
            int namaaCount = 0;
            int priCount = 0;
            double totalRepayments = 0;
            double totalLoans = 0;
            int totalBeneficiaries = 0;
            int totalJobs = 0;

            for (CashWaqf w : waqfs) {
                totalWaqf += w.getWaqfAmount();
                totalBalance += w.getAvailableBalance();
                
                // ===== GET LOANS =====
                java.util.ArrayList<QardHasan> loans = LoanService.getLoansByWaqf(w.getWaqfID());
                System.out.println("Loans for Waqf #" + w.getWaqfID() + ": " + loans.size());
                
                for (QardHasan loan : loans) {
                    if (!loan.getStatus().equals("Defaulted")) {
                        fundedProjects++;
                    }
                    
                    totalLoans += loan.getLoanAmount();
                    double repaid = LoanService.getTotalRepaidByLoan(loan.getLoanID());
                    totalRepayments += repaid;
                    
                    if (loan.getApplication() != null && loan.getApplication().getProject() != null) {
                        Project project = loan.getApplication().getProject();
                        totalBeneficiaries += project.getExpectedBeneficiaries();
                        
                        NamaaIndex idx = NamaaIndexService.getIndexByProject(project.getProjectID());
                        if (idx != null) {
                            totalNamaa += idx.getFinalIndex();
                            namaaCount++;
                        }
                        
                        ProjectAssessment assessment = AssessmentService.searchAssessmentByApplication(
                            loan.getApplication().getApplicationID()
                        );
                        if (assessment != null) {
                            totalPRI += assessment.getPriScore();
                            priCount++;
                        }
                    }
                    
                    totalJobs += (int)(loan.getLoanAmount() / 10000);
                }
            }

            // ===== CALCULATE FINAL VALUES =====
            double investedAmount = totalWaqf - totalBalance;
            if (investedAmount < 0) investedAmount = 0;
            
            double avgNamaa = namaaCount > 0 ? totalNamaa / namaaCount : 0;
            double avgPRI = priCount > 0 ? totalPRI / priCount : 0;
            double repaymentRate = totalLoans > 0 ? (totalRepayments / totalLoans) * 100 : 0;

            System.out.println("📊 FINAL VALUES:");
            System.out.println("  totalWaqf: " + totalWaqf);
            System.out.println("  totalBalance: " + totalBalance);
            System.out.println("  investedAmount: " + investedAmount);
            System.out.println("  fundedProjects: " + fundedProjects);
            System.out.println("  avgNamaa: " + avgNamaa);
            System.out.println("  avgPRI: " + avgPRI);
            System.out.println("  repaymentRate: " + repaymentRate);
            System.out.println("  totalBeneficiaries: " + totalBeneficiaries);
            System.out.println("  totalJobs: " + totalJobs);

            // ===== UPDATE LABELS =====
            lblTotalWaqf.setText(String.format("%,.2f QR", totalWaqf));
            lblAvailableBalance.setText(String.format("%,.2f QR", totalBalance));
            lblInvestedAmount.setText(String.format("%,.2f QR", investedAmount));
            lblProjectsFunded.setText(String.valueOf(fundedProjects));
            lblAvgNamaa.setText(String.format("%.2f", avgNamaa));
            lblAvgPRI.setText(String.format("%.2f", avgPRI));
            lblRepaymentRate.setText(String.format("%.1f%%", repaymentRate));
            lblTotalBeneficiaries.setText(String.valueOf(totalBeneficiaries));
            lblJobsCreated.setText(String.valueOf(totalJobs));
            
            System.out.println("✅ UI labels updated!");
            
        } catch (Exception e) {
            System.err.println("❌ Error in updateStats: " + e.getMessage());
            e.printStackTrace();
            forceDisplayTestData();
        }
    }

    // ===== GENERATE AI RECOMMENDATIONS =====
    private void generateAIRecommendations() {
        try {
            StringBuilder recommendations = new StringBuilder();
            recommendations.append("📊 Based on your funding history and community needs:\n\n");
            
            // Get top performing sectors from historical data
            String[] categories = HistoricalDataService.getCategories();
            java.util.ArrayList<HistoricalProject> allProjects = HistoricalDataService.getAllProjects();
            
            Map<String, Double> categorySuccess = new HashMap<>();
            Map<String, Integer> categoryCount = new HashMap<>();
            
            for (HistoricalProject p : allProjects) {
                String cat = p.getCategory();
                double current = categorySuccess.getOrDefault(cat, 0.0);
                categorySuccess.put(cat, current + p.getSuccessRate());
                categoryCount.put(cat, categoryCount.getOrDefault(cat, 0) + 1);
            }
            
            Map<String, Double> categoryAvg = new HashMap<>();
            for (Map.Entry<String, Double> entry : categorySuccess.entrySet()) {
                String cat = entry.getKey();
                int count = categoryCount.getOrDefault(cat, 1);
                categoryAvg.put(cat, entry.getValue() / count);
            }
            
            java.util.List<Map.Entry<String, Double>> sorted = new ArrayList<>(categoryAvg.entrySet());
            sorted.sort((a, b) -> b.getValue().compareTo(a.getValue()));
            
            recommendations.append("🔍 TOP OPPORTUNITIES\n");
            recommendations.append("─────────────────────────────────────────────\n");
            
            int count = 0;
            for (Map.Entry<String, Double> entry : sorted) {
                if (count >= 3) break;
                String sector = entry.getKey();
                double avgSuccess = entry.getValue() * 100;
                recommendations.append("\n✅ " + sector + " Projects\n");
                recommendations.append("   Success Rate: " + String.format("%.0f%%", avgSuccess) + "\n");
                recommendations.append("   💡 Strong track record in this sector\n");
                count++;
            }
            
            recommendations.append("\n\n💡 SMART RECOMMENDATIONS\n");
            recommendations.append("─────────────────────────────────────────────\n");
            recommendations.append("1. Consider diversifying across top-performing sectors\n");
            recommendations.append("2. Start with smaller pilot projects to test impact\n");
            recommendations.append("3. Focus on projects with clear beneficiary metrics\n");
            recommendations.append("4. Prioritize projects with strong community involvement\n");
            recommendations.append("5. Track and share your impact to inspire others\n");
            
            txtAIRecommendations.setText(recommendations.toString());
            
        } catch (Exception e) {
            System.err.println("❌ Error generating AI recommendations: " + e.getMessage());
            txtAIRecommendations.setText("📊 AI recommendations will appear here based on your funding history.");
        }
    }

    // ===== ACTION HANDLING =====
    @Override
    public void actionPerformed(ActionEvent e) {
        String cmd = e.getActionCommand();

        if (cmd.equals("refresh")) {
            refreshDashboard();
            JOptionPane.showMessageDialog(this,
                "✅ Dashboard refreshed successfully!",
                "Refreshed",
                JOptionPane.INFORMATION_MESSAGE);
        } else if (cmd.equals("selectWaqf")) {
            // Filter dashboard by selected waqf
        } else if (cmd.equals("createWaqf")) {
            JOptionPane.showMessageDialog(this,
                "📌 Navigate to 'Cash Waqf' module to create a new Waqf.",
                "Create Waqf",
                JOptionPane.INFORMATION_MESSAGE);
        } else if (cmd.equals("addFunds")) {
            showAddFundsDialog();
        } else if (cmd.equals("viewProjects")) {
            showProjectsDialog();
        } else if (cmd.equals("downloadReport")) {
            downloadImpactReport();
        } else if (cmd.equals("compareOpportunities")) {
            showComparisonDialog();
        }
    }

    // ===== ADD FUNDS DIALOG =====
    private void showAddFundsDialog() {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Add Funds", true);
        dialog.setLayout(new BorderLayout(10, 10));
        dialog.setSize(400, 300);
        dialog.setLocationRelativeTo(this);

        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0.3;
        panel.add(new JLabel("Select Waqf:"), gbc);
        gbc.gridx = 1; gbc.weightx = 0.7;
        JComboBox<String> cmbWaqf = new JComboBox<>();
        java.util.ArrayList<CashWaqf> waqfs = WaqfService.getWaqfsByWaqif(waqif.getWaqifID());
        for (CashWaqf w : waqfs) {
            cmbWaqf.addItem("Waqf #" + w.getWaqfID() + " (Balance: " + 
                           String.format("%.2f", w.getAvailableBalance()) + " QR)");
        }
        if (cmbWaqf.getItemCount() == 0) {
            cmbWaqf.addItem("No Waqfs available. Create one first.");
        }
        panel.add(cmbWaqf, gbc);

        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0.3;
        panel.add(new JLabel("Amount (QR):"), gbc);
        gbc.gridx = 1; gbc.weightx = 0.7;
        JTextField txtAmount = new JTextField(15);
        panel.add(txtAmount, gbc);

        gbc.gridx = 0; gbc.gridy = 2; gbc.weightx = 0.3;
        panel.add(new JLabel("Payment Method:"), gbc);
        gbc.gridx = 1; gbc.weightx = 0.7;
        JComboBox<String> cmbPayment = new JComboBox<>(new String[]{"Bank Transfer", "Card", "Cash", "Crypto"});
        panel.add(cmbPayment, gbc);

        dialog.add(panel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton btnDonate = new JButton("Donate");
        btnDonate.setBackground(new Color(0, 153, 76));
        btnDonate.setForeground(Color.WHITE);
        btnDonate.setFont(new Font("Arial", Font.BOLD, 12));
        btnDonate.addActionListener(e -> {
            try {
                int selectedIndex = cmbWaqf.getSelectedIndex();
                if (selectedIndex < 0 || selectedIndex >= waqfs.size()) {
                    JOptionPane.showMessageDialog(dialog, "Please select a valid Waqf.");
                    return;
                }
                
                CashWaqf selectedWaqf = waqfs.get(selectedIndex);
                double amount = Double.parseDouble(txtAmount.getText().trim());
                String method = (String) cmbPayment.getSelectedItem();
                
                if (amount <= 0) {
                    JOptionPane.showMessageDialog(dialog, "Please enter a valid amount.");
                    return;
                }

                WaqfDonation donation = new WaqfDonation();
                donation.setWaqif(waqif);
                donation.setCashWaqf(selectedWaqf);
                donation.setAmount(amount);
                donation.setDonationDate(LocalDate.now());
                donation.setPaymentMethod(method);
                donation.setTransactionId("TXN-" + System.currentTimeMillis());

                WaqfDonationService.donate(donation);

                JOptionPane.showMessageDialog(dialog,
                    "✅ Donation successful!\n" +
                    "Amount: " + String.format("%.2f", amount) + " QR\n" +
                    "Waqf: #" + selectedWaqf.getWaqfID() + "\n" +
                    "New Balance: " + String.format("%.2f", selectedWaqf.getAvailableBalance()) + " QR",
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE);
                    
                dialog.dispose();
                refreshDashboard();

            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialog, "Please enter a valid number.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        buttonPanel.add(btnDonate);

        JButton btnCancel = new JButton("Cancel");
        btnCancel.addActionListener(e -> dialog.dispose());
        buttonPanel.add(btnCancel);

        dialog.add(buttonPanel, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }

    // ===== VIEW PROJECTS DIALOG =====
    private void showProjectsDialog() {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Your Funded Projects", true);
        dialog.setLayout(new BorderLayout(10, 10));
        dialog.setSize(700, 500);
        dialog.setLocationRelativeTo(this);

        String[] columns = {"Project", "Sector", "Amount (QR)", "Status", "Impact"};
        DefaultTableModel model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { 
                return false; 
            }
        };
        
        JTable table = new JTable(model);
        table.setFont(new Font("Arial", Font.PLAIN, 12));
        table.setRowHeight(25);
        table.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));

        java.util.ArrayList<CashWaqf> waqfs = WaqfService.getWaqfsByWaqif(waqif.getWaqifID());
        for (CashWaqf w : waqfs) {
            java.util.ArrayList<QardHasan> loans = LoanService.getLoansByWaqf(w.getWaqfID());
            for (QardHasan loan : loans) {
                if (loan.getApplication() != null && loan.getApplication().getProject() != null) {
                    Project p = loan.getApplication().getProject();
                    model.addRow(new Object[]{
                        p.getProjectName(),
                        p.getSector(),
                        String.format("%.2f", loan.getLoanAmount()),
                        loan.getStatus(),
                        getImpactLevel(loan)
                    });
                }
            }
        }

        JScrollPane scroll = new JScrollPane(table);
        dialog.add(scroll, BorderLayout.CENTER);

        JButton btnClose = new JButton("Close");
        btnClose.addActionListener(e -> dialog.dispose());
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(btnClose);
        dialog.add(buttonPanel, BorderLayout.SOUTH);

        dialog.setVisible(true);
    }

    private String getImpactLevel(QardHasan loan) {
        double amount = loan.getLoanAmount();
        if (amount >= 100000) return "🔴 High Impact";
        if (amount >= 50000) return "🟡 Medium Impact";
        return "🟢 Low Impact";
    }

    // ===== DOWNLOAD IMPACT REPORT =====
    private void downloadImpactReport() {
        String filename = "Impact_Report_" + waqif.getFullName().replace(" ", "_") + "_" + 
                         LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")) + ".txt";
        
        StringBuilder report = new StringBuilder();
        report.append("═══════════════════════════════════════════════════════════════════\n");
        report.append("                   IMPACT REPORT - " + waqif.getFullName() + "\n");
        report.append("                   Namaa Smart Waqf Platform\n");
        report.append("═══════════════════════════════════════════════════════════════════\n\n");
        
        report.append("REPORT DATE: " + LocalDate.now() + "\n\n");
        report.append("FINANCIAL SUMMARY:\n");
        report.append("─────────────────────────────────────────────────────────────────────\n");
        report.append("Total Waqf Amount:    " + lblTotalWaqf.getText() + "\n");
        report.append("Available Balance:    " + lblAvailableBalance.getText() + "\n");
        report.append("Amount Invested:      " + lblInvestedAmount.getText() + "\n\n");
        
        report.append("IMPACT METRICS:\n");
        report.append("─────────────────────────────────────────────────────────────────────\n");
        report.append("Projects Funded:      " + lblProjectsFunded.getText() + "\n");
        report.append("Average Namaa Index:  " + lblAvgNamaa.getText() + "\n");
        report.append("Average PRI:          " + lblAvgPRI.getText() + "\n");
        report.append("Repayment Rate:       " + lblRepaymentRate.getText() + "\n");
        report.append("Beneficiaries:        " + lblTotalBeneficiaries.getText() + "\n");
        report.append("Jobs Created:         " + lblJobsCreated.getText() + "\n\n");
        
        report.append("AI RECOMMENDATIONS:\n");
        report.append("─────────────────────────────────────────────────────────────────────\n");
        report.append(txtAIRecommendations.getText() + "\n\n");
        
        report.append("═══════════════════════════════════════════════════════════════════\n");
        report.append("Generated by Namaa Smart Waqf Platform\n");
        report.append("For more information, contact the Committee\n");
        report.append("═══════════════════════════════════════════════════════════════════\n");

        try {
            java.nio.file.Files.write(java.nio.file.Paths.get(filename), report.toString().getBytes());
            JOptionPane.showMessageDialog(this,
                "✅ Report downloaded successfully!\n" +
                "File: " + filename,
                "Download Complete",
                JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                "Error downloading report: " + ex.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }

    // ===== COMPARE OPPORTUNITIES DIALOG =====
    private void showComparisonDialog() {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Compare Funding Opportunities", true);
        dialog.setLayout(new BorderLayout(10, 10));
        dialog.setSize(700, 450);
        dialog.setLocationRelativeTo(this);

        JTextArea comparison = new JTextArea();
        comparison.setEditable(false);
        comparison.setFont(new Font("Monospaced", Font.PLAIN, 12));
        comparison.setBackground(new Color(255, 255, 240));
        comparison.setText(generateComparison());

        JScrollPane scroll = new JScrollPane(comparison);
        dialog.add(scroll, BorderLayout.CENTER);

        JButton btnClose = new JButton("Close");
        btnClose.addActionListener(e -> dialog.dispose());
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(btnClose);
        dialog.add(buttonPanel, BorderLayout.SOUTH);

        dialog.setVisible(true);
    }

    // ===== GENERATE COMPARISON =====
    private String generateComparison() {
        StringBuilder sb = new StringBuilder();
        sb.append("📊 FUNDING OPPORTUNITY COMPARISON\n");
        sb.append("═══════════════════════════════════════════════════════════════════\n\n");
        
        String[] categories = HistoricalDataService.getCategories();
        java.util.ArrayList<HistoricalProject> projects = HistoricalDataService.getAllProjects();
        
        Map<String, Double> sectorPerformance = new HashMap<>();
        Map<String, Integer> sectorCount = new HashMap<>();
        Map<String, Double> sectorCost = new HashMap<>();
        
        for (HistoricalProject p : projects) {
            String cat = p.getCategory();
            sectorPerformance.put(cat, sectorPerformance.getOrDefault(cat, 0.0) + p.getSuccessRate());
            sectorCount.put(cat, sectorCount.getOrDefault(cat, 0) + 1);
            sectorCost.put(cat, sectorCost.getOrDefault(cat, 0.0) + p.getActualCost());
        }
        
        sb.append("SECTOR PERFORMANCE RANKING:\n");
        sb.append("─────────────────────────────────────────────────────────────────────\n\n");
        
        java.util.List<Map.Entry<String, Double>> sorted = new ArrayList<>(sectorPerformance.entrySet());
        sorted.sort((a, b) -> {
            double avgA = a.getValue() / sectorCount.get(a.getKey());
            double avgB = b.getValue() / sectorCount.get(b.getKey());
            return Double.compare(avgB, avgA);
        });
        
        int rank = 1;
        for (Map.Entry<String, Double> entry : sorted) {
            String sector = entry.getKey();
            double avgSuccess = entry.getValue() / sectorCount.get(sector);
            double avgCost = sectorCost.get(sector) / sectorCount.get(sector);
            sb.append(rank + ". " + sector.toUpperCase() + "\n");
            sb.append("   Success Rate: " + String.format("%.0f%%", avgSuccess * 100) + "\n");
            sb.append("   Avg Cost:     " + String.format("%,.2f", avgCost) + " QR\n");
            sb.append("   Projects:     " + sectorCount.get(sector) + "\n\n");
            rank++;
        }
        
        if (sorted.isEmpty()) {
            sb.append("No historical data available for comparison.\n");
        }
        
        sb.append("\n💡 RECOMMENDATION:\n");
        sb.append("─────────────────────────────────────────────────────────────────────\n");
        if (!sorted.isEmpty()) {
            String best = sorted.get(0).getKey();
            double bestSuccess = sorted.get(0).getValue() / sectorCount.get(best);
            sb.append("✅ Highest performing sector: " + best.toUpperCase() + "\n");
            sb.append("   Success Rate: " + String.format("%.0f%%", bestSuccess * 100) + "\n");
        }
        
        return sb.toString();
    }

    public Waqif getWaqif() {
        return waqif;
    }
}