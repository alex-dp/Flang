package eu.depa.flang.ui.fragments;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.List;

import eu.depa.flang.Constants;
import eu.depa.flang.R;
import eu.depa.flang.ResultLine;
import eu.depa.flang.adapters.ResultListAdapter;
import eu.depa.flang.ui.activities.Test;

public class THistoryByDate extends android.support.v4.app.Fragment implements View.OnClickListener {

    private static List<String> grades;

    private static String average() {
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

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.t_history_by_date, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getContext());
        List<String> dates = Arrays.asList(prefs.getString("dates", "").replaceFirst(";;", "").split(";;"));
        grades = Arrays.asList(prefs.getString("grades", "").replaceFirst(";;", "").split(";;"));
        ListView list = null;
        if (getView() != null)
            list = (ListView) getView().findViewById(R.id.test_history_list);

        Button test = null;
        if (getView() != null)
            test = (Button) getView().findViewById(R.id.test_hbd);
        if (test != null)
            test.setOnClickListener(this);

        if (grades.isEmpty() || (grades.size() == 1 && grades.get(0).equals(""))) {
            LinearLayout g = (LinearLayout) getView().findViewById(R.id.no_tests_group);
            g.setVisibility(View.VISIBLE);

            if ((!Constants.isNetworkAvailable(getContext()) || prefs.getInt("learned", 0) < 3) && test != null)
                test.setVisibility(View.GONE);
        } else {

            RelativeLayout a = (RelativeLayout) getView().findViewById(R.id.header_history);
            a.setVisibility(View.VISIBLE);
            ResultLine[] data = new ResultLine[dates.size()];

            for (int i = 0; i < data.length; i++)
                data[i] = new ResultLine(dates.get(i),
                        grades.get(i),
                        goodOrBad(grades.get(i)));

            ResultListAdapter adapter = new ResultListAdapter(getContext(), R.layout.results_list_row, data);
            if (list != null)
                list.setAdapter(adapter);

            TextView avg = (TextView) getView().findViewById(R.id.average);
            avg.setText(average());
        }
    }

    private Boolean goodOrBad(String s) {
        if (!s.equals(""))
            return Integer.parseInt(s) >= 6;
        return null;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        onCreate(null);
    }

    @Override
    public void onClick(View v) {
        startActivity(new Intent(getContext(), Test.class));
    }
}
