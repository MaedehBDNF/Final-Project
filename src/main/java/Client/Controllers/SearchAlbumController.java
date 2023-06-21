package Client.Controllers;

import Client.ClientManager;
import Client.LoadManager;
import Shared.Entities.AlbumEntity;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class SearchAlbumController implements Initializable {
    private ClientManager client;
    private LoadManager loader;
    private ArrayList<AlbumEntity> albums;
    private Stage stage;
    private String title;

    @FXML
    private Label pageTitle;
    @FXML
    private Button back;
    @FXML
    private VBox results;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.pageTitle.setText(this.title);

        if (this.albums != null) {
            for (AlbumEntity albumEntity : this.albums) {
                String buttonText = String.format("%-60s %-20s", albumEntity.getTitle(), "Album");
                Button albumButton = new Button(buttonText);
                albumButton.setUserData(albumEntity);
                albumButton.setPrefHeight(30);
                albumButton.setPrefWidth(1000);
                albumButton.setOnAction(event -> {
                    System.out.println(albumButton.getText());
                });
                this.results.getChildren().add(albumButton);
            }
        }
    }

    @FXML
    public void back() {
        this.stage = (Stage) this.back.getScene().getWindow();
        this.loader.loadMainPage(this.stage, this.client.getCurrentUser());
    }

    public void setClient(ClientManager client) {
        this.client = client;
        this.loader = new LoadManager(this.client);
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setAlbums(ArrayList<AlbumEntity> albums) {
        this.albums = albums;
    }
}
