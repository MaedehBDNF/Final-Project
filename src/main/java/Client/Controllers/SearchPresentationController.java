package Client.Controllers;

import Client.ClientManager;
import Client.LoadManager;
import Shared.Dto.Search.SearchResponseDto;
import Shared.Entities.*;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class SearchPresentationController implements Initializable {
    private ClientManager client;
    private LoadManager loader;
    private SearchResponseDto searchResponseDto;
    private ArrayList<AlbumEntity> albums;
    private ArrayList<UserEntity> friends;
    private ArrayList<ArtistEntity> followings;
    private ArrayList<PlaylistEntity> playlists;
    private Stage stage;

    @FXML
    private Label pageTitle;
    @FXML
    private Button back;
    @FXML
    private VBox results;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // check for presenting complete search
        if (this.searchResponseDto != null) {
            this.pageTitle.setText("Found items");
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
                    results.getChildren().add(musicButton);
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
                    results.getChildren().add(albumButton);
                }
            }

            ArrayList<PlaylistEntity> playlists = this.searchResponseDto.getPlaylists();
            if (playlists != null) {
                for (PlaylistEntity playlistEntity : playlists) {
                    String buttonText = String.format("%-60s %-15s %-20s", playlistEntity.getTitle(), playlistEntity.getPopularity(), "Playlist");
                    Button playlistButton = new Button(buttonText);
                    playlistButton.setUserData(playlistEntity);
                    playlistButton.setOnAction(event -> {
                        System.out.println(playlistButton.getText());
                    });
                    results.getChildren().add(playlistButton);
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
                    results.getChildren().add(artistButton);
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
                        System.out.println(userButton.getText());
                    });
                    results.getChildren().add(userButton);
                }
            }
        }
        // check for presenting user albums
        if (this.albums != null) {
            this.pageTitle.setText("Your favorite albums");
            for (AlbumEntity albumEntity : this.albums) {
                String buttonText = String.format("%-60s %-20s", albumEntity.getTitle(), "Album");
                Button albumButton = new Button(buttonText);
                albumButton.setUserData(albumEntity);
                albumButton.setPrefHeight(30);
                albumButton.setPrefWidth(1000);
                albumButton.setOnAction(event -> {
                    System.out.println(albumButton.getText());
                });
                results.getChildren().add(albumButton);
            }
        }
        // check for presenting user followed artists
        if (this.followings != null) {
            this.pageTitle.setText("Your followings");
            for (ArtistEntity artistEntity : this.followings) {
                String buttonText = String.format("%-60s %-20s", artistEntity.getName(), "Artist");
                Button artistButton = new Button(buttonText);
                artistButton.setUserData(artistEntity);
                artistButton.setPrefHeight(30);
                artistButton.setPrefWidth(1000);
                artistButton.setOnAction(event -> {
                    System.out.println(artistButton.getText());
                });
                results.getChildren().add(artistButton);
            }
        }
        // check for presenting user friends
        if (this.friends != null) {
            this.pageTitle.setText("Your friends");
            for (UserEntity userEntity : this.friends) {
                String buttonText = String.format("%-60s %-20s", userEntity.getUsername(), "User");
                Button userButton = new Button(buttonText);
                userButton.setUserData(userEntity);
                userButton.setPrefHeight(30);
                userButton.setPrefWidth(1000);
                userButton.setOnAction(event -> {
                    System.out.println(userButton.getText());
                });
                results.getChildren().add(userButton);
            }
        }
        // check for presenting user playlists
        if (this.playlists != null) {
            this.pageTitle.setText("Your playlists");
            for (PlaylistEntity playlistEntity : this.playlists) {
                String buttonText = String.format("%-60s %-15s %-20s", playlistEntity.getTitle(), playlistEntity.getPopularity(), "Playlist");
                Button playlistButton = new Button(buttonText);
                playlistButton.setUserData(playlistEntity);
                playlistButton.setOnAction(event -> {
                    System.out.println(playlistButton.getText());
                });
                results.getChildren().add(playlistButton);
            }
        }
    }

    @FXML
    public void back() {
        this.stage = (Stage) this.back.getScene().getWindow();
        this.loader.loadStartPage(this.stage);
    }

    public void setClient(ClientManager client) {
        this.client = client;
        this.loader = new LoadManager(this.client);
    }

    public void setResults(SearchResponseDto dto) {
        this.searchResponseDto = dto;
    }

    public void setAlbums(ArrayList<AlbumEntity> albums) {
        this.albums = albums;
    }

    public void setFollowings(ArrayList<ArtistEntity> followings) {
        this.followings = followings;
    }

    public void setFriends(ArrayList<UserEntity> friends) {
        this.friends = friends;
    }

    public void setPlaylists(ArrayList<PlaylistEntity> playlists) {
        this.playlists = playlists;
    }
}
