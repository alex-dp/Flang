package eu.depa.flang;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    static Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context = getApplicationContext();
        final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);

        if (prefs.getBoolean("first", true))
            startActivity(new Intent(this, ScreenSlidePagerActivity.class));
        prefs.edit().putBoolean("first", false).apply();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            BitmapDrawable bmpd = ((BitmapDrawable) getDrawable(R.drawable.ic_task));
            Bitmap bmp = null;
            if (bmpd != null)
                bmp = bmpd.getBitmap();
            //noinspection deprecation
            setTaskDescription(new ActivityManager.TaskDescription(null,
                    bmp,
                    getResources().getColor(R.color.colorPrimary)));
        }

        Spinner fromSpinner = (Spinner) findViewById(R.id.from);
        Spinner toSpinner = (Spinner) findViewById(R.id.to);

        SpinnerAdapter langAdapter = new CustomArrayAdapter<>(
                context,
                Constants.getLangsArr());

        fromSpinner.setAdapter(langAdapter);
        toSpinner.setAdapter(langAdapter);

        fromSpinner.setSelection(prefs.getInt("from", 0));
        toSpinner.setSelection(prefs.getInt("to", 1));

        fromSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                prefs.edit().putInt("from", (int) id).apply();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
        toSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                prefs.edit().putInt("to", (int) id).apply();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START))
            drawer.closeDrawer(GravityCompat.START);
        else super.onBackPressed();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        switch (item.getItemId()) {

            case R.id.nav_manage:
                startActivity(new Intent(this, Settings.class));
                break;
            case R.id.nav_progress:
                startActivity(new Intent(this, Progress.class));
                break;
            case R.id.nav_feedback:
                Intent intent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts("mailto", "depasquale.a@tuta.io", null));
                intent.putExtra(Intent.EXTRA_EMAIL, "depasquale.a@tuta.io");
                intent.putExtra(Intent.EXTRA_SUBJECT, "Flang: ");
                intent.putExtra(Intent.EXTRA_TEXT, "API: " + Build.VERSION.SDK_INT +
                        "\nVERSION: " + BuildConfig.VERSION_CODE + "\n\n");
                try {
                    startActivity(intent);
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(MainActivity.this, getString(R.string.email_noclient), Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.nav_help:
                startActivity(new Intent(this, ScreenSlidePagerActivity.class));
                break;
            case R.id.nav_test_history:
                startActivity(new Intent(this, TestHistory.class));
                break;
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void getNot(View view) {
        Intent i = new Intent(context, NotificationService.class);
        startService(i);
    }

    public void resetAlarm(View view) {
        Constants.resetAlarm(context);
        Toast.makeText(context, R.string.begin_toast, Toast.LENGTH_SHORT).show();
    }

    public void gotoGitHub(View view) {
        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://github.com/deeepaaa/Flang")));
    }

    public void gotoYandTran(View view) {
        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://translate.yandex.com")));
    }
}
