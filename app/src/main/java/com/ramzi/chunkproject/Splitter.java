package com.ramzi.chunkproject;

import android.os.Environment;
import com.ramzi.chunkproject.correct.CipherEncryption;

import java.io.*;
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
            CipherEncryption.Encrypt(f.getAbsolutePath());
//            CryptoAES.Encrypt(f.getAbsolutePath(),"1!asertg7*a".toCharArray());
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

    public static void splitTwo(File file)
    {
        try {
//            File file = new File("C:/Documents/Despicable Me 2 - Trailer (HD) - YouTube.mp4");//File read from Source folder to Split.
            if (file.exists()) {

                String videoFileName = file.getName().substring(0, file.getName().lastIndexOf(".")); // Name of the videoFile without extension
                File splitFile = new File(Environment.getExternalStorageDirectory() +
                        File.separator + FILE_SPLIT+File.separator+"olakka"+File.separator+ videoFileName);//Destination folder to save.
                if (!splitFile.exists()) {
                    splitFile.mkdirs();
                    System.out.println("Directory Created -> "+ splitFile.getAbsolutePath());
                }

                int i = 01;// Files count starts from 1
                InputStream inputStream = new FileInputStream(file);
                String videoFile = splitFile.getAbsolutePath() +"/"+ String.format("%02d", i) +"_"+ file.getName();// Location to save the files which are Split from the original file.
                OutputStream outputStream = new FileOutputStream(videoFile);
                System.out.println("File Created Location: "+ videoFile);
                int totalPartsToSplit = 20;// Total files to split.
                int splitSize = inputStream.available() / totalPartsToSplit;
                int streamSize = 0;
                int read = 0;
                while ((read = inputStream.read()) != -1) {

                    if (splitSize == streamSize) {
                        if (i != totalPartsToSplit) {
                            i++;
                            String fileCount = String.format("%02d", i); // output will be 1 is 01, 2 is 02
                            videoFile = splitFile.getAbsolutePath() +"/"+ fileCount +"_"+ file.getName();
                            outputStream = new FileOutputStream(videoFile);
                            System.out.println("File Created Location: "+ videoFile);
                            streamSize = 0;
                        }
                    }
                    outputStream.write(read);
                    streamSize++;
                }
                ///storage/emulated/0/mama.mp4
//                ffmpeg -i INPUT.mp4 -acodec copy -f segment -vcodec copy -reset_timestamps 1 -map 0 OUTPUT%d.mp4
//                ffmpeg -i ORIGINALFILE.mp4 -acodec copy -vcodec copy -ss 0 -t 00:15:00 OUTFILE-1.mp4
//                ffmpeg -i ORIGINALFILE.mp4 -acodec copy -vcodec copy -ss 00:15:00 -t 00:15:00 OUTFILE-2.mp4
//                ffmpeg -i ORIGINALFILE.mp4 -acodec copy -vcodec copy -ss 00:30:00 -t 00:15:00 OUTFILE-3.mp4
                inputStream.close();
                outputStream.close();
                System.out.println("Total files Split ->"+ totalPartsToSplit);
            } else {
                System.err.println(file.getAbsolutePath() +" File Not Found.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
