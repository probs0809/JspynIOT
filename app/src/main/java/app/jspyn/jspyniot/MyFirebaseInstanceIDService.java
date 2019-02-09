package app.jspyn.jspyniot;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

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
        if (user != null){
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
