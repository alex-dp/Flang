package eu.depa.flang;

import android.content.Context;
import android.support.annotation.StringRes;
import android.view.Gravity;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class BottomToast extends Toast {

    private Context context;
    private String message;

    public BottomToast(Context pContext) {
        this(pContext, "foo");
    }


    public BottomToast(Context pContext, @StringRes int pMessage) {
        this(pContext, pContext.getString(pMessage));
    }


    public BottomToast(Context pContext, String pMessage) {
        super(pContext);
        context = pContext;
        message = pMessage;
        setView(getView());
    }

    @Override
    public View getView() {
        TextView view = new TextView(context);
        RelativeLayout layout = new RelativeLayout(context);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);

        params.setMargins(16, 16, 16, 16);

        view.setBackgroundColor(Constants.getColor(context, R.color.darkGray));
        view.setLayoutParams(params);
        layout.setLayoutParams(params);
        view.setText(message);
        view.setPadding(32, 32, 32, 32);
        view.setTextColor(Constants.getColor(context, R.color.white_background));
        view.setTextSize(17);
        view.setGravity(View.TEXT_ALIGNMENT_CENTER);

        layout.addView(view);
        setGravity(Gravity.BOTTOM, 0, 64);
        return layout;
    }
}
