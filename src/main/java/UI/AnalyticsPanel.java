package UI;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import Model.HistoricalProject;
import Service.HistoricalDataService;

public class AnalyticsPanel extends JPanel implements ActionListener {
    private JComboBox<String> cmbCategoryFilter;  // For table filtering
    private JComboBox<String> cmbPredictionCategory;  // For prediction
    private JTable table;
    private DefaultTableModel tableModel;
    private JTextArea txtInsights;
    private JButton btnRefresh, btnPredict, btnViewDetails;
    private JTextField txtBudget, txtProjectName;
    private JLabel lblPrediction;
    private JPanel predictionPanel;

    public AnalyticsPanel() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // ===== TOP: Title =====
        JLabel lblTitle = new JLabel("📊 Project Analytics & Historical Data", JLabel.CENTER);
        lblTitle.setFont(new Font("Arial", Font.BOLD, 20));
        lblTitle.setForeground(new Color(0, 102, 204));
        add(lblTitle, BorderLayout.NORTH);

        // ===== CENTER: Split Panel =====
        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        splitPane.setDividerLocation(280);
        splitPane.setResizeWeight(0.55);

        // ---- Table Panel (Top) ----
        JPanel tablePanel = new JPanel(new BorderLayout(5, 5));
        tablePanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createEtchedBorder(),
            "📋 Historical Projects",
            TitledBorder.LEFT,
            TitledBorder.TOP,
            new Font("Arial", Font.BOLD, 12)
        ));

        // ===== FIXED: Filter Panel for Table =====
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        filterPanel.setBackground(new Color(245, 248, 250));
        
        JLabel lblFilter = new JLabel("Filter by Category:");
        lblFilter.setFont(new Font("Arial", Font.BOLD, 12));
        filterPanel.add(lblFilter);
        
        cmbCategoryFilter = new JComboBox<>();
        cmbCategoryFilter.setFont(new Font("Arial", Font.PLAIN, 12));
        cmbCategoryFilter.setPreferredSize(new Dimension(150, 28));
        cmbCategoryFilter.setBackground(Color.WHITE);
        
        // Add default categories for filter
        cmbCategoryFilter.addItem("All");
        cmbCategoryFilter.addItem("Agriculture");
        cmbCategoryFilter.addItem("Education");
        cmbCategoryFilter.addItem("Healthcare");
        cmbCategoryFilter.addItem("Technology");
        cmbCategoryFilter.addItem("Small Business");
        cmbCategoryFilter.addItem("Infrastructure");
        
        cmbCategoryFilter.addActionListener(this);
        filterPanel.add(cmbCategoryFilter);

        btnRefresh = new JButton("🔄 Refresh Data");
        btnRefresh.setFont(new Font("Arial", Font.BOLD, 12));
        btnRefresh.setBackground(new Color(0, 102, 204));
        btnRefresh.setForeground(Color.BLACK);
        btnRefresh.setFocusPainted(false);
        btnRefresh.addActionListener(this);
        filterPanel.add(btnRefresh);

        tablePanel.add(filterPanel, BorderLayout.NORTH);

        // Table
        String[] columns = {"ID", "Project", "Category", "Cost (QR)", "Duration", "Beneficiaries", "Success Rate", "Namaa Index"};
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

        // Color-code success rate
        table.setDefaultRenderer(Object.class, new javax.swing.table.DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                
                if (column == 6) {
                    try {
                        String strVal = value.toString().replace("%", "");
                        double rate = Double.parseDouble(strVal);
                        if (rate >= 80) {
                            c.setBackground(new Color(0, 200, 0));
                            c.setForeground(Color.WHITE);
                        } else if (rate >= 60) {
                            c.setBackground(new Color(255, 200, 0));
                            c.setForeground(Color.BLACK);
                        } else {
                            c.setBackground(new Color(200, 0, 0));
                            c.setForeground(Color.WHITE);
                        }
                    } catch (Exception e) {
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

        JScrollPane scrollPane = new JScrollPane(table);
        tablePanel.add(scrollPane, BorderLayout.CENTER);

        // ---- Bottom Panel: Insights & Predictions ----
        JPanel bottomPanel = new JPanel(new GridLayout(2, 1, 5, 5));

        // Insights Panel
        JPanel insightsPanel = new JPanel(new BorderLayout(5, 5));
        insightsPanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createEtchedBorder(),
            "💡 Insights & Best Practices",
            TitledBorder.LEFT,
            TitledBorder.TOP,
            new Font("Arial", Font.BOLD, 12)
        ));

        txtInsights = new JTextArea(5, 30);
        txtInsights.setEditable(false);
        txtInsights.setFont(new Font("Monospaced", Font.PLAIN, 12));
        txtInsights.setBackground(new Color(255, 255, 240));
        txtInsights.setText("Select a category from the filter above to view insights...");
        insightsPanel.add(new JScrollPane(txtInsights), BorderLayout.CENTER);

        // ===== FIXED: Prediction Panel with its OWN Category dropdown =====
        predictionPanel = new JPanel(new BorderLayout(8, 10));
        predictionPanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createEtchedBorder(),
            "🔮 Project Success Prediction",
            TitledBorder.LEFT,
            TitledBorder.TOP,
            new Font("Arial", Font.BOLD, 12)
        ));

        // ---- Input Panel ----
        JPanel inputPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 5));
        inputPanel.setBackground(Color.WHITE);
        
        // ===== FIXED: Category dropdown for prediction =====
        JLabel lblCategory = new JLabel("Category:*");
        lblCategory.setFont(new Font("Arial", Font.BOLD, 12));
        inputPanel.add(lblCategory);
        
        cmbPredictionCategory = new JComboBox<>();
        cmbPredictionCategory.setFont(new Font("Arial", Font.PLAIN, 12));
        cmbPredictionCategory.setPreferredSize(new Dimension(120, 28));
        cmbPredictionCategory.setBackground(Color.WHITE);
        
        // Add categories for prediction
        cmbPredictionCategory.addItem("Agriculture");
        cmbPredictionCategory.addItem("Education");
        cmbPredictionCategory.addItem("Healthcare");
        cmbPredictionCategory.addItem("Technology");
        cmbPredictionCategory.addItem("Small Business");
        cmbPredictionCategory.addItem("Infrastructure");
        
        inputPanel.add(cmbPredictionCategory);
        
        inputPanel.add(new JLabel("Project Name:"));
        txtProjectName = new JTextField(12);
        txtProjectName.setPreferredSize(new Dimension(120, 28));
        inputPanel.add(txtProjectName);
        
        inputPanel.add(new JLabel("Budget (QR):"));
        txtBudget = new JTextField(10);
        txtBudget.setPreferredSize(new Dimension(100, 28));
        inputPanel.add(txtBudget);
        
        btnPredict = new JButton("🔮 Predict Success");
        btnPredict.setFont(new Font("Arial", Font.BOLD, 12));
        btnPredict.setBackground(new Color(153, 0, 153));
        btnPredict.setForeground(Color.BLACK);
        btnPredict.setFocusPainted(false);
        btnPredict.setPreferredSize(new Dimension(140, 30));
        btnPredict.addActionListener(this);
        inputPanel.add(btnPredict);
        
        btnViewDetails = new JButton("View Details");
        btnViewDetails.setFont(new Font("Arial", Font.BOLD, 12));
        btnViewDetails.setBackground(new Color(0, 102, 204));
        btnViewDetails.setForeground(Color.BLACK);
        btnViewDetails.setFocusPainted(false);
        btnViewDetails.setPreferredSize(new Dimension(120, 30));
        btnViewDetails.addActionListener(this);
        inputPanel.add(btnViewDetails);

        predictionPanel.add(inputPanel, BorderLayout.NORTH);

        // Prediction preview area
        lblPrediction = new JLabel("Select a category, enter project details, and click 'Predict Success'");
        lblPrediction.setFont(new Font("Arial", Font.ITALIC, 12));
        lblPrediction.setForeground(Color.GRAY);
        lblPrediction.setHorizontalAlignment(SwingConstants.CENTER);
        lblPrediction.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        predictionPanel.add(lblPrediction, BorderLayout.CENTER);

        bottomPanel.add(insightsPanel);
        bottomPanel.add(predictionPanel);

        splitPane.setTopComponent(tablePanel);
        splitPane.setBottomComponent(bottomPanel);

        add(splitPane, BorderLayout.CENTER);

        // Load initial data
        updateDisplay();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == cmbCategoryFilter || e.getSource() == btnRefresh) {
            updateDisplay();
        } else if (e.getSource() == btnPredict) {
            predictSuccess();
        } else if (e.getSource() == btnViewDetails) {
            showProjectDetails();
        }
    }

    private void updateDisplay() {
        String category = (String) cmbCategoryFilter.getSelectedItem();
        if (category == null) category = "All";
        
        // Update table
        tableModel.setRowCount(0);
        var projects = HistoricalDataService.getAllProjects();
        
        for (HistoricalProject p : projects) {
            if (category.equals("All") || p.getCategory().equals(category)) {
                Object[] row = {
                    p.getProjectId(),
                    p.getProjectName(),
                    p.getCategory(),
                    String.format("%,.2f", p.getActualCost()),
                    p.getActualDuration() + " months",
                    p.getBeneficiariesReached(),
                    String.format("%.0f", p.getSuccessRate() * 100) + "%",
                    String.format("%.2f", p.getFinalIndex())
                };
                tableModel.addRow(row);
            }
        }

        // Update insights
        StringBuilder insights = new StringBuilder();
        insights.append("📊 " + category + " PROJECT INSIGHTS\n");
        insights.append("─────────────────────────────────────────────\n\n");
        
        insights.append("BENCHMARKS:\n");
        insights.append(HistoricalDataService.getBenchmarks(category));
        insights.append("\n");
        
        insights.append("BEST PRACTICES:\n");
        insights.append(HistoricalDataService.getBestPractices(category));
        
        txtInsights.setText(insights.toString());
        
        // Clear prediction preview
        lblPrediction.setText("Select a category, enter project details, and click 'Predict Success'");
        lblPrediction.setFont(new Font("Arial", Font.ITALIC, 12));
        lblPrediction.setForeground(Color.GRAY);
        txtBudget.setText("");
        txtProjectName.setText("");
    }

    // ===== PREDICT SUCCESS with Pop-up Window =====
    private void predictSuccess() {
        // ===== FIXED: Get category from prediction dropdown =====
        String category = (String) cmbPredictionCategory.getSelectedItem();
        if (category == null) category = "Agriculture";
        
        String projectName = txtProjectName.getText().trim();
        String budgetText = txtBudget.getText().trim();
        
        if (projectName.isEmpty() || budgetText.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "Please enter both Project Name and Budget.",
                "Missing Information",
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        try {
            double budget = Double.parseDouble(budgetText);
            
            // Predict success rate
            double successRate = HistoricalDataService.predictSuccessRate(category, budget);
            
            // Get recommendations
            String recommendations = HistoricalDataService.getRecommendations(category, budget, category);
            
            // Create pop-up dialog
            JDialog predictionDialog = new JDialog(
                (Frame) SwingUtilities.getWindowAncestor(this), 
                "🔮 Success Prediction Result", 
                true
            );
            predictionDialog.setLayout(new BorderLayout(10, 10));
            predictionDialog.setSize(500, 400);
            predictionDialog.setLocationRelativeTo(this);
            
            // Build prediction text
            StringBuilder prediction = new StringBuilder();
            prediction.append("╔═══════════════════════════════════════════════════════════════════╗\n");
            prediction.append("║                    SUCCESS PREDICTION                             ║\n");
            prediction.append("╚═══════════════════════════════════════════════════════════════════╝\n\n");
            prediction.append("Project Name:   " + projectName + "\n");
            prediction.append("Category:       " + category + "\n");
            prediction.append("Budget:         " + String.format("%,.2f", budget) + " QR\n");
            prediction.append("─────────────────────────────────────────────────────────────────────\n");
            prediction.append("📈 Estimated Success Rate: " + String.format("%.0f", successRate * 100) + "%\n\n");
            
            if (successRate >= 0.80) {
                prediction.append("✅ HIGH Probability of Success\n");
                prediction.append("   This project type has a strong track record.\n");
            } else if (successRate >= 0.60) {
                prediction.append("⚠️ MODERATE Probability of Success\n");
                prediction.append("   Consider risk mitigation strategies.\n");
            } else {
                prediction.append("❌ LOW Probability of Success\n");
                prediction.append("   Significant improvements recommended.\n");
            }
            
            prediction.append("\n");
            prediction.append("📋 RECOMMENDATIONS:\n");
            prediction.append("─────────────────────────────────────────────────────────────────────\n");
            prediction.append(recommendations);
            
            prediction.append("\n");
            prediction.append("═══════════════════════════════════════════════════════════════════\n");
            prediction.append("Generated by Namaa Smart Waqf Platform\n");
            prediction.append("Analytics & Prediction Engine\n");
            
            // Text area
            JTextArea resultArea = new JTextArea(prediction.toString());
            resultArea.setEditable(false);
            resultArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
            resultArea.setBackground(new Color(255, 255, 240));
            resultArea.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
            
            JScrollPane scrollPane = new JScrollPane(resultArea);
            scrollPane.setPreferredSize(new Dimension(480, 300));
            scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
            
            predictionDialog.add(scrollPane, BorderLayout.CENTER);
            
            // Buttons
            JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
            JButton btnClose = new JButton("Close");
            btnClose.setFont(new Font("Arial", Font.BOLD, 12));
            btnClose.setBackground(new Color(0, 102, 204));
            btnClose.setForeground(Color.WHITE);
            btnClose.setFocusPainted(false);
            btnClose.setPreferredSize(new Dimension(100, 35));
            btnClose.addActionListener(ev -> predictionDialog.dispose());
            buttonPanel.add(btnClose);
            
            JButton btnCopy = new JButton("📋 Copy to Clipboard");
            btnCopy.setFont(new Font("Arial", Font.PLAIN, 12));
            btnCopy.setBackground(new Color(200, 200, 200));
            btnCopy.setFocusPainted(false);
            btnCopy.addActionListener(ev -> {
                java.awt.Toolkit.getDefaultToolkit().getSystemClipboard()
                    .setContents(new java.awt.datatransfer.StringSelection(resultArea.getText()), null);
                JOptionPane.showMessageDialog(predictionDialog, 
                    "✅ Copied to clipboard!", 
                    "Copy Success", 
                    JOptionPane.INFORMATION_MESSAGE);
            });
            buttonPanel.add(btnCopy);
            
            predictionDialog.add(buttonPanel, BorderLayout.SOUTH);
            
            predictionDialog.setVisible(true);
            
            // Update preview
            lblPrediction.setText("✅ Prediction for \"" + projectName + "\": " + 
                                 String.format("%.0f", successRate * 100) + "% success rate in " + category);
            lblPrediction.setFont(new Font("Arial", Font.BOLD, 12));
            lblPrediction.setForeground(new Color(0, 153, 76));
            
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this,
                "Please enter a valid number for Budget.",
                "Invalid Input",
                JOptionPane.ERROR_MESSAGE);
        }
    }

    private void showProjectDetails() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this,
                "Please select a project from the table.",
                "No Selection",
                JOptionPane.WARNING_MESSAGE);
            return;
        }

        int projectId = (int) tableModel.getValueAt(selectedRow, 0);
        HistoricalProject project = HistoricalDataService.getProjectById(projectId);
        
        if (project == null) {
            JOptionPane.showMessageDialog(this,
                "Project not found.",
                "Error",
                JOptionPane.ERROR_MESSAGE);
            return;
        }

        StringBuilder details = new StringBuilder();
        details.append("📋 PROJECT DETAILS\n");
        details.append("═══════════════════════════════════════════\n\n");
        details.append("Project ID:      " + project.getProjectId() + "\n");
        details.append("Project Name:    " + project.getProjectName() + "\n");
        details.append("Category:        " + project.getCategory() + "\n");
        details.append("Status:          " + project.getProjectStatus() + "\n");
        details.append("Completion Date: " + project.getCompletionDate() + "\n\n");
        
        details.append("📊 PERFORMANCE METRICS:\n");
        details.append("───────────────────────────────────────────\n");
        details.append("Actual Cost:     " + String.format("%,.2f", project.getActualCost()) + " QR\n");
        details.append("Duration:        " + project.getActualDuration() + " months\n");
        details.append("Beneficiaries:   " + project.getBeneficiariesReached() + "\n");
        details.append("Success Rate:    " + String.format("%.0f", project.getSuccessRate() * 100) + "%\n");
        details.append("Namaa Index:     " + String.format("%.2f", project.getFinalIndex()) + "\n\n");
        
        details.append("📈 SCORES:\n");
        details.append("───────────────────────────────────────────\n");
        details.append("Economic:        " + String.format("%.2f", project.getEconomicScore()) + "\n");
        details.append("Social:          " + String.format("%.2f", project.getSocialScore()) + "\n");
        details.append("Sustainability:  " + String.format("%.2f", project.getSustainabilityScore()) + "\n");
        details.append("Innovation:      " + String.format("%.2f", project.getInnovationScore()) + "\n\n");
        
        details.append("💡 LESSONS LEARNED:\n");
        details.append("───────────────────────────────────────────\n");
        details.append(project.getLessonsLearned() + "\n");

        JTextArea detailText = new JTextArea(details.toString());
        detailText.setFont(new Font("Monospaced", Font.PLAIN, 12));
        detailText.setEditable(false);
        detailText.setBackground(new Color(255, 255, 240));
        
        JScrollPane scroll = new JScrollPane(detailText);
        scroll.setPreferredSize(new Dimension(600, 500));
        
        JOptionPane.showMessageDialog(this,
            scroll,
            "Project Details",
            JOptionPane.INFORMATION_MESSAGE);
    }

    public static void refreshData() {
        HistoricalDataService.refresh();
    }
}