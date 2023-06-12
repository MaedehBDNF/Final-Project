package Shared.Entities;

import Shared.Dto.File.FileDto;
import java.util.ArrayList;

public class UserEntity {
    private int id;
    private String username = "";
    private String password = "";
    private String email = "";
    private FileDto profilePicture = null;
    private ArrayList<UserEntity> friends;
    private ArrayList<ArtistEntity> followings;
    private ArrayList<PlaylistEntity> playlists;
    private ArrayList<AlbumEntity> albums;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public FileDto getProfilePicture() {
        return profilePicture;
    }

    public void setProfilePicture(FileDto profilePicture) {
        this.profilePicture = profilePicture;
    }


    public ArrayList<UserEntity> getFriends() {
        return friends;
    }

    public void setFriends(ArrayList<UserEntity> friends) {
        this.friends = friends;
    }

    public ArrayList<ArtistEntity> getFollowings() {
        return followings;
    }

    public void setFollowings(ArrayList<ArtistEntity> followings) {
        this.followings = followings;
    }

    public ArrayList<PlaylistEntity> getPlaylists() {
        return playlists;
    }

    public void setPlaylists(ArrayList<PlaylistEntity> playlists) {
        this.playlists = playlists;
    }

    public ArrayList<AlbumEntity> getAlbums() {
        return albums;
    }

    public void setAlbums(ArrayList<AlbumEntity> albums) {
        this.albums = albums;
    }

    @Override
    public String toString() {
        return "UserEntity{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", email='" + email + '\'' +
                ", profilePicture=" + profilePicture +
                ", friends=" + friends +
                ", followings=" + followings +
                ", playlists=" + playlists +
                ", albums=" + albums +
                '}';
    }
}
