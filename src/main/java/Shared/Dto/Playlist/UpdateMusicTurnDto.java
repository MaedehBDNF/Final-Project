package Shared.Dto.Playlist;

public class UpdateMusicTurnDto {
    private int playlistId;
    private int musicId;
    private double turn;

    public int getPlaylistId() {
        return playlistId;
    }

    public void setPlaylistId(int playlistId) {
        this.playlistId = playlistId;
    }

    public int getMusicId() {
        return musicId;
    }

    public void setMusicId(int musicId) {
        this.musicId = musicId;
    }

    public double getTurn() {
        return turn;
    }

    public void setTurn(double turn) {
        this.turn = turn;
    }
}
