package Shared.Dto.Playlist;

import Shared.Entities.MusicEntity;

import java.util.ArrayList;

public class CreatePlaylistDto {
    private String title;
    private int creatorId;
    private String description;
    private int popularity = 0;
    private boolean privatePL = false;
    private int coverId;
    private ArrayList<MusicEntity> tracks;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getCreatorId() {
        return creatorId;
    }

    public void setCreatorId(int creatorId) {
        this.creatorId = creatorId;
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

    public int getCoverId() {
        return coverId;
    }

    public void setCoverId(int coverId) {
        this.coverId = coverId;
    }

    public ArrayList<MusicEntity> getTracks() {
        return tracks;
    }

    public void setTracks(ArrayList<MusicEntity> tracks) {
        this.tracks = tracks;
    }
}
