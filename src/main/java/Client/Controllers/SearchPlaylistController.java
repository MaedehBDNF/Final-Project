package Client.Controllers;

import Client.ClientManager;
import Client.LoadManager;
import Shared.Dto.Playlist.FindOnePlaylistDto;
import Shared.Entities.PlaylistEntity;
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
import java.util.ResourceBundle;

public class SearchPlaylistController implements Initializable {
    private ClientManager client;
    private LoadManager loader;
    private final ObjectMapper mapper = new ObjectMapper();
    private ArrayList<PlaylistEntity> playlists;
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

        if (this.playlists != null) {
            for (PlaylistEntity playlistEntity : this.playlists) {
                String buttonText = String.format("%-60s %-15s %-20s", playlistEntity.getTitle(), playlistEntity.getPopularity(), "Playlist");
                Button playlistButton = new Button(buttonText);
                playlistButton.setUserData(playlistEntity);
                playlistButton.setPrefHeight(30);
                playlistButton.setPrefWidth(1000);
                playlistButton.setOnAction(event -> {
                    PlaylistEntity playlist = (PlaylistEntity) playlistButton.getUserData();
                    this.loader.loadPlaylistPresentationPage(this.fullInfoPlaylist(playlist.getId()));
                });
                this.results.getChildren().add(playlistButton);
            }
        }
    }

    @FXML
    public void back() {
        this.stage = (Stage) this.back.getScene().getWindow();
        this.loader.loadMainPage(this.stage);
    }

    public void setClient(ClientManager client) {
        this.client = client;
        this.loader = new LoadManager(this.client);
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setPlaylists(ArrayList<PlaylistEntity> playlists) {
        this.playlists = playlists;
    }

    private PlaylistEntity fullInfoPlaylist(int playlistId) {
        FindOnePlaylistDto dto = new FindOnePlaylistDto();
        dto.setId(playlistId);
        Response response = this.client.findOnePlaylist(dto);
        return this.mapper.convertValue(response.getData(), PlaylistEntity.class);
    }
}
