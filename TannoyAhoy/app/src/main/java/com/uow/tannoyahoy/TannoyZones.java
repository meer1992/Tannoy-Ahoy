package com.uow.tannoyahoy;

import android.location.Location;
import android.util.Log;

import java.util.LinkedList;

/**
 * Created by Pwnbot on 24/05/2015.
 */
public class TannoyZones {
    public static TannoyZones ourInstance;

    //parallel lists
    private LinkedList<Location> locations;
    private LinkedList<String> locationNames;
    private LinkedList<LinkedList<Inequality>> boundaries;
    //private String closestZone;
    private int closestZoneIndex;

    public static TannoyZones getInstance() {
        if (ourInstance == null) { ourInstance = new TannoyZones(); }
        return ourInstance;
    }

    private TannoyZones() {
        locationNames = new LinkedList<String>();
        locations = new LinkedList<Location>();
        locationNames.add("Auckland Airport");
        locationNames.add("Britomart Transport Centre");
        locationNames.add("Christchurch Airport");
        locationNames.add("Wellington Airport");
        locationNames.add("Wellington Railway Station");

        Location loc = new Location("");
        loc.setLatitude(-37.007286);
        loc.setLongitude(174.784879);
        locations.add(loc); //auckland

        loc = new Location("");
        loc.setLatitude(-36.843330);
        loc.setLongitude(174.766897);
        locations.add(loc); //Britomart

        loc = new Location("");
        loc.setLatitude(-41.326712);
        loc.setLongitude(174.807385);
        locations.add(loc); //Christchurch

        loc = new Location("");
        loc.setLatitude(-43.484660);
        loc.setLongitude(172.537080);
        locations.add(loc);//Wellington Airport

        loc = new Location("");
        loc.setLatitude(-41.279067);
        loc.setLongitude(174.780195);
        locations.add(loc); //Welling Railway

        //closestZone = locationNames.get(0);
        closestZoneIndex = 0;
    }

    //public void setLocations(LinkedList<Location> locs) { locations = locs; }

    //public void setLocationNames(LinkedList<String> names) { locationNames = names; }

    public LinkedList<String> getLocationNames() { return locationNames; }

    public LinkedList<Location> getLocations() { return locations; }

    //public String getClosestZone() { return  closestZone; }

    //public void setClosestZone(String place) { closestZone = place; }

    public LinkedList<LinkedList<Inequality>> getBoundaries() { return boundaries; }

    public void setBoundaries(LinkedList<LinkedList<Inequality>> boundaries1) { boundaries = boundaries1; }

    public void setClosestZoneIndex(int closestZoneIndex1) { closestZoneIndex = closestZoneIndex1; }

    public int getClosestZoneIndex() { return closestZoneIndex; }
}
