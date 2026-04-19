package model;

public class Application {

    private int applicationId;
    private int studentId;
    private int companyId;
    private String status;

    public Application() {
    }

    public Application(int applicationId, int studentId, int companyId, String status) {
        this.applicationId = applicationId;
        this.studentId = studentId;
        this.companyId = companyId;
        this.status = status;
    }

    public int getApplicationId() {
        return applicationId;
    }

    public void setApplicationId(int applicationId) {
        this.applicationId = applicationId;
    }

    public int getStudentId() {
        return studentId;
    }

    public void setStudentId(int studentId) {
        this.studentId = studentId;
    }

    public int getCompanyId() {
        return companyId;
    }

    public void setCompanyId(int companyId) {
        this.companyId = companyId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}