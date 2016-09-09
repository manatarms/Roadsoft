package com.example.sachinf.roadsoft;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by sachinf on 8/26/2016.
 */
public class MetaDataHandler{

    //----------------------------------------- READ META DATA --------------------------------------------------------//
    private static final String TAG = "ROADSOFT_LOG";
    private static MediaMetadataRetriever retriever;

    public Map<String, String> readMetaData(Context context, Uri data) {

//        File sdcard = Environment.getExternalStorageDirectory();
        String realPath = getRealPathFromURI(context,data);
        File file = new File(realPath);

        if (file.exists()) {
            // Log.e(TAG, "PATH " + file.getAbsolutePath());

            //Added in API level 10
            //retriever = new MediaMetadataRetriever();

                try {
                    //FOR META DATA
                    //retriever.setDataSource(file.getAbsolutePath());

//                    for (int i = 0; i < 1000; i++) {
//                        //only Metadata != null is printed!
//                        //5 is datetime
//                        if (retriever.extractMetadata(i) != null) {
//                            Log.i(TAG, "Metadata :: " + i + " " + retriever.extractMetadata(i));
//                        }
//                    }
                    Date lastModDate = new Date(file.lastModified());
                    Log.i(TAG, "File last modified @ : " + lastModDate.toString());
                    Log.i(TAG, "File last modified @ : " + String.valueOf(lastModDate.getTime()));
                    long epoch = lastModDate.getTime()/1000;
                    Map<String,String> videoMap = new HashMap<String,String>();
                    videoMap.put("video_name",file.getName());
                    videoMap.put("video_file_path",realPath);
                    videoMap.put("video_created_timestamp",String.valueOf(epoch));
                    return videoMap;

                } catch (Exception e) {
                    Log.e(TAG, "Exception : " + e.getMessage());
                    e.printStackTrace();
                    return null;
                }

        } else {
            Log.e(TAG, ".mp4 file doesn´t exist.");
        }

        return null;
    }
    //----------------------------------------- GET ABSOLUTE DATA PATH --------------------------------------------------------//
    private String getRealPathFromURI(Context context,Uri contentURI) {
        String result;
        Cursor cursor = context.getContentResolver().query(contentURI, null, null, null, null);
        if (cursor == null) {
            result = contentURI.getPath();
            //Log.e(TAG, "RETURNED PATH");
        } else {
            cursor.moveToFirst();
            int ind = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            result = cursor.getString(ind);
            cursor.close();
            //Log.e(TAG, "RETURNED CURSOR");
        }
        return result;
    }
    //----------------------------------------- MAKE TOAST--------------------------------------------------------//
    public void makeResultToast(Context context, int resultCode) {
        if(resultCode == Activity.RESULT_OK) {
            Toast.makeText(context, "Video saved successfully", Toast.LENGTH_LONG).show();
        }else if (resultCode == Activity.RESULT_CANCELED) {
            Toast.makeText(context, "The video was cancelled", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(context, "Something went wrong please try again", Toast.LENGTH_LONG).show();
        }
    }

}
