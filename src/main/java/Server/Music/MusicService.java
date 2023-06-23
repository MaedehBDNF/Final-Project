package Server.Music;

import Server.Config.DatabaseConfigDto;
import Shared.Dto.Music.MusicDto;
import Shared.Entities.CommentEntity;
import Shared.Entities.MusicEntity;
import Shared.Enums.Error;
import Shared.Enums.Status;
import Shared.Enums.Title;
import Shared.Response;
import java.util.ArrayList;

public class MusicService {
    private MusicRepository musicRepository;

    public MusicService(DatabaseConfigDto config) {
        this.musicRepository = new MusicRepository(config);
    }

    public MusicEntity addMusic(MusicDto music){
        return this.musicRepository.insertIntoTable(music);
    }

    public Response findOne(int id){
        Response response = new Response();
        response.setTitle(Title.findOneMusic);
        MusicEntity music = this.findOneEntity(id);
        if (music.getId() == 0) {
            response.setError(Error.notFound);
            return response;
        }
        response.setData(music);
        response.successful();
        return response;
    }

    public MusicEntity findOneEntity(int id){
        return this.musicRepository.findOne(id);
    }

    public ArrayList<MusicEntity> search(String str){
        return this.musicRepository.search(str);
    }

    public Response addComment(CommentEntity comment) {
        Response response = new Response();
        response.setTitle(Title.addCommentOnMusic);
        if (this.findOne(comment.getMusicId()).getStatus().equals(Status.failed)){
            response.setError(Error.notFound);
            return response;
        }
        if (this.musicRepository.addComment(comment)){
            response.successful();
        }
        return response;
    }

    public boolean doesMusicBelongsToPL(int playlistId, int musicId) {
        return this.musicRepository.doesMusicBelongsToPL(playlistId, musicId);
    }

    public Response doesUserLikedMusic(int playlistId, int musicId) {
        Response response = new Response();
        response.setTitle(Title.doesUserLikedMusic);
        response.setData(this.doesMusicBelongsToPL(playlistId, musicId));
        response.successful();
        return response;
    }

    public boolean likeMusic(int id){
        return this.musicRepository.likeMusic(id);
    }

    public boolean dislikeMusic(int id){
        return this.musicRepository.dislikeMusic(id);
    }

    public void close() {
        this.musicRepository.closeConnection();
    }
}
