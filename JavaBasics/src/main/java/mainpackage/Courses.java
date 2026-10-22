package mainpackage;
import java.util.ArrayList;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Courses {
    private  String name;
    private  String description;
    private int semester;
    private String department;
    int ects;

    private Professors assignedProfessor;
    private ArrayList<Students> enrolledStudents = new ArrayList<>();

    public Courses(String name, String description, int ects, int semester, String department) {
        this.name = name;
        this.description = description;
        this.ects = ects;
        this.semester = semester;
        this.department = department;
    }

    Courses(){}
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public Professors getAssignedProfessor() {
        return assignedProfessor;
    }
    public void setAssignedProfessor(Professors assignedProfessor) {
        this.assignedProfessor = assignedProfessor;
    }

    public ArrayList<Students> getEnrolledStudents() {
        return enrolledStudents;
    }

    public String toString() {
        return name + " " + description + " " + ects;
    }


    public String getDescription() {
        return description;
    }

    public int getEts() {
        return ects;
    }


    public void setDescription(String description) {
        this.description = description;
    }

    public void setEts(int ects) {
        this.ects = ects;
    }
    // Μέθοδος για την αρχική εισαγωγή του μαθήματος από τη Γραμματεία
    public void addCourseToDB() {
        String sql = "INSERT INTO courses (course_name, course_description, ects, professor_id, semester, department) VALUES (?, ?, ?, NULL, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, this.name);
            pstmt.setString(2, this.description);
            pstmt.setInt(3, this.ects);
            pstmt.setInt(4, this.semester);
            pstmt.setString(5, this.department);

            pstmt.executeUpdate();
            System.out.println("Το μάθημα '" + this.name + "' αποθηκεύτηκε επιτυχώς στη βάση!");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Μέθοδος για την ενημέρωση του καθηγητή όταν γίνεται η ανάθεση
    public void updateProfessorInDB(String professorUsername) {
        String sqlGetId = "SELECT id FROM users WHERE username = ?";
        String sqlUpdate = "UPDATE courses SET professor_id = ? WHERE course_name = ?";

        try (Connection conn = DatabaseConnection.getConnection()) {
            int profId = -1;
            // Βρίσκουμε το auto-increment ID του καθηγητή βάσει του username του
            try (PreparedStatement pstmtId = conn.prepareStatement(sqlGetId)) {
                pstmtId.setString(1, professorUsername);
                try (ResultSet rs = pstmtId.executeQuery()) {
                    if (rs.next()) profId = rs.getInt("id");
                }
            }

            //Ενημερώνουμε τον πίνακα courses
            if (profId != -1) {
                try (PreparedStatement pstmtUpdate = conn.prepareStatement(sqlUpdate)) {
                    pstmtUpdate.setInt(1, profId);
                    pstmtUpdate.setString(2, this.name);
                    pstmtUpdate.executeUpdate();
                    System.out.println("Η βάση ενημερώθηκε: Ο καθηγητής ανατέθηκε στο μάθημα " + this.name);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
