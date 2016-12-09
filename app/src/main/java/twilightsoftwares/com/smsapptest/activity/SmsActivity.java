package twilightsoftwares.com.smsapptest.activity;

import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.telephony.SmsManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.drive.Drive;
import com.google.android.gms.drive.DriveApi;
import com.google.android.gms.drive.DriveContents;
import com.google.android.gms.drive.DriveFile;
import com.google.android.gms.drive.DriveFolder;
import com.google.android.gms.drive.DriveId;
import com.google.android.gms.drive.MetadataChangeSet;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import twilightsoftwares.com.smsapptest.R;
import twilightsoftwares.com.smsapptest.adapter.SmsAdapter;
import twilightsoftwares.com.smsapptest.modal.SmsDisplayModal;
import twilightsoftwares.com.smsapptest.utils.MarshmallowPermission;

public class SmsActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
    private static final String TAG = "SmsActivity";

    ArrayList<SmsDisplayModal> smsMessagesList = new ArrayList<>();
    ArrayList<SmsDisplayModal> smsMessagesListGroup = new ArrayList<>();
    MarshmallowPermission marshmallowPermission;
    Context context;
    SmsAdapter smsAdapter, smsAdapterGroup;
    //finding views by butterknike library
    @BindView(R.id.editTextMessage)
    EditText input;
    @BindView(R.id.lv_sms)
    ListView lv_messages;
    @BindView(R.id.lv_sms_group)
    ListView lv_messages_group;
    @BindView(R.id.tv_sms_type)
    TextView tv_view_type;
    @BindView(R.id.et_search)
    EditText et_search;
    @BindView(R.id.tv_sms_notification)
    TextView tv_sms_notification;
    private static final int REQUEST_CODE_RESOLUTION = 1;
    private static final  int REQUEST_CODE_OPENER = 2;
    private GoogleApiClient mGoogleApiClient;
    private boolean fileOperation = false;
    private DriveId mFileId;
    public DriveFile file;

    public static SmsActivity inst = new SmsActivity();

    //1E:F6:17:E2:21:AF:65:80:84:E8:AF:BA:04:28:C3:BD:B6:B5:29:94
    @org.jetbrains.annotations.Contract(pure = true)
    public static SmsActivity instance() {
        return inst;
    }
    AlertDialog.Builder new_msg;
    static final int PICK_CONTACT = 11;
    String numberToSend = "09894320513", textToSend;
    SmsManager smsManager = SmsManager.getDefault();
    StringBuilder sb = new StringBuilder();
    String backup;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sms);
        context = this;
        ButterKnife.bind(this);
        marshmallowPermission = new MarshmallowPermission(this);
        displaySms();
        tv_sms_notification.setVisibility(View.GONE);

        et_search.addTextChangedListener(new

                                                 TextWatcher() {
                                                     @Override public void beforeTextChanged (CharSequence s,int start, int count, int after){
                                                     }
                                                     @Override public void onTextChanged (CharSequence s,int start, int before, int count){
                                                         if (smsAdapter != null) smsAdapter.getFilter().filter(s.toString());
                                                     }
                                                     @Override public void afterTextChanged (Editable s){
                                                     }
                                                 }

        );
    }

    @OnClick(R.id.btnSendMessage)
    public void btnSendMessage(View view) {
        if (view.getId() == R.id.btnSendMessage) {

            textToSend = input.getText().toString().trim();
            if (textToSend.length() >= 1)
                getNumberPermission();
            else input.setHint("please type a message");
        }
    }

    @OnClick(R.id.tv_backup)
    public void tv_backup(View view) {
        System.out.println("touched backup");

        if (view.getId() == R.id.tv_backup) {
            uploadtodrive();
            askWritefilepermission();


        }

    }



    private void askWritefilepermission() {

        if (marshmallowPermission.checkPermissionForExternalStorage()) {
            writeToFile("smsbackup", backup);
        } else {
            marshmallowPermission.requestPermissionForExternalStorage();
            askWritefilepermission();
        }


    }

    @OnClick(R.id.tv_sms_type)
    public void tv_sms_type(View view) {
        System.out.println("touched view change");
        if (view.getId() == R.id.tv_sms_type) {


            if (lv_messages.getVisibility() == View.VISIBLE) {
                lv_messages.setVisibility(View.GONE);
                lv_messages_group.setVisibility(View.VISIBLE);
                tv_view_type.setText("tap for seperate view");

            } else {
                lv_messages.setVisibility(View.VISIBLE);
                lv_messages_group.setVisibility(View.GONE);
                tv_view_type.setText("tap for group view");


            }

        }
    }



    private void getNumberPermission() {

        if (marshmallowPermission.checkPermissionForReadingContacts()) {
            getNumber();
        } else {
            marshmallowPermission.requestPermissionForReadContacts();
            getNumberPermission();
        }

    }

    private void getNumber() {


        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType(ContactsContract.Contacts.CONTENT_TYPE);
        startActivityForResult(intent, PICK_CONTACT);

    }

    @Override
    public void onStart() {
        super.onStart();
        inst = this;

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {


            case REQUEST_CODE_RESOLUTION:
                if (resultCode == RESULT_OK) {
                    mGoogleApiClient.connect();
                }
                break;
            case PICK_CONTACT:
                Cursor cursor = null;
                String phoneNumber = "";
                List<String> allNumbers = new ArrayList<String>();
                int phoneIdx = 0;
                try {
                    Uri result = data.getData();
                    String id = result.getLastPathSegment();
                    cursor = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, ContactsContract.CommonDataKinds.Phone.CONTACT_ID + "=?", new String[]{id}, null);
                    phoneIdx = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DATA);
                    if (cursor.moveToFirst()) {
                        while (cursor.isAfterLast() == false) {
                            phoneNumber = cursor.getString(phoneIdx);
                            allNumbers.add(phoneNumber);
                            cursor.moveToNext();
                        }
                    } else {
                        //no results actions
                    }
                } catch (Exception e) {
                    //error actions
                } finally {
                    if (cursor != null) {
                        cursor.close();
                    }

                    final CharSequence[] items = allNumbers.toArray(new String[allNumbers.size()]);
                    AlertDialog.Builder builder = new AlertDialog.Builder(SmsActivity.this);
                    builder.setTitle("Choose a number");
                    builder.setItems(items, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int item) {
                            String selectedNumber = items[item].toString();
                            selectedNumber = selectedNumber.replace("-", "");
                            numberToSend = selectedNumber;
                            System.out.println(numberToSend);
                            sendMessageCheckPermission();

                        }
                    });
                    AlertDialog alert = builder.create();
                    if (allNumbers.size() > 1) {
                        alert.show();
                    } else {
                        String selectedNumber = phoneNumber.toString();
                        selectedNumber = selectedNumber.replace("-", "");
                        numberToSend = selectedNumber;
                        System.out.println(numberToSend);

                        sendMessageCheckPermission();

                    }

                    if (phoneNumber.length() == 0) {
                        //no numbers found actions
                    }
                }
                break;

            default:
                super.onActivityResult(requestCode, resultCode, data);
                break;

        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mGoogleApiClient == null) {

            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addApi(Drive.API)
                    .addScope(Drive.SCOPE_FILE)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .build();
        }

        mGoogleApiClient.connect();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mGoogleApiClient != null) {

            // disconnect Google API client connection
            mGoogleApiClient.disconnect();
        }
        super.onPause();
    }
    private void sendMessageCheckPermission() {
        if (marshmallowPermission.checkPermissionForSendSMS()) {
            sendMessage();
        } else {
            marshmallowPermission.requestPermissionForSendSMS();
            sendMessageCheckPermission();
        }


    }

    private void sendMessage() {
        smsManager.sendTextMessage(numberToSend, null, textToSend, null, null);
        Toast.makeText(this, "Message sent!", Toast.LENGTH_SHORT).show();
    }


    private void displaySms() {
        if (marshmallowPermission.checkPermissionForReadsSMS()) {
            refreshSmsInbox();
        } else {
            marshmallowPermission.requestPermissionForReadsSMS();
            displaySms();
        }
    }

    public void updateInbox(SmsDisplayModal smsDisplayModal) {
        smsMessagesList.add(smsDisplayModal);
        smsAdapter.notifyDataSetChanged();
        tv_sms_notification.setVisibility(View.VISIBLE);

        new_msg=  new AlertDialog.Builder(context)
                .setTitle("New Message").setMessage(smsDisplayModal.name+"\n"+smsDisplayModal.message)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) { dialog.dismiss();}
                } );
        new_msg.show();

    }

    HashMap<String, StringBuilder> hashMap = new HashMap<>();

    public void refreshSmsInbox() {


        ContentResolver contentResolver = getContentResolver();
        Cursor smsInboxCursor = contentResolver.query(Uri.parse("content://sms/inbox"), null, null, null, null);
        int indexBody = smsInboxCursor.getColumnIndex("body");
        int indexAddress = smsInboxCursor.getColumnIndex("address");
        if (indexBody < 0 || !smsInboxCursor.moveToFirst()) return;
        smsMessagesList.clear();
        do {

            smsMessagesList.add(new SmsDisplayModal(smsInboxCursor.getString(indexAddress), smsInboxCursor.getString(indexBody)));
            sb.append("\n");
            sb.append("number  :  " + smsInboxCursor.getString(indexAddress) + "  :");
            sb.append("\n");
            sb.append("message  :  " + smsInboxCursor.getString(indexBody));
            sb.append("\n");

            if (hashMap.containsKey(smsInboxCursor.getString(indexAddress))) {
                StringBuilder builder = hashMap.get(smsInboxCursor.getString(indexAddress));
                builder.append(smsInboxCursor.getString(indexBody) + "\n");
            } else {
                StringBuilder builder = new StringBuilder();
                builder.append(smsInboxCursor.getString(indexBody) + "\n");
                hashMap.put(smsInboxCursor.getString(indexAddress), builder);
            }

        } while (smsInboxCursor.moveToNext());
        backup = sb.toString();
        System.out.println(backup);
        smsAdapter = new SmsAdapter(getApplicationContext(), smsMessagesList);
        lv_messages.setAdapter(smsAdapter);
        System.out.println("hashMap.size():  " + hashMap.size());


        for (Map.Entry<String, StringBuilder> entry : hashMap.entrySet()) {
            String key = entry.getKey();
            StringBuilder tab = entry.getValue();

            smsMessagesListGroup.add(new SmsDisplayModal(key, tab.toString()));
            // do something with key and/or tab
        }
        smsAdapterGroup = new SmsAdapter(getApplicationContext(), smsMessagesList);
        lv_messages_group.setAdapter(smsAdapterGroup);
        System.out.println("smsMessagesListGroup " + smsMessagesListGroup.size());
    }


    //backup sms in local

    public void writeToFile(String fileName, String body) {
        FileOutputStream fos = null;

        try {
            final File dir = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/smsapp/");

            if (!dir.exists()) {
                if (!dir.mkdirs()) {
                    Log.e("ALERT", "could not create the directories");
                }
            }

            final File myFile = new File(dir, fileName + ".txt");

            if (!myFile.exists()) {
                myFile.createNewFile();
                Toast.makeText(this, "Backup saved in your mobile with file name : smsbackup, in the folder smsapp.", Toast.LENGTH_SHORT).show();

            }

            fos = new FileOutputStream(myFile);

            fos.write(body.getBytes());
            fos.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    private void uploadtodrive() {
        fileOperation = true;

        // create new contents resource
        Drive.DriveApi.newDriveContents(mGoogleApiClient)
                .setResultCallback(driveContentsCallback);
    }


    @Override
    public void onConnectionFailed(ConnectionResult result) {

        Log.i(TAG, "GoogleApiClient connection failed: " + result.toString());

        if (!result.hasResolution()) {

            GoogleApiAvailability.getInstance().getErrorDialog(this, result.getErrorCode(), 0).show();
            return;
        }



        try {

            result.startResolutionForResult(this, REQUEST_CODE_RESOLUTION);

        } catch (IntentSender.SendIntentException e) {

            Log.e(TAG, "Exception while starting resolution activity", e);
        }
    }


    @Override
    public void onConnected(Bundle connectionHint) {

        Toast.makeText(getApplicationContext(), "Connected", Toast.LENGTH_LONG).show();
    }


    @Override
    public void onConnectionSuspended(int cause) {

        Log.i(TAG, "GoogleApiClient connection suspended");
    }


    public void CreateFileOnGoogleDrive(DriveApi.DriveContentsResult result){


        final DriveContents driveContents = result.getDriveContents();

        // Perform I/O off the UI thread.
        new Thread() {
            @Override
            public void run() {
                // write content to DriveContents
                OutputStream outputStream = driveContents.getOutputStream();
                Writer writer = new OutputStreamWriter(outputStream);
                try {
                    writer.write(backup);
                    writer.close();
                } catch (IOException e) {
                    Log.e(TAG, e.getMessage());
                }

                MetadataChangeSet changeSet = new MetadataChangeSet.Builder()
                        .setTitle("smsbackup")
                        .setMimeType("text/plain")
                        .setStarred(true).build();

                // create a file in root folder
                Drive.DriveApi.getRootFolder(mGoogleApiClient)
                        .createFile(mGoogleApiClient, changeSet, driveContents)
                        .setResultCallback(fileCallback);
            }
        }.start();
    }

    /**
     * Handle result of Created file
     */
    final private ResultCallback<DriveFolder.DriveFileResult> fileCallback = new
            ResultCallback<DriveFolder.DriveFileResult>() {
                @Override
                public void onResult(DriveFolder.DriveFileResult result) {
                    if (result.getStatus().isSuccess()) {

                        Toast.makeText(getApplicationContext(), "file created: "+""+
                                result.getDriveFile().getDriveId(), Toast.LENGTH_LONG).show();

                    }

                    return;

                }
            };


    final ResultCallback<DriveApi.DriveContentsResult> driveContentsCallback =
            new ResultCallback<DriveApi.DriveContentsResult>() {
                @Override
                public void onResult(DriveApi.DriveContentsResult result) {

                    if (result.getStatus().isSuccess()) {

                        if (fileOperation == true) {

                            CreateFileOnGoogleDrive(result);

                        }
                    }


                }
            };


}
