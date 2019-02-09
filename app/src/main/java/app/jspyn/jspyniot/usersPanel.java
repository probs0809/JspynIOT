package app.jspyn.jspyniot;

import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.crystal.crystalpreloaders.widgets.CrystalPreloader;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.tnlsystems.jspynio.Jspyn;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class usersPanel extends AppCompatActivity {
    View home;
    private FirebaseAuth mAuth;
    private final String postUrl = "http://192.168.4.1/login";
    FirebaseDatabase database;
    DatabaseReference myRef;
    FirebaseUser user;
    String deviceName;
    LayoutInflater inflater;
    RelativeLayout layout;
    Handler handler;
    String UID;
    CrystalPreloader loader;
    private DrawerLayout dl;
    AlertDialog.Builder mBuilder;
    String API;
    DropDownClass drop;
    String[] sensorList = {"Select Item","Temperature","Humidity___"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_users_panel);


        loader = (CrystalPreloader)findViewById(R.id.loader);

        dl = (DrawerLayout)findViewById(R.id.dl2);
        final NavigationView navigationView = (NavigationView)findViewById(R.id.nv2);

        handler = new Handler(this.getMainLooper());
        Intent j =getIntent();
        deviceName = j.getStringExtra("deviceName");
        mAuth =FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        user = mAuth.getCurrentUser();
        mBuilder =  new AlertDialog.Builder(usersPanel.this);
        inflater = LayoutInflater.from(this);
        layout = (RelativeLayout)findViewById(R.id.layout3);
        home = (View)findViewById(R.id.bac);
        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dl.openDrawer(navigationView);
            }
        });
        API = j.getStringExtra("API");
        myRef = database.getReference("smartHome/"+user.getUid()+"/dashboard/"+deviceName+"/Application");

        new Thread(new Runnable() {
            @Override
            public void run() {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        myRef.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                layout.removeAllViews();
                                for (DataSnapshot panelDataSnapshot : dataSnapshot.getChildren()){
                                    final ApplicationPost appPost = panelDataSnapshot.getValue(ApplicationPost.class);

                                    switch (appPost.Type){
                                        case "Button":
                                            View button = inflater.inflate(R.layout.button,layout,false);
                                            final TextView button_ = (TextView)button.findViewById(R.id.Button);
                                            //final Button button_ = new Button(usersPanel.this);
                                            button_.setText(appPost.Name);
                                            button_.setEnabled(true);
                                            button_.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View v) {
                                                    //Toast.makeText(getApplicationContext(),"Pressed",Toast.LENGTH_LONG).show();
                                                    new Thread(new Runnable() {
                                                        @Override
                                                        public void run() {
                                                            new Jspyn(API).digitalWrite(Integer.parseInt(appPost.Pin),"HIGH");
                                                        }
                                                    }).start();

                                                }
                                            });

                                            button.setX(appPost.cord_x);
                                            button.setY(appPost.cord_y);
                                            layout.addView(button);
                                            break;

                                        case "Switch":
                                            View switc = inflater.inflate(R.layout.myswitch,layout,false);
                                            Switch switch_ = (Switch) switc.findViewById(R.id.Switch);
                                            //Switch switch_ = new Switch(usersPanel.this);
                                            switch_.setText(appPost.Name);
                                            switc.setX(appPost.cord_x);
                                            switc.setY(appPost.cord_y);
                                            switch_.setEnabled(true);
                                            switch_.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                                                @Override
                                                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                                                    if (isChecked){
                                                        //Toast.makeText(getApplicationContext(),"Checked",Toast.LENGTH_LONG).show();
                                                        new Thread(new Runnable() {
                                                            @Override
                                                            public void run() {
                                                                new Jspyn(API).digitalWrite(Integer.parseInt(appPost.Pin),"HIGH");
                                                            }
                                                        }).start();

                                                    }
                                                    else{
                                                        //Toast.makeText(getApplicationContext(),"Checked",Toast.LENGTH_LONG).show();

                                                        new Thread(new Runnable() {
                                                            @Override
                                                            public void run() {
                                                                new Jspyn(API).digitalWrite(Integer.parseInt(appPost.Pin),"LOW");
                                                            }
                                                        }).start();

                                                    }
                                                }
                                            });
                                            layout.addView(switc);
                                            break;

                                        case "SeekBar":
                                            View seek = inflater.inflate(R.layout.myseekbar,layout,false);
                                            SeekBar seek_ = (SeekBar) seek.findViewById(R.id.seekBar);
                                            //SeekBar seek_ = new SeekBar(usersPanel.this);
                                            seek_.setMax(255);
                                            seek.setX(appPost.cord_x);
                                            seek.setY(appPost.cord_y);
                                            seek_.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                                                @Override
                                                public void onProgressChanged(SeekBar seekBar, final int progress, boolean fromUser) {
                                                    //Toast.makeText(getApplicationContext(),"P: "+ progress,Toast.LENGTH_LONG).show();
                                                    new Thread(new Runnable() {
                                                        @Override
                                                        public void run() {
                                                            new Jspyn(API).analogWrite(Integer.parseInt(appPost.Pin),progress);
                                                        }
                                                    }).start();

                                                }

                                                @Override
                                                public void onStartTrackingTouch(SeekBar seekBar) {

                                                }

                                                @Override
                                                public void onStopTrackingTouch(SeekBar seekBar) {

                                                }
                                            });
                                            seek_.setEnabled(true);
                                            layout.addView(seek);
                                            break;
                                    }
                                }
                                loader.setVisibility(View.GONE);
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                    }
                });
            }
        }).start();



        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.editLayout:
                        Intent j =  new Intent(usersPanel.this, setControl.class);
                        j.putExtra("deviceName",deviceName);
                        startActivity(j);
                        dl.closeDrawers();
                        break;

                    case R.id.deleteDevice:
                        //mAuth.signOut();
                        myRef = database.getReference("smartHome/"+user.getUid()+"/dashboard/"+deviceName);
                        myRef.removeValue();
                        Intent i =  new Intent(usersPanel.this, MainActivity.class);
                        startActivity(i);
                        dl.closeDrawers();
                        break;

                    case R.id.setUp:
                        configure();
                        dl.closeDrawers();
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

    private void configure(){

        View cView = getLayoutInflater().inflate(R.layout.configure_jspyn,null);

        mBuilder.setView(cView);

        final AlertDialog dialog = mBuilder.create();

        final TextView ssid = (TextView)cView.findViewById(R.id.ssid);
        final TextView password = (TextView)cView.findViewById(R.id.wPassword);
        final Spinner sensor = (Spinner) cView.findViewById(R.id.sensor);
        final TextView sName = (TextView)cView.findViewById(R.id.sensorId);
        TextView set = (TextView)cView.findViewById(R.id.config);

        drop = new DropDownClass();
        sensor.setOnItemSelectedListener(drop);
        ArrayAdapter aa = new ArrayAdapter(this,android.R.layout.simple_spinner_item,sensorList);
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sensor.setAdapter(aa);
        dialog.show();

        set.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final String ssidS = ssid.getText().toString();
                final String passwordS = password.getText().toString();
                final String customSName = sName.getText().toString();
                final String SENSOR = sensorList[drop.position];

                if (SENSOR.equals("Select Item") ){

                    Toast.makeText(getApplicationContext(),"Please select the correct pin number...",Toast.LENGTH_LONG).show();
                }
                else {
                    //final Intent i = new Intent(DashBoard.this, setControl.class);
                    //i.putExtra("deviceName",dName);
                    new Thread(new Runnable() {
                        @Override
                        public void run() {



                        String postBody = "SSID="+ssidS+"&PASSWORD="+passwordS+"&API_KEY="+API+"&SENSOR_TYPE="+SENSOR+"&UID="+user.getUid()+"&SENSOR_NAME="+customSName;

                        //JSON = MediaType.parse("application/x-www-form-urlencoded; charset=utf-8");
                        try {
                            postRequest(postUrl,postBody);
                            dialog.dismiss();
                            //startActivity(i);

                        }
                        catch (IOException e) {
                            e.printStackTrace();
                        }




                        }
                    }).start();



                }

            }
        });


    }

    private void postRequest(String postUrl,String postBody) throws IOException {

        OkHttpClient client = new OkHttpClient();

        //RequestBody body = RequestBody.create(JSON, postBody);

        Request request = new Request.Builder()
                .url(postUrl + "?" + postBody)
                .build();

        Call call = client.newCall(request);

        Response response = null;
        try {
            response = call.execute();
            Toast.makeText(getApplicationContext(),"Status: "+response.body().toString(),Toast.LENGTH_LONG).show();
        } catch (IOException e) {
            e.printStackTrace();
        }

        /*client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {

            }
        });*/
    }
}
