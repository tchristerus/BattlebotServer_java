import Managers.SocketManager;
import com.corundumstudio.socketio.Configuration;
import com.corundumstudio.socketio.SocketConfig;
import com.corundumstudio.socketio.SocketIOClient;
import com.corundumstudio.socketio.SocketIOServer;
import com.corundumstudio.socketio.listener.ConnectListener;

import javax.bluetooth.*;
import java.io.IOException;
import java.util.Vector;

/**
 * Created by on 12/10/2017.
 */



public class Main {

    public static SocketManager socketManager;

    public static void main(String[] args) throws IOException, InterruptedException {
        socketManager = new SocketManager("localhost", 1337);

        socketManager.startServer();

        RemoteDeviceDiscovery rmd = new RemoteDeviceDiscovery();
        rmd.search();
    }
}
