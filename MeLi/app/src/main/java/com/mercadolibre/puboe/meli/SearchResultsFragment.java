package com.mercadolibre.puboe.meli;

import android.app.Activity;
import android.app.ListFragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ListView;

import com.mercadolibre.puboe.meli.model.Item;
import com.mercadolibre.puboe.meli.model.Search;


/**
 * Activities that contain this fragment must implement the
 * {@link SearchResultsFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link SearchResultsFragment#newInstance} factory method to
 * create an instance of this fragment.
 *
 */
public class SearchResultsFragment extends ListFragment {

    public static final String KEY_DATA = "key_data";

//    private Search searchObject;
    private SearchAdapter adapter;
    private ListView listView;
    private View mainView;

    private OnFragmentInteractionListener mListener;

    public static SearchResultsFragment newInstance() {
        SearchResultsFragment fragment = new SearchResultsFragment();
//        Bundle args = new Bundle();
//        args.putSerializable(KEY_DATA, search);
//        fragment.setArguments(args);
        return fragment;
    }

    public SearchResultsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.i("SearchResultsFragemnt", "onCreateView");

        mainView = inflater.inflate(R.layout.fragment_search_results, container, false);
        return mainView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        listView = (ListView) mainView.findViewById(android.R.id.list);
        listView.setOnScrollListener(new mOnScrollListener());

        Log.i("SearchResultsFragemnt", "onViewCreated with saved state: " + (savedInstanceState != null) + " and searchObject: " + (mListener.getSearchObjectFromActivity() != null) + " and adapter: " + (adapter != null));
//        System.out.println("Not first: " + savedInstanceState.getBoolean("not_first"));
        if(savedInstanceState != null && savedInstanceState.getBoolean("not_first")) {
            showResults(mListener.getSearchObjectFromActivity());
        }
//        if(searchObject == null && savedInstanceState != null) {
//            Search search = (Search) savedInstanceState.getSerializable(KEY_DATA);
//            if(search != null)
//                showResults(search);
//        } else if (searchObject != null){
//            showResults(searchObject);
//        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        Log.i("SearchResultsFragment", "onAttach");
        try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        Log.i("SearchResultsFragment", "dettaching fragment");
        mListener = null;
    }

    @Override
    public void onStart() {
        super.onStart();
        Bundle args = getArguments();
//        if (args != null) {
//            showResults((Search)args.getSerializable(KEY_DATA));
//        } else if (searchObject != null) {
//            showResults(searchObject);
//        }
        // When in two-pane layout, set the listview to highlight the selected list item
        // (We do this during onStart because at the point the listview is available.)
        if (getFragmentManager().findFragmentById(R.id.list_fragment) != null) {
            listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        Log.i("SearchResultsFragment", "onSaveInstanceState");
        outState.putBoolean("not_first", true);
//        outState.putSerializable(KEY_DATA, searchObject);
        super.onSaveInstanceState(outState);
    }

    public void showResults(Search results) {
//        searchObject = results;

        if (adapter == null) {
            adapter = new SearchAdapter(getActivity(), results);
            setListAdapter(adapter);
        } else if(adapter != null && getListAdapter() != null){
            adapter.notifyDataSetChanged();
        } else {
            setListAdapter(adapter);
        }
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        Item item = (Item)l.getItemAtPosition(position);
        mListener.onItemSelected(item.getId());
        getListView().setItemChecked(position, true);
    }

    public interface OnFragmentInteractionListener {

        public void onItemSelected(String id);

        public void onRequestMoreItems();

        public Search getSearchObjectFromActivity();

    }

    private class mOnScrollListener implements AbsListView.OnScrollListener {

        @Override
        public void onScrollStateChanged(AbsListView absListView, int i) {
        }

        @Override
        public void onScroll(AbsListView absListView, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
            if (mListener.getSearchObjectFromActivity() == null) {
                Log.w("onScroll", "searchObject == null");
                return;
            }

            if (firstVisibleItem + visibleItemCount == totalItemCount && totalItemCount > mListener.getSearchObjectFromActivity().getPaging().getOffset()) {
                Log.w("onScroll", "firstVisible: " + firstVisibleItem + ", visibleCount:" + visibleItemCount + ", totalCount: " + totalItemCount);
                mListener.onRequestMoreItems();
            }
        }
    }

//        public Search getSearchObject() {
//        return searchObject;
//    }
}
