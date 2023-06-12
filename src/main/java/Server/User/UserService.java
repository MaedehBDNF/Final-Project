package Server.User;

import Shared.Entities.AlbumEntity;
import Server.Config.DatabaseConfigDto;
import Server.Playlist.PlaylistService;
import Shared.Dto.User.*;
import Shared.Entities.UserEntity;
import Shared.Enums.Error;
import Shared.Enums.Title;
import Shared.Response;
import org.mindrot.jbcrypt.BCrypt;

import java.util.ArrayList;

public class UserService {
    private final UserRepository userRepository;
    private final PlaylistService playlistService;
    public UserService(DatabaseConfigDto config){
        this.userRepository = new UserRepository(config);
        this.playlistService = new PlaylistService(config);
    }

    public Response register(RegisterDto registerDto){
        UserEntity userEntity = new UserEntity();
        Response response = new Response();
        response.setTitle(Title.register);
        // check existence
        if (this.findByUsername(registerDto.getUsername()).getId() != 0){
            response.setError(Error.duplicateDataError);
            return response;
        }
        // add to user table if exist
        userEntity.setUsername(registerDto.getUsername());
        userEntity.setPassword(BCrypt.hashpw(registerDto.getPassword(), BCrypt.gensalt()));
        userEntity.setEmail(registerDto.getEmail());
        int userId = this.userRepository.insertIntoTable(userEntity).getId();
        if (userId == 0) {
            response.setError(Error.databaseError);
            return response;
        }
        // add liked music playlist ID to table
        this.playlistService.createLikedMusicPlaylist(userId);

        response.successful();
        userEntity.setPassword(null);
        response.setData(userEntity);
        return response;
    }

    public Response login(LoginDto loginDto){
        Response response = new Response();
        response.setTitle(Title.login);
        UserEntity foundUser = this.findByUsername(loginDto.getUsername());
        if (foundUser.getId() != 0) {
            if (BCrypt.checkpw(loginDto.getPassword(), foundUser.getPassword())){
                response.successful();
                foundUser.setPassword(null);
                response.setData(foundUser);
            } else {
                response.setError(Error.wrongPassword);
            }
        } else {
            response.setError(Error.wrongUsername);
        }
        return response;
    }

    public Response logOut(){
        Response response = new Response();
        response.setTitle(Title.logOut);
        response.successful();
        return response;
    }

    public boolean followUser(int userId, int friendId) {
        return this.userRepository.followUser(userId, friendId);
    }

    public boolean followArtist(int userId, int artistId) {
        return this.userRepository.followArtist(userId, artistId);
    }

    public Response getUserFollowings(int userId) {
        Response response = new Response();
        response.setTitle(Title.getUserFollowings);
        response.setData(this.userRepository.getUserFollowings(userId));
        response.successful();
        return response;
    }

    public Response getUserFriends(int userId) {
        Response response = new Response();
        response.setTitle(Title.getUserFriends);
        response.setData(this.userRepository.getUserFriends(userId));
        response.successful();
        return response;
    }

    public UserEntity findByUsername(String username){
        return this.userRepository.findByUsername(username);
    }

    public UserEntity findOneEntity(int id){
        return this.userRepository.findOne(id);
    }

    public boolean updateUserProfilePicture(int userId, int profilePicId) {
        return this.userRepository.updateUserProfilePicture(userId, profilePicId);
    }

    public Response findOne(int id){
        Response response = new Response();
        response.setTitle(Title.findOneUser);
        UserEntity userEntity = this.userRepository.findOne(id);
        if (userEntity.getId() != 0) {
            response.setData(userEntity);
            response.successful();
        } else {
            response.setError(Error.notFound);
        }
        return response;
    }

    public ArrayList<AlbumEntity> findUserLikedAlbums(int userId) {
        return this.userRepository.findUserLikedAlbums(userId);
    }

    public ArrayList<UserEntity> searchUser(String username) {
        return this.userRepository.searchUser(username);
    }

    public void close(){
        this.userRepository.close();
        this.playlistService.close();
    }
}
