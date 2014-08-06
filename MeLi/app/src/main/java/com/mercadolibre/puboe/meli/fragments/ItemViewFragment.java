package com.mercadolibre.puboe.meli.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.mercadolibre.puboe.meli.tracker.ItemDAO;
import com.mercadolibre.puboe.meli.R;
import com.mercadolibre.puboe.meli.model.Item;
import com.mercadolibre.puboe.meli.sqlite.ItemDAOImpl;
import com.squareup.picasso.Picasso;

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

    public static ItemViewFragment newInstance() {
        ItemViewFragment fragment = new ItemViewFragment();
        return fragment;
    }
    public ItemViewFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        if (savedInstanceState != null) {
            Item item = (Item) savedInstanceState.getSerializable(KEY_ITEM);
            if(item != null)
                itemObject = item;
        }
        View view = inflater.inflate(R.layout.fragment_item_view, container, false);
        mainView = view;

        return view;
    }

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
        if(itemObject != null)
            outState.putSerializable(KEY_ITEM, itemObject);
    }

    public void showItem(final Item item) {
        itemObject = item;
        ImageView imageView = (ImageView)mainView.findViewById(R.id.item_image);

        if(item.getPictures() != null && !item.getPictures().isEmpty()) {
            String url = item.getPictures().get(0).getUrl();
            if (url != null && !url.isEmpty()) {
                Picasso.with(getActivity()).load(url).into(imageView);
            } else {
                imageView.setImageResource(R.drawable.imagequeued);
            }
        } else {
            imageView.setImageResource(R.drawable.imagequeued);
        }

        TextView title = (TextView)mainView.findViewById(R.id.item_title);
        title.setText(item.getTitle());
        TextView price = (TextView)mainView.findViewById(R.id.item_price);
        price.setText("Precio: $" + item.getPrice());
        TextView condition = (TextView)mainView.findViewById(R.id.item_condition);
        condition.setText("Articulo " + (item.getCondition().equals("new")?"nuevo":"usado"));
        TextView available = (TextView)mainView.findViewById(R.id.item_available_quantity);
        available.setText(item.getAvailableQuantity() + " diponibles");

        final ItemDAO itemDao = ItemDAOImpl.getInstance(getActivity().getApplicationContext());

        final Button track = (Button) mainView.findViewById(R.id.track_button);
        final Button untrack = (Button) mainView.findViewById(R.id.untrack_button);

        View.OnClickListener l = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch(view.getId()) {
                    case R.id.track_button:
                        itemDao.saveItem(item);
                        track.setVisibility(View.GONE);
                        untrack.setVisibility(View.VISIBLE);
                        break;

                    case R.id.untrack_button:
                        itemDao.deleteItem(item);
                        untrack.setVisibility(View.GONE);
                        track.setVisibility(View.VISIBLE);
                        break;
                }
            }
        };
        track.setOnClickListener(l);
        untrack.setOnClickListener(l);

        if(itemDao.exists(item.getId())) {
            untrack.setVisibility(View.VISIBLE);
        } else {
            track.setVisibility(View.VISIBLE);
        }
//        getActivity().setTitle(item.getTitle());
    }


}
