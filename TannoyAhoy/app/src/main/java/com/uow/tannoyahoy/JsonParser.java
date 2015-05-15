package com.uow.tannoyahoy;


import android.util.Log;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;


/**
 * Created by Isa on 9/05/2015.
 * Adjusted on 14/5/15.
 */
public class JsonParser {

    private static final String TAG_NAME = "name";
    private static final String TAG_QUEUE = "queue";
    private static final String TAG_TIME = "time";
    private static final String TAG_MESSAGE = "message";
    private String sender_ = "";
    private List<Data> lstData_ = new LinkedList<Data>();
    public JsonParser(String strJsonText) {

        try {

            // Creates a new JSONObject with name/value mappings from the JSON string.
            JSONObject jsonObj = new JSONObject(strJsonText);


            // Grabs the sender and saves it to the sender string variable
            sender_ = jsonObj.optString(TAG_NAME);



            // Returns the value mapped by name if it exists and is a JSONArray. Returns null otherwise.
            //JSONArray jsonMainNode = jsonObj.optJSONArray(TAG_QUEUE);
            JSONObject jsonMainNode = jsonObj.optJSONObject(TAG_QUEUE);
            //creates array of data
            lstData_ = new LinkedList<Data>();

            // Process each JSON Node
            //int lengthJsonArr = jsonMainNode.length();
            Iterator<String> theKeys = jsonMainNode.keys();

            //New JSON parser code
            while(theKeys.hasNext()){
                String theCurrentKey = theKeys.next();
                JSONObject jsonChildNode = jsonMainNode.getJSONObject(theCurrentKey);

                // Fetch node values
                String date = jsonChildNode.optString(TAG_TIME);
                String message = jsonChildNode.getString(TAG_MESSAGE);
                // Adding to list
                lstData_.add(new Data(date,message));
            }

            //Old JSON parser code

            /*
            for (int i = 0; i < lengthJsonArr; i++) {
                // Get Object for each JSON node.
                JSONObject jsonChildNode = jsonMainNode.getJSONObject(i);

                // Fetch node values
                String date = jsonChildNode.optString(TAG_TIME);
                String message = jsonChildNode.getString(TAG_MESSAGE);
                // Adding to list
                lstData_.add(new Data(date,message));

            }
            */

            //writes list to debug if needed
            /*
            for (int i = 0; i < lstData_.size(); i++) {
                Log.d("Json", lstData_.get(i).Message());

            }*/

        } catch (JSONException jse) {

            jse.printStackTrace();

        }
        catch(Exception ex)
        {
            ex.printStackTrace();
        }
    }
    public JsonParser(JSONObject JsonText) {
        try {
            // Creates a new JSONObject with name/value mappings from the JSON string.


            // Grabs the sender and saves it to the sender string variable
            sender_ = JsonText.optString(TAG_NAME);



            // Returns the value mapped by name if it exists and is a JSONArray. Returns null otherwise.

            //JSONArray jsonMainNode = jsonObj.optJSONArray(TAG_QUEUE);
            JSONObject jsonMainNode = JsonText.optJSONObject(TAG_QUEUE);
            //creates array of data
            lstData_ = new LinkedList<Data>();

            // Process each JSON Node
            //int lengthJsonArr = jsonMainNode.length();
            Iterator<String> theKeys = jsonMainNode.keys();

            //New JSON parser code
            while(theKeys.hasNext()){
                String theCurrentKey = theKeys.next();
                JSONObject jsonChildNode = jsonMainNode.getJSONObject(theCurrentKey);

                // Fetch node values
                String date = jsonChildNode.optString(TAG_TIME);
                String message = jsonChildNode.getString(TAG_MESSAGE);
                // Adding to list
                lstData_.add(new Data(date,message));
            }
            //writes list to debug if needed
            /*
            for (int i = 0; i < lstData_.size(); i++) {
                Log.d("Json", lstData_.get(i).Message());

            }*/
        } catch (JSONException jse) {

            jse.printStackTrace();

        }
        catch(Exception ex)
        {

            ex.printStackTrace();
        }
    }
    public List<Data> getList()
    {
        return lstData_;
    }
    public String getSender()
    {
        return sender_;
    }
}
