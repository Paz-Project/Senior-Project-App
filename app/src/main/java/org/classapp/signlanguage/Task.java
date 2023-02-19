package org.classapp.signlanguage;

import android.os.AsyncTask;

import com.chaquo.python.PyObject;
import com.chaquo.python.Python;

public class Task extends AsyncTask<Python, Void, Void> {

    private String moduleName;

    public Task(String moduleName) {
        this.moduleName = moduleName;
    }

    @Override
    protected Void doInBackground(Python... pythons) {
        PredictionActivity.predictionModule = pythons[0].getModule(moduleName);
        return null;
    }
}
