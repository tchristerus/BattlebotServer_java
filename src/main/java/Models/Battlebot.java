package Models;

import Managers.SocketManager;
import Utils.ConsoleUtil;
import org.json.JSONObject;

import javax.bluetooth.BluetoothConnectionException;
import javax.bluetooth.RemoteDevice;
import javax.bluetooth.UUID;
import javax.microedition.io.Connector;
import javax.microedition.io.StreamConnection;
import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.ArrayList;

public class Battlebot {
    private RemoteDevice remoteDevice;
    private StreamConnection btConn;
    private SocketManager socketManager;
    private String mac;
    private String friendlyName;
    private ArrayList<String> parts = new ArrayList<>();
    private ConsoleUtil consoleUtil;
    // Streams
    DataInput is = null;
    DataOutput os = null;
    Thread listener;

    public Battlebot(SocketManager socketManager, String friendlyName, String mac, ConsoleUtil consoleUtil) throws IOException {
        // this.remoteDevice = remoteDevice;
        this.socketManager = socketManager;
        this.mac = mac;
        this.friendlyName = friendlyName;
        this.consoleUtil = consoleUtil;
    }

    public void openConnection() {
        listener = new Thread(() -> {
            UUID uuid = new UUID("1", false);

            try {
                consoleUtil.write("Connecting to " + friendlyName + "...");
                btConn = (StreamConnection) Connector.open("btspp://" + mac + ":" + uuid.toString() + ";authenticate=false;encrypt=false;master=false;");
                consoleUtil.write("Connection established with bot: " + friendlyName);


                is = btConn.openDataInputStream();
                os = btConn.openDataOutputStream();
                String line;
                try {
                    // Stop here and doesn't progress
                    while ((line = is.readLine()) != null) {
                        if (!line.isEmpty()) {
                            String[] items = line.split("&");

                            if (items.length == 3) {

                                JSONObject json = new JSONObject();

                                json.put("name", friendlyName);
                                json.put("mac", mac);
                                json.put("speed", items[0]);
                                json.put("distance", items[1]);
                                json.put("time", items[2]);

                                socketManager.sendToAllClients("update_bot", json.toString());
                            }
                        }
                    }
                } catch (BluetoothConnectionException e) {
                    consoleUtil.write("Connection timed out to " + mac);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } catch (BluetoothConnectionException e) {
                consoleUtil.write("Failed to connect to: " + friendlyName + "\nError:\n" + e);
                JSONObject json = new JSONObject();
                json.put("mac", mac);

                socketManager.sendToAllClients("connection_failed", json.toString());
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        listener.start();
    }

    public void closeConnection() throws IOException {
        btConn.close();
        listener.stop();
    }

    public void sendMessage(int message){

        try {
            consoleUtil.write("Sending message: " + message);
            os.writeInt(message);
        } catch (IOException e) {
            e.printStackTrace();
            consoleUtil.write(e.getMessage());
        }


    }

    public String getMac() {
        return this.mac;
    }

    public String getFriendlyName() {
        return friendlyName;
    }

    public RemoteDevice getRemoteDevice() {
        return remoteDevice;
    }
}
