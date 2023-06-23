package Shared.Entities;

import Shared.Dto.File.FileDto;
import java.util.ArrayList;

public class PlaylistEntity {
    private int id;
    private String title;
    private UserEntity creator;
    private String description;
    private int popularity = 0;
    private boolean privatePL = false;
    private FileDto cover;
    private boolean isLock = false;
    private ArrayList<MusicPlaylistEntity> tracks;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public UserEntity getCreator() {
        return creator;
    }

    public void setCreator(UserEntity creator) {
        this.creator = creator;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getPopularity() {
        return popularity;
    }

    public void setPopularity(int popularity) {
        this.popularity = popularity;
    }

    public boolean isPrivatePL() {
        return privatePL;
    }

    public void setPrivatePL(boolean privatePL) {
        this.privatePL = privatePL;
    }

    public FileDto getCover() {
        return cover;
    }

    public void setCover(FileDto cover) {
        this.cover = cover;
    }

    public boolean isLock() {
        return isLock;
    }

    public void setLock(boolean lock) {
        isLock = lock;
    }

    public ArrayList<MusicPlaylistEntity> getTracks() {
        return tracks;
    }

    public void setTracks(ArrayList<MusicPlaylistEntity> tracks) {
        this.tracks = tracks;
    }

    @Override
    public String toString() {
        return "PlaylistEntity{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", creator=" + creator +
                ", description='" + description + '\'' +
                ", popularity=" + popularity +
                ", privatePL=" + privatePL +
                ", cover=" + cover +
                ", isLock=" + isLock +
                ", tracks=" + tracks +
                '}';
    }
}
