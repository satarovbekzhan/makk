package sample.database;

import com.microsoft.sqlserver.jdbc.SQLServerDriver;

import java.sql.*;

public abstract class DB {
    public static TestRepo testRepo;
    public static UserRepo userRepo;
    public static CategoryRepo categoryRepo;
    public static ProductRepo productRepo;
    public static CompositionRepo compositionRepo;
    public static OrderRepo orderRepo;

    private final String url;

    protected DB(String url) {
        this.url = url;
    }

    protected Connection getConnection() throws SQLException {
        DriverManager.registerDriver(new SQLServerDriver());
        return DriverManager.getConnection(this.url);
    }

    public static void initializeDatabaseModels(String username, String password) throws SQLException {
        // Create connection url from given username and password
        String host = "127.0.0.1";
        String port = "1433";
        String database = "mmm";
        String connUrl = "jdbc:sqlserver://" + host + ":" + port +
                ";databaseName=" + database + ";user=" + username + ";password=" + password + ";";

        // Create an instance of a TestRepo and try to get connection
        testRepo = new TestRepo(connUrl);
        testRepo.getUserRole();

        // Instantiate other database repositories
        userRepo = new UserRepo(connUrl);
        categoryRepo = new CategoryRepo(connUrl);
        productRepo = new ProductRepo(connUrl);
        compositionRepo = new CompositionRepo(connUrl);
        orderRepo = new OrderRepo(connUrl);
    }
}
