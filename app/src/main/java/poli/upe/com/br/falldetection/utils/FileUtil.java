package poli.upe.com.br.falldetection.utils;

import android.content.Context;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class FileUtil {

    public static BufferedReader readDataFile(Context context, String filename) {
        BufferedReader inputReader = null;

        try {
            inputReader = new BufferedReader(new InputStreamReader(context.getAssets().open(filename)));
//            inputReader.close();
        } catch (FileNotFoundException ex) {
            System.err.println("File not found: " + filename);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return inputReader;
    }

}
