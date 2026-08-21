package UI;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.*;
import Model.User;
import Service.UserService;
import Service.AIService;

public class LoginFrame extends JFrame implements ActionListener {
    private JTextField txtUsername;
    private JPasswordField txtPassword;
    private JButton btnLogin, btnExit, btnGuest;
    private JCheckBox chkShowPassword, chkRememberMe;
    private JLabel lblStatus, lblVersion, lblAIChat;
    private JPanel loginPanel, mainPanel;
    private CardLayout cardLayout;
    private int loginAttempts = 0;
    private static final int MAX_ATTEMPTS = 5;
    private Timer unlockTimer;

    public LoginFrame() {
        setTitle("Namaa Smart Waqf Platform - Login");
        setSize(900, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setUndecorated(true);
        setShape(new java.awt.geom.RoundRectangle2D.Double(0, 0, 900, 600, 30, 30));

        mainPanel = new JPanel(new CardLayout());
        mainPanel.setBackground(new Color(245, 248, 250));

        loginPanel = createLoginPanel();
        mainPanel.add(loginPanel, "login");

        JPanel loadingPanel = createLoadingPanel();
        mainPanel.add(loadingPanel, "loading");

        add(mainPanel);

        setupKeyBindings();

        setVisible(true);
    }

    private JPanel createLoginPanel() {
        JPanel panel = new JPanel(new BorderLayout(0, 0));
        panel.setBackground(new Color(245, 248, 250));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // LEFT: Branding Section
        JPanel leftPanel = new JPanel(new BorderLayout(15, 15));
        leftPanel.setBackground(new Color(0, 51, 102));
        leftPanel.setBorder(BorderFactory.createEmptyBorder(40, 40, 40, 40));
        leftPanel.setPreferredSize(new Dimension(350, 560));

        JPanel brandPanel = new JPanel(new GridBagLayout());
        brandPanel.setBackground(new Color(0, 51, 102));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel logoLabel = new JLabel("🏛️");
        logoLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 60));
        logoLabel.setForeground(Color.WHITE);
        logoLabel.setHorizontalAlignment(SwingConstants.CENTER);
        gbc.gridx = 0; gbc.gridy = 0;
        gbc.gridwidth = 2;
        brandPanel.add(logoLabel, gbc);

        JLabel titleLabel = new JLabel("NAMA A SMART WAQF");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 28));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        gbc.gridy = 1;
        brandPanel.add(titleLabel, gbc);

        JLabel subtitleLabel = new JLabel("Islamic Social Finance Platform");
        subtitleLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        subtitleLabel.setForeground(new Color(200, 200, 200));
        subtitleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        gbc.gridy = 2;
        brandPanel.add(subtitleLabel, gbc);

        String[][] features = {
            {"💰", "Smart Cash Waqf Management"},
            {"🤖", "AI-Powered Assessment & Coaching"},
            {"📊", "Real-Time Impact Analytics"},
            {"🧠", "Institutional Knowledge Base"},
            {"🏛️", "Executive & Board Reporting"}
        };

        JPanel featuresPanel = new JPanel(new GridLayout(features.length, 1, 5, 8));
        featuresPanel.setBackground(new Color(0, 51, 102));

        for (String[] feature : features) {
            JPanel featureItem = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
            featureItem.setBackground(new Color(0, 51, 102));
            
            JLabel iconLabel = new JLabel(feature[0]);
            iconLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 16));
            iconLabel.setForeground(Color.WHITE);
            featureItem.add(iconLabel);
            
            JLabel textLabel = new JLabel(feature[1]);
            textLabel.setFont(new Font("Arial", Font.PLAIN, 13));
            textLabel.setForeground(Color.WHITE);
            featureItem.add(textLabel);
            
            featuresPanel.add(featureItem);
        }

        gbc.gridy = 4;
        gbc.insets = new Insets(15, 5, 5, 5);
        brandPanel.add(featuresPanel, gbc);

        leftPanel.add(brandPanel, BorderLayout.CENTER);

        // RIGHT: Login Form
        JPanel rightPanel = new JPanel(new GridBagLayout());
        rightPanel.setBackground(Color.WHITE);
        rightPanel.setBorder(BorderFactory.createEmptyBorder(40, 40, 40, 40));

        GridBagConstraints rGbc = new GridBagConstraints();
        rGbc.insets = new Insets(8, 8, 8, 8);
        rGbc.fill = GridBagConstraints.HORIZONTAL;

        rGbc.gridx = 0; rGbc.gridy = 0; rGbc.gridwidth = 2;
        JLabel welcomeLabel = new JLabel("Welcome Back");
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 24));
        welcomeLabel.setForeground(new Color(0, 51, 102));
        rightPanel.add(welcomeLabel, rGbc);

        rGbc.gridy = 1;
        JLabel descLabel = new JLabel("Sign in to access your dashboard");
        descLabel.setFont(new Font("Arial", Font.PLAIN, 13));
        descLabel.setForeground(Color.GRAY);
        rightPanel.add(descLabel, rGbc);

        rGbc.gridy = 2;
        rightPanel.add(Box.createVerticalStrut(20), rGbc);

        rGbc.gridy = 3; rGbc.gridwidth = 1;
        rGbc.gridx = 0;
        JLabel lblUsername = new JLabel("👤 Username");
        lblUsername.setFont(new Font("Arial", Font.BOLD, 12));
        lblUsername.setForeground(new Color(0, 51, 102));
        rightPanel.add(lblUsername, rGbc);

        rGbc.gridx = 1;
        txtUsername = new JTextField(20);
        txtUsername.setFont(new Font("Arial", Font.PLAIN, 14));
        txtUsername.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
            BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));
        txtUsername.addActionListener(this);
        rightPanel.add(txtUsername, rGbc);

        rGbc.gridy = 4; rGbc.gridx = 0;
        JLabel lblPassword = new JLabel("🔒 Password");
        lblPassword.setFont(new Font("Arial", Font.BOLD, 12));
        lblPassword.setForeground(new Color(0, 51, 102));
        rightPanel.add(lblPassword, rGbc);

        rGbc.gridx = 1;
        txtPassword = new JPasswordField(20);
        txtPassword.setFont(new Font("Arial", Font.PLAIN, 14));
        txtPassword.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
            BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));
        txtPassword.addActionListener(this);
        rightPanel.add(txtPassword, rGbc);

        rGbc.gridy = 5; rGbc.gridx = 0; rGbc.gridwidth = 1;
        chkShowPassword = new JCheckBox("Show Password");
        chkShowPassword.setFont(new Font("Arial", Font.PLAIN, 11));
        chkShowPassword.setBackground(Color.WHITE);
        chkShowPassword.addActionListener(this);
        chkShowPassword.setActionCommand("showPassword");
        rightPanel.add(chkShowPassword, rGbc);

        rGbc.gridx = 1;
        chkRememberMe = new JCheckBox("Remember Me");
        chkRememberMe.setFont(new Font("Arial", Font.PLAIN, 11));
        chkRememberMe.setBackground(Color.WHITE);
        rightPanel.add(chkRememberMe, rGbc);

        rGbc.gridy = 6; rGbc.gridx = 0; rGbc.gridwidth = 2;
        rGbc.insets = new Insets(15, 8, 8, 8);
        btnLogin = new JButton("🔑 Sign In");
        btnLogin.setFont(new Font("Arial", Font.BOLD, 16));
        btnLogin.setBackground(new Color(0, 102, 204));
        btnLogin.setForeground(Color.BLUE);
        btnLogin.setFocusPainted(false);
        btnLogin.setPreferredSize(new Dimension(200, 45));
        btnLogin.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnLogin.addActionListener(this);
        btnLogin.setActionCommand("login");
        rightPanel.add(btnLogin, rGbc);

        rGbc.gridy = 7;
        rGbc.insets = new Insets(5, 8, 8, 8);
        lblStatus = new JLabel(" ");
        lblStatus.setFont(new Font("Arial", Font.PLAIN, 12));
        lblStatus.setForeground(Color.RED);
        lblStatus.setHorizontalAlignment(SwingConstants.CENTER);
        rightPanel.add(lblStatus, rGbc);

        rGbc.gridy = 8;
        JPanel guestPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        guestPanel.setBackground(Color.WHITE);
        
        btnGuest = new JButton("👤 Continue as Guest");
        btnGuest.setFont(new Font("Arial", Font.PLAIN, 12));
        btnGuest.setBackground(new Color(240, 240, 240));
        btnGuest.setForeground(new Color(0, 51, 102));
        btnGuest.setFocusPainted(false);
        btnGuest.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnGuest.addActionListener(this);
        btnGuest.setActionCommand("guest");
        guestPanel.add(btnGuest);

        btnExit = new JButton("✕");
        btnExit.setFont(new Font("Arial", Font.BOLD, 14));
        btnExit.setBackground(new Color(200, 50, 50));
        btnExit.setForeground(Color.WHITE);
        btnExit.setFocusPainted(false);
        btnExit.setPreferredSize(new Dimension(35, 35));
        btnExit.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnExit.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
        btnExit.addActionListener(this);
        btnExit.setActionCommand("exit");
        
        JPanel exitPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        exitPanel.setBackground(Color.WHITE);
        exitPanel.add(btnExit);
        
        rightPanel.add(guestPanel, rGbc);

        rGbc.gridy = 9;
        rGbc.insets = new Insets(10, 8, 5, 8);
        lblAIChat = new JLabel("🤖 AI Service: " + (AIService.isAIAvailable() ? "✅ Active" : "⚠️ Disabled"));
        lblAIChat.setFont(new Font("Arial", Font.PLAIN, 10));
        lblAIChat.setForeground(AIService.isAIAvailable() ? new Color(0, 153, 76) : Color.RED);
        lblAIChat.setHorizontalAlignment(SwingConstants.CENTER);
        rightPanel.add(lblAIChat, rGbc);

        panel.add(leftPanel, BorderLayout.WEST);
        panel.add(rightPanel, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createLoadingPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(Color.WHITE);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);

        JLabel loadingLabel = new JLabel("🔄 Loading Dashboard...");
        loadingLabel.setFont(new Font("Arial", Font.BOLD, 18));
        loadingLabel.setForeground(new Color(0, 102, 204));
        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(loadingLabel, gbc);

        JProgressBar progressBar = new JProgressBar();
        progressBar.setIndeterminate(true);
        progressBar.setPreferredSize(new Dimension(300, 20));
        gbc.gridy = 1;
        panel.add(progressBar, gbc);

        JLabel welcomeLabel = new JLabel("Welcome to Namaa Smart Waqf Platform");
        welcomeLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        welcomeLabel.setForeground(Color.GRAY);
        gbc.gridy = 2;
        panel.add(welcomeLabel, gbc);

        return panel;
    }

    private void setupKeyBindings() {
        getRootPane().setDefaultButton(btnLogin);

        KeyStroke escapeKey = KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0);
        Action escapeAction = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        };
        getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(escapeKey, "escape");
        getRootPane().getActionMap().put("escape", escapeAction);

        MouseAdapter dragAdapter = new MouseAdapter() {
            private Point initialClick;
            @Override
            public void mousePressed(MouseEvent e) {
                initialClick = e.getPoint();
            }
            @Override
            public void mouseDragged(MouseEvent e) {
                if (initialClick != null) {
                    Point current = e.getLocationOnScreen();
                    setLocation(current.x - initialClick.x, current.y - initialClick.y);
                }
            }
        };
        loginPanel.addMouseListener(dragAdapter);
        loginPanel.addMouseMotionListener(dragAdapter);
    }

    private void performLogin() {
        String username = txtUsername.getText().trim();
        String password = new String(txtPassword.getPassword());

        if (username.isEmpty() || password.isEmpty()) {
            lblStatus.setText("⚠️ Please enter both username and password");
            animateStatus(lblStatus, Color.RED);
            return;
        }

        if (loginAttempts >= MAX_ATTEMPTS) {
            lblStatus.setText("🔒 Too many failed attempts. Please wait 30 seconds.");
            btnLogin.setEnabled(false);
            btnLogin.setBackground(Color.GRAY);
            
            if (unlockTimer != null) {
                unlockTimer.stop();
            }
            unlockTimer = new Timer(30000, new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    loginAttempts = 0;
                    btnLogin.setEnabled(true);
                    btnLogin.setBackground(new Color(0, 102, 204));
                    lblStatus.setText("⏳ Account unlocked. Try again.");
                    unlockTimer.stop();
                }
            });
            unlockTimer.start();
            return;
        }

        showLoading();

        SwingWorker<User, Void> worker = new SwingWorker<>() {
            @Override
            protected User doInBackground() {
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
                return UserService.login(username, password);
            }

            @Override
            protected void done() {
                try {
                    User user = get();
                    if (user != null) {
                        loginSuccess(user);
                    } else {
                        loginFailed();
                    }
                } catch (Exception e) {
                    loginFailed();
                    lblStatus.setText("⚠️ Error: " + e.getMessage());
                }
            }
        };
        worker.execute();
    }

    private void loginSuccess(User user) {
        dispose();
        SwingUtilities.invokeLater(() -> {
            new DashboardFrame(user);
        });
    }

    // ===== FIXED: Login Failed - Returns to Login Screen =====
    private void loginFailed() {
        loginAttempts++;
        
        // Show error message on status label
        String message = "❌ Invalid username or password (Attempt " + loginAttempts + "/" + MAX_ATTEMPTS + ")";
        lblStatus.setText(message);
        animateStatus(lblStatus, Color.RED);
        
        // Clear password field
        txtPassword.setText("");
        txtPassword.requestFocus();

        // ===== FIXED: Return to login panel =====
        showLoginPanel();

        // Show popup for invalid credentials
        if (loginAttempts < MAX_ATTEMPTS) {
            JOptionPane.showMessageDialog(this,
                "❌ Invalid username or password!\n\n" +
                "Please try again.\n" +
                "Attempt " + loginAttempts + " of " + MAX_ATTEMPTS,
                "Login Failed",
                JOptionPane.ERROR_MESSAGE);
        }

        if (loginAttempts >= MAX_ATTEMPTS) {
            lblStatus.setText("🔒 Too many failed attempts. Please wait 30 seconds.");
            btnLogin.setEnabled(false);
            btnLogin.setBackground(Color.GRAY);
            
            if (unlockTimer != null) {
                unlockTimer.stop();
            }
            unlockTimer = new Timer(30000, new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    loginAttempts = 0;
                    btnLogin.setEnabled(true);
                    btnLogin.setBackground(new Color(0, 102, 204));
                    lblStatus.setText("⏳ Account unlocked. Try again.");
                    unlockTimer.stop();
                }
            });
            unlockTimer.start();
        }
    }

    private void showLoading() {
        CardLayout cl = (CardLayout) mainPanel.getLayout();
        cl.show(mainPanel, "loading");
    }

    // ===== FIXED: Show Login Panel =====
    private void showLoginPanel() {
        CardLayout cl = (CardLayout) mainPanel.getLayout();
        cl.show(mainPanel, "login");
    }

    private void animateStatus(JLabel label, Color color) {
        label.setForeground(color);
        Timer timer = new Timer(100, new ActionListener() {
            private int count = 0;
            @Override
            public void actionPerformed(ActionEvent e) {
                count++;
                if (count % 2 == 0) {
                    label.setForeground(color);
                } else {
                    label.setForeground(new Color(200, 200, 200));
                }
                if (count >= 6) {
                    ((Timer) e.getSource()).stop();
                    label.setForeground(color);
                }
            }
        });
        timer.start();
    }
    
    private void guestLogin() {
        // Create a guest with limited permissions
        User guest = UserService.searchUser("guest");
        if (guest == null) {
            guest = new User(999, "Guest User", "guest@namaa.com", 
                    "guest", "guest", "00000000");
            // Add as a beneficiary with limited access
            UserService.addUser(guest);
        }
        // Inform user about limited access
        JOptionPane.showMessageDialog(this,
            "👤 You are logged in as Guest.\n\n" +
            "Guest users have limited access to:\n" +
            "• View public data\n" +
            "• Browse knowledge base\n\n" +
            "To access all features, please login with your credentials.",
            "Guest Login",
            JOptionPane.INFORMATION_MESSAGE);
        loginSuccess(guest);
    }
    

    @Override
    public void actionPerformed(ActionEvent e) {
        String cmd = e.getActionCommand();

        if (cmd.equals("login") || e.getSource() == txtUsername || e.getSource() == txtPassword) {
            performLogin();
        } else if (cmd.equals("showPassword")) {
            if (chkShowPassword.isSelected()) {
                txtPassword.setEchoChar((char) 0);
            } else {
                txtPassword.setEchoChar('•');
            }
        } else if (cmd.equals("guest")) {
            guestLogin();
        } else if (cmd.equals("exit")) {
            int confirm = JOptionPane.showConfirmDialog(this,
                    "Are you sure you want to exit Namaa?",
                    "Confirm Exit", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                System.exit(0);
            }
        }
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        SwingUtilities.invokeLater(() -> {
            new LoginFrame();
        });
    }
}