package com.ramzi.chunkproject;

import java.io.File;
import java.io.FileFilter;

/**
 * Created by oliveboard on 10/1/19.
 *
 * @auther Ramesh M Nair
 */
public class FilteExted implements FileFilter {
    String ext;

    public FilteExted(String ext) {
        this.ext = ext;
    }

    @Override
    public boolean accept(File dir) {
        return dir.getName().endsWith(ext);
    }
}