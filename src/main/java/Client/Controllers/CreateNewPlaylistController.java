package Client.Controllers;

import Client.ClientManager;
import Client.LoadManager;
import Shared.Dto.File.FileDto;
import Shared.Dto.File.UploadDto;
import Shared.Dto.Playlist.CreatePlaylistDto;
import Shared.Entities.PlaylistEntity;
import Shared.Enums.Status;
import Shared.Enums.UploadType;
import Shared.Response;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.Window;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class CreateNewPlaylistController implements Initializable {
    private ClientManager client;
    private LoadManager loader;
    private final ObjectMapper mapper = new ObjectMapper();
    private File selectedFile;
    private final String projectDirectory = System.getProperty("user.dir");
    private Stage stage;

    @FXML
    private RadioButton noButton, yesButton;
    @FXML
    private Button back, create, setCover;
    @FXML
    private TextField title;
    @FXML
    private TextArea description;
    @FXML
    private Circle cover;
    @FXML
    private Label errorMessage, titleError;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            this.selectedFile = new File(this.projectDirectory + "\\src\\main\\resources\\Images\\DefaultPlaylistCover.png");
            Image image = new Image(new FileInputStream(this.selectedFile.getPath()));
            this.cover.setFill(new ImagePattern(image));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void create() {
        this.stage = (Stage)this.back.getScene().getWindow();

        this.titleError.setVisible(false);
        this.errorMessage.setVisible(false);


        CreatePlaylistDto dto = new CreatePlaylistDto();

        if (!this.title.getText().isEmpty()) {
            dto.setTitle(this.title.getText());
            dto.setDescription(this.description.getText());
            dto.setCreatorId(this.client.getCurrentUserId());
            if (this.yesButton.isSelected()) {
                dto.setPrivatePL(true);
            }
            Response response = this.client.createPlaylist(dto);
            if (response.getStatus().equals(Status.successful)) {
                PlaylistEntity playlist = this.mapper.convertValue(response.getData(), PlaylistEntity.class);
                FileDto playlistCover = this.uploadCover(playlist.getId());
                if (playlistCover != null) {
                    dto.setCoverId(playlistCover.getId());
                }
                this.loader.loadMainPage(this.stage);
            } else {
                this.errorMessage.setVisible(true);
            }
        } else {
            this.titleError.setVisible(true);
        }
    }

    @FXML
    public void setCover() {
        this.stage = (Stage)this.back.getScene().getWindow();
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("JPEG or PNG Files", new String[]{"*.jpg", "*.jpeg", "*.png"}));
        File newCover = fileChooser.showOpenDialog((Window)null);
        if (newCover != null) {
            this.selectedFile = this.client.copyFile(UploadType.userProfilePicture, newCover);
        }

        try {
            Image image = new Image(new FileInputStream(this.selectedFile.getPath()));
            this.cover.setFill(new ImagePattern(image));
        } catch (IOException var4) {
            var4.printStackTrace();
        }
    }

    @FXML
    public void back() {
        this.stage = (Stage) this.back.getScene().getWindow();
        this.loader.loadMainPage(this.stage);
    }

    public void setClient(ClientManager client) {
        this.client = client;
        this.loader = new LoadManager(this.client);
    }

    private FileDto uploadCover(int playlistId) {
        if (this.selectedFile.getName().equals("DefaultPlaylistCover.png")) {
            return null;
        }

        UploadDto uploadDto = new UploadDto();
        String fileName = this.selectedFile.getName();
        uploadDto.setName(fileName.substring(0, fileName.lastIndexOf(".")));
        uploadDto.setMemeType(fileName.substring(fileName.lastIndexOf(".") + 1));
        uploadDto.setUploadType(UploadType.playlistCover);
        uploadDto.setReferenceId(playlistId);
        Response response = this.client.uploadPicture(uploadDto, this.selectedFile);
        return this.mapper.convertValue(response.getData(), FileDto.class);
    }
}
