# Smart Internship Allocation System 🎓

An automated, merit-based placement platform built with Core Java, Java Swing, and MySQL. This system eliminates manual allocation bottlenecks by using a **Greedy Algorithm** and **Priority-based processing** to seamlessly match students with companies based on academic rank and technical skills.

## 🚀 Key Features
* **Dual-Portal Interface:** Secure login for Admins (Management) and Students (Candidates).
* **Two-Phase Smart Algorithm:** 1. *High Priority:* Processes direct student applications.
  2. *Greedy Fallback:* Automatically matches unassigned students to the best available roles based on skills.
* **Strict Meritocracy:** Processes students strictly by their academic rank.
* **Dynamic Reallocation:** Enforces a "One Internship Rule". If a student rejects an offer, the system instantly restocks the seat and re-runs the algorithm to pass it to the next deserving candidate.
* **Opportunities Board:** Students can actively browse open roles and submit custom applications with their resume links.

## 🛠️ Tech Stack
* **Frontend:** Java Swing 
* **Backend:** Core Java (Data Structures & Algorithms)
* **Database:** MySQL (Relational models, Foreign Keys, Cascade Deletes)

## 🧠 DSA Concepts Applied
* **Greedy Algorithm:** Matches students to their optimal local choice instantly ($O(N \times M)$ worst-case).
* **Priority Processing (Conceptual Max-Heap):** Utilizes optimized DB indexing (`ORDER BY rank ASC`) to mimic an Extract-Max operation, ensuring $O(N \log N)$ sorting efficiency.
* **Array Traversal & Intersection:** Parses dynamic strings into arrays to determine skill matches via linear search.

## ⚙️ How to Run
1. Clone this repository.
2. Execute the provided `.sql` script in MySQL Workbench to create the database schema.
3. Add the `mysql-connector-java.jar` file to your project build path.
4. Update the `DBConnection.java` file with your MySQL username and password.
5. Run the `AppLauncher.java` (or your main file) to launch the GUI!
