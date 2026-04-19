package model;

public class Student {

    private int studentId;
    private String name;
    private int rankPosition;
    private double cgpa;
    private String skills;
    private String preferences;

    public Student() {
    }

    public Student(int studentId, String name, int rankPosition, double cgpa, String skills, String preferences) {
        this.studentId = studentId;
        this.name = name;
        this.rankPosition = rankPosition;
        this.cgpa = cgpa;
        this.skills = skills;
        this.preferences = preferences;
    }

    public int getStudentId() {
        return studentId;
    }

    public void setStudentId(int studentId) {
        this.studentId = studentId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getRankPosition() {
        return rankPosition;
    }

    public void setRankPosition(int rankPosition) {
        this.rankPosition = rankPosition;
    }

    public double getCgpa() {
        return cgpa;
    }

    public void setCgpa(double cgpa) {
        this.cgpa = cgpa;
    }

    public String getSkills() {
        return skills;
    }

    public void setSkills(String skills) {
        this.skills = skills;
    }

    public String getPreferences() {
        return preferences;
    }

    public void setPreferences(String preferences) {
        this.preferences = preferences;
    }
}