package app.jspyn.jspyniot;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ShortcutInfo;
import android.content.pm.ShortcutManager;
import android.graphics.Color;
import android.graphics.drawable.Icon;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.os.Build;
import android.provider.ContactsContract;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.daimajia.swipe.SwipeLayout;
import com.daimajia.swipe.adapters.RecyclerSwipeAdapter;
import com.gc.materialdesign.views.ButtonIcon;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.jaredrummler.materialspinner.MaterialSpinner;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.logging.Handler;

import app.jspyn.jspyniot.non_activity.DropDownClass;
import cn.pedant.SweetAlert.SweetAlertDialog;
import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by prabodhmayekar on 26/01/19.
 */

class RecyclerViewAdapter extends RecyclerSwipeAdapter<RecyclerViewAdapter.SimpleViewHolder> {
    private final String ESP_URL = "http://192.168.4.1/login";
    private FirebaseDatabase database;
    private DatabaseReference myRef;
    FirebaseUser user;
    private DropDownClass drop;
    private Context mContext;
    private Handler handler;
    private ArrayList<String> smart_home_sensor_list = new ArrayList<>();
    private ArrayList<String> api_key_list = new ArrayList<>();
    private ArrayList<String> device_name_list = new ArrayList<>();

    RecyclerViewAdapter(Context context, ArrayList<String> API, ArrayList<String> DeviceName) {
        this.mContext = context;
        this.device_name_list = DeviceName;
        this.api_key_list = API;
    }

    @Override
    public SimpleViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.dashview, parent, false);
        return new SimpleViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final SimpleViewHolder viewHolder, @SuppressLint("RecyclerView") final int position) {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        user = mAuth.getCurrentUser();
        String deviceName = device_name_list.get(position);



        viewHolder.edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent j = new Intent(mContext, setControl.class);
                j.putExtra("deviceName", device_name_list.get(position));
                j.putExtra("api_key_list", api_key_list.get(position));
                //startActivity(j);
                mContext.startActivity(j);

            }
        });

        viewHolder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new SweetAlertDialog(mContext, SweetAlertDialog.WARNING_TYPE)
                        .setTitleText("Are you sure?")
                        .setContentText("You won't be able to recover this device!")
                        .setConfirmText("Delete")
                        .setCancelText("Cancel")
                        .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sDialog) {
                                myRef = database.getReference("smartHome/" + user.getUid() + "/dashboard/" + device_name_list.get(position));
                                myRef.removeValue();
                                myRef = database.getReference("apiStatus/" + api_key_list.get(position));
                                myRef.removeValue();
                                sDialog
                                        .setTitleText("Deleted!")
                                        .setContentText("Your device has been deleted!")
                                        .setConfirmText("OK")
                                        .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                            @Override
                                            public void onClick(SweetAlertDialog sweetAlertDialog) {
                                                sweetAlertDialog.hide();
                                            }
                                        })
                                        .changeAlertType(SweetAlertDialog.SUCCESS_TYPE);
                            }
                        })
                        .show();
            }
        });

        viewHolder.config.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                configure(api_key_list.get(position));
            }
        });
        viewHolder.button.setText(deviceName.replace("-", " "));
        myRef = database.getReference("apiStatus/" + api_key_list.get(position));
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                try {
                    DeviceStatus d = dataSnapshot.getValue(DeviceStatus.class);
                    assert d != null;
                    if (d.status == 1){
                        viewHolder.button.setTextColor(Color.parseColor("#40CF0E"));
                    }
                    else{
                        viewHolder.button.setTextColor(Color.parseColor("#F44F07"));
                    }
                }
                catch (Exception e){

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        viewHolder.button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(mContext, usersPanel.class);
                i.putExtra("deviceName", device_name_list.get(position));
                i.putExtra("api", api_key_list.get(position));
                //startActivity(i);
                mContext.startActivity(i);

            }
        });
        viewHolder.addShortcut.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N_MR1)
            @Override
            public void onClick(View v) {

                    addShortcut(device_name_list.get(position), api_key_list.get(position));

            }
        });
    }

    @Override
    public int getItemCount() {
        return api_key_list.size();
    }

    @Override
    public int getSwipeLayoutResourceId(int position) {
        return 0;
    }

    private void configure(final String API) {
        database = FirebaseDatabase.getInstance();
        smart_home_sensor_list.add("Select Sensor");
        myRef = database.getReference("jspynData/smartHomeSensors");
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot dashdataSnapshot : dataSnapshot.getChildren()) {
                    smart_home_sensor_list.add(dashdataSnapshot.getValue().toString());
                    //Toast.makeText(mContext,dashdataSnapshot.getValue().toString(),Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        View cView = LayoutInflater.from(mContext).inflate(R.layout.configure_jspyn, null);
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(mContext);
        mBuilder.setView(cView);
        final AlertDialog dialog = mBuilder.create();
        final TextView ssid = (TextView) cView.findViewById(R.id.ssid);
        final TextView password = (TextView) cView.findViewById(R.id.wPassword);
        final MaterialSpinner sensor = (MaterialSpinner) cView.findViewById(R.id.sensor);
        final TextView sName = (TextView) cView.findViewById(R.id.sensorId);
        TextView set = (TextView) cView.findViewById(R.id.config);
        drop = new DropDownClass();
        sensor.setItems(smart_home_sensor_list);
        sensor.setOnItemSelectedListener(drop);
        dialog.show();
        set.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String ssidS = ssid.getText().toString();
                final String passwordS = password.getText().toString();
                final String customSName = sName.getText().toString();
                final String SENSOR = smart_home_sensor_list.get(drop.position);
                if (ssidS.equals("")) {
                    Toast.makeText(mContext, "Please input SSID.", Toast.LENGTH_LONG).show();
                }
                else if(isInternetOn()){
                    showDialog();
                }
                else {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            String postBody = "SSID=" + ssidS + "&PASSWORD=" + passwordS + "&API_KEY=" + API + "&SENSOR_TYPE=" + SENSOR + "&UID=" + user.getUid() + "&SENSOR_NAME=" + customSName.replace(' ','-');
                            try {
                                postRequest(ESP_URL, postBody);

                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }).start();

                    dialog.dismiss();

                }

            }
        });


    }

    private void postRequest(String postUrl, String postBody) throws IOException {

        OkHttpClient client = new OkHttpClient();

        //RequestBody body = RequestBody.create(JSON, postBody);

        Request request = new Request.Builder()
                .url(postUrl + "?" + postBody)
                .build();

        Call call = client.newCall(request);

        Response response = null;
        try {
            response = call.execute();

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    static class SimpleViewHolder extends RecyclerView.ViewHolder {
        ButtonIcon edit;
        ButtonIcon delete;
        ButtonIcon config;
        TextView button;
        ButtonIcon addShortcut;
        SwipeLayout swipeLayout;

        SimpleViewHolder(View view) {
            super(view);
            edit = (ButtonIcon) view.findViewById(R.id.edit);
            delete = (ButtonIcon) view.findViewById(R.id.deleteDevice);
            config = (ButtonIcon) view.findViewById(R.id.config);
            button = (TextView) view.findViewById(R.id.d10);
            addShortcut = (ButtonIcon)view.findViewById(R.id.addShortcut);
            SwipeLayout swipeLayout = (SwipeLayout) view.findViewById(R.id.sl);
            swipeLayout.setShowMode(SwipeLayout.ShowMode.LayDown);
            swipeLayout.addDrag(SwipeLayout.DragEdge.Left, (LinearLayout) view.findViewById(R.id.bottom_wrapper));
            swipeLayout.addSwipeListener(new SwipeLayout.SwipeListener() {
                @Override
                public void onClose(SwipeLayout layout) {
                    //when the SurfaceView totally cover the BottomView.
                }

                @Override
                public void onUpdate(SwipeLayout layout, int leftOffset, int topOffset) {
                    //you are swiping.
                }

                @Override
                public void onStartOpen(SwipeLayout layout) {

                }

                @Override
                public void onOpen(SwipeLayout layout) {
                    //when the BottomView totally show.
                }

                @Override
                public void onStartClose(SwipeLayout layout) {

                }

                @Override
                public void onHandRelease(SwipeLayout layout, float xvel, float yvel) {
                    //when user's hand released.
                }
            });
            YoYo.with(Techniques.StandUp)
                    .duration(700)
                    .repeat(0)
                    .playOn(view.findViewById(R.id.sl));

        }
    }

    private boolean isInternetOn() {

        // get Connectivity Manager object to check connection
        ConnectivityManager connec = (ConnectivityManager)mContext.getSystemService(Context.CONNECTIVITY_SERVICE);

        assert connec != null;
        NetworkInfo activeNetwork = connec.getActiveNetworkInfo();
        // Check for network connections
        if (activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE ) {

            // if connected with internet

            Toast.makeText(mContext, " Connected ", Toast.LENGTH_LONG).show();
            return true;

        }
        return false;
    }

    private void showDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setMessage("Please disconnect mobile data")
                .setCancelable(false)
                .setPositiveButton("Disconnect Data", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        mContext.startActivity(new Intent("android.settings.SETTINGS"));
//                        setMobileDataEnabled(mContext,false);
                    }
                })
                .setNegativeButton("Quit", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }

    private void addShortcut(String name, String api) {
        //Adding shortcut for MainActivity
        //on Home screen

        ShortcutManager mShortcutManager = mContext.getSystemService(ShortcutManager.class);

        assert mShortcutManager != null;
        if (mShortcutManager.isRequestPinShortcutSupported()) {
            Intent i = new Intent(mContext, userPanelTwo.class);
            i.putExtra("deviceName", name);
            i.putExtra("api", api);
            i.setAction(Intent.ACTION_MAIN);
            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);

            ShortcutInfo pinShortcutInfo = new ShortcutInfo.Builder(mContext, api)
                    .setIcon(Icon.createWithResource(mContext,R.mipmap.ic_launcher))
                    .setShortLabel(name)
                    .setIntent(i)
                    .build();
            Intent pinnedShortcutCallbackIntent = mShortcutManager.createShortcutResultIntent(pinShortcutInfo);

            // Configure the intent so that your app's broadcast receiver gets
            // the callback successfully.For details, see PendingIntent.getBroadcast().
            PendingIntent successCallback = PendingIntent.getBroadcast(mContext, /* request code */ 0,
                    pinnedShortcutCallbackIntent, /* flags */ 0);

            mShortcutManager.requestPinShortcut(pinShortcutInfo,
                    successCallback.getIntentSender());
        }
    }


}
