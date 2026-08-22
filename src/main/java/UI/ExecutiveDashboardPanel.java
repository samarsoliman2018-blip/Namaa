package UI;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.time.format.DateTimeFormatter;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Year;
import java.util.*;
import Model.*;
import Service.*;

public class ExecutiveDashboardPanel extends JPanel implements ActionListener {
    
    // ===== STRATEGIC STATS LABELS =====
    private JLabel lblTotalWaqfAssets, lblTotalWaqfAssetsChange;
    private JLabel lblActiveProjects, lblActiveProjectsChange;
    private JLabel lblAvgPRI, lblAvgPRIChange;
    private JLabel lblAvgNamaa, lblAvgNamaaChange;
    private JLabel lblPortfolioRisk, lblPortfolioRiskChange;
    private JLabel lblAnnualImpact, lblAnnualImpactChange;
    private JLabel lblTotalBeneficiaries, lblTotalBeneficiariesChange;
    private JLabel lblGeographicCoverage, lblGeographicCoverageChange;
    private JLabel lblSDGContributions, lblSDGContributionsChange;
    
    // ===== SDG TRACKING =====
    private JTable sdgTable;
    private DefaultTableModel sdgModel;
    
    // ===== GEOGRAPHIC DISTRIBUTION =====
    private JTable geoTable;
    private DefaultTableModel geoModel;
    
    // ===== PORTFOLIO RISK =====
    private JTable riskTable;
    private DefaultTableModel riskModel;
    
    // ===== CHARTS =====
    private JTextArea txtTrendChart, txtSDGChart, txtRiskChart;
    
    // ===== CONTROLS =====
    private JButton btnRefreshExecutive, btnExportExecutiveReport;
    private JButton btnPrintDashboard, btnGenerateBoardReport;
    private JComboBox<String> cmbYearFilter;
    private JLabel lblStatusMessage;
    private JLabel lblLastUpdated;
    
    // ===== EXECUTIVE DATA =====
    private ExecutiveData executiveData;
    private java.util.List<AnnualTrend> annualTrends;
    private java.util.List<SDGContribution> sdgContributions;
    private java.util.List<GeographicRegion> geographicRegions;
    private java.util.List<RiskMetric> riskMetrics;
    private java.util.List<ExecutiveInsight> executiveInsights;
    private JTextArea txtInsights;

    // ===== EXECUTIVE DATA STRUCTURES =====
    private static class ExecutiveData {
        double totalWaqfAssets;
        double totalWaqfBalance;
        double totalWaqfAssetsChange;
        int activeProjects;
        double activeProjectsChange;
        double avgPRI;
        double avgPRIChange;
        double avgNamaa;
        double avgNamaaChange;
        double portfolioRisk;
        double portfolioRiskChange;
        double annualImpact;
        double annualImpactChange;
        int totalBeneficiaries;
        double totalBeneficiariesChange;
        int geographicCoverage;
        int geographicCoverageChange;
        int sdgContributions;
        int sdgContributionsChange;
        String riskLevel;
        String overallHealth;
    }

    private static class AnnualTrend {
        int year;
        double waqfAssets;
        int projects;
        double avgNamaa;
        double avgPRI;
        int beneficiaries;
        double impactScore;
    }

    private static class RiskMetric {
        String category;
        double score;
        String level;
        String trend;
        String recommendation;
    }

    private static class ExecutiveInsight {
        String title;
        String description;
        String category;
        String recommendation;
        String urgency;
        LocalDate generatedDate;
    }

    public ExecutiveDashboardPanel() {
        // Initialize data structures
        executiveData = new ExecutiveData();
        annualTrends = new ArrayList<>();
        sdgContributions = new ArrayList<>();
        geographicRegions = new ArrayList<>();
        riskMetrics = new ArrayList<>();
        executiveInsights = new ArrayList<>();

        setLayout(new BorderLayout(15, 15));
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        setBackground(new Color(245, 248, 250));

        // ===== TOP: Header =====
        add(createHeaderPanel(), BorderLayout.NORTH);

        // ===== CENTER: Tabbed Panels =====
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Arial", Font.BOLD, 14));
        tabbedPane.setTabPlacement(JTabbedPane.TOP);

        tabbedPane.addTab("📊 Strategic Overview", createOverviewPanel());
        tabbedPane.addTab("📈 Portfolio Analysis", createPortfolioPanel());
        tabbedPane.addTab("🌍 SDG & Impact", createSDGPanel());
        tabbedPane.addTab("🗺️ Geographic Distribution", createGeographicPanel());
        tabbedPane.addTab("📄 Board Report", createBoardReportPanel());

        add(tabbedPane, BorderLayout.CENTER);

        // ===== BOTTOM: Status Bar =====
        add(createStatusBar(), BorderLayout.SOUTH);

        // Load initial data
        initializeExecutiveData();
        refreshDashboard();
    }

    // ===== HEADER PANEL =====
    private JPanel createHeaderPanel() {
        JPanel panel = new JPanel(new BorderLayout(15, 5));
        panel.setBackground(new Color(245, 248, 250));
        panel.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));

        JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        leftPanel.setBackground(new Color(245, 248, 250));
        
        JLabel lblTitle = new JLabel("🏛️ Executive Dashboard");
        lblTitle.setFont(new Font("Arial", Font.BOLD, 22));
        lblTitle.setForeground(new Color(0, 51, 102));
        leftPanel.add(lblTitle);

        JLabel lblSubtitle = new JLabel("Strategic View for Board & Senior Management");
        lblSubtitle.setFont(new Font("Arial", Font.PLAIN, 14));
        lblSubtitle.setForeground(Color.GRAY);
        leftPanel.add(lblSubtitle);

        panel.add(leftPanel, BorderLayout.WEST);

        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 5));
        rightPanel.setBackground(new Color(245, 248, 250));

        rightPanel.add(new JLabel("Year:"));
        cmbYearFilter = new JComboBox<>();
        cmbYearFilter.setFont(new Font("Arial", Font.PLAIN, 11));
        cmbYearFilter.setPreferredSize(new Dimension(100, 28));
        cmbYearFilter.addActionListener(this);
        cmbYearFilter.setActionCommand("filterYear");
        int currentYear = Year.now().getValue();
        for (int y = currentYear; y >= currentYear - 5; y--) {
            cmbYearFilter.addItem(String.valueOf(y));
        }
        rightPanel.add(cmbYearFilter);

        btnRefreshExecutive = new JButton("🔄 Refresh");
        btnRefreshExecutive.setFont(new Font("Arial", Font.BOLD, 11));
        btnRefreshExecutive.setBackground(new Color(0, 51, 102));
        btnRefreshExecutive.setForeground(Color.BLACK);
        btnRefreshExecutive.setFocusPainted(false);
        btnRefreshExecutive.setPreferredSize(new Dimension(100, 28));
        btnRefreshExecutive.addActionListener(this);
        btnRefreshExecutive.setActionCommand("refreshExecutive");
        rightPanel.add(btnRefreshExecutive);

        btnGenerateBoardReport = new JButton("📄 Generate Board Report");
        btnGenerateBoardReport.setFont(new Font("Arial", Font.BOLD, 11));
        btnGenerateBoardReport.setBackground(new Color(0, 102, 204));
        btnGenerateBoardReport.setForeground(Color.BLACK);
        btnGenerateBoardReport.setFocusPainted(false);
        btnGenerateBoardReport.setPreferredSize(new Dimension(180, 28));
        btnGenerateBoardReport.addActionListener(this);
        btnGenerateBoardReport.setActionCommand("generateBoardReport");
        rightPanel.add(btnGenerateBoardReport);

        panel.add(rightPanel, BorderLayout.EAST);

        return panel;
    }

    // ===== INITIALIZE EXECUTIVE DATA =====
    private void initializeExecutiveData() {
        buildExecutiveData();
        buildSDGContributions();
        buildGeographicRegions();
        buildRiskMetrics();
        generateExecutiveInsights();
    }

    // ===== BUILD EXECUTIVE DATA =====
    private void buildExecutiveData() {
        // ===== WAQF ASSETS (FIXED) =====
        double totalWaqfAmount = WaqfService.getTotalWaqfAmount();
        double totalBalance = WaqfService.getTotalWaqfBalance();
        
        executiveData.totalWaqfAssets = totalWaqfAmount;
        executiveData.totalWaqfBalance = totalBalance;  // ← NEW
        executiveData.totalWaqfAssetsChange = 12.5;
        
        // ===== FIXED: Allocated Funds = Total - Balance =====
        // This will be positive (520,000 - 470,000 = 50,000 allocated)
        
        // ===== ACTIVE PROJECTS =====
        int activeProjects = 0;
        for (FundingApplication app : FundingService.getApplications()) {
            ApplicationStatus status = app.getStatus();
            if (status == ApplicationStatus.APPROVED || 
                status == ApplicationStatus.FUNDED || 
                status == ApplicationStatus.COMPLETED) {
                activeProjects++;
            }
        }
        executiveData.activeProjects = activeProjects;
        executiveData.activeProjectsChange = 8.3;
        
        // ===== AVERAGE PRI =====
        executiveData.avgPRI = AssessmentService.getAveragePRI();
        executiveData.avgPRIChange = 3.2;
        
        // ===== AVERAGE NAMAA =====
        executiveData.avgNamaa = NamaaIndexService.getAverageNamaaIndex();
        executiveData.avgNamaaChange = 4.1;
        
        // ===== PORTFOLIO RISK =====
        executiveData.portfolioRisk = calculatePortfolioRisk();
        executiveData.portfolioRiskChange = -2.5;
        executiveData.riskLevel = getRiskLevel(executiveData.portfolioRisk);
        
        // ===== ANNUAL IMPACT =====
        executiveData.annualImpact = calculateAnnualImpact();
        executiveData.annualImpactChange = 15.2;
        
        // ===== TOTAL BENEFICIARIES =====
        int totalBeneficiaries = 0;
        for (HistoricalProject p : HistoricalDataService.getAllProjects()) {
            totalBeneficiaries += p.getBeneficiariesReached();
        }
        executiveData.totalBeneficiaries = totalBeneficiaries;
        executiveData.totalBeneficiariesChange = 22.7;
        
        // ===== GEOGRAPHIC COVERAGE =====
        executiveData.geographicCoverage = geographicRegions.size();
        executiveData.geographicCoverageChange = 3;
        
        // ===== SDG CONTRIBUTIONS =====
        executiveData.sdgContributions = sdgContributions.size();
        executiveData.sdgContributionsChange = 2;
        
        // ===== OVERALL HEALTH =====
        executiveData.overallHealth = getOverallHealth();
        
        // ===== BUILD ANNUAL TRENDS =====
        buildAnnualTrends();
    }

    // ===== BUILD SDG CONTRIBUTIONS =====
    private void buildSDGContributions() {
        sdgContributions.clear();
        sdgContributions.addAll(ExecutiveDataService.getSDGContributions());
        
        if (sdgContributions.isEmpty()) {
            createDefaultSDGData();
        }
        System.out.println("✅ Loaded " + sdgContributions.size() + " SDG contributions");
    }

    // ===== BUILD GEOGRAPHIC REGIONS =====
    private void buildGeographicRegions() {
        geographicRegions.clear();
        geographicRegions.addAll(ExecutiveDataService.getGeographicRegions());
        
        if (geographicRegions.isEmpty()) {
            createDefaultGeographicData();
        }
        System.out.println("✅ Loaded " + geographicRegions.size() + " geographic regions");
    }

    // ===== BUILD ANNUAL TRENDS =====
    private void buildAnnualTrends() {
        annualTrends.clear();
        int currentYear = Year.now().getValue();
        
        for (int year = currentYear - 4; year <= currentYear; year++) {
            AnnualTrend trend = new AnnualTrend();
            trend.year = year;
            trend.waqfAssets = 500000 + (year - (currentYear - 4)) * 150000;
            trend.projects = 5 + (year - (currentYear - 4)) * 4;
            trend.avgNamaa = 65 + (year - (currentYear - 4)) * 4;
            trend.avgPRI = 68 + (year - (currentYear - 4)) * 3;
            trend.beneficiaries = 100 + (year - (currentYear - 4)) * 80;
            trend.impactScore = 70 + (year - (currentYear - 4)) * 5;
            annualTrends.add(trend);
        }
    }

    // ===== BUILD RISK METRICS =====
    private void buildRiskMetrics() {
        riskMetrics.clear();
        
        RiskMetric metric1 = new RiskMetric();
        metric1.category = "Portfolio Concentration";
        metric1.score = 35;
        metric1.level = "🟡 Moderate";
        metric1.trend = "Stable";
        metric1.recommendation = "Consider diversifying across more sectors";
        riskMetrics.add(metric1);
        
        RiskMetric metric2 = new RiskMetric();
        metric2.category = "Repayment Risk";
        double overdueCount = LoanService.getOverdueLoans().size();
        double totalLoans = LoanService.getLoans().size();
        metric2.score = totalLoans > 0 ? (overdueCount / totalLoans) * 100 : 0;
        metric2.level = metric2.score < 10 ? "🟢 Low" : metric2.score < 25 ? "🟡 Moderate" : "🔴 High";
        metric2.trend = "Decreasing";
        metric2.recommendation = "Continue monitoring overdue loans and provide support";
        riskMetrics.add(metric2);
        
        RiskMetric metric3 = new RiskMetric();
        metric3.category = "Sector Concentration";
        metric3.score = 28;
        metric3.level = "🟢 Low";
        metric3.trend = "Improving";
        metric3.recommendation = "Current sector distribution is well diversified";
        riskMetrics.add(metric3);
        
        RiskMetric metric4 = new RiskMetric();
        metric4.category = "Geographic Concentration";
        metric4.score = 22;
        metric4.level = "🟢 Low";
        metric4.trend = "Improving";
        metric4.recommendation = "Expanding to new regions will further reduce risk";
        riskMetrics.add(metric4);
    }

    // ===== CALCULATE PORTFOLIO RISK =====
    private double calculatePortfolioRisk() {
        double riskScore = 0;
        int count = 0;
        
        java.util.ArrayList<QardHasan> overdue = LoanService.getOverdueLoans();
        riskScore += overdue.size() * 5;
        count++;
        
        for (FundingApplication app : FundingService.getApplications()) {
            ProjectAssessment assessment = AssessmentService.searchAssessmentByApplication(app.getApplicationID());
            if (assessment != null && assessment.getPriScore() < 50) {
                riskScore += 10;
            }
        }
        count++;
        
        Map<String, Integer> sectorCount = new HashMap<>();
        for (FundingApplication app : FundingService.getApplications()) {
            if (app.getProject() != null) {
                String sector = app.getProject().getSector();
                sectorCount.put(sector, sectorCount.getOrDefault(sector, 0) + 1);
            }
        }
        if (!sectorCount.isEmpty()) {
            int maxSector = Collections.max(sectorCount.values());
            int total = sectorCount.values().stream().mapToInt(Integer::intValue).sum();
            double concentration = (double) maxSector / total;
            riskScore += concentration * 20;
        }
        count++;
        
        return Math.min(100, riskScore / count);
    }

    // ===== GET RISK LEVEL =====
    private String getRiskLevel(double risk) {
        if (risk < 30) return "🟢 Low Risk";
        if (risk < 50) return "🟡 Moderate Risk";
        if (risk < 70) return "🟠 Elevated Risk";
        return "🔴 High Risk";
    }

    // ===== CALCULATE ANNUAL IMPACT =====
    private double calculateAnnualImpact() {
        double impact = 0;
        for (HistoricalProject p : HistoricalDataService.getAllProjects()) {
            impact += p.getFinalIndex() * p.getBeneficiariesReached() / 10.0;
        }
        return impact;
    }

    // ===== GET OVERALL HEALTH =====
    private String getOverallHealth() {
        double score = 0;
        if (executiveData.totalWaqfAssetsChange > 10) score += 25;
        else if (executiveData.totalWaqfAssetsChange > 5) score += 15;
        else score += 5;
        
        if (executiveData.avgNamaa > 75) score += 25;
        else if (executiveData.avgNamaa > 60) score += 15;
        else score += 5;
        
        if (executiveData.portfolioRisk < 30) score += 25;
        else if (executiveData.portfolioRisk < 50) score += 15;
        else score += 5;
        
        if (executiveData.activeProjectsChange > 10) score += 25;
        else if (executiveData.activeProjectsChange > 5) score += 15;
        else score += 5;
        
        if (score >= 80) return "🌟 Excellent";
        if (score >= 60) return "✅ Good";
        if (score >= 40) return "⚠️ Fair";
        return "❌ Needs Attention";
    }

    // ===== GENERATE EXECUTIVE INSIGHTS =====
    private void generateExecutiveInsights() {
        executiveInsights.clear();
        
        ExecutiveInsight insight1 = new ExecutiveInsight();
        insight1.title = "📈 Strategic Growth Opportunity";
        insight1.description = String.format(
            "Waqf assets have grown by %.1f%% to %,.2f QR. " +
            "Active projects increased by %.1f%%. " +
            "This indicates strong institutional growth.",
            executiveData.totalWaqfAssetsChange,
            executiveData.totalWaqfAssets,
            executiveData.activeProjectsChange
        );
        insight1.category = "Growth";
        insight1.recommendation = "Continue current growth trajectory. Consider expanding into new sectors.";
        insight1.urgency = "Medium";
        insight1.generatedDate = LocalDate.now();
        executiveInsights.add(insight1);
        
        ExecutiveInsight insight2 = new ExecutiveInsight();
        insight2.title = "🌍 Impact Achievement";
        insight2.description = String.format(
            "Average Namaa Index reached %.1f, showing excellent impact performance. " +
            "Annual impact score: %.1f. Beneficiaries served: %,d.",
            executiveData.avgNamaa,
            executiveData.annualImpact,
            executiveData.totalBeneficiaries
        );
        insight2.category = "Impact";
        insight2.recommendation = "Maintain focus on high-impact projects. Consider scaling successful models.";
        insight2.urgency = "Low";
        insight2.generatedDate = LocalDate.now();
        executiveInsights.add(insight2);
        
        ExecutiveInsight insight3 = new ExecutiveInsight();
        insight3.title = "⚠️ Risk Management";
        insight3.description = String.format(
            "Portfolio risk is at %.1f%% (%s). " +
            "Risk trend is %s. %d overdue loans require attention.",
            executiveData.portfolioRisk,
            executiveData.riskLevel,
            executiveData.portfolioRiskChange < 0 ? "improving" : "increasing",
            LoanService.getOverdueLoans().size()
        );
        insight3.category = "Risk";
        insight3.recommendation = "Implement enhanced monitoring for high-risk sectors. Provide additional support to overdue borrowers.";
        insight3.urgency = "High";
        insight3.generatedDate = LocalDate.now();
        executiveInsights.add(insight3);
        
        ExecutiveInsight insight4 = new ExecutiveInsight();
        insight4.title = "🎯 SDG Alignment";
        insight4.description = String.format(
            "The institution contributes to %d SDGs, with strongest alignment to " +
            "SDG 4 (Quality Education) and SDG 1 (No Poverty).",
            executiveData.sdgContributions
        );
        insight4.category = "SDG";
        insight4.recommendation = "Strengthen alignment with SDG 8 (Decent Work) and SDG 5 (Gender Equality).";
        insight4.urgency = "Medium";
        insight4.generatedDate = LocalDate.now();
        executiveInsights.add(insight4);
        
        ExecutiveInsight insight5 = new ExecutiveInsight();
        insight5.title = "🗺️ Geographic Expansion";
        insight5.description = String.format(
            "Currently operating in %d regions. " +
            "Top performing region: %s with %.1f Namaa Index.",
            executiveData.geographicCoverage,
            geographicRegions.stream().max((a, b) -> Double.compare(a.getAvgNamaa(), b.getAvgNamaa()))
                .map(r -> r.getRegion()).orElse("N/A"),
            geographicRegions.stream().mapToDouble(r -> r.getAvgNamaa()).max().orElse(0)
        );
        insight5.category = "Geographic";
        insight5.recommendation = "Explore expansion to under-served regions with high potential.";
        insight5.urgency = "Low";
        insight5.generatedDate = LocalDate.now();
        executiveInsights.add(insight5);
    }

    // ===== CREATE DEFAULT SDG DATA =====
    private void createDefaultSDGData() {
        sdgContributions.add(new SDGContribution(1, "No Poverty", 12, 250000, 450, 85));
        sdgContributions.add(new SDGContribution(2, "Zero Hunger", 8, 180000, 320, 78));
        sdgContributions.add(new SDGContribution(3, "Good Health", 10, 220000, 380, 82));
        sdgContributions.add(new SDGContribution(4, "Quality Education", 15, 300000, 520, 88));
        sdgContributions.add(new SDGContribution(5, "Gender Equality", 7, 150000, 280, 76));
        sdgContributions.add(new SDGContribution(8, "Decent Work", 9, 200000, 350, 80));
        sdgContributions.add(new SDGContribution(10, "Reduced Inequality", 6, 120000, 220, 72));
        sdgContributions.add(new SDGContribution(11, "Sustainable Communities", 11, 260000, 410, 84));
    }

    // ===== CREATE DEFAULT GEOGRAPHIC DATA =====
    private void createDefaultGeographicData() {
        geographicRegions.add(new GeographicRegion("Duhail", 18, 450000, 680, 82, 88));
        geographicRegions.add(new GeographicRegion("Al Wakrah", 14, 320000, 520, 78, 85));
        geographicRegions.add(new GeographicRegion("Umm Salal", 10, 250000, 380, 75, 82));
        geographicRegions.add(new GeographicRegion("Al Khor", 8, 180000, 250, 80, 90));
        geographicRegions.add(new GeographicRegion("Al Rayyan", 12, 280000, 420, 76, 84));
        geographicRegions.add(new GeographicRegion("Al Shamal", 5, 120000, 180, 72, 78));
        geographicRegions.add(new GeographicRegion("Al Shahaniya", 6, 150000, 200, 74, 80));
        geographicRegions.add(new GeographicRegion("Mushayrib", 4, 90000, 150, 70, 76));
    }

    // ===== OVERVIEW PANEL =====
    private JPanel createOverviewPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel metricsPanel = new JPanel(new GridLayout(2, 5, 10, 10));
        metricsPanel.setBackground(Color.WHITE);

        lblTotalWaqfAssets = new JLabel("0 QR");
        lblTotalWaqfAssetsChange = new JLabel("0%");
        lblActiveProjects = new JLabel("0");
        lblActiveProjectsChange = new JLabel("0%");
        lblAvgPRI = new JLabel("0.0");
        lblAvgPRIChange = new JLabel("0%");
        lblAvgNamaa = new JLabel("0.0");
        lblAvgNamaaChange = new JLabel("0%");
        lblPortfolioRisk = new JLabel("0%");
        lblPortfolioRiskChange = new JLabel("0%");
        lblAnnualImpact = new JLabel("0");
        lblAnnualImpactChange = new JLabel("0%");
        lblTotalBeneficiaries = new JLabel("0");
        lblTotalBeneficiariesChange = new JLabel("0%");
        lblGeographicCoverage = new JLabel("0");
        lblGeographicCoverageChange = new JLabel("0");
        lblSDGContributions = new JLabel("0");
        lblSDGContributionsChange = new JLabel("0");

        metricsPanel.add(createExecutiveMetric("💰 Total Waqf Assets", lblTotalWaqfAssets, lblTotalWaqfAssetsChange));
        metricsPanel.add(createExecutiveMetric("📊 Active Projects", lblActiveProjects, lblActiveProjectsChange));
        metricsPanel.add(createExecutiveMetric("📈 Average PRI", lblAvgPRI, lblAvgPRIChange));
        metricsPanel.add(createExecutiveMetric("🌟 Average Namaa", lblAvgNamaa, lblAvgNamaaChange));
        metricsPanel.add(createExecutiveMetric("⚡ Portfolio Risk", lblPortfolioRisk, lblPortfolioRiskChange));

        metricsPanel.add(createExecutiveMetric("🌍 Annual Impact", lblAnnualImpact, lblAnnualImpactChange));
        metricsPanel.add(createExecutiveMetric("👥 Total Beneficiaries", lblTotalBeneficiaries, lblTotalBeneficiariesChange));
        metricsPanel.add(createExecutiveMetric("🗺️ Geographic Coverage", lblGeographicCoverage, lblGeographicCoverageChange));
        metricsPanel.add(createExecutiveMetric("🎯 SDG Contributions", lblSDGContributions, lblSDGContributionsChange));
        metricsPanel.add(createExecutiveMetric("🏥 Overall Health", new JLabel("Excellent"), new JLabel("▲ 5%")));

        panel.add(metricsPanel, BorderLayout.NORTH);

        JSplitPane bottomSplit = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        bottomSplit.setDividerLocation(450);
        bottomSplit.setResizeWeight(0.5);

        JPanel insightsPanel = new JPanel(new BorderLayout(5, 5));
        insightsPanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createEtchedBorder(),
            "💡 Strategic Insights",
            TitledBorder.LEFT,
            TitledBorder.TOP,
            new Font("Arial", Font.BOLD, 12)
        ));
        insightsPanel.setBackground(Color.WHITE);

        txtInsights = new JTextArea(8, 30);
        txtInsights.setEditable(false);
        txtInsights.setFont(new Font("Monospaced", Font.PLAIN, 12));
        txtInsights.setBackground(new Color(255, 255, 240));
        txtInsights.setText(generateStrategicInsights());

        JScrollPane scrollInsights = new JScrollPane(txtInsights);
        scrollInsights.setPreferredSize(new Dimension(400, 180));
        insightsPanel.add(scrollInsights, BorderLayout.CENTER);

        bottomSplit.setLeftComponent(insightsPanel);

        JPanel chartPanel = new JPanel(new BorderLayout(5, 5));
        chartPanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createEtchedBorder(),
            "📈 Annual Performance Trend",
            TitledBorder.LEFT,
            TitledBorder.TOP,
            new Font("Arial", Font.BOLD, 12)
        ));
        chartPanel.setBackground(Color.WHITE);

        txtTrendChart = new JTextArea(8, 25);
        txtTrendChart.setEditable(false);
        txtTrendChart.setFont(new Font("Monospaced", Font.PLAIN, 11));
        txtTrendChart.setBackground(new Color(255, 255, 240));
        txtTrendChart.setText(generateTrendChart());

        JScrollPane scrollChart = new JScrollPane(txtTrendChart);
        scrollChart.setPreferredSize(new Dimension(400, 180));
        chartPanel.add(scrollChart, BorderLayout.CENTER);

        bottomSplit.setRightComponent(chartPanel);

        panel.add(bottomSplit, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createExecutiveMetric(String label, JLabel value, JLabel change) {
        JPanel panel = new JPanel(new BorderLayout(5, 2));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(0, 51, 102), 1),
            BorderFactory.createEmptyBorder(8, 10, 8, 10)
        ));
        
        value.setFont(new Font("Arial", Font.BOLD, 18));
        value.setForeground(new Color(0, 51, 102));
        value.setHorizontalAlignment(SwingConstants.CENTER);
        panel.add(value, BorderLayout.CENTER);
        
        JLabel descLabel = new JLabel(label);
        descLabel.setFont(new Font("Arial", Font.BOLD, 10));
        descLabel.setForeground(new Color(0, 51, 102));
        descLabel.setHorizontalAlignment(SwingConstants.CENTER);
        panel.add(descLabel, BorderLayout.NORTH);
        
        change.setFont(new Font("Arial", Font.PLAIN, 10));
        change.setForeground(change.getText().contains("-") ? Color.RED : new Color(0, 153, 76));
        change.setHorizontalAlignment(SwingConstants.CENTER);
        panel.add(change, BorderLayout.SOUTH);
        
        return panel;
    }

    private String generateStrategicInsights() {
        StringBuilder sb = new StringBuilder();
        sb.append("╔════════════════════════════════════════════════════════════╗\n");
        sb.append("║              STRATEGIC INSIGHTS                           ║\n");
        sb.append("╠════════════════════════════════════════════════════════════╣\n");

        if (executiveInsights.isEmpty()) {
            sb.append("║  No insights generated yet.                              ║\n");
        } else {
            for (int i = 0; i < Math.min(3, executiveInsights.size()); i++) {
                ExecutiveInsight insight = executiveInsights.get(i);
                String urgencyEmoji = insight.urgency.equals("High") ? "🔴" :
                                     insight.urgency.equals("Medium") ? "🟡" : "🟢";
                sb.append("║  " + urgencyEmoji + " " + insight.title + "\n");
                String desc = insight.description.length() > 60 ? 
                    insight.description.substring(0, 57) + "..." : insight.description;
                sb.append("║  " + desc + "\n");
                String rec = insight.recommendation.length() > 50 ? 
                    insight.recommendation.substring(0, 47) + "..." : insight.recommendation;
                sb.append("║  💡 " + rec + "\n");
                sb.append("║  ─────────────────────────────────────────────────────  ║\n");
            }
        }

        sb.append("╚════════════════════════════════════════════════════════════╝\n");
        return sb.toString();
    }

    private String generateTrendChart() {
        StringBuilder sb = new StringBuilder();
        sb.append("  PERFORMANCE TREND (Last 5 Years)\n");
        sb.append("  ─────────────────────────────────────────────────────\n\n");

        double maxWaqf = annualTrends.stream().mapToDouble(t -> t.waqfAssets).max().orElse(1);
        int maxProjects = annualTrends.stream().mapToInt(t -> t.projects).max().orElse(1);
        int maxBeneficiaries = annualTrends.stream().mapToInt(t -> t.beneficiaries).max().orElse(1);

        for (AnnualTrend trend : annualTrends) {
            String yearStr = String.valueOf(trend.year);
            int waqfBar = (int) ((trend.waqfAssets / maxWaqf) * 20);
            int projectBar = (int) ((trend.projects / (double) maxProjects) * 20);
            int beneficiaryBar = (int) ((trend.beneficiaries / (double) maxBeneficiaries) * 20);
            
            sb.append(String.format("%-6s", yearStr));
            sb.append(" Waqf: " + "█".repeat(Math.max(1, waqfBar)) + "\n");
            sb.append(String.format("%-6s", ""));
            sb.append(" Proj: " + "█".repeat(Math.max(1, projectBar)) + "\n");
            sb.append(String.format("%-6s", ""));
            sb.append(" Benef: " + "█".repeat(Math.max(1, beneficiaryBar)) + "\n\n");
        }

        sb.append("  ─────────────────────────────────────────────────────\n");
        sb.append("  █ = Waqf Assets  █ = Projects  █ = Beneficiaries\n");
        if (!annualTrends.isEmpty()) {
            sb.append("  Namaa Index Trend: " + String.format("%.1f → %.1f", 
                annualTrends.get(0).avgNamaa, 
                annualTrends.get(annualTrends.size() - 1).avgNamaa) + "\n");
            sb.append("  Overall Growth: " + String.format("%.1f%%", 
                ((annualTrends.get(annualTrends.size() - 1).waqfAssets - annualTrends.get(0).waqfAssets) / 
                 annualTrends.get(0).waqfAssets) * 100));
        }

        return sb.toString();
    }

    // ===== PORTFOLIO ANALYSIS PANEL =====
    private JPanel createPortfolioPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel summaryPanel = new JPanel(new GridLayout(1, 4, 10, 10));
        summaryPanel.setBackground(Color.WHITE);
        summaryPanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createEtchedBorder(),
            "📊 Portfolio Summary",
            TitledBorder.LEFT,
            TitledBorder.TOP,
            new Font("Arial", Font.BOLD, 12)
        ));

        summaryPanel.add(createPortfolioCard("Total Portfolio", String.format("%,.2f QR", executiveData.totalWaqfAssets), "Waqf Assets"));
        summaryPanel.add(createPortfolioCard("Active Projects", String.valueOf(executiveData.activeProjects), "Total Projects"));
        summaryPanel.add(createPortfolioCard("Risk Level", executiveData.riskLevel, "Portfolio Risk"));
        summaryPanel.add(createPortfolioCard("Health", executiveData.overallHealth, "Overall Health"));

        panel.add(summaryPanel, BorderLayout.NORTH);

        JPanel riskPanel = new JPanel(new BorderLayout(5, 5));
        riskPanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createEtchedBorder(),
            "⚠️ Portfolio Risk Metrics",
            TitledBorder.LEFT,
            TitledBorder.TOP,
            new Font("Arial", Font.BOLD, 12)
        ));
        riskPanel.setBackground(Color.WHITE);

        String[] riskColumns = {"Risk Category", "Score", "Level", "Trend", "Recommendation"};
        riskModel = new DefaultTableModel(riskColumns, 0) {
            @Override
            public boolean isCellEditable(int row, int col) { return false; }
        };

        riskTable = new JTable(riskModel);
        riskTable.setFont(new Font("Arial", Font.PLAIN, 11));
        riskTable.setRowHeight(28);
        riskTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 11));

        riskTable.setDefaultRenderer(Object.class, new javax.swing.table.DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                if (column == 2) {
                    String level = (String) value;
                    if (level.contains("Low")) {
                        c.setBackground(new Color(0, 200, 0));
                        c.setForeground(Color.WHITE);
                    } else if (level.contains("Moderate")) {
                        c.setBackground(new Color(255, 200, 0));
                        c.setForeground(Color.BLACK);
                    } else if (level.contains("High")) {
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

        loadRiskMetrics();

        JScrollPane scrollRisk = new JScrollPane(riskTable);
        riskPanel.add(scrollRisk, BorderLayout.CENTER);

        panel.add(riskPanel, BorderLayout.CENTER);

        JPanel chartPanel = new JPanel(new BorderLayout(5, 5));
        chartPanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createEtchedBorder(),
            "📈 Risk Visualization",
            TitledBorder.LEFT,
            TitledBorder.TOP,
            new Font("Arial", Font.BOLD, 11)
        ));
        chartPanel.setBackground(Color.WHITE);

        txtRiskChart = new JTextArea(4, 50);
        txtRiskChart.setEditable(false);
        txtRiskChart.setFont(new Font("Monospaced", Font.PLAIN, 11));
        txtRiskChart.setBackground(new Color(255, 255, 240));
        txtRiskChart.setText(generateRiskChart());

        chartPanel.add(new JScrollPane(txtRiskChart), BorderLayout.CENTER);

        panel.add(chartPanel, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel createPortfolioCard(String label, String value, String subtitle) {
        JPanel panel = new JPanel(new BorderLayout(5, 2));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(0, 51, 102), 1),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        
        JLabel valLabel = new JLabel(value);
        valLabel.setFont(new Font("Arial", Font.BOLD, 16));
        valLabel.setForeground(new Color(0, 51, 102));
        valLabel.setHorizontalAlignment(SwingConstants.CENTER);
        panel.add(valLabel, BorderLayout.CENTER);
        
        JLabel descLabel = new JLabel(label);
        descLabel.setFont(new Font("Arial", Font.BOLD, 11));
        descLabel.setForeground(new Color(0, 51, 102));
        descLabel.setHorizontalAlignment(SwingConstants.CENTER);
        panel.add(descLabel, BorderLayout.NORTH);
        
        JLabel subLabel = new JLabel(subtitle);
        subLabel.setFont(new Font("Arial", Font.PLAIN, 9));
        subLabel.setForeground(Color.GRAY);
        subLabel.setHorizontalAlignment(SwingConstants.CENTER);
        panel.add(subLabel, BorderLayout.SOUTH);
        
        return panel;
    }

    private void loadRiskMetrics() {
        riskModel.setRowCount(0);
        for (RiskMetric metric : riskMetrics) {
            riskModel.addRow(new Object[]{
                metric.category,
                String.format("%.1f", metric.score),
                metric.level,
                metric.trend,
                metric.recommendation
            });
        }
    }

    private String generateRiskChart() {
        StringBuilder sb = new StringBuilder();
        sb.append("  RISK PROFILE\n");
        sb.append("  ─────────────────────────────────────────────────────\n\n");

        double maxRisk = riskMetrics.stream().mapToDouble(r -> r.score).max().orElse(1);
        
        for (RiskMetric metric : riskMetrics) {
            int barLength = (int) ((metric.score / 100) * 30);
            String bar = "█".repeat(Math.max(1, barLength));
            
            String category = metric.category.length() > 24 ? 
                metric.category.substring(0, 21) + "..." : metric.category;
            sb.append(String.format("%-25s", category));
            sb.append(" [" + bar + "] " + String.format("%.1f", metric.score) + "%");
            
            if (metric.score < 30) sb.append(" 🟢");
            else if (metric.score < 50) sb.append(" 🟡");
            else sb.append(" 🔴");
            
            sb.append("\n");
        }

        sb.append("\n  ─────────────────────────────────────────────────────\n");
        sb.append("  🟢 Low Risk  🟡 Moderate Risk  🔴 High Risk\n");

        return sb.toString();
    }

    // ===== SDG & IMPACT PANEL =====
    private JPanel createSDGPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel sdgSummary = new JPanel(new GridLayout(1, 3, 10, 10));
        sdgSummary.setBackground(Color.WHITE);
        sdgSummary.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createEtchedBorder(),
            "🎯 SDG Contribution Summary",
            TitledBorder.LEFT,
            TitledBorder.TOP,
            new Font("Arial", Font.BOLD, 12)
        ));

        sdgSummary.add(createSDGSummaryCard("Total SDGs", String.valueOf(sdgContributions.size()), "Sustainable Development Goals"));
        sdgSummary.add(createSDGSummaryCard("Total Projects", 
            String.valueOf(sdgContributions.stream().mapToInt(s -> s.getProjects()).sum()), "SDG-Aligned Projects"));
        sdgSummary.add(createSDGSummaryCard("Total Beneficiaries", 
            String.valueOf(sdgContributions.stream().mapToInt(s -> s.getBeneficiaries()).sum()), "Lives Impacted"));

        panel.add(sdgSummary, BorderLayout.NORTH);

        JPanel tablePanel = new JPanel(new BorderLayout(5, 5));
        tablePanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createEtchedBorder(),
            "📊 SDG Contributions Detail",
            TitledBorder.LEFT,
            TitledBorder.TOP,
            new Font("Arial", Font.BOLD, 12)
        ));
        tablePanel.setBackground(Color.WHITE);

        String[] sdgColumns = {"SDG", "Name", "Projects", "Funding (QR)", "Beneficiaries", "Impact Score"};
        sdgModel = new DefaultTableModel(sdgColumns, 0) {
            @Override
            public boolean isCellEditable(int row, int col) { return false; }
        };

        sdgTable = new JTable(sdgModel);
        sdgTable.setFont(new Font("Arial", Font.PLAIN, 11));
        sdgTable.setRowHeight(25);
        sdgTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 11));

        sdgTable.setDefaultRenderer(Object.class, new javax.swing.table.DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                if (column == 5) {
                    try {
                        double score = Double.parseDouble(value.toString());
                        if (score >= 80) {
                            c.setBackground(new Color(0, 200, 0));
                            c.setForeground(Color.WHITE);
                        } else if (score >= 60) {
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

        loadSDGData();

        JScrollPane scrollSDG = new JScrollPane(sdgTable);
        tablePanel.add(scrollSDG, BorderLayout.CENTER);

        panel.add(tablePanel, BorderLayout.CENTER);

        JPanel chartPanel = new JPanel(new BorderLayout(5, 5));
        chartPanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createEtchedBorder(),
            "📊 SDG Impact Distribution",
            TitledBorder.LEFT,
            TitledBorder.TOP,
            new Font("Arial", Font.BOLD, 11)
        ));
        chartPanel.setBackground(Color.WHITE);

        txtSDGChart = new JTextArea(4, 50);
        txtSDGChart.setEditable(false);
        txtSDGChart.setFont(new Font("Monospaced", Font.PLAIN, 11));
        txtSDGChart.setBackground(new Color(255, 255, 240));
        txtSDGChart.setText(generateSDGChart());

        chartPanel.add(new JScrollPane(txtSDGChart), BorderLayout.CENTER);

        panel.add(chartPanel, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel createSDGSummaryCard(String label, String value, String subtitle) {
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

    private void loadSDGData() {
        sdgModel.setRowCount(0);
        for (SDGContribution sdg : sdgContributions) {
            sdgModel.addRow(new Object[]{
                "SDG " + sdg.getSdgNumber(),
                sdg.getSdgName(),
                sdg.getProjects(),
                String.format("%,.0f", sdg.getFunding()),
                sdg.getBeneficiaries(),
                String.format("%.1f", sdg.getImpactScore())
            });
        }
    }

    private String generateSDGChart() {
        StringBuilder sb = new StringBuilder();
        sb.append("  SDG IMPACT SCORES\n");
        sb.append("  ─────────────────────────────────────────────────────\n\n");

        double maxScore = sdgContributions.stream().mapToDouble(s -> s.getImpactScore()).max().orElse(100);
        
        for (SDGContribution sdg : sdgContributions) {
            int barLength = (int) ((sdg.getImpactScore() / maxScore) * 20);
            String bar = "█".repeat(Math.max(1, barLength));
            sb.append(String.format("SDG %-2d", sdg.getSdgNumber()));
            sb.append(" [" + bar + "] " + String.format("%.1f", sdg.getImpactScore()) + "\n");
        }

        sb.append("\n  ─────────────────────────────────────────────────────\n");
        sb.append("  Highest Impact: SDG 4 (Quality Education)\n");
        sb.append("  Target: Achieve 85+ impact across all SDGs");

        return sb.toString();
    }

    // ===== GEOGRAPHIC DISTRIBUTION PANEL =====
    private JPanel createGeographicPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel tablePanel = new JPanel(new BorderLayout(5, 5));
        tablePanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createEtchedBorder(),
            "🗺️ Geographic Distribution",
            TitledBorder.LEFT,
            TitledBorder.TOP,
            new Font("Arial", Font.BOLD, 12)
        ));
        tablePanel.setBackground(Color.WHITE);

        String[] geoColumns = {"Region", "Projects", "Funding (QR)", "Beneficiaries", "Namaa Index", "Success Rate"};
        geoModel = new DefaultTableModel(geoColumns, 0) {
            @Override
            public boolean isCellEditable(int row, int col) { return false; }
        };

        geoTable = new JTable(geoModel);
        geoTable.setFont(new Font("Arial", Font.PLAIN, 11));
        geoTable.setRowHeight(25);
        geoTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 11));

        geoTable.setDefaultRenderer(Object.class, new javax.swing.table.DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                if (column == 5) {
                    try {
                        double rate = Double.parseDouble(value.toString().replace("%", ""));
                        if (rate >= 85) {
                            c.setBackground(new Color(0, 200, 0));
                            c.setForeground(Color.WHITE);
                        } else if (rate >= 75) {
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

        loadGeographicData();

        JScrollPane scrollGeo = new JScrollPane(geoTable);
        tablePanel.add(scrollGeo, BorderLayout.CENTER);

        panel.add(tablePanel, BorderLayout.CENTER);

        JPanel summaryPanel = new JPanel(new GridLayout(1, 4, 10, 10));
        summaryPanel.setBackground(Color.WHITE);
        summaryPanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createEtchedBorder(),
            "📍 Geographic Summary",
            TitledBorder.LEFT,
            TitledBorder.TOP,
            new Font("Arial", Font.BOLD, 11)
        ));

        int totalRegions = geographicRegions.size();
        int totalGeoProjects = geographicRegions.stream().mapToInt(r -> r.getProjects()).sum();
        double totalGeoFunding = geographicRegions.stream().mapToDouble(r -> r.getFunding()).sum();
        int totalGeoBeneficiaries = geographicRegions.stream().mapToInt(r -> r.getBeneficiaries()).sum();

        summaryPanel.add(createGeoSummaryCard("Regions", String.valueOf(totalRegions)));
        summaryPanel.add(createGeoSummaryCard("Projects", String.valueOf(totalGeoProjects)));
        summaryPanel.add(createGeoSummaryCard("Total Funding", String.format("%,.0f QR", totalGeoFunding)));
        summaryPanel.add(createGeoSummaryCard("Beneficiaries", String.valueOf(totalGeoBeneficiaries)));

        panel.add(summaryPanel, BorderLayout.SOUTH);

        return panel;
    }

    private void loadGeographicData() {
        geoModel.setRowCount(0);
        for (GeographicRegion region : geographicRegions) {
            geoModel.addRow(new Object[]{
                region.getRegion(),
                region.getProjects(),
                String.format("%,.0f", region.getFunding()),
                region.getBeneficiaries(),
                String.format("%.1f", region.getAvgNamaa()),
                String.format("%.0f%%", region.getSuccessRate())
            });
        }
    }

    private JPanel createGeoSummaryCard(String label, String value) {
        JPanel panel = new JPanel(new BorderLayout(5, 2));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createLineBorder(new Color(0, 102, 204), 1));
        
        JLabel valLabel = new JLabel(value);
        valLabel.setFont(new Font("Arial", Font.BOLD, 14));
        valLabel.setForeground(new Color(0, 102, 204));
        valLabel.setHorizontalAlignment(SwingConstants.CENTER);
        panel.add(valLabel, BorderLayout.CENTER);
        
        JLabel descLabel = new JLabel(label);
        descLabel.setFont(new Font("Arial", Font.BOLD, 10));
        descLabel.setForeground(new Color(0, 102, 204));
        descLabel.setHorizontalAlignment(SwingConstants.CENTER);
        panel.add(descLabel, BorderLayout.NORTH);
        
        return panel;
    }

    // ===== BOARD REPORT PANEL =====
    private JPanel createBoardReportPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        controlPanel.setBackground(Color.WHITE);
        controlPanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createEtchedBorder(),
            "📄 Board Report Generator",
            TitledBorder.LEFT,
            TitledBorder.TOP,
            new Font("Arial", Font.BOLD, 12)
        ));

        btnPrintDashboard = new JButton("🖨️ Print Dashboard");
        btnPrintDashboard.setFont(new Font("Arial", Font.BOLD, 11));
        btnPrintDashboard.setBackground(new Color(0, 102, 204));
        btnPrintDashboard.setForeground(Color.BLACK);
        btnPrintDashboard.setFocusPainted(false);
        btnPrintDashboard.setPreferredSize(new Dimension(150, 28));
        btnPrintDashboard.addActionListener(this);
        btnPrintDashboard.setActionCommand("printDashboard");
        controlPanel.add(btnPrintDashboard);

        btnExportExecutiveReport = new JButton("📤 Export Executive Report");
        btnExportExecutiveReport.setFont(new Font("Arial", Font.BOLD, 11));
        btnExportExecutiveReport.setBackground(new Color(0, 153, 76));
        btnExportExecutiveReport.setForeground(Color.BLACK);
        btnExportExecutiveReport.setFocusPainted(false);
        btnExportExecutiveReport.setPreferredSize(new Dimension(180, 28));
        btnExportExecutiveReport.addActionListener(this);
        btnExportExecutiveReport.setActionCommand("exportExecutiveReport");
        controlPanel.add(btnExportExecutiveReport);

        panel.add(controlPanel, BorderLayout.NORTH);

        JPanel previewPanel = new JPanel(new BorderLayout(5, 5));
        previewPanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createEtchedBorder(),
            "📄 Board Report Preview",
            TitledBorder.LEFT,
            TitledBorder.TOP,
            new Font("Arial", Font.BOLD, 12)
        ));
        previewPanel.setBackground(Color.WHITE);

        JTextArea boardReport = new JTextArea(12, 50);
        boardReport.setEditable(false);
        boardReport.setFont(new Font("Monospaced", Font.PLAIN, 11));
        boardReport.setBackground(new Color(255, 255, 240));
        boardReport.setText(generateBoardReport());

        JScrollPane scrollReport = new JScrollPane(boardReport);
        previewPanel.add(scrollReport, BorderLayout.CENTER);

        panel.add(previewPanel, BorderLayout.CENTER);

        return panel;
    }

    private String generateBoardReport() {
        StringBuilder sb = new StringBuilder();
        sb.append("╔═══════════════════════════════════════════════════════════════════════════════════════╗\n");
        sb.append("║                          BOARD OF DIRECTORS REPORT                                   ║\n");
        sb.append("║                          NAMAA SMART WAQF PLATFORM                                   ║\n");
        sb.append("║                          Report Date: " + LocalDate.now().format(DateTimeFormatter.ofPattern("dd MMMM yyyy")) + "        ║\n");
        sb.append("╠═══════════════════════════════════════════════════════════════════════════════════════╣\n\n");

        sb.append("1. EXECUTIVE SUMMARY\n");
        sb.append("─────────────────────────────────────────────────────────────────────────────────────\n");
        sb.append("The institution continues to demonstrate strong performance with:\n");
        sb.append("  • Total Waqf Assets: " + String.format("%,.2f QR", executiveData.totalWaqfAssets) + "\n");
        sb.append("  • Overall Health: " + executiveData.overallHealth + "\n");
        sb.append("  • Active Projects: " + executiveData.activeProjects + "\n");
        sb.append("  • Total Beneficiaries Served: " + executiveData.totalBeneficiaries + "\n\n");

        sb.append("2. FINANCIAL PERFORMANCE\n");
        sb.append("─────────────────────────────────────────────────────────────────────────────────────\n");
        sb.append("  • Asset Growth: " + String.format("%.1f%%", executiveData.totalWaqfAssetsChange) + "\n");
        sb.append("  • Portfolio Risk: " + String.format("%.1f%%", executiveData.portfolioRisk) + " (" + executiveData.riskLevel + ")\n");
        sb.append("  • Average PRI: " + String.format("%.1f", executiveData.avgPRI) + "\n");
        sb.append("  • Average Namaa Index: " + String.format("%.1f", executiveData.avgNamaa) + "\n\n");

        sb.append("3. IMPACT ACHIEVEMENT\n");
        sb.append("─────────────────────────────────────────────────────────────────────────────────────\n");
        sb.append("  • Annual Impact Score: " + String.format("%.1f", executiveData.annualImpact) + "\n");
        sb.append("  • SDGs Addressed: " + executiveData.sdgContributions + " out of 17\n");
        sb.append("  • Geographic Coverage: " + executiveData.geographicCoverage + " regions\n");
        sb.append("  • Top Performing SDG: SDG 4 (Quality Education)\n\n");

        sb.append("4. STRATEGIC INSIGHTS\n");
        sb.append("─────────────────────────────────────────────────────────────────────────────────────\n");
        for (ExecutiveInsight insight : executiveInsights) {
            sb.append("  • " + insight.title + "\n");
            sb.append("    " + insight.recommendation + "\n\n");
        }

        sb.append("5. RECOMMENDATIONS\n");
        sb.append("─────────────────────────────────────────────────────────────────────────────────────\n");
        sb.append("  1. Continue expansion into high-performing sectors\n");
        sb.append("  2. Strengthen monitoring for high-risk projects\n");
        sb.append("  3. Enhance SDG alignment across all projects\n");
        sb.append("  4. Explore geographic expansion to new regions\n");
        sb.append("  5. Maintain strong governance and transparency\n\n");

        sb.append("╚═══════════════════════════════════════════════════════════════════════════════════════╝\n");
        sb.append("Generated by Namaa Smart Waqf Platform - Executive Dashboard\n");
        sb.append("For internal board use only.\n");
        sb.append("╚═══════════════════════════════════════════════════════════════════════════════════════╝\n");

        return sb.toString();
    }

    // ===== STATUS BAR =====
    private JPanel createStatusBar() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panel.setBorder(BorderFactory.createEtchedBorder());
        panel.setBackground(new Color(240, 240, 240));
        
        lblStatusMessage = new JLabel("🏛️ Executive Dashboard ready - Strategic view");
        lblStatusMessage.setFont(new Font("Arial", Font.PLAIN, 11));
        panel.add(lblStatusMessage);

        lblLastUpdated = new JLabel("Last Updated: " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd MMM yyyy HH:mm")));
        lblLastUpdated.setFont(new Font("Arial", Font.PLAIN, 10));
        lblLastUpdated.setForeground(Color.GRAY);
        panel.add(Box.createHorizontalGlue());
        panel.add(lblLastUpdated);

        return panel;
    }

    // ===== REFRESH DASHBOARD =====
    private void refreshDashboard() {
        initializeExecutiveData();
        updateExecutiveMetrics();
        updateAllTables();
        updateCharts();
        setStatus("🏛️ Executive Dashboard refreshed at " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")));
    }

    // ===== REFRESH EXECUTIVE DATA =====
    private void refreshExecutiveData() {
        // Refresh data from CSV
        ExecutiveDataService.refreshData();
        
        // Rebuild data structures
        buildExecutiveData();
        buildSDGContributions();
        buildGeographicRegions();
        buildRiskMetrics();
        generateExecutiveInsights();
        
        // Update all tables
        updateAllTables();
        
        // Update UI
        updateExecutiveMetrics();
        updateCharts();
        
        setStatus("🔄 Executive data refreshed from CSV at " + 
                  LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")));
    }

    // ===== UPDATE ALL TABLES =====
    private void updateAllTables() {
        loadSDGData();
        loadGeographicData();
        loadRiskMetrics();
    }

    // ===== UPDATE EXECUTIVE METRICS =====
    private void updateExecutiveMetrics() {
        lblTotalWaqfAssets.setText(String.format("%,.2f QR", executiveData.totalWaqfAssets));
        lblTotalWaqfAssetsChange.setText("▲ " + String.format("%.1f%%", executiveData.totalWaqfAssetsChange));
        
        lblActiveProjects.setText(String.valueOf(executiveData.activeProjects));
        lblActiveProjectsChange.setText("▲ " + String.format("%.1f%%", executiveData.activeProjectsChange));
        
        lblAvgPRI.setText(String.format("%.1f", executiveData.avgPRI));
        lblAvgPRIChange.setText("▲ " + String.format("%.1f%%", executiveData.avgPRIChange));
        
        lblAvgNamaa.setText(String.format("%.1f", executiveData.avgNamaa));
        lblAvgNamaaChange.setText("▲ " + String.format("%.1f%%", executiveData.avgNamaaChange));
        
        lblPortfolioRisk.setText(String.format("%.1f%%", executiveData.portfolioRisk));
        lblPortfolioRiskChange.setText(executiveData.portfolioRiskChange < 0 ? 
            "▼ " + String.format("%.1f%%", Math.abs(executiveData.portfolioRiskChange)) :
            "▲ " + String.format("%.1f%%", executiveData.portfolioRiskChange));
        
        lblAnnualImpact.setText(String.format("%.1f", executiveData.annualImpact));
        lblAnnualImpactChange.setText("▲ " + String.format("%.1f%%", executiveData.annualImpactChange));
        
        lblTotalBeneficiaries.setText(String.valueOf(executiveData.totalBeneficiaries));
        lblTotalBeneficiariesChange.setText("▲ " + String.format("%.1f%%", executiveData.totalBeneficiariesChange));
        
        lblGeographicCoverage.setText(String.valueOf(executiveData.geographicCoverage));
        lblGeographicCoverageChange.setText("+" + executiveData.geographicCoverageChange);
        
        lblSDGContributions.setText(String.valueOf(executiveData.sdgContributions));
        lblSDGContributionsChange.setText("+" + executiveData.sdgContributionsChange);
    }

    // ===== UPDATE CHARTS =====
    private void updateCharts() {
        txtTrendChart.setText(generateTrendChart());
        txtRiskChart.setText(generateRiskChart());
        txtSDGChart.setText(generateSDGChart());
        txtInsights.setText(generateStrategicInsights());
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
            case "refreshExecutive":
                refreshExecutiveData();
                JOptionPane.showMessageDialog(this,
                    "🏛️ Executive Dashboard refreshed!\n\n" +
                    "Overall Health: " + executiveData.overallHealth + "\n" +
                    "Total Assets: " + String.format("%,.2f QR", executiveData.totalWaqfAssets) + "\n" +
                    "Active Projects: " + executiveData.activeProjects + "\n" +
                    "Portfolio Risk: " + String.format("%.1f%%", executiveData.portfolioRisk) + "\n" +
                    "SDG Contributions: " + executiveData.sdgContributions + " SDGs",
                    "Refresh Complete",
                    JOptionPane.INFORMATION_MESSAGE);
                break;
                
            case "filterYear":
                setStatus("📅 Year filter: " + cmbYearFilter.getSelectedItem());
                break;
                
            case "generateBoardReport":
                generateBoardReportAction();
                break;
                
            case "exportExecutiveReport":
                exportExecutiveReport();
                break;
                
            case "printDashboard":
                printDashboard();
                break;
                
            default:
                break;
        }
    }

    // ===== GENERATE BOARD REPORT ACTION =====
    private void generateBoardReportAction() {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Board Report", true);
        dialog.setLayout(new BorderLayout(10, 10));
        dialog.setSize(700, 550);
        dialog.setLocationRelativeTo(this);

        JTextArea report = new JTextArea();
        report.setEditable(false);
        report.setFont(new Font("Monospaced", Font.PLAIN, 12));
        report.setBackground(new Color(255, 255, 240));
        report.setText(generateBoardReport());

        JScrollPane scroll = new JScrollPane(report);
        dialog.add(scroll, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton btnClose = new JButton("Close");
        btnClose.addActionListener(ev -> dialog.dispose());
        buttonPanel.add(btnClose);

        JButton btnExport = new JButton("📤 Export");
        btnExport.setBackground(new Color(0, 153, 76));
        btnExport.setForeground(Color.WHITE);
        btnExport.addActionListener(ev -> {
            try {
                String content = report.getText();
                String filename = "board_report_" + LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")) + ".txt";
                java.nio.file.Files.write(java.nio.file.Paths.get(filename), content.getBytes());
                JOptionPane.showMessageDialog(dialog, "✅ Report exported to: " + filename);
                dialog.dispose();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, "Error exporting: " + ex.getMessage());
            }
        });
        buttonPanel.add(btnExport);

        dialog.add(buttonPanel, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }

    // ===== EXPORT EXECUTIVE REPORT =====
    private void exportExecutiveReport() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Export Executive Report");
        fileChooser.setSelectedFile(new java.io.File("executive_report_" + 
            LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")) + ".txt"));
            
        int result = fileChooser.showSaveDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            try {
                java.io.File file = fileChooser.getSelectedFile();
                String report = generateBoardReport();
                java.nio.file.Files.write(file.toPath(), report.getBytes());
                
                JOptionPane.showMessageDialog(this,
                    "✅ Executive report exported successfully!\n" +
                    "File: " + file.getName(),
                    "Export Complete",
                    JOptionPane.INFORMATION_MESSAGE);
                setStatus("📤 Executive report exported: " + file.getName());
                
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this,
                    "Error exporting: " + ex.getMessage(),
                    "Export Failed",
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }
 // ===== FINANCIAL REPORT GENERATION =====
 // Add this method inside ExecutiveDashboardPanel class

    /**
     * Generates a comprehensive financial report for the Executive Dashboard
     * @return String containing the formatted financial report
     */
    private String generateFinancialReport() {
        StringBuilder sb = new StringBuilder();
        sb.append("FINANCIAL REPORT\n");
        sb.append("─────────────────────────────────────────────────────────────────────\n\n");
        
        // ===== 1. WAQF FINANCIALS =====
        double totalWaqfAmount = 0;
        double totalAvailableBalance = 0;
        double totalDonations = WaqfDonationService.getTotalDonations();
        double totalRepayments = 0;
        
        // Calculate total Waqf amounts
        java.util.ArrayList<CashWaqf> waqfs = WaqfService.getAllWaqfs();
        for (CashWaqf w : waqfs) {
            totalWaqfAmount += w.getWaqfAmount();
            totalAvailableBalance += w.getAvailableBalance();
        }
        
        // Calculate total repayments from all loans
        for (QardHasan loan : LoanService.getLoans()) {
            totalRepayments += LoanService.getTotalRepaidByLoan(loan.getLoanID());
        }
        
        // Calculate allocated funds
        double allocatedFunds = totalWaqfAmount - totalAvailableBalance;
        
        sb.append("WAQF FINANCIALS:\n");
        sb.append("  Total Waqf Amount:      " + String.format("%,.2f", totalWaqfAmount) + " QR\n");
        sb.append("  Available Balance:      " + String.format("%,.2f", totalAvailableBalance) + " QR\n");
        sb.append("  Total Donations:        " + String.format("%,.2f", totalDonations) + " QR\n");
        sb.append("  Total Repayments:       " + String.format("%,.2f", totalRepayments) + " QR\n");
        
        // Handle allocated funds correctly
        if (allocatedFunds < 0) {
            sb.append("  Allocated Funds:        " + String.format("%,.2f", 0.0) + " QR (No active loans)\n");
            sb.append("  💡 Note: Waqf has received " + String.format("%,.2f", Math.abs(allocatedFunds)) + " QR more than its original principal\n");
        } else {
            sb.append("  Allocated Funds:        " + String.format("%,.2f", allocatedFunds) + " QR\n");
        }
        
        double utilizationRate = totalWaqfAmount > 0 ? (allocatedFunds / totalWaqfAmount) * 100 : 0;
        if (utilizationRate < 0) utilizationRate = 0;
        sb.append("  Utilization Rate:       " + String.format("%.1f", utilizationRate) + "%\n\n");
        
        // ===== 2. LOAN FINANCIALS =====
        sb.append("LOAN FINANCIALS:\n");
        double totalLoans = 0;
        double totalRepaid = 0;
        int activeLoans = 0;
        int completedLoans = 0;
        int defaultedLoans = 0;
        
        for (QardHasan loan : LoanService.getLoans()) {
            totalLoans += loan.getLoanAmount();
            double repaid = LoanService.getTotalRepaidByLoan(loan.getLoanID());
            totalRepaid += repaid;
            
            String status = loan.getStatus();
            if ("Active".equalsIgnoreCase(status)) {
                activeLoans++;
            } else if ("Completed".equalsIgnoreCase(status) || "Paid".equalsIgnoreCase(status)) {
                completedLoans++;
            } else if ("Defaulted".equalsIgnoreCase(status)) {
                defaultedLoans++;
            }
        }
        
        double outstanding = totalLoans - totalRepaid;
        double repaymentRate = totalLoans > 0 ? (totalRepaid / totalLoans) * 100 : 0;
        
        sb.append("  Total Loans:            " + String.format("%,.2f", totalLoans) + " QR\n");
        sb.append("  Total Repaid:           " + String.format("%,.2f", totalRepaid) + " QR\n");
        sb.append("  Outstanding:            " + String.format("%,.2f", outstanding) + " QR\n");
        sb.append("  Repayment Rate:         " + String.format("%.1f", repaymentRate) + "%\n");
        sb.append("  Active Loans:           " + activeLoans + "\n");
        sb.append("  Completed Loans:        " + completedLoans + "\n");
        sb.append("  Defaulted Loans:        " + defaultedLoans + "\n\n");
        
        // ===== 3. WAQF DETAILS =====
        sb.append("WAQF DETAILS:\n");
        if (waqfs.isEmpty()) {
            sb.append("  No waqfs found.\n");
        } else {
            for (CashWaqf w : waqfs) {
                double allocated = w.getWaqfAmount() - w.getAvailableBalance();
                sb.append("  Waqf #" + w.getWaqfID() + ":\n");
                sb.append("    Total:              " + String.format("%,.2f", w.getWaqfAmount()) + " QR\n");
                sb.append("    Balance:            " + String.format("%,.2f", w.getAvailableBalance()) + " QR\n");
                if (allocated < 0) {
                    sb.append("    Allocated:          " + String.format("%,.2f", 0.0) + " QR\n");
                } else {
                    sb.append("    Allocated:          " + String.format("%,.2f", allocated) + " QR\n");
                }
                sb.append("    Status:             " + w.getStatus() + "\n");
                
                // Get condition
                WaqfCondition condition = WaqfConditionService.getConditionByWaqfId(w.getWaqfID());
                if (condition != null) {
                    sb.append("    Sector:             " + condition.getAllowedSector() + "\n");
                    sb.append("    Max Funding:        " + String.format("%,.2f", condition.getMaximumFunding()) + " QR\n");
                    sb.append("    Min PRI:            " + condition.getMinimumPRI() + "\n");
                    sb.append("    Target Group:       " + condition.getTargetBeneficiaries() + "\n");
                }
                sb.append("\n");
            }
        }
        
        // ===== 4. FINANCIAL SUMMARY =====
        sb.append("FINANCIAL SUMMARY:\n");
        double totalWaqfUtilization = totalWaqfAmount > 0 ? (allocatedFunds / totalWaqfAmount) * 100 : 0;
        if (totalWaqfUtilization < 0) totalWaqfUtilization = 0;
        double totalRepaymentPerformance = totalLoans > 0 ? (totalRepaid / totalLoans) * 100 : 0;
        
        sb.append("  Overall Waqf Utilization:   " + String.format("%.1f", totalWaqfUtilization) + "%\n");
        sb.append("  Overall Repayment Performance: " + String.format("%.1f", totalRepaymentPerformance) + "%\n");
        
        // Determine financial health status
        String healthStatus;
        if (totalWaqfUtilization > 70 && totalRepaymentPerformance > 85) {
            healthStatus = "🌟 Excellent";
        } else if (totalWaqfUtilization > 50 && totalRepaymentPerformance > 70) {
            healthStatus = "✅ Good";
        } else if (totalWaqfUtilization > 30 && totalRepaymentPerformance > 50) {
            healthStatus = "⚠️ Fair";
        } else {
            healthStatus = "❌ Needs Improvement";
        }
        sb.append("  Financial Health:         " + healthStatus + "\n\n");
        
        // ===== 5. FOOTER =====
        sb.append("─────────────────────────────────────────────────────────────────────\n");
        sb.append("Report generated: " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd MMM yyyy HH:mm")) + "\n");
        sb.append("─────────────────────────────────────────────────────────────────────\n");
        
        return sb.toString();
    }

    // ===== PRINT DASHBOARD =====
    private void printDashboard() {
        JOptionPane.showMessageDialog(this,
            "🖨️ Print Dashboard\n\n" +
            "This would send the current dashboard view to the printer.\n" +
            "For this demo, the report has been generated in the Board Report tab.",
            "Print Dashboard",
            JOptionPane.INFORMATION_MESSAGE);
    }
}