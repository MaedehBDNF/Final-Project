package Client.Controllers;

import Client.ClientManager;
import Client.LoadManager;
import Shared.Dto.Playlist.FindOnePlaylistDto;
import Shared.Dto.User.FollowUserDto;
import Shared.Entities.ArtistEntity;
import Shared.Entities.PlaylistEntity;
import Shared.Entities.UserEntity;
import Shared.Enums.Status;
import Shared.Response;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.layout.VBox;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.ResourceBundle;

public class UserPresentationController implements Initializable {
    private ClientManager client;
    private LoadManager loader;
    private final ObjectMapper mapper = new ObjectMapper();
    private final String projectDirectory = System.getProperty("user.dir");
    private UserEntity user;
    private ArrayList<PlaylistEntity> pls;

    @FXML
    private Button addToFriends, friends, followings;
    @FXML
    private Circle userProfile;
    @FXML
    private Label username, message;
    @FXML
    private VBox playlists;

    public UserPresentationController() {
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.username.setText(this.user.getUsername());

        if (this.user.getProfilePicture() != null && this.user.getProfilePicture().getId() != 0) {
            try {
                Image image = new Image(new FileInputStream(this.client.download(this.user.getProfilePicture())));
                this.userProfile.setFill(new ImagePattern(image));
            } catch (IOException var4) {
                var4.printStackTrace();
            }
        } else {
            try {
                Image image = new Image(new FileInputStream(this.projectDirectory + "\\src\\main\\resources\\Images\\DefaultProfilePhoto.jpg"));
                this.userProfile.setFill(new ImagePattern(image));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (this.pls != null && !this.pls.isEmpty()) {
            for (PlaylistEntity playlistEntity : this.pls) {
                String buttonText = String.format("%-60s %-15s %-20s", playlistEntity.getTitle(), playlistEntity.getPopularity(), "Playlist");
                Button playlistButton = new Button(buttonText);
                playlistButton.setUserData(playlistEntity);
                playlistButton.setPrefHeight(30);
                playlistButton.setPrefWidth(1000);
                playlistButton.setOnAction(event -> {
                    PlaylistEntity playlist = (PlaylistEntity) playlistButton.getUserData();
                    this.loader.loadPlaylistPresentationPage(this.fullInfoPlaylist(playlist.getId()));
                });
                this.playlists.getChildren().add(playlistButton);
            }
        }
        if (this.doesUserFollowedUser()) {
            this.addToFriends.setText("added to friends");
        }
    }

    @FXML
    private void friends() {
        Response response = this.client.getUserFriends(this.user.getId());
        UserEntity[] friendsArr = this.mapper.convertValue(response.getData(), UserEntity[].class);
        ArrayList<UserEntity> friends = new ArrayList<>(Arrays.asList(friendsArr));
        this.loader.loadFriendsPresentationPage(friends, "Friends");
    }

    @FXML
    private void followings() {
        Response response = this.client.getUserFollowings(this.user.getId());
        ArtistEntity[] followingsArr = this.mapper.convertValue(response.getData(), ArtistEntity[].class);
        ArrayList<ArtistEntity> followings = new ArrayList<>(Arrays.asList(followingsArr));
        this.loader.loadFollowingsPresentationPage(followings, "Followings");
    }

    @FXML
    private void addToFriends() {
        FollowUserDto dto = new FollowUserDto();
        dto.setFriendId(this.user.getId());
        Response response = this.client.followUser(dto);
        if (response.getStatus().equals(Status.successful)) {
            this.addToFriends.setText("added to friends");
        } else {
            this.message.setText("Sorry something went wrong!");
        }
        this.message.setVisible(true);
    }

    public void setClient(ClientManager client) {
        this.client = client;
        this.loader = new LoadManager(this.client);
    }

    public void setUser(UserEntity user) {
        this.user = user;
    }

    public void setPlaylists(ArrayList<PlaylistEntity> playlists) {
        this.pls = playlists;
    }

    private boolean doesUserFollowedUser() {
        Response response = this.client.doesUserFollowedUser(this.user.getId());
        return this.mapper.convertValue(response.getData(), boolean.class);
    }

    private PlaylistEntity fullInfoPlaylist(int playlistId) {
        FindOnePlaylistDto dto = new FindOnePlaylistDto();
        dto.setId(playlistId);
        Response response = this.client.findOnePlaylist(dto);
        return this.mapper.convertValue(response.getData(), PlaylistEntity.class);
    }
}