package Managers;

import Models.Battlebot;

import javax.bluetooth.RemoteDevice;
import java.io.IOException;
import java.util.ArrayList;

public class BattlebotManager {
    private BluetoothManager bluetoothManager;
    private ArrayList<Battlebot> battlebots = new ArrayList<>();

    public BattlebotManager(){
        bluetoothManager = new BluetoothManager(this);
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
