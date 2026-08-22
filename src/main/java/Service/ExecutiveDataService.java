package Service;

import Model.SDGContribution;
import Model.GeographicRegion;
import java.io.*;
import java.util.*;

public class ExecutiveDataService {
    private static ArrayList<SDGContribution> sdgContributions = new ArrayList<>();
    private static ArrayList<GeographicRegion> geographicRegions = new ArrayList<>();
    
    private static final String SDG_FILE = "sdg_data.csv";
    private static final String GEO_FILE = "geographic_data.csv";
    
    static {
        loadSDGData();
        loadGeographicData();
    }
    
    // ===== LOAD SDG DATA =====
    private static void loadSDGData() {
        sdgContributions.clear();
        File file = new File(SDG_FILE);
        
        if (!file.exists()) {
            System.out.println("⚠️ SDG data file not found. Creating default data.");
            createDefaultSDGData();
            return;
        }
        
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            boolean isFirstLine = true;
            
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                if (isFirstLine) {
                    isFirstLine = false;
                    continue;
                }
                
                String[] data = line.split(",");
                if (data.length >= 6) {
                    try {
                        int sdgNumber = Integer.parseInt(data[0].trim());
                        String sdgName = data[1].trim();
                        int projects = Integer.parseInt(data[2].trim());
                        double funding = Double.parseDouble(data[3].trim());
                        int beneficiaries = Integer.parseInt(data[4].trim());
                        double impactScore = Double.parseDouble(data[5].trim());
                        
                        sdgContributions.add(new SDGContribution(
                            sdgNumber, sdgName, projects, funding, beneficiaries, impactScore
                        ));
                    } catch (Exception e) {
                        System.err.println("Error parsing SDG line: " + line);
                    }
                }
            }
            System.out.println("✅ Loaded " + sdgContributions.size() + " SDG contributions");
        } catch (IOException e) {
            System.err.println("Error loading SDG data: " + e.getMessage());
        }
    }
    
    // ===== LOAD GEOGRAPHIC DATA =====
    private static void loadGeographicData() {
        geographicRegions.clear();
        File file = new File(GEO_FILE);
        
        if (!file.exists()) {
            System.out.println("⚠️ Geographic data file not found. Creating default data.");
            createDefaultGeographicData();
            return;
        }
        
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            boolean isFirstLine = true;
            
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                if (isFirstLine) {
                    isFirstLine = false;
                    continue;
                }
                
                String[] data = line.split(",");
                if (data.length >= 6) {
                    try {
                        String region = data[0].trim();
                        int projects = Integer.parseInt(data[1].trim());
                        double funding = Double.parseDouble(data[2].trim());
                        int beneficiaries = Integer.parseInt(data[3].trim());
                        double avgNamaa = Double.parseDouble(data[4].trim());
                        double successRate = Double.parseDouble(data[5].trim());
                        
                        geographicRegions.add(new GeographicRegion(
                            region, projects, funding, beneficiaries, avgNamaa, successRate
                        ));
                    } catch (Exception e) {
                        System.err.println("Error parsing geographic line: " + line);
                    }
                }
            }
            System.out.println("✅ Loaded " + geographicRegions.size() + " geographic regions");
        } catch (IOException e) {
            System.err.println("Error loading geographic data: " + e.getMessage());
        }
    }
    
    // ===== CREATE DEFAULT SDG DATA =====
    private static void createDefaultSDGData() {
        sdgContributions.add(new SDGContribution(1, "No Poverty", 12, 250000, 450, 85));
        sdgContributions.add(new SDGContribution(2, "Zero Hunger", 8, 180000, 320, 78));
        sdgContributions.add(new SDGContribution(3, "Good Health", 10, 220000, 380, 82));
        sdgContributions.add(new SDGContribution(4, "Quality Education", 15, 300000, 520, 88));
        sdgContributions.add(new SDGContribution(5, "Gender Equality", 7, 150000, 280, 76));
        sdgContributions.add(new SDGContribution(8, "Decent Work", 9, 200000, 350, 80));
        sdgContributions.add(new SDGContribution(10, "Reduced Inequality", 6, 120000, 220, 72));
        sdgContributions.add(new SDGContribution(11, "Sustainable Communities", 11, 260000, 410, 84));
        saveSDGData();
    }
    
    // ===== CREATE DEFAULT GEOGRAPHIC DATA =====
    private static void createDefaultGeographicData() {
        geographicRegions.add(new GeographicRegion("Duhail", 18, 450000, 680, 82, 88));
        geographicRegions.add(new GeographicRegion("Al Wakrah", 14, 320000, 520, 78, 85));
        geographicRegions.add(new GeographicRegion("Umm Salal", 10, 250000, 380, 75, 82));
        geographicRegions.add(new GeographicRegion("Al Khor", 8, 180000, 250, 80, 90));
        geographicRegions.add(new GeographicRegion("Al Rayyan", 12, 280000, 420, 76, 84));
        geographicRegions.add(new GeographicRegion("Al Shamal", 5, 120000, 180, 72, 78));
        geographicRegions.add(new GeographicRegion("Al Shahaniya", 6, 150000, 200, 74, 80));
        geographicRegions.add(new GeographicRegion("Mushayrib", 4, 90000, 150, 70, 76));
        saveGeographicData();
    }
    
    // ===== SAVE SDG DATA =====
    public static void saveSDGData() {
        try (FileWriter writer = new FileWriter(SDG_FILE, false)) {
            writer.write("SDGNumber,SDGName,Projects,Funding,Beneficiaries,ImpactScore\n");
            for (SDGContribution s : sdgContributions) {
                writer.write(
                    s.getSdgNumber() + "," +
                    s.getSdgName() + "," +
                    s.getProjects() + "," +
                    s.getFunding() + "," +
                    s.getBeneficiaries() + "," +
                    s.getImpactScore() + "\n"
                );
            }
            System.out.println("✅ Saved " + sdgContributions.size() + " SDG records");
        } catch (IOException e) {
            System.err.println("Error saving SDG data: " + e.getMessage());
        }
    }
    
    // ===== SAVE GEOGRAPHIC DATA =====
    public static void saveGeographicData() {
        try (FileWriter writer = new FileWriter(GEO_FILE, false)) {
            writer.write("Region,Projects,Funding,Beneficiaries,AvgNamaa,SuccessRate\n");
            for (GeographicRegion g : geographicRegions) {
                writer.write(
                    g.getRegion() + "," +
                    g.getProjects() + "," +
                    g.getFunding() + "," +
                    g.getBeneficiaries() + "," +
                    g.getAvgNamaa() + "," +
                    g.getSuccessRate() + "\n"
                );
            }
            System.out.println("✅ Saved " + geographicRegions.size() + " geographic records");
        } catch (IOException e) {
            System.err.println("Error saving geographic data: " + e.getMessage());
        }
    }
    
    // ===== GETTERS =====
    public static ArrayList<SDGContribution> getSDGContributions() {
        return new ArrayList<>(sdgContributions);
    }
    
    public static ArrayList<GeographicRegion> getGeographicRegions() {
        return new ArrayList<>(geographicRegions);
    }
    
    // ===== REFRESH =====
    public static void refreshData() {
        loadSDGData();
        loadGeographicData();
        System.out.println("🔄 Executive data refreshed");
    }
}