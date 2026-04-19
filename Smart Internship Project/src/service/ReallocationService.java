package service;

import database.DBConnection;
import model.Company;
import model.Student;
import util.HeapManager;
import util.SkillMatcher;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class ReallocationService {

    public void reallocateSeat(int companyId) {
        
        try {
            Connection conn = DBConnection.getConnection();

            // 1. Fetch the specific company details for the newly opened seat
            String companyQuery = "SELECT * FROM companies WHERE company_id = ?";
            PreparedStatement psCompany = conn.prepareStatement(companyQuery);
            psCompany.setInt(1, companyId);
            ResultSet rsCompany = psCompany.executeQuery();

            Company company = null;
            if (rsCompany.next()) {
                company = new Company(
                        rsCompany.getInt("company_id"),
                        rsCompany.getString("company_name"),
                        rsCompany.getString("role"),
                        rsCompany.getInt("seats"), 
                        rsCompany.getString("required_skills"),
                        rsCompany.getString("duration")
                );
            }

            if (company == null) return; // Exit if company doesn't exist

            // 2. Fetch only NOT_ALLOCATED students to find a replacement
            String studentQuery = "SELECT s.* FROM students s " +
                                  "LEFT JOIN allocations a ON s.student_id = a.student_id " +
                                  "WHERE a.allocation_status = 'NOT_ALLOCATED' OR a.student_id IS NULL";
            PreparedStatement psStudent = conn.prepareStatement(studentQuery);
            ResultSet rsStudents = psStudent.executeQuery();

            // Load them into the Max Heap to ensure the smartest unallocated student gets first dibs
            HeapManager heapManager = new HeapManager();
            while (rsStudents.next()) {
                Student s = new Student(
                        rsStudents.getInt("student_id"),
                        rsStudents.getString("name"),
                        rsStudents.getInt("rank_position"),
                        rsStudents.getDouble("cgpa"),
                        rsStudents.getString("skills"),
                        rsStudents.getString("preferences")
                );
                heapManager.addStudent(s);
            }

            // 3. Search for the next best match
            boolean reallocated = false;
            
            while (!heapManager.isEmpty()) {
                
                Student unallocatedStudent = heapManager.getNextStudent();
                int matchScore = SkillMatcher.calculateSkillScore(unallocatedStudent, company);

                if (matchScore > 0) {
                    
                    // Match found! Update their allocation status in the database
                    String updateQuery = "UPDATE allocations SET company_id = ?, allocation_status = 'ALLOCATED' " +
                                         "WHERE student_id = ?";
                    PreparedStatement psUpdate = conn.prepareStatement(updateQuery);
                    psUpdate.setInt(1, companyId);
                    psUpdate.setInt(2, unallocatedStudent.getStudentId());

                    int rowsAffected = psUpdate.executeUpdate();

                    // If they didn't have an existing NOT_ALLOCATED row to update, insert a new one
                    if (rowsAffected == 0) {
                        String insertQuery = "INSERT INTO allocations (student_id, company_id, allocation_status) VALUES (?, ?, 'ALLOCATED')";
                        PreparedStatement psInsert = conn.prepareStatement(insertQuery);
                        psInsert.setInt(1, unallocatedStudent.getStudentId());
                        psInsert.setInt(2, companyId);
                        psInsert.executeUpdate();
                    }

                    System.out.println("Seat successfully reallocated to Student ID: " + unallocatedStudent.getStudentId());
                    reallocated = true;
                    
                    // Break the loop so we only fill the ONE rejected seat
                    break; 
                }
            }

            // 4. Fallback: If no unallocated student matches the skills, return the seat to the company
            if (!reallocated) {
                String updateSeatsQuery = "UPDATE companies SET seats = seats + 1 WHERE company_id = ?";
                PreparedStatement psSeats = conn.prepareStatement(updateSeatsQuery);
                psSeats.setInt(1, companyId);
                psSeats.executeUpdate();
                System.out.println("No matching students found. Seat returned to company.");
            }

        } catch (Exception e) {
            System.out.println("Error during reallocation process.");
            e.printStackTrace();
        }
    }
}