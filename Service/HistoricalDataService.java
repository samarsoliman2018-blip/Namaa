package Service;

import Model.HistoricalProject;
import java.io.*;
import java.util.*;

public class HistoricalDataService {
    private static ArrayList<HistoricalProject> projects = new ArrayList<>();
    private static final String FILE = "projects_history.csv";
    private static boolean isLoaded = false;

    static {
        loadHistory();
    }

    // ===== LOAD HISTORICAL DATA =====
    public static void loadHistory() {
        projects.clear();
        File file = new File(FILE);
        if (!file.exists()) {
            // Create default historical data if file doesn't exist
            createDefaultData();
            return;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(FILE))) {
            String line = reader.readLine(); // Skip header
            while ((line = reader.readLine()) != null && !line.trim().isEmpty()) {
                String[] data = line.split(",");
                if (data.length >= 14) {
                    try {
                        HistoricalProject p = new HistoricalProject();
                        p.setProjectId(Integer.parseInt(data[0].trim()));
                        p.setProjectName(data[1].trim());
                        p.setCategory(data[2].trim());
                        p.setActualCost(Double.parseDouble(data[3].trim()));
                        p.setActualDuration(Integer.parseInt(data[4].trim()));
                        p.setBeneficiariesReached(Integer.parseInt(data[5].trim()));
                        p.setSuccessRate(Double.parseDouble(data[6].trim()));
                        p.setLessonsLearned(data[7].trim());
                        p.setEconomicScore(Double.parseDouble(data[8].trim()));
                        p.setSocialScore(Double.parseDouble(data[9].trim()));
                        p.setSustainabilityScore(Double.parseDouble(data[10].trim()));
                        p.setInnovationScore(Double.parseDouble(data[11].trim()));
                        p.setFinalIndex(Double.parseDouble(data[12].trim()));
                        p.setProjectStatus(data[13].trim());
                        p.setCompletionDate(data.length > 14 ? data[14].trim() : "N/A");
                        projects.add(p);
                    } catch (Exception e) {
                        System.err.println("Error parsing line: " + line);
                    }
                }
            }
            System.out.println("Loaded " + projects.size() + " historical projects");
        } catch (IOException e) {
            e.printStackTrace();
        }
        isLoaded = true;
    }

    // ===== CREATE DEFAULT HISTORICAL DATA =====
    private static void createDefaultData() {
        System.out.println("Creating default historical data...");
        
        // Sample projects to start with
        HistoricalProject p1 = new HistoricalProject(
            1, "School Renovation", "Education",
            95000, 11, 150, 0.85, 
            "Community engagement was crucial for success",
            85, 90, 75, 80, 82.5, "Completed", "2025-06-15"
        );
        
        HistoricalProject p2 = new HistoricalProject(
            2, "Farm Irrigation System", "Agriculture",
            48000, 13, 80, 0.92,
            "Seasonal timing and local knowledge are key",
            70, 85, 80, 75, 77.5, "Completed", "2025-08-20"
        );
        
        HistoricalProject p3 = new HistoricalProject(
            3, "Community Health Clinic", "Healthcare",
            120000, 14, 300, 0.78,
            "Need more equipment and trained staff",
            75, 80, 85, 70, 77.5, "Completed", "2025-10-10"
        );
        
        HistoricalProject p4 = new HistoricalProject(
            4, "Women's Vocational Training", "Education",
            60000, 10, 200, 0.95,
            "Partnerships with local NGOs boosted success",
            90, 95, 85, 85, 88.75, "Completed", "2025-12-01"
        );
        
        HistoricalProject p5 = new HistoricalProject(
            5, "Solar Power Initiative", "Technology",
            150000, 16, 500, 0.88,
            "Maintenance planning is critical for sustainability",
            80, 75, 90, 95, 85.0, "Completed", "2026-02-28"
        );
        
        projects.add(p1);
        projects.add(p2);
        projects.add(p3);
        projects.add(p4);
        projects.add(p5);
        
        saveAllToFile();
        System.out.println("Created " + projects.size() + " default historical projects");
    }

    // ===== SAVE ALL TO FILE =====
    public static void saveAllToFile() {
        try (FileWriter writer = new FileWriter(FILE, false)) {
            writer.write("ProjectID,ProjectName,Category,ActualCost,ActualDuration,BeneficiariesReached,SuccessRate,LessonsLearned,EconomicScore,SocialScore,SustainabilityScore,InnovationScore,FinalIndex,Status,CompletionDate\n");
            
            for (HistoricalProject p : projects) {
                writer.write(
                    p.getProjectId() + "," +
                    p.getProjectName() + "," +
                    p.getCategory() + "," +
                    p.getActualCost() + "," +
                    p.getActualDuration() + "," +
                    p.getBeneficiariesReached() + "," +
                    p.getSuccessRate() + "," +
                    p.getLessonsLearned() + "," +
                    p.getEconomicScore() + "," +
                    p.getSocialScore() + "," +
                    p.getSustainabilityScore() + "," +
                    p.getInnovationScore() + "," +
                    p.getFinalIndex() + "," +
                    p.getProjectStatus() + "," +
                    p.getCompletionDate() + "\n"
                );
            }
            System.out.println("Saved " + projects.size() + " projects to history file");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // ===== ADD PROJECT TO HISTORY =====
    public static void addHistoricalProject(HistoricalProject project) {
        projects.add(project);
        saveAllToFile();
    }

    // ===== GET ALL PROJECTS =====
    public static ArrayList<HistoricalProject> getAllProjects() {
        return projects;
    }

    // ===== GET PROJECTS BY CATEGORY =====
    public static ArrayList<HistoricalProject> getProjectsByCategory(String category) {
        ArrayList<HistoricalProject> result = new ArrayList<>();
        for (HistoricalProject p : projects) {
            if (p.getCategory().equalsIgnoreCase(category)) {
                result.add(p);
            }
        }
        return result;
    }

    // ===== GET PROJECT BY ID =====
    public static HistoricalProject getProjectById(int id) {
        for (HistoricalProject p : projects) {
            if (p.getProjectId() == id) {
                return p;
            }
        }
        return null;
    }

    // ===== GET BEST PRACTICES BY CATEGORY =====
    public static String getBestPractices(String category) {
        StringBuilder practices = new StringBuilder();
        practices.append("BEST PRACTICES FROM PAST PROJECTS");
        if (!category.equals("All")) {
            practices.append(" (" + category + ")");
        }
        practices.append("\n");
        practices.append("─────────────────────────────────────────────\n\n");
        
        int count = 0;
        for (HistoricalProject p : projects) {
            if ((category.equals("All") || p.getCategory().equalsIgnoreCase(category)) 
                    && p.getSuccessRate() >= 0.80) {
                practices.append("✓ " + p.getProjectName() + " (Success Rate: " + 
                               String.format("%.0f", p.getSuccessRate() * 100) + "%)\n");
                practices.append("  Lesson: " + p.getLessonsLearned() + "\n\n");
                count++;
            }
        }
        
        if (count == 0) {
            practices.append("No projects found with success rate >= 80% in this category.\n");
        }
        
        return practices.toString();
    }

    // ===== GET BENCHMARKS BY CATEGORY =====
    public static String getBenchmarks(String category) {
        StringBuilder benchmarks = new StringBuilder();
        benchmarks.append("BENCHMARKS FOR " + category.toUpperCase() + " PROJECTS\n");
        benchmarks.append("─────────────────────────────────────────────\n\n");
        
        double totalCost = 0;
        int totalDuration = 0;
        int totalBeneficiaries = 0;
        double totalIndex = 0;
        double totalSuccess = 0;
        int count = 0;
        
        for (HistoricalProject p : projects) {
            if (p.getCategory().equalsIgnoreCase(category)) {
                totalCost += p.getActualCost();
                totalDuration += p.getActualDuration();
                totalBeneficiaries += p.getBeneficiariesReached();
                totalIndex += p.getFinalIndex();
                totalSuccess += p.getSuccessRate();
                count++;
            }
        }
        
        if (count > 0) {
            benchmarks.append("Number of Projects:  " + count + "\n");
            benchmarks.append("Average Cost:        " + String.format("%,.2f", totalCost/count) + " SAR\n");
            benchmarks.append("Average Duration:    " + (totalDuration/count) + " months\n");
            benchmarks.append("Average Beneficiaries: " + (totalBeneficiaries/count) + "\n");
            benchmarks.append("Average Namaa Index: " + String.format("%.2f", totalIndex/count) + "\n");
            benchmarks.append("Average Success Rate: " + String.format("%.0f", (totalSuccess/count) * 100) + "%\n");
        } else {
            benchmarks.append("No historical data available for this category.\n");
        }
        
        return benchmarks.toString();
    }

    // ===== PREDICT SUCCESS RATE =====
    public static double predictSuccessRate(String category, double estimatedCost) {
        double totalSuccess = 0;
        int count = 0;
        
        for (HistoricalProject p : projects) {
            if (p.getCategory().equalsIgnoreCase(category)) {
                // Projects within +/- 30% of estimated budget
                if (estimatedCost >= p.getActualCost() * 0.7 && 
                    estimatedCost <= p.getActualCost() * 1.3) {
                    totalSuccess += p.getSuccessRate();
                    count++;
                }
            }
        }
        
        if (count > 0) {
            return totalSuccess / count;
        }
        
        // Fallback: average success rate for all projects in category
        for (HistoricalProject p : projects) {
            if (p.getCategory().equalsIgnoreCase(category)) {
                totalSuccess += p.getSuccessRate();
                count++;
            }
        }
        
        return count > 0 ? totalSuccess / count : 0.5;
    }

    // ===== GET RECOMMENDATIONS FOR NEW PROJECT =====
    public static String getRecommendations(String category, double budget, String sector) {
        StringBuilder recommendations = new StringBuilder();
        recommendations.append("📋 RECOMMENDATIONS FOR NEW PROJECT\n");
        recommendations.append("─────────────────────────────────────────────\n\n");
        
        // Get benchmarks
        recommendations.append("Based on " + category + " projects:\n");
        recommendations.append(getBenchmarks(category));
        recommendations.append("\n");
        
        // Get best practices
        recommendations.append("RECOMMENDED BEST PRACTICES:\n");
        recommendations.append(getBestPractices(category));
        recommendations.append("\n");
        
        // Success rate prediction
        double successRate = predictSuccessRate(category, budget);
        recommendations.append("📈 SUCCESS PREDICTION:\n");
        recommendations.append("─────────────────────────────────────────────\n");
        recommendations.append("Estimated Success Probability: " + String.format("%.0f", successRate * 100) + "%\n");
        
        if (successRate >= 0.80) {
            recommendations.append("✅ This project has HIGH probability of success.\n");
            recommendations.append("   Proceed with confidence.\n");
        } else if (successRate >= 0.60) {
            recommendations.append("⚠️ This project has MODERATE probability of success.\n");
            recommendations.append("   Consider addressing risks before proceeding.\n");
        } else {
            recommendations.append("❌ This project has LOW probability of success.\n");
            recommendations.append("   Significant improvements needed.\n");
        }
        
        return recommendations.toString();
    }

    // ===== GET CATEGORIES LIST =====
    public static String[] getCategories() {
        Set<String> categorySet = new HashSet<>();
        categorySet.add("All");
        for (HistoricalProject p : projects) {
            categorySet.add(p.getCategory());
        }
        return categorySet.toArray(new String[0]);
    }

    // ===== REFRESH DATA =====
    public static void refresh() {
        loadHistory();
    }
}