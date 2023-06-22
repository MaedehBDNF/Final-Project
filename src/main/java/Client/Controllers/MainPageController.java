package Client.Controllers;

import Client.ClientManager;
import Client.LoadManager;
import Shared.Dto.File.FileDto;
import Shared.Dto.File.UploadDto;
import Shared.Dto.Search.SearchResponseDto;
import Shared.Entities.*;
import Shared.Enums.Status;
import Shared.Enums.UploadType;
import Shared.Response;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.Window;

public class MainPageController implements Initializable {
    private ClientManager client;
    private LoadManager loader;
    private UserEntity user;
    private final ObjectMapper mapper = new ObjectMapper();
    private File selectedFile;
    private Stage stage;

    @FXML
    private Circle profilePhoto;
    @FXML
    private Label username;
    @FXML
    private Button editPicture, createPlaylist, favoriteAlbums, friends, followings, playLists, logout;
    @FXML
    private TextField searchText, searchAlbumText, searchArtistText, searchMusicText, searchPlayListText, searchUserText;

    public MainPageController() {
    }

    public void setClient(ClientManager client) {
        this.client = client;
        this.loader = new LoadManager(this.client);
    }

    public void setUser(UserEntity user) {
        this.user = user;
    }

    public void initialize(URL location, ResourceBundle resources) {
        if (this.user.getProfilePicture() != null) {
            try {
                Image image = new Image(new FileInputStream(this.client.download(this.user.getProfilePicture())));
                this.profilePhoto.setFill(new ImagePattern(image));
            } catch (IOException var4) {
                var4.printStackTrace();
            }
        }

        this.username.setText(this.user.getUsername());

        this.searchText.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                this.search();
            }
        });

        this.searchAlbumText.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                this.searchAlbums();
            }
        });

        this.searchMusicText.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                this.searchMusics();
            }
        });

        this.searchArtistText.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                this.searchArtists();
            }
        });

        this.searchPlayListText.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                this.searchPlaylists();
            }
        });

        this.searchUserText.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                this.searchUsers();
            }
        });
    }

    @FXML
    public void search() {
        this.stage = (Stage) this.searchText.getScene().getWindow();
        Response response = this.client.completeSearch(this.searchText.getText());
        SearchResponseDto result = this.mapper.convertValue(response.getData(), SearchResponseDto.class);
        this.loader.loadMainCompleteSearchPage(this.stage, result, "Found Items");
    }

    @FXML
    public void searchAlbums() {
        this.stage = (Stage) this.searchText.getScene().getWindow();
        Response response = this.client.searchAlbums(this.searchAlbumText.getText());
        AlbumEntity[] albumsArr = this.mapper.convertValue(response.getData(), AlbumEntity[].class);
        ArrayList<AlbumEntity> albums = new ArrayList<>(Arrays.asList(albumsArr));
        this.loader.loadSearchPageForAlbums(this.stage, albums, "Found Albums");
    }

    @FXML
    public void searchArtists() {
        this.stage = (Stage) this.searchText.getScene().getWindow();
        Response response = this.client.searchArtists(this.searchArtistText.getText());
        ArtistEntity[] followingsArr = this.mapper.convertValue(response.getData(), ArtistEntity[].class);
        ArrayList<ArtistEntity> followings = new ArrayList<>(Arrays.asList(followingsArr));
        this.loader.loadSearchPageForArtists(this.stage, followings, "Found Artists");
    }

    @FXML
    public void searchMusics() {
        this.stage = (Stage) this.searchText.getScene().getWindow();
        Response response = this.client.searchMusics(this.searchMusicText.getText());
        MusicEntity[] musicsArr = this.mapper.convertValue(response.getData(), MusicEntity[].class);
        ArrayList<MusicEntity> musics = new ArrayList<>(Arrays.asList(musicsArr));
        this.loader.loadSearchPageForMusics(this.stage, musics, "Found Musics");
    }

    @FXML
    public void searchPlaylists() {
        this.stage = (Stage) this.searchText.getScene().getWindow();
        Response response = this.client.searchPlaylists(this.searchPlayListText.getText());
        PlaylistEntity[] playlistArr = this.mapper.convertValue(response.getData(), PlaylistEntity[].class);
        ArrayList<PlaylistEntity> playlists = new ArrayList<>(Arrays.asList(playlistArr));
        this.loader.loadSearchPageForPlaylists(this.stage, playlists, "Found Playlists");
    }

    @FXML
    public void searchUsers() {
        this.stage = (Stage) this.searchText.getScene().getWindow();
        Response response = this.client.searchUsers(this.searchUserText.getText());
        UserEntity[] friendsArr = this.mapper.convertValue(response.getData(), UserEntity[].class);
        ArrayList<UserEntity> friends = new ArrayList<>(Arrays.asList(friendsArr));
        this.loader.loadSearchPageForUsers(this.stage, friends, "Found Users");
    }

    @FXML
    public void createPlayList() {
        this.stage = (Stage)this.logout.getScene().getWindow();
        this.loader.loadCreateNewPlaylistPage(this.stage);
    }

    @FXML
    public void favoriteAlbums() {
        this.stage = (Stage)this.logout.getScene().getWindow();
        Response response = this.client.getUserLikedAlbums();
        AlbumEntity[] albumsArr = this.mapper.convertValue(response.getData(), AlbumEntity[].class);
        ArrayList<AlbumEntity> albums = new ArrayList<>(Arrays.asList(albumsArr));
        this.loader.loadSearchPageForAlbums(this.stage, albums, "Your Favorite Albums");
    }

    @FXML
    private void friends() {
        this.stage = (Stage)this.logout.getScene().getWindow();
        Response response = this.client.getUserFriends(this.client.getCurrentUserId());
        UserEntity[] friendsArr = this.mapper.convertValue(response.getData(), UserEntity[].class);
        ArrayList<UserEntity> friends = new ArrayList<>(Arrays.asList(friendsArr));
        this.loader.loadSearchPageForUsers(this.stage, friends, "Your Friends");
    }

    @FXML
    private void followings() {
        this.stage = (Stage)this.logout.getScene().getWindow();
        Response response = this.client.getUserFollowings(this.client.getCurrentUserId());
        ArtistEntity[] followingsArr = this.mapper.convertValue(response.getData(), ArtistEntity[].class);
        ArrayList<ArtistEntity> followings = new ArrayList<>(Arrays.asList(followingsArr));
        this.loader.loadSearchPageForArtists(this.stage, followings, "Your Followings");
    }

    @FXML
    private void playLists() {
        this.stage = (Stage)this.logout.getScene().getWindow();
        Response response = this.client.getUserPlaylists(this.client.getCurrentUserId());
        PlaylistEntity[] playlistArr = this.mapper.convertValue(response.getData(), PlaylistEntity[].class);
        ArrayList<PlaylistEntity> playlists = new ArrayList<>(Arrays.asList(playlistArr));
        this.loader.loadSearchPageForPlaylists(this.stage, playlists, "Your Playlists");
    }

    @FXML
    private void logout() {
        this.stage = (Stage)this.logout.getScene().getWindow();
        Response response = this.client.logout();
        if (response.getStatus().equals(Status.successful)) {
            this.loader.loadStartPage(this.stage);
        }
    }

    @FXML
    public void editPicture() {
        this.stage = (Stage)this.logout.getScene().getWindow();
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("JPEG or PNG Files", new String[]{"*.jpg", "*.jpeg", "*.png"}));
        File newProfile = fileChooser.showOpenDialog((Window)null);
        if (newProfile != null) {
            this.selectedFile = this.client.copyFile(UploadType.userProfilePicture, newProfile);
        }
        try {
            Image image = new Image(new FileInputStream(this.selectedFile.getPath()));
            this.profilePhoto.setFill(new ImagePattern(image));
        } catch (IOException var4) {
            var4.printStackTrace();
        }
        this.uploadProfilePicture(this.user.getId());
    }

    private FileDto uploadProfilePicture(int userId) {
        UploadDto uploadDto = new UploadDto();
        String fileName = this.selectedFile.getName();
        uploadDto.setName(fileName.substring(0, fileName.lastIndexOf(".")));
        uploadDto.setMemeType(fileName.substring(fileName.lastIndexOf(".") + 1));
        uploadDto.setReferenceId(userId);
        uploadDto.setUploadType(UploadType.userProfilePicture);
        Response response = this.client.uploadPicture(uploadDto, this.selectedFile);
        return this.mapper.convertValue(response.getData(), FileDto.class);
    }
}
