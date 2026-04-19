package util;

import model.Student;


import java.util.PriorityQueue;
import java.util.Comparator;

public class HeapManager {

    // Priority queue (Max Heap)
    private PriorityQueue<Student> studentHeap;

    public HeapManager() {

        studentHeap = new PriorityQueue<>(new Comparator<Student>() {

            @Override
            public int compare(Student s1, Student s2) {

                double score1 = calculateCombinedScore(s1);
                double score2 = calculateCombinedScore(s2);

                return Double.compare(score2, score1); // max heap
            }
        });
    }

    // Add student to heap
    public void addStudent(Student student) {
        studentHeap.add(student);
    }

    // Get highest priority student
    public Student getNextStudent() {
        return studentHeap.poll();
    }

    // Check if heap is empty
    public boolean isEmpty() {
        return studentHeap.isEmpty();
    }

    // Combined priority score
    private double calculateCombinedScore(Student student) {

        double cgpaScore = student.getCgpa();

        // Rank weight (lower rank is better)
        double rankScore = 1.0 / student.getRankPosition();

        return cgpaScore + rankScore;
    }
}