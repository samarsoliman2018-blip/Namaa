package Service;

import Model.*;
import java.time.LocalDate;
import java.util.*;

public class DashboardService {
    
    // ===== WAQIF DASHBOARD =====
    public static Map<String, Object> getWaqifDashboard(int waqifId) {
        Map<String, Object> dashboard = new LinkedHashMap<>();
        
        // Get all waqfs by this waqif
        ArrayList<CashWaqf> waqfs = WaqfService.getWaqfsByWaqif(waqifId);
        
        // Get donations
        ArrayList<WaqfDonation> donations = WaqfDonationService.getDonationsByWaqif(waqifId);
        
        dashboard.put("waqifs", waqfs);
        dashboard.put("totalDonated", WaqfDonationService.getTotalDonationsByWaqif(waqifId));
        dashboard.put("totalBeneficiariesReached", calculateTotalBeneficiaries(waqfs));
        dashboard.put("averageNamaaIndex", calculateAverageNamaaIndex(waqfs));
        dashboard.put("topPerformingWaqf", getTopPerformingWaqf(waqfs));
        dashboard.put("recentDonations", getRecentDonations(donations, 5));
        dashboard.put("totalActiveLoans", getTotalActiveLoans(waqfs));
        dashboard.put("waqfCount", waqfs.size());
        dashboard.put("donationCount", donations.size());
        
        return dashboard;
    }
    
    // ===== BENEFICIARY DASHBOARD =====
    public static Map<String, Object> getBeneficiaryDashboard(int beneficiaryId) {
        Map<String, Object> dashboard = new LinkedHashMap<>();
        
        // Get all applications by this beneficiary
        ArrayList<FundingApplication> applications = FundingService.getApplicationsByBeneficiary(beneficiaryId);
        
        // Get loans for this beneficiary
        ArrayList<QardHasan> loans = LoanService.getLoansByBeneficiary(beneficiaryId);
        
        // Get periodic reports
        ArrayList<PeriodicReport> reports = PeriodicReportService.getReportsByBeneficiary(beneficiaryId);
        
        // FIXED: Get advice - using correct method name
        ArrayList<AdviceTraining> advice = AdviceService.getAdviceForBeneficiary(beneficiaryId);
        
        dashboard.put("applications", applications);
        dashboard.put("loans", loans);
        dashboard.put("reports", reports);
        dashboard.put("advice", advice);
        dashboard.put("activeLoans", getActiveLoans(loans));
        dashboard.put("pendingApplications", getPendingApplications(applications));
        dashboard.put("approvedApplications", getApprovedApplications(applications));
        dashboard.put("rejectedApplications", getRejectedApplications(applications));
        dashboard.put("nextPaymentDue", getNextPaymentDue(loans));
        dashboard.put("unreadAdvice", AdviceService.getUnreadAdvice(beneficiaryId));
        dashboard.put("totalRepaid", getTotalRepaidByBeneficiary(beneficiaryId));
        dashboard.put("totalRemaining", getTotalRemainingByBeneficiary(beneficiaryId));
        dashboard.put("latestReport", PeriodicReportService.getLatestReport(
            !applications.isEmpty() ? applications.get(0).getApplicationID() : 0
        ));
        
        return dashboard;
    }
    
    // ===== ADMIN DASHBOARD =====
    public static Map<String, Object> getAdminDashboard() {
        Map<String, Object> dashboard = new LinkedHashMap<>();
        
        // Core metrics
        dashboard.put("totalWaqifs", UserService.getAllWaqifs().size());
        dashboard.put("totalBeneficiaries", UserService.getAllBeneficiaries().size());
        dashboard.put("totalWaqfAmount", WaqfService.getTotalWaqfAmount());
        dashboard.put("totalAvailableBalance", WaqfService.getTotalWaqfBalance());
        dashboard.put("totalActiveLoans", LoanService.getActiveLoans().size());
        
        // PRI & Namaa calculations
        dashboard.put("averagePRI", AssessmentService.getAveragePRI());
        dashboard.put("averageNamaa", NamaaIndexService.getAverageNamaaIndex());
        dashboard.put("bestProject", NamaaIndexService.getBestPerformingProject());
        
        // Risk metrics
        dashboard.put("overdueLoans", LoanService.getOverdueLoans());
        dashboard.put("pendingApplications", FundingService.getPendingApplications());
        dashboard.put("pendingReports", PeriodicReportService.getPendingReviewReports());
        
        // Savings & sustainability
        double totalWaqfBalance = WaqfService.getTotalWaqfBalance();
        double totalOutstandingLoans = getTotalOutstandingLoans();
        dashboard.put("totalSavings", totalWaqfBalance - totalOutstandingLoans);
        dashboard.put("waqfUtilizationRate", calculateWaqfUtilization());
        dashboard.put("totalDistributed", totalOutstandingLoans);
        dashboard.put("totalRepaid", getTotalRepaidAll());
        
        return dashboard;
    }
    
    // ===== HELPER METHODS =====
    private static double calculateTotalBeneficiaries(ArrayList<CashWaqf> waqfs) {
        int total = 0;
        for (CashWaqf w : waqfs) {
            // For each waqf, get loans and sum beneficiaries
            ArrayList<QardHasan> loans = LoanService.getLoansByWaqf(w.getWaqfID());
            for (QardHasan loan : loans) {
                if (loan.getApplication() != null && 
                    loan.getApplication().getProject() != null) {
                    total += loan.getApplication().getProject().getExpectedBeneficiaries();
                }
            }
        }
        return total;
    }
    
    // FIXED: Added Collections import and proper handling
    private static double calculateAverageNamaaIndex(ArrayList<CashWaqf> waqfs) {
        if (waqfs == null || waqfs.isEmpty()) return 0;
        
        double total = 0;
        int count = 0;
        for (CashWaqf w : waqfs) {
            ArrayList<QardHasan> loans = LoanService.getLoansByWaqf(w.getWaqfID());
            for (QardHasan loan : loans) {
                if (loan.getApplication() != null && 
                    loan.getApplication().getProject() != null) {
                    NamaaIndex idx = NamaaIndexService.getIndexByProject(
                        loan.getApplication().getProject().getProjectID()
                    );
                    if (idx != null) {
                        total += idx.getFinalIndex();
                        count++;
                    }
                }
            }
        }
        return count > 0 ? total / count : 0;
    }
    
    private static CashWaqf getTopPerformingWaqf(ArrayList<CashWaqf> waqfs) {
        if (waqfs == null || waqfs.isEmpty()) return null;
        
        CashWaqf best = null;
        double bestIndex = 0;
        for (CashWaqf w : waqfs) {
            // Create a temporary list with just this waqf
            ArrayList<CashWaqf> singleList = new ArrayList<>();
            singleList.add(w);
            double avg = calculateAverageNamaaIndex(singleList);
            if (avg > bestIndex) {
                bestIndex = avg;
                best = w;
            }
        }
        return best;
    }
    
    private static ArrayList<WaqfDonation> getRecentDonations(ArrayList<WaqfDonation> donations, int limit) {
        if (donations == null || donations.isEmpty()) return new ArrayList<>();
        
        // Sort by date descending and return top N
        donations.sort((a, b) -> b.getDonationDate().compareTo(a.getDonationDate()));
        ArrayList<WaqfDonation> recent = new ArrayList<>();
        for (int i = 0; i < Math.min(limit, donations.size()); i++) {
            recent.add(donations.get(i));
        }
        return recent;
    }
    
    private static int getTotalActiveLoans(ArrayList<CashWaqf> waqfs) {
        int total = 0;
        for (CashWaqf w : waqfs) {
            total += LoanService.getLoansByWaqf(w.getWaqfID()).size();
        }
        return total;
    }
    
    private static ArrayList<QardHasan> getActiveLoans(ArrayList<QardHasan> loans) {
        ArrayList<QardHasan> active = new ArrayList<>();
        for (QardHasan loan : loans) {
            if (!"Completed".equalsIgnoreCase(loan.getStatus()) && 
                !"Defaulted".equalsIgnoreCase(loan.getStatus())) {
                active.add(loan);
            }
        }
        return active;
    }
    
    // FIXED: Using enum comparison instead of String
    private static ArrayList<FundingApplication> getPendingApplications(ArrayList<FundingApplication> apps) {
        ArrayList<FundingApplication> pending = new ArrayList<>();
        for (FundingApplication app : apps) {
            ApplicationStatus status = app.getStatus();
            if (status == ApplicationStatus.PENDING || 
                status == ApplicationStatus.UNDER_REVIEW) {
                pending.add(app);
            }
        }
        return pending;
    }
    
    // FIXED: Using enum comparison instead of String
    private static ArrayList<FundingApplication> getApprovedApplications(ArrayList<FundingApplication> apps) {
        ArrayList<FundingApplication> approved = new ArrayList<>();
        for (FundingApplication app : apps) {
            if (app.getStatus() == ApplicationStatus.APPROVED) {
                approved.add(app);
            }
        }
        return approved;
    }
    
    // FIXED: Using enum comparison instead of String
    private static ArrayList<FundingApplication> getRejectedApplications(ArrayList<FundingApplication> apps) {
        ArrayList<FundingApplication> rejected = new ArrayList<>();
        for (FundingApplication app : apps) {
            if (app.getStatus() == ApplicationStatus.REJECTED) {
                rejected.add(app);
            }
        }
        return rejected;
    }
    
    private static LocalDate getNextPaymentDue(ArrayList<QardHasan> loans) {
        LocalDate next = null;
        for (QardHasan loan : loans) {
            if (!"Completed".equalsIgnoreCase(loan.getStatus()) && 
                loan.getDueDate() != null) {
                if (next == null || loan.getDueDate().isBefore(next)) {
                    next = loan.getDueDate();
                }
            }
        }
        return next;
    }
    
    private static double getTotalRepaidByBeneficiary(int beneficiaryId) {
        double total = 0;
        ArrayList<QardHasan> loans = LoanService.getLoansByBeneficiary(beneficiaryId);
        for (QardHasan loan : loans) {
            total += LoanService.getTotalRepaidByLoan(loan.getLoanID());
        }
        return total;
    }
    
    private static double getTotalRemainingByBeneficiary(int beneficiaryId) {
        double total = 0;
        ArrayList<QardHasan> loans = LoanService.getLoansByBeneficiary(beneficiaryId);
        for (QardHasan loan : loans) {
            total += LoanService.getRemainingBalance(loan.getLoanID());
        }
        return total;
    }
    
    private static double getTotalOutstandingLoans() {
        double total = 0;
        for (QardHasan loan : LoanService.getLoans()) {
            if (!"Completed".equalsIgnoreCase(loan.getStatus())) {
                total += LoanService.getRemainingBalance(loan.getLoanID());
            }
        }
        return total;
    }
    
    private static double getTotalRepaidAll() {
        double total = 0;
        for (Repayment r : LoanService.getRepayments()) {
            total += r.getAmount();
        }
        return total;
    }
    
    private static double calculateWaqfUtilization() {
        double totalBalance = WaqfService.getTotalWaqfBalance();
        double totalDistributed = getTotalOutstandingLoans();
        if (totalBalance + totalDistributed == 0) return 0;
        return (totalDistributed / (totalBalance + totalDistributed)) * 100;
    }
}