package Model;

import java.time.LocalDate;

public class Repayment {
    private int repaymentID;
    private QardHasan loan;
    private double amount;
    private LocalDate paymentDate;  

    public Repayment() {

    }

    public Repayment(int repaymentID,
            QardHasan loan,
            double amount,
            LocalDate paymentDate) {

        this.repaymentID = repaymentID;
        this.loan = loan;
        this.setAmount(amount);
        this.paymentDate = paymentDate;
    }

    public void setRepaymentID(int repaymentID) {
        this.repaymentID = repaymentID;
    }

    public void setLoan(QardHasan loan) {
        this.loan = loan;
    }

    public void setAmount(double amount) {
    	if (amount < 0) 
    		throw new IllegalArgumentException("Amount cannot be negative");
        this.amount = amount;
    }

    public void setPaymentDate(LocalDate paymentDate) {
        this.paymentDate = paymentDate;
    }

    public int getRepaymentID() {
        return repaymentID;
    }

    public QardHasan getLoan() {
        return loan;
    }

    public double getAmount() {
        return amount;
    }

    public LocalDate getPaymentDate() {
        return paymentDate;
    }

    @Override
    public String toString() {
        return "Repayment [repaymentID=" + repaymentID + 
               ", loan=" + loan + 
               ", amount=" + amount + 
               ", paymentDate=" + paymentDate + "]";
    }
}