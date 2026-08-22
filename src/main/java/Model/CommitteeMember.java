package Model;
import java.util.Objects;
public class CommitteeMember extends User {
	private int memberID;
    private String specialization;

    public CommitteeMember() {

    }

    public CommitteeMember(int userID, String fullName, String email,
            String username, String password, String phoneNumber,
            int memberID, String specialization) {

        super(userID, fullName, email, username, password, phoneNumber);

        this.memberID = memberID;
        this.specialization = specialization;
    }

    public int getMemberID() {
        return memberID;
    }

    public void setMemberID(int memberID) {
        this.memberID = memberID;
    }

    public String getSpecialization() {
        return specialization;
    }

    public void setSpecialization(String specialization) {
        this.specialization = specialization;
    }

    @Override
	public String toString() {
		return "CommitteeMember [memberID=" + memberID + ", specialization=" + specialization + "]";
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

