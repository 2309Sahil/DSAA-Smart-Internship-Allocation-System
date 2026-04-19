package service;

import database.DBConnection;
import model.Student;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class StudentService {

    // Add a student to database
    public void addStudent(Student student) {

        String query = "INSERT INTO students(name, rank_position, cgpa, skills, preferences) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {

            ps.setString(1, student.getName());
            ps.setInt(2, student.getRankPosition());
            ps.setDouble(3, student.getCgpa());
            ps.setString(4, student.getSkills());
            ps.setString(5, student.getPreferences());

            ps.executeUpdate();

            System.out.println("Student added successfully.");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Fetch all students from database
    public List<Student> getAllStudents() {

        List<Student> students = new ArrayList<>();

        String query = "SELECT * FROM students";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(query);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {

                Student student = new Student(
                        rs.getInt("student_id"),
                        rs.getString("name"),
                        rs.getInt("rank_position"),
                        rs.getDouble("cgpa"),
                        rs.getString("skills"),
                        rs.getString("preferences")
                );

                students.add(student);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return students;
    }
}