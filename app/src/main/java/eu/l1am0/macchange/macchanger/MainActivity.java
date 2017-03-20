package eu.l1am0.macchange.macchanger;

import android.Manifest;
import android.content.Context;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.support.v4.app.ActivityCompat;
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
import java.util.concurrent.TimeUnit;

import static android.location.GpsStatus.GPS_EVENT_STARTED;
import static android.location.GpsStatus.GPS_EVENT_STOPPED;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /*Get the Application Context*/
        final Context context = getApplicationContext();

        systemHelpers.registerMacChangeRecievers(context);



        /*Get current Mac*/
        if(!systemHelpers.canSU()){
            setMacAdressToTextView("No Su");
        }else {

            if (!systemHelpers.hasBusyBox(context)) {
                setMacAdressToTextView("No Bussy");
            } else {
                setMacAdressToTextView(systemHelpers.getMacAddress());
            }
        }



        /*Button Listener*/
        final Button changeMac = (Button) findViewById(R.id.macChangeButton);
        changeMac.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {

                systemHelpers.setRandomMacAddress(1);

                WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
                wifiManager.setWifiEnabled(false);
                wifiManager.setWifiEnabled(true);

                setMacAdressToTextView(systemHelpers.getMacAddress());


            }
        });
    }

    private void setMacAdressToTextView(String macAdress){
        TextView macText = (TextView) findViewById(R.id.macText);
        macText.setText(macAdress);
    }

}
