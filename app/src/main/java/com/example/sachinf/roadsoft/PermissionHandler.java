package com.example.sachinf.roadsoft;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.support.v4.app.ActivityCompat;
import android.widget.Toast;

/**
 * Created by sachinf on 8/29/2016.
 */
public class PermissionHandler {

    //----------------------------------------- GET PERMISSIONS --------------------------------------------------------//
    private static final int MY_PERMISSIONS_REQUEST_CODE = 0;


    public void getPermission(Activity activity) {
        ActivityCompat.requestPermissions(activity,new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.CAMERA,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.RECORD_AUDIO},
                MY_PERMISSIONS_REQUEST_CODE);

    }

    public void makePermissionToast(Context context){
        CharSequence text = "Oops! We need access permission.";
        int duration = Toast.LENGTH_SHORT;
        Toast toast = Toast.makeText(context, text, duration);
        toast.show();
    }

}
