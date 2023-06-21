package Client.Controllers;

import Client.ClientManager;
import Client.LoadManager;
import Shared.Entities.MusicEntity;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class SearchMusicController implements Initializable {
    private ClientManager client;
    private LoadManager loader;
    private ArrayList<MusicEntity> musics;
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

        if (this.musics != null) {
            for (MusicEntity musicEntity : this.musics) {
                String buttonText = String.format("%-60s %-15s %-20s", musicEntity.getTitle(), musicEntity.getPopularity(), "Track");
                Button musicButton = new Button(buttonText);
                musicButton.setUserData(musicEntity);
                musicButton.setPrefHeight(30);
                musicButton.setPrefWidth(1000);
                musicButton.setOnAction(event -> {
                    System.out.println(musicButton.getText());
                });
                this.results.getChildren().add(musicButton);
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

    public void setMusics(ArrayList<MusicEntity> musics) {
        this.musics = musics;
    }

}
