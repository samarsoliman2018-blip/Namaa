package Model;

import java.util.Objects;

public class Beneficiary extends User {
    private int beneficiaryID;
    private String education;
    private int experienceYears;
    private String targetGroup;  

    public Beneficiary() {
        super();
        this.targetGroup = "General";
    }

    public Beneficiary(int userID, String fullName, String email,
            String username, String password, String phoneNumber,
            int beneficiaryID, String education, int experienceYears, String targetGroup) {

        super(userID, fullName, email, username, password, phoneNumber);
        this.beneficiaryID = beneficiaryID;
        this.education = education;
        this.experienceYears = experienceYears;
        this.targetGroup = targetGroup;  // ← ADD THIS
    }

    // ===== GETTERS AND SETTERS =====
    public int getBeneficiaryID() { return beneficiaryID; }
    public void setBeneficiaryID(int beneficiaryID) { this.beneficiaryID = beneficiaryID; }

    public String getEducation() { return education; }
    public void setEducation(String education) { this.education = education; }

    public int getExperienceYears() { return experienceYears; }
    public void setExperienceYears(int experienceYears) { this.experienceYears = experienceYears; }

    // ===== NEW: Target Group =====
    public String getTargetGroup() { return targetGroup; }
    public void setTargetGroup(String targetGroup) { this.targetGroup = targetGroup; }

    @Override
    public String toString() {
        return "Beneficiary [beneficiaryID=" + beneficiaryID + 
               ", education=" + education + 
               ", experienceYears=" + experienceYears + 
               ", targetGroup=" + targetGroup + "]";
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
        return Objects.hash(userID);
    }
}