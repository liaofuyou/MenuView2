package me.ajax.menuview2.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import me.ajax.menuview2.R;


/**
 * Created by aj on 2018/4/2
 */

public class SubMenuView extends View {

    Rect timeTextBounds = new Rect();
    Paint mPaint = new Paint();
    Paint textPaint = new Paint();
    float percent = 1;
    Bitmap mBitmap;
    String showText;
    boolean isCenterView;
    boolean isChooseView;

    public SubMenuView(Context context) {
        super(context);
        init();
    }

    public SubMenuView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public SubMenuView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    void init() {

        //画笔
        mPaint.setColor(0XFFFFFFFF);
        mPaint.setAntiAlias(true);
        mPaint.setStyle(Paint.Style.FILL);

        textPaint.setColor(0XFFFFFFFF);
        textPaint.setAntiAlias(true);
        textPaint.setTextSize(dp2Dx(16));

        mBitmap = changeBitmapSize(dp2Dx(20), dp2Dx(20));

    }

    private Bitmap changeBitmapSize(int newWidth, int newHeight) {

        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher);
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();

        //计算压缩的比率
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;

        //获取想要缩放的matrix
        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);

        //获取新的bitmap
        return Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, true);
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int mWidth = getMeasuredWidth();
        int mHeight = getMeasuredHeight();


        canvas.save();
        canvas.translate(mWidth / 2, mHeight / 2);

        float radius = getInitRadius();
        if (isChooseView) {
            radius -= (radius - dp2Dx(30)) * percent;
        } else {
            radius *= percent;
        }
        canvas.drawCircle(0, 0, radius, mPaint);
        textPaint.getTextBounds(showText, 0, showText.length(), timeTextBounds);
        canvas.drawText(showText, -timeTextBounds.width() / 2f, timeTextBounds.height() / 2f, textPaint);

        canvas.restore();
    }

    private float getInitRadius() {
        int mHeight = getMeasuredHeight();
        float radius;
        if (isCenterView) {
            radius = mHeight / 2;
        } else {
            radius = (mHeight / 2 - dp2Dx(8));
        }
        return radius;
    }

    public void setPercent(float percent, boolean isCenterView) {
        this.percent = percent;
        this.isCenterView = isCenterView;
        invalidate();
    }

    public void setChooseView(boolean chooseView) {
        isChooseView = chooseView;
    }

    public void setCenterView(boolean centerView) {
        isCenterView = centerView;
    }

    public void setShowText(String showText) {
        this.showText = showText;
    }

    public void setPaintColor(int color) {
        if (this.mPaint != null) {
            this.mPaint.setColor(color);
        }
    }

    public String getShowText() {
        return showText;
    }

    int dp2Dx(int dp) {
        return (int) (getResources().getDisplayMetrics().density * dp);
    }


    void l(Object o) {
        Log.e("######", o.toString());
    }
}
