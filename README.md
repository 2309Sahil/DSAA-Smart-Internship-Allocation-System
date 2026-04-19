# Smart Internship Allocation Engine

A professional N-Tier Java application designed to automate and optimize the internship recruitment process. The system uses a **Max-Heap** data structure to ensure a merit-based "Student-First" allocation, paired with a **Skill-Matching algorithm** to ensure students are placed in roles that fit their technical profile.

## 🚀 Key Features

* **Meritocratic Allocation:** Prioritizes students using a combined score of CGPA and Academic Rank.
* **Skill-Matching Logic:** Calculates compatibility scores between student profiles and company requirements.
* **Dynamic Reallocation:** If a student rejects an offer, the system instantly triggers a "next-best" match using a Max-Heap Priority Queue.
* **The "One Internship Rule":** Prevents offer hoarding by locking a student's ability to apply once they have a pending or accepted offer.
* **Dual-Portal System:** Dedicated dashboards for Admins (system control) and Students (offer management).

---

## 🛠️ Technical Architecture

The project is built using a clean **N-Tier Architecture** to ensure scalability and separation of concerns:

* **UI Layer:** Java Swing (Modern Slate/Tailwind-inspired color palette).
* **Service Layer:** Business logic handling the core allocation and reallocation workflows.
* **Utility Layer (The Brain):**
    * `HeapManager`: Manages student priority using a Max-Heap.
    * `SkillMatcher`: Normalizes and intersects skill sets for contextual matching.
* **Data Layer:** Persistent storage via **MySQL** with **JDBC** connectivity.

---

## 🧠 The Algorithm

### 1. Priority Scoring
The system ranks students by a combined merit score. Using a Max-Heap, we ensure that the student with the highest priority is always processed first.
$$Score = CGPA + \left(\frac{1}{RankPosition}\right)$$

### 2. Matching Logic
The `SkillMatcher` parses comma-separated skills, normalizes them to lowercase, and calculates an intersection score:
* **Input:** Student Skills ("Java, Python") & Company Requirements ("Java, SQL")
* **Processing:** Case-insensitive string tokenization.
* **Output:** Integer score (e.g., `1` match found).

---

## 📊 Database Schema

To run this project, ensure your MySQL database has the following tables:

```sql
-- Students Table
CREATE TABLE students (
    student_id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100),
    rank_position INT,
    cgpa DOUBLE,
    skills TEXT,
    preferences TEXT
);

-- Companies Table
CREATE TABLE companies (
    company_id INT PRIMARY KEY AUTO_INCREMENT,
    company_name VARCHAR(100),
    role VARCHAR(100),
    seats INT,
    skills_required TEXT,
    duration VARCHAR(50)
);

-- Allocations Table
CREATE TABLE allocations (
    allocation_id INT PRIMARY KEY AUTO_INCREMENT,
    student_id INT,
    company_id INT,
    allocation_status ENUM('ALLOCATED', 'ACCEPTED'),
    FOREIGN KEY (student_id) REFERENCES students(student_id),
    FOREIGN KEY (company_id) REFERENCES companies(company_id)
);
```

---

## ⚙️ Setup & Installation

1.  **Clone the Repo:** `git clone https://github.com/yourusername/internship-allocation.git`
2.  **Database Config:** Update the `DBConnection.java` file with your MySQL URL, username, and password.
3.  **Add Driver:** Ensure the `mysql-connector-java.jar` is added to your project's build path.
4.  **Run:** Execute the `Main.java` file to launch the Admin Dashboard.

---

## 📈 Future Roadmap
* [ ] Integration of automated email notifications using JavaMail API.
* [ ] Exporting allocation results to PDF/Excel reports.
* [ ] Machine Learning implementation for predictive hiring trends.
