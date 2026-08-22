package Model;

import java.time.LocalDate;

public class CashWaqf {


    private int waqfID;
    private Waqif waqif;
    private double waqfAmount;
    private double availableBalance;
    private LocalDate creationDate;
    private String status;
    private double totalImpactScore; // Calculated from funded projects
    private int numberOfBeneficiariesServed;
    private double averageRepaymentRate; // For Qard Hasan performance
    private String waqfPurpose; // "Education", "Healthcare", "Micro-enterprise"

   
    public CashWaqf() {

    }



    public CashWaqf(int waqfID, Waqif waqif, double waqfAmount, double availableBalance, LocalDate creationDate,
			String status, double totalImpactScore, int numberOfBeneficiariesServed, double averageRepaymentRate,
			String waqfPurpose) {
		super();
		this.waqfID = waqfID;
		this.waqif = waqif;
		this.setWaqfAmount(waqfAmount);
		this.setAvailableBalance(availableBalance);
		this.creationDate = creationDate;
		this.status = status;
		this.setTotalImpactScore(totalImpactScore);
		this.setNumberOfBeneficiariesServed(numberOfBeneficiariesServed);
		this.setAverageRepaymentRate(averageRepaymentRate);
		this.waqfPurpose = waqfPurpose;
	}



	public double getTotalImpactScore() {
		return totalImpactScore;
	}



	public void setTotalImpactScore(double totalImpactScore) {
		if (totalImpactScore < 0) 
			throw new IllegalArgumentException("Total Impact Score cannot be negative");
	    
		this.totalImpactScore = totalImpactScore;
	}



	public int getNumberOfBeneficiariesServed() {
		return numberOfBeneficiariesServed;
	}



	public void setNumberOfBeneficiariesServed(int numberOfBeneficiariesServed) {
		if (numberOfBeneficiariesServed < 0) 
			throw new IllegalArgumentException("Number Of Beneficiaries Served cannot be negative");
		this.numberOfBeneficiariesServed = numberOfBeneficiariesServed;
	}



	public double getAverageRepaymentRate() {
		return averageRepaymentRate;
	}



	public void setAverageRepaymentRate(double averageRepaymentRate) {
		if (averageRepaymentRate < 0) 
			throw new IllegalArgumentException("Average Repaymen tRate cannot be negative");
		this.averageRepaymentRate = averageRepaymentRate;
	}



	public String getWaqfPurpose() {
		return waqfPurpose;
	}



	public void setWaqfPurpose(String waqfPurpose) {
		this.waqfPurpose = waqfPurpose;
	}



	public CashWaqf(int waqfID, Waqif waqif, double waqfAmount,
                    double availableBalance, LocalDate creationDate,
                    String status) {


        this.waqfID = waqfID;
        this.waqif = waqif;
        this.waqfAmount = waqfAmount;
        this.availableBalance = availableBalance;
        this.creationDate = creationDate;
        this.status = status;

    }




    public int getWaqfID() {
        return waqfID;
    }


    public void setWaqfID(int waqfID) {
        this.waqfID = waqfID;
    }



    public Waqif getWaqif() {
        return waqif;
    }


    public void setWaqif(Waqif waqif) {
        this.waqif = waqif;
    }



    public double getWaqfAmount() {
        return waqfAmount;
    }


    public void setWaqfAmount(double waqfAmount) {

        if(waqfAmount >= 0)
            this.waqfAmount = waqfAmount;

    }




    public double getAvailableBalance() {
        return availableBalance;
    }



    public void setAvailableBalance(double availableBalance) {
    	if (availableBalance < 0) 
    		throw new IllegalArgumentException("Available Balance cannot be negative");
    	this.availableBalance = availableBalance;

    }




    public LocalDate getCreationDate() {
        return creationDate;
    }



    public void setCreationDate(LocalDate creationDate) {
        this.creationDate = creationDate;
    }




    public String getStatus() {
        return status;
    }



    public void setStatus(String status) {
        this.status = status;
    }




    // Add new donation to the waqf

    public void addDonation(double amount){


        if(amount > 0){

            waqfAmount += amount;
            availableBalance += amount;

        }

    }




    // Allocate money for Qard Hasan loan

    public boolean allocateFunding(double amount){


        if(amount > 0 && amount <= availableBalance){

            availableBalance -= amount;

            return true;

        }


        return false;

    }




    // Receive repayment from beneficiary

    public void receiveRepayment(double amount){


        if(amount > 0){

            availableBalance += amount;

        }

    }



	@Override
	public String toString() {
		return "CashWaqf [waqfID=" + waqfID + ", waqif=" + waqif + ", waqfAmount=" + waqfAmount + ", availableBalance="
				+ availableBalance + ", creationDate=" + creationDate + ", status=" + status + ", totalImpactScore="
				+ totalImpactScore + ", numberOfBeneficiariesServed=" + numberOfBeneficiariesServed
				+ ", averageRepaymentRate=" + averageRepaymentRate + ", waqfPurpose=" + waqfPurpose + "]";
	}



	public double getAllocatedFunds() {
	    return waqfAmount - availableBalance;
	}





}