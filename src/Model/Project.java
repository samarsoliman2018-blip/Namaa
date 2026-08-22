package Model;

public class Project {

    private int projectID;
    private String projectName;
    private String sector;
    private String description;
    private String location;
    private double projectCost;
    private int expectedBeneficiaries;
    private int durationMonths;

    public Project() {

    }

    public Project(int projectID, String projectName,
            String sector, String description,
            String location, double projectCost,
            int expectedBeneficiaries,
            int durationMonths) {

        this.projectID = projectID;
        this.projectName = projectName;
        this.sector = sector;
        this.description = description;
        this.location = location;
        this.setProjectCost(projectCost);
        this.setExpectedBeneficiaries(expectedBeneficiaries);
        this.setDurationMonths(durationMonths);
    }

    public int getProjectID() {
        return projectID;
    }

    public void setProjectID(int projectID) {
        this.projectID = projectID;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public String getSector() {
        return sector;
    }

    public void setSector(String sector) {
        this.sector = sector;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public double getProjectCost() {
        return projectCost;
    }

    public void setProjectCost(double projectCost) {
    	if (projectCost < 0) 
    		throw new IllegalArgumentException("Project Cost cannot be negative");
        this.projectCost = projectCost;
    }

    public int getExpectedBeneficiaries() {
        return expectedBeneficiaries;
    }

    public void setExpectedBeneficiaries(int expectedBeneficiaries) {
    	if (expectedBeneficiaries < 0) 
    		throw new IllegalArgumentException("Expected Beneficiaries cannot be negative");
        this.expectedBeneficiaries = expectedBeneficiaries;
    }

    public int getDurationMonths() {
        return durationMonths;
    }

    public void setDurationMonths(int durationMonths) {
    	if (durationMonths < 0) 
    		throw new IllegalArgumentException("Duration Months cannot be negative");
        this.durationMonths = durationMonths;
    }

    @Override
    public String toString() {
        return projectName;
    }

}