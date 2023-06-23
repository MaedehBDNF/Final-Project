package Server.User;

import Shared.Entities.AlbumEntity;
import Shared.Entities.ArtistEntity;
import Server.Config.DatabaseConfigDto;
import Shared.Entities.GenreEntity;
import Shared.Dto.File.FileDto;
import Shared.Entities.UserEntity;

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
        String query = "SELECT " +
                "\"user\".id AS \"userId\", " +
                "\"user\".username AS \"userUsername\", " +
                "\"user\".password AS \"userPassword\", " +
                "\"user\".email AS \"userEmail\", " +
                "\"file\".id AS \"fileId\", " +
                "\"file\".name AS \"fileName\", " +
                "\"file\".\"memeType\" AS \"fileMemeType\" " +
                "FROM \"user\" " +
                "LEFT JOIN \"file\" ON \"user\".\"profilePictureId\" = \"file\".id " +
                "WHERE \"user\".username = ?;";
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
        ArrayList<Integer> userIds = new ArrayList<>();
        String query = "SELECT " +
                "\"user\".id AS \"userId\", " +
                "\"user\".username AS \"userUsername\", " +
                "\"user\".email AS \"userEmail\", " +
                "\"file\".id AS \"fileId\", " +
                "\"file\".name AS \"fileName\", " +
                "\"file\".\"memeType\" AS \"fileMemeType\" " +
                "FROM \"user\" " +
                "LEFT JOIN \"file\" ON \"user\".\"profilePictureId\" = \"file\".id " +
                "WHERE \"user\".username LIKE ? " +
                "ORDER BY \"user\".id DESC;";
        try {
            PreparedStatement selectStatement = this.connection.prepareStatement(query);
            selectStatement.setString(1, "%" + username + "%");
            ResultSet rs = selectStatement.executeQuery();
            FileDto profilePicture = new FileDto();
            while (rs.next()){
                int userId = rs.getInt("userId");
                if (userIds.contains(userId)) continue;
                userIds.add(userId);
                UserEntity userEntity = new UserEntity();
                userEntity.setId(userId);
                userEntity.setUsername(rs.getString("userUsername"));
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
        String query = "SELECT " +
                "\"user\".id AS \"userId\", " +
                "username, " +
                "email, " +
                "\"file\".id AS \"fileId\", " +
                "name, " +
                "\"memeType\" " +
                "FROM \"user\" " +
                "LEFT JOIN \"file\" ON \"user\".\"profilePictureId\" = \"file\".id " +
                "WHERE \"user\".id = ?;";
        try {
            PreparedStatement selectStatement = this.connection.prepareStatement(query);
            selectStatement.setInt(1, id);
            ResultSet rs = selectStatement.executeQuery();
            FileDto profilePicture = new FileDto();
            if (rs.next()){
                userEntity.setId(rs.getInt("userId"));
                userEntity.setUsername(rs.getString("username"));
                userEntity.setEmail(rs.getString("email"));
                profilePicture.setId(rs.getInt("fileId"));
                profilePicture.setName(rs.getString("name"));
                profilePicture.setMemeType(rs.getString("memeType"));
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


    public boolean doesUserFollowedArtist(int userId, int artistId) {
        String query = "SELECT * FROM \"following\" WHERE \"userId\" = ? AND \"artistId\" = ?;";
        try {
            PreparedStatement selectStatement = this.connection.prepareStatement(query);
            selectStatement.setInt(1, userId);
            selectStatement.setInt(2, artistId);
            ResultSet rs = selectStatement.executeQuery();
            if (rs.next()) {
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean doesUserFollowedUser(int userId, int friendId) {
        String query = "SELECT * FROM \"friend\" WHERE \"userId\" = ? AND \"friendId\" = ?;";
        try {
            PreparedStatement selectStatement = this.connection.prepareStatement(query);
            selectStatement.setInt(1, userId);
            selectStatement.setInt(2, friendId);
            ResultSet rs = selectStatement.executeQuery();
            if (rs.next()) {
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public ArrayList<AlbumEntity> findUserLikedAlbums(int userId){
        ArrayList<AlbumEntity> albums = new ArrayList<>();
        ArrayList<Integer> albumIds = new ArrayList<>();
        String query = "SELECT " +
                "\"albumId\", " +
                "title, " +
                "\"artistId\", " +
                "\"artist\".name AS \"artistName\", " +
                "\"album\".\"genreId\" AS \"albumGenreId\", " +
                "\"genre\".name AS \"genreName\", " +
                "\"releaseDate\", " +
                "popularity " +
                "FROM \"userAlbums\" " +
                "LEFT JOIN \"album\" ON \"userAlbums\".\"albumId\" = \"album\".id " +
                "LEFT JOIN \"artist\" ON \"album\".\"artistId\" = \"artist\".id " +
                "LEFT JOIN \"genre\" ON \"album\".\"genreId\" = \"genre\".id " +
                "WHERE \"userId\" = ?;";
        try {
            PreparedStatement selectStatement = this.connection.prepareStatement(query);
            selectStatement.setInt(1, userId);
            ResultSet rs = selectStatement.executeQuery();
            AlbumEntity album = new AlbumEntity();
            ArtistEntity artist = new ArtistEntity();
            GenreEntity genre = new GenreEntity();
            while (rs.next()){
                int albumId = rs.getInt("albumId");
                if (albumIds.contains(albumId)) continue;
                albumIds.add(albumId);
                album.setId(albumId);
                album.setTitle(rs.getString("title"));
                artist.setId(rs.getInt("artistId"));
                artist.setName(rs.getString("artistName"));
                album.setArtist(artist);
                genre.setId(rs.getInt("albumGenreId"));
                genre.setName(rs.getString("genreName"));
                album.setGenre(genre);
                album.setReleaseDate(rs.getDate("releaseDate").toLocalDate());
                album.setPopularity(rs.getInt("popularity"));
                albums.add(album);
            }
        } catch (SQLException e){
            e.printStackTrace();
        }
        return albums;
    }

    public ArrayList<UserEntity> getUserFriends(int userId) {
        ArrayList<UserEntity> friends = new ArrayList<>();
        String query = "SELECT " +
                "\"user\".id AS \"userId\", " +
                "\"user\".username AS \"userUsername\", " +
                "\"file\".id AS \"fileId\", " +
                "\"file\".name AS \"fileName\", " +
                "\"file\".\"memeType\" AS \"fileMemeType\" " +
                "FROM \"friend\" " +
                "LEFT JOIN \"user\" on \"friend\".\"friendId\" = \"user\".id " +
                "LEFT JOIN \"file\" ON \"user\".\"profilePictureId\" = \"file\".id " +
                "WHERE \"friend\".\"userId\" = ?;";
        try {
            PreparedStatement insertStatement = this.connection.prepareStatement(query);
            insertStatement.setInt(1, userId);
            ResultSet rs = insertStatement.executeQuery();
            while (rs.next()) {
                UserEntity userEntity = new UserEntity();
                FileDto profilePicture = new FileDto();
                userEntity.setId(rs.getInt("userId"));
                userEntity.setUsername(rs.getString("userUsername"));
                profilePicture.setId(rs.getInt("fileId"));
                profilePicture.setName(rs.getString("fileName"));
                profilePicture.setMemeType(rs.getString("fileMemeType"));
                userEntity.setProfilePicture(profilePicture);
                friends.add(userEntity);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return friends;
    }

    public ArrayList<ArtistEntity> getUserFollowings(int userId) {
        ArrayList<ArtistEntity> followings = new ArrayList<>();
        String query = "SELECT " +
                "\"artist\".id AS \"artistId\", " +
                "\"artist\".name AS \"artistName\", " +
                "\"file\".id AS \"fileId\", " +
                "\"file\".name AS \"fileName\", " +
                "\"file\".\"memeType\" AS \"fileMemeType\" " +
                "FROM \"following\" " +
                "LEFT JOIN \"artist\" on \"following\".\"artistId\" = \"artist\".id " +
                "LEFT JOIN \"file\" ON \"artist\".\"profilePictureId\" = \"file\".id " +
                "WHERE \"following\".\"userId\" = ?;";
        try {
            PreparedStatement insertStatement = this.connection.prepareStatement(query);
            insertStatement.setInt(1, userId);
            ResultSet rs = insertStatement.executeQuery();
            while (rs.next()) {
                ArtistEntity artistEntity = new ArtistEntity();
                FileDto profilePicture = new FileDto();
                artistEntity.setId(rs.getInt("artistId"));
                artistEntity.setName(rs.getString("artistName"));
                profilePicture.setId(rs.getInt("fileId"));
                profilePicture.setName(rs.getString("fileName"));
                profilePicture.setMemeType(rs.getString("fileMemeType"));
                artistEntity.setProfilePicture(profilePicture);
                followings.add(artistEntity);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return followings;
    }

    public void close(){
        try {
            this.connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
