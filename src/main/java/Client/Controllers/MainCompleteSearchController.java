package Client.Controllers;

import Client.ClientManager;
import Client.LoadManager;
import Shared.Dto.Search.SearchResponseDto;
import Shared.Dto.User.FindOneUserDto;
import Shared.Entities.*;
import Shared.Response;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.ResourceBundle;

public class MainCompleteSearchController implements Initializable {
    private ClientManager client;
    private LoadManager loader;
    private final ObjectMapper mapper = new ObjectMapper();
    private SearchResponseDto searchResponseDto;
    private Stage stage;
    private String title;

    @FXML
    private Label pageTitle;
    @FXML
    private Button back;
    @FXML
    private VBox results;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.pageTitle.setText(this.title);

        if (this.searchResponseDto != null) {
            ArrayList<MusicEntity> musics = this.searchResponseDto.getMusics();
            if (musics != null) {
                for (MusicEntity musicEntity : musics) {
                    String buttonText = String.format("%-60s %-15s %-20s", musicEntity.getTitle(), musicEntity.getPopularity(), "Track");
                    Button musicButton = new Button(buttonText);
                    musicButton.setUserData(musicEntity);
                    musicButton.setPrefHeight(30);
                    musicButton.setPrefWidth(1000);
                    musicButton.setOnAction(event -> {
                        // todo 1. find one 2. load page
                        System.out.println(musicButton.getText());
                    });
                    this.results.getChildren().add(musicButton);
                }
            }

            ArrayList<AlbumEntity> albums = this.searchResponseDto.getAlbums();
            if (albums != null) {
                for (AlbumEntity albumEntity : albums) {
                    String buttonText = String.format("%-60s %-20s", albumEntity.getTitle(), "Album");
                    Button albumButton = new Button(buttonText);
                    albumButton.setUserData(albumEntity);
                    albumButton.setPrefHeight(30);
                    albumButton.setPrefWidth(1000);
                    albumButton.setOnAction(event -> {
                        System.out.println(albumButton.getText());
                    });
                    this.results.getChildren().add(albumButton);
                }
            }

            ArrayList<PlaylistEntity> playlists = this.searchResponseDto.getPlaylists();
            if (playlists != null) {
                for (PlaylistEntity playlistEntity : playlists) {
                    String buttonText = String.format("%-60s %-15s %-20s", playlistEntity.getTitle(), playlistEntity.getPopularity(), "Playlist");
                    Button playlistButton = new Button(buttonText);
                    playlistButton.setUserData(playlistEntity);
                    playlistButton.setPrefHeight(30);
                    playlistButton.setPrefWidth(1000);
                    playlistButton.setOnAction(event -> {
                        System.out.println(playlistButton.getText());
                    });
                    this.results.getChildren().add(playlistButton);
                }
            }

            ArrayList<ArtistEntity> artists = this.searchResponseDto.getArtists();
            if (artists != null) {
                for (ArtistEntity artistEntity : artists) {
                    String buttonText = String.format("%-60s %-20s", artistEntity.getName(), "Artist");
                    Button artistButton = new Button(buttonText);
                    artistButton.setUserData(artistEntity);
                    artistButton.setPrefHeight(30);
                    artistButton.setPrefWidth(1000);
                    artistButton.setOnAction(event -> {
                        System.out.println(artistButton.getText());
                    });
                    this.results.getChildren().add(artistButton);
                }
            }

            ArrayList<UserEntity> users = this.searchResponseDto.getUsers();
            if (users != null) {
                for (UserEntity userEntity : users) {
                    String buttonText = String.format("%-60s %-20s", userEntity.getUsername(), "User");
                    Button userButton = new Button(buttonText);
                    userButton.setUserData(userEntity);
                    userButton.setPrefHeight(30);
                    userButton.setPrefWidth(1000);
                    userButton.setOnAction(event -> {
                        UserEntity user = (UserEntity) userButton.getUserData();
                        this.loader.loadUserPresentationPage(this.fullInfoOfUser(user.getId()), this.getUserPlaylists(user.getId()));
                    });
                    this.results.getChildren().add(userButton);
                }
            }
        }
    }

    @FXML
    public void back() {
        this.stage = (Stage) this.back.getScene().getWindow();
        this.loader.loadMainPage(this.stage, this.client.getCurrentUser());
    }

    public void setClient(ClientManager client) {
        this.client = client;
        this.loader = new LoadManager(this.client);
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setResults(SearchResponseDto dto) {
        this.searchResponseDto = dto;
    }

    private ArrayList<PlaylistEntity> getUserPlaylists(int userId) {
        Response response = this.client.getUserPlaylists(userId);
        PlaylistEntity[] playlistArr = this.mapper.convertValue(response.getData(), PlaylistEntity[].class);
        return new ArrayList<PlaylistEntity>(Arrays.asList(playlistArr));
    }

    private UserEntity fullInfoOfUser(int id) {
        FindOneUserDto dto = new FindOneUserDto();
        dto.setUserId(id);
        Response response = this.client.findOneUser(dto);
        return this.mapper.convertValue(response.getData(), UserEntity.class);
    }
}
