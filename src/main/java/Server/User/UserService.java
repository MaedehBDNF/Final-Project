package Server.User;

import Server.Config.DatabaseConfigDto;
import Shared.Dto.User.*;
import Shared.Enums.Error;
import Shared.Enums.Title;
import Shared.Response;
import org.mindrot.jbcrypt.BCrypt;

import java.util.ArrayList;

public class UserService {
    private UserRepository userRepository;
    public UserService(DatabaseConfigDto config){
        this.userRepository = new UserRepository(config);
    }

    public Response register(RegisterDto registerDto){
        UserEntity userEntity = new UserEntity();
        Response response = new Response();
        response.setTitle(Title.register);

        if (this.findByUsername(registerDto.getUsername()).getId() != 0){
            response.setError(Error.duplicateUsername);
            return response;
        }

        userEntity.setUsername(registerDto.getUsername());
        userEntity.setPassword(BCrypt.hashpw(registerDto.getPassword(), BCrypt.gensalt()));
        userEntity.setEmail(registerDto.getEmail());
        if (this.userRepository.insertIntoTable(userEntity).getId() == 0) {
            response.setError(Error.databaseError);
            return response;
        }
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

    public ArrayList<UserEntity> searchUser(String username) {
        return this.userRepository.searchUser(username);
    }

    public void close(){
        this.userRepository.close();
    }
}
