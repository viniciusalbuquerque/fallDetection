package poli.upe.com.br.falldetection;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.hardware.TriggerEvent;
import android.hardware.TriggerEventListener;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import org.opencv.core.Mat;
import org.opencv.ml.ANN_MLP;
import org.opencv.ml.SVM;
import org.opencv.ml.TrainData;

import java.util.EventListener;

public class MainActivity extends AppCompatActivity implements SensorEventListener {

    private TextView mTextViewAcelerometer;
    private TextView mTextViewGravity;

    private SensorManager mSensorManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mTextViewAcelerometer = (TextView) findViewById(R.id.main_textview_acelerometer);
        mTextViewGravity = (TextView) findViewById(R.id.main_textview_gravity);

        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

//        Mat mat = new Mat();
//        ANN_MLP ann_mlp = ANN_MLP.create();
//        ann_mlp.setActivationFunction(ANN_MLP.SIGMOID_SYM);
//
//        Mat samples = new Mat();
//        samples.
//
//        TrainData trainData = TrainData.create();
//
//        ann_mlp.train();

    }

    @Override
    protected void onResume() {
        super.onResume();
        mSensorManager.registerListener(this, mSensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY),
                SensorManager.SENSOR_DELAY_NORMAL);
        mSensorManager.registerListener(this,
                mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        switch (sensorEvent.sensor.getType()) {
            case Sensor.TYPE_ACCELEROMETER:
                accelerometerSensor(sensorEvent);
                break;
            case Sensor.TYPE_GRAVITY:
                gravitySensor(sensorEvent);
                break;
            default:
                break;
        }
    }

    private void gravitySensor(SensorEvent sensorEvent) {
        float[] values = sensorEvent.values;

        float x = values[0];
        float y = values[1];
        float z = values[2];

        String text = String.format("------Gravity------\nX:%s\nY:%s\nZ:%s", String.valueOf(x), String.valueOf(y), String.valueOf(z));
        mTextViewGravity.setText(text);

    }

    private void accelerometerSensor(SensorEvent sensorEvent) {
        float[] values = sensorEvent.values;

        float x = values[0];
        float y = values[1];
        float z = values[2];

        String text = String.format("------Acelerometer------\nX:%s\nY:%s\nZ:%s", String.valueOf(x), String.valueOf(y), String.valueOf(z));
        mTextViewAcelerometer.setText(text);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {
        //Do something
    }
}
