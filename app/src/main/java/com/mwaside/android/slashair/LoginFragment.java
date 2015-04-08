package com.mwaside.android.slashair;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.android.slashair.SlashAirPreferenceActivity;
import com.mwaside.android.slashair.R;

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
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import static com.mwaside.android.slashair.MainActivity.END_POINT_URL;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link LoginFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 */
public class LoginFragment extends Fragment {

    private OnFragmentInteractionListener mListener;
    private EditText mUsernameView, mPasswordView;
    private View mProgressView,  mLoginFormView;
    private String TAG = "Login_fragment";

    public LoginFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View fragment = inflater.inflate(R.layout.fragment_login, container, false);
        mUsernameView = (EditText) fragment.findViewById(R.id.login_username);
        mPasswordView = (EditText) fragment.findViewById(R.id.login_password);
        mProgressView = fragment.findViewById(R.id.login_progress);
        mLoginFormView = fragment.findViewById(R.id.login_form);

        Button mLogInButton = (Button)fragment.findViewById(R.id.log_in_button);
        mLogInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i(TAG, "login button clicked");
                attemptLogin();
            }
        });
        return fragment;
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
        public void onLoginFragmentInteraction();
    }


    /**
     * Attempts to sign in using credentials specified by the login form.
     * If there are form errors (invalid username, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    public void attemptLogin() {
        // Reset errors.
        mUsernameView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        String username = mUsernameView.getText().toString().trim();
        String password = mPasswordView.getText().toString().trim();

        boolean cancel = false;
        View focusView = null;

        if (TextUtils.isEmpty(password)) {
            mPasswordView.setError(getString(R.string.error_field_required));
            focusView = mPasswordView;
            cancel = true;
        }

        if (TextUtils.isEmpty(username)) {
            mUsernameView.setError(getString(R.string.error_field_required));
            focusView = mUsernameView;
            cancel = true;
        }
        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
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
                new UserLoginTask(username, password).execute((Void) null);
            }
        }
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
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
            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    public class UserLoginTask extends AsyncTask<Void, Void, Boolean> {

        private final String mUsername;
        private final String mPassword;

        UserLoginTask(String username, String password) {
            mUsername = username;
            mPassword = password;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            try {
                SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext());
                String authenticationUrl = END_POINT_URL + "api_v1/token-auth/";
                Log.i(TAG, "authentication url: "+authenticationUrl);
                // Create a new HttpClient and POST header
                HttpClient httpClient = new DefaultHttpClient();
                HttpPost httpPost = new HttpPost(authenticationUrl);
                // Add POST data - username & password
                // Dangerous! Should only be done over https
                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
                nameValuePairs.add(new BasicNameValuePair("username", mUsername));
                nameValuePairs.add(new BasicNameValuePair("password", mPassword));
                httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
                ResponseHandler<String> responseHandler = new BasicResponseHandler();
                // Execute HTTP Post Request
                HttpResponse response;
                Log.i(TAG, "http posting authentication");
                response = httpClient.execute(httpPost);
                Log.i(TAG, "response for authentication:" + response);

                //get response status code
                int status_code = response.getStatusLine().getStatusCode();
                Log.i(TAG, "response status code:" + status_code);

                // get response content
                HttpEntity entity = response.getEntity();
                String response_content = EntityUtils.toString(entity);
                Log.i(TAG, "response content: " + response_content);

                //if response status code is not 200 the credentials are invalid
                if (response.getStatusLine().getStatusCode() != 200) {
                    return false;
                }

                // Parse response content
                JSONObject jsonObject = new JSONObject(response_content);

                // save auth token sent to preference activity
                SharedPreferences.Editor editor = settings.edit();
                editor.putString(SlashAirPreferenceActivity.AUTH_TOKEN, jsonObject.getString("token"));
                editor.commit();

                Log.i(TAG, "authentication was successful");
                return true;
            } catch (JSONException e) {
                e.printStackTrace();
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

            if (success) {
                SharedPreferences mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext());
                SharedPreferences.Editor editor = mSharedPreferences.edit();
                editor.putBoolean(SlashAirPreferenceActivity.KEY_LOGGED_IN, true);
                editor.commit();

                Log.i(TAG, "Done");
                mListener.onLoginFragmentInteraction();
            } else {
                mUsernameView.setError(getString(R.string.error_invalid_username_or_password));
                mPasswordView.setError(getString(R.string.error_invalid_username_or_password));
            }
        }

        @Override
        protected void onCancelled() {
            showProgress(false);
        }
    }

}
