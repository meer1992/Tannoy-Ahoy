package com.uow.tannoyahoy;

/**
 * Created by Pwnbot on 2/05/2015.
 */
public class Settings {
    private static Settings ourInstance;
    private Boolean mHasBackgroundUpdates;
    private Boolean mHasBackgroundAlerts;
    private Boolean loggedIn;
    private Boolean hasProximityUpdates;
    private long mReconnectInterval;
    private long mLocationInterval;
    private long mFastestLocationInterval;
    private String username;
    private String password;
    private int powerSettingsIndex;

    private long mAnnouncementUpdateInterval;


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

    public void setLoggedIn(boolean value) {
        loggedIn = value;
    }

    public void setUsername(String username) {this.username = username;}

    public void setPassword(String password) {this.password = password;}

    public void setFastestLocationInterval(long value) {
        if (value < mLocationInterval) {mFastestLocationInterval = value;}
        else {mLocationInterval = mFastestLocationInterval = value;}
    }

    public void setAnnouncementUpdateInterval(long value) {mAnnouncementUpdateInterval = value*1000;}

    //legacy code, still might be implemented/useful
    public Boolean hasBackgroundUpdates() {return mHasBackgroundUpdates;}

    //legacy code, still might be implemented/useful
    public Boolean hasBackgroundAlerts() {return  mHasBackgroundAlerts;}

    public Boolean getHasProximityUpdates() { return  hasProximityUpdates; }

    public void setHasProximityUpdates(Boolean updates) { hasProximityUpdates = updates; }

    public void setHasBackgroundUpdates(boolean value){mHasBackgroundUpdates = value;}

    public void setHasBackgroundAlerts(boolean value){mHasBackgroundAlerts = value;}

    public long getLocationUpdateInterval() {return mLocationInterval;}

    public long getFastestLocationUpdateInterval() {return mFastestLocationInterval;}

    public long getReconnectInterval() {return mReconnectInterval;}

    public int getPowerSettingsIndex() { return  powerSettingsIndex; }

    public void setPowerSettingsIndex(int newSetting) { powerSettingsIndex = newSetting; }

    public Boolean getLoggedIn()  {return loggedIn;}

    public String getUsername() {return username;}

    public String getPassword() {return password;}

    public long getAnnouncementUpdateInterval() {return mAnnouncementUpdateInterval;}

    //legacy code, still might be implemented/useful
    public void setReconnectInterval(long value) {mReconnectInterval = value; }


    public void resetSettings() {
        mHasBackgroundUpdates = Constants.DEFAULT_HAS_BACKGROUND_LOCATION_UPDATES;
        mHasBackgroundAlerts = Constants.DEFAULT_HAS_LOCATION_NOTIFICATIONS;
        mLocationInterval = Constants.DEFAULT_LOCATION_UPDATE_INTERVAL;
        mFastestLocationInterval = Constants.DEFAULT_FASTEST_LOCATION_UPDATE_INTERVAL;
        mReconnectInterval = Constants.DEFAULT_RECONNECT_TO_LOCATION_CLIENT_INTERVAL;
        mAnnouncementUpdateInterval=Constants.DEFAULT_ANNOUNCEMENT_UPDATE_INTERVAL;
        hasProximityUpdates = Constants.DEFAULT_HAS_PROXIMITY_UPDATES;
        loggedIn = Constants.DEFAULT_LOGGEDIN;
        powerSettingsIndex = Constants.DEFAULT_POWER_SETTINGS_INDEX;
    }
}
