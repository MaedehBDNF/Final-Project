package Client.Controllers;

import Client.ClientManager;
import Client.LoadManager;
import Shared.Entities.UserEntity;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class FriendsPresentationController implements Initializable {
    private ClientManager client;
    private LoadManager loader;
    private ArrayList<UserEntity> friends;
    private String title;

    @FXML
    private Label pageTitle;
    @FXML
    private VBox results;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.pageTitle.setText(this.title);

        if (this.friends != null) {
            for (UserEntity userEntity : this.friends) {
                String buttonText = String.format("%-60s %-20s", userEntity.getUsername(), "User");
                Button userButton = new Button(buttonText);
                userButton.setUserData(userEntity);
                userButton.setPrefHeight(30);
                userButton.setPrefWidth(1000);
                userButton.setOnAction(event -> {
                    System.out.println(userButton.getText());
                });
                this.results.getChildren().add(userButton);
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

    public void setFriends(ArrayList<UserEntity> friends) {
        this.friends = friends;
    }
}
