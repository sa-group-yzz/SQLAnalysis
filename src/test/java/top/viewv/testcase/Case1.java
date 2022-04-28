package top.viewv.testcase;

import java.sql.*;

public class Case1 {
    static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";
    static final String DB_URL = "jdbc:mysql://test:3306/test";
    static final String USER = "root";
    static final String PASS = "password";

    public static void main(String[] args) throws SQLException, ClassNotFoundException {
        Connection conn = null;
        Statement stmt = null;
        Class.forName(JDBC_DRIVER);
        System.out.println("Connecting to database...");
        conn = DriverManager.getConnection(DB_URL, USER, PASS);
        stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery("SELECT id,name,price from cars where price >=10000 limit 1");
        rs.next();
        int a;
        int b = args.length;
        if(rs.getInt(3) > 100) {
            a = 1;
        } else {
            a = b + 1;
        }
    }
}