package Server.Tables;

import Server.Config.DatabaseConfigDto;
import java.io.File;
import java.io.FileNotFoundException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Scanner;

public class InitializerRepository {
    Connection connection;
    Statement statement;

    public InitializerRepository(DatabaseConfigDto config) {
        String url = String.format(
                "jdbc:postgresql://%s:%d/%s",
                config.getHost(),
                config.getPort(),
                config.getDatabase()
        );
        String user = config.getUsername();
        String pass = config.getPassword();

        try {
            this.connection = DriverManager.getConnection(url, user, pass);
            System.out.println("Connected to the PostgreSQL database for create tables.");
            this.statement = connection.createStatement();
        }
        catch (SQLException e) {
            e.printStackTrace();
        }

        this.createTables();
        this.closeConnection();
    }

    private void createTables() {
        String queryPath = System.getProperty("user.dir") + "\\src\\main\\java\\Server\\Tables\\up.sql";
        try {
            File myObj = new File(queryPath);
            Scanner myReader = new Scanner(myObj);
            StringBuilder queries = new StringBuilder();
            while (myReader.hasNextLine()) {
                queries.append(myReader.nextLine());
                queries.append("\n");
            }
            myReader.close();
            this.statement.execute(queries.toString());
        } catch (FileNotFoundException | SQLException e) {
            e.printStackTrace();
        }
    }

    private void closeConnection() {
        try {
            this.statement.close();
            this.connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
