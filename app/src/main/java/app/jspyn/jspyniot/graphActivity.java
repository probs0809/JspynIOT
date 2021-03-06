package app.jspyn.jspyniot;

import android.content.Intent;
import android.icu.math.BigDecimal;
import android.os.Bundle;
import android.os.Handler;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TableLayout;
import android.widget.Toast;

import com.crystal.crystalpreloaders.widgets.CrystalPreloader;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.LegendRenderer;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import okhttp3.OkHttpClient;

public class graphActivity extends AppCompatActivity {
    View addDevice;

    AlertDialog.Builder mBuilder;
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
    GraphView graph;
    DataPoint[] points;
    LineGraphSeries<DataPoint> series;

    public static float convertTo100(float input) {
        String input_string = Float.toString(input);
        BigDecimal inputBD = new BigDecimal(input_string);
        String hhStr = input_string.split("\\.")[0];
        BigDecimal output = new BigDecimal(Float.toString(Integer.parseInt(hhStr)));
        output = output.add((inputBD.subtract(output).divide(BigDecimal.valueOf(60), 10, BigDecimal.ROUND_HALF_EVEN)).multiply(BigDecimal.valueOf(100)));

        return Float.parseFloat(output.toString());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graph);

        loader = findViewById(R.id.loader);
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        user = mAuth.getCurrentUser();
        handler = new Handler(this.getMainLooper());


        points = new DataPoint[100];
        UID = mAuth.getUid();
        client = new OkHttpClient();
        mBuilder = new AlertDialog.Builder(graphActivity.this);
        inflater = LayoutInflater.from(this);
        graph = findViewById(R.id.graph);

       /* graph.getViewport().setXAxisBoundsManual(true);
        graph.getViewport().setMinX(0);
        graph.getViewport().setMaxX(24);

        graph.getViewport().setYAxisBoundsManual(true);
        graph.getViewport().setMinY(-50);
        graph.getViewport().setMaxY(50);*/

        graph.getViewport().setScrollable(true);

        Intent i = getIntent();
        final String sensorName = i.getStringExtra("sensorName");
        final String sensorLocationName = i.getStringExtra("sensorLocationName");
        final String sensorDate = i.getStringExtra("sensorDate");
        series = new LineGraphSeries<>();
        graph.setTitle(sensorName + "|" + sensorLocationName);
        graph.getGridLabelRenderer().setVerticalAxisTitle("Data");
        graph.getGridLabelRenderer().setHorizontalAxisTitle("Time");
        graph.getViewport().setScalable(true);
        graph.getViewport().setScalableY(true);
        graph.getViewport().setScrollable(true);
        graph.getLegendRenderer().setVisible(true);
        graph.getLegendRenderer().setAlign(LegendRenderer.LegendAlign.TOP);

        graph.addSeries(series);


        myRef = database.getReference("smartHome/" + user.getUid() + "/" + user.getUid() + "-Sensors/" + sensorName + "/" + sensorLocationName + "/" + sensorDate);
        new Thread(new Runnable() {
            @Override
            public void run() {
                handler.post(new Runnable() {
                    @Override
                    public void run() {

                        myRef.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                series.resetData(new DataPoint[]{
                                        new DataPoint(0, 0)
                                });

                                for (DataSnapshot dashdataSnapshot : dataSnapshot.getChildren()) {
                                    //final dashboardDataPost dashPost = dashdataSnapshot.getValue(dashboardDataPost.class);
                                    //Toast.makeText(getApplicationContext(),"QQQ: "+dashPost.api+"  "+dashPost.DeviceName,Toast.LENGTH_LONG).show();
                                    String time;
                                    time = dashdataSnapshot.getKey();
                                    int snap = Integer.parseInt(String.valueOf(dataSnapshot.child(time).getValue()));

                                    float timeFloat = Float.parseFloat(time.replace(":", "."));
                                    Toast.makeText(getApplicationContext(), "service: " + snap + "/" + convertTo100(timeFloat), Toast.LENGTH_LONG).show();


                                    series.setDrawDataPoints(true);
                                    series.setDrawBackground(true);
                                    series.setThickness(5);
                                    series.appendData(new DataPoint(convertTo100(timeFloat), snap), false, 100);


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


    }


}



