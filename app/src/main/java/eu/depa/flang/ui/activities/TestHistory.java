package eu.depa.flang.ui.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.MenuItem;
import android.view.View;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import eu.depa.flang.Constants;
import eu.depa.flang.R;
import eu.depa.flang.ui.fragments.THistoryByDate;
import eu.depa.flang.ui.fragments.THistoryChart;

public class TestHistory extends TestableActivity {

    private static String average(List<String> pGrades) {
        double sum = 0.0;
        DecimalFormat df = new DecimalFormat("#.##");
        for (String s : pGrades)
            sum += Integer.parseInt(s);
        return String.valueOf(df.format(sum / pGrades.size()));
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.test_history);
        setTitle(R.string.test_history);

        ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager);
        setupViewPager(viewPager);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);

        if (getSupportActionBar() != null) getSupportActionBar().setElevation(0);
    }

    private void setupViewPager(ViewPager viewPager) {
        THAdapter adapter = new THAdapter(getSupportFragmentManager());
        adapter.addFragment(new THistoryByDate(), getString(R.string.by_date));
        adapter.addFragment(new THistoryChart(), getString(R.string.chart));
        viewPager.setAdapter(adapter);
    }

    public void share(MenuItem item) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        List<String> grades = Arrays.asList(prefs.getString("grades", "").replaceFirst(";;", "").split(";;"));
        Intent share = new Intent(Intent.ACTION_SEND);
        share.putExtra(Intent.EXTRA_TEXT, getString(R.string.my_average_test_score_in) + " " +
                Constants.getLangsArr(this)[prefs.getInt("to", 1)] + " " +
                getString(R.string.is) + " " +
                average(grades) + "!");
        share.setType("text/plain");
        startActivity(Intent.createChooser(share, getString(R.string.chooser)));
    }

    public void gotoTest(View view) {
        Constants.gotoTest(this);
    }

    class THAdapter extends FragmentPagerAdapter {
        private final List<android.support.v4.app.Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public THAdapter(android.support.v4.app.FragmentManager manager) {
            super(manager);
        }

        @Override
        public android.support.v4.app.Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(android.support.v4.app.Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }
}
