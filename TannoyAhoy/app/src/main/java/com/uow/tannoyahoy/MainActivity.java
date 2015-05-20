package com.uow.tannoyahoy;


import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;



public class MainActivity extends ActionBarActivity {
//    UserLocalStore userLocalStore;

    private TannoySpeech theTannoySpeech;
    private LocationBroadcastReceiver locationBroadcastReceiver;
    public final static String LOG_TAG = "TannoyMainActivity";
    private final String strJsonParserExample =
            "{\"sender\":\"Test Server\",\"queue\":[{\"time\":\"2015-04-07T20:14:05.335358\",\"message\":\"This is a test message\"}" +
                                                  ",{\"time\":\"2015-04-07T20:14:56.4748071\",\"message\":\"This is some more stuff being added\"}" +
                                                  ",{\"time\":\"2015-04-07T20:15:26.4748071\",\"message\":\"Singapore Airlines SG03 gate changed from gate 3 to gate 4\"}]}";
    private final static String strNewJsonExample =
            "{\"name\":\"Test\",\"queue\":{\"128\":{\"time\":\"2015-05-14T16:11:15.0770064\",\"ID\":128,\"message\":\"first message in here\",\"sender\":\"hat\"},\"129\":{\"time\":\"2015-05-14T16:11:15.2080644\",\"ID\":129,\"message\":\"second message in here\",\"sender\":\"hat\"},\"130\":{\"time\":\"2015-05-14T16:11:15.3591318\",\"ID\":130,\"message\":\"third message in here\",\"sender\":\"hat\"},\"131\":{\"time\":\"2015-05-14T16:11:15.5392119\",\"ID\":131,\"message\":\"fourth message in here\",\"sender\":\"hat\"},\"132\":{\"time\":\"2015-05-14T16:11:15.702285\",\"ID\":132,\"message\":\"fifth message in here\",\"sender\":\"hat\"},\"133\":{\"time\":\"2015-05-14T16:11:15.913377\",\"ID\":133,\"message\":\"sixth message in here\",\"sender\":\"hat\"},\"134\":{\"time\":\"2015-05-14T16:11:16.0384326\",\"ID\":134,\"message\":\"seventh and final message in here\",\"sender\":\"hat\"}},\"next_id\":135}";

    private JsonParser theMainJsonParser;
    private static final int SPEECH_REQUEST_CODE = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //define the activity layout.
        setContentView(R.layout.activity_main);
        //example of json call (can use a straight json object or a string formated to Json standard
        //theMainJsonParser = new JsonParser(strNewJsonExample);
        //Log.d(LOG_TAG, strJsonParserExample);

        //setup the location-backend
        locationBroadcastReceiver = new LocationBroadcastReceiver();
        setupReceiver();
        startService(new Intent(App.context, BackgroundLocationService.class));

        //Also perform initial setup of activity components
        //such as initialising Text To Speech
        theTannoySpeech = new TannoySpeech(this);

//        userLocalStore = new UserLocalStore(this);
    }

    private void setupReceiver() {
        registerReceiver(locationBroadcastReceiver, new IntentFilter(Constants.CONNECTED_ACTION));
        registerReceiver(locationBroadcastReceiver, new IntentFilter(Constants.BOOT_COMPLETED_ACTION));
        registerReceiver(locationBroadcastReceiver, new IntentFilter(Constants.CONNECTION_FAILED_ACTION));
        registerReceiver(locationBroadcastReceiver, new IntentFilter(Constants.CONNECTION_SUSPENDED_ACTION));
        registerReceiver(locationBroadcastReceiver, new IntentFilter(Constants.LOCATION_CHANGED_ACTION));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    /**
     * This method is invoked when user clicks settings menu option
     * @param menuItem
     **/
    public void settingsClicked (MenuItem menuItem) {
        startActivity(new Intent(this, SettingsActivity.class));
    }
    /**
     * This method is invoked when user clicks about menu option
     * @param menuItem
     */
    public void aboutClicked (MenuItem menuItem) {
        startActivity(new Intent(this,AboutActivity.class));
    }

    /**
     * This method is invoked when user clicks login menu option
     * @param menuItem
     **/
    public void loginClicked (MenuItem menuItem) {
        startActivity(new Intent(this,LoginActivity.class));
    }

    /**
     * This method is invoked when user clicks make announcement menu option
     * @param menuItem
     **/
    public void makeAnnouncementClicked (MenuItem menuItem) {
        //add code to check if logged in

        startActivity(new Intent(this,MakeAnnouncement.class));
        Toast.makeText(getApplicationContext(),"Please Login first", Toast.LENGTH_LONG).show();
    }

    /**Gets the message queue when user clicks "update", then calls directUpdateListViewMain*/
    public void updateListViewMain(View theView) {
        // TODO: Move this action into a singleton instance, and periodically update
        RequestQueue theQueue = Volley.newRequestQueue(this);
        //NOTE: 10.0.2.2 is for emulators only.
        String theURL = "http://10.0.2.2:8080/queue?server=Test";

        JsonObjectRequest theJsonRequest = new JsonObjectRequest
                (Request.Method.GET, theURL, null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(LOG_TAG, "HTTP Response is: " + response);

                        theMainJsonParser = new JsonParser(response);
                        directUpdateListViewMain();
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d(LOG_TAG, "HTTP Request didn't work!" + error);
                    }
                });
        //Old code handling a string instead. To be removed later on.
        /*
        StringRequest theStringRequest = new StringRequest
                (Request.Method.GET, theURL,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                // Log the first 500 characters of the response string.
                                Log.d(LOG_TAG, "HTTP Response is: " + response);

                                theMainJsonParser = new JsonParser(response);
                                directUpdateListViewMain();
                            }
                        }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Log.d(LOG_TAG, "HTTP Request didn't work!" + error);
                            }
                        });
        */
        theQueue.add(theJsonRequest);
    }
    /**Directly updates the main list of messages when user clicks "update"*/
    public void directUpdateListViewMain(){
        //adapter which essentially calls toString() on a list of objects, then passes it into a listView
        ArrayAdapter<Data> theAdapter;
        theAdapter = new ArrayAdapter<Data>(this,R.layout.list_item_main,theMainJsonParser.getList());
        ListView theListview = (ListView) findViewById(R.id.listViewMain);
        theListview.setAdapter(theAdapter);
    }
    /**Clears the main list of messages when user clicks "clear"*/
    public void clearListViewMain(View theView) {
        ListView theListview = (ListView) findViewById(R.id.listViewMain);
        theListview.setAdapter(null);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopService(new Intent(App.context, BackgroundLocationService.class));
    }

    /**An example of using TannoySpeech. Buttons can call this with XML android:onClick="sendMessage"*/

    /*public void sendMessage(View theView) {
        //get the text from the edit_message box, then turn it into a string
        String theTextToRead = ((EditText) findViewById(R.id.edit_message)).getText().toString();

        theTannoySpeech.speak(theTextToRead);
    }*/

    /**Voice recognition component*/

    /*public void getVoice(View theView) {
        // Create an intent that can start the Speech Recognizer activity
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        // Start the activity, the intent will be populated with the speech text
        startActivityForResult(intent, SPEECH_REQUEST_CODE);
    }*/

    /**Activated after google completes voice recognition*/

    /*@Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //if the speech recognition worked, output to a TextView
        if (requestCode == SPEECH_REQUEST_CODE && resultCode == RESULT_OK) {
            List<String> results = data.getStringArrayListExtra(
                    RecognizerIntent.EXTRA_RESULTS);
            String spokenText = results.get(0);
            //find the text box to output to, cast it to TextView, set its text to the output
            ((TextView)findViewById(R.id.textViewVoiceOut)).setText(spokenText);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }*/
}
