package Models;

import Managers.SocketManager;
import org.json.JSONObject;

import javax.bluetooth.UUID;
import javax.microedition.io.Connector;
import javax.microedition.io.StreamConnection;
import java.io.DataInput;
import java.io.IOException;

public class ConnectionListener extends Thread {

    private volatile StreamConnection btConn;
    private volatile SocketManager socketManager;
    private volatile String mac;
    private volatile String friendlyName;
    private boolean isRunning;
    // Streams
    private volatile DataInput is = null;


    public ConnectionListener(SocketManager socketManager, String friendlyName, String mac) throws IOException {
        this.socketManager = socketManager;
        this.mac = mac;
        this.friendlyName = friendlyName;
        this.isRunning = true;

        UUID uuid = new UUID("1", false);
        btConn = (StreamConnection) Connector.open("btspp://" + mac + ":"+ uuid.toString() +";authenticate=false;encrypt=false;master=false;");
    }

    @Override
    public void run() {
        try{

            System.out.println("Connecting to " + mac + "...");

            System.out.println("Connection established with bot: " + mac);


            while(isRunning) {
                readLine();
            }

        } catch (Exception e) {

        } finally {
            System.out.println("Connection stopped");
            Thread.currentThread().interrupt();
        }
    }

    public void readLine() {
        try{
            is = btConn.openDataInputStream();
            String line = is.readLine();

            if(line != null) {
                String[] items = line.split("&");

                JSONObject json = new JSONObject();
                json.put("name", friendlyName);
                json.put("mac", mac);
                json.put("speed", items[0]);
                json.put("distance", items[1]);
                json.put("time", items[2]);

                socketManager.sendToAllClients("update_bot", json.toString());
            }
            else{
                isRunning = false;
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
