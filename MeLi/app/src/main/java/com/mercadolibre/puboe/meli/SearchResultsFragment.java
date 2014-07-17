package com.mercadolibre.puboe.meli;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;


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

    private Search searchObject;
    SearchAdapter adapter;
    ListView listview;

    private OnFragmentInteractionListener mListener;

    public static SearchResultsFragment newInstance() {
        SearchResultsFragment fragment = new SearchResultsFragment();
        Bundle args = new Bundle();
//        args.putString(ARG_PARAM1, param1);
//        args.putString(ARG_PARAM2, param2);
//        fragment.setArguments(args);
        return fragment;
    }
    public SearchResultsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//        if (getArguments() != null) {
//            mParam1 = getArguments().getString(ARG_PARAM1);
//            mParam2 = getArguments().getString(ARG_PARAM2);
//        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.i("SearchResultsFragemnt", "onCreateView");

        View mainView = inflater.inflate(R.layout.fragment_search_results, container, false);
        listview = (ListView) mainView.findViewById(android.R.id.list);
        listview.setOnScrollListener(new mOnScrollListener());

        return mainView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.i("SearchResultsFragemnt", "onViewCreated with saved state: " + (savedInstanceState != null) + " and searchObject: " + (searchObject != null) + " and adapter: " + (adapter != null));
        if(savedInstanceState != null) {
            showResults((Search) savedInstanceState.getSerializable(KEY_DATA));
        } else if (searchObject != null){
            showResults();
        }
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
    public void onSaveInstanceState(Bundle outState) {
        Log.i("SearchResultsFragment", "onSaveInstanceState");
        outState.putSerializable(KEY_DATA, searchObject);
        super.onSaveInstanceState(outState);
    }

    public void showResults() {
        if (listview.getAdapter() == null)
            listview.setAdapter(adapter);

        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Item item = (Item)listview.getItemAtPosition(position);
                mListener.onItemSelected(item.getId());
            }
        });
    }

    public void showResults(Search results) {

        if (adapter == null && searchObject == null) {
            searchObject = results;
            adapter = new SearchAdapter(getActivity(), searchObject);
            listview.setAdapter(adapter);

        } else {
            searchObject.getResults().addAll(results.getResults());
            searchObject.setPaging(results.getPaging());
            adapter.notifyDataSetChanged();

            if (listview.getAdapter() == null)
                listview.setAdapter(adapter);
        }
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Item item = (Item)listview.getItemAtPosition(position);
                mListener.onItemSelected(item.getId());
            }
        });
    }

    public interface OnFragmentInteractionListener {

        public void onItemSelected(String id);

        public void onRequestMoreItems();

    }

    private class mOnScrollListener implements AbsListView.OnScrollListener {

        @Override
        public void onScrollStateChanged(AbsListView absListView, int i) {
        }

        @Override
        public void onScroll(AbsListView absListView, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
            if (getSearchObject() == null) {
                Log.w("onScroll", "searchObject == null");
                return;
            }

            if (firstVisibleItem + visibleItemCount == totalItemCount && totalItemCount > getSearchObject().getPaging().getOffset()) {
                Log.w("onScroll", "firstVisible: " + firstVisibleItem + ", visibleCount:" + visibleItemCount + ", totalCount: " + totalItemCount);
                mListener.onRequestMoreItems();
            }
        }
    }

        public Search getSearchObject() {
        return searchObject;
    }
}
