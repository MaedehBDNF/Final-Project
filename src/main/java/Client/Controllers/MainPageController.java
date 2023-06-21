package Client.Controllers;

import Client.ClientManager;
import Client.LoadManager;
import Shared.Entities.UserEntity;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;

public class MainPageController implements Initializable {
    private ClientManager client;
    private LoadManager loader;
    private UserEntity user;
    private final ObjectMapper mapper = new ObjectMapper();
    @FXML
    private Circle profile;
    @FXML
    private Label username;

    public MainPageController() {
    }

    public void setClient(ClientManager client) {
        this.client = client;
        this.loader = new LoadManager(this.client);
    }

    public void setUser(UserEntity user) {
        this.user = user;
    }

    public void initialize(URL location, ResourceBundle resources) {
        if (this.user.getProfilePicture() != null) {
            try {
                Image image = new Image(new FileInputStream(this.client.download(this.user.getProfilePicture())));
                this.profile.setFill(new ImagePattern(image));
            } catch (IOException var4) {
                var4.printStackTrace();
            }
        }

        this.username.setText(this.user.getUsername());
    }
}
