package Model;


import java.time.LocalDate;

public class ProjectAssessment {
	private int assessmentID;
    private FundingApplication application;

    private double economicScore;
    private double technicalScore;
    private double socialScore;
    private double environmentalScore;
    private double innovationScore;

    private double priScore;
    private String recommendation;
    private LocalDate assessmentDate;

    public ProjectAssessment() {

    }

    public ProjectAssessment(int assessmentID,
            FundingApplication application,
            double economicScore,
            double technicalScore,
            double socialScore,
            double environmentalScore,
            double innovationScore,
            LocalDate assessmentDate) {

        this.assessmentID = assessmentID;
        this.application = application;
        this.setEconomicScore(economicScore);
        this.setTechnicalScore(technicalScore);
        this.setSocialScore(socialScore);
        this.setEnvironmentalScore(environmentalScore);
        this.setInnovationScore(innovationScore);
        this.assessmentDate = assessmentDate;

        calculatePRI();
        generateRecommendation();
    }


	public void setAssessmentID(int assessmentID) {
		this.assessmentID = assessmentID;
	}

	public void setApplication(FundingApplication application) {
		this.application = application;
	}

	public void setEconomicScore(double economicScore) {
		if (economicScore < 0 || economicScore > 100) 
			throw new IllegalArgumentException("Economic Score must be between 0-100");
		this.economicScore = economicScore;
	}

	public void setTechnicalScore(double technicalScore) {
		this.technicalScore = technicalScore;
	}

	public void setSocialScore(double socialScore) {
		if (socialScore < 0  || socialScore > 100) 
			throw new IllegalArgumentException("Social Score must be between 0-100");
		this.socialScore = socialScore;
	}

	public void setEnvironmentalScore(double environmentalScore) {
		if (environmentalScore < 0 || environmentalScore > 100) 
			throw new IllegalArgumentException("Environmental Score must be between 0-100");
		this.environmentalScore = environmentalScore;
	}

	public void setInnovationScore(double innovationScore) {
		if (innovationScore < 0 || innovationScore > 100) 
			throw new IllegalArgumentException("Innovation Score must be between 0-100");
		this.innovationScore = innovationScore;
	}

	public void setPriScore(double priScore) {
		if (priScore < 0 || priScore > 100) 
			throw new IllegalArgumentException("PRI Score must be between 0-100");
		this.priScore = priScore;
	}

	public void setRecommendation(String recommendation) {
		this.recommendation = recommendation;
	}

	public void setAssessmentDate(LocalDate assessmentDate) {
		this.assessmentDate = assessmentDate;
	}

	
	public void calculatePRI() {
		   // Weighted: Economic 30%, Social 25%, Technical 20%, Environmental 15%, Innovation 10%
	    priScore = (economicScore * 0.30) 
	             + (socialScore * 0.25) 
	             + (technicalScore * 0.20) 
	             + (environmentalScore * 0.15) 
	             + (innovationScore * 0.10);
	}

    public void generateRecommendation() {

        if (priScore >= 80)
            recommendation = "Approved";
        else if (priScore >= 60)
            recommendation = "Needs Revision";
        else
            recommendation = "Rejected";

    }

    public int getAssessmentID() {
        return assessmentID;
    }

    public FundingApplication getApplication() {
        return application;
    }

    public double getEconomicScore() {
        return economicScore;
    }

    public double getTechnicalScore() {
        return technicalScore;
    }

    public double getSocialScore() {
        return socialScore;
    }

    public double getEnvironmentalScore() {
        return environmentalScore;
    }

    public double getInnovationScore() {
        return innovationScore;
    }

    public double getPriScore() {
        return priScore;
    }

    public String getRecommendation() {
        return recommendation;
    }

    public LocalDate getAssessmentDate() {
        return assessmentDate;
    }
    
    @Override
	public String toString() {
		return "ProjectAssessment [assessmentID=" + assessmentID + ", application=" + application + ", economicScore="
				+ economicScore + ", technicalScore=" + technicalScore + ", socialScore=" + socialScore
				+ ", environmentalScore=" + environmentalScore + ", innovationScore=" + innovationScore + ", priScore="
				+ priScore + ", recommendation=" + recommendation + ", assessmentDate=" + assessmentDate + "]";
	}
    

}