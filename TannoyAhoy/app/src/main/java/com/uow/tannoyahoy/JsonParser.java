package com.uow.tannoyahoy;


import android.util.Log;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.LinkedList;
import java.util.List;


/**
 * Created by Isa on 9/05/2015.
 */
public class JsonParser {

    private static final String TAG_SENDER = "sender";
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
            sender_ = new String(jsonObj.optString(TAG_SENDER).toString());

            // Returns the value mapped by name if it exists and is a JSONArray. Returns null otherwise.
            JSONArray jsonMainNode = jsonObj.optJSONArray(TAG_QUEUE);
            //creates array of data
             lstData_ = new LinkedList<Data>();

            // Process each JSON Node
            int lengthJsonArr = jsonMainNode.length();

            for (int i = 0; i < lengthJsonArr; i++) {
                // Get Object for each JSON node.
                JSONObject jsonChildNode = jsonMainNode.getJSONObject(i);

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

        } catch (JSONException e) {
            e.printStackTrace();
        }
        catch(Exception e) {
            e.printStackTrace();
        }
    }

    public JsonParser(JSONObject JsonText) {
        try {
            // Creates a new JSONObject with name/value mappings from the JSON string.
            sender_ = new String (JsonText.optString(TAG_SENDER).toString());

            //Returns the value mapped by name if it exists and is a JSONArray. Returns null otherwise.
            JSONArray jsonMainNode = JsonText.optJSONArray(TAG_QUEUE);
            lstData_ = new LinkedList<Data>();

            // Process each JSON Node
            int lengthJsonArr = jsonMainNode.length();

            for (int i = 0; i < lengthJsonArr; i++) {
                // Get Object for each JSON node.
                JSONObject jsonChildNode = jsonMainNode.getJSONObject(i);

                // Fetch node values
                String date = jsonChildNode.optString(TAG_TIME);
                String message = jsonChildNode.getString(TAG_MESSAGE);
                //Adding to list
                lstData_.add(new Data(date,message));
            }

            //writes list to debug if needed
            /*
            for (int i = 0; i < lstData_.size(); i++) {
                Log.d("Json", lstData_.get(i).Message());

            }*/

        } catch (JSONException e) {
            e.printStackTrace();
        }
        catch(Exception e) {
            e.printStackTrace();
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
