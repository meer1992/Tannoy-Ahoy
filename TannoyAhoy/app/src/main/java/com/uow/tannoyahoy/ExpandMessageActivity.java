package com.uow.tannoyahoy;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;


public class ExpandMessageActivity extends ActionBarActivity {

    private TannoySpeech theTannoySpeech;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expand_message);

        // Get the message from the intent
        Intent intent = getIntent();
        String message = intent.getStringExtra(MainActivity.EXPAND_MESSAGE);

        // Fill the text view
        TextView theTextView = (TextView) findViewById(R.id.textToExpand);
        theTextView.setText(message);

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

    /**Reads aloud the expanded message when the user clicks "read aloud"*/
    public void readExpandedMessage(View theView) {
        //get the text from the edit_message box, then turn it into a string
        String theTextToRead = ((TextView) findViewById(R.id.textToExpand)).getText().toString();

        theTannoySpeech.speak(theTextToRead);

    }
}
