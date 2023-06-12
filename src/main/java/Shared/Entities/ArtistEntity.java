package Shared.Entities;

import Shared.Dto.File.FileDto;
import java.util.ArrayList;

public class ArtistEntity {
    private int id;
    private String name;
    private GenreEntity genre;
    private String biography;
    private FileDto profilePicture;
    private ArrayList<String> socialMediaLinks;
    private ArrayList<MusicEntity> tracks;
    private ArrayList<AlbumEntity> albums;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public GenreEntity getGenre() {
        return genre;
    }

    public void setGenre(GenreEntity genre) {
        this.genre = genre;
    }

    public String getBiography() {
        return biography;
    }

    public void setBiography(String biography) {
        this.biography = biography;
    }

    public FileDto getProfilePicture() {
        return profilePicture;
    }

    public void setProfilePicture(FileDto profilePicture) {
        this.profilePicture = profilePicture;
    }

    public ArrayList<String> getSocialMediaLinks() {
        return socialMediaLinks;
    }

    public void setSocialMediaLinks(ArrayList<String> socialMediaLinks) {
        this.socialMediaLinks = socialMediaLinks;
    }

    public ArrayList<MusicEntity> getTracks() {
        return tracks;
    }

    public void setTracks(ArrayList<MusicEntity> tracks) {
        this.tracks = tracks;
    }

    public ArrayList<AlbumEntity> getAlbums() {
        return albums;
    }

    public void setAlbums(ArrayList<AlbumEntity> albums) {
        this.albums = albums;
    }

    @Override
    public String toString() {
        return "ArtistEntity{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", genre=" + genre +
                ", biography='" + biography + '\'' +
                ", profilePicture=" + profilePicture +
                ", socialMediaLinks=" + socialMediaLinks +
                ", tracks=" + tracks +
                ", albums=" + albums +
                '}';
    }
}
