package model;

public class Company {

    private int companyId;
    private String companyName;
    private String role;
    private int seats;
    private String requiredSkills;
    private String duration;

    public Company() {
    }

    public Company(int companyId, String companyName, String role, int seats, String requiredSkills, String duration) {
        this.companyId = companyId;
        this.companyName = companyName;
        this.role = role;
        this.seats = seats;
        this.requiredSkills = requiredSkills;
        this.duration = duration;
    }

    public int getCompanyId() {
        return companyId;
    }

    public void setCompanyId(int companyId) {
        this.companyId = companyId;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public int getSeats() {
        return seats;
    }

    public void setSeats(int seats) {
        this.seats = seats;
    }

    public String getRequiredSkills() {
        return requiredSkills;
    }

    public void setRequiredSkills(String requiredSkills) {
        this.requiredSkills = requiredSkills;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }
}