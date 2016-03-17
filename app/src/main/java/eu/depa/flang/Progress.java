package eu.depa.flang;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class Progress extends BaseActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.progress);
        setTitle(R.string.progress);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);

        TextView count = (TextView) findViewById(R.id.count);

        count.setText(String.valueOf(prefs.getInt("learned", 0)));

        if (!isNetworkAvailable()) {
            Button test = (Button) findViewById(R.id.test);
            test.setVisibility(View.GONE);
        }
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public void gotoTest(View view) {
        if (isNetworkAvailable()) startActivity(new Intent(Progress.this, Test.class));
        else
            Toast.makeText(Progress.this, getString(R.string.no_network), Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.share_menu, menu);
        return true;
    }

    public void share(MenuItem item) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        Intent share = new Intent(Intent.ACTION_SEND);
        share.putExtra(Intent.EXTRA_TEXT, getString(R.string.i_learned) + " " +
                String.valueOf(prefs.getInt("learned", 0)) + " " +
                getString(R.string.words_in) + " " +
                Constants.getLangsArr()[prefs.getInt("to", 0)]);
        share.setType("text/plain");
        startActivity(Intent.createChooser(share, getString(R.string.chooser)));
    }
}