package Service;

import Model.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.io.*;

public class LoanService {
    private static ArrayList<QardHasan> loans = new ArrayList<>();
    private static ArrayList<Repayment> repayments = new ArrayList<>();
    private static final String LOAN_FILE = "loans.csv";
    private static final String REPAYMENT_FILE = "repayments.csv";
    private static int nextLoanId = 1;
    private static int nextRepaymentId = 1;
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE;

    static {
        loadLoans();
        loadRepayments();
        if (!loans.isEmpty()) {
            for (QardHasan loan : loans) {
                if (loan.getLoanID() >= nextLoanId) {
                    nextLoanId = loan.getLoanID() + 1;
                }
            }
        }
        if (!repayments.isEmpty()) {
            for (Repayment r : repayments) {
                if (r.getRepaymentID() >= nextRepaymentId) {
                    nextRepaymentId = r.getRepaymentID() + 1;
                }
            }
        }
    }

 // In LoanService.java - createLoan() method

 // In LoanService.java

    public static void createLoan(QardHasan loan) {
        if (loan.getLoanID() == 0) {
            loan.setLoanID(nextLoanId++);
        }
        loans.add(loan);
        saveAllLoansToFile();  // ← This should save to loans.csv
        
        // Allocate funding from Waqf
        WaqfService.allocateFunding(
            loan.getCashWaqf().getWaqfID(),
            loan.getLoanAmount()
        );
    }

    
    public static void addRepayment(Repayment repayment) {
        if (repayment.getRepaymentID() == 0) {
            repayment.setRepaymentID(nextRepaymentId++);
        }
        if (repayment.getPaymentDate() == null) {
            repayment.setPaymentDate(LocalDate.now());
        }
        repayments.add(repayment);
        saveAllRepaymentsToFile();
        
        // Update waqf balance
        repayment.getLoan().getCashWaqf().receiveRepayment(repayment.getAmount());
        WaqfService.receiveRepayment(
            repayment.getLoan().getCashWaqf().getWaqfID(),
            repayment.getAmount()
        );
        
        // Check if loan is fully paid
        checkLoanCompletion(repayment.getLoan());
    }

    public static ArrayList<QardHasan> getLoans() {
        return new ArrayList<>(loans);
    }

    public static ArrayList<Repayment> getRepayments() {
        return new ArrayList<>(repayments);
    }

    public static QardHasan searchLoan(int id) {
        for (QardHasan loan : loans) {
            if (loan.getLoanID() == id) {
                return loan;
            }
        }
        return null;
    }

    // ===== GET LOANS BY BENEFICIARY =====
 // In LoanService.java
    public static ArrayList<QardHasan> getLoansByBeneficiary(int beneficiaryId) {
        ArrayList<QardHasan> result = new ArrayList<>();
        for (QardHasan loan : loans) {
            if (loan.getApplication() != null && 
                loan.getApplication().getBeneficiary() != null &&
                loan.getApplication().getBeneficiary().getBeneficiaryID() == beneficiaryId) {
                result.add(loan);
            }
        }
        System.out.println("🔍 Found " + result.size() + " loans for beneficiary " + beneficiaryId);
        return result;
    }

    // ===== GET REPAYMENTS BY LOAN =====
    public static ArrayList<Repayment> getRepaymentsByLoan(int loanId) {
        ArrayList<Repayment> result = new ArrayList<>();
        for (Repayment r : repayments) {
            if (r.getLoan().getLoanID() == loanId) {
                result.add(r);
            }
        }
        return result;
    }

    // ===== GET TOTAL REPAID BY LOAN =====
    public static double getTotalRepaidByLoan(int loanId) {
        double total = 0;
        for (Repayment r : repayments) {
            if (r.getLoan().getLoanID() == loanId) {
                total += r.getAmount();
            }
        }
        return total;
    }

    // ===== GET REMAINING BALANCE BY LOAN =====
    public static double getRemainingBalance(int loanId) {
        QardHasan loan = searchLoan(loanId);
        if (loan == null) return 0;
        return loan.getLoanAmount() - getTotalRepaidByLoan(loanId);
    }

    // ===== GET OVERDUE LOANS =====
    public static ArrayList<QardHasan> getOverdueLoans() {
        ArrayList<QardHasan> overdue = new ArrayList<>();
        LocalDate today = LocalDate.now();
        for (QardHasan loan : loans) {
            if (loan.getDueDate() != null && 
                loan.getDueDate().isBefore(today) && 
                !loan.getStatus().equals("Completed") && 
                getRemainingBalance(loan.getLoanID()) > 0) {
                overdue.add(loan);
            }
        }
        return overdue;
    }

    // ===== GET ACTIVE LOANS =====
    public static ArrayList<QardHasan> getActiveLoans() {
        ArrayList<QardHasan> active = new ArrayList<>();
        for (QardHasan loan : loans) {
            if (!loan.getStatus().equals("Completed") && 
                !loan.getStatus().equals("Defaulted") &&
                getRemainingBalance(loan.getLoanID()) > 0) {
                active.add(loan);
            }
        }
        return active;
    }

    // ===== CHECK AND UPDATE LOAN COMPLETION =====
    private static void checkLoanCompletion(QardHasan loan) {
        double remaining = getRemainingBalance(loan.getLoanID());
        if (remaining <= 0.01) { // Small tolerance for floating point
            loan.setStatus("Completed");
            saveAllLoansToFile();
        }
    }

    

    // ===== UPDATE LOAN STATUS =====
    public static void updateLoanStatus(int loanId, String status) {
        QardHasan loan = searchLoan(loanId);
        if (loan != null) {
            loan.setStatus(status);
            saveAllLoansToFile();
        }
    }

    // ===== GET LOAN SUMMARY FOR BENEFICIARY =====
    public static String getLoanSummary(int beneficiaryId) {
        ArrayList<QardHasan> loans = getLoansByBeneficiary(beneficiaryId);
        if (loans.isEmpty()) {
            return "No loans found for this beneficiary.";
        }
        
        StringBuilder summary = new StringBuilder();
        summary.append("=== LOAN SUMMARY ===\n");
        double totalLoans = 0;
        double totalRepaid = 0;
        
        for (QardHasan loan : loans) {
            double repaid = getTotalRepaidByLoan(loan.getLoanID());
            double remaining = loan.getLoanAmount() - repaid;
            totalLoans += loan.getLoanAmount();
            totalRepaid += repaid;
            
            summary.append("\nLoan #").append(loan.getLoanID())
                   .append(" | Amount: ").append(String.format("%.2f", loan.getLoanAmount()))
                   .append(" | Repaid: ").append(String.format("%.2f", repaid))
                   .append(" | Remaining: ").append(String.format("%.2f", remaining))
                   .append(" | Status: ").append(loan.getStatus())
                   .append(" | Due: ").append(loan.getDueDate());
        }
        
        summary.append("\n\nTOTAL LOANS: ").append(String.format("%.2f", totalLoans))
               .append(" | TOTAL REPAID: ").append(String.format("%.2f", totalRepaid))
               .append(" | OVERALL REMAINING: ").append(String.format("%.2f", totalLoans - totalRepaid));
        
        return summary.toString();
    }

    public static int getNextLoanId() {
        return nextLoanId++;
    }

    public static int getNextRepaymentId() {
        return nextRepaymentId++;
    }

    // ===== SAVE ALL LOANS =====
    private static void saveAllLoansToFile() {
        try (FileWriter writer = new FileWriter(LOAN_FILE, false)) {
            writer.write("LoanID,ApplicationID,WaqfID,LoanAmount,IssueDate,DueDate,Status\n");
            
            for (QardHasan loan : loans) {
                writer.write(
                    loan.getLoanID() + "," +
                    loan.getApplication().getApplicationID() + "," +
                    loan.getCashWaqf().getWaqfID() + "," +
                    loan.getLoanAmount() + "," +
                    loan.getIssueDate().format(DATE_FORMATTER) + "," +
                    (loan.getDueDate() != null ? loan.getDueDate().format(DATE_FORMATTER) : "") + "," +
                    loan.getStatus() + "\n"
                );
            }
            writer.flush();
            System.out.println("Saved " + loans.size() + " loans to CSV");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // ===== SAVE ALL REPAYMENTS =====
    private static void saveAllRepaymentsToFile() {
        try (FileWriter writer = new FileWriter(REPAYMENT_FILE, false)) {
            writer.write("RepaymentID,LoanID,Amount,PaymentDate\n");
            
            for (Repayment repayment : repayments) {
                writer.write(
                    repayment.getRepaymentID() + "," +
                    repayment.getLoan().getLoanID() + "," +
                    repayment.getAmount() + "," +
                    repayment.getPaymentDate().format(DATE_FORMATTER) + "\n"
                );
            }
            writer.flush();
            System.out.println("Saved " + repayments.size() + " repayments to CSV");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // ===== LOAD LOANS =====
 // In LoanService.java - loadLoans()
    private static void loadLoans() {
        File file = new File(LOAN_FILE);
        if (!file.exists()) return;
        
        try (BufferedReader reader = new BufferedReader(new FileReader(LOAN_FILE))) {
            String line;
            boolean isFirstLine = true;
            
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                if (isFirstLine) {
                    isFirstLine = false;
                    continue;
                }
                
                String[] data = line.split(",");
                if (data.length >= 7) {
                    try {
                        int loanId = Integer.parseInt(data[0].trim());
                        int applicationId = Integer.parseInt(data[1].trim());
                        int waqfId = Integer.parseInt(data[2].trim());
                        double amount = Double.parseDouble(data[3].trim());
                        LocalDate issueDate = LocalDate.parse(data[4].trim());
                        LocalDate dueDate = LocalDate.parse(data[5].trim());
                        String status = data[6].trim();
                        
                        FundingApplication app = FundingService.searchApplication(applicationId);
                        CashWaqf waqf = WaqfService.searchWaqf(waqfId);
                        
                        // ===== FIXED: Check if waqf is null =====
                        if (app != null && waqf != null) {
                            QardHasan loan = new QardHasan();
                            loan.setLoanID(loanId);
                            loan.setApplication(app);
                            loan.setCashWaqf(waqf);
                            loan.setLoanAmount(amount);
                            loan.setIssueDate(issueDate);
                            loan.setDueDate(dueDate);
                            loan.setStatus(status);
                            loans.add(loan);
                            System.out.println("✅ Loaded loan: " + loanId + " with Waqf #" + waqfId);
                        } else {
                            System.err.println("❌ Application or Waqf not found for loan: " + loanId);
                            System.err.println("   App: " + applicationId + ", Waqf: " + waqfId);
                        }
                    } catch (Exception e) {
                        System.err.println("Error parsing loan line: " + line);
                        e.printStackTrace();
                    }
                }
            }
            System.out.println("Loaded " + loans.size() + " loans from CSV");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    // ===== LOAD REPAYMENTS (FIXED - Proper date parsing) =====
    private static void loadRepayments() {
        File file = new File(REPAYMENT_FILE);
        if (!file.exists()) return;
        
        try (BufferedReader reader = new BufferedReader(new FileReader(REPAYMENT_FILE))) {
            String line;
            boolean isFirstLine = true;
            
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                if (isFirstLine) {
                    isFirstLine = false;
                    continue;
                }
                
                String[] data = line.split(",");
                if (data.length >= 4) {
                    try {
                        int repaymentId = Integer.parseInt(data[0].trim());
                        int loanId = Integer.parseInt(data[1].trim());
                        double amount = Double.parseDouble(data[2].trim());
                        
                        // FIXED: Parse LocalDate correctly
                        LocalDate paymentDate = LocalDate.parse(data[3].trim(), DATE_FORMATTER);
                        
                        QardHasan loan = searchLoan(loanId);
                        
                        if (loan != null) {
                            Repayment repayment = new Repayment();
                            repayment.setRepaymentID(repaymentId);
                            repayment.setLoan(loan);
                            repayment.setAmount(amount);
                            repayment.setPaymentDate(paymentDate);  // This should work now
                            repayments.add(repayment);
                        } else {
                            System.err.println("Loan not found for repayment: " + repaymentId);
                        }
                    } catch (Exception e) {
                        System.err.println("Error parsing repayment line: " + line);
                        e.printStackTrace();
                    }
                }
            }
            System.out.println("Loaded " + repayments.size() + " repayments from CSV");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
 // In LoanService.java
    public static ArrayList<QardHasan> getLoansByWaqf(int waqfId) {
        ArrayList<QardHasan> result = new ArrayList<>();
        for (QardHasan loan : loans) {
            if (loan.getCashWaqf() != null && 
                loan.getCashWaqf().getWaqfID() == waqfId) {
                result.add(loan);
            }
        }
        return result;
    }
}