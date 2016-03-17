package eu.depa.flang;

import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.widget.ListView;

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