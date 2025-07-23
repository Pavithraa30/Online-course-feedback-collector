import java.io.FileWriter;
import java.sql.*;
import java.util.*;

public class FeedbackService {
    Scanner sc = new Scanner(System.in);

    // 1. Submit Feedback
    public void submitFeedback() {
        try (Connection con = DBConnection.getConnection()) {
            Student student = studentLoginOrRegister(con);

            List<Course> courses = getCourses(con);
            for (Course c : courses) {
                System.out.println(c.getId() + ". " + c.getName() + " - " + c.getInstructor());
            }

            System.out.print("Enter course ID: ");
            int courseId = sc.nextInt(); sc.nextLine();

            PreparedStatement check = con.prepareStatement("SELECT * FROM feedback WHERE student_id = ? AND course_id = ?");
            check.setInt(1, student.getId());
            check.setInt(2, courseId);
            ResultSet rs = check.executeQuery();

            if (rs.next()) {
                System.out.println("Feedback already exists. Update? (yes/no)");
                if (!sc.nextLine().equalsIgnoreCase("yes")) return;

                System.out.print("New rating (1-5): ");
                int rating = sc.nextInt(); sc.nextLine();
                System.out.print("New comment: ");
                String comments = sc.nextLine();

                PreparedStatement update = con.prepareStatement("UPDATE feedback SET rating=?, comments=?, feedback_date=NOW() WHERE id=?");
                update.setInt(1, rating);
                update.setString(2, comments);
                update.setInt(3, rs.getInt("id"));
                update.executeUpdate();

                System.out.println("✅ Feedback updated.");
            } else {
                System.out.print("Enter rating (1-5): ");
                int rating = sc.nextInt(); sc.nextLine();
                System.out.print("Enter comments: ");
                String comments = sc.nextLine();

                PreparedStatement insert = con.prepareStatement("INSERT INTO feedback(student_id, course_id, rating, comments) VALUES (?, ?, ?, ?)");
                insert.setInt(1, student.getId());
                insert.setInt(2, courseId);
                insert.setInt(3, rating);
                insert.setString(4, comments);
                insert.executeUpdate();

                System.out.println("✅ Feedback submitted.");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 2. Student login or register
    private Student studentLoginOrRegister(Connection con) throws SQLException {
        System.out.print("Enter email: ");
        String email = sc.nextLine();
        System.out.print("Enter password: ");
        String password = sc.nextLine();

        PreparedStatement ps = con.prepareStatement("SELECT * FROM students WHERE email=? AND password=?");
        ps.setString(1, email);
        ps.setString(2, password);
        ResultSet rs = ps.executeQuery();

        if (rs.next()) {
            Student s = new Student();
            s.setId(rs.getInt("id"));
            s.setName(rs.getString("name"));
            s.setEmail(email);
            s.setPassword(password);
            return s;
        } else {
            System.out.print("New user. Enter your name: ");
            String name = sc.nextLine();
            ps = con.prepareStatement("INSERT INTO students(name, email, password) VALUES (?, ?, ?)", Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, name);
            ps.setString(2, email);
            ps.setString(3, password);
            ps.executeUpdate();

            rs = ps.getGeneratedKeys();
            int id = rs.next() ? rs.getInt(1) : 0;
            Student s = new Student(name, email, password);
            s.setId(id);
            return s;
        }
    }

    // 3. Get all courses
    private List<Course> getCourses(Connection con) throws SQLException {
        List<Course> list = new ArrayList<>();
        ResultSet rs = con.createStatement().executeQuery("SELECT * FROM courses");
        while (rs.next()) {
            Course c = new Course();
            c.setId(rs.getInt("id"));
            c.setName(rs.getString("name"));
            c.setInstructor(rs.getString("instructor"));
            list.add(c);
        }
        return list;
    }

    // 4. Admin Login
    public boolean adminLogin() {
        try (Connection con = DBConnection.getConnection()) {
            System.out.print("Admin username: ");
            String uname = sc.nextLine();
            System.out.print("Password: ");
            String pass = sc.nextLine();

            PreparedStatement ps = con.prepareStatement("SELECT * FROM admin WHERE username = ? AND password = ?");
            ps.setString(1, uname);
            ps.setString(2, pass);
            ResultSet rs = ps.executeQuery();

            return rs.next();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    // 5. View Feedback Report (Admin)
    public void viewFeedbackReport() {
        try (Connection con = DBConnection.getConnection()) {
            String query = "SELECT s.name AS student, c.name AS course, f.rating, f.comments, f.feedback_date " +
                           "FROM feedback f JOIN students s ON f.student_id = s.id " +
                           "JOIN courses c ON f.course_id = c.id ORDER BY f.feedback_date DESC";

            ResultSet rs = con.createStatement().executeQuery(query);
            while (rs.next()) {
                System.out.printf("\nStudent: %s\nCourse: %s\nRating: %d\nComment: %s\nDate: %s\n",
                        rs.getString("student"), rs.getString("course"),
                        rs.getInt("rating"), rs.getString("comments"), rs.getTimestamp("feedback_date"));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 6. Export to CSV & Show in Console
    public void exportFeedbackToCSV() {
        try (Connection con = DBConnection.getConnection();
             FileWriter writer = new FileWriter("feedback_report.csv")) {

            String query = "SELECT s.name AS student, c.name AS course, f.rating, f.comments, f.feedback_date " +
                           "FROM feedback f JOIN students s ON f.student_id = s.id " +
                           "JOIN courses c ON f.course_id = c.id ORDER BY f.feedback_date DESC";

            ResultSet rs = con.createStatement().executeQuery(query);

            // Write CSV header
            writer.write("Student,Course,Rating,Comment,Date\n");

            // Console header
            System.out.println("\n=== Feedback Report ===");
            System.out.printf("%-15s %-20s %-6s %-20s %-20s\n", "Student", "Course", "Rate", "Comment", "Date");

            // Write data row by row
            while (rs.next()) {
                String student = rs.getString("student");
                String course = rs.getString("course");
                int rating = rs.getInt("rating");
                String comment = rs.getString("comments").replace("\"", "\"\"");
                Timestamp date = rs.getTimestamp("feedback_date");

                // Console display
                System.out.printf("%-15s %-20s %-6d %-20s %-20s\n", student, course, rating, comment, date.toString());

                // Write to CSV
                writer.write(String.format("\"%s\",\"%s\",%d,\"%s\",\"%s\"\n",
                        student, course, rating, comment, date.toString()));
            }

            writer.flush();
            System.out.println("\n CSV exported to feedback_report.csv");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
