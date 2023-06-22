package Server.Album;

import Shared.Dto.File.FileDto;
import Shared.Entities.*;
import Server.Config.DatabaseConfigDto;
import Shared.Dto.Album.CreatorAlbumDto;
import Shared.Dto.Album.LikeAlbumDto;

import java.sql.*;
import java.util.ArrayList;

public class AlbumRepository {
    private Connection connection;

    public AlbumRepository(DatabaseConfigDto config) {
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
            System.out.println("Connected to the PostgreSQL database in album repository.");
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public AlbumEntity insertIntoTable(CreatorAlbumDto creatorAlbumDto){
        AlbumEntity album = new AlbumEntity();
        String query = "INSERT INTO \"album\" (title, \"artistId\", \"genreId\", \"releaseDate\", popularity, \"coverId\") VALUES (?, ?, ?, ?, ?, ?) RETURNING id;";
        try {
            PreparedStatement insertStatement = this.connection.prepareStatement(query);
            insertStatement.setString(1, creatorAlbumDto.getTitle());
            insertStatement.setInt(2, creatorAlbumDto.getArtistId());
            insertStatement.setInt(3, creatorAlbumDto.getGenreId());
            insertStatement.setDate(4, Date.valueOf(creatorAlbumDto.getReleaseDate()));
            insertStatement.setInt(5, creatorAlbumDto.getPopularity());
            insertStatement.setInt(6, creatorAlbumDto.getCoverId());
            insertStatement.execute();
            ResultSet rs = insertStatement.getResultSet();
            if (rs.next()){
                album.setId(rs.getInt("id"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return album;
    }

    public AlbumEntity findOne(int id){
        AlbumEntity album = new AlbumEntity();
        String query = "SELECT " +
                "\"album\".id AS \"albumId\", " +
                "title, " +
                "\"artist\".id AS \"artistId\", " +
                "\"artist\".name AS \"artistName\", " +
                "\"genre\".id AS \"genreId\", " +
                "\"genre\".name AS \"genreName\", " +
                "\"releaseDate\", " +
                "popularity, " +
                "\"file\".id AS \"coverId\"," +
                "\"file\".name AS \"coverName\", " +
                "\"file\".\"memeType\" AS \"coverMemeType\" " +
                "FROM \"album\" " +
                "LEFT JOIN \"artist\" ON \"album\".\"artistId\" = \"artist\".id " +
                "LEFT JOIN \"genre\" ON \"album\".\"genreId\" = \"genre\".id " +
                "LEFT JOIN \"file\" ON \"album\".\"coverId\" = \"file\".id " +
                "WHERE \"album\".id = ?;";
        try {
            PreparedStatement selectStatement = this.connection.prepareStatement(query);
            selectStatement.setInt(1, id);
            ResultSet rs = selectStatement.executeQuery();
            ArtistEntity artist = new ArtistEntity();
            GenreEntity genre = new GenreEntity();
            FileDto cover = new FileDto();
            if (rs.next()){
                album.setId(rs.getInt("albumId"));
                album.setTitle(rs.getString("title"));
                artist.setId(rs.getInt("artistId"));
                artist.setName(rs.getString("artistName"));
                album.setArtist(artist);
                genre.setId(rs.getInt("genreId"));
                genre.setName(rs.getString("genreName"));
                album.setGenre(genre);
                album.setReleaseDate(rs.getDate("releaseDate").toLocalDate());
                album.setPopularity(rs.getInt("popularity"));
                cover.setId(rs.getInt("coverId"));
                cover.setName(rs.getString("coverName"));
                cover.setMemeType(rs.getString("coverMemeType"));
                album.setCover(cover);
            }
        } catch (SQLException e){
            e.printStackTrace();
        }
        return album;
    }

    public ArrayList<MusicEntity> findAlbumSongs(int albumId){
        ArrayList<MusicEntity> musics = new ArrayList<>();
        ArrayList<Integer> musicIds = new ArrayList<>();
        String query = "SELECT " +
                "\"music\".id AS \"musicId\", " +
                "\"music\".title AS \"musicTitle\", " +
                "\"music\".\"artistId\" AS \"musicArtistId\", " +
                "\"artist\".name AS \"artistName\", " +
                "\"music\".\"genreId\" AS \"musicGenreId\", " +
                "\"genre\".\"name\" AS \"genreName\", " +
                "duration, " +
                "\"music\".\"releaseDate\" AS \"musicReleaseDate\", " +
                "\"music\".popularity AS \"musicPopularity\" " +
                "FROM \"album\" " +
                "LEFT JOIN \"music\" ON \"music\".\"albumId\" = \"album\".id " +
                "LEFT JOIN \"genre\" ON \"music\".\"genreId\" = \"genre\".id " +
                "LEFT JOIN \"artist\" ON \"album\".\"artistId\" = \"artist\".id " +
                "WHERE \"album\".id = ?;";
        try {
            PreparedStatement selectStatement = this.connection.prepareStatement(query);
            selectStatement.setInt(1, albumId);
            ResultSet rs = selectStatement.executeQuery();
            while (rs.next()){
                int musicId = rs.getInt("musicId");
                if (musicIds.contains(musicId)) continue;
                MusicEntity music = new MusicEntity();
                ArtistEntity artist = new ArtistEntity();
                GenreEntity genre = new GenreEntity();
                music.setId(musicId);
                music.setTitle(rs.getString("musicTitle"));
                artist.setId(rs.getInt("musicArtistId"));
                artist.setName(rs.getString("artistName"));
                music.setArtist(artist);
                genre.setId(rs.getInt("musicGenreId"));
                genre.setName(rs.getString("genreName"));
                music.setGenre(genre);
                music.setDuration(rs.getInt("duration"));
                music.setReleaseDate(rs.getDate("musicReleaseDate").toLocalDate());
                music.setPopularity(rs.getInt("musicPopularity"));
                musicIds.add(musicId);
                musics.add(music);
            }
        } catch (SQLException e){
            e.printStackTrace();
        }
        return musics;
    }

    public ArrayList<AlbumEntity> search(String str){
        ArrayList<AlbumEntity> albums = new ArrayList<>();
        ArrayList<Integer> albumIds = new ArrayList<>();
        String query = "SELECT " +
                "\"album\".id AS \"albumId\", " +
                "\"album\".title AS \"albumTitle\", " +
                "\"artist\".id AS \"artistId\", " +
                "\"artist\".name AS \"artistName\", " +
                "popularity " +
                "FROM \"album\" " +
                "LEFT JOIN \"artist\" ON \"album\".\"artistId\" = \"artist\".id " +
                "LEFT JOIN \"genre\" ON \"album\".\"genreId\" = \"genre\".id " +
                "WHERE \"album\".title LIKE ? " +
                "OR \"artist\".name LIKE ? " +
                "OR \"genre\".name LIKE ? " +
                "ORDER BY popularity DESC;";
        try {
            PreparedStatement selectStatement = this.connection.prepareStatement(query);
            selectStatement.setString(1, "%" + str + "%");
            selectStatement.setString(2, "%" + str + "%");
            selectStatement.setString(3, "%" + str + "%");
            ResultSet rs = selectStatement.executeQuery();
            while (rs.next()){
                int albumId = rs.getInt("albumId");
                if (albumIds.contains(albumId)) continue;
                albumIds.add(albumId);
                AlbumEntity album = new AlbumEntity();
                ArtistEntity artist = new ArtistEntity();
                album.setId(albumId);
                album.setTitle(rs.getString("albumTitle"));
                artist.setId(rs.getInt("artistId"));
                artist.setName(rs.getString("artistName"));
                album.setArtist(artist);
                album.setPopularity(rs.getInt("popularity"));
                albums.add(album);
            }
        } catch (SQLException e){
            e.printStackTrace();
        }
        return albums;
    }

    public boolean increasePopularity(int id){
        String query = "UPDATE \"album\" SET popularity = popularity + 1 WHERE id = ?;";
        try {
            PreparedStatement updateStatement = this.connection.prepareStatement(query);
            updateStatement.setInt(1, id);
            updateStatement.execute();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean addToUserLikedAlbums(LikeAlbumDto likeDto){
        String query = "INSERT INTO \"userAlbums\" (\"userId\", \"albumId\") VALUES (?, ?);";
        try {
            PreparedStatement insertStatement = this.connection.prepareStatement(query);
            insertStatement.setInt(1, likeDto.getUserid());
            insertStatement.setInt(2, likeDto.getAlbumId());
            insertStatement.execute();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public void closeConnection() {
        try {
            this.connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
