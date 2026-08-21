package Model;

import java.time.LocalDate;

public class AdviceTraining {
    private int trainingId;
    private Beneficiary beneficiary;
    private String title;
    private String content;
    private String type; // "Advice", "Training", "Workshop", "Guidance"
    private LocalDate sentDate;
    private boolean isRead;
    private String videoUrl; // For training videos (optional)
    private String attachmentUrl; // For additional resources (optional)

    public AdviceTraining() {
        this.sentDate = LocalDate.now();
        this.isRead = false;
        this.type = "Advice";
    }

    public AdviceTraining(int trainingId, Beneficiary beneficiary, String title, 
                            String content, String type, LocalDate sentDate, 
                            boolean isRead, String videoUrl, String attachmentUrl) {
        this.trainingId = trainingId;
        this.beneficiary = beneficiary;
        this.title = title;
        this.content = content;
        this.type = type;
        this.sentDate = sentDate;
        this.isRead = isRead;
        this.videoUrl = videoUrl;
        this.attachmentUrl = attachmentUrl;
    }

    // ===== Getters and Setters =====
    public int getTrainingId() {
        return trainingId;
    }

    public void setTrainingId(int trainingId) {
        this.trainingId = trainingId;
    }

    public Beneficiary getBeneficiary() {
        return beneficiary;
    }

    public void setBeneficiary(Beneficiary beneficiary) {
        this.beneficiary = beneficiary;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public LocalDate getSentDate() {
        return sentDate;
    }

    public void setSentDate(LocalDate sentDate) {
        this.sentDate = sentDate;
    }

    public boolean isRead() {
        return isRead;
    }

    public void setRead(boolean read) {
        isRead = read;
    }

    public String getVideoUrl() {
        return videoUrl;
    }

    public void setVideoUrl(String videoUrl) {
        this.videoUrl = videoUrl;
    }

    public String getAttachmentUrl() {
        return attachmentUrl;
    }

    public void setAttachmentUrl(String attachmentUrl) {
        this.attachmentUrl = attachmentUrl;
    }

    // ===== Helper Methods =====
    public void markAsRead() {
        this.isRead = true;
    }

    public boolean isUnread() {
        return !this.isRead;
    }

    public String getTypeIcon() {
        switch (type.toLowerCase()) {
            case "advice": return "💡";
            case "training": return "📚";
            case "workshop": return "🔧";
            case "guidance": return "🤝";
            default: return "📝";
        }
    }

    @Override
    public String toString() {
        return "AdviceAndTraining{" +
                "trainingId=" + trainingId +
                ", beneficiary=" + (beneficiary != null ? beneficiary.getFullName() : "null") +
                ", title='" + title + '\'' +
                ", type='" + type + '\'' +
                ", sentDate=" + sentDate +
                ", isRead=" + isRead +
                '}';
    }
}