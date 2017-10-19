package Models;

import Managers.SocketManager;

import javax.bluetooth.RemoteDevice;
import javax.bluetooth.UUID;
import javax.microedition.io.Connector;
import javax.microedition.io.StreamConnection;
import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class Battlebot {
    private RemoteDevice remoteDevice;
    private StreamConnection btConn;
    private SocketManager socketManager;

    // Streams
    DataInput is = null;
    DataOutput os = null;

    public Battlebot(RemoteDevice remoteDevice, SocketManager socketManager) throws IOException {
        this.remoteDevice = remoteDevice;
        this.socketManager = socketManager;
    }

    public void openConnection(){
//        UUID uuid = new UUID("00001101-0000-1000-8000-00805F9B34FB", false);

        UUID uuid = new UUID("d0c722b07e1511e1b0c40800200c9a66", false);
        System.out.println("btspp://" + getRemoteDevice().getBluetoothAddress() + ":"+ uuid +";authenticate=false;encrypt=false;master=false;");
        try {
            btConn = (StreamConnection) Connector.open("btspp://" + getRemoteDevice().getBluetoothAddress() + ":"+ uuid.toString() +";authenticate=false;encrypt=false;master=false;");
            System.out.println("Connection established with bot: " + remoteDevice.getFriendlyName(false));

            is = btConn.openDataInputStream();
            Thread listener = new Thread(() -> {
                int line;
                try {
                    // Stop here and doesn't progress
                    while ((line = is.readInt()) != -1) {
                        socketManager.sendToAllClients("data_received", line);
                    }
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

    public RemoteDevice getRemoteDevice() {
        return remoteDevice;
    }
}
