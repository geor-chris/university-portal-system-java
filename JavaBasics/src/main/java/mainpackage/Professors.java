package mainpackage;

import java.util.ArrayList;

import static mainpackage.Secretaries.input;
import static mainpackage.Secretaries.input1;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.ResultSet;

public class Professors extends Users {

    private String professorsMajor;
    private String office;
    private ArrayList<Courses> courses=new ArrayList<Courses>();
    private ArrayList<Grades> grades=new ArrayList<Grades>();

    public Professors(String username, String name, String surname, String department, String professorsMajor, String office, String password) {
        super(username, name, surname, department, "Professor", password);
        this.professorsMajor = professorsMajor;
        this.office = office;
    }

    public String getProfessorsMajor() {
        return professorsMajor;
    }
    public void setProfessorsMajor(String professorsMajor) {
        this.professorsMajor = professorsMajor;
    }

    public String getOffice() {
        return office;
    }

    public void setOffice(String office) {
        this.office = office;
    }



    public void submitGrade(Students student, Courses course, Secretaries secretary) { //υποβολη βαθμων, στη συνεχεια στελνεται στη γραμματεια
        int gradeValue=input1();
        String description=input();
        int semester=input1();
        Grades grade=new Grades(gradeValue,description, semester);
        grade.setCourse(course);
        grades.add(grade);
        secretary.assignStudent(student,course,grade);

    }
    @Override
    public String getRole() {
        return "Professor";
    }

    public void addProfessorToDB() {
        //Αποθήκευση στον πίνακα users και λήψη του ID
        int generatedId = super.addUsersToDB();

        if (generatedId != -1) {
            //Εισαγωγή στον πίνακα professors
            String sql = "INSERT INTO professors (id, major, office) VALUES (?, ?, ?)";

            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement pstmt = conn.prepareStatement(sql)) {

                pstmt.setInt(1, generatedId);
                pstmt.setString(2, this.getProfessorsMajor()); // Από το πεδίο professorsMajor
                pstmt.setString(3, this.getOffice());         // Από το πεδίο office

                pstmt.executeUpdate();
                System.out.println("Τα ειδικά στοιχεία του καθηγητή αποθηκεύτηκαν επιτυχώς!");

            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }


    public ArrayList<Courses> getCourses() {
        return courses;
    }

    public void addCourses(Courses course) {
        courses.add(course);
    }

    public void viewGrades(){ //προβολη βαθμω ν που εχει βαλει ενας καθηγητης
        for(Grades grade:grades){
            Courses course=grade.getCourse();
            if(course.getAssignedProfessor() == this){
                System.out.println(grade);
            }
        }
    }





}
