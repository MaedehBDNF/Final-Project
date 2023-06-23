package Server.Music;

import Shared.Entities.AlbumEntity;
import Shared.Entities.ArtistEntity;
import Server.Config.DatabaseConfigDto;
import Shared.Entities.GenreEntity;
import Shared.Dto.File.FileDto;
import Shared.Dto.Music.MusicDto;
import Shared.Entities.CommentEntity;
import Shared.Entities.MusicEntity;
import java.sql.*;
import java.util.ArrayList;

public class MusicRepository {
    private Connection connection;

    public MusicRepository(DatabaseConfigDto config) {
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
            System.out.println("Connected to the PostgreSQL database in music repository.");
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public MusicEntity insertIntoTable(MusicDto dto) {
        MusicEntity musicEntity = new MusicEntity();
        String query = "INSERT INTO \"music\" (title, \"artistId\", \"genreId\", \"albumId\", duration, \"releaseDate\", popularity, lyric, \"trackFile\") VALUES (?, ?, ? , ?, ?, ?, ?, ?, ?) RETURNING id;";
        try {
            PreparedStatement insertStatement = this.connection.prepareStatement(query);
            insertStatement.setString(1, dto.getTitle());
            insertStatement.setInt(2, dto.getArtistId());
            insertStatement.setInt(3, dto.getGenreId());
            insertStatement.setInt(4, dto.getAlbumId());
            insertStatement.setInt(5, dto.getDuration());
            insertStatement.setDate(6, Date.valueOf(dto.getReleaseDate()));
            insertStatement.setInt(7, dto.getPopularity());
            insertStatement.setString(8, dto.getLyric());
            insertStatement.setInt(9, dto.getFileId());
            insertStatement.execute();
            ResultSet rs = insertStatement.getResultSet();
            if (rs.next()) {
                musicEntity.setId(rs.getInt("id"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return musicEntity;
    }

    public MusicEntity findOne(int id){
        MusicEntity music = new MusicEntity();
        String query = "SELECT " +
                "\"music\".id AS \"musicId\", " +
                "\"music\".title AS \"musicTitle\", " +
                "duration, " +
                "\"music\".\"releaseDate\" AS \"musicReleaseDate\", " +
                "\"music\".popularity AS \"musicPopularity\", " +
                "lyric, " +
                "\"artist\".id AS \"artistId\", " +
                "\"artist\".name AS \"artistName\", " +
                "\"genre\".id AS \"genreId\", " +
                "\"genre\".name AS \"genreName\", " +
                "\"album\".id AS \"albumId\", " +
                "\"album\".title AS \"albumTitle\", " +
                "\"file\".id AS \"fileId\", " +
                "\"file\".\"memeType\" AS \"fileMemeType\" " +
                "FROM \"music\" " +
                "LEFT JOIN \"artist\" ON \"music\".\"artistId\" = \"artist\".id " +
                "LEFT JOIN \"genre\" ON \"music\".\"genreId\" = \"genre\".id " +
                "LEFT JOIN \"album\" ON \"music\".\"albumId\" = \"album\".id " +
                "LEFT JOIN \"file\" ON \"music\".\"trackFile\" = \"file\".id " +
                "WHERE \"music\".id = ?;";
        try {
            PreparedStatement selectStatement = this.connection.prepareStatement(query);
            selectStatement.setInt(1, id);
            ResultSet rs = selectStatement.executeQuery();
            ArtistEntity artist = new ArtistEntity();
            GenreEntity genre = new GenreEntity();
            AlbumEntity album = new AlbumEntity();
            FileDto file = new FileDto();
            if (rs.next()) {
                music.setId(rs.getInt("musicId"));
                music.setTitle(rs.getString("musicTitle"));
                music.setDuration(rs.getInt("duration"));
                music.setReleaseDate(rs.getDate("musicReleaseDate").toLocalDate());
                music.setPopularity(rs.getInt("musicPopularity"));
                music.setLyric(rs.getString("lyric"));
                artist.setId(rs.getInt("artistId"));
                artist.setName(rs.getString("artistName"));
                music.setArtist(artist);
                genre.setId(rs.getInt("genreId"));
                genre.setName(rs.getString("genreName"));
                music.setGenre(genre);
                album.setId(rs.getInt("albumId"));
                album.setTitle(rs.getString("albumTitle"));
                music.setAlbum(album);
                file.setId(rs.getInt("fileId"));
                file.setMemeType(rs.getString("fileMemeType"));
                music.setFile(file);
            }
            music.setComments(this.findComments(id));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return music;
    }

    private ArrayList<CommentEntity> findComments(int id){
        ArrayList<CommentEntity> comments = new ArrayList<>();
        String query = "SELECT * FROM \"musicComments\" WHERE \"musicId\" = ?;";
        try {
            PreparedStatement selectStatement = this.connection.prepareStatement(query);
            selectStatement.setInt(1, id);
            ResultSet rs = selectStatement.executeQuery();
            while (rs.next()){
                CommentEntity comment = new CommentEntity();
                comment.setComment(rs.getString("comment"));
                comment.setUserId(rs.getInt("userId"));
                comment.setMusicId(rs.getInt("musicId"));
                comments.add(comment);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return comments;
    }

    public boolean addComment(CommentEntity comment) {
        String query = "INSERT INTO \"musicComments\" (\"musicId\", \"userId\", comment) VALUES (?, ?, ?);";
        try {
            PreparedStatement insertStatement = this.connection.prepareStatement(query);
            insertStatement.setInt(1, comment.getMusicId());
            insertStatement.setInt(2, comment.getUserId());
            insertStatement.setString(3, comment.getComment());
            insertStatement.execute();
            return true;
        } catch (SQLException e){
            e.printStackTrace();
        }
        return false;
    }

    public ArrayList<MusicEntity> search(String str){
        ArrayList<MusicEntity> musics = new ArrayList<>();
        ArrayList<Integer> musicIds = new ArrayList<>();
        String query = "SELECT " +
                "\"music\".id AS \"musicId\", " +
                "\"music\".title AS \"musicTitle\"," +
                "\"artist\".name AS \"artistName\",  " +
                "\"music\".popularity AS \"musicPopularity\" " +
                "FROM \"music\" " +
                "LEFT JOIN \"artist\" ON \"music\".\"artistId\" = \"artist\".id " +
                "LEFT JOIN \"genre\" ON \"music\".\"genreId\" = \"genre\".id " +
                "LEFT JOIN \"album\" ON \"music\".\"albumId\" = \"album\".id " +
                "WHERE \"music\".title LIKE ? " +
                "OR \"artist\".name LIKE ? " +
                "OR \"album\".title LIKE ? " +
                "OR \"genre\".name LIKE ? " +
                "ORDER BY \"musicPopularity\" DESC;"; // it sorts from larger to smaller
        try {
            PreparedStatement selectStatement = this.connection.prepareStatement(query);
            selectStatement.setString(1, "%" + str + "%");
            selectStatement.setString(2, "%" + str + "%");
            selectStatement.setString(3, "%" + str + "%");
            selectStatement.setString(4, "%" + str + "%");
            ResultSet rs = selectStatement.executeQuery();
            while (rs.next()){
                int musicId = rs.getInt("musicId");
                if (musicIds.contains(musicId)) continue;
                musicIds.add(musicId);
                MusicEntity music = new MusicEntity();
                ArtistEntity artist = new ArtistEntity();
                music.setId(musicId);
                music.setTitle(rs.getString("musicTitle"));
                artist.setName(rs.getString("artistName"));
                music.setArtist(artist);
                music.setPopularity(rs.getInt("musicPopularity"));
                musics.add(music);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return musics;
    }

    public boolean likeMusic(int id){
        String query = "UPDATE \"music\" SET popularity = popularity + 1 WHERE id = ?;";
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

    public boolean dislikeMusic(int id){
        String query = "UPDATE \"music\" SET popularity = popularity - 1 WHERE id = ?;";
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

    public boolean doesMusicBelongsToPL(int playlistId, int musicId) {
        String query = "SELECT * FROM \"playlistTrack\" WHERE \"playlistId\" = ? AND \"musicID\" = ?;";
        try {
            PreparedStatement selectStatement = this.connection.prepareStatement(query);
            selectStatement.setInt(1, playlistId);
            selectStatement.setInt(2, musicId);
            selectStatement.execute();
            ResultSet rs = selectStatement.getResultSet();
            if (rs.next()){
                return true;
            }
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
