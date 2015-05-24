package com.uow.tannoyahoy;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import java.text.DateFormat;
import java.util.Date;

/**
 * Created by Pwnbot on 9/05/2015.
 */
public class LocationThread extends Thread implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, LocationListener{


    private static LocationThread ourInstance;
    private GoogleApiClient googleApiClient;
    private Boolean isRunning = false;
    private Boolean hasStarted = false;
    private static final String TAG = LocationThread.class.getSimpleName();

    private LocationThread() { //one-time initialisation
        hasStarted = false;
        googleApiClient = new GoogleApiClient.Builder(App.context)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
    }

    public static LocationThread getInstance() {
        if (ourInstance == null) { ourInstance = new LocationThread(); }
        return ourInstance;
    }

    private void initialise() {
        isRunning = true;
        Log.d(TAG, "onInitialise");
    }

    @Override
    public synchronized void start() {
        super.start();
        hasStarted = true;
        initialise();
        Log.d(TAG, "onStart");
    }

    @Override
    public void run() {
        super.run();
        if (isRunning) { //if active thread, attempt to reconnect if not connected
            Log.d(TAG, "running");
            if (!googleApiClient.isConnected() && !googleApiClient.isConnecting()) {
                googleApiClient.connect();
                Log.d(TAG, "attempting to connect");
            }
        }
    }

    public void onConnected(Bundle bundle) {
        LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, createLocationRequest(), this);

        //notify anyone who cares of connection success
        Intent connectedIntent = new Intent(Constants.CONNECTED_ACTION);
        LocalBroadcastManager.getInstance(App.context).sendBroadcast(connectedIntent);

        Log.d(TAG, "onConnected");

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        if (connectionResult.hasResolution()) {

            //notify anyone who cares of connection failure
            Intent connectionFailedIntent = new Intent(Constants.CONNECTION_FAILED_ACTION);
            connectionFailedIntent.putExtra(Constants.CONNECTION_RESULT_TAG, connectionResult);
            LocalBroadcastManager.getInstance(App.context).sendBroadcast(connectionFailedIntent);
        }
        yield();
        Log.d(TAG, "onConnectionFailed");
    }

    @Override
    public void onConnectionSuspended(int i) {
        LocationServices.FusedLocationApi.removeLocationUpdates(googleApiClient, this);

        //connect suspended broadcast
        Intent connectionSuspendedIntent = new Intent(Constants.CONNECTION_SUSPENDED_ACTION);
        connectionSuspendedIntent.putExtra(Constants.CONNECTION_SUSPENDED_ERROR_CODE_TAG, i);
        LocalBroadcastManager.getInstance(App.context).sendBroadcast(connectionSuspendedIntent);

        yield();
        Log.d(TAG, "onConnectionSuspended");
    }

    @Override
    public void onLocationChanged(Location location) {

        //notify anyone who cares of latest position
        Intent locationChangedIntent = new Intent(Constants.LOCATION_CHANGED_ACTION);
        CurrentLocation.currentLocation = location;
        CurrentLocation.updateTime = DateFormat.getDateTimeInstance().format(new Date());
        LocalBroadcastManager.getInstance(App.context).sendBroadcast(locationChangedIntent);

        yield();
        Log.d(TAG, "onLocationChanged");
    }

    private LocationRequest createLocationRequest() {
        LocationRequest mLocationRequest = new LocationRequest();
        Settings settings = Settings.getInstance();
        mLocationRequest.setInterval(settings.getLocationUpdateInterval());
        mLocationRequest.setFastestInterval(settings.getFastestLocationUpdateInterval());
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        return mLocationRequest;
    }

    public Boolean isRunning() {
        return isRunning;
    }

    //turn the thread on or off
    public void setRunning(Boolean newState) {
        isRunning = newState;
        if (isRunning == false && googleApiClient.isConnected()) {
            if (googleApiClient.isConnected()) { googleApiClient.disconnect(); }
        }
        else { initialise(); run(); }
    }

    public Boolean hasStarted() { return hasStarted; }
}
