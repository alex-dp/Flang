package eu.depa.flang.ui.activities;

import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.widget.ListView;
import android.widget.Toast;

import eu.depa.flang.BuildConfig;
import eu.depa.flang.Constants;
import eu.depa.flang.R;

public class Settings extends BaseActivity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setTitle(R.string.settings);
        
        getFragmentManager().beginTransaction().replace(android.R.id.content,
                new PrefsFragment()).commit();
    }

    static public class PrefsFragment extends PreferenceFragment implements
            Preference.OnPreferenceChangeListener,
            Preference.OnPreferenceClickListener {
        
        public PrefsFragment(){}
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.settings);

            Preference interval = findPreference("interval"),
                    version = findPreference("vers");
            interval.setOnPreferenceChangeListener(this);
            version.setOnPreferenceClickListener(this);
            version.setSummary(BuildConfig.VERSION_NAME);
        }

        @Override
        public boolean onPreferenceChange(Preference preference, Object newValue) {

            switch (preference.getKey()) {
                case "interval":
                    Constants.resetAlarm(getActivity().getApplicationContext());
                    Toast.makeText(getActivity(), "I worked. sort of.", Toast.LENGTH_SHORT).show();
            }
            return true;
        }

        int clicks = 0;

        @Override
        public boolean onPreferenceClick(Preference preference) {
            clicks++;
            if (clicks == 5) {
                Toast.makeText(getActivity(), R.string.stop_clicking, Toast.LENGTH_SHORT).show();
                clicks = 0;
            }
            return true;
        }

        @Override
        public void onActivityCreated(Bundle savedInstanceState) {
            super.onActivityCreated(savedInstanceState);
            ListView lv = null;
            if (getView() != null)
                lv = (ListView) getView().findViewById(android.R.id.list);
            if (lv != null) lv.setPadding(0, 0, 0, 0);
        }
    }
}