package Managers;

import com.intel.bluetooth.RemoteDeviceHelper;

import javax.bluetooth.*;
import java.io.IOException;
import java.util.Vector;

public class BluetoothManager {
    public final Vector/*<RemoteDevice>*/ devicesDiscovered = new Vector();
    public BattlebotManager battlebotManager;

    public BluetoothManager(BattlebotManager battlebotManager) {
        this.battlebotManager = battlebotManager;
    }

    public void search() throws IOException, InterruptedException {

        final Object inquiryCompletedEvent = new Object();

        devicesDiscovered.clear();

        DiscoveryListener listener = new DiscoveryListener() {

            public void deviceDiscovered(RemoteDevice btDevice, DeviceClass cod) {
                System.out.println("Device " + btDevice.getBluetoothAddress() + " found");
                devicesDiscovered.addElement(btDevice);
                try {
                    RemoteDeviceHelper.authenticate(btDevice, "1234");
                } catch (IOException e) {
                    e.printStackTrace();
                }
                try {
                    System.out.println("     name " + btDevice.getFriendlyName(false));
                } catch (IOException cantGetDeviceName) {
                }
            }

            public void inquiryCompleted(int discType) {
                System.out.println("Device Inquiry completed!");
                synchronized (inquiryCompletedEvent) {
                    inquiryCompletedEvent.notifyAll();
                }
            }

            public void serviceSearchCompleted(int transID, int respCode) {
            }

            public void servicesDiscovered(int transID, ServiceRecord[] servRecord) {
            }
        };
        synchronized (inquiryCompletedEvent) {
            boolean started = LocalDevice.getLocalDevice().getDiscoveryAgent().startInquiry(DiscoveryAgent.GIAC, listener);
            if (started) {
                inquiryCompletedEvent.wait();
                devicesDiscovered.forEach(battlebot -> {
                    RemoteDevice device = (RemoteDevice) battlebot;
                    try {
                        if (device.getFriendlyName(false).contains("bt")) {
                            battlebotManager.createBattlebot(device);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
                System.out.println("Total bots: " + battlebotManager.getTotalBots());
            }
        }
    }
}
