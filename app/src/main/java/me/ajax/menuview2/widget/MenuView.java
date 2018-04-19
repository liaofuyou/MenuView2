package me.ajax.menuview2.widget;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import static me.ajax.menuview2.utils.GeometryUtils.polarX;
import static me.ajax.menuview2.utils.GeometryUtils.polarY;

/**
 * Created by aj on 2018/4/16
 */

public class MenuView extends ViewGroup {

    private Paint mPaint = new Paint();
    private RectF rectF = new RectF(-dp2Dx(5), -dp2Dx(5), dp2Dx(5), dp2Dx(5));

    private SubMenuView subMenuViews[][] = new SubMenuView[4][3];
    private int[] currClickSubViewIndex = new int[2];
    private List<View> selectViews = new ArrayList<>();

    private LoadingLayer loadingLayer;
    private ResultLayer resultLayer;

    private boolean isStart = false;
    private ValueAnimator animator;

    public MenuView(Context context) {
        super(context);
        init();
    }

    public MenuView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public MenuView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    void init() {

        setBackgroundColor(0x00000000);

        //画笔
        mPaint.setColor(Color.WHITE);
        mPaint.setAntiAlias(true);
        mPaint.setStrokeWidth(dp2Dx(2));
        mPaint.setStyle(Paint.Style.FILL);

        currClickSubViewIndex[0] = -1;
        currClickSubViewIndex[1] = -1;
        initSubView();


        animator = ValueAnimator.ofFloat(0, 1);
        animator.setDuration(500);
        //animator.setInterpolator(new BounceInterpolator());
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                super.onAnimationStart(animation);
                if (loadingLayer != null) {
                    loadingLayer.setBottomButtonText("BACK");
                }
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                isStart = false;

                selectViews.add(subMenuViews[currClickSubViewIndex[0]][currClickSubViewIndex[1]]);
                currClickSubViewIndex[0] = -1;
                currClickSubViewIndex[1] = -1;

                if (selectViews.size() == 4) {
                    loadingLayer.startAnimator(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            if (resultLayer != null) {
                                resultLayer.animate().alpha(1F).start();
                            }
                            if (loadingLayer != null) {
                                loadingLayer.setBottomButtonText("CALL");
                            }
                        }
                    });
                }
            }
        });
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                requestLayout();

                int currStep = selectViews.size();
                if (currStep < subMenuViews.length) {
                    for (int i = 0; i < subMenuViews[currStep].length; i++) {
                        SubMenuView view = subMenuViews[currStep][i];
                        view.setPercent(animation.getAnimatedFraction(), i == 1);
                    }
                }
            }
        });

    }

    void startAnimator(ValueAnimator animator) {
        if (animator != null) {
            animator.cancel();
            animator.start();
        }
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int size = dp2Dx(80);
        for (SubMenuView[] subMenuView : subMenuViews) {
            for (SubMenuView aSubMenuView : subMenuView) {
                measureChild(aSubMenuView, MeasureSpec.makeMeasureSpec(size, MeasureSpec.EXACTLY)
                        , MeasureSpec.makeMeasureSpec(size, MeasureSpec.EXACTLY));
            }
        }
        measureChild(loadingLayer, widthMeasureSpec, heightMeasureSpec);
        measureChild(resultLayer, widthMeasureSpec, heightMeasureSpec);
    }

    void initSubView() {

        OnClickListener onClickListener = new OnClickListener() {
            @Override
            public void onClick(View v) {

                if (selectViews.size() == 4) return;

                if (isStart) return;
                isStart = true;

                for (int i = 0; i < subMenuViews.length; i++) {
                    for (int j = 0; j < subMenuViews[i].length; j++) {
                        if (subMenuViews[i][j] == v) {
                            currClickSubViewIndex[0] = i;
                            currClickSubViewIndex[1] = j;
                            subMenuViews[i][j].setChooseView(true);
                            break;
                        }
                    }
                }
                startAnimator(animator);
            }
        };

        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 3; j++) {
                SubMenuView view = new SubMenuView(getContext());
                view.setOnClickListener(onClickListener);
                view.setShowText(" ");
                view.setCenterView(j == 1);
                if (i == 0) view.setPaintColor(0xFF7F1874);
                if (i == 1) view.setPaintColor(0xFFEACF02);
                if (i == 2) view.setPaintColor(0xFFFF5938);
                if (i == 3) view.setPaintColor(0xFF1DB0B8);

                addView(subMenuViews[i][j] = view, generateDefaultLayoutParams());
            }
        }

        //加载界面
        loadingLayer = new LoadingLayer(getContext());
        loadingLayer.setBottomButtonClickListener(new LoadingLayer.BottomButtonClickListener() {
            @Override
            public void click(String text) {
                if ("CALL".equals(text)) {
                    Toast.makeText(getContext(), "YOU CAN DO THAT , HAHA", Toast.LENGTH_SHORT).show();
                } else if ("BACK".equals(text)) {

                    Toast.makeText(getContext(), "THERE IS NOTHING TO DO", Toast.LENGTH_SHORT).show();
                } else if ("CLOSE".equals(text)) {

                    Toast.makeText(getContext(), "THERE IS NOTHING TO DO", Toast.LENGTH_SHORT).show();
                }
            }
        });
        addView(loadingLayer, generateDefaultLayoutParams());


        //结果界面
        resultLayer = new ResultLayer(getContext());
        resultLayer.setAlpha(0);
        resultLayer.setCancelListener(new ResultLayer.CancelListener() {
            @Override
            public void cancel() {
                selectViews.clear();
                isStart = false;
                loadingLayer.setBottomButtonText("CLOSE");

                for (SubMenuView[] subMenuView : subMenuViews) {
                    for (SubMenuView aSubMenuView : subMenuView) {
                        aSubMenuView.setChooseView(false);
                    }
                }

                requestLayout();
            }
        });
        addView(resultLayer, generateDefaultLayoutParams());

    }

    @Override
    protected void onDraw(Canvas canvas) {

        int mWidth = getMeasuredWidth();
        int mHeight = getMeasuredHeight();

        canvas.save();
        canvas.translate(mWidth / 2, mHeight);

        //背景
        canvas.drawCircle(0, dp2Dx(60), dp2Dx(60) + mHeight - dp2Dx(30), mPaint);

        //正方形
        canvas.save();
        canvas.translate(0, -(mHeight - dp2Dx(30)));
        canvas.rotate(45);
        canvas.drawRect(rectF, mPaint);
        canvas.restore();

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

        //背景的几个控件
        backViewLayout();

        //InnerViewGroup
        loadingLayer.layout(0, 0, r, b);
        resultLayer.layout(0, 0, r, b);

    }

    protected void backViewLayout() {

        int height = getMeasuredHeight();
        int width = getMeasuredWidth();
        int hQuarter = height / 4;
        int hHalf = height / 2;
        int wHalf = width / 2;

        float animatedFraction = animator.getAnimatedFraction();

        layoutClear();


        int currStep = selectViews.size();

        //初始状态
        if (!isStart) {
            if (currStep < subMenuViews.length) {
                for (int i = 0; i < subMenuViews[currStep].length; i++) {
                    View view = subMenuViews[currStep][i];
                    int p = dp2Dx(130);
                    int a = -180 + (45 * (i + 1));
                    viewLayoutByPolar(view, p, a);
                }
            }
        }
        //运动状态
        else {

            if (currStep + 1 < subMenuViews.length) {
                for (int i = 0; i < subMenuViews[currStep + 1].length; i++) {
                    View view = subMenuViews[currStep + 1][i];
                    float p = dp2Dx(130) * animatedFraction;
                    float a = -180 + (45 * (i + 1));
                    viewLayoutByPolar(view, p, a);
                }
            }

            //当前选择的View顶上去
            int row = currClickSubViewIndex[0];
            int col = currClickSubViewIndex[1];

            float a1 = -180 + (45 * (col + 1));
            float a2 = -130 + (20 * currStep);
            if (currStep >= 2) a2 += 20;
            polarTransaction(subMenuViews[row][col], dp2Dx(130), a1, dp2Dx(220), a2, animatedFraction);
        }

        //已经选择了的View
        for (int i = 0; i < selectViews.size(); i++) {
            float a = -130 + (20 * i);
            if (i >= 2) a += 20;
            viewLayoutByPolar(selectViews.get(i), dp2Dx(220), a);
        }
    }

    //通过极坐标平移
    void polarTransaction(View view, float p1, float a1, float p2, float a2, float fraction) {
        float p = p1 + (p2 - p1) * fraction;
        float a = a1 + (a2 - a1) * fraction;
        viewLayoutByPolar(view, p, a);
    }

    void layoutClear() {
        for (SubMenuView[] subMenuView : subMenuViews) {
            for (SubMenuView aSubMenuView : subMenuView) {
                aSubMenuView.layout(0, 0, 0, 0);
            }
        }
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

    void l(Object o) {
        Log.e("######", o.toString());
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
}
