package com.arunkr.saavn.downloader.activity_frag;

import android.content.SharedPreferences;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.TwoStatePreference;

import com.arunkr.saavn.downloader.R;

/**
 * Created by Arun Kumar Shreevastava on 12/12/16.
 */

public class MyPreferenceFragment  extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener
{
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.preferences);

        {
            Preference pref = findPreference("preference_save_location");
            pref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference arg0)
                {
                    FolderChooserDialog fragment = new FolderChooserDialog();
                    fragment.show(getFragmentManager(), "FOLDER_FRAGMENT");
                    return true;
                }
            });
        }
    }

    public void onResume()
    {
        super.onResume();
        // prevent fragment being transparent
        // note, setting color here only seems to affect the "main" preference fragment screen, and not sub-screens
        // note, on Galaxy Nexus Android 4.3 this sets to black rather than the dark grey that the background theme should be (and what the sub-screens use); works okay on Nexus 7 Android 5
        // we used to use a light theme for the PreferenceFragment, but mixing themes in same activity seems to cause problems (e.g., for EditTextPreference colors)
        TypedArray array = getActivity().getTheme().obtainStyledAttributes(new int[] {
                android.R.attr.colorBackground
        });
        int backgroundColor = array.getColor(0, Color.BLACK);
		/*if( MyDebug.LOG ) {
			int r = (backgroundColor >> 16) & 0xFF;
			int g = (backgroundColor >> 8) & 0xFF;
			int b = (backgroundColor >> 0) & 0xFF;
			Log.d(TAG, "backgroundColor: " + r + " , " + g + " , " + b);
		}*/
        getView().setBackgroundColor(backgroundColor);
        array.recycle();

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this.getActivity());
        sharedPreferences.registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key)
    {
        Preference pref = findPreference(key);
        if( pref instanceof TwoStatePreference){
            TwoStatePreference twoStatePref = (TwoStatePreference)pref;
            twoStatePref.setChecked(sharedPreferences.getBoolean(key, true));
        }
    }
}
