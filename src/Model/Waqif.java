package Model;
import java.util.Objects;
public class Waqif extends User {
	private int waqifID;
    private double totalWaqfAmount;

    public Waqif() {

    }

    public Waqif(int userID, String fullName, String email,String username, String password, String phoneNumber,
            int waqifID, double totalWaqfAmount) {
        super(userID, fullName, email, username, password, phoneNumber);

        this.waqifID = waqifID;
        this.setTotalWaqfAmount(totalWaqfAmount);
    }

    public int getWaqifID() {
        return waqifID;
    }

    public void setWaqifID(int waqifID) {
        this.waqifID = waqifID;
    }

    public double getTotalWaqfAmount() {
        return totalWaqfAmount;
    }

    public void setTotalWaqfAmount(double totalWaqfAmount) {
    	if (totalWaqfAmount < 0) 
    		throw new IllegalArgumentException("Total Waqf Amount cannot be negative");
        this.totalWaqfAmount = totalWaqfAmount;
    }
    
    @Override
	public String toString() {
		return "Waqif [waqifID=" + waqifID + ", totalWaqfAmount=" + totalWaqfAmount + "]";
	}
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return userID == user.userID;
    }

    @Override
    public int hashCode() {
        return Objects.hash(userID);  // ← Uses Objects.hash()
    }
}


