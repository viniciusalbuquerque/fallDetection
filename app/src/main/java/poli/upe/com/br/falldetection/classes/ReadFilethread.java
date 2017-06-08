package poli.upe.com.br.falldetection.classes;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;

import poli.upe.com.br.falldetection.classes.interfaces.FileReaderThreadResult;
import poli.upe.com.br.falldetection.utils.FileUtil;

public class ReadFilethread extends AsyncTask<String, Integer, BufferedReader> {

    private Context mContext;
    private FileReaderThreadResult mFileReaderThreadResult;

    public ReadFilethread(Context mContext, FileReaderThreadResult fileReaderThreadResult) {
        this.mContext = mContext;
        mFileReaderThreadResult = fileReaderThreadResult;
    }

    @Override
    protected BufferedReader doInBackground(String... strings) {
        Log.d("FileReader", "Começou!");
        String filename = strings[0];
        return FileUtil.readDataFile(mContext, filename);
    }

    @Override
    protected void onPostExecute(BufferedReader bufferedReader) {
        Log.d("FileReader", "Terminou!");
        super.onPostExecute(bufferedReader);
        mFileReaderThreadResult.readerResult(bufferedReader);
    }
}
