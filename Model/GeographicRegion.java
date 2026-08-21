package Model;

public class GeographicRegion {
    private String region;
    private int projects;
    private double funding;
    private int beneficiaries;
    private double avgNamaa;
    private double successRate;

    // Constructor
    public GeographicRegion(String region, int projects, double funding, 
                           int beneficiaries, double avgNamaa, double successRate) {
        this.region = region;
        this.projects = projects;
        this.funding = funding;
        this.beneficiaries = beneficiaries;
        this.avgNamaa = avgNamaa;
        this.successRate = successRate;
    }

    // Getters and Setters
    public String getRegion() { return region; }
    public void setRegion(String region) { this.region = region; }
    
    public int getProjects() { return projects; }
    public void setProjects(int projects) { this.projects = projects; }
    
    public double getFunding() { return funding; }
    public void setFunding(double funding) { this.funding = funding; }
    
    public int getBeneficiaries() { return beneficiaries; }
    public void setBeneficiaries(int beneficiaries) { this.beneficiaries = beneficiaries; }
    
    public double getAvgNamaa() { return avgNamaa; }
    public void setAvgNamaa(double avgNamaa) { this.avgNamaa = avgNamaa; }
    
    public double getSuccessRate() { return successRate; }
    public void setSuccessRate(double successRate) { this.successRate = successRate; }
}