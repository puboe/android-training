package com.mercadolibre.puboe.meli;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.FragmentTransaction;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.mercadolibre.puboe.meli.asynctask.ItemAsyncTask;
import com.mercadolibre.puboe.meli.asynctask.ItemCallbackInterface;
import com.mercadolibre.puboe.meli.asynctask.SearchAsyncTask;
import com.mercadolibre.puboe.meli.asynctask.SearchCallbackInterface;
import com.mercadolibre.puboe.meli.model.Item;
import com.mercadolibre.puboe.meli.model.Search;

public class SearchResults extends Activity implements SearchCallbackInterface,
                                                        SearchResultsFragment.OnFragmentInteractionListener,
                                                        ItemCallbackInterface {

    public static final String SEARCH_ASYNC_TASK_IN_PROGRESS = "search_async_task_in_progress";
    public static final String ITEM_ASYNC_TASK_IN_PROGRESS = "item_async_task_in_progress";
    public static final String SEARCH_ASYNC_TASK_QUERY = "search_async_task_query";
    public static final String ITEM_ASYNC_TASK_QUERY = "search_async_task_query";
    public static final String ACTION_SHOW_VIP = "action_show_vip";
    public static final String KEY_DATA = "key_data";
    public static final String KEY_VIP_ID = "key_vip_id";
    private Search searchObject;
    private String query;
    private SearchAsyncTask searchAsyncTask;
    private ItemAsyncTask itemAsyncTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.w("SearchResults", "onCreate");

        if(savedInstanceState != null) {
            Log.i(SearchResults.class.getSimpleName(), "on Create: Restoring SavedInstanceState");
            searchObject = (Search) savedInstanceState.getSerializable(KEY_DATA);
            if (savedInstanceState.getBoolean(SEARCH_ASYNC_TASK_IN_PROGRESS)) {
                Log.i(SearchResults.class.getSimpleName(), "Restoring SearchAsyncTask");
                String query = savedInstanceState.getString(SEARCH_ASYNC_TASK_QUERY);
                onSearchRequested(query);
            }
            if (savedInstanceState.getBoolean(ITEM_ASYNC_TASK_IN_PROGRESS)) {
                Log.i(SearchResults.class.getSimpleName(), "Restoring ItemAsyncTask");
                String query = savedInstanceState.getString(ITEM_ASYNC_TASK_QUERY);
                onItemSelected(query);
            }
        }

        setContentView(R.layout.activity_search_results);

        if (findViewById(R.id.fragment_container) != null) {
            if (savedInstanceState != null) {
                Log.i("SearchResults", "savedInstanceState != NULL");
                return;
            }
            SearchResultsFragment firstFragment = SearchResultsFragment.newInstance();

            getFragmentManager().beginTransaction()
                    .add(R.id.fragment_container, firstFragment).commit();
        } else {
            Log.i("SearchResults", "fragment_container == NULL");
        }

        Intent intent = getIntent();
        handleIntent(intent);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable(KEY_DATA, searchObject);
        if (searchAsyncTask != null && searchAsyncTask.getStatus() != AsyncTask.Status.FINISHED) {
            Log.i(SearchResults.class.getSimpleName(), "Saving SearchAsyncTask");
            String query = searchAsyncTask.getQuery();
            searchAsyncTask.cancel(true);
            if (query != null) {
                outState.putBoolean(SEARCH_ASYNC_TASK_IN_PROGRESS, true);
                outState.putString(SEARCH_ASYNC_TASK_QUERY, query);
            }
            searchAsyncTask = null;
        }
        if (itemAsyncTask != null && itemAsyncTask.getStatus() != AsyncTask.Status.FINISHED) {
            Log.i(SearchResults.class.getSimpleName(), "Saving ItemAsyncTask");
            String query = itemAsyncTask.getQuery();
            itemAsyncTask.cancel(true);
            if (query != null) {
                outState.putBoolean(ITEM_ASYNC_TASK_IN_PROGRESS, true);
                outState.putString(ITEM_ASYNC_TASK_QUERY, query);
            }
            itemAsyncTask = null;
        }
    }

    @Override
    protected void onRestoreInstanceState(Bundle state) {
        Log.i(SearchResults.class.getSimpleName(), "onResoreInstanceState");
//        searchObject = (Search) state.getSerializable(KEY_DATA);
        super.onRestoreInstanceState(state);
    }

    public void onNewIntent(Intent intent) {
        setIntent(intent);
        handleIntent(intent);
    }

    private void handleIntent(Intent intent) {
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getExtras().getString(SearchBox.KEY_QUERY);
            onSearchRequested(query);
        } else if(intent.getAction().equals(ACTION_SHOW_VIP)) {
            String id = intent.getExtras().getString(KEY_VIP_ID);
            onItemSelected(id);
        }
    }

    private void onSearchRequested(String query) {
        Log.w("doSearchQuery", query);
        if(query == null)
            return;
        this.query = query;
        searchAsyncTask =  (SearchAsyncTask) new SearchAsyncTask(this).execute(query);
    }

    @Override
    public void onSearchSuccess(Search response) {
        if(searchObject != null) {
            searchObject.getResults().addAll(response.getResults());
            searchObject.setPaging(response.getPaging());
        } else {
            searchObject = response;
        }

        if (findViewById(R.id.fragment_container) != null) {
            Log.i(SearchResults.class.getSimpleName(), "SINGLE-PANE itemViewFragment == null");

            SearchResultsFragment searchResultsFragment =
                    (SearchResultsFragment) getFragmentManager().findFragmentById(R.id.fragment_container);
            if (searchResultsFragment != null) {
                searchResultsFragment.showResults(searchObject);
            } else {
                Log.i("SearchResults", "searchResultsFragment == NULL");
            }
        } else {
            Log.i(SearchResults.class.getSimpleName(), "TWO-PANE searchResultsFragment != null");
            SearchResultsFragment searchResultsFragment = (SearchResultsFragment)
                    getFragmentManager().findFragmentById(R.id.list_fragment);
            searchResultsFragment.showResults(searchObject);

//            SearchResultsFragment newFragment = SearchResultsFragment.newInstance(searchObject);
//            FragmentTransaction transaction = getFragmentManager().beginTransaction();
//
//            transaction.addToBackStack(null);
//            transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
//            transaction.replace(R.id.fragment_container, newFragment);
//
//            transaction.commit();
        }
        searchAsyncTask = null;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.search_results, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onItemSelected(String id) {
        itemAsyncTask = (ItemAsyncTask) new ItemAsyncTask(this).execute(id);
    }

    @Override
    public void onRequestMoreItems() {
        if(query == null)
            return;
        getSearchObject().getPaging().setOffset(getSearchObject().getPaging().getOffset()+15);
        String newQuery = query + "&offset=" + getSearchObject().getPaging().getOffset();
        Log.w("doSearchMore", newQuery);
        searchAsyncTask = (SearchAsyncTask) new SearchAsyncTask(this).execute(newQuery);
    }

    @Override
    public void onItemRequestSuccess(Item response) {

        if (findViewById(R.id.fragment_container) != null) {
            Log.i(SearchResults.class.getSimpleName(), "SINGLE-PANE itemViewFragment == null");
            ItemViewFragment newFragment = ItemViewFragment.newInstance(response);
            FragmentTransaction transaction = getFragmentManager().beginTransaction();

            transaction.addToBackStack(null);
            transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
            transaction.replace(R.id.fragment_container, newFragment);

            transaction.commit();
        } else {
            Log.i(SearchResults.class.getSimpleName(), "TWO-PANE itemViewFragment != null");
            ItemViewFragment itemViewFragment = (ItemViewFragment) getFragmentManager().findFragmentById(R.id.vip_fragment);
            itemViewFragment.showItem(response);
        }
        itemAsyncTask = null;
    }

    @Override
    public Search getSearchObjectFromActivity() {
        return searchObject;
    }

    public Search getSearchObject() {
        return searchObject;
    }

    public String getQuery() {
        return query;
    }

}
