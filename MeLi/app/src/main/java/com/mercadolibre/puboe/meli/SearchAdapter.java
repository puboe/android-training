package com.mercadolibre.puboe.meli;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.mercadolibre.puboe.meli.model.Item;
import com.mercadolibre.puboe.meli.model.Search;
import com.mercadolibre.puboe.meli.photomanager.PhotoView;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Iterator;
import java.util.List;

/**
 * Created by puboe on 03/07/14.
 */
public class SearchAdapter extends BaseAdapter {
    private Context context;
    private Search search;

    public SearchAdapter(Context context, Search search) {
        this.context = context;
        this.search = search;
    }

    @Override
    public int getCount() {
        return search.getResults().size();
    }

    @Override
    public Object getItem(int i) {
        Object[] items = search.getResults().toArray();
        return items[i];
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    private class ViewHolder {
        PhotoView photoView;
        TextView txtTitle;
        TextView subtitle;
        TextView txtPrice;
        TextView available;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder holder = null;

        LayoutInflater mInflater = (LayoutInflater)
                context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        if (view == null) {

//            Log.i("SearchAdapter", "New view for: " + i);
            view = mInflater.inflate(R.layout.search_results_item, null);
            holder = new ViewHolder();
            holder.txtTitle = (TextView) view.findViewById(R.id.row_title);
            holder.txtPrice = (TextView) view.findViewById(R.id.row_price);
            holder.photoView = (PhotoView) view.findViewById(R.id.row_thumbnail);
            holder.subtitle = (TextView) view.findViewById(R.id.row_subtitle);
            holder.available = (TextView) view.findViewById(R.id.row_available);
            view.setTag(holder);
        }
        else {
//            Log.i("SearchAdapter", "Cached view for: " + i);
            holder = (ViewHolder) view.getTag();
        }

        Item rowItem = (Item) getItem(i);

        holder.txtTitle.setText(rowItem.getTitle());
        holder.txtPrice.setText("$" + rowItem.getPrice().toString());
        try {
            URL mUrl = null;
            mUrl = new URL(rowItem.getThumbnail());
//            Log.i("SearchAdapter", "Set image URL for: " + rowItem.getThumbnail());
            holder.photoView.setImageURL(mUrl);
        } catch (MalformedURLException e) {
//            e.printStackTrace();
            Log.e(SearchAdapter.class.getSimpleName(), "Malformed URL");
            holder.photoView.setImageResource(R.drawable.imagedownloadfailed);
        }
        if(holder.subtitle != null) {
            if(!rowItem.getSubtitle().equals("null")) {
                holder.subtitle.setText(rowItem.getSubtitle());
            } else {
                holder.subtitle.setVisibility(View.GONE);
            }
        }
        if(holder.available != null) {
            holder.available.setText(rowItem.getAvailableQuantity() + " disponibles");
        }

        return view;
    }
}
