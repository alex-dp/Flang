package eu.depa.flang.ui.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.rmtheis.yandtran.language.Language;
import com.rmtheis.yandtran.translate.Translate;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import eu.depa.flang.Constants;
import eu.depa.flang.FlowLayout;
import eu.depa.flang.R;

public class Test extends BaseActivity implements View.OnClickListener {

    private static String translation = "default";
    private static Context context;
    private final List<String> words_from = new ArrayList<>(),
            words_to = new ArrayList<>();
    private int curr_pos = 0,
            singlePad = 5,
            n_questions = 10,
            n_choices = 4,
            totWidth;
    private String from, to;

    private final ImageView[] views = new ImageView[n_questions];
    private final boolean[] correct_arr = new boolean[n_questions];

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.test);
        setTitle(R.string.take_a_test);
        context = this;

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        from = Constants.langCodes[prefs.getInt("from", 0)];
        to = Constants.langCodes[prefs.getInt("to", 1)];

        final Random or_rand = new Random();
        new Thread(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < n_questions; i++) {
                    int original_pos = or_rand.nextInt(Constants.words.length);
                    words_from.add(translate(Constants.words[original_pos], "en", from));
                    words_to.add(translate(words_from.get(i), from, to));
                }
            }
        }).run();

        final RelativeLayout mom = (RelativeLayout) findViewById(R.id.test_mom);

        new Thread(new Runnable() {
            @Override
            public void run() {
                final AdView adView = new AdView(context);
                final RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT);
                params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
                params.addRule(RelativeLayout.CENTER_HORIZONTAL);
                adView.setAdSize(AdSize.BANNER);
                adView.setAdUnitId(Constants.test_ad_unit_id);
                final AdRequest adRequest = new AdRequest.Builder().build();

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mom.addView(adView, params);
                        adView.loadAd(adRequest);
                    }
                });
            }
        }).start();

        final LinearLayout notches = new LinearLayout(getBaseContext());
        notches.setOrientation(LinearLayout.HORIZONTAL);
        ViewTreeObserver vto = mom.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                mom.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                totWidth = mom.getMeasuredWidth() + singlePad;

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        for (int i = 0; i < n_questions; i++) {
                            views[i] = new ImageView(getBaseContext());
                            float width = (totWidth / n_questions) - singlePad;
                            final LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams((int) width, 12);
                            layoutParams.setMarginStart(singlePad);
                            views[i].setImageDrawable(Constants.getDrawable(
                                    getApplicationContext(), R.drawable.gray_rect));
                            final int finalI = i;
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    notches.addView(views[finalI], layoutParams);
                                }
                            });
                        }
                    }
                }).start();

                mom.addView(notches, 0);
            }
        });
        populateViews();
    }

    private void populateViews() {
        final FlowLayout trans_mom = new FlowLayout(getBaseContext());
        final RelativeLayout mom = (RelativeLayout) findViewById(R.id.test_mom);
        final Animation
                fadeIn = AnimationUtils.loadAnimation(getBaseContext(), R.anim.fade_in),
                toLeft = AnimationUtils.loadAnimation(getBaseContext(), R.anim.slide_to_left);
        setNewWordToGuess();

        new Thread(new Runnable() {
            @Override
            public void run() {
                int n_rand = new Random().nextInt(n_choices);
                for (int i = 0; i < n_choices; i++) {
                    final TextView translation = new TextView(getBaseContext());
                    translation.setPadding(32, 32, 32, 32);
                    translation.setBackground(Constants.getDrawable(getApplicationContext(), R.drawable.gray_frame));
                    translation.setTextColor(Color.rgb(0, 0, 0));
                    translation.setTextSize(20);
                    translation.setClickable(true);
                    if (i == n_rand)
                        try {
                            translation.setText(words_to.get(curr_pos));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    else {
                        Random m_rand = new Random(new Random().nextInt());
                        int pos = m_rand.nextInt(Constants.words.length);
                        String temp = translate(Constants.words[pos], "en", from);
                        final String fin = translate(temp, from, to);
                        translation.setText(fin);
                    }
                    translation.setOnClickListener((Test) context);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            trans_mom.addView(translation);
                        }
                    });
                }


            }
        }).start();

        toLeft.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                mom.removeViewAt(mom.getChildCount() - 2);
                mom.addView(trans_mom, mom.getChildCount() - 1);

                trans_mom.startAnimation(fadeIn);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });

        final RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        params.addRule(RelativeLayout.BELOW, R.id.trans_word_above);
        trans_mom.setLayoutParams(params);

        if (curr_pos != 0) {
            mom.getChildAt(mom.getChildCount() - 2).startAnimation(toLeft);
        } else mom.addView(trans_mom, mom.getChildCount());
    }

    private void animateEditText(boolean correct) {

        final Animation
                toLeftAnim = AnimationUtils.loadAnimation(getBaseContext(), R.anim.slide_to_left),
                fadeIn = AnimationUtils.loadAnimation(getBaseContext(), R.anim.fade_in);

        final TextView TB = (TextView) findViewById(R.id.word_to_guess);

        toLeftAnim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                populateViews();    //set new word to guess and words to pick from
                TB.startAnimation(fadeIn);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        TB.startAnimation(toLeftAnim);
        views[curr_pos].setImageDrawable(Constants.getDrawable(this,
                (correct) ? R.drawable.green_rect : R.drawable.red_rect));
        correct_arr[curr_pos] = correct;
        curr_pos++;
        if (curr_pos >= n_questions) {
            Intent i = new Intent(Test.this, TestResults.class);
            i.putStringArrayListExtra("from", (ArrayList<String>) words_from);
            i.putStringArrayListExtra("to", (ArrayList<String>) words_to);
            i.putExtra("correct", correct_arr);
            startActivity(i);
            finish();
        }
    }

    private void setNewWordToGuess() {
        TextView original = (TextView) findViewById(R.id.word_to_guess);
        try {
            original.setText(words_from.get(curr_pos));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String translate(final String word, final String from, final String to) {

        Translate.setKey(Constants.key);
        Thread translate = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    translation = Translate.execute(word, Language.fromString(from), Language.fromString(to));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        translate.start();
        while (true) {
            if (!translate.isAlive()) break;
        }

        return translation;
    }

    @Override
    public void onClick(View v) {
        TextView view;
        try {
            view = (TextView) v;
            boolean correct = view.getText().toString().equalsIgnoreCase(words_to.get(curr_pos));
            view.setBackground(Constants.getDrawable(this, correct ? R.drawable.green_frame : R.drawable.red_frame));
            animateEditText(correct);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
