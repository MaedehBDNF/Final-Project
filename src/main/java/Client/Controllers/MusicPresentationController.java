package Client.Controllers;

import Client.ClientManager;
import Client.LoadManager;
import com.fasterxml.jackson.databind.ObjectMapper;

public class MusicPresentationController {
    private ClientManager client;
    private LoadManager loader;
    private final ObjectMapper mapper = new ObjectMapper();

    public MusicPresentationController() {
    }

    public void setClient(ClientManager client) {
        this.client = client;
        this.loader = new LoadManager(this.client);
    }
}
