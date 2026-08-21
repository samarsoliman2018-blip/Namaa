package Model;

import java.time.LocalDate;

public class WaqfDonation {
    private int donationId;
    private Waqif waqif;
    private CashWaqf cashWaqf;
    private double amount;
    private LocalDate donationDate;
    private String paymentMethod; // "Card", "Bank Transfer", "Cash", "Crypto"
    private String transactionId;

    public WaqfDonation() {
        this.donationDate = LocalDate.now();
        this.paymentMethod = "Bank Transfer";
        setAmount(amount);
    }

    public WaqfDonation(int donationId, Waqif waqif, CashWaqf cashWaqf, 
                       double amount, LocalDate donationDate, 
                       String paymentMethod, String transactionId) {
        this.donationId = donationId;
        this.waqif = waqif;
        this.cashWaqf = cashWaqf;
        this.setAmount(amount);
        this.donationDate = donationDate != null ? donationDate : LocalDate.now();
        this.paymentMethod = paymentMethod != null ? paymentMethod : "Bank Transfer";
        this.transactionId = transactionId;
    }

    // ===== Getters and Setters =====
    public int getDonationId() { 
        return donationId; 
    }
    
    public void setDonationId(int donationId) { 
        this.donationId = donationId; 
    }

    public Waqif getWaqif() { 
        return waqif; 
    }
    
    public void setWaqif(Waqif waqif) { 
        this.waqif = waqif; 
    }

    public CashWaqf getCashWaqf() { 
        return cashWaqf; 
    }
    
    public void setCashWaqf(CashWaqf cashWaqf) { 
        this.cashWaqf = cashWaqf; 
    }

    public double getAmount() { 
        return amount; 
    }
    
    public void setAmount(double amount) { 
    	if (amount < 0) 
    		throw new IllegalArgumentException("Amount cannot be negative");
    	this.amount = amount;
     
    	
    }

    public LocalDate getDonationDate() { 
        return donationDate; 
    }
    
    public void setDonationDate(LocalDate donationDate) { 
        this.donationDate = donationDate; 
    }

    public String getPaymentMethod() { 
        return paymentMethod; 
    }
    
    public void setPaymentMethod(String paymentMethod) { 
        this.paymentMethod = paymentMethod; 
    }

    public String getTransactionId() { 
        return transactionId; 
    }
    
    public void setTransactionId(String transactionId) { 
        this.transactionId = transactionId; 
    }

    // ===== Helper Methods =====
    public String getReceiptInfo() {
        return "Donation #" + donationId + 
               " | Amount: " + String.format("%.2f", amount) + 
               " | Date: " + donationDate +
               " | Transaction: " + (transactionId != null ? transactionId : "N/A");
    }

    public boolean isCompleted() {
        return transactionId != null && !transactionId.isEmpty();
    }

    @Override
    public String toString() {
        return "WaqfDonation{" +
                "donationId=" + donationId +
                ", waqif=" + (waqif != null ? waqif.getFullName() : "null") +
                ", cashWaqf=" + (cashWaqf != null ? cashWaqf.getWaqfID() : "null") +
                ", amount=" + amount +
                ", donationDate=" + donationDate +
                ", paymentMethod='" + paymentMethod + '\'' +
                ", transactionId='" + transactionId + '\'' +
                '}';
    }
}