package com.uow.tannoyahoy;

import android.app.Application;
import android.content.Context;

/**
 * Created by Pwnbot on 9/05/2015.
 */
public class App extends Application {
    public static Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
    }
}