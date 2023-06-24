
package Client.Controllers;

import Client.ClientManager;
import Client.LoadManager;
import Shared.Dto.File.FileDto;
import Shared.Dto.File.UploadDto;
import Shared.Dto.Music.FindOneMusicDto;
import Shared.Dto.Playlist.AddPlaylistDto;
import Shared.Dto.Playlist.LikePlaylistDto;
import Shared.Dto.Playlist.UpdateMusicTurnDto;
import Shared.Entities.MusicEntity;
import Shared.Entities.MusicPlaylistEntity;
import Shared.Entities.PlaylistEntity;
import Shared.Enums.Status;
import Shared.Enums.UploadType;
import Shared.Response;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;
import javafx.stage.FileChooser;
import javafx.stage.Window;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;


public class PlaylistPresentationController implements Initializable {
    private ClientManager client;
    private LoadManager loader;
    private final ObjectMapper mapper = new ObjectMapper();
    private PlaylistEntity playlist;
    private final String projectDirectory = System.getProperty("user.dir");
    private File selectedFile;


    @FXML
    private VBox musics;
    @FXML
    private Circle playlistCover;
    @FXML
    private Button like, play, editCover, swap, addToPlaylists;
    @FXML
    private ImageView likeImage;
    @FXML
    private TextField numOfMusic1, numOfMusic2;
    @FXML
    private TextArea description;
    @FXML
    private Label playlistName, creator, popularity, swapError, likeError, addPlsError, playError, swapLabel, withLabel;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.mapper.registerModule(new JavaTimeModule());

        this.description.setEditable(false);

        if (this.playlist.getCreator().getId() == this.client.getCurrentUserId()) {
            this.editCover.setVisible(true);
            this.swap.setVisible(true);
            this.swapLabel.setVisible(true);
            this.withLabel.setVisible(true);
            this.numOfMusic1.setVisible(true);
            this.numOfMusic2.setVisible(true);
        }
        this.playlistName.setText(this.playlist.getTitle());
        this.creator.setText(this.playlist.getCreator().getUsername());
        this.popularity.setText(Integer.toString(this.playlist.getPopularity()));
        if (this.playlist.getDescription() != null) {
            this.description.setText(this.playlist.getDescription());
        }
        if (this.playlist.getCover() != null && this.playlist.getCover().getId() != 0) {
            try {
                Image image = new Image(new FileInputStream(this.client.download(this.playlist.getCover())));
                this.playlistCover.setFill(new ImagePattern(image));
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            try {
                Image image = new Image(new FileInputStream(this.projectDirectory + "\\src\\main\\resources\\Images\\DefaultPlaylistCover.png"));
                this.playlistCover.setFill(new ImagePattern(image));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        ArrayList<MusicPlaylistEntity> musics = this.playlist.getTracks();
        if (musics != null) {
            this.fillMusicsBox();
        }
        if (this.doesUserLikedPlaylist()) {
            try {
                Image image = new Image(new FileInputStream(System.getProperty("user.dir") + "\\src\\main\\resources\\Images\\likeRed.png"));
                this.likeImage.setImage(image);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
        if (this.doesUserAddedPlaylist()) {
            this.addToPlaylists.setText("added to playlists");
        }
    }

    @FXML
    private void like() {
        this.invisibleErrors();
        LikePlaylistDto likePlaylistDto = new LikePlaylistDto();
        likePlaylistDto.setId(this.playlist.getId());
        Response response = this.client.likePlaylist(likePlaylistDto);
        if (response.getStatus().equals(Status.successful)) {
            try {
                Image image = new Image(new FileInputStream(System.getProperty("user.dir") + "\\src\\main\\resources\\Images\\likeRed.png"));
                this.likeImage.setImage(image);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        } else {
            this.likeError.setVisible(true);
        }
    }

    @FXML
    private void play() {
        this.invisibleErrors();
        if (!this.playlist.getTracks().isEmpty()) {
            this.loader.loadMusicPresentationPage(0, (ArrayList<MusicEntity>) ((ArrayList<?>) this.playlist.getTracks()), true);
        } else {
            this.playError.setVisible(true);
        }
    }

    @FXML
    private void addToPlaylists() {
        this.invisibleErrors();
        if (!this.doesUserAddedPlaylist()) {
            AddPlaylistDto dto = new AddPlaylistDto();
            dto.setId(this.playlist.getId());
            dto.setUserId(this.client.getCurrentUserId());
            Response response = this.client.addPlaylist(dto);
            if (response.getStatus().equals(Status.successful)) {
                this.addToPlaylists.setText("added to playlists");
            } else {
                this.addPlsError.setVisible(true);
            }
        }
    }

    @FXML
    private void swap() {
        this.invisibleErrors();
        int music1 = 0;
        int music2 = 0;
        ArrayList<MusicPlaylistEntity> tracks = this.playlist.getTracks();
        int numOfMusics = tracks.size();
        try {
            music1 = Integer.parseInt(this.numOfMusic1.getText()) - 1;
            music2 = Integer.parseInt(this.numOfMusic2.getText()) - 1;
        } catch (Exception e) {
            this.swapError.setText("Wrong input!");
            this.swapError.setVisible(true);
            return;
        }
        if (music1 < 0 || music1 >= numOfMusics || music2 < 0 || music2 >= numOfMusics) {
            this.swapError.setText("Out of range numbers!");
            this.swapError.setVisible(true);
            return;
        }
        UpdateMusicTurnDto dto = new UpdateMusicTurnDto();
        dto.setPlaylistId(this.playlist.getId());
        dto.setMusicId(tracks.get(music1).getId());
        if (music1 < music2) {
            if (music2 < numOfMusics - 1) {
                double music1NewTurn = (tracks.get(music2).getTurn() + tracks.get(music2 + 1).getTurn()) / 2;
                dto.setTurn(music1NewTurn);
                this.playlist.setTracks(this.changeTurn(dto, tracks, music1, music2));
            } else if (music2 == numOfMusics - 1) {
                double music1NewTurn = Math.ceil(tracks.get(music2).getTurn() + 1);
                dto.setTurn(music1NewTurn);
                this.playlist.setTracks(this.changeTurn(dto, tracks, music1, music2));
            }
        } else if (music2 < music1) {
            if (music2 > 0) {
                double music1NewTurn = (tracks.get(music2).getTurn() + tracks.get(music2 - 1).getTurn()) / 2;
                dto.setTurn(music1NewTurn);
                this.playlist.setTracks(this.changeTurn(dto, tracks, music1, music2));
            } else {
                double music1NewTurn = tracks.get(music2).getTurn() / 2;
                dto.setTurn(music1NewTurn);
                this.playlist.setTracks(this.changeTurn(dto, tracks, music1, music2));
            }
        }

        this.fillMusicsBox();
    }

    @FXML
    public void editCover() {
        this.invisibleErrors();
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("JPEG or PNG Files", new String[]{"*.jpg", "*.jpeg", "*.png"}));
        File newProfile = fileChooser.showOpenDialog((Window)null);
        if (newProfile == null) {
            return;
        }
        this.selectedFile = this.client.copyFile(UploadType.playlistCover, newProfile);
        try {
            Image image = new Image(new FileInputStream(this.selectedFile.getPath()));
            this.playlistCover.setFill(new ImagePattern(image));
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.playlist.setCover(this.uploadCover(this.playlist.getId()));
    }

    public void setClient(ClientManager client) {
        this.client = client;
        this.loader = new LoadManager(this.client);
    }

    public void setPlaylist(PlaylistEntity playlist) {
        this.playlist = playlist;
    }

    private boolean doesUserLikedPlaylist() {
        Response response = this.client.doesUserLikedPlaylist(this.playlist.getId());
        return this.mapper.convertValue(response.getData(), boolean.class);
    }

    private boolean doesUserAddedPlaylist() {
        Response response = this.client.doesUserAddedPlaylist(this.playlist.getId());
        return this.mapper.convertValue(response.getData(), boolean.class);
    }

    private FileDto uploadCover(int playlistId) {
        UploadDto uploadDto = new UploadDto();
        String fileName = this.selectedFile.getName();
        uploadDto.setName(fileName.substring(0, fileName.lastIndexOf(".")));
        uploadDto.setMemeType(fileName.substring(fileName.lastIndexOf(".") + 1));
        uploadDto.setReferenceId(playlistId);
        uploadDto.setUploadType(UploadType.playlistCover);
        Response response = this.client.uploadPicture(uploadDto, this.selectedFile);
        return this.mapper.convertValue(response.getData(), FileDto.class);
    }

    private void invisibleErrors() {
        this.swapError.setVisible(false);
        this.likeError.setVisible(false);
        this.addPlsError.setVisible(false);
    }

    private ArrayList<MusicPlaylistEntity> changeTurn(UpdateMusicTurnDto dto, ArrayList<MusicPlaylistEntity> tracks, int music1, int music2) {
        if (this.client.changeMusicOrderInPlaylist(dto).getStatus().equals(Status.successful)) {
            MusicPlaylistEntity m1 = tracks.get(music1);
            tracks.remove(m1);
            tracks.add(music2, m1);
        } else {
            this.swapError.setText("Something went wrong!");
            this.swapError.setVisible(true);
        }
        return tracks;
    }

    private void fillMusicsBox() {
        int number = 1;
        this.musics.getChildren().clear();
        this.numOfMusic1.clear();
        this.numOfMusic2.clear();
        int i = 0;
        for (MusicPlaylistEntity music : this.playlist.getTracks()) {
            String buttonText = String.format("%-10s %-60s %-15s %-20s", number + ")", music.getTitle(), music.getPopularity(), "Track");
            Button musicButton = new Button(buttonText);
            musicButton.setId(Integer.toString(i));
            musicButton.setPrefHeight(30);
            musicButton.setPrefWidth(1000);
            musicButton.setOnAction(event -> {
                this.loader.loadMusicPresentationPage(Integer.parseInt(musicButton.getId()), (ArrayList<MusicEntity>) ((ArrayList<?>) this.playlist.getTracks()), false);
            });
            this.musics.getChildren().add(musicButton);
            number++;
            i++;
        }
    }
}