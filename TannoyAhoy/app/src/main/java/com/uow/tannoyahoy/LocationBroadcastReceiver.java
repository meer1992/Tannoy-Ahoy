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
/*    private LinkedList<Inequality> mInequalityList;*/
    private Activity mainActivity;
    private static LocationBroadcastReceiver ourInstance;

    private LocationBroadcastReceiver() {}

    public static LocationBroadcastReceiver getInstance() {
        if (ourInstance == null) { ourInstance = new LocationBroadcastReceiver(); }
        return ourInstance;
    }

    /**
     * Keeps reference of the creator of this, so that when the DetermineClosestPlace task wishes to update the location spinner, it has access to the activity's UI elements.
     * @param activity Reference to the activity that created this
     */
    public void setRootActivity(Activity activity) { mainActivity = activity; }

    /**
     * Checks to see whether the intent was one this receiver has indicated interest in. If it is, it performs the associated response code
     * @param context Where the signal came from
     * @param intent The signal sent through the LocalBroadcastManager
     */
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
            //tempHardcodeSetup();
/*            if (CurrentLocation.currentLocation != null) {
                if (!inequalityTest(new BigDecimal(CurrentLocation.currentLocation.getLatitude()), new BigDecimal(CurrentLocation.currentLocation.getLongitude()))) {
                    new DetermineClosestZone(TannoyZones.getInstance(), CurrentLocation.currentLocation).execute("");
                }
            }
            else { Log.d(TAG, "Current location is null"); }*/
            new DetermineClosestPlace(TannoyZones.getInstance()).execute("");

        }
        else {
            Log.d(TAG, "Received unexpected intent " + intent.toString());
        }
    }

    private class DetermineClosestPlace extends AsyncTask<String, String, LinkedList<String>> {
        private TannoyZones tannoyZones;

        public DetermineClosestPlace(TannoyZones zones) { tannoyZones = zones; }

        /**
         * Tests whether the current closest place is still the closest place. If it is not, then it checks the other inequality lists to see which of those is the new closest place.
         * @param params
         * @return
         */
        @Override
        protected LinkedList<String> doInBackground(String... params) {
            //if fails the test
            if (!inequalityTest(tannoyZones.getBoundaries().get(tannoyZones.getClosestZoneIndex()), new BigDecimal(CurrentLocation.currentLocation.getLatitude()), new BigDecimal(CurrentLocation.currentLocation.getLongitude()))) {
                for (int i = 0; i < tannoyZones.getBoundaries().size(); i++) {
                    // loop through each other inequalityList
                    if (i != tannoyZones.getClosestZoneIndex()) {
                        //if passes the test
                        if (inequalityTest(tannoyZones.getBoundaries().get(i), new BigDecimal(CurrentLocation.currentLocation.getLatitude()), new BigDecimal(CurrentLocation.currentLocation.getLongitude()))) {
                            //set this inequalityList to the new closest place
                            tannoyZones.setClosestZoneIndex(i);
                            Log.d(TAG, "Changed closest place to " + i);
                            break;
                        }
                    }
                }
            }
            else { Log.d(TAG, "Location unchanged"); }
            //add a * to closest place
            LinkedList locationNames = (LinkedList<String>)tannoyZones.getLocationNames().clone();
            String closestPlace = tannoyZones.getLocationNames().get(tannoyZones.getClosestZoneIndex()).concat("*");
            locationNames.set(tannoyZones.getClosestZoneIndex(), closestPlace);
            return locationNames;
        }

        /**
         *  Updates the location spinner on the main activity to indicate the closest place, and set the spinner's selection the closest place if the user specifies it in the settings
         * @param locationNames the list of places that support announcements
         */
        @Override
        protected void onPostExecute(LinkedList<String> locationNames) {
            super.onPostExecute(locationNames);

            //update the spinner
            Spinner locationSpinner = (Spinner) mainActivity.findViewById(R.id.headerSpinner);
            int selectedPosition = locationSpinner.getSelectedItemPosition();
            ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(mainActivity, R.layout.spinner_header_main, locationNames);
            locationSpinner.setAdapter(spinnerArrayAdapter);

            // if user wants spinner to retain previous selection
            if (!Settings.getInstance().getHasProximityUpdates()) { locationSpinner.setSelection(selectedPosition); }
            //if user wants spinner to automatically set to closest place
            else { locationSpinner.setSelection(tannoyZones.getClosestZoneIndex()); }
        }

        /**
         * Tests whether an inequality list, tied to a given place, is closest.
         * @param inequalityList list of inequalities to test
         * @param xPos x-coord of user's location
         * @param yPos y-coord of user's location
         * @return returns true if the user's location was on the correct side of all inequalities, else returns false
         */
        private Boolean inequalityTest(LinkedList<Inequality> inequalityList, BigDecimal xPos, BigDecimal yPos) {
            //get request from server to populate lists in TannoyZones
            //sanitise the keys from the server
            if (inequalityList != null) {
                for (Inequality inequality : inequalityList) { // loop through each inequality
                    if (!inequality.testInequality(xPos, yPos)) { // if it fails once, the whole test fails
                        Log.d(TAG, "failed inequality test");
                        return false;
                    }
                }
                Log.d(TAG, "passed inequality test");
                return true;
            }
            else { Log.d(TAG, "Inequalities list is null"); return false; }
        }
    }
/*
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
*/

/*    private class DetermineClosestZone extends AsyncTask<String, String, Integer> {

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
            tannoyZones.setClosestZone(closestPlace);
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

    }*/
}
