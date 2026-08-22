package Model;
import java.time.LocalDate;

public class QardHasan {

	private int loanID;
    private CashWaqf cashWaqf;
    private FundingApplication application;

    private double loanAmount;
    private LocalDate issueDate;
    private LocalDate dueDate;

    private String status;

    public QardHasan() {

    }

    public QardHasan(int loanID,CashWaqf cashWaqf,FundingApplication application,
            double loanAmount,LocalDate issueDate,LocalDate dueDate,String status) {

        this.loanID = loanID;
        this.cashWaqf = cashWaqf;
        this.application = application;
        this.setLoanAmount(loanAmount);
        this.issueDate = issueDate;
        this.dueDate = dueDate;
        this.status = status;
    }

	public void setLoanID(int loanID) {
		this.loanID = loanID;
	}

	public void setCashWaqf(CashWaqf cashWaqf) {
		this.cashWaqf = cashWaqf;
	}

	public void setApplication(FundingApplication application) {
		this.application = application;
	}

	public void setLoanAmount(double loanAmount) {
		if (loanAmount < 0) 
    		throw new IllegalArgumentException("Loan Amount cannot be negative");
       
		this.loanAmount = loanAmount;
	}

	public void setIssueDate(LocalDate issueDate) {
		this.issueDate = issueDate;
	}

	public void setDueDate(LocalDate dueDate) {
		this.dueDate = dueDate;
	}

    public int getLoanID() {
        return loanID;
    }

    public CashWaqf getCashWaqf() {
        return cashWaqf;
    }

    public FundingApplication getApplication() {
        return application;
    }

    public double getLoanAmount() {
        return loanAmount;
    }

    public LocalDate getIssueDate() {
        return issueDate;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
    
    @Override
	public String toString() {
		return "QardHasan [loanID=" + loanID + ", cashWaqf=" + cashWaqf + ", application=" + application
				+ ", loanAmount=" + loanAmount + ", issueDate=" + issueDate + ", dueDate=" + dueDate + ", status="
				+ status + "]";
	}
    

}