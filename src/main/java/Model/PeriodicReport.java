package Model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class PeriodicReport {
    
    // ===== ENUM =====
    public enum ReportStatus {
        DRAFT, SUBMITTED, REVIEWED, APPROVED, REJECTED, ARCHIVED
    }
    
    // ===== EXISTING FIELDS =====
    private int reportId;
    private FundingApplication application;
    private LocalDate reportDate;
    private String progressDescription;
    private List<String> imageUrls;
    private double progressPercentage;
    private ReportStatus status;
    private String adminFeedback;
    private double aiAnalysisScore;
    
    // ===== NEW TEMPLATE FIELDS =====
    private double amountSpent;           // المبلغ المنفق
    private double amountRemaining;       // المبلغ المتبقي
    private double revenueGenerated;      // الإيرادات المحققة
    private int actualBeneficiaries;      // المستفيدين الفعليين
    private String achievements;          // الإنجازات
    private String challenges;            // التحديات
    private String futurePlans;           // الخطط المستقبلية

    // ===== CONSTRUCTORS =====
    public PeriodicReport() {
        this.reportDate = LocalDate.now();
        this.status = ReportStatus.DRAFT;
        this.imageUrls = new ArrayList<>();
        this.progressPercentage = 0;
        this.aiAnalysisScore = 0;
        this.amountSpent = 0;
        this.amountRemaining = 0;
        this.revenueGenerated = 0;
        this.actualBeneficiaries = 0;
        this.achievements = "";
        this.challenges = "";
        this.futurePlans = "";
    }

    public PeriodicReport(int reportId, FundingApplication application, LocalDate reportDate,
                         String progressDescription, List<String> imageUrls, double progressPercentage,
                         ReportStatus status, String adminFeedback, double aiAnalysisScore) {
        this.reportId = reportId;
        this.application = application;
        this.reportDate = reportDate;
        this.progressDescription = progressDescription;
        this.imageUrls = imageUrls != null ? imageUrls : new ArrayList<>();
        this.progressPercentage = progressPercentage;
        this.status = status;
        this.adminFeedback = adminFeedback;
        this.aiAnalysisScore = aiAnalysisScore;
        this.amountSpent = 0;
        this.amountRemaining = 0;
        this.revenueGenerated = 0;
        this.actualBeneficiaries = 0;
    }

    // ===== GETTERS AND SETTERS (EXISTING) =====
    public int getReportId() { return reportId; }
    public void setReportId(int reportId) { this.reportId = reportId; }

    public FundingApplication getApplication() { return application; }
    public void setApplication(FundingApplication application) { this.application = application; }

    public LocalDate getReportDate() { return reportDate; }
    public void setReportDate(LocalDate reportDate) { this.reportDate = reportDate; }

    public String getProgressDescription() { return progressDescription; }
    public void setProgressDescription(String progressDescription) { this.progressDescription = progressDescription; }

    public List<String> getImageUrls() { return imageUrls; }
    public void setImageUrls(List<String> imageUrls) { this.imageUrls = imageUrls; }
    public void addImageUrl(String imageUrl) { this.imageUrls.add(imageUrl); }

    public double getProgressPercentage() { return progressPercentage; }
    public void setProgressPercentage(double progressPercentage) { this.progressPercentage = progressPercentage; }

    public ReportStatus getStatus() { return status; }
    public void setStatus(ReportStatus status) { this.status = status; }
    
    // ===== FIXED: setStatus(String) method for backward compatibility =====
    public void setStatus(String statusStr) {
        try {
            this.status = ReportStatus.valueOf(statusStr.toUpperCase());
        } catch (IllegalArgumentException e) {
            this.status = ReportStatus.DRAFT;
        }
    }

    public String getAdminFeedback() { return adminFeedback; }
    public void setAdminFeedback(String adminFeedback) { this.adminFeedback = adminFeedback; }

    public double getAiAnalysisScore() { return aiAnalysisScore; }
    public void setAiAnalysisScore(double aiAnalysisScore) { this.aiAnalysisScore = aiAnalysisScore; }

    // ===== NEW TEMPLATE GETTERS AND SETTERS =====
    public double getAmountSpent() { return amountSpent; }
    public void setAmountSpent(double amountSpent) { this.amountSpent = amountSpent; }
    
    public double getAmountRemaining() { return amountRemaining; }
    public void setAmountRemaining(double amountRemaining) { this.amountRemaining = amountRemaining; }
    
    public double getRevenueGenerated() { return revenueGenerated; }
    public void setRevenueGenerated(double revenueGenerated) { this.revenueGenerated = revenueGenerated; }
    
    public int getActualBeneficiaries() { return actualBeneficiaries; }
    public void setActualBeneficiaries(int actualBeneficiaries) { this.actualBeneficiaries = actualBeneficiaries; }
    
    public String getAchievements() { return achievements; }
    public void setAchievements(String achievements) { this.achievements = achievements; }
    
    public String getChallenges() { return challenges; }
    public void setChallenges(String challenges) { this.challenges = challenges; }
    
    public String getFuturePlans() { return futurePlans; }
    public void setFuturePlans(String futurePlans) { this.futurePlans = futurePlans; }

    // ===== HELPER METHODS =====
    public boolean isSubmitted() { return status == ReportStatus.SUBMITTED; }
    public boolean isApproved() { return status == ReportStatus.APPROVED; }
    public boolean isDraft() { return status == ReportStatus.DRAFT; }
    
    public void submit() { this.status = ReportStatus.SUBMITTED; }
    public void review() { this.status = ReportStatus.REVIEWED; }
    public void approve() { this.status = ReportStatus.APPROVED; }
    public void reject() { this.status = ReportStatus.REJECTED; }
    
    public double getUtilizationRate() {
        double total = amountSpent + amountRemaining;
        return total > 0 ? (amountSpent / total) * 100 : 0;
    }
    
    public double getRevenueToCostRatio() {
        return amountSpent > 0 ? revenueGenerated / amountSpent : 0;
    }

    // ===== GENERATE AI ANALYSIS =====
    public String generateAIAnalysis() {
        if (application == null) {
            return "⚠️ No application linked to this report.";
        }
        
        StringBuilder sb = new StringBuilder();
        sb.append("📊 AI ANALYSIS - PERIODIC REPORT\n");
        sb.append("─────────────────────────────────────────────\n\n");
        sb.append("Project: ").append(application.getProject().getProjectName()).append("\n");
        sb.append("Sector: ").append(application.getProject().getSector()).append("\n");
        sb.append("Progress: ").append(String.format("%.1f%%", progressPercentage)).append("\n");
        sb.append("Amount Spent: ").append(String.format("%.2f QR", amountSpent)).append("\n");
        sb.append("Amount Remaining: ").append(String.format("%.2f QR", amountRemaining)).append("\n");
        sb.append("Revenue Generated: ").append(String.format("%.2f QR", revenueGenerated)).append("\n");
        sb.append("Beneficiaries Reached: ").append(actualBeneficiaries).append("\n\n");
        
        // Performance assessment
        sb.append("📈 PERFORMANCE ASSESSMENT:\n");
        double totalBudget = amountSpent + amountRemaining;
        double spentRatio = totalBudget > 0 ? (amountSpent / totalBudget) * 100 : 0;

        if (progressPercentage >= 80 && spentRatio <= 80) {
            sb.append("✅ Project is on track and within budget.\n");
        } else if (progressPercentage < 50 && spentRatio > 60) {
            sb.append("⚠️ Budget may be insufficient for remaining work.\n");
        } else if (progressPercentage >= 80 && spentRatio > 90) {
            sb.append("⚠️ Project nearly complete, budget almost fully utilized.\n");
        } else {
            sb.append("ℹ️ Project is progressing as expected.\n");
        }
        
        // Revenue analysis
        if (revenueGenerated > 0) {
            double roi = amountSpent > 0 ? (revenueGenerated / amountSpent) * 100 : 0;
            sb.append("\n💰 REVENUE ANALYSIS:\n");
            sb.append("   ROI: ").append(String.format("%.1f%%", roi)).append("\n");
            if (roi > 100) {
                sb.append("   ✅ Excellent return on investment.\n");
            } else if (roi > 50) {
                sb.append("   ✅ Good return on investment.\n");
            } else {
                sb.append("   ℹ️ Revenue generation needs improvement.\n");
            }
        }
        
        // Achievements
        if (achievements != null && !achievements.isEmpty()) {
            sb.append("\n✅ ACHIEVEMENTS:\n").append(achievements).append("\n");
        }
        
        // Challenges
        if (challenges != null && !challenges.isEmpty()) {
            sb.append("\n⚠️ CHALLENGES:\n").append(challenges).append("\n");
        }
        
        // Future Plans
        if (futurePlans != null && !futurePlans.isEmpty()) {
            sb.append("\n📋 FUTURE PLANS:\n").append(futurePlans).append("\n");
        }
        
        // Recommendations
        sb.append("\n💡 RECOMMENDATIONS:\n");
        if (progressPercentage < 50 && amountSpent > 0) {
            sb.append("   • Review project timeline and resource allocation.\n");
        }
        if (challenges != null && !challenges.isEmpty()) {
            sb.append("   • Address challenges proactively with committee support.\n");
        }
        if (progressPercentage >= 80) {
            sb.append("   • Plan for project completion and documentation.\n");
        }
        sb.append("   • Continue regular reporting for accurate tracking.\n");
        sb.append("─────────────────────────────────────────────");
        
        return sb.toString();
    }

    @Override
    public String toString() {
        return "PeriodicReport{" +
                "reportId=" + reportId +
                ", application=" + (application != null ? application.getApplicationID() : "null") +
                ", reportDate=" + reportDate +
                ", progressPercentage=" + progressPercentage +
                ", status=" + status +
                ", amountSpent=" + amountSpent +
                ", revenueGenerated=" + revenueGenerated +
                ", actualBeneficiaries=" + actualBeneficiaries +
                '}';
    }
}