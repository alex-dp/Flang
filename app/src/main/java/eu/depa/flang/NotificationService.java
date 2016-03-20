package eu.depa.flang;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.IBinder;
import android.os.PowerManager;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;

import com.rmtheis.yandtran.language.Language;
import com.rmtheis.yandtran.translate.Translate;

import java.util.Random;

public class NotificationService extends Service {

    private PowerManager.WakeLock mWakeLock;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void work() {
        PowerManager pm = (PowerManager) getSystemService(POWER_SERVICE);
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        if (prefs.getString("interval", "5").equals("0"))
            stopSelf();
        mWakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "flang");
        mWakeLock.acquire();
        new PollTask(this).execute();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        work();
        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mWakeLock.release();
    }

    class PollTask extends AsyncTask<Void, Void, Void> {

        public String translatedText, chosen;
        public Context context;

        public PollTask(Context pContext) {
            context = pContext;
        }

        @Override
        protected Void doInBackground(Void... params) {
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
            SharedPreferences.Editor editor = prefs.edit();
            String from = Constants.langCodes[prefs.getInt("from", 0)];
            String to = Constants.langCodes[prefs.getInt("to", 1)];

            int random;
            do
                random = new Random().nextInt(Constants.words.length);
            while (prefs.getBoolean("w" + String.valueOf(random), false));

            chosen = Constants.words[random];
            chosen = translate(chosen, "en", from);
            translatedText = translate(chosen, from, to);
            editor.putInt("learned", prefs.getInt("learned", 0) + 1).apply();

            editor.putBoolean("w" + String.valueOf(random), true).apply();
            return null;
        }

        private String translate(final String word, final String from, final String to) {

            Translate.setKey(Constants.key);
            String translation = null;
            try {
                translation = Translate.execute(word, Language.fromString(from), Language.fromString(to));
            } catch (Exception e) {
                e.printStackTrace();
            }

            return translation;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
            SharedPreferences.Editor editor = prefs.edit();
            NotificationCompat.Builder builder =
                    new NotificationCompat.Builder(context)
                            .setSmallIcon(R.drawable.ic_task)
                            .setContentTitle(translatedText)
                            .setContentText(chosen)
                            .setColor(Color.argb(0x00, 0xff, 0x3b, 0x3b))
                            .setLights(Color.argb(0x00, 0xff, 0xff, 0xff), 500, 500);
            if (prefs.getBoolean("show_in_lockscreen", true))
                builder.setPublicVersion(builder.build());

            if(prefs.getInt("learned", 0) >= Constants.words.length) {
                builder = builder.setContentText("You learned them all!");
                editor.putInt("learned", 0).apply();
                for (int i = 0; i < Constants.words.length; i++)
                    editor.putBoolean("w" + String.valueOf(i), false).apply();
            }

            int NOTIFICATION_ID = 12345;

            Intent targetIntent = new Intent(context, WordInfo.class);
            targetIntent.putExtra("original", chosen)
                    .putExtra("translated", translatedText)
                    .putExtra("wordInfo", true);
            PendingIntent contentIntent = PendingIntent.getActivity(context, 0, targetIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            builder.setContentIntent(contentIntent);
            NotificationManager nManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            nManager.notify(NOTIFICATION_ID, builder.build());
        }
    }
}