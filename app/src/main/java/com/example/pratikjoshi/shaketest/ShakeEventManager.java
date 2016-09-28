package com.example.pratikjoshi.shaketest;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by Pratik Joshi on 09/07/2016.
 */
public class ShakeEventManager extends Service implements SensorEventListener {

    private SensorManager sManager;
    private Sensor s;


    private static final int MOV_COUNTS=1;
    private static final int MOV_THRESHOLD=4;
    private static final float ALPHA=0.8F;
    private static final int SHAKE_WINDOW_TIME_INTERVAL=1000;

    //G-force
    private float gravity[]= new float[3];

    private static int counter;
    private long firstMoveTime;
    private ShakeListener listener;

    public ShakeEventManager() {

    }
    @Override
    public void onCreate() {
        Toast.makeText(this, "Service was Created", Toast.LENGTH_LONG).show();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // Perform your long running operations here.
        Toast.makeText(this, "Service Started", Toast.LENGTH_LONG).show();
        return START_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public void setListener(ShakeListener listener){
        this.listener=listener;
    }

    public void init(Context ctx){
        sManager = (SensorManager)  ctx.getSystemService(Context.SENSOR_SERVICE);
        s = sManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        register();
    }

    public void register() {
        sManager.registerListener(this, s, SensorManager.SENSOR_DELAY_NORMAL);
    }


    @Override
    public void onSensorChanged(SensorEvent event) {
        float maxAcc = calcMaxAcceleration(event);
        Log.d("SwA", "Max Acc ["+maxAcc+"]");
        if (maxAcc >= MOV_THRESHOLD) {
            if (counter == 0) {
                counter++;
                firstMoveTime = System.currentTimeMillis();
                Log.d("SwA", "First mov..");
            } else {
                long now = System.currentTimeMillis();
                if ((now - firstMoveTime) < SHAKE_WINDOW_TIME_INTERVAL)
                    counter++;
                else {
                    resetAllData();
                    counter++;
                    return;
                }
                Log.d("SwA", "Mov counter ["+counter+"]");

                if (counter >= MOV_COUNTS)
                    if (listener != null){
                        listener.onShake();
                        /*Intent intent= new Intent(this,MainActivity.class);
                        intent.putExtra("message","Y");
                        startActivity(intent);*/

                    }

            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    public void deregister() {
        sManager.unregisterListener(this);
    }

    private float calcMaxAcceleration(SensorEvent event) {
        gravity[0] = calcGravityForce(event.values[0], 0);
        gravity[1] = calcGravityForce(event.values[1], 1);
        gravity[2] = calcGravityForce(event.values[2], 2);

        float accX = event.values[0] - gravity[0];
        float accY = event.values[1] - gravity[1];
        float accZ = event.values[2] - gravity[2];

        float max1 = Math.max(accX, accY);
        return Math.max(max1, accZ);
    }

    // Low pass filter
    private float calcGravityForce(float currentVal, int index) {
        return  ALPHA * gravity[index] + (1 - ALPHA) * currentVal;
    }

    private void resetAllData() {
        Log.d("SwA", "Reset all data");
        counter = 0;
        firstMoveTime = System.currentTimeMillis();
    }

    private void openMain(){

    }

    public static interface ShakeListener {
        public void onShake();

        void onResume();

        void onPause();
    }



}
