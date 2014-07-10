package com.mercadolibre.puboe.meli;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AbsListView;
import android.widget.ListView;

public class SearchResults extends ListActivity implements SearchInterface {

    public static final String KEY_DATA = "key_data";
    private Search searchObject;
    private String query;
    SearchAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.w("SearchResults", "onCreate");
        setContentView(R.layout.activity_search_results);

        Intent intent = getIntent();
        handleIntent(intent);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable(KEY_DATA, searchObject);
    }

    @Override
    protected void onRestoreInstanceState(Bundle state) {
        Search search = (Search) state.getSerializable(KEY_DATA);
        onSearchSuccess(search);
        super.onRestoreInstanceState(state);
    }

    public void onNewIntent(Intent intent) {
        setIntent(intent);
        handleIntent(intent);
    }

    private void handleIntent(Intent intent) {
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getExtras().getString(SearchBox.KEY_QUERY);

            doSearch(query);
        }
    }

    private void doSearch(String query) {
        Log.w("doSearchQuery", query);
        if(query == null)
            return;

        this.query = query;
        new SearchAsyncTask(this).execute(query);
    }

    private void doSearchMore(String query) {
        if(query == null)
            return;
        getSearchObject().getPaging().setOffset(getSearchObject().getPaging().getOffset()+15);
        String newQuery = query + "&offset=" + getSearchObject().getPaging().getOffset();
        Log.w("doSearchMore", newQuery);
        new SearchAsyncTask(this).execute(newQuery);
    }

    @Override
    public void onSearchSuccess(Search response) {
        if (getSearchObject() == null) {
            searchObject = response;
            adapter = new SearchAdapter(this, searchObject);
            ListView listview = (ListView) findViewById(android.R.id.list);
            listview.setOnScrollListener(new mOnScrollListener());
            listview.setAdapter(adapter);
        } else {
            searchObject.getResults().addAll(response.getResults());
            searchObject.setPaging(response.getPaging());
            adapter.notifyDataSetChanged();
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

    private class mOnScrollListener implements AbsListView.OnScrollListener {

        @Override
        public void onScrollStateChanged(AbsListView absListView, int i) {
        }

        @Override
        public void onScroll(AbsListView absListView, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
           if(firstVisibleItem+visibleItemCount == totalItemCount && totalItemCount > getSearchObject().getPaging().getOffset()) {
               Log.w("onScroll", "firstVisible: " + firstVisibleItem + ", visibleCount:" + visibleItemCount + ", totalCount: " + totalItemCount);
               doSearchMore(getQuery());
           }
        }
    }

    public Search getSearchObject() {
        return searchObject;
    }

    public String getQuery() {
        return query;
    }

    public SearchAdapter getAdapter() {
        return adapter;
    }
}
