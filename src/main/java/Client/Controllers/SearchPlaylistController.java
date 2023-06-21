package Client.Controllers;

import Client.ClientManager;
import Client.LoadManager;
import Shared.Entities.PlaylistEntity;
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
                    System.out.println(playlistButton.getText());
                });
                this.results.getChildren().add(playlistButton);
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

    public void setPlaylists(ArrayList<PlaylistEntity> playlists) {
        this.playlists = playlists;
    }
}
