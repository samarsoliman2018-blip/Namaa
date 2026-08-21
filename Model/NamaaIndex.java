package Model;
public class NamaaIndex {
	private int indexID;
    private Project project;
    private double economicImpact;
    private double socialImpact;
    private double sustainability;
    private double innovation;
    private double finalIndex;

    public NamaaIndex() {

    }

    public NamaaIndex(int indexID,Project project,double economicImpact,double socialImpact,
            double sustainability,double innovation) {
        this.indexID = indexID;
        this.project = project;
        this.setEconomicImpact(economicImpact);
        this.setSocialImpact(socialImpact);
        this.setSustainability(sustainability);
        this.setInnovation(innovation);

        calculateIndex();
    }
    
	public double getEconomicImpact() {
		return economicImpact;
	}

	public void setEconomicImpact(double economicImpact) {
		if (economicImpact < 0 || economicImpact > 100) 
			throw new IllegalArgumentException("Economic Impact must be between 0-100");
		this.economicImpact = economicImpact;
	}

	public double getSocialImpact() {
		return socialImpact;
	}

	public void setSocialImpact(double socialImpact) {
		if (socialImpact < 0 || socialImpact > 100) 
			throw new IllegalArgumentException("Social Impact must be between 0-100");
		this.socialImpact = socialImpact;
	}

	public double getSustainability() {
		return sustainability;
	}

	public void setSustainability(double sustainability) {
		if (sustainability < 0 || sustainability > 100) 
			throw new IllegalArgumentException("Sustainability Impact must be between 0-100");
		this.sustainability = sustainability;
	}

	public double getInnovation() {
		return innovation;
	}

	public void setInnovation(double innovation) {
		if (innovation < 0 || innovation > 100) 
			throw new IllegalArgumentException("innovation Impact must be between 0-100");
		this.innovation = innovation;
	}

	public void setIndexID(int indexID) {
		this.indexID = indexID;
	}

	public void setProject(Project project) {
		this.project = project;
	}

	public void setFinalIndex(double finalIndex) {
		if (finalIndex < 0 || finalIndex > 100) 
			throw new IllegalArgumentException("Final Index must be between 0-100");
		this.finalIndex = finalIndex;
	}
	
	
	public void calculateIndex() {
		finalIndex = (economicImpact * 0.35) + (socialImpact * 0.30) + (sustainability * 0.25) + (innovation * 0.10);
	}

    public int getIndexID() {
        return indexID;
    }

    public Project getProject() {
        return project;
    }

    public double getFinalIndex() {
        return finalIndex;
    }
    
    @Override
	public String toString() {
		return "NamaaIndex [indexID=" + indexID + ", project=" + project + ", economicImpact=" + economicImpact
				+ ", socialImpact=" + socialImpact + ", sustainability=" + sustainability + ", innovation=" + innovation
				+ ", finalIndex=" + finalIndex + "]";
	}
    

}