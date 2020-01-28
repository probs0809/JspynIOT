package app.jspyn.jspyniot;

import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessagingService;

import static android.content.ContentValues.TAG;

/**
 * Created by prabodhmayekar on 22/12/18.
 */

public class MyFirebaseInstanceIDService extends FirebaseMessagingService {

    String refreshedToken;
    FirebaseAuth mAuth;
    FirebaseUser user;
    DatabaseReference myRef;
    FirebaseDatabase database;


    @Override
    public void onNewToken(String token) {
        refreshedToken = token;
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        if (user != null) {
            myRef = database.getReference("smartHome/" + user.getUid() + "/FCMID/result/token");
            myRef.setValue(token);

        }
        Log.d(TAG, "Refreshed token: " + token);

        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // Instance ID token to your app server.
        //sendRegistrationToServer(token);
    }
}
