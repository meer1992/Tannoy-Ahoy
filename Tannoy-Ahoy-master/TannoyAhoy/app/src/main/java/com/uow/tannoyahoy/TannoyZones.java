package com.uow.tannoyahoy;

import android.location.Location;

import java.util.LinkedList;

/**
 * Created by Pwnbot on 24/05/2015.
 */
public class TannoyZones {
    public static TannoyZones ourInstance;

    //parallel lists
    private LinkedList<Location> locations;
    private LinkedList<String> locationNames;

    public static TannoyZones getInstance() {
        if (ourInstance == null) { ourInstance = new TannoyZones(); }
        return ourInstance;
    }

    private TannoyZones() {
        locationNames = new LinkedList<String>();
        locations = new LinkedList<Location>();
    }

    public void setLocations(LinkedList<Location> locs) { locations = locs; }

    public void setLocationNames(LinkedList<String> names) { locationNames = names; }

    public LinkedList<String> getLocationNames() { return locationNames; }

    public LinkedList<Location> getLocations() { return locations; }
}
