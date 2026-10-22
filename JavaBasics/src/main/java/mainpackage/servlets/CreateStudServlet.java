package mainpackage.servlets;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import mainpackage.DatabaseConnection;
import org.mindrot.jbcrypt.BCrypt;

@WebServlet("/CreateStudServlet")
public class CreateStudServlet extends HttpServlet {

    // Η doGet εμφανίζει τη φόρμα όταν πατάμε τον σύνδεσμο από το μενού της Γραμματείας
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();

        // Σιγουρεύεται ότι ο χρήστης είναι συνδεδεμένος
        String loggedUser = (String) request.getSession().getAttribute("loggedUser");
        if (loggedUser == null) {
            out.println("<h3>Πρέπει να συνδεθείτε πρώτα!</h3><a href='index.html'>Επιστροφή</a>");
            return;
        }

        out.println("<!DOCTYPE html><html lang='el'><head><meta charset='UTF-8'><title>Δημιουργία Φοιτητή</title> <link rel='stylesheet' type='text/css' href='style.css'></head><body><img src=\"logo.png\" class=\"logo\" alt=\"Λογότυπο Πανεπιστημίου\"><img src='logo2.png' class='logo2' alt='Λογότυπο 2'>");
        out.println("<h2>Φόρμα Εγγραφής Νέου Φοιτητή</h2>");

        // Η φόρμα ξανακαλεί το ίδιο Servlet αλλά με τη μέθοδο POST
        out.println("<form action='CreateStudServlet' method='POST'>");
        out.println("  <label>Username (Υποχρεωτικό):</label><br>");
        out.println("  <input type='text' name='username' required><br><br>");
        out.println("  <label>Password (Υποχρεωτικό):</label><br>");
        out.println("  <input type='password' name='password' required><br><br>");
        out.println("  <label>Όνομα:</label><br>");
        out.println("  <input type='text' name='name'><br><br>");
        out.println("  <label>Επώνυμο:</label><br>");
        out.println("  <input type='text' name='surname'><br><br>");
        out.println("  <label>Τμήμα (Department):</label><br>");
        out.println("  <input type='text' name='department'><br><br>");
        out.println("  <label>Αριθμός Μητρώου (ΑΜ - Υποχρεωτικό):</label><br>");
        out.println("  <input type='number' name='am' required><br><br>");
        out.println("  <label>Εξάμηνο (Semester - Υποχρεωτικό):</label><br>");
        out.println("  <input type='number' name='semester' min='1' max='14' required><br><br>");
        out.println("  <input type='submit' value='Δημιουργία Φοιτητή'>");
        out.println("</form>");

        out.println("<br><a href='LoginServlet' class=\"back-btn\">Επιστροφή στο Μενού</a>");
        out.println("</body></html>");
    }

    // Η doPost αναλαμβάνει να αποθηκεύσει τα δεδομένα όταν πατηθεί το κουμπί υποβολής
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        request.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();

        String username = request.getParameter("username");
        String plainPassword = request.getParameter("password");
        String name = request.getParameter("name");
        String surname = request.getParameter("surname");
        String department = request.getParameter("department");
        String amStr = request.getParameter("am");
        String semesterStr = request.getParameter("semester");

        if (username == null || plainPassword == null || amStr == null || semesterStr == null ||
                username.trim().isEmpty() || plainPassword.trim().isEmpty() || amStr.trim().isEmpty() || semesterStr.trim().isEmpty()) {

            out.println("<h3 style='color:red;'>Σφάλμα: Παρακαλώ συμπληρώστε όλα τα υποχρεωτικά πεδία!</h3>");
            out.println("<a href='CreateStudServlet'>Επιστροφή στη φόρμα</a>");
            return;
        }

        int am = Integer.parseInt(amStr);
        int semester = Integer.parseInt(semesterStr);

        // Κρυπτογράφηση κωδικού με BCrypt
        String hashedPassword = BCrypt.hashpw(plainPassword, BCrypt.gensalt());

        String sqlUser = "INSERT INTO users (username, password, name, surname, department, uni_role) VALUES (?, ?, ?, ?, ?, 'Student')";
        String sqlStudent = "INSERT INTO students (id, am, semester) VALUES (?, ?, ?)";

        Connection conn = null;
        PreparedStatement pstmtUser = null;
        PreparedStatement pstmtStudent = null;

        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false); // Ξεκινάμε Transaction για ασφάλεια δεδομένων

            // Εισαγωγή στον πίνακα `users`
            pstmtUser = conn.prepareStatement(sqlUser, Statement.RETURN_GENERATED_KEYS);
            pstmtUser.setString(1, username);
            pstmtUser.setString(2, hashedPassword);
            pstmtUser.setString(3, name);
            pstmtUser.setString(4, surname);
            pstmtUser.setString(5, department);

            int affectedRows = pstmtUser.executeUpdate();
            if (affectedRows == 0) {
                throw new Exception("Η δημιουργία του χρήστη απέτυχε.");
            }

            // Λήψη του ID που έδωσε αυτόματα η βάση (Auto Increment) στον πίνακα users
            int userId = -1;
            try (ResultSet generatedKeys = pstmtUser.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    userId = generatedKeys.getInt(1);
                }
            }

            if (userId == -1) {
                throw new Exception("Δεν βρέθηκε το παραγόμενο ID χρήστη.");
            }

            // Εισαγωγή στον πίνακα students με το κλειδί id του χρήστη
            pstmtStudent = conn.prepareStatement(sqlStudent);
            pstmtStudent.setInt(1, userId);
            pstmtStudent.setInt(2, am);
            pstmtStudent.setInt(3, semester);
            pstmtStudent.executeUpdate();

            // Ολοκλήρωση Transaction αν πέτυχαν και τα δύο inserts
            conn.commit();

            out.println("<!DOCTYPE html><html lang='el'><head><meta charset='UTF-8'><title>Επιτυχία</title> <link rel='stylesheet' type='text/css' href='style.css'></head><body><img src=\"logo.png\" class=\"logo\" alt=\"Λογότυπο Πανεπιστημίου\"><img src='logo2.png' class='logo2' alt='Λογότυπο 2'>");
            out.println("<h2 style='color:green;'>Ο φοιτητής δημιουργήθηκε με επιτυχία!</h2>");
            out.println("<p>Username: <b>" + username + "</b></p>");
            out.println("<p>Αριθμός Μητρώου (ΑΜ): <b>" + am + "</b></p>");
            out.println("<br><a href='CreateStudServlet'>Δημιουργία νέου φοιτητή</a>");
            out.println("<br><a href='LoginServlet' class=\"back-btn\">Επιστροφή στο Μενού</a>");
            out.println("</body></html>");

        } catch (Exception e) {
            // Αν πάει κάτι στραβά, κάνουμε rollback για να μην μείνουν "ορφανά" δεδομένα
            if (conn != null) {
                try { conn.rollback(); } catch (Exception ex) { ex.printStackTrace(); }
            }
            out.println("<h3 style='color:red;'>Σφάλμα κατά τη δημιουργία: " + e.getMessage() + "</h3>");
            out.println("<a href='CreateStudServlet'>Επιστροφή στη φόρμα</a>");
            e.printStackTrace();
        } finally {
            try { if (pstmtStudent != null) pstmtStudent.close(); } catch (Exception e) {}
            try { if (pstmtUser != null) pstmtUser.close(); } catch (Exception e) {}
            try { if (conn != null) conn.close(); } catch (Exception e) {}
        }
    }
}