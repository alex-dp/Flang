package eu.depa.flang.ui.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;

import eu.depa.flang.Constants;
import eu.depa.flang.R;

public class Progress extends SharableActivity implements MenuItem.OnMenuItemClickListener {

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

        if (!Constants.isNetworkAvailable(this) || prefs.getInt("learned", 0) < 3) {
            Button test = (Button) findViewById(R.id.test);
            test.setVisibility(View.GONE);
        }

        if (learnt == 1)
            word.setText(R.string.word);

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
                final AdRequest adRequest = new AdRequest.Builder()
                        .addTestDevice("FE6364C53B96FC7B9194F54AA9DED7C3")
                        .build();

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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (Constants.isNetworkAvailable(this))
            menu.add(0, 0, 1, R.string.take_a_test)
                    .setOnMenuItemClickListener(this)
                    .setIcon(R.drawable.ic_check_white)
                    .setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
        getMenuInflater().inflate(R.menu.share_menu, menu);
        return true;
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        if (item.getItemId() == 0)
            startActivity(new Intent(getBaseContext(), Test.class));
        return true;
    }
}