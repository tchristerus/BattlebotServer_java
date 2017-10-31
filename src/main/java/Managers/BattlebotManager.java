package Managers;

import Models.Battlebot;
import com.corundumstudio.socketio.AckRequest;
import com.corundumstudio.socketio.SocketIOClient;
import com.corundumstudio.socketio.listener.DataListener;
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

    public BattlebotManager(SocketManager socketManager, String btId) {
        bluetoothManager = new BluetoothManager(this, btId);
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

        this.socketManager.getSocketServer().addEventListener("close", String.class, new DataListener<String>() {
            @Override
            public void onData(SocketIOClient socketIOClient, String s, AckRequest ackRequest) throws Exception {
                System.out.println("Fubar");
                battlebots.forEach(battlebot -> {
                    try {
                        battlebot.closeConnection();
                        System.out.println("Closed connection");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
            }
        });
    }

    public Battlebot createBattlebot(String friendlyName, String btMac) throws IOException {
        Battlebot battlebot = new Battlebot(socketManager, friendlyName, btMac);
        battlebots.add(battlebot);
        return battlebot;
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

    public ObservableList<Battlebot> getBattlebots(){
        return battlebots;
    }


    public Battlebot searchByName(String name){
        for(Battlebot battlebot: battlebots){
            if(battlebot.getFriendlyName().equals(name))
                return battlebot;
        }
        return null;
    }
}
