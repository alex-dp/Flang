package eu.depa.flang.ui.activities;

import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import eu.depa.flang.Constants;
import eu.depa.flang.R;

public class TestableActivity extends SharableActivity implements View.OnClickListener {

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (Constants.isNetworkAvailable(this) && learnedMoreThan(5)) {
            MenuItem test = menu.add(0, 0, 0, R.string.take_a_test)
                    .setActionView(R.layout.take_a_test_menu_item);
            test.getActionView().setOnClickListener(this);
            test.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
        }
        getMenuInflater().inflate(R.menu.share_menu, menu);
        return true;
    }

    @Override
    public void onClick(View v) {
        Constants.gotoTest(this);
    }

    public void gotoTest(View view) {
        Constants.gotoTest(this);
    }

    public boolean learnedMoreThan(int this_much) {
        return PreferenceManager.getDefaultSharedPreferences(this).getInt("learned", 0) > this_much;
    }
}
