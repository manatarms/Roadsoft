package com.example.sachinf.roadsoft;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.provider.MediaStore;
import android.widget.Toast;

/**
 * Created by sachinf on 8/26/2016.
 */
public class DefaultCameraHandler {
    private static final boolean DEFAULT_CAMERA = true;
    private static final int REQUEST_VIDEO_CAPTURE = 1;

    //----------------------------------------- CAMERA --------------------------------------------------------//
    public void dispatchTakeVideoIntent(Context context, Activity activity) {
        if (DEFAULT_CAMERA) {
            Intent takeVideoIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);

            if (takeVideoIntent.resolveActivity(context.getPackageManager()) != null) {
                activity.startActivityForResult(takeVideoIntent, REQUEST_VIDEO_CAPTURE);
            }
        } else {
            Intent cameraIntent = new Intent(context, VideoCapture.class);
            activity.startActivity(cameraIntent);

        }

    }


}
