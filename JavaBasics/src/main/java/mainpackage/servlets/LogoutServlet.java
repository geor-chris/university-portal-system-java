package mainpackage.servlets;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@WebServlet("/LogoutServlet")
public class LogoutServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        // 1. Καθαρισμός της Cache μνήμης της εφαρμογής (Απαίτηση εκφώνησης)
        // Αυτές οι εντολές λένε στον browser "ΜΗΝ αποθηκεύσεις αυτή τη σελίδα στο ιστορικό/cache"
        response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate"); // Για HTTP 1.1
        response.setHeader("Pragma", "no-cache"); // Για παλιότερους browsers (HTTP 1.0)
        response.setDateHeader("Expires", 0); // Διαγραφή ληγμένων δεδομένων

        // 2. Ακύρωση του Session (Invalidate)
        // Το getSession(false) φέρνει το session ΜΟΝΟ αν υπάρχει ήδη, χωρίς να δημιουργεί νέο
        HttpSession session = request.getSession(false);

        if (session != null) {
            session.invalidate(); // Σκοτώνει το session και διαγράφει τον "loggedUser" από τη μνήμη του Tomcat
        }

        // 3. Επιστροφή στην αρχική σελίδα σύνδεσης
        response.sendRedirect("index.html");
    }
}