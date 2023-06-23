package Shared.Dto.Playlist;

public class DoesUserLikedPlaylistDto {
    private int userId;
    private int playlistId;

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getPlaylistId() {
        return playlistId;
    }

    public void setPlaylistId(int playlistId) {
        this.playlistId = playlistId;
    }
}
