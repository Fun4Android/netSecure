package eu.l1am0.macchange.macchanger;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.Toast;


public class ConnectivityChangeReciever extends BroadcastReceiver {


    @Override
    public void onReceive(Context aContext, Intent intent) {
        CharSequence text = "Connection Changed!";
        int duration = Toast.LENGTH_SHORT;

        Toast toast = Toast.makeText(aContext, text, duration);
        toast.show();


    }

}
