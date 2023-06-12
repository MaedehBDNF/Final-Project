package Server.Manager;

import Server.Album.AlbumService;
import Server.Artist.ArtistService;
import Server.Config.DatabaseConfigDto;
import Server.FileManager.FileService;
import Server.Genre.GenreService;
import Shared.Entities.CommentEntity;
import Server.Music.MusicService;
import Shared.Entities.PlaylistEntity;
import Server.Playlist.PlaylistService;
import Shared.Entities.UserEntity;
import Server.User.UserService;
import Shared.Cryptography.AESEncryption;
import Shared.Cryptography.RSAEncryption;
import Shared.Dto.File.*;
import Shared.Dto.Genre.FindOneGenreDto;
import Shared.Dto.Music.FindOneMusicDto;
import Shared.Dto.Music.LikeMusicDto;
import Shared.Dto.Music.SearchMusicDto;
import Shared.Dto.Search.SearchRequestDto;
import Shared.Dto.Search.SearchResponseDto;
import Shared.Dto.User.*;
import Shared.Enums.Error;
import Shared.Enums.Status;
import Shared.Enums.Title;
import Shared.Request;
import Shared.Response;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.io.*;
import java.net.Socket;

public class Manager implements Runnable {
    private int currentUserId = -1;
    private Socket socket;
    private InputStream inputStream;
    private DataInputStream dataInputStream;
    private OutputStream outputStream;
    private DataOutputStream dataOutputStream;
    private ObjectMapper mapper = new ObjectMapper();

    private boolean doesExit = false;
    private boolean needResponse = true;

    private UserService userService;
    private AlbumService albumService;
    private ArtistService artistService;
    private PlaylistService playlistService;
    private MusicService musicService;
    private GenreService genreService;
    private FileService fileService;

    private RSAEncryption encryptor;
    private String AES_SECRET = "";
    private AESEncryption aesEncryption;

    public Manager(Socket socket, DatabaseConfigDto config) {
        this.userService = new UserService(config);
        this.albumService = new AlbumService(config);
        this.artistService = new ArtistService(config);
        this.playlistService = new PlaylistService(config);
        this.musicService = new MusicService(config);
        this.genreService = new GenreService(config);
        this.fileService = new FileService(config, socket);
        this.encryptor =  new RSAEncryption();

        this.socket = socket;
        try {
            this.inputStream = socket.getInputStream();
            this.dataInputStream = new DataInputStream(inputStream);

            this.outputStream = socket.getOutputStream();
            this.dataOutputStream = new DataOutputStream(outputStream);

            this.sendRSAPublicKey();
            this.receiveAESSecret();
            this.aesEncryption = new AESEncryption(AES_SECRET);

            this.mapper.registerModule(new JavaTimeModule()); // for existence of LocalDate in parsing to JSON
        } catch (IOException e){
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        try {
            while (true) {
                String requestCipherText = this.dataInputStream.readUTF();
                String command = this.aesEncryption.decrypt(requestCipherText);
                System.out.println(command);
                Request clientRequest = this.mapper.readValue(command, Request.class);
                Response response = this.manage(clientRequest);
                if (this.doesExit) {
                    break;
                }
                if (!this.needResponse) {
                    this.needResponse = true;
                    continue;
                }
                String responseCommand = this.mapper.writeValueAsString(response);
                System.out.println(responseCommand);
                String responseCipherText = this.aesEncryption.encrypt(responseCommand);
                this.dataOutputStream.writeUTF(responseCipherText);
                this.dataOutputStream.flush();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            closeThread();
        }
    }

    private void sendRSAPublicKey(){
        try {
            // Send the server public key to the client
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(this.outputStream);
            objectOutputStream.writeObject(this.encryptor.getPublicKey());
            System.out.println("Server public-Key sent.");
        } catch (IOException e){
            e.printStackTrace();
        }
    }

    private void receiveAESSecret(){
        try {
            String cipherSecretKey = dataInputStream.readUTF();
            AES_SECRET = encryptor.decrypt(cipherSecretKey);
            System.out.println("AES Secret received!");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void closeThread() {
        try {
            System.out.println("Closing repositories connections and client socket.");
            this.userService.close();
            this.albumService.close();
            this.artistService.close();
            this.playlistService.close();
            this.musicService.close();
            this.genreService.close();
            this.fileService.hardClose();
            this.dataInputStream.close();
            this.dataOutputStream.close();
            this.inputStream.close();
            this.outputStream.close();
            this.socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Response manage(Request request) {
        DownloadDto downloadDto;
        switch (request.getTitle()) {
            // User Management
            case register:
                RegisterDto registerDto = this.mapper.convertValue(request.getData(), RegisterDto.class);
                return this.register(registerDto);
            case login:
                LoginDto loginDto = this.mapper.convertValue(request.getData(), LoginDto.class);
                return this.login(loginDto);
            case findOneUser:
                FindOneUserDto findOneUserDto = this.mapper.convertValue(request.getData(), FindOneUserDto.class);
                return this.userService.findOne(findOneUserDto.getId());
            case searchUser:
                SearchUserDto searchUserDto = this.mapper.convertValue(request.getData(), SearchUserDto.class);
                return this.searchUser(searchUserDto.getUsername());
            case followUser:
                FollowUserDto followUserDto = this.mapper.convertValue(request.getData(), FollowUserDto.class);
                return this.followUser(request.getUserId(), followUserDto);
            case followArtist:
                FollowArtistDto followArtistDto = this.mapper.convertValue(request.getData(), FollowArtistDto.class);
                return this.followArtist(request.getUserId(), followArtistDto);
            case logOut:
                if (request.getUserId() != this.currentUserId) break;
                return this.logout();

            // File Management
            case upload:
                if (request.getUserId() != this.currentUserId) break;
                UploadDto uploadDto = this.mapper.convertValue(request.getData(), UploadDto.class);
                return this.uploadFile(uploadDto);
            case getFileInfo:
                if (request.getUserId() != this.currentUserId) break;
                downloadDto = this.mapper.convertValue(request.getData(), DownloadDto.class);
                return this.fileService.getFileInfo(downloadDto);
            case download:
                if (request.getUserId() != this.currentUserId) break;
                downloadDto = this.mapper.convertValue(request.getData(), DownloadDto.class);
                this.fileService.download(downloadDto);
                this.needResponse = false;
                break;

            // Genre Manager
            case findOneGenre:
                if (request.getUserId() != this.currentUserId) break;
                FindOneGenreDto findOneGenreDto = this.mapper.convertValue(request.getData(), FindOneGenreDto.class);
                return this.genreService.findOneGenre(findOneGenreDto.getId());
            case findAllGenres:
                if (request.getUserId() != this.currentUserId) break;
                return this.genreService.findAll();

            // Music Manager
            case findOneMusic:
                if (request.getUserId() != this.currentUserId) break;
                FindOneMusicDto findOneMusicDto = this.mapper.convertValue(request.getData(), FindOneMusicDto.class);
                return this.musicService.findOne(findOneMusicDto.getId());
            case likeMusic:
                if (request.getUserId() != this.currentUserId) break;
                LikeMusicDto likeMusicDto = this.mapper.convertValue(request.getData(), LikeMusicDto.class);
                return this.likeMusic(request.getUserId(), likeMusicDto.getId());
            case addCommentOnMusic:
                if (request.getUserId() != this.currentUserId) break;
                CommentEntity comment = this.mapper.convertValue(request.getData(), CommentEntity.class);
                this.musicService.addComment(comment);
                this.needResponse = false;
                break;
            case searchMusic:
                if (request.getUserId() != this.currentUserId) break;
                SearchMusicDto searchMusicDto = this.mapper.convertValue(request.getData(), SearchMusicDto.class);
                return this.searchMusic(searchMusicDto.getValue());

            // All
            case completeSearch:
                if (request.getUserId() != this.currentUserId) break;
                SearchRequestDto searchDto = this.mapper.convertValue(request.getData(), SearchRequestDto.class);
                return this.search(searchDto.getValue());

            // Exit and close client sockets
            case exit:
                this.doesExit = true;
                break;
        }
        Response response = new Response();
        response.setTitle(request.getTitle());
        response.setStatus(Status.failed);
        response.setError(Error.forbidden);
        return response;
    }

    private Response register(RegisterDto registerDto){
        Response response = this.userService.register(registerDto);
        if (response.getStatus().equals(Status.successful)){
            this.currentUserId = ((UserEntity) response.getData()).getId();
        }
        return response;
    }

    private Response login(LoginDto loginDto) {
        Response response = this.userService.login(loginDto);
        if (response.getStatus().equals(Status.successful)){
            this.currentUserId = ((UserEntity) response.getData()).getId();
        }
        return response;
    }

    private Response logout(){
        this.currentUserId = -1;
        return this.userService.logOut();
    }

    private Response followUser(int userId, FollowUserDto dto) {
        Response response = new Response();
        response.setTitle(Title.followUser);
        if (this.userService.findOneEntity(userId).getId() == 0 ||
                this.userService.findOneEntity(dto.getFriendId()).getId() == 0
        ) {
            response.setError(Error.notFound);
            return response;
        }
        this.userService.followUser(userId, dto.getFriendId());
        response.successful();
        return response;
    }

    private Response followArtist(int userId, FollowArtistDto dto) {
        Response response = new Response();
        response.setTitle(Title.followUser);
//        if (this.userService.findOneEntity(userId).getId() == 0 ||
//                this.artistService.findOneEntity(dto.getArtistId()).getId() == 0
//        ) {
//            response.setError(Error.notFound);
//            return response;
//        }
        this.userService.followArtist(userId, dto.getArtistId());
        response.successful();
        return response;
    }

    private Response uploadFile (UploadDto uploadDto){
        Response response = new Response();
        switch (uploadDto.getUploadType()) {
            case userProfilePicture:
                if (this.userService.findOneEntity(uploadDto.getReferenceId()).getId() == 0) {
                    response.setError(Error.notFound);
                    return response;
                }
                response = this.fileService.uploadFile(uploadDto);
                if (response.getStatus().equals(Status.failed)) return response;
                if (!this.userService.updateUserProfilePicture(uploadDto.getReferenceId(), ((FileDto) response.getData()).getId())) {
                    response.setStatus(Status.failed);
                    response.setData(null);
                }
                return response;
            case playlistCover:
//                if (this.playlistService.findOneEntity(uploadDto.referenceId).getId() == 0) {
//                    response.setError(Error.notFound);
//                    return response;
//                }
                response = this.fileService.uploadFile(uploadDto);
                if (response.getStatus() == Status.failed) return response;
//                if (!this.playlistService.updatePlayerCover(uploadDto.referenceId, ((FileEntity) response.getData()).getId())) {
//                    response.setStatus(Status.failed);
//                    response.setData(null);
//                }
                return response;
            default:
                response.setError(Error.badRequest);
                return response;
        }
    }

    private Response searchUser(String username) {
        Response response = new Response();
        response.setTitle(Title.searchUser);
        response.setData(this.userService.searchUser(username));
        response.successful();
        return response;
    }

    private Response searchMusic(String value) {
        Response response = new Response();
        response.setTitle(Title.searchMusic);
        response.setData(this.musicService.search(value));
        response.successful();
        return response;
    }

    private Response search(String value) {
        Response response = new Response();
        response.setTitle(Title.completeSearch);
        SearchResponseDto results = new SearchResponseDto();
        results.setUsers(this.userService.searchUser(value));
//        results.setMusics(this.artistService.search(value));
//        results.setMusics(this.albumService.search(value));
//        results.setMusics(this.playlistService.search(value));
        results.setMusics(this.musicService.search(value));
        response.setData(results);
        response.successful();
        return response;
    }

    private Response likeMusic(int userId, int musicId) {
        Response response = new Response();
        response.setTitle(Title.likeMusic);
        this.musicService.likeMusic(musicId);
//        PlaylistEntity likedSongPL = this.playlistService.addSongToLikedMusicPlaylist(userId, musicId);
//        response.setData(likedSongPL);
        response.successful();
        return response;
    }
}
