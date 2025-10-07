import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class Database {

    private static Database instance;
    private Connection connection;

    private Database() {}

    public static Database getInstance() {
        if (instance == null) {
            instance = new Database();
        }
        return instance;
    }

    public void connect() {
        Properties props = new Properties();
        try (FileInputStream fis = new FileInputStream("config.properties")) {
            props.load(fis);

            String url = props.getProperty("jdbc.url");
            String user = props.getProperty("jdbc.user");
            String password = props.getProperty("jdbc.password");
            String driver = props.getProperty("jdbc.driver");

            if (driver != null && !driver.isBlank()) {
                Class.forName(driver);
            }

            connection = DriverManager.getConnection(url, user, password);
            connection.setAutoCommit(true);

            System.out.println("Connected to DB successfully!");
        } catch (IOException | ClassNotFoundException | SQLException e) {
            throw new RuntimeException("Could not connect to DB: " + e.getMessage(), e);
        }
    }

    public Connection getConnection() {
        if (connection == null) {
            throw new IllegalStateException("Database not connected yet!");
        }
        return connection;
    }
}
