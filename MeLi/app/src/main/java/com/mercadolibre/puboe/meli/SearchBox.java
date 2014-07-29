package com.mercadolibre.puboe.meli;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.Toast;

import com.mercadolibre.puboe.meli.tracker.AlarmSetter;

public class SearchBox extends Activity {

    public static final String KEY_QUERY = "key_query";
    EditText editText;
    Button b;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.w("SearchResults", "onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_box);
//        AlarmSetter.startAlarm(this);

        editText = (EditText) findViewById(R.id.search_query);

        Log.w("EditTextContent", editText.getText().toString());
        b = (Button) findViewById(R.id.search_button);

        OnClickListener l = new OnClickListener() {
            @Override
            public void onClick(View v) {
                startSearchActivity(editText.getText().toString().replaceAll(" ", "+"));
            }
        };
        b.setOnClickListener(l);
    }

    public void startSearchActivity(String query) {
        Log.w("startSearchQuery", query);
        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            // fetch data
            Intent intent = new Intent(this, SearchResults.class);
            intent.putExtra(KEY_QUERY, query);
            intent.setAction(Intent.ACTION_SEARCH);
            startActivity(intent);
        } else {
            // display error
            Toast.makeText(SearchBox.this,"No hay conexión", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.search_box, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            Intent startSettingsActivity = new Intent(this, SettingsActivity.class);
            startActivity(startSettingsActivity);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
