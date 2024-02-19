package com.sherdle.webtoapp.service.sensor;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Handler;
import android.os.Looper;

public class BearingSensorManager {

    private final Context context;
    private SensorManager sensorManager;
    private Sensor accelerometer;
    private Sensor magnetometer;

    private final float[] accelerometerData = new float[3];
    private final float[] magnetometerData = new float[3];
    private final float[] rotationMatrix = new float[9];
    private final float[] orientationValues = new float[3];

    private OnBearingChangeListener bearingChangeListener;
    private final Handler handler;

    public BearingSensorManager(Context context) {
        this.context = context;
        initSensors();
        handler = new Handler(Looper.getMainLooper());
    }

    private void initSensors() {
        sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        if (sensorManager != null) {
            accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
            magnetometer = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        }
    }

    public void start() {
        if (sensorManager != null) {
            sensorManager.registerListener(sensorEventListener, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
            sensorManager.registerListener(sensorEventListener, magnetometer, SensorManager.SENSOR_DELAY_NORMAL);
        }
    }

    public void stop() {
        if (sensorManager != null) {
            sensorManager.unregisterListener(sensorEventListener);
        }
    }

    public void setOnBearingChangeListener(OnBearingChangeListener listener) {
        this.bearingChangeListener = listener;
    }

    private final SensorEventListener sensorEventListener = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent event) {
            if (event.sensor == accelerometer) {
                System.arraycopy(event.values, 0, accelerometerData, 0, event.values.length);
            } else if (event.sensor == magnetometer) {
                System.arraycopy(event.values, 0, magnetometerData, 0, event.values.length);
            }

            updateOrientation();
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {
            // Do nothing for now
        }
    };

    private void updateOrientation() {
        if (SensorManager.getRotationMatrix(rotationMatrix, null, accelerometerData, magnetometerData)) {
            SensorManager.getOrientation(rotationMatrix, orientationValues);

            float azimuthInRadians = orientationValues[0];
            final float azimuthInDegrees = (float) Math.toDegrees(azimuthInRadians);

            if (bearingChangeListener != null) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        bearingChangeListener.onBearingChanged(azimuthInDegrees);
                    }
                });
            }
        }
    }

    public interface OnBearingChangeListener {
        void onBearingChanged(float bearing);
    }
}

