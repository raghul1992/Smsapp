package twilightsoftwares.com.smsapptest.views;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

public class TextViewLight extends TextView {


    Context mContext;
    static String textFont = "Montserrat-Light.ttf";

    public TextViewLight(Context context) {
        super(context);
        this.mContext = context;
        inite();
    }

    public TextViewLight(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
        inite();
    }

    public TextViewLight(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mContext = context;
        inite();
    }

    private void inite() {
        if (!isInEditMode()) {
            if(textFont == "" ||textFont == null){

            }else {
                this.setTypeface(Typeface.createFromAsset(mContext.getAssets(), textFont));
            }
        }
    }
}


