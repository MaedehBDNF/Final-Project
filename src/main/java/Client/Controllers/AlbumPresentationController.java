package Client.Controllers;

import Client.ClientManager;
import Client.LoadManager;
import Shared.Dto.Album.FindOneAlbumDto;
import Shared.Dto.Album.LikeAlbumDto;
import Shared.Entities.AlbumEntity;
import Shared.Entities.MusicEntity;
import Shared.Enums.Status;
import Shared.Response;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class AlbumPresentationController implements Initializable {
    private ClientManager client;
    private LoadManager loader;
    private final ObjectMapper mapper = new ObjectMapper();
    private AlbumEntity album;
    private ArrayList<MusicEntity> albumMusics;

    @FXML
    private Circle albumCover;
    @FXML
    private Button like, play;
    @FXML
    private ImageView likeImage;
    @FXML
    private VBox musics;
    @FXML
    private Label albumName, releaseDate, popularity, genre, artist, message;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.mapper.registerModule(new JavaTimeModule());

        this.albumName.setText(this.album.getTitle());
        this.artist.setText(this.album.getArtist().getName());
        this.genre.setText(this.album.getGenre().getName());
        this.releaseDate.setText(this.album.getReleaseDate().toString());
        this.popularity.setText(Integer.toString(this.album.getPopularity()));

        if (this.album.getCover() != null) {
            try {
                AlbumEntity album = this.fullInfoAlbum(this.album.getId());
                Image image = new Image(new FileInputStream(this.client.download(album.getCover())));
                this.albumCover.setFill(new ImagePattern(image));
            } catch (IOException var4) {
                var4.printStackTrace();
            }
        }
        if (this.albumMusics != null) {
            for (MusicEntity musicEntity : this.albumMusics) {
                String buttonText = String.format("%-60s %-15s %-20s", musicEntity.getTitle(), musicEntity.getPopularity(), "Track");
                javafx.scene.control.Button musicButton = new javafx.scene.control.Button(buttonText);
                musicButton.setUserData(musicEntity);
                musicButton.setPrefHeight(30);
                musicButton.setPrefWidth(1000);
                musicButton.setOnAction(event -> {
                    // todo 1. find one 2. load page
                    System.out.println(musicButton.getText());
                });
                this.musics.getChildren().add(musicButton);
            }
        }
        if (this.doesUserLikedAlbum()) {
            try {
                Image image = new Image(new FileInputStream(System.getProperty("user.dir") + "\\src\\main\\resources\\Images\\likeRed.png"));
                this.likeImage.setImage(image);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    @FXML
    private void like() {
        LikeAlbumDto likeAlbumDto = new LikeAlbumDto();
        likeAlbumDto.setAlbumId(this.album.getId());
        likeAlbumDto.setUserid(this.client.getCurrentUserId());
        Response response = this.client.likeAlbum(likeAlbumDto);
        if (response.getStatus().equals(Status.successful)) {
            try {
                Image image = new Image(new FileInputStream(System.getProperty("user.dir") + "\\src\\main\\resources\\Images\\likeRed.png"));
                this.likeImage.setImage(image);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        } else {
            this.message.setVisible(true);
        }
    }

    @FXML
    private void play() {
        // todo
    }

    public void setClient(ClientManager client) {
        this.client = client;
        this.loader = new LoadManager(this.client);
    }

    public void setAlbum(AlbumEntity album) {
        this.album = album;
    }

    public void setAlbumMusics(ArrayList<MusicEntity> albumMusics) {
        this.albumMusics = albumMusics;
    }

    private AlbumEntity fullInfoAlbum(int albumId) {
        FindOneAlbumDto dto = new FindOneAlbumDto();
        dto.setId(albumId);
        Response response = this.client.findOneAlbum(dto);
        return this.mapper.convertValue(response.getData(), AlbumEntity.class);
    }

    private boolean doesUserLikedAlbum() {
        Response response = this.client.doesUserLikedAlbum(this.album.getId());
        return this.mapper.convertValue(response.getData(), boolean.class);
    }
}