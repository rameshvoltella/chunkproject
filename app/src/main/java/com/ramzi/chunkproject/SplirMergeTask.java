package com.ramzi.chunkproject;

import android.os.AsyncTask;

import java.io.File;
import java.io.IOException;

/**
 * Created by oliveboard on 10/1/19.
 *
 * @auther Ramesh M Nair
 */
public class SplirMergeTask extends AsyncTask<Void, Void, Void> {
    int task = -1;
    TaskCallBack taskCallBack;
    boolean isSuccess = true;
    File f;

    public SplirMergeTask(int task, TaskCallBack taskCallBack,File f) {
        this.task = task;
        this.taskCallBack = taskCallBack;
        this.f=f;
    }

    @Override
    protected Void doInBackground(Void... voids) {
        if (task == 0) {
            try {
               Splitter.splite(f,100);
            } catch (IOException e) {
                e.printStackTrace();
                isSuccess=false;
            }


        } else if (task == 1) {
            try {
                Merger.merger(f);
            } catch (Exception e) {
                e.printStackTrace();
                isSuccess=false;
            }

        }
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);

        taskCallBack.taskCallback(isSuccess,task);
    }
}
