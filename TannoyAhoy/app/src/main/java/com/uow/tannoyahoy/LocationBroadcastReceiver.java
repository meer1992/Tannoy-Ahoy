package com.uow.tannoyahoy;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import java.math.BigDecimal;
import java.util.LinkedList;

/**
 * Created by Pwnbot on 10/05/2015.
 */
public class LocationBroadcastReceiver extends BroadcastReceiver {
    //private SharedPreferences mPrefs;
    private static final String TAG = LocationBroadcastReceiver.class.getSimpleName();
    private LinkedList<Inequality> mInequalityList;
    private Activity mMainActivity;

    public LocationBroadcastReceiver(Activity activity) { mMainActivity = activity;}

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
            Log.d(TAG, intent.toString());
            tempHardcodeSetup();
            if (CurrentLocation.currentLocation != null) {
                if (!inequalityTest(new BigDecimal(CurrentLocation.currentLocation.getLatitude()), new BigDecimal(CurrentLocation.currentLocation.getLongitude()))) {
                    new DetermineClosestZone(TannoyZones.getInstance(), CurrentLocation.currentLocation).execute("");
                }
            }
            else { Log.d(TAG, "Current location is null"); }
        }
        else {
            Log.d(TAG, "Received unexpected intent " + intent.toString());
        }
    }

    private Boolean inequalityTest(BigDecimal xPos, BigDecimal yPos) {
        //get request from server to populate lists in TannoyZones
        //sanitise the keys from the server
        if (mInequalityList != null) {
            for (Inequality inequality : mInequalityList) {
                if (!inequality.testInequality(xPos, yPos)) {
                    Log.d(TAG, "failed inequality test");
                    return false;
                }
            }
            Log.d(TAG, "passed inequality test");
            return true;
        }
        else { Log.d(TAG, "Inequalities list is null"); return false; }
    }
    
    //TO BE REPLACED
    private void tempHardcodeSetup() {
        LinkedList<String> names = new LinkedList<String>();
        names.add("Auckland Aiport");
        names.add("Wellington Airport");
        names.add("Christchurch Airport");
        names.add("Wellington Railway Station");
        LinkedList<Location> locs = new LinkedList<Location>();
        Location loc = new Location("");
        loc.setLatitude(-37.007286);
        loc.setLongitude(174.784879);
        locs.add(loc);
        loc = new Location("");
        loc.setLatitude(-41.326712);
        loc.setLongitude(174.807385);
        locs.add(loc);
        loc = new Location("");
        loc.setLatitude(-43.484660);
        loc.setLongitude(172.537080);
        locs.add(loc);
        loc = new Location("");
        loc.setLatitude(-41.279067);
        loc.setLongitude(174.780195);
        locs.add(loc);
        TannoyZones.getInstance().setLocationNames(names);
        TannoyZones.getInstance().setLocations(locs);
    }

    private class DetermineClosestZone extends AsyncTask<String, String, Integer> {

        private TannoyZones tannoyZones;
        private Location currentLocation;
        private LinkedList<Inequality> inequalities;

        public DetermineClosestZone(TannoyZones zones, Location currentLoc) {
            tannoyZones = zones;
            currentLocation = currentLoc;
            inequalities = new LinkedList<Inequality>();
        }
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Integer doInBackground(String... params) {
            //initialise impossible values
            double smallestDistance = 1000000000;
            int smallDistanceIndex = -1;

            for (int i = 0; i < tannoyZones.getLocations().size(); i++) {
                double deltaX = tannoyZones.getLocations().get(i).getLatitude() - currentLocation.getLatitude();
                double deltaY = tannoyZones.getLocations().get(i).getLongitude() - currentLocation.getLongitude();
                //retains 10m accuracy
                double distance = Math.sqrt(Math.pow(deltaX, 2) + Math.pow(deltaY, 2));

                if (distance < smallestDistance) {
                    smallestDistance = distance;
                    smallDistanceIndex = i;
                }
            }
            Log.d(TAG, "smallest distance at index: " + smallDistanceIndex);
            for (int i = 0; i < tannoyZones.getLocations().size(); i++) {
                if (i != smallDistanceIndex) {
                    BigDecimal deltaX = new BigDecimal(tannoyZones.getLocations().get(i).getLatitude() - tannoyZones.getLocations().get(smallDistanceIndex).getLatitude());
                    BigDecimal deltaY = new BigDecimal(tannoyZones.getLocations().get(i).getLongitude() - tannoyZones.getLocations().get(smallDistanceIndex).getLongitude());

                    BigDecimal midPointX = new BigDecimal(tannoyZones.getLocations().get(smallDistanceIndex).getLatitude()).add(deltaX.divide(new BigDecimal(2)));
                    BigDecimal midPointY = new BigDecimal(tannoyZones.getLocations().get(smallDistanceIndex).getLongitude()).add(deltaY.divide(new BigDecimal(2)));

                    //normal to tho the line between the closest point and the current point is deltaY * y - deltaX * x - k = 0
                    BigDecimal normalConstant = (deltaY.multiply(midPointY).add(deltaX.multiply(midPointX)));
                    Inequality normalInequality = new Inequality(deltaX, deltaY, normalConstant);
                    normalInequality.setIsAboveLine(new BigDecimal(tannoyZones.getLocations().get(smallDistanceIndex).getLatitude()), new BigDecimal(tannoyZones.getLocations().get(smallDistanceIndex).getLongitude()));
                    inequalities.add(normalInequality);
                }
            }
            Log.d(TAG, "done in background");
            return new Integer(smallDistanceIndex);
        }

        @Override
        protected void onPostExecute(Integer smallestDistanceIndex) {
            super.onPostExecute(smallestDistanceIndex);

            //temporary placeholder for acknowledging closest location in spinner
            LinkedList<String> locationNames = new LinkedList<String>();
            //copy the location names
            for (String place : tannoyZones.getLocationNames()) { locationNames.add(place); }

            String closestPlace = tannoyZones.getLocationNames().get(smallestDistanceIndex).concat("*");
            Log.d(TAG, closestPlace);
            locationNames.set(smallestDistanceIndex, closestPlace);

            mInequalityList = inequalities; // update the broadcastreceiver's list of inequalities

            //update the spinner
            Spinner locationSpinner = (Spinner)mMainActivity.findViewById(R.id.headerSpinner);
            ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(mMainActivity, android.R.layout.simple_spinner_item, locationNames);
            spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item); // The drop down view
            locationSpinner.setAdapter(spinnerArrayAdapter);
        }
    }

    private class Inequality {
        public BigDecimal deltaX;
        public BigDecimal deltaY;
        public BigDecimal constant;
        public Boolean isAboveLine;

        public Inequality(BigDecimal xCoeff, BigDecimal yCoeff, BigDecimal k) {
            deltaX = xCoeff;
            deltaY = yCoeff;
            constant = k;
        }

        public void setIsAboveLine(BigDecimal x, BigDecimal y) {
            BigDecimal xProduct = deltaX.multiply(x);
            BigDecimal yProduct = deltaY.multiply(y);

            BigDecimal result = yProduct.add(xProduct).subtract(constant);
            isAboveLine = (result.compareTo(new BigDecimal(0)) > 0); // if the number is greater than 0, it is above the line
        }

        public Boolean testInequality(BigDecimal x, BigDecimal y) {
            BigDecimal xProduct = deltaX.multiply(x);
            BigDecimal yProduct = deltaY.multiply(y);
            BigDecimal result = yProduct.add(xProduct).subtract(constant);
            return (isAboveLine == (result.compareTo(new BigDecimal(0)) > 0)); // if on the correct side
        }

    }
}
