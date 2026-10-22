package mainpackage.servlets;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;
import mainpackage.DatabaseConnection;

@WebServlet("/CoursesAndProfs")
public class CoursesAndProfsServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();

        // Γράφουμε το σταθερό κομμάτι της HTML (το "κεφάλι" και την αρχή του πίνακα)
        out.println("<!DOCTYPE html><html lang='el'><head><meta charset='UTF-8'><title>Προβολή μαθημάτων και υπεύθυνου Καθηγητή ανά μάθημα</title><link rel='stylesheet' type='text/css' href='style.css'></head><body><img src=\"logo.png\" class=\"logo\" alt=\"Λογότυπο Πανεπιστημίου\"><img src='logo2.png' class='logo2' alt='Λογότυπο 2'>");
        out.println("<h2>Προβολή μαθημάτων και υπεύθυνου Καθηγητή ανά μάθημα</h2><table border='1'><tr><th>Όνομα Μαθήματος</th><th>Καθηγητής</th></tr>");

        // Συνδεόμαστε στη βάση και παίρνουμε τα δεδομένα
        String sql = "SELECT c.course_name, u.name, u.surname " +
                "FROM courses c LEFT JOIN users u ON c.professor_id = u.id";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            // Εδώ προσθέτουμε δυναμικά τις γραμμές (<tr>) στον πίνακα
            while (rs.next()) {
                String prof = (rs.getString("name") != null) ? rs.getString("name") + " " + rs.getString("surname") : "Δεν έχει ανατεθεί";

                out.println("<tr>");
                out.println("<td>" + rs.getString("course_name") + "</td>");
                out.println("<td>" + prof + "</td>");
                out.println("</tr>");
            }

        } catch (SQLException e) {
            out.println("<tr><td colspan='4'>Σφάλμα βάσης: " + e.getMessage() + "</td></tr>");
        }

        // Κλείνουμε τον πίνακα και το body


        out.println("</table>");
        out.println("<br><a href='LoginServlet' class=\"back-btn\">Επιστροφή στο Μενού</a>");
        out.println("</body></html>");
    }
}