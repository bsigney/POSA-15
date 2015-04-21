package com.assignment.posa.downloadfilterimageviewer;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;
import android.util.Log;

import java.util.HashMap;

public class RetainedFragmentManager {

    /**
     * Debugging tag used by the Android logger.
     */
    private final String TAG = getClass().getSimpleName();

    /**
     * Name used to identify the RetainedFragment
     */
    private final String mRetainedFragmentTag;

    /**
     * Reference to the FragmentManager
     */
    private final FragmentManager mFragmentManger;

    /**
     * Reference to the RetainedFragment
     */
    private RetainedFragment mRetainedFragment;

    /**
     * Constructor
     */
    public RetainedFragmentManager(Activity activity, String retainedFragmentTag) {
        mFragmentManger = activity.getFragmentManager();
        mRetainedFragmentTag = retainedFragmentTag;
    }

    /**
     * @return true if this was first time it was called, else false
     */
    public boolean isFirstUsed() {
        // Find the RetainedFragment on Activity restarts
        mRetainedFragment = (RetainedFragment) mFragmentManger.findFragmentByTag(mRetainedFragmentTag);

        if (mRetainedFragment != null) {
            return false;
        } else {
            mRetainedFragment = new RetainedFragment();
            mFragmentManger.beginTransaction()
                .add(mRetainedFragment, mRetainedFragmentTag)
                    .commit();

            return true;
        }
    }

    public void put(String key, Object object) {
        mRetainedFragment.put(key, object);
    }

    public <T> T get(String key) {
        return (T) mRetainedFragment.get(key);
    }

    public Activity getActivity() {
        return mRetainedFragment.getActivity();
    }

    private class RetainedFragment extends Fragment {

        private HashMap<String, Object> mData = new HashMap<String, Object>();

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            Log.d(TAG, "onCreate called to setRetainedInstance to true");
            setRetainInstance(true);
        }

        public void put(String key, Object object) {
            mData.put(key, object);
        }

        public <T> T get(String key) {
            return (T) mData.get(key);
        }
    }

}
