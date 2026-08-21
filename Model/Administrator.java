package Model;
import java.util.Objects;

public class Administrator extends User {
	private int adminID;

    public Administrator() {

    }

    public Administrator(int userID, String fullName, String email,
            String username, String password, String phoneNumber,
            int adminID) {

        super(userID, fullName, email, username, password, phoneNumber);

        this.adminID = adminID;
    }

    public int getAdminID() {
        return adminID;
    }

    public void setAdminID(int adminID) {
        this.adminID = adminID;
    }
    
    
    @Override
   	public String toString() {
   		return "Administrator [adminID=" + adminID + "]";
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