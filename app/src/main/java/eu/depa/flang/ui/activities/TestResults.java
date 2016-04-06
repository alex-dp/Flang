package eu.depa.flang.ui.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.TextView;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;

import eu.depa.flang.Constants;
import eu.depa.flang.R;
import eu.depa.flang.ResultLine;
import eu.depa.flang.adapters.ResultListAdapter;

public class TestResults extends SharableActivity {

    private boolean[] correct;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.test_results);
        setTitle(R.string.test_results);

        ArrayList<String> from = getIntent().getStringArrayListExtra("from");
        ArrayList<String> to = getIntent().getStringArrayListExtra("to");
        correct = getIntent().getBooleanArrayExtra("correct");
        ListView list = (ListView) findViewById(R.id.result_list);

        ResultLine[] data = new ResultLine[10];

        for (int i = 0; i < 10; i++)
            data[i] = new ResultLine(from.get(i), to.get(i), correct[i]);

        ResultListAdapter adapter = new ResultListAdapter(this, R.layout.results_list_row, data);

        list.setAdapter(adapter);

        TextView score = (TextView) findViewById(R.id.score_placeholder);
        score.setText(String.valueOf(sum(correct)));
    }

    private int sum(boolean[] pCorrect) {
        int tmp = 0;
        for (boolean bool : pCorrect)
            if (bool) tmp++;
        return tmp;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        new Thread(new Runnable() {
            @Override
            public void run() {
                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                String dates = prefs.getString("dates", "");
                String grades = prefs.getString("grades", "");
                DateFormat df = DateFormat.getDateInstance();
                dates += ";;" + String.valueOf(df.format(new Date()));
                grades += ";;" + String.valueOf(sum(correct));
                prefs.edit().putString("dates", dates).apply();
                prefs.edit().putString("grades", grades).apply();
            }
        }).start();
    }

    public void share(MenuItem item) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        Intent share = new Intent(Intent.ACTION_SEND);
        String opt = getString(R.string.test_in_phrase);
        share.putExtra(Intent.EXTRA_TEXT, getString(R.string.i_scored) + " " +
            sum(correct) + " " +
            getString(R.string.in_a) + " " +
                Constants.getLangsArr(this)[prefs.getInt("to", 1)] +
                (opt.equals("null") ? "" : " " + opt) + "!");
        share.setType("text/plain");
        startActivity(Intent.createChooser(share, getString(R.string.chooser)));
    }
}
