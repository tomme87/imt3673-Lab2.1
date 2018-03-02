package no.ntnu.tomme87.imt3673.lab2_1;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceFragment;

/**
 * Created by Tomme on 02.03.2018.
 */

public class SettingsFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener {
    static final String RSS_URL = "pref_rss_url";
    static final String MAX_ITEMS = "pref_list_num";
    static final String FREQUENCY = "pref_list_freq";

    private String rssUrl;
    private String maxItems;
    private String frequency;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.preferences);

        setupPrefVars(getPreferenceScreen().getSharedPreferences());
    }

    private void setupPrefVars(SharedPreferences sharedPreferences) {
        rssUrl = sharedPreferences.getString(RSS_URL, null);
        maxItems = sharedPreferences.getString(MAX_ITEMS, getResources().getString(R.string.list_result_default));
        frequency = sharedPreferences.getString(FREQUENCY, getResources().getString(R.string.list_result_frequency_default));
    }

    @Override
    public void onResume() {
        super.onResume();
        getPreferenceScreen().getSharedPreferences()
                .registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        getPreferenceScreen().getSharedPreferences()
                .unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        setupPrefVars(sharedPreferences);
        Utils.setupScheduler(getContext());
    }
}
