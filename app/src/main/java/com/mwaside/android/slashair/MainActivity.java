package com.mwaside.android.slashair;

import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;


public class MainActivity extends ActionBarActivity implements LoginFragment.OnFragmentInteractionListener, QuickToUpFragment.OnFragmentInteractionListener,
        LogOutFragment.OnFragmentInteractionListener, RecentTransactionsFragment.OnFragmentInteractionListener, BillingFragment.OnFragmentInteractionListener, NavigationDrawerFragment.NavigationDrawerCallbacks{

    static final public String END_POINT_URL = "http://www.slashair.com/";
    private CharSequence mTitle;

    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private NavigationDrawerFragment mNavigationDrawerFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        if (savedInstanceState == null) {
//            getSupportFragmentManager().beginTransaction()
//                    .add(R.id.container, new LoginFragment())
//                    .commit();
//        }

        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);
        mTitle = getTitle();

        // Set up the drawer.
        mNavigationDrawerFragment.setUp(
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout));
    }

    public void restoreActionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(mTitle);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!mNavigationDrawerFragment.isDrawerOpen()) {
            // Only show items in the action bar relevant to this screen
            // if the drawer is not showing. Otherwise, let the drawer
            // decide what to show in the action bar.
            getMenuInflater().inflate(R.menu.main, menu);
            restoreActionBar();
            return true;
        }
        return super.onCreateOptionsMenu(menu);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onLoginFragmentInteraction() {
        getSupportFragmentManager().beginTransaction().replace(R.id.container, new QuickToUpFragment()).commit();

    }

    @Override
    public void onQuickTopupFragmentInteraction(Uri uri) {

    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {
        // update the main content by replacing fragments
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.container, onSectionAttached(position + 1))
                .commit();
    }

    public Fragment login_required(Fragment fragment){
        SharedPreferences preferences;
        preferences = PreferenceManager.getDefaultSharedPreferences(this);

        if (preferences.getBoolean(SlashAirPreferenceActivity.KEY_LOGGED_IN, false)){
            return fragment;
        }
        return new LoginFragment();
    }
    public Fragment onSectionAttached(int number) {
        switch (number) {
            case 1:
                mTitle = getString(R.string.quick_topup_section);
                return login_required(new QuickToUpFragment());
            case 2:
                mTitle = getString(R.string.recent_transaction_section);
                return login_required(new RecentTransactionsFragment());
            case 3:
                mTitle = getString(R.string.balance_section);
                return login_required(new BillingFragment());
            default:
                mTitle = getString(R.string.logout_section);
                return new LogOutFragment();
        }
    }

    @Override
    public void onLogOutFragmentInteraction(Uri uri) {

    }

    @Override
    public void onRecentTransactionFragmentInteraction(Uri uri) {

    }

    @Override
    public void onBillingFragmentInteraction(Uri uri) {

    }
}
