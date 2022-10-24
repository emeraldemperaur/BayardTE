package iot.empiaurhouse.bayardte;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.wearable.watchface.CanvasWatchFaceService;
import android.support.wearable.watchface.WatchFaceService;
import android.support.wearable.watchface.WatchFaceStyle;
import android.util.TypedValue;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.View;
import android.view.WindowInsets;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.ref.WeakReference;
import java.util.Calendar;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

import iot.empiaurhouse.bayardte.wearutils.SunDial;
import iot.empiaurhouse.bayardte.wearutils.SynopsisManager;

/**
 * Analog watch face with a ticking second hand. In ambient mode, the second hand isn"t
 * shown. On devices with low-bit ambient mode, the hands are drawn without anti-aliasing in ambient
 * mode. The watch face is drawn with less contrast in mute mode.
 * <p>
 * Important Note: Because watch face apps do not have a default Activity in
 * their project, you will need to set your Configurations to
 * "Do not launch Activity" for both the Wear and/or Application modules. If you
 * are unsure how to do this, please review the "Run Starter project" section
 * in the Google Watch Face Code Lab:
 * https://codelabs.developers.google.com/codelabs/watchface/index.html#0
 */
public class MyWatchFace extends CanvasWatchFaceService {

    /*
     * Updates rate in milliseconds for interactive mode. We update once a second to advance the
     * second hand.
     */
    private static final long INTERACTIVE_UPDATE_RATE_MS = TimeUnit.SECONDS.toMillis(1);

    /**
     * Handler message id for updating the time periodically in interactive mode.
     */
    private static final int MSG_UPDATE_TIME = 0;

    @Override
    public Engine onCreateEngine() {
        return new Engine();
    }

    private static class EngineHandler extends Handler {
        private final WeakReference<MyWatchFace.Engine> mWeakReference;

        public EngineHandler(MyWatchFace.Engine reference) {
            mWeakReference = new WeakReference<>(reference);
        }

        @Override
        public void handleMessage(Message msg) {
            MyWatchFace.Engine engine = mWeakReference.get();
            if (engine != null) {
                switch (msg.what) {
                    case MSG_UPDATE_TIME:
                        engine.handleUpdateTimeMessage();
                        break;
                }
            }
        }
    }

    private class Engine extends CanvasWatchFaceService.Engine {
        private static final float HOUR_STROKE_WIDTH = 5f;
        private static final float MINUTE_STROKE_WIDTH = 3f;
        private static final float SECOND_TICK_STROKE_WIDTH = 2f;

        private static final float CENTER_GAP_AND_CIRCLE_RADIUS = 4f;

        private static final int SHADOW_RADIUS = 6;
        /* Handler to update the time once a second in interactive mode. */
        private final Handler mUpdateTimeHandler = new EngineHandler(this);
        private Calendar mCalendar;
        private final BroadcastReceiver mTimeZoneReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                mCalendar.setTimeZone(TimeZone.getDefault());
                invalidate();
            }
        };
        private boolean mRegisteredTimeZoneReceiver = false;
        private boolean mMuteMode;
        private float mCenterX;
        private float mCenterY;
        private float mSecondHandLength;
        private float sMinuteHandLength;
        private float sHourHandLength;
        private int mWatchHandColor;
        private int mWatchHandHighlightColor;
        private int mWatchHandShadowColor;
        private Paint mBackgroundPaint;
        private Bitmap mBackgroundBitmap;
        private Bitmap mGrayBackgroundBitmap;
        private Paint ring1Paint;
        private Bitmap ring1Bitmap;
        private Paint ring2Paint;
        private Bitmap ring2Bitmap;
        private Paint ring3Paint;
        private boolean mAmbient;
        private boolean mLowBitAmbient;
        private boolean mBurnInProtection;
        private SynopsisManager synopsisManager;
        private int specW, specH;
        private View myLayout;
        private TextView day, date, month, hour, minute, second;
        private final Point displaySize = new Point();
        float mXOffset = 0;
        float mYOffset = 0;
        private ImageView bteRing1;
        private ImageView bteRing2;
        private ImageView bteRing3;
        private ImageView bteRing3B;
        private TextView bteHours;
        private TextView bteMinutes;
        private TextView bteZone;
        private TextView btePeriod;
        private TextView bteTemp;
        private TextView bteCity;
        private TextView bteME;
        private String bteCityStr;
        private String hexCode1 = "#FFFFFF";
        private String hexCode2 = "#FFFFFF";
        private String hexCode3 = "#000000";
        private Canvas teCanvas;
        private LocationManager locationManager;


        @Override
        public void onCreate(SurfaceHolder holder) {
            super.onCreate(holder);

            setWatchFaceStyle(new WatchFaceStyle.Builder(MyWatchFace.this)
                    .setAcceptsTapEvents(true)
                    .build());

            mCalendar = Calendar.getInstance();
            synopsisManager = new SynopsisManager();
            synopsisManager.getModel();
            locationManager = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);

            LayoutInflater inflater =
                    (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            myLayout = inflater.inflate(R.layout.watchface, null);
            Display display = ((WindowManager) getSystemService(Context.WINDOW_SERVICE))
                    .getDefaultDisplay();
            display.getSize(displaySize);

            bteRing1 = (ImageView) myLayout.findViewById(R.id.bte_r1);
            bteRing2 = (ImageView) myLayout.findViewById(R.id.bte_r2);
            bteRing3 = (ImageView) myLayout.findViewById(R.id.bte_r3_v2);
            bteRing3B = (ImageView) myLayout.findViewById(R.id.bte_r3b_v2);
            bteHours = (TextView) myLayout.findViewById(R.id.bte_hours);
            bteMinutes = (TextView) myLayout.findViewById(R.id.bte_minutes);
            btePeriod = (TextView) myLayout.findViewById(R.id.bte_period);
            bteZone = (TextView) myLayout.findViewById(R.id.bte_timeZone);
            bteTemp = (TextView) myLayout.findViewById(R.id.bte_temp);
            bteCity = (TextView) myLayout.findViewById(R.id.bte_city);
            bteME = (TextView) myLayout.findViewById(R.id.bte_me);



        }




        @Override
        public void onDestroy() {
            mUpdateTimeHandler.removeMessages(MSG_UPDATE_TIME);
            super.onDestroy();
        }

        @Override
        public void onPropertiesChanged(Bundle properties) {
            super.onPropertiesChanged(properties);
            mLowBitAmbient = properties.getBoolean(PROPERTY_LOW_BIT_AMBIENT, false);
            mBurnInProtection = properties.getBoolean(PROPERTY_BURN_IN_PROTECTION, false);
        }

        @Override
        public void onTimeTick() {
            super.onTimeTick();
            invalidate();
        }

        @Override
        public void onAmbientModeChanged(boolean inAmbientMode) {
            super.onAmbientModeChanged(inAmbientMode);
            mAmbient = inAmbientMode;

            updateWatchHandStyle();

            /* Check and trigger whether or not timer should be running (only in active mode). */
            updateTimer();
        }

        private void updateWatchHandStyle() {
            if (mAmbient) {
                mBackgroundPaint.setAntiAlias(true);
                ring1Paint.setAntiAlias(true);


            } else {
                mBackgroundPaint.setAntiAlias(true);
                ring1Paint.setAntiAlias(true);
                ring1Paint.setShadowLayer(SHADOW_RADIUS, 0, 0, mWatchHandShadowColor);

            }
        }

        @Override
        public void onInterruptionFilterChanged(int interruptionFilter) {
            super.onInterruptionFilterChanged(interruptionFilter);
            boolean inMuteMode = (interruptionFilter == WatchFaceService.INTERRUPTION_FILTER_NONE);

            /* Dim display in mute mode. */
            if (mMuteMode != inMuteMode) {
                mMuteMode = inMuteMode;
                invalidate();
            }
        }

        @Override
        public void onSurfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            super.onSurfaceChanged(holder, format, width, height);

            /*
             * Find the coordinates of the center point on the screen, and ignore the window
             * insets, so that, on round watches with a "chin", the watch face is centered on the
             * entire screen, not just the usable portion.
             */
            mCenterX = width / 2f;
            mCenterY = height / 2f;

            /*
             * Calculate lengths of different hands based on watch screen size.
             */
            mSecondHandLength = (float) (mCenterX * 0.875);
            sMinuteHandLength = (float) (mCenterX * 0.75);
            sHourHandLength = (float) (mCenterX * 0.5);

            /* Scale loaded background image (more efficient) if surface dimensions change. */

            /*
             * Create a gray version of the image only if it will look nice on the device in
             * ambient mode. That means we don"t want devices that support burn-in
             * protection (slight movements in pixels, not great for images going all the way to
             * edges) and low ambient mode (degrades image quality).
             *
             * Also, if your watch face will know about all images ahead of time (users aren"t
             * selecting their own photos for the watch face), it will be more
             * efficient to create a black/white version (png, etc.) and load that when you need it.
             */
            if (!mBurnInProtection && !mLowBitAmbient) {
                //initGrayBackgroundBitmap();
            }
        }

        private Bitmap scaleBitmap(int width, Bitmap bitmap) {
            float scale = ((float) width) / (float) mBackgroundBitmap.getWidth();
            bitmap = Bitmap.createScaledBitmap(bitmap, (int) (mBackgroundBitmap.getWidth() * scale), (int) (mBackgroundBitmap.getHeight() * scale), true);
            return bitmap;
        }



        /**
         * Captures tap event (and tap type). The {@link WatchFaceService#TAP_TYPE_TAP} case can be
         * used for implementing specific logic to handle the gesture.
         */
        @Override
        public void onTapCommand(int tapType, int x, int y, long eventTime) {
            Animation rotationAnimation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.rotate);
            Animation deRotationAnimation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.derotate);


            switch (tapType) {
                case TAP_TYPE_TOUCH:
                    ring1Paint = new Paint();
                    ring1Paint.setColor(Color.RED);
                    ring3Paint = new Paint();
                    ring3Paint.setColor(Color.RED);
                    hexCode1 = "#FFFFFF";
                    hexCode2 = "#FFFFFF";
                    hexCode3 = "#000000";
                    if (bteHours.getVisibility() == View.INVISIBLE){
                        bteHours.setVisibility(View.VISIBLE);
                        bteMinutes.setVisibility(View.VISIBLE);
                        btePeriod.setVisibility(View.VISIBLE);
                        bteZone.setVisibility(View.VISIBLE);
                        bteTemp.setVisibility(View.VISIBLE);
                        bteCity.setVisibility(View.VISIBLE);
                        bteRing1.setColorFilter(Color.WHITE);
                        bteRing2.setColorFilter(Color.WHITE);
                        bteRing3.setColorFilter(Color.BLACK);
                        bteRing3B.setColorFilter(Color.WHITE);
                        bteME.setVisibility(View.INVISIBLE);

                    }
                    else if (bteHours.getVisibility() == View.VISIBLE){
                        bteHours.setVisibility(View.INVISIBLE);
                        bteMinutes.setVisibility(View.INVISIBLE);
                        btePeriod.setVisibility(View.INVISIBLE);
                        bteZone.setVisibility(View.INVISIBLE);
                        bteTemp.setVisibility(View.INVISIBLE);
                        bteCity.setVisibility(View.INVISIBLE);
                        bteRing1.setColorFilter(Color.WHITE);
                        bteRing2.setColorFilter(Color.WHITE);
                        bteRing3.setColorFilter(Color.WHITE);
                        bteRing3B.setColorFilter(Color.BLACK);
                        bteME.setVisibility(View.VISIBLE);

                    }
                    Toast.makeText(getApplicationContext(), R.string.message, Toast.LENGTH_LONG)
                            .show();
                    // The user has started touching the screen.
                    break;
                case TAP_TYPE_TOUCH_CANCEL:
                    // The user has started a different gesture or otherwise cancelled the tap.

                    break;
                case TAP_TYPE_TAP:
                    // The user has completed the tap gesture.
                    ring1Paint = new Paint();
                    ring1Paint.setColor(Color.RED);
                    ring3Paint = new Paint();
                    ring3Paint.setColor(Color.RED);
                    hexCode1 = "#FFFFFF";
                    hexCode2 = "#FFFFFF";
                    hexCode3 = "#000000";
                    Toast.makeText(getApplicationContext(), R.string.message, Toast.LENGTH_SHORT)
                            .show();
                    break;
            }
            invalidate();
        }

        @Override
        public void onDraw(Canvas canvas, Rect bounds) {
            long now = System.currentTimeMillis();
            teCanvas = canvas;
            mCalendar.setTimeInMillis(now);
            myLayout.measure(specW, specH);
            myLayout.layout(0, 0, myLayout.getMeasuredWidth(), myLayout.getMeasuredHeight());
            final float seconds =
                    (mCalendar.get(Calendar.SECOND) + mCalendar.get(Calendar.MILLISECOND) / 1000f);
            final float secondsRotation = seconds * 6f;

            final float minutesRotation = mCalendar.get(Calendar.MINUTE) * 6f;

            final float hourHandOffset = mCalendar.get(Calendar.MINUTE) / 2f;
            final float hoursRotation = (mCalendar.get(Calendar.HOUR) * 30) + hourHandOffset;

            Integer hour = mCalendar.getTime().getHours();
            Integer minute = mCalendar.getTime().getMinutes();
            SunDial sunDial = new SunDial();
            String hourStr = sunDial.fetchHourTime();
            String minuteStr = sunDial.fetchMinuteTime();
            String timePeriod = sunDial.fetchTimePeriod();
            String timeZone = sunDial.fetchTimeZone();



            // Draw it to the Canvas
            canvas.drawColor(Color.parseColor(hexCode1));
            canvas.translate(mXOffset, mYOffset);
            myLayout.draw(canvas);
            bteRing1.setColorFilter(Color.parseColor(hexCode1));
            bteRing2.setColorFilter(Color.parseColor(hexCode2));
            bteRing3.setColorFilter(Color.parseColor(hexCode3));
            bteRing1.setRotation(secondsRotation);
            bteRing2.setRotation(secondsRotation - 3f);
            bteHours.setText(hourStr);
            bteMinutes.setText(minuteStr);
            btePeriod.setText(timePeriod);
            bteZone.setText(timeZone);

        }

        @Override
        public void onApplyWindowInsets(WindowInsets insets) {
            super.onApplyWindowInsets(insets);

            if (insets.isRound()) {
                // Shrink the face to fit on a round screen
                //mYOffset = mXOffset = displaySize.x * 0.1f;
                //displaySize.y -= 2 * mXOffset;
                //displaySize.x -= 2 * mXOffset;
            } else {
                mXOffset = mYOffset = 0;
            }

            // Recompute the MeasureSpec fields - these determine the actual size of the layout
            specW = View.MeasureSpec.makeMeasureSpec(displaySize.x, View.MeasureSpec.EXACTLY);
            specH = View.MeasureSpec.makeMeasureSpec(displaySize.y, View.MeasureSpec.EXACTLY);
        }





        public int spToPx(float sp, Context context) {
            return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, sp, context.getResources().getDisplayMetrics());
        }

        @Override
        public void onVisibilityChanged(boolean visible) {
            super.onVisibilityChanged(visible);

            if (visible) {
                registerReceiver();
                /* Update time zone in case it changed while we weren"t visible. */
                mCalendar.setTimeZone(TimeZone.getDefault());
                invalidate();
            } else {
                unregisterReceiver();
            }

            /* Check and trigger whether or not timer should be running (only in active mode). */
            updateTimer();
        }

        private void registerReceiver() {
            if (mRegisteredTimeZoneReceiver) {
                return;
            }
            mRegisteredTimeZoneReceiver = true;
            IntentFilter filter = new IntentFilter(Intent.ACTION_TIMEZONE_CHANGED);
            MyWatchFace.this.registerReceiver(mTimeZoneReceiver, filter);
        }

        private void unregisterReceiver() {
            if (!mRegisteredTimeZoneReceiver) {
                return;
            }
            mRegisteredTimeZoneReceiver = false;
            MyWatchFace.this.unregisterReceiver(mTimeZoneReceiver);
        }

        /**
         * Starts/stops the {@link #mUpdateTimeHandler} timer based on the state of the watch face.
         */
        private void updateTimer() {
            mUpdateTimeHandler.removeMessages(MSG_UPDATE_TIME);
            if (shouldTimerBeRunning()) {
                mUpdateTimeHandler.sendEmptyMessage(MSG_UPDATE_TIME);
            }
        }

        /**
         * Returns whether the {@link #mUpdateTimeHandler} timer should be running. The timer
         * should only run in active mode.
         */
        private boolean shouldTimerBeRunning() {
            return isVisible() && !mAmbient;
        }

        /**
         * Handle updating the time periodically in interactive mode.
         */
        private void handleUpdateTimeMessage() {
            invalidate();
            if (shouldTimerBeRunning()) {
                long timeMs = System.currentTimeMillis();
                long delayMs = INTERACTIVE_UPDATE_RATE_MS
                        - (timeMs % INTERACTIVE_UPDATE_RATE_MS);
                mUpdateTimeHandler.sendEmptyMessageDelayed(MSG_UPDATE_TIME, delayMs);
            }
        }
    }
}