package Models;

import Managers.SocketManager;

import javax.bluetooth.RemoteDevice;
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

        try {
            btConn = (StreamConnection) Connector.open("btspp://" + getRemoteDevice().getBluetoothAddress() + ":2;authenticate=true;encrypt=true;master=false;");
            System.out.println("Connection established with bot: " + remoteDevice.getFriendlyName(false));

            if(remoteDevice.isAuthenticated()) {
                is = btConn.openDataInputStream();
                Thread listener = new Thread(() -> {
                    String line;
                    try {
                        // Stop here and doesn't progress
                        while ((line = is.readUTF()) != null) {
                            socketManager.sendToAllClients("connected", line);
                        }
                    } catch (IOException e) {

                    }
                });

                listener.start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public RemoteDevice getRemoteDevice() {
        return remoteDevice;
    }
}
