package poli.upe.com.br.falldetection.classes;

import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;

import poli.upe.com.br.falldetection.classes.interfaces.DefineInstancesResult;
import weka.core.Instances;

public class DefiningInstancesThread extends AsyncTask<BufferedReader, Integer, Instances> {

    private DefineInstancesResult mDefineInstancesResult;

    public DefiningInstancesThread(DefineInstancesResult mDefineInstancesResult) {
        this.mDefineInstancesResult = mDefineInstancesResult;
    }

    @Override
    protected Instances doInBackground(BufferedReader... bufferedReaders) {
        return defineInstances(bufferedReaders[0]);
    }

    private Instances defineInstances(BufferedReader bufferedReader) {
        Log.d("Instances", "Começou!");
        Instances instances = null;
        try {
            instances = new Instances(bufferedReader);
            instances.setClassIndex(instances.numAttributes() - 1);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return instances;
    }

    @Override
    protected void onPostExecute(Instances instances) {
        super.onPostExecute(instances);
        Log.d("Instances", "Terminou!");
        mDefineInstancesResult.definingInstancesResult(instances);
    }
}
