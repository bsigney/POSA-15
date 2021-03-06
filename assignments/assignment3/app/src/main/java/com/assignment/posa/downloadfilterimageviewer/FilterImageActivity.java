package com.assignment.posa.downloadfilterimageviewer;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

/**
 * An Activity that downloads an image, stores it in a local file on
 * the local device, and returns a Uri to the image file.
 */
public class FilterImageActivity extends BaseImageActivity {
    /**
     * Debugging tag used by the Android logger.
     */
    private final String TAG = getClass().getSimpleName();

    public static final String ACTION_FILTER_IMAGE = "intent.action.FILTER_IMAGE";

    @Override
    public Uri doInBackgroundHook(Context context, Uri url) {
        Log.d(TAG, "Filtering image: " + url.toString());

        return Utils.grayScaleFilter(context, url);
    }
}
