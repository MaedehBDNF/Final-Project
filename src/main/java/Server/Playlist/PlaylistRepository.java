package Server.Playlist;

import Server.Config.DatabaseConfigDto;
import Shared.Dto.Playlist.*;
import Shared.Entities.MusicPlaylistEntity;
import Shared.Entities.PlaylistEntity;
import Shared.Entities.UserEntity;
import Shared.Dto.File.FileDto;

import java.sql.*;
import java.util.ArrayList;

public class PlaylistRepository {
    private Connection connection;

    public PlaylistRepository(DatabaseConfigDto config) {
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
            System.out.println("Connected to the PostgreSQL database in playlist repository.");
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public PlaylistEntity insertIntoTable(CreatePlaylistDto createPlaylistDto, boolean isLock) {
        PlaylistEntity playlistEntity = new PlaylistEntity();
        PreparedStatement insertStatement;
        String query = "INSERT INTO \"playlist\" (title, \"creatorId\", description, popularity, \"isPrivate\", \"isLock\") VALUES (?, ?, ?, ?, ?, ?) RETURNING id;";
        try {
            insertStatement = this.connection.prepareStatement(query);
            insertStatement.setString(1, createPlaylistDto.getTitle());
            insertStatement.setInt(2, createPlaylistDto.getCreatorId());
            insertStatement.setString(3, createPlaylistDto.getDescription());
            insertStatement.setInt(4, createPlaylistDto.getPopularity());
            insertStatement.setBoolean(5, createPlaylistDto.isPrivatePL());
            insertStatement.setBoolean(6, isLock);
            insertStatement.execute();
            ResultSet rs = insertStatement.getResultSet();
            if (rs.next()){
                playlistEntity.setId(rs.getInt("id"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return playlistEntity;
    }

    public PlaylistEntity findOne(int id) {
        PlaylistEntity playlistEntity = new PlaylistEntity();
        String query = "SELECT " +
                "\"playlist\".id As \"playlistId\", " +
                "\"playlist\".title AS \"playlistTitle\", " +
                "\"creatorId\", " +
                "description, " +
                "\"playlist\".popularity AS \"playlistPopularity\", " +
                "\"isPrivate\", " +
                "\"isLock\", " +
                "\"playlistTrack\".\"musicId\", " +
                "\"music\".title AS \"musicTitle\", " +
                "\"music\".popularity AS \"musicPopularity\", " +
                "\"playlistTrack\".turn, " +
                "\"file\".id AS \"coverId\", " +
                "\"file\".name AS \"coverName\", " +
                "\"file\".\"memeType\" AS \"coverMemeType\" " +
                "FROM \"playlist\" " +
                "LEFT JOIN \"playlistTrack\" ON \"playlistTrack\".\"playlistId\" = \"playlist\".id " +
                "LEFT JOIN \"file\" ON \"playlist\".\"coverId\" = \"file\".id " +
                "LEFT JOIN \"music\" ON \"playlistTrack\".\"musicId\" = \"music\".id " +
                "WHERE \"playlist\".id = ? " +
                "ORDER BY \"playlistTrack\".turn ASC;";
        try {
            PreparedStatement selectStatement = this.connection.prepareStatement(query);
            selectStatement.setInt(1, id);
            ResultSet rs = selectStatement.executeQuery();
            UserEntity creator = new UserEntity();
            FileDto cover = new FileDto();
            ArrayList<MusicPlaylistEntity> tracks = new ArrayList<>();
            ArrayList<Integer> trackIds = new ArrayList<>();
            while (rs.next()){
                if (playlistEntity.getId() == 0){
                    playlistEntity.setId(rs.getInt("playlistId"));
                    playlistEntity.setTitle(rs.getString("playlistTitle"));
                    creator.setId(rs.getInt("creatorId"));
                    playlistEntity.setCreator(creator);
                    playlistEntity.setDescription(rs.getString("description"));
                    playlistEntity.setPopularity(rs.getInt("playlistPopularity"));
                    playlistEntity.setPrivatePL(rs.getBoolean("isPrivate"));
                    cover.setId(rs.getInt("coverId"));
                    cover.setName(rs.getString("coverName"));
                    cover.setMemeType(rs.getString("coverMemeType"));
                    playlistEntity.setLock(rs.getBoolean("isLock"));
                    playlistEntity.setCover(cover);
                }
                int trackId = rs.getInt("musicId");
                if (trackIds.contains(trackId) || trackId == 0) continue;
                trackIds.add(trackId);
                MusicPlaylistEntity track = new MusicPlaylistEntity();
                track.setId(rs.getInt("musicId"));
                track.setTitle(rs.getString("musicTitle"));
                track.setPopularity(rs.getInt("musicPopularity"));
                track.setTurn(rs.getDouble("turn"));
                tracks.add(track);
            }
            playlistEntity.setTracks(tracks);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return playlistEntity;
    }

    public ArrayList<PlaylistEntity> findAllUserPlaylists(int userId){
        ArrayList<PlaylistEntity> playlists = new ArrayList<>();
        ArrayList<Integer> playlistIds = new ArrayList<>();
        String query = "SELECT " +
                "\"playlist\".id, " +
                "\"playlist\".title, " +
                "\"playlist\".popularity " +
                "FROM \"userPlaylists\" " +
                "LEFT JOIN \"playlist\" ON \"userPlaylists\".\"playlistId\" = \"playlist\".id " +
                "WHERE \"userPlaylists\".\"userId\" = ?;";
        try {
            PreparedStatement selectStatement = this.connection.prepareStatement(query);
            selectStatement.setInt(1, userId);
            ResultSet rs = selectStatement.executeQuery();
            while (rs.next()){
                int playlistId = rs.getInt("id");
                if (playlistIds.contains(playlistId)) continue;
                playlistIds.add(playlistId);
                PlaylistEntity playlist = new PlaylistEntity();
                playlist.setId(rs.getInt("id"));
                playlist.setTitle(rs.getString("title"));
                playlist.setPopularity(rs.getInt("popularity"));
                playlists.add(playlist);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return playlists;
    }

    public ArrayList<PlaylistEntity> findUserPublicPlaylists(int userId) {
        ArrayList<PlaylistEntity> playlists = new ArrayList<>();
        String query = "SELECT " +
                "\"playlist\".id, " +
                "\"playlist\".title, " +
                "\"playlist\".popularity " +
                "FROM \"userPlaylists\" " +
                "LEFT JOIN \"playlist\" ON \"userPlaylists\".\"playlistId\" = \"playlist\".id " +
                "WHERE \"userPlaylists\".\"userId\" = ? AND NOT \"playlist\".\"isPrivate\";";
        try {
            PreparedStatement selectStatement = this.connection.prepareStatement(query);
            selectStatement.setInt(1, userId);
            ResultSet rs = selectStatement.executeQuery();
            while (rs.next()){
                PlaylistEntity playlist = new PlaylistEntity();
                playlist.setId(rs.getInt("id"));
                playlist.setTitle(rs.getString("title"));
                playlist.setPopularity(rs.getInt("popularity"));
                playlists.add(playlist);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return playlists;
    }

    public ArrayList<PlaylistEntity> search(String str, int creatorId) {
        ArrayList<PlaylistEntity> playlists = new ArrayList<>();
        ArrayList<Integer> playlistIds = new ArrayList<>();
        String query = "SELECT id, title, popularity FROM \"playlist\" WHERE title LIKE ? AND (NOT \"isPrivate\" OR \"creatorId\" = ?);";
        try {
            PreparedStatement selectStatement = this.connection.prepareStatement(query);
            selectStatement.setString(1, "%" + str + "%");
            selectStatement.setInt(2, creatorId);
            ResultSet rs = selectStatement.executeQuery();
            while (rs.next()){
                int playlistId = rs.getInt("id");
                if (playlistIds.contains(playlistId)) continue;
                playlistIds.add(playlistId);
                PlaylistEntity playlist = new PlaylistEntity();
                playlist.setId(playlistId);
                playlist.setTitle(rs.getString("title"));
                playlist.setPopularity(rs.getInt("popularity"));
                playlists.add(playlist);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return playlists;
    }

    public boolean addToUserPlaylists(AddPlaylistDto addPlaylistDto) {
        String query = "INSERT INTO \"userPlaylists\" (\"userId\", \"playlistId\") VALUES (?, ?);";
        try {
            PreparedStatement insertStatement = this.connection.prepareStatement(query);
            insertStatement.setInt(1, addPlaylistDto.getUserId());
            insertStatement.setInt(2, addPlaylistDto.getId());
            insertStatement.execute();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean increasePopularity(int id){
        String query = "UPDATE \"playlist\" SET popularity = popularity + 1 WHERE id = ?;";
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

    public double findLastRowTurn(int id) {
        String query = "SELECT * FROM \"playlistTrack\" WHERE \"playlistId\" = ? ORDER BY turn DESC LIMIT 1;";
        try {
            PreparedStatement selectStatement = this.connection.prepareStatement(query);
            selectStatement.setInt(1, id);
            selectStatement.execute();
            ResultSet rs = selectStatement.getResultSet();
            if (rs.next()){
                return rs.getDouble("turn");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public int getLikedMusicsPlaylist(int userId) {
        String query = "SELECT id FROM \"playlist\" WHERE \"creatorId\" = ?  AND \"isLock\"";
        try {
            PreparedStatement insertStatement = this.connection.prepareStatement(query);
            insertStatement.setInt(1, userId);
            ResultSet rs = insertStatement.executeQuery();
            if (rs.next()) {
                return rs.getInt("id");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    public PlaylistEntity addMusic(AddMusicToPlaylistDto addMusicToPlaylistDto, double turn) {
        PlaylistEntity playlist = new PlaylistEntity();
        String query = "INSERT INTO \"playlistTrack\" (\"playlistId\", \"musicId\", turn) VALUES (?, ?, ?);";
        try {
            PreparedStatement insertStatement = this.connection.prepareStatement(query);
            insertStatement.setInt(1, addMusicToPlaylistDto.getId());
            insertStatement.setInt(2, addMusicToPlaylistDto.getMusicId());
            insertStatement.setDouble(3, turn);
            insertStatement.execute();
            playlist.setId(addMusicToPlaylistDto.getId());
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return playlist;
    }

    public boolean hasPlaylistBeenLiked(int userId, int playlistId) {
        String query = "SELECT * FROM \"userPlaylists\" WHERE \"userId\" = ? AND \"playlistId\" = ? AND \"isLiked\";";
        try {
            PreparedStatement selectStatement = this.connection.prepareStatement(query);
            selectStatement.setInt(1, userId);
            selectStatement.setInt(2, playlistId);
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

    public boolean hasPlaylistBeenAdded(int userId, int playlistId) {
        String query = "SELECT * FROM \"userPlaylists\" WHERE \"userId\" = ? AND \"playlistId\" = ?;";
        try {
            PreparedStatement selectStatement = this.connection.prepareStatement(query);
            selectStatement.setInt(1, userId);
            selectStatement.setInt(2, playlistId);
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

    public boolean likePlaylist(int userId, int playlistId) {
        String query = "UPDATE \"userPlaylists\" SET \"isLiked\" = TRUE WHERE \"playlistId\" = ? AND \"userId\" = ?;";
        try {
            PreparedStatement updateStatement = this.connection.prepareStatement(query);
            updateStatement.setInt(1, playlistId);
            updateStatement.setInt(2, userId);
            updateStatement.execute();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }


    public boolean removeMusic(RemoveMusicFromPlaylistDto removeMusicDto) {
        String query = "DELETE FROM \"playlistTrack\" WHERE \"playlistId\" = ? AND \"musicId\" = ?;";
        try {
            PreparedStatement insertStatement = this.connection.prepareStatement(query);
            insertStatement.setInt(1, removeMusicDto.getId());
            insertStatement.setInt(2, removeMusicDto.getMusicId());
            insertStatement.execute();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean updateMusicTurn(UpdateMusicTurnDto updateMusicTurnDto) {
        String query = "UPDATE \"playlistTrack\" SET turn = ? WHERE \"playlistId\" = ? AND \"musicId\" = ?;";
        try {
            PreparedStatement updateStatement = this.connection.prepareStatement(query);
            updateStatement.setDouble(1, updateMusicTurnDto.getTurn());
            updateStatement.setInt(2, updateMusicTurnDto.getPlaylistId());
            updateStatement.setInt(3, updateMusicTurnDto.getMusicId());
            updateStatement.execute();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean updatePlayerCover(int playlistId, int coverId) {
        String query = "UPDATE \"playlist\" SET \"coverId\" = ? WHERE id = ?;";
        try {
            PreparedStatement updateStatement = this.connection.prepareStatement(query);
            updateStatement.setInt(1, coverId);
            updateStatement.setInt(2, playlistId);
            updateStatement.execute();
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
