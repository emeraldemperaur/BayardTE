package iot.empiaurhouse.bayardte.utils;

import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;

public class TypeWriterTextView extends androidx.appcompat.widget.AppCompatTextView {

    private CharSequence sequence;
    private int mIndex;
    private long delay = 150; //default is 150 milliseconds

    public TypeWriterTextView(Context context) {
        super(context);
    }


    public TypeWriterTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }


    private Handler handler = new Handler();
    private Runnable runnable = new Runnable() {

        @Override
        public void run() {
            setText(sequence.subSequence(0, mIndex++));
            if (mIndex <= sequence.length()) {
                handler.postDelayed(runnable, delay);
            }
        }
    };


    public void displayTextWithAnimation(CharSequence txt) {
        sequence = txt;
        mIndex = 0;

        setText("");
        handler.removeCallbacks(runnable);
        handler.postDelayed(runnable, delay);
    }


    public void setCharacterDelay(long m) {
        delay = m;
    }



}
