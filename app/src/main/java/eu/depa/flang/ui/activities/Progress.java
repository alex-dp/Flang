package eu.depa.flang.ui.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import eu.depa.flang.Constants;
import eu.depa.flang.R;

public class Progress extends SharableActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.progress);
        setTitle(R.string.progress);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);

        TextView count = (TextView) findViewById(R.id.count),
                word = (TextView) findViewById(R.id.word_or_words);
        int learnt = prefs.getInt("learned", 0);

        count.setText(String.valueOf(learnt));

        if (!Constants.isNetworkAvailable(this) || prefs.getInt("learned", 0) < 3) {
            Button test = (Button) findViewById(R.id.test);
            test.setVisibility(View.GONE);
        }

        if (learnt == 1)
            word.setText(R.string.word);
    }

    public void gotoTest(View view) {
        if (Constants.isNetworkAvailable(this))
            startActivity(new Intent(Progress.this, Test.class));
        else
            Toast.makeText(Progress.this, getString(R.string.no_network), Toast.LENGTH_SHORT).show();
    }

    public void share(MenuItem item) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        Intent share = new Intent(Intent.ACTION_SEND);
        share.putExtra(Intent.EXTRA_TEXT, getString(R.string.i_learned) + " " +
                String.valueOf(prefs.getInt("learned", 0)) + " " +
                getString(R.string.words_in) + " " +
                Constants.getLangsArr(this)[prefs.getInt("to", 0)]);
        share.setType("text/plain");
        startActivity(Intent.createChooser(share, getString(R.string.chooser)));
    }
}