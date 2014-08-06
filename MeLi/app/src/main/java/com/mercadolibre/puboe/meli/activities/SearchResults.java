package com.mercadolibre.puboe.meli.activities;

import android.app.FragmentTransaction;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.mercadolibre.puboe.meli.fragments.ItemViewFragment;
import com.mercadolibre.puboe.meli.R;
import com.mercadolibre.puboe.meli.fragments.SearchResultsFragment;
import com.mercadolibre.puboe.meli.asynctask.ItemCallbackInterface;
import com.mercadolibre.puboe.meli.asynctask.SearchCallbackInterface;
import com.mercadolibre.puboe.meli.model.Item;
import com.mercadolibre.puboe.meli.model.Search;
import com.mercadolibre.puboe.meli.robospice.ItemRetrofitRequest;
import com.mercadolibre.puboe.meli.robospice.SearchRetrofitRequest;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;

public class SearchResults extends BaseActivity implements SearchCallbackInterface,
        SearchResultsFragment.OnFragmentInteractionListener, ItemCallbackInterface {

    public static final String ACTION_SHOW_VIP = "action_show_vip";
    public static final String KEY_DATA = "key_data";
    public static final String KEY_ITEM = "key_item";
    public static final String KEY_VIP_ID = "key_vip_id";
    public static final Integer LIMIT = 15;
    private Search searchObject;
    private Item itemObject;
    private String query;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.w("SearchResults", "onCreate");

        if(savedInstanceState != null) {
            Log.i(SearchResults.class.getSimpleName(), "on Create: Restoring SavedInstanceState");
            searchObject = (Search) savedInstanceState.getSerializable(KEY_DATA);
            itemObject = (Item) savedInstanceState.getSerializable(KEY_ITEM);
        }

        setContentView(R.layout.activity_search_results);

        if (findViewById(R.id.fragment_container) != null) {
            SearchResultsFragment firstFragment = SearchResultsFragment.newInstance();
            if (savedInstanceState != null) {
                Log.i("SearchResults", "savedInstanceState PORTRAIT != NULL");
                getFragmentManager().beginTransaction().addToBackStack(null).replace(R.id.fragment_container, firstFragment).commit();
            } else {
                getFragmentManager().beginTransaction().add(R.id.fragment_container, firstFragment).commit();
            }
        } else if(findViewById(R.id.list_frame) != null) {
            SearchResultsFragment searchResultsFragment = SearchResultsFragment.newInstance();
            ItemViewFragment itemViewFragment = ItemViewFragment.newInstance();
            if (savedInstanceState != null) {
                Log.i("SearchResults", "savedInstanceState LAND != NULL");
                getFragmentManager().beginTransaction().replace(R.id.list_frame, searchResultsFragment).commit();
                getFragmentManager().beginTransaction().replace(R.id.vip_frame, itemViewFragment).commit();
            } else {
                getFragmentManager().beginTransaction().add(R.id.list_frame, searchResultsFragment).commit();
                if(itemObject != null) {
                    Bundle bundle = new Bundle();
                    bundle.putSerializable(ItemViewFragment.KEY_ITEM, itemObject);
                    itemViewFragment.setArguments(bundle);
                }
                getFragmentManager().beginTransaction().add(R.id.vip_frame, itemViewFragment).commit();
            }
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
        outState.putSerializable(KEY_ITEM, itemObject);
    }

    @Override
    protected void onRestoreInstanceState(Bundle state) {
        Log.i(SearchResults.class.getSimpleName(), "onRestoreInstanceState");
        super.onRestoreInstanceState(state);
    }

    public void onNewIntent(Intent intent) {
        setIntent(intent);
        handleIntent(intent);
    }

    private void handleIntent(Intent intent) {
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getExtras().getString(SearchBox.KEY_QUERY);
            Log.i(SearchResults.class.getSimpleName(), "Query: " + query);
            onSearchRequested(query);
        } else if(intent.getAction().equals(ACTION_SHOW_VIP)) {
            String id = intent.getExtras().getString(KEY_VIP_ID);
            onItemSelected(id);
        } else if(intent.getAction().equals(Intent.ACTION_VIEW)) {
            Uri data = intent.getData();
            String query = data.getPath().substring(1).replaceAll("-", "+");
            Log.i(SearchResults.class.getSimpleName(), "Query: " + query);
            onSearchRequested(query);
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
                    getFragmentManager().findFragmentById(R.id.list_frame);
            searchResultsFragment.showResults(searchObject);
        }
    }

    @Override
    public void onItemSelected(String id) {
        ItemRetrofitRequest itemRetrofitRequest = new ItemRetrofitRequest(id);
        getItemSpiceManager().execute(itemRetrofitRequest, new ItemRequestListener());
    }

    @Override
    public void onItemRequestSuccess(Item response) {
        itemObject = response;

        if (findViewById(R.id.fragment_container) != null) {
            Log.i(SearchResults.class.getSimpleName(), "SINGLE-PANE");
            ItemViewFragment newFragment = ItemViewFragment.newInstance();
            Bundle bundle = new Bundle();
            bundle.putSerializable(ItemViewFragment.KEY_ITEM, itemObject);
            newFragment.setArguments(bundle);
            FragmentTransaction transaction = getFragmentManager().beginTransaction();

            transaction.addToBackStack(null);
            transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
            transaction.replace(R.id.fragment_container, newFragment);

            transaction.commit();
        } else {
            Log.i(SearchResults.class.getSimpleName(), "TWO-PANE");
            ItemViewFragment itemViewFragment = (ItemViewFragment) getFragmentManager().findFragmentById(R.id.vip_frame);
            itemViewFragment.showItem(itemObject);
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
            Toast.makeText(SearchResults.this, "Search request failed", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onRequestSuccess(Search result) {
//            Toast.makeText(SearchResults.this, "search request success", Toast.LENGTH_SHORT).show();
            onSearchSuccess(result);
        }
    }

    public final class ItemRequestListener implements RequestListener<Item> {

        @Override
        public void onRequestFailure(SpiceException spiceException) {
            Toast.makeText(SearchResults.this, "Item request failed", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onRequestSuccess(Item result) {
//            Toast.makeText(SearchResults.this, "item request success", Toast.LENGTH_SHORT).show();
            onItemRequestSuccess(result);
        }
    }

}
