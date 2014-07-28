package com.mercadolibre.puboe.meli;

import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.mercadolibre.puboe.meli.asynctask.ItemAsyncTask;
import com.mercadolibre.puboe.meli.asynctask.ItemCallbackInterface;
import com.mercadolibre.puboe.meli.asynctask.SearchCallbackInterface;
import com.mercadolibre.puboe.meli.model.Item;
import com.mercadolibre.puboe.meli.model.Search;
import com.mercadolibre.puboe.meli.robospice.ItemRetrofitRequest;
import com.mercadolibre.puboe.meli.robospice.SearchRetrofitRequest;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;

public class SearchResults extends BaseActivity implements SearchCallbackInterface,
                                                        SearchResultsFragment.OnFragmentInteractionListener,
                                                        ItemCallbackInterface {

    private static final String TAG_TASK_FRAGMENT = "task_fragment";
    public static final String SEARCH_ASYNC_TASK_IN_PROGRESS = "search_async_task_in_progress";
    public static final String ITEM_ASYNC_TASK_IN_PROGRESS = "item_async_task_in_progress";
    public static final String SEARCH_ASYNC_TASK_QUERY = "search_async_task_query";
    public static final String ITEM_ASYNC_TASK_QUERY = "search_async_task_query";
    public static final String ACTION_SHOW_VIP = "action_show_vip";
    public static final String KEY_DATA = "key_data";
    public static final String KEY_VIP_ID = "key_vip_id";
    public static final Integer LIMIT = 15;
    private Search searchObject;
    private String query;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.w("SearchResults", "onCreate");

        if(savedInstanceState != null) {
            Log.i(SearchResults.class.getSimpleName(), "on Create: Restoring SavedInstanceState");
            searchObject = (Search) savedInstanceState.getSerializable(KEY_DATA);
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
//        if (searchAsyncTask != null && searchAsyncTask.getStatus() != AsyncTask.Status.FINISHED) {
//            Log.i(SearchResults.class.getSimpleName(), "Saving SearchAsyncTask");
//            String query = searchAsyncTask.getQuery();
//            searchAsyncTask.cancel(true);
//            if (query != null) {
//                outState.putBoolean(SEARCH_ASYNC_TASK_IN_PROGRESS, true);
//                outState.putString(SEARCH_ASYNC_TASK_QUERY, query);
//            }
//            searchAsyncTask = null;
//        }
//        if (itemAsyncTask != null && itemAsyncTask.getStatus() != AsyncTask.Status.FINISHED) {
//            Log.i(SearchResults.class.getSimpleName(), "Saving ItemAsyncTask");
//            String query = itemAsyncTask.getQuery();
//            itemAsyncTask.cancel(true);
//            if (query != null) {
//                outState.putBoolean(ITEM_ASYNC_TASK_IN_PROGRESS, true);
//                outState.putString(ITEM_ASYNC_TASK_QUERY, query);
//            }
//            itemAsyncTask = null;
//        }
    }

    @Override
    protected void onRestoreInstanceState(Bundle state) {
        Log.i(SearchResults.class.getSimpleName(), "onRestoreInstanceState");
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
        SearchRetrofitRequest searchRetrofitRequest = new SearchRetrofitRequest(query, 0, LIMIT);
        getSearchSpiceManager().execute(searchRetrofitRequest, new SearchRequestListener());
    }

    @Override
    public void onRequestMoreItems() {
        if(query == null)
            return;
        getSearchObject().getPaging().setOffset(getSearchObject().getPaging().getOffset()+15);

        SearchRetrofitRequest searchRetrofitRequest = new SearchRetrofitRequest(query, getSearchObject().getPaging().getOffset(), LIMIT);
        getSearchSpiceManager().execute(searchRetrofitRequest, new SearchRequestListener());
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
        }
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
        ItemRetrofitRequest itemRetrofitRequest = new ItemRetrofitRequest(id);
        getItemSpiceManager().execute(itemRetrofitRequest, new ItemRequestListener());
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

    public final class SearchRequestListener implements RequestListener<Search> {

        @Override
        public void onRequestFailure(SpiceException spiceException) {
            Toast.makeText(SearchResults.this, "search request failure", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onRequestSuccess(Search result) {
            Toast.makeText(SearchResults.this, "search request success", Toast.LENGTH_SHORT).show();
            onSearchSuccess(result);
        }
    }

    public final class ItemRequestListener implements RequestListener<Item> {

        @Override
        public void onRequestFailure(SpiceException spiceException) {
            Toast.makeText(SearchResults.this, "item request failure", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onRequestSuccess(Item result) {
            Toast.makeText(SearchResults.this, "item request success", Toast.LENGTH_SHORT).show();
            onItemRequestSuccess(result);
        }
    }

}
