package Server.Genre;

import Server.Config.DatabaseConfigDto;
import java.sql.*;
import java.util.ArrayList;

public class GenreRepository {
    Connection connection;
    Statement statement;

    public GenreRepository(DatabaseConfigDto config){
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
            System.out.println("Connected to the PostgreSQL database in artist repository.");
            this.statement = connection.createStatement();
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public GenreEntity insertIntoTable(GenreEntity genreEntity) {
        String query = "INSERT INTO \"genre\" (name, description) VALUES (?, ?) RETURNING id;";
        try {
            PreparedStatement insertStatement = this.connection.prepareStatement(query);
            insertStatement.setString(1, genreEntity.getName());
            insertStatement.setString(2, genreEntity.getDescription());
            insertStatement.execute();

            ResultSet rs = insertStatement.getResultSet();
            if (rs.next()) {
                genreEntity.setId(rs.getInt("id"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return genreEntity;
    }

    public GenreEntity findOne(int id){
        GenreEntity genre = new GenreEntity();
        String query = "SELECT * From \"genre\" WHERE id = ?";
        try {
            PreparedStatement selectStatement = this.connection.prepareStatement(query);
            selectStatement.setInt(1, id);
            ResultSet rs = selectStatement.executeQuery();
            if (rs.next()){
                genre.setId(rs.getInt("id"));
                genre.setName(rs.getString("name"));
                genre.setDescription(rs.getString("description"));
            }
        } catch (SQLException e){
            e.printStackTrace();
        }
        return genre;
    }

    public ArrayList<GenreEntity> findAll(){
        ArrayList<GenreEntity> genres = new ArrayList<>();
        String query = "SELECT * From \"file\"";
        try {
            PreparedStatement selectStatement = this.connection.prepareStatement(query);
            ResultSet rs = selectStatement.executeQuery();
            while (rs.next()){
                GenreEntity genre = new GenreEntity();
                genre.setId(rs.getInt("id"));
                genre.setName(rs.getString("name"));
                genre.setDescription(rs.getString("description"));
                genres.add(genre);
            }
        } catch (SQLException e){
            e.printStackTrace();
        }
        return genres;
    }

    public GenreEntity findByName(String name){
        GenreEntity genre = new GenreEntity();
        String query = "SELECT * From \"genre\" WHERE name = ?";
        try {
            PreparedStatement selectStatement = this.connection.prepareStatement(query);
            selectStatement.setString(1, name);
            ResultSet rs = selectStatement.executeQuery();
            if (rs.next()){
                genre.setId(rs.getInt("id"));
                genre.setName(rs.getString("name"));
                genre.setDescription(rs.getString("description"));
            }
        } catch (SQLException e){
            e.printStackTrace();
        }
        return genre;
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
