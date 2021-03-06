package com.mwaside.android.slashair;

import android.app.Activity;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link BillingFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 */
public class BillingFragment extends Fragment {

    private OnFragmentInteractionListener mListener;
    TextView how_to_topup, balance_view;
    public BillingFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root_view = inflater.inflate(R.layout.fragment_billing, container, false);

        balance_view = (TextView) root_view.findViewById(R.id.show_balance);
        how_to_topup = (TextView) root_view.findViewById(R.id.how_to_bill);

        new FindBalance().execute();
        how_to_topup.setText(new ServerEndpoint(getActivity().getApplicationContext()).how_to_topup());


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
        public void onBillingFragmentInteraction(Uri uri);
    }

    public class FindBalance extends AsyncTask<Void, Void, String>{

        @Override
        protected String doInBackground(Void... voids) {
            return new ServerEndpoint(getActivity().getApplicationContext()).get_balance();
        }

        protected void onPostExecute(final String balance){
            balance_view.setText(balance);
        }
    }

}
