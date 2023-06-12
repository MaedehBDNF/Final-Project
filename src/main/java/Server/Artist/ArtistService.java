package Server.Artist;

import Server.Config.DatabaseConfigDto;
import Shared.Dto.Artist.CreateArtistDto;
import Shared.Entities.ArtistEntity;
import Shared.Enums.Error;
import Shared.Enums.Title;
import Shared.Response;
import java.util.ArrayList;

public class ArtistService {
    private final ArtistRepository artistRepository;

    public ArtistService(DatabaseConfigDto config) {
        this.artistRepository = new ArtistRepository(config);
    }

    public ArtistEntity addArtist(CreateArtistDto createArtistDto) {
        return this.artistRepository.insertIntoTable(createArtistDto);
    }

    public Response findOne(int id){
        Response response = new Response();
        response.setTitle(Title.findOneArtist);
        ArtistEntity artist = this.findOneEntity(id);
        if (artist.getId() == 0) {
            response.setError(Error.notFound);
            return response;
        }
        response.setData(artist);
        response.successful();
        return response;
    }

    public ArtistEntity findOneEntity(int id){
        return this.artistRepository.findOne(id);
    }

    public ArrayList<ArtistEntity> search(String str){
        return this.artistRepository.search(str);
    }

    public Response findArtistAlbums(int id){
        Response response = new Response();
        response.setTitle(Title.findAlbumSongs);
        response.setData(this.artistRepository.findArtistAlbums(id));
        response.successful();
        return response;
    }

    public void close() {
        this.artistRepository.closeConnection();
    }
}
