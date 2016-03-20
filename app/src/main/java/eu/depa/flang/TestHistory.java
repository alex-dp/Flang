package eu.depa.flang;

import android.app.LocalActivityManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.MenuItem;
import android.widget.TabHost;

import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.List;

public class TestHistory extends SharableActivity {

    LocalActivityManager LAM;

    public static String average(List<String> pGrades) {
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

        LAM = new LocalActivityManager(this, false);
        LAM.dispatchCreate(savedInstanceState);
        TabHost tabHost = (TabHost) findViewById(R.id.tabhost);
        tabHost.setup(LAM);

        TabHost.TabSpec spec1 = tabHost.newTabSpec("");
        spec1.setIndicator(getString(R.string.by_date), Constants.getDrawable(this, R.drawable.ic_menu_settings));
        Intent in1 = new Intent(this, THistoryByDate.class);
        spec1.setContent(in1);

        TabHost.TabSpec spec2 = tabHost.newTabSpec("");
        spec2.setIndicator(getString(R.string.chart), Constants.getDrawable(this, R.drawable.ic_menu_progress));
        Intent in2 = new Intent(this, THistoryChart.class);
        spec2.setContent(in2);

        tabHost.addTab(spec1);
        tabHost.addTab(spec2);
    }

    @Override
    protected void onResume() {
        super.onResume();
        LAM.dispatchResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        LAM.dispatchPause(isFinishing());
    }

    public void share(MenuItem item) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        List<String> grades = Arrays.asList(prefs.getString("grades", "").replaceFirst(";;", "").split(";;"));
        Intent share = new Intent(Intent.ACTION_SEND);
        share.putExtra(Intent.EXTRA_TEXT, getString(R.string.my_average_test_score_in) + " " +
                Constants.getLangsArr()[prefs.getInt("to", 1)] + " " +
                getString(R.string.is) + " " +
                average(grades) + "!");
        share.setType("text/plain");
        startActivity(Intent.createChooser(share, getString(R.string.chooser)));
    }
}
