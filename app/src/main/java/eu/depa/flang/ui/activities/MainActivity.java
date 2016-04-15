package eu.depa.flang.ui.activities;

import android.app.ActivityManager;
import android.content.Context;
import android.content.DialogInterface;
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
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.ScaleAnimation;
import android.widget.AdapterView;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.Toast;

import eu.depa.flang.BottomToast;
import eu.depa.flang.BuildConfig;
import eu.depa.flang.Constants;
import eu.depa.flang.NotificationService;
import eu.depa.flang.R;
import eu.depa.flang.adapters.CustomArrayAdapter;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private static Context context;
    private static SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    protected void onStart() {
        super.onStart();

        context = this;
        prefs = PreferenceManager.getDefaultSharedPreferences(context);

        if (prefs.getBoolean("first", true)) {
            startActivityForResult(new Intent(context, Help.class), 5682);
            return;
        }

        continueOnCreate();
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
                Intent intent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts("mailto", "depasquale.a@tuta.io", null))
                        .putExtra(Intent.EXTRA_EMAIL, "depasquale.a@tuta.io")
                        .putExtra(Intent.EXTRA_SUBJECT, "Flang: ")
                        .putExtra(Intent.EXTRA_TEXT, "API: " + Build.VERSION.SDK_INT +
                                "\nVERSION: " + BuildConfig.VERSION_CODE + "\n\n");
                try {
                    startActivity(intent);
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(MainActivity.this, getString(R.string.email_noclient), Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.nav_help:
                startActivity(new Intent(this, Help.class));
                break;
            case R.id.nav_test_history:
                startActivity(new Intent(this, TestHistory.class));
                break;
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == resultCode) {
            prefs.edit().putBoolean("first", false).apply();
            continueOnCreate();
        }
    }

    private void continueOnCreate() {

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
            setTaskDescription(new ActivityManager.TaskDescription(null,
                    bmp,
                    Constants.getColor(this, R.color.colorPrimary)));
        }

        Spinner fromSpinner = (Spinner) findViewById(R.id.from);
        final Spinner toSpinner = (Spinner) findViewById(R.id.to);

        SpinnerAdapter langAdapter = new CustomArrayAdapter<>(
                context,
                Constants.getLangsArr(this));

        fromSpinner.setAdapter(langAdapter);
        toSpinner.setAdapter(langAdapter);

        updateSpinners(fromSpinner, toSpinner, false);

        fromSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                prefs.edit().putInt("from", position).apply();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        toSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, final int position, long id) {
                AlertDialog.Builder deleterDialog = new AlertDialog.Builder(context, R.style.orangePD);
                deleterDialog.setTitle(R.string.delete_progress)
                        .setMessage(getString(R.string.changing_language_will_erase_data))
                        .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Constants.deleteAllProgress(prefs.edit());
                                prefs.edit().putInt("to", position).apply();
                                dialog.dismiss();
                            }
                        })
                        .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                toSpinner.setSelection(prefs.getInt("to", 1));
                                dialog.dismiss();
                            }
                        })
                        .setCancelable(false);

                if (position != prefs.getInt("to", 1)) {
                    deleterDialog.create();
                    deleterDialog.show();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        if (Constants.isDebuggable(this))
            findViewById(R.id.get_not_btn).setVisibility(View.VISIBLE);
    }

    public void getNot(View view) {
        startService(new Intent(context, NotificationService.class));
    }

    public void resetAlarm(View view) {
        getNot(null);
        Constants.resetAlarm(context);
        new BottomToast(this, R.string.begin_toast).show();
    }

    public void gotoGitHub(View view) {
        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://github.com/deeepaaa/Flang")));
    }

    public void gotoYandTran(View view) {
        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://translate.yandex.com")));
    }

    public void swapLangs(View view) {
        view.startAnimation(AnimationUtils.loadAnimation(context, R.anim.swap));

        Spinner fromSpinner = (Spinner) findViewById(R.id.from);
        final Spinner toSpinner = (Spinner) findViewById(R.id.to);

        prefs.edit()
                .putInt("from", toSpinner.getSelectedItemPosition())
                .putInt("to", fromSpinner.getSelectedItemPosition())
                .apply();

        updateSpinners(fromSpinner, toSpinner, true);
    }

    private void updateSpinners(final Spinner fromSpinner, final Spinner toSpinner, boolean animate) {

        if (!animate) {
            fromSpinner.setSelection(prefs.getInt("from", 0));
            toSpinner.setSelection(prefs.getInt("to", 1));
        } else {
            final ScaleAnimation shrinkOut = (ScaleAnimation) AnimationUtils.loadAnimation(context, R.anim.shrink_out),
                    bloatIn = (ScaleAnimation) AnimationUtils.loadAnimation(context, R.anim.bloat_in);

            shrinkOut.setInterpolator(context, android.R.interpolator.linear);
            bloatIn.setInterpolator(context, android.R.interpolator.linear);
            shrinkOut.setDuration(200);
            bloatIn.setDuration(200);
            shrinkOut.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    fromSpinner.setSelection(prefs.getInt("from", 0));
                    toSpinner.setSelection(prefs.getInt("to", 1));

                    fromSpinner.startAnimation(bloatIn);
                    toSpinner.startAnimation(bloatIn);
                }

                @Override
                public void onAnimationRepeat(Animation animation) {
                }
            });

            fromSpinner.startAnimation(shrinkOut);
            toSpinner.startAnimation(shrinkOut);
        }
    }
}
