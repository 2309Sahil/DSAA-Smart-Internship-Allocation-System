package service;

import database.DBConnection;
import model.Company;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class CompanyService {

    // Add a company / internship
    public void addCompany(Company company) {

        String query = "INSERT INTO companies(company_name, role, seats, required_skills, duration) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {

            ps.setString(1, company.getCompanyName());
            ps.setString(2, company.getRole());
            ps.setInt(3, company.getSeats());
            ps.setString(4, company.getRequiredSkills());
            ps.setString(5, company.getDuration());

            ps.executeUpdate();

            System.out.println("Company added successfully.");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Fetch all companies
    public List<Company> getAllCompanies() {

        List<Company> companies = new ArrayList<>();

        String query = "SELECT * FROM companies";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(query);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {

                Company company = new Company(
                        rs.getInt("company_id"),
                        rs.getString("company_name"),
                        rs.getString("role"),
                        rs.getInt("seats"),
                        rs.getString("required_skills"),
                        rs.getString("duration")
                );

                companies.add(company);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return companies;
    }

    // Update company seats
    public void updateSeats(int companyId, int seats) {

        String query = "UPDATE companies SET seats = ? WHERE company_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {

            ps.setInt(1, seats);
            ps.setInt(2, companyId);

            ps.executeUpdate();

            System.out.println("Company seats updated.");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}