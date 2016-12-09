package twilightsoftwares.com.smsapptest;

import android.app.Application;
import android.content.SharedPreferences;
import android.net.Uri;
import android.preference.PreferenceManager;

/**
 * Created by twilightuser on 7/12/16.
 */

public class SmsApplication extends Application {

    public static SharedPreferences sp;
    public static final Uri INBOX_URI = Uri.parse("content://sms/inbox");

    @Override
    public void onCreate() {
        super.onCreate();

      //  PreferenceManager.setDefaultValues(this, R.xml.settings, false);
        sp = PreferenceManager.getDefaultSharedPreferences(this);
    }
}
