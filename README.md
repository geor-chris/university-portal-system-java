# University Management System

A 3-tier web application developed in Java for managing university courses, professor assignments, and student grades. The system provides role-based access control for **Students**, **Professors**, and **Secretaries**.

## Features & Role Capabilities

* **Authentication & Security:**
  * Role-based login mechanism (`username` & `password`).
  * Secure password storage using **salted and hashed** passwords.
  * Full HTTP session management with session invalidation and cache clearing upon logout.

* **Secretary Portal:**
  * View all available courses (course title, semester, assigned professor).
  * Assign courses to professors.
  * Manage student, professor, and course records.

* **Professor Portal:**
  * View grade lists per course for already graded subjects.
  * Submit and register grades for students in ungraded courses assigned to them.

* **Student Portal:**
  * View grades per individual course.
  * View grades filtered by semester.
  * View overall grade average across all examined courses.

## Architecture & Tech Stack

The project follows a **3-tier architecture**:
1. **Presentation Tier:** Dynamically generated HTML5 and CSS3 rendered directly via Java Servlets (`HttpServletResponse` / `PrintWriter`).
2. **Application / Business Logic Tier:** Java Servlets (`StudentServlet`, `ProfessorServlet`, `SecretaryServlet`), Object-Oriented Domain Model (`Users`, `Students`, `Professors`, `Secretaries`, `Courses`, `Grades`).
3. **Data Tier:** Relational Database (MySQL) connected via **JDBC**.

* **Server:** Apache Tomcat

## Getting Started (How to Run Locally)

### Prerequisites
* JDK (Java Development Kit)
* Apache Tomcat Server
* MySQL or PostgreSQL Server

### Setup Steps
1. Import the database schema and sample records using `JavaBasics/src/main/resources/uni_db.sql`.
2. Configure your database credentials (URL, username, password) in the database connection class.
3. Deploy the project to Apache Tomcat and run the application.

## Demo / Test Accounts

You can use the following pre-configured credentials (included in the database seed script) to test the different user roles and permissions:

| Role | Username | Password | Access / Capabilities |
| :--- | :--- | :--- | :--- |
| **Secretary** | `secretary` | `12345` | Manage courses, assign professors, view enrollments |
| **Professor** | `professor` | `12345` | View assigned courses, submit and edit student grades |
| **Student** | `student` | `12345` | View course grades, semester breakdown, and GPA |
| **Secretary** | `secretary2` | `12345` | Manage courses, assign professors, view enrollments |
| **Professor** | `professor2` | `12345` | View assigned courses, submit and edit student grades |
| **Student** | `student2` | `12345` | View course grades, semester breakdown, and GPA |

