package com.example.sachinf.roadsoft;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.ToggleButton;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.Map;
import java.util.UUID;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private static final int MY_PERMISSIONS_REQUEST_CODE = 0;

    private static boolean monitorLocation = true;
    private static boolean insertLocationIntoDatabase = false;
    private static String sessionId;

    private GoogleMap mMap;
    private TextView latlngText;
    private Button recordButton;
    private ToggleButton locationToggle;
    private ArrayList gotPermission = new ArrayList(5);

    private static  MetaDataHandler metaDataHandler = new MetaDataHandler();
    private static NotificationHandler notificationHandler = new NotificationHandler();
    private static PermissionHandler permissionHandler = new PermissionHandler();
    private static DefaultCameraHandler defaultCameraHandler = new DefaultCameraHandler();
    private static LocationHandler locationHandler = new LocationHandler();
    private static SQLHelperHandler sqlHelperHandler;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        //GET ELEMENTS
        latlngText = (TextView) findViewById(R.id.latlngtextView);
        latlngText.setMovementMethod(new ScrollingMovementMethod());

        locationToggle = (ToggleButton) findViewById(R.id.locationToggle);
        recordButton = (Button) findViewById(R.id.recordButton);

        //GET DB HELPER
        sqlHelperHandler = new SQLHelperHandler(this);

        //SET LISTENERS
        setRecordButtonListener();
        setToggleChangeListener();

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            permissionHandler.getPermission(MapsActivity.this);
        }

    } // END onCreate

// ----------------------------------------- SETTERS & GETTERS --------------------------------------------------------//
    public static boolean isInsertLocationIntoDatabase() {
        return insertLocationIntoDatabase;
    }

    public static boolean isMonitorLocation() {
        return monitorLocation;
    }

    public static String getSessionId(){
        return sessionId;
    }

    private static void setSessionId(){
        sessionId = UUID.randomUUID().toString();
    }
// ----------------------------------------- SET LISTENERS --------------------------------------------------------//
    private void setRecordButtonListener(){
        recordButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                    defaultCameraHandler.dispatchTakeVideoIntent(getApplicationContext(),MapsActivity.this);
                    insertLocationIntoDatabase = true;
                    setSessionId();
                } else {
                    permissionHandler.getPermission(MapsActivity.this);
                }

            }
        });
    }
    private void setToggleChangeListener() {
        locationToggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    monitorLocation = true;
                    recordButton.setEnabled(true);
                    notificationHandler.makeNotification(getApplicationContext(), true);

                    locationHandler.makeLocationManager(getApplicationContext(),MapsActivity.this,mMap,latlngText,sqlHelperHandler);
                } else {
                    monitorLocation = false;
                    insertLocationIntoDatabase = false;

                    recordButton.setEnabled(false);
                    notificationHandler.makeNotification(getApplicationContext(), false);
                    locationHandler.destroyLocationManager(getApplicationContext());
                }
            }
        });
    }

//-----------------------------------------MAKE MAP --------------------------------------------------------//
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(0, 0), 14.0f));
        // Add a marker in Sydney and move the camera
        locationHandler.makeLocationManager(getApplicationContext(),MapsActivity.this,mMap,latlngText,sqlHelperHandler);
        recordButton.setEnabled(true);
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);

        } else {
            permissionHandler.getPermission(MapsActivity.this);
        }


    }// END  onMapReady


//----------------------------------------- GOT CAMERA RESULT --------------------------------------------------------//
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        insertLocationIntoDatabase = false;

        if (resultCode == RESULT_OK) {
            Uri dataUri = data.getData();
            Map<String,String> videoMap = metaDataHandler.readMetaData(getApplicationContext(),dataUri);
            boolean result = sqlHelperHandler.insertVideoData(videoMap.get("video_name"),videoMap.get("video_file_path"),videoMap.get("video_created_timestamp"));
            sqlHelperHandler.makeDatabaseResultToast(getApplicationContext(),result,SQLHelperHandler.VIDEO);
        }else{
            boolean result = sqlHelperHandler.deleteCancelledVideoEntries();
            sqlHelperHandler.makeDatabaseResultToast(this,result,SQLHelperHandler.DELETE);
        }
        metaDataHandler.makeResultToast(getApplicationContext(),resultCode);

    }

//----------------------------------------- GOT PERMISSIONS --------------------------------------------------------//



    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_CODE: {
                if (grantResults.length > 0 ) {
                    for(int i=0;i<grantResults.length;i++){
                        if(grantResults[i] == PackageManager.PERMISSION_GRANTED){
                            gotPermission.add(i,true);
                        }
                        else{
                            gotPermission.add(i,false);
                            permissionHandler.makePermissionToast(getApplicationContext());
                        }
                    }
                }else {
                    permissionHandler.makePermissionToast(getApplicationContext());
                    gotPermission.add(new Boolean[]{false,false,false,false,false});
                }

                if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    mMap.setMyLocationEnabled(true);
                }

            }
            return;
        }
    }





}//END activity
