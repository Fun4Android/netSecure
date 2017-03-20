package eu.l1am0.macchange.macchanger;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.Toast;

public class BootReciever extends BroadcastReceiver{

        @Override
        public void onReceive(Context aContext, Intent intent) {
            CharSequence text = "Bootup ::DDDDDD!";
            int duration = Toast.LENGTH_LONG;

            Toast toast = Toast.makeText(aContext, text, duration);
            toast.show();
            systemHelpers.changeMacAddress(1, "88:3E:D3:44:33:23");

        }

}
