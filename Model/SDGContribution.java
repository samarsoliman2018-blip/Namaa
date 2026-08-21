package Model;

public class SDGContribution {
    private int sdgNumber;
    private String sdgName;
    private int projects;
    private double funding;
    private int beneficiaries;
    private double impactScore;

    // Constructor
    public SDGContribution(int sdgNumber, String sdgName, int projects, 
                          double funding, int beneficiaries, double impactScore) {
        this.sdgNumber = sdgNumber;
        this.sdgName = sdgName;
        this.projects = projects;
        this.funding = funding;
        this.beneficiaries = beneficiaries;
        this.impactScore = impactScore;
    }

    // Getters and Setters
    public int getSdgNumber() { return sdgNumber; }
    public void setSdgNumber(int sdgNumber) { this.sdgNumber = sdgNumber; }
    
    public String getSdgName() { return sdgName; }
    public void setSdgName(String sdgName) { this.sdgName = sdgName; }
    
    public int getProjects() { return projects; }
    public void setProjects(int projects) { this.projects = projects; }
    
    public double getFunding() { return funding; }
    public void setFunding(double funding) { this.funding = funding; }
    
    public int getBeneficiaries() { return beneficiaries; }
    public void setBeneficiaries(int beneficiaries) { this.beneficiaries = beneficiaries; }
    
    public double getImpactScore() { return impactScore; }
    public void setImpactScore(double impactScore) { this.impactScore = impactScore; }
}