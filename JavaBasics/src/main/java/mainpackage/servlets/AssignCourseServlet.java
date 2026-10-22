package mainpackage.servlets;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import mainpackage.DatabaseConnection;

@WebServlet("/AssignCourseServlet")
public class AssignCourseServlet extends HttpServlet {

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        request.setCharacterEncoding("UTF-8");

        PrintWriter out = response.getWriter();
        out.println("<!DOCTYPE html><html lang='el'><head><meta charset='UTF-8'><title>Αποτέλεσμα Ανάθεσης</title><link rel='stylesheet' type='text/css' href='style.css'></head><body><img src=\"logo.png\" class=\"logo\" alt=\"Λογότυπο Πανεπιστημίου\"><img src='logo2.png' class='logo2' alt='Λογότυπο 2'>");

        String profUsername = request.getParameter("professor");
        String courseName = request.getParameter("course");


        String sqlGetId = "SELECT id FROM users WHERE username = ?";
        String sqlUpdate = "UPDATE courses SET professor_id = ? WHERE course_name = ?";

        try (Connection conn = DatabaseConnection.getConnection()) {
            int profId = -1;

            // Βρήσκουμε το ID του καθηγητή
            try (PreparedStatement pstmtId = conn.prepareStatement(sqlGetId)) {
                pstmtId.setString(1, profUsername);
                try (ResultSet rs = pstmtId.executeQuery()) {
                    if (rs.next()) profId = rs.getInt("id");
                }
            }

            // Αν βρεθεί ο καθηγητής, κάνε update
            if (profId != -1) {
                try (PreparedStatement pstmtUpdate = conn.prepareStatement(sqlUpdate)) {
                    pstmtUpdate.setInt(1, profId);
                    pstmtUpdate.setString(2, courseName);
                    int rows = pstmtUpdate.executeUpdate();

                    if (rows > 0) {
                        out.println("<h3 style='color:green;'>Η ανάθεση ολοκληρώθηκε επιτυχώς!</h3>");
                        out.println("<p>Ο καθηγητής με username <b>" + profUsername + "</b> ανατέθηκε στο μάθημα <b>" + courseName + "</b>.</p>");
                    } else {
                        out.println("<h3 style='color:red;'>Σφάλμα: Το μάθημα δεν βρέθηκε.</h3>");
                        out.println("<p>Έψαξα για το μάθημα: <b>" + courseName + "</b> αλλά δεν υπάρχει στη βάση.</p>");
                    }
                }
            } else {
                out.println("<h3 style='color:red;'>Σφάλμα: Ο καθηγητής δεν βρέθηκε.</h3>");
                out.println("<p>Έψαξα για username: <b>" + profUsername + "</b> αλλά δεν υπάρχει. Σιγουρέψου ότι δεν έβαλες ονοματεπώνυμο!</p>");
            }
        } catch (SQLException e) {
            out.println("<h3>Σφάλμα βάσης: " + e.getMessage() + "</h3>");
        }

        out.println("<br><br><a href='assigncourses.html'>Επιστροφή στην Ανάθεση</a>");
        out.println("<br><a href='LoginServlet' class=\"back-btn\">Επιστροφή στο Μενού</a>");
        out.println("</body></html>");
    }
}