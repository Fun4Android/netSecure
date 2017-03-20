package eu.l1am0.macchange.macchanger;

import android.content.Context;
import android.content.IntentFilter;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.wifi.WifiManager;
import android.provider.Settings;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.util.Random;

/**
 * Created by yooui on 20.03.17.
 */

public class systemHelpers {

    /**
     * Check wheater the device is rooted
     * @return true if rooted
     * @return false if not rooted
     */
    public static boolean canSU() {
        Process process = null;
        int exitValue = -1;
        try {
            process = Runtime.getRuntime().exec("su");
            DataOutputStream toProcess = new DataOutputStream(process.getOutputStream());
            toProcess.writeBytes("exec id\n");
            toProcess.flush();
            exitValue = process.waitFor();
        } catch (Exception e) {
            exitValue = -1;
        }
        return exitValue == 0;
    }

    public static boolean hasBusyBox(Context aContext) {
        String command = executeCommandAsSu("busybox");
        boolean hasBusy = false;

        if(command.length() != 0){
            hasBusy = true;
        }

        return hasBusy;
    }

    /**
     * Exectute the given command in a system shell
     * @param command The to execute command, su is automaticly applied
     * @return the result of the command
     */
    public static String executeCommandAsSu(String command) {
        String line, returnString = null;
        StringBuilder log = new StringBuilder();

        try {
            Process rootProcess = Runtime.getRuntime().exec(new String[]{"su", "-c", command});


            /*Get the read and write streams*/
            BufferedReader osRes = new BufferedReader(new InputStreamReader(rootProcess.getInputStream()));


            if (null != osRes) {
                // Grab the results
                while ((line = osRes.readLine()) != null) {
                    log.append(line);
                }
                //Close Streams
                osRes.close();

                returnString = log.toString();
            }
        } catch (Exception e) {
        }

        return returnString;
    }

    /**
     * Get the current mac address from ifconfig - su required
     * @return current mac address
     */
    public static String getMacAddress(){
        String ifConf = systemHelpers.executeCommandAsSu("busybox ifconfig wlan0");

        int startPosition = ifConf.indexOf("HWaddr")+ "HWaddr".length() + 1;
        int endPosition = startPosition + 17;

        return ifConf.substring(startPosition,endPosition);
    }


    /**
     * Register the broadcastrecievers for getting different network changes
     *
     * @param aContext Application context
     */
    public static void registerMacChangeRecievers(Context aContext) {
        aContext.registerReceiver(
                new ConnectivityChangeReciever(),
                new IntentFilter(
                        ConnectivityManager.CONNECTIVITY_ACTION));
        aContext.registerReceiver(
                new ConnectivityChangeReciever(),
                new IntentFilter(
                        WifiManager.WIFI_STATE_CHANGED_ACTION));
        aContext.registerReceiver(
                new ConnectivityChangeReciever(),
                new IntentFilter(LocationManager.PROVIDERS_CHANGED_ACTION));

    }

    /**
     * Set a new MacAddress for device
     * @param method how to change the mac 0 = hw ether, 1 = echo > wcnss_mac_addr
     * @param newMac
     */
    public static void changeMacAddress(int method, String newMac){
        switch (method){
            case 0:
                systemHelpers.executeCommandAsSu("busybox ifconfig wlan0 hw ether "+ newMac);
                break;
            case 1:
                systemHelpers.executeCommandAsSu("echo " + newMac + " > /sys/devices/fb000000.qcom,wcnss-wlan/wcnss_mac_addr");
                break;
            default:
                break;
        }
    }

    /**
     * Activates the RFC 3041 privacy extension
     */
    public static void activateIPv6Random(){
        systemHelpers.executeCommandAsSu("echo \"2\">/proc/sys/net/ipv6/conf/wlan0/use_tempaddr");
    }

    /**
     * Set new Macadress random
     * @param method how to change the mac 0 = hw ether, 1 = echo > wcnss_mac_addr
     */
    public static void setRandomMacAddress(int method){
        changeMacAddress(method, randomMacAdress());
    }

    /**
     * Generate a randomMacAddress
     * right now we dont care about NIC standards
     * @return randomMac
     */
    public static String randomMacAdress(){
            Random rand = new Random();
            byte[] macAddr = new byte[6];
            rand.nextBytes(macAddr);

            macAddr[0] = (byte)(macAddr[0] & (byte)254);  //zeroing last 2 bytes to make it unicast and locally adminstrated

            StringBuilder sb = new StringBuilder(18);
            for(byte b : macAddr){

                if(sb.length() > 0)
                    sb.append(":");

                sb.append(String.format("%02x", b));
            }


            return sb.toString();
    }

}
