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

    public static final String KEY_ITEM = "key_item";

    private Item itemObject;
    View mainView;

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

//    @Override
//    public void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        if (getArguments() != null) {
//            this.itemObject = (Item)getArguments().getSerializable(KEY_ITEM);
//        }
//    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        if (savedInstanceState != null) {
            itemObject = (Item) savedInstanceState.getSerializable(KEY_ITEM);
        }
        View view = inflater.inflate(R.layout.fragment_item_view, container, false);
        mainView = view;

        return view;
    }

//    @Override
//    public void onViewCreated(View view, Bundle savedInstanceState) {
//        super.onViewCreated(view, savedInstanceState);
//        if(itemObject != null) {
//            showItem(itemObject);
//        }
//    }

    @Override
    public void onStart() {
        super.onStart();

        Bundle args = getArguments();
        if (args != null) {
            // Set article based on argument passed in
            showItem((Item) args.getSerializable(KEY_ITEM));
        } else if (itemObject != null) {
            // Set article based on saved instance state defined during onCreateView
            showItem(itemObject);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable(KEY_ITEM, itemObject);
    }

    public void showItem(Item item) {
        itemObject = item;
        PhotoView photoView = (PhotoView)getActivity().findViewById(R.id.item_image);

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
