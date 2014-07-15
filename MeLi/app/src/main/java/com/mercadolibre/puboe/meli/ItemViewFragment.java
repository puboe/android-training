package com.mercadolibre.puboe.meli;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ItemViewFragment#newInstance} factory method to
 * create an instance of this fragment.
 *
 */
public class ItemViewFragment extends Fragment {

    private Item itemObject;

    public static final String KEY_ITEM = "key_item";

    public static ItemViewFragment newInstance(Item item) {
        ItemViewFragment fragment = new ItemViewFragment();
        Bundle args = new Bundle();
        args.putSerializable(KEY_ITEM, item);
        fragment.setArguments(args);
        return fragment;
    }
    public ItemViewFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            this.itemObject = (Item)getArguments().getSerializable(KEY_ITEM);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_item_view, container, false);
//        showItem(this.itemObject);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        showItem(itemObject);
    }

    public void showItem(Item item) {
        itemObject = item;
        PhotoView photoView = (PhotoView)getActivity().findViewById(R.id.item_image);

//        photoView.setImageResource(R.drawable.imagedownloading);
        try {
            URL mUrl = null;
            mUrl = new URL(item.getImageUrl());
            Log.i("ItemViewActivity", item.getImageUrl());
            photoView.setImageURL(mUrl);
        } catch (MalformedURLException e) {
            e.printStackTrace();
            photoView.setImageResource(R.drawable.imagedownloadfailed);
        }
        TextView title = (TextView)getActivity().findViewById(R.id.item_title);
        title.setText(item.getTitle());
        TextView price = (TextView)getActivity().findViewById(R.id.item_price);
        price.setText("Precio: $" + item.getPrice());
        TextView condition = (TextView)getActivity().findViewById(R.id.item_condition);
        condition.setText("Articulo " + (item.getCondition().equals("new")?"nuevo":"usado"));
        TextView available = (TextView)getActivity().findViewById(R.id.item_available_quantity);
        available.setText(item.getAvailableQuantity() + " diponibles");

    }


}
