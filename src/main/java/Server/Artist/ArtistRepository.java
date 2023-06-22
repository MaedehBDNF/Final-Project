package Server.Artist;

import Shared.Entities.AlbumEntity;
import Server.Config.DatabaseConfigDto;
import Shared.Entities.ArtistEntity;
import Shared.Entities.GenreEntity;
import Shared.Entities.MusicEntity;
import Shared.Dto.Artist.CreateArtistDto;
import Shared.Dto.File.FileDto;

import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;

public class ArtistRepository {
    private Connection connection;

    public ArtistRepository(DatabaseConfigDto config) {
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
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public ArtistEntity insertIntoTable(CreateArtistDto createArtistDto) {
        ArtistEntity artist = new ArtistEntity();
        String query = "INSERT INTO \"artist\" (name, \"genreId\", biography, \"profilePictureId\", \"socialLinks\") Values (?, ?, ?, ?, ?) RETURNING id;";
        try {
            PreparedStatement insertStatement = this.connection.prepareStatement(query);
            insertStatement.setString(1, createArtistDto.getName());
            insertStatement.setInt(2, createArtistDto.getGenreId());
            insertStatement.setString(3, createArtistDto.getBiography());
            insertStatement.setInt(4, createArtistDto.getProfilePictureId());
            Array array = this.connection.createArrayOf("VARCHAR", createArtistDto.getSocialMediaLinks().toArray());
            insertStatement.setArray(5, array);
            insertStatement.execute();
            ResultSet rs = insertStatement.getResultSet();
            if (rs.next()){
                artist.setId(rs.getInt("id"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return artist;
    }

    public ArtistEntity findOne(int id) {
        ArtistEntity artist = new ArtistEntity();
        String query = "SELECT " +
                "\"artist\".id AS\"artistId\", " +
                "\"artist\".name AS \"artistName\", " +
                "biography, " +
                "\"socialLinks\", " +
                "\"genre\".id AS \"genreId\", " +
                "\"genre\".name AS \"genreName\", " +
                "\"music\".id AS \"musicId\", " +
                "\"music\".title AS \"musicTitle\", " +
                "\"music\".popularity AS \"musicPopularity\", " +
                "\"album\".id AS \"albumId\", " +
                "\"album\".title AS \"albumTitle\", " +
                "\"album\".popularity AS \"albumPopularity\", " +
                "\"file\".id AS \"profilePictureId\"," +
                "\"file\".name AS \"fileName\", " +
                "\"file\".\"memeType\" AS \"fileMemeType\" " +
                "FROM \"artist\" " +
                "LEFT JOIN \"genre\" ON \"artist\".\"genreId\" = \"genre\".id " +
                "LEFT JOIN \"music\" ON \"artist\".id = \"music\".\"artistId\" " +
                "LEFT JOIN \"album\" ON \"artist\".id = \"album\".\"artistId\" " +
                "LEFT JOIN \"file\" ON \"artist\".\"profilePictureId\" = \"file\".id " +
                "WHERE \"artist\".id = ?;";
        try {
            PreparedStatement selectStatement = this.connection.prepareStatement(query);
            selectStatement.setInt(1, id);
            ResultSet rs = selectStatement.executeQuery();
            GenreEntity genre = new GenreEntity();
            FileDto profilePicture = new FileDto();
            while (rs.next()) {
                if (artist.getId() == 0){
                    artist.setId(rs.getInt("artistId"));
                    artist.setName(rs.getString("artistName"));
                    artist.setBiography(rs.getString("biography"));
                    String[] stringArray = (String[]) rs.getArray("socialLinks").getArray(); // convert sql Array to java Array
                    ArrayList<String> links = new ArrayList<>(Arrays.asList(stringArray)); // convert java Array to ArrayList
                    artist.setSocialMediaLinks(links);
                }

                if (artist.getGenre() == null){
                    genre.setId(rs.getInt("genreId"));
                    genre.setName(rs.getString("genreName"));
                    artist.setGenre(genre);
                }

                if (artist.getProfilePicture() == null){
                    profilePicture.setId(rs.getInt("profilePictureId"));
                    profilePicture.setName(rs.getString("fileName"));
                    profilePicture.setMemeType(rs.getString("fileMemeType"));
                    artist.setProfilePicture(profilePicture);
                }

                if (rs.getInt("musicId") != 0){
                    MusicEntity track = new MusicEntity();
                    track.setId(rs.getInt("musicId"));
                    track.setTitle(rs.getString("musicTitle"));
                    track.setPopularity(rs.getInt("musicPopularity"));
                    ArrayList<MusicEntity> tracks = artist.getTracks();
                    tracks.add(track);
                    artist.setTracks(tracks);
                }

                if (rs.getInt("albumId") != 0){
                    AlbumEntity album = new AlbumEntity();
                    album.setId(rs.getInt("albumId"));
                    album.setTitle(rs.getString("albumTitle"));
                    album.setPopularity(rs.getInt("albumPopularity"));
                    ArrayList<AlbumEntity> albums = artist.getAlbums();
                    albums.add(album);
                    artist.setAlbums(albums);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return artist;
    }

    public ArrayList<ArtistEntity> search(String str) {
        ArrayList<ArtistEntity> artists = new ArrayList<>();
        String query = "SELECT " +
                "\"artist\".id AS \"artistId\", " +
                "\"artist\".name AS \"artistName\", " +
                "\"genre\".id AS \"genreId\", " +
                "\"genre\".name AS \"genreName\" " +
                "FROM \"artist\" " +
                "LEFT JOIN \"genre\" ON \"artist\".\"genreId\" = \"genre\".id " +
                "WHERE \"artist\".name LIKE ? " +
                "OR \"genre\".name LIKE ?;";
        try {
            PreparedStatement selectStatement = this.connection.prepareStatement(query);
            selectStatement.setString(1, "%" + str + "%");
            selectStatement.setString(2, "%" + str + "%");
            ResultSet rs = selectStatement.executeQuery();
            while (rs.next()) {
                ArtistEntity artist = new ArtistEntity();
                GenreEntity genre = new GenreEntity();
                artist.setId(rs.getInt("artistId"));
                artist.setName(rs.getString("artistName"));
                genre.setId(rs.getInt("genreId"));
                genre.setName(rs.getString("genreName"));
                artist.setGenre(genre);
                artists.add(artist);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return artists;
    }

    public ArrayList<AlbumEntity> findArtistAlbums(int id){
        ArrayList<AlbumEntity> albums = new ArrayList<>();
        ArrayList<Integer> artistIds = new ArrayList<>();
        String query = "SELECT " +
                "\"album\".id AS \"albumId\", " +
                "\"album\".title AS \"albumTitle\"," +
                "\"artist\".name AS \"artistName\",  " +
                "\"album\".popularity AS \"albumPopularity\" " +
                "FROM \"album\" " +
                "LEFT JOIN \"artist\" ON \"album\".\"artistId\" = \"artist\".id " +
                "WHERE \"album\".id = ? " +
                "ORDER BY popularity DESC;";
        try {
            PreparedStatement selectStatement = this.connection.prepareStatement(query);
            selectStatement.setInt(1, id);
            ResultSet rs = selectStatement.executeQuery();
            ArtistEntity artist = new ArtistEntity();
            while (rs.next()){
                int artistId = rs.getInt("albumId");
                if (artistIds.contains(artistId)) continue;
                artistIds.add(artistId);
                AlbumEntity album = new AlbumEntity();
                album.setId(artistId);
                album.setTitle(rs.getString("albumTitle"));
                artist.setId(id);
                artist.setName(rs.getString("artistName"));
                album.setArtist(artist);
                album.setPopularity(rs.getInt("popularity"));
                albums.add(album);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return albums;
    }

    public void closeConnection() {
        try {
            this.connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
