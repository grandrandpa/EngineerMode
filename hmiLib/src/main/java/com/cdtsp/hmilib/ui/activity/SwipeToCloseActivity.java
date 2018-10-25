package com.cdtsp.hmilib.ui.activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.TypeEvaluator;
import android.animation.ValueAnimator;
import android.annotation.TargetApi;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

public class SwipeToCloseActivity extends FragmentActivity {

    private final String TAG = getClass().getSimpleName();
//    private View mCloseControlBar;
    private View mContentLayout;
    private View mBgView;

    private int mScreenWidth;
    private int mScreenHeight;

    private boolean mFlingToClose;
    private boolean mSwipeEnable = true;

    private GestureDetector mGestureDetector;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_swipe_to_close);
        initScreenParams();

        setContentView();

        mGestureDetector = new GestureDetector(getApplicationContext(), new GestureDetector.OnGestureListener() {
            @Override
            public boolean onDown(MotionEvent motionEvent) {
                //DO Nothing
                return false;
            }

            @Override
            public void onShowPress(MotionEvent motionEvent) {
                //DO Nothing
            }

            @Override
            public boolean onSingleTapUp(MotionEvent motionEvent) {
                //DO Nothing
                return false;
            }

            @Override
            public boolean onScroll(MotionEvent motionEvent, MotionEvent motionEvent1, float v, float v1) {
                //DO Nothing
                return false;
            }

            @Override
            public void onLongPress(MotionEvent motionEvent) {
                //DO Nothing
            }

            @Override
            public boolean onFling(MotionEvent motionEvent, MotionEvent motionEvent1, float velocityX, float velocityY) {
                Log.d(TAG, "onFling: velocityX=" + velocityX);
                if (mCanDrag) {
                    if (velocityX > 500) {
                        mFlingToClose = true;
                        animateToFling(motionEvent1, velocityX, velocityY);
                    }
                }
                return false;
            }
        });

//        setupViews();

    }

    private void setContentView() {
        FrameLayout rootLayout = new FrameLayout(this);

        mBgView = new View(this);
        mBgView.setBackgroundColor(Color.BLACK);
        FrameLayout.LayoutParams bgViewParams = new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        rootLayout.addView(mBgView, bgViewParams);

        mContentLayout = new FrameLayout(this);
        FrameLayout.LayoutParams contentLayoutParams = new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        rootLayout.addView(mContentLayout, contentLayoutParams);

        mContentLayout.setPivotX(mScreenWidth);
        mContentLayout.setPivotY(mScreenHeight/2f);

        setContentView(rootLayout);
    }

    @Override
    public void setContentView(int layoutResID) {
        LayoutInflater.from(this).inflate(layoutResID, (ViewGroup) mContentLayout, true);
    }

    @Override
    protected void onStop() {
        super.onStop();

        setContentScale(1);
    }

    private void initScreenParams() {
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        mScreenWidth = dm.widthPixels;
        mScreenHeight = dm.heightPixels;
    }


    float firstDownX;
    float firstDownY;
    private void setupViews() {
//        mBgView = findViewById(R.id.bg_layout);
////        mCloseControlBar = findViewById(R.id.close_control_bar);
//        mContentLayout = findViewById(R.id.content_root_layout);
//        mContentLayout.setPivotX(mScreenWidth);
//        mContentLayout.setPivotY(mScreenHeight/2f);

//        mCloseControlBar.setOnTouchListener(new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(View view, MotionEvent motionEvent) {
//                if (mGestureDetector != null) {
//                    mGestureDetector.onTouchEvent(motionEvent);
//                }
//                float x = motionEvent.getRawX();
//                float y = motionEvent.getRawY();
//                switch (motionEvent.getAction()) {
//                    case MotionEvent.ACTION_DOWN:
//                        firstDownX = x;
//                        firstDownY = y;
//                        mContentLayout.setPivotY(y);
//                        Log.d(TAG, "onTouch: ACTION_DOWN, (" + x + ", " + y + ")");
//                        return true;
//                    case MotionEvent.ACTION_MOVE:
//                        float distanceX = x - firstDownX;
//                        float distanceY = y - firstDownY;
//                        float scale = 1f;
//                        if (distanceX > 0) {
//                            scale = 1 - distanceX / mScreenWidth;
//                            setContentScale(scale);
//                        }
//                        mContentLayout.setTranslationY(distanceY);
//                        Log.d(TAG, "onTouch: ACTION_MOVE, (" + x + ", " + y + ")");
//                        break;
//                    case MotionEvent.ACTION_UP:
//                        if (mFlingToClose) {
//                            mFlingToClose = false;
//                        } else {
//                            setContentScale(1f);
//                            mContentLayout.setTranslationY(0);
//                        }
//                        Log.d(TAG, "onTouch: ACTION_UP, (" + x + ", " + y + ")");
//                        break;
//                }
//                return false;
//            }
//        });
    }

    private ValueAnimator mFlingAnimator;
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private void animateToFling(MotionEvent motionEvent1, float velocityX, float velocityY) {
        // 计算起始点
        Point startPoint = new Point((int) (motionEvent1.getRawX() + mDistX), (int) motionEvent1.getRawY());
        float ratio = velocityY / velocityX;
        Point endPoint = new Point((int) (firstDownX + mScreenWidth), (int) ((mScreenWidth - motionEvent1.getRawX()) * ratio));

        if (mFlingAnimator == null) {
            mFlingAnimator = ValueAnimator.ofObject(mPositionEvaluator, startPoint, endPoint);
            mFlingAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator valueAnimator) {
                    Point p = (Point) valueAnimator.getAnimatedValue();
                    float distanceX = p.x - firstDownX;
                    float distanceY = p.y - firstDownY;
                    float scale = 1 - distanceX / mScreenWidth;
                    setContentScale(scale);
//                mContentLayout.setTranslationY(distanceY);
                }
            });
            mFlingAnimator.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationStart(Animator animation, boolean isReverse) {
                    //在fling动画开始前，再次确认设置缩放中心
                    mContentLayout.setPivotX(mScreenWidth);
                    mContentLayout.setPivotY(mScreenHeight/2f);
                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
//                mContentLayout.postDelayed(new Runnable() {
//                    @Override
//                    public void run() {
//                        finish();
//                    }
//                }, 50);
                    Log.d(TAG, "onAnimationEnd: finish Swipe Activity");
                    finish();
//                onBackPressed();
                }
            });
        } else {
            mFlingAnimator.setObjectValues(startPoint, endPoint);
        }
        mFlingAnimator.setDuration(calculateFingTime(Math.abs(velocityX), Math.abs(endPoint.x - startPoint.x)));
        Log.d(TAG, "animateToFling: start fling anim");
        mFlingAnimator.start();
    }

    private long calculateFingTime(float velocityX, int distance) {
        return (long) (1000f * distance / velocityX);
    }

    private PositionEvaluator mPositionEvaluator = new PositionEvaluator();
    private class PositionEvaluator implements TypeEvaluator<Point> {
        private Point position;

        public PositionEvaluator() {
            position = new Point();
        }

        @Override
        public Point evaluate(float fraction, Point startValue, Point endValue) {
            position.x = (int) (startValue.x * (1-fraction) + endValue.x * fraction);
            position.y = (int) (startValue.y * (1-fraction) + endValue.y * fraction);
            return position;
        }
    }

    /**
     * 设置Content的缩放
     * @param scale
     */
    private void setContentScale(float scale) {
        if (mContentLayout != null) {
            mContentLayout.setScaleX(scale);
            mContentLayout.setScaleY(scale);
            if (scale>0.9) {
                mContentLayout.setAlpha(scale);
            } else {
                mContentLayout.setAlpha(0.9f);
            }
        }
        if (scale < 0.85f && scale >= 0.5f) {
//            mBgView.setAlpha(1 - (0.85f-scale) / (0.85f-0.1f));//内容区域（mContentLayout）缩放到0.88时，背景开始变的透明，到0.1的时候，背景变成全透明
            mBgView.setAlpha(1 - (0.85f-scale) / (0.85f-0.3f));
        } else if (scale >= 0.85f) {
            mBgView.setAlpha(1f);
        }
    }

    @Deprecated
    protected void setMyContentView(int contentId) {
        LayoutInflater.from(this).inflate(contentId, (ViewGroup) mContentLayout, true);
    }

    protected View getContentLayout() {
        return mContentLayout;
    }

    protected void setSwipeEnable(boolean swipe) {
        mSwipeEnable = swipe;
    }

    protected View getBgView() {
        return mBgView;
    }

    protected int getScreenWidth() {
        return mScreenWidth;
    }

    protected int getScreenHeight() {
        return mScreenHeight;
    }

    boolean mCanDrag;
    float mDistX;
    float mDistY;

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (!mSwipeEnable) {
            return super.dispatchTouchEvent(ev);
        }
        boolean finalResut = super.dispatchTouchEvent(ev);
        if (mGestureDetector != null) {
            mGestureDetector.onTouchEvent(ev);
        }
        int pointerCount = ev.getPointerCount();
        int pointerIndex = ev.getActionIndex();
        float x = ev.getRawX() + mDistX;
        float y = ev.getRawY() + mDistY;
//        Log.d(TAG, "dispatchTouchEvent: ev.getRawX()=" + ev.getRawX() + ", ev.getX()=" + ev.getX() + ", ev.getX(0)=" + ev.getX(0) + ", mDistX=" + mDistX);
        switch (ev.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                Log.d(TAG, "dispatchTouchEvent: ACTION_DOWN, pointerCount=" + pointerCount);
                break;
            case MotionEvent.ACTION_POINTER_DOWN:
                Log.d(TAG, "dispatchTouchEvent: ACTION_POINTER_DOWN, pointerCount=" + pointerCount);
                if (!mCanDrag) {
                    firstDownX = x;
                    firstDownY = y;
                    mContentLayout.setPivotY(y);
                } else {
                    if (pointerIndex == 0) {
                        mDistX = ev.getX(pointerIndex + 1) + mDistX - ev.getX(pointerIndex);
                        mDistY = ev.getY(pointerIndex + 1) + mDistY - ev.getY(pointerIndex);
                    }
                }
                if (pointerCount >= 2) {
                    mCanDrag = true;

                    //在拖拽之前，再设置一次缩放中心点
                    mContentLayout.setPivotX(mScreenWidth);
                    mContentLayout.setPivotY(mScreenHeight/2f);
                }
                break;
            case MotionEvent.ACTION_MOVE:
//                Log.d(TAG, "dispatchTouchEvent: ACTION_MOVE(" + x + ", " + y + "), pointerCount=" + pointerCount);
                if (mCanDrag) {
                    float distanceX = x - firstDownX;
                    float distanceY = y - firstDownY;
                    float scale = 1f;
                    if (distanceX > 0) {
                        scale = 1 - distanceX / mScreenWidth;
                        setContentScale(scale);
                    }
                    return false;
                }
                break;
            case MotionEvent.ACTION_POINTER_UP:
                Log.d(TAG, "dispatchTouchEvent: ACTION_POINTER_UP, pointerCount=" + pointerCount);
                if (mCanDrag) {
                    if (pointerIndex == 0) {
                        //主手指抬起
                        mDistX = ev.getX(pointerIndex) - ev.getX(pointerIndex + 1);
                        mDistY = ev.getY(pointerIndex) - ev.getY(pointerIndex + 1);
                    }
                    return false;
                }
                break;
            case MotionEvent.ACTION_UP:
                Log.d(TAG, "dispatchTouchEvent: ACTION_UP");
                mDistX = 0;
                mDistY = 0;
                if (mCanDrag) {
                    if (mFlingToClose) {
                        mFlingToClose = false;
                    } else {
                        mContentLayout.setScaleX(1f);
                        mContentLayout.setScaleY(1f);
                        mContentLayout.setTranslationY(0);
                        mContentLayout.setAlpha(1f);
                    }
                    mCanDrag = false;
                    return false;
                }
                break;
        }
        return finalResut;
    }
}
