package mainpackage.servlets;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;
import mainpackage.DatabaseConnection;

@WebServlet("/StudShowGradesBySem")
public class StudShowGradesBySem extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();

        // Ελέγχουμε ποιος φοιτητής είναι συνδεδεμένος
        HttpSession session = request.getSession();
        String loggedUser = (String) session.getAttribute("loggedUser");

        if (loggedUser == null) {
            out.println("<h3>Πρέπει να συνδεθείτε πρώτα!</h3><a href='index.html'>Επιστροφή</a>");
            return;
        }

        out.println("<!DOCTYPE html><html lang='el'><head><meta charset='UTF-8'><title>Οι Βαθμολογίες μου</title><link rel='stylesheet' type='text/css' href='style.css'></head><body><img src=\"logo.png\" class=\"logo\" alt=\"Λογότυπο Πανεπιστημίου\"><img src='logo2.png' class='logo2' alt='Λογότυπο 2'>");
        out.println("<h2>Αναλυτική Βαθμολογία ανά Εξάμηνο (Φοιτητής: " + loggedUser + ")</h2><br>");

        // Οι βαθμοί ταξινομημένοι ανά εξάμηνο
        String sql = "SELECT c.semester, g.course_name, g.bathmos, g.description, c.ects " +
                "FROM grades g " +
                "JOIN courses c ON g.course_name = c.course_name " +
                "JOIN students s ON g.am = s.am " +
                "JOIN users u ON s.id = u.id " +
                "WHERE u.username = ? " +
                "ORDER BY c.semester ASC, g.course_name ASC";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {

            pst.setString(1, loggedUser);
            try (ResultSet rs = pst.executeQuery()) {

                int currentSemester = -1; // Μεταβλητή για να θυμόμαστε σε ποιο εξάμηνο βρισκόμαστε
                boolean hasGrades = false;

                while (rs.next()) {
                    hasGrades = true;
                    int semester = rs.getInt("semester");

                    // Αν διαβάζουμε βαθμό από νέο εξάμηνο, φτιάχνουμε νέο πίνακα
                    if (semester != currentSemester) {
                        // Αν δεν είναι το πρώτο εξάμηνο που τυπώνουμε, πρέπει πρώτα να κλείσουμε τον προηγούμενο πίνακα
                        if (currentSemester != -1) {
                            out.println("</table><br>");
                        }
                        currentSemester = semester;
                        out.println("<h3 style='color:blue;'>Εξάμηνο " + currentSemester + "</h3>");
                        out.println("<table border='1'><tr><th>Μάθημα</th><th>Βαθμός</th><th>Περιγραφή</th><th>ECTS</th></tr>");
                    }

                    // Τυπώνουμε τα στοιχεία του μαθήματος
                    out.println("<tr>");
                    out.println("<td>" + rs.getString("course_name") + "</td>");

                    // Αν ο βαθμός είναι κάτω από 5, τον κάνουμε κόκκινο, αλλιώς πράσινο!
                    int grade = rs.getInt("bathmos");
                    if (grade >= 5) {
                        out.println("<td style='color:green; font-weight:bold;'>" + grade + "</td>");
                    } else {
                        out.println("<td style='color:red; font-weight:bold;'>" + grade + "</td>");
                    }

                    out.println("<td>" + rs.getString("description") + "</td>");
                    out.println("<td>" + rs.getInt("ects") + "</td>");
                    out.println("</tr>");
                }

                // Στο τέλος, αν βρέθηκαν βαθμοί, κλείνουμε και τον τελευταίο πίνακα
                if (hasGrades) {
                    out.println("</table><br>");
                } else {
                    out.println("<p style='color:gray;'>Δεν υπάρχουν ακόμα καταχωρημένοι βαθμοί στο όνομά σας.</p>");
                }
            }

        } catch (SQLException e) {
            out.println("<p>Σφάλμα Βάσης: " + e.getMessage() + "</p>");
        }

        out.println("<br><a href='LoginServlet' class=\"back-btn\">Επιστροφή στο Μενού</a>");
        out.println("</body></html>");
    }
}