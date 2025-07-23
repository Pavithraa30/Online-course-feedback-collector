import java.sql.*;

public class DBConnection {
    private static final String URL = "jdbc:mysql://localhost:3306/feedback_system";
    private static final String USER = "root";
    private static final String PASSWORD = "Pavi@2004"; // Change if your MySQL password is set

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}
