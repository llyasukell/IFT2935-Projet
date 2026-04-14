import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class Db {

    private static final String CONFIG_FILE = "db.properties";

    public static Connection getConnection() throws SQLException {
        Properties props = new Properties();
        try (FileInputStream in = new FileInputStream(CONFIG_FILE)) {
            props.load(in);
        } catch (IOException e) {
            throw new SQLException("Impossible de lire " + CONFIG_FILE + " : " + e.getMessage());
        }

        String url = props.getProperty("url");
        String user = props.getProperty("user");
        String password = props.getProperty("password");

        if (url == null || user == null || password == null) {
            throw new SQLException("db.properties doit contenir url, user, password");
        }

        return DriverManager.getConnection(url, user, password);
    }
}
