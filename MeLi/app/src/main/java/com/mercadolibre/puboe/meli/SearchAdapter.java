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
        PhotoView photoView;
        TextView txtTitle;
        TextView subTitle;
        TextView txtPrice;
        TextView available;
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
            holder.photoView = (PhotoView) view.findViewById(R.id.row_thumbnail);
            holder.subTitle = (TextView) view.findViewById(R.id.row_subtitle);
            holder.available = (TextView) view.findViewById(R.id.row_available);
            view.setTag(holder);
        }
        else {
            holder = (ViewHolder) view.getTag();
        }

        Item rowItem = (Item) getItem(i);

        holder.txtTitle.setText(rowItem.getTitle());
        holder.txtPrice.setText("$" + rowItem.getPrice().toString());
        holder.photoView.setImageURL(rowItem.getThumbnail());
//        PhotoManager.getInstance().startDownload(rowItem.getThumbnail(), holder.photoView);
        if(holder.subTitle != null) {
            if(!rowItem.getSubtitle().equals("null")) {
                holder.subTitle.setText(rowItem.getSubtitle());
            } else {
                holder.subTitle.setVisibility(View.GONE);
            }

        }
        if(holder.available != null) {
            holder.available.setText(rowItem.getAvailableQuantity() + " disponibles");
        }

        return view;
    }
}
