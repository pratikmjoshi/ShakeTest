package com.example.pratikjoshi.shaketest;

import android.app.Service;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.widget.Toast;

/**
 * Created by Pratik Joshi on 07/08/2016.
 */
public class ShakeBackgroundService extends Service implements SensorEventListener {

    SensorManager sensorMgr;
    Sensor sensor;
    private long lastUpdate=0;
    private static final int SHAKE_THRESHOLD = 20;
    float lastX,lastY,lastZ;

    public int onStartCommand(Intent intent, int flags, int startId){
        sensorMgr = (SensorManager) getSystemService(SENSOR_SERVICE);
        sensor=sensorMgr.getDefaultSensor(sensor.TYPE_ACCELEROMETER);
        sensorMgr.registerListener(this, sensor, SensorManager.SENSOR_DELAY_NORMAL);



        return START_STICKY;

    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    @Override
    public void onSensorChanged(SensorEvent event) {
        Sensor sensor= event.sensor;
        if (sensor.getType() == Sensor.TYPE_ACCELEROMETER ) {
            long curTime = System.currentTimeMillis();
            // only allow one update every 100ms.
            if ((curTime - lastUpdate) > 100) {
                long diffTime = (curTime - lastUpdate);
                lastUpdate = curTime;

                float x = event.values[0];
                float y = event.values[1];
                float z = event.values[2];

                float speed = Math.abs((x+y+z - lastX - lastY - lastZ) / diffTime * 10000);

                if (speed > SHAKE_THRESHOLD) {
                    Toast.makeText(this,"ok",Toast.LENGTH_SHORT).show();
                    Intent intent= new Intent(getApplicationContext(),MainActivity.class);
                    intent.putExtra("message","Y");
                    startActivity(intent);
                }
                lastX = x;
                lastY = y;
                lastZ = z;
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
