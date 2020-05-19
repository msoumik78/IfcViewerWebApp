
package dao;

import org.apache.commons.dbutils.DbUtils;
import org.apache.log4j.Logger;
import util.ApplicationException;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import java.io.InputStream;
import java.sql.*;
import java.util.Properties;

public class DBUtil {

    private static String dbUrl = null;
    private static String dbUser = null;
    private static String dbPassword = null;
    private static String dbDriver = null;

    static final Logger LOGGER = Logger.getLogger("applicationError");


    static {
        try (InputStream input = DBUtil.class.getClassLoader().getResourceAsStream("database.properties")) {
            Properties prop = new Properties();
            prop.load(input);

            dbDriver = prop.getProperty("db.driver");
            dbUrl = prop.getProperty("db.url");
            dbUser = prop.getProperty("db.user");
            dbPassword = prop.getProperty("db.password");

            Class.forName(dbDriver);
        } catch (Exception ex) {
            LOGGER.error("In DBUtil static block : Exception encountered ", ex);
            throw new ApplicationException("Exception encountered in DBUtil static block :"+ex.getMessage());
        }
    }

/*    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(dbUrl);
    }*/

    public static Connection getConnection()  {
        Connection connection = null;
        try {
            Context context = new InitialContext();
            DataSource dataSource = (DataSource) context.lookup("java:comp/env/jdbc/myapp");
            try {
                connection= dataSource.getConnection();
            } catch (SQLException e) {
                LOGGER.error("In DBUtil.getConnection : SQL exception encountered ", e);
                throw new ApplicationException("SQL Exception encountered in DBUtil.getConnection :"+e.getMessage());            }
        } catch (NamingException e) {
            LOGGER.error("In DBUtil.getConnection : NamingException encountered ", e);
            throw new ApplicationException("NamingException encountered in DBUtil.getConnection :"+e.getMessage());                  }
        return connection;
    }

    public static void closeResources(ResultSet rs, Statement stmt, Connection con)  {
        DbUtils.closeQuietly(rs);
        DbUtils.closeQuietly(stmt);
        DbUtils.closeQuietly(con);
    }

    public static void closeResources(Statement stmt, Connection con) {
        DbUtils.closeQuietly(stmt);
        DbUtils.closeQuietly(con);
    }

    public static void closeResources(PreparedStatement stmt, Connection con) {
        DbUtils.closeQuietly(stmt);
        DbUtils.closeQuietly(con);
    }

}
