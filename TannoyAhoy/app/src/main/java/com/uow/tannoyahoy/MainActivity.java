package com.uow.tannoyahoy;


import android.app.Activity;
import android.content.Intent;
import android.speech.RecognizerIntent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import java.util.List;



public class MainActivity extends ActionBarActivity {
    private TannoySpeech theTannoySpeech;
    final String strJsonParserExample = "{\"sender\":\"Test Server\",\"queue\":[{\"time\":\"2015-04-07T20:14:05.335358\",\"message\":\"This is a test message\"}" +
            ",{\"time\":\"2015-04-07T20:14:56.4748071\",\"message\":\"This is some more stuff being added\"}]}";

    private static final int SPEECH_REQUEST_CODE = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //define the activity layout.
        setContentView(R.layout.activity_main);
        //example of json call (can use a straight json object or a string formated to Json standard
        JsonParser json = new JsonParser(strJsonParserExample);


        //Also perform initial setup of activity components
        //such as initialising Text To Speech
        theTannoySpeech = new TannoySpeech(this);
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

    /**Starts the "about" activity when the user clicks "about"*/
    public void gotoAbout(View theView) {
        startActivity(new Intent(this,about.class));
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
