package Utils;

import Managers.SocketManager;

public class ConsoleUtil {
    private SocketManager socketManager;

    public ConsoleUtil(SocketManager socketManager){
        this.socketManager = socketManager;
    }

    public void write(String message){
        System.out.println(message);
        socketManager.sendToAllClients("console_message", message);
    }
}
