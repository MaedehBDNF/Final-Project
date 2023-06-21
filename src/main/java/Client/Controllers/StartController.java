package Client.Controllers;

import Client.ClientManager;
import Client.LoadManager;
import Shared.Dto.Search.SearchResponseDto;
import Shared.Response;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;


public class StartController implements Initializable {
    private ClientManager client;
    private LoadManager loader;
    private final ObjectMapper mapper = new ObjectMapper();
    private Stage stage;

    @FXML
    private TextField searchBar;
    @FXML
    private Button signUp, logIn;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        searchBar.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                this.search();
            }
        });
    }

    public void setClient(ClientManager client) {
        this.client = client;
        this.loader = new LoadManager(this.client);
    }

    @FXML
    public void signUp() {
        this.stage = (Stage) this.signUp.getScene().getWindow();
        this.loader.loadSignUpPage(this.stage);
    }

    @FXML
    public void logIn() {
        this.stage = (Stage) this.logIn.getScene().getWindow();
        this.loader.loadLoginPage(this.stage);
    }

    public void search() {
        this.stage = (Stage) this.searchBar.getScene().getWindow();
        Response res = this.client.completeSearch(this.searchBar.getText());
        SearchResponseDto result = this.mapper.convertValue(res.getData(), SearchResponseDto.class);
        this.loader.loadSearchPage(this.stage, result);
    }
}