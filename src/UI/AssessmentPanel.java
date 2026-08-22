package UI;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.time.LocalDate;
import Model.*;
import Service.*;

public class AssessmentPanel extends JPanel implements ActionListener {
    private JTextField txtAssessmentID, txtAppID, txtEconomic, txtTechnical, 
                         txtSocial, txtEnvironmental, txtInnovation;
    private JTextArea txtResult;
    private JTable table;
    private DefaultTableModel tableModel;
    private JButton btnEvaluate, btnAIEvaluate, btnClear, btnViewAll, btnLoad;
    private ProjectAssessment currentAssessment;
    private JLabel lblCurrentAssessment, lblStatusMessage;
    private JComboBox<String> cmbApplicationSelector;  // ← NEW: Dropdown for applications

    public AssessmentPanel() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Title
        JLabel lblTitle = new JLabel("NAMAA SMART WAQF PLATFORM (AI Project Assessment)", JLabel.CENTER);
        lblTitle.setFont(new Font("Arial", Font.BOLD, 20));
        lblTitle.setForeground(new Color(0, 102, 204));
        add(lblTitle, BorderLayout.NORTH);

        // Split Panel
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setDividerLocation(480);
        splitPane.setResizeWeight(0.45);

        // LEFT PANEL
        JPanel leftPanel = new JPanel(new BorderLayout(5, 5));

        // Form Panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createEtchedBorder(),
            "Assessment Details",
            TitledBorder.LEFT,
            TitledBorder.TOP,
            new Font("Arial", Font.BOLD, 12)
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(3, 3, 3, 3);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        txtAssessmentID = new JTextField(10);
        txtAppID = new JTextField(10);
        txtEconomic = new JTextField(10);
        txtTechnical = new JTextField(10);
        txtSocial = new JTextField(10);
        txtEnvironmental = new JTextField(10);
        txtInnovation = new JTextField(10);

        // Row 1: Assessment ID (Auto)
        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0.3;
        JLabel lblID = new JLabel("Assessment ID:");
        lblID.setFont(new Font("Arial", Font.BOLD, 12));
        formPanel.add(lblID, gbc);

        gbc.gridx = 1; gbc.weightx = 0.7;
        txtAssessmentID.setEditable(false);
        txtAssessmentID.setBackground(new Color(240, 240, 240));
        txtAssessmentID.setPreferredSize(new Dimension(120, 28));
        txtAssessmentID.setText(String.valueOf(getNextAssessmentId()));
        formPanel.add(txtAssessmentID, gbc);

        // Row 2: Application Selector (NEW: Dropdown)
        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0.3;
        JLabel lblAppSelector = new JLabel("Select Application:");
        lblAppSelector.setFont(new Font("Arial", Font.BOLD, 12));
        formPanel.add(lblAppSelector, gbc);

        gbc.gridx = 1; gbc.weightx = 0.7;
        cmbApplicationSelector = new JComboBox<>();
        cmbApplicationSelector.setFont(new Font("Arial", Font.PLAIN, 12));
        cmbApplicationSelector.setPreferredSize(new Dimension(120, 28));
        cmbApplicationSelector.addActionListener(this);
        cmbApplicationSelector.setActionCommand("selectApplication");
        loadApplications();
        formPanel.add(cmbApplicationSelector, gbc);

        // Row 3: Application ID (read-only, auto-filled)
        gbc.gridx = 0; gbc.gridy = 2; gbc.weightx = 0.3;
        JLabel lblAppID = new JLabel("Application ID:");
        lblAppID.setFont(new Font("Arial", Font.BOLD, 12));
        formPanel.add(lblAppID, gbc);

        gbc.gridx = 1; gbc.weightx = 0.7;
        txtAppID.setEditable(false);
        txtAppID.setBackground(Color.WHITE);
        txtAppID.setPreferredSize(new Dimension(120, 28));
        formPanel.add(txtAppID, gbc);

        // Row 4-8: Scores
        String[] scoreLabels = {"Economic (0-100):", "Technical (0-100):", "Social (0-100):", 
                               "Environmental (0-100):", "Innovation (0-100):"};
        JTextField[] scoreFields = {txtEconomic, txtTechnical, txtSocial, txtEnvironmental, txtInnovation};

        for (int i = 0; i < scoreLabels.length; i++) {
            gbc.gridx = 0; gbc.gridy = i + 3; gbc.weightx = 0.3;
            JLabel lbl = new JLabel(scoreLabels[i]);
            lbl.setFont(new Font("Arial", Font.BOLD, 12));
            formPanel.add(lbl, gbc);
            gbc.gridx = 1; gbc.weightx = 0.7;
            scoreFields[i].setPreferredSize(new Dimension(120, 28));
            formPanel.add(scoreFields[i], gbc);
        }

        leftPanel.add(formPanel, BorderLayout.NORTH);

        // Result Panel
        JPanel resultPanel = new JPanel(new BorderLayout());
        resultPanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createEtchedBorder(),
            "📊 Results",
            TitledBorder.LEFT,
            TitledBorder.TOP,
            new Font("Arial", Font.BOLD, 12)
        ));
        
        txtResult = new JTextArea(10, 25);
        txtResult.setEditable(false);
        txtResult.setFont(new Font("Monospaced", Font.PLAIN, 12));
        txtResult.setBackground(new Color(255, 255, 240));
        txtResult.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        txtResult.setText("Select an application, enter scores, and click 'Evaluate'");
        
        JScrollPane scrollResult = new JScrollPane(txtResult);
        scrollResult.setPreferredSize(new Dimension(300, 200));
        scrollResult.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        resultPanel.add(scrollResult, BorderLayout.CENTER);
        leftPanel.add(resultPanel, BorderLayout.CENTER);

        // Current Assessment Display
        JPanel currentPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        currentPanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createEtchedBorder(),
            "Current Assessment",
            TitledBorder.LEFT,
            TitledBorder.TOP,
            new Font("Arial", Font.BOLD, 11)
        ));
        lblCurrentAssessment = new JLabel("None selected");
        lblCurrentAssessment.setFont(new Font("Arial", Font.BOLD, 12));
        lblCurrentAssessment.setForeground(Color.RED);
        currentPanel.add(lblCurrentAssessment);
        leftPanel.add(currentPanel, BorderLayout.SOUTH);

        // RIGHT PANEL: Table
        JPanel rightPanel = new JPanel(new BorderLayout(5, 5));

        JLabel lblTableTitle = new JLabel("All Assessments", JLabel.CENTER);
        lblTableTitle.setFont(new Font("Arial", Font.BOLD, 14));
        rightPanel.add(lblTableTitle, BorderLayout.NORTH);

        String[] columns = {"ID", "Application", "PRI Score", "Recommendation", "Date"};
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
                    setStatus("Selected Assessment ID: " + id + " - Click 'Load Selected'");
                }
            }
        });

        table.getColumnModel().getColumn(0).setPreferredWidth(50);
        table.getColumnModel().getColumn(1).setPreferredWidth(80);
        table.getColumnModel().getColumn(2).setPreferredWidth(80);
        table.getColumnModel().getColumn(3).setPreferredWidth(120);
        table.getColumnModel().getColumn(4).setPreferredWidth(100);

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createEtchedBorder());
        rightPanel.add(scrollPane, BorderLayout.CENTER);

        // Buttons
        JPanel buttonPanel = new JPanel(new GridLayout(2, 3, 5, 5));
        buttonPanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createEtchedBorder(),
            "Actions",
            TitledBorder.LEFT,
            TitledBorder.TOP,
            new Font("Arial", Font.BOLD, 12)
        ));

        btnEvaluate = new JButton("Evaluate");
        btnAIEvaluate = new JButton("🤖 AI Evaluate");
        btnClear = new JButton("Clear");
        btnViewAll = new JButton("View All");
        btnLoad = new JButton("Load Selected");
        JButton btnRefreshApps = new JButton("🔄 Refresh Apps");

        JButton[] buttons = {btnEvaluate, btnAIEvaluate, btnClear, btnViewAll, btnLoad, btnRefreshApps};
        Color[] colors = {
            new Color(0, 153, 76),   // Evaluate - Green
            new Color(0, 102, 204),  // AI Evaluate - Blue
            new Color(128, 128, 128), // Clear - Gray
            new Color(0, 0, 153),    // View All - Dark Blue
            new Color(204, 0, 0),     // Load - Red
            new Color(255, 153, 0)    // Refresh Apps - Orange
        };

        for (int i = 0; i < buttons.length; i++) {
            buttons[i].setFont(new Font("Arial", Font.BOLD, 11));
            buttons[i].setBackground(colors[i]);
            buttons[i].setForeground(Color.BLACK);
            buttons[i].setFocusPainted(false);
            buttons[i].setPreferredSize(new Dimension(100, 32));
            buttons[i].addActionListener(this);
            buttonPanel.add(buttons[i]);
        }

        rightPanel.add(buttonPanel, BorderLayout.SOUTH);

        splitPane.setLeftComponent(leftPanel);
        splitPane.setRightComponent(rightPanel);
        add(splitPane, BorderLayout.CENTER);

        // Status Bar
        JPanel statusBar = new JPanel(new FlowLayout(FlowLayout.LEFT));
        statusBar.setBorder(BorderFactory.createEtchedBorder());
        lblStatusMessage = new JLabel("Ready - Select an application and enter scores");
        lblStatusMessage.setFont(new Font("Arial", Font.PLAIN, 11));
        statusBar.add(lblStatusMessage);
        add(statusBar, BorderLayout.SOUTH);

        currentAssessment = new ProjectAssessment();
        updateTable();
        updateCurrentDisplay();
    }

    // ===== LOAD APPLICATIONS INTO COMBOBOX =====
    private void loadApplications() {
        cmbApplicationSelector.removeAllItems();
        
        java.util.ArrayList<FundingApplication> apps = FundingService.getApplications();
        
        if (apps.isEmpty()) {
            cmbApplicationSelector.addItem("No applications found");
            setStatus("⚠️ No applications available. Submit an application first.");
            return;
        }
        
        for (FundingApplication app : apps) {
            String displayText = "ID: " + app.getApplicationID() + 
                                " | " + app.getProject().getProjectName() + 
                                " (" + app.getStatus() + ")";
            cmbApplicationSelector.addItem(displayText);
        }
        
        setStatus("Loaded " + apps.size() + " applications");
    }

    private int getNextAssessmentId() {
        int maxId = 0;
        for (ProjectAssessment p : AssessmentService.getAssessments()) {
            if (p.getAssessmentID() > maxId) {
                maxId = p.getAssessmentID();
            }
        }
        return maxId + 1;
    }

    private void updateCurrentDisplay() {
        if (currentAssessment != null && currentAssessment.getAssessmentID() > 0) {
            lblCurrentAssessment.setText("ID: " + currentAssessment.getAssessmentID() + 
                                        " | PRI: " + String.format("%.2f", currentAssessment.getPriScore()) +
                                        " | " + currentAssessment.getRecommendation());
            lblCurrentAssessment.setForeground(new Color(0, 102, 204));
        } else {
            lblCurrentAssessment.setText("None selected");
            lblCurrentAssessment.setForeground(Color.RED);
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        try {
            if (e.getSource() == cmbApplicationSelector) {
                // Auto-fill application ID when selected from dropdown
                String selected = (String) cmbApplicationSelector.getSelectedItem();
                if (selected != null && !selected.equals("No applications found")) {
                    try {
                        int start = selected.indexOf("ID: ") + 4;
                        int end = selected.indexOf(" |");
                        if (start > 0 && end > start) {
                            int appId = Integer.parseInt(selected.substring(start, end));
                            txtAppID.setText(String.valueOf(appId));
                            setStatus("Selected Application ID: " + appId);
                        }
                    } catch (Exception ex) {
                        // Ignore
                    }
                }
                return;
            }
            
            if (e.getSource() == btnEvaluate) {
                evaluateProject();
            } else if (e.getSource() == btnAIEvaluate) {
                generateAIEvaluation();
            } else if (e.getSource() == btnClear) {
                clearForm();
            } else if (e.getSource() == btnViewAll) {
                updateTable();
                setStatus("Displaying all assessments");
            } else if (e.getSource() == btnLoad) {
                loadSelectedAssessment();
            } else if (e.getActionCommand() != null && e.getActionCommand().equals("selectApplication")) {
                // Already handled above
            } else if (e.getSource() instanceof JButton && ((JButton)e.getSource()).getText().contains("Refresh Apps")) {
                loadApplications();
                setStatus("Applications refreshed");
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                "Error: " + ex.getMessage(),
                "Operation Failed",
                JOptionPane.ERROR_MESSAGE);
            setStatus("Error: " + ex.getMessage());
        }
    }

    private void evaluateProject() {
        String appIdText = txtAppID.getText().trim();
        if (appIdText.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please select or enter an Application ID.");
            return;
        }

        try {
            int appId = Integer.parseInt(appIdText);
            FundingApplication app = FundingService.searchApplication(appId);
            if (app == null) {
                JOptionPane.showMessageDialog(this, "Application ID " + appId + " not found!");
                return;
            }

            double economic = Double.parseDouble(txtEconomic.getText().trim());
            double technical = Double.parseDouble(txtTechnical.getText().trim());
            double social = Double.parseDouble(txtSocial.getText().trim());
            double environmental = Double.parseDouble(txtEnvironmental.getText().trim());
            double innovation = Double.parseDouble(txtInnovation.getText().trim());

            if (economic < 0 || economic > 100 || technical < 0 || technical > 100 ||
                social < 0 || social > 100 || environmental < 0 || environmental > 100 ||
                innovation < 0 || innovation > 100) {
                JOptionPane.showMessageDialog(this, "All scores must be between 0 and 100.");
                return;
            }

            int id = getNextAssessmentId();
            currentAssessment = new ProjectAssessment(
                id, app, economic, technical, social, environmental, innovation, LocalDate.now()
            );

            AssessmentService.addAssessment(currentAssessment);
            txtAssessmentID.setText(String.valueOf(getNextAssessmentId()));
            updateTable();
            updateCurrentDisplay();

            String recommendation = currentAssessment.getRecommendation();
            String emoji = recommendation.equals("Approved") ? "✅" : 
                          recommendation.equals("Needs Revision") ? "⚠️" : "❌";
            
            txtResult.setText(
                "╔════════════════════════════════════════════\n" +
                "║           ASSESSMENT RESULTS               \n" +
                "╠════════════════════════════════════════════\n" +
                "║  Economic:    " + String.format("%7.2f", economic) + "\n" +
                "║  Technical:   " + String.format("%7.2f", technical) + "\n" +
                "║  Social:      " + String.format("%7.2f", social) + "\n" +
                "║  Environmental:" + String.format("%7.2f", environmental) + "\n" +
                "║  Innovation:  " + String.format("%7.2f", innovation) + "\n" +
                "╠════════════════════════════════════════════\n" +
                "║  ★ PRI SCORE:  " + String.format("%7.2f", currentAssessment.getPriScore()) + "\n" +
                "║  ★ Recommendation: " + String.format("%-16s", emoji + " " + recommendation) + "\n" +
                "╚════════════════════════════════════════════\n\n" +
                "💡 Click '🤖 AI Evaluate' for detailed AI analysis."
            );

            setStatus("Assessment ID " + id + " completed with PRI: " + 
                     String.format("%.2f", currentAssessment.getPriScore()));

            JOptionPane.showMessageDialog(this,
                "✅ Assessment completed!\n" +
                "PRI Score: " + String.format("%.2f", currentAssessment.getPriScore()) + "\n" +
                "Recommendation: " + recommendation,
                "Success",
                JOptionPane.INFORMATION_MESSAGE);

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Please enter valid numbers for all scores.");
        }
    }

    private void generateAIEvaluation() {
        String appIdText = txtAppID.getText().trim();
        if (appIdText.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter Application ID first.");
            return;
        }

        try {
            int appId = Integer.parseInt(appIdText);
            FundingApplication app = FundingService.searchApplication(appId);
            if (app == null) {
                JOptionPane.showMessageDialog(this, "Application ID " + appId + " not found!");
                return;
            }

            ProjectAssessment assessment = AssessmentService.searchAssessmentByApplication(appId);
            if (assessment == null) {
                JOptionPane.showMessageDialog(this, "Please evaluate the project first (click 'Evaluate').");
                return;
            }

            txtResult.setText("🤖 AI is analyzing the project...\nPlease wait...");

            SwingWorker<String, Void> worker = new SwingWorker<>() {
                @Override
                protected String doInBackground() {
                    return AIService.generateProjectEvaluation(
                        app.getProject().getProjectName(),
                        app.getProject().getSector(),
                        assessment.getPriScore(),
                        assessment.getEconomicScore(),
                        assessment.getTechnicalScore(),
                        assessment.getSocialScore(),
                        assessment.getEnvironmentalScore(),
                        assessment.getInnovationScore()
                    );
                }

                @Override
                protected void done() {
                    try {
                        String result = get();
                        txtResult.setText(result);
                        setStatus("AI Evaluation completed");
                    } catch (Exception ex) {
                        txtResult.setText("⚠️ AI evaluation failed: " + ex.getMessage() + 
                                         "\n\n💡 To enable AI:\n1. Get API key from OpenAI\n2. Go to AI → AI Configuration");
                        setStatus("AI Error: " + ex.getMessage());
                    }
                }
            };
            worker.execute();

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
        }
    }

    private void loadSelectedAssessment() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Please select an assessment from the table.");
            return;
        }

        int modelRow = table.convertRowIndexToModel(selectedRow);
        int id = (int) tableModel.getValueAt(modelRow, 0);
        
        ProjectAssessment loaded = AssessmentService.searchAssessment(id);
        if (loaded != null) {
            currentAssessment = loaded;
            txtAssessmentID.setText(String.valueOf(currentAssessment.getAssessmentID()));
            txtAppID.setText(String.valueOf(currentAssessment.getApplication().getApplicationID()));
            txtEconomic.setText(String.valueOf(currentAssessment.getEconomicScore()));
            txtTechnical.setText(String.valueOf(currentAssessment.getTechnicalScore()));
            txtSocial.setText(String.valueOf(currentAssessment.getSocialScore()));
            txtEnvironmental.setText(String.valueOf(currentAssessment.getEnvironmentalScore()));
            txtInnovation.setText(String.valueOf(currentAssessment.getInnovationScore()));
            updateCurrentDisplay();
            
            txtResult.setText(
                "╔════════════════════════════════════════════╗\n" +
                "║        LOADED ASSESSMENT                   ║\n" +
                "╠════════════════════════════════════════════╣\n" +
                "║  Assessment ID:  " + String.format("%-17d", currentAssessment.getAssessmentID()) + "║\n" +
                "║  Application ID: " + String.format("%-17d", currentAssessment.getApplication().getApplicationID()) + "║\n" +
                "║  PRI Score:      " + String.format("%7.2f", currentAssessment.getPriScore()) + "      ║\n" +
                "║  Recommendation: " + String.format("%-16s", currentAssessment.getRecommendation()) + "║\n" +
                "║  Date:           " + currentAssessment.getAssessmentDate() + "      ║\n" +
                "╚════════════════════════════════════════════╝"
            );
            
            setStatus("Loaded Assessment ID: " + id);
            JOptionPane.showMessageDialog(this, "📋 Assessment loaded successfully!");
        }
    }

    private void clearForm() {
        txtAssessmentID.setText(String.valueOf(getNextAssessmentId()));
        txtAppID.setText("");
        txtEconomic.setText("");
        txtTechnical.setText("");
        txtSocial.setText("");
        txtEnvironmental.setText("");
        txtInnovation.setText("");
        txtResult.setText("Select an application, enter scores, and click 'Evaluate'");
        currentAssessment = new ProjectAssessment();
        updateCurrentDisplay();
        setStatus("Form cleared. Ready for new assessment.");
        if (cmbApplicationSelector.getItemCount() > 0) {
            cmbApplicationSelector.setSelectedIndex(0);
        }
    }

    private void updateTable() {
        tableModel.setRowCount(0);
        for (ProjectAssessment p : AssessmentService.getAssessments()) {
            Object[] row = {
                p.getAssessmentID(),
                p.getApplication().getApplicationID(),
                String.format("%.2f", p.getPriScore()),
                p.getRecommendation(),
                p.getAssessmentDate()
            };
            tableModel.addRow(row);
        }
        setStatus("Displaying " + AssessmentService.getAssessments().size() + " assessments");
    }

    private void setStatus(String message) {
        if (lblStatusMessage != null) {
            lblStatusMessage.setText("Status: " + message);
        }
    }
}