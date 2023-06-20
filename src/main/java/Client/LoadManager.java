package Client;

import Client.Controllers.*;
import Shared.Dto.Search.SearchResponseDto;
import Shared.Entities.UserEntity;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import java.io.IOException;

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

    public void loadMainPage(Stage stage, UserEntity userEntity) {
        stage.close();
        MainPageController controller = new MainPageController();
        controller.setClient(this.client);
        controller.setUser(userEntity);
        FXMLLoader loader = new FXMLLoader();
        loader.setController(controller);
        loader.setLocation(getClass().getClassLoader().getResource("MainPage.fxml"));
        this.basicLoad(loader);
    }

    public void loadSearchPage(Stage stage, SearchResponseDto dto) {
        stage.close();
        SearchPresentationController controller = new SearchPresentationController();
        controller.setClient(this.client);
        controller.setResults(dto);
        FXMLLoader loader = new FXMLLoader();
        loader.setController(controller);
        loader.setLocation(getClass().getClassLoader().getResource("SearchPresentation.fxml"));
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
