package Client.Controllers;

import Client.ClientManager;
import Client.LoadManager;
import Shared.Entities.ArtistEntity;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class FollowingsPresentationController implements Initializable {
    private ClientManager client;
    private LoadManager loader;
    private ArrayList<ArtistEntity> followings;
    private String title;

    @FXML
    private Label pageTitle;
    @FXML
    private VBox results;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.pageTitle.setText(this.title);

        if (this.followings != null) {
            for (ArtistEntity artistEntity : this.followings) {
                String buttonText = String.format("%-60s %-20s", artistEntity.getName(), "Artist");
                Button artistButton = new Button(buttonText);
                artistButton.setUserData(artistEntity);
                artistButton.setPrefHeight(30);
                artistButton.setPrefWidth(1000);
                artistButton.setOnAction(event -> {
                    System.out.println(artistButton.getText());
                });
                this.results.getChildren().add(artistButton);
            }
        }
    }

    public void setClient(ClientManager client) {
        this.client = client;
        this.loader = new LoadManager(this.client);
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setFollowings(ArrayList<ArtistEntity> followings) {
        this.followings = followings;
    }
}
