package com.merryjs.PhotoViewer;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.content.FileProvider;
import com.stfalcon.frescoimageviewer.ImageViewer;

import java.io.File;
import java.io.FileOutputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MerryPhotoOverlay extends RelativeLayout {
    private TextView tvTitle;
    private TextView tvTitlePager;

    private TextView tvDescription;
    private TextView tvShare;
    private TextView tvClose;
    private ImageViewer imageViewer;
    private String sharingText;
    private Context context;
    public static Context mContext;
    public String authToken;

    public void setImageViewer(ImageViewer imageViewer){
        this.imageViewer = imageViewer;
    }
    public MerryPhotoOverlay(Context context) {
        super(context);
        this.context = context;
        mContext = context;
        init();
    }

    public MerryPhotoOverlay(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public MerryPhotoOverlay(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public void setAuthToken(String authToken) {
        this.authToken = authToken;
    }

    public void setHideShareButton(Boolean hideShareButton) {
        tvShare.setVisibility(hideShareButton ? View.GONE : View.VISIBLE);
    }
    public void setHideCloseButton(Boolean hideCloseButton) {
        tvClose.setVisibility(hideCloseButton ? View.GONE : View.VISIBLE);
    }

    public void setHideTitle(Boolean hideTitle) {
        tvTitlePager.setVisibility(hideTitle? View.GONE : View.VISIBLE);
    }

    public void setPagerText(String text) {
        tvTitlePager.setText(text);
    }

    public void setPagerTextColor(String color) {
        tvTitlePager.setTextColor(Color.parseColor(color));
    }

    public void setDescription(String description) {
        tvDescription.setText(description);
    }

    public void setDescriptionTextColor(int color) {
        tvDescription.setTextColor(color);
    }

    public void setShareText(String text) {
        tvShare.setText(text);
    }

    public void setShareContext(String text) {
        this.sharingText = text;
    }

    public void setShareTextColor(String color) {
        tvShare.setTextColor(Color.parseColor(color));
    }

    public void setTitleTextColor(int color) {
        tvTitle.setTextColor(color);
    }

    public void setTitleText(String text) {
        tvTitle.setText(text);
    }

    private void sendShareIntent() {
        new BitmapAsyncTask().execute(sharingText, authToken);
    }

    static public void showErrorToast() {
        Toast.makeText(mContext, R.string.shareError, Toast.LENGTH_LONG).show();
    }

    static public void shareImage(Bitmap bitmap, Uri shareUri) {
        String fileName = shareUri.getLastPathSegment();

        Pattern pattern = Pattern.compile(".(?:gif|jpe?g|tiff?|png|webp|bmp|heic)$");
        Matcher matcher = pattern.matcher(fileName);

        boolean isFileNameWithExt = matcher.find();

        if (!isFileNameWithExt) {
            fileName += ".jpeg";
        }

        try {
            File cachePath = new File(mContext.getCacheDir(), "images");
            cachePath.mkdirs();
            FileOutputStream stream = new FileOutputStream(cachePath + "/" + fileName); // overwrites this image every time
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
            stream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        File imagePath = new File(mContext.getCacheDir(), "images");
        File newFile = new File(imagePath, fileName);
        Uri contentUri = FileProvider.getUriForFile(mContext, mContext.getPackageName() + ".provider", newFile);

        if (contentUri != null) {
            Intent shareIntent = new Intent();
            shareIntent.setAction(Intent.ACTION_SEND);
            shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            shareIntent.setDataAndType(contentUri, mContext.getContentResolver().getType(contentUri));
            shareIntent.putExtra(Intent.EXTRA_STREAM, contentUri);
            shareIntent.setType("image/*");
            mContext.startActivity(Intent.createChooser(shareIntent, "Choose an app"));
        }
    }


    private void init() {
        View view = inflate(getContext(), R.layout.photo_viewer_overlay, this);

        tvTitlePager = (TextView) view.findViewById(R.id.tvTitlePager);
        tvTitle = (TextView) view.findViewById(R.id.tvTitle);
        tvDescription = (TextView) view.findViewById(R.id.tvDescription);

        tvShare = (TextView) view.findViewById(R.id.btnShare);
        tvShare.setOnClickListener(new SingleClickListener() {
            @Override
            public void onClicked(View v) {
                sendShareIntent();
            }
        });

        tvClose = (TextView) view.findViewById(R.id.btnClose);
        tvClose.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
               imageViewer.onDismiss();
            }
        });
    }
}
