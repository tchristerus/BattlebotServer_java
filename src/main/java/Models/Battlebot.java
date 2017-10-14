package Models;

import javax.bluetooth.RemoteDevice;
import javax.microedition.io.Connector;
import javax.microedition.io.StreamConnection;
import java.io.IOException;

public class Battlebot {
    private RemoteDevice remoteDevice;
    private StreamConnection btConn;

    public Battlebot(RemoteDevice remoteDevice) {
        this.remoteDevice = remoteDevice;
        try {
            btConn = (StreamConnection) Connector.open("btspp://" + getRemoteDevice().getBluetoothAddress() + ":2;authenticate=true;encrypt=true;master=false;");
            System.out.println("Connection established with bot: " + remoteDevice.getFriendlyName(false));
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public RemoteDevice getRemoteDevice() {
        return remoteDevice;
    }
}
