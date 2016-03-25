package eu.depa.flang.ui.activities;

import android.app.ActivityManager;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
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
            BitmapDrawable bmpd = ((BitmapDrawable) getDrawable(R.drawable.ic_task));
            Bitmap bmp = null;
            if (bmpd != null)
                bmp = bmpd.getBitmap();
            setTaskDescription(new ActivityManager.TaskDescription(null,
                    bmp,
                    Constants.getColor(this, R.color.colorPrimary)));
        }

        if (getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        lockOrientation();
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unlockOrientation();
    }

    private void lockOrientation() {
        if (super.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            super.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_USER_PORTRAIT);
        } else {
            super.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_USER_LANDSCAPE);
        }
    }

    private void unlockOrientation() {
        super.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
    }
}
