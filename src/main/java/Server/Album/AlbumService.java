package Server.Album;

import Server.Config.DatabaseConfigDto;
import Shared.Entities.AlbumEntity;
import Shared.Entities.MusicEntity;
import Shared.Dto.Album.CreatorAlbumDto;
import Shared.Dto.Album.LikeAlbumDto;
import Shared.Enums.Error;
import Shared.Enums.Title;
import Shared.Response;

import java.util.ArrayList;

public class AlbumService {
    private final AlbumRepository albumRepository;

    public AlbumService(DatabaseConfigDto config) {
        this.albumRepository = new AlbumRepository(config);
    }

    public AlbumEntity addAlbum(CreatorAlbumDto creatorAlbumDto){
        return this.albumRepository.insertIntoTable(creatorAlbumDto);
    }

    public Response findOne(int id){
        Response response = new Response();
        response.setTitle(Title.findOneAlbum);
        AlbumEntity albumEntity = this.findOneEntity(id);
        if (albumEntity.getId() == 0){
            response.setError(Error.notFound);
            return response;
        }
        response.setData(albumEntity);
        response.successful();
        return response;
    }

    public AlbumEntity findOneEntity(int id) {
        return this.albumRepository.findOne(id);
    }

    public ArrayList<MusicEntity> findAlbumSongs(int albumId) {
        return this.albumRepository.findAlbumSongs(albumId);
    }

    public ArrayList<AlbumEntity> search(String str) {
        return this.albumRepository.search(str);
    }

    public boolean likeAlbum(LikeAlbumDto likeAlbumDto) {
        return this.albumRepository.increasePopularity(likeAlbumDto.getAlbumId()) && this.albumRepository.addToUserLikedAlbums(likeAlbumDto);
    }

    public void close() {
        this.albumRepository.closeConnection();
    }
}
