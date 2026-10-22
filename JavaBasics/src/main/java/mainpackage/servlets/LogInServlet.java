package mainpackage.servlets;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import mainpackage.DatabaseConnection;
import org.mindrot.jbcrypt.BCrypt;

@WebServlet("/LoginServlet")
public class LogInServlet extends HttpServlet {

    // Η doPost τρέχει ΜΟΝΟ όταν ο χρήστης πατάει "Είσοδος" από την αρχική σελίδα
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        request.setCharacterEncoding("UTF-8");

        String username = request.getParameter("username");
        String plainPassword = request.getParameter("password");
        PrintWriter out = response.getWriter();

        String dbHashPassword = null;
        String userRole = null;
        String userFullName = null;

        String sql = "SELECT password, uni_role, name, surname FROM users WHERE username = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, username);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    dbHashPassword = rs.getString("password");
                    userRole = rs.getString("uni_role");
                    userFullName = rs.getString("name") + " " + rs.getString("surname");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        boolean isPasswordCorrect = false;
        if (dbHashPassword != null) {
            try {
                isPasswordCorrect = BCrypt.checkpw(plainPassword, dbHashPassword);
            } catch (IllegalArgumentException e) {
                out.println("<h3>ΠΡΟΣΟΧΗ: Ο κωδικός στη βάση δεν είναι κρυπτογραφημένος!</h3>");
            }
        }

        if (isPasswordCorrect) {
            // Αποθηκεύουμε τα πάντα στο Session για να τα "θυμάται" ο server
            HttpSession session = request.getSession();
            session.setAttribute("loggedUser", username);
            session.setAttribute("userRole", userRole);
            session.setAttribute("userFullName", userFullName);

            //Κάνουμε ανακατεύθυνση στη doGet για να τυπώσει το μενού!
            response.sendRedirect("LoginServlet");

        } else {
            out.println("<!DOCTYPE html><html lang='el'><head><meta charset='UTF-8'><title>Σφάλμα</title><link rel='stylesheet' type='text/css' href='style.css'></head><body>");
            out.println("<h2 style='color:red;'>Σφάλμα: Λάθος Όνομα Χρήστη ή Κωδικός.</h2>");
            out.println("<a href='index.html'>Επιστροφή στην Είσοδο</a>");
            out.println("</body></html>");
        }
    }

    // Η doGet τρέχει όταν πατάμε ένα link (GET request) προς το LoginServlet
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();
        HttpSession session = request.getSession(false);

        // Αν κάποιος προσπαθήσει να μπει χωρίς να έχει κάνει login (άδειο session), τον πετάμε έξω!
        if (session == null || session.getAttribute("loggedUser") == null) {
            response.sendRedirect("index.html");
            return;
        }

        // Ανακτούμε τα στοιχεία από το Session
        String userFullName = (String) session.getAttribute("userFullName");
        String userRole = (String) session.getAttribute("userRole");

        // Τυπώνουμε το μενού
        out.println("<!DOCTYPE html><html lang='el'><head><meta charset='UTF-8'><title>Πίνακας Ελέγχου</title><link rel='stylesheet' type='text/css' href='style.css'></head><body>");
        out.println("<img src='logo.png' class='logo' alt='Λογότυπο'><img src='logo2.png' class='logo2' alt='Λογότυπο 2'><br><br>");
        out.println("<br><h2 style='color:green;'>Καλώς ήρθες, " + userFullName + " (" + userRole + ")</h2>");
        out.println("<br><h3>Επιλέξτε Λειτουργία:</h3><ul>");

        switch (userRole) {
            case "Secretary":
                out.println("<li><a href='assigncourses.html'>Ανάθεση Μαθήματος σε Καθηγητή</a></li>");
                out.println("<li><a href='ShowCoursesServlet'>Προβολή Όλων των Μαθημάτων</a></li>");
                out.println("<li><a href='CoursesAndProfs'>Προβολή Μαθημάτων και Καθηγητών</a></li>");
                out.println("<li><a href='CreateStudServlet'>Δημιουργία Φοιτητή</a></li>");
                out.println("<li><a href='CreateCourseServlet'>Δημιουργία Μαθήματος</a></li>");
                out.println("<li><a href='CreateProfServlet'>Δημιουργία Καθηγητή</a></li>");
                out.println("<li><a href='GeneratePendingListServlet'>Προβολή Φοιτητών χωρίς Βαθμολογία</a></li>");
                break;
            case "Professor":
                out.println("<li><a href='ProffSubmitGradeServlet'>Καταχώρηση Βαθμολογιών</a></li>");
                out.println("<li><a href='ProffShowGrades'>Προβολή λίστας βαθμολογίας ανά μάθημα</a></li>");
                break;
            case "Student":
                out.println("<li><a href='StudShowGradesBySem'>Προβολή των Βαθμών μου ανά εξάμηνο</a></li>");
                out.println("<li><a href='StudShowGradesByCourse'>Προβολή των Βαθμών μου ανά μάθημα</a></li>");
                out.println("<li><a href='StudShowGradesTotal'>Προβολή της συνολικής βαθμολογίας</a></li>");
                break;
        }
        out.println("</ul>");
        out.println("<br><br><a href='LogoutServlet' style='color:red; font-weight:bold;'>Αποσύνδεση</a>");
        out.println("</body></html>");
    }
}