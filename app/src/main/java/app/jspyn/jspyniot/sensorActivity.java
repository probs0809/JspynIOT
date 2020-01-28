package app.jspyn.jspyniot;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;

import com.crystal.crystalpreloaders.widgets.CrystalPreloader;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.jaredrummler.materialspinner.MaterialSpinner;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import okhttp3.OkHttpClient;

public class sensorActivity extends AppCompatActivity {

    AlertDialog.Builder mBuilder;
    FirebaseDatabase database;
    DatabaseReference myRef;
    FirebaseUser user;
    OkHttpClient client;
    String SensorLocation, SensorDate, SensorType, UID;
    Handler handler;
    CrystalPreloader loader;
    LayoutInflater inflater;
    MaterialSpinner selectLocaion, selectDate, sensorType;
    Button Submit;
    List<String> type = new ArrayList<>();
    List<String> locations = new ArrayList<String>();
    List<String> Date = new ArrayList<String>();
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sensor_location_dashboard);

        loader = findViewById(R.id.loader);

        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        user = mAuth.getCurrentUser();
        handler = new Handler(this.getMainLooper());
        selectLocaion = findViewById(R.id.selectSensor);
        selectDate = findViewById(R.id.selectDate);
        sensorType = findViewById(R.id.sensorType);

        YoYo.with(Techniques.FadeInUp)
                .duration(700)
                .repeat(1)
                .playOn(findViewById(R.id.imageView2));

        UID = mAuth.getUid();
        client = new OkHttpClient();
        mBuilder = new AlertDialog.Builder(sensorActivity.this);
        inflater = LayoutInflater.from(this);
        Submit = findViewById(R.id.Submit);
        locations.add("Select Sensor Location");
        Date.add("Select Date");
        type.add("Select Sensor");


        myRef = database.getReference("smartHome/" + user.getUid() + "/" + user.getUid() + "-Sensors/");
        new Thread(new Runnable() {
            @Override
            public void run() {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        myRef.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                type.clear();
                                for (DataSnapshot dashdatasnapshot : dataSnapshot.getChildren()) {
                                    type.add(dashdatasnapshot.getKey());
                                }
                                loader.setVisibility(View.GONE);
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                    }
                });
            }
        }).start();

        sensorType.setItems(type);
        sensorType.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener() {
            @Override
            public void onItemSelected(MaterialSpinner view, int position, long id, Object item) {
                SensorType = type.get(position);
                if (!Objects.equals(SensorType, "")) {
                    myRef = database.getReference("smartHome/" + user.getUid() + "/" + user.getUid() + "-Sensors/" + SensorType);
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    myRef.addValueEventListener(new ValueEventListener() {

                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                            locations.clear();
                                            //locations.add("Select Sensor Location");
                                            for (DataSnapshot dashdataSnapshot : dataSnapshot.getChildren()) {
                                                locations.add(dashdataSnapshot.getKey());
                                            }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) {

                                        }
                                    });
                                }
                            });
                        }
                    }).start();
                    selectLocaion.setEnabled(true);
                    selectLocaion.setItems(locations);
                    selectLocaion.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(MaterialSpinner view, int position, long id, Object item) {
                            SensorLocation = locations.get(position);
                            if (!Objects.equals(SensorLocation, "")) {
                                myRef = database.getReference("smartHome/" + user.getUid() + "/" + user.getUid() + "-Sensors/" + SensorType + "/" + SensorLocation);
                                new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        handler.post(new Runnable() {
                                            @Override
                                            public void run() {
                                                myRef.addValueEventListener(new ValueEventListener() {
                                                    @Override
                                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                        Date.clear();
                                                        //Date.add("Select Date");
                                                        for (final DataSnapshot dashdataSnapshot : dataSnapshot.getChildren()) {
                                                            Date.add(dashdataSnapshot.getKey());
                                                        }
                                                    }

                                                    @Override
                                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                                    }
                                                });
                                            }
                                        });
                                    }
                                }).start();
                                selectDate.setEnabled(true);
                                selectDate.setItems(Date);
                                selectDate.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener() {
                                    @Override
                                    public void onItemSelected(MaterialSpinner view, int position, long id, Object item) {
                                        SensorDate = Date.get(position);
                                    }
                                });
                            }
                        }
                    });

                }
            }
        });


        Submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!Objects.equals(SensorLocation, "") || !Objects.equals(SensorDate, "") || !Objects.equals(SensorType, "")) {
                    Intent i = new Intent(sensorActivity.this, graphActivity.class);
                    i.putExtra("sensorDate", SensorDate);
                    i.putExtra("sensorName", SensorType);
                    i.putExtra("sensorLocationName", SensorLocation);
                    startActivity(i);
                }

            }
        });


    }

}



