package mainpackage;
import java.util.ArrayList;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.ResultSet;
public class Students extends Users {

    private final int id;
    private int semester;

    private ArrayList<Grades> myGrades = new ArrayList<>();
    //δέχεται κανονικά int id και int semester στο τέλος
    public Students(String username, String name, String surname, String department, int id, int semester,String password) {
        // Στέλνουμε το "Student" κατευθείαν στη μάνα (Users)
        super(username, name, surname, department, "Student",password);
        this.id = id;
        this.semester = semester;
    }

    // όχι setter καθώς δεν πρέπει να αλλάξει
    public int getRegistrationNumber() {
        return id;
    }
    public int getSemester() {
        return semester;
    }
    public ArrayList<Grades> getMyGrades() { return myGrades; }
    public void addGrade(Grades grade) {
            myGrades.add(grade);
    }
    @Override
    public String getRole() {
        return "Student";
    }

    public void addStudentToDB() {
        // Καλούμε την addUsersToDB της μάνας (Users) για να σωθεί ο κορμός
        // και κρατάμε το ID που μας επιστρέφει
        int generatedId = super.addUsersToDB();

        if (generatedId != -1) {
            // Εφόσον πήραμε το ID, σώζουμε τα υπόλοιπα στον πίνακα students
            String sql = "INSERT INTO students (id, am, semester) VALUES (?, ?, ?)";

            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement pstmt = conn.prepareStatement(sql)) {

                pstmt.setInt(1, generatedId); // Το ID από τον πίνακα users
                pstmt.setInt(2, this.getRegistrationNumber()); // Το AM του φοιτητή
                pstmt.setInt(3, this.semester); // Το εξάμηνο

                pstmt.executeUpdate();
                System.out.println("Τα στοιχεία του φοιτητή ενημερώθηκαν επιτυχώς!");

            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }


    public void viewMyGrades() { //ενας μαθητης βλεπει ολους τους βαθμους του
        System.out.println("\n--- Βαθμολογίες φοιτητή: " + getSurname() + " ---");
        if (myGrades.isEmpty()) {
            System.out.println("Δεν υπάρχουν ακόμη καταχωρημένοι βαθμοί.");
        } else {
            for (Grades g : myGrades) {
                System.out.println("Μάθημα: " + g.getCourse().getName() + " | Βαθμός: " + g.getGrade());
            }
        }
    }
}