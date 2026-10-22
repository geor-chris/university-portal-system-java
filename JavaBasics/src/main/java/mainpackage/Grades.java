package mainpackage;

public class Grades {

    int grade;
    String description;
    private Courses course;
    int semester;

    public Grades(int grade, String description, int semester) {
        this.grade = grade;
        this.description = description;
        this.semester=semester;
    }

    public void setDescription(String description) {
        this.description = description;
    }
    public void setGrade(int grade) {
        this.grade = grade;
    }
    public void setCourse(Courses course) { this.course = course; }

    public Courses getCourse() { return course; }
    public String getDescription() {
        return description;
    }
    public int getGrade() {
        return grade;
    }



    public Grades() {}

    public String toString() {
        return grade + " " + description;
    }

}
