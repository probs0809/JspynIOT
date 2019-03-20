package app.jspyn.jspyniot;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.crystal.crystalpreloaders.widgets.CrystalPreloader;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.daimajia.swipe.util.Attributes;
import com.gc.materialdesign.views.ButtonFloat;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.util.ArrayList;

import app.jspyn.jspyniot.non_activity.dashboardDataPost;
import jp.wasabeef.recyclerview.animators.SlideInLeftAnimator;
import jp.wasabeef.recyclerview.animators.adapters.AlphaInAnimationAdapter;
import jp.wasabeef.recyclerview.animators.adapters.SlideInBottomAnimationAdapter;
import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class dashBoard extends AppCompatActivity {
    View toggle_drawer;
    OkHttpClient client;
    String api, device_name, UID;
    Handler handler;
    CrystalPreloader pre_loader;
    LayoutInflater inflater;
    ButtonFloat buttonFloat;
    int exitCounter = 0;
    private FirebaseAuth mAuth;
    private FirebaseDatabase database;
    private DatabaseReference myRef,ref_two, ref_three;
    private FirebaseUser user;
    private DrawerLayout my_drawer_layout;
    private RecyclerView recyclerView;
    private RecyclerView.Adapter mAdapter;
    private ArrayList<String> API = new ArrayList<>();
    private ArrayList<String> DeviceName = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dash_board);
        pre_loader = (CrystalPreloader) findViewById(R.id.loader);
        my_drawer_layout = (DrawerLayout) findViewById(R.id.dl);
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);


        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        handler = new Handler(this.getMainLooper());
        final NavigationView navigationView = (NavigationView) findViewById(R.id.navView);
        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        user = mAuth.getCurrentUser();
        UID = user.getUid();
        client = new OkHttpClient();
        inflater = LayoutInflater.from(this);
        buttonFloat = (ButtonFloat) findViewById(R.id.buttonFloat);
        buttonFloat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addDevice();
            }
        });
        toggle_drawer = (View) findViewById(R.id.addDevice);



        YoYo.with(Techniques.FadeInUp)
                .duration(700)
                .repeat(1)
                .playOn(findViewById(R.id.imageView2));

        YoYo.with(Techniques.FadeIn)
                .duration(700)
                .repeat(0)
                .playOn(findViewById(R.id.bcl));

        YoYo.with(Techniques.RotateIn)
                .duration(1000)
                .repeat(0)
                .playOn(toggle_drawer);

        View headerView = navigationView.inflateHeaderView(R.layout.header);
        final ImageView headerImageView = (ImageView) headerView.findViewById(R.id.imgView);
        final TextView headerEmailText = (TextView) headerView.findViewById(R.id.imgText);
        final TextView headerUsername = (TextView) headerView.findViewById(R.id.usrName);

        Typeface tf = Typeface.createFromAsset(getAssets(),"trench100free.ttf");
        headerUsername.setTypeface(tf);

        ref_three = database.getReference("smartHome/" + user.getUid());
        new Thread(new Runnable() {
            @Override
            public void run() {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        ref_three.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                new ImageLoadTask(dataSnapshot.child("photoURL").getValue().toString(), headerImageView).execute();
                                headerEmailText.setText(dataSnapshot.child("email").getValue().toString());
                                headerUsername.setText(dataSnapshot.child("name").getValue().toString());
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                    }
                });
            }
        }).start();

        toggle_drawer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                my_drawer_layout.openDrawer(navigationView);
            }
        });

        ref_two = database.getReference("smartHome/" + user.getUid() + "/dashboard/");
        new Thread(new Runnable() {
            @Override
            public void run() {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        ref_two.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                pre_loader.setVisibility(View.VISIBLE);
                                API.clear();
                                DeviceName.clear();
                                recyclerView.removeAllViews();
                                for (DataSnapshot dashdataSnapshot : dataSnapshot.getChildren()) {
                                    final dashboardDataPost dashPost = dashdataSnapshot.getValue(dashboardDataPost.class);
                                    API.add(dashPost.api);
                                    DeviceName.add(dashPost.DeviceName);
                                }
                                pre_loader.setVisibility(View.GONE);
                                mAdapter = new RecyclerViewAdapter(dashBoard.this, API, DeviceName);
                                ((RecyclerViewAdapter) mAdapter).setMode(Attributes.Mode.Single);
                                recyclerView.setAdapter(new SlideInBottomAnimationAdapter(new AlphaInAnimationAdapter(mAdapter)));

                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                    }
                });
            }
        }).start();

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.addDeviceMenu:
                        addDevice();
                        my_drawer_layout.closeDrawers();
                        break;

                    case R.id.MySensors:
                        Intent j = new Intent(dashBoard.this, sensorActivity.class);
                        startActivity(j);
                        my_drawer_layout.closeDrawers();
                        break;

                    case R.id.logoutMenu:
                        mAuth.signOut();
                        Intent i = new Intent(dashBoard.this, MainActivity.class);
                        startActivity(i);
                        my_drawer_layout.closeDrawers();
                        break;
                }
                return true;
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    private void addDevice() {
        View mView = getLayoutInflater().inflate(R.layout.activity_add_device_name, null);
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(dashBoard.this);
        mBuilder.setView(mView);
        final AlertDialog dialog = mBuilder.create();
        dialog.show();
        final TextView deviceName = (TextView) mView.findViewById(R.id.dName);
        final TextView addDname = (TextView) mView.findViewById(R.id.addDname);


        addDname.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                device_name = deviceName.getText().toString();
                device_name = device_name.replace(' ', '-');
                if (device_name.isEmpty() || device_name.length() >= 9 ) {
                    Toast.makeText(getApplicationContext(), "Please input valid Name with less then 9 charactors", Toast.LENGTH_LONG).show();
                } else {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            Request request = new Request.Builder()
                                    .url("http://35.200.218.116/api")
                                    .build();

                            Call call = client.newCall(request);

                            Response response = null;

                            try {
                                response = call.execute();
                                dialog.dismiss();
                                api = response.body().string();
                                myRef = database.getReference("smartHome/" + user.getUid() + "/dashboard/" + device_name + "/DeviceName/");
                                myRef.setValue(device_name);
                                myRef = database.getReference("smartHome/" + user.getUid() + "/dashboard/" + device_name + "/api/");
                                myRef.setValue(api);
                                myRef = database.getReference("apiStatus/" + api+"/status");
                                myRef.setValue(0);

                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }).start();
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        exitCounter += 1;
        if (exitCounter == 2) {
            //finish();
            moveTaskToBack(true);
            exitCounter = 0;

        } else {

            Toast.makeText(getApplicationContext(), "Please press again to exit", Toast.LENGTH_LONG).show();
        }

    }



}






