package poli.upe.com.br.falldetection;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;

import poli.upe.com.br.falldetection.classes.ClassificationThread;
import poli.upe.com.br.falldetection.classes.TrainThread;
import poli.upe.com.br.falldetection.classes.interfaces.ClassificationThreadResult;
import poli.upe.com.br.falldetection.classes.interfaces.TrainThreadResult;
import poli.upe.com.br.falldetection.utils.FileUtil;
import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.classifiers.evaluation.NominalPrediction;
import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.FastVector;
import weka.core.Instance;
import weka.core.Instances;

public class MainActivity extends AppCompatActivity implements SensorEventListener,
        ClassificationThreadResult, TrainThreadResult {

    private TextView mTextViewAcelerometer;
    private TextView mTextViewGravity;
    private TextView mTextVireGyroscope;

    private SensorManager mSensorManager;

    private ArrayList<Attribute> values;
    private Evaluation evaluation;
    private Classifier classifier;
    private Instances instances;

    private ClassificationThread mClassificationThread;

//    private Thread thread;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mTextViewAcelerometer = (TextView) findViewById(R.id.main_textview_acelerometer);
        mTextViewGravity = (TextView) findViewById(R.id.main_textview_gravity);
        mTextVireGyroscope = (TextView) findViewById(R.id.main_textview_gyroscope);

        BufferedReader bufferedReader = FileUtil.readDataFile(this, "fall2.txt");
        instances = defineInstances(bufferedReader);

        new TrainThread(this).execute(instances);

//        train();
//
//        if(this.evaluation != null) {
//            double v = calculateAccuracy(this.evaluation.predictions());
//            this.mTextViewGravity.setText(String.valueOf(v));
//
//        }
//
//        this.values = new ArrayList<>();
//        this.mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

    }


    private Instances defineInstances(BufferedReader bufferedReader) {
        Instances instances = null;
        try {
            instances = new Instances(bufferedReader);
            instances.setClassIndex(instances.numAttributes() - 1);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return instances;
    }

//    private void train() {
//        Log.d("ClassificationT", "entrou!");
//            if(this.evaluation == null) {
//                try {
//                    if(this.classifier == null) {
//                        this.classifier = new J48();
//                    }
//                    this.evaluation = new Evaluation(instances);
//                    this.classifier.buildClassifier(instances);
//                    this.evaluation.evaluateModel(this.classifier, instances);
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            }
//    }

    @Override
    protected void onResume() {
        super.onResume();
        if(mSensorManager != null) {
//            mSensorManager.registerListener(this, mSensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY),
//                    SensorManager.SENSOR_DELAY_NORMAL);
            mSensorManager.registerListener(this,
                    mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_NORMAL);
//            mSensorManager.registerListener(this, mSensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE),
//                    SensorManager.SENSOR_DELAY_NORMAL);
        }
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
            case Sensor.TYPE_GYROSCOPE:
                gyroscopeSensor(sensorEvent);
                break;
            default:
                break;
        }
    }

    private ArrayList<Attribute> createAttributes() {
        ArrayList<Attribute> attributes = new ArrayList<>();

        for(int i = 0; i < 102; i++) {
            Attribute attribute = new Attribute("x".concat(String.valueOf(i)));
            attributes.add(attribute);
        }

        Attribute classAtt = new Attribute("class");
        attributes.add(classAtt);

        return attributes;
    }

    private void gyroscopeSensor(SensorEvent sensorEvent) {
        float[] values = sensorEvent.values;

        float x = values[0];
        float y = values[1];
        float z = values[2];

        String text = String.format("------Gyroscope------\nX:%s\nY:%s\nZ:%s", String.valueOf(x), String.valueOf(y), String.valueOf(z));
        mTextVireGyroscope.setText(text);
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

        if(this.values != null) {
            if(this.values.size() >= 102) {
                while(this.values.size() != 99) {
                    this.values.remove(0);
                }
            }

            int size = this.values.size();
            Attribute xAtt = new Attribute("x".concat(String.valueOf(size)));
            xAtt.addStringValue(String.valueOf(x));
            size++;
            Attribute yAtt = new Attribute("x".concat(String.valueOf(size)));
            yAtt.addStringValue(String.valueOf(y));
            size++;
            Attribute zAtt = new Attribute("x".concat(String.valueOf(size)));
            zAtt.addStringValue(String.valueOf(z));

            this.values.add(xAtt);
            this.values.add(yAtt);
            this.values.add(zAtt);

            if(this.values.size() == 102) {
                Instance instance = new DenseInstance(this.values.size());
                for(int i = 0; i < this.values.size(); i++) {
                    try {
                        instance.setValue(this.values.get(i), "");
                    } catch (IllegalArgumentException e) {
                        instance.setMissing(i);
                    }
                }

                try {
                    if(this.mClassificationThread == null || this.mClassificationThread.getStatus() == AsyncTask.Status.FINISHED) {
                        if(this.classifier != null && this.instances != null) {
                            this.mClassificationThread = new ClassificationThread(this.classifier, this.instances, this);
                            this.mClassificationThread.execute(instance);
                        }
                    }
//                    if(this.mClassificationThread != null) {
//                        if(this.mClassificationThread.getStatus() != AsyncTask.Status.RUNNING) {
//                            this.mClassificationThread.execute(instance);
//                        }
//                    }
//                    executeThreadOfClassification(instance);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        String text = String.format("------Acelerometer------\nX:%s\nY:%s\nZ:%s", String.valueOf(x), String.valueOf(y), String.valueOf(z));
        mTextViewAcelerometer.setText(text);
    }

//    private void executeThreadOfClassification(final Instance instance) {
//        Runnable runnable = new Runnable() {
//            @Override
//            public void run() {
//                classify(instance);
//            }
//        };
//
//        this.thread = new Thread(runnable);
//        this.thread.run();
//    }

//    private void classify(Instance instance) {
//        if(this.evaluation != null && this.classifier != null) {
//            try {
//                instance.setDataset(this.instances);
//                double v1 = this.classifier.classifyInstance(instance);
////                double v = this.evaluation.evaluateModelOnceAndRecordPrediction(this.classifier,instance);
//
//                this.mTextVireGyroscope.setText(String.valueOf(v1));
//                this.thread.interrupt();
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }
//    }

    public static double calculateAccuracy(FastVector predictions) {
        double correct = 0;

        for (int i = 0; i < predictions.size(); i++) {
            NominalPrediction np = (NominalPrediction) predictions.elementAt(i);
            if (np.predicted() == np.actual()) {
                correct++;
            }
        }

        return 100 * correct / predictions.size();
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {
        //Do something
    }

    @Override
    public void onPostExecution(Double classification) {
        this.mTextVireGyroscope.setText(String.valueOf(classification));
        Log.d("Classification", String.valueOf(classification));
    }

    @Override
    public void onPostTraining(boolean hasErrors, Evaluation evaluation, Classifier classifier) {
        if(!hasErrors) {
            Log.d("Train", "Terminou!");
            this.evaluation = evaluation;
            this.classifier = classifier;
            if(this.evaluation != null) {
                double v = calculateAccuracy(this.evaluation.predictions());
                this.mTextViewGravity.setText(String.valueOf(v));

            }
            this.values = new ArrayList<>();
            this.mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        } else {

        }

    }
}
