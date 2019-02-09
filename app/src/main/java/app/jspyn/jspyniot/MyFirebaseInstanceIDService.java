package app.jspyn.jspyniot.non_activity;

import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;
import com.google.firebase.messaging.FirebaseMessagingService;

import static android.content.ContentValues.TAG;

/**
 * Created by prabodhmayekar on 22/12/18.
 */

public class MyFirebaseInstanceIDService extends FirebaseMessagingService {

    String refreshedToken;


    @Override
    public void onNewToken(String token) {
        refreshedToken = token;
        Log.d(TAG, "Refreshed token: " + token);

        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // Instance ID token to your app server.
        //sendRegistrationToServer(token);
    }
}
