package mainpackage;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.ResultSet;
import org.mindrot.jbcrypt.BCrypt;

public class Users {

    private String username;
    private String name;
    private String surname;
    private String department;
    private String uni_role;
    private String password;

    private static int usersCounter = 0; //static καθώς θέλουμε τον συνολικό αριθμώ χρηστών και όχι κάθε χρήστης δικό του counter

    public Users(String username, String name, String surname, String department, String uni_role, String plainPassword) {
        this.username = username;
        this.name = name;
        this.surname = surname;
        this.department = department;
        this.uni_role = uni_role;
        this.password = BCrypt.hashpw(plainPassword, BCrypt.gensalt());
        usersCounter++;  //αυξάνεται +1 όταν φτιάχνουμε έναν χρήστη
    }


    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    //όχι setter για UserCounter καθώς δεν θέλουμε να αλλάζεται από τον χρήστη

    public static int getUsersCounter() {
        return usersCounter;
    }

    public String getRole() {
        return "User";
    }

    public int addUsersToDB() {
        String sql = "INSERT INTO users (username, password , name, surname, department, uni_role) VALUES (?, ?, ?, ?, ?, ?)";
        int generatedId = -1; // Αρχικοποίηση με -1. Αν μείνει -1, σημαίνει ότι κάτι πήγε λάθος.

        // Προσθέτουμε το PreparedStatement.RETURN_GENERATED_KEYS
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {

            // Χρησιμοποιούμε το 'this' για να πάρουμε τις τιμές αυτού του αντικειμένου
            pstmt.setString(1, this.username);
            pstmt.setString(2, this.password);
            //pstmt.setString(2, BCrypt.hashpackage mainpackage.servlets;
            //
            //import javax.servlet.ServletException;
            //import javax.servlet.annotation.WebServlet;
            //import javax.servlet.http.HttpServlet;
            //import javax.servlet.http.HttpServletRequest;
            //import javax.servlet.http.HttpServletResponse;
            //import javax.servlet.http.HttpSession;
            //import java.io.IOException;
            //import java.io.PrintWriter;
            //import java.sql.*;
            //import mainpackage.DatabaseConnection;
            //
            //@WebServlet("/StudShowGradesTotal")
            //public class StudShowGradesTotal extends HttpServlet {
            //
            //    @Override
            //    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
            //        response.setContentType("text/html;charset=UTF-8");
            //        PrintWriter out = response.getWriter();
            //
            //        // Ελέγχουμε ποιος φοιτητής είναι συνδεδεμένος
            //        HttpSession session = request.getSession();
            //        String loggedUser = (String) session.getAttribute("loggedUser");
            //
            //        if (loggedUser == null) {
            //            out.println("<h3>Πρέπει να συνδεθείτε πρώτα!</h3><a href='index.html'>Επιστροφή</a>");
            //            return;
            //        }
            //
            //        out.println("<!DOCTYPE html><html lang='el'><head><meta charset='UTF-8'><title>Συνολική Βαθμολογία</title><link rel='stylesheet' type='text/css' href='style.css'></head><body><img src=\"logo.png\" class=\"logo\" alt=\"Λογότυπο Πανεπιστημίου\"><img src='logo2.png' class='logo2' alt='Λογότυπο 2'>");
            //        out.println("<h2>Συνολική Λίστα Βαθμολογιών (Φοιτητής: " + loggedUser + ")</h2>");
            //
            //        // Οι βαθμοί ταξινομημένοι αλφαβητικά ανά μάθημα
            //        String sql = "SELECT g.course_name, g.bathmos, g.description, c.ects " +
            //                "FROM grades g " +
            //                "JOIN courses c ON g.course_name = c.course_name " +
            //                "JOIN students s ON g.am = s.am " +
            //                "JOIN users u ON s.id = u.id " +
            //                "WHERE u.username = ? " +
            //                "ORDER BY g.course_name ASC";
            //
            //        try (Connection conn = DatabaseConnection.getConnection();
            //             PreparedStatement pst = conn.prepareStatement(sql)) {
            //
            //            pst.setString(1, loggedUser);
            //            try (ResultSet rs = pst.executeQuery()) {
            //
            //                boolean hasGrades = false;
            //
            //                while (rs.next()) {
            //                    // Φτιάχνουμε την επικεφαλίδα του πίνακα μόνο την πρώτη φορά που θα βρει βαθμό
            //                    if (!hasGrades) {
            //                        out.println("<table border='1'><tr><th>Μάθημα</th><th>Βαθμός</th><th>Περιγραφή</th><th>ECTS</th></tr>");
            //                        hasGrades = true;
            //                    }
            //
            //                    // Τυπώνουμε τη γραμμή με τα στοιχεία του μαθήματος
            //                    out.println("<tr>");
            //                    out.println("<td>" + rs.getString("course_name") + "</td>");
            //
            //                    // Κρατάμε τον χρωματισμό (πράσινο/κόκκινο) ανάλογα με το αν πέρασε το μάθημα
            //                    int grade = rs.getInt("bathmos");
            //                    if (grade >= 5) {
            //                        out.println("<td style='color:green; font-weight:bold;'>" + grade + "</td>");
            //                    } else {
            //                        out.println("<td style='color:red; font-weight:bold;'>" + grade + "</td>");
            //                    }
            //
            //                    out.println("<td>" + rs.getString("description") + "</td>");
            //                    out.println("<td>" + rs.getInt("ects") + "</td>");
            //                    out.println("</tr>");
            //                }
            //
            //                // Κλείνουμε τον πίνακα ή βγάζουμε μήνυμα αν δεν έχει βαθμούς
            //                if (hasGrades) {
            //                    out.println("</table><br>");
            //                } else {
            //                    out.println("<p style='color:gray;'>Δεν υπάρχουν ακόμα καταχωρημένοι βαθμοί στο όνομά σας.</p>");
            //                }
            //            }
            //
            //        } catch (SQLException e) {
            //            out.println("<p>Σφάλμα Βάσης: " + e.getMessage() + "</p>");
            //        }
            //
            //        out.println("<br><a href='LoginServlet' class=\"back-btn\">Επιστροφή στο Μενού</a>");
            //        out.println("</body></html>");
            //    }
            //}pw(this.password, BCrypt.gensalt()));

            pstmt.setString(3, this.name);
            pstmt.setString(4, this.surname);
            pstmt.setString(5, this.department);
            pstmt.setString(6, this.getRole()); // Εδώ ο ρόλος μπαίνει δυναμικά!

            pstmt.executeUpdate();

            // Παίρνουμε το ID που μόλις έφτιαξε η βάση για αυτόν τον χρήστη
            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    generatedId = generatedKeys.getInt(1);
                }
            }
            System.out.println("Ο χρήστης " + this.username + " αποθηκεύτηκε στη βάση με ID: " + generatedId);

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return generatedId; // Επιστρέφουμε το ID
    }
}


