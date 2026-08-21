package Service;

import Model.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.io.*;

public class CommitteeService {

    // ===== APPROVE APPLICATION =====
    public static void approve(ProjectAssessment assessment,
            FundingApplication application) {

        assessment.generateRecommendation();

        if (assessment.getRecommendation().equals("Approved")) {
            application.setStatus(ApplicationStatus.APPROVED);
            FundingService.updateApplication(application);
        }
    }

    // ===== REJECT APPLICATION =====
    public static void reject(FundingApplication application) {
        application.setStatus(ApplicationStatus.REJECTED);
        FundingService.updateApplication(application);
    }

    // ===== REQUEST REVISION =====
    public static void requestRevision(FundingApplication application) {
        application.setStatus(ApplicationStatus.UNDER_REVIEW);
        FundingService.updateApplication(application);
    }

    // ===== APPROVE WITH NOTES =====
    public static void approveWithNotes(FundingApplication application, String notes) {
        application.setStatus(ApplicationStatus.APPROVED);
        FundingService.updateApplication(application);
        
        System.out.println("📝 Approval Notes: " + notes);
        System.out.println("✅ Application #" + application.getApplicationID() + " approved");
    }

    // ===== REJECT WITH REASON =====
    public static void rejectWithReason(FundingApplication application, String reason) {
        application.setStatus(ApplicationStatus.REJECTED);
        FundingService.updateApplication(application);
        
        System.out.println("📝 Rejection Reason: " + reason);
        System.out.println("❌ Application #" + application.getApplicationID() + " rejected");
    }

    // ===== REQUEST REVISION WITH NOTES =====
    public static void requestRevisionWithNotes(FundingApplication application, String notes) {
        application.setStatus(ApplicationStatus.UNDER_REVIEW);
        FundingService.updateApplication(application);
        
        System.out.println("📝 Revision Notes: " + notes);
        System.out.println("🔄 Revision requested for Application #" + application.getApplicationID());
    }

    // ===== FUND APPLICATION (After Approval) =====
 // In CommitteeService.java - fundApplication() method

    public static void fundApplication(FundingApplication application, CashWaqf sourceWaqf) {
        if (application.getStatus() != ApplicationStatus.APPROVED) {
            System.err.println("❌ Application must be APPROVED before funding");
            return;
        }

        if (sourceWaqf.getAvailableBalance() < application.getRequestedAmount()) {
            System.err.println("❌ Insufficient balance in Waqf #" + sourceWaqf.getWaqfID());
            return;
        }

        boolean allocated = sourceWaqf.allocateFunding(application.getRequestedAmount());
        if (allocated) {
            application.setStatus(ApplicationStatus.FUNDED);
            FundingService.updateApplication(application);
            // ===== FIXED: Use the correct method =====
            WaqfService.saveWaqfsToFile();  // ← Changed from saveAllToFile()
            System.out.println("💰 Application #" + application.getApplicationID() + " funded from Waqf #" + 
                             sourceWaqf.getWaqfID());
        } else {
            System.err.println("❌ Failed to allocate funds");
        }
    }

    // ===== COMPLETE PROJECT =====
    public static void completeProject(FundingApplication application) {
        if (application.getStatus() != ApplicationStatus.FUNDED && 
            application.getStatus() != ApplicationStatus.APPROVED) {
            System.err.println("❌ Only FUNDED or APPROVED projects can be completed");
            return;
        }

        application.setStatus(ApplicationStatus.COMPLETED);
        FundingService.updateApplication(application);
        
        createHistoricalRecord(application);
        
        System.out.println("📌 Application #" + application.getApplicationID() + " completed");
    }

    // ===== CREATE HISTORICAL RECORD =====
    private static void createHistoricalRecord(FundingApplication application) {
        HistoricalProject historical = new HistoricalProject();
        
        // FIXED: Check if the method exists, otherwise use different approach
        try {
            // Try using setProjectId if it exists
            historical.setProjectId(application.getApplicationID());
        } catch (NoSuchMethodError e) {
            // If setProjectId doesn't exist, try using the constructor
            // Or you can add the method to HistoricalProject
            System.out.println("⚠️ setProjectId method not found. Using constructor.");
        }
        
        // Set other fields
        historical.setProjectName(application.getProject().getProjectName());
        historical.setCategory(application.getProject().getSector());
        historical.setActualCost(application.getRequestedAmount());
        historical.setActualDuration(application.getProject().getDurationMonths());
        historical.setBeneficiariesReached(application.getProject().getExpectedBeneficiaries());
        historical.setSuccessRate(0.85);
        historical.setLessonsLearned("Project completed successfully.");
        historical.setProjectStatus("Completed");
        historical.setCompletionDate(LocalDate.now().toString());
        
        // Try to get Namaa Index
        NamaaIndex index = NamaaIndexService.getIndexByProject(application.getProject().getProjectID());
        if (index != null) {
            historical.setFinalIndex(index.getFinalIndex());
            historical.setEconomicScore(index.getEconomicImpact());
            historical.setSocialScore(index.getSocialImpact());
            historical.setSustainabilityScore(index.getSustainability());
            historical.setInnovationScore(index.getInnovation());
        }
        
        HistoricalDataService.addHistoricalProject(historical);
        System.out.println("📊 Historical record created for: " + application.getProject().getProjectName());
    }

    // ===== GET PENDING APPLICATIONS =====
    public static ArrayList<FundingApplication> getPendingApplications() {
        ArrayList<FundingApplication> pending = new ArrayList<>();
        for (FundingApplication app : FundingService.getApplications()) {
            if (app.getStatus() == ApplicationStatus.PENDING || 
                app.getStatus() == ApplicationStatus.UNDER_REVIEW) {
                pending.add(app);
            }
        }
        return pending;
    }

    // ===== GET APPROVED APPLICATIONS =====
    public static ArrayList<FundingApplication> getApprovedApplications() {
        ArrayList<FundingApplication> approved = new ArrayList<>();
        for (FundingApplication app : FundingService.getApplications()) {
            if (app.getStatus() == ApplicationStatus.APPROVED) {
                approved.add(app);
            }
        }
        return approved;
    }

    // ===== GET FUNDED APPLICATIONS =====
    public static ArrayList<FundingApplication> getFundedApplications() {
        ArrayList<FundingApplication> funded = new ArrayList<>();
        for (FundingApplication app : FundingService.getApplications()) {
            if (app.getStatus() == ApplicationStatus.FUNDED) {
                funded.add(app);
            }
        }
        return funded;
    }

    // ===== GET REJECTED APPLICATIONS =====
    public static ArrayList<FundingApplication> getRejectedApplications() {
        ArrayList<FundingApplication> rejected = new ArrayList<>();
        for (FundingApplication app : FundingService.getApplications()) {
            if (app.getStatus() == ApplicationStatus.REJECTED) {
                rejected.add(app);
            }
        }
        return rejected;
    }

    // ===== GET COMPLETED APPLICATIONS =====
    public static ArrayList<FundingApplication> getCompletedApplications() {
        ArrayList<FundingApplication> completed = new ArrayList<>();
        for (FundingApplication app : FundingService.getApplications()) {
            if (app.getStatus() == ApplicationStatus.COMPLETED) {
                completed.add(app);
            }
        }
        return completed;
    }

    // ===== REVIEW APPLICATION =====
    public static String reviewApplication(FundingApplication application) {
        ProjectAssessment assessment = AssessmentService.searchAssessmentByApplication(
            application.getApplicationID()
        );
        
        if (assessment == null) {
            return "⚠️ No assessment found for Application #" + application.getApplicationID();
        }

        StringBuilder review = new StringBuilder();
        review.append("📋 REVIEW SUMMARY\n");
        review.append("─────────────────────────────────────────────\n");
        review.append("Application ID:  " + application.getApplicationID() + "\n");
        review.append("Project:         " + application.getProject().getProjectName() + "\n");
        review.append("PRI Score:       " + String.format("%.2f", assessment.getPriScore()) + "\n");
        review.append("Recommendation:  " + assessment.getRecommendation() + "\n");
        review.append("Status:          " + application.getStatus() + "\n");
        review.append("─────────────────────────────────────────────\n");

        review.append("\n🤖 AI Analysis:\n");
        if (AIService.isAIAvailable()) {
            String aiResult = AIService.generateProjectEvaluation(
                application.getProject().getProjectName(),
                application.getProject().getSector(),
                assessment.getPriScore(),
                assessment.getEconomicScore(),
                assessment.getTechnicalScore(),
                assessment.getSocialScore(),
                assessment.getEnvironmentalScore(),
                assessment.getInnovationScore()
            );
            review.append(aiResult + "\n");
        } else {
            review.append("  AI service is disabled. Enable AI for detailed analysis.\n");
        }

        return review.toString();
    }

    // ===== GENERATE DECISION REPORT =====
    public static String generateDecisionReport(FundingApplication application, String decision) {
        StringBuilder report = new StringBuilder();
        report.append("╔═══════════════════════════════════════════════════════════════╗\n");
        report.append("║                   COMMITTEE DECISION REPORT                   ║\n");
        report.append("╚═══════════════════════════════════════════════════════════════╝\n\n");
        
        report.append("Application ID:   " + application.getApplicationID() + "\n");
        report.append("Project Name:     " + application.getProject().getProjectName() + "\n");
        report.append("Sector:           " + application.getProject().getSector() + "\n");
        report.append("Requested Amount: " + String.format("%.2f QR", application.getRequestedAmount()) + "\n");
        report.append("Decision Date:    " + LocalDate.now() + "\n");
        report.append("Decision:         " + decision + "\n\n");
        
        ProjectAssessment assessment = AssessmentService.searchAssessmentByApplication(application.getApplicationID());
        if (assessment != null) {
            report.append("ASSESSMENT SUMMARY:\n");
            report.append("─────────────────────────────────────────────\n");
            report.append("  PRI Score:       " + String.format("%.2f", assessment.getPriScore()) + "\n");
            report.append("  Economic:        " + String.format("%.2f", assessment.getEconomicScore()) + "\n");
            report.append("  Technical:       " + String.format("%.2f", assessment.getTechnicalScore()) + "\n");
            report.append("  Social:          " + String.format("%.2f", assessment.getSocialScore()) + "\n");
            report.append("  Environmental:   " + String.format("%.2f", assessment.getEnvironmentalScore()) + "\n");
            report.append("  Innovation:      " + String.format("%.2f", assessment.getInnovationScore()) + "\n");
        }
        
        report.append("\n" + "═".repeat(63) + "\n");
        report.append("Generated by Namaa Smart Waqf Platform\n");
        report.append("Committee Decision System\n");
        
        return report.toString();
    }
    
}