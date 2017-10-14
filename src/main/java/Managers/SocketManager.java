package Managers;

import com.corundumstudio.socketio.Configuration;
import com.corundumstudio.socketio.SocketConfig;
import com.corundumstudio.socketio.SocketIOServer;

public class SocketManager {
    private Configuration config;
    private SocketConfig socketConfig;
    private SocketIOServer socketServer;

    public SocketManager(String hostname, int port) {
        config = new Configuration();
        config.setHostname(hostname);
        config.setPort(port);

        socketConfig = new SocketConfig();
        socketConfig.setReuseAddress(true);

        config.setSocketConfig(socketConfig);

        socketServer = new SocketIOServer(config);
        registerEvents();

    }

    public void startServer() {
        socketServer.start();
    }

    public void stopServer() {
        socketServer.stop();
    }

    public void registerEvents() {
        socketServer.addConnectListener(socketIOClient -> sendToAllClients("connected", "Someone connected: " + socketIOClient.getSessionId()));
    }

    public void sendToAllClients(String event, Object data) {
        socketServer.getAllClients().forEach(client -> client.sendEvent(event, data));
    }

    public SocketIOServer getSocketServer() {
        return socketServer;
    }
}
