package mainpackage;

import java.io.File;
import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.Scanner;

public class CreateUsers {

    static Scanner sc = new Scanner(System.in);
    static ArrayList<Professors> profs = new ArrayList<>();
    static ArrayList<Students> students = new ArrayList<>();
    static ArrayList<Courses> courses = new ArrayList<>();
    // Κρατάμε μια αναφορά στην πρώτη γραμματεία που δημιουργείται για το ερώτημα 10
    static Secretaries activeSecretary = null;

    static String input() {
        String input;
        while (true) {
            try {
                input = sc.nextLine();
                // Αν το πεδίο είναι εντελώς κενό (ή έχει μόνο κενά διαστήματα), πετάει σφάλμα
                if (input.trim().isEmpty()) {
                    throw new InputException("Το πεδίο δεν μπορεί να είναι κενό. Παρακαλώ πληκτρολογήστε κάτι.");
                } else {
                    return input; // Δέχεται πλέον τα πάντα: γράμματα, νούμερα, σύμβολα!
                }
            } catch (InputException e) {
                System.out.println(e.getMessage());
            }
        }
    }

    static int input1() {
        String input;
        while (true) {
            try {
                input = sc.nextLine();
                if (input.matches(".*[a-zA-Zα-ωΑ-Ω].*") || input.isEmpty()) {
                    throw new InputException("Παρακαλώ δώστε έναν έγκυρο αριθμό");
                } else {
                    return Integer.parseInt(input);
                }
            } catch (InputException e) {
                System.out.println(e.getMessage());
            }
        }
    }

    public static ArrayList<Students> LoadStudentsFromFile(String filename) {
        System.out.println("\n=== Έναρξη φόρτωσης δεδομένων από το αρχείο: " + filename + " ===");
        try {
            File file = new File(filename);
            Scanner reader = new Scanner(file);
            while (reader.hasNextLine()) {
                String username = reader.next();
                String name = reader.next();
                String surname = reader.next();
                String department = reader.next();
                int id = reader.nextInt();
                int semester = reader.nextInt();
                String password = reader.next();

                Students student = new Students(username, name, surname, department, id, semester, password);
                // Αποθήκευση στη βάση δεδομένων κατά τη φόρτωση από αρχείο
                student.addStudentToDB();
                students.add(student);
            }
            reader.close();
        } catch (Exception e) {
            System.out.println("[ΣΦΑΛΜΑ]: " + e.getMessage());
        } finally {
            System.out.println("=== Λήξη διαδικασίας φόρτωσης αρχείου ===");
        }
        return students;
    }

    public static void loadDataFromDB() {
        try (java.sql.Connection conn = DatabaseConnection.getConnection()) {

            // Φόρτωση Καθηγητών από τη Βάση
            String sqlProfs = "SELECT u.username, u.name, u.surname, u.department, p.major, p.office, u.password FROM users u JOIN professors p ON u.id = p.id";
            try (java.sql.Statement stmt = conn.createStatement();
                 java.sql.ResultSet rs = stmt.executeQuery(sqlProfs)) {
                while (rs.next()) {
                    profs.add(new Professors(rs.getString("username"), rs.getString("name"), rs.getString("surname"), rs.getString("department"), rs.getString("major"), rs.getString("office"), rs.getString("password")));
                }
            }

            // Φόρτωση Μαθημάτων από τη Βάση
            String sqlCourses = "SELECT course_name, course_description, ects, semester, department FROM courses";
            try (java.sql.Statement stmt = conn.createStatement();
                 java.sql.ResultSet rs = stmt.executeQuery(sqlCourses)) {
                while (rs.next()) {
                    courses.add(new Courses(rs.getString("course_name"), rs.getString("course_description"), rs.getInt("ects"), rs.getInt("semester"), rs.getString("department")));
                }
            }

            // Φόρτωση Φοιτητών από τη Βάση
            String sqlStudents = "SELECT u.username, u.name, u.surname, u.department, s.am, s.semester, u.password FROM users u JOIN students s ON u.id = s.id";
            try (java.sql.Statement stmt = conn.createStatement();
                 java.sql.ResultSet rs = stmt.executeQuery(sqlStudents)) {
                while (rs.next()) {
                    // Έλεγχος για να μη διπλο-μπούν αν τους διάβασε ήδη το αρχείο .txt
                    boolean exists = false;
                    for (Students st : students) {
                        if (st.getUsername().equals(rs.getString("username"))) exists = true;
                    }
                    if (!exists) {
                        students.add(new Students(rs.getString("username"), rs.getString("name"), rs.getString("surname"), rs.getString("department"), rs.getInt("am"), rs.getInt("semester"), rs.getString("password")));
                    }
                }
            }
            //Φόρτωση Βαθμών από τη Βάση
            String sqlGrades = "SELECT am, course_name, bathmos, description, semester FROM grades";
            try (java.sql.Statement stmt = conn.createStatement();
                 java.sql.ResultSet rs = stmt.executeQuery(sqlGrades)) {

                while (rs.next()) {
                    int am = rs.getInt("am");
                    String cName = rs.getString("course_name");
                    int bathmos = rs.getInt("bathmos");
                    String desc = rs.getString("description");
                    int semester = rs.getInt("semester");

                    // Βρίσκουμε σε ποιον φοιτητή ανήκει ο βαθμός (βάσει ΑΜ)
                    Students targetStudent = null;
                    for (Students s : students) {
                        if (s.getRegistrationNumber() == am) {
                            targetStudent = s;
                            break;
                        }
                    }

                    // Βρίσκουμε σε ποιο μάθημα ανήκει ο βαθμός
                    Courses targetCourse = null;
                    for (Courses c : courses) {
                        if (c.getName().equals(cName)) {
                            targetCourse = c;
                            break;
                        }
                    }

                    // Αν βρεθούν και τα δύο, προσθέτουμε τον βαθμό στη μνήμη της Java
                    if (targetStudent != null && targetCourse != null) {
                        Grades g = new Grades(bathmos, desc, semester);
                        g.setCourse(targetCourse);
                        targetStudent.addGrade(g);
                    }
                }
            }
            System.out.println("[ΣΥΣΤΗΜΑ] Τα δεδομένα φορτώθηκαν επιτυχώς από τη Βάση Δεδομένων!");

        } catch (Exception e) {
            System.out.println("Σφάλμα κατά τη φόρτωση από τη βάση: " + e.getMessage());
        }
    }


    public static void main(String[] args) {
        String username, name, surname, department, major, office, work_hours,password;
        int regnum, semester;
        boolean exitMenu = false;

        students = LoadStudentsFromFile("students.txt");
        loadDataFromDB();
        while (!exitMenu) {
            System.out.println("\n--- Μενού Δημιουργίας Χρηστών ---");
            System.out.println("1. Δημιουργία Απλού Χρήστη");
            System.out.println("2. Δημιουργία Φοιτητή");
            System.out.println("3. Δημιουργία Καθηγητή");
            System.out.println("4. Δημιουργία Γραμματείας");
            System.out.println("5. Ολοκλήρωση και Μετάβαση στις Λειτουργίες Γραμματείας");
            System.out.println("0. Έξοδος");
            System.out.print("Επιλογή: ");

            int choice = input1();

            switch (choice) {
                case 1:
                    System.out.println("[Δημιουργία User]");
                    System.out.print("Username: "); username = input();
                    System.out.print("Password: "); password = input();
                    System.out.print("Name: "); name = input();
                    System.out.print("Surname: "); surname = input();
                    System.out.print("Department: "); department = input();

                    Users newUser = new Users(username, name, surname, department, "user",password);
                    newUser.addUsersToDB();
                    break;
                case 2:
                    System.out.println("[Δημιουργία Student]");
                    System.out.print("Username: "); username = input();
                    System.out.print("Password: "); password = input();
                    System.out.print("Name: "); name = input();
                    System.out.print("Surname: "); surname = input();
                    System.out.print("Department: "); department = input();
                    System.out.print("Registration Number: "); regnum = input1();
                    System.out.print("Semester: "); semester = input1 ();
                    Students newStudent = new Students(username, name, surname, department, regnum, semester, password);
                    newStudent.addStudentToDB();
                    students.add(newStudent);
                    break;
                case 3:
                    System.out.println("[Δημιουργία Professor]");
                    System.out.print("Username: "); username = input();
                    System.out.print("Password: "); password = input();
                    System.out.print("Name: "); name = input();
                    System.out.print("Surname: "); surname = input();
                    System.out.print("Department: "); department = input();
                    System.out.print("Major: "); major = input();
                    System.out.print("Office: "); office = input();
                    Professors newProf = new Professors(username, name, surname, department, major, office, password);
                    newProf.addProfessorToDB();
                    profs.add(newProf);
                    break;
                case 4:
                    System.out.println("[Δημιουργία Secretary]");
                    System.out.print("Username: "); username = input();
                    System.out.print("Password: "); password = input();
                    System.out.print("Name: "); name = input();
                    System.out.print("Surname: "); surname = input();
                    System.out.print("Department: "); department = input();
                    System.out.print("Office: "); office = input();
                    System.out.print("Work Hours: "); work_hours = input();
                    Secretaries newSec = new Secretaries(username, name, surname, department, office, work_hours, password);
                    newSec.addSecretaryToDB();
                    if (activeSecretary == null) activeSecretary = newSec;
                    break;
                case 5:
                    // Αυτόματη δημιουργία "εικονικής" γραμματείας αν δεν υπάρχει
                    if (activeSecretary == null) {
                        activeSecretary = new Secretaries("admin", "System", "Admin", "Γραμματεία", "Κεντρικό", "08:00-16:00", "123");
                    }

                    exitMenu = true;
                    int choice2 = -1;

                    while(choice2 != 0) {
                        System.out.println("\n--- Μενού Γραμματείας ---");
                        System.out.println("1. Ανάθεση Καθηγητών σε Μαθήματα");
                        System.out.println("2. Ανάθεση Βαθμών (από Καθηγητές)");
                        System.out.println("3. Προβολή Βαθμών Φοιτητών");
                        System.out.println("4. Δημιουργία Νέων Μαθημάτων");
                        System.out.println("0. Έξοδος");
                        System.out.print("Επιλογή: ");

                        choice2 = input1();

                        switch(choice2) {
                            case 1:
                                System.out.println("\n--- Διαδικασία Ανάθεσης Καθηγητών σε Μαθήματα ---");
                                if (profs.isEmpty() || courses.isEmpty()) {
                                    System.out.println("Πρέπει να υπάρχουν καταχωρημένοι καθηγητές και μαθήματα.");
                                    break;
                                }

                                System.out.print("Δώστε το Επώνυμο του Καθηγητή: ");
                                String profSurname = sc.nextLine();
                                Professors selectedProf = null;
                                for (Professors p : profs) {
                                    if (p.getSurname().equalsIgnoreCase(profSurname)) { selectedProf = p; break; }
                                }
                                if (selectedProf == null) { System.out.println("Δεν βρέθηκε τέτοιος καθηγητής."); break; }

                                System.out.print("Δώστε το Όνομα του Μαθήματος: ");
                                String courseName = sc.nextLine();
                                Courses selectedCourse = null;
                                for (Courses c : courses) {
                                    if (c.getName().equalsIgnoreCase(courseName)) { selectedCourse = c; break; }
                                }
                                if (selectedCourse == null) { System.out.println("Δεν βρέθηκε τέτοιο μάθημα."); break; }

                                selectedCourse.setAssignedProfessor(selectedProf);
                                selectedProf.addCourses(selectedCourse);
                                selectedCourse.updateProfessorInDB(selectedProf.getUsername());
                                break;

                            case 2:
                                System.out.println("\n--- Υποβολή Βαθμών ---");
                                if (profs.isEmpty() || students.isEmpty() || courses.isEmpty()) {
                                    System.out.println("Λείπουν δεδομένα (Καθηγητές/Φοιτητές/Μαθήματα) για βαθμολόγηση.");
                                    break;
                                }

                                System.out.print("Δώστε το Επώνυμο του Καθηγητή που βάζει τον βαθμό: ");
                                String gradProfSurname = sc.nextLine();
                                Professors gradingProf = null;
                                for (Professors p : profs) {
                                    if (p.getSurname().equalsIgnoreCase(gradProfSurname)) { gradingProf = p; break; }
                                }
                                if (gradingProf == null) { System.out.println("Δεν βρέθηκε τέτοιος καθηγητής."); break; }

                                System.out.print("Δώστε το Όνομα του Μαθήματος: ");
                                String gradCourseName = sc.nextLine();
                                Courses gradCourse = null;
                                for (Courses c : courses) {
                                    if (c.getName().equalsIgnoreCase(gradCourseName)) { gradCourse = c; break; }
                                }
                                if (gradCourse == null) { System.out.println("Δεν βρέθηκε τέτοιο μάθημα."); break; }

                                System.out.print("Δώστε το Επώνυμο του Φοιτητή που βαθμολογείται: ");
                                String gradStudentSurname = sc.nextLine();
                                Students gradStudent = null;
                                for (Students s : students) {
                                    if (s.getSurname().equalsIgnoreCase(gradStudentSurname)) { gradStudent = s; break; }
                                }
                                if (gradStudent == null) { System.out.println("Δεν βρέθηκε τέτοιος φοιτητής."); break; }

                                System.out.println("--- Βαθμολόγηση για τον/την " + gradStudent.getSurname() + " ---");
                                System.out.println("Εισάγετε τον Βαθμό (Ακέραιος):");

                                // ΕΔΩ ΚΑΛΕΙΤΑΙ Η ΜΕΘΟΔΟΣ ΣΟΥ
                                // Προσοχή: Στη 2η ερώτηση (Περιγραφή) ΜΗ βάλεις αριθμό γιατί η input() σου το απαγορεύει!
                                System.out.println("(Μετά τον βαθμό θα ζητηθεί Περιγραφή. Π.χ. γράψε 'Άριστα' ή 'Πέρασε', ΟΧΙ αριθμούς!)");
                                gradingProf.submitGrade(gradStudent, gradCourse, activeSecretary);
                                break;

                            case 3:
                                System.out.println("\n--- Τελική Προβολή Βαθμολογιών ---");
                                if (!students.isEmpty()) {
                                    for (Students student : students) {
                                        student.viewMyGrades();
                                    }
                                } else {
                                    System.out.println("Δεν υπάρχουν εγγεγραμμένοι φοιτητές στο σύστημα.");
                                }
                                break;

                            case 4:
                                System.out.println("\n--- Δημιουργία Μαθημάτων ---");
                                activeSecretary.createsCourses(courses);
                                break;

                            case 0:
                                System.out.println("Επιστροφή...");
                                break;
                            default:
                                System.out.println("Μη έγκυρη επιλογή. Παρακαλώ προσπαθήστε ξανά.");
                        }
                    }
                    break;
                case 0:
                    System.exit(0);
                default:
                    System.out.println("Μη έγκυρη επιλογή.");
            }
        }





        }


    }
