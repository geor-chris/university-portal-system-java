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

@WebServlet("/ShowCoursesServlet")
public class ShowCoursesServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();

        // Πίνακας μαθήματος
        out.println("<!DOCTYPE html><html lang='el'><head><meta charset='UTF-8'><title>Προβολή Μαθημάτων</title><link rel='stylesheet' type='text/css' href='style.css'></head><body><img src=\"logo.png\" class=\"logo\" alt=\"Λογότυπο Πανεπιστημίου\"><img src='logo2.png' class='logo2' alt='Λογότυπο 2'>");
        out.println("<h2>Προβολή Μαθημάτων</h2><table border='1'><tr><th>Όνομα Μαθήματος</th><th>Περιγραφή</th><th>Μονάδες ECTS</th><th>Εξάμηνο</th><th>Τμήμα</th></tr>");


        String sql = "SELECT c.course_name, c.course_description, c.ects, c.semester, c.department FROM courses c";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            // Προσθέτουμε τα νέα δεδομένα δυναμικά στις γραμμές (<tr>)
            while (rs.next()) {
                out.println("<tr>");
                out.println("<td>" + rs.getString("course_name") + "</td>");
                out.println("<td>" + rs.getString("course_description") + "</td>");
                out.println("<td>" + rs.getInt("ects") + "</td>");
                out.println("<td>" + rs.getInt("semester") + "</td>");

                // Αν το τμήμα είναι κενό, να εμφανίζει μια παύλα
                String dept = rs.getString("department");
                if (dept == null || dept.trim().isEmpty()) {
                    dept = "-";
                }
                out.println("<td>" + dept + "</td>");
                out.println("</tr>");
            }

        } catch (SQLException e) {
            out.println("<tr><td colspan='5'>Σφάλμα βάσης: " + e.getMessage() + "</td></tr>");
        }

        // Κλείνουμε τον πίνακα και κουμπί επιστροφής
        out.println("</table>");
        out.println("<br><a href='LoginServlet' class=\"back-btn\">Επιστροφή στο Μενού</a>");
        out.println("</body></html>");
    }
}