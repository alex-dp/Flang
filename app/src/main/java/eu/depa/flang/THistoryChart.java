package eu.depa.flang;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.DataPointInterface;
import com.jjoe64.graphview.series.LineGraphSeries;
import com.jjoe64.graphview.series.OnDataPointTapListener;
import com.jjoe64.graphview.series.Series;

import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.List;

public class THistoryChart extends AppCompatActivity implements OnDataPointTapListener {

    public static String average(List<String> grades) {
        double sum = 0.0;
        DecimalFormat df = new DecimalFormat("#.##");
        for (String s : grades)
            sum += Integer.parseInt(s);
        return String.valueOf(df.format(sum / grades.size()));
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.t_history_chart);

        if (getSupportActionBar() != null) getSupportActionBar().hide();

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        List<String> grades = Arrays.asList(prefs.getString("grades", "").replaceFirst(";;", "").split(";;"));

        try {
            grades = grades.subList(grades.size() - 20, grades.size());
        } catch (Exception e) {
            e.printStackTrace();
        }
        final GraphView graph = (GraphView) findViewById(R.id.graph);

        if (grades.isEmpty() || (grades.size() == 1 && grades.get(0).equals(""))) {
            LinearLayout g = (LinearLayout) findViewById(R.id.no_tests_group);
            g.setVisibility(View.VISIBLE);
            graph.setVisibility(View.GONE);
        } else {
            DataPoint[] data = new DataPoint[grades.size()];
            for (int i = 0; i < data.length; i++)
                data[i] = new DataPoint(i, Integer.valueOf(grades.get(i)));
            LineGraphSeries<DataPoint> series = new LineGraphSeries<>(data);
            //noinspection deprecation
            series.setColor(getResources().getColor(R.color.colorAccent));
            series.setDrawDataPoints(true);
            graph.addSeries(series);

            LineGraphSeries<DataPoint> avg = new LineGraphSeries<>(new DataPoint[]{
                    new DataPoint(0, Double.valueOf(average(grades))),
                    new DataPoint(series.getHighestValueX(), Double.valueOf(average(grades)))
            });

            avg.setTitle(getString(R.string.average));
            //noinspection deprecation
            avg.setColor(getResources().getColor
                    (Double.valueOf(average(grades)) >= 6 ?
                            R.color.pale_green : R.color.pale_red));
            graph.addSeries(avg);
            series.setOnDataPointTapListener(this);
        }
    }

    @Override
    public void onTap(Series series, DataPointInterface dataPointInterface) {
        RelativeLayout mom = (RelativeLayout) findViewById(R.id.chart_mom);
        Toast toast = Toast.makeText(
                THistoryChart.this,
                String.valueOf(dataPointInterface.getY()),
                Toast.LENGTH_SHORT);
        double Y = dataPointInterface.getY();
        toast.setGravity(Gravity.BOTTOM, 1000, (int) ((mom.getMeasuredHeight() - 176) / 10 * Y) + 48);
        toast.show();
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