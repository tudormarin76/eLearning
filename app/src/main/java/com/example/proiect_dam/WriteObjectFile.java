package com.example.proiect_dam;

import android.os.AsyncTask;
import android.os.Environment;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;


public class WriteObjectFile extends AsyncTask<JSONObject, Void, Void> {

    public AsyncResponse delegate = null;

    public WriteObjectFile(AsyncResponse delegate){
        this.delegate = delegate;
    }

    @Override
    protected Void doInBackground(JSONObject... jsons) {


     File root = new File( Environment.getExternalStorageDirectory() + File.separator + "/ProiectDAM/App_cache");
     File file = new File(root,"data.json");

        if (!file.exists()) {
            try {
                root.mkdirs();
                file.createNewFile();
            } catch (IOException ioe) {
                ioe.printStackTrace();
            }
        }
        FileOutputStream fileOutputStream = null;
        try {
            fileOutputStream = new FileOutputStream(file);
            OutputStreamWriter writer = new OutputStreamWriter(fileOutputStream);
            writer.append(jsons[0].toString());
            writer.close();
            fileOutputStream.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


        return null;
    }

    public void WriteJSON(JSONObject json){
        execute(json);
    }

    @Override
    public void onPostExecute(Void aVoid) {
        delegate.processFinish(null);
    }
}