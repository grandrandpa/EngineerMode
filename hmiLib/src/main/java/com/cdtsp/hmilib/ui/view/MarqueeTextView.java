package com.cdtsp.hmilib.ui.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Shader;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.ViewTreeObserver;
import android.widget.TextView;

/**
 * Created by Administrator on 2018/3/7.
 * 使用本跑马灯控件注意事项：
 * 1、必须添加属性android:maxLines="1"，否则控件高度会有问题
 * 2、必须添加属性android:ellipsize="none"，否则文字长度超出控件长度时会有省略号出现
 */
public class MarqueeTextView extends TextView {

    private final String TAG = "MarqueeTextView";
    private final boolean DEBUG = false;

    private final int MSG_MARQUEE = 0;
    private final int DELAY_START = 2000;
    private final int DELAY_UPDATE = 5;
    private String mText;
    private float mTextWidth;
    private float mWidth;
    private int mBaseLine;
    private Paint mTextPaint;

    public MarqueeTextView(Context context) {
        this(context, null);
    }

    public MarqueeTextView(Context context, AttributeSet attrs) {
        super(context, attrs);

/* 2018/07/27 fix memory leak DEL-S */
//        getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
//            @Override
//            public void onGlobalLayout() {
//                getViewTreeObserver().removeOnGlobalLayoutListener(this);
//                mWidth = getWidth();
//                mText = getText().toString();
//                mTextPaint = getPaint();
//                mTextWidth = mTextPaint.measureText(mText);
//                mBaseLine = getBaseline();
////                if (DEBUG)
//                Log.d(TAG, "onGlobalLayout: mTextWidth=" + mTextWidth + ", mWidth=" + mWidth + ", baseLine=" + mBaseLine);
//                if (isMarquee()) {
//                    if (!mStop) {
//                        mHandler.sendEmptyMessageDelayed(MSG_MARQUEE, DELAY_START);
//                    } else {
//                        //在文本长度超过控件长度的情况下，如果已经通过stop()禁止了跑马灯，那么还需要在次重新刷新一次，以便在控件的末尾显示渐变效果
//                        postInvalidate();
//                    }
//                }
//            }
//        });
/* 2018/07/27 fix memory leak DEL-E */
    }

/* 2018/07/27 fix memory leak ADD-S */
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        mWidth = getWidth();
        mText = getText().toString();
        mTextPaint = getPaint();
        mTextWidth = mTextPaint.measureText(mText);
        mBaseLine = getBaseline();
        Log.d(TAG, "onSizeChanged: mTextWidth=" + mTextWidth + ", mWidth=" + mWidth + ", baseLine=" + mBaseLine + ", w:"+w);
        if (isMarquee()) {
            if (!mStop) {
                mHandler.sendEmptyMessageDelayed(MSG_MARQUEE, DELAY_START);
                if (DEBUG) Log.d(TAG, "onSizeChanged send message");
            } else {
                //在文本长度超过控件长度的情况下，如果已经通过stop()禁止了跑马灯，那么还需要在次重新刷新一次，以便在控件的末尾显示渐变效果
                postInvalidate();
            }
        }
    }
/* 2018/07/27 fix memory leak ADD-E */

    @Override
    protected void onAttachedToWindow() {
        if (DEBUG) Log.d(TAG, "onAttachedToWindow() called");
        super.onAttachedToWindow();
    }

    private boolean mReset = true;
    @Override
    public void setText(CharSequence text, BufferType type) {
        super.setText(text, type);
        if (mTextPaint != null) {
            mHandler.removeMessages(MSG_MARQUEE);
//            mText = text.toString();
            if (text == null) {
                mText = "";
            } else {
                mText = text.toString();
            }
            mTextWidth = mTextPaint.measureText(mText);
//            scrollTo(0, 0);
            if (isMarquee()) {
                mReset = true;
                mTextOffSet = 0;
/* 2018/07/27 fix memory leak MOD-S */
//                if (!mStop) {
                if (!mStop && !mHandler.hasMessages(MSG_MARQUEE)) {
/* 2018/07/27 fix memory leak MOD-E */
                    mHandler.sendEmptyMessageDelayed(MSG_MARQUEE, DELAY_START);
                }
            }
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        if (DEBUG) Log.d(TAG, "onDetachedFromWindow() called");
        super.onDetachedFromWindow();
    }

    @Override
    protected void onWindowVisibilityChanged(int visibility) {
        super.onWindowVisibilityChanged(visibility);
        Log.d(TAG, "onWindowVisibilityChanged() called with: visibility = [" + (visibility == VISIBLE ? true : false) + "]");
/* 2018/07/27 fix memory leak MOD-S */
//        if (isMarquee()) {
//            if (visibility == VISIBLE) {
//                if (!mStop) {
//                    mHandler.sendEmptyMessageDelayed(MSG_MARQUEE, DELAY_START);
//                }
//            } else {
//                mHandler.removeMessages(MSG_MARQUEE);
//            }
//        }
        if (visibility == VISIBLE) {
            if (isMarquee() && !mStop && !mHandler.hasMessages(MSG_MARQUEE)) {
                mHandler.sendEmptyMessageDelayed(MSG_MARQUEE, DELAY_START);
                if (DEBUG) Log.d(TAG, "onWindowVisibilityChanged send message");
            }
        } else if(mHandler.hasMessages(MSG_MARQUEE)){
            mHandler.removeMessages(MSG_MARQUEE);
            if (DEBUG) Log.d(TAG, "onWindowVisibilityChanged remove message");
        }
/* 2018/07/27 fix memory leak MOD-E */
    }

    private Paint mShadowPaint;
    private float mShadowWidth;
    private float mShadowPos = 0.1f;
    @Override
    protected void onDraw(Canvas canvas) {
        mWidth = getWidth();//若TextView的宽度是WRAP_CONTENT，那么mWidth是会随着text长度而变化的，所以，这个地方需要重新获取宽度，保证后面isMarquee()方法返回值的正确性
        if (DEBUG) Log.d(TAG, "onDraw: setText: mText=" + mText + ", mTextWidth=" + mTextWidth + ", mWidth=" + mWidth);
        if (!isMarquee()) {
            //如果不需要跑马灯，调用原生接口
            super.onDraw(canvas);
        } else {
//        canvas.drawLine(0, mBaseLine, mWidth, mBaseLine, mTextPaint);
//            Log.d(TAG, "onDraw: " + mText);
//            mTextPaint.setColor(getCurrentTextColor());
//            canvas.drawText(mText, 0, mBaseLine, mTextPaint);
//            canvas.drawText(mText, mTextWidth + mWidth / 2, mBaseLine, mTextPaint);

            canvas.saveLayer(0, 0, getWidth(),getHeight(), mTextPaint);//创建一个图层，在该图层上进行文字和shader的绘制

            mTextPaint.setColor(getCurrentTextColor());
            canvas.drawText(mText, -mTextOffSet, mBaseLine, mTextPaint);
            canvas.drawText(mText, -mTextOffSet + mTextWidth + mWidth / 2, mBaseLine, mTextPaint);

            if (mShadowPaint == null) {
                mShadowPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
                mShadowPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));
            }
            if (mReset) {
                mShadowPaint.setShader(new LinearGradient(0, 0, getWidth(), 0,
                        new int[]{0xFFFF0000, 0x00FF0000},
                        new float[]{1-mShadowPos, 1},
                        Shader.TileMode.CLAMP));
            }
            canvas.drawRect(0, 0, getWidth(), getHeight(), mShadowPaint);
            if (mReset && !mStop) {//只有在重设了TextView并且没有通过stop()方法禁止跑马灯,才进行如下设置
                mShadowPaint.setShader(new LinearGradient(0, 0, getWidth(), 0,
                        new int[]{0x00FF0000, 0xFFFF0000, 0xFFFF0000, 0x00FF0000},
                        new float[]{0, mShadowPos, 1-mShadowPos, 1},
                        Shader.TileMode.REPEAT));
            }
            mReset = false;
        }
    }

    private int mTextOffSet;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_MARQUEE:
//                    int scrollX = getScrollX();
//                    if (scrollX < mTextWidth + mWidth/2  + mWidth/2)  {
//                        scrollX += 1;
//                    } else {
//                        scrollX = (int)mWidth / 2;
//                    }
//                    if (DEBUG) Log.d(TAG, "handleMessage: scrollX=" + scrollX);
//                    scrollTo(scrollX, 0);
                    if (mTextOffSet < mTextWidth + mWidth/2  + mWidth/2)  {
                        mTextOffSet += 1;
                    } else {
                        mTextOffSet = (int)mWidth / 2;
                    }
                    if (DEBUG) Log.d(TAG, "handleMessage: mTextOffSet=" + mTextOffSet);
                    postInvalidate();
                    mHandler.sendEmptyMessageDelayed(0, DELAY_UPDATE);
                    break;
            }
            super.handleMessage(msg);
        }
    };

    /**
     * 该方法通过判断文字宽度和TextView控件宽度来决定是否需要跑马灯
     * 注：这个方法只有在布局完成后才能获取到正确的值
     * @return
     */
    private boolean isMarquee() {
        return mTextWidth > mWidth;
    }

    /**
     * 开始跑马灯
     * 默认是不需要调用的，只有在调用了{@link #stop()}方法之后才需要调用本方法来重新开启跑马灯
     */
    public void start() {
        mStop = false;
        if (isMarquee()) {
            mTextOffSet = 0;
            mHandler.removeMessages(MSG_MARQUEE);
            mHandler.sendEmptyMessageAtTime(MSG_MARQUEE, DELAY_START);
        }
    }

    private boolean mStop;

    /**
     * 禁止跑马灯
     * 在禁止了跑马灯之后，需要通过{@link #start()}来重新开启跑马灯
     */
    public void stop() {
        mStop = true;
/* 2018/07/27 fix memory leak MOD-S */
//        if (isMarquee()) {
        if (mHandler.hasMessages(MSG_MARQUEE)) {
/* 2018/07/27 fix memory leak MOD-E */
            mTextOffSet = 0;
            mHandler.removeMessages(MSG_MARQUEE);
            postInvalidate();
        }
    }
}
