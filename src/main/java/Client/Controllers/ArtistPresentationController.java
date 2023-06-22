package Client.Controllers;

import Client.ClientManager;
import Client.LoadManager;
import Shared.Dto.User.FollowArtistDto;
import Shared.Entities.AlbumEntity;
import Shared.Entities.ArtistEntity;
import Shared.Entities.MusicEntity;
import Shared.Enums.Status;
import Shared.Response;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.layout.VBox;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class ArtistPresentationController implements Initializable {
    private ClientManager client;
    private LoadManager loader;
    private final ObjectMapper mapper = new ObjectMapper();
    private ArtistEntity artist;
    private String sLinks;

    @FXML
    private Label artistName, genre, message;
    @FXML
    private TextArea biography, socialLinks;
    @FXML
    private Circle artistProfile;
    @FXML
    private Button follow;
    @FXML
    private VBox albumsAndTracks;


    public void initialize(URL location, ResourceBundle resources) {
        this.artistName.setText(this.artist.getName());
        this.genre.setText(this.artist.getGenre().getName());
        this.biography.setText(this.artist.getBiography());
        this.setSocialLinks();
        this.socialLinks.setText(this.sLinks);

        if (this.artist.getProfilePicture() != null) {
            try {
                Image image = new Image(new FileInputStream(this.client.download(this.artist.getProfilePicture())));
                this.artistProfile.setFill(new ImagePattern(image));
            } catch (IOException var4) {
                var4.printStackTrace();
            }
        }

        ArrayList<MusicEntity> tracks = this.artist.getTracks();
        if (tracks != null) {
            for (MusicEntity musicEntity : tracks) {
                String buttonText = String.format("%-60s %-15s %-20s", musicEntity.getTitle(), musicEntity.getPopularity(), "Track");
                Button musicButton = new Button(buttonText);
                musicButton.setUserData(musicEntity);
                musicButton.setPrefHeight(30);
                musicButton.setPrefWidth(1000);
                musicButton.setOnAction(event -> {
                    // todo 1. find one 2. load page
                    System.out.println(musicButton.getText());
                });
                this.albumsAndTracks.getChildren().add(musicButton);
            }
        }

        ArrayList<AlbumEntity> albums = this.artist.getAlbums();
        if (albums != null) {
            for (AlbumEntity albumEntity : albums) {
                String buttonText = String.format("%-60s %-20s", albumEntity.getTitle(), "Album");
                Button albumButton = new Button(buttonText);
                albumButton.setUserData(albumEntity);
                albumButton.setPrefHeight(30);
                albumButton.setPrefWidth(1000);
                albumButton.setOnAction(event -> {
                    // todo 1. find one 2. load page
                    System.out.println(albumButton.getText());
                });
                this.albumsAndTracks.getChildren().add(albumButton);
            }
        }
    }

    @FXML
    private void follow() {
        FollowArtistDto dto = new FollowArtistDto();
        dto.setArtistId(this.artist.getId());
        Response response = this.client.followArtist(dto);
        if (response.getStatus().equals(Status.successful)) {
            this.follow.setText("Followed");
        } else {
            this.message.setText("Sorry something went wrong!");
            this.message.setVisible(true);
        }
    }

    public void setClient(ClientManager client) {
        this.client = client;
        this.loader = new LoadManager(this.client);
    }

    public void setArtist(ArtistEntity artist) {
        this.artist = artist;
    }

    private void setSocialLinks() {
        for (String link: this.artist.getSocialMediaLinks()) {
            this.sLinks += link;
            this.sLinks += "\n";
        }
    }
}
