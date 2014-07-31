package com.mercadolibre.puboe.meli.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.mercadolibre.puboe.meli.fragments.ItemViewFragment;
import com.mercadolibre.puboe.meli.R;
import com.mercadolibre.puboe.meli.model.Item;
import com.mercadolibre.puboe.meli.robospice.ItemRetrofitRequest;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;

public class DeepLinkingActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_deep_linking);

        Intent intent = getIntent();
        Uri data = intent.getData();
        String[] pathStrings = data.getPath().split("-");
        String itemId = pathStrings[0].substring(1) + pathStrings[1];
        Log.i(DeepLinkingActivity.class.getSimpleName(), "URI itemId: " + itemId);

        ItemRetrofitRequest itemRetrofitRequest = new ItemRetrofitRequest(itemId);
        getItemSpiceManager().execute(itemRetrofitRequest, new DeepLinkingActivity.ItemRequestListener());
    }

    private void onItemRequestSuccess(Item result) {
        ItemViewFragment itemViewFragment = (ItemViewFragment) getFragmentManager().findFragmentById(R.id.vip_fragment);
        if(itemViewFragment != null) {
            itemViewFragment.showItem(result);
        }
    }

    public final class ItemRequestListener implements RequestListener<Item> {

        @Override
        public void onRequestFailure(SpiceException spiceException) {
            Toast.makeText(DeepLinkingActivity.this, "Item request failed", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onRequestSuccess(Item result) {
//            Toast.makeText(DeepLinkingActivity.this, "Item request success", Toast.LENGTH_SHORT).show();
            onItemRequestSuccess(result);
        }

    }
}
