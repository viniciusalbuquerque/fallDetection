package poli.upe.com.br.falldetection.classes.interfaces;

import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;

public interface TrainThreadResult {

    public void onPostTraining(boolean hasErrors, Evaluation evaluation, Classifier classifier);
}
