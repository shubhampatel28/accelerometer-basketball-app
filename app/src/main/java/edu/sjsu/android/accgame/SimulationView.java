package edu.sjsu.android.accgame;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Point;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.view.Display;
import android.view.Surface;
import android.view.View;
import android.view.WindowManager;


public class SimulationView extends View implements SensorEventListener {


    private SensorManager sensorManager;
    private Sensor sensor;
    private Display mDisplay;

    private Bitmap mField, mBasket, mBitmap;
    private static final int BALL_SIZE = 64;
    private static final int BASKET_SIZE = 80;

    private float mXOrigin, mYOrigin, mHorizontalBound, mVerticalBound;
    private float mSensorX, mSensorY, mSensorZ;
    private long mSensorTimeStamp;
    private Particle mBall;
    int height;
    int width;
    Point p;


    //SimulationView constructor
    public SimulationView(Context context) {
        super(context);
        mBall = new Particle();

        Bitmap ball = BitmapFactory.decodeResource(getResources(), R.drawable.ball);
        mBitmap = Bitmap.createScaledBitmap(ball, BALL_SIZE, BALL_SIZE, true);
        Bitmap basket = BitmapFactory.decodeResource(getResources(), R.drawable.basket);
        mBasket = Bitmap.createScaledBitmap(basket, BASKET_SIZE, BASKET_SIZE, true);
        BitmapFactory.Options opts = new BitmapFactory.Options();
        opts.inPreferredConfig = Bitmap.Config.RGB_565;
        mField = BitmapFactory.decodeResource(getResources(), R.drawable.field, opts);
        WindowManager mWindowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        mDisplay = mWindowManager.getDefaultDisplay();
        sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        sensorManager.registerListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_NORMAL);

        p = new Point();
        mDisplay.getSize(p);
        width = p.x;
        height = p.y;
        mField = Bitmap.createScaledBitmap(mField, width, height, true);

    }


    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        float multiplier = 0.5f;
        mXOrigin = w * multiplier;
        mYOrigin = h * multiplier;
        mHorizontalBound = (w - BALL_SIZE) * multiplier;
        mVerticalBound = (h - BALL_SIZE) * multiplier;
        Bitmap ball = BitmapFactory.decodeResource(getResources(), R.drawable.ball);
        mBitmap = Bitmap.createScaledBitmap(ball, BALL_SIZE, BALL_SIZE, true);
        Bitmap basket = BitmapFactory.decodeResource(getResources(), R.drawable.basket);
        mBasket = Bitmap.createScaledBitmap(basket, BASKET_SIZE, BASKET_SIZE, true);
        BitmapFactory.Options opts = new BitmapFactory.Options();
        opts.inPreferredConfig = Bitmap.Config.RGB_565;
        mField = BitmapFactory.decodeResource(getResources(), R.drawable.field, opts);
        mField = Bitmap.createScaledBitmap(mField, width, height, true);

        WindowManager mWindowManager = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
        mDisplay = mWindowManager.getDefaultDisplay();

    }


    public void startSimulation() {
        sensorManager.registerListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                SensorManager.SENSOR_DELAY_NORMAL);
    }

    public void stopSimulation() {
        sensorManager.unregisterListener(this);

    }


    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        if (sensorEvent.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            if (mDisplay.getRotation() == Surface.ROTATION_0) {
                mSensorX = sensorEvent.values[0];
                mSensorY = sensorEvent.values[1];
            } else if (mDisplay.getRotation() == Surface.ROTATION_90) {
                mSensorX = -sensorEvent.values[1];
                mSensorY = sensorEvent.values[0];
            } else if (mDisplay.getRotation() == Surface.ROTATION_270) {
                mSensorX = sensorEvent.values[1];
                mSensorY = -sensorEvent.values[0];
            }
            mSensorZ = sensorEvent.values[2];
            mSensorTimeStamp = sensorEvent.timestamp;
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawBitmap(mField, 0, 0, null);
        canvas.drawBitmap(mBasket, mXOrigin - BASKET_SIZE / 2, mYOrigin - BASKET_SIZE / 2, null);
        mBall.updatePosition(mSensorX, mSensorY, mSensorZ, mSensorTimeStamp);
        mBall.resolveCollisionWithBounds(mHorizontalBound, mVerticalBound);
        canvas.drawBitmap(mBitmap, (mXOrigin - BALL_SIZE / 2) + mBall.mPosX, (mYOrigin - BALL_SIZE / 2) - mBall.mPosY, null);
        invalidate();
    }
}
