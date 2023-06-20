package Client;

import javafx.application.Application;
import javafx.stage.Stage;

import java.io.*;
import java.net.Socket;

public class ClientMain extends Application {
    private static Socket socket;
    public static void main(String[] args) throws IOException{
        final int PORT = 3000;
        socket = new Socket("localhost", PORT);

        launch();
    }

    @Override
    public void start(Stage stage) {
        ClientManager clientManager = new ClientManager(socket);
        LoadManager loader = new LoadManager(clientManager);
        loader.loadStartPage(stage);
    }
}
