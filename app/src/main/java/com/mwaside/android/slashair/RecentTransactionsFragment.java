package com.mwaside.android.slashair;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link RecentTransactionsFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 */
public class RecentTransactionsFragment extends Fragment {

    private OnFragmentInteractionListener mListener;
    private ArrayAdapter<String> recent_transaction_adapter;
    private String Tag = "RecentTransactionFragment";
    public RecentTransactionsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root_view = inflater.inflate(R.layout.fragment_recent_transactions, container, false);

        //initialize adapter
        recent_transaction_adapter = new ArrayAdapter<String>(
                getActivity().getApplicationContext(),
                R.layout.list_recent_transaction,
                R.id.list_recent_transaction_textview,
                new ServerEndpoint(getActivity().getApplicationContext()).get_recent_transaction()
        );

        ListView listView = (ListView) root_view.findViewById(R.id.listview_recent_transaction);
        listView.setAdapter(recent_transaction_adapter);

//        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                String item = mFocastAdapter.getItem(position);
//                Toast.makeText(getActivity(), item, Toast.LENGTH_SHORT).show();
//            }
//        });
        return root_view;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
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
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onRecentTransactionFragmentInteraction(Uri uri);
    }

}
