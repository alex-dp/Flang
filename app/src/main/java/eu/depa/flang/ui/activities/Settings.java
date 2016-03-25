package eu.depa.flang.ui.activities;

import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.widget.ListView;
import android.widget.Toast;

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

    static public class PrefsFragment extends PreferenceFragment implements Preference.OnPreferenceChangeListener {
        
        public PrefsFragment(){}
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.settings);

            Preference interval = findPreference("interval");
            interval.setOnPreferenceChangeListener(this);
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

        @Override
        public void onActivityCreated(Bundle savedInstanceState) {
            super.onActivityCreated(savedInstanceState);
            ListView lv = (ListView) getView().findViewById(android.R.id.list);
            if (lv != null) lv.setPadding(0, 0, 0, 0);
        }
    }
}