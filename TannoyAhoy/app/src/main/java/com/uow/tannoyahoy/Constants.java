package com.uow.tannoyahoy;

/**
 * Created by Pwnbot on 8/05/2015.
 */
public final class Constants {

    //TAGS
    public static final String CONNECTION_FAILED_ACTION = "action.CONNECTION_FAILED";
    public static final String CONNECTION_SUSPENDED_ACTION = "action.CONNECTION_SUSPENDED";
    public static final String BOOT_COMPLETED_ACTION = "android.intent.action.BOOT_COMPLETED";
    public static final String CONNECTED_ACTION = "action.CONNECTION_SUCCEEDED";
    public static final String LOCATION_CHANGED_ACTION = "action.LOCATION_CHANGED";
    public static final String CONNECTION_RESULT_TAG = "CONNECTION_RESULT";
    public static final String CONNECTION_SUSPENDED_ERROR_CODE_TAG = "SUSPENDED_ERROR_CODE";
    public static final String LOCATION_TAG = "LOCATION";
    public static final String UPDATE_TIME_TAG = "UPDATE_TIME_TAG";

    public static final String URL = "https://bedrock.resnet.cms.waikato.ac.nz:8080/queue?server=";

    //default data
    public static final long DEFAULT_LOCATION_UPDATE_INTERVAL = 30000; //20 seconds
    public static final long DEFAULT_FASTEST_LOCATION_UPDATE_INTERVAL = DEFAULT_LOCATION_UPDATE_INTERVAL /2;
    public static final long DEFAULT_RECONNECT_TO_LOCATION_CLIENT_INTERVAL = DEFAULT_LOCATION_UPDATE_INTERVAL;
    public static final Boolean DEFAULT_HAS_BACKGROUND_LOCATION_UPDATES = false;
    public static final Boolean DEFAULT_HAS_LOCATION_NOTIFICATIONS = false;

    public static final long DEFAULT_ANNOUNCEMENT_UPDATE_INTERVAL = 30000; //20 seconds

    //class cannot be initialised
    public Constants () { throw new AssertionError();}
}
