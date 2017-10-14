package Models;

import javax.bluetooth.RemoteDevice;

public class Battlebot {
    private RemoteDevice remoteDevice;

    public Battlebot(RemoteDevice remoteDevice){
        this.remoteDevice = remoteDevice;
    }

    public RemoteDevice getRemoteDevice() {
        return remoteDevice;
    }
}
