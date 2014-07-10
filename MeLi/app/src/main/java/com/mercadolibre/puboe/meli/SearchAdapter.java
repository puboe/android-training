package com.mercadolibre.puboe.meli;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

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
        return search.getResults().get(i);
    }

    @Override
    public long getItemId(int i) {
        return search.getResults().indexOf(search.getResults().get(i));
    }

    private class ViewHolder {
        ImageView imageView;
        TextView txtTitle;
        TextView txtPrice;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder holder = null;

        LayoutInflater mInflater = (LayoutInflater)
                context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        if (view == null) {
            view = mInflater.inflate(R.layout.search_results_item, null);
            holder = new ViewHolder();
            holder.txtTitle = (TextView) view.findViewById(R.id.row_title);
            holder.txtPrice = (TextView) view.findViewById(R.id.row_price);
            holder.imageView = (ImageView) view.findViewById(R.id.row_thumbnail);
            view.setTag(holder);
        }
        else {
            holder = (ViewHolder) view.getTag();
        }

        Item rowItem = (Item) getItem(i);

        holder.txtTitle.setText(rowItem.getTitle());
        holder.txtPrice.setText("$" + rowItem.getPrice().toString());
        PhotoManager.getInstance().startDownload(rowItem.getThumbnail(), holder.imageView);

        return view;
    }
}
