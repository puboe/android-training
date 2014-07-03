package com.mercadolibre.puboe.meli;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.mercadolibre.puboe.meli.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class SearchResults extends ListActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.w("SearchResults", "onCreate");
        setContentView(R.layout.activity_search_results);

        Intent intent = getIntent();
        handleIntent(intent);

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

        new processSearch().execute(query);
    }

    private class processSearch extends CustomAsyncTask {

        private final String searchBaseUrl = "https://api.mercadolibre.com/sites/MLA/search?q=";
        private final String searchParameters = "&limit=100";

        @Override
        protected String doInBackground(String... query) {
            String url = searchBaseUrl + query[0] + searchParameters;
            Log.w("doInBackgroudURL", url);
            return super.doInBackground(url);
        }

        @Override
        protected void onPostExecute(String result) {
            try {
                final ListView listview = (ListView) findViewById(android.R.id.list);
                JSONObject json = new JSONObject(result);
                JSONArray resultsArray = json.getJSONArray("results");

                if(SearchResults.this == null)
                    return;


                ArrayAdapter<String> adapter = new ArrayAdapter<String>(SearchResults.this, android.R.layout.simple_list_item_1);
                //new CustomArrayAdapter(SearchResults.this, R.id.row_layout);

                for(int i = 0; i < resultsArray.length(); i++) {
                    JSONObject row = resultsArray.getJSONObject(i);
                    //ResultRow r =  new ResultRow(row.getString("title"), row.getString("price"));
                    Log.d("Row"+i, row.getString("title"));
                    adapter.add(row.getString("title"));
                    //adapter.add(r);
                }
                listview.setAdapter(adapter);
            } catch (JSONException e) {
                Log.w("onPostExecuteLogOut", e.getLocalizedMessage());
            }

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

}
