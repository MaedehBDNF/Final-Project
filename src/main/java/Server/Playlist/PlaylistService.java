package Server.Playlist;

import Server.Config.DatabaseConfigDto;
import Shared.Dto.Playlist.*;
import Shared.Entities.PlaylistEntity;
import Shared.Enums.Error;
import Shared.Enums.Title;
import Shared.Response;
import java.util.ArrayList;

public class PlaylistService {
    private final PlaylistRepository playlistRepository;

    public PlaylistService(DatabaseConfigDto config) {
        this.playlistRepository = new PlaylistRepository(config);
    }

    public PlaylistEntity createNewPlaylist(CreatePlaylistDto createPlaylistDto){
        PlaylistEntity playlistEntity = this.playlistRepository.insertIntoTable(createPlaylistDto, false);
        AddPlaylistDto addPlaylistDto = new AddPlaylistDto();
        addPlaylistDto.setId(playlistEntity.getId());
        addPlaylistDto.setUserId(createPlaylistDto.getCreatorId());
        this.addToUserPlaylists(addPlaylistDto);
        return playlistEntity;
    }
    public PlaylistEntity insertLikedMusicsPlaylist(CreatePlaylistDto createPlaylistDto){
        return this.playlistRepository.insertIntoTable(createPlaylistDto, true);
    }

    public Response findOne(int userId, int id){
        Response response = new Response();
        response.setTitle(Title.findOnePlaylist);
        PlaylistEntity playlist = this.findOneEntity(id);
        if (playlist.getId() == 0){
            response.setError(Error.notFound);
            return response;
        }
        if (playlist.isPrivatePL()) {
            if (playlist.getCreator().getId() == userId) {
                response.setData(playlist);
                response.successful();
                return response;
            } else {
                response.setError(Error.forbidden);
                return response;
            }
        } else {
            response.setData(playlist);
            response.successful();
        }
        return response;
    }
    public PlaylistEntity findOneEntity(int id) {
        return this.playlistRepository.findOne(id);
    }

    public ArrayList<PlaylistEntity> findAllUserPlaylists(int userId) {
        return this.playlistRepository.findAllUserPlaylists(userId);
    }

    public PlaylistEntity findUserPlaylists(int userId, int playlist) {
        return this.playlistRepository.findUserPlaylists(userId, playlist);
    }

    public ArrayList<PlaylistEntity> search(String str, int userId) {
        return this.playlistRepository.search(str, userId);
    }

    public Response addToUserPlaylists(AddPlaylistDto addPlaylistDto) {
        Response response = new Response();
        response.setTitle(Title.addPlaylist);
        PlaylistEntity playlist = this.findOneEntity(addPlaylistDto.getId());
        if (playlist.isPrivatePL()) {
            if (playlist.getCreator().getId() == addPlaylistDto.getUserId()) {
                PlaylistEntity playlistEntity = this.findUserPlaylists(addPlaylistDto.getUserId(), addPlaylistDto.getId());
                if (playlistEntity.getId() != 0) {
                    response.setError(Error.duplicateDataError);
                    return response;
                }
                this.playlistRepository.addToUserPlaylists(addPlaylistDto);
                response.setData(playlist);
                response.successful();
                return response;
            } else {
                response.setError(Error.forbidden);
                return response;
            }
        } else {
            this.playlistRepository.addToUserPlaylists(addPlaylistDto);
            response.successful();
            return response;
        }
    }

    public boolean likePlaylist(AddPlaylistDto addPlaylistDto) {
        PlaylistEntity playlistEntity = this.findUserPlaylists(addPlaylistDto.getUserId(), addPlaylistDto.getId());
        boolean result = true;
        if (playlistEntity.getId() == 0) {
            result = this.playlistRepository.addToUserPlaylists(addPlaylistDto);
        }
        return result && this.playlistRepository.increasePopularity(addPlaylistDto.getId());
    }

    private double makeTurn(int playlistId) {
        double turn = this.playlistRepository.findLastRowTurn(playlistId);
        turn = Math.ceil(turn) + 1;
        return turn;
    }

    public PlaylistEntity addMusic(AddMusicToPlaylistDto addMusicToPlaylistDto, int userId, boolean isLock) {
        PlaylistEntity pl = this.findOneEntity(addMusicToPlaylistDto.getId());
        if (pl.getCreator().getId() != userId || (pl.isLock() && !isLock)) {
            return new PlaylistEntity();
        }
        if (this.playlistRepository.findMusicPlaylist(addMusicToPlaylistDto.getId(), addMusicToPlaylistDto.getMusicId())) {
            return new PlaylistEntity();
        }
        double turn = this.makeTurn(addMusicToPlaylistDto.getId());
        return this.playlistRepository.addMusic(addMusicToPlaylistDto, turn);
    }

    public boolean removeMusic(RemoveMusicFromPlaylistDto removeMusicDto, int userId, boolean isLock) {
        PlaylistEntity pl = this.findOneEntity(removeMusicDto.getId());
        if (pl.getCreator().getId() != userId || (pl.isLock() && !isLock)) {
            return false;
        }
        return this.playlistRepository.removeMusic(removeMusicDto);
    }

    public boolean updateMusicTurn(UpdateMusicTurnDto updateMusicTurnDto) {
        return this.playlistRepository.updateMusicTurn(updateMusicTurnDto);
    }

    public int createLikedMusicPlaylist(int userId) {
        CreatePlaylistDto createPlaylistDto = new CreatePlaylistDto();
        createPlaylistDto.setTitle("Liked Musics");
        createPlaylistDto.setCreatorId(userId);
        createPlaylistDto.setDescription("This playList has made for liked musics.");
        createPlaylistDto.setPrivatePL(true);
        int playlistId = this.insertLikedMusicsPlaylist(createPlaylistDto).getId();
        AddPlaylistDto addPlaylistDto = new AddPlaylistDto();
        addPlaylistDto.setId(playlistId);
        addPlaylistDto.setUserId(userId);
        this.addToUserPlaylists(addPlaylistDto);
        return playlistId;
    }

    public boolean updatePlayerCover(int playlistId, int coverId) {
        return this.playlistRepository.updatePlayerCover(playlistId, coverId);
    }

    public int getLikedMusicsPlaylist(int userId) {
        return this.playlistRepository.getLikedMusicsPlaylist(userId);
    }

    public void close() {
        this.playlistRepository.closeConnection();
    }
}
