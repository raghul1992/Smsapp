package twilightsoftwares.com.smsapptest.utils;

import android.annotation.TargetApi;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.telephony.SmsMessage;

import twilightsoftwares.com.smsapptest.activity.SmsActivity;
import twilightsoftwares.com.smsapptest.modal.SmsDisplayModal;

/**
 * Created by twilightuser on 7/12/16.
 */
public class SmsBroadcastReceiver extends BroadcastReceiver {

    public static final String SMS_BUNDLE = "pdus";
    SmsDisplayModal smsDisplayModal;

    @TargetApi(Build.VERSION_CODES.M)
    public void onReceive(Context context, Intent intent) {
        Bundle intentExtras = intent.getExtras();

        if (intentExtras != null) {
            Object[] sms = (Object[]) intentExtras.get(SMS_BUNDLE);
            String smsMessageStr = "";
            for (int i = 0; i < sms.length; ++i) {
                String format = intentExtras.getString("format");
                SmsMessage smsMessage = SmsMessage.createFromPdu((byte[]) sms[i], format);

                String smsBody = smsMessage.getMessageBody().toString();
                String address = smsMessage.getOriginatingAddress();
                smsDisplayModal = new SmsDisplayModal(address,smsBody);


            }

            SmsActivity.instance().updateInbox(smsDisplayModal);
        }
    }
}