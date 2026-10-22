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
import java.sql.SQLException;
import mainpackage.DatabaseConnection;

@WebServlet("/GeneratePendingListServlet")
public class GeneratePendingListServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();

        out.println("<!DOCTYPE html><html lang='el'><head><meta charset='UTF-8'><title>Δημιουργία Λίστας</title><link rel='stylesheet' type='text/css' href='style.css'></head><body><img src=\"logo.png\" class=\"logo\" alt=\"Λογότυπο Πανεπιστημίου\"><img src='logo2.png' class='logo2' alt='Λογότυπο 2'>");
        out.println("<h2>Διαχείριση Λίστας Εκκρεμών Βαθμολογιών</h2>");

        // Καθαρίζουμε την προηγούμενη λίστα
        String sqlClean = "DELETE FROM pending_grades";

        // Βρίσκουμε όλους τους φοιτητές που δεν έχουν βαθμό σε κάποιο μάθημα
        String sqlFindEmpty =
                "SELECT s.am, c.course_name " +
                        "FROM students s " +
                        "CROSS JOIN courses c " +
                        "WHERE NOT EXISTS (" +
                        "    SELECT 1 FROM grades g " +
                        "    WHERE g.am = s.am AND g.course_name = c.course_name" +
                        ")";

        // Εισάγουμε αυτούς τους φοιτητές στον πίνακα ελέγχου
        String sqlInsert = "INSERT IGNORE INTO pending_grades (am, course_name) VALUES (?, ?)";

        // ΝΕΟ ΕΡΩΤΗΜΑ SQL: Φέρνει τα αναλυτικά στοιχεία Φοιτητή και Καθηγητή
        String sqlDisplay =
                "SELECT pg.am, u_stud.name AS s_name, u_stud.surname AS s_surname, " +
                        "       pg.course_name, " +
                        "       u_prof.name AS p_name, u_prof.surname AS p_surname " +
                        "FROM pending_grades pg " +
                        "JOIN students s ON pg.am = s.am " +
                        "JOIN users u_stud ON s.id = u_stud.id " +
                        "JOIN courses c ON pg.course_name = c.course_name " +
                        "LEFT JOIN users u_prof ON c.professor_id = u_prof.id " +
                        "ORDER BY pg.course_name ASC, pg.am ASC";

        try (Connection conn = DatabaseConnection.getConnection()) {
            conn.setAutoCommit(false); // Ξεκινάμε Transaction

            // 1. Καθαρισμός
            try (PreparedStatement pstClean = conn.prepareStatement(sqlClean)) {
                pstClean.executeUpdate();
            }

            int count = 0;
            // 2. Εύρεση και Εισαγωγή
            try (PreparedStatement pstFind = conn.prepareStatement(sqlFindEmpty);
                 ResultSet rs = pstFind.executeQuery();
                 PreparedStatement pstInsert = conn.prepareStatement(sqlInsert)) {

                while (rs.next()) {
                    pstInsert.setInt(1, rs.getInt("am"));
                    pstInsert.setString(2, rs.getString("course_name"));
                    pstInsert.executeUpdate();
                    count++;
                }
            }

            conn.commit(); // Οριστικοποίηση αλλαγών

            out.println("<h3 style='color:green;'>✓ Η λίστα δημιουργήθηκε και ενημερώθηκε με επιτυχία!</h3>");
            out.println("<p>Συνολικά βρέθηκαν <b>" + count + "</b> εκκρεμότητες προς βαθμολόγηση.</p>");

            // 3. ΕΜΦΑΝΙΣΗ ΤΗΣ ΛΙΣΤΑΣ ΣΤΗΝ ΟΘΟΝΗ
            if (count > 0) {
                out.println("<h3>Αναλυτική Λίστα Εκκρεμοτήτων</h3>");
                out.println("<table border='1'>");
                out.println("<tr><th>ΑΜ Φοιτητή</th><th>Ονοματεπώνυμο Φοιτητή</th><th>Μάθημα</th><th>Υπεύθυνος Καθηγητής</th></tr>");

                try (PreparedStatement pstDisplay = conn.prepareStatement(sqlDisplay);
                     ResultSet rsDisplay = pstDisplay.executeQuery()) {

                    while (rsDisplay.next()) {
                        out.println("<tr>");
                        out.println("<td>" + rsDisplay.getInt("am") + "</td>");
                        out.println("<td>" + rsDisplay.getString("s_name") + " " + rsDisplay.getString("s_surname") + "</td>");
                        out.println("<td>" + rsDisplay.getString("course_name") + "</td>");

                        // Ελέγχουμε αν υπάρχει ανατεθειμένος καθηγητής στο μάθημα
                        String profName = rsDisplay.getString("p_name");
                        String profSurname = rsDisplay.getString("p_surname");

                        if (profName != null && profSurname != null) {
                            out.println("<td>" + profName + " " + profSurname + "</td>");
                        } else {
                            out.println("<td style='color:orange;'><i>Δεν έχει ανατεθεί</i></td>");
                        }

                        out.println("</tr>");
                    }
                }
                out.println("</table>");
            } else {
                out.println("<p style='color:blue;'>Όλοι οι φοιτητές έχουν βαθμολογηθεί σε όλα τα μαθήματα!</p>");
            }

        } catch (SQLException e) {
            out.println("<h3 style='color:red;'>Σφάλμα κατά τη δημιουργία: " + e.getMessage() + "</h3>");
            e.printStackTrace();
        }

        out.println("<br><a href='LoginServlet' class=\"back-btn\">Επιστροφή στο Μενού</a>");
        out.println("</body></html>");
    }
}