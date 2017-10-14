package Managers;

import Models.Battlebot;
import com.intel.bluetooth.btspp.Connection;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;

import javax.bluetooth.RemoteDevice;
import java.io.IOException;
import java.util.ArrayList;

public class BattlebotManager {
    private BluetoothManager bluetoothManager;
    private ObservableList<Battlebot> battlebots = FXCollections.observableList(new ArrayList<>());

    public BattlebotManager(){
        bluetoothManager = new BluetoothManager(this);

        battlebots.addListener(new ListChangeListener<Battlebot>() {
            @Override
            public void onChanged(Change<? extends Battlebot> c) {
                // fires when new bots are added
                battlebots.forEach(battlebot -> {
                    // TODO THE DEVICE IS FOUND BUT IT SEEMS LIKE IT DOES NOT HAVE ANY CONNECITON YET
                    System.out.println("Authenticated: " + battlebot.getRemoteDevice().isAuthenticated());});
            }
        });
    }

    public void createBattlebot(RemoteDevice remoteDevice){
        Battlebot battlebot = new Battlebot(remoteDevice);
        battlebots.add(battlebot);
    }

    public void searchBots() throws IOException, InterruptedException {
        bluetoothManager.search();
    }

    public int getTotalBots(){
        return this.battlebots.size();
    }
}
