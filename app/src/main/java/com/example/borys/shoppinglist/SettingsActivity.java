package com.example.borys.shoppinglist;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;

public class SettingsActivity extends PreferenceActivity {

    public static final String KEY_PREF = "bg_change";
    private String preferencesName = "";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        preferencesName = getIntent().getExtras().getString(MainActivity.EXTRA_PREFERENCES_NAME);
        getPreferenceManager().setSharedPreferencesName(preferencesName);
        addPreferencesFromResource(R.xml.preferences);

        final CheckBoxPreference checkboxPref = (CheckBoxPreference) getPreferenceManager().findPreference("bg_change");

        checkboxPref.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                SharedPreferences sharedPref = getSharedPreferences(preferencesName, Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putBoolean(KEY_PREF, (Boolean) newValue);
                editor.commit();
                
                return true;
            }
        });
    }
}
