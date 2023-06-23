package Client.Controllers;

import Client.ClientManager;
import Client.LoadManager;
import Shared.Dto.User.LoginDto;
import Shared.Entities.UserEntity;
import Shared.Enums.Error;
import Shared.Enums.Status;
import Shared.Response;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class LogInController {
    private ClientManager client;
    private LoadManager loader;
    private final ObjectMapper mapper = new ObjectMapper();
    private Stage stage;

    @FXML
    private TextField username;
    @FXML
    private PasswordField password;
    @FXML
    private Button back, login, signUp;
    @FXML
    private Label loginLabel, usernameLabel, passwordLabel, wrongUsername, wrongPassword;

    public void setClient(ClientManager client) {
        this.client = client;
        this.loader = new LoadManager(this.client);
    }

    @FXML
    public void login() {
        this.stage = (Stage) this.login.getScene().getWindow();
        this.wrongUsername.setVisible(false);
        this.wrongPassword.setVisible(false);

        String username = this.username.getText();
        String password = this.password.getText();

        LoginDto loginDto = new LoginDto();
        loginDto.setUsername(username);
        loginDto.setPassword(password);
        Response response = this.client.login(loginDto);

        if (response.getStatus().equals(Status.failed)) {
            if (response.getError().equals(Error.wrongUsername)) {
                this.wrongUsername.setVisible(true);
            }
            if (response.getError().equals(Error.wrongPassword)) {
                this.wrongPassword.setVisible(true);
            }
            return;
        }

        this.loader.loadMainPage(this.stage);
    }

    @FXML
    public void loadSignUp() {
        this.stage = (Stage) this.login.getScene().getWindow();
        this.loader.loadSignUpPage(this.stage);
    }

    @FXML
    public void back() {
        this.stage = (Stage) this.login.getScene().getWindow();
        this.loader.loadStartPage(this.stage);
    }
}