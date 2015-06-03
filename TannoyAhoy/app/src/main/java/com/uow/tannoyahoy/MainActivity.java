package com.uow.tannoyahoy;


import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;


public class MainActivity extends ActionBarActivity {
//    UserLocalStore userLocalStore;

    ListView theListview;
    Spinner theSpinner;
    private LocationBroadcastReceiver locationBroadcastReceiver = LocationBroadcastReceiver.getInstance();
    private Context thisContext;
    private String currentSelectedZone = "";
    private String theFilter = "";
    static private  boolean updateThreadStarted = false;
    private static String previousResponse = "[]";
    private static String currentResponse = "[]";
    private final static String TAG = "TannoyMain";
    public final static String EXPAND_MESSAGE = "TannoyExpandMessage";

    private final static String strNewJsonExample =
            "{\"name\":\"Test\",\"queue\":{\"128\":{\"time\":\"2015-05-14T16:11:15.0770064\",\"ID\":128,\"message\":\"first message in here\",\"sender\":\"hat\"},\"129\":{\"time\":\"2015-05-14T16:11:15.2080644\",\"ID\":129,\"message\":\"second message in here\",\"sender\":\"hat\"},\"130\":{\"time\":\"2015-05-14T16:11:15.3591318\",\"ID\":130,\"message\":\"third message in here\",\"sender\":\"hat\"},\"131\":{\"time\":\"2015-05-14T16:11:15.5392119\",\"ID\":131,\"message\":\"fourth message in here\",\"sender\":\"hat\"},\"132\":{\"time\":\"2015-05-14T16:11:15.702285\",\"ID\":132,\"message\":\"fifth message in here\",\"sender\":\"hat\"},\"133\":{\"time\":\"2015-05-14T16:11:15.913377\",\"ID\":133,\"message\":\"sixth message in here\",\"sender\":\"hat\"},\"134\":{\"time\":\"2015-05-14T16:11:16.0384326\",\"ID\":134,\"message\":\"seventh and final message in here\",\"sender\":\"hat\"}},\"next_id\":135}";

    private final static String messageJson =
            "[{\"time\":\"2015-05-23T13:18:26.257385\",\"ID\":0,\"message\":\"foobar\"},{\"time\":\"2015-05-23T15:16:06.482569\",\"ID\":4,\"message\":\"Singapore Airlines SG03 gate changed from gate 3 to gate 4\"},{\"time\":\"2015-05-23T15:16:07.781032\",\"ID\":8,\"message\":\"your message in here\"},{\"time\":\"2015-05-23T15:16:04.657521\",\"ID\":1,\"message\":\"your message in here\"},{\"time\":\"2015-05-23T15:16:06.875664\",\"ID\":5,\"message\":\"your message in here\"},{\"time\":\"2015-05-23T15:16:05.631447\",\"ID\":2,\"message\":\"your message in here\"},{\"time\":\"2015-05-23T15:16:07.229624\",\"ID\":6,\"message\":\"your message in here\"},{\"time\":\"2015-05-23T15:16:06.157877\",\"ID\":3,\"message\":\"your message in here\"},{\"time\":\"2015-05-23T15:16:07.507202\",\"ID\":7,\"message\":\"your message in here\"}]";

    //private final static String[] arrayOfLocations = {"Auckland Airport","Britomart Transport Centre","Christchurch Airport","Wellington Airport","Wellington Railway Station"};


    private JsonParser theMainJsonParser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //define the activity layout.
        setContentView(R.layout.activity_main);
        //example of json call (can use a straight json object or a string formated to Json standard
        //theMainJsonParser = new JsonParser(strNewJsonExample);
        //Log.d(TAG, strJsonParserExample);

        //Also perform initial setup of activity components
        thisContext = this;




        //setup the location-backend
        setupReceiver();
        startAutoUpdate();
        startService(new Intent(thisContext, BackgroundLocationService.class));
        if (TannoyZones.getInstance().getBoundaries() == null) { new DetermineTannoyBoundaries(TannoyZones.getInstance()).execute(""); }
        //Also perform initial setup of activity components

//        userLocalStore = new UserLocalStore(this);

        //handle going to another activity and bringing the selected list item with it
        theListview = (ListView) findViewById(R.id.listViewMain);
        theListview.setOnItemClickListener(
                new AdapterView.OnItemClickListener() {

                    @Override
                    public void onItemClick(AdapterView<?> arg0, View view,
                                            int position, long id) {
                        Object theObject = theListview.getItemAtPosition(position);
                        String theMessage = theObject.toString();
                        Log.d(TAG, "Message is: " + theMessage);

                        Intent intent = new Intent(thisContext, ExpandMessageActivity.class);
                        intent.putExtra(EXPAND_MESSAGE, theMessage);
                        startActivity(intent);
                    }
                }
        );

        theSpinner = (Spinner) findViewById(R.id.headerSpinner);
        theSpinner.setAdapter(
                new ArrayAdapter<String>(this, R.layout.spinner_header_main, TannoyZones.getInstance().getLocationNames())
        );

        currentSelectedZone = TannoyZones.getInstance().getLocationNames().get(TannoyZones.getInstance().getClosestZoneIndex());

        theSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                currentSelectedZone = TannoyZones.getInstance().getLocationNames().get(position);

                updateListViewMain(null);
                Log.d("MainSpinnerUpdate", "updated");
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


    }
    private void startAutoUpdate()
    {
       /* new Thread(new Runnable() {
            public void run() {
               /* Timer timer = new Timer();
                timer.schedule(new TimerTask()
                {
                    @Override
                    public void run()
                        {


                    }
                }, 0,Settings.getInstance().getAnnouncementUpdateInterval());
                new Timer().scheduleAtFixedRate(new TimerTask() {
                    public void run() {
                        updateListViewMain(null);
                        Log.d("AutoUpdate", "it auto updated");
                    }
                }, 0, Settings.getInstance().getAnnouncementUpdateInterval());//put here time 1000 milliseconds=1 second
            }
        }).start();*/

        if(updateThreadStarted == false) {
            updateThreadStarted = true;

            ScheduledExecutorService exec = Executors.newSingleThreadScheduledExecutor();
            exec.scheduleAtFixedRate(new Runnable() {
                @Override
                public void run() {
                    // do stuff
                    updateListViewMain(null);
                    if (currentResponse.equals(previousResponse))
                    {
                    }
                    else
                    {
                        Log.d("AutoUpdate", "put notification here");
                    }
                    previousResponse = currentResponse;



                Log.d("AutoUpdate","it auto updated");
            }
        }, 0, Settings.getInstance().getAnnouncementUpdateInterval(), TimeUnit.MILLISECONDS);

        }
        else
        {
            Log.d("AutoUpdate", "It shouldn't update");

        }



    }

    private void setupReceiver() {
            LocationBroadcastReceiver.getInstance().setRootActivity(this);
            LocalBroadcastManager.getInstance(App.context).registerReceiver(locationBroadcastReceiver, new IntentFilter(Constants.CONNECTED_ACTION));
            LocalBroadcastManager.getInstance(App.context).registerReceiver(locationBroadcastReceiver, new IntentFilter(Constants.CONNECTION_FAILED_ACTION));
            LocalBroadcastManager.getInstance(App.context).registerReceiver(locationBroadcastReceiver, new IntentFilter(Constants.CONNECTION_SUSPENDED_ACTION));
            LocalBroadcastManager.getInstance(App.context).registerReceiver(locationBroadcastReceiver, new IntentFilter(Constants.LOCATION_CHANGED_ACTION));
            LocalBroadcastManager.getInstance(App.context).registerReceiver(locationBroadcastReceiver, new IntentFilter(Constants.BOOT_COMPLETED_ACTION));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        MenuItem item = menu.findItem(R.id.action_logout);
        MenuItem item2 = menu.findItem(R.id.action_login);

        if (Settings.getInstance().getLoggedIn() == false) {
            item.setVisible(false);
        } else {
            item2.setVisible(false);
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem item = menu.findItem(R.id.action_make_announcement);

        if (Settings.getInstance().getLoggedIn() == true) {
            item.setEnabled(true);
            item.getIcon().setAlpha(255);
        } else {
            // disabled
            item.getIcon().setAlpha(130);
        }
        return super.onPrepareOptionsMenu(menu);
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

    public void logoutClicked (MenuItem menuitem) {
        Settings.getInstance().setLoggedIn(false);
        Toast.makeText(getApplicationContext(), "Logged out", Toast.LENGTH_LONG).show();
        startActivity(new Intent(this, MainActivity.class));
    }



    /**
     * This method is invoked when user clicks make announcement menu option
     * @param menuItem
     **/
    public void makeAnnouncementClicked (MenuItem menuItem) {
        //add code to check if logged in
        if (Settings.getInstance().getLoggedIn() == true) {
            startActivity(new Intent(this, MakeAnnouncement.class));
        } else {
            // disabled
            Toast.makeText(getApplicationContext(), "Please Login first", Toast.LENGTH_LONG).show();
        }
    }




    /**Gets the message queue when user clicks "update", then calls directUpdateListViewMain*/
    public void updateListViewMain(View theView) {
        // TODO: Periodically update
        // TODO: Wrap into a nice method like string s = sendToServer("GETLIST",null)

        //hardcoded string
        /*
        theMainJsonParser = new JsonParser(messageJson);
        directUpdateListViewMain();
        */

        //RequestQueue theQueue = Volley.newRequestQueue(this);

        // Get the RequestQueue
        RequestQueue theQueue = TannoyRequestQueueSingleton.getInstance(this.getApplicationContext()).
                getRequestQueue();

        //NOTE: 10.0.2.2 is for emulators only.
        //Uses HTTPS
        theFilter = currentSelectedZone.replace(" ", "%20");
        String theURL = Constants.URL + theFilter;
        Log.d("VolleyGet", "it was called");

        StringRequest theStringRequest = new StringRequest
                (Request.Method.GET, theURL,
                        new Response.Listener<String>() {

                            @Override
                            public void onResponse(String response) {
                                Log.d(TAG, "HTTP Response is: " + response);
                                Log.d("VolleyGet", "inside the response");
                                currentResponse = response;

                                theMainJsonParser = new JsonParser(response);
                                directUpdateListViewMain();
                            }
                        }, new Response.ErrorListener() {
                            @Override
                             public void onErrorResponse(VolleyError error) {
                                Log.d(TAG, "HTTP Request didn't work!" + error);
                            }
                        });

        theQueue.add(theStringRequest);

        /*
        //specifically request a JSON object from the server
        JsonObjectRequest theJsonRequest = new JsonObjectRequest
                (Request.Method.GET, theURL, null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(TAG, "HTTP Response is: " + response);

                        theMainJsonParser = new JsonParser(response);
                        directUpdateListViewMain();
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("main", "**6**");
                        Log.d(TAG, "HTTP Request didn't work!" + error);
                    }
                });
        */
    }
    /**Directly updates the main list of messages when user clicks "update"*/
    public void directUpdateListViewMain(){
        //adapter which essentially calls toString() on a list of objects, then passes it into a listView
        ArrayAdapter<Data> theAdapter;
        theAdapter = new ArrayAdapter<Data>(this,R.layout.list_item_main,theMainJsonParser.getList());
        //theListview = (ListView) findViewById(R.id.listViewMain);
        theListview.setAdapter(theAdapter);
    }

    /**Clears the main list of messages when user clicks "clear"*/
    public void clearListViewMain(View theView) {
        //theListview = (ListView) findViewById(R.id.listViewMain);
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

}
