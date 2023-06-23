package Client;

import Client.Controllers.*;
import Shared.Dto.Search.SearchResponseDto;
import Shared.Entities.*;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import java.io.IOException;
import java.util.ArrayList;

public class LoadManager {
    private final ClientManager client;

    public LoadManager(ClientManager client) {
        this.client = client;
    }

    public void loadStartPage(Stage stage) {
        stage.close();
        StartController controller = new StartController();
        controller.setClient(this.client);
        FXMLLoader loader = new FXMLLoader();
        loader.setController(controller);
        loader.setLocation(getClass().getClassLoader().getResource("Start.fxml"));
        this.basicLoad(loader);
    }

    public void loadSignUpPage(Stage stage) {
        stage.close();
        SignUpController controller = new SignUpController();
        controller.setClient(this.client);
        FXMLLoader loader = new FXMLLoader();
        loader.setController(controller);
        loader.setLocation(getClass().getClassLoader().getResource("SignUp.fxml"));
        this.basicLoad(loader);
    }

    public void loadLoginPage(Stage stage) {
        stage.close();
        LogInController controller = new LogInController();
        controller.setClient(this.client);
        FXMLLoader loader = new FXMLLoader();
        loader.setController(controller);
        loader.setLocation(getClass().getClassLoader().getResource("Login.fxml"));
        this.basicLoad(loader);
    }

    public void loadMainPage(Stage stage) {
        stage.close();
        MainPageController controller = new MainPageController();
        controller.setClient(this.client);
        controller.setUser(this.client.getCurrentUser());
        FXMLLoader loader = new FXMLLoader();
        loader.setController(controller);
        loader.setLocation(getClass().getClassLoader().getResource("MainPage.fxml"));
        this.basicLoad(loader);
    }

    public void loadSearchPage(Stage stage, SearchResponseDto dto, String title) {
        stage.close();
        SearchPresentationController controller = new SearchPresentationController();
        controller.setClient(this.client);
        controller.setResults(dto);
        controller.setTitle(title);
        FXMLLoader loader = new FXMLLoader();
        loader.setController(controller);
        loader.setLocation(getClass().getClassLoader().getResource("SearchPresentation.fxml"));
        this.basicLoad(loader);
    }

    public void loadMainCompleteSearchPage(Stage stage, SearchResponseDto dto, String title) {
        stage.close();
        MainCompleteSearchController controller = new MainCompleteSearchController();
        controller.setClient(this.client);
        controller.setResults(dto);
        controller.setTitle(title);
        FXMLLoader loader = new FXMLLoader();
        loader.setController(controller);
        loader.setLocation(getClass().getClassLoader().getResource("SearchPresentation.fxml"));
        this.basicLoad(loader);
    }

    public void loadSearchPageForAlbums(Stage stage, ArrayList<AlbumEntity> dto, String title) {
        stage.close();
        SearchAlbumController controller = new SearchAlbumController();
        controller.setClient(this.client);
        controller.setAlbums(dto);
        controller.setTitle(title);
        FXMLLoader loader = new FXMLLoader();
        loader.setController(controller);
        loader.setLocation(getClass().getClassLoader().getResource("SearchPresentation.fxml"));
        this.basicLoad(loader);
    }

    public void loadSearchPageForUsers(Stage stage, ArrayList<UserEntity> dto, String title) {
        stage.close();
        SearchUserController controller = new SearchUserController();
        controller.setClient(this.client);
        controller.setUsers(dto);
        controller.setTitle(title);
        FXMLLoader loader = new FXMLLoader();
        loader.setController(controller);
        loader.setLocation(getClass().getClassLoader().getResource("SearchPresentation.fxml"));
        this.basicLoad(loader);
    }

    public void loadSearchPageForArtists(Stage stage, ArrayList<ArtistEntity> dto, String title) {
        stage.close();
        SearchArtistController controller = new SearchArtistController();
        controller.setClient(this.client);
        controller.setArtists(dto);
        controller.setTitle(title);
        FXMLLoader loader = new FXMLLoader();
        loader.setController(controller);
        loader.setLocation(getClass().getClassLoader().getResource("SearchPresentation.fxml"));
        this.basicLoad(loader);
    }

    public void loadSearchPageForPlaylists(Stage stage, ArrayList<PlaylistEntity> dto, String title) {
        stage.close();
        SearchPlaylistController controller = new SearchPlaylistController();
        controller.setClient(this.client);
        controller.setPlaylists(dto);
        controller.setTitle(title);
        FXMLLoader loader = new FXMLLoader();
        loader.setController(controller);
        loader.setLocation(getClass().getClassLoader().getResource("SearchPresentation.fxml"));
        this.basicLoad(loader);
    }

    public void loadSearchPageForMusics(Stage stage, ArrayList<MusicEntity> dto, String title) {
        stage.close();
        SearchMusicController controller = new SearchMusicController();
        controller.setClient(this.client);
        controller.setMusics(dto);
        controller.setTitle(title);
        FXMLLoader loader = new FXMLLoader();
        loader.setController(controller);
        loader.setLocation(getClass().getClassLoader().getResource("SearchPresentation.fxml"));
        this.basicLoad(loader);
    }

    public void loadCreateNewPlaylistPage(Stage stage) {
        stage.close();
        CreateNewPlaylistController controller = new CreateNewPlaylistController();
        FXMLLoader loader = new FXMLLoader();
        loader.setController(controller);
        loader.setLocation(getClass().getClassLoader().getResource("CreateNewPlaylist.fxml"));
        this.basicLoad(loader);
    }

    public void loadUserPresentationPage(UserEntity user, ArrayList<PlaylistEntity> pls) {
        UserPresentationController controller = new UserPresentationController();
        controller.setClient(this.client);
        controller.setUser(user);
        controller.setPlaylists(pls);
        FXMLLoader loader = new FXMLLoader();
        loader.setController(controller);
        loader.setLocation(getClass().getClassLoader().getResource("UserPresentation.fxml"));
        this.basicLoad(loader);
    }

    public void loadFriendsPresentationPage(ArrayList<UserEntity> friends, String title) {
        FriendsPresentationController controller = new FriendsPresentationController();
        controller.setClient(this.client);
        controller.setFriends(friends);
        controller.setTitle(title);
        FXMLLoader loader = new FXMLLoader();
        loader.setController(controller);
        loader.setLocation(getClass().getClassLoader().getResource("ResultsPresentation.fxml"));
        this.basicLoad(loader);
    }

    public void loadFollowingsPresentationPage(ArrayList<ArtistEntity> followings, String title) {
        FollowingsPresentationController controller = new FollowingsPresentationController();
        controller.setClient(this.client);
        controller.setFollowings(followings);
        controller.setTitle(title);
        FXMLLoader loader = new FXMLLoader();
        loader.setController(controller);
        loader.setLocation(getClass().getClassLoader().getResource("ResultsPresentation.fxml"));
        this.basicLoad(loader);
    }

    public void loadArtistPresentationPage(ArtistEntity artist) {
        ArtistPresentationController controller = new ArtistPresentationController();
        controller.setClient(this.client);
        controller.setArtist(artist);
        FXMLLoader loader = new FXMLLoader();
        loader.setController(controller);
        loader.setLocation(getClass().getClassLoader().getResource("ArtistPresentation.fxml"));
        this.basicLoad(loader);
    }

    public void loadAlbumPresentationPage(AlbumEntity album, ArrayList<MusicEntity> musics) {
        AlbumPresentationController controller = new AlbumPresentationController();
        controller.setClient(this.client);
        controller.setAlbum(album);
        controller.setAlbumMusics(musics);
        FXMLLoader loader = new FXMLLoader();
        loader.setController(controller);
        loader.setLocation(getClass().getClassLoader().getResource("AlbumPresentation.fxml"));
        this.basicLoad(loader);
    }

    // todo: Complete

    private void basicLoad(FXMLLoader loader) {
        try {
            Parent root = loader.load();
            Stage stage = new Stage();
            Image icon = new Image("Images/SpotifyIcon.png");
            stage.getIcons().add(icon);
            stage.setTitle("Spotify");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
