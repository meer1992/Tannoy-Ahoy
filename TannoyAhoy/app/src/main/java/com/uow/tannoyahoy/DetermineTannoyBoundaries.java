package com.uow.tannoyahoy;

import android.app.Activity;
import android.location.Location;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import java.math.BigDecimal;
import java.util.LinkedList;

/**
 * Created by Pwnbot on 30/05/2015.
 */
public class DetermineTannoyBoundaries extends AsyncTask<String, String, Integer> {

    private TannoyZones tannoyZones;
    private LinkedList<LinkedList<Inequality>> inequalities;
    private final String TAG = DetermineTannoyBoundaries.class.getSimpleName();

    public DetermineTannoyBoundaries(TannoyZones zones) {
        tannoyZones = zones;
        inequalities = new LinkedList<LinkedList<Inequality>>();
    }

    @Override
    protected Integer doInBackground(String... params) {
        for (int closestPlaceIndex = 0; closestPlaceIndex < tannoyZones.getLocations().size(); closestPlaceIndex++) {
            LinkedList<Inequality> inequalityList = new LinkedList<Inequality>();
            for (int i = 0; i < tannoyZones.getLocations().size(); i++) {
                if (i != closestPlaceIndex) {
                    Log.d("test", i + "location:" + " lat: " + tannoyZones.getLocations().get(i).getLatitude() + " long: " +tannoyZones.getLocations().get(i).getLongitude());
                    Log.d("test", closestPlaceIndex + "location:" + " lat: " + tannoyZones.getLocations().get(closestPlaceIndex).getLatitude() + " long: " +tannoyZones.getLocations().get(closestPlaceIndex).getLongitude());
                    BigDecimal deltaX = new BigDecimal(tannoyZones.getLocations().get(i).getLatitude() - tannoyZones.getLocations().get(closestPlaceIndex).getLatitude());
                    BigDecimal deltaY = new BigDecimal(tannoyZones.getLocations().get(i).getLongitude() - tannoyZones.getLocations().get(closestPlaceIndex).getLongitude());

                    BigDecimal midPointX = new BigDecimal(tannoyZones.getLocations().get(closestPlaceIndex).getLatitude()).add(deltaX.divide(new BigDecimal(2)));
                    BigDecimal midPointY = new BigDecimal(tannoyZones.getLocations().get(closestPlaceIndex).getLongitude()).add(deltaY.divide(new BigDecimal(2)));

                    //normal to tho the line between the closest point and the current point is deltaY * y + deltaX * x - k = 0
                    BigDecimal normalConstant = (deltaY.multiply(midPointY).add(deltaX.multiply(midPointX)));
                    Inequality normalInequality = new Inequality(deltaX, deltaY, normalConstant);
                    Log.d("test", "deltaX" + deltaX.toString() + " deltaY" + deltaY.toString() + " constant" + normalConstant.toString());
                    normalInequality.setIsAboveLine(new BigDecimal(tannoyZones.getLocations().get(closestPlaceIndex).getLatitude()), new BigDecimal(tannoyZones.getLocations().get(closestPlaceIndex).getLongitude()));
                    inequalityList.add(normalInequality);
                }
            }
            //Log.d("inequalityList length:", "" + inequalityList.size());
            inequalities.add(inequalityList);
        }
        tannoyZones.setBoundaries(inequalities);
        Log.d(TAG, "done in background");
        return null;
    }
}
