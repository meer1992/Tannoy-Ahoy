package com.example.hat.tannoybasicio;


import android.content.Intent;
import android.speech.RecognizerIntent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import java.util.List;


public class MainActivity extends ActionBarActivity {
    private TannoySpeech theTannoySpeech;

    private static final int SPEECH_REQUEST_CODE = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //define the activity layout.
        setContentView(R.layout.activity_main);

        //Also performs initial setup of activity components
        //such as initialising Text To Speech
        theTannoySpeech = new TannoySpeech(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        //return true;
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    /**called when the user clicks "send". An example of using TannoySpeech*/
    public void sendMessage(View theView) {
        //get the text from the edit_message box, then turn it into a string
        String theTextToRead = ((EditText) findViewById(R.id.edit_message)).getText().toString();

        theTannoySpeech.speak(theTextToRead);
    }

    /**Voice recognition component*/
    public void getVoice(View theView) {
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
            ((TextView)findViewById(R.id.textViewVoiceOut)).setText(spokenText);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
