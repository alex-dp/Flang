package eu.depa.flang;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.List;

public class THistoryByDate extends AppCompatActivity {

    static List<String> grades, dates;

    public static String average() {
        double sum = 0.0;
        DecimalFormat df = new DecimalFormat("#.##");
        for (String s : grades)
            try {
                sum += Integer.parseInt(s);
            } catch (Exception e) {
                e.printStackTrace();
            }
        return String.valueOf(df.format(sum / grades.size()));
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.t_history_by_date);

        if (getSupportActionBar() != null) getSupportActionBar().hide();

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        dates = Arrays.asList(prefs.getString("dates", "").replaceFirst(";;", "").split(";;"));
        grades = Arrays.asList(prefs.getString("grades", "").replaceFirst(";;", "").split(";;"));
        ListView list = (ListView) findViewById(R.id.test_history_list);

        if (grades.isEmpty() || (grades.size() == 1 && grades.get(0).equals(""))) {
            LinearLayout g = (LinearLayout) findViewById(R.id.no_tests_group);
            g.setVisibility(View.VISIBLE);
        } else {

            RelativeLayout a = (RelativeLayout) findViewById(R.id.header_history);
            a.setVisibility(View.VISIBLE);
            ResultLine[] data = new ResultLine[dates.size()];

            for (int i = 0; i < data.length; i++)
                data[i] = new ResultLine(dates.get(i),
                        grades.get(i),
                        goodOrBad(grades.get(i)));

            ResultListAdapter adapter = new ResultListAdapter(this, R.layout.results_list_row, data);
            list.setAdapter(adapter);

            TextView avg = (TextView) findViewById(R.id.average);
            avg.setText(average());
        }
    }

    private Boolean goodOrBad(String s) {
        if (!s.equals(""))
            return Integer.parseInt(s) >= 6;
        return null;
    }

    public void gotoTest(View view) {
        startActivity(new Intent(this, Test.class));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        onCreate(null);
    }
}
