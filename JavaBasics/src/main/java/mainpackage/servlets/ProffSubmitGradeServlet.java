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

@WebServlet("/ProffSubmitGradeServlet")
public class ProffSubmitGradeServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();

        HttpSession session = request.getSession();
        String loggedUser = (String) session.getAttribute("loggedUser");

        if (loggedUser == null) {
            out.println("<h3>Πρέπει να συνδεθείτε πρώτα!</h3><a href='index.html'>Επιστροφή</a>");
            return;
        }

        out.println("<!DOCTYPE html><html lang='el'><head><meta charset='UTF-8'><title>Καταχώρηση Βαθμών</title><link rel='stylesheet' type='text/css' href='style.css'></head><body><img src=\"logo.png\" class=\"logo\" alt=\"Λογότυπο Πανεπιστημίου\"><img src='logo2.png' class='logo2' alt='Λογότυπο 2'>");
        out.println("<h2>Καταχώρηση Βαθμολογιών (Καθηγητής: " + loggedUser + ")</h2>");

        String sqlProfId = "SELECT id FROM users WHERE username = ?";
        String sqlCourses = "SELECT course_name FROM courses WHERE professor_id = ?";

        // Οι φοιτητές που βρίσκονται στη λίστα της γραμματείας (pending_grades)
        String sqlFromPendingList =
                "SELECT s.am, u.name, u.surname, s.semester " +
                        "FROM students s " +
                        "JOIN users u ON s.id = u.id " +
                        "JOIN pending_grades pg ON s.am = pg.am " +
                        "WHERE pg.course_name = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstProf = conn.prepareStatement(sqlProfId)) {

            pstProf.setString(1, loggedUser);
            int profId = -1;
            try (ResultSet rsProf = pstProf.executeQuery()) {
                if (rsProf.next()) {
                    profId = rsProf.getInt("id");
                }
            }

            if (profId != -1) {
                List<String> courses = new ArrayList<>();
                try (PreparedStatement pstCourses = conn.prepareStatement(sqlCourses)) {
                    pstCourses.setInt(1, profId);
                    try (ResultSet rsCourses = pstCourses.executeQuery()) {
                        while (rsCourses.next()) {
                            courses.add(rsCourses.getString("course_name"));
                        }
                    }
                }

                if (courses.isEmpty()) {
                    out.println("<p>Δεν σάς έχει ανατεθεί κάποιο μάθημα.</p>");
                } else {
                    out.println("<form action='ProffSubmitGradeServlet' method='POST'>");
                    boolean globalHasPending = false;

                    for (String courseName : courses) {
                        out.println("<h3 style='color:blue;'>Μάθημα: " + courseName + "</h3>");

                        try (PreparedStatement pstStuds = conn.prepareStatement(sqlFromPendingList)) {
                            pstStuds.setString(1, courseName);
                            try (ResultSet rsStuds = pstStuds.executeQuery()) {

                                boolean courseHasPending = false;
                                while (rsStuds.next()) {
                                    if (!courseHasPending) {
                                        out.println("<table border='1' cellpadding='5'>");
                                        out.println("<tr><th>ΑΜ</th><th>Φοιτητής</th><th>Βαθμός</th><th>Περιγραφή</th></tr>");
                                        courseHasPending = true;
                                        globalHasPending = true;
                                    }

                                    int am = rsStuds.getInt("am");
                                    String fullName = rsStuds.getString("name") + " " + rsStuds.getString("surname");
                                    int sem = rsStuds.getInt("semester");

                                    out.println("<tr>");
                                    out.println("  <td>" + am + "</td>");
                                    out.println("  <td>" + fullName + "</td>");
                                    // Εδώ βάζουμε το hidden input ΜΑΖΙ με το input του βαθμού στο ίδιο κελί
                                    out.println("  <td><input type='hidden' name='sem_" + am + "_" + courseName + "' value='" + sem + "'>");
                                    out.println("      <input type='number' name='grade_" + am + "_" + courseName + "' min='0' max='10'></td>");
                                    out.println("  <td><input type='text' name='desc_" + am + "_" + courseName + "'></td>");
                                    out.println("</tr>");
                                }

                                if (courseHasPending) {
                                    out.println("</table><br>");
                                } else {
                                    out.println("<p style='color:red;'>⚠ Η Γραμματεία δεν έχει εκδώσει εκκρεμότητες βαθμολόγησης για αυτό το μάθημα ή έχουν ολοκληρωθεί.</p>");
                                }
                            }
                        }
                    }

                    if (globalHasPending) {
                        out.println("<input type='submit' value='Καταχώρηση Βαθμολογιών'>");
                    }
                    out.println("</form>");
                }
            }

        } catch (SQLException e) {
            out.println("<p style='color:red;'>Σφάλμα: " + e.getMessage() + "</p>");
        }

        out.println("<br><a href='LoginServlet' class=\"back-btn\">Επιστροφή στο Μενού</a>");
        out.println("</body></html>");
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        HttpSession session = request.getSession();
        String loggedUser = (String) session.getAttribute("loggedUser");

        if (loggedUser == null) {
            response.sendRedirect("index.html");
            return;
        }

        String sqlProfId = "SELECT id FROM users WHERE username = ?";
        String sqlInsert = "INSERT INTO grades (am, course_name, bathmos, description, professor_id, semester) VALUES (?, ?, ?, ?, ?, ?)";
        String sqlDeletePending = "DELETE FROM pending_grades WHERE am = ? AND course_name = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstProf = conn.prepareStatement(sqlProfId)) {

            pstProf.setString(1, loggedUser);
            int profId = -1;
            try (ResultSet rsProf = pstProf.executeQuery()) {
                if (rsProf.next()) {
                    profId = rsProf.getInt("id");
                }
            }

            if (profId != -1) {
                conn.setAutoCommit(false); // Transaction για ταυτόχρονο Insert και Delete
                try (PreparedStatement pstInsert = conn.prepareStatement(sqlInsert);
                     PreparedStatement pstDelete = conn.prepareStatement(sqlDeletePending)) {

                    java.util.Enumeration<String> paramNames = request.getParameterNames();
                    while (paramNames.hasMoreElements()) {
                        String paramName = paramNames.nextElement();

                        if (paramName.startsWith("grade_")) {
                            String gradeStr = request.getParameter(paramName);

                            if (gradeStr != null && !gradeStr.trim().isEmpty()) {
                                //ΠΡΩΤΑ κάνουμε split για να πάρουμε τα am και courseName
                                String[] parts = paramName.split("_", 3);
                                int am = Integer.parseInt(parts[1]);
                                String courseName = parts[2];

                                //μπορούμε να διαβάσουμε το sem και το desc χρησιμοποιώντας τα am/courseName
                                int sem = Integer.parseInt(request.getParameter("sem_" + am + "_" + courseName));
                                String desc = request.getParameter("desc_" + am + "_" + courseName);
                                if (desc == null) desc = "";

                                int grade = Integer.parseInt(gradeStr);

                                //Προσθήκη βαθμού
                                pstInsert.setInt(1, am);
                                pstInsert.setString(2, courseName);
                                pstInsert.setInt(3, grade);
                                pstInsert.setString(4, desc);
                                pstInsert.setInt(5, profId);
                                pstInsert.setInt(6, sem);
                                pstInsert.executeUpdate();

                                //Διαγραφή από τις εκκρεμότητες
                                pstDelete.setInt(1, am);
                                pstDelete.setString(2, courseName);
                                pstDelete.executeUpdate();
                            }
                        }
                    }
                }
                conn.commit();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        response.sendRedirect("ProffSubmitGradeServlet");
    }
}