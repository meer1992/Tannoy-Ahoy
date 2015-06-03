package com.uow.tannoyahoy;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.SeekBar;
import android.widget.TextView;


public class SettingsActivity extends ActionBarActivity {
    private SeekBar seekBar;
    private TextView textview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        addListenerUpdateCheck();
        addListenerNotificationCheck();
        addListenerSeekBar();
    }
    public void addListenerSeekBar()
    {
        seekBar = (SeekBar) findViewById(R.id.seekBar);
        textview = (TextView) findViewById(R.id.textView1);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            int progress = 0;

            @Override
            public void onProgressChanged(SeekBar seekBar, int progresValue, boolean fromUser) {
                progress = progresValue;
                textview.setText("Update rate (sec): " + (progress+10));
                Settings.getInstance().setAnnouncementUpdateInterval(progress+10);

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {


            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }

        });
    }
    public void addListenerUpdateCheck()
    {
        CheckBox yourCheckBox = (CheckBox) findViewById (R.id.checkBox2);

        yourCheckBox.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                //is chkIos checked?
                if (((CheckBox) v).isChecked()) {
                    Log.d("help", "set background updates to true");
                    Settings.getInstance().setHasBackgroundUpdates(true);
                    //Case 1
                } else {
                    Log.d("help", "set background updates to false");
                    Settings.getInstance().setHasBackgroundUpdates(false);
                }
                //case 2

            }
        });
    }
    public void addListenerNotificationCheck()
    {
        CheckBox yourCheckBox = (CheckBox) findViewById (R.id.checkBox3);

        yourCheckBox.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                //is chkIos checked?
                if (((CheckBox) v).isChecked()) {
                    Log.d("help", "set background alerts to true");
                    Settings.getInstance().setHasBackgroundAlerts(true);
                    //Case 1
                } else {
                    Log.d("help", "set background alerts to false");
                    Settings.getInstance().setHasBackgroundAlerts(false);
                }
                //case 2

            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_settings, menu);
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
    public void onCheckboxClicked(View view) {
        // Is the view now checked?
        boolean checked = ((CheckBox) view).isChecked();

        // Check which checkbox was clicked
        switch(view.getId()) {
            case R.id.checkBox2:
                if (checked) { Log.d("CheckBox", "This was checked in the settings activity");                              }
                // Put some meat on the sandwich
                else {
                    Log.d("CheckBox", "This was unchecked in the settings activity"); }
                // Remove the meat
                break;


        }
    }


}
