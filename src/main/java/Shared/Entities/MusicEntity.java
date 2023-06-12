package Shared.Entities;

import Shared.Dto.File.FileDto;
import java.time.LocalDate;
import java.util.ArrayList;

public class MusicEntity {
    private int id;
    private String title;
    private ArtistEntity artist;
    private GenreEntity genre;
    private AlbumEntity album;
    private int duration = 0;
    private LocalDate releaseDate;
    private int popularity = 0;
    private String lyric;
    private FileDto file;
    private ArrayList<CommentEntity> comments;

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

    public ArtistEntity getArtist() {
        return artist;
    }

    public void setArtist(ArtistEntity artist) {
        this.artist = artist;
    }

    public GenreEntity getGenre() {
        return genre;
    }

    public void setGenre(GenreEntity genre) {
        this.genre = genre;
    }

    public AlbumEntity getAlbum() {
        return album;
    }

    public void setAlbum(AlbumEntity album) {
        this.album = album;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public LocalDate getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(LocalDate releaseDate) {
        this.releaseDate = releaseDate;
    }

    public int getPopularity() {
        return popularity;
    }

    public void setPopularity(int popularity) {
        this.popularity = popularity;
    }

    public String getLyric() {
        return lyric;
    }

    public void setLyric(String lyric) {
        this.lyric = lyric;
    }

    public FileDto getFile() {
        return file;
    }

    public void setFile(FileDto file) {
        this.file = file;
    }

    public ArrayList<CommentEntity> getComments() {
        return comments;
    }

    public void setComments(ArrayList<CommentEntity> comments) {
        this.comments = comments;
    }

    @Override
    public String toString() {
        return "MusicEntity{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", artist=" + artist +
                ", genre=" + genre +
                ", album=" + album +
                ", duration=" + duration +
                ", releaseDate=" + releaseDate +
                ", popularity=" + popularity +
                ", lyric='" + lyric + '\'' +
                ", file=" + file +
                ", comments=" + comments +
                '}';
    }
}
