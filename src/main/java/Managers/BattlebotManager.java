package Managers;

import Models.Battlebot;
import Utils.ConsoleUtil;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;

import java.io.IOException;
import java.util.ArrayList;

public class BattlebotManager {
    private BluetoothManager bluetoothManager;
    private ObservableList<Battlebot> battlebots = FXCollections.observableList(new ArrayList<>());
    private SocketManager socketManager;
    private ConsoleUtil consoleUtil;

    public BattlebotManager(SocketManager socketManager, String btId, ConsoleUtil consoleUtil) {
        bluetoothManager = new BluetoothManager(this, btId,consoleUtil);
        this.consoleUtil = consoleUtil;
        this.socketManager = socketManager;

        battlebots.addListener(new ListChangeListener<Battlebot>() {
            @Override
            public void onChanged(Change<? extends Battlebot> c) {
                // fires when new bots are added
                battlebots.forEach(battlebot -> {
                    socketManager.sendToAllClients("botConnected", "Bot with address: " + battlebot.getMac() + " connected to the server");
                });
            }
        });

    }

    public Battlebot createOrGetBattlebot(String friendlyName, String btMac) throws IOException {
        for (Battlebot battlebot : battlebots) {
            if (battlebot.getMac() == btMac)
                return battlebot;
        }

        Battlebot battlebot = new Battlebot(socketManager, friendlyName, btMac, consoleUtil);
        battlebots.add(battlebot);
        return battlebot;
    }

    public void searchBots() throws IOException, InterruptedException {
        bluetoothManager.search();
    }

    public void connectAll() {
        for (Battlebot battlebot : battlebots) {
            battlebot.openConnection();
        }
    }

    public int getTotalBots() {
        return this.battlebots.size();
    }

    public ObservableList<Battlebot> getBattlebots() {
        return battlebots;
    }


    public Battlebot searchByName(String name) {
        for (Battlebot battlebot : battlebots) {
            if (battlebot.getFriendlyName().equals(name))
                return battlebot;
        }
        return null;
    }
}
