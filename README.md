# Gym Management System - Database Connection Fix

## Problem
You're getting two errors:
1. `No suitable driver found for jdbc:oracle:thin:@localhost:1521:xe`
2. `NullPointerException: Cannot invoke "java.sql.Connection.prepareStatement(String)" because "<local1>" is null`

## Root Cause
- Oracle JDBC driver is not in the classpath
- The application doesn't handle null database connections properly

## Solution Steps

### Step 1: Download Oracle JDBC Driver
1. Go to Oracle's official website: https://www.oracle.com/database/technologies/appdev/jdbc-downloads.html
2. Download `ojdbc8.jar` or `ojdbc11.jar` (compatible with your Java version)
3. Place the downloaded JAR file in the `lib` folder in your project directory

### Step 2: Compile with JDBC Driver
```powershell
# Navigate to your project directory
cd "d:\GYM-MANAGEMENT-SYSTEM"

# Compile all Java files with the JDBC driver in classpath
javac -cp "lib/*;." *.java
```

### Step 3: Run with JDBC Driver
```powershell
# Test the database connection first
java -cp "lib/*;." TestConnection

# If connection works, run the main application
java -cp "lib/*;." Dashboard
```

### Step 4: Verify Oracle Database is Running
Make sure your Oracle database is:
- Running on localhost
- Listening on port 1521
- Has the service name 'orcl' (as configured in Database.java)
- The user 'SYS' exists with password 'Farhan9876'

### Alternative: Use Different Database
If you don't have Oracle installed, you can modify the Database.java to use:

#### H2 Database (Embedded)
```java
private static final String URL = "jdbc:h2:./gymdb";
private static final String USER = "sa";
private static final String PASSWORD = "";
```

#### MySQL
```java
private static final String URL = "jdbc:mysql://localhost:3306/gym_db";
private static final String USER = "root";
private static final String PASSWORD = "your_password";
```

#### SQLite
```java
private static final String URL = "jdbc:sqlite:gym.db";
private static final String USER = "";
private static final String PASSWORD = "";
```

## Files Modified
- `Database.java` - Added driver loading and better error handling
- `MemberManagement.java` - Added null connection checking
- `TestConnection.java` - Created for testing database connectivity

## Testing
Run `java -cp "lib/*;." TestConnection` to verify your database connection before using the main application.
