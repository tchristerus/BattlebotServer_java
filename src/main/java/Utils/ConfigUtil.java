package Utils;

import Structs.BattlebotStruct;

import java.io.*;
import java.util.ArrayList;

public class ConfigUtil {
    private FileInputStream fstream;
    private BufferedReader br;

    public ConfigUtil(String filename) throws FileNotFoundException {
        fstream = new FileInputStream(filename);
        br = new BufferedReader(new InputStreamReader(fstream));
    }


    // bot11=macAddress
    // bot17=macAdress
    public ArrayList<BattlebotStruct> parseConfig() throws IOException {
        String strLine;
        ArrayList<BattlebotStruct> botsStructs = new ArrayList<>();

        while ((strLine = br.readLine()) != null) {

            if (strLine.charAt(0) != '#') {
                String[] parts = strLine.split("=");

                // creating the bot struct and adding it to the list
                BattlebotStruct btBotStruct = new BattlebotStruct();
                btBotStruct.botName = parts[0];
                btBotStruct.macAddress = parts[1];
                botsStructs.add(btBotStruct);

            }
        }

        return botsStructs;
    }
}
