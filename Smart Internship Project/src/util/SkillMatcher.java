package util;

import model.Student;
import model.Company;

public class SkillMatcher {

    // Calculate skill match score
    public static int calculateSkillScore(Student student, Company company) {

        String studentSkills = student.getSkills().toLowerCase();
        String companySkills = company.getRequiredSkills().toLowerCase();

        String[] studentSkillList = studentSkills.split(",");
        String[] companySkillList = companySkills.split(",");

        int score = 0;

        for (String sSkill : studentSkillList) {
            for (String cSkill : companySkillList) {

                if (sSkill.trim().equals(cSkill.trim())) {
                    score++;
                }

            }
        }

        return score;
    }
}