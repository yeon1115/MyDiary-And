package com.google.firebase.quickstart.database;

import android.os.ResultReceiver;

public class ActivityHelper extends ResultReceiver {
    public interface ActivityResultListener {
        void onActivityResult(String chosenPhoto);
    }

    public ActivityHelper(){
        super(null);
    }

    private ActivityResultListener mActivityResultListener;

    public void setActivityResultListener(ActivityResultListener mActivityResultListener) {
        this.mActivityResultListener = mActivityResultListener;
    }

    public ActivityResultListener getActivityResultListener() {
        return mActivityResultListener;
    }
}
