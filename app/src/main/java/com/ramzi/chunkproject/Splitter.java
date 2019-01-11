package com.ramzi.chunkproject;

import android.os.Environment;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * Created by oliveboard on 10/1/19.
 *
 * @auther Ramesh M Nair
 */
public class Splitter {
    private static final String exted = ".cfg";
    private static final String ext_p = ".part";
    private static final String TAG ="per" ;
    private static final String FILE_SPLIT ="ChunkProject";
    static void splite(File file, int size) throws IOException {
        if (!file.exists()) {
            throw new IOException("File not found!");
        }
        File splite_dir = new File(Environment.getExternalStorageDirectory() +
                File.separator + FILE_SPLIT, file.getName()+".partfile");
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
            CryptoAES.Encrypt(f.getAbsolutePath(),"1!asertg7*a".toCharArray());
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
        try {
            for (File f2 : splite_dir.listFiles()) {
                if (f2.getName().endsWith(".part")) {
                    f2.delete(); // may fail mysteriously - returns boolean you may want to check
                }
            }
        }
        catch (Exception e)
        {

        }
    }
}
