package Shared.Dto.Playlist;

public class ChangeMusicOrderInPlaylistDto {
    private int id;
    private int musicId;
    private double order;

    public int getId() {
        return id;
    }

    public void setId(int id) {

        this.id = id;
    }

    public int getMusicId() {
        return musicId;
    }

    public void setMusicId(int musicId) {

        this.musicId = musicId;
    }

    public double getOrder() {
        return order;
    }

    public void setOrder(double order) {
        this.order = order;
    }
}
