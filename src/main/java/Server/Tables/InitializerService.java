package Server.Tables;

import Server.Album.AlbumService;
import Server.Artist.ArtistService;
import Server.Config.DatabaseConfigDto;
import Server.FileManager.FileService;
import Server.Genre.GenreEntity;
import Server.Genre.GenreService;
import Server.Music.MusicService;
import Server.Playlist.PlaylistService;
import Server.User.UserService;

import java.util.ArrayList;

public class InitializerService {
    private UserService userService;
    private AlbumService albumService;
    private ArtistService artistService;
    private PlaylistService playlistService;
    private MusicService musicService;
    private GenreService genreService;
    private FileService fileService;
    private ArrayList<GenreEntity> genres = new ArrayList<>();

    public InitializerService(DatabaseConfigDto config) {
        new InitializerRepository(config);
        this.userService = new UserService(config);
        this.albumService = new AlbumService(config);
        this.artistService = new ArtistService(config);
        this.playlistService = new PlaylistService(config);
        this.musicService = new MusicService(config);
        this.genreService = new GenreService(config);
        this.fileService = new FileService(config);
        seedData();
        close();
    }

    private void seedData() {
        this.seedGenres();
    }

    private void close() {
        System.out.println("Closing initializer service.");
        this.userService.close();
        this.albumService.close();
        this.artistService.close();
        this.playlistService.close();
        this.musicService.close();
        this.genreService.close();
        this.fileService.lightClose();
    }
    
    private void seedGenres() {
        GenreEntity genreEntity = new GenreEntity();
        genreEntity.setName("Rap");
        genreEntity.setDescription("Cool");
        this.genres.add(this.genreService.addGenre(genreEntity));
    }
}
