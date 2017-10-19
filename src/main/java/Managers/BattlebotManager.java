package Managers;

import Models.Battlebot;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;

import javax.bluetooth.RemoteDevice;
import java.io.IOException;
import java.util.ArrayList;

public class BattlebotManager {
    private BluetoothManager bluetoothManager;
    private ObservableList<Battlebot> battlebots = FXCollections.observableList(new ArrayList<>());
    private SocketManager socketManager;

    public BattlebotManager(SocketManager socketManager) {
        bluetoothManager = new BluetoothManager(this);
        this.socketManager = socketManager;

        battlebots.addListener(new ListChangeListener<Battlebot>() {
            @Override
            public void onChanged(Change<? extends Battlebot> c) {
                // fires when new bots are added
                battlebots.forEach(battlebot -> {
                    socketManager.sendToAllClients("botConnected", "Bot with address: " + battlebot.getRemoteDevice().getBluetoothAddress() + " connected to the server");
                });
            }
        });
    }

    public void createBattlebot(RemoteDevice remoteDevice) throws IOException {
        Battlebot battlebot = new Battlebot(remoteDevice, socketManager);
        battlebots.add(battlebot);
    }

    public void searchBots() throws IOException, InterruptedException {
        bluetoothManager.search();
    }

    public void connectAll(){
        for (Battlebot battlebot : battlebots) {
            battlebot.openConnection();
        }
    }

    public int getTotalBots() {
        return this.battlebots.size();
    }
}
