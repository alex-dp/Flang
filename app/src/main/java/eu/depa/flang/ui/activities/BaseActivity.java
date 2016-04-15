package eu.depa.flang.ui.activities;

import android.app.ActivityManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import eu.depa.flang.Constants;
import eu.depa.flang.R;

public class BaseActivity extends AppCompatActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            BitmapDrawable bmpd = (BitmapDrawable) getDrawable(R.drawable.ic_task);
            Bitmap bmp = null;
            if (bmpd != null)
                bmp = bmpd.getBitmap();
            setTaskDescription(new ActivityManager.TaskDescription(null,
                    bmp,
                    Constants.getColor(this, R.color.colorPrimary)));
        }

        if (getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
