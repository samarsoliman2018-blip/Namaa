package UI;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import Model.*;
import Service.*;

public class NamaaIndexPanel extends JPanel implements ActionListener {
    private JTextField txtIndexID, txtProjectID, txtEconomic, txtSocial, 
                         txtSustainability, txtInnovation;
    private JTextArea txtResult;
    private JTable table;
    private DefaultTableModel tableModel;
    private JButton btnCalculate, btnClear, btnViewAll, btnLoadProject;
    private NamaaIndex currentIndex;
    private JLabel lblStatusMessage, lblProjectName;
    private JComboBox<String> cmbProject;

    public NamaaIndexPanel() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Title
        JLabel lblTitle = new JLabel("NAMAA SMART WAQF PLATFORM (Namaa Index Calculator)", JLabel.CENTER);
        lblTitle.setFont(new Font("Arial", Font.BOLD, 20));
        lblTitle.setForeground(new Color(0, 102, 204));
        add(lblTitle, BorderLayout.NORTH);

        // Split Panel
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setDividerLocation(450);
        splitPane.setResizeWeight(0.4);

        // LEFT PANEL
        JPanel leftPanel = new JPanel(new BorderLayout(5, 5));

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createEtchedBorder(),
            "Impact Metrics (0-100)",
            TitledBorder.LEFT,
            TitledBorder.TOP,
            new Font("Arial", Font.BOLD, 12)
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 4, 4, 4);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // ===== INITIALIZE ALL TEXT FIELDS =====
        txtIndexID = new JTextField(10);
        txtProjectID = new JTextField(10);
        txtEconomic = new JTextField(10);
        txtSocial = new JTextField(10);
        txtSustainability = new JTextField(10);
        txtInnovation = new JTextField(10);
        lblProjectName = new JLabel("Not loaded");

        // Row 1: Index ID (Auto)
        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0.3;
        JLabel lblIndexID = new JLabel("Index ID:");
        lblIndexID.setFont(new Font("Arial", Font.BOLD, 12));
        formPanel.add(lblIndexID, gbc);

        gbc.gridx = 1; gbc.weightx = 0.7;
        txtIndexID.setEditable(false);
        txtIndexID.setBackground(new Color(240, 240, 240));
        txtIndexID.setPreferredSize(new Dimension(120, 28));
        txtIndexID.setText(String.valueOf(getNextIndexId()));
        formPanel.add(txtIndexID, gbc);

        // Row 2: Project Selection (ComboBox with all projects)
        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0.3;
        JLabel lblProjectSelect = new JLabel("Select Project:");
        lblProjectSelect.setFont(new Font("Arial", Font.BOLD, 12));
        formPanel.add(lblProjectSelect, gbc);

        gbc.gridx = 1; gbc.weightx = 0.7;
        JPanel projectPanel = new JPanel(new BorderLayout(5, 0));
        cmbProject = new JComboBox<>();
        cmbProject.setFont(new Font("Arial", Font.PLAIN, 12));
        cmbProject.setPreferredSize(new Dimension(120, 28));
        cmbProject.addActionListener(this); // Auto-load project details when selected
        projectPanel.add(cmbProject, BorderLayout.CENTER);
        
        JButton btnRefreshProjects = new JButton("↻");
        btnRefreshProjects.setFont(new Font("Arial", Font.BOLD, 14));
        btnRefreshProjects.setPreferredSize(new Dimension(30, 28));
        btnRefreshProjects.setToolTipText("Refresh project list");
        btnRefreshProjects.addActionListener(this);
        btnRefreshProjects.setActionCommand("refreshProjects");
        projectPanel.add(btnRefreshProjects, BorderLayout.EAST);
        
        formPanel.add(projectPanel, gbc);

        // Row 3: Project ID (read-only, shows selected project ID)
        gbc.gridx = 0; gbc.gridy = 2; gbc.weightx = 0.3;
        JLabel lblProjectID = new JLabel("Project ID:");
        lblProjectID.setFont(new Font("Arial", Font.BOLD, 12));
        formPanel.add(lblProjectID, gbc);

        gbc.gridx = 1; gbc.weightx = 0.7;
        txtProjectID.setEditable(false);
        txtProjectID.setBackground(new Color(240, 240, 240));
        txtProjectID.setPreferredSize(new Dimension(120, 28));
        formPanel.add(txtProjectID, gbc);

        // Row 4: Project Name (read-only)
        gbc.gridx = 0; gbc.gridy = 3; gbc.weightx = 0.3;
        JLabel lblProjectNameLabel = new JLabel("Project Name:");
        lblProjectNameLabel.setFont(new Font("Arial", Font.BOLD, 12));
        formPanel.add(lblProjectNameLabel, gbc);

        gbc.gridx = 1; gbc.weightx = 0.7;
        lblProjectName.setFont(new Font("Arial", Font.PLAIN, 12));
        lblProjectName.setForeground(new Color(0, 102, 204));
        lblProjectName.setBackground(new Color(240, 240, 240));
        lblProjectName.setOpaque(true);
        lblProjectName.setPreferredSize(new Dimension(120, 28));
        lblProjectName.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        formPanel.add(lblProjectName, gbc);

        // Row 5: Economic Impact
        gbc.gridx = 0; gbc.gridy = 4; gbc.weightx = 0.3;
        JLabel lblEconomic = new JLabel("Economic Impact:");
        lblEconomic.setFont(new Font("Arial", Font.BOLD, 12));
        formPanel.add(lblEconomic, gbc);

        gbc.gridx = 1; gbc.weightx = 0.7;
        txtEconomic.setPreferredSize(new Dimension(120, 28));
        formPanel.add(txtEconomic, gbc);

        // Row 6: Social Impact
        gbc.gridx = 0; gbc.gridy = 5; gbc.weightx = 0.3;
        JLabel lblSocial = new JLabel("Social Impact:");
        lblSocial.setFont(new Font("Arial", Font.BOLD, 12));
        formPanel.add(lblSocial, gbc);

        gbc.gridx = 1; gbc.weightx = 0.7;
        txtSocial.setPreferredSize(new Dimension(120, 28));
        formPanel.add(txtSocial, gbc);

        // Row 7: Sustainability
        gbc.gridx = 0; gbc.gridy = 6; gbc.weightx = 0.3;
        JLabel lblSustainability = new JLabel("Sustainability:");
        lblSustainability.setFont(new Font("Arial", Font.BOLD, 12));
        formPanel.add(lblSustainability, gbc);

        gbc.gridx = 1; gbc.weightx = 0.7;
        txtSustainability.setPreferredSize(new Dimension(120, 28));
        formPanel.add(txtSustainability, gbc);

        // Row 8: Innovation
        gbc.gridx = 0; gbc.gridy = 7; gbc.weightx = 0.3;
        JLabel lblInnovation = new JLabel("Innovation:");
        lblInnovation.setFont(new Font("Arial", Font.BOLD, 12));
        formPanel.add(lblInnovation, gbc);

        gbc.gridx = 1; gbc.weightx = 0.7;
        txtInnovation.setPreferredSize(new Dimension(120, 28));
        formPanel.add(txtInnovation, gbc);

        leftPanel.add(formPanel, BorderLayout.NORTH);

        // Result Area
        JPanel resultPanel = new JPanel(new BorderLayout());
        resultPanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createEtchedBorder(),
            "📊 Index Result",
            TitledBorder.LEFT,
            TitledBorder.TOP,
            new Font("Arial", Font.BOLD, 11)
        ));
        txtResult = new JTextArea(8, 25);
        txtResult.setEditable(false);
        txtResult.setFont(new Font("Monospaced", Font.PLAIN, 12));
        txtResult.setBackground(new Color(255, 255, 240));
        txtResult.setText("Select a project and enter metrics, then click Calculate");
        
        JScrollPane scrollResult = new JScrollPane(txtResult);
        scrollResult.setPreferredSize(new Dimension(300, 160));
        resultPanel.add(scrollResult, BorderLayout.CENTER);
        leftPanel.add(resultPanel, BorderLayout.CENTER);

        // Buttons
        JPanel buttonPanel = new JPanel(new GridLayout(1, 3, 5, 5));
        btnCalculate = new JButton("Calculate");
        btnClear = new JButton("Clear");
        btnViewAll = new JButton("View All");

        JButton[] buttons = {btnCalculate, btnClear, btnViewAll};
        Color[] colors = {Color.GREEN, Color.BLUE, Color.ORANGE, Color.PINK}; 
        
        for (int i = 0; i < buttons.length; i++) {
            buttons[i].setFont(new Font("Arial", Font.BOLD, 12));
            buttons[i].setBackground(colors[i]);
            buttons[i].setForeground(Color.RED);
            buttons[i].setFocusPainted(false);
            buttons[i].setPreferredSize(new Dimension(100, 40));
            buttons[i].addActionListener(this);
            buttonPanel.add(buttons[i]);
        }


        leftPanel.add(buttonPanel, BorderLayout.SOUTH);

        // RIGHT PANEL: Table
        JPanel rightPanel = new JPanel(new BorderLayout(5, 5));

        JLabel lblTableTitle = new JLabel("All Namaa Indexes", JLabel.CENTER);
        lblTableTitle.setFont(new Font("Arial", Font.BOLD, 14));
        rightPanel.add(lblTableTitle, BorderLayout.NORTH);

        String[] columns = {"ID", "Project ID", "Project Name", "Final Index", "Grade"};
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
                    setStatus("Selected Index ID: " + id);
                }
            }
        });

        table.getColumnModel().getColumn(0).setPreferredWidth(50);
        table.getColumnModel().getColumn(1).setPreferredWidth(80);
        table.getColumnModel().getColumn(2).setPreferredWidth(150);
        table.getColumnModel().getColumn(3).setPreferredWidth(80);
        table.getColumnModel().getColumn(4).setPreferredWidth(100);

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createEtchedBorder());
        rightPanel.add(scrollPane, BorderLayout.CENTER);

        splitPane.setLeftComponent(leftPanel);
        splitPane.setRightComponent(rightPanel);
        add(splitPane, BorderLayout.CENTER);

        // Status Bar
        JPanel statusBar = new JPanel(new FlowLayout(FlowLayout.LEFT));
        statusBar.setBorder(BorderFactory.createEtchedBorder());
        lblStatusMessage = new JLabel("Ready - Select a project and enter metrics");
        lblStatusMessage.setFont(new Font("Arial", Font.PLAIN, 11));
        statusBar.add(lblStatusMessage);
        add(statusBar, BorderLayout.SOUTH);

        // Initialize
        currentIndex = new NamaaIndex();
        loadProjects();
        updateTable();
    }

    // ===== GET NEXT INDEX ID =====
    private int getNextIndexId() {
        int maxId = 0;
        for (NamaaIndex idx : NamaaIndexService.getIndexes()) {
            if (idx.getIndexID() > maxId) {
                maxId = idx.getIndexID();
            }
        }
        return maxId + 1;
    }

    // ===== LOAD PROJECTS INTO COMBOBOX =====
 // ===== REPLACE loadProjects() method =====
    private void loadProjects() {
        cmbProject.removeAllItems();
        
        // ===== FIXED: Get projects from ProjectService directly =====
        java.util.ArrayList<Project> allProjects = ProjectService.getProjects();
        
        if (allProjects.isEmpty()) {
            cmbProject.addItem("No projects found - Submit an application first");
            setStatus("⚠️ No projects available. Submit a funding application first.");
            return;
        }
        
        // Add each project with clear display
        for (Project project : allProjects) {
            // Check if project has an application for status
            String status = "Pending";
            for (FundingApplication app : FundingService.getApplications()) {
                if (app.getProject().getProjectID() == project.getProjectID()) {
                    status = app.getStatus().name();
                    break;
                }
            }
            
            String displayText = "ID: " + project.getProjectID() + 
                                " | " + project.getProjectName() + 
                                " (" + project.getSector() + ") - " + status;
            cmbProject.addItem(displayText);
        }
        
        setStatus("Loaded " + allProjects.size() + " projects from ProjectService");
    }

    // ===== GET SELECTED PROJECT =====
    private Project getSelectedProject() {
        String selected = (String) cmbProject.getSelectedItem();
        
        if (selected == null || selected.equals("No projects found - Submit an application first") ||
            selected.equals("No projects found")) {
            return null;
        }
        
        try {
            int start = selected.indexOf("ID: ") + 4;
            int end = selected.indexOf(" |");
            if (start > 0 && end > start) {
                int projectId = Integer.parseInt(selected.substring(start, end));
                
                // Find the project from applications
                for (FundingApplication app : FundingService.getApplications()) {
                    if (app.getProject().getProjectID() == projectId) {
                        return app.getProject();
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        try {
            // Handle project selection - auto-load project details
            if (e.getSource() == cmbProject) {
                Project selectedProject = getSelectedProject();
                if (selectedProject != null) {
                    txtProjectID.setText(String.valueOf(selectedProject.getProjectID()));
                    lblProjectName.setText(selectedProject.getProjectName());
                    setStatus("Loaded Project: " + selectedProject.getProjectName() + 
                             " (ID: " + selectedProject.getProjectID() + ")");
                } else {
                    txtProjectID.setText("");
                    lblProjectName.setText("Not loaded");
                }
                return;
            }
            
            String command = e.getActionCommand();
            if ("refreshProjects".equals(command)) {
                loadProjects();
                JOptionPane.showMessageDialog(this,
                    "Project list refreshed!",
                    "Refreshed",
                    JOptionPane.INFORMATION_MESSAGE);
                return;
            }
            
            if (e.getSource() == btnCalculate) {
                calculateIndex();
            } else if (e.getSource() == btnClear) {
                clearForm();
            } else if (e.getSource() == btnViewAll) {
                updateTable();
                setStatus("Displaying all indexes");
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                "Error: " + ex.getMessage(),
                "Operation Failed",
                JOptionPane.ERROR_MESSAGE);
            setStatus("Error: " + ex.getMessage());
        }
    }

    // ===== CALCULATE INDEX =====
    private void calculateIndex() {
        Project project = getSelectedProject();
        if (project == null) {
            JOptionPane.showMessageDialog(this,
                "Please select a project from the dropdown.",
                "No Project Selected",
                JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            double economic = Double.parseDouble(txtEconomic.getText().trim());
            double social = Double.parseDouble(txtSocial.getText().trim());
            double sustainability = Double.parseDouble(txtSustainability.getText().trim());
            double innovation = Double.parseDouble(txtInnovation.getText().trim());

            // Validate metrics are between 0-100
            if (economic < 0 || economic > 100 || social < 0 || social > 100 ||
                sustainability < 0 || sustainability > 100 || innovation < 0 || innovation > 100) {
                JOptionPane.showMessageDialog(this,
                    "All metrics must be between 0 and 100.",
                    "Invalid Metric",
                    JOptionPane.ERROR_MESSAGE);
                return;
            }

            int id = getNextIndexId();
            
            // Create NamaaIndex
            currentIndex = new NamaaIndex(id, project, economic, social, sustainability, innovation);
            NamaaIndexService.addIndex(currentIndex);
            txtIndexID.setText(String.valueOf(getNextIndexId()));

            double finalIndex = currentIndex.getFinalIndex();
            String grade;
            String emoji;
            if (finalIndex >= 80) {
                grade = "Excellent";
                emoji = "🌟";
            } else if (finalIndex >= 60) {
                grade = "Good";
                emoji = "✅";
            } else if (finalIndex >= 40) {
                grade = "Average";
                emoji = "⚠️";
            } else {
                grade = "Needs Improvement";
                emoji = "❌";
            }

            // Display results with project info
            txtResult.setText(
                "╔════════════════════════════════════════════\n" +
                "║        NAMAA INDEX RESULTS                 \n" +
                "╠════════════════════════════════════════════\n" +
                "║  Project:     " + String.format("%-25s", project.getProjectName()) + "\n" +
                "║  Project ID:  " + String.format("%-25d", project.getProjectID()) + "\n" +
                "║  Sector:      " + String.format("%-25s", project.getSector()) + "\n" +
                "╠════════════════════════════════════════════\n" +
                "║  Economic:    " + String.format("%7.2f", economic) + "\n" +
                "║  Social:      " + String.format("%7.2f", social) + " \n" +
                "║  Sustainability:" + String.format("%7.2f", sustainability) + "\n" +
                "║  Innovation:  " + String.format("%7.2f", innovation) + "\n" +
                "╠════════════════════════════════════════════\n" +
                "║  ★ FINAL INDEX: " + String.format("%7.2f", finalIndex) + " \n" +
                "║  ★ GRADE:      " + String.format("%-25s", emoji + " " + grade) + "\n" +
                "╚════════════════════════════════════════════"
            );

            updateTable();
            setStatus("Index ID " + id + " calculated for Project: " + project.getProjectName() + 
                     " | Score: " + String.format("%.2f", finalIndex));

            JOptionPane.showMessageDialog(this,
                "✅ Index calculated successfully!\n" +
                "Project: " + project.getProjectName() + "\n" +
                "Final Index: " + String.format("%.2f", finalIndex) + "\n" +
                "Grade: " + emoji + " " + grade,
                "Success",
                JOptionPane.INFORMATION_MESSAGE);

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this,
                "Please enter valid numbers for all metrics.",
                "Invalid Input",
                JOptionPane.ERROR_MESSAGE);
        }
    }

    // ===== CLEAR FORM =====
    private void clearForm() {
        txtIndexID.setText(String.valueOf(getNextIndexId()));
        txtProjectID.setText("");
        lblProjectName.setText("Not loaded");
        txtEconomic.setText("");
        txtSocial.setText("");
        txtSustainability.setText("");
        txtInnovation.setText("");
        txtResult.setText("Select a project and enter metrics, then click Calculate");
        currentIndex = new NamaaIndex();
        if (cmbProject.getItemCount() > 0) {
            cmbProject.setSelectedIndex(0);
        }
        setStatus("Form cleared. Ready for new calculation.");
    }

    // ===== UPDATE TABLE =====
    private void updateTable() {
        tableModel.setRowCount(0);
        for (NamaaIndex idx : NamaaIndexService.getIndexes()) {
            double finalIndex = idx.getFinalIndex();
            String grade = finalIndex >= 80 ? "🌟 Excellent" :
                          finalIndex >= 60 ? "✅ Good" :
                          finalIndex >= 40 ? "⚠️ Average" : "❌ Needs Improvement";
            Object[] row = {
                idx.getIndexID(),
                idx.getProject().getProjectID(),
                idx.getProject().getProjectName(),
                String.format("%.2f", finalIndex),
                grade
            };
            tableModel.addRow(row);
        }
        setStatus("Displaying " + NamaaIndexService.getIndexes().size() + " indexes");
    }

    private void setStatus(String message) {
        if (lblStatusMessage != null) {
            lblStatusMessage.setText("Status: " + message);
        }
    }
}