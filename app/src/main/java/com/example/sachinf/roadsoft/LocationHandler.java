package com.example.sachinf.roadsoft;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;

/**
 * Created by sachinf on 8/29/2016.
 */
public class LocationHandler {

    private static final long LOCATION_REFRESH_TIME = 0;
    private static final float LOCATION_REFRESH_DISTANCE = 0;

    private LocationManager locationManager;
    private LocationListener locationListener;
    private LatLng latlng;
    private String timestamp;
    private String latlngString;
    private String currentSession;
    private String sessionID;

    private String speed;
    private static TimeHandler timeHandler = new TimeHandler();
    private static NotificationHandler notificationHandler = new NotificationHandler();
    private static PermissionHandler permissionHandler = new PermissionHandler();



    public void makeLocationManager(final Context context, Activity activity,final GoogleMap mMap, final TextView latlngText, final SQLHelperHandler sqlHelperHandler) {

        //GET THE LOCATION
        // Acquire a reference to the system Location Manager
        locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        notificationHandler.makeNotification(context,true);


        locationListener = new LocationListener() {
            public void onLocationChanged(Location location) {
                // Called when a new location is found by the network location provider.
                if (MapsActivity.isMonitorLocation()) {
                    locationChangeHandler(context,location,mMap,latlngText,sqlHelperHandler);
                }

            }

            public void onStatusChanged(String provider, int status, Bundle extras) {
            }

            public void onProviderEnabled(String provider) {
            }

            public void onProviderDisabled(String provider) {
            }
        };//END LocationListener

        //GET LOCATION UPDATES
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, LOCATION_REFRESH_TIME,
                    LOCATION_REFRESH_DISTANCE, locationListener);


        } else {
            permissionHandler.getPermission(activity);
        }


    }//END makeLocationManager()


    public void destroyLocationManager(Context context) {
        if ((locationManager != null) && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            locationManager.removeUpdates(locationListener);
            locationManager = null;
        }
    }//END destroyLocationManager()

    private void locationChangeHandler(Context context,Location location,GoogleMap mMap,TextView latlngText,SQLHelperHandler sqlHelperHandler) {
        latlng = new LatLng(location.getLatitude(), location.getLongitude());
        latlngString = latlng.toString();
        sessionID =  MapsActivity.getSessionId();
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latlng));
        timestamp = timeHandler.getSystemTime().toString();
        speed = String.valueOf(location.getSpeed());
        latlngText.setText(latlngString + " AT: " + timestamp +" SPEED: "+ speed+"\n" + latlngText.getText());
        if(MapsActivity.isInsertLocationIntoDatabase()){
            boolean result = sqlHelperHandler.insertLatLngData(latlngString,timestamp, speed);
            if(currentSession != sessionID) {
                //SHOW TOAST ONLY ONCE PER SESSION
                sqlHelperHandler.makeDatabaseResultToast(context, result,SQLHelperHandler.LOCATION);
            }
            currentSession = sessionID;
        }
    }



}
