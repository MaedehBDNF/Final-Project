package Shared.Entities;

import java.time.LocalDate;

public class AlbumEntity {
    private int id;
    private String title = "";
    private ArtistEntity artist;
    private GenreEntity genre;
    private LocalDate releaseDate;
    private int popularity = 0;
    private FileEntity cover;

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

    public FileEntity getCover() {
        return cover;
    }

    public void setCover(FileEntity cover) {
        this.cover = cover;
    }

    @Override
    public String toString() {
        return "AlbumEntity{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", artist=" + artist +
                ", genre=" + genre +
                ", releaseDate=" + releaseDate +
                ", popularity=" + popularity +
                ", cover=" + cover +
                '}';
    }
}
