package Model;
import java.util.Objects;
public class User {

	protected int userID;
	protected String fullName;
	protected String email;
	protected String username;
	protected String password;
	protected String phoneNumber;

	public User() {

	}

	public User(int userID, String fullName, String email, String username, String password, String phoneNumber) {

		this.userID = userID;
		this.fullName = fullName;
		this.email = email;
		this.username = username;
		this.password = password;
		this.phoneNumber = phoneNumber;
	}

	public int getUserID() {
		return userID;
	}

	public void setUserID(int userID) {
		this.userID = userID;
	}

	public String getFullName() {
		return fullName;
	}

	public void setFullName(String fullName) {
		this.fullName = fullName;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getPhoneNumber() {
		return phoneNumber;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	@Override
	public String toString() {
		return "User [userID=" + userID + ", fullName=" + fullName + ", email=" + email + ", username=" + username
				+ ", password=" + password + ", phoneNumber=" + phoneNumber + "]";
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

