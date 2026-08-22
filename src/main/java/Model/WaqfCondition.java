package Model;

public class WaqfCondition {

	private int conditionID;
    private CashWaqf cashWaqf;
    private String allowedSector;
    private double maximumFunding;
    private double minimumPRI;
    private String targetBeneficiaries;

    public WaqfCondition() {

    }

    public WaqfCondition(int conditionID, CashWaqf cashWaqf,
            String allowedSector, double maximumFunding,
            int minimumPRI, String targetBeneficiaries) {

        this.conditionID = conditionID;
        this.cashWaqf = cashWaqf;
        this.allowedSector = allowedSector;
        this.setMaximumFunding(maximumFunding);
        this.setMinimumPRI(minimumPRI);
        this.targetBeneficiaries = targetBeneficiaries;
    }

    public int getConditionID() {
        return conditionID;
    }

    public void setConditionID(int conditionID) {
        this.conditionID = conditionID;
    }

    public CashWaqf getCashWaqf() {
        return cashWaqf;
    }

    public void setCashWaqf(CashWaqf cashWaqf) {
        this.cashWaqf = cashWaqf;
    }

    public String getAllowedSector() {
        return allowedSector;
    }

    public void setAllowedSector(String allowedSector) {
        this.allowedSector = allowedSector;
    }

    public double getMaximumFunding() {
        return maximumFunding;
    }

    public void setMaximumFunding(double maximumFunding) {
    	if (maximumFunding < 0) 
    		throw new IllegalArgumentException("Maximum Funding cannot be negative");
        this.maximumFunding = maximumFunding;
    }

    public double getMinimumPRI() {
        return minimumPRI;
    }

    public void setMinimumPRI(int minimumPRI) {
    	if (minimumPRI < 0 || minimumPRI > 100) 
    		throw new IllegalArgumentException("Minimum PRI must be between 0-100");
        this.minimumPRI = minimumPRI;
    }

    public String getTargetBeneficiaries() {
        return targetBeneficiaries;
    }

    public void setTargetBeneficiaries(String targetBeneficiaries) {
        this.targetBeneficiaries = targetBeneficiaries;
    }


    public boolean isProjectAccepted(String sector, double requestedAmount, int pri, String beneficiaryTargetGroup) {
        // 1. Check Sector
    	boolean sectorMatches = "All".equalsIgnoreCase(allowedSector) || allowedSector.equalsIgnoreCase(sector); 
        // 2. Check Amount
        boolean amountMatches = requestedAmount <= maximumFunding;
        
        // 3. Check PRI
        boolean priMatches = pri >= minimumPRI;
        
        // 4. Check Beneficiary Target Group (NEW)
        boolean targetMatches = targetBeneficiaries.equals("All Beneficiaries") || 
                                targetBeneficiaries.equalsIgnoreCase(beneficiaryTargetGroup);
        
        return sectorMatches && amountMatches && priMatches && targetMatches;
    }
    
    @Override
	public String toString() {
		return "WaqfCondition [conditionID=" + conditionID + ", cashWaqf=" + cashWaqf + ", allowedSector="
				+ allowedSector + ", maximumFunding=" + maximumFunding + ", minimumPRI=" + minimumPRI
				+ ", targetBeneficiaries=" + targetBeneficiaries + "]";
	}
    

}