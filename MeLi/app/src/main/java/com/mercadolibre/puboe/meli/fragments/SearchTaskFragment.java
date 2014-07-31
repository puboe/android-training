package com.mercadolibre.puboe.meli.fragments;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;

import com.mercadolibre.puboe.meli.asynctask.CustomAsyncTask;
import com.mercadolibre.puboe.meli.asynctask.SearchCallbackInterface;
import com.mercadolibre.puboe.meli.model.Search;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * This Fragment manages a single background task and retains
 * itself across configuration changes.
 */
public class SearchTaskFragment extends Fragment {

    public static final String KEY_ARGS = "key_args";
    private SearchCallbackInterface mCallbacks;
    private SearchAsyncTask searchAsyncTask;

    /**
     * Hold a reference to the parent Activity so we can report the
     * task's current progress and results. The Android framework
     * will pass us a reference to the newly created Activity after
     * each configuration change.
     */
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mCallbacks = (SearchCallbackInterface) activity;
    }

    /**
     * This method will only be called once when the retained
     * Fragment is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Retain this fragment across configuration changes.
        setRetainInstance(true);

        Bundle args = getArguments();
        String query = args.getString(KEY_ARGS);

        // Create and execute the background task.
        searchAsyncTask = (SearchAsyncTask) new SearchAsyncTask().execute(query);
    }

    /**
     * Set the callback to null so we don't accidentally leak the
     * Activity instance.
     */
    @Override
    public void onDetach() {
        super.onDetach();
        mCallbacks = null;
    }

    /**
     * A dummy task that performs some (dumb) background work and
     * proxies progress updates and results back to the Activity.
     *
     * Note that we need to check if the callbacks are null in each
     * method in case they are invoked after the Activity's and
     * Fragment's onDestroy() method have been called.
     */
    private class SearchAsyncTask extends CustomAsyncTask {

        private final String searchBaseUrl = "https://api.mercadolibre.com/sites/MLA/search?q=";
        private final String searchParameters = "&limit=15";

        /**
         * Note that we do NOT call the callback object's methods
         * directly from the background thread, as this could result
         * in a race condition.
         */
        @Override
        protected String doInBackground(String... query) {
            setQuery(query[0]);
            String url = searchBaseUrl + query[0] + searchParameters;
            Log.w(SearchAsyncTask.class.getSimpleName(), "doInBackgroudURL: " + url);
            return super.doInBackground(url);
        }

        @Override
        protected void onPostExecute(String result) {
            try {
                JSONObject jsonSearch = new JSONObject(result);
                Search response = Search.parseSearchObject(jsonSearch);
                Log.i(SearchAsyncTask.class.getSimpleName(), "callbackInterface: " + mCallbacks);
                if(mCallbacks != null) {
                    mCallbacks.onSearchSuccess(response);
                }
            } catch (JSONException e) {
                Log.w(SearchAsyncTask.class.getSimpleName(), "onPostExecute: " + e.getLocalizedMessage());
            }
        }
    }
}
