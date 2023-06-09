package Server;

import Server.Config.DatabaseConfigDto;
import Server.Config.ServerConfigDto;
import Server.Manager.Manager;
import Server.Tables.InitializerService;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.FileReader;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class ServerMain {
    static ObjectMapper mapper = new ObjectMapper();
    static ServerConfigDto config;

    public static void main(String[] args) {
        readConfig();
        final int serverPort = config.getServerPort();
        try {
            ServerSocket server = new ServerSocket(serverPort);
            new InitializerService(config.getDatabase());

            while (true) {
                Socket socket = server.accept();
                System.out.println("Client connected.");
                Manager service = new Manager(socket, config.getDatabase());
                Thread t = new Thread(service);
                t.start();
            }
        } catch (IOException e){
            e.printStackTrace();
        }

    }


    public static void readConfig() {
        String configPath = System.getProperty("user.dir") + "\\src\\main\\java\\Server\\config.json";
        try {
            config = mapper.readValue(new FileReader(configPath), ServerConfigDto.class);
            config.setDatabase(mapper.convertValue(config.getDatabase(), DatabaseConfigDto.class));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
