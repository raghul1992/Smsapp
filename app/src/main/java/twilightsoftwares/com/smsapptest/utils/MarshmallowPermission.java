package twilightsoftwares.com.smsapptest.utils;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.widget.Toast;

/**
 * Created by twilightuser on 4/11/16.
 */
public class MarshmallowPermission {

    public static final int SMS_READ_PERMISSION_REQUEST_CODE = 1;
    public static final int SMS_SEND_PERMISSION_REQUEST_CODE = 2;
    public static final int EXTERNAL_STORAGE_PERMISSION_REQUEST_CODE = 3;
    public static final int CONTACTS_READ_PERMISSION_REQUEST_CODE = 4;

    Activity activity;

    public MarshmallowPermission(Activity activity) {
        this.activity = activity;
    }

    public boolean checkPermissionForReadsSMS(){
        int result = ContextCompat.checkSelfPermission(activity, Manifest.permission.READ_SMS);
        if (result == PackageManager.PERMISSION_GRANTED){
            return true;
        } else {
            return false;
        }
    }

    public boolean checkPermissionForSendSMS(){
        int result = ContextCompat.checkSelfPermission(activity, Manifest.permission.SEND_SMS);
        if (result == PackageManager.PERMISSION_GRANTED){
            return true;
        } else {
            return false;
        }
    }

    public boolean checkPermissionForExternalStorage(){
        int result = ContextCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (result == PackageManager.PERMISSION_GRANTED){
            return true;
        } else {
            return false;
        }
    }
    public boolean checkPermissionForReadingContacts(){
        int result = ContextCompat.checkSelfPermission(activity, Manifest.permission.READ_CONTACTS);
        if (result == PackageManager.PERMISSION_GRANTED){
            return true;
        } else {
            return false;
        }
    }

    public void requestPermissionForSendSMS(){
        if (ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.SEND_SMS)){
            Toast.makeText(activity, "SEND SMS permission needed. Please allow in App Settings for additional functionality.", Toast.LENGTH_LONG).show();
        } else {
            ActivityCompat.requestPermissions(activity,new String[]{Manifest.permission.SEND_SMS},SMS_SEND_PERMISSION_REQUEST_CODE);
        }
    }


    public void requestPermissionForReadsSMS(){
        if (ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.READ_SMS)){
            Toast.makeText(activity, "Microphone permission needed for READING SMS. Please allow in App Settings for additional functionality.", Toast.LENGTH_LONG).show();
        } else {
            ActivityCompat.requestPermissions(activity,new String[]{Manifest.permission.READ_SMS},SMS_READ_PERMISSION_REQUEST_CODE);
        }
    }

    public void requestPermissionForExternalStorage(){
        if (ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE)){
            Toast.makeText(activity, "External Storage permission needed. Please allow in App Settings for additional functionality.", Toast.LENGTH_LONG).show();
        } else {
            ActivityCompat.requestPermissions(activity,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},EXTERNAL_STORAGE_PERMISSION_REQUEST_CODE);
        }
    }

    public void requestPermissionForReadContacts(){
        if (ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.READ_CONTACTS)){
            Toast.makeText(activity, "Microphone permission needed for READING SMS. Please allow in App Settings for additional functionality.", Toast.LENGTH_LONG).show();
        } else {
            ActivityCompat.requestPermissions(activity,new String[]{Manifest.permission.READ_CONTACTS},CONTACTS_READ_PERMISSION_REQUEST_CODE);
        }
    }




}