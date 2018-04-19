package me.ajax.menuview2.widget;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import static me.ajax.menuview2.utils.GeometryUtils.polarX;
import static me.ajax.menuview2.utils.GeometryUtils.polarY;

/**
 * Created by aj on 2018/4/16
 */

public class LoadingLayer extends ViewGroup {

    private Paint mPaint = new Paint();

    private ValueAnimator animator;
    private TextView bottomButton;
    private ProgressBar progressBar;

    private boolean isReverse = false;
    private boolean isStart = false;

    //动画结束监听
    private Animator.AnimatorListener animatorEndListener;

    public LoadingLayer(Context context) {
        super(context);
        init();
    }

    public LoadingLayer(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public LoadingLayer(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    void init() {

        //画笔
        mPaint.setColor(Color.GRAY);
        mPaint.setAntiAlias(true);
        mPaint.setStrokeWidth(dp2Dx(2));
        mPaint.setStyle(Paint.Style.FILL);
        setBackgroundColor(0x00000000);
        initSubView();

        progressBar.setVisibility(INVISIBLE);

        animator = ValueAnimator.ofFloat(0, 1);
        animator.setDuration(500);
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                super.onAnimationStart(animation);
                if (!isReverse) {
                    bottomButton.setVisibility(GONE);
                }
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                isStart = false;

                if (!isReverse) {
                    progressBar.setVisibility(VISIBLE);
                    postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            progressBar.setVisibility(GONE);
                            isReverse = true;
                            animator.reverse();
                        }
                    }, 500);
                } else {
                    bottomButton.setVisibility(VISIBLE);
                    if (animatorEndListener != null) {
                        animatorEndListener.onAnimationEnd(animation);
                    }
                }
            }
        });
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                invalidate();
            }
        });
    }

    public void startAnimator(Animator.AnimatorListener animatorEndListener) {
        if (isStart) return;
        isStart = true;
        isReverse = false;
        this.animatorEndListener = animatorEndListener;
        if (animator != null) {
            animator.cancel();
            animator.start();
        }
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        measureChild(bottomButton, MeasureSpec.makeMeasureSpec(MeasureSpec.getSize(widthMeasureSpec), MeasureSpec.AT_MOST)
                , MeasureSpec.makeMeasureSpec(MeasureSpec.getSize(widthMeasureSpec), MeasureSpec.AT_MOST));

        measureChild(progressBar, MeasureSpec.makeMeasureSpec(MeasureSpec.getSize(widthMeasureSpec), MeasureSpec.AT_MOST)
                , MeasureSpec.makeMeasureSpec(MeasureSpec.getSize(widthMeasureSpec), MeasureSpec.AT_MOST));
    }

    void initSubView() {

        progressBar = new ProgressBar(getContext());
        addView(progressBar, generateDefaultLayoutParams());

        bottomButton = new TextView(getContext());
        bottomButton.setText("CLOSE");
        bottomButton.setTextColor(Color.WHITE);
        bottomButton.setTextSize(20);
        bottomButton.setClickable(true);
        bottomButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (bottomButtonClickListener != null) {
                    bottomButtonClickListener.click(bottomButton.getText().toString());
                }
            }
        });
        addView(bottomButton, generateDefaultLayoutParams());
    }

    public void setBottomButtonText(String text) {
        if (bottomButton != null) {
            bottomButton.setText(text);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {

        int mWidth = getMeasuredWidth();
        int mHeight = getMeasuredHeight();

        canvas.save();
        canvas.translate(mWidth / 2, mHeight);

        float animatedFraction = animator.getAnimatedFraction();
        //背景
        canvas.drawCircle(0, dp2Dx(40) - dp2Dx(40) * animatedFraction, dp2Dx(90) + (mHeight - dp2Dx(90)) * animatedFraction, mPaint);

        canvas.restore();

        super.onDraw(canvas);
    }


    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {

        int height = getMeasuredHeight();
        int width = getMeasuredWidth();
        int hQuarter = height / 4;
        int hHalf = height / 2;
        int wHalf = width / 2;

        viewLayoutByPolar(bottomButton, dp2Dx(5), -90);
        viewLayoutByPolar(progressBar, hHalf, -90);

    }

    //通过极坐标平移
    void polarTransaction(View view, float p1, float a1, float p2, float a2, float fraction) {
        float p = p1 + (p2 - p1) * fraction;
        float a = a1 + (a2 - a1) * fraction;
        viewLayoutByPolar(view, p, a);
    }


    void viewLayoutByPolar(View view, float p, float a) {
        int centerX = getMeasuredWidth() / 2 + (int) polarX(p, a);
        int centerY = getMeasuredHeight() + (int) polarY(p, a);
        int viewHalf = view.getMeasuredWidth() / 2;

        view.layout(centerX - viewHalf, centerY - viewHalf, centerX + viewHalf, centerY + viewHalf);
    }


    int dp2Dx(int dp) {
        return (int) (getResources().getDisplayMetrics().density * dp);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        stopAnimAndRemoveCallbacks();
    }

    private void stopAnimAndRemoveCallbacks() {

        if (animator != null) animator.end();

        Handler handler = this.getHandler();
        if (handler != null) {
            handler.removeCallbacksAndMessages(null);
        }
    }

    BottomButtonClickListener bottomButtonClickListener;

    public void setBottomButtonClickListener(BottomButtonClickListener bottomButtonClickListener) {
        this.bottomButtonClickListener = bottomButtonClickListener;
    }

    interface BottomButtonClickListener {
        void click(String text);
    }
}
