package app.jspyn.jspyniot;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TableLayout;
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

import java.io.IOException;

import okhttp3.Call;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class DashBoard extends AppCompatActivity {
    View addDevice;

    AlertDialog.Builder mBuilder;
    private FirebaseAuth mAuth;
    FirebaseDatabase database;
    DatabaseReference myRef;
    FirebaseUser user;
    OkHttpClient client;
    String api;
    TableLayout tableLayout;
    String dName;
    String UID;
    Handler handler;
    CrystalPreloader loader;
    LayoutInflater inflater;
    private DrawerLayout  dl;
    int exitCounter = 0;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dash_board);

        loader = (CrystalPreloader)findViewById(R.id.loader);

        dl = (DrawerLayout)findViewById(R.id.dl);


        final NavigationView navigationView = (NavigationView)findViewById(R.id.navView);
        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        user = mAuth.getCurrentUser();
        handler = new Handler(this.getMainLooper());



        UID = mAuth.getUid();
        client = new OkHttpClient();
        mBuilder =  new AlertDialog.Builder(DashBoard.this);
        inflater = LayoutInflater.from(this);
        tableLayout = (TableLayout) findViewById(R.id.tableLayout);


        addDevice = (View)findViewById(R.id.addDevice);

        addDevice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dl.openDrawer(navigationView);
            }
        });

        myRef = database.getReference("smartHome/" + user.getUid() + "/dashboard/");
        new Thread(new Runnable() {
            @Override
            public void run() {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        myRef.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                tableLayout.removeAllViewsInLayout();
                                for(DataSnapshot dashdataSnapshot : dataSnapshot.getChildren()){
                                    final dashboardDataPost dashPost = dashdataSnapshot.getValue(dashboardDataPost.class);
                                    //Toast.makeText(getApplicationContext(),"QQQ: "+dashPost.api+"  "+dashPost.DeviceName,Toast.LENGTH_LONG).show();
                                    final View view = inflater.inflate(R.layout.dashview,tableLayout,false);
                                    TextView button = (TextView)view.findViewById(R.id.d10);
                                    button.setText(dashPost.DeviceName);
                                    button.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            Intent i = new Intent(DashBoard.this, usersPanel.class);
                                            i.putExtra("deviceName",dashPost.DeviceName);
                                            i.putExtra("API",dashPost.api);
                                            startActivity(i);

                                        }
                                    });
                                    tableLayout.addView(view);
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
                    case R.id.addDeviceMenu:
                        addDevice();
                        dl.closeDrawers();
                        break;

                    case R.id.MySensors:
                        Intent j = new Intent(DashBoard.this, sensorDashboard.class);
                        startActivity(j);
                        dl.closeDrawers();
                        break;

                    case R.id.logoutMenu:
                        mAuth.signOut();
                        Intent i =  new Intent(DashBoard.this, MainActivity.class);
                        startActivity(i);
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


    private void addDevice() {
        View mView = getLayoutInflater().inflate(R.layout.activity_add_device_name,null);

        mBuilder.setView(mView);

        final AlertDialog dialog = mBuilder.create();
        dialog.show();
        final TextView deviceName = (TextView)mView.findViewById(R.id.dName);
        final TextView addDname = (TextView)mView.findViewById(R.id.addDname);


        addDname.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dName = deviceName.getText().toString();
                dName = dName.replace(' ','-');
                if(dName.isEmpty()){
                    Toast.makeText(getApplicationContext(),"Please input valid Name",Toast.LENGTH_LONG).show();
                }
                else {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            Request request = new Request.Builder()
                                    .url("https://us-central1-jspyn-39604.cloudfunctions.net/jspynio/api")
                                    .build();

                            Call call = client.newCall(request);

                            Response response = null;
                            try {
                                response = call.execute();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }

                            try {
                                api = response.body().string();
                                myRef = database.getReference("smartHome/" + user.getUid() + "/dashboard/" + dName + "/DeviceName/");
                                myRef.setValue(dName);
                                myRef = database.getReference("smartHome/" + user.getUid() + "/dashboard/" + dName + "/api/");
                                myRef.setValue(api);

                                dialog.dismiss();

                            } catch (IOException e) {
                                e.printStackTrace();
                            }

                        }
                    }).start();
                    //configure();
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
       exitCounter += 1;
       if (exitCounter == 2){
           //finish();
           moveTaskToBack(true);
           exitCounter = 0;

       }
       else{

           Toast.makeText(getApplicationContext(),"Please press again to exit",Toast.LENGTH_LONG).show();
       }

    }
}



