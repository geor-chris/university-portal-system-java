package mainpackage;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {


    private static final String URL = "jdbc:mysql://localhost:3306/uni_db?useUnicode=true&characterEncoding=UTF-8";
    // Τα στοιχεία σύνδεσης.

    private static final String USER = "root";
    private static final String PASSWORD = "";

    public static Connection getConnection() {
        Connection connection = null;
        try {
            // Φόρτωση του Driver της MySQL
            Class.forName("com.mysql.cj.jdbc.Driver");
            // Άνοιγμα της σύνδεσης
            connection = DriverManager.getConnection(URL, USER, PASSWORD);
            System.out.println("Επιτυχής σύνδεση με τη βάση δεδομένων!");
        } catch (ClassNotFoundException e) {
            System.out.println("Σφάλμα: Δεν βρέθηκε ο JDBC Driver.");
            e.printStackTrace();
        } catch (SQLException e) {
            System.out.println("Σφάλμα: Πρόβλημα κατά τη σύνδεση με τη βάση.");
            e.printStackTrace();
        }
        return connection;
    }
}
