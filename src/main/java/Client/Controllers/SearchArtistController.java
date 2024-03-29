package Client.Controllers;

import Client.ClientManager;
import Client.LoadManager;
import Shared.Dto.Artist.FindOneArtistDto;
import Shared.Entities.ArtistEntity;
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

public class SearchArtistController implements Initializable {
    private ClientManager client;
    private LoadManager loader;
    private final ObjectMapper mapper = new ObjectMapper();
    private ArrayList<ArtistEntity> artists;
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

        if (this.artists != null) {
            for (ArtistEntity artistEntity : this.artists) {
                String buttonText = String.format("%-60s %-20s", artistEntity.getName(), "Artist");
                Button artistButton = new Button(buttonText);
                artistButton.setUserData(artistEntity);
                artistButton.setPrefHeight(30);
                artistButton.setPrefWidth(1000);
                artistButton.setOnAction(event -> {
                    ArtistEntity artist = (ArtistEntity) artistButton.getUserData();
                    this.loader.loadArtistPresentationPage(this.fullInfoOfArtist(artist.getId()));
                });
                this.results.getChildren().add(artistButton);
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

    public void setArtists(ArrayList<ArtistEntity> artists) {
        this.artists = artists;
    }

    private ArtistEntity fullInfoOfArtist(int artistId) {
        FindOneArtistDto dto = new FindOneArtistDto();
        dto.setId(artistId);
        Response response = this.client.findOneArtist(dto);
        return this.mapper.convertValue(response.getData(), ArtistEntity.class);
    }
}
