package com.merryjs.PhotoViewer;

import android.os.SystemClock;
import android.view.View;

public abstract class SingleClickListener implements View.OnClickListener {
    private static final long THRESHOLD_MILLIS = 1000L;
    private long lastClickMillis;

    @Override public void onClick(View v) {
        long now = SystemClock.elapsedRealtime();
        if (now - lastClickMillis > THRESHOLD_MILLIS) {
            onClicked(v);
        }
        lastClickMillis = now;
    }

    public abstract void onClicked(View v);
}
