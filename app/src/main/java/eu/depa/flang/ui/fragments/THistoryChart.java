package eu.depa.flang.ui.fragments;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.DataPointInterface;
import com.jjoe64.graphview.series.LineGraphSeries;
import com.jjoe64.graphview.series.OnDataPointTapListener;
import com.jjoe64.graphview.series.Series;

import java.util.Arrays;
import java.util.List;

import eu.depa.flang.R;

public class THistoryChart extends android.support.v4.app.Fragment implements OnDataPointTapListener {

    private static double average(List<String> grades) {
        double sum = 0.0;
        for (String s : grades)
            sum += Integer.parseInt(s);
        return sum / grades.size();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.t_history_chart, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getContext());
        List<String> grades = Arrays.asList(prefs.getString("grades", "").replaceFirst(";;", "").split(";;"));

        try {
            grades = grades.subList(grades.size() - 20, grades.size());
        } catch (Exception e) {
            e.printStackTrace();
        }
        final GraphView graph = (GraphView) getActivity().findViewById(R.id.graph);

        if ((grades.isEmpty() || (grades.size() == 1 && grades.get(0).equals(""))) && getView() != null) {
            getView().findViewById(R.id.no_tests_group).setVisibility(View.VISIBLE);
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
                    new DataPoint(0, average(grades)),
                    new DataPoint(series.getHighestValueX(), average(grades))
            });

            avg.setTitle(getString(R.string.average));
            //noinspection deprecation
            avg.setColor(getResources().getColor
                    (average(grades) >= 6 ?
                            R.color.pale_green : R.color.pale_red));
            graph.addSeries(avg);
            series.setOnDataPointTapListener(this);
        }
    }

    @Override
    public void onTap(Series series, DataPointInterface dataPointInterface) {
        RelativeLayout mom = null;
        if (getView() != null)
            mom = (RelativeLayout) getView().findViewById(R.id.chart_mom);
        Toast toast = Toast.makeText(
                getContext(),
                String.valueOf(dataPointInterface.getY()),
                Toast.LENGTH_SHORT);
        double Y = dataPointInterface.getY();
        if (mom != null)
            toast.setGravity(Gravity.BOTTOM, 1000, (int) ((mom.getMeasuredHeight() - 176) / 10 * Y) + 48);
        toast.show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        onCreate(null);
    }
}