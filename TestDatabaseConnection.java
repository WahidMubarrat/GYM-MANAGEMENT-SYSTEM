public class TestDatabaseConnection {
    public static void main(String[] args) {
        System.out.println("Testing Database Connection...");
        System.out.println("=============================");
        
        // Test the connection
        boolean connected = Database.testConnection();
        
        if (connected) {
            System.out.println("✅ Database connection successful!");
        } else {
            System.out.println("❌ Database connection failed!");
        }
        
        System.out.println("=============================");
    }
}
