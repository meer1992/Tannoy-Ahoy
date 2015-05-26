package com.uow.tannoyahoy;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Created by Pwnbot on 4/05/2015.
 *
 */
public class BackgroundLocationService extends Service {

    private LocationThread thread;
    private BroadcastReceiver receiver;
    private ScheduledExecutorService scheduledExecutor;
    private boolean schedulerStarted = false;
    private static final String TAG = BackgroundLocationService.class.getSimpleName();

    @Override
    public void onCreate() {
        super.onCreate();
        setupReceiver();
        Log.d(TAG, "onCreate");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);

        //make thread go
        thread = LocationThread.getInstance();
        if (thread.hasStarted()) { thread.setRunning(true); }
        else { thread.start(); }

        //Schedule checks for whether a reconnect to client is necessary every X seconds
        scheduledExecutor = Executors.newScheduledThreadPool(1); //1 core
        Settings settings = Settings.getInstance();
        scheduledExecutor.scheduleAtFixedRate(thread, settings.getReconnectInterval(), settings.getReconnectInterval(), TimeUnit.MILLISECONDS);

        Log.d(TAG, "onStart");
        return START_STICKY;
    }

    private void setupReceiver() {
        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction().equals(Constants.CONNECTED_ACTION)) {
                    if (schedulerStarted) {
                        scheduledExecutor.shutdown();
                        schedulerStarted = false;
                    }
                }
                else if (intent.getAction().equals(Constants.CONNECTION_FAILED_ACTION) || intent.getAction().equals(Constants.CONNECTION_SUSPENDED_ACTION)) {
                    if (!schedulerStarted) {
                        scheduledExecutor = Executors.newScheduledThreadPool(1); //1 core
                        Settings settings = Settings.getInstance();
                        scheduledExecutor.scheduleAtFixedRate(thread, settings.getReconnectInterval(), settings.getReconnectInterval(), TimeUnit.MILLISECONDS);
                        schedulerStarted = true;
                    }
                }
                else { Log.d(TAG, "Received unexpected intent"); }
            }
        };

        LocalBroadcastManager.getInstance(App.context).registerReceiver(receiver, new IntentFilter(Constants.CONNECTED_ACTION));
        LocalBroadcastManager.getInstance(App.context).registerReceiver(receiver, new IntentFilter(Constants.CONNECTION_FAILED_ACTION));
        LocalBroadcastManager.getInstance(App.context).registerReceiver(receiver, new IntentFilter(Constants.CONNECTION_SUSPENDED_ACTION));
    }
    private Boolean serviceConnected() { //not used atm
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        return ConnectionResult.SUCCESS == resultCode;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    } //this service cannot be bound

    @Override
    public void onDestroy() {
        thread.setRunning(false); //turn off the thread
        scheduledExecutor.shutdown(); // stop the scheduler
        super.onDestroy();
        Log.d(TAG, "onDestroy");

    }

}