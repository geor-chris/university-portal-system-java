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
import java.sql.SQLException;
import mainpackage.DatabaseConnection;

@WebServlet("/CreateCourseServlet")
public class CreateCourseServlet extends HttpServlet {

    // Η doGet εμφανίζει τη φόρμα εγγραφής μαθήματος
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();

        out.println("<!DOCTYPE html><html lang='el'><head><meta charset='UTF-8'><title>Δημιουργία Μαθήματος</title><link rel='stylesheet' type='text/css' href='style.css'></head><body><img src=\"logo.png\" class=\"logo\" alt=\"Λογότυπο Πανεπιστημίου\"><img src='logo2.png' class='logo2' alt='Λογότυπο 2'>");
        out.println("<h2>Φόρμα Δημιουργίας Νέου Μαθήματος</h2>");

        // Η φόρμα υποβάλλει τα δεδομένα στο ίδιο Servlet μέσω POST
        out.println("<form action='CreateCourseServlet' method='POST'>");
        out.println("  <label>Όνομα Μαθήματος (course_name - Υποχρεωτικό):</label><br>");
        out.println("  <input type='text' name='course_name' required><br><br>");

        out.println("  <label>Περιγραφή Μαθήματος (course_description):</label><br>");
        out.println("  <textarea name='course_description' rows='4' cols='40'></textarea><br><br>");

        out.println("  <label>Μονάδες ECTS (Υποχρεωτικό):</label><br>");
        out.println("  <input type='number' name='ects' min='1' max='30' required><br><br>");

        out.println("  <label>Εξάμηνο (semester - Υποχρεωτικό):</label><br>");
        out.println("  <input type='number' name='semester' min='1' max='14' required><br><br>");

        out.println("  <label>Τμήμα (department):</label><br>");
        out.println("  <input type='text' name='department'><br><br>");

        out.println("  <input type='submit' value='Δημιουργία Μαθήματος'>");
        out.println("</form>");

        out.println("<br><a href='LoginServlet' class=\"back-btn\">Επιστροφή στο Μενού</a>");
        out.println("</body></html>");
    }

    // Η doPost αναλαμβάνει την αποθήκευση του μαθήματος στον πίνακα courses
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        request.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();

        String courseName = request.getParameter("course_name");
        String courseDesc = request.getParameter("course_description");
        String ectsStr = request.getParameter("ects");
        String semesterStr = request.getParameter("semester");
        String department = request.getParameter("department");

        // Έλεγχος υποχρεωτικών πεδίων
        if (courseName == null || ectsStr == null || semesterStr == null ||
                courseName.trim().isEmpty() || ectsStr.trim().isEmpty() || semesterStr.trim().isEmpty()) {

            out.println("<h3 style='color:red;'>Σφάλμα: Παρακαλώ συμπληρώστε όλα τα υποχρεωτικά πεδία!</h3>");
            out.println("<a href='CreateCourseServlet'>Επιστροφή στη φόρμα</a>");
            return;
        }

        int ects = Integer.parseInt(ectsStr);
        int semester = Integer.parseInt(semesterStr);

        // Εισαγωγή του μαθήματος στη βάση
        String sql = "INSERT INTO courses (course_name, course_description, ects, professor_id, semester, department) VALUES (?, ?, ?, NULL, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, courseName);
            pstmt.setString(2, courseDesc);
            pstmt.setInt(3, ects);
            pstmt.setInt(4, semester);
            pstmt.setString(5, department);

            int rows = pstmt.executeUpdate();

            if (rows > 0) {
                out.println("<!DOCTYPE html><html lang='el'><head><meta charset='UTF-8'><title>Επιτυχία</title></head><body>");
                out.println("<h2 style='color:green;'>Το μάθημα δημιουργήθηκε με επιτυχία!</h2>");
                out.println("<p>Όνομα Μαθήματος: <b>" + courseName + "</b></p>");
                out.println("<p>ECTS: <b>" + ects + "</b></p>");
                out.println("<p>Εξάμηνο: <b>" + semester + "</b></p>");
                out.println("<br><a href='CreateCourseServlet'>Δημιουργία νέου μαθήματος</a>");
                out.println("<br><a href='LogoutServlet'>Επιστροφή στην Αρχική</a>");
                out.println("</body></html>");
            } else {
                out.println("<h3 style='color:red;'>Σφάλμα: Δεν κατέστη εφικτή η αποθήκευση του μαθήματος.</h3>");
                out.println("<a href='CreateCourseServlet'>Επιστροφή στη φόρμα</a>");
            }

        } catch (SQLException e) {
            out.println("<h3 style='color:red;'>Σφάλμα βάσης δεδομένων: " + e.getMessage() + "</h3>");
            out.println("<a href='CreateCourseServlet'>Επιστροφή στη φόρμα</a>");
            e.printStackTrace();
        }
    }
}