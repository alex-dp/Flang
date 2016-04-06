package eu.depa.flang.ui.activities;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.ScaleAnimation;
import android.widget.ImageButton;
import android.widget.ImageView;

import eu.depa.flang.Constants;
import eu.depa.flang.R;
import eu.depa.flang.ui.fragments.ScreenSlidePageFragment;

public class Help extends FragmentActivity {

    private static final int NUM_PAGES = 3;
    private static Context context;
    private ViewPager mPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_screen_slide);
        context = this;

        final ImageButton arrow = (ImageButton) findViewById(R.id.arrow);

        final ImageView[] dots = {
                (ImageView) findViewById(R.id.first_indic),
                (ImageView) findViewById(R.id.second_indic),
                (ImageView) findViewById(R.id.third_indic)
        };

        new Thread(new Runnable() {
            @Override
            public void run() {
                dots[0].setImageDrawable(Constants.getDrawable(context, R.drawable.selecteditem_dot));
                mPager = (ViewPager) findViewById(R.id.pager);
                PagerAdapter mPagerAdapter = new ScreenSlidePagerAdapter(getSupportFragmentManager());
                mPager.setAdapter(mPagerAdapter);
                mPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                    @Override
                    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                    }

                    @Override
                    public void onPageSelected(final int position) {

                        for (ImageView dot : dots)
                            dot.setImageDrawable(Constants.getDrawable(getApplicationContext(), R.drawable.nonselecteditem_dot));
                        dots[position].setImageDrawable(Constants.getDrawable(getApplicationContext(), R.drawable.selecteditem_dot));

                        new Thread(new Runnable() {
                            @Override
                            public void run() {

                                if (position == NUM_PAGES - 1) {
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            arrow.setVisibility(View.VISIBLE);
                                        }
                                    });

                                    final ScaleAnimation bloatAnimation = (ScaleAnimation)
                                            AnimationUtils.loadAnimation(getBaseContext(), R.anim.bloat_in);
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            arrow.setImageDrawable(Constants.getDrawable(getApplicationContext(), R.drawable.ic_check));
                                            arrow.startAnimation(bloatAnimation);
                                        }
                                    });
                                } else if (arrow.getVisibility() == View.VISIBLE && position == NUM_PAGES - 2) {
                                    final ScaleAnimation scaleAnimation = (ScaleAnimation)
                                            AnimationUtils.loadAnimation(getBaseContext(), R.anim.shrink_out);
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            arrow.startAnimation(scaleAnimation);
                                        }
                                    });
                                    scaleAnimation.setAnimationListener(new Animation.AnimationListener() {
                                        @Override
                                        public void onAnimationStart(Animation animation) {

                                        }

                                        @Override
                                        public void onAnimationEnd(Animation animation) {
                                            arrow.setVisibility(View.GONE);
                                        }

                                        @Override
                                        public void onAnimationRepeat(Animation animation) {

                                        }
                                    });
                                }
                            }
                        }).start();
                    }

                    @Override
                    public void onPageScrollStateChanged(int state) {
                    }
                });
            }
        }).start();
    }

    @Override
    public void onBackPressed() {
        if (mPager.getCurrentItem() != 0)
            mPager.setCurrentItem(mPager.getCurrentItem() - 1);
        else {
            setResult(5682);
            finish();
        }
    }

    public void next(View view) {
        if (mPager.getCurrentItem() != mPager.getChildCount())
            mPager.setCurrentItem(mPager.getCurrentItem() + 1);
        else {
            setResult(5682);
            finish();
        }
    }

    private class ScreenSlidePagerAdapter extends FragmentStatePagerAdapter {
        public ScreenSlidePagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            ScreenSlidePageFragment frag = new ScreenSlidePageFragment();
            Bundle b = new Bundle();
            b.putInt("pos", position);
            frag.setArguments(b);
            return frag;
        }

        @Override
        public int getCount() {
            return NUM_PAGES;
        }
    }
}