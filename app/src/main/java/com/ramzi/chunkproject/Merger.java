package com.ramzi.chunkproject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.SequenceInputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Properties;

/**
 * Created by oliveboard on 10/1/19.
 *
 * @auther Ramesh M Nair
 */
public class Merger {
    private static final String exted = ".cfg";
    private static final String ext_p = ".part";
    private static final String TAG ="per" ;
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
}
