package eu.depa.flang.ui.activities;

import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.text.InputType;
import android.view.View;
import android.widget.ListView;

import eu.depa.flang.BottomToast;
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

        int clicks = 0;
        
        public PrefsFragment(){}

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.settings);

            EditTextPreference interval = (EditTextPreference) findPreference("interval");
            Preference version = findPreference("vers");
            interval.setOnPreferenceChangeListener(this);
            interval.getEditText().setInputType(InputType.TYPE_CLASS_NUMBER);
            interval.getEditText().setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            version.setOnPreferenceClickListener(this);
            version.setSummary(BuildConfig.VERSION_NAME);

            new Thread(new Runnable() {
                @Override
                public void run() {
                    //noinspection InfiniteLoopStatement
                    while (true) {
                        if (clicks > 0)
                            clicks--;
                        try {
                            Thread.sleep(700);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }).start();
        }

        @Override
        public boolean onPreferenceChange(final Preference preference, final Object newValue) {

            switch (preference.getKey()) {
                case "interval":
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                Thread.sleep(1000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            if (!newValue.equals(""))
                                Constants.resetAlarm(getActivity().getApplicationContext());
                            else PreferenceManager
                                    .getDefaultSharedPreferences(getActivity())
                                    .edit()
                                    .putString("interval", "30")
                                    .apply();
                        }
                    }).start();
            }
            return true;
        }

        @Override
        public boolean onPreferenceClick(Preference preference) {
            clicks++;
            if (clicks == 5) {
                new BottomToast(getActivity(), R.string.stop_clicking).show();
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