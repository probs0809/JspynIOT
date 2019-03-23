package app.jspyn.jspyniot;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.ClipData;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.DragEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.OvershootInterpolator;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.crystal.crystalpreloaders.widgets.CrystalPreloader;
import com.gc.materialdesign.views.ButtonFlat;
import com.gc.materialdesign.views.Slider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.jaredrummler.materialspinner.MaterialSpinner;

import java.util.concurrent.atomic.AtomicBoolean;

import app.jspyn.jspyniot.non_activity.ApplicationPost;
import app.jspyn.jspyniot.non_activity.DropDownClass;
import app.jspyn.jspyniot.non_activity.gpioPost;
import cn.pedant.SweetAlert.SweetAlertDialog;
import tyrantgit.explosionfield.ExplosionField;

public class setControl extends AppCompatActivity {
    RelativeLayout mGrid;
    LinearLayout buttonForm, switchForm, seekForm;
    View subButton, subSwitch, subSeek;
    EditText buttonName, switchName, seekName;
    MaterialSpinner buttonSpinner, switchSpinner, seekSpinner;
    FirebaseDatabase database;
    DatabaseReference myRef, gpioRef;
    FirebaseUser User;
    DropDownClass drop;
    Handler handler;
    CrystalPreloader loader;
    int identifier = 0;
    TextView title;
    String[] pinList = {"Select Relay", "Relay_1", "Relay_2", "Relay_3", "Relay_4"};
    String[] pwmList = {"Select Variable", "Var_1", "Var_2"};
    ArrayAdapter aa, ab;
    TextView drawer;
    LayoutInflater inflater;
    private FirebaseAuth mAuth;
    private FrameLayout mScrollView;
    private ValueAnimator mAnimator;
    private AtomicBoolean mIsScrolling = new AtomicBoolean(false);
    private String deviceName, API;
    private DrawerLayout drawerLayout;
    int x,y;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_control);
        drawerLayout = (DrawerLayout) findViewById(R.id.setControl);
        final NavigationView navigationView = (NavigationView) findViewById(R.id.nc2);

        drop = new DropDownClass();
        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        handler = new Handler(this.getMainLooper());
        Intent i = getIntent();
        deviceName = i.getStringExtra("deviceName");
        API = i.getStringExtra("API");
        Toast.makeText(getApplicationContext(), "Setup Device: " + deviceName, Toast.LENGTH_LONG).show();
        mGrid = (RelativeLayout) findViewById(R.id.layoutTwo);
        buttonForm = (LinearLayout) findViewById(R.id.buttonForm);
        switchForm = (LinearLayout) findViewById(R.id.switchForm);
        seekForm = (LinearLayout) findViewById(R.id.seekForm);
        mScrollView = (FrameLayout) findViewById(R.id.scroll_view);
        //mScrollView.setSmoothScrollingEnabled(true);
        subButton = (View) findViewById(R.id.submitButton);
        subSeek = (View) findViewById(R.id.submitSeek);
        subSwitch = (View) findViewById(R.id.submitSwitch);
        buttonName = (EditText) findViewById(R.id.vName);
        switchName = (EditText) findViewById(R.id.vsName);
        seekName = (EditText) findViewById(R.id.vsName2);
        loader = (CrystalPreloader) findViewById(R.id.loader);
        buttonSpinner = (MaterialSpinner) findViewById(R.id.buttonPinNo);
        switchSpinner = (MaterialSpinner) findViewById(R.id.switchPinNo);
        seekSpinner = (MaterialSpinner) findViewById(R.id.seekPinNo);
        database = FirebaseDatabase.getInstance();
        mAuth = FirebaseAuth.getInstance();
        User = mAuth.getCurrentUser();
        drawer = (TextView) findViewById(R.id.bac2);
        aa = new ArrayAdapter(getApplicationContext(), android.R.layout.simple_spinner_item, pinList);
        ab = new ArrayAdapter(getApplicationContext(), android.R.layout.simple_spinner_item, pwmList);
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        title = (TextView) findViewById(R.id.title2);

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.addButton2:
                        mGrid.setVisibility(View.GONE);
                        buttonForm.setVisibility(View.VISIBLE);
                        switchForm.setVisibility(View.GONE);
                        seekForm.setVisibility(View.GONE);
                        buttonSpinner.setItems(pinList);
                        buttonSpinner.setOnItemSelectedListener(drop);
                        drawerLayout.closeDrawers();
                        break;

                    case R.id.addSwitch2:
                        mGrid.setVisibility(View.GONE);
                        switchForm.setVisibility(View.VISIBLE);
                        buttonForm.setVisibility(View.GONE);
                        seekForm.setVisibility(View.GONE);
                        switchSpinner.setItems(pinList);
                        switchSpinner.setOnItemSelectedListener(drop);
                        drawerLayout.closeDrawers();
                        break;

                    case R.id.addSeekbar2:
                        mGrid.setVisibility(View.GONE);
                        seekForm.setVisibility(View.VISIBLE);
                        switchForm.setVisibility(View.GONE);
                        buttonForm.setVisibility(View.GONE);
                        seekSpinner.setItems(pwmList);
                        seekSpinner.setOnItemSelectedListener(drop);
                        drawerLayout.closeDrawers();
                        break;

                    case R.id.Submit2:

//                        gpioRef = database.getReference("GPIO/" + API);
//                        gpioPost gpioPOst = new gpioPost(0, 0, 0, 0, 0, 0);
//                        gpioRef.setValue(gpioPOst);
//                        //gpioRef = database.getReference("GPIO/"+API);
//                        //gpioPost gpioPst = new gpioPost(0,0,0,0,0,0);
//                        //gpioRef.setValue(gpioPst);

                        Intent i = new Intent(setControl.this, dashBoard.class);
                        startActivity(i);
                        drawerLayout.closeDrawers();
                        break;
                }
                return true;
            }
        });

        inflater = LayoutInflater.from(this);

        drawer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.openDrawer(navigationView);
            }
        });

        subButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mGrid.setVisibility(View.VISIBLE);
                buttonForm.setVisibility(View.GONE);

                if (buttonName.getText().toString().isEmpty() || pinList[drop.position].equals("Select Relay")) {
                    Toast.makeText(getApplicationContext(), "Please check your input", Toast.LENGTH_LONG).show();
                } else {
                    new createButton(buttonName.getText().toString(), pinList[drop.position]);
                }

            }
        });

        subSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mGrid.setVisibility(View.VISIBLE);
                switchForm.setVisibility(View.GONE);

                if (switchName.getText().toString().isEmpty() || pinList[drop.position].equals("Select Relay")) {
                    Toast.makeText(getApplicationContext(), "Please check your input", Toast.LENGTH_LONG).show();
                } else {
                    new createSwitch(switchName.getText().toString(), pinList[drop.position]);
                }
            }
        });

        subSeek.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mGrid.setVisibility(View.VISIBLE);
                seekForm.setVisibility(View.GONE);

                if (seekName.getText().toString().isEmpty() || pwmList[drop.position].equals("Select Variable")) {
                    Toast.makeText(getApplicationContext(), "Please check your input", Toast.LENGTH_LONG).show();
                } else {
                    new createSeek(seekName.getText().toString(), pwmList[drop.position]);
                }
            }
        });

        myRef = database.getReference("smartHome/" + User.getUid() + "/dashboard/" + deviceName + "/Application");


        new Thread(new Runnable() {
            @Override
            public void run() {
                handler.post(new Runnable() {
                    @Override
                    public void run() {

                        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                for (DataSnapshot panelDataSnapshot : dataSnapshot.getChildren()) {
                                    final ApplicationPost appPost = panelDataSnapshot.getValue(ApplicationPost.class);

                                    switch (appPost.Type) {
                                        case "Button":
                                            new createButton(appPost.Name, appPost.Pin, appPost.Id, appPost.cord_x, appPost.cord_y);
                                            break;

                                        case "Switch":
                                            new createSwitch(appPost.Name, appPost.Pin, appPost.Id, appPost.cord_x, appPost.cord_y);
                                            break;

                                        case "SeekBar":
                                            new createSeek(appPost.Name, appPost.Pin, appPost.Id, appPost.cord_x, appPost.cord_y);
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


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    private void startScrolling(int from, int to) {
        if (from != to && mAnimator == null) {
            mIsScrolling.set(true);
            mAnimator = new ValueAnimator();
            mAnimator.setInterpolator(new OvershootInterpolator());
            mAnimator.setDuration(Math.abs(to - from));
            mAnimator.setIntValues(from, to);
            mAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator valueAnimator) {
                    //mScrollView.smoothScrollTo(0, (int) valueAnimator.getAnimatedValue());
                }
            });
            mAnimator.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mIsScrolling.set(false);
                    mAnimator = null;
                }
            });
            mAnimator.start();
        }
    }

    private void stopScrolling() {
        if (mAnimator != null) {
            mAnimator.cancel();
        }
    }

    @Override
    public void onBackPressed() {
        Toast.makeText(getApplicationContext(), "Please select submit", Toast.LENGTH_LONG).show();
    }

    static public class LongPressListener implements View.OnLongClickListener {
        @Override
        public boolean onLongClick(View view) {
            final ClipData data = ClipData.newPlainText("", "");
            View.DragShadowBuilder shadowBuilder = new View.DragShadowBuilder(view);
            view.startDragAndDrop(data, shadowBuilder, view, 0);
            //view.setVisibility(View.INVISIBLE);
            return true;
        }
    }

    class createButton {
        createButton(final String Name, final String pin) {
            identifier = identifier + 1;
            RelativeLayout relativeLayout = (RelativeLayout) findViewById(R.id.layoutTwo);
            relativeLayout.setOnDragListener(new DragListener());
            final View itemView = inflater.inflate(R.layout.button, relativeLayout, false);
            itemView.setBackgroundColor(getResources().getColor(R.color.cardview_shadow_start_color,getTheme()));
            final ButtonFlat text = (ButtonFlat) itemView.findViewById(R.id.Button);
            itemView.setX(44);
            itemView.setY(224);
            text.setText(Name);
            itemView.setOnLongClickListener(new LongPressListener());
            itemView.setOnDragListener(new DragListener(identifier, Name, "Button", pin));
            relativeLayout.addView(itemView);
        }

        createButton(final String Name, final String pin, final int id, final float cord_x, final float cord_y) {
            identifier = id;
            RelativeLayout relativeLayout = (RelativeLayout) findViewById(R.id.layoutTwo);
            relativeLayout.setOnDragListener(new DragListener());
            View itemView = inflater.inflate(R.layout.button, relativeLayout, false);
            itemView.setBackgroundColor(getResources().getColor(R.color.cardview_shadow_start_color,getTheme()));
            ButtonFlat text = (ButtonFlat) itemView.findViewById(R.id.Button);
            text.setText(Name);
            itemView.setX(cord_x);
            itemView.setY(cord_y);
            itemView.setOnLongClickListener(new LongPressListener());
            itemView.setOnDragListener(new DragListener(id, Name, "Button", pin));
            relativeLayout.addView(itemView);
        }
    }

    class createSwitch {
        createSwitch(final String Name, final String pin) {
            identifier = identifier + 1;
            RelativeLayout relativeLayout = (RelativeLayout) findViewById(R.id.layoutTwo);
            relativeLayout.setOnDragListener(new DragListener());
            final View itemView = inflater.inflate(R.layout.myswitch, relativeLayout, false);
            itemView.setBackgroundColor(getResources().getColor(R.color.cardview_shadow_start_color,getTheme()));
            itemView.setX(44);
            itemView.setY(224);
            final com.gc.materialdesign.views.Switch aSwitch = (com.gc.materialdesign.views.Switch) itemView.findViewById(R.id.Switch);
            TextView textS = (TextView) itemView.findViewById(R.id.SwitchText);
            textS.setText(Name);
            //aSwitch.setText(Name);
            itemView.setOnLongClickListener(new LongPressListener());
            itemView.setOnDragListener(new DragListener(identifier, Name, "Switch", pin));
            relativeLayout.addView(itemView);
        }

        createSwitch(final String Name, final String pin, final int id, final float cord_x, final float cord_y) {
            identifier = id;
            RelativeLayout relativeLayout = (RelativeLayout) findViewById(R.id.layoutTwo);
            relativeLayout.setOnDragListener(new DragListener());
            View itemView = inflater.inflate(R.layout.myswitch, relativeLayout, false);
            itemView.setBackgroundColor(getResources().getColor(R.color.cardview_shadow_start_color,getTheme()));
            com.gc.materialdesign.views.Switch aSwitch = (com.gc.materialdesign.views.Switch) itemView.findViewById(R.id.Switch);
            TextView textS = (TextView) itemView.findViewById(R.id.SwitchText);
            textS.setText(Name);
            //aSwitch.setText(Name);
            itemView.setX(cord_x);
            itemView.setY(cord_y);
            itemView.setOnLongClickListener(new LongPressListener());
            itemView.setOnDragListener(new DragListener(id, Name, "Switch", pin));
            relativeLayout.addView(itemView);
        }
    }

    class createSeek {

        createSeek(final String Name, final String pin) {
            identifier = identifier + 1;
            RelativeLayout relativeLayout = (RelativeLayout) findViewById(R.id.layoutTwo);
            relativeLayout.setOnDragListener(new DragListener());
            final View itemView = inflater.inflate(R.layout.myseekbar, relativeLayout, false);
            itemView.setBackgroundColor(getResources().getColor(R.color.cardview_shadow_start_color,getTheme()));
            itemView.setX(44);
            itemView.setY(224);
            final Slider seekBar = (Slider) itemView.findViewById(R.id.seekBar);
            TextView textSeek = (TextView) itemView.findViewById(R.id.seekBarText);
            textSeek.setText(Name);
            itemView.setOnLongClickListener(new LongPressListener());
            itemView.setOnDragListener(new DragListener(identifier, Name, "SeekBar", pin));
            //seekBar.setOnLongClickListener(new LongPressListener());
            relativeLayout.addView(itemView);
        }

        createSeek(final String Name, final String pin, final int id, final float cord_x, final float cord_y) {
            identifier = id;
            RelativeLayout relativeLayout = (RelativeLayout) findViewById(R.id.layoutTwo);
            relativeLayout.setOnDragListener(new DragListener());
            View itemView = inflater.inflate(R.layout.myseekbar, relativeLayout, false);
            itemView.setBackgroundColor(getResources().getColor(R.color.cardview_shadow_start_color,getTheme()));
            Slider seekBar = (Slider) itemView.findViewById(R.id.seekBar);
            TextView textSeek = (TextView) itemView.findViewById(R.id.seekBarText);
            textSeek.setText(Name);
            itemView.setX(cord_x);
            itemView.setY(cord_y);
            itemView.setOnLongClickListener(new LongPressListener());
            itemView.setOnDragListener(new DragListener(id, Name, "SeekBar", pin));
            //seekBar.setOnLongClickListener(new LongPressListener());
            relativeLayout.addView(itemView);
        }


    }

    class DragListener implements View.OnDragListener {
        private float cord_x, cord_y;
        private String Pin;
        private String Name, Type;
        private int Did;
        private boolean bool;

        DragListener() {
            bool = false;
        }

        DragListener(int Did, String Name, String Type, String pin) {
            this.Name = Name;
            this.Type = Type;
            this.Pin = pin;
            this.Did = Did;
            this.bool = true;

        }

        @Override
        public boolean onDrag(View v, DragEvent event) {
            final View view = (View) event.getLocalState();
            switch (event.getAction()) {

                case DragEvent.ACTION_DRAG_LOCATION:
                    if (view == v) return true;
                    x = (int) event.getX();
                    y = (int) event.getY();
//                    final Rect rect = new Rect();
//                    mScrollView.getHitRect(rect);
//                    final int scrollY = mScrollView.getScrollY();

                    if (event.getY() < 150) {
                        title.setText("Drop To Remove");
                        title.setTextColor(getColor(R.color.red));
                    } else {
                        title.setText("Drag Here To Remove");
                        title.setTextColor(getColor(R.color.white));
                    }
//                    if (event.getY() - scrollY > mScrollView.getBottom() - 250) {
//                        startScrolling(scrollY, mGrid.getHeight());
//                    } else if (event.getY() - scrollY < mScrollView.getTop() + 250) {
//                        startScrolling(scrollY, 0);
//                    } else {
//                        stopScrolling();
//                    }

                    new Thread(new Runnable() {
                        @Override
                        public void run() {

                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    view.setX(x);
                                    view.setY(y);
                                }
                            });
                        }
                    }).start();

                    break;

                case DragEvent.ACTION_DROP:
                    view.setVisibility(View.VISIBLE);
                    cord_x = view.getX();
                    cord_y = view.getY();

                    if (bool) {

                        if (cord_y < 150) {
                            new SweetAlertDialog(setControl.this, SweetAlertDialog.WARNING_TYPE)
                                    .setTitleText("Are you sure?")
                                    .setContentText("You want to remove widget??")
                                    .setConfirmText("Remove")
                                    .setCancelText("Cancel")
                                    .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                        @Override
                                        public void onClick(SweetAlertDialog sDialog) {
                                            database.getReference("smartHome/" + User.getUid() + "/dashboard/" + deviceName + "/Application/" + Did)
                                                    .removeValue();

                                            ExplosionField mExplosionField = ExplosionField.attach2Window(setControl.this);
                                            mExplosionField.explode(view);
                                            mGrid.removeView(view);
                                            sDialog
                                                    .hide();

                                        }
                                    })
                                    .show();


                            title.setText("Drag Here To Remove");
                            title.setTextColor(getColor(R.color.white));


                        } else {
                            ApplicationPost AppValues = new ApplicationPost(Did, Name, Type, Pin, cord_x, cord_y);
                            myRef = database.getReference("smartHome/" + User.getUid() + "/dashboard/" + deviceName + "/Application/" + Did);
                            myRef.setValue(AppValues);
                        }

                    }
                    break;

                case DragEvent.ACTION_DRAG_ENDED:
                    if (!event.getResult()) {
                        view.setVisibility(View.VISIBLE);
                    }
                    break;
            }
            return true;
        }
    }

}