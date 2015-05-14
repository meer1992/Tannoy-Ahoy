package com.uow.tannoyahoy;

/**
 * Created by Pwnbot on 2/05/2015.
 */
public class Settings {
    private static Settings ourInstance;
    private Boolean mHasBackgroundUpdates;
    private Boolean mHasBackgroundAlerts;
    private long mReconnectInterval;
    private long mLocationInterval;
    private long mFastestLocationInterval;


    public static Settings getInstance() {
        if (ourInstance == null) {ourInstance = new Settings();}
        return ourInstance;
    }

    private Settings() {
        resetSettings();
    }

    //legacy code, still might be implemented/useful
    public void toggleBackgroundUpdates() {
        mHasBackgroundUpdates = !mHasBackgroundUpdates;
    }

    //legacy code, still might be implemented/useful
    public void toggleBackgroundAlerts() {
        mHasBackgroundAlerts = !mHasBackgroundAlerts;
        mHasBackgroundUpdates = !mHasBackgroundUpdates;
    }

    public void setLocationInterval(long value) {mLocationInterval = value;}

    public void setFastestLocationInterval(long value) {
        if (value < mLocationInterval) {mFastestLocationInterval = value;}
        else {mLocationInterval = mFastestLocationInterval = value;}
    }

    //legacy code, still might be implemented/useful
    public Boolean hasBackgroundUpdates() {return mHasBackgroundUpdates;}

    //legacy code, still might be implemented/useful
    public Boolean hasBackgroundAlerts() {return  mHasBackgroundAlerts;}

    public long getLocationUpdateInterval() {return mLocationInterval;}

    public long getFastestLocationUpdateInterval() {return mFastestLocationInterval;}

    public long getReconnectInterval() {return mReconnectInterval; }

    //legacy code, still might be implemented/useful
    public void setReconnectInterval(long value) {mReconnectInterval = value; }


    public void resetSettings() {
        mHasBackgroundUpdates = Constants.DEFAULT_HAS_BACKGROUND_LOCATION_UPDATES;
        mHasBackgroundAlerts = Constants.DEFAULT_HAS_LOCATION_NOTIFICATIONS;
        mLocationInterval = Constants.DEFAULT_LOCATION_UPDATE_INTERVAL;
        mFastestLocationInterval = Constants.DEFAULT_FASTEST_LOCATION_UPDATE_INTERVAL;
        mReconnectInterval = Constants.DEFAULT_RECONNECT_TO_LOCATION_CLIENT_INTERVAL;
    }
}
