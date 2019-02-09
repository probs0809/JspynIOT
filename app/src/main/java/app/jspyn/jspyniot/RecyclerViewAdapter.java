package app.jspyn.jspyniot;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.daimajia.swipe.SwipeLayout;
import com.daimajia.swipe.adapters.RecyclerSwipeAdapter;
import com.gc.materialdesign.views.ButtonIcon;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Handler;


import cn.pedant.SweetAlert.SweetAlertDialog;
import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by prabodhmayekar on 26/01/19.
 */

class RecyclerViewAdapter extends RecyclerSwipeAdapter<RecyclerViewAdapter.SimpleViewHolder> {
    FirebaseAuth mAuth;
    FirebaseDatabase database;
    DatabaseReference myRef;
    FirebaseUser user;

    private final String postUrl = "http://192.168.4.1/login";
    public String[] sensorList = {"Select Item","Temperature","Humidity___"};
    AlertDialog.Builder mBuilder;
    DropDownClass drop;
    private Context mContext;
    private Handler handler;
    private ArrayList<String> API = new ArrayList<>();
    private ArrayList<String> DeviceName = new ArrayList<>();

    public static class SimpleViewHolder extends RecyclerView.ViewHolder {
        ButtonIcon edit ;
        ButtonIcon delete ;
        ButtonIcon config ;
        TextView button ;
        SwipeLayout swipeLayout ;

        public SimpleViewHolder(View view) {
            super(view);
            edit = (ButtonIcon)view.findViewById(R.id.edit);
            delete = (ButtonIcon)view.findViewById(R.id.deleteDevice);
            config = (ButtonIcon)view.findViewById(R.id.config);
            button = (TextView)view.findViewById(R.id.d10);
            SwipeLayout swipeLayout = (SwipeLayout)view.findViewById(R.id.sl);
            swipeLayout.setShowMode(SwipeLayout.ShowMode.LayDown);
            swipeLayout.addDrag(SwipeLayout.DragEdge.Left, (LinearLayout)view.findViewById(R.id.bottom_wrapper));
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
        }
    }




    //protected SwipeItemRecyclerMangerImpl mItemManger = new SwipeItemRecyclerMangerImpl(this);

    public RecyclerViewAdapter(Context context, ArrayList<String> API,ArrayList<String> DeviceName) {
        this.mContext = context;
        this.DeviceName = DeviceName;
        this.API = API;
    }


    @Override
    public SimpleViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.dashview,parent,false);
        return new SimpleViewHolder(view);
    }

    @Override
    public void onBindViewHolder(SimpleViewHolder viewHolder, final int position) {
        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        user = mAuth.getCurrentUser();

        viewHolder.edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent j =  new Intent(mContext, setControl.class);
                j.putExtra("deviceName",DeviceName.get(position));
                j.putExtra("API",API.get(position));
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
                                myRef = database.getReference("smartHome/"+user.getUid()+"/dashboard/"+DeviceName.get(position));
                                myRef.removeValue();
                                myRef = database.getReference("GPIO/"+API.get(position));
                                myRef.removeValue();
                                sDialog
                                        .setTitleText("Deleted!")
                                        .setContentText("Your device has been deleted!")
                                        .changeAlertType(SweetAlertDialog.SUCCESS_TYPE);
                            }
                        })
                        .show();
            }
        });

        viewHolder.config.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               configure(API.get(position));
            }
        });
        viewHolder.button.setText(DeviceName.get(position));
        viewHolder.button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(mContext, usersPanel.class);
                i.putExtra("deviceName",DeviceName.get(position));
                i.putExtra("API",API.get(position));
                //startActivity(i);
                mContext.startActivity(i);

            }
        });
    }


    @Override
    public int getItemCount() {
        return API.size();
    }

    @Override
    public int getSwipeLayoutResourceId(int position) {
        return 0;
    }


    public void configure(final String API){

        View cView = LayoutInflater.from(mContext).inflate(R.layout.configure_jspyn,null);
        mBuilder =  new AlertDialog.Builder(mContext);
        mBuilder.setView(cView);

        final AlertDialog dialog = mBuilder.create();

        final TextView ssid = (TextView)cView.findViewById(R.id.ssid);
        final TextView password = (TextView)cView.findViewById(R.id.wPassword);
        final Spinner sensor = (Spinner) cView.findViewById(R.id.sensor);
        final TextView sName = (TextView)cView.findViewById(R.id.sensorId);
        TextView set = (TextView)cView.findViewById(R.id.config);

        drop = new DropDownClass();
        sensor.setOnItemSelectedListener(drop);
        ArrayAdapter aa = new ArrayAdapter(mContext,android.R.layout.simple_spinner_item,sensorList);
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

                    Toast.makeText(mContext,"Please select the correct pin number...",Toast.LENGTH_LONG).show();
                }
                else {
                    //final Intent i = new Intent(DashBoard.this, setControl.class);
                    //i.putExtra("deviceName",dName);
                    new Thread(new Runnable() {
                        @Override
                        public void run() {



                            String postBody = "SSID="+ssidS+"&PASSWORD="+passwordS+"&API_KEY="+API+"&SENSOR_TYPE="+SENSOR+"&UID="+user.getUid()+"&SENSOR_NAME="+customSName;

                            try {
                                postRequest(postUrl,postBody);

                            }
                            catch (IOException e) {
                                e.printStackTrace();
                            }




                        }
                    }).start();

                    dialog.dismiss();

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
            //Toast.makeText(mContext,"Status: "+response.body().toString(),Toast.LENGTH_LONG).show();

        } catch (IOException e) {
            e.printStackTrace();
            //Toast.makeText(mContext,"Status: "+e,Toast.LENGTH_LONG).show();
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
