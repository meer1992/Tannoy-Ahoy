package com.uow.tannoyahoy;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;


public class MakeAnnouncement extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_make_announcement);

        setUpMakeAnnouncementButton();
    }

    private void setUpMakeAnnouncementButton(){
        Button makeAnnouncement = (Button) findViewById(R.id.makeAnnouncementBtn);

        makeAnnouncement.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
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

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * This method is invoked when user clicks settings menu option
     * @param menuItem
     **/
    public void settingsClicked (MenuItem menuItem) {
        startActivity(new Intent(this,SettingsActivity.class));
    }
}
