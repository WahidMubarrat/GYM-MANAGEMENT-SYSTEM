import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Database {
    private static final String URL = "jdbc:oracle:thin:@localhost:1521:orcl";
    private static final String USER = "c##gym_admin";
    private static final String PASSWORD = "gym123";
    
    // Static block to load the Oracle JDBC driver
    static {
        try {
            Class.forName("oracle.jdbc.driver.OracleDriver");
            System.out.println("Oracle JDBC Driver loaded successfully!");
        } catch (ClassNotFoundException e) {
            System.err.println("Oracle JDBC Driver not found! Please add ojdbc jar to classpath.");
            System.err.println("Error: " + e.getMessage());
        }
    }
    
    // Connect to the database
    public static Connection connect() {
        try {
            System.out.println("Attempting to connect to database...");
            Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
            System.out.println("Database connection successful!");
            return conn;
        } catch (SQLException e) {
            System.err.println("Error connecting to the database: " + e.getMessage());
            System.err.println("Please check:");
            System.err.println("1. Oracle database is running");
            System.err.println("2. Connection URL is correct: " + URL);
            System.err.println("3. Username and password are correct");
            System.err.println("4. Oracle JDBC driver is in classpath");
            return null;
        }
    }
    
    // Test database connection
    public static boolean testConnection() {
        Connection conn = connect();
        if (conn != null) {
            try {
                conn.close();
                return true;
            } catch (SQLException e) {
                System.err.println("Error closing test connection: " + e.getMessage());
            }
        }
        return false;
    }
}

