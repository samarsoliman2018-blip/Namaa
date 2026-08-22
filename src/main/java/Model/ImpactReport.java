package Model;

import java.time.LocalDate;

public class ImpactReport {
    private int reportID;
    private Project project;
    private ProjectAssessment assessment;
    private NamaaIndex namaaIndex;
    private LocalDate reportDate;
    private String reportSummary;

    public ImpactReport() {
        this.reportDate = LocalDate.now();
    }

    // ===== CONSTRUCTOR WITH ALL FIELDS =====
    public ImpactReport(int reportID, Project project, ProjectAssessment assessment,
                        NamaaIndex namaaIndex, LocalDate reportDate, String reportSummary) {
        this.reportID = reportID;
        this.project = project;
        this.assessment = assessment;
        this.namaaIndex = namaaIndex;
        this.reportDate = reportDate;
        this.reportSummary = reportSummary;
    }

    // ===== GETTERS AND SETTERS =====
    public int getReportID() { return reportID; }
    public void setReportID(int reportID) { this.reportID = reportID; }

    public Project getProject() { return project; }
    public void setProject(Project project) { this.project = project; }

    public ProjectAssessment getAssessment() { return assessment; }
    public void setAssessment(ProjectAssessment assessment) { this.assessment = assessment; }

    public NamaaIndex getNamaaIndex() { return namaaIndex; }
    public void setNamaaIndex(NamaaIndex namaaIndex) { this.namaaIndex = namaaIndex; }

    public LocalDate getReportDate() { return reportDate; }
    public void setReportDate(LocalDate reportDate) { this.reportDate = reportDate; }

    public String getReportSummary() { return reportSummary; }
    public void setReportSummary(String reportSummary) { this.reportSummary = reportSummary; }

    @Override
    public String toString() {
        return "ImpactReport{" +
                "reportID=" + reportID +
                ", project=" + (project != null ? project.getProjectName() : "null") +
                ", reportDate=" + reportDate +
                '}';
    }
}