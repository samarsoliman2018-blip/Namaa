package Model;

public class HistoricalProject {
    private int projectId;
    private String projectName;
    private String category;
    private double actualCost;
    private int actualDuration;
    private int beneficiariesReached;
    private double successRate;
    private String lessonsLearned;
    private double economicScore;
    private double socialScore;
    private double sustainabilityScore;
    private double innovationScore;
    private double finalIndex;
    private String projectStatus;
    private String completionDate;

    // ===== CONSTRUCTORS =====
    public HistoricalProject() {
    }

    public HistoricalProject(int projectId, String projectName, String category,
                            double actualCost, int actualDuration, int beneficiariesReached,
                            double successRate, String lessonsLearned, double economicScore,
                            double socialScore, double sustainabilityScore, double innovationScore,
                            double finalIndex, String projectStatus, String completionDate) {
        this.projectId = projectId;
        this.projectName = projectName;
        this.category = category;
        this.setActualCost(actualCost);
        this.setActualDuration(actualDuration);
        this.setBeneficiariesReached(beneficiariesReached);
        this.setSuccessRate(successRate);
        this.lessonsLearned = lessonsLearned;
        this.setEconomicScore(economicScore);
        this.setSocialScore(socialScore);
        this.setSustainabilityScore(sustainabilityScore);
        this.setInnovationScore(innovationScore);
        this.setFinalIndex(finalIndex);
        this.projectStatus = projectStatus;
        this.completionDate = completionDate;
    }

    // ===== GETTERS AND SETTERS =====
    public int getProjectId() { return projectId; }
    public void setProjectId(int projectId) { this.projectId = projectId; }  

    public String getProjectName() { return projectName; }
    public void setProjectName(String projectName) { this.projectName = projectName; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public double getActualCost() { return actualCost; }
    public void setActualCost(double actualCost) { 
    	if (actualCost < 0) 
    		throw new IllegalArgumentException("ActualCost cannot be negative");
    	this.actualCost = actualCost; 
    	}

    public int getActualDuration() { return actualDuration; }
    public void setActualDuration(int actualDuration) { 
    	if (actualDuration < 0) 
    		throw new IllegalArgumentException("Actual Duration cannot be negative");
    	this.actualDuration = actualDuration; }

    public int getBeneficiariesReached() { return beneficiariesReached; }
    public void setBeneficiariesReached(int beneficiariesReached) { 
    	if (beneficiariesReached < 0) 
    		throw new IllegalArgumentException("Beneficiaries Reached cannot be negative");
    	this.beneficiariesReached = beneficiariesReached; }

    public double getSuccessRate() { return successRate; }
    public void setSuccessRate(double successRate) { 
    	if (successRate < 0) 
    		throw new IllegalArgumentException("Success Rate cannot be negative");
    	this.successRate = successRate; }

    public String getLessonsLearned() { return lessonsLearned; }
    public void setLessonsLearned(String lessonsLearned) { this.lessonsLearned = lessonsLearned; }

    public double getEconomicScore() { return economicScore; }
    public void setEconomicScore(double economicScore) { 
    	if (economicScore < 0 || economicScore > 100) 
    		throw new IllegalArgumentException("Economic Score must be between 0-100");
    	this.economicScore = economicScore; }

    public double getSocialScore() { return socialScore; }
    public void setSocialScore(double socialScore) { 
    	if (socialScore < 0 || socialScore > 100) 
    		throw new IllegalArgumentException("Social Score must be between 0-100");
    	this.socialScore = socialScore; }

    public double getSustainabilityScore() { return sustainabilityScore; }
    public void setSustainabilityScore(double sustainabilityScore) { 
    	if (sustainabilityScore < 0 || sustainabilityScore > 100) 
    		throw new IllegalArgumentException("Sustainability Score must be between 0-100");
    	this.sustainabilityScore = sustainabilityScore; }

    public double getInnovationScore() { return innovationScore; }
    public void setInnovationScore(double innovationScore) { 
    	if (innovationScore < 0 || innovationScore > 100) 
    		throw new IllegalArgumentException("Innovation Score must be between 0-100");
    	this.innovationScore = innovationScore; }

    public double getFinalIndex() { return finalIndex; }
    public void setFinalIndex(double finalIndex) { 
    	if (finalIndex < 0 || finalIndex > 100) 
    		throw new IllegalArgumentException("Final Index must be between 0-100");
    	this.finalIndex = finalIndex; }

    public String getProjectStatus() { return projectStatus; }
    public void setProjectStatus(String projectStatus) { this.projectStatus = projectStatus; }

    public String getCompletionDate() { return completionDate; }
    public void setCompletionDate(String completionDate) { this.completionDate = completionDate; }

    @Override
    public String toString() {
        return "HistoricalProject{" +
                "projectId=" + projectId +
                ", projectName='" + projectName + '\'' +
                ", category='" + category + '\'' +
                ", actualCost=" + actualCost +
                ", successRate=" + successRate +
                ", finalIndex=" + finalIndex +
                '}';
    }
}