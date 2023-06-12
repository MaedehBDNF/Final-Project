import Shared.Cryptography.AESEncryption;
import Shared.Cryptography.RSAEncryption;
import Shared.Dto.Album.FindOneAlbumDto;
import Shared.Dto.Album.LikeAlbumDto;
import Shared.Dto.Artist.FindOneArtistDto;
import Shared.Dto.File.DownloadDto;
import Shared.Dto.File.FileDto;
import Shared.Dto.File.UploadDto;
import Shared.Dto.Genre.FindOneGenreDto;
import Shared.Dto.Music.DislikeMusicDto;
import Shared.Dto.Music.FindOneMusicDto;
import Shared.Dto.Music.LikeMusicDto;
import Shared.Dto.Playlist.*;
import Shared.Dto.Search.SearchRequestDto;
import Shared.Dto.Search.SearchResponseDto;
import Shared.Dto.User.FollowArtistDto;
import Shared.Dto.User.FollowUserDto;
import Shared.Dto.User.LoginDto;
import Shared.Dto.User.RegisterDto;
import Shared.Entities.*;
import Shared.Enums.Status;
import Shared.Enums.Title;
import Shared.Enums.UploadType;
import Shared.Request;
import Shared.Response;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import javax.swing.*;
import javax.swing.filechooser.FileSystemView;
import java.io.*;
import java.net.Socket;
import java.security.PublicKey;
import java.util.UUID;

public class Test {
    private static final JFileChooser fileChooser = new JFileChooser(FileSystemView.getFileSystemView().getHomeDirectory());
    private static int currentUserID;
    private static final ObjectMapper objectMapper = new ObjectMapper();
    private static InputStream inputStream;
    private static DataInputStream dataInputStream;
    private static OutputStream outputStream;
    private static DataOutputStream dataOutputStream;
    private static final RSAEncryption rsaEncryption = new RSAEncryption();
    private static PublicKey serverPubKey;
    private static String AES_SECRET_KEY;
    private static AESEncryption aesEncryption;
    private static final String downloadDirectory = System.getProperty("user.dir") + "\\src\\main\\java\\Client\\Downloads\\";
    private static int downloadId;
    private static FileDto downloadFileInfo;

    public static void main(String[] args) throws IOException, InterruptedException {
        final int PORT = 3000;

        Socket socket = new Socket("localhost", PORT);

        inputStream = socket.getInputStream();
        dataInputStream = new DataInputStream(inputStream);

        outputStream = socket.getOutputStream();
        dataOutputStream = new DataOutputStream(outputStream);

        objectMapper.registerModule(new JavaTimeModule()); // for existence of LocalDate in parsing to JSON

        getServerRSAPublicKey();
        sendAESKey();
        aesEncryption = new AESEncryption(AES_SECRET_KEY);


        // test with login
        System.out.println("WITH LOGIN");
        register(1);
        register(2);
        register(3);
        register(4);
        register(6);
        login("user1", "user1");
//        uploadProfilePicture();
//        getDownloadInfo();
//        download();
//        completeSearch("e");
//        PlaylistEntity playlistEntity = createPlaylist("public", currentUserID, "I love my life.", false);
//        findOnePlaylist(2);
//        findAllGenres();
//        followUser(2);
//        followArtist(4);
//        getUserFriends();
//        getUserFollowings();
//        findAlbumSongs(2);
//        likeAlbum(1);
//        likeAlbum(2);
//        likeAlbum(3);
//        getUserLikedAlbums();
//        likeMusic(1);
//        likeMusic(2);
//        dislikeMusic(1);
//        addCommentToMusic(1);
//        addCommentToMusic(2);
//        findOneMusic(1);
//        findAllUserPlaylists();
//        addMusicToPlaylist(1, playlistEntity.getId());
//        addMusicToPlaylist(1, playlistEntity.getId());
//        addMusicToPlaylist(2, playlistEntity.getId());
//        addMusicToPlaylist(3, playlistEntity.getId());
//        findOnePlaylist(playlistEntity.getId());
//        changeMusicOrderInPlaylist(2, playlistEntity.getId(), 4.0);
//        findOnePlaylist(playlistEntity.getId());
//        changeMusicOrderInPlaylist(2, playlistEntity.getId(), 1.5);
//        findOnePlaylist(playlistEntity.getId());

        logout();

//        login("user2", "user2");
//        addPlaylist(playlistEntity.getId(), currentUserID);
//        likePlaylist(playlistEntity.getId(), currentUserID);
//        findAllUserPlaylists();

        // test without login
        System.out.println("WITHOUT LOGIN");
//        completeSearch("e");
//        findOneAlbum(1);
//        findOneMusic(1);
//        findOneGenre(2);
//        findOnePlaylist(3);
//        findOneArtist(4);
//        findAllGenres();
//        followUser(3);
//        followArtist(4);
//        getUserFriends();
//        getUserFollowings();
//        findAlbumSongs(2);
//        likeAlbum(1);
//        likeAlbum(2);
//        likeAlbum(3);
//        getUserLikedAlbums();
//        likeMusic(1);
//        likeMusic(2);
//        addCommentToMusic(1);
//        addCommentToMusic(2);
//        findOneMusic(1);
//        findAllUserPlaylists();

        exit();
        dataOutputStream.close();
        dataInputStream.close();
    }

    private static void register(int n) {
        RegisterDto registerDto = new RegisterDto();
        registerDto.setUsername("user" + n);
        registerDto.setPassword("user" + n);
        registerDto.setEmail("user" + n + "@gmail.com");
        Request request = new Request();
        request.setTitle(Title.register);
        request.setData(registerDto);
        sendReqToServer(request);
        Response response = getResFromServer();
        if (response.getStatus().equals(Status.successful)) {
            currentUserID = objectMapper.convertValue(response.getData(), UserEntity.class).getId();
        }
    }

    private static void login(String username, String password) {
        LoginDto loginDto = new LoginDto();
        loginDto.setUsername(username);
        loginDto.setPassword(password);
        Request request = new Request();
        request.setTitle(Title.login);
        request.setData(loginDto);
        sendReqToServer(request);
        Response rs = getResFromServer();
        if (rs.getStatus().equals(Status.successful)) {
            currentUserID = objectMapper.convertValue(rs.getData(), UserEntity.class).getId();
        }
    }

    private static void uploadProfilePicture() {
        String path = getFileDirectory();
        File file = new File(path);
        UploadDto uploadDto = new UploadDto();
        uploadDto.setMemeType("jpg");
        uploadDto.setName(file.getName().substring(0, file.getName().lastIndexOf('.')));
        uploadDto.setReferenceId(currentUserID);
        uploadDto.setUploadType(UploadType.userProfilePicture);
        Request request = new Request();
        request.setTitle(Title.upload);
        request.setUserId(currentUserID);
        request.setData(uploadDto);
        sendReqToServer(request);
        sendFile(file);
        Response response = getResFromServer();
        downloadId = objectMapper.convertValue(response.getData(), FileDto.class).getId();
    }

    private static void getDownloadInfo(){
        Request request = new Request();
        request.setUserId(currentUserID);
        request.setTitle(Title.getFileInfo);
        DownloadDto downloadDto = new DownloadDto();
        downloadDto.setFileId(downloadId);
        request.setData(downloadDto);
        sendReqToServer(request);
        Response response1 = getResFromServer();
        downloadFileInfo = objectMapper.convertValue(response1.getData(), FileDto.class);
    }

    private static void download() {
        Request request = new Request();
        request.setTitle(Title.download);
        request.setUserId(currentUserID);
        DownloadDto downloadDto = new DownloadDto();
        downloadDto.setFileId(downloadId);
        request.setData(downloadDto);
        sendReqToServer(request);
        receiveFile(downloadFileInfo);
    }

    private static void completeSearch(String value) {
        Request request = new Request();
        request.setTitle(Title.completeSearch);
        SearchRequestDto searchDto = new SearchRequestDto();
        searchDto.setValue(value);
        request.setUserId(currentUserID);
        request.setData(searchDto);
        sendReqToServer(request);
        SearchResponseDto searchResponseDto = objectMapper.convertValue(getResFromServer().getData(), SearchResponseDto.class);
        System.out.println("complete search:");
        System.out.println("Users:");
        if (searchResponseDto.getUsers() != null) {
            for (UserEntity user: searchResponseDto.getUsers()) {
                System.out.println(user);
            }
        }
        System.out.println("Artists:");
        if (searchResponseDto.getArtists() != null) {
            for (ArtistEntity artist: searchResponseDto.getArtists()) {
                System.out.println(artist);
            }
        }
        System.out.println("Albums:");
        if (searchResponseDto.getAlbums() != null) {
            for (AlbumEntity album: searchResponseDto.getAlbums()) {
                System.out.println(album);
            }
        }
        System.out.println("musics:");
        if (searchResponseDto.getMusics() != null) {
            for (MusicEntity music: searchResponseDto.getMusics()) {
                System.out.println(music);
            }
        }
        System.out.println("playlists:");
        if (searchResponseDto.getPlaylists() != null) {
            for (PlaylistEntity playlist: searchResponseDto.getPlaylists()) {
                System.out.println(playlist);
            }
        }
    }

    private static void findOneMusic(int musicId) {
        FindOneMusicDto findOneMusicDto = new FindOneMusicDto();
        findOneMusicDto.setId(musicId);
        Request request = new Request();
        request.setTitle(Title.findOneMusic);
        request.setUserId(currentUserID);
        request.setData(findOneMusicDto);
        sendReqToServer(request);
        Response response = getResFromServer();
        MusicEntity musicEntity = objectMapper.convertValue(response.getData(), MusicEntity.class);
        System.out.println(musicEntity);
    }

    private static void findOneAlbum(int albumId) {
        FindOneAlbumDto findOneAlbumDto = new FindOneAlbumDto();
        findOneAlbumDto.setId(albumId);
        Request request = new Request();
        request.setTitle(Title.findOneAlbum);
        request.setUserId(currentUserID);
        request.setData(findOneAlbumDto);
        sendReqToServer(request);
        Response response = getResFromServer();
        AlbumEntity albumEntity = objectMapper.convertValue(response.getData(), AlbumEntity.class);
        System.out.println(albumEntity);
    }

    private static void findOneArtist(int artistId) {
        FindOneArtistDto findOneArtistDto = new FindOneArtistDto();
        findOneArtistDto.setId(artistId);
        Request request = new Request();
        request.setTitle(Title.findOneArtist);
        request.setUserId(currentUserID);
        request.setData(findOneArtistDto);
        sendReqToServer(request);
        Response response = getResFromServer();
        ArtistEntity artistEntity = objectMapper.convertValue(response.getData(), ArtistEntity.class);
        System.out.println(artistEntity);
    }

    private static void findOneGenre(int genreId) {
        FindOneGenreDto findOneGenreDto = new FindOneGenreDto();
        findOneGenreDto.setId(genreId);
        Request request = new Request();
        request.setTitle(Title.findOneGenre);
        request.setUserId(currentUserID);
        request.setData(findOneGenreDto);
        sendReqToServer(request);
        Response response = getResFromServer();
        GenreEntity genreEntity = objectMapper.convertValue(response.getData(), GenreEntity.class);
        System.out.println(genreEntity);
    }

    private static void findAllGenres() {
        Request request = new Request();
        request.setTitle(Title.findAllGenres);
        request.setUserId(currentUserID);
        sendReqToServer(request);
        getResFromServer();
    }

    private static void findOnePlaylist(int playlistId) {
        FindOnePlaylistDto findOnePlaylistDto = new FindOnePlaylistDto();
        findOnePlaylistDto.setId(playlistId);
        Request request = new Request();
        request.setTitle(Title.findOnePlaylist);
        request.setUserId(currentUserID);
        request.setData(findOnePlaylistDto);
        sendReqToServer(request);
        Response response = getResFromServer();
        PlaylistEntity playlistEntity = objectMapper.convertValue(response.getData(), PlaylistEntity.class);
        System.out.println(playlistEntity);
    }

    private static void followUser(int friendId) {
        FollowUserDto followUserDto = new FollowUserDto();
        followUserDto.setFriendId(friendId);
        Request request = new Request();
        request.setTitle(Title.followUser);
        request.setUserId(currentUserID);
        request.setData(followUserDto);
        sendReqToServer(request);
        getResFromServer();
    }

    private static void followArtist(int artistId) {
        FollowArtistDto followArtistDto = new FollowArtistDto();
        followArtistDto.setArtistId(artistId);
        Request request = new Request();
        request.setTitle(Title.followArtist);
        request.setUserId(currentUserID);
        request.setData(followArtistDto);
        sendReqToServer(request);
        getResFromServer();
    }

    private static void getUserFriends() {
        Request request = new Request();
        request.setTitle(Title.getUserFriends);
        request.setUserId(currentUserID);
        sendReqToServer(request);
        getResFromServer();
    }

    private static void getUserFollowings() {
        Request request = new Request();
        request.setTitle(Title.getUserFollowings);
        request.setUserId(currentUserID);
        sendReqToServer(request);
        getResFromServer();
    }

    private static PlaylistEntity createPlaylist(String title, int creatorId, String description, boolean isPrivate) {
        CreatePlaylistDto createPlaylistDto = new CreatePlaylistDto();
        createPlaylistDto.setTitle(title);
        createPlaylistDto.setCreatorId(currentUserID);
        createPlaylistDto.setDescription(description);
        createPlaylistDto.setPrivatePL(isPrivate);
        Request request = new Request();
        request.setUserId(currentUserID);
        request.setTitle(Title.createPlaylist);
        request.setData(createPlaylistDto);
        sendReqToServer(request);
        Response response = getResFromServer();
        return objectMapper.convertValue(response.getData(), PlaylistEntity.class);
    }

    private static void findAlbumSongs(int albumId) {
        FindOneAlbumDto findOneAlbumDto = new FindOneAlbumDto();
        findOneAlbumDto.setId(albumId);
        Request request = new Request();
        request.setUserId(currentUserID);
        request.setTitle(Title.findAlbumSongs);
        request.setData(findOneAlbumDto);
        sendReqToServer(request);
        getResFromServer();
    }

    private static void likeAlbum(int albumId) {
        LikeAlbumDto likeAlbumDto = new LikeAlbumDto();
        likeAlbumDto.setAlbumId(albumId);
        likeAlbumDto.setUserid(currentUserID);
        Request request = new Request();
        request.setUserId(currentUserID);
        request.setTitle(Title.likeAlbum);
        request.setData(likeAlbumDto);
        sendReqToServer(request);
        getResFromServer();
    }

    private static void getUserLikedAlbums() {
        Request request = new Request();
        request.setUserId(currentUserID);
        request.setTitle(Title.findUserLikedAlbums);
        sendReqToServer(request);
        getResFromServer();
    }

    private static void likeMusic(int musicId) {
        Request request = new Request();
        LikeMusicDto likeMusicDto = new LikeMusicDto();
        likeMusicDto.setId(musicId);
        request.setUserId(currentUserID);
        request.setTitle(Title.likeMusic);
        request.setData(likeMusicDto);
        sendReqToServer(request);
        getResFromServer();
    }

    private static void dislikeMusic(int musicId) {
        Request request = new Request();
        DislikeMusicDto dislikeMusicDto = new DislikeMusicDto();
        dislikeMusicDto.setId(musicId);
        request.setUserId(currentUserID);
        request.setTitle(Title.dislikeMusic);
        request.setData(dislikeMusicDto);
        sendReqToServer(request);
        getResFromServer();
    }

    private static void addCommentToMusic(int musicId) {
        Request request = new Request();
        request.setUserId(currentUserID);
        request.setTitle(Title.addCommentOnMusic);
        CommentEntity commentEntity = new CommentEntity();
        commentEntity.setMusicId(musicId);
        commentEntity.setUserId(currentUserID);
        commentEntity.setComment("Comment on music");
        request.setData(commentEntity);
        sendReqToServer(request);
        getResFromServer();
    }

    private static void findAllUserPlaylists() {
        Request request = new Request();
        request.setUserId(currentUserID);
        request.setTitle(Title.findAllUserPlaylists);
        sendReqToServer(request);
        Response response = getResFromServer();
        PlaylistEntity[] pls = objectMapper.convertValue(response.getData(), PlaylistEntity[].class);
        for (PlaylistEntity pl: pls) {
            System.out.println(pl);
        }
    }

    private static void addMusicToPlaylist(int musicId, int playlistId) {
        Request request = new Request();
        request.setUserId(currentUserID);
        request.setTitle(Title.addMusicToPlaylist);
        AddMusicToPlaylistDto addMusic = new AddMusicToPlaylistDto();
        addMusic.setMusicId(musicId);
        addMusic.setId(playlistId);
        request.setData(addMusic);
        sendReqToServer(request);
        getResFromServer();
    }

    private static void changeMusicOrderInPlaylist(int musicId, int playlistId, double newOrder) {
        Request request = new Request();
        request.setUserId(currentUserID);
        request.setTitle(Title.changeMusicOrderInPlaylist);
        UpdateMusicTurnDto musicTurnDto = new UpdateMusicTurnDto();
        musicTurnDto.setMusicId(musicId);
        musicTurnDto.setId(playlistId);
        musicTurnDto.setTurn(newOrder);
        request.setData(musicTurnDto);
        sendReqToServer(request);
        getResFromServer();
    }

    private static void addPlaylist(int playlistId, int userId) {
        Request request = new Request();
        request.setUserId(currentUserID);
        request.setTitle(Title.addPlaylist);
        AddPlaylistDto addPlaylist = new AddPlaylistDto();
        addPlaylist.setUserId(userId);
        addPlaylist.setId(playlistId);
        request.setData(addPlaylist);
        sendReqToServer(request);
        getResFromServer();
    }

    private static void likePlaylist(int playlistId, int userId) {
        Request request = new Request();
        request.setUserId(currentUserID);
        request.setTitle(Title.likePlayList);
        LikePlaylistDto likePlaylist = new LikePlaylistDto();
        likePlaylist.setId(playlistId);
        request.setData(likePlaylist);
        sendReqToServer(request);
        getResFromServer();
    }

    private static void logout() {
        Request request = new Request();
        request.setTitle(Title.logOut);
        request.setUserId(currentUserID);
        sendReqToServer(request);
        getResFromServer();
    }

    private static void exit() {
        Request request = new Request();
        request.setTitle(Title.exit);
        request.setUserId(currentUserID);
        sendReqToServer(request);
    }

    private static void getServerRSAPublicKey() {
        try {
            // Receive the server public key
            ObjectInputStream objectInputStream = new ObjectInputStream(inputStream);
            serverPubKey = (PublicKey) objectInputStream.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private static void sendAESKey() {
        UUID secretKey = UUID.randomUUID();
        AES_SECRET_KEY = secretKey.toString().substring(0, 16);
        // encrypt with RSA and send the AES key to server
        try {
            String cipherSecretKey = rsaEncryption.encrypt(AES_SECRET_KEY, serverPubKey);
            dataOutputStream.writeUTF(cipherSecretKey);
            dataOutputStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("AES key sent!");
    }

    private static void sendReqToServer(Request request) {
        try {
            String requestJSON = objectMapper.writeValueAsString(request);
            System.out.println(requestJSON);
            String cipherRequest = aesEncryption.encrypt(requestJSON);
            dataOutputStream.writeUTF(cipherRequest);
            dataOutputStream.flush();
        } catch (JsonProcessingException e) {
            System.out.println("A problem in parsing to JSON occurred.");
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static Response getResFromServer() {
        Response response = null;
        try {
            String cipherResponse = dataInputStream.readUTF();
            String responseJSON = aesEncryption.decrypt(cipherResponse);
            System.out.println(responseJSON);
            response = objectMapper.readValue(responseJSON, Response.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return response;
    }

    private static void sendFile(File file){
        int bytes = 0;
        try {
            FileInputStream fileInputStream = new FileInputStream(file);

            dataOutputStream.writeLong(file.length());
            byte[] buffer = new byte[4 * 1024];
            while ((bytes = fileInputStream.read(buffer)) != -1) {
                dataOutputStream.write(buffer, 0, bytes);
                dataOutputStream.flush();
            }
            fileInputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static String receiveFile(FileDto fileDto) {
        int bytes = 0;
        String filePath = "";
        try {
            File file = File.createTempFile(
                    fileDto.getName() + " - ",
                    "." + fileDto.getMemeType(),
                    new File(downloadDirectory)
            );
            FileOutputStream fileOutputStream = new FileOutputStream(file);

            long size = dataInputStream.readLong();
            byte[] buffer = new byte[4 * 1024];
            while (size > 0 && (bytes = dataInputStream.read(buffer, 0, (int)Math.min(buffer.length, size))) != -1) {
                fileOutputStream.write(buffer, 0, bytes);
                size -= bytes;
            }
            System.out.println(String.format("%s file downloaded.", fileDto.getMemeType()));
            fileOutputStream.close();
        } catch (IOException e){
            e.printStackTrace();
        }
        return filePath;
    }

    private static String getFileDirectory(){
        int r = fileChooser.showOpenDialog(null);
        if (r == JFileChooser.APPROVE_OPTION) {
            return fileChooser.getSelectedFile().getAbsolutePath();
        } else { return null; }
    }
}