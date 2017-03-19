package eu.l1am0.macchange.macchanger;

import android.content.Context;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Inet6Address;
import java.net.NetworkInterface;
import java.util.Collections;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /*Get the Application Context*/
        Context context = getApplicationContext();

        /*Get current Mac*/
        if(canSU()){
            setMacAdressToTextView(executeCommand("busybox ifconfig wlan0\n"));
        }

        /*Button Listener*/
        final Button changeMac = (Button) findViewById(R.id.macChangeButton);
        changeMac.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                executeCommand("busybox ifconfig wlan0 hw ether 99:00:22:33:44:00");
                setMacAdressToTextView(executeCommand("busybox ifconfig wlan0"));
            }
        });
    }

    public void setMacAdressToTextView(String macAdress){
        TextView macText = (TextView) findViewById(R.id.macText);
        macText.setText(macAdress);
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

    private boolean canSU() {
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
}
