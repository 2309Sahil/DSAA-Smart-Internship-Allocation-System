package service;

import database.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class AllocationService {

    public void runAllocation() throws Exception {
        try (Connection conn = DBConnection.getConnection()) {
            
            // 1. Get all UNALLOCATED students, strictly ordered by Merit (Rank 1 is best)
            // This ensures top students get their first choice before lower-ranked students.
            String studentQuery = "SELECT student_id, skills FROM students WHERE student_id NOT IN " +
                                  "(SELECT student_id FROM allocations WHERE allocation_status IN ('ALLOCATED', 'ACCEPTED')) " +
                                  "ORDER BY rank_position ASC";
            
            PreparedStatement psStudents = conn.prepareStatement(studentQuery);
            ResultSet rsStudents = psStudents.executeQuery();

            while (rsStudents.next()) {
                int studentId = rsStudents.getInt("student_id");
                String studentSkills = rsStudents.getString("skills");
                if (studentSkills == null) studentSkills = "";
                studentSkills = studentSkills.toLowerCase();

                boolean isAllocated = false;

                // =========================================================
                // PHASE 1: Check Explicit Applications (High Priority)
                // =========================================================
                String appQuery = "SELECT c.company_id, c.required_skills, c.seats FROM applications a " +
                                  "JOIN companies c ON a.company_id = c.company_id " +
                                  "WHERE a.student_id = ? AND c.seats > 0 " +
                                  "ORDER BY a.application_date ASC"; // Try their earliest application first
                
                PreparedStatement psApps = conn.prepareStatement(appQuery);
                psApps.setInt(1, studentId);
                ResultSet rsApps = psApps.executeQuery();

                while (rsApps.next()) {
                    int companyId = rsApps.getInt("company_id");
                    String reqSkills = rsApps.getString("required_skills");
                    
                    // If they specifically applied, and they have at least some matching skills, give it to them!
                    if (checkSkillsMatch(studentSkills, reqSkills)) {
                        assignSeat(conn, studentId, companyId);
                        isAllocated = true;
                        break; // Move to the next student
                    }
                }

                // =========================================================
                // PHASE 2: Fallback to General Pool (Smart Greedy Match)
                // =========================================================
                // If they didn't apply anywhere, or got rejected from their top choices,
                // we find the next available company that fits their skills.
                if (!isAllocated) {
                    String companyQuery = "SELECT company_id, required_skills, seats FROM companies WHERE seats > 0";
                    PreparedStatement psCompanies = conn.prepareStatement(companyQuery);
                    ResultSet rsCompanies = psCompanies.executeQuery();

                    while (rsCompanies.next()) {
                        int companyId = rsCompanies.getInt("company_id");
                        String reqSkills = rsCompanies.getString("required_skills");

                        if (checkSkillsMatch(studentSkills, reqSkills)) {
                            assignSeat(conn, studentId, companyId);
                            break; // Move to the next student
                        }
                    }
                }
            }
        }
    }

    // Helper Method: Checks if the student has the skills the company wants
    private boolean checkSkillsMatch(String studentSkills, String requiredSkills) {
        if (requiredSkills == null || requiredSkills.trim().isEmpty()) return true; // Company requires no specific skills
        if (studentSkills.isEmpty()) return false; // Student has no skills, but company wants some

        String[] reqArray = requiredSkills.toLowerCase().split(",");
        for (String req : reqArray) {
            // If the student has even one of the required skills, we consider it a match.
            // (You can change this to require ALL skills if you want the algorithm to be stricter!)
            if (studentSkills.contains(req.trim())) {
                return true;
            }
        }
        return false;
    }

    // Helper Method: Safely writes the allocation to the DB and updates seat counts
    private void assignSeat(Connection conn, int studentId, int companyId) throws Exception {
        // 1. Create the allocation record
        String insertQuery = "INSERT INTO allocations (student_id, company_id, allocation_status) VALUES (?, ?, 'ALLOCATED')";
        PreparedStatement psInsert = conn.prepareStatement(insertQuery);
        psInsert.setInt(1, studentId);
        psInsert.setInt(2, companyId);
        psInsert.executeUpdate();

        // 2. Decrease the company's available seats by 1
        String updateSeats = "UPDATE companies SET seats = seats - 1 WHERE company_id = ?";
        PreparedStatement psUpdate = conn.prepareStatement(updateSeats);
        psUpdate.setInt(1, companyId);
        psUpdate.executeUpdate();
    }
}