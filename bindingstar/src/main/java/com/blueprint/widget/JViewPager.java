package com.blueprint.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.PointF;
import android.graphics.Rect;
import android.support.annotation.Nullable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;

import java.lang.reflect.Field;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;

import static com.blueprint.helper.LogHelper.slog_d;

/**
 * @author 江祖赟.
 * @date 2017/6/7
 * @des [一句话描述]
 */
public class JViewPager extends ViewPager {
    private static final String TAG = LoopImagePager.class.getSimpleName();
    private static final long LOOPINTERVAL = 2;
    private Disposable mSubscribe;
    private Rect mVisibleRect = new Rect();
    private boolean mMove;
    private PointF mLastMoved = new PointF();
    private int mCurrentPosition = 20;
    private boolean mAutoLoop = true;
    private boolean noScrollable = true;
    private PagerAdapter mPagerAdapter;
    private RobeVPCanvas mRobeVPCanvas;
    private int mW;
    private int mH;

    public JViewPager(Context context){
        super(context);
    }

    public JViewPager(Context context, AttributeSet attrs){
        super(context, attrs);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh){
        mW = w;
        mH = h;
        super.onSizeChanged(w, h, oldw, oldh);
    }

    private void startLoop(){
        if(!mAutoLoop || mPagerAdapter == null || mPagerAdapter.getCount()<=1) {
            return;
        }
        if(mSubscribe == null || mSubscribe.isDisposed()) {
            slog_d(TAG, "start loop ==============");
            mSubscribe = Observable.interval(LOOPINTERVAL, TimeUnit.SECONDS).observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<Long>() {
                @Override
                public void accept(Long aLong) throws Exception{
                    //并不是立刻執行
                    if(getLocalVisibleRect(mVisibleRect)) {
                        //可见
                        setCurrentItem(++mCurrentPosition);
                    }else {
                        //取消自动loop
                        stopLoop();
                    }
                }
            });
        }
    }

    private void stopLoop(){
        if(mAutoLoop && mSubscribe != null) {
            slog_d(TAG, "stop auto loop ==============");
            mCurrentPosition = getCurrentItem();
            mSubscribe.dispose();
        }
    }

    public boolean isAutoLoop(){
        return mAutoLoop;
    }

    public void setAutoLoop(boolean autoLoop){
        mAutoLoop = autoLoop;
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event){
        try {
            if(mAutoLoop && mPagerAdapter != null && mPagerAdapter.getCount()>1) {
                //在列表中 上下滚动事件传不到 但是能够接收少量move事件
                //所以简单的down就停止不够，当上下滑动开启循环
                switch(event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        mLastMoved.set(event.getX(), event.getY());
                        mMove = false;
                        stopLoop();
                        break;
                    case MotionEvent.ACTION_MOVE:
                        if(Math.abs(event.getX()-mLastMoved.x)<Math.abs(event.getY()-mLastMoved.y)) {
                            //down的时候关了
                            startLoop();
                        }else {
                            slog_d(TAG, "move");
                            mMove = true;
                            stopLoop();
                        }
                        break;
                    case MotionEvent.ACTION_UP:
                        slog_d(TAG, "up");
                        mMove = false;
                        startLoop();
                        break;
                }
            }
            return super.dispatchTouchEvent(event);
        }catch(IllegalArgumentException ex) {
            //            https://github.com/chrisbanes/PhotoView/issues/31
            ex.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev){
        if(noScrollable) {
            return false;
        }else {
            return super.onInterceptTouchEvent(ev);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev){
        if(noScrollable) {
            return false;
        }else {
            return super.onInterceptTouchEvent(ev);
        }
    }

    @Override
    public void setAdapter(@Nullable PagerAdapter adapter){
        super.setAdapter(mPagerAdapter = adapter);
    }

    public void setNoScrollable(boolean noScrollable){
        this.noScrollable = noScrollable;
    }

    @Override
    protected void onDetachedFromWindow(){
        super.onDetachedFromWindow();
        //往下滚动到看不到
        stopLoop();
    }

    @Override
    protected void onAttachedToWindow(){
        super.onAttachedToWindow();
        slog_d(TAG, " onAttachedToWindow ==============");
        //向上滚动到可见
        startLoop();
    }

    @Override
    protected void onDraw(Canvas canvas){
        super.onDraw(canvas);
        //不可见滚动到可见会draw一次
        //可见到不可见不会触发
        //滚动轮播图的时候也会触发 左右滚动
        if(mAutoLoop && mSubscribe != null && !mMove && getLocalVisibleRect(mVisibleRect)) {
            startLoop();
        }

        if(mRobeVPCanvas != null) {
            mRobeVPCanvas.drawRobe(canvas,mW,mH);
        }

    }

    /**
     * set the duration between two slider changes.
     */
    public void setSliderTransformDuration(int period){
        setSliderTransformDuration(period, new DecelerateInterpolator());
    }

    public void setSliderTransformDuration(int period, Interpolator interpolator){
        try {
            Field mScroller = ViewPager.class.getDeclaredField("mScroller");
            mScroller.setAccessible(true);
            FixedSpeedScroller scroller = new FixedSpeedScroller(getContext(), interpolator, period);
            mScroller.set(this, scroller);
        }catch(Exception e) {

        }
    }

    public RobeVPCanvas getRobeVPCanvas(){
        return mRobeVPCanvas;
    }

    public JViewPager setRobeVPCanvas(RobeVPCanvas robeVPCanvas){
        mRobeVPCanvas = robeVPCanvas;
        return this;
    }

    /**
     * 装饰类 绘制 其他专用
     */
    public static abstract class RobeVPCanvas {
        public abstract void drawRobe(Canvas canvas, int width, int heigh);
    }
}
