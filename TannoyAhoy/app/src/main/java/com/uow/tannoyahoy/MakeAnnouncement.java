package com.uow.tannoyahoy;

import android.content.Intent;
import android.speech.RecognizerIntent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;


public class MakeAnnouncement extends ActionBarActivity {

    private static final int SPEECH_REQUEST_CODE = 0;

    private static final String ANNOUNCETAG = "TannAnounceClick";

    Spinner theSpinner;

    private String currentSelectedZone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_make_announcement);

        theSpinner = (Spinner) findViewById(R.id.announceHeaderSpinner);
        theSpinner.setAdapter(
                new ArrayAdapter<String>(this, R.layout.spinner_header_main, TannoyZones.getInstance().getLocationNames())
        );

        currentSelectedZone = TannoyZones.getInstance().getLocationNames().get(TannoyZones.getInstance().getClosestZoneIndex());

        theSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                currentSelectedZone = TannoyZones.getInstance().getLocationNames().get(position);

                Log.d("AnnounceSpinnerUpdate", "updated");
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        setUpMakeAnnouncementButton();


    }

    private void setUpMakeAnnouncementButton(){
        Button makeAnnouncement = (Button) findViewById(R.id.makeAnnouncementBtn);

        makeAnnouncement.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                /***Putting messages onto the server ***/

                // Get the RequestQueue
                RequestQueue theQueue = TannoyRequestQueueSingleton.getInstance(getApplicationContext()).
                        getRequestQueue();

                //DON't need to replace spaces with %20s for JSON
                String theServer = currentSelectedZone;

                String theURL = Constants.ADDURL;

                //get message from textbox
                String theMessage = ((EditText)findViewById(R.id.etMakeAnnouncement)).getText().toString();

                //try putting the JSON object together
                JSONObject theJsonObject = new JSONObject();
                try {

                    theJsonObject.put("server",currentSelectedZone);
                    theJsonObject.put("message",theMessage);
                    theJsonObject.put("username",Settings.getInstance().getUsername());
                    theJsonObject.put("password",Settings.getInstance().getPassword());

                } catch (JSONException e) {
                    e.printStackTrace(); return;
                }

                Log.d(ANNOUNCETAG, "Making request: " + theJsonObject.toString());

                //send the JSON ADD request
                JsonObjectRequest theJsonRequest = new JsonObjectRequest
                        (Request.Method.PUT, theURL, theJsonObject, new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                Log.d(ANNOUNCETAG, "HTTP Response is: " + response);
                            }
                        }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Log.d(ANNOUNCETAG, "HTTP Request didn't work!" + error);
                            }
                        });

                theQueue.add(theJsonRequest);

                //go back to main activity, while the request is being sent

                Intent a = new Intent(v.getContext(), MainActivity.class);
                a.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(a);
                Toast.makeText(getApplicationContext(), "Announcement made", Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_make_announcement, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
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

    /**Voice recognition component*/
    public void recogniseVoice(View theView) {
        // Create an intent that can start the Speech Recognizer activity
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        // Start the activity, the intent will be populated with the speech text
        startActivityForResult(intent, SPEECH_REQUEST_CODE);
    }

    /**Activated after google completes voice recognition*/
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //if the speech recognition worked, output to a TextView
        if (requestCode == SPEECH_REQUEST_CODE && resultCode == RESULT_OK) {
            List<String> results = data.getStringArrayListExtra(
                    RecognizerIntent.EXTRA_RESULTS);
            String spokenText = results.get(0);
            //find the text box to output to, cast it to TextView, set its text to the output
            ((EditText)findViewById(R.id.etMakeAnnouncement)).setText(spokenText);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
