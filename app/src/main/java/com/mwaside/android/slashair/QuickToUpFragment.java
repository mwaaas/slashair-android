package com.mwaside.android.slashair;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import static com.mwaside.android.slashair.MainActivity.END_POINT_URL;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link QuickToUpFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 */
public class QuickToUpFragment extends Fragment {

    private OnFragmentInteractionListener mListener;
    EditText phone_number_view, airtime_view;
    Button send_airtime_btn;
    String phone_number, airtime;
    private View mProgressView, mQuickTopFormView;
    final private String TAG = "QuickTopupFragment";


    public QuickToUpFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View fragment_view = inflater.inflate(R.layout.fragment_quick_to_up, container, false);

        // initialize input texts
        phone_number_view = (EditText) fragment_view.findViewById(R.id.phone_number);
        airtime_view = (EditText) fragment_view.findViewById(R.id.airtime);

        // initilize button
        send_airtime_btn = (Button) fragment_view.findViewById(R.id.send_airtime_button);

        mProgressView = fragment_view.findViewById(R.id.send_airtime_progress);
        mQuickTopFormView = fragment_view.findViewById(R.id.quick_top_up_form);

        send_airtime_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                send_airtime();
            }
        });

        return fragment_view;
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
        public void onQuickTopupFragmentInteraction(Uri uri);
    }

    private void send_airtime(){
        phone_number = phone_number_view.getText().toString().trim();
        airtime = airtime_view.getText().toString().trim();

        Pattern p = Pattern.compile("[1-9]\\d{2}-[1-9]\\d{2}-\\d{4}");
        // if the fields are empty
        if (TextUtils.isEmpty(phone_number)){
            phone_number_view.setError("This field is required");
            phone_number_view.requestFocus();
        }
        else if (TextUtils.isEmpty(airtime)){
            airtime_view.setError("This field is required");
            airtime_view.requestFocus();
        }
        else if (! (Patterns.PHONE.matcher(phone_number).matches())){
            phone_number_view.setError("Invalid phone number format");
            phone_number_view.requestFocus();
        }
        else if (! TextUtils.isDigitsOnly(airtime)){
            airtime_view.setError("airtime should be digits only");
            airtime_view.requestFocus();
        }
        else{
            // check for connectivity and alert if otherwise else
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            ConnectivityManager connectivityManager =
                    (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo ni = connectivityManager.getActiveNetworkInfo();
            if (ni == null || !ni.isConnected()) {
                Toast.makeText(getActivity(), R.string.no_connection, Toast.LENGTH_SHORT).show();
            } else {
                showProgress(true);
                new SendAirtimeTask().execute((Void) null);
            }

        }

    }
    public void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mQuickTopFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    public class SendAirtimeTask extends AsyncTask<Void, Void, Boolean> {

        @Override
        protected Boolean doInBackground(Void... params) {
            try {
                SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext());
                String send_airtime_endpoint = END_POINT_URL + "api_v1/send_airtime/";
                // Create a new HttpClient and POST header
                HttpClient httpClient = new DefaultHttpClient();
                HttpPost httpPost = new HttpPost(send_airtime_endpoint);
                // Add POST data -
                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(1);
                nameValuePairs.add(new BasicNameValuePair(phone_number, airtime));
                httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

                // get header
                SharedPreferences preferences= PreferenceManager.getDefaultSharedPreferences(getActivity());
                httpPost.addHeader("Authorization", "Token " + preferences.getString(SlashAirPreferenceActivity.AUTH_TOKEN, " "));

                ResponseHandler<String> responseHandler = new BasicResponseHandler();
                // Execute HTTP Post Request
                HttpResponse response;
                Log.i(TAG, "http posting send airtime the url is :" + send_airtime_endpoint);
                response = httpClient.execute(httpPost);
                Log.i(TAG, "response for authentication:" + response);

                //get response status code
                int status_code = response.getStatusLine().getStatusCode();
                Log.i(TAG, "response status code:" + status_code);

                // get response content
                HttpEntity entity = response.getEntity();
                String response_content = EntityUtils.toString(entity);
                Log.i(TAG, "response content: " + response_content);

                //if response status code is not 202 the credentials are invalid
                if (response.getStatusLine().getStatusCode() != 202) {
                    return false;
                }

                Log.i(TAG, "authentication was successful");
                return true;
            } catch (ClientProtocolException e) {
                e.printStackTrace();
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return false;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            showProgress(false);
        }

        @Override
        protected void onCancelled() {
            showProgress(false);
        }
    }

}
