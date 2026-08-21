package UI;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import Model.*;
import Service.*;
import java.time.LocalDate;
import Service.AIService;

public class CompleteProjectDialog extends JDialog {
    private FundingApplication app;
    private ProjectAssessment assessment;
    private NamaaIndex index;
    
    private JTextField txtDuration, txtBeneficiaries, txtSuccessRate;
    private JTextArea txtLessonsLearned;
    private JButton btnSave, btnCancel, btnAISuggest;
    
    public CompleteProjectDialog(JFrame parent, FundingApplication app, 
                                  ProjectAssessment assessment, NamaaIndex index) {
        super(parent, "Complete Project - " + app.getProject().getProjectName(), true);
        this.app = app;
        this.assessment = assessment;
        this.index = index;
        
        setLayout(new BorderLayout(10, 10));
        setSize(600, 520);
        setLocationRelativeTo(parent);
        
        // Main panel
        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        int row = 0;
        
        // Project Name
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0.3;
        JLabel lblProject = new JLabel("Project:");
        lblProject.setFont(new Font("Arial", Font.BOLD, 12));
        mainPanel.add(lblProject, gbc);
        gbc.gridx = 1; gbc.weightx = 0.7;
        JLabel lblProjectName = new JLabel(app.getProject().getProjectName());
        lblProjectName.setFont(new Font("Arial", Font.BOLD, 12));
        lblProjectName.setForeground(new Color(0, 102, 204));
        mainPanel.add(lblProjectName, gbc);
        row++;
        
        // Sector
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0.3;
        mainPanel.add(new JLabel("Sector:"), gbc);
        gbc.gridx = 1; gbc.weightx = 0.7;
        JLabel lblSector = new JLabel(app.getProject().getSector());
        mainPanel.add(lblSector, gbc);
        row++;
        
        // PRI Score
        if (assessment != null) {
            gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0.3;
            mainPanel.add(new JLabel("PRI Score:"), gbc);
            gbc.gridx = 1; gbc.weightx = 0.7;
            JLabel lblPRI = new JLabel(String.format("%.2f", assessment.getPriScore()));
            lblPRI.setFont(new Font("Arial", Font.BOLD, 12));
            lblPRI.setForeground(new Color(0, 153, 76));
            mainPanel.add(lblPRI, gbc);
            row++;
        }
        
        // Namaa Index
        if (index != null) {
            gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0.3;
            mainPanel.add(new JLabel("Namaa Index:"), gbc);
            gbc.gridx = 1; gbc.weightx = 0.7;
            JLabel lblIndex = new JLabel(String.format("%.2f", index.getFinalIndex()));
            lblIndex.setFont(new Font("Arial", Font.BOLD, 12));
            lblIndex.setForeground(new Color(0, 102, 204));
            mainPanel.add(lblIndex, gbc);
            row++;
        }
        
        // Separator
        gbc.gridx = 0; gbc.gridy = row; gbc.gridwidth = 2;
        mainPanel.add(new JSeparator(), gbc);
        row++;
        gbc.gridwidth = 1;
        
        // Actual Duration
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0.3;
        mainPanel.add(new JLabel("Actual Duration (months):"), gbc);
        gbc.gridx = 1; gbc.weightx = 0.7;
        txtDuration = new JTextField("12");
        txtDuration.addKeyListener(new KeyAdapter() {
            public void keyReleased(KeyEvent e) {
                regenerateAILessons();
            }
        });
        mainPanel.add(txtDuration, gbc);
        row++;
        
        // Beneficiaries Reached
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0.3;
        mainPanel.add(new JLabel("Beneficiaries Reached:"), gbc);
        gbc.gridx = 1; gbc.weightx = 0.7;
        txtBeneficiaries = new JTextField("50");
        txtBeneficiaries.addKeyListener(new KeyAdapter() {
            public void keyReleased(KeyEvent e) {
                regenerateAILessons();
            }
        });
        mainPanel.add(txtBeneficiaries, gbc);
        row++;
        
        // Success Rate
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0.3;
        mainPanel.add(new JLabel("Success Rate (%):"), gbc);
        gbc.gridx = 1; gbc.weightx = 0.7;
        txtSuccessRate = new JTextField("85");
        txtSuccessRate.addKeyListener(new KeyAdapter() {
            public void keyReleased(KeyEvent e) {
                regenerateAILessons();
            }
        });
        mainPanel.add(txtSuccessRate, gbc);
        row++;
        
        // Lessons Learned
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0.3;
        mainPanel.add(new JLabel("Lessons Learned:"), gbc);
        gbc.gridx = 1; gbc.weightx = 0.7;
        txtLessonsLearned = new JTextArea(5, 20);
        txtLessonsLearned.setLineWrap(true);
        txtLessonsLearned.setWrapStyleWord(true);
        txtLessonsLearned.setFont(new Font("Monospaced", Font.PLAIN, 12));
        JScrollPane scroll = new JScrollPane(txtLessonsLearned);
        scroll.setPreferredSize(new Dimension(200, 100));
        mainPanel.add(scroll, gbc);
        row++;
        
        // AI Suggestion Button
        JPanel aiPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnAISuggest = new JButton("🤖 Generate AI Lessons");
        btnAISuggest.setFont(new Font("Arial", Font.BOLD, 11));
        btnAISuggest.setBackground(new Color(0, 102, 204));
        btnAISuggest.setForeground(Color.BLACK);
        btnAISuggest.setFocusPainted(false);
        btnAISuggest.addActionListener(e -> regenerateAILessons());
        aiPanel.add(btnAISuggest);
        gbc.gridx = 0; gbc.gridy = row; gbc.gridwidth = 2;
        mainPanel.add(aiPanel, gbc);
        row++;
        
        add(mainPanel, BorderLayout.CENTER);
        
        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        btnSave = new JButton("💾 Save to History");
        btnSave.setFont(new Font("Arial", Font.BOLD, 12));
        btnSave.setBackground(new Color(0, 153, 76));
        btnSave.setForeground(Color.BLACK);
        btnSave.setFocusPainted(false);
        btnSave.addActionListener(e -> saveProject());
        buttonPanel.add(btnSave);
        
        btnCancel = new JButton("Cancel");
        btnCancel.setFont(new Font("Arial", Font.PLAIN, 12));
        btnCancel.addActionListener(e -> dispose());
        buttonPanel.add(btnCancel);
        
        add(buttonPanel, BorderLayout.SOUTH);
        
        // Generate initial AI lessons
        txtLessonsLearned.setText("🤖 Generating AI lessons...");
        regenerateAILessons();
    }
    
    private void regenerateAILessons() {
        SwingWorker<String, Void> worker = new SwingWorker<>() {
            @Override
            protected String doInBackground() {
                try {
                    int duration = Integer.parseInt(txtDuration.getText().trim());
                    int beneficiaries = Integer.parseInt(txtBeneficiaries.getText().trim());
                    double successRate = Double.parseDouble(txtSuccessRate.getText().trim()) / 100.0;
                    double finalIndex = 0;
                    
                    if (index != null) {
                        finalIndex = index.getFinalIndex();
                    } else if (assessment != null) {
                        finalIndex = (assessment.getEconomicScore() + 
                                     assessment.getSocialScore() + 
                                     assessment.getEnvironmentalScore() + 
                                     assessment.getInnovationScore()) / 4.0;
                    }
                    
                    return AIService.generateLessonsLearned(
                        app.getProject().getProjectName(),
                        app.getProject().getSector(),
                        duration,
                        beneficiaries,
                        successRate,
                        finalIndex
                    );
                } catch (Exception e) {
                    return generateFallbackLessons();
                }
            }

            @Override
            protected void done() {
                try {
                    String result = get();
                    txtLessonsLearned.setText(result);
                } catch (Exception e) {
                    txtLessonsLearned.setText(generateFallbackLessons());
                }
            }
        };
        worker.execute();
    }
    
    private String generateFallbackLessons() {
        StringBuilder sb = new StringBuilder();
        sb.append("📚 LESSONS LEARNED (AI Fallback Mode)\n");
        sb.append("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n\n");
        sb.append("Project: ").append(app.getProject().getProjectName()).append("\n");
        sb.append("Sector: ").append(app.getProject().getSector()).append("\n\n");
        sb.append("Based on the project assessment:\n");
        
        if (assessment != null && assessment.getPriScore() >= 80) {
            sb.append("✅ SUCCESS FACTORS:\n");
            sb.append("  • Strong planning and execution\n");
            sb.append("  • Effective community engagement\n");
            sb.append("  • Adequate resource allocation\n\n");
            sb.append("📌 RECOMMENDATIONS:\n");
            sb.append("  • Document and share best practices\n");
            sb.append("  • Consider scaling to similar communities\n");
        } else if (assessment != null && assessment.getPriScore() >= 60) {
            sb.append("⚠️ CHALLENGES & OPPORTUNITIES:\n");
            sb.append("  • Some implementation challenges faced\n");
            sb.append("  • Valuable lessons learned\n\n");
            sb.append("📌 RECOMMENDATIONS:\n");
            sb.append("  • Strengthen planning phase\n");
            sb.append("  • Increase stakeholder engagement\n");
        } else {
            sb.append("❌ KEY CHALLENGES:\n");
            sb.append("  • Significant barriers encountered\n");
            sb.append("  • Resource limitations\n\n");
            sb.append("📌 RECOMMENDATIONS:\n");
            sb.append("  • Conduct thorough feasibility study\n");
            sb.append("  • Build stronger partnerships\n");
        }
        
        sb.append("\n💡 To enable full AI lessons, set OPENAI_API_KEY.");
        return sb.toString();
    }
    
    private void saveProject() {
        try {
            int duration = Integer.parseInt(txtDuration.getText().trim());
            int beneficiaries = Integer.parseInt(txtBeneficiaries.getText().trim());
            double successRate = Double.parseDouble(txtSuccessRate.getText().trim()) / 100.0;
            String lessons = txtLessonsLearned.getText().trim();
            
            if (duration <= 0 || beneficiaries <= 0) {
                JOptionPane.showMessageDialog(this, "Please enter valid numbers for all fields.");
                return;
            }
            
            if (successRate < 0 || successRate > 1) {
                JOptionPane.showMessageDialog(this, "Success rate must be between 0 and 100.");
                return;
            }
            
            if (lessons.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please enter lessons learned.");
                return;
            }
            
            // Create historical project
            HistoricalProject historical = new HistoricalProject();
            historical.setProjectId(app.getApplicationID());
            historical.setProjectName(app.getProject().getProjectName());
            historical.setCategory(app.getProject().getSector());
            historical.setActualCost(app.getRequestedAmount());
            historical.setActualDuration(duration);
            historical.setBeneficiariesReached(beneficiaries);
            historical.setSuccessRate(successRate);
            historical.setLessonsLearned(lessons);
            
            if (assessment != null) {
                historical.setEconomicScore(assessment.getEconomicScore());
                historical.setSocialScore(assessment.getSocialScore());
                historical.setSustainabilityScore(assessment.getEnvironmentalScore());
                historical.setInnovationScore(assessment.getInnovationScore());
            }
            
            if (index != null) {
                historical.setFinalIndex(index.getFinalIndex());
            } else if (assessment != null) {
                historical.setFinalIndex((assessment.getEconomicScore() + 
                                         assessment.getSocialScore() + 
                                         assessment.getEnvironmentalScore() + 
                                         assessment.getInnovationScore()) / 4.0);
            }
            
            historical.setProjectStatus("Completed");
            historical.setCompletionDate(LocalDate.now().toString());
            
            HistoricalDataService.addHistoricalProject(historical);
            
            // FIXED: Use enum value for status
            app.setStatus(ApplicationStatus.COMPLETED);
            FundingService.updateApplication(app);
            
            JOptionPane.showMessageDialog(this, 
                "✅ Project completed and saved to history successfully!\n" +
                "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n" +
                "Project: " + app.getProject().getProjectName() + "\n" +
                "Duration: " + duration + " months\n" +
                "Beneficiaries: " + beneficiaries + "\n" +
                "Success Rate: " + String.format("%.0f", successRate * 100) + "%",
                "Success",
                JOptionPane.INFORMATION_MESSAGE);
            
            dispose();
            
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Please enter valid numbers for all fields.");
        }
    }
}