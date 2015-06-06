package com.uow.tannoyahoy;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;


public class ExpandMessageActivity extends ActionBarActivity {

    private TannoySpeech theTannoySpeech;
    private int theMessageID;
    private String currentSelectedZone = "";

    private static final String REMOVE_TAG = "TannRemoveClick";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expand_message);

        // Get the message from the intent
        Intent intent = getIntent();
        String message = intent.getStringExtra(MainActivity.EXPAND_MESSAGE);
        theMessageID = intent.getIntExtra(MainActivity.EXPAND_ID,-1);

        //Get the server
        currentSelectedZone = intent.getStringExtra(MainActivity.EXPAND_SERVER);

        // Fill the text view
        TextView theTextView = (TextView) findViewById(R.id.textToExpand);
        theTextView.setText(message);
        Log.d("GetID", "Expanded ID is:" + theMessageID);

        //Initialise Text To Speech
        theTannoySpeech = new TannoySpeech(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_login, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        int id = item.getItemId();

        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * This method is invoked when user clicks settings menu option
     * @param menuItem
     **/
    public void settingsClicked (MenuItem menuItem) {
        startActivity(new Intent(this,SettingsActivity.class));
    }

    /**Reads aloud the expanded message when the user clicks "read aloud"*/
    public void readExpandedMessage(View theView) {
        //get the text from the edit_message box, then turn it into a string
        String theTextToRead = ((TextView) findViewById(R.id.textToExpand)).getText().toString();

        theTannoySpeech.speak(theTextToRead);

    }
    /**remove messages from the server ***/
    public void removeTheItem(View theView) {
        // Get the RequestQueue
        RequestQueue theQueue = TannoyRequestQueueSingleton.getInstance(getApplicationContext()).
                getRequestQueue();

        String theURL = Constants.REMOVEURL;

        //try putting the JSON object together
        JSONObject theJsonObject = new JSONObject();
        try {

            theJsonObject.put("server",currentSelectedZone);
            theJsonObject.put("ID",theMessageID);
            theJsonObject.put("username",Settings.getInstance().getUsername());
            theJsonObject.put("password",Settings.getInstance().getPassword());

        } catch (JSONException e) {
            e.printStackTrace(); return;
        }

        Log.d(REMOVE_TAG, "Making removal: " + theJsonObject.toString());

        //send the JSON REMOVE request
        JsonObjectRequest theJsonRequest = new JsonObjectRequest
                (Request.Method.PUT, theURL, theJsonObject, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(REMOVE_TAG, "HTTP Response is: " + response);
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d(REMOVE_TAG, "HTTP Request didn't work!" + error);
                    }
                });

        theQueue.add(theJsonRequest);

        //go back to main activity, while the remove request is being sent
        Intent a = new Intent(theView.getContext(), MainActivity.class);
        a.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(a);
        Toast.makeText(getApplicationContext(), "Removal made", Toast.LENGTH_LONG).show();
    }
}
