package poli.upe.com.br.falldetection.classes;


import android.os.AsyncTask;
import android.util.Log;

import poli.upe.com.br.falldetection.classes.interfaces.TrainThreadResult;
import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.classifiers.trees.J48;
import weka.core.Instances;

public class TrainThread extends AsyncTask<Instances, Integer, Integer> {

    private Evaluation evaluation;
    private Classifier classifier;
    private TrainThreadResult mTrainThreadResult;

    public TrainThread(TrainThreadResult mTrainThreadResult) {
        this.mTrainThreadResult = mTrainThreadResult;
    }

    @Override
    protected Integer doInBackground(Instances... instances) {
        if(this.evaluation == null || this.classifier == null) {
            return -1;
        }
        return train(instances[0]);
    }

    @Override
    protected void onPostExecute(Integer aVoid) {
        super.onPostExecute(aVoid);
        if(aVoid < 0) {
            mTrainThreadResult.onPostTraining(true, null, null);
        } else {
            mTrainThreadResult.onPostTraining(false, evaluation, classifier);
        }
    }

    private int train(Instances instances) {
        Log.d("Train", "Começou!");
        if(this.evaluation == null) {
            try {
                if(this.classifier == null) {
                    this.classifier = new J48();
                }
                this.evaluation = new Evaluation(instances);
                this.classifier.buildClassifier(instances);
                this.evaluation.evaluateModel(this.classifier, instances);
                return 0;
            } catch (Exception e) {
                e.printStackTrace();
                return -1;
            }
        }
        return 1;
    }
}
