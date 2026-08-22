package Service;

import Model.FundingApplication;
import Model.PeriodicReport;
import Model.PeriodicReport.ReportStatus;
import java.time.LocalDate;
import java.util.ArrayList;
import java.io.*;

public class PeriodicReportService {
    private static ArrayList<PeriodicReport> reports = new ArrayList<>();
    private static final String FILE = "periodic_reports.csv";
    private static int nextId = 1;

    static {
        loadReports();
        if (!reports.isEmpty()) {
            for (PeriodicReport r : reports) {
                if (r.getReportId() >= nextId) {
                    nextId = r.getReportId() + 1;
                }
            }
        }
    }

    public static void submitReport(PeriodicReport report) {
        if (report.getReportId() == 0) {
            report.setReportId(nextId++);
        }
        if (report.getReportDate() == null) {
            report.setReportDate(LocalDate.now());
        }
        if (report.getStatus() == null) {
            report.setStatus(ReportStatus.DRAFT);
        }
        reports.add(report);
        saveAllToFile();
    }

    public static ArrayList<PeriodicReport> getReports() {
        return new ArrayList<>(reports);
    }

    public static ArrayList<PeriodicReport> getReportsByApplication(int applicationId) {
        ArrayList<PeriodicReport> result = new ArrayList<>();
        for (PeriodicReport r : reports) {
            if (r.getApplication() != null && 
                r.getApplication().getApplicationID() == applicationId) {
                result.add(r);
            }
        }
        return result;
    }

    public static ArrayList<PeriodicReport> getReportsByBeneficiary(int beneficiaryId) {
        ArrayList<PeriodicReport> result = new ArrayList<>();
        for (PeriodicReport r : reports) {
            if (r.getApplication() != null && 
                r.getApplication().getBeneficiary() != null &&
                r.getApplication().getBeneficiary().getBeneficiaryID() == beneficiaryId) {
                result.add(r);
            }
        }
        return result;
    }

    public static PeriodicReport getLatestReport(int applicationId) {
        ArrayList<PeriodicReport> appReports = getReportsByApplication(applicationId);
        if (appReports.isEmpty()) return null;
        return appReports.get(appReports.size() - 1);
    }

    public static void updateReport(PeriodicReport report) {
        for (int i = 0; i < reports.size(); i++) {
            if (reports.get(i).getReportId() == report.getReportId()) {
                reports.set(i, report);
                saveAllToFile();
                return;
            }
        }
    }

    public static ArrayList<PeriodicReport> getPendingReviewReports() {
        ArrayList<PeriodicReport> result = new ArrayList<>();
        for (PeriodicReport r : reports) {
            if (r.getStatus() == ReportStatus.SUBMITTED) {
                result.add(r);
            }
        }
        return result;
    }

    public static int getNextId() {
        return nextId++;
    }

    // ===== SAVE ALL (UPDATED WITH TEMPLATE FIELDS) =====
    private static void saveAllToFile() {
        try (FileWriter writer = new FileWriter(FILE, false)) {
            writer.write("ReportID,ApplicationID,ReportDate,ProgressDescription,ProgressPercentage,Status,AdminFeedback,AIScore,AmountSpent,AmountRemaining,RevenueGenerated,ActualBeneficiaries,Achievements,Challenges,FuturePlans\n");
            
            for (PeriodicReport r : reports) {
                writer.write(
                    r.getReportId() + "," +
                    r.getApplication().getApplicationID() + "," +
                    r.getReportDate() + "," +
                    r.getProgressDescription() + "," +
                    r.getProgressPercentage() + "," +
                    r.getStatus().name() + "," +
                    r.getAdminFeedback() + "," +
                    r.getAiAnalysisScore() + "," +
                    r.getAmountSpent() + "," +
                    r.getAmountRemaining() + "," +
                    r.getRevenueGenerated() + "," +
                    r.getActualBeneficiaries() + "," +
                    escapeCSV(r.getAchievements()) + "," +
                    escapeCSV(r.getChallenges()) + "," +
                    escapeCSV(r.getFuturePlans()) + "\n"
                );
            }
            writer.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // ===== LOAD (UPDATED WITH TEMPLATE FIELDS) =====
    private static void loadReports() {
        File file = new File(FILE);
        if (!file.exists()) return;
        
        reports.clear();
        
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
                if (data.length >= 15) {
                    try {
                        int reportId = Integer.parseInt(data[0].trim());
                        int applicationId = Integer.parseInt(data[1].trim());
                        LocalDate reportDate = LocalDate.parse(data[2].trim());
                        String description = data[3].trim();
                        double progress = Double.parseDouble(data[4].trim());
                        ReportStatus status = ReportStatus.valueOf(data[5].trim().toUpperCase());
                        String feedback = data[6].trim();
                        double aiScore = Double.parseDouble(data[7].trim());
                        
                        // ===== NEW TEMPLATE FIELDS =====
                        double amountSpent = Double.parseDouble(data[8].trim());
                        double amountRemaining = Double.parseDouble(data[9].trim());
                        double revenue = Double.parseDouble(data[10].trim());
                        int beneficiaries = Integer.parseInt(data[11].trim());
                        String achievements = data[12].trim();
                        String challenges = data[13].trim();
                        String futurePlans = data[14].trim();
                        
                        FundingApplication app = FundingService.searchApplication(applicationId);
                        
                        if (app != null) {
                            PeriodicReport report = new PeriodicReport();
                            report.setReportId(reportId);
                            report.setApplication(app);
                            report.setReportDate(reportDate);
                            report.setProgressDescription(description);
                            report.setProgressPercentage(progress);
                            report.setStatus(status);
                            report.setAdminFeedback(feedback);
                            report.setAiAnalysisScore(aiScore);
                            
                            // Set template fields
                            report.setAmountSpent(amountSpent);
                            report.setAmountRemaining(amountRemaining);
                            report.setRevenueGenerated(revenue);
                            report.setActualBeneficiaries(beneficiaries);
                            report.setAchievements(achievements);
                            report.setChallenges(challenges);
                            report.setFuturePlans(futurePlans);
                            
                            reports.add(report);
                        }
                    } catch (Exception e) {
                        System.err.println("Error parsing periodic report line: " + line);
                        e.printStackTrace();
                    }
                }
            }
            System.out.println("Loaded " + reports.size() + " periodic reports from CSV");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // ===== CSV HELPER =====
    private static String escapeCSV(String value) {
        if (value == null) return "";
        if (value.contains(",") || value.contains("\"") || value.contains("\n")) {
            return "\"" + value.replace("\"", "\"\"") + "\"";
        }
        return value;
    }
}