package UI;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import Service.AIService;

public class AIConfigPanel extends JDialog {
    private JComboBox<String> cmbProvider;
    private JTextField txtOpenAIKey, txtGeminiKey, txtGroqKey;
    private JButton btnSave, btnTest, btnCancel;
    private JTextArea txtStatus;

    public AIConfigPanel(JFrame parent) {
        super(parent, "AI Configuration", true);
        setLayout(new BorderLayout(10, 10));
        setSize(550, 450);
        setLocationRelativeTo(parent);

        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        int row = 0;

        // Title
        JLabel lblTitle = new JLabel("AI Provider Configuration", JLabel.CENTER);
        lblTitle.setFont(new Font("Arial", Font.BOLD, 16));
        gbc.gridx = 0; gbc.gridy = row; gbc.gridwidth = 2;
        mainPanel.add(lblTitle, gbc);
        row++;

        // Provider Selection
        gbc.gridy = row; gbc.gridwidth = 1;
        gbc.gridx = 0; gbc.weightx = 0.3;
        mainPanel.add(new JLabel("AI Provider:"), gbc);
        gbc.gridx = 1; gbc.weightx = 0.7;
        cmbProvider = new JComboBox<>(new String[]{"gemini", "openai", "groq"});
        cmbProvider.setSelectedItem(AIService.getCurrentProvider());
        cmbProvider.addActionListener(e -> updateFields());
        mainPanel.add(cmbProvider, gbc);
        row++;

        // Separator
        gbc.gridy = row; gbc.gridx = 0; gbc.gridwidth = 2;
        mainPanel.add(new JSeparator(), gbc);
        row++;
        gbc.gridwidth = 1;

        // OpenAI Key
        gbc.gridy = row; gbc.gridx = 0; gbc.weightx = 0.3;
        mainPanel.add(new JLabel("OpenAI API Key:"), gbc);
        gbc.gridx = 1; gbc.weightx = 0.7;
        txtOpenAIKey = new JTextField(20);
        txtOpenAIKey.setText(System.getProperty("openai.api.key", ""));
        mainPanel.add(txtOpenAIKey, gbc);
        row++;

        // Gemini Key
        gbc.gridy = row; gbc.gridx = 0; gbc.weightx = 0.3;
        mainPanel.add(new JLabel("Gemini API Key:"), gbc);
        gbc.gridx = 1; gbc.weightx = 0.7;
        txtGeminiKey = new JTextField(20);
        txtGeminiKey.setText(System.getProperty("gemini.api.key", ""));
        mainPanel.add(txtGeminiKey, gbc);
        row++;

        // Groq Key
        gbc.gridy = row; gbc.gridx = 0; gbc.weightx = 0.3;
        mainPanel.add(new JLabel("Groq API Key:"), gbc);
        gbc.gridx = 1; gbc.weightx = 0.7;
        txtGroqKey = new JTextField(20);
        txtGroqKey.setText(System.getProperty("groq.api.key", ""));
        mainPanel.add(txtGroqKey, gbc);
        row++;

        // Status
        gbc.gridy = row; gbc.gridx = 0; gbc.gridwidth = 2;
        txtStatus = new JTextArea(6, 30);
        txtStatus.setEditable(false);
        txtStatus.setFont(new Font("Monospaced", Font.PLAIN, 11));
        txtStatus.setBackground(new Color(255, 255, 240));
        txtStatus.setText(AIService.getAvailableProviders() + 
                         "\n\nEnter API keys and click 'Test Connection'");
        JScrollPane scroll = new JScrollPane(txtStatus);
        scroll.setPreferredSize(new Dimension(400, 120));
        mainPanel.add(scroll, gbc);
        row++;

        add(mainPanel, BorderLayout.CENTER);

        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        
        btnTest = new JButton("Test Connection");
        btnTest.setBackground(new Color(0, 102, 204));
        btnTest.setForeground(Color.BLACK);
        btnTest.setFocusPainted(false);
        btnTest.addActionListener(e -> testConnection());
        buttonPanel.add(btnTest);

        btnSave = new JButton("Save & Enable");
        btnSave.setBackground(new Color(0, 153, 76));
        btnSave.setForeground(Color.BLACK);
        btnSave.setFocusPainted(false);
        btnSave.addActionListener(e -> saveConfig());
        buttonPanel.add(btnSave);

        btnCancel = new JButton("Cancel");
        btnCancel.addActionListener(e -> dispose());
        buttonPanel.add(btnCancel);

        add(buttonPanel, BorderLayout.SOUTH);
        
        updateFields();
    }

    private void updateFields() {
        String provider = (String) cmbProvider.getSelectedItem();
        // Highlight the selected provider's field
        // Just visual feedback
    }

    
    private void saveConfig() {
        String provider = (String) cmbProvider.getSelectedItem();
        
        // Save all keys
        System.setProperty("openai.api.key", txtOpenAIKey.getText().trim());
        System.setProperty("gemini.api.key", txtGeminiKey.getText().trim());
        System.setProperty("groq.api.key", txtGroqKey.getText().trim());
        
        // Set the active provider
        String selectedProvider = (String) cmbProvider.getSelectedItem();
        System.setProperty("ai.provider", selectedProvider);
        
        // Update AIService - we need to reload
        // Since keys are static, we use setters
        AIService.setOpenAIKey(txtOpenAIKey.getText().trim());
        AIService.setGeminiKey(txtGeminiKey.getText().trim());
        AIService.setGroqKey(txtGroqKey.getText().trim());
        
        JOptionPane.showMessageDialog(this, 
            "✅ Configuration saved!\n\n" +
            "Provider: " + provider + "\n" +
            "Status: " + (AIService.isAIAvailable() ? "ENABLED" : "DISABLED (Check API key)"),
            "Success",
            JOptionPane.INFORMATION_MESSAGE);
        dispose();
    }

    private String getKeyForProvider(String provider) {
        switch (provider.toLowerCase()) {
            case "openai": return txtOpenAIKey.getText().trim();
            case "gemini": return txtGeminiKey.getText().trim();
            case "groq": return txtGroqKey.getText().trim();
            default: return "";
        }
    }

    private void setKeyForProvider(String provider, String key) {
        switch (provider.toLowerCase()) {
            case "openai": txtOpenAIKey.setText(key); break;
            case "gemini": txtGeminiKey.setText(key); break;
            case "groq": txtGroqKey.setText(key); break;
        }
    }
    
 // In AIConfigPanel.java
    private void testConnection() {
        String provider = (String) cmbProvider.getSelectedItem();
        String apiKey = getKeyForProvider(provider);
        
        if (apiKey.isEmpty()) {
            txtStatus.setText("⚠️ Please enter an API key for " + provider + ".");
            return;
        }

        // ===== TEST WITH BOTH FORMATS =====
        txtStatus.setText("🔍 Testing connection...\n");
        
        // Store current keys
        String oldKey = getKeyForProvider(provider);
        setKeyForProvider(provider, apiKey);
        
        SwingWorker<String, Void> worker = new SwingWorker<>() {
            @Override
            protected String doInBackground() {
                try {
                    // Try sending a simple message
                    String result = AIService.sendChatMessage(
                        "You are a helpful assistant.",
                        "Reply with exactly: 'Connection successful!'"
                    );
                    
                    // Check if result contains the expected response or an error
                    if (result.contains("Connection successful") || 
                        result.contains("Gemini")) {
                        return "✅ Connection successful!\n\n" +
                               "Provider: " + provider + "\n" +
                               "Key Format: " + (apiKey.startsWith("AIzaSy") ? "Legacy (AIzaSy)" : "New (AQ.)") + "\n" +
                               "Response: " + result;
                    } else {
                        return "⚠️ Connection issue:\n" + result;
                    }
                } catch (Exception e) {
                    return "⚠️ Connection failed: " + e.getMessage();
                } finally {
                    if (oldKey != null && !oldKey.isEmpty()) {
                        setKeyForProvider(provider, oldKey);
                    }
                }
            }

            @Override
            protected void done() {
                try {
                    String result = get();
                    txtStatus.setText(result);
                } catch (Exception e) {
                    txtStatus.setText("⚠️ Error: " + e.getMessage());
                }
            }
        };
        worker.execute();
    }
}