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

@WebServlet("/CreateProfServlet")
public class CreateProfServlet extends HttpServlet {

    // Η doGet εμφανίζει τη φόρμα δημιουργίας καθηγητή
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();

        out.println("<!DOCTYPE html><html lang='el'><head><meta charset='UTF-8'><title>Δημιουργία Καθηγητή</title><link rel='stylesheet' type='text/css' href='style.css'></head><body><img src=\"logo.png\" class=\"logo\" alt=\"Λογότυπο Πανεπιστημίου\"><img src='logo2.png' class='logo2' alt='Λογότυπο 2'>");
        out.println("<h2>Φόρμα Εγγραφής Νέου Καθηγητή</h2>");

        out.println("<form action='CreateProfServlet' method='POST'>");
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
        out.println("  <label>Ειδικότητα / Αντικείμενο (Major):</label><br>");
        out.println("  <input type='text' name='major'><br><br>");
        out.println("  <label>Γραφείο (Office):</label><br>");
        out.println("  <input type='text' name='office'><br><br>");
        out.println("  <input type='submit' value='Δημιουργία Καθηγητή'>");
        out.println("</form>");

        out.println("<br><a href='LoginServlet' class=\"back-btn\">Επιστροφή στο Μενού</a>");
        out.println("</body></html>");
    }

    // Η doPost αποθηκεύει τα δεδομένα στους πίνακες users και professors
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
        String major = request.getParameter("major");
        String office = request.getParameter("office");

        if (username == null || plainPassword == null || username.trim().isEmpty() || plainPassword.trim().isEmpty()) {
            out.println("<h3 style='color:red;'>Σφάλμα: Το Username και το Password είναι υποχρεωτικά!</h3>");
            out.println("<a href='CreateProfServlet'>Επιστροφή στη φόρμα</a>");
            return;
        }

        // Κρυπτογράφηση κωδικού με BCrypt
        String hashedPassword = BCrypt.hashpw(plainPassword, BCrypt.gensalt());

        String sqlUser = "INSERT INTO users (username, password, name, surname, department, uni_role) VALUES (?, ?, ?, ?, ?, 'Professor')";
        String sqlProf = "INSERT INTO professors (id, major, office) VALUES (?, ?, ?)";

        Connection conn = null;
        PreparedStatement pstmtUser = null;
        PreparedStatement pstmtProf = null;

        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false); // Έναρξη Transaction

            // Εισαγωγή στον πίνακα users
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

            // Λήψη του ID χρήστη
            int userId = -1;
            try (ResultSet generatedKeys = pstmtUser.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    userId = generatedKeys.getInt(1);
                }
            }

            if (userId == -1) {
                throw new Exception("Δεν βρέθηκε το παραγόμενο ID καθηγητή.");
            }

            // Εισαγωγή στον πίνακα professors
            pstmtProf = conn.prepareStatement(sqlProf);
            pstmtProf.setInt(1, userId);
            pstmtProf.setString(2, major);
            pstmtProf.setString(3, office);
            pstmtProf.executeUpdate();

            // Ολοκλήρωση Transaction
            conn.commit();

            out.println("<!DOCTYPE html><html lang='el'><head><meta charset='UTF-8'><title>Επιτυχία</title><link rel='stylesheet' type='text/css' href='style.css'></head><body>");
            out.println("<h2 style='color:green;'>Ο καθηγητής δημιουργήθηκε με επιτυχία!</h2>");
            out.println("<p>Username: <b>" + username + "</b></p>");
            out.println("<br><a href='CreateProfServlet'>Δημιουργία νέου καθηγητή</a>");
            out.println("<br><a href='LoginServlet' class=\"back-btn\">Επιστροφή στο Μενού</a>");
            out.println("</body></html>");

        } catch (Exception e) {
            if (conn != null) {
                try { conn.rollback(); } catch (Exception ex) { ex.printStackTrace(); }
            }
            out.println("<h3 style='color:red;'>Σφάλμα κατά τη δημιουργία: " + e.getMessage() + "</h3>");
            out.println("<a href='CreateProfServlet'>Επιστροφή στη φόρμα</a>");
            e.printStackTrace();
        } finally {
            try { if (pstmtProf != null) pstmtProf.close(); } catch (Exception e) {}
            try { if (pstmtUser != null) pstmtUser.close(); } catch (Exception e) {}
            try { if (conn != null) conn.close(); } catch (Exception e) {}
        }
    }
}
