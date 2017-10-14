
import Managers.BattlebotManager;
import Managers.SocketManager;

import java.io.IOException;

/**
 * Created by on 12/10/2017.
 */


public class Main {

    public static SocketManager socketManager;
    public static BattlebotManager battlebotManager;

    public static void main(String[] args) throws IOException, InterruptedException {
        socketManager = new SocketManager("localhost", 1337);

        socketManager.startServer();

        battlebotManager = new BattlebotManager();
        battlebotManager.searchBots();

    }
}
