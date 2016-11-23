package com.example.borys.shoppinglist;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;

public class SettingsActivity extends PreferenceActivity {

    public static final String KEY_PREF = "bg_change";
    public static final String KEY_LIST_PREF = "list_pref";
    private String preferencesName = "";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        preferencesName = getIntent().getExtras().getString(MainActivity.EXTRA_PREFERENCES_NAME);
        getPreferenceManager().setSharedPreferencesName(preferencesName);
        addPreferencesFromResource(R.xml.preferences);

        final CheckBoxPreference checkboxPref = (CheckBoxPreference) getPreferenceManager().findPreference(KEY_PREF);
        final ListPreference listPref = (ListPreference) getPreferenceManager().findPreference(KEY_LIST_PREF);

        checkboxPref.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                SharedPreferences sharedPref = getSharedPreferences(preferencesName, Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putBoolean(KEY_PREF, (Boolean) newValue);
                editor.commit();

                Intent in = new Intent(SettingsActivity.this,MainActivity.class);
                setResult(Activity.RESULT_OK,in);

                return true;
            }
        });
        listPref.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                SharedPreferences sharedPref = getSharedPreferences(preferencesName, Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putString(KEY_LIST_PREF, (String) newValue);
                editor.commit();

                Intent in = new Intent(SettingsActivity.this,MainActivity.class);
                setResult(Activity.RESULT_OK,in);

                return true;
            }
        });
    }
}
