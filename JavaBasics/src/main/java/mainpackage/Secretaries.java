package mainpackage;

import java.util.ArrayList;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.ResultSet;
import java.util.Scanner;

public class Secretaries extends Users{

    static String input() {
        String input;
        while (true) {
            try {
                input = sc.nextLine();
                // Αν το πεδίο είναι εντελώς κενό (ή έχει μόνο κενά διαστήματα), πετάει σφάλμα
                if (input.trim().isEmpty()) {
                    throw new InputException("Το πεδίο δεν μπορεί να είναι κενό. Παρακαλώ πληκτρολογήστε κάτι.");
                } else {
                    return input; // Δέχεται πλέον τα πάντα: γράμματα, νούμερα, σύμβολα!
                }
            } catch (InputException e) {
                System.out.println(e.getMessage());
            }
        }
    }

    static int input1(){ //εισαγωγη ακεραιου με ελεγχο για τη σωστη μορφη δεδομενων

        String input;
        while(true){
            try {
                input = sc.nextLine();
                if (input.matches(".*[a-zA-Zα-ωΑ-Ω].*") || input.isEmpty()) {
                    throw new InputException("Παρακαλώ δώστε έναν έγκυρο αριθμό");
                }
                else{
                    return Integer.parseInt(input);
                }
            }catch (InputException e){System.out.println(e.getMessage());}
        }
    }

    static Scanner sc = new Scanner(System.in);
    private String office;
    private String work_hours;

    private static ArrayList<Students> gradedStudents = new ArrayList<>();
    private static ArrayList<Students> notGradedStudents = new ArrayList<>();

    public Secretaries(String username, String name, String surname, String department, String office, String work_hours, String password) {
        // Προσθέτουμε τον ρόλο "Secretary" κατευθείαν στην κλήση της Users
        super(username, name, surname, department, "Secretary", password);
        this.office = office;
        this.work_hours = work_hours;
    }

    public String getOffice() {
        return office;
    }
    public void setOffice(String office) {
        this.office = office;
    }
    public String getWork_hours() {
        return work_hours;
    }
    public void setWork_hours(String work_hours) {
        this.work_hours = work_hours;

    }

    @Override
    public String getRole() {
        return "Secretary";
    }

    public void addSecretaryToDB() {
        // 1. Αποθήκευση στον πίνακα users και λήψη του ID
        int generatedId = super.addUsersToDB();

        if (generatedId != -1) {
            // 2. Εισαγωγή στον πίνακα secretaries
            String sql = "INSERT INTO secretaries (id, office, work_hours) VALUES (?, ?, ?)";

            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement pstmt = conn.prepareStatement(sql)) {

                pstmt.setInt(1, generatedId);
                pstmt.setString(2, this.getOffice());     // Από το πεδίο office
                pstmt.setString(3, this.getWork_hours()); // Από το πεδίο work_hours

                pstmt.executeUpdate();
                System.out.println("Τα στοιχεία της γραμματείας αποθηκεύτηκαν επιτυχώς!");

            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public void createsProfessor(ArrayList<Professors> p){ //δημιουργεια καθηγητων
        while(true){
            System.out.println("Θέλετε να συνεχίσετε;(Ν/Ο)");
            String answer=sc.nextLine();
            if(!answer.equals("Ν") && !answer.equals("Ο")){
                System.out.println("Παρακαλώ απαντήστε με Ν (για ναι) ή Ο (για όχι)");

            }else if(answer.equals("Ο")) break;
            else{
                System.out.println("Δώστε το όνομα χρήστη");
                String username = input();
                System.out.println("Δώστε τον κωδικό");
                String password = input();
                System.out.println("Δώστε το μικρό όνομα");
                String name = input();
                System.out.println("Δώστε το επώνυμο");
                String surname = input();
                System.out.println("Δώστε το όνομα του τμήματος");
                String department = input();
                System.out.println("Δώστε το όνομα του αντικειμένου");
                String major = input();
                System.out.println("Δώστε το όνομα του γραφείου");
                String office = input();
                Professors professor=new Professors(username,name,surname,department,major,office,password);
                p.add(professor);
            }
        }
    }




    public void createsCourses(ArrayList<Courses> c){ //δημιουργεια μαθηματων
        while(true){
            System.out.println("Θέλετε να συνεχίσετε;(Ν/Ο)");
            String answer=sc.nextLine();
            if(!answer.equals("Ν") && !answer.equals("Ο")){
                System.out.println("Παρακαλώ απαντήστε με Ν (για ναι) ή Ο (για όχι)");

            }else if(answer.equals("Ο")) break;
            else{
                System.out.println("Δώστε το όνομα του μαθήματος");
                String name = input();
                System.out.println("Δώστε την περιγραφή");
                String description = input();
                System.out.println("Δώστε τις μονάδες ECTS");
                int ects = input1();
                System.out.println("Δώστε το Εξάμηνο");
                int semester = input1();
                System.out.println("Δώστε Τμήμα");
                String department = input();

                Courses course=new Courses(name,description,ects,semester,department);
                course.addCourseToDB();
                c.add(course);

            }
        }
    }



    public void assignStudent(Students student, Courses course, Grades grade) {
        student.addGrade(grade);
        if (gradedStudents.contains(student)) {
            gradedStudents.remove(student);
        }
        gradedStudents.add(student);

        //ΑΠΟΘΗΚΕΥΣΗ ΒΑΘΜΟΥ ΣΤΗ ΒΑΣΗ ΔΕΔΟΜΕΝΩΝ (grades)
        String sqlGetProfId = "SELECT id FROM users WHERE username = ?";
        String sqlInsertGrade = "INSERT INTO grades (am, course_name, bathmos, description, professor_id) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection()) {
            int profId = -1;
            // Βρίσκουμε το ID του καθηγητή που διδάσκει το μάθημα
            if (course.getAssignedProfessor() != null) {
                try (PreparedStatement pstmtId = conn.prepareStatement(sqlGetProfId)) {
                    pstmtId.setString(1, course.getAssignedProfessor().getUsername());
                    try (ResultSet rs = pstmtId.executeQuery()) {
                        if (rs.next()) profId = rs.getInt("id");
                    }
                }
            }

            // Εισαγωγή στον πίνακα grades
            try (PreparedStatement pstmtGrade = conn.prepareStatement(sqlInsertGrade)) {
                pstmtGrade.setInt(1, student.getRegistrationNumber()); // am
                pstmtGrade.setString(2, course.getName());             // course_name
                pstmtGrade.setInt(3, grade.getGrade());                 // bathmos
                pstmtGrade.setString(4, grade.getDescription());         // description
                pstmtGrade.setInt(5, profId);                           // professor_id

                pstmtGrade.executeUpdate();
                System.out.println("Ο βαθμός αποθηκεύτηκε επιτυχώς στη βάση δεδομένων!");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        System.out.println("Ο μαθητής " + student.getSurname() + " έλαβε τον βαθμό: " + grade.getGrade() + ", στο μάθημα: " + course.getName());
    }

    public void addUngradedStudent(Students student){
        notGradedStudents.add(student);
    }

}