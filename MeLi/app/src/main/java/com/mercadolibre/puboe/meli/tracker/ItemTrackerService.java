package com.mercadolibre.puboe.meli.tracker;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.mercadolibre.puboe.meli.Item;
import com.mercadolibre.puboe.meli.ItemDAO;
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

    private final String itemsBaseUrl = "https://api.mercadolibre.com/items/";

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
        ItemDAO itemDAO = ItemDAOImpl.getInstance(this);
        List<Item> items = itemDAO.getAllItems();

        for(Item item: items) {
            Item requestedItem = requestItem(item.getId());
            if(requestedItem != null) {
                System.out.println("dbItem price: $" + item.getPrice() + " , requestedItem price: $" + requestedItem.getPrice());
                if (item.getPrice() != requestedItem.getPrice()) {
//                TODO throw notification
                    Toast.makeText(this, "Item: " + requestedItem.getId() + " cambió de precio.", Toast.LENGTH_LONG).show();
                }
//              TODO chequear si la publicacion está por terminar o ya terminó
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
