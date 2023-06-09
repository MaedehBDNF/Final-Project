package Shared.Dto.Playlist;

public class AddMusicToPlaylistDto {
    private int id;
    private int musicId;

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
}
