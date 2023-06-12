package Server.FileManager;

import Server.Config.DatabaseConfigDto;
import Shared.Entities.FileEntity;

import java.sql.*;

public class FileRepository {
    private Connection connection;
    private Statement statement;

    public FileRepository(DatabaseConfigDto config){
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
            System.out.println("Connected to the PostgreSQL database for File related functionalities.");
            this.statement = connection.createStatement();
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public int insertIntoTable(FileEntity fileEntity) throws SQLException {
        String query = "INSERT INTO \"file\" (name, \"memeType\", path) VALUES (?, ?, ?) RETURNING id;";
        try {
            PreparedStatement insertStatement = this.connection.prepareStatement(query);
            insertStatement.setString(1, fileEntity.getName());
            insertStatement.setString(2, fileEntity.getMemeType());
            insertStatement.setString(3, fileEntity.getPath());
            insertStatement.execute();
            ResultSet rs = insertStatement.getResultSet();
            rs.next();
            return rs.getInt(1);
        } catch (SQLException e) {
            e.printStackTrace();
            throw e;
        }
    }

    public FileEntity getFileInfo(int id) {
        FileEntity file = new FileEntity();
        String query = "SELECT * FROM \"file\" WHERE id = ?;";
        try {
            PreparedStatement selectStatement = this.connection.prepareStatement(query);
            selectStatement.setInt(1, id);
            ResultSet rs = selectStatement.executeQuery();
            if (rs.next()){
                file.setId(rs.getInt("id"));
                file.setName(rs.getString("name"));
                file.setMemeType(rs.getString("memeType"));
                file.setPath(rs.getString("path"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return file;
    }

    public void closeConnection() {
        try {
            this.statement.close();
            this.connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
