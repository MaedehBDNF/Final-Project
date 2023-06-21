package Client;

import Client.Config.ClientConfigDto;
import Server.Config.DatabaseConfigDto;
import Server.Config.ServerConfigDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.application.Application;
import javafx.stage.Stage;

import java.io.*;
import java.net.Socket;

public class ClientMain extends Application {
    private static Socket socket;
    static ObjectMapper mapper = new ObjectMapper();
    private static ClientConfigDto config;

    public static void main(String[] args) throws IOException{
        readConfig();
        final int PORT = config.getPort();
        socket = new Socket("localhost", PORT);

        launch();
    }

    @Override
    public void start(Stage stage) {
        ClientManager clientManager = new ClientManager(socket);
        LoadManager loader = new LoadManager(clientManager);
        loader.loadStartPage(stage);
    }

    public static void readConfig() {
        String configPath = System.getProperty("user.dir") + "\\src\\main\\java\\Client\\Config\\config.json";
        try {
            config = mapper.readValue(new FileReader(configPath), ClientConfigDto.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
