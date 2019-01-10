package com.ramzi.chunkproject;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import com.obsez.android.lib.filechooser.ChooserDialog;



import java.io.*;
import java.util.*;

/**
 * Created by oliveboard on 10/1/19.
 *
 * @auther Ramesh M Nair
 */
public class MainActivity extends AppCompatActivity implements TaskCallBack {
    private static final String exted = ".cfg";
    private static final String ext_p = ".part";
    private static final String TAG ="per" ;
    private static final String FILE_SPLIT ="ChunkProject";
    int values=-1;
    TextView splitText,mergeText;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        splitText=(TextView)findViewById(R.id.splittxt);
        mergeText=(TextView)findViewById(R.id.mergetxt);

        isStoragePermissionGranted();
    }
   /* public static List<File> splitFile(File f) throws IOException {
        int partCounter = 1;
        List<File> result = new ArrayList<>();
        int sizeOfFiles = 1024 * 1024;// 1MB
        byte[] buffer = new byte[sizeOfFiles]; // create a buffer of bytes sized as the one chunk size

        BufferedInputStream bis = new BufferedInputStream(new FileInputStream(f));
        String name = f.getName();

        int tmp = 0;
        while ((tmp = bis.read(buffer)) > 0) {
            File newFile = new File(f.getParent(), name + "." + String.format("%03d", partCounter++)); // naming files as <inputFileName>.001, <inputFileName>.002, ...
            FileOutputStream out = new FileOutputStream(newFile);
            out.write(buffer, 0, tmp);//tmp is chunk size. Need it for the last chunk, which could be less then 1 mb.
            result.add(newFile);
        }
        return result;
    }



    public static void mergeFiles(List<File> files, File into)
            throws IOException {
        BufferedOutputStream mergingStream = new BufferedOutputStream(new FileOutputStream(into))
        for (File f : files) {
            InputStream is = new FileInputStream(f);
            Files.copy(is, mergingStream);
            is.close();
        }
        mergingStream.close();
    }*/






    public  boolean isStoragePermissionGranted() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                Log.v(TAG,"Permission is granted");
                makeTheDir();
                return true;
            } else {

                Log.v(TAG,"Permission is revoked");
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                return false;
            }
        }
        else { //permission is automatically granted on sdk<23 upon installation
            Log.v(TAG,"Permission is granted");
            makeTheDir();
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(grantResults[0]== PackageManager.PERMISSION_GRANTED){
            Log.v(TAG,"Permission: "+permissions[0]+ "was "+grantResults[0]);
            makeTheDir();
            //resume tasks needing this permission
        }
        else
        {
            finish();
        }

    }



    public void makeTheDir()
    {
        File spliteFolder = new File(Environment.getExternalStorageDirectory() +
                File.separator + FILE_SPLIT);
        if(!spliteFolder.exists())
        {
            spliteFolder.mkdirs();
        }
    }

    public void split(View view) {

        File f=new File(splitText.getText().toString());
        if(f.exists()) {
            showProgressDialog("splitting wait.....");

            new SplirMergeTask(0, this, f).execute();
        }
        else
        {
            Toast.makeText(getApplicationContext(),"select files dude",1).show();
        }

    }

    public void merge(View view) {

        File f=new File(mergeText.getText().toString());
        if(f.exists()) {
            showProgressDialog("Merging wait.....");

            new SplirMergeTask(1, this, f).execute();
        }
        else
        {
            Toast.makeText(getApplicationContext(),"select files dude",1).show();

        }

    }

    public void selectSplite(View view) {
        selectFile(0);
    }

    public void selectMerge(View view) {
        selectFile(1);
    }


    public void selectFile(final int value)
    {
//        if (checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
//                == PackageManager.PERMISSION_GRANTED) {
            this.values = value;
        if (values == 0) {
            new ChooserDialog().with(this)
                    .withStartFile(Environment.getExternalStorageDirectory().toString())
                    .withChosenListener(new ChooserDialog.Result() {
                        @Override
                        public void onChoosePath(String path, File pathFile) {
                            splitText.setText("" + path);

                            Toast.makeText(MainActivity.this, "FILE: " + path, Toast.LENGTH_SHORT).show();
                        }
                    })
                    .build()
                    .show();
        }
        else
        {
            new ChooserDialog().with(this)
                    .withFilter(true, false)
                    .withStartFile(Environment.getExternalStorageDirectory() +
                            File.separator + FILE_SPLIT)
                    .withChosenListener(new ChooserDialog.Result() {
                        @Override
                        public void onChoosePath(String path, File pathFile) {
                            mergeText.setText(path);
//                            Toast.makeText(MainActivity.this, "FOLDER: " + path, Toast.LENGTH_SHORT).show();
                        }
                    })
                    .build()
                    .show();
        }
//        }
    }


    @Override
    public void taskCallback(boolean isComplete, int type) {
        hideProgressDialog();

        if(isComplete)
        {
            if(type==0)
            {
                Toast.makeText(getApplicationContext(),"Successfully splitted",1).show();
            }
            else
            {
                Toast.makeText(getApplicationContext(),"Successfully merged",1).show();

            }
        }
        else
        {
            Toast.makeText(getApplicationContext(),"Operation failed",1).show();

        }
    }



    protected ProgressDialog progressDialog;


    protected void showProgressDialog(String message) {


        if (progressDialog == null || !progressDialog.isShowing()) {
            progressDialog = new ProgressDialog(this);
            progressDialog.setMessage(message);
            progressDialog.setCanceledOnTouchOutside(false);
            if (!isFinishing()) {
                progressDialog.show();
            }
        }
    }

    protected void hideProgressDialog() {
        if (progressDialog != null && progressDialog.isShowing()) {
            if (!isFinishing()) {
                progressDialog.dismiss();
            }
        }
    }
}




