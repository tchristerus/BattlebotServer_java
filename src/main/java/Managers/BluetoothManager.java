package Managers;

import Utils.ConsoleUtil;

import javax.bluetooth.*;
import java.io.IOException;
import java.util.Vector;

public class BluetoothManager {
    public final Vector/*<RemoteDevice>*/ devicesDiscovered = new Vector();
    public BattlebotManager battlebotManager;
    private String btId;
    private ConsoleUtil consoleUtil;

    public BluetoothManager(BattlebotManager battlebotManager, String btId, ConsoleUtil consoleUtil) {
        this.battlebotManager = battlebotManager;
        this.btId = btId;
        this.consoleUtil = consoleUtil;
    }

    public void search() throws IOException, InterruptedException {

        final Object inquiryCompletedEvent = new Object();

        devicesDiscovered.clear();

        DiscoveryListener listener = new DiscoveryListener() {

            public void deviceDiscovered(RemoteDevice btDevice, DeviceClass cod) {

                try {
                    System.out.print(btDevice.getFriendlyName(false));
                } catch (IOException cantGetDeviceName) {
                }
                consoleUtil.write("=" + btDevice.getBluetoothAddress());
                devicesDiscovered.addElement(btDevice);
//                try {
//                    RemoteDeviceHelper.authenticate(btDevice, "1234");
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }

            }

            public void inquiryCompleted(int discType) {
                consoleUtil.write("Device Inquiry completed!");
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
                });
//                battlebotManager.connectAll();
            }
        }
    }
}
