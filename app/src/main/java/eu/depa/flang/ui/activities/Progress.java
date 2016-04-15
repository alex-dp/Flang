package eu.depa.flang.ui.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;

import eu.depa.flang.Constants;
import eu.depa.flang.R;

public class Progress extends SharableActivity {

    private static Context context;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.progress);
        setTitle(R.string.progress);

        context = this;
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);

        TextView count = (TextView) findViewById(R.id.count),
                word = (TextView) findViewById(R.id.word_or_words);
        int learnt = prefs.getInt("learned", 0);

        count.setText(String.valueOf(learnt));

        if (learnt == 1)
            word.setText(R.string.word);

        if (prefs.getInt("learned", 0) >= 2)
            findViewById(R.id.gototest_btn).setVisibility(View.VISIBLE);

        new Thread(new Runnable() {
            @Override
            public void run() {
                final AdView adView = new AdView(context);
                RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT);
                params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
                params.addRule(RelativeLayout.CENTER_HORIZONTAL);
                adView.setLayoutParams(params);
                adView.setAdSize(AdSize.BANNER);
                adView.setAdUnitId(Constants.progress_ad_unit_id);
                final RelativeLayout mom = (RelativeLayout) findViewById(R.id.progress_mom);
                final AdRequest adRequest = new AdRequest.Builder().build();

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mom.addView(adView);
                        adView.loadAd(adRequest);
                    }
                });
            }
        }).start();
    }

    public void gotoTest(View view) {
        Constants.gotoTest(context);
    }

    public void share(MenuItem item) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        Intent share = new Intent(Intent.ACTION_SEND);
        share.putExtra(Intent.EXTRA_TEXT, getString(R.string.i_learned) + " " +
                String.valueOf(prefs.getInt("learned", 0)) + " " +
                getString(R.string.words_in) + " " +
                Constants.getLangsArr(context)[prefs.getInt("to", 0)]);
        share.setType("text/plain");
        startActivity(Intent.createChooser(share, getString(R.string.chooser)));
    }
}