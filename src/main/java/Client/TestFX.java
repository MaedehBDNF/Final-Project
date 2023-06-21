package Client;
import Client.Controllers.StartController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;

public class TestFX extends Application {

    @Override
    public void start(Stage stage) throws IOException {

        // test all pages

        testStart(stage);
        testSignUp(stage);
        testLogIn(stage);

    }

    public static void main(String[] args) {
        launch();
    }

    private static void testStart(Stage stage) {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setController(new StartController());
            loader.setLocation(TestFX.class.getClassLoader().getResource("Start.fxml"));
            Parent root = loader.load();
            Image icon = new Image("Images/SpotifyIcon.png");
            stage.getIcons().add(icon);
            stage.setTitle("Spotify");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private static void testSignUp(Stage stage) {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setController(new StartController());
            loader.setLocation(TestFX.class.getClassLoader().getResource("SignUp.fxml"));
            Parent root = loader.load();
            Image icon = new Image("Images/SpotifyIcon.png");
            stage.getIcons().add(icon);
            stage.setTitle("SignUp");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private static void testLogIn(Stage stage) {
        try {FXMLLoader loader = new FXMLLoader();
            loader.setController(new StartController());
            loader.setLocation(TestFX.class.getClassLoader().getResource("LogIn.fxml"));
            Parent root = loader.load();
            Image icon = new Image("Images/SpotifyIcon.png");
            stage.getIcons().add(icon);
            stage.setTitle("SignUp");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}