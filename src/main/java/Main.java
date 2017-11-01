import Managers.BattlebotManager;
import Managers.BluetoothManager;
import Managers.SocketManager;
import Models.Battlebot;
import Structs.BattlebotStruct;
import Utils.ConfigUtil;
import Utils.ConsoleUtil;
import com.corundumstudio.socketio.AckRequest;
import com.corundumstudio.socketio.SocketIOClient;
import com.corundumstudio.socketio.listener.DataListener;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by on 12/10/2017.
 */


public class Main {

    public static SocketManager socketManager;
    public static BattlebotManager battlebotManager;
    public static ConfigUtil configUtil;
    public static ArrayList<BattlebotStruct> battlebotStructs;
    public static String botId = "bt";
    public static boolean searchDevices = false;
    public static ConsoleUtil consoleUtil;

    public static void main(String[] args) throws IOException, InterruptedException {
        configUtil = new ConfigUtil("config.txt");
        battlebotStructs = configUtil.parseConfig();

        socketManager = new SocketManager("localhost", 8080);
        consoleUtil = new ConsoleUtil(socketManager);
        battlebotManager = new BattlebotManager(socketManager, botId, consoleUtil);
        socketManager.startServer();

        if (searchDevices) {
            BluetoothManager bluetoothManager = new BluetoothManager(battlebotManager, "1", consoleUtil);
            bluetoothManager.search();
        }

        reloadConfig();

        socketManager.getSocketServer().addEventListener("reconnectEvent", String.class, new DataListener<String>() {
            @Override
            public void onData(SocketIOClient socketIOClient, String s, AckRequest ackRequest) throws Exception {
                consoleUtil.write("Reconnect command received for: " + s);
                Battlebot battlebot = battlebotManager.searchByName(s);
                if (battlebot != null) {
                    battlebot.openConnection();
                } else {
                    consoleUtil.write(s + " not found");
                }
            }
        });
        socketManager.getSocketServer().addEventListener("search", String.class, new DataListener<String>() {
            @Override
            public void onData(SocketIOClient socketIOClient, String s, AckRequest ackRequest) throws Exception {
                consoleUtil.write("Search command received...");
                consoleUtil.write("Searching..");
                BluetoothManager bluetoothManager = new BluetoothManager(battlebotManager, "1", consoleUtil);
                bluetoothManager.search();
            }
        });

        socketManager.getSocketServer().addEventListener("reload_config", String.class, new DataListener<String>() {
            @Override
            public void onData(SocketIOClient socketIOClient, String s, AckRequest ackRequest) throws Exception {
                consoleUtil.write("Reload config command received...");
                reloadConfig();
            }
        });

    }

    public static void reloadConfig() {
        for (BattlebotStruct battlebotStruct : battlebotStructs) {
            try {
                Battlebot bot = battlebotManager.createOrGetBattlebot(battlebotStruct.botName, battlebotStruct.macAddress);
                if (bot != null) {
                    bot.openConnection();
                }
            } catch (Exception e) {
                consoleUtil.write("Connection failed with " + battlebotStruct.botName);
            }
        }
    }
}
