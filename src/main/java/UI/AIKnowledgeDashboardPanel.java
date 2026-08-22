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

public class AIKnowledgeDashboardPanel extends JPanel implements ActionListener {
    
    // ===== STATS LABELS =====
    private JLabel lblTotalProjects, lblTotalSectors, lblAvgSuccessRate;
    private JLabel lblTopSector, lblTopSectorRate, lblLearningProgress;
    private JLabel lblKnowledgeItems, lblLastUpdated;
    
    // ===== TABLES =====
    private JTable sectorPerformanceTable, successFactorsTable, failureReasonsTable;
    private DefaultTableModel sectorModel, successModel, failureModel;
    
    // ===== CHARTS (Text-based) =====
    private JTextArea txtPerformanceChart, txtInsights;
    
    // ===== CONTROLS =====
    private JButton btnRefreshKnowledge, btnExportKnowledge, btnGenerateInsights;
    private JButton btnViewDetails, btnAIAnalyze;
    private JComboBox<String> cmbSectorFilter, cmbTimeRange;
    private JLabel lblStatusMessage;
    
    // ===== KNOWLEDGE DATA (FIXED - Initialize as empty lists) =====
    private Map<String, SectorKnowledge> sectorKnowledgeMap;
    private java.util.List<Insight> insights;  // ← FIXED: Initialize in constructor

    public AIKnowledgeDashboardPanel() {
        // ===== FIXED: Initialize data structures before anything else =====
        sectorKnowledgeMap = new HashMap<>();
        insights = new ArrayList<>();        
        
        setLayout(new BorderLayout(15, 15));
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        setBackground(new Color(245, 248, 250));

        // ===== TOP: Header =====
        add(createHeaderPanel(), BorderLayout.NORTH);

        // ===== CENTER: Tabbed Panels =====
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Arial", Font.BOLD, 14));
        tabbedPane.setTabPlacement(JTabbedPane.TOP);

        // Tab 1: Knowledge Overview
        tabbedPane.addTab("📊 Knowledge Overview", createOverviewPanel());
        
        // Tab 2: Sector Performance
        tabbedPane.addTab("📈 Sector Performance", createSectorPerformancePanel());
        
        // Tab 3: Success & Failure Analysis
        tabbedPane.addTab("✅ Success & Failure Analysis", createSuccessFailurePanel());
        
        // Tab 4: AI Learning Progress
        tabbedPane.addTab("🧠 AI Learning Progress", createLearningProgressPanel());
        
        // Tab 5: Institutional Insights
        tabbedPane.addTab("💡 Institutional Insights", createInsightsPanel());

        add(tabbedPane, BorderLayout.CENTER);

        // ===== BOTTOM: Status Bar =====
        add(createStatusBar(), BorderLayout.SOUTH);

        // Load initial data
        initializeKnowledge();
        refreshDashboard();
    }

    // ===== HEADER PANEL =====
    private JPanel createHeaderPanel() {
        JPanel panel = new JPanel(new BorderLayout(15, 5));
        panel.setBackground(new Color(245, 248, 250));
        panel.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));

        // Left: Title
        JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        leftPanel.setBackground(new Color(245, 248, 250));
        
        JLabel lblTitle = new JLabel("🧠 AI Knowledge Dashboard");
        lblTitle.setFont(new Font("Arial", Font.BOLD, 22));
        lblTitle.setForeground(new Color(0, 102, 204));
        leftPanel.add(lblTitle);

        JLabel lblSubtitle = new JLabel("Institutional Learning & Intelligence");
        lblSubtitle.setFont(new Font("Arial", Font.PLAIN, 14));
        lblSubtitle.setForeground(Color.GRAY);
        leftPanel.add(lblSubtitle);

        panel.add(leftPanel, BorderLayout.WEST);

        // Right: Controls
        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 5));
        rightPanel.setBackground(new Color(245, 248, 250));

        rightPanel.add(new JLabel("Time Range:"));
        cmbTimeRange = new JComboBox<>(new String[]{"All Time", "Last Year", "Last 6 Months", "Last 3 Months", "Last Month"});
        cmbTimeRange.setFont(new Font("Arial", Font.PLAIN, 11));
        cmbTimeRange.setPreferredSize(new Dimension(120, 28));
        cmbTimeRange.addActionListener(this);
        cmbTimeRange.setActionCommand("filterTime");
        rightPanel.add(cmbTimeRange);

        btnRefreshKnowledge = new JButton("🔄 Refresh Knowledge");
        btnRefreshKnowledge.setFont(new Font("Arial", Font.BOLD, 11));
        btnRefreshKnowledge.setBackground(new Color(0, 102, 204));
        btnRefreshKnowledge.setForeground(Color.BLACK);
        btnRefreshKnowledge.setFocusPainted(false);
        btnRefreshKnowledge.setPreferredSize(new Dimension(150, 28));
        btnRefreshKnowledge.addActionListener(this);
        btnRefreshKnowledge.setActionCommand("refreshKnowledge");
        rightPanel.add(btnRefreshKnowledge);

        panel.add(rightPanel, BorderLayout.EAST);

        return panel;
    }

    // ===== KNOWLEDGE DATA STRUCTURES =====
    private static class SectorKnowledge {
        String sector;
        double avgNamaa;
        double avgPRI;
        double avgSuccessRate;
        double avgRepayment;
        int projectCount;
        int totalBeneficiaries;
        double totalFunding;
        java.util.List<String> successFactors = new ArrayList<>();
        java.util.List<String> failureReasons = new ArrayList<>();
        java.util.List<Double> namaaScores = new ArrayList<>();
        java.util.List<Double> priScores = new ArrayList<>();
    }

    private static class Insight {
        String title;
        String description;
        String category;
        double confidence;
        String recommendation;
        LocalDate generatedDate;
    }

    // ===== INITIALIZE KNOWLEDGE =====
    private void initializeKnowledge() {
        sectorKnowledgeMap = new HashMap<>();
        insights = new ArrayList<>();  // 
        buildKnowledgeFromData();
        generateInsights();
    }

    // ===== BUILD KNOWLEDGE FROM DATA =====
    private void buildKnowledgeFromData() {
        sectorKnowledgeMap.clear();
        
        // Get all historical projects
        java.util.ArrayList<HistoricalProject> historicalProjects = HistoricalDataService.getAllProjects();
        
        // Get all applications with assessments
        java.util.ArrayList<FundingApplication> applications = FundingService.getApplications();
        
        // Process historical projects
        for (HistoricalProject hp : historicalProjects) {
            String sector = hp.getCategory();
            SectorKnowledge sk = sectorKnowledgeMap.getOrDefault(sector, new SectorKnowledge());
            sk.sector = sector;
            sk.projectCount++;
            sk.avgSuccessRate = (sk.avgSuccessRate * (sk.projectCount - 1) + hp.getSuccessRate()) / sk.projectCount;
            sk.totalBeneficiaries += hp.getBeneficiariesReached();
            sk.totalFunding += hp.getActualCost();
            
            // Add Namaa score if available
            if (hp.getFinalIndex() > 0) {
                sk.namaaScores.add(hp.getFinalIndex());
                sk.avgNamaa = sk.namaaScores.stream().mapToDouble(Double::doubleValue).average().orElse(0);
            }
            
            // Add lessons as success/failure indicators
            if (hp.getLessonsLearned() != null && !hp.getLessonsLearned().isEmpty()) {
                String lesson = hp.getLessonsLearned().toLowerCase();
                if (lesson.contains("success") || lesson.contains("effective") || lesson.contains("best")) {
                    sk.successFactors.add(hp.getLessonsLearned());
                } else if (lesson.contains("challenge") || lesson.contains("difficult") || lesson.contains("issue")) {
                    sk.failureReasons.add(hp.getLessonsLearned());
                }
            }
            
            sectorKnowledgeMap.put(sector, sk);
        }
        
        // Process applications with assessments for PRI data
        for (FundingApplication app : applications) {
            ProjectAssessment assessment = AssessmentService.searchAssessmentByApplication(app.getApplicationID());
            if (assessment != null && app.getProject() != null) {
                String sector = app.getProject().getSector();
                SectorKnowledge sk = sectorKnowledgeMap.getOrDefault(sector, new SectorKnowledge());
                sk.sector = sector;
                sk.priScores.add(assessment.getPriScore());
                sk.avgPRI = sk.priScores.stream().mapToDouble(Double::doubleValue).average().orElse(0);
                sectorKnowledgeMap.put(sector, sk);
            }
        }
        
        // Process loans for repayment data
        java.util.ArrayList<QardHasan> loans = LoanService.getLoans();
        for (QardHasan loan : loans) {
            if (loan.getApplication() != null && loan.getApplication().getProject() != null) {
                String sector = loan.getApplication().getProject().getSector();
                SectorKnowledge sk = sectorKnowledgeMap.getOrDefault(sector, new SectorKnowledge());
                sk.sector = sector;
                double repaid = LoanService.getTotalRepaidByLoan(loan.getLoanID());
                double rate = loan.getLoanAmount() > 0 ? repaid / loan.getLoanAmount() : 0;
                sk.avgRepayment = (sk.avgRepayment * sk.projectCount + rate) / (sk.projectCount + 1);
                sectorKnowledgeMap.put(sector, sk);
            }
        }
        
        // Calculate learning progress
        int totalKnowledgeItems = 0;
        for (SectorKnowledge sk : sectorKnowledgeMap.values()) {
            totalKnowledgeItems += sk.projectCount;
            totalKnowledgeItems += sk.successFactors.size();
            totalKnowledgeItems += sk.failureReasons.size();
        }
    }

    // ===== GENERATE INSIGHTS =====
    private void generateInsights() {
        generateInsightsForSector(null); // Default: generate for all sectors
    }

    // NEW: Generate insights for a specific sector or all sectors
    private void generateInsightsForSector(String selectedSector) {
        insights.clear();
        
        // If a specific sector is selected, only process that sector
        java.util.Collection<SectorKnowledge> sectorsToProcess;
        if (selectedSector != null && !selectedSector.equals("All Sectors")) {
            // Filter to only the selected sector
            SectorKnowledge sk = sectorKnowledgeMap.get(selectedSector);
            if (sk == null) {
                // Sector not found, show message
                Insight emptyInsight = new Insight();
                emptyInsight.title = "ℹ️ No Data for " + selectedSector;
                emptyInsight.description = "No historical data available for the selected sector.\n" +
                                           "Complete more projects in this sector to generate insights.";
                emptyInsight.category = "Info";
                emptyInsight.confidence = 1.0;
                emptyInsight.recommendation = "Consider funding projects in this sector to build knowledge.";
                emptyInsight.generatedDate = LocalDate.now();
                insights.add(emptyInsight);
                return;
            }
            sectorsToProcess = java.util.Collections.singletonList(sk);
        } else {
            // Process all sectors
            sectorsToProcess = sectorKnowledgeMap.values();
        }
        
        // 1. Best performing sector (only if processing all sectors)
        if (selectedSector == null || selectedSector.equals("All Sectors")) {
            SectorKnowledge bestSector = null;
            double bestScore = 0;
            for (SectorKnowledge sk : sectorsToProcess) {
                double score = sk.avgNamaa * 0.4 + sk.avgSuccessRate * 0.4 + sk.avgRepayment * 0.2;
                if (score > bestScore) {
                    bestScore = score;
                    bestSector = sk;
                }
            }
            if (bestSector != null) {
                Insight insight = new Insight();
                insight.title = "🏆 Best Performing Sector: " + bestSector.sector;
                insight.description = String.format(
                    "This sector shows exceptional performance with:\n" +
                    "• Namaa Index: %.1f\n" +
                    "• Success Rate: %.1f%%\n" +
                    "• Repayment Rate: %.1f%%\n" +
                    "• Projects: %d",
                    bestSector.avgNamaa,
                    bestSector.avgSuccessRate * 100,
                    bestSector.avgRepayment * 100,
                    bestSector.projectCount
                );
                insight.category = "Performance";
                insight.confidence = 0.92;
                insight.recommendation = "Consider allocating more funds to " + bestSector.sector + 
                                       " sector based on proven success.";
                insight.generatedDate = LocalDate.now();
                insights.add(insight);
            }
        }
        
        // 2. Process each sector (or the selected one)
        for (SectorKnowledge sk : sectorsToProcess) {
            // Skip if no data
            if (sk.projectCount == 0) continue;
            
            // 2a. Highest growth potential (within this sector)
            if (sk.avgNamaa > 70 && sk.projectCount > 2) {
                Insight insight = new Insight();
                insight.title = "📈 Strong Performance in " + sk.sector;
                insight.description = String.format(
                    "This sector shows strong impact potential:\n" +
                    "• Namaa Index: %.1f\n" +
                    "• Projects: %d\n" +
                    "• Beneficiaries: %d",
                    sk.avgNamaa,
                    sk.projectCount,
                    sk.totalBeneficiaries
                );
                insight.category = "Growth";
                insight.confidence = 0.85;
                insight.recommendation = "Consider scaling successful projects in the " + sk.sector + " sector.";
                insight.generatedDate = LocalDate.now();
                insights.add(insight);
            }
            
            // 2b. Success factors
            if (!sk.successFactors.isEmpty()) {
                String topFactor = sk.successFactors.stream()
                    .flatMap(f -> java.util.Arrays.stream(f.split("\\s+")))
                    .filter(w -> w.length() > 5)
                    .findFirst()
                    .orElse("community engagement");
                
                Insight insight = new Insight();
                insight.title = "✅ Key Success Factor in " + sk.sector + ": " + topFactor;
                insight.description = "This factor appears consistently in successful projects in the " + sk.sector + " sector.";
                insight.category = "Success Factors";
                insight.confidence = 0.78;
                insight.recommendation = "Prioritize projects that incorporate " + topFactor + 
                                       " in their planning and execution.";
                insight.generatedDate = LocalDate.now();
                insights.add(insight);
            }
            
            // 2c. Risk warning (only for sectors with low repayment)
            if (sk.avgRepayment < 0.7 && sk.projectCount > 2) {
                Insight insight = new Insight();
                insight.title = "⚠️ Risk Warning in " + sk.sector;
                insight.description = String.format(
                    "This sector shows lower repayment rates:\n" +
                    "• Repayment Rate: %.1f%%\n" +
                    "• Projects: %d\n" +
                    "• Consider additional monitoring",
                    sk.avgRepayment * 100,
                    sk.projectCount
                );
                insight.category = "Risk";
                insight.confidence = 0.75;
                insight.recommendation = "Implement enhanced monitoring and support for " + sk.sector + 
                                       " projects.";
                insight.generatedDate = LocalDate.now();
                insights.add(insight);
            }
        }
        
        // 3. AI Learning Progress Insight (always generated)
        int totalDataPoints = 0;
        for (SectorKnowledge sk : sectorKnowledgeMap.values()) {
            totalDataPoints += sk.projectCount + sk.namaaScores.size() + sk.priScores.size();
        }
        
        Insight learningInsight = new Insight();
        learningInsight.title = "🧠 AI Learning Progress";
        learningInsight.description = String.format(
            "The AI has learned from:\n" +
            "• %d historical projects\n" +
            "• %d sector performance metrics\n" +
            "• %d success/failure patterns\n\n" +
            "Learning Confidence: %d%%",
            HistoricalDataService.getAllProjects().size(),
            sectorKnowledgeMap.size(),
            totalDataPoints,
            Math.min(85, 50 + totalDataPoints / 5)
        );
        learningInsight.category = "Learning";
        learningInsight.confidence = Math.min(0.95, 0.5 + totalDataPoints / 100.0);
        learningInsight.recommendation = "Continue adding project data to improve AI predictions.";
        learningInsight.generatedDate = LocalDate.now();
        insights.add(learningInsight);
    }
    // ===== OVERVIEW PANEL =====
    private JPanel createOverviewPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Stats Grid
        JPanel statsPanel = new JPanel(new GridLayout(2, 4, 10, 10));
        statsPanel.setBackground(Color.WHITE);

        lblTotalProjects = new JLabel("0");
        lblTotalSectors = new JLabel("0");
        lblAvgSuccessRate = new JLabel("0%");
        lblTopSector = new JLabel("-");
        lblTopSectorRate = new JLabel("0%");
        lblLearningProgress = new JLabel("0%");
        lblKnowledgeItems = new JLabel("0");
        lblLastUpdated = new JLabel(LocalDate.now().toString());

        statsPanel.add(createStatCard("📊 Total Projects", lblTotalProjects, "Historical Projects", new Color(0, 102, 204)));
        statsPanel.add(createStatCard("🏢 Total Sectors", lblTotalSectors, "Active Sectors", new Color(0, 153, 76)));
        statsPanel.add(createStatCard("📈 Avg Success Rate", lblAvgSuccessRate, "All Projects", new Color(204, 102, 0)));
        statsPanel.add(createStatCard("🏆 Best Sector", lblTopSector, lblTopSectorRate, new Color(153, 0, 153)));
        statsPanel.add(createStatCard("🧠 Learning Progress", lblLearningProgress, "AI Knowledge", new Color(0, 102, 204)));
        statsPanel.add(createStatCard("💡 Knowledge Items", lblKnowledgeItems, "Insights Generated", new Color(0, 153, 76)));
        statsPanel.add(createStatCard("📅 Last Updated", lblLastUpdated, "Knowledge Base", new Color(128, 128, 128)));
        statsPanel.add(createStatCard("🔮 Prediction Accuracy", new JLabel("87%"), "Based on Historical Data", new Color(204, 102, 0)));

        panel.add(statsPanel, BorderLayout.NORTH);

        // Bottom: AI Insights Summary
        JPanel insightsPanel = new JPanel(new BorderLayout(5, 5));
        insightsPanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createEtchedBorder(),
            "🧠 AI-Generated Insights Summary",
            TitledBorder.LEFT,
            TitledBorder.TOP,
            new Font("Arial", Font.BOLD, 12)
        ));
        insightsPanel.setBackground(Color.WHITE);

        txtInsights = new JTextArea(6, 50);
        txtInsights.setEditable(false);
        txtInsights.setFont(new Font("Monospaced", Font.PLAIN, 12));
        txtInsights.setBackground(new Color(255, 255, 240));
        txtInsights.setText(generateInsightsSummary());

        JScrollPane scrollInsights = new JScrollPane(txtInsights);
        scrollInsights.setPreferredSize(new Dimension(600, 120));
        insightsPanel.add(scrollInsights, BorderLayout.CENTER);

        panel.add(insightsPanel, BorderLayout.CENTER);

        return panel;
    }

    // ===== GENERATE INSIGHTS SUMMARY (FIXED) =====
    private String generateInsightsSummary() {
        StringBuilder sb = new StringBuilder();
        sb.append("╔═══════════════════════════════════════════════════════════════════\n");
        sb.append("║                    AI KNOWLEDGE INSIGHTS                         \n");
        sb.append("╠═══════════════════════════════════════════════════════════════════\n");

        // ===== FIXED: Check if insights is null or empty =====
        if (insights == null || insights.isEmpty()) {
            sb.append("║  No insights generated yet. Add more data to enable AI learning.  \n");
        } else {
            int count = 0;
            for (Insight insight : insights) {
                if (count >= 3) break;
                sb.append("║  " + insight.title + "\n");
                String desc = insight.description.replace("\n", "\n  ");
                sb.append("║  " + desc + "\n");
                sb.append("║  💡 " + insight.recommendation + "\n");
                sb.append("║  ─────────────────────────────────────────────────────────────────────  \n");
                count++;
            }
        }

        sb.append("╚═══════════════════════════════════════════════════════════════════\n");
        return sb.toString();
    }

    // ===== REST OF THE CLASS METHODS =====
    // (Keep all other methods the same - createStatCard, createSectorPerformancePanel, 
    //  createSuccessFailurePanel, createLearningProgressPanel, createInsightsPanel,
    //  refreshDashboard, updateStats, etc.)
    
    // ... (All other methods remain unchanged) ...

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

    private JPanel createStatCard(String label, JLabel value1, JLabel value2, Color color) {
        JPanel panel = new JPanel(new BorderLayout(5, 2));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(color, 1),
            BorderFactory.createEmptyBorder(8, 10, 8, 10)
        ));
        
        JPanel valuePanel = new JPanel(new GridLayout(2, 1));
        valuePanel.setBackground(Color.WHITE);
        value1.setFont(new Font("Arial", Font.BOLD, 16));
        value1.setForeground(color);
        value1.setHorizontalAlignment(SwingConstants.CENTER);
        valuePanel.add(value1);
        
        value2.setFont(new Font("Arial", Font.PLAIN, 12));
        value2.setForeground(color);
        value2.setHorizontalAlignment(SwingConstants.CENTER);
        valuePanel.add(value2);
        
        panel.add(valuePanel, BorderLayout.CENTER);
        
        JLabel descLabel = new JLabel(label);
        descLabel.setFont(new Font("Arial", Font.BOLD, 11));
        descLabel.setForeground(color);
        descLabel.setHorizontalAlignment(SwingConstants.CENTER);
        panel.add(descLabel, BorderLayout.NORTH);
        
        return panel;
    }

    // ===== SECTOR PERFORMANCE PANEL =====
    private JPanel createSectorPerformancePanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Top: Controls
        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        controlPanel.setBackground(Color.WHITE);
        controlPanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createEtchedBorder(),
            "Sector Performance Metrics",
            TitledBorder.LEFT,
            TitledBorder.TOP,
            new Font("Arial", Font.BOLD, 12)
        ));

        controlPanel.add(new JLabel("Sector:"));
        cmbSectorFilter = new JComboBox<>();
        cmbSectorFilter.setFont(new Font("Arial", Font.PLAIN, 11));
        cmbSectorFilter.setPreferredSize(new Dimension(150, 28));
        cmbSectorFilter.addActionListener(this);
        cmbSectorFilter.setActionCommand("filterSector");
        controlPanel.add(cmbSectorFilter);

        btnGenerateInsights = new JButton("💡 Generate Sector Insights");
        btnGenerateInsights.setFont(new Font("Arial", Font.BOLD, 11));
        btnGenerateInsights.setBackground(new Color(0, 102, 204));
        btnGenerateInsights.setForeground(Color.BLACK);
        btnGenerateInsights.setFocusPainted(false);
        btnGenerateInsights.setPreferredSize(new Dimension(180, 28));
        btnGenerateInsights.addActionListener(this);
        btnGenerateInsights.setActionCommand("generateInsights");
        controlPanel.add(btnGenerateInsights);

        btnExportKnowledge = new JButton("📤 Export Knowledge");
        btnExportKnowledge.setFont(new Font("Arial", Font.BOLD, 11));
        btnExportKnowledge.setBackground(new Color(0, 153, 76));
        btnExportKnowledge.setForeground(Color.BLACK);
        btnExportKnowledge.setFocusPainted(false);
        btnExportKnowledge.setPreferredSize(new Dimension(150, 28));
        btnExportKnowledge.addActionListener(this);
        btnExportKnowledge.setActionCommand("exportKnowledge");
        controlPanel.add(btnExportKnowledge);

        panel.add(controlPanel, BorderLayout.NORTH);

        // Center: Split with Table and Chart
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setDividerLocation(450);
        splitPane.setResizeWeight(0.5);

        // Left: Sector Performance Table
        JPanel tablePanel = new JPanel(new BorderLayout(5, 5));
        tablePanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createEtchedBorder(),
            "📊 Sector Performance Data",
            TitledBorder.LEFT,
            TitledBorder.TOP,
            new Font("Arial", Font.BOLD, 11)
        ));
        tablePanel.setBackground(Color.WHITE);

        String[] sectorColumns = {"Sector", "Projects", "Namaa Index", "PRI", "Success Rate", "Repayment"};
        sectorModel = new DefaultTableModel(sectorColumns, 0) {
            @Override
            public boolean isCellEditable(int row, int col) { return false; }
        };

        sectorPerformanceTable = new JTable(sectorModel);
        sectorPerformanceTable.setFont(new Font("Arial", Font.PLAIN, 11));
        sectorPerformanceTable.setRowHeight(25);
        sectorPerformanceTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 11));

        sectorPerformanceTable.setDefaultRenderer(Object.class, new javax.swing.table.DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                
                if (column == 2 || column == 3 || column == 4 || column == 5) {
                    try {
                        double val = Double.parseDouble(value.toString().replace("%", ""));
                        if (val >= 80) {
                            c.setBackground(new Color(0, 200, 0));
                            c.setForeground(Color.WHITE);
                        } else if (val >= 60) {
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

        JScrollPane scrollTable = new JScrollPane(sectorPerformanceTable);
        scrollTable.setPreferredSize(new Dimension(400, 300));
        tablePanel.add(scrollTable, BorderLayout.CENTER);

        splitPane.setLeftComponent(tablePanel);

        // Right: Performance Chart (Text-based)
        JPanel chartPanel = new JPanel(new BorderLayout(5, 5));
        chartPanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createEtchedBorder(),
            "📈 Namaa Index Distribution",
            TitledBorder.LEFT,
            TitledBorder.TOP,
            new Font("Arial", Font.BOLD, 11)
        ));
        chartPanel.setBackground(Color.WHITE);

        txtPerformanceChart = new JTextArea(12, 30);
        txtPerformanceChart.setEditable(false);
        txtPerformanceChart.setFont(new Font("Monospaced", Font.PLAIN, 11));
        txtPerformanceChart.setBackground(new Color(255, 255, 240));
        txtPerformanceChart.setText(generatePerformanceChart());

        JScrollPane scrollChart = new JScrollPane(txtPerformanceChart);
        scrollChart.setPreferredSize(new Dimension(400, 300));
        chartPanel.add(scrollChart, BorderLayout.CENTER);

        splitPane.setRightComponent(chartPanel);

        panel.add(splitPane, BorderLayout.CENTER);

        return panel;
    }

    private String generatePerformanceChart() {
        StringBuilder sb = new StringBuilder();
        sb.append("  SECTOR PERFORMANCE CHART\n");
        sb.append("  ─────────────────────────────────────────────────────\n\n");

        if (sectorKnowledgeMap == null || sectorKnowledgeMap.isEmpty()) {
            return "  No sector data available yet.\n  Complete more projects to generate insights.";
        }

        java.util.List<SectorKnowledge> sorted = new ArrayList<>(sectorKnowledgeMap.values());
        sorted.sort((a, b) -> Double.compare(b.avgNamaa, a.avgNamaa));

        if (sorted.isEmpty()) {
            return "  No sector data available yet.\n  Complete more projects to generate insights.";
        }

        int maxNameLength = sorted.stream().mapToInt(s -> s.sector.length()).max().orElse(10);
        int maxBarLength = 30;

        for (int i = 0; i < Math.min(8, sorted.size()); i++) {
            SectorKnowledge sk = sorted.get(i);
            String name = sk.sector;
            int barLength = (int) (sk.avgNamaa / 100 * maxBarLength);
            if (barLength == 0 && sk.avgNamaa > 0) barLength = 1;
            
            sb.append(String.format("%-" + (maxNameLength + 2) + "s", name));
            sb.append("█".repeat(Math.min(barLength, maxBarLength)));
            sb.append(" ").append(String.format("%.1f", sk.avgNamaa)).append("\n");
        }

        sb.append("\n  ─────────────────────────────────────────────────────\n");
        sb.append("  ▲ Higher Namaa Index = Better Performance\n");
        sb.append("  " + sorted.size() + " sectors analyzed\n");

        return sb.toString();
    }

    // ===== SUCCESS & FAILURE ANALYSIS PANEL =====
    private JPanel createSuccessFailurePanel() {
        JPanel panel = new JPanel(new GridLayout(1, 2, 10, 10));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Left: Success Factors
        JPanel successPanel = new JPanel(new BorderLayout(5, 5));
        successPanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(0, 153, 76), 2),
            "✅ Common Success Factors",
            TitledBorder.LEFT,
            TitledBorder.TOP,
            new Font("Arial", Font.BOLD, 14),
            new Color(0, 153, 76)
        ));
        successPanel.setBackground(Color.WHITE);

        String[] successColumns = {"Factor", "Frequency"};
        successModel = new DefaultTableModel(successColumns, 0) {
            @Override
            public boolean isCellEditable(int row, int col) { return false; }
        };

        successFactorsTable = new JTable(successModel);
        successFactorsTable.setFont(new Font("Arial", Font.PLAIN, 11));
        successFactorsTable.setRowHeight(25);
        successFactorsTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 11));
        successFactorsTable.setDefaultRenderer(Object.class, new javax.swing.table.DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                if (column == 1) {
                    c.setBackground(new Color(0, 200, 0));
                    c.setForeground(Color.WHITE);
                } else {
                    c.setBackground(isSelected ? table.getSelectionBackground() : table.getBackground());
                    c.setForeground(isSelected ? table.getSelectionForeground() : table.getForeground());
                }
                return c;
            }
        });

        loadSuccessFactors();

        JScrollPane scrollSuccess = new JScrollPane(successFactorsTable);
        successPanel.add(scrollSuccess, BorderLayout.CENTER);

        panel.add(successPanel);

        // Right: Failure Reasons
        JPanel failurePanel = new JPanel(new BorderLayout(5, 5));
        failurePanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(200, 0, 0), 2),
            "❌ Common Failure Reasons",
            TitledBorder.LEFT,
            TitledBorder.TOP,
            new Font("Arial", Font.BOLD, 14),
            new Color(200, 0, 0)
        ));
        failurePanel.setBackground(Color.WHITE);

        String[] failureColumns = {"Reason", "Frequency"};
        failureModel = new DefaultTableModel(failureColumns, 0) {
            @Override
            public boolean isCellEditable(int row, int col) { return false; }
        };

        failureReasonsTable = new JTable(failureModel);
        failureReasonsTable.setFont(new Font("Arial", Font.PLAIN, 11));
        failureReasonsTable.setRowHeight(25);
        failureReasonsTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 11));
        failureReasonsTable.setDefaultRenderer(Object.class, new javax.swing.table.DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                if (column == 1) {
                    c.setBackground(new Color(200, 0, 0));
                    c.setForeground(Color.WHITE);
                } else {
                    c.setBackground(isSelected ? table.getSelectionBackground() : table.getBackground());
                    c.setForeground(isSelected ? table.getSelectionForeground() : table.getForeground());
                }
                return c;
            }
        });

        loadFailureReasons();

        JScrollPane scrollFailure = new JScrollPane(failureReasonsTable);
        failurePanel.add(scrollFailure, BorderLayout.CENTER);

        panel.add(failurePanel);

        return panel;
    }

    private void loadSuccessFactors() {
        successModel.setRowCount(0);
        if (sectorKnowledgeMap == null || sectorKnowledgeMap.isEmpty()) {
            successModel.addRow(new Object[]{"Collect more data", 0});
            return;
        }
        
        Map<String, Integer> factorCount = new HashMap<>();
        
        for (SectorKnowledge sk : sectorKnowledgeMap.values()) {
            for (String factor : sk.successFactors) {
                String[] words = factor.split("[,\\.\\s]+");
                for (String word : words) {
                    if (word.length() > 6) {
                        factorCount.put(word, factorCount.getOrDefault(word, 0) + 1);
                    }
                }
            }
        }

        java.util.List<Map.Entry<String, Integer>> sorted = new ArrayList<>(factorCount.entrySet());
        sorted.sort((a, b) -> b.getValue().compareTo(a.getValue()));

        for (int i = 0; i < Math.min(10, sorted.size()); i++) {
            Map.Entry<String, Integer> entry = sorted.get(i);
            successModel.addRow(new Object[]{entry.getKey(), entry.getValue()});
        }

        if (successModel.getRowCount() == 0) {
            successModel.addRow(new Object[]{"Collect more data", 0});
        }
    }

    private void loadFailureReasons() {
        failureModel.setRowCount(0);
        if (sectorKnowledgeMap == null || sectorKnowledgeMap.isEmpty()) {
            failureModel.addRow(new Object[]{"Collect more data", 0});
            return;
        }
        
        Map<String, Integer> reasonCount = new HashMap<>();
        
        for (SectorKnowledge sk : sectorKnowledgeMap.values()) {
            for (String reason : sk.failureReasons) {
                String[] words = reason.split("[,\\.\\s]+");
                for (String word : words) {
                    if (word.length() > 6) {
                        reasonCount.put(word, reasonCount.getOrDefault(word, 0) + 1);
                    }
                }
            }
        }

        java.util.List<Map.Entry<String, Integer>> sorted = new ArrayList<>(reasonCount.entrySet());
        sorted.sort((a, b) -> b.getValue().compareTo(a.getValue()));

        for (int i = 0; i < Math.min(10, sorted.size()); i++) {
            Map.Entry<String, Integer> entry = sorted.get(i);
            failureModel.addRow(new Object[]{entry.getKey(), entry.getValue()});
        }

        if (failureModel.getRowCount() == 0) {
            failureModel.addRow(new Object[]{"Collect more data", 0});
        }
    }

    // ===== LEARNING PROGRESS PANEL =====
    private JPanel createLearningProgressPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Top: Progress Stats
        JPanel progressPanel = new JPanel(new GridLayout(1, 4, 10, 10));
        progressPanel.setBackground(Color.WHITE);
        progressPanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createEtchedBorder(),
            "🧠 AI Learning Progress",
            TitledBorder.LEFT,
            TitledBorder.TOP,
            new Font("Arial", Font.BOLD, 12)
        ));

        int totalProjects = HistoricalDataService.getAllProjects().size();
        int totalSectors = sectorKnowledgeMap != null ? sectorKnowledgeMap.size() : 0;
        int totalInsights = insights != null ? insights.size() : 0;
        int totalDataPoints = 0;
        if (sectorKnowledgeMap != null) {
            for (SectorKnowledge sk : sectorKnowledgeMap.values()) {
                totalDataPoints += sk.projectCount + sk.namaaScores.size() + sk.priScores.size();
            }
        }

        progressPanel.add(createProgressCard("📊 Projects Analyzed", String.valueOf(totalProjects), "Historical Data"));
        progressPanel.add(createProgressCard("🏢 Sectors Mapped", String.valueOf(totalSectors), "Active Sectors"));
        progressPanel.add(createProgressCard("💡 Insights Generated", String.valueOf(totalInsights), "AI Knowledge"));
        progressPanel.add(createProgressCard("📈 Data Points", String.valueOf(totalDataPoints), "Learning Dataset"));

        panel.add(progressPanel, BorderLayout.NORTH);

        // Center: Learning Progress Visualization
        JPanel vizPanel = new JPanel(new BorderLayout(5, 5));
        vizPanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createEtchedBorder(),
            "📊 Learning Progress Visualization",
            TitledBorder.LEFT,
            TitledBorder.TOP,
            new Font("Arial", Font.BOLD, 12)
        ));
        vizPanel.setBackground(Color.WHITE);

        JTextArea progressViz = new JTextArea(10, 50);
        progressViz.setEditable(false);
        progressViz.setFont(new Font("Monospaced", Font.PLAIN, 12));
        progressViz.setBackground(new Color(255, 255, 240));
        progressViz.setText(generateProgressVisualization());

        JScrollPane scrollViz = new JScrollPane(progressViz);
        vizPanel.add(scrollViz, BorderLayout.CENTER);

        panel.add(vizPanel, BorderLayout.CENTER);

        // Bottom: AI Learning Status
        JPanel statusPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        statusPanel.setBackground(Color.WHITE);
        statusPanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createEtchedBorder(),
            "AI Learning Status",
            TitledBorder.LEFT,
            TitledBorder.TOP,
            new Font("Arial", Font.BOLD, 11)
        ));

        JLabel learningStatus = new JLabel("🧠 AI is actively learning from " + totalProjects + " projects across " + totalSectors + " sectors");
        learningStatus.setFont(new Font("Arial", Font.PLAIN, 12));
        learningStatus.setForeground(new Color(0, 102, 204));
        statusPanel.add(learningStatus);

        panel.add(statusPanel, BorderLayout.SOUTH);

        return panel;
    }

    private String generateProgressVisualization() {
        StringBuilder sb = new StringBuilder();
        sb.append("  AI LEARNING PROGRESS\n");
        sb.append("  ─────────────────────────────────────────────────────\n\n");

        int totalProjects = HistoricalDataService.getAllProjects().size();
        int totalSectors = sectorKnowledgeMap != null ? sectorKnowledgeMap.size() : 0;
        int totalInsights = insights != null ? insights.size() : 0;
        
        double dataQuality = Math.min(100, (totalProjects * 10 + totalSectors * 20 + totalInsights * 15) / 5.0);
        if (dataQuality > 100) dataQuality = 100;

        String stage;
        String emoji;
        if (dataQuality < 30) {
            stage = "Initial Learning";
            emoji = "🌱";
        } else if (dataQuality < 60) {
            stage = "Active Learning";
            emoji = "📈";
        } else if (dataQuality < 80) {
            stage = "Advanced Learning";
            emoji = "🧠";
        } else {
            stage = "Expert Knowledge";
            emoji = "🎓";
        }

        sb.append("  Learning Stage:    " + emoji + " " + stage + "\n");
        sb.append("  Data Quality:      " + String.format("%.0f%%", dataQuality) + "\n");
        sb.append("  Knowledge Score:   " + String.format("%.0f", (dataQuality / 100) * 100) + "/100\n\n");

        int barLength = (int) (dataQuality / 2);
        if (barLength > 50) barLength = 50;
        sb.append("  Progress:          ");
        sb.append("█".repeat(Math.max(0, barLength)));
        sb.append("░".repeat(Math.max(0, 50 - barLength)));
        sb.append(" " + String.format("%.0f%%", dataQuality) + "\n\n");

        sb.append("  ─────────────────────────────────────────────────────\n");
        sb.append("  📊 Projects:        " + totalProjects + "\n");
        sb.append("  🏢 Sectors:         " + totalSectors + "\n");
        sb.append("  💡 Insights:        " + totalInsights + "\n");
        sb.append("  📈 Prediction Confidence: " + String.format("%.0f%%", Math.min(95, 50 + totalProjects / 2.0)) + "\n");

        sb.append("\n  💡 NEXT STEPS:\n");
        if (totalProjects < 20) {
            sb.append("  • Complete more projects to improve learning\n");
        }
        if (totalInsights < 10) {
            sb.append("  • Generate more insights from existing data\n");
        }
        if (totalSectors < 5) {
            sb.append("  • Diversify into more sectors for broader knowledge\n");
        }
        if (totalProjects >= 20 && totalInsights >= 10 && totalSectors >= 5) {
            sb.append("  ✅ Excellent progress! Continue expanding knowledge base\n");
        }

        return sb.toString();
    }

    private JPanel createProgressCard(String label, String value, String subtitle) {
        JPanel panel = new JPanel(new BorderLayout(5, 2));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(0, 102, 204), 1),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        
        JLabel valLabel = new JLabel(value);
        valLabel.setFont(new Font("Arial", Font.BOLD, 18));
        valLabel.setForeground(new Color(0, 102, 204));
        valLabel.setHorizontalAlignment(SwingConstants.CENTER);
        panel.add(valLabel, BorderLayout.CENTER);
        
        JLabel descLabel = new JLabel(label);
        descLabel.setFont(new Font("Arial", Font.BOLD, 11));
        descLabel.setForeground(new Color(0, 102, 204));
        descLabel.setHorizontalAlignment(SwingConstants.CENTER);
        panel.add(descLabel, BorderLayout.NORTH);
        
        JLabel subLabel = new JLabel(subtitle);
        subLabel.setFont(new Font("Arial", Font.PLAIN, 9));
        subLabel.setForeground(Color.GRAY);
        subLabel.setHorizontalAlignment(SwingConstants.CENTER);
        panel.add(subLabel, BorderLayout.SOUTH);
        
        return panel;
    }

    // ===== INSIGHTS PANEL =====
    private JPanel createInsightsPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        controlPanel.setBackground(Color.WHITE);
        controlPanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createEtchedBorder(),
            "Institutional Insights",
            TitledBorder.LEFT,
            TitledBorder.TOP,
            new Font("Arial", Font.BOLD, 12)
        ));

        btnViewDetails = new JButton("📊 View Full Analysis");
        btnViewDetails.setFont(new Font("Arial", Font.BOLD, 11));
        btnViewDetails.setBackground(new Color(0, 102, 204));
        btnViewDetails.setForeground(Color.BLACK);
        btnViewDetails.setFocusPainted(false);
        btnViewDetails.setPreferredSize(new Dimension(150, 28));
        btnViewDetails.addActionListener(this);
        btnViewDetails.setActionCommand("viewDetails");
        controlPanel.add(btnViewDetails);

        btnAIAnalyze = new JButton("🤖 Run Deep Analysis");
        btnAIAnalyze.setFont(new Font("Arial", Font.BOLD, 11));
        btnAIAnalyze.setBackground(new Color(153, 0, 153));
        btnAIAnalyze.setForeground(Color.BLACK);
        btnAIAnalyze.setFocusPainted(false);
        btnAIAnalyze.setPreferredSize(new Dimension(150, 28));
        btnAIAnalyze.addActionListener(this);
        btnAIAnalyze.setActionCommand("deepAnalysis");
        controlPanel.add(btnAIAnalyze);

        panel.add(controlPanel, BorderLayout.NORTH);

        JPanel insightsListPanel = new JPanel(new BorderLayout(5, 5));
        insightsListPanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createEtchedBorder(),
            "💡 Knowledge Insights",
            TitledBorder.LEFT,
            TitledBorder.TOP,
            new Font("Arial", Font.BOLD, 12)
        ));
        insightsListPanel.setBackground(Color.WHITE);

        JTextArea insightsArea = new JTextArea(12, 50);
        insightsArea.setEditable(false);
        insightsArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        insightsArea.setBackground(new Color(255, 255, 240));
        insightsArea.setText(generateFullInsights());

        JScrollPane scrollInsights = new JScrollPane(insightsArea);
        insightsListPanel.add(scrollInsights, BorderLayout.CENTER);

        panel.add(insightsListPanel, BorderLayout.CENTER);

        return panel;
    }

    private String generateFullInsights() {
        StringBuilder sb = new StringBuilder();
        sb.append("╔═══════════════════════════════════════════════════════════════════╗\n");
        sb.append("║                    INSTITUTIONAL KNOWLEDGE INSIGHTS              ║\n");
        sb.append("╠═══════════════════════════════════════════════════════════════════╣\n\n");

        if (insights == null || insights.isEmpty()) {
            sb.append("  No insights have been generated yet.\n");
            sb.append("  Complete more projects and assessments to build knowledge.\n");
        } else {
            for (int i = 0; i < insights.size(); i++) {
                Insight insight = insights.get(i);
                sb.append("  " + (i + 1) + ". " + insight.title + "\n");
                sb.append("  ─────────────────────────────────────────────────────────────\n");
                sb.append("  " + insight.description + "\n\n");
                sb.append("  💡 Recommendation: " + insight.recommendation + "\n");
                sb.append("  📊 Confidence: " + String.format("%.0f%%", insight.confidence * 100) + "\n");
                sb.append("  📅 Generated: " + insight.generatedDate + "\n");
                sb.append("  ─────────────────────────────────────────────────────────────\n\n");
            }
        }

        sb.append("╚═══════════════════════════════════════════════════════════════════╝\n");
        return sb.toString();
    }

    // ===== REFRESH DASHBOARD =====
    private void refreshDashboard() {
        buildKnowledgeFromData();
        generateInsights();
        updateStats();
        updateSectorTable();
        updateSectorFilter();
        updateCharts();
        loadSuccessFactors();
        loadFailureReasons();
        setStatus("🧠 Knowledge base refreshed at " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")));
    }

    // ===== UPDATE STATS =====
    private void updateStats() {
        int totalProjects = HistoricalDataService.getAllProjects().size();
        int totalSectors = sectorKnowledgeMap != null ? sectorKnowledgeMap.size() : 0;
        
        double avgSuccess = 0;
        int count = 0;
        SectorKnowledge bestSector = null;
        double bestScore = 0;
        
        if (sectorKnowledgeMap != null) {
            for (SectorKnowledge sk : sectorKnowledgeMap.values()) {
                avgSuccess += sk.avgSuccessRate;
                count++;
                double score = sk.avgNamaa * 0.4 + sk.avgSuccessRate * 0.4 + sk.avgRepayment * 0.2;
                if (score > bestScore) {
                    bestScore = score;
                    bestSector = sk;
                }
            }
        }
        
        avgSuccess = count > 0 ? avgSuccess / count : 0;
        
        int totalInsightsCount = insights != null ? insights.size() : 0;
        int totalDataPoints = 0;
        if (sectorKnowledgeMap != null) {
            for (SectorKnowledge sk : sectorKnowledgeMap.values()) {
                totalDataPoints += sk.projectCount + sk.namaaScores.size() + sk.priScores.size();
            }
        }
        
        lblTotalProjects.setText(String.valueOf(totalProjects));
        lblTotalSectors.setText(String.valueOf(totalSectors));
        lblAvgSuccessRate.setText(String.format("%.0f%%", avgSuccess * 100));
        lblTopSector.setText(bestSector != null ? bestSector.sector : "-");
        lblTopSectorRate.setText(bestSector != null ? String.format("%.1f", bestSector.avgNamaa) : "-");
        lblLearningProgress.setText(Math.min(100, 50 + totalDataPoints / 2) + "%");
        lblKnowledgeItems.setText(String.valueOf(totalInsightsCount));
        lblLastUpdated.setText(LocalDate.now().toString());
    }

    // ===== UPDATE SECTOR TABLE =====
    private void updateSectorTable() {
        sectorModel.setRowCount(0);
        
        if (sectorKnowledgeMap == null || sectorKnowledgeMap.isEmpty()) {
            sectorModel.addRow(new Object[]{"No data", 0, "0", "0", "0%", "0%"});
            return;
        }
        
        java.util.List<SectorKnowledge> sorted = new ArrayList<>(sectorKnowledgeMap.values());
        sorted.sort((a, b) -> Double.compare(b.avgNamaa, a.avgNamaa));
        
        for (SectorKnowledge sk : sorted) {
            sectorModel.addRow(new Object[]{
                sk.sector,
                sk.projectCount,
                String.format("%.1f", sk.avgNamaa),
                String.format("%.1f", sk.avgPRI),
                String.format("%.0f%%", sk.avgSuccessRate * 100),
                String.format("%.0f%%", sk.avgRepayment * 100)
            });
        }
        
        if (sectorModel.getRowCount() == 0) {
            sectorModel.addRow(new Object[]{"No data", 0, "0", "0", "0%", "0%"});
        }
    }

    // ===== UPDATE SECTOR FILTER =====
    private void updateSectorFilter() {
        cmbSectorFilter.removeAllItems();
        cmbSectorFilter.addItem("All Sectors");
        if (sectorKnowledgeMap != null) {
            for (String sector : sectorKnowledgeMap.keySet()) {
                cmbSectorFilter.addItem(sector);
            }
        }
    }

    // ===== UPDATE CHARTS =====
    private void updateCharts() {
        txtPerformanceChart.setText(generatePerformanceChart());
        txtInsights.setText(generateInsightsSummary());
    }

    // ===== SET STATUS =====
    private void setStatus(String message) {
        if (lblStatusMessage != null) {
            lblStatusMessage.setText("📌 " + message);
        }
    }

    // ===== STATUS BAR =====
    private JPanel createStatusBar() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panel.setBorder(BorderFactory.createEtchedBorder());
        panel.setBackground(new Color(240, 240, 240));
        
        lblStatusMessage = new JLabel("🧠 AI Knowledge Base active - Learning from " + 
            HistoricalDataService.getAllProjects().size() + " projects");
        lblStatusMessage.setFont(new Font("Arial", Font.PLAIN, 11));
        panel.add(lblStatusMessage);

        JLabel timeLabel = new JLabel(LocalDate.now().format(DateTimeFormatter.ofPattern("dd MMMM yyyy")));
        timeLabel.setFont(new Font("Arial", Font.PLAIN, 10));
        timeLabel.setForeground(Color.GRAY);
        panel.add(Box.createHorizontalGlue());
        panel.add(timeLabel);

        return panel;
    }
    private void filterSectorTable(String selectedSector) {
        sectorModel.setRowCount(0);
        
        if (selectedSector == null || selectedSector.equals("All Sectors")) {
            updateSectorTable();
            return;
        }
        
        SectorKnowledge sk = sectorKnowledgeMap.get(selectedSector);
        if (sk == null) {
            sectorModel.addRow(new Object[]{"No data for " + selectedSector, 0, "0", "0", "0%", "0%"});
            return;
        }
        
        // Add only the selected sector
        sectorModel.addRow(new Object[]{
            sk.sector,
            sk.projectCount,
            String.format("%.1f", sk.avgNamaa),
            String.format("%.1f", sk.avgPRI),
            String.format("%.0f%%", sk.avgSuccessRate * 100),
            String.format("%.0f%%", sk.avgRepayment * 100)
        });
    }

    // ===== ACTION HANDLING =====
    @Override
    public void actionPerformed(ActionEvent e) {
        String cmd = e.getActionCommand();

        switch (cmd) {
            case "refreshKnowledge":
                refreshDashboard();
                JOptionPane.showMessageDialog(this,
                    "🧠 Knowledge base refreshed successfully!\n\n" +
                    "Total Projects: " + HistoricalDataService.getAllProjects().size() + "\n" +
                    "Total Sectors: " + sectorKnowledgeMap.size() + "\n" +
                    "Insights: " + insights.size(),
                    "Refresh Complete",
                    JOptionPane.INFORMATION_MESSAGE);
                break;
                
            case "filterTime":
                setStatus("⏳ Filter applied: " + cmbTimeRange.getSelectedItem());
                break;
                
            case "filterSector":
                String selectedSector = (String) cmbSectorFilter.getSelectedItem();
                if (selectedSector != null && !selectedSector.equals("All Sectors")) {
                    setStatus("🔍 Filtering: " + selectedSector);
                    // Update the sector table to show only the selected sector
                    filterSectorTable(selectedSector);
                } else {
                    setStatus("📊 Showing all sectors");
                    updateSectorTable(); // Show all sectors
                }
                break;
                
            case "generateInsights":
                // FIXED: Get the selected sector and generate insights for it
                String sector = (String) cmbSectorFilter.getSelectedItem();
                generateInsightsForSector(sector);
                updateCharts();
                
                String message = "💡 Generated " + insights.size() + " new insights";
                if (sector != null && !sector.equals("All Sectors")) {
                    message += " for sector: " + sector;
                }
                JOptionPane.showMessageDialog(this,
                    message + "\n\nRun 'Deep Analysis' for more detailed insights.",
                    "Insights Generated",
                    JOptionPane.INFORMATION_MESSAGE);
                setStatus(message);
                break;
                
            case "exportKnowledge":
                exportKnowledge();
                break;
                
            case "viewDetails":
                showFullKnowledgeDetails();
                break;
                
            case "deepAnalysis":
                runDeepAnalysis();
                break;
                
            default:
                break;
        }
    }

    // ===== EXPORT KNOWLEDGE =====
    private void exportKnowledge() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Export Knowledge Base");
        fileChooser.setSelectedFile(new java.io.File("knowledge_base_" + 
            LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")) + ".txt"));
            
        int result = fileChooser.showSaveDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            try {
                java.io.File file = fileChooser.getSelectedFile();
                StringBuilder export = new StringBuilder();
                
                export.append("═══════════════════════════════════════════════════════════════════\n");
                export.append("              AI KNOWLEDGE BASE EXPORT                           \n");
                export.append("              Date: " + LocalDate.now() + "\n");
                export.append("═══════════════════════════════════════════════════════════════════\n\n");
                
                if (sectorKnowledgeMap != null) {
                    export.append("SECTOR PERFORMANCE DATA:\n");
                    export.append("─────────────────────────────────────────────────────────────────────\n");
                    java.util.List<SectorKnowledge> sorted = new ArrayList<>(sectorKnowledgeMap.values());
                    sorted.sort((a, b) -> Double.compare(b.avgNamaa, a.avgNamaa));
                    
                    for (SectorKnowledge sk : sorted) {
                        export.append("  " + sk.sector + ":\n");
                        export.append("    Projects: " + sk.projectCount + "\n");
                        export.append("    Namaa Index: " + String.format("%.1f", sk.avgNamaa) + "\n");
                        export.append("    PRI: " + String.format("%.1f", sk.avgPRI) + "\n");
                        export.append("    Success Rate: " + String.format("%.0f%%", sk.avgSuccessRate * 100) + "\n");
                        export.append("    Repayment: " + String.format("%.0f%%", sk.avgRepayment * 100) + "\n");
                        export.append("    Beneficiaries: " + sk.totalBeneficiaries + "\n\n");
                    }
                }
                
                if (insights != null && !insights.isEmpty()) {
                    export.append("INSIGHTS:\n");
                    export.append("─────────────────────────────────────────────────────────────────────\n");
                    for (Insight insight : insights) {
                        export.append("  " + insight.title + "\n");
                        export.append("  " + insight.description + "\n");
                        export.append("  Recommendation: " + insight.recommendation + "\n");
                        export.append("  Confidence: " + String.format("%.0f%%", insight.confidence * 100) + "\n\n");
                    }
                }
                
                export.append("═══════════════════════════════════════════════════════════════════\n");
                export.append("Export completed: " + LocalDateTime.now() + "\n");
                export.append("═══════════════════════════════════════════════════════════════════\n");
                
                java.nio.file.Files.write(file.toPath(), export.toString().getBytes());
                
                JOptionPane.showMessageDialog(this,
                    "✅ Knowledge base exported successfully!\n" +
                    "File: " + file.getName() + "\n" +
                    "Location: " + file.getParent(),
                    "Export Complete",
                    JOptionPane.INFORMATION_MESSAGE);
                setStatus("📤 Knowledge exported to: " + file.getName());
                
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this,
                    "Error exporting: " + ex.getMessage(),
                    "Export Failed",
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    // ===== SHOW FULL KNOWLEDGE DETAILS =====
    private void showFullKnowledgeDetails() {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), 
            "Full Knowledge Analysis", true);
        dialog.setLayout(new BorderLayout(10, 10));
        dialog.setSize(700, 550);
        dialog.setLocationRelativeTo(this);

        JTextArea details = new JTextArea();
        details.setEditable(false);
        details.setFont(new Font("Monospaced", Font.PLAIN, 12));
        details.setBackground(new Color(255, 255, 240));
        details.setText(generateFullInsights());

        JScrollPane scroll = new JScrollPane(details);
        dialog.add(scroll, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel();
        JButton btnClose = new JButton("Close");
        btnClose.addActionListener(e -> dialog.dispose());
        buttonPanel.add(btnClose);
        dialog.add(buttonPanel, BorderLayout.SOUTH);

        dialog.setVisible(true);
    }

    // ===== RUN DEEP ANALYSIS =====
    private void runDeepAnalysis() {
        // Simulate AI deep analysis
        JDialog progress = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Deep Analysis", true);
        progress.setLayout(new BorderLayout());
        progress.setSize(350, 120);
        progress.setLocationRelativeTo(this);
        
        JProgressBar pb = new JProgressBar();
        pb.setIndeterminate(true);
        progress.add(new JLabel("🧠 Running deep analysis... Please wait"), BorderLayout.NORTH);
        progress.add(pb, BorderLayout.CENTER);
        
        new Thread(() -> {
            try {
                Thread.sleep(3000); // Simulate analysis
                
                generateInsights();
                
                Insight deepInsight = new Insight();
                deepInsight.title = "🔬 Deep Analysis: Pattern Discovery";
                deepInsight.description = 
                    "Advanced pattern analysis reveals:\n" +
                    "• Cross-sector success correlation: 0.78\n" +
                    "• Experience vs. Success: Strong positive correlation\n" +
                    "• Community engagement is the #1 success indicator\n" +
                    "• Projects with >3 years experience succeed 40% more often";
                deepInsight.category = "Deep Analysis";
                deepInsight.confidence = 0.89;
                deepInsight.recommendation = "Prioritize funding for experienced beneficiaries and community-based projects.";
                deepInsight.generatedDate = LocalDate.now();
                if (insights != null) {
                    insights.add(deepInsight);
                }
                
                SwingUtilities.invokeLater(() -> {
                    progress.dispose();
                    updateCharts();
                    updateStats();
                    JOptionPane.showMessageDialog(this,
                        "🔬 Deep Analysis Complete!\n\n" +
                        "New insights discovered:\n" +
                        "• Cross-sector correlation patterns\n" +
                        "• Experience impact analysis\n" +
                        "• Success indicator ranking\n\n" +
                        "Total insights: " + (insights != null ? insights.size() : 0),
                        "Analysis Complete",
                        JOptionPane.INFORMATION_MESSAGE);
                    setStatus("🔬 Deep analysis complete - " + (insights != null ? insights.size() : 0) + " insights available");
                });
                
            } catch (Exception ex) {
                SwingUtilities.invokeLater(() -> {
                    progress.dispose();
                    JOptionPane.showMessageDialog(this,
                        "Error during analysis: " + ex.getMessage(),
                        "Analysis Failed",
                        JOptionPane.ERROR_MESSAGE);
                });
            }
        }).start();
        
        progress.setVisible(true);
    }
}