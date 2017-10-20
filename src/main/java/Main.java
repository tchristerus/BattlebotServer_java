import Managers.BattlebotManager;
import Managers.BluetoothManager;
import Managers.SocketManager;
import Models.Battlebot;
import Structs.BattlebotStruct;
import Utils.ConfigUtil;

import java.io.IOException;
import java.util.ArrayList;
import javax.bluetooth.UUID;

/**
 * Created by on 12/10/2017.
 */


public class Main {

    public static SocketManager socketManager;
    public static BattlebotManager battlebotManager;
    public static ConfigUtil configUtil;
    public static ArrayList<BattlebotStruct> battlebotStructs;
    public static String botId = "bt";
    public static String[] btMacs = new String[]{"98D3313079F7", "98D331901B10"};//bot 17, bot11
    public static boolean searchDevices = false;

    public static void main(String[] args) throws IOException, InterruptedException {
        configUtil = new ConfigUtil("config.txt");
        battlebotStructs = configUtil.parseConfig();

        socketManager = new SocketManager("localhost", 8080);
        battlebotManager = new BattlebotManager(socketManager, botId);
        socketManager.startServer();

        if(searchDevices){
            BluetoothManager bluetoothManager = new BluetoothManager(battlebotManager, "1");
            bluetoothManager.search();
        }

        for (BattlebotStruct battlebotStruct: battlebotStructs){
            Battlebot battlebot = battlebotManager.createBattlebot(battlebotStruct.botName, battlebotStruct.macAddress);
            battlebot.openConnection();
        }
    }
}
