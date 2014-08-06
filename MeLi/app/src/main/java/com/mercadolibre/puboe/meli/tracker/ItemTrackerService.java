package com.mercadolibre.puboe.meli.tracker;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;
import android.widget.Toast;

import com.mercadolibre.puboe.meli.R;
import com.mercadolibre.puboe.meli.activities.SearchResults;
import com.mercadolibre.puboe.meli.model.Item;
import com.mercadolibre.puboe.meli.sqlite.ItemDAOImpl;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

/**
 * Created by puboe on 22/07/14.
 */
public class ItemTrackerService extends IntentService {

    private static final String itemsBaseUrl = "https://api.mercadolibre.com/items/";

    public ItemTrackerService() {
        super("ItemTrackerService");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Toast.makeText(this, "Item tracker service starting", Toast.LENGTH_SHORT).show();
        return super.onStartCommand(intent,flags,startId);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        ItemDAO itemDAO = ItemDAOImpl.getInstance(this.getApplicationContext());
        List<Item> items = itemDAO.getAllItems();
        int mId = 0;
        for(Item item: items) {
            Item requestedItem = requestItem(item.getId());
            if(requestedItem != null) {
                System.out.println("dbItem " + item.getId() + " price: $" + item.getPrice() + " , requestedItem " + requestedItem.getId() + " price: $" + requestedItem.getPrice());
                if (!item.getPrice().equals(requestedItem.getPrice())) {
                    Log.i("ItemTrackerService", "Item " + item.getId() + " cambio de precio");
                    NotificationCompat.Builder mBuilder =
                            new NotificationCompat.Builder(this)
                                    .setSmallIcon(R.drawable.ic_launcher)
                                    .setAutoCancel(true)
                                    .setContentTitle("Item: " + item.getId())
                                    .setContentText("Cambio su precio de $" + item.getPrice() + " a $" + requestedItem.getPrice());

                    Intent resultIntent = new Intent(this, SearchResults.class);
                    resultIntent.setAction(SearchResults.ACTION_SHOW_VIP);
                    resultIntent.putExtra(SearchResults.KEY_VIP_ID, item.getId());

                    TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
                    stackBuilder.addParentStack(SearchResults.class);
                    stackBuilder.addNextIntent(resultIntent);
                    PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
                    mBuilder.setContentIntent(resultPendingIntent);
                    NotificationManager mNotificationManager =
                            (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                    // mId allows you to update the notification later on.
                    mNotificationManager.notify(mId++, mBuilder.build());
                    itemDAO.updateItem(requestedItem);
                }
            } else {
                Log.e(ItemTrackerService.class.getSimpleName(), "requestedItem == null, pero debería ser: " + item.getId());
            }
        }


    }

    private Item requestItem(String itemId) {
        HttpClient httpclient = new DefaultHttpClient();
        HttpResponse response;
        Item responseItem = null;
        try {
            response = httpclient.execute(new HttpGet(itemsBaseUrl + itemId));
            Log.d("GET", itemId);
            StatusLine statusLine = response.getStatusLine();
            if(statusLine.getStatusCode() == HttpStatus.SC_OK){
                ByteArrayOutputStream out = new ByteArrayOutputStream();
                response.getEntity().writeTo(out);
                out.close();
                String responseString = out.toString();
                responseItem = Item.parseCompleteItem(new JSONObject(responseString));
            } else{
                //Closes the connection.
                response.getEntity().getContent().close();
                throw new IOException(statusLine.getReasonPhrase());
            }
        } catch (ClientProtocolException e) {
            //TODO Handle problems..
        } catch (Exception e) {
            Log.e(ItemTrackerService.class.getSimpleName(), "RequestItem Exception");
        }
        return responseItem;
    }

}
