package Models;

import Managers.SocketManager;
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
    // Streams
    DataInput is = null;
    DataOutput os = null;
    Thread listener;

    public Battlebot(SocketManager socketManager, String friendlyName, String mac) throws IOException {
       // this.remoteDevice = remoteDevice;
        this.socketManager = socketManager;
        this.mac = mac;
        this.friendlyName = friendlyName;
    }

    public void openConnection(){
        UUID uuid = new UUID("1", false);

//        UUID uuid = new UUID("00001101-0000-1000-8000-00805F9B34FB", false);
//        System.out.println("btspp://" + getRemoteDevice().getBluetoothAddress() + ":"+ uuid +";authenticate=false;encrypt=false;master=false;");
//        + ":"+ uuid.toString() +";authenticate=false;encrypt=false;master=false;"
        //98D3313079F7
        try {
            System.out.println("Connecting to " + mac + "...");
            btConn = (StreamConnection) Connector.open("btspp://" + mac + ":"+ uuid.toString() +";authenticate=false;encrypt=false;master=false;");
            System.out.println("Connection established with bot: " + mac);


            is = btConn.openDataInputStream();
             listener = new Thread(() -> {
                String line;
                try {
                    // Stop here and doesn't progress
                    while ((line = is.readLine()) != null) {
                        if(!line.isEmpty()) {
//                            socketManager.sendToAllClients("data_received", line);
                            //TODO sent to json
                            String[] items = line.split("&");

                            JSONObject json = new JSONObject();
                            json.put("name", friendlyName);
                            json.put("mac", mac);
                            json.put("speed", items[0]);
                            json.put("distance", items[1]);
                            json.put("time", items[2]);

                            socketManager.sendToAllClients("update_bot", json.toString());
                        }
                    }
                }catch(BluetoothConnectionException e){
                    System.out.println("Connection timed out to " + mac);
                } catch (IOException e) {
                    e.printStackTrace();
                }
             });

            listener.start();

        } catch (IOException e) {
            e.printStackTrace();
            System.out.println(remoteDevice.getBluetoothAddress()+ "Adress");
        }
    }

    public void closeConnection() throws IOException{
        btConn.close();
        listener.stop();
    }

    public String getMac(){
        return this.mac;
    }
    public RemoteDevice getRemoteDevice() {
        return remoteDevice;
    }
}
