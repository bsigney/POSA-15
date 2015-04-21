package com.assignment.posa.downloadfilterimageviewer;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import com.assignment.posa.bsigney.downloadfilterimageviewer.R;

/**
 * Created by bsigney on 18/04/15.
 */
public abstract class BaseImageActivity extends LifecycleLoggingActivity {

    /**
     * Debugging tag used by the Android logger.
     */
    private final String TAG = getClass().getSimpleName();

    /**
     * Keys to be used by the RetainedFragmentManager
     */
    private static final String URL = "url";
    private static final String IMAGE_PATH = "imagePath";
    private static final String ASYNC_TASK = "asynTask";

    /**
     * Retain state information between configuration changes
     */
    protected RetainedFragmentManager mRetainedFragmentManger =
        new RetainedFragmentManager(this, "BaseImageActivityTag");

    private ProgressBar mProgressBar;

    /**
     * Must be overriden to define an operation that runs in the background thread.
     *
     * @return a URI to the updated image
     */
    public abstract Uri doInBackgroundHook(Context context, Uri url);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_base_image);

        mProgressBar = (ProgressBar) findViewById(R.id.progressBar);

        if (mRetainedFragmentManger.isFirstUsed()) {
            // Store URL
            Uri url = getIntent().getData();

            Log.d(TAG, "onCreate: First time on activity: " + url.toString());
            mRetainedFragmentManger.put(URL, url);
        } else {
            // Config change has occured, retrieve the URL
            Uri pathToImage = mRetainedFragmentManger.get(IMAGE_PATH);

            // if url is set then set result of Activity
            if (pathToImage != null) {
                Log.d(TAG, "activity completed since result has been completed");

                setActivityResult(RESULT_OK, pathToImage);
                finish();
            } else {
                Log.d(TAG, "activity still in progress");
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        // creates an Intent that contains the path to the resulting image file, and sets this as the
        // result of the Activity
        ImageAsyncTask imageAsyncTask = mRetainedFragmentManger.get(ASYNC_TASK);
        if (imageAsyncTask == null) {
            Log.d(TAG, "creating and executing ImageAsyncTask");

            Uri url = mRetainedFragmentManger.get(URL);

            imageAsyncTask = new ImageAsyncTask();
            mRetainedFragmentManger.put(ASYNC_TASK, imageAsyncTask.execute(url));
        } else {
            Log.d(TAG, "ImageAsyncTask still in progress");
        }
    }

    private void setActivityResult(int resultCode, Uri url) {
        Intent intent = new Intent();
        intent.setData(url);
        setResult(resultCode, intent);
    }

    private class ImageAsyncTask extends AsyncTask<Uri, Void, Uri> {

        @Override
        protected Uri doInBackground(Uri... urls) {
            Log.d(TAG, "ImageAsyncTask doInBackground");

            return doInBackgroundHook(BaseImageActivity.this, urls[0]);
        }

        @Override
        protected void onPreExecute() {
            Log.d(TAG, "ImageAsyncTask onPreExecute");
            super.onPreExecute();

            mProgressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected void onPostExecute(Uri uri) {
            Log.d(TAG, "ImageAsyncTask onPostExecute");
            super.onPostExecute(uri);

            if(uri != null) {
                Log.d(TAG, "ImageAsyncTask RESULT OK");
                mRetainedFragmentManger.put(IMAGE_PATH, uri);
                setActivityResult(RESULT_OK, uri);
            } else {
                Log.d(TAG, "ImageAsyncTask RESULT CANCELLED");
                setActivityResult(RESULT_CANCELED, uri);
            }

            mProgressBar.setVisibility(View.GONE);
            finish();

        }
    }


}
