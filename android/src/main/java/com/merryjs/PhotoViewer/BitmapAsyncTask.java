package com.merryjs.PhotoViewer;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class BitmapAsyncTask extends AsyncTask<String, Void, Bitmap> {
    private String stringUrl;
    private String authToken;

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected Bitmap doInBackground(String... strings) {
        stringUrl = strings[0];
        authToken = strings[1];

        try {
            URL url = new URL(strings[0]);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            if (authToken != null && stringUrl.contains("ic.pics.livejournal")) {
                String bearerAuth = "Bearer " + authToken;
                connection.setRequestProperty("Authorization", bearerAuth);
            }

            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap myBitmap = BitmapFactory.decodeStream(input);
            return myBitmap;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    protected void onPostExecute(Bitmap bitmap) {
        super.onPostExecute(bitmap);

        if(bitmap != null) {
            MerryPhotoOverlay.shareImage(bitmap, Uri.parse(stringUrl));
        } else {
            MerryPhotoOverlay.showErrorToast();
        }
    }
}
