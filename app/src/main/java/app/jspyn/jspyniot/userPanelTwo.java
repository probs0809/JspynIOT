package app.jspyn.jspyniot;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.crystal.crystalpreloaders.widgets.CrystalPreloader;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.gc.materialdesign.views.ButtonFlat;
import com.gc.materialdesign.views.Slider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import app.jspyn.jspyniot.non_activity.ApplicationPost;

public class userPanelTwo extends AppCompatActivity {
    int Value;

    TextView title;
    FirebaseDatabase database;
    DatabaseReference myRef, secRef,gRef;
    FirebaseUser user;
    String deviceName;
    LayoutInflater inflater;
    RelativeLayout layout;
    Handler handler;
    CrystalPreloader loader;
    AlertDialog.Builder mBuilder;
    String API;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_panel_two);


        loader = findViewById(R.id.loader);



        handler = new Handler(this.getMainLooper());
        Intent j = getIntent();
        deviceName = j.getStringExtra("deviceName");
        String deviceNameTwo = deviceName;

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        user = mAuth.getCurrentUser();
        mBuilder = new AlertDialog.Builder(userPanelTwo.this);
        inflater = LayoutInflater.from(this);
        layout = findViewById(R.id.layout3);

        title = findViewById(R.id.title);
        title.setText(deviceNameTwo.replace("-"," ").toUpperCase());


        API = j.getStringExtra("api");
        myRef = database.getReference("smartHome/" + user.getUid() + "/dashboard/" + deviceName + "/Application");

        YoYo.with(Techniques.FlipInX)
                .duration(700)
                .repeat(0)
                .playOn(title);

        new Thread(new Runnable() {
            @Override
            public void run() {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
                            @SuppressLint("SetTextI18n")
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                layout.removeAllViews();
                                for (DataSnapshot panelDataSnapshot : dataSnapshot.getChildren()) {
                                    final ApplicationPost appPost = panelDataSnapshot.getValue(ApplicationPost.class);

                                    if (appPost != null) {
                                        switch (appPost.Type) {
                                            case "Button":
                                                View button = inflater.inflate(R.layout.button, layout, false);
                                                final ButtonFlat button_ = button.findViewById(R.id.Button);
                                                final TextView ButtonState = button.findViewById(R.id.ButtonStatus);
                                                //final Button button_ = new Button(usersPanel.this);
                                                button_.setText(appPost.Name);
                                                button_.setEnabled(true);
                                                if (appPost.gpio == 1){
                                                    button_.setBackgroundColor(getResources().getColor(R.color.accent,getTheme()));
                                                    ButtonState.setText("ON");
                                                    ButtonState.setTextColor(getResources().getColor(R.color.accent,getTheme()));
                                                }
                                                else if (appPost.gpio == 0)
                                                {
                                                    button_.setBackgroundColor(getResources().getColor(R.color.colorAccent,getTheme()));
                                                    ButtonState.setText("OFF");
                                                    ButtonState.setTextColor(getResources().getColor(R.color.colorAccent,getTheme()));
                                                }
                                                button_.setOnClickListener(new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View v) {
                                                        //Toast.makeText(getApplicationContext(),"Pressed",Toast.LENGTH_LONG).show();
                                                        new Thread(new Runnable() {
                                                            @Override
                                                            public void run() {
                                                                secRef = database.getReference("smartHome/" + user.getUid() + "/dashboard/" + deviceName + "/Application/" + appPost.Id + "/gpio");
                                                                secRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                                                    @Override
                                                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                                        if (dataSnapshot.getValue() != null) {
                                                                            if (dataSnapshot.getValue().toString().equals("0")) {
                                                                                secRef.setValue(1);
                                                                                gRef = database.getReference("GPIO/" + API + "/" + appPost.Pin);
                                                                                gRef.setValue(1);
                                                                                ButtonState.setText("On");
                                                                                ButtonState.setTextColor(getResources().getColor(R.color.accent, getTheme()));
                                                                                button_.setBackgroundColor(getResources().getColor(R.color.accent, getTheme()));
                                                                            } else if (dataSnapshot.getValue().toString().equals("1")) {
                                                                                secRef.setValue(0);
                                                                                gRef = database.getReference("GPIO/" + API + "/" + appPost.Pin);
                                                                                gRef.setValue(0);
                                                                                ButtonState.setText("OFF");
                                                                                ButtonState.setTextColor(getResources().getColor(R.color.colorAccent, getTheme()));
                                                                                button_.setBackgroundColor(getResources().getColor(R.color.colorAccent, getTheme()));
                                                                            }
                                                                        }
                                                                    }

                                                                    @Override
                                                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                                                    }
                                                                });


                                                                //new Jspyn(API).digitalWrite(Integer.parseInt(appPost.Pin),"HIGH");

                                                            }
                                                        }).start();

                                                    }
                                                });

                                                button.setX(appPost.cord_x);
                                                button.setY(appPost.cord_y);
                                                YoYo.with(Techniques.FlipInX)
                                                        .duration(700)
                                                        .repeat(0)
                                                        .playOn(button_);
                                                layout.addView(button);
                                                break;

                                            case "Switch":
                                                View switc = inflater.inflate(R.layout.myswitch, layout, false);
                                                final com.gc.materialdesign.views.Switch switch_ = switc.findViewById(R.id.Switch);
                                                TextView textS = switc.findViewById(R.id.SwitchText);
                                                final TextView SwitchState = switc.findViewById(R.id.SwitchStatus);
                                                textS.setText(appPost.Name);
                                                //Switch switch_ = new Switch(usersPanel.this);


                                                switc.setX(appPost.cord_x);
                                                switc.setY(appPost.cord_y);

                                                switch_.setEnabled(true);
                                                if (!switch_.isAccessibilityFocused()) {
                                                    if (appPost.gpio == 0) {
                                                        SwitchState.setText("OFF");
                                                        SwitchState.setTextColor(getResources().getColor(R.color.colorAccent,getTheme()));
                                                        switch_.setChecked(false);
                                                    } else {
                                                        SwitchState.setTextColor(getResources().getColor(R.color.accent,getTheme()));
                                                        SwitchState.setText("ON");
                                                        switch_.setChecked(true);
                                                    }
                                                }

                                                switch_.setOncheckListener(new com.gc.materialdesign.views.Switch.OnCheckListener() {
                                                    @Override
                                                    public void onCheck(com.gc.materialdesign.views.Switch view, boolean check) {
                                                        if (check) {
                                                            //Toast.makeText(getApplicationContext(),"Checked",Toast.LENGTH_LONG).show();
                                                            new Thread(new Runnable() {
                                                                @Override
                                                                public void run() {
                                                                    secRef = database.getReference("smartHome/" + user.getUid() + "/dashboard/" + deviceName + "/Application/" + appPost.Id + "/gpio");
                                                                    secRef.setValue(1);
                                                                    gRef = database.getReference("GPIO/"+API+"/"+appPost.Pin);
                                                                    gRef.setValue(1);
                                                                }
                                                            }).start();

                                                            SwitchState.setTextColor(getResources().getColor(R.color.accent,getTheme()));
                                                            SwitchState.setText("ON");

                                                        } else {
                                                            //Toast.makeText(getApplicationContext(),"Checked",Toast.LENGTH_LONG).show();

                                                            new Thread(new Runnable() {
                                                                @Override
                                                                public void run() {
                                                                    secRef = database.getReference("smartHome/" + user.getUid() + "/dashboard/" + deviceName + "/Application/" + appPost.Id + "/gpio");
                                                                    secRef.setValue(0);
                                                                    gRef = database.getReference("GPIO/"+API+"/"+appPost.Pin);
                                                                    gRef.setValue(0);
                                                                }
                                                            }).start();

                                                            SwitchState.setTextColor(getResources().getColor(R.color.colorAccent,getTheme()));
                                                            SwitchState.setText("OFF");

                                                        }
                                                    }
                                                });


                                                YoYo.with(Techniques.FlipInX)
                                                        .duration(700)
                                                        .repeat(0)
                                                        .playOn(switch_);

                                                layout.addView(switc);
                                                break;

                                            case "SeekBar":
                                                View seek = inflater.inflate(R.layout.myseekbar, layout, false);
                                                final Slider seek_ = seek.findViewById(R.id.seekBar);
                                                //SeekBar seek_ = new SeekBar(usersPanel.this);
                                                TextView textSeek = seek.findViewById(R.id.seekBarText);
                                                final TextView stateSeek = seek.findViewById(R.id.SeekStatus);
                                                textSeek.setText(appPost.Name);
                                                seek.setX(appPost.cord_x);
                                                seek.setY(appPost.cord_y);
                                                seek_.setEnabled(true);
                                                if (!seek_.isAccessibilityFocused()) {
                                                    seek_.setValue(appPost.gpio);

                                                    stateSeek.setText(Integer.toString(appPost.gpio));
                                                    //seek_.setProgress(appPost.gpio);
                                                }
    //                                            seek_.setNumericTransformer(new DiscreteSeekBar.NumericTransformer() {
    //                                                @Override
    //                                                public int transform(int value) {
    //                                                    secRef = database.getReference("GPIO/"+API+"/"+appPost.Pin);
    //                                                    secRef.setValue(value);
    //                                                    secRef = database.getReference("smartHome/"+user.getUid()+"/dashboard/"+deviceName+"/Application/"+appPost.Id+"/gpio");
    //                                                    secRef.setValue(value);
    //                                                    return value;
    //                                                }
    //                                            });

                                                seek_.setOnValueChangedListener(new Slider.OnValueChangedListener() {
                                                    @Override
                                                    public void onValueChanged(int value) {
                                                        Value = value;
                                                        handler.postDelayed(new Runnable() {
                                                            @Override
                                                            public void run() {
                                                                stateSeek.setText(Integer.toString(Value));
                                                                secRef = database.getReference("smartHome/" + user.getUid() + "/dashboard/" + deviceName + "/Application/" + appPost.Id + "/gpio");
                                                                secRef.setValue( Value);
                                                                gRef = database.getReference("GPIO/"+API+"/"+appPost.Pin);
                                                                gRef.setValue( Value);
                                                            }
                                                        },2000);
                                                    }
                                                });



    //                                            seek_.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
    //                                                @Override
    //                                                public void onProgressChanged(SeekBar seekBar, final int progress, boolean fromUser) {
    //                                                    //Toast.makeText(getApplicationContext(),"P: "+ progress,Toast.LENGTH_LONG).show();
    //                                                    new Thread(new Runnable() {
    //                                                        @Override
    //                                                        public void run() {
    //                                                            //new Jspyn(API).analogWrite(Integer.parseInt(appPost.Pin),progress);
    //                                                            secRef = database.getReference("GPIO/"+API+"/"+appPost.Pin);
    //                                                            secRef.setValue(progress);
    //                                                            secRef = database.getReference("smartHome/"+user.getUid()+"/dashboard/"+deviceName+"/Application/"+appPost.Id+"/gpio");
    //                                                            secRef.setValue(progress);
    //                                                        }
    //                                                    }).start();
    //
    //                                                }
    //
    //                                                @Override
    //                                                public void onStartTrackingTouch(SeekBar seekBar) {
    //
    //                                                }
    //
    //                                                @Override
    //                                                public void onStopTrackingTouch(SeekBar seekBar) {
    //
    //                                                }
    //                                            });
                                                seek_.setEnabled(true);
                                                YoYo.with(Techniques.FlipInX)
                                                        .duration(700)
                                                        .repeat(0)
                                                        .playOn(seek_);

                                                layout.addView(seek);
                                                break;
                                        }
                                    }
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

}
