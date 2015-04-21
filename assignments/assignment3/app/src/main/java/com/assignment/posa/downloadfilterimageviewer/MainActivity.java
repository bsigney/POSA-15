package com.assignment.posa.downloadfilterimageviewer;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.webkit.URLUtil;
import android.widget.EditText;

import com.assignment.posa.bsigney.downloadfilterimageviewer.R;

import java.io.File;


public class MainActivity extends LifecycleLoggingActivity {

    /**
     * Debugging tag used by the Android logger.
     */
    private final String TAG = getClass().getSimpleName();

    /**
     * A value that uniquely identifies the request to download an image.
     */
    private static final int DOWNLOAD_IMAGE_REQUEST = 1;

    /**
     * A value that uniquely identifies the request to filter an image
     */
    private static final int FILTER_IMAGE_REQUEST = 2;

    /**
     * EditText field for entering the desired URL to an image.
     */
    private EditText mUrlEditText;

    /**
     * URL for the image that's downloaded by default if the user
     * doesn't specify otherwise.
     */
    private Uri mDefaultUrl = Uri.parse("http://www.dre.vanderbilt.edu/~schmidt/robot.png");

    /**
     * Flag to indicate if download and filter image process is in progress
     */
    private Boolean mProcessInProgress = false;

    private static final String IN_PROGRESS_KEY = "inProgressFlag";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mUrlEditText = (EditText) findViewById(R.id.url);

        if (savedInstanceState != null) {
            Log.d(TAG, "Restoring saved instance state");
            mProcessInProgress = savedInstanceState.getBoolean(IN_PROGRESS_KEY);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putBoolean(IN_PROGRESS_KEY, mProcessInProgress);
    }


    public void downloadImage(View view) {
        hideKeyboard(this, mUrlEditText.getWindowToken());

        startDownloadActivity(getUrl());
    }

    private void startDownloadActivity(Uri url) {
        if (url != null) {
            if (mProcessInProgress) {
                Utils.showToast(this, "Already dowloading image");
            } else if (!URLUtil.isValidUrl(url.toString())) {
                Utils.showToast(this, "Invalid URL");
            } else {
                mProcessInProgress = true;

                Log.d(TAG, "Downloading image started");

                Intent intent = createDownloadImageIntent(url);
                startActivityForResult(intent, DOWNLOAD_IMAGE_REQUEST);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(TAG, "onActivityResult: resultCode: " + resultCode + "; requestCode: " + requestCode);
        if (resultCode == RESULT_OK) {

            if (requestCode == DOWNLOAD_IMAGE_REQUEST) {
                Intent intent = createFilterImageIntent(data.getData());

                Log.d(TAG, "Download Image completed.  Filtering image commence");
                startActivityForResult(intent, FILTER_IMAGE_REQUEST);
            } else {
                Intent intent = createGalleryIntent(data.getDataString());

                Log.d(TAG, "Filter operation completed. Gallery view started.");
                startActivity(intent);
            }

        } else if (resultCode == RESULT_CANCELED) {
            Log.d(TAG, "Image process cancelled: " + requestCode );
            String errorMsg = (requestCode == DOWNLOAD_IMAGE_REQUEST ?
                "download image failed" : "filter image operation failed");

            Utils.showToast(this, errorMsg);
        }

        mProcessInProgress = false;
    }

    /**
     * Factory method that returns an implicit Intent for downloading
     * an image.
     */
    private Intent createDownloadImageIntent(Uri url) {
        Log.d(TAG, "creating download image intent: " + url.toString());
        return new Intent(DownloadImageActivity.ACTION_DOWNLOAD_IMAGE, url);
    }

    /**
     * Factory method that returns an implicit Intent for filtering an image
     */
    private Intent createFilterImageIntent(Uri url) {
        Log.d(TAG, "creating filter image intent: " + url.toString());
        Intent filterIntent = new Intent(FilterImageActivity.ACTION_FILTER_IMAGE);
        filterIntent.setDataAndType(url, "image/*");
        return filterIntent;
    }

    /**
     * Factory method that returns an implicit Intent for viewing the
     * downloaded and filtered image in the Gallery app.
     */
    private Intent createGalleryIntent(String pathToImageFile) {
        // Create an intent that will start the Gallery app to view
        // the image.
        Log.i(TAG, "createGalleryIntent: " + pathToImageFile);
        File imageFile = new File(pathToImageFile);
        Uri imageUri = Uri.fromFile(imageFile);

        Intent intent = new Intent(Intent.ACTION_VIEW, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setDataAndType(imageUri, "image/*");

        return intent;

    }

    /**
     * Get the URL to download based on user input.
     */
    protected Uri getUrl() {
        // Get the text the user typed in the edit text (if anything).
        Uri url = Uri.parse(mUrlEditText.getText().toString());

        // If the user didn't provide a URL then use the default.
        String uri = url.toString();
        if (uri == null || uri.equals("")) {
            url = mDefaultUrl;
        }

        return url;
    }

    /**
     * This method is used to hide a keyboard after a user has
     * finished typing the url.
     */
    public void hideKeyboard(Activity activity,
                             IBinder windowToken) {
        InputMethodManager mgr =
                (InputMethodManager) activity.getSystemService
                        (INPUT_METHOD_SERVICE);
        mgr.hideSoftInputFromWindow(windowToken,
                0);
    }
}
