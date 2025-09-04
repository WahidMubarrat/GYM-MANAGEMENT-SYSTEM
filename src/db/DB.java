package db;


import java.sql.*;
import oracle.jdbc.OracleConnection;

public final class DB {
private static final String URL = "jdbc:oracle:thin:@//localhost:1521/ORCLPDB";

    private static final String USER = "GYM_USER";   // your DB user
    private static final String PASS = "gym123";     // your DB password

    private DB(){}

    public static Connection get() throws SQLException {
        Connection c = DriverManager.getConnection(URL, USER, PASS);
        c.setAutoCommit(true);
        return c;
    }

    public static OracleConnection ora(Connection c) throws SQLException {
        if (c.isWrapperFor(OracleConnection.class)) {
            return c.unwrap(OracleConnection.class);
        }
        throw new SQLException("Not an OracleConnection");
    }
}
