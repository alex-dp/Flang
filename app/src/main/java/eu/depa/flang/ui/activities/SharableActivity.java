package eu.depa.flang.ui.activities;

import android.view.Menu;
import android.view.MenuItem;

import eu.depa.flang.R;

public class SharableActivity extends BaseActivity {


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.share_menu, menu);
        return true;
    }

    public void share(MenuItem item) {
    }
}
