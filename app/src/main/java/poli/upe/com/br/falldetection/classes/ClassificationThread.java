package poli.upe.com.br.falldetection.classes;

import android.os.AsyncTask;

import java.util.ArrayList;

import poli.upe.com.br.falldetection.classes.interfaces.ClassificationThreadResult;
import weka.classifiers.Classifier;
import weka.core.Instance;
import weka.core.Instances;


public class ClassificationThread extends AsyncTask<Instance, Integer, Double> {

    private Classifier mClassifier;
    private Instances mInstances;
    private ClassificationThreadResult mClassificationThreadResult;

    public ClassificationThread(Classifier classifier, Instances instances, ClassificationThreadResult classificationThreadResult) {
        this.mClassifier = classifier;
        this.mInstances = instances;
        this.mClassificationThreadResult = classificationThreadResult;
    }

    @Override
    protected Double doInBackground(Instance... instances) {
        return classify(instances[0]);
    }

    private Double classify(Instance instance) {
        Double classification = null;
        if(this.mClassifier != null) {
            try {
                instance.setDataset(this.mInstances);
                classification = this.mClassifier.classifyInstance(instance);
//                classification = this.evaluation.evaluateModelOnceAndRecordPrediction(this.classifier,instance);

//                this.mTextVireGyroscope.setText(String.valueOf(v1));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return classification;
    }

    @Override
    protected void onPostExecute(Double aDouble) {
        super.onPostExecute(aDouble);
        mClassificationThreadResult.onPostExecution(aDouble);
    }
}
