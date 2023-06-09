package Server.User;

import Server.Config.DatabaseConfigDto;
import Shared.Dto.File.FileDto;

import java.sql.*;
import java.util.ArrayList;

public class UserRepository {
    private Connection connection;

    public UserRepository(DatabaseConfigDto config){
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
            System.out.println("Connected to the PostgreSQL database for User related functionalities.");
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public UserEntity insertIntoTable(UserEntity userEntity) {
        String query = "INSERT INTO \"user\" (username, password, email) VALUES (?, ?, ?) RETURNING id;";
        try {
            PreparedStatement insertStatement = this.connection.prepareStatement(query);
            insertStatement.setString(1, userEntity.getUsername());
            insertStatement.setString(2, userEntity.getPassword());
            insertStatement.setString(3, userEntity.getEmail());
            insertStatement.execute();
            ResultSet rs = insertStatement.getResultSet();
            if (rs.next()) {
                userEntity.setId(rs.getInt("id"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return userEntity;
    }

    public UserEntity findByUsername(String username) {
        UserEntity userEntity = new UserEntity();
        String query = "SELECT \"user\".id AS \"userId\", \"user\".username AS \"userUsername\", \"user\".password AS \"userPassword\", \"user\".email AS \"userEmail\", \"file\".id AS \"fileId\", \"file\".name AS \"fileName\", \"file\".\"memeType\" AS \"fileMemeType\" FROM \"user\" LEFT JOIN \"file\" ON \"user\".\"profilePictureId\" = \"file\".id WHERE \"user\".username = ?;";
        try {
            PreparedStatement selectStatement = this.connection.prepareStatement(query);
            selectStatement.setString(1, username);
            ResultSet rs = selectStatement.executeQuery();
            FileDto profilePicture = new FileDto();
            if (rs.next()){
                userEntity.setId(rs.getInt("userId"));
                userEntity.setUsername(rs.getString("userUsername"));
                userEntity.setPassword(rs.getString("userPassword"));
                userEntity.setEmail(rs.getString("userEmail"));
                profilePicture.setId(rs.getInt("fileId"));
                profilePicture.setName(rs.getString("fileName"));
                profilePicture.setMemeType(rs.getString("fileMemeType"));
                userEntity.setProfilePicture(profilePicture);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return userEntity;
    }

    public ArrayList<UserEntity> searchUser(String username) {
        ArrayList<UserEntity> userEntities = new ArrayList<>();
        String query = "SELECT \"user\".id AS \"userId\", \"user\".username AS \"userUsername\", \"user\".password AS \"userPassword\", \"user\".email AS \"userEmail\", \"file\".id AS \"fileId\", \"file\".name AS \"fileName\", \"file\".\"memeType\" AS \"fileMemeType\" FROM \"user\" LEFT JOIN \"file\" ON \"user\".\"profilePictureId\" = \"file\".id WHERE \"user\".username LIKE ? ORDER BY \"user\".id DESC;";
        try {
            PreparedStatement selectStatement = this.connection.prepareStatement(query);
            selectStatement.setString(1, "%" + username + "%");
            ResultSet rs = selectStatement.executeQuery();
            FileDto profilePicture = new FileDto();
            while (rs.next()){
                UserEntity userEntity = new UserEntity();
                userEntity.setId(rs.getInt("userId"));
                userEntity.setUsername(rs.getString("userUsername"));
                userEntity.setPassword(rs.getString("userPassword"));
                userEntity.setEmail(rs.getString("userEmail"));

                profilePicture.setId(rs.getInt("fileId"));
                profilePicture.setName(rs.getString("fileName"));
                profilePicture.setMemeType(rs.getString("fileMemeType"));
                userEntity.setProfilePicture(profilePicture);

                userEntities.add(userEntity);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return userEntities;
    }

    public UserEntity findOne(int id) {
        UserEntity userEntity = new UserEntity();
        String query = "SELECT \"user\".id AS \"userId\", \"user\".username AS \"userUsername\", \"user\".password AS \"userPassword\", \"user\".email AS \"userEmail\", \"file\".id AS \"fileId\", \"file\".name AS \"fileName\", \"file\".\"memeType\" AS \"fileMemeType\" FROM \"user\" LEFT JOIN \"file\" ON \"user\".\"profilePictureId\" = \"file\".id WHERE \"user\".id = ?;";
        try {
            PreparedStatement selectStatement = this.connection.prepareStatement(query);
            selectStatement.setInt(1, id);
            ResultSet rs = selectStatement.executeQuery();
            FileDto profilePicture = new FileDto();
            if (rs.next()){
                userEntity.setId(rs.getInt("userId"));
                userEntity.setUsername(rs.getString("userUsername"));
                userEntity.setPassword(rs.getString("userPassword"));
                userEntity.setEmail(rs.getString("userEmail"));
                profilePicture.setId(rs.getInt("fileId"));
                profilePicture.setName(rs.getString("fileName"));
                profilePicture.setMemeType(rs.getString("fileMemeType"));
                userEntity.setProfilePicture(profilePicture);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return userEntity;
    }

    public boolean updateUserProfilePicture(int userId, int profilePhotoId) {
        String query = "UPDATE \"user\" SET \"profilePictureId\" = ? WHERE id = ?;";
        try {
            PreparedStatement updateStatement = this.connection.prepareStatement(query);
            updateStatement.setInt(1, profilePhotoId);
            updateStatement.setInt(2, userId);
            updateStatement.execute();
            return true;
        } catch (SQLException e){
            e.printStackTrace();
            return false;
        }
    }

    public boolean followUser(int userId, int friendId) {
        String query = "INSERT INTO \"friend\" (\"userId\", \"friendId\") VALUES (?, ?);";
        try {
            PreparedStatement insertStatement = this.connection.prepareStatement(query);
            insertStatement.setInt(1, userId);
            insertStatement.setInt(2, friendId);
            insertStatement.execute();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean followArtist(int userId, int artistId) {
        String query = "INSERT INTO \"following\" (\"userId\", \"artistId\") VALUES (?, ?);";
        try {
            PreparedStatement insertStatement = this.connection.prepareStatement(query);
            insertStatement.setInt(1, userId);
            insertStatement.setInt(2, artistId);
            insertStatement.execute();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public void close(){
        try {
            this.connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
