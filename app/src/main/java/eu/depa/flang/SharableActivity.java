package eu.depa.flang;

import android.view.Menu;
import android.view.MenuItem;

public class SharableActivity extends BaseActivity {


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.share_menu, menu);
        return true;
    }

    public void share(MenuItem item) {
    }
}
