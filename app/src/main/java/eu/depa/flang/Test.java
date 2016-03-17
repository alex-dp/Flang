package eu.depa.flang;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.rmtheis.yandtran.language.Language;
import com.rmtheis.yandtran.translate.Translate;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Test extends BaseActivity implements View.OnClickListener {

    public static String translation = "default";
    int curr_pos = 0,
            singlePad = 5,
            n_questions = 10,
            n_choices = 4,
            totWidth;
    ImageView[] views = new ImageView[n_questions];
    String from, to;
    List<String> words_from = new ArrayList<>(),
            words_to = new ArrayList<>();
    boolean[] correct_arr = new boolean[n_questions];

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.test);
        setTitle(R.string.take_a_test);

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

        final LinearLayout mom = (LinearLayout) findViewById(R.id.test_mom);
        final LinearLayout notches = new LinearLayout(getBaseContext());
        notches.setOrientation(LinearLayout.HORIZONTAL);
        ViewTreeObserver vto = mom.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                mom.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                totWidth = mom.getMeasuredWidth() + singlePad;

                for (int i = 0; i < n_questions; i++) {
                    views[i] = new ImageView(getBaseContext());
                    float width = (totWidth / n_questions) - singlePad;
                    LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams((int) width, 12);
                    layoutParams.setMarginStart(singlePad);
                    views[i].setImageDrawable(getDrawableM(R.drawable.gray_rect));
                    notches.addView(views[i], layoutParams);
                }

                mom.addView(notches, 0);
            }
        });
        populateViews();
    }

    private boolean populateViews() {
        final FlowLayout trans_mom = new FlowLayout(getBaseContext());
        final LinearLayout mom = (LinearLayout) findViewById(R.id.test_mom);
        setNewWordToGuess();

        int n_rand = new Random().nextInt(n_choices);
        for (int i = 0; i < n_choices; i++) {
            final TextView translation = new TextView(getBaseContext());
            translation.setPadding(16, 16, 16, 16);
            translation.setBackground(getDrawableM(R.drawable.gray_rect));
            translation.setTextColor(Color.rgb(0, 0, 0));
            translation.setTextSize(20);
            translation.setClickable(true);
            if (i == n_rand)
                translation.setText(words_to.get(curr_pos));
            else {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Random m_rand = new Random(new Random().nextInt());
                        int pos = m_rand.nextInt(Constants.words.length);
                        String temp = translate(Constants.words[pos], "en", from);
                        final String fin = translate(temp, from, to);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                translation.setText(fin);
                            }
                        });
                    }
                }).start();
            }
            translation.setOnClickListener(this);
            trans_mom.addView(translation);
        }
        final Animation
                fadeIn = AnimationUtils.loadAnimation(getBaseContext(), R.anim.fade_in),
                toLeft = AnimationUtils.loadAnimation(getBaseContext(), R.anim.slide_to_left);
        toLeft.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                mom.getChildAt(mom.getChildCount() - 1).setVisibility(View.GONE);
                mom.addView(trans_mom);
                trans_mom.startAnimation(fadeIn);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });
        if (curr_pos != 0) {
            mom.getChildAt(mom.getChildCount() - 1).startAnimation(toLeft);
        } else mom.addView(trans_mom);
        return true;
    }

    public void animateEditText(boolean correct) {

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
                if (populateViews())    //set new word to guess and words to pick from
                    TB.startAnimation(fadeIn);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        TB.startAnimation(toLeftAnim);
        views[curr_pos].setImageDrawable(getDrawableM(
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

    void setNewWordToGuess() {
        TextView original = (TextView) findViewById(R.id.word_to_guess);
        original.setText(words_from.get(curr_pos));
    }

    @SuppressLint("NewApi")
    public Drawable getDrawableM(int id) {
        //noinspection deprecation
        return (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) ?
                getDrawable(id) : getResources().getDrawable(id);
    }

    String translate(final String word, final String from, final String to) {

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
        TextView view = new TextView(getBaseContext());
        try {
            view = (TextView) v;
        } catch (Exception e) {
            e.printStackTrace();
        }
        animateEditText((view.getText().toString().equalsIgnoreCase(words_to.get(curr_pos))));
    }
}
