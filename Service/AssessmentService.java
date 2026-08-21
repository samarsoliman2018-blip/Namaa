package Service;

import Model.FundingApplication;
import Model.ProjectAssessment;
import java.time.LocalDate;
import java.util.ArrayList;
import java.io.*;

public class AssessmentService {
    private static ArrayList<ProjectAssessment> assessments = new ArrayList<>();
    private static final String FILE = "assessment.csv";
    private static int nextId = 1;

    static {
        loadAssessments();
        if (!assessments.isEmpty()) {
            // Find max ID for auto-increment
            for (ProjectAssessment a : assessments) {
                if (a.getAssessmentID() >= nextId) {
                    nextId = a.getAssessmentID() + 1;
                }
            }
        }
    }

    public static void addAssessment(ProjectAssessment assessment) {
        if (assessment.getAssessmentID() == 0) {
            assessment.setAssessmentID(nextId++);
        }
        assessments.add(assessment);
        saveAllToFile();
    }

    public static ArrayList<ProjectAssessment> getAssessments() {
        return new ArrayList<>(assessments);
    }

    public static ProjectAssessment searchAssessment(int id) {
        for (ProjectAssessment p : assessments) {
            if (p.getAssessmentID() == id)
                return p;
        }
        return null;
    }

    public static ProjectAssessment searchAssessmentByApplication(int applicationId) {
        for (ProjectAssessment p : assessments) {
            if (p.getApplication() != null && 
                p.getApplication().getApplicationID() == applicationId) {
                return p;
            }
        }
        return null;
    }

    public static void updateAssessment(ProjectAssessment assessment) {
        for (int i = 0; i < assessments.size(); i++) {
            if (assessments.get(i).getAssessmentID() == assessment.getAssessmentID()) {
                assessments.set(i, assessment);
                saveAllToFile();
                return;
            }
        }
    }

    public static void deleteAssessment(int id) {
        assessments.removeIf(a -> a.getAssessmentID() == id);
        saveAllToFile();
    }

    public static double getAveragePRI() {
        if (assessments.isEmpty()) return 0;
        double total = 0;
        for (ProjectAssessment a : assessments) {
            total += a.getPriScore();
        }
        return total / assessments.size();
    }

    public static ArrayList<ProjectAssessment> getAssessmentsByRecommendation(String recommendation) {
        ArrayList<ProjectAssessment> result = new ArrayList<>();
        for (ProjectAssessment a : assessments) {
            if (a.getRecommendation().equalsIgnoreCase(recommendation)) {
                result.add(a);
            }
        }
        return result;
    }

    // ===== SAVE ALL: Rewrite entire file =====
    private static void saveAllToFile() {
        try (FileWriter writer = new FileWriter(FILE, false)) {
            writer.write("AssessmentID,ApplicationID,EconomicScore,TechnicalScore,SocialScore,EnvironmentalScore,InnovationScore,PRIScore,Recommendation,AssessmentDate\n");
            
            for (ProjectAssessment assessment : assessments) {
                writer.write(
                    assessment.getAssessmentID() + "," +
                    assessment.getApplication().getApplicationID() + "," +
                    assessment.getEconomicScore() + "," +
                    assessment.getTechnicalScore() + "," +
                    assessment.getSocialScore() + "," +
                    assessment.getEnvironmentalScore() + "," +
                    assessment.getInnovationScore() + "," +
                    assessment.getPriScore() + "," +
                    assessment.getRecommendation() + "," +
                    assessment.getAssessmentDate() + "\n"
                );
            }
            writer.flush();
            System.out.println("Saved " + assessments.size() + " assessments to CSV");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // ===== LOAD from CSV (FIXED - Complete object creation) =====
 // In AssessmentService.java, update the load method:

    private static void loadAssessments() {
        File file = new File(FILE);
        if (!file.exists()) return;
        
        assessments.clear();
        
        try (BufferedReader reader = new BufferedReader(new FileReader(FILE))) {
            String line;
            boolean isFirstLine = true;
            
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                if (isFirstLine) {
                    isFirstLine = false;
                    continue;
                }
                
                String[] data = line.split(",");
                if (data.length >= 10) {
                    try {
                        int assessmentId = Integer.parseInt(data[0].trim());
                        int applicationId = Integer.parseInt(data[1].trim());
                        double economic = Double.parseDouble(data[2].trim());
                        double technical = Double.parseDouble(data[3].trim());
                        double social = Double.parseDouble(data[4].trim());
                        double environmental = Double.parseDouble(data[5].trim());
                        double innovation = Double.parseDouble(data[6].trim());
                        double pri = Double.parseDouble(data[7].trim());
                        String recommendation = data[8].trim();
                        
                        // Parse LocalDate (keep as LocalDate for assessment date)
                        LocalDate date = LocalDate.parse(data[9].trim());
                        
                        // Get the application from FundingService
                        FundingApplication app = FundingService.searchApplication(applicationId);
                        
                        if (app != null) {
                            ProjectAssessment assessment = new ProjectAssessment();
                            assessment.setAssessmentID(assessmentId);
                            assessment.setApplication(app);
                            assessment.setEconomicScore(economic);
                            assessment.setTechnicalScore(technical);
                            assessment.setSocialScore(social);
                            assessment.setEnvironmentalScore(environmental);
                            assessment.setInnovationScore(innovation);
                            assessment.setPriScore(pri);
                            assessment.setRecommendation(recommendation);
                            assessment.setAssessmentDate(date);
                            assessments.add(assessment);
                        } else {
                            System.err.println("Application not found for ID: " + applicationId);
                        }
                    } catch (Exception e) {
                        System.err.println("Error parsing assessment line: " + line);
                        e.printStackTrace();
                    }
                }
            }
            System.out.println("Loaded " + assessments.size() + " assessments from CSV");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static int getNextId() {
        return nextId++;
    }
}