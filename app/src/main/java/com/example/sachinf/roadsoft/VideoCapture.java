package com.example.sachinf.roadsoft;

import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.media.CamcorderProfile;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import java.io.IOException;



/**
 * Created by sachinf on 8/19/2016.
 */


public class VideoCapture extends AppCompatActivity implements View.OnClickListener, SurfaceHolder.Callback {
    MediaRecorder recorder;
    SurfaceHolder holder;
    boolean recording = false;

    private static final int MY_PERMISSIONS_REQUEST_LOCATION = 0;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        recorder = new MediaRecorder();
        if ((ActivityCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) && (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED)
                &&(ActivityCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)
                &&(ActivityCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)
                ) {
            initRecorder();
        }else{
            getPermission();
        }

        setContentView(R.layout.video_capture);


        SurfaceView cameraView = (SurfaceView) findViewById(R.id.cameraView);
        holder = cameraView.getHolder();
        holder.addCallback(this);
        holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

        cameraView.setClickable(true);
        cameraView.setOnClickListener(this);

    }

    private void initRecorder() {


        recorder.setAudioSource(MediaRecorder.AudioSource.DEFAULT);
        recorder.setVideoSource(MediaRecorder.VideoSource.DEFAULT);

        CamcorderProfile cpHigh = CamcorderProfile.get(CamcorderProfile.QUALITY_HIGH);
        recorder.setProfile(cpHigh);
        recorder.setOutputFile("/sdcard/videocapture_example.mp4");
        recorder.setMaxDuration(50000); // 50 seconds
        recorder.setMaxFileSize(5000000); // Approximately 5 megabytes
    }

    private void prepareRecorder(){
        recorder.setPreviewDisplay(holder.getSurface());

        try{
            recorder.prepare();
        }catch (IllegalStateException e){
            e.printStackTrace();
            finish();
        }catch (IOException e){
            e.printStackTrace();
            finish();
        }
    }

    public void onClick(View v){
        if(recording){
            recorder.stop();
            recording = false;

            initRecorder();
            prepareRecorder();

        }else{
            recording = true;
            recorder.start();

        }
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        prepareRecorder();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int i, int i1, int i2) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        if(recording){
            recorder.stop();
            recording = false;
        }
        recorder.release();
        finish();
    }

    private void getPermission() {
        ActivityCompat.requestPermissions(this,
                new String[]{android.Manifest.permission.RECORD_AUDIO, android.Manifest.permission.CAMERA, android.Manifest.permission.READ_EXTERNAL_STORAGE, android.Manifest.permission.WRITE_EXTERNAL_STORAGE},
                MY_PERMISSIONS_REQUEST_LOCATION);
    }
}
