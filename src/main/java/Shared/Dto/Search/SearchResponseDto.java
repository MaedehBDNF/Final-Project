package Shared.Dto.Search;

import Server.Album.AlbumEntity;
import Server.Artist.ArtistEntity;
import Server.Music.MusicEntity;
import Server.Playlist.PlaylistEntity;
import Server.User.UserEntity;

import java.util.ArrayList;

public class SearchResponseDto {
    private ArrayList<UserEntity> users;
    private ArrayList<ArtistEntity> artists;
    private ArrayList<AlbumEntity> albums;
    private ArrayList<PlaylistEntity> playlists;
    private ArrayList<MusicEntity> musics;

    public ArrayList<UserEntity> getUsers() {
        return users;
    }

    public void setUsers(ArrayList<UserEntity> users) {
        this.users = users;
    }

    public ArrayList<ArtistEntity> getArtists() {
        return artists;
    }

    public void setArtists(ArrayList<ArtistEntity> artists) {
        this.artists = artists;
    }

    public ArrayList<AlbumEntity> getAlbums() {
        return albums;
    }

    public void setAlbums(ArrayList<AlbumEntity> albums) {
        this.albums = albums;
    }

    public ArrayList<PlaylistEntity> getPlaylists() {
        return playlists;
    }

    public void setPlaylists(ArrayList<PlaylistEntity> playlists) {
        this.playlists = playlists;
    }

    public ArrayList<MusicEntity> getMusics() {
        return musics;
    }

    public void setMusics(ArrayList<MusicEntity> musics) {
        this.musics = musics;
    }
}
