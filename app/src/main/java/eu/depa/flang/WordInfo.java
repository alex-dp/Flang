package eu.depa.flang;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.MenuItem;
import android.widget.TextView;

public class WordInfo extends SharableActivity {

    public String ordinalNo(int value) {
        int hunRem = value % 100;
        int tenRem = value % 10;
        if (hunRem - tenRem == 10) {
            return getString(R.string.std_ord_indicator);
        }
        switch (tenRem) {
            case 1:
                return getString(R.string.one_ord_indicator);
            case 2:
                return getString(R.string.two_ord_indicator);
            case 3:
                return getString(R.string.thr_ord_indicator);
            default:
                return getString(R.string.std_ord_indicator);
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.word_info);

        setTitle(R.string.word_info);

        NotificationManager nManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        nManager.cancel(12345);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        int learned = prefs.getInt("learned", 1);
        String learnedText = getString(R.string.this_is_the) + " " +
                String.valueOf(learned) + ordinalNo(learned) + " " +
                getString(R.string.word) + " " +
                getString(R.string.you_learn);

        TextView original = (TextView) findViewById(R.id.original),
                translated = (TextView) findViewById(R.id.translated),
                count = (TextView) findViewById(R.id.words_learnt);

        original.setText(getIntent().getStringExtra("original"));
        translated.setText(getIntent().getStringExtra("translated"));
        count.setText(learnedText);
    }

    public String getOriginal() {
        return getIntent().getStringExtra("original");
    }

    public String getTrans() {
        return getIntent().getStringExtra("translated");
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(getApplicationContext(), MainActivity.class));
        finish();
    }

    public void share(MenuItem item) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        Intent share = new Intent(Intent.ACTION_SEND);
        share.putExtra(Intent.EXTRA_TEXT,
                getString(R.string.i_learned) + " " +
                        getString(R.string.that) + " " +
                        getTrans() + " " +
                        getString(R.string.means) + " " +
                        getOriginal() + " " +
                        getString(R.string.in) + " " +
                        Constants.getLangsArr()[prefs.getInt("to", 1)]);
        share.setType("text/plain");
        startActivity(Intent.createChooser(share, getString(R.string.chooser)));
    }
}
