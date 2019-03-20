package app.jspyn.jspyniot;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.github.florent37.materialtextfield.MaterialTextField;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    private static final int RC_SIGN_IN = 9001;
    View sign_in;
    GoogleSignInClient mGoogleSignInClient;
    FirebaseDatabase database;
    DatabaseReference myRef;
    TextView location;
    int exitCounter = 0;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin);
        sign_in = (View) findViewById(R.id.signin);

        location = (TextView)findViewById(R.id.location);
        YoYo.with(Techniques.FadeInUp)
                .duration(700)
                .repeat(1)
                .playOn(findViewById(R.id.imageView2));

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        mAuth = FirebaseAuth.getInstance();

        database = FirebaseDatabase.getInstance();

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            startActivity(new Intent(MainActivity.this, dashBoard.class));
        }

        sign_in.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Objects.equals(location.getText().toString(), "")){
                    Toast.makeText(getApplicationContext(),"Please Input Location", Toast.LENGTH_LONG).show();
                }
                else{
                    signIn();
                }
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                //Log.w(TAG, "Google sign in failed", e);
                // [START_EXCLUDE]
                //updateUI(null);
                // [END_EXCLUDE]
            }
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser user = mAuth.getCurrentUser();
                            myRef = database.getReference("smartHome/" + user.getUid() + "/email");
                            myRef.setValue(user.getEmail());
                            myRef = database.getReference("smartHome/" + user.getUid() + "/name");
                            myRef.setValue(user.getDisplayName());
                            myRef = database.getReference("smartHome/" + user.getUid() + "/FCMID");
                            myRef.setValue(FirebaseInstanceId.getInstance().getInstanceId());
                            myRef = database.getReference("smartHome/" + user.getUid() + "/UID");
                            myRef.setValue(user.getUid());
                            myRef = database.getReference("smartHome/" + user.getUid() + "/photoURL");
                            myRef.setValue(user.getPhotoUrl().toString());
                            myRef = database.getReference("smartHome/" + user.getUid() + "/location");
                            myRef.setValue(location.getText().toString());
                            startActivity(new Intent(MainActivity.this, dashBoard.class));
                        } else {
                            Toast.makeText(getApplicationContext(), "Error Signing in...", Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    public void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onBackPressed() {
        exitCounter += 1;
        if (exitCounter == 2) {
            moveTaskToBack(true);
            exitCounter = 0;

        } else {
            Toast.makeText(getApplicationContext(), "Please press again to exit", Toast.LENGTH_LONG).show();
        }
    }
}
