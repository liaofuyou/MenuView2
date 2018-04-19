package me.ajax.menuview2.widget;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import me.ajax.menuview2.R;


/**
 * Created by aj on 2018/4/16
 */

public class ResultLayer extends LinearLayout {

    public ResultLayer(Context context) {
        super(context);
        init();
    }

    public ResultLayer(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ResultLayer(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    void init() {
        //画笔
        setBackgroundColor(0x00000000);
        initSubView();
    }

    void initSubView() {

        setOrientation(VERTICAL);
        //setGravity(Gravity.CENTER_HORIZONTAL);


        LayoutParams imageLp = new LayoutParams(dp2Dx(65), dp2Dx(65));
        imageLp.gravity = Gravity.CENTER;

        ImageView avatarImageView = new ImageView(getContext());
        avatarImageView.setImageResource(R.mipmap.ic_launcher_round);
        addView(avatarImageView, imageLp);

        LayoutParams lp = new LayoutParams(-2, -2);
        lp.gravity = Gravity.CENTER;
        lp.topMargin = dp2Dx(8);

        TextView nameTextView = new TextView(getContext());
        nameTextView.setText("AJ Liao");
        nameTextView.setTextColor(Color.DKGRAY);
        nameTextView.setTextSize(20);

        addView(nameTextView, lp);

        TextView descTextView = new TextView(getContext());
        descTextView.setText("Double truck | Two helpers");
        descTextView.setTextColor(0xFFEACF02);
        descTextView.setTextSize(14);

        addView(descTextView, lp);

        TextView dateTextView = new TextView(getContext());
        dateTextView.setText("25 Jun 2017 | 9:30");
        dateTextView.setTextColor(Color.DKGRAY);
        dateTextView.setTextSize(14);

        addView(dateTextView, lp);

        TextView cancelTextView = new TextView(getContext());
        cancelTextView.setText("CANCEL");
        cancelTextView.setTextColor(Color.DKGRAY);
        cancelTextView.setTextSize(14);
        cancelTextView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getVisibility() != VISIBLE) return;
                animate().alpha(0F)
                        .setListener(new AnimatorListenerAdapter() {
                            @Override
                            public void onAnimationEnd(Animator animation) {
                                super.onAnimationEnd(animation);
                                if (cancelListener != null) {
                                    cancelListener.cancel();
                                }
                                animate().setListener(null);
                            }
                        })
                        .start();
            }
        });

        addView(cancelTextView, lp);
    }

    int dp2Dx(int dp) {
        return (int) (getResources().getDisplayMetrics().density * dp);
    }

    private CancelListener cancelListener;

    public void setCancelListener(CancelListener cancelListener) {
        this.cancelListener = cancelListener;
    }

    interface CancelListener {
        void cancel();
    }
}
