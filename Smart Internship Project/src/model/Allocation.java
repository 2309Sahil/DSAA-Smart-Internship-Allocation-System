package model;

public class Allocation {

    private int allocationId;
    private int studentId;
    private int companyId;
    private String allocationStatus;

    public Allocation() {
    }

    public Allocation(int allocationId, int studentId, int companyId, String allocationStatus) {
        this.allocationId = allocationId;
        this.studentId = studentId;
        this.companyId = companyId;
        this.allocationStatus = allocationStatus;
    }

    public int getAllocationId() {
        return allocationId;
    }

    public void setAllocationId(int allocationId) {
        this.allocationId = allocationId;
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

    public String getAllocationStatus() {
        return allocationStatus;
    }

    public void setAllocationStatus(String allocationStatus) {
        this.allocationStatus = allocationStatus;
    }
}