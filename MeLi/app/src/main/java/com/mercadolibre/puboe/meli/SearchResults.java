package com.mercadolibre.puboe.meli;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class SearchResults extends ListActivity implements SearchInterface {

    public static final String KEY_DATA = "key_data";
    private Search searchObject;


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
        // TODO aca gurdar los datos (el objeto Search)
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

        new SearchAsyncTask(this).execute(query);
    }

    @Override
    public void onSearchSuccess(Search response) {
        searchObject = response;
        SearchAdapter adapter = new SearchAdapter(this, response);
        ListView listview = (ListView) findViewById(android.R.id.list);
        listview.setAdapter(adapter);
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

}
