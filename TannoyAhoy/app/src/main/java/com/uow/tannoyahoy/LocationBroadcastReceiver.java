package com.uow.tannoyahoy;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

/**
 * Created by Pwnbot on 10/05/2015.
 */
public class LocationBroadcastReceiver extends BroadcastReceiver {
    //private SharedPreferences mPrefs;
    private static final String TAG = LocationBroadcastReceiver.class.getSimpleName();

    //filters for intents produced by the locationService
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(Constants.BOOT_COMPLETED_ACTION)) {
/*            boolean mUpdatesRequested = false;
            mPrefs = context.getSharedPreferences("SharedPreferences", Context.MODE_PRIVATE);

            if (mPrefs.contains("KEY_UPDATES_ON")) {
                mUpdatesRequested = mPrefs.getBoolean("KEY_UPDATES_ON", false);
            }
            if (mUpdatesRequested) {
                ComponentName component = new ComponentName(context.getPackageName(), BackgroundLocationService.class.getName());
                ComponentName service = context.startService(new Intent().setComponent(component));

                if (service == null) {
                    Log.d(TAG, "Could not start service " + component.toString());
                }
            }*/
        }
        else if (intent.getAction().equals(Constants.CONNECTED_ACTION)) {
            //do something
            Log.d(TAG, intent.toString());
        }
        else if (intent.getAction().equals(Constants.CONNECTION_FAILED_ACTION)) {
            // do something
            Log.d(TAG, intent.toString());
        }
        else if (intent.getAction().equals(Constants.CONNECTION_SUSPENDED_ACTION)) {
            //do something
            Log.d(TAG, intent.toString());
        }
        else if (intent.getAction().equals(Constants.LOCATION_CHANGED_ACTION)) {
            // do something
            Log.d(TAG, intent.toString());
        }
        else {
            Log.d(TAG, "Received unexpected intent " + intent.toString());
        }
    }
}
