package eu.l1am0.macchange.macchanger;

import android.widget.TextView;

import java.io.BufferedReader;
import java.io.InputStreamReader;

/**
 * Created by yooui on 15.03.17.
 */

public class MacChanger {
    public void changeMac(String newMac){
        executeCommand("busybox ifconfig wlan0 hw ether " + newMac);
    }

    private String executeCommand(String command){
        String line, returnString = null;
        StringBuilder log = new StringBuilder();

        try {
            Process rootProcess = Runtime.getRuntime().exec(new String[]{"su","-c",command});


            /*Get the read and write streams*/
            BufferedReader osRes  = new BufferedReader(new InputStreamReader(rootProcess.getInputStream()));



            if (null != osRes) {
                // Grab the results
                while ((line = osRes.readLine()) != null) {
                    log.append(line);
                }
                //Close Streams
                osRes.close();

                returnString = log.toString();
            }
        } catch(Exception e){
        }

        return returnString;
    }
}
