package Client;

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
import Shared.Dto.User.*;
import Shared.Entities.*;
import Shared.Enums.Status;
import Shared.Enums.Title;
import Shared.Enums.UploadType;
import Shared.Request;
import Shared.Response;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.*;
import java.net.Socket;
import java.nio.file.Files;
import java.security.PublicKey;
import java.util.UUID;

public class ClientManager {
    private final Socket socket;
    private int currentUserId = -1;
    private UserEntity currentUser;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private InputStream inputStream;
    private DataInputStream dataInputStream;
    private OutputStream outputStream;
    private DataOutputStream dataOutputStream;
    private final RSAEncryption rsaEncryption = new RSAEncryption();
    private PublicKey serverPubKey;
    private AESEncryption aesEncryption;
    private final String downloadDirectory = System.getProperty("user.dir") + "\\src\\main\\resources\\Images\\Downloads\\";

    public ClientManager(Socket socket) {
        this.socket = socket;
        this.startConnection();
        this.makeDownloadsDirectory();
    }

    public UserEntity getCurrentUser() {
        return currentUser;
    }

    public void setCurrentUser(UserEntity currentUser) {
        this.currentUser = currentUser;
    }

    public Response register(RegisterDto dto) {
        Request request = new Request();
        request.setTitle(Title.register);
        request.setData(dto);
        this.sendReqToServer(request);
        Response response = this.getResFromServer();
        if (response.getStatus().equals(Status.successful)) {
            this.currentUser = objectMapper.convertValue(response.getData(), UserEntity.class);
            this.currentUserId = this.currentUser.getId();

        }
        return response;
    }

    public Response login(LoginDto dto) {
        Request request = new Request();
        request.setTitle(Title.login);
        request.setData(dto);
        this.sendReqToServer(request);
        Response response = this.getResFromServer();
        if (response.getStatus().equals(Status.successful)) {
            this.currentUser = objectMapper.convertValue(response.getData(), UserEntity.class);
            this.currentUserId = this.currentUser.getId();
        }
        return response;
    }

    public File copyFile(UploadType type, File file) {
        File copyFile;
        try {
            copyFile = File.createTempFile(
                    type + " - ", file.getName().substring(file.getName().lastIndexOf('.')),
                    new File(this.downloadDirectory)
            );
            InputStream is = null;
            OutputStream os = null;
            try {
                is = new FileInputStream(file);
                os = new FileOutputStream(copyFile);
                byte[] buffer = new byte[1024];
                int length;
                while ((length = is.read(buffer)) > 0) {
                    os.write(buffer, 0, length);
                }
            } finally {
                is.close();
                os.close();
            }
            return copyFile;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return file;
    }

    public Response uploadPicture(UploadDto dto, File file) {
        Request request = new Request();
        request.setTitle(Title.upload);
        request.setUserId(this.currentUserId);
        request.setData(dto);
        this.sendReqToServer(request);
        this.sendFile(file);
        return this.getResFromServer();
    }

    public Response getDownloadInfo(DownloadDto dto){
        Request request = new Request();
        request.setUserId(this.currentUserId);
        request.setTitle(Title.getFileInfo);
        request.setData(dto);
        this.sendReqToServer(request);
        return this.getResFromServer();
    }

    public String download(FileDto dto) {
        // check if file exists, return path from local
        String path = this.downloadDirectory + dto.getName() + "." + dto.getMemeType();
        File file = new File(path);
        if (file.exists()) {
            return path;
        }
        // And if not exists, download it then return path
        Request request = new Request();
        request.setTitle(Title.download);
        request.setUserId(currentUserId);
        DownloadDto downloadDto = new DownloadDto();
        downloadDto.setFileId(dto.getId());
        request.setData(downloadDto);
        sendReqToServer(request);
        return this.receiveFile(dto);
    }

    public Response followUser(FollowUserDto dto) {
        Request request = new Request();
        request.setTitle(Title.followUser);
        request.setUserId(this.currentUserId);
        request.setData(dto);
        this.sendReqToServer(request);
        return this.getResFromServer();
    }

    public Response followArtist(FollowArtistDto dto) {
        Request request = new Request();
        request.setTitle(Title.followArtist);
        request.setUserId(this.currentUserId);
        request.setData(dto);
        this.sendReqToServer(request);
        return this.getResFromServer();
    }

    public Response getUserFriends() {
        Request request = new Request();
        request.setTitle(Title.getUserFriends);
        request.setUserId(this.currentUserId);
        this.sendReqToServer(request);
        return this.getResFromServer();
    }

    public Response getUserFollowings() {
        Request request = new Request();
        request.setTitle(Title.getUserFollowings);
        request.setUserId(this.currentUserId);
        this.sendReqToServer(request);
        return this.getResFromServer();
    }

    public Response getUserLikedAlbums() {
        Request request = new Request();
        request.setUserId(this.currentUserId);
        request.setTitle(Title.findUserLikedAlbums);
        this.sendReqToServer(request);
        return this.getResFromServer();
    }

    public Response getUserPlaylists() {
        Request request = new Request();
        request.setUserId(this.currentUserId);
        request.setTitle(Title.findAllUserPlaylists);
        this.sendReqToServer(request);
        return this.getResFromServer();
    }

    public Response completeSearch(String value) {
        Request request = new Request();
        request.setTitle(Title.completeSearch);
        SearchRequestDto searchDto = new SearchRequestDto();
        searchDto.setValue(value);
        request.setUserId(this.currentUserId);
        request.setData(searchDto);
        this.sendReqToServer(request);
        return this.getResFromServer();
    }

    public Response searchAlbums(String value) {
        Request request = new Request();
        request.setTitle(Title.searchAlbum);
        SearchRequestDto searchDto = new SearchRequestDto();
        searchDto.setValue(value);
        request.setUserId(this.currentUserId);
        request.setData(searchDto);
        this.sendReqToServer(request);
        return this.getResFromServer();
    }

    public Response searchArtists(String value) {
        Request request = new Request();
        request.setTitle(Title.searchArtist);
        SearchRequestDto searchDto = new SearchRequestDto();
        searchDto.setValue(value);
        request.setUserId(this.currentUserId);
        request.setData(searchDto);
        this.sendReqToServer(request);
        return this.getResFromServer();
    }

    public Response searchMusics(String value) {
        Request request = new Request();
        request.setTitle(Title.searchMusic);
        SearchRequestDto searchDto = new SearchRequestDto();
        searchDto.setValue(value);
        request.setUserId(this.currentUserId);
        request.setData(searchDto);
        this.sendReqToServer(request);
        return this.getResFromServer();
    }

    public Response searchUsers(String value) {
        Request request = new Request();
        request.setTitle(Title.searchUser);
        SearchUserDto searchUserDto = new SearchUserDto();
        searchUserDto.setUsername(value);
        request.setUserId(this.currentUserId);
        request.setData(searchUserDto);
        this.sendReqToServer(request);
        return this.getResFromServer();
    }

    public Response searchPlaylists(String value) {
        Request request = new Request();
        request.setTitle(Title.searchPlaylist);
        SearchRequestDto searchDto = new SearchRequestDto();
        searchDto.setValue(value);
        request.setUserId(this.currentUserId);
        request.setData(searchDto);
        this.sendReqToServer(request);
        return this.getResFromServer();
    }

    public Response findOneMusic(FindOneMusicDto dto) {
        Request request = new Request();
        request.setTitle(Title.findOneMusic);
        request.setUserId(this.currentUserId);
        request.setData(dto);
        this.sendReqToServer(request);
        return this.getResFromServer();
    }

    public Response findOneAlbum(FindOneAlbumDto dto) {
        Request request = new Request();
        request.setTitle(Title.findOneAlbum);
        request.setUserId(this.currentUserId);
        request.setData(dto);
        this.sendReqToServer(request);
        return this.getResFromServer();
    }

    public Response findOneArtist(FindOneArtistDto dto) {
        Request request = new Request();
        request.setTitle(Title.findOneArtist);
        request.setUserId(this.currentUserId);
        request.setData(dto);
        this.sendReqToServer(request);
        return this.getResFromServer();
    }

    public Response findOneGenre(FindOneGenreDto dto) {
        Request request = new Request();
        request.setTitle(Title.findOneGenre);
        request.setUserId(this.currentUserId);
        request.setData(dto);
        this.sendReqToServer(request);
        return this.getResFromServer();
    }

    public Response findOnePlaylist(FindOnePlaylistDto dto) {
        Request request = new Request();
        request.setTitle(Title.findOnePlaylist);
        request.setUserId(this.currentUserId);
        request.setData(dto);
        this.sendReqToServer(request);
        return this.getResFromServer();
    }

    public Response findAllGenres() {
        Request request = new Request();
        request.setTitle(Title.findAllGenres);
        request.setUserId(this.currentUserId);
        this.sendReqToServer(request);
        return this.getResFromServer();
    }

    public Response createPlaylist(CreatePlaylistDto dto) {
        Request request = new Request();
        request.setUserId(this.currentUserId);
        request.setTitle(Title.createPlaylist);
        request.setData(dto);
        this.sendReqToServer(request);
        return this.getResFromServer();
    }

    public Response findAlbumSongs(FindOneAlbumDto dto) {
        Request request = new Request();
        request.setUserId(this.currentUserId);
        request.setTitle(Title.findAlbumSongs);
        request.setData(dto);
        this.sendReqToServer(request);
        return this.getResFromServer();
    }

    public Response likeAlbum(LikeAlbumDto dto) {
        Request request = new Request();
        request.setUserId(this.currentUserId);
        request.setTitle(Title.likeAlbum);
        request.setData(dto);
        this.sendReqToServer(request);
        return this.getResFromServer();
    }

    public Response likeMusic(LikeMusicDto dto) {
        Request request = new Request();
        request.setUserId(this.currentUserId);
        request.setTitle(Title.likeMusic);
        request.setData(dto);
        this.sendReqToServer(request);
        return this.getResFromServer();
    }

    public Response dislikeMusic(DislikeMusicDto dto) {
        Request request = new Request();
        request.setUserId(this.currentUserId);
        request.setTitle(Title.dislikeMusic);
        request.setData(dto);
        this.sendReqToServer(request);
        return this.getResFromServer();
    }

    public Response addCommentToMusic(CommentEntity dto) {
        Request request = new Request();
        request.setUserId(this.currentUserId);
        request.setTitle(Title.addCommentOnMusic);
        request.setData(dto);
        this.sendReqToServer(request);
        return this.getResFromServer();
    }

    public Response addMusicToPlaylist(AddMusicToPlaylistDto dto) {
        Request request = new Request();
        request.setUserId(this.currentUserId);
        request.setTitle(Title.addMusicToPlaylist);
        request.setData(dto);
        this.sendReqToServer(request);
        return this.getResFromServer();
    }

    public Response changeMusicOrderInPlaylist(UpdateMusicTurnDto dto) {
        Request request = new Request();
        request.setUserId(this.currentUserId);
        request.setTitle(Title.changeMusicOrderInPlaylist);
        request.setData(dto);
        this.sendReqToServer(request);
        return this.getResFromServer();
    }

    public Response addPlaylist(AddPlaylistDto dto) {
        Request request = new Request();
        request.setUserId(this.currentUserId);
        request.setTitle(Title.addPlaylist);
        request.setData(dto);
        this.sendReqToServer(request);
        return this.getResFromServer();
    }

    public Response likePlaylist(LikePlaylistDto dto) {
        Request request = new Request();
        request.setUserId(this.currentUserId);
        request.setTitle(Title.likePlayList);
        request.setData(dto);
        this.sendReqToServer(request);
        return this.getResFromServer();
    }

    public Response logout() {
        Request request = new Request();
        request.setTitle(Title.logOut);
        request.setUserId(this.currentUserId);
        this.sendReqToServer(request);
        return this.getResFromServer();
    }

    public void exit() {
        Request request = new Request();
        request.setTitle(Title.exit);
        request.setUserId(this.currentUserId);
        this.sendReqToServer(request);
        this.closeConnection();
    }

    public void sendFile(File file){
        int bytes = 0;
        try {
            FileInputStream fileInputStream = new FileInputStream(file);

            this.dataOutputStream.writeLong(file.length());
            byte[] buffer = new byte[4 * 1024];
            while ((bytes = fileInputStream.read(buffer)) != -1) {
                this.dataOutputStream.write(buffer, 0, bytes);
                this.dataOutputStream.flush();
            }
            fileInputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String receiveFile(FileDto fileDto) {
        int bytes = 0;
        String filePath = "";
        try {
            File file = File.createTempFile(
                    fileDto.getName() + " - ",
                    "." + fileDto.getMemeType(),
                    new File(this.downloadDirectory)
            );
            filePath = file.getPath();
            FileOutputStream fileOutputStream = new FileOutputStream(file);

            long size = this.dataInputStream.readLong();
            byte[] buffer = new byte[4 * 1024];
            while (size > 0 && (bytes = this.dataInputStream.read(buffer, 0, (int)Math.min(buffer.length, size))) != -1) {
                fileOutputStream.write(buffer, 0, bytes);
                size -= bytes;
            }
            System.out.printf("%s file downloaded.%n", fileDto.getMemeType());
            fileOutputStream.close();
        } catch (IOException e){
            e.printStackTrace();
        }
        return filePath;
    }

    private void startConnection() {
        try {
            this.inputStream = this.socket.getInputStream();
            this.dataInputStream = new DataInputStream(this.inputStream);
            this.outputStream = this.socket.getOutputStream();
            this.dataOutputStream = new DataOutputStream(this.outputStream);
            this.getServerRSAPublicKey();
            this.sendAESKey();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void closeConnection() {
        try {
            this.dataInputStream.close();
            this.dataOutputStream.close();
            this.inputStream.close();
            this.outputStream.close();
            this.socket.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void getServerRSAPublicKey(){
        try {
            // Receive the server public key
            ObjectInputStream objectInputStream = new ObjectInputStream(this.inputStream);
            this.serverPubKey = (PublicKey) objectInputStream.readObject();
        } catch (IOException | ClassNotFoundException e){
            e.printStackTrace();
        }
    }

    private void sendAESKey(){
        UUID secretKey = UUID.randomUUID();
        String AES_SECRET_KEY = secretKey.toString().substring(0, 16);
        this.aesEncryption = new AESEncryption(AES_SECRET_KEY);
        // encrypt with RSA and send the AES key to server
        try {
            String cipherSecretKey = this.rsaEncryption.encrypt(AES_SECRET_KEY, this.serverPubKey);
            this.dataOutputStream.writeUTF(cipherSecretKey);
            this.dataOutputStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("AES key sent!");
    }

    private void sendReqToServer(Request request){
        try {
            String requestJSON = this.objectMapper.writeValueAsString(request);
            String cipherRequest = this.aesEncryption.encrypt(requestJSON);
            dataOutputStream.writeUTF(cipherRequest);
            dataOutputStream.flush();
        } catch (JsonProcessingException e) {
            System.out.println("A problem in parsing to JSON occurred.");
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Response getResFromServer(){
        Response response = null;
        try {
            String cipherResponse = this.dataInputStream.readUTF();
            String responseJSON = this.aesEncryption.decrypt(cipherResponse);
            response = this.objectMapper.readValue(responseJSON, Response.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return response;
    }

    private void makeDownloadsDirectory() {
        boolean success;
        File directory = new File(this.downloadDirectory);
        if (!directory.exists()) {
            System.out.println("Downloads directory not exists, creating now");
            success = directory.mkdir();
            if (success) {
                System.out.printf("Successfully created new directory : %s%n", this.downloadDirectory);
            } else {
                System.out.printf("Failed to create new directory: %s%n", this.downloadDirectory);
            }
        }
    }
}
