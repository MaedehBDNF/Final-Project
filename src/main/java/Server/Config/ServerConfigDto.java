package Server.Config;

public class ServerConfigDto {
    private int serverPort;
    private DatabaseConfigDto database;

    public int getServerPort() {
        return serverPort;
    }

    public void setServerPort(int serverPort) {
        this.serverPort = serverPort;
    }

    public DatabaseConfigDto getDatabase() {
        return database;
    }

    public void setDatabase(DatabaseConfigDto database) {
        this.database = database;
    }
}
