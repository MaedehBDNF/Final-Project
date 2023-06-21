package Client.Controllers;

import Client.ClientManager;
import Client.LoadManager;
import Shared.Response;
import Shared.Dto.File.FileDto;
import Shared.Dto.File.UploadDto;
import Shared.Dto.User.RegisterDto;
import Shared.Entities.UserEntity;
import Shared.Enums.Error;
import Shared.Enums.Status;
import Shared.Enums.UploadType;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.regex.Pattern;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.Window;
import javafx.stage.FileChooser.ExtensionFilter;

public class SignUpController implements Initializable {
    private ClientManager client;
    private LoadManager loader;
    private final ObjectMapper mapper = new ObjectMapper();
    private File selectedFile;
    private final String projectDirectory = System.getProperty("user.dir");
    private Stage stage;
    @FXML
    private TextField usernameText, emailText;
    @FXML
    private PasswordField passwordText;
    @FXML
    private Button back, signUp, editProfilePhoto;
    @FXML
    private Label username, password, email, invalidEmail, usernameError, passwordError, generalError;
    @FXML
    private Circle profile;

    public SignUpController() {
    }

    public void initialize(URL location, ResourceBundle resources) {
        try {
            this.selectedFile = new File(this.projectDirectory + "\\src\\main\\resources\\Images\\DefaultProfilePhoto.jpg");
            Image image = new Image(new FileInputStream(this.selectedFile.getPath()));
            this.profile.setFill(new ImagePattern(image));
        } catch (IOException var4) {
            var4.printStackTrace();
        }

    }

    public void setClient(ClientManager client) {
        this.client = client;
        this.loader = new LoadManager(this.client);
    }

    @FXML
    public void signUp() {
        this.stage = (Stage)this.signUp.getScene().getWindow();
        this.usernameError.setVisible(false);
        this.passwordError.setVisible(false);
        this.invalidEmail.setVisible(false);
        this.generalError.setVisible(false);
        String username = this.usernameText.getText();
        String password = this.passwordText.getText();
        String email = this.emailText.getText();
        String profilePath = this.selectedFile.getPath();
        if (!username.isEmpty() && username.length() <= 26 && username.length() >= 5) {
            if (password.isEmpty()) {
                this.passwordError.setVisible(true);
            } else if (!this.checkEmailValidity(email)) {
                this.invalidEmail.setVisible(true);
            } else {
                RegisterDto registerDto = new RegisterDto();
                registerDto.setUsername(username);
                registerDto.setPassword(password);
                registerDto.setEmail(email);
                Response response = this.client.register(registerDto);
                if (response.getStatus().equals(Status.failed)) {
                    if (response.getError().equals(Error.duplicateDataError)) {
                        this.usernameError.setText("This username is taken. Please choose another one.");
                        this.usernameError.setVisible(true);
                    } else {
                        this.generalError.setVisible(true);
                    }
                } else {
                    UserEntity user = this.mapper.convertValue(response.getData(), UserEntity.class);
                    FileDto profilePicture = this.uploadProfilePicture(user.getId());
                    if (profilePicture == null) {
                        this.generalError.setVisible(true);
                    } else {
                        user.setProfilePicture(profilePicture);
                    }

                    this.loader.loadMainPage(this.stage, user);
                }
            }
        } else {
            this.usernameError.setText("Username should have length between 5 and 26.");
            this.usernameError.setVisible(true);
        }
    }

    @FXML
    public void back() {
        this.stage = (Stage)this.signUp.getScene().getWindow();
        this.loader.loadStartPage(this.stage);
    }

    @FXML
    public void getProfilePicture() {
        this.stage = (Stage)this.signUp.getScene().getWindow();
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new ExtensionFilter("JPEG or PNG Files", new String[]{"*.jpg", "*.jpeg", "*.png"}));
        File newProfile = fileChooser.showOpenDialog((Window)null);
        if (newProfile != null) {
            this.selectedFile = this.client.copyFile(UploadType.userProfilePicture, newProfile);
        }

        try {
            Image image = new Image(new FileInputStream(this.selectedFile.getPath()));
            this.profile.setFill(new ImagePattern(image));
        } catch (IOException var4) {
            var4.printStackTrace();
        }

    }

    private boolean checkEmailValidity(String email) {
        Pattern pattern = Pattern.compile("((\\w+)|(\\w+.\\w+))+@+[a-zA-Z]+.[a-zA-Z]+");
        return Pattern.matches(pattern.toString(), email);
    }

    private FileDto uploadProfilePicture(int userId) {
        if (this.selectedFile.getName().equals("DefaultProfilePhoto.jpg")) {
            this.selectedFile = this.client.copyFile(UploadType.userProfilePicture, this.selectedFile);
        }

        UploadDto uploadDto = new UploadDto();
        String fileName = this.selectedFile.getName();
        uploadDto.setName(fileName.substring(0, fileName.lastIndexOf(".")));
        uploadDto.setMemeType(fileName.substring(fileName.lastIndexOf(".") + 1));
        uploadDto.setReferenceId(userId);
        uploadDto.setUploadType(UploadType.userProfilePicture);
        Response response = this.client.uploadPicture(uploadDto, this.selectedFile);
        return this.mapper.convertValue(response.getData(), FileDto.class);
    }
}
