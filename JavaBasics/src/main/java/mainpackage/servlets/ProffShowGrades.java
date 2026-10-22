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
import java.util.ArrayList;
import java.util.List;
import mainpackage.DatabaseConnection;

@WebServlet("/ProffShowGrades")
public class ProffShowGrades extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();

        // Ελέγχουμε ποιος καθηγητής είναι συνδεδεμένος
        HttpSession session = request.getSession();
        String loggedUser = (String) session.getAttribute("loggedUser");

        if (loggedUser == null) {
            out.println("<h3>Πρέπει να συνδεθείτε πρώτα!</h3><a href='index.html'>Επιστροφή</a>");
            return;
        }

        out.println("<!DOCTYPE html><html lang='el'><head><meta charset='UTF-8'><title>Προβολή Βαθμολογιών</title><link rel='stylesheet' type='text/css' href='style.css'></head><body><img src=\"logo.png\" class=\"logo\" alt=\"Λογότυπο Πανεπιστημίου\"><img src='logo2.png' class='logo2' alt='Λογότυπο 2'>");
        out.println("<h2>Βαθμολογίες ανά Μάθημα (Καθηγητής: " + loggedUser + ")</h2><br>");

        try (Connection conn = DatabaseConnection.getConnection()) {

            // Βρίσκουμε το ID του καθηγητή από το username του
            int profId = -1;
            try (PreparedStatement pst = conn.prepareStatement("SELECT id FROM users WHERE username = ?")) {
                pst.setString(1, loggedUser);
                try (ResultSet rs = pst.executeQuery()) {
                    if (rs.next()) profId = rs.getInt("id");
                }
            }

            // Βρίσκουμε τα μαθήματα που διδάσκει
            List<String> myCourses = new ArrayList<>();
            try (PreparedStatement pst = conn.prepareStatement("SELECT course_name FROM courses WHERE professor_id = ?")) {
                pst.setInt(1, profId);
                try (ResultSet rs = pst.executeQuery()) {
                    while (rs.next()) myCourses.add(rs.getString("course_name"));
                }
            }

            if (myCourses.isEmpty()) {
                out.println("<h3>Δεν έχετε αναλάβει κάποιο μάθημα ακόμα.</h3>");
            } else {

                // Για κάθε μάθημα που διδάσκει ο καθηγητής, φτιάχνουμε έναν πίνακα
                for (String course : myCourses) {
                    out.println("<h3>Μάθημα: " + course + "</h3>");

                    // Μόνο οι φοιτητές που έχουν βαθμό στο μάθημα
                    String sqlGrades = "SELECT g.am, u.name, u.surname, g.bathmos, g.description " +
                            "FROM grades g " +
                            "JOIN students s ON g.am = s.am " +
                            "JOIN users u ON s.id = u.id " +
                            "WHERE g.course_name = ?";

                    try (PreparedStatement pst = conn.prepareStatement(sqlGrades)) {
                        pst.setString(1, course);
                        try (ResultSet rs = pst.executeQuery()) {
                            boolean tableStarted = false;

                            while (rs.next()) {
                                // Αν βρει έστω και έναν φοιτητή με βαθμό, φτιάχνει την επικεφαλίδα του πίνακα
                                if (!tableStarted) {
                                    out.println("<table border='1'><tr><th>ΑΜ</th><th>Ονοματεπώνυμο</th><th>Βαθμός</th><th>Περιγραφή</th></tr>");
                                    tableStarted = true;
                                }

                                out.println("<tr>");
                                out.println("<td>" + rs.getInt("am") + "</td>");
                                out.println("<td>" + rs.getString("name") + " " + rs.getString("surname") + "</td>");
                                out.println("<td>" + rs.getInt("bathmos") + "</td>");
                                out.println("<td>" + rs.getString("description") + "</td>");
                                out.println("</tr>");
                            }

                            if (tableStarted) {
                                out.println("</table><br>");
                            } else {
                                // Αν δεν βρήκε κανέναν, βγάζει αυτό το μήνυμα αντί για άδειο πίνακα
                                out.println("<p style='color:gray;'>Δεν υπάρχουν ακόμα καταχωρημένοι βαθμοί για αυτό το μάθημα.</p>");
                            }
                        }
                    }
                }
            }

        } catch (SQLException e) {
            out.println("Σφάλμα Βάσης: " + e.getMessage());
        }

        out.println("<br><br><a href='ProffSubmitGradeServlet'>Επιστροφή στην Καταχώρηση Βαθμών</a>");
        out.println("<br><a href='LoginServlet' class=\"back-btn\">Επιστροφή στο Μενού</a>");
        out.println("</body></html>");
    }
}