package eu.depa.flang;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.util.Arrays;
import java.util.List;

public class TestHistory extends BaseActivity {

    List<String> grades, dates;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.test_history);
        setTitle(R.string.test_history);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        dates = Arrays.asList(prefs.getString("dates", "").replaceFirst(";;", "").split(";;"));
        grades = Arrays.asList(prefs.getString("grades", "").replaceFirst(";;", "").split(";;"));
        ListView list = (ListView) findViewById(R.id.test_history_list);

        if (grades.isEmpty() || (grades.size() == 1 && grades.get(0).length() == 0)) {
            LinearLayout g = (LinearLayout) findViewById(R.id.no_tests_group);
            g.setVisibility(View.VISIBLE);
        } else {
            ResultLine[] data = new ResultLine[dates.size()];

            for (int i = 0; i < data.length; i++)
                data[i] = new ResultLine(dates.get(i),
                        grades.get(i),
                        goodOrBad(grades.get(i)));

            ResultListAdapter adapter = new ResultListAdapter(this, R.layout.results_list_row, data);
            list.setAdapter(adapter);
        }

        TextView avg = (TextView) findViewById(R.id.average);
        avg.setText(average());
    }

    private Boolean goodOrBad(String s) {
        if (!s.equals(""))
            return Integer.parseInt(s) >= 6;
        return null;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.share_menu, menu);
        return true;
    }

    public String average() {
        int sum = 0;
        for (String s : grades)
            sum += Integer.parseInt(s);
        return String.valueOf(sum / grades.size());
    }

    public void share(MenuItem item) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        Intent share = new Intent(Intent.ACTION_SEND);
        share.putExtra(Intent.EXTRA_TEXT, getString(R.string.my_average_test_score_in) + " " +
                Constants.getLangsArr()[prefs.getInt("to", 1)] + " " +
                getString(R.string.is) + " " +
                average() + "!");
        share.setType("text/plain");
        startActivity(Intent.createChooser(share, getString(R.string.chooser)));
    }
}
