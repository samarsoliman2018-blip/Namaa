package Model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class FundingApplication {
    private int applicationID;
    private Beneficiary beneficiary;
    private Project project;
    private LocalDateTime applicationDate;
    private double requestedAmount;
    private ApplicationStatus status;
    
    // ===== DOCUMENT STORAGE FIELDS =====
    private String businessPlanFile;
    private String financialStatementsFile;
    private String supportingDocumentsFile;
    private transient List<String> uploadedFileNames;
    private transient List<String> uploadedFilePaths;
    
    public FundingApplication() {
        this.status = ApplicationStatus.PENDING;
        this.applicationDate = LocalDateTime.now();
        this.uploadedFileNames = new ArrayList<>();
        this.uploadedFilePaths = new ArrayList<>();
    }

    public FundingApplication(int applicationID, Beneficiary beneficiary, Project project,
            LocalDateTime applicationDate, double requestedAmount, ApplicationStatus status) {
        this.applicationID = applicationID;
        this.beneficiary = beneficiary;
        this.project = project;
        this.applicationDate = applicationDate;
        this.setRequestedAmount(requestedAmount);
        this.status = status;
        this.uploadedFileNames = new ArrayList<>();
        this.uploadedFilePaths = new ArrayList<>();
    }

    // ===== GETTERS AND SETTERS =====
    public int getApplicationID() { return applicationID; }
    public void setApplicationID(int applicationID) { this.applicationID = applicationID; }

    public Beneficiary getBeneficiary() { return beneficiary; }
    public void setBeneficiary(Beneficiary beneficiary) { this.beneficiary = beneficiary; }

    public Project getProject() { return project; }
    public void setProject(Project project) { this.project = project; }

    public LocalDateTime getApplicationDate() { return applicationDate; }
    public void setApplicationDate(LocalDateTime applicationDate) { this.applicationDate = applicationDate; }

    public double getRequestedAmount() { return requestedAmount; }
    public void setRequestedAmount(double requestedAmount) { 
    	if (requestedAmount < 0) 
    		throw new IllegalArgumentException("Requested Amount cannot be negative");
    	this.requestedAmount = requestedAmount; }

    public ApplicationStatus getStatus() { return status; }
    public void setStatus(ApplicationStatus status) { this.status = status; }

    // ===== DOCUMENT GETTERS AND SETTERS =====
    public String getBusinessPlanFile() { return businessPlanFile; }
    public void setBusinessPlanFile(String businessPlanFile) { 
        this.businessPlanFile = businessPlanFile;
        System.out.println("📄 Business Plan set: " + businessPlanFile);
    }

    public String getFinancialStatementsFile() { return financialStatementsFile; }
    public void setFinancialStatementsFile(String financialStatementsFile) { 
        this.financialStatementsFile = financialStatementsFile;
        System.out.println("📊 Financial Statements set: " + financialStatementsFile);
    }

    public String getSupportingDocumentsFile() { return supportingDocumentsFile; }
    public void setSupportingDocumentsFile(String supportingDocumentsFile) { 
        this.supportingDocumentsFile = supportingDocumentsFile;
        System.out.println("📎 Supporting Documents set: " + supportingDocumentsFile);
    }

    public List<String> getUploadedFileNames() { 
        if (uploadedFileNames == null) {
            uploadedFileNames = new ArrayList<>();
        }
        return uploadedFileNames; 
    }
    
    public void setUploadedFileNames(List<String> uploadedFileNames) { 
        this.uploadedFileNames = uploadedFileNames; 
    }
    
    public List<String> getUploadedFilePaths() { 
        if (uploadedFilePaths == null) {
            uploadedFilePaths = new ArrayList<>();
        }
        return uploadedFilePaths; 
    }
    
    public void setUploadedFilePaths(List<String> uploadedFilePaths) { 
        this.uploadedFilePaths = uploadedFilePaths; 
    }
    
    public void addUploadedFile(String fileName, String filePath) {
        if (uploadedFileNames == null) {
            uploadedFileNames = new ArrayList<>();
        }
        if (uploadedFilePaths == null) {
            uploadedFilePaths = new ArrayList<>();
        }
        this.uploadedFileNames.add(fileName);
        this.uploadedFilePaths.add(filePath);
        System.out.println("📎 Additional file added: " + fileName);
    }

    // ===== HELPER METHODS =====
    public boolean hasBusinessPlan() { 
        return businessPlanFile != null && !businessPlanFile.isEmpty(); 
    }
    
    public boolean hasFinancialStatements() { 
        return financialStatementsFile != null && !financialStatementsFile.isEmpty(); 
    }
    
    public boolean hasSupportingDocuments() { 
        return supportingDocumentsFile != null && !supportingDocumentsFile.isEmpty(); 
    }

    public void approve() { this.status = ApplicationStatus.APPROVED; }
    public void reject() { this.status = ApplicationStatus.REJECTED; }
    public void setUnderReview() { this.status = ApplicationStatus.UNDER_REVIEW; }
    public void setFunded() { this.status = ApplicationStatus.FUNDED; }
    public void setCompleted() { this.status = ApplicationStatus.COMPLETED; }

    @Override
    public String toString() {
        return "FundingApplication [applicationID=" + applicationID + 
               ", status=" + status + "]";
    }
}