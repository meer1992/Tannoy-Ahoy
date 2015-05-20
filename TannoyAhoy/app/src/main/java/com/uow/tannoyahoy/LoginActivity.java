package com.uow.tannoyahoy;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;


public class LoginActivity extends ActionBarActivity {
//    UserLocalStore userLocalStore;
//    EditText etUsername, etPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

//        etUsername = (EditText) findViewById(R.id.etUsername);
//        etPassword = (EditText) findViewById(R.id.etPassword);
        setUpLoginButton();

//        userLocalStore = new UserLocalStore(this);
    }

    private void setUpLoginButton(){
        Button loginButton = (Button) findViewById(R.id.btLogin);

        loginButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
//                String username = etUsername.getText().toString();
//                String password = etPassword.getText().toString();
//                User user = new User(username, password);
//                userLocalStore.storeUserData(user);
//                userLocalStore.setUserLoggedIn(true);

                Intent a = new Intent(v.getContext(), MainActivity.class);
                a.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(a);
                Toast.makeText(getApplicationContext(), "Logged in", Toast.LENGTH_LONG).show();
            }
        });
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
