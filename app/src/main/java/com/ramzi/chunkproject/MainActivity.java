package com.ramzi.chunkproject;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import java.io.*;
import java.nio.file.Files;
import java.util.*;

/**
 * Created by oliveboard on 10/1/19.
 *
 * @auther Ramesh M Nair
 */
public class MainActivity extends AppCompatActivity {
    private static final String exted = ".cfg";
    private static final String ext_p = ".part";
    private static final String TAG ="per" ;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                Log.v(TAG, "Permission is granted");
                //File write logic here
            }
            else
            {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
            }
        }
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

    static void splite(File file,int size) throws IOException {
        if (!file.exists()) {
            throw new IOException("File not found!");
        }
        File splite_dir = new File(Environment.getExternalStorageDirectory() +
                File.separator + "TollCulator", file.getName()+".partfile");
        if (!splite_dir.exists())
            splite_dir.mkdir();
        System.out.println("Please Wait...");
        byte buf[] = new byte[size*1024*1024];
        FileInputStream fis = new FileInputStream(file);
        FileOutputStream fos = null;
        File f = null;
        int count = 0;
        int len;
        while ((len = fis.read(buf)) != -1) {
            f = new File(splite_dir, (count++) + ext_p);
            fos = new FileOutputStream(f);
            fos.write(buf, 0, len);
            fos.close();
        }
        Properties prop = new Properties();
        prop.setProperty("filename", file.getName());
        prop.setProperty("part_count", count + "");
        File prop_file = new File(splite_dir.getAbsoluteFile(),file.getName() + ".cfg");
        fos = new FileOutputStream(prop_file);
        prop.store(fos, file.getName());
        fos.close();
        fis.close();
        System.out.println("Storage path:" + splite_dir.getAbsoluteFile());
        System.out.println("splite success!");
    }

    static void merger(File dir) throws Exception {
        if (!(dir.exists() && dir.isDirectory()))
            throw new RuntimeException("Dir not exists or dir not a Directory");
        File[] files = dir.listFiles(new FilteExted(exted));
        if (files.length == 0)
            throw new RuntimeException(exted + "File not found!");
        Properties prop = new Properties();
        FileInputStream fis = new FileInputStream(files[0]);
        prop.load(fis);
        String filename = prop.getProperty("filename");
        int filecount = Integer.parseInt(prop.getProperty("part_count"));
        files = dir.listFiles(new FilteExted(ext_p));
        if (files.length != filecount)
            throw new RuntimeException("part file is missing");
        System.out.println("Please Wait...");
        ArrayList<FileInputStream> filelist = new ArrayList<FileInputStream>();
        for (int i = 0; i < filecount; i++) {
            filelist.add(new FileInputStream(new File(dir, i + ext_p)));
        }
        Enumeration<FileInputStream> em = Collections.enumeration(filelist);
        SequenceInputStream sis = new SequenceInputStream(em);
        File targ_file = new File(dir, filename);
        FileOutputStream fos = new FileOutputStream(targ_file);
        byte buf[] = new byte[1024];
        int len = 0;
        while ((len = sis.read(buf)) != -1) {
            fos.write(buf);
        }
        fos.close();
        sis.close();
        System.out.println("merger succes!");
    }


    public  boolean isStoragePermissionGranted() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                Log.v(TAG,"Permission is granted");
                return true;
            } else {

                Log.v(TAG,"Permission is revoked");
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                return false;
            }
        }
        else { //permission is automatically granted on sdk<23 upon installation
            Log.v(TAG,"Permission is granted");
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(grantResults[0]== PackageManager.PERMISSION_GRANTED){
            Log.v(TAG,"Permission: "+permissions[0]+ "was "+grantResults[0]);
            //resume tasks needing this permission
        }
    }

    public void split(View view) {
        File f=new File(Environment.getExternalStorageDirectory() +
                File.separator + "janu", "pra.mp4");
        File splite_dir = new File(Environment.getExternalStorageDirectory() +
                File.separator + "TollCulator", "pra.mp4.partfile");
        try {
//            splite(f,1);
            merger(splite_dir);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}


    class FilteExted implements FileFilter {
        String ext;

        public FilteExted(String ext) {
            this.ext = ext;
        }

        @Override
        public boolean accept(File dir) {
            return dir.getName().endsWith(ext);
        }
    }

