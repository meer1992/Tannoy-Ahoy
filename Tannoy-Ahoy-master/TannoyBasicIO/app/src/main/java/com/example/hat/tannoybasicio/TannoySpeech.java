package com.example.hat.tannoybasicio;


import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import java.util.Locale;

/**
 * TannoySpeech can easily turn any text into speech according to the system locale.
 * If text-to-speech is not installed, it will prompt to install it.
 */
public class TannoySpeech {
    private final Context theContext;
    private TextToSpeech theTTS;
    public final static String LOGTAG = "TannoySpeech";

    /**
     * NOTE: Initialise TannoySpeech in an onCreate method
     * theContext: your Context, Example: TannoySpeech foo = new TannoySpeech(this);
     */
    public TannoySpeech(Context context){
        theContext = context;

        theTTS = new TextToSpeech(theContext, new TextToSpeech.OnInitListener() {
            //checks TTS will work with the current system locale upon initialising
            @Override
            public void onInit(int theStatus) {
                if(theStatus == TextToSpeech.SUCCESS) {
                    //effectively a series of OR'd if statements checking myTTS will work
                    switch (theTTS.isLanguageAvailable(Locale.getDefault())) {
                        case TextToSpeech.LANG_AVAILABLE:
                        case TextToSpeech.LANG_COUNTRY_AVAILABLE:
                        case TextToSpeech.LANG_COUNTRY_VAR_AVAILABLE:
                            //code executed if any of above three are true
                            Log.d(LOGTAG, "System locale supported");
                            theTTS.setLanguage(Locale.getDefault());
                            break;
                        case TextToSpeech.LANG_MISSING_DATA:
                            Log.d(LOGTAG,"MISSING TTS DATA, trying to get data..");
                            //try to get the device to install the necessary TTS data
                            Intent installDataIntent = new Intent();
                            installDataIntent.setAction(TextToSpeech.Engine.ACTION_INSTALL_TTS_DATA);
                            theContext.startActivity(installDataIntent);
                            break;
                        case TextToSpeech.LANG_NOT_SUPPORTED:
                            //The language isnt supported by android
                            Log.d(LOGTAG,"locale language NOT SUPPORTED");
                            break;
                    }
                }
            }
        });
    }

    /**Speaks out the given string lineToSpeak*/
    public void speak(String lineToSpeak){
        //Two different speech commands for different versions of android
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            theTTS.speak(lineToSpeak, TextToSpeech.QUEUE_ADD, null, "TANNOY_SPEECH_TRY");
        }
        else {
            theTTS.speak(lineToSpeak,TextToSpeech.QUEUE_ADD,null);
        }
    }
}
