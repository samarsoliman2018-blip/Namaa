package UI;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import Model.*;
import Service.*;

public class DashboardFrame extends JFrame implements ActionListener {
	static {
	    // Force service initialization
	    Class<?> userService = UserService.class;
	    Class<?> waqfService = WaqfService.class;
	    Class<?> fundingService = FundingService.class;
	}
    private User user;
    private SessionManager sessionManager;
    private JPanel mainPanel;
    private CardLayout cardLayout;
    private JLabel statusLabel;
    private JLabel moduleLabel;
    private JPanel statusBar;
    private JPanel welcomePanel;
    private JMenuBar menuBar;
    private JToolBar toolbar;
    private JButton btnRefreshAll;
    
    // Dashboard Panels
    private WaqifDashboardPanel waqifDashboard;
    private BeneficiaryDashboardPanel beneficiaryDashboard;
    private CommitteeDashboardPanel committeeDashboard;
    private AdminDashboardPanel adminDashboard;
    private AIKnowledgeDashboardPanel aiKnowledgeDashboard;
    private ExecutiveDashboardPanel executiveDashboard;
    
    // Individual Module Panels
    private CashWaqfPanel cashWaqfPanel;
    private FundingPanel fundingPanel;
    private AssessmentPanel assessmentPanel;
    private CommitteePanel committeePanel;
    private LoanPanel loanPanel;
    private NamaaIndexPanel namaaIndexPanel;
    private ReportPanel reportPanel;
    private AnalyticsPanel analyticsPanel;
    
    // Store card names for navigation
    private java.util.List<String> cardNames;

    public DashboardFrame(User user) {
        this.user = user;
        this.cardNames = new java.util.ArrayList<>();
        
        setTitle("Namaa Smart Waqf Platform - " + user.getFullName());
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        setLayout(new BorderLayout());

        // ===== CREATE MENU BAR =====
        menuBar = new JMenuBar();
        createMenuBar();
        setJMenuBar(menuBar);

        // ===== TOOLBAR =====
        toolbar = new JToolBar();
        toolbar.setFloatable(false);
        toolbar.setBackground(new Color(240, 240, 240));
        createToolbar();
        add(toolbar, BorderLayout.NORTH);

        // ===== MAIN PANEL (Card Layout) =====
        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);
        mainPanel.setBackground(Color.WHITE);

        // Initialize ALL panels
        initializeAllPanels();

        // Add welcome panel
        welcomePanel = createWelcomePanel(user);
        mainPanel.add(welcomePanel, "welcome");
        cardNames.add("welcome");

        // ===== ADD ALL MODULES =====
        addAllModules();

        add(mainPanel, BorderLayout.CENTER);

        // ===== STATUS BAR =====
        statusBar = new JPanel(new BorderLayout());
        statusBar.setBorder(BorderFactory.createEtchedBorder());
        statusBar.setPreferredSize(new Dimension(getWidth(), 28));

        statusLabel = new JLabel(" Welcome, " + user.getFullName() + " | Role: " + getRoleDisplayName());
        statusLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        statusBar.add(statusLabel, BorderLayout.WEST);

        moduleLabel = new JLabel("Module: Dashboard");
        moduleLabel.setFont(new Font("Arial", Font.PLAIN, 11));
        moduleLabel.setForeground(Color.GRAY);
        statusBar.add(moduleLabel, BorderLayout.CENTER);

        JLabel timeLabel = new JLabel(LocalDate.now().format(DateTimeFormatter.ofPattern("dd MMMM yyyy")));
        timeLabel.setFont(new Font("Arial", Font.PLAIN, 10));
        timeLabel.setForeground(Color.GRAY);
        statusBar.add(timeLabel, BorderLayout.EAST);

        add(statusBar, BorderLayout.SOUTH);

        // Show welcome by default
        cardLayout.show(mainPanel, "welcome");
        
        // Print debug info
        System.out.println("=== CARDS IN MAIN PANEL ===");
        for (String name : cardNames) {
            System.out.println("  - " + name);
        }
        
        setVisible(true);
        startSessionManagement();
    }
    
    
    // ===== ADD THIS METHOD =====
    private long getLastActivityTime() {
        // Use reflection or maintain activity time in SessionManager
        // For simplicity, assume session is active
        return System.currentTimeMillis();
    }
    
    

    // ===== INITIALIZE ALL PANELS =====
    private void initializeAllPanels() {
        System.out.println("=== INITIALIZING ALL PANELS ===");
        System.out.println("User class: " + user.getClass().getSimpleName());

        // Core module panels - these work for everyone
        cashWaqfPanel = new CashWaqfPanel();
        fundingPanel = new FundingPanel();
        assessmentPanel = new AssessmentPanel();
        committeePanel = new CommitteePanel();
        loanPanel = new LoanPanel();
        namaaIndexPanel = new NamaaIndexPanel();
        reportPanel = new ReportPanel();
        analyticsPanel = new AnalyticsPanel();
        System.out.println("  Core panels initialized");

        // Role-specific dashboards - only initialize the one that matches the user
        if (user instanceof Waqif) {
            try {
                waqifDashboard = new WaqifDashboardPanel((Waqif) user);
                System.out.println("  WaqifDashboard initialized");
            } catch (Exception e) {
                System.out.println("  WaqifDashboard init failed: " + e.getMessage());
                waqifDashboard = null;
            }
        } else {
            waqifDashboard = null;
        }

        if (user instanceof Beneficiary) {
            try {
                beneficiaryDashboard = new BeneficiaryDashboardPanel((Beneficiary) user);
                System.out.println("  BeneficiaryDashboard initialized");
            } catch (Exception e) {
                System.out.println("  BeneficiaryDashboard init failed: " + e.getMessage());
                beneficiaryDashboard = null;
            }
        } else {
            beneficiaryDashboard = null;
        }

        if (user instanceof CommitteeMember) {
            try {
                committeeDashboard = new CommitteeDashboardPanel((CommitteeMember) user);
                System.out.println("  CommitteeDashboard initialized");
            } catch (Exception e) {
                System.out.println("  CommitteeDashboard init failed: " + e.getMessage());
                committeeDashboard = null;
            }
        } else {
            committeeDashboard = null;
        }

        if (user instanceof Administrator) {
            try {
                adminDashboard = new AdminDashboardPanel((Administrator) user);
                System.out.println("  AdminDashboard initialized");
            } catch (Exception e) {
                System.out.println("  AdminDashboard init failed: " + e.getMessage());
                adminDashboard = null;
            }
        } else {
            adminDashboard = null;
        }

        // These are always initialized
        aiKnowledgeDashboard = new AIKnowledgeDashboardPanel();
        executiveDashboard = new ExecutiveDashboardPanel();
        System.out.println("  AIKnowledgeDashboard and ExecutiveDashboard initialized");
    }

    // ===== ADD ALL MODULES TO MAIN PANEL =====
    private void addAllModules() {
        System.out.println("=== ADDING ALL MODULES ===");

        // AI Knowledge Dashboard (all users)
        mainPanel.add(aiKnowledgeDashboard, "aiKnowledge");
        cardNames.add("aiKnowledge");
        System.out.println("  Added: aiKnowledge");

        // Executive Dashboard (all users)
        mainPanel.add(executiveDashboard, "executive");
        cardNames.add("executive");
        System.out.println("  Added: executive");

        // Add role-specific dashboard
        if (user instanceof Administrator && adminDashboard != null) {
            mainPanel.add(adminDashboard, "adminDashboard");
            cardNames.add("adminDashboard");
            System.out.println("  Added: adminDashboard");
        } else if (user instanceof Waqif && waqifDashboard != null) {
            mainPanel.add(waqifDashboard, "waqifDashboard");
            cardNames.add("waqifDashboard");
            System.out.println("  Added: waqifDashboard");
        } else if (user instanceof Beneficiary && beneficiaryDashboard != null) {
            mainPanel.add(beneficiaryDashboard, "beneficiaryDashboard");
            cardNames.add("beneficiaryDashboard");
            System.out.println("  Added: beneficiaryDashboard");
        } else if (user instanceof CommitteeMember && committeeDashboard != null) {
            mainPanel.add(committeeDashboard, "committeeDashboard");
            cardNames.add("committeeDashboard");
            System.out.println("  Added: committeeDashboard");
        }

        // Add all module panels (available to everyone)
        mainPanel.add(cashWaqfPanel, "waqf");
        cardNames.add("waqf");
        
        mainPanel.add(fundingPanel, "funding");
        cardNames.add("funding");
        
        mainPanel.add(assessmentPanel, "assessment");
        cardNames.add("assessment");
        
        mainPanel.add(committeePanel, "committee");
        cardNames.add("committee");
        
        mainPanel.add(loanPanel, "loan");
        cardNames.add("loan");
        
        mainPanel.add(namaaIndexPanel, "index");
        cardNames.add("index");
        
        mainPanel.add(reportPanel, "report");
        cardNames.add("report");
        
        mainPanel.add(analyticsPanel, "analytics");
        cardNames.add("analytics");
        
        System.out.println("  Added: waqf, funding, assessment, committee, loan, index, report, analytics");
    }

    // ===== GET ROLE DISPLAY NAME =====
    private String getRoleDisplayName() {
        if (user instanceof Waqif) return "Donor (Waqif)";
        if (user instanceof Beneficiary) return "Beneficiary";
        if (user instanceof Administrator) return "System Administrator";
        if (user instanceof CommitteeMember) return "Committee Member";
        return "User";
    }

    // ===== CREATE MENU BAR =====
    private void createMenuBar() {
        JMenu fileMenu = new JMenu("File");
        fileMenu.setFont(new Font("Arial", Font.BOLD, 14));
        fileMenu.setMnemonic(KeyEvent.VK_F);

        JMenuItem miDashboard = new JMenuItem("Dashboard");
        miDashboard.setFont(new Font("Arial", Font.PLAIN, 13));
        miDashboard.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_D, InputEvent.CTRL_DOWN_MASK));
        miDashboard.addActionListener(this);
        miDashboard.setActionCommand("welcome");
        fileMenu.add(miDashboard);

        fileMenu.addSeparator();

        JMenuItem miLogout = new JMenuItem("Logout");
        miLogout.setFont(new Font("Arial", Font.PLAIN, 13));
        miLogout.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_L, InputEvent.CTRL_DOWN_MASK));
        miLogout.addActionListener(this);
        miLogout.setActionCommand("Logout");
        fileMenu.add(miLogout);

        JMenuItem miExit = new JMenuItem("Exit");
        miExit.setFont(new Font("Arial", Font.PLAIN, 13));
        miExit.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_X, InputEvent.CTRL_DOWN_MASK));
        miExit.addActionListener(this);
        miExit.setActionCommand("Exit");
        fileMenu.add(miExit);

        menuBar.add(fileMenu);

        // ---- MODULES MENU (Role-Based) ----
        JMenu modulesMenu = new JMenu("Modules");
        modulesMenu.setFont(new Font("Arial", Font.BOLD, 14));
        modulesMenu.setMnemonic(KeyEvent.VK_M);

        if (user instanceof Administrator) {
            addAdminModules(modulesMenu);
        } else if (user instanceof Waqif) {
            addWaqifModules(modulesMenu);
        } else if (user instanceof Beneficiary) {
            addBeneficiaryModules(modulesMenu);
        } else if (user instanceof CommitteeMember) {
            addCommitteeModules(modulesMenu);
        }

        menuBar.add(modulesMenu);

        // ---- KNOWLEDGE MENU (All users) ----
        JMenu knowledgeMenu = new JMenu("Knowledge");
        knowledgeMenu.setFont(new Font("Arial", Font.BOLD, 14));
        knowledgeMenu.setMnemonic(KeyEvent.VK_K);

        JMenuItem miAIKnowledge = new JMenuItem("🧠 AI Knowledge Dashboard");
        miAIKnowledge.setFont(new Font("Arial", Font.PLAIN, 13));
        miAIKnowledge.addActionListener(this);
        miAIKnowledge.setActionCommand("aiKnowledge");
        knowledgeMenu.add(miAIKnowledge);

        menuBar.add(knowledgeMenu);

        // ---- EXECUTIVE MENU (Admin only) ----
        if (user instanceof Administrator) {
            JMenu executiveMenu = new JMenu("Executive");
            executiveMenu.setFont(new Font("Arial", Font.BOLD, 14));
            executiveMenu.setMnemonic(KeyEvent.VK_E);

            JMenuItem miExecutive = new JMenuItem("🏛️ Executive Dashboard");
            miExecutive.setFont(new Font("Arial", Font.PLAIN, 13));
            miExecutive.addActionListener(this);
            miExecutive.setActionCommand("executive");
            executiveMenu.add(miExecutive);

            menuBar.add(executiveMenu);
        }

        // ---- VIEW MENU ----
        JMenu viewMenu = new JMenu("View");
        viewMenu.setFont(new Font("Arial", Font.BOLD, 14));
        viewMenu.setMnemonic(KeyEvent.VK_V);

        JMenuItem miRefresh = new JMenuItem("Refresh All");
        miRefresh.setFont(new Font("Arial", Font.PLAIN, 13));
        miRefresh.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_R, InputEvent.CTRL_DOWN_MASK));
        miRefresh.addActionListener(this);
        miRefresh.setActionCommand("Refresh");
        viewMenu.add(miRefresh);

        viewMenu.addSeparator();

        JCheckBoxMenuItem miStatusBar = new JCheckBoxMenuItem("Show Status Bar");
        miStatusBar.setFont(new Font("Arial", Font.PLAIN, 13));
        miStatusBar.setSelected(true);
        miStatusBar.addActionListener(this);
        miStatusBar.setActionCommand("toggleStatusBar");
        viewMenu.add(miStatusBar);

        menuBar.add(viewMenu);

        // ---- HELP MENU ----
        JMenu helpMenu = new JMenu("Help");
        helpMenu.setFont(new Font("Arial", Font.BOLD, 14));
        helpMenu.setMnemonic(KeyEvent.VK_H);

        JMenuItem miHelp = new JMenuItem("Help Topics");
        miHelp.setFont(new Font("Arial", Font.PLAIN, 13));
        miHelp.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_H, InputEvent.CTRL_DOWN_MASK));
        miHelp.addActionListener(this);
        miHelp.setActionCommand("help");
        helpMenu.add(miHelp);

        JMenuItem miUserGuide = new JMenuItem("User Guide");
        miUserGuide.setFont(new Font("Arial", Font.PLAIN, 13));
        miUserGuide.addActionListener(this);
        miUserGuide.setActionCommand("userGuide");
        helpMenu.add(miUserGuide);

        helpMenu.addSeparator();

        JMenuItem miAbout = new JMenuItem("About Namaa");
        miAbout.setFont(new Font("Arial", Font.PLAIN, 13));
        miAbout.addActionListener(this);
        miAbout.setActionCommand("about");
        helpMenu.add(miAbout);

        menuBar.add(helpMenu);

        // ---- AI MENU (Admin only) ----
        if (user instanceof Administrator) {
            JMenu aiMenu = new JMenu("AI");
            aiMenu.setFont(new Font("Arial", Font.BOLD, 14));
            aiMenu.setMnemonic(KeyEvent.VK_A);

            JMenuItem miAIConfig = new JMenuItem("AI Configuration");
            miAIConfig.setFont(new Font("Arial", Font.PLAIN, 13));
            miAIConfig.addActionListener(this);
            miAIConfig.setActionCommand("aiConfig");
            aiMenu.add(miAIConfig);

            JMenuItem miAIStatus = new JMenuItem("AI Status");
            miAIStatus.setFont(new Font("Arial", Font.PLAIN, 13));
            miAIStatus.addActionListener(this);
            miAIStatus.setActionCommand("aiStatus");
            aiMenu.add(miAIStatus);

            menuBar.add(aiMenu);
        }

        // ---- USER INFO ----
        JPanel userPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        userPanel.setBackground(new Color(240, 240, 240));

        String roleEmoji = user instanceof Waqif ? "🤲" :
                          user instanceof Beneficiary ? "👤" :
                          user instanceof Administrator ? "⚙️" : "👥";

        JLabel lblUserInfo = new JLabel(roleEmoji + " " + user.getFullName() + " (" + getRoleDisplayName() + ")");
        lblUserInfo.setFont(new Font("Arial", Font.BOLD, 12));
        lblUserInfo.setForeground(new Color(0, 102, 204));
        userPanel.add(lblUserInfo);

        JSeparator separator = new JSeparator(JSeparator.VERTICAL);
        separator.setPreferredSize(new Dimension(1, 30));
        userPanel.add(separator);

        JButton btnLogoutQuick = new JButton("Logout");
        btnLogoutQuick.setFont(new Font("Arial", Font.BOLD, 11));
        btnLogoutQuick.setToolTipText("Logout");
        btnLogoutQuick.setFocusPainted(false);
        btnLogoutQuick.setBorder(BorderFactory.createEmptyBorder(2, 10, 2, 10));
        btnLogoutQuick.setBackground(new Color(200, 50, 50));
        btnLogoutQuick.setForeground(Color.WHITE);
        btnLogoutQuick.addActionListener(this);
        btnLogoutQuick.setActionCommand("Logout");
        userPanel.add(btnLogoutQuick);

        menuBar.add(Box.createHorizontalGlue());
        menuBar.add(userPanel);
    }

    // ===== ADMIN MODULES =====
    private void addAdminModules(JMenu modulesMenu) {
        JMenuItem miDashboard = new JMenuItem("⚙️ Admin Dashboard");
        miDashboard.setFont(new Font("Arial", Font.PLAIN, 13));
        miDashboard.addActionListener(this);
        miDashboard.setActionCommand("adminDashboard");
        modulesMenu.add(miDashboard);

        modulesMenu.addSeparator();

        JMenuItem miWaqf = new JMenuItem("💰 Cash Waqf");
        miWaqf.setFont(new Font("Arial", Font.PLAIN, 13));
        miWaqf.addActionListener(this);
        miWaqf.setActionCommand("waqf");
        modulesMenu.add(miWaqf);

        JMenuItem miFunding = new JMenuItem("📝 Funding Applications");
        miFunding.setFont(new Font("Arial", Font.PLAIN, 13));
        miFunding.addActionListener(this);
        miFunding.setActionCommand("funding");
        modulesMenu.add(miFunding);

        JMenuItem miAssessment = new JMenuItem("🤖 AI Assessment");
        miAssessment.setFont(new Font("Arial", Font.PLAIN, 13));
        miAssessment.addActionListener(this);
        miAssessment.setActionCommand("assessment");
        modulesMenu.add(miAssessment);

        JMenuItem miCommittee = new JMenuItem("👥 Committee");
        miCommittee.setFont(new Font("Arial", Font.PLAIN, 13));
        miCommittee.addActionListener(this);
        miCommittee.setActionCommand("committee");
        modulesMenu.add(miCommittee);

        JMenuItem miLoan = new JMenuItem("💰 Loans");
        miLoan.setFont(new Font("Arial", Font.PLAIN, 13));
        miLoan.addActionListener(this);
        miLoan.setActionCommand("loan");
        modulesMenu.add(miLoan);

        JMenuItem miIndex = new JMenuItem("📊 Namaa Index");
        miIndex.setFont(new Font("Arial", Font.PLAIN, 13));
        miIndex.addActionListener(this);
        miIndex.setActionCommand("index");
        modulesMenu.add(miIndex);

        JMenuItem miReport = new JMenuItem("📄 Reports");
        miReport.setFont(new Font("Arial", Font.PLAIN, 13));
        miReport.addActionListener(this);
        miReport.setActionCommand("report");
        modulesMenu.add(miReport);

        JMenuItem miAnalytics = new JMenuItem("📊 Analytics");
        miAnalytics.setFont(new Font("Arial", Font.PLAIN, 13));
        miAnalytics.addActionListener(this);
        miAnalytics.setActionCommand("analytics");
        modulesMenu.add(miAnalytics);
    }

    // ===== WAQIF MODULES =====
    private void addWaqifModules(JMenu modulesMenu) {
        JMenuItem miDashboard = new JMenuItem("📊 My Impact Dashboard");
        miDashboard.setFont(new Font("Arial", Font.PLAIN, 13));
        miDashboard.addActionListener(this);
        miDashboard.setActionCommand("waqifDashboard");
        modulesMenu.add(miDashboard);

        JMenuItem miWaqf = new JMenuItem("💰 My Waqfs");
        miWaqf.setFont(new Font("Arial", Font.PLAIN, 13));
        miWaqf.addActionListener(this);
        miWaqf.setActionCommand("waqf");
        modulesMenu.add(miWaqf);

        JMenuItem miDonate = new JMenuItem("💳 Make Donation");
        miDonate.setFont(new Font("Arial", Font.PLAIN, 13));
        miDonate.addActionListener(this);
        miDonate.setActionCommand("funding");
        modulesMenu.add(miDonate);

        JMenuItem miAnalytics = new JMenuItem("📊 Impact Analytics");
        miAnalytics.setFont(new Font("Arial", Font.PLAIN, 13));
        miAnalytics.addActionListener(this);
        miAnalytics.setActionCommand("analytics");
        modulesMenu.add(miAnalytics);

        JMenuItem miReports = new JMenuItem("📄 My Reports");
        miReports.setFont(new Font("Arial", Font.PLAIN, 13));
        miReports.addActionListener(this);
        miReports.setActionCommand("report");
        modulesMenu.add(miReports);
    }

    // ===== BENEFICIARY MODULES =====
    private void addBeneficiaryModules(JMenu modulesMenu) {
        JMenuItem miDashboard = new JMenuItem("📊 My Workspace");
        miDashboard.setFont(new Font("Arial", Font.PLAIN, 13));
        miDashboard.addActionListener(this);
        miDashboard.setActionCommand("beneficiaryDashboard");
        modulesMenu.add(miDashboard);

        JMenuItem miFunding = new JMenuItem("📝 Apply for Funding");
        miFunding.setFont(new Font("Arial", Font.PLAIN, 13));
        miFunding.addActionListener(this);
        miFunding.setActionCommand("funding");
        modulesMenu.add(miFunding);

        JMenuItem miLoan = new JMenuItem("💰 My Loans");
        miLoan.setFont(new Font("Arial", Font.PLAIN, 13));
        miLoan.addActionListener(this);
        miLoan.setActionCommand("loan");
        modulesMenu.add(miLoan);

        JMenuItem miReports = new JMenuItem("📄 My Reports");
        miReports.setFont(new Font("Arial", Font.PLAIN, 13));
        miReports.addActionListener(this);
        miReports.setActionCommand("report");
        modulesMenu.add(miReports);
    }

    // ===== COMMITTEE MODULES =====
    private void addCommitteeModules(JMenu modulesMenu) {
        JMenuItem miDashboard = new JMenuItem("👥 Committee Dashboard");
        miDashboard.setFont(new Font("Arial", Font.PLAIN, 13));
        miDashboard.addActionListener(this);
        miDashboard.setActionCommand("committeeDashboard");
        modulesMenu.add(miDashboard);

        JMenuItem miCommittee = new JMenuItem("📋 Review Applications");
        miCommittee.setFont(new Font("Arial", Font.PLAIN, 13));
        miCommittee.addActionListener(this);
        miCommittee.setActionCommand("committee");
        modulesMenu.add(miCommittee);

        JMenuItem miAssessment = new JMenuItem("🤖 View Assessments");
        miAssessment.setFont(new Font("Arial", Font.PLAIN, 13));
        miAssessment.addActionListener(this);
        miAssessment.setActionCommand("assessment");
        modulesMenu.add(miAssessment);

        JMenuItem miIndex = new JMenuItem("📊 Namaa Index");
        miIndex.setFont(new Font("Arial", Font.PLAIN, 13));
        miIndex.addActionListener(this);
        miIndex.setActionCommand("index");
        modulesMenu.add(miIndex);

        JMenuItem miAnalytics = new JMenuItem("📊 Analytics");
        miAnalytics.setFont(new Font("Arial", Font.PLAIN, 13));
        miAnalytics.addActionListener(this);
        miAnalytics.setActionCommand("analytics");
        modulesMenu.add(miAnalytics);
    }

    // ===== CREATE TOOLBAR =====
    private void createToolbar() {
        toolbar.removeAll();

        JButton btnDashboard = new JButton("🏠 Dashboard");
        btnDashboard.setActionCommand("welcome");
        btnDashboard.addActionListener(this);
        btnDashboard.setFont(new Font("Arial", Font.PLAIN, 11));
        btnDashboard.setFocusPainted(false);
        btnDashboard.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        toolbar.add(btnDashboard);

        toolbar.addSeparator();

        if (user instanceof Administrator) {
            toolbar.add(createToolbarButton("⚙️ Admin", "adminDashboard"));
            toolbar.add(createToolbarButton("💰 Waqf", "waqf"));
            toolbar.add(createToolbarButton("📝 Funding", "funding"));
            toolbar.add(createToolbarButton("🤖 AI", "assessment"));
            toolbar.add(createToolbarButton("👥 Committee", "committee"));
            toolbar.add(createToolbarButton("💰 Loans", "loan"));
            toolbar.add(createToolbarButton("📊 Index", "index"));
            toolbar.add(createToolbarButton("📄 Reports", "report"));
            toolbar.add(createToolbarButton("📊 Analytics", "analytics"));
            toolbar.add(createToolbarButton("🏛️ Executive", "executive"));
        } else if (user instanceof Waqif) {
            toolbar.add(createToolbarButton("📊 Dashboard", "waqifDashboard"));
            toolbar.add(createToolbarButton("💰 Waqfs", "waqf"));
            toolbar.add(createToolbarButton("💳 Donate", "funding"));
            toolbar.add(createToolbarButton("📊 Analytics", "analytics"));
            toolbar.add(createToolbarButton("📄 Reports", "report"));
        } else if (user instanceof Beneficiary) {
            toolbar.add(createToolbarButton("📊 Workspace", "beneficiaryDashboard"));
            toolbar.add(createToolbarButton("📝 Apply", "funding"));
            toolbar.add(createToolbarButton("💰 Loans", "loan"));
            toolbar.add(createToolbarButton("📄 Reports", "report"));
        } else if (user instanceof CommitteeMember) {
            toolbar.add(createToolbarButton("👥 Committee", "committeeDashboard"));
            toolbar.add(createToolbarButton("📋 Review", "committee"));
            toolbar.add(createToolbarButton("🤖 AI", "assessment"));
            toolbar.add(createToolbarButton("📊 Index", "index"));
            toolbar.add(createToolbarButton("📊 Analytics", "analytics"));
        }

        toolbar.addSeparator();
        toolbar.add(createToolbarButton("🧠 Knowledge", "aiKnowledge"));

        toolbar.addSeparator();

        btnRefreshAll = new JButton("🔄 Refresh");
        btnRefreshAll.setFont(new Font("Arial", Font.BOLD, 12));
        btnRefreshAll.setFocusPainted(false);
        btnRefreshAll.setBackground(new Color(0, 102, 204));
        btnRefreshAll.setForeground(Color.BLACK);
        btnRefreshAll.addActionListener(this);
        btnRefreshAll.setActionCommand("Refresh");
        toolbar.add(btnRefreshAll);

        toolbar.revalidate();
        toolbar.repaint();
    }

    private JButton createToolbarButton(String text, String action) {
        JButton button = new JButton(text);
        button.setActionCommand(action);
        button.addActionListener(this);
        button.setFont(new Font("Arial", Font.PLAIN, 11));
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        return button;
    }

    // ===== CREATE WELCOME PANEL =====
    private JPanel createWelcomePanel(User user) {
        JPanel panel = new JPanel(new BorderLayout(20, 20));
        panel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));
        panel.setBackground(new Color(245, 248, 250));

        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(245, 248, 250));

        JLabel welcomeLabel = new JLabel("Welcome to Namaa Smart Waqf Platform, " + user.getFullName() + "!", JLabel.CENTER);
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 28));
        welcomeLabel.setForeground(new Color(0, 102, 204));
        headerPanel.add(welcomeLabel, BorderLayout.NORTH);

        String role = user.getClass().getSimpleName();
        String roleDesc = role.equals("Administrator") ? "System Administrator" :
                         role.equals("Waqif") ? "Donor (Waqif)" :
                         role.equals("Beneficiary") ? "Beneficiary" : "Committee Member";

        JLabel roleLabel = new JLabel("Role: " + roleDesc, JLabel.CENTER);
        roleLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        roleLabel.setForeground(Color.GRAY);
        headerPanel.add(roleLabel, BorderLayout.CENTER);

        panel.add(headerPanel, BorderLayout.NORTH);

        // ===== QUICK ACCESS CARDS =====
        JPanel cardsPanel = new JPanel(new GridLayout(2, 4, 15, 15));
        cardsPanel.setBackground(new Color(245, 248, 250));

        String[][] modules = getQuickAccessModules();

        for (String[] module : modules) {
            cardsPanel.add(createQuickAccessCard(module[0], module[1], module[2], module[3]));
        }

        panel.add(cardsPanel, BorderLayout.CENTER);

        // Footer
        JPanel footerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        footerPanel.setBackground(new Color(245, 248, 250));

        JButton btnGoToModule = new JButton("🚀 Go to Modules");
        btnGoToModule.setFont(new Font("Arial", Font.BOLD, 14));
        btnGoToModule.setBackground(new Color(0, 102, 204));
        btnGoToModule.setForeground(Color.BLACK);
        btnGoToModule.setFocusPainted(false);
        btnGoToModule.setPreferredSize(new Dimension(180, 40));
        btnGoToModule.addActionListener(e -> showModuleNavigationDialog());
        footerPanel.add(btnGoToModule);

        JButton btnRefresh = new JButton("🔄 Refresh Data");
        btnRefresh.setFont(new Font("Arial", Font.BOLD, 14));
        btnRefresh.setBackground(new Color(200, 200, 200));
        btnRefresh.setFocusPainted(false);
        btnRefresh.setPreferredSize(new Dimension(150, 40));
        btnRefresh.addActionListener(e -> {
            refreshAll();
            HistoricalDataService.refresh();
            JOptionPane.showMessageDialog(this, "✅ All data refreshed successfully!", "Refresh Complete", JOptionPane.INFORMATION_MESSAGE);
        });
        footerPanel.add(btnRefresh);

        panel.add(footerPanel, BorderLayout.SOUTH);

        return panel;
    }

    // ===== GET QUICK ACCESS MODULES =====
    private String[][] getQuickAccessModules() {
        if (user instanceof Administrator) {
            return new String[][]{
                {"⚙️", "Admin Dashboard", "System administration", "adminDashboard"},
                {"💰", "Cash Waqf", "Manage Waqfs", "waqf"},
                {"📝", "Funding", "Manage applications", "funding"},
                {"🤖", "AI Assessment", "AI-powered evaluation", "assessment"},
                {"👥", "Committee", "Committee decisions", "committee"},
                {"💰", "Loans", "Manage loans", "loan"},
                {"📊", "Namaa Index", "Impact measurement", "index"},
                {"📄", "Reports", "Generate reports", "report"},
                {"📊", "Analytics", "View analytics", "analytics"},
                {"🧠", "Knowledge", "AI Knowledge Base", "aiKnowledge"},
                {"🏛️", "Executive", "Executive view", "executive"}
            };
        } else if (user instanceof Waqif) {
            return new String[][]{
                {"📊", "My Impact", "View your impact metrics", "waqifDashboard"},
                {"💰", "My Waqfs", "Manage your Waqfs", "waqf"},
                {"💳", "Donate", "Make a donation", "funding"},
                {"📊", "Analytics", "View analytics", "analytics"},
                {"📄", "Reports", "View reports", "report"},
                {"🧠", "Knowledge", "AI Knowledge Base", "aiKnowledge"}
            };
        } else if (user instanceof Beneficiary) {
            return new String[][]{
                {"📊", "My Workspace", "Your entrepreneur workspace", "beneficiaryDashboard"},
                {"📝", "Apply", "Apply for funding", "funding"},
                {"💰", "My Loans", "View your loans", "loan"},
                {"📄", "Reports", "Submit reports", "report"},
                {"🧠", "Knowledge", "AI Knowledge Base", "aiKnowledge"}
            };
        } else if (user instanceof CommitteeMember) {
            return new String[][]{
                {"👥", "Committee", "Committee dashboard", "committeeDashboard"},
                {"📋", "Review", "Review applications", "committee"},
                {"🤖", "AI", "View assessments", "assessment"},
                {"📊", "Index", "Namaa Index", "index"},
                {"📊", "Analytics", "View analytics", "analytics"},
                {"🧠", "Knowledge", "AI Knowledge Base", "aiKnowledge"}
            };
        }
        return new String[][]{{"📊", "Dashboard", "Main dashboard", "welcome"}};
    }

    // ===== CREATE QUICK ACCESS CARD =====
    private JPanel createQuickAccessCard(String emoji, String title, String description, String action) {
        JPanel card = new JPanel(new BorderLayout(5, 5));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        card.setCursor(new Cursor(Cursor.HAND_CURSOR));

        JLabel emojiLabel = new JLabel(emoji);
        emojiLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 32));
        emojiLabel.setHorizontalAlignment(SwingConstants.CENTER);
        card.add(emojiLabel, BorderLayout.NORTH);

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 14));
        titleLabel.setForeground(new Color(0, 51, 102));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        card.add(titleLabel, BorderLayout.CENTER);

        JLabel descLabel = new JLabel(description);
        descLabel.setFont(new Font("Arial", Font.PLAIN, 10));
        descLabel.setForeground(Color.GRAY);
        descLabel.setHorizontalAlignment(SwingConstants.CENTER);
        card.add(descLabel, BorderLayout.SOUTH);

        card.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                navigateToModule(action);
            }
            @Override
            public void mouseEntered(MouseEvent e) {
                card.setBackground(new Color(240, 248, 255));
                card.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(0, 102, 204), 2),
                    BorderFactory.createEmptyBorder(15, 15, 15, 15)
                ));
            }
            @Override
            public void mouseExited(MouseEvent e) {
                card.setBackground(Color.WHITE);
                card.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
                    BorderFactory.createEmptyBorder(15, 15, 15, 15)
                ));
            }
        });

        return card;
    }

    // ===== SHOW MODULE NAVIGATION DIALOG =====
    private void showModuleNavigationDialog() {
        String[][] modules = getQuickAccessModules();
        String[] moduleNames = new String[modules.length];
        for (int i = 0; i < modules.length; i++) {
            moduleNames[i] = modules[i][1];
        }

        String selected = (String) JOptionPane.showInputDialog(
            this,
            "Select a module to navigate to:",
            "Module Navigation",
            JOptionPane.QUESTION_MESSAGE,
            null,
            moduleNames,
            moduleNames[0]
        );

        if (selected != null) {
            for (String[] module : modules) {
                if (module[1].equals(selected)) {
                    navigateToModule(module[3]);
                    break;
                }
            }
        }
    }

    // ===== NAVIGATE TO MODULE =====
    private void navigateToModule(String action) {
        System.out.println("🔍 Navigating to: " + action);
        System.out.println("Available cards: " + cardNames);

        if (action.equals("welcome")) {
            cardLayout.show(mainPanel, "welcome");
            setStatus("Dashboard");
            setModule("Dashboard");
            return;
        }

        if (cardNames.contains(action)) {
            cardLayout.show(mainPanel, action);
            setStatus("Module: " + getModuleDisplayName(action));
            setModule(getModuleDisplayName(action));
            System.out.println("✅ Showing card: " + action);
        } else {
            System.out.println("❌ Card not found: " + action);
            
            StringBuilder msg = new StringBuilder();
            msg.append("This module is not available for your role.\n\n");
            msg.append("Available modules:\n");
            for (String name : cardNames) {
                if (!name.equals("welcome")) {
                    msg.append("  • ").append(getModuleDisplayName(name)).append("\n");
                }
            }
            
            JOptionPane.showMessageDialog(this,
                msg.toString(),
                "Access Denied",
                JOptionPane.WARNING_MESSAGE);
        }
    }

    private String getModuleDisplayName(String key) {
        switch (key) {
            case "welcome": return "Dashboard";
            case "waqifDashboard": return "My Impact Dashboard";
            case "beneficiaryDashboard": return "My Workspace";
            case "committeeDashboard": return "Committee Dashboard";
            case "adminDashboard": return "Admin Dashboard";
            case "aiKnowledge": return "AI Knowledge Dashboard";
            case "executive": return "Executive Dashboard";
            case "waqf": return "Cash Waqf";
            case "funding": return "Funding Applications";
            case "assessment": return "AI Assessment";
            case "committee": return "Committee";
            case "loan": return "Loans";
            case "index": return "Namaa Index";
            case "report": return "Reports";
            case "analytics": return "Analytics";
            default: return key;
        }
    }

    // ===== REFRESH ALL =====
    private void refreshAll() {
        JPanel newWelcome = createWelcomePanel(user);
        mainPanel.remove(welcomePanel);
        mainPanel.add(newWelcome, "welcome", 0);
        welcomePanel = newWelcome;
        mainPanel.revalidate();
        mainPanel.repaint();
        setStatus("✅ All panels refreshed!");
    }

    // ===== SET STATUS =====
    private void setStatus(String message) {
        if (statusLabel != null) {
            statusLabel.setText(" " + message);
        }
    }

    // ===== SET MODULE =====
    private void setModule(String moduleName) {
        if (moduleLabel != null) {
            moduleLabel.setText("Module: " + moduleName);
        }
    }

    // ===== SHOW HELP DIALOG =====
    private void showHelpDialog() {
        JTextArea helpText = new JTextArea(
            "NAMA SMART WAQF PLATFORM - HELP\n" +
            "===========================================================\n\n" +
            "NAVIGATION:\n" +
            "-----------------------------------------------------------\n" +
            "• Use the Menu Bar or Toolbar to navigate between modules\n" +
            "• Quick access cards on the Dashboard provide easy navigation\n" +
            "• Use Ctrl+D to return to Dashboard\n\n" +
            "ROLE-BASED ACCESS:\n" +
            "-----------------------------------------------------------\n" +
            "• Waqif: Impact dashboard, Waqf management, Donations\n" +
            "• Beneficiary: Workspace, Applications, Loans, Reports\n" +
            "• Committee: Review applications, Assessments, Analytics\n" +
            "• Admin: Full system management, Executive view\n\n" +
            "DASHBOARDS:\n" +
            "-----------------------------------------------------------\n" +
            "• Impact Dashboard - View your impact metrics\n" +
            "• Workspace - Manage your applications and loans\n" +
            "• Committee Dashboard - Review and approve applications\n" +
            "• Admin Dashboard - System administration\n" +
            "• AI Knowledge - Institutional learning & intelligence\n" +
            "• Executive Dashboard - Strategic view for leadership\n\n" +
            "KEYBOARD SHORTCUTS:\n" +
            "-----------------------------------------------------------\n" +
            "Ctrl+D  - Go to Dashboard\n" +
            "Ctrl+L  - Logout\n" +
            "Ctrl+X  - Exit\n" +
            "Ctrl+R  - Refresh\n" +
            "Ctrl+H  - Help"
        );
        helpText.setFont(new Font("Monospaced", Font.PLAIN, 12));
        helpText.setBackground(new Color(255, 255, 240));
        helpText.setEditable(false);

        JScrollPane scroll = new JScrollPane(helpText);
        scroll.setPreferredSize(new Dimension(600, 450));

        JOptionPane.showMessageDialog(this,
            scroll,
            "Help Topics",
            JOptionPane.INFORMATION_MESSAGE);
    }

    // ===== SHOW USER GUIDE =====
    private void showUserGuide() {
        JTextArea guideText = new JTextArea(
            "USER GUIDE - NAMAA SMART WAQF PLATFORM\n" +
            "===========================================================\n\n" +
            "GETTING STARTED:\n" +
            "-----------------------------------------------------------\n" +
            "1. Login with your credentials\n" +
            "2. Your role determines which modules you can access\n" +
            "3. Use the Dashboard as your starting point\n\n" +
            "WAQIF (DONOR):\n" +
            "-----------------------------------------------------------\n" +
            "• View your impact metrics\n" +
            "• Create and manage Cash Waqfs\n" +
            "• Make donations\n" +
            "• View analytics and reports\n\n" +
            "BENEFICIARY:\n" +
            "-----------------------------------------------------------\n" +
            "• Submit funding applications\n" +
            "• Upload documents\n" +
            "• View loan details and repayment schedule\n" +
            "• Submit progress reports\n" +
            "• Receive AI coaching\n\n" +
            "COMMITTEE MEMBER:\n" +
            "-----------------------------------------------------------\n" +
            "• Review funding applications\n" +
            "• View assessments\n" +
            "• Make approval decisions\n" +
            "• Monitor Namaa Index\n\n" +
            "ADMINISTRATOR:\n" +
            "-----------------------------------------------------------\n" +
            "• Full system management\n" +
            "• User management\n" +
            "• AI configuration\n" +
            "• System backups\n" +
            "• Executive reporting\n\n" +
            "AI FEATURES:\n" +
            "-----------------------------------------------------------\n" +
            "• AI Assessment - Automated project evaluation\n" +
            "• AI Coach - Personalized business advice for beneficiaries\n" +
            "• AI Knowledge - Institutional learning and insights\n" +
            "• AI Recommendations - Smart funding suggestions"
        );
        guideText.setFont(new Font("Monospaced", Font.PLAIN, 12));
        guideText.setBackground(new Color(255, 255, 240));
        guideText.setEditable(false);

        JScrollPane scroll = new JScrollPane(guideText);
        scroll.setPreferredSize(new Dimension(700, 500));

        JOptionPane.showMessageDialog(this,
            scroll,
            "User Guide",
            JOptionPane.INFORMATION_MESSAGE);
    }

    // ===== SHOW ABOUT DIALOG =====
    private void showAboutDialog() {
        JPanel aboutPanel = new JPanel(new BorderLayout(10, 10));
        aboutPanel.setBackground(Color.WHITE);

        JLabel titleLabel = new JLabel("Namaa Smart Waqf Platform", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(new Color(0, 102, 204));
        aboutPanel.add(titleLabel, BorderLayout.CENTER);

        JTextArea details = new JTextArea(
            "Version: 2.0\n\n" +
            "A comprehensive Islamic social finance platform\n" +
            "integrating Waqf, Qard Hasan, and AI-powered\n" +
            "decision support for community development.\n\n" +
            "FEATURES:\n" +
            "• 6 Role-Based Dashboards\n" +
            "• AI-Powered Assessment & Coaching\n" +
            "• Institutional Knowledge Base\n" +
            "• Executive & Board Reporting\n" +
            "• Full System Administration\n\n" +
            "(c) 2026 Namaa Smart Waqf Platform\n" +
            "All Rights Reserved.\n\n" +
            "Built with Java Swing\n" +
            "Data stored in CSV format\n" +
            "AI powered by Gemini/OpenAI"
        );
        details.setFont(new Font("Monospaced", Font.PLAIN, 12));
        details.setBackground(Color.WHITE);
        details.setEditable(false);
        details.setAlignmentX(Component.CENTER_ALIGNMENT);
        aboutPanel.add(details, BorderLayout.SOUTH);

        aboutPanel.setPreferredSize(new Dimension(400, 380));

        JOptionPane.showMessageDialog(this,
            aboutPanel,
            "About Namaa",
            JOptionPane.INFORMATION_MESSAGE);
    }

    // ===== ACTION HANDLING =====
    @Override
    public void actionPerformed(ActionEvent e) {
        String cmd = e.getActionCommand();
        
        if (sessionManager != null) {
            sessionManager.resetActivityTimer();
        }

        // ===== FILE MENU ACTIONS =====
        if (cmd.equals("Logout")) {
            int confirm = JOptionPane.showConfirmDialog(this,
                    "Are you sure you want to logout?",
                    "Confirm Logout", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                dispose();
                new LoginFrame();
            }
            return;
        }

        if (cmd.equals("Exit")) {
            int confirm = JOptionPane.showConfirmDialog(this,
                    "Are you sure you want to exit Namaa?",
                    "Confirm Exit", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                System.exit(0);
            }
            return;
        }

        if (cmd.equals("welcome") || cmd.equals("Dashboard")) {
            cardLayout.show(mainPanel, "welcome");
            setStatus("Welcome back, " + user.getFullName());
            setModule("Dashboard");
            return;
        }

        // ===== VIEW MENU ACTIONS =====
        if (cmd.equals("Refresh")) {
            refreshAll();
            JOptionPane.showMessageDialog(this,
                "✅ All panels have been refreshed!",
                "Refresh Complete",
                JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        if (cmd.equals("toggleStatusBar")) {
            statusBar.setVisible(!statusBar.isVisible());
            return;
        }

        // ===== HELP MENU ACTIONS =====
        if (cmd.equals("help")) {
            showHelpDialog();
            return;
        }

        if (cmd.equals("userGuide")) {
            showUserGuide();
            return;
        }

        if (cmd.equals("about")) {
            showAboutDialog();
            return;
        }

        // ===== AI MENU ACTIONS =====
        if (cmd.equals("aiConfig")) {
            AIConfigPanel config = new AIConfigPanel(this);
            config.setVisible(true);
            refreshAll();
            return;
        }

        if (cmd.equals("aiStatus")) {
            String status = AIService.isAIAvailable() ?
                "✅ AI is ENABLED and ready to use\n\n" +
                "AI Features available:\n" +
                "  • AI Evaluation in Assessment Panel\n" +
                "  • AI Lessons in Project Completion\n" +
                "  • AI Coach for Beneficiaries\n" +
                "  • AI Knowledge Dashboard\n" +
                "  • AI Insights & Recommendations" :
                "⚠️ AI is DISABLED\n\n" +
                "To enable AI:\n" +
                "1. Go to AI → AI Configuration\n" +
                "2. Enter your API key\n" +
                "3. Click 'Test Connection'\n" +
                "4. Click 'Save & Enable'\n\n" +
                "Get API key from:\n" +
                "• Gemini: https://makersuite.google.com/app/apikey\n" +
                "• OpenAI: https://platform.openai.com/api-keys";
            JOptionPane.showMessageDialog(this, status, "AI Status",
                AIService.isAIAvailable() ? JOptionPane.INFORMATION_MESSAGE : JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (sessionManager != null) {
            sessionManager.resetActivityTimer();
        }

        // ===== MODULE NAVIGATION =====
        navigateToModule(cmd);
    }
    private void logout() {
        if (sessionManager != null) {
            sessionManager.endSession();
        }
        
        int confirm = JOptionPane.showConfirmDialog(this,
            "Are you sure you want to logout?",
            "Confirm Logout",
            JOptionPane.YES_NO_OPTION);
            
        if (confirm == JOptionPane.YES_OPTION) {
            dispose();
            new LoginFrame();
        }
    }
    
    private void startSessionManagement() {
        try {
            sessionManager = SessionManager.getInstance();
            sessionManager.startSession(this, () -> {
                // Logout action
                SwingUtilities.invokeLater(() -> {
                    dispose();
                    new LoginFrame();
                    JOptionPane.showMessageDialog(null,
                        "🔒 You have been logged out due to inactivity.",
                        "Session Ended",
                        JOptionPane.INFORMATION_MESSAGE);
                });
            });
            
            // Update status bar with session info
            updateStatusBarWithSession();
            
        } catch (Exception e) {
            System.err.println("⚠️ Session Manager error: " + e.getMessage());
            // Continue without session management if there's an error
        }
    }
    
    private void updateStatusBarWithSession() {
        Timer timer = new Timer(60000, e -> { // Update every minute
            if (sessionManager != null) {
                long inactiveTime = System.currentTimeMillis() - sessionManager.getLastActivityTime();
                long remainingMinutes = (30 * 60 * 1000 - inactiveTime) / 60000;
                if (remainingMinutes > 0 && remainingMinutes <= 30) {
                    if (statusLabel != null) {
                        statusLabel.setText(" ⏳ Session timeout in " + remainingMinutes + " min | " + user.getFullName());
                    }
                }
            }
        });
        timer.start();
    }  
}
