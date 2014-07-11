package com.mercadolibre.puboe.meli;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by puboe on 10/07/14.
 */
public class ItemViewActivity extends Activity implements ItemCallbackInterface {

    public static final String KEY_ITEM = "key_item";
    private Item itemObject;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.w("ItemView", "onCreate");
        setContentView(R.layout.activity_item_view);

        Intent intent = getIntent();
        handleIntent(intent);
    }

    private void handleIntent(Intent intent) {
        String id = intent.getExtras().getString(KEY_ITEM);
        new ItemAsyncTask(this).execute(id);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable(KEY_ITEM, itemObject);
    }

    @Override
    protected void onRestoreInstanceState(Bundle state) {
        Item item = (Item) state.getSerializable(KEY_ITEM);
        onItemRequestSuccess(item);
        super.onRestoreInstanceState(state);
    }

    @Override
    public void onItemRequestSuccess(Item response) {
        itemObject = response;
        ImageView imageView = (ImageView)findViewById(R.id.item_image);
            PhotoManager.getInstance().startDownload(response.getImageUrl(), imageView);
        TextView title = (TextView)findViewById(R.id.item_title);
            title.setText(response.getTitle());
        TextView price = (TextView)findViewById(R.id.item_price);
            price.setText("Precio: $" + response.getPrice());
        TextView condition = (TextView)findViewById(R.id.item_condition);
            condition.setText("Articulo " + (response.getCondition().equals("new")?"nuevo":"usado"));
        TextView available = (TextView)findViewById(R.id.item_available_quantity);
            available.setText(response.getAvailableQuantity() + " diponibles");
    }
}
