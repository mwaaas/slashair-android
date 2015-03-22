package com.example.android.slashair;

import android.preference.Preference;
import android.preference.PreferenceActivity;

/**
 * Created by mwas on 3/22/15.
 */
public class SlashAirPreferenceActivity extends PreferenceActivity implements Preference.OnPreferenceChangeListener{
    public static final String KEY_LOGGED_IN = "loggedIn";
    public static final String KEY_USERNAME = "username";
    public static final String AUTH_TOKEN = "auth_token";

    @Override
    public boolean onPreferenceChange(Preference preference, Object o) {
        return false;
    }
}
