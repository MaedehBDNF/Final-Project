package Server.Manager;

import Server.Album.AlbumService;
import Server.Artist.ArtistService;
import Server.Config.DatabaseConfigDto;
import Shared.Dto.Album.DoesUserLikedAlbumDto;
import Shared.Dto.Music.*;
import Shared.Entities.*;
import Server.FileManager.FileService;
import Server.Genre.GenreService;
import Server.Music.MusicService;
import Server.Playlist.PlaylistService;
import Server.User.UserService;
import Shared.Cryptography.AESEncryption;
import Shared.Cryptography.RSAEncryption;
import Shared.Dto.Album.FindOneAlbumDto;
import Shared.Dto.Album.LikeAlbumDto;
import Shared.Dto.Artist.FindOneArtistDto;
import Shared.Dto.File.*;
import Shared.Dto.Genre.FindOneGenreDto;
import Shared.Dto.Playlist.*;
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
import com.mpatric.mp3agic.ID3v2;
import com.mpatric.mp3agic.Mp3File;
import java.io.*;
import java.net.Socket;

public class Manager implements Runnable {
    private int currentUserId = -1;
    private final Socket socket;
    private InputStream inputStream;
    private DataInputStream dataInputStream;
    private OutputStream outputStream;
    private DataOutputStream dataOutputStream;
    private final ObjectMapper mapper = new ObjectMapper();

    private boolean doesExit = false;
    private boolean needResponse = true;

    private final UserService userService;
    private final AlbumService albumService;
    private final ArtistService artistService;
    private final PlaylistService playlistService;
    private final MusicService musicService;
    private final GenreService genreService;
    private final FileService fileService;

    private final RSAEncryption encryptor;
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
                if (this.currentUserId == -1 || request.getUserId() != this.currentUserId) break;
                FindOneUserDto findOneUserDto = this.mapper.convertValue(request.getData(), FindOneUserDto.class);
                return this.userService.findOne(findOneUserDto.getUserId());
            case searchUser:
                if (this.currentUserId == -1 || request.getUserId() != this.currentUserId) break;
                SearchUserDto searchUserDto = this.mapper.convertValue(request.getData(), SearchUserDto.class);
                return this.searchUser(searchUserDto.getUsername());
            case followUser:
                if (this.currentUserId == -1 || request.getUserId() != this.currentUserId) break;
                FollowUserDto followUserDto = this.mapper.convertValue(request.getData(), FollowUserDto.class);
                return this.followUser(request.getUserId(), followUserDto);
            case findUserLikedAlbums:
                if (this.currentUserId == -1 || request.getUserId() != this.currentUserId) break;
                return this.findUserLikedAlbums(request.getUserId());
            case getUserFriends:
                if (this.currentUserId == -1 || request.getUserId() != this.currentUserId) break;
                FindUserFriendsDto findUserFriendsDto = this.mapper.convertValue(request.getData(), FindUserFriendsDto.class);
                return this.userService.getUserFriends(findUserFriendsDto.getUserId());
            case followArtist:
                if (this.currentUserId == -1 || request.getUserId() != this.currentUserId) break;
                FollowArtistDto followArtistDto = this.mapper.convertValue(request.getData(), FollowArtistDto.class);
                return this.followArtist(request.getUserId(), followArtistDto);
            case getUserFollowings:
                if (this.currentUserId == -1 || request.getUserId() != this.currentUserId) break;
                FindUserFollowingsDto findUserFollowingsDto = this.mapper.convertValue(request.getData(), FindUserFollowingsDto.class);
                return this.userService.getUserFollowings(findUserFollowingsDto.getUserId());
            case doesUserFollowedArtist:
                if (this.currentUserId == -1 || request.getUserId() != this.currentUserId) break;
                DoesUserFollowedArtistDto doesUserFollowedArtistDto = this.mapper.convertValue(request.getData(), DoesUserFollowedArtistDto.class);
                return this.userService.doesUserFollowedArtist(doesUserFollowedArtistDto);
            case doesUserFollowedUser:
                if (this.currentUserId == -1 || request.getUserId() != this.currentUserId) break;
                DoesUserFollowedUserDto doesUserFollowedUserDto = this.mapper.convertValue(request.getData(), DoesUserFollowedUserDto.class);
                return this.userService.doesUserFollowedUser(doesUserFollowedUserDto);
            case logOut:
                if (this.currentUserId == -1 || request.getUserId() != this.currentUserId) break;
                return this.logout();

            // File Management
            case upload:
                if (this.currentUserId == -1 || request.getUserId() != this.currentUserId) break;
                UploadDto uploadDto = this.mapper.convertValue(request.getData(), UploadDto.class);
                return this.uploadFile(uploadDto);
            case getFileInfo:
                downloadDto = this.mapper.convertValue(request.getData(), DownloadDto.class);
                return this.fileService.getFileInfo(downloadDto);
            case download:
                downloadDto = this.mapper.convertValue(request.getData(), DownloadDto.class);
                this.fileService.download(downloadDto);
                this.needResponse = false;
                break;

            // Genre Manager
            case findOneGenre:
                FindOneGenreDto findOneGenreDto = this.mapper.convertValue(request.getData(), FindOneGenreDto.class);
                return this.genreService.findOneGenre(findOneGenreDto.getId());
            case findAllGenres:
                return this.genreService.findAll();

            // Album Manager
            case findOneAlbum:
                FindOneAlbumDto findOneAlbumDto = this.mapper.convertValue(request.getData(), FindOneAlbumDto.class);
                return this.albumService.findOne(findOneAlbumDto.getId());
            case findAlbumSongs:
                FindOneAlbumDto findOneAlbumSongs = this.mapper.convertValue(request.getData(), FindOneAlbumDto.class);
                return this.findAlbumSongs(findOneAlbumSongs.getId());
            case searchAlbum:
                SearchRequestDto searchAlbumDto = this.mapper.convertValue(request.getData(), SearchRequestDto.class);
                return this.searchAlbum(searchAlbumDto.getValue());
            case likeAlbum:
                if (this.currentUserId == -1 || request.getUserId() != this.currentUserId) break;
                LikeAlbumDto likeAlbumDto = this.mapper.convertValue(request.getData(), LikeAlbumDto.class);
                return this.likeAlbum(likeAlbumDto);
            case doesUserLikedAlbum:
                if (this.currentUserId == -1 || request.getUserId() != this.currentUserId) break;
                DoesUserLikedAlbumDto doesUserLikedAlbumDto = this.mapper.convertValue(request.getData(), DoesUserLikedAlbumDto.class);
                return this.albumService.doesUserLikedAlbum(doesUserLikedAlbumDto);

            // Artist Manager
            case findOneArtist:
                FindOneArtistDto findOneArtistDto = this.mapper.convertValue(request.getData(), FindOneArtistDto.class);
                return this.artistService.findOne(findOneArtistDto.getId());
            case findArtistAlbums:
                FindOneArtistDto findOneArtistAlbums = this.mapper.convertValue(request.getData(), FindOneArtistDto.class);
                return this.artistService.findArtistAlbums(findOneArtistAlbums.getId());
            case searchArtist:
                SearchRequestDto searchArtistDto = this.mapper.convertValue(request.getData(), SearchRequestDto.class);
                return this.searchArtist(searchArtistDto.getValue());

            // Music Manager
            case findOneMusic:
                FindOneMusicDto findOneMusicDto = this.mapper.convertValue(request.getData(), FindOneMusicDto.class);
                return this.musicService.findOne(findOneMusicDto.getId());
            case likeMusic:
                if (this.currentUserId == -1 || request.getUserId() != this.currentUserId) break;
                LikeMusicDto likeMusicDto = this.mapper.convertValue(request.getData(), LikeMusicDto.class);
                return this.likeMusic(request.getUserId(), likeMusicDto.getId());
            case dislikeMusic:
                if (this.currentUserId == -1 || request.getUserId() != this.currentUserId) break;
                DislikeMusicDto dislikeMusicDto = this.mapper.convertValue(request.getData(), DislikeMusicDto.class);
                return this.dislikeMusic(request.getUserId(), dislikeMusicDto.getId());
            case addCommentOnMusic:
                if (this.currentUserId == -1 || request.getUserId() != this.currentUserId) break;
                CommentEntity comment = this.mapper.convertValue(request.getData(), CommentEntity.class);
                return this.musicService.addComment(comment);
            case searchMusic:
                SearchRequestDto searchMusicDto = this.mapper.convertValue(request.getData(), SearchRequestDto.class);
                return this.searchMusic(searchMusicDto.getValue());
            case doesUserLikedMusic:
                if (this.currentUserId == -1 || request.getUserId() != this.currentUserId) break;
                DoesUserLikedMusicDto doesUserLikedMusicDto = this.mapper.convertValue(request.getData(), DoesUserLikedMusicDto.class);
                int userLikedMusicPL = this.playlistService.getLikedMusicsPlaylist(doesUserLikedMusicDto.getUserId());
                return this.musicService.doesUserLikedMusic(userLikedMusicPL, doesUserLikedMusicDto.getMusicId());
            case downloadMusicCover:
                DownloadMusicCoverDto downloadMusicCoverDto = this.mapper.convertValue(request.getData(), DownloadMusicCoverDto.class);
                this.downloadMusicCover(downloadMusicCoverDto.getMusicId());
                this.needResponse = false;
                break;

            // Playlist Manager
            case createPlaylist:
                if (this.currentUserId == -1 || request.getUserId() != this.currentUserId) break;
                CreatePlaylistDto createPlaylistDto = this.mapper.convertValue(request.getData(), CreatePlaylistDto.class);
                return this.createPlaylist(createPlaylistDto);
            case findOnePlaylist:
                FindOnePlaylistDto findOnePlaylistDto = this.mapper.convertValue(request.getData(), FindOnePlaylistDto.class);
                return this.playlistService.findOne(currentUserId, findOnePlaylistDto.getId());
            case findAllUserPlaylists:
                if (this.currentUserId == -1 || request.getUserId() != this.currentUserId) break;
                FindUserPlaylistsDto findUserPlaylistsDto = this.mapper.convertValue(request.getData(), FindUserPlaylistsDto.class);
                return this.findAllUserPlaylist(findUserPlaylistsDto.getUserId());
            case searchPlaylist:
                SearchRequestDto searchPlaylistDto = this.mapper.convertValue(request.getData(), SearchRequestDto.class);
                return this.searchPlaylist(searchPlaylistDto.getValue());
            case likePlayList:
                if (this.currentUserId == -1 || request.getUserId() != this.currentUserId) break;
                LikePlaylistDto likePlaylistDto = this.mapper.convertValue(request.getData(), LikePlaylistDto.class);
                return this.likePlaylist(likePlaylistDto);
            case addPlaylist:
                if (this.currentUserId == -1 || request.getUserId() != this.currentUserId) break;
                AddPlaylistDto addPlaylistDto = this.mapper.convertValue(request.getData(), AddPlaylistDto.class);
                return this.playlistService.addToUserPlaylists(addPlaylistDto);
            case addMusicToPlaylist:
                if (this.currentUserId == -1 || request.getUserId() != this.currentUserId) break;
                AddMusicToPlaylistDto addMusicToPlaylistDto = this.mapper.convertValue(request.getData(), AddMusicToPlaylistDto.class);
                return this.addMusicToPlaylist(addMusicToPlaylistDto);
            case removeMusicFromPlaylist:
                if (this.currentUserId == -1 || request.getUserId() != this.currentUserId) break;
                RemoveMusicFromPlaylistDto removeMusicDto = this.mapper.convertValue(request.getData(), RemoveMusicFromPlaylistDto.class);
                return this.removeMusicFromPlaylist(removeMusicDto);
            case changeMusicOrderInPlaylist:
                if (this.currentUserId == -1 || request.getUserId() != this.currentUserId) break;
                UpdateMusicTurnDto updateMusicTurnDto = this.mapper.convertValue(request.getData(), UpdateMusicTurnDto.class);
                return this.changeMusicOrderInPlaylist(updateMusicTurnDto);
            case doesUserLikedPlaylist:
                if (this.currentUserId == -1 || request.getUserId() != this.currentUserId) break;
                DoesUserLikedPlaylistDto doesUserLikedPlaylistDto = this.mapper.convertValue(request.getData(), DoesUserLikedPlaylistDto.class);
                return this.playlistService.doesUserLikedPlaylist(doesUserLikedPlaylistDto);
            case doesUserAddedPlaylist:
                if (this.currentUserId == -1 || request.getUserId() != this.currentUserId) break;
                DoesUserAddedPlaylistDto doesUSerAddedPlaylistDto = this.mapper.convertValue(request.getData(), DoesUserAddedPlaylistDto.class);
                return this.playlistService.doesUserAddedPlaylist(doesUSerAddedPlaylistDto);

            // All
            case completeSearch:
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
        return this.userService.followUser(userId, dto.getFriendId());
    }

    private Response followArtist(int userId, FollowArtistDto dto) {
        Response response = new Response();
        response.setTitle(Title.followArtist);
        if (this.userService.findOneEntity(userId).getId() == 0 ||
                this.artistService.findOneEntity(dto.getArtistId()).getId() == 0
        ) {
            response.setError(Error.notFound);
            return response;
        }
        response.setError(this.userService.followArtist(userId, dto.getArtistId()));
        if (!response.getError().equals(Error.none)) {
            return response;
        }
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
                if (this.playlistService.findOneEntity(uploadDto.getReferenceId()).getId() == 0) {
                    response.setError(Error.notFound);
                    return response;
                }
                response = this.fileService.uploadFile(uploadDto);
                if (response.getStatus() == Status.failed) return response;
                if (!this.playlistService.updatePlayerCover(uploadDto.getReferenceId(), ((FileDto) response.getData()).getId())) {
                    response.setStatus(Status.failed);
                    response.setData(null);
                }
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

    private Response searchAlbum(String value) {
        Response response = new Response();
        response.setTitle(Title.searchAlbum);
        response.setData(this.albumService.search(value));
        response.successful();
        return response;
    }

    private Response searchArtist(String str){
        Response response = new Response();
        response.setTitle(Title.searchArtist);
        response.setData(this.artistService.search(str));
        response.successful();
        return response;
    }

    private Response searchPlaylist(String str){
        Response response = new Response();
        response.setTitle(Title.searchPlaylist);
        response.setData(this.playlistService.search(str, this.currentUserId));
        response.successful();
        return response;
    }

    private Response createPlaylist(CreatePlaylistDto createPlaylistDto) {
        Response response = new Response();
        response.setTitle(Title.createPlaylist);
        response.setData(this.playlistService.createNewPlaylist(createPlaylistDto));
        response.successful();
        return response;
    }

    private Response findUserLikedAlbums(int userId) {
        Response response = new Response();
        response.setTitle(Title.findUserLikedAlbums);
        response.setData(this.userService.findUserLikedAlbums(userId));
        response.successful();
        return response;
    }

    private Response findAlbumSongs(int id){
        Response response = new Response();
        response.setTitle(Title.findAlbumSongs);
        response.setData(this.albumService.findAlbumSongs(id));
        response.successful();
        return response;
    }

    private Response likeAlbum(LikeAlbumDto likeAlbumDto) {
        return this.albumService.likeAlbum(likeAlbumDto);
    }

    private Response findAllUserPlaylist(int userId) {
        Response response = new Response();
        response.setTitle(Title.findAllUserPlaylists);
        if (userId == this.currentUserId) {
            response.setData(this.playlistService.findAllUserPlaylists(userId));
        } else {
            response.setData(this.playlistService.findUserPublicPlaylists(userId));
        }
        response.successful();
        return response;
    }

    private Response search(String value) {
        Response response = new Response();
        response.setTitle(Title.completeSearch);
        SearchResponseDto results = new SearchResponseDto();
        if (this.currentUserId != -1) {
            results.setUsers(this.userService.searchUser(value));
        }
        results.setArtists(this.artistService.search(value));
        results.setAlbums(this.albumService.search(value));
        results.setPlaylists(this.playlistService.search(value, this.currentUserId));
        results.setMusics(this.musicService.search(value));
        response.setData(results);
        response.successful();
        return response;
    }

    private Response likeMusic(int userId, int musicId) {
        Response response = new Response();
        response.setTitle(Title.likeMusic);
        int userLikedMusicPL = this.playlistService.getLikedMusicsPlaylist(userId);
        if (this.musicService.doesMusicBelongsToPL(userLikedMusicPL, musicId)) {
            response.setError(Error.duplicateDataError);
            return response;
        }
        AddMusicToPlaylistDto addDto = new AddMusicToPlaylistDto();
        addDto.setId(userLikedMusicPL);
        addDto.setMusicId(musicId);
        PlaylistEntity likedSongPL = this.playlistService.addMusic(addDto, userId, true);
        if (likedSongPL.getId() == 0) {
            response.setError(Error.forbidden);
            return response;
        }
        if (!this.musicService.likeMusic(musicId)){
            response.setError(Error.databaseError);
            return response;
        }
        response.setData(likedSongPL);
        response.successful();
        return response;
    }

    private Response dislikeMusic(int userId, int musicId) {
        Response response = new Response();
        response.setTitle(Title.dislikeMusic);
        RemoveMusicFromPlaylistDto removeMusicDto = new RemoveMusicFromPlaylistDto();
        removeMusicDto.setId(this.playlistService.getLikedMusicsPlaylist(userId));
        if (removeMusicDto.getId() == -1) {
            response.setError(Error.notFound);
            return response;
        }
        removeMusicDto.setMusicId(musicId);
        if(!this.playlistService.removeMusic(removeMusicDto, userId, true)) {
            response.setError(Error.databaseError);
            return response;
        }
        if (!this.musicService.dislikeMusic(musicId)){
            response.setError(Error.databaseError);
            return response;
        }
        response.successful();
        return response;
    }

    private Response likePlaylist(LikePlaylistDto likePlaylistDto) {
        return this.playlistService.likePlaylist(likePlaylistDto.getId(), this.currentUserId);
    }

    private void downloadMusicCover(int musicId) {
        MusicEntity musicEntity = this.musicService.findOneEntity(musicId);
        this.fileService.downloadMusicCover(musicEntity.getFile().getId());
    }

    private Response addMusicToPlaylist(AddMusicToPlaylistDto addMusicToPlaylistDto) {
        Response response = new Response();
        response.setTitle(Title.addMusicToPlaylist);
        if (this.playlistService.addMusic(addMusicToPlaylistDto, this.currentUserId, false).getId() != 0) {
            response.successful();
            return response;
        } else {
            response.setError(Error.forbidden);
            return response;
        }
    }

    private Response removeMusicFromPlaylist(RemoveMusicFromPlaylistDto removeMusicDto) {
        Response response = new Response();
        response.setTitle(Title.removeMusicFromPlaylist);
        if (this.playlistService.removeMusic(removeMusicDto, this.currentUserId, false)) {
            response.successful();
            return response;
        } else {
            response.setError(Error.forbidden);
            return response;
        }
    }

    private Response changeMusicOrderInPlaylist(UpdateMusicTurnDto updateMusicTurnDto) {
        Response response = new Response();
        response.setTitle(Title.changeMusicOrderInPlaylist);
        if (this.playlistService.updateMusicTurn(updateMusicTurnDto)) {
            response.successful();
            return response;
        } else {
            response.setError(Error.databaseError);
            return response;
        }
    }
}
