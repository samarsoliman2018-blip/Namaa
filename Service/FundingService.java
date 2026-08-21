package Service;

import Model.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.io.*;

public class FundingService {
    private static ArrayList<FundingApplication> applications = new ArrayList<>();
    private static final String FILE = "funding_applications.csv";
    private static int nextId = 1;
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    static {
        loadApplications();
        if (!applications.isEmpty()) {
            for (FundingApplication app : applications) {
                if (app.getApplicationID() >= nextId) {
                    nextId = app.getApplicationID() + 1;
                }
            }
        }
    }

 // In FundingService.java - submitApplication() method

    public static void submitApplication(FundingApplication app) {
        if (app.getApplicationID() == 0) {
            app.setApplicationID(nextId++);
        }
        if (app.getApplicationDate() == null) {
            app.setApplicationDate(LocalDateTime.now());
        }
        
        // ===== FIXED: Also save the project to projects.csv =====
        Project project = app.getProject();
        if (project != null) {
            ProjectService.addProject(project);
            System.out.println("✅ Project saved to projects.csv: " + project.getProjectName());
        }
        
        applications.add(app);
        saveAllToFile();
        System.out.println("✅ Application #" + app.getApplicationID() + " submitted and saved");
    }

    public static ArrayList<FundingApplication> getApplications() {
        return new ArrayList<>(applications);
    }

    public static FundingApplication searchApplication(int id) {
        for (FundingApplication app : applications) {
            if (app.getApplicationID() == id)
                return app;
        }
        return null;
    }

    public static void approveApplication(int id) {
        FundingApplication app = searchApplication(id);
        if (app != null) {
            app.approve();
            saveAllToFile();
        }
    }

    public static void rejectApplication(int id) {
        FundingApplication app = searchApplication(id);
        if (app != null) {
            app.reject();
            saveAllToFile();
        }
    }

    public static void updateApplication(FundingApplication updatedApp) {
        for (int i = 0; i < applications.size(); i++) {
            if (applications.get(i).getApplicationID() == updatedApp.getApplicationID()) {
                applications.set(i, updatedApp);
                saveAllToFile();
                return;
            }
        }
    }

    public static void deleteApplication(int id) {
        applications.removeIf(a -> a.getApplicationID() == id);
        saveAllToFile();
    }

    public static ArrayList<FundingApplication> getApplicationsByBeneficiary(int beneficiaryId) {
        ArrayList<FundingApplication> result = new ArrayList<>();
        for (FundingApplication app : applications) {
            if (app.getBeneficiary() != null && 
                app.getBeneficiary().getBeneficiaryID() == beneficiaryId) {
                result.add(app);
            }
        }
        return result;
    }

    public static ArrayList<FundingApplication> getApplicationsByStatus(ApplicationStatus status) {
        ArrayList<FundingApplication> result = new ArrayList<>();
        for (FundingApplication app : applications) {
            if (app.getStatus() == status) {
                result.add(app);
            }
        }
        return result;
    }

    public static ArrayList<FundingApplication> getPendingApplications() {
        return getApplicationsByStatus(ApplicationStatus.PENDING);
    }

    public static ArrayList<FundingApplication> getApprovedApplications() {
        return getApplicationsByStatus(ApplicationStatus.APPROVED);
    }

    public static int getNextId() {
        return nextId++;
    }

    // ===== SAVE ALL =====
 // In FundingService.java - Update saveAllToFile()

    private static void saveAllToFile() {
        try (FileWriter writer = new FileWriter(FILE, false)) {
            writer.write("ApplicationID,BeneficiaryID,ProjectID,ApplicationDate,RequestedAmount,Status,BusinessPlanFile,FinancialStatementsFile,SupportingDocumentsFile\n");
            
            for (FundingApplication app : applications) {
                writer.write(
                    app.getApplicationID() + "," +
                    app.getBeneficiary().getBeneficiaryID() + "," +
                    app.getProject().getProjectID() + "," +
                    app.getApplicationDate().format(DATE_FORMATTER) + "," +
                    app.getRequestedAmount() + "," +
                    app.getStatus().name() + "," +
                    (app.getBusinessPlanFile() != null ? app.getBusinessPlanFile() : "") + "," +
                    (app.getFinancialStatementsFile() != null ? app.getFinancialStatementsFile() : "") + "," +
                    (app.getSupportingDocumentsFile() != null ? app.getSupportingDocumentsFile() : "") + "\n"
                );
            }
            writer.flush();
            System.out.println("Saved " + applications.size() + " applications to CSV");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

   

    // ===== LOAD =====
    private static void loadApplications() {
        File file = new File(FILE);
        if (!file.exists()) return;
        
        applications.clear();
        
        try (BufferedReader reader = new BufferedReader(new FileReader(FILE))) {
            String line;
            boolean isFirstLine = true;
            
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                if (isFirstLine) {
                    isFirstLine = false;
                    continue;
                }
                
                String[] data = line.split(",", -1);
                if (data.length >= 6) {
                    try {
                        int appId = Integer.parseInt(data[0].trim());
                        int beneficiaryId = Integer.parseInt(data[1].trim());
                        int projectId = Integer.parseInt(data[2].trim());
                        LocalDateTime dateTime = LocalDateTime.parse(data[3].trim(), DATE_FORMATTER);
                        double requestedAmount = Double.parseDouble(data[4].trim());
                        ApplicationStatus status = ApplicationStatus.valueOf(data[5].trim().toUpperCase());
                        
                        Beneficiary beneficiary = (Beneficiary) UserService.searchUserById(beneficiaryId);
                        Project project = ProjectService.searchProject(projectId);
                        
                        if (beneficiary != null && project != null) {
                            FundingApplication app = new FundingApplication();
                            app.setApplicationID(appId);
                            app.setBeneficiary(beneficiary);
                            app.setProject(project);
                            app.setApplicationDate(dateTime);
                            app.setRequestedAmount(requestedAmount);
                            app.setStatus(status);
                            
                            // ===== FIXED: Use the correct field names (getBusinessPlanFile, etc.) =====
                            if (data.length > 6) {
                                String bpFile = data[6].trim();
                                if (!bpFile.isEmpty()) {
                                    app.setBusinessPlanFile(bpFile);
                                }
                            }
                            if (data.length > 7) {
                                String fsFile = data[7].trim();
                                if (!fsFile.isEmpty()) {
                                    app.setFinancialStatementsFile(fsFile);
                                }
                            }
                            if (data.length > 8) {
                                String sdFile = data[8].trim();
                                if (!sdFile.isEmpty()) {
                                    app.setSupportingDocumentsFile(sdFile);
                                }
                            }
                            
                            applications.add(app);
                        }
                    } catch (Exception e) {
                        System.err.println("Error parsing application line: " + line);
                        e.printStackTrace();
                    }
                }
            }
            System.out.println("Loaded " + applications.size() + " applications from CSV");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static LocalDateTime parseDateTime(String dateStr) {
        try {
            return LocalDateTime.parse(dateStr.trim(), DATE_FORMATTER);
        } catch (Exception e) {
            try {
                LocalDate date = LocalDate.parse(dateStr.trim());
                return date.atStartOfDay();
            } catch (Exception ex) {
                System.err.println("Could not parse date: " + dateStr);
                return LocalDateTime.now();
            }
        }
    }
}