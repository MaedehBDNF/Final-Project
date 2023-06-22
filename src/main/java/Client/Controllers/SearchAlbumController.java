package Client.Controllers;

import Client.ClientManager;
import Client.LoadManager;
import Shared.Dto.Album.FindOneAlbumDto;
import Shared.Entities.AlbumEntity;
import Shared.Entities.MusicEntity;
import Shared.Response;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
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

public class SearchAlbumController implements Initializable {
    private ClientManager client;
    private LoadManager loader;
    private final ObjectMapper mapper = new ObjectMapper();
    private ArrayList<AlbumEntity> albums;
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
        this.mapper.registerModule(new JavaTimeModule());
        this.pageTitle.setText(this.title);

        if (this.albums != null) {
            for (AlbumEntity albumEntity : this.albums) {
                String buttonText = String.format("%-60s %-20s", albumEntity.getTitle(), "Album");
                Button albumButton = new Button(buttonText);
                albumButton.setUserData(albumEntity);
                albumButton.setPrefHeight(30);
                albumButton.setPrefWidth(1000);
                albumButton.setOnAction(event -> {
                    AlbumEntity album = (AlbumEntity) albumButton.getUserData();
                    this.loader.loadAlbumPresentationPage(this.fullInfoAlbum(album.getId()), this.findAlbumSongs(album.getId()));
                });
                this.results.getChildren().add(albumButton);
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

    public void setAlbums(ArrayList<AlbumEntity> albums) {
        this.albums = albums;
    }

    private ArrayList<MusicEntity> findAlbumSongs(int albumId) {
        FindOneAlbumDto dto = new FindOneAlbumDto();
        dto.setId(albumId);
        Response response = this.client.findAlbumSongs(dto);
        MusicEntity[] musicsArr = this.mapper.convertValue(response.getData(), MusicEntity[].class);
        return new ArrayList<>(Arrays.asList(musicsArr));
    }

    private AlbumEntity fullInfoAlbum(int albumId) {
        FindOneAlbumDto dto = new FindOneAlbumDto();
        dto.setId(albumId);
        Response response = this.client.findOneAlbum(dto);
        return this.mapper.convertValue(response.getData(), AlbumEntity.class);
    }
}
